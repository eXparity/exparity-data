/*
 *
 */

package org.exparity.data.html;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.exparity.data.BadConversionException;
import org.exparity.data.types.Array;
import org.exparity.data.types.Pair;
import org.exparity.data.types.Scalar;
import org.exparity.data.types.Table;
import org.htmlparser.Node;
import org.htmlparser.Text;
import org.htmlparser.tags.Bullet;
import org.htmlparser.tags.BulletList;
import org.htmlparser.tags.CompositeTag;
import org.htmlparser.tags.DefinitionList;
import org.htmlparser.tags.DefinitionListBullet;
import org.htmlparser.tags.SelectTag;
import org.htmlparser.tags.TableColumn;
import org.htmlparser.tags.TableHeader;
import org.htmlparser.tags.TableRow;
import org.htmlparser.tags.TableTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.SimpleNodeIterator;
import org.htmlparser.visitors.NodeVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Stewart Bissett
 */
public class HtmlParserTag extends Tag {

    private static final Logger LOG = LoggerFactory.getLogger(HtmlParserTag.class);

    private final org.htmlparser.Tag tag;

    public static Tag of(final org.htmlparser.Node node) {
        if (node instanceof org.htmlparser.Tag && !((org.htmlparser.Tag) node).isEndTag()) {
            return new HtmlParserTag((org.htmlparser.Tag) node);
        } else {
            throw new BadConversionException("Unable to convert " + node + " to html tag");
        }
    }

    public static List<Tag> of(final NodeList nodeList) {
        List<Tag> tags = new ArrayList<>();
        for (SimpleNodeIterator i = nodeList.elements(); i.hasMoreNodes();) {
            Node next = i.nextNode();
            if (next instanceof org.htmlparser.Tag && !((org.htmlparser.Tag) next).isEndTag()) {
                tags.add(new HtmlParserTag((org.htmlparser.Tag) next));
            }
        }
        return tags;
    }

    private HtmlParserTag(final org.htmlparser.Tag tag) {
        this.tag = tag;
    }

    @Override
    public String getAttribute(final String name) {
        return tag.getAttribute(name);
    }

    @Override
    public String getName() {
        return tag.getTagName().toLowerCase();
    }

    @Override
    public Tag getParent() {
        return tag.getParent() != null ? new HtmlParserTag((org.htmlparser.Tag) tag.getParent()) : null;
    }

    @Override
    public String getText() {
        return getText(tag, false);
    }

    @Override
    public String getFormattedText() {
        return getText(tag, true);
    }

    @Override
    public List<Attribute> getAttributes() {
        List<Attribute> attributes = new ArrayList<>();
        for (Object obj : tag.getAttributesEx()) {
            org.htmlparser.Attribute attribute = (org.htmlparser.Attribute) obj;
            attributes.add(new Attribute(attribute.getName(), attribute.getValue()));
        }
        return attributes;
    }

    @Override
    public List<Tag> getChildren() {
        if (tag.getChildren() == null) {
            return new ArrayList<>();
        } else {
            return HtmlParserTag.of(tag.getChildren());
        }
    }

    @Override
    public boolean isType(final String... types) {
        for (String type : types) {
            if (this.getName().equalsIgnoreCase(type)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Array toArray() throws BadConversionException {
        if (tag instanceof SelectTag) {
            return createArray((SelectTag) tag);
        } else if (tag instanceof BulletList) {
            return createArray((BulletList) tag);
        } else if (tag instanceof DefinitionList) {
            return createArray((DefinitionList) tag);
        } else {
            throw new BadConversionException("Unable to convert " + tag + " to an array");
        }
    }

    private Array createArray(final DefinitionList list) {
        List<Scalar> values = new ArrayList<>();
        for (Node node : list.getChildrenAsNodeArray()) {
            if (node instanceof DefinitionListBullet) {
                DefinitionListBullet bullet = (DefinitionListBullet) node;
                if (node.getText().equalsIgnoreCase("dd")) {
                    values.add(new Scalar(bullet.getStringText()));
                }
            }
        }
        return Array.of(values);
    }

    private Array createArray(final BulletList list) {
        List<Scalar> values = new ArrayList<>();
        for (Node node : list.getChildrenAsNodeArray()) {
            if (node instanceof Bullet) {
                Bullet bullet = (Bullet) node;
                values.add(new Scalar(bullet.getStringText()));
            }
        }
        return Array.of(values);
    }

    private Array createArray(final SelectTag list) {
        Scalar[] values = new Scalar[list.getOptionTags().length];
        for (int i = 0; i < values.length; ++i) {
            values[i] = new Scalar(list.getOptionTags()[i].getValue());
        }
        return new Array(values);
    }

    @Override
    public Table toTable() throws BadConversionException {
        if (tag instanceof TableTag) {
            return createTable((TableTag) tag);
        } else {
            throw new BadConversionException("Unable to convert " + tag + " to a table");
        }
    }

    private Table createTable(final TableTag table) {
        List<Scalar> headers = new ArrayList<>();
        List<Scalar[]> data = new ArrayList<>();
        Map<Integer, Pair<Scalar, Integer>> spannedRows = new HashMap<>();

        for (TableRow row : table.getRows()) {
            if (row.getHeaderCount() > 0) {
                for (TableHeader header : row.getHeaders()) {
                    handleSpanning(spannedRows, headers, header);
                }
            }
            if (row.getColumnCount() > 0) {
                List<Scalar> values = new ArrayList<>();
                for (TableColumn column : row.getColumns()) {
                    handleSpanning(spannedRows, values, column);
                }
                data.add(values.toArray(new Scalar[] {}));
            }
        }

        List<Scalar[]> padded = padTableWithBlankColumnsIfRequired(data);
        Table extract = Table.withHeader(Array.of(headers).asStringArray());
        for (Scalar[] row : padded) {
            extract = extract.addRow(row);
        }

        if (LOG.isTraceEnabled()) {
            try {
                extract.writeTo(System.out);
            } catch (IOException e) {
                // Ignore exception
            }
        }
        return extract;
    }

    private void handleSpanning(final Map<Integer, Pair<Scalar, Integer>> spannedRows,
            final List<Scalar> values,
            final CompositeTag column) {
        for (int i = 0; i < getValueOrDefault(column.getAttribute("colspan"), 1); ++i) {
            int columnId = values.size();
            if (spannedRows.get(columnId) != null) {
                values.add(spannedRows.get(columnId).getValue1());
                int remaingRowsToSpan = spannedRows.get(columnId).getValue2() - 1;
                if (remaingRowsToSpan == 0) {
                    spannedRows.remove(columnId);
                } else {
                    spannedRows.put(columnId, Pair.create(spannedRows.get(columnId).getValue1(), remaingRowsToSpan));
                }
            }

            columnId = values.size();
            Scalar value = new Scalar(getText(column, false));
            values.add(value);
            int rowspan = getValueOrDefault(column.getAttribute("rowspan"), 1);
            if (rowspan > 1) {
                int remaingRowsToSpan = rowspan - 1;
                spannedRows.put(columnId, Pair.create(value, remaingRowsToSpan));
            }

        }
    }

    private List<Scalar[]> padTableWithBlankColumnsIfRequired(final List<Scalar[]> source) {
        List<Scalar[]> result = new ArrayList<>();
        int width = getMaximumColumnWidth(source);
        for (Scalar[] row : source) {
            if (row.length == width) {
                result.add(row);
            } else {
                Scalar[] padded = new Scalar[width];
                for (int i = 0; i < width; ++i) {
                    if (i < row.length) {
                        padded[i] = row[i];
                    } else {
                        padded[i] = new Scalar("");
                    }
                }
                result.add(padded);
            }
        }
        return result;
    }

    private int getMaximumColumnWidth(final List<Scalar[]> data) {
        int maxColumns = 0;
        for (Scalar[] row : data) {
            maxColumns = Math.max(maxColumns, row.length);
        }
        return maxColumns;
    }

    private String getText(final Node node, final boolean withFormatting) {
        if (node.getChildren() == null) {
            return StringUtils.EMPTY;
        }

        final StringBuffer buffer = new StringBuffer();
        try {
            node.getChildren().visitAllNodesWith(new NodeVisitor(true) {
                @Override
                public void visitTag(final org.htmlparser.Tag tag) {
                    if (withFormatting && tag.breaksFlow()) {
                        buffer.append(SystemUtils.LINE_SEPARATOR);
                    }
                }

                @Override
                public void visitStringNode(final Text txt) {
                    buffer.append(txt.getText());
                }
            });
        } catch (ParserException e) {
            throw new RuntimeException(e);
        }
        return buffer.toString();
    }

    private int getValueOrDefault(final String value, final int defaultValue) {
        return StringUtils.isBlank(value) ? defaultValue : Integer.valueOf(value);
    }

    @Override
    public String toString() {
        return "Tag [" + getName() + "]";
    }
}
