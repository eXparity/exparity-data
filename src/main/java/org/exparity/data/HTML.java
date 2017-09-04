/*
 *
 */

package org.exparity.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.exparity.data.html.Anchor;
import org.exparity.data.html.DuplicateTagException;
import org.exparity.data.html.HtmlParserTag;
import org.exparity.data.html.HtmlSelector;
import org.exparity.data.html.Tag;
import org.exparity.data.types.Array;
import org.exparity.data.types.Table;
import org.exparity.io.TextDataSource;
import org.exparity.io.classpath.JcpFile;
import org.exparity.io.filesystem.FileSystemFile;
import org.exparity.io.internet.InternetFile;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.lexer.Page;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.SimpleNodeIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link HTML} models a HTML document. Instantiate using the factory methods.
 *
 * @author Stewart Bissett
 */
public final class HTML extends Text {

    private static final Logger LOG = LoggerFactory.getLogger(HTML.class);
    // private static final XmlFactory<HTML> DEFAULT_XML_CONVERTER = new HtmlCleanerXmlFactory();

    // Tags
    public static final String ANCHOR_TAG = "a";
    public static final String BREAK_TAG = "br";
    public static final String DEFINITION_LIST_TAG = "dl";
    public static final String HEAD_TAG = "head";
    public static final String META_TAG = "meta";
    public static final String ORDERED_LIST_TAG = "ol";
    public static final String P_TAG = "p";
    public static final String SELECT_TAG = "select";
    public static final String SPAN_TAG = "span";
    public static final String TABLE_DATA_TAG = "td";
    public static final String TABLE_HEADER = "thead";
    public static final String TABLE_HEADER_DATA_TAG = "th";
    public static final String TABLE_ROW_TAG = "tr";
    public static final String TABLE_TAG = "table";
    public static final String TD_TAG = "td";
    public static final String TITLE_TAG = "title";
    public static final String UNORDERED_LIST_TAG = "ul";

    // Attributes
    public static final String CONTENT_ATTRIBUTE = "content";
    public static final String HTTP_EQUIV_ATTRIBUTE = "http-equiv";
    public static final String HREF_ATTRIBUTE = "href";

    // Headers
    public static final String CONTENT_TYPE_HEADER = "Content-Type";
    public static final String LOCATION_HEADER = "Location";

    /**
     * Return an empty {@link HTML}
     */
    public static HTML empty() {
        return new HTML(new Page(), new NodeList());
    }

    /**
     * Factory method for creating a {@link HTML} from a file
     *
     * @return A {@link HTML} document
     */
    public static HTML openResource(final String resource) throws IOException {
        return HTML.read(JcpFile.open(resource));
    }

    /**
     * Factory method for creating a {@link HTML} from a file
     *
     * @return A {@link HTML} document
     */
    public static HTML openResource(final String resource, final Class<?> klass) throws IOException {
        return HTML.read(JcpFile.open(resource, klass));
    }

    /**
     * Factory method for creating a {@link HTML} from a file
     *
     * @return A {@link HTML} document
     */
    public static HTML openResource(final String resource, final ClassLoader loader) throws IOException {
        return HTML.read(JcpFile.open(resource, loader));
    }

    /**
     * Factory method for creating a {@link HTML} from a file
     *
     * @return A {@link HTML} document
     */
    public static HTML openFile(final String file) throws IOException {
        return HTML.read(FileSystemFile.open(file));
    }

    /**
     * Factory method for creating a {@link HTML} from a URL.
     *
     * @return A {@link HTML} document
     */
    public static HTML openFile(final File file) throws IOException {
        return HTML.read(FileSystemFile.open(file));
    }

    /**
     * Factory method for creating a {@link HTML} from a URL.
     *
     * @return A {@link HTML} document
     */
    public static HTML openURL(final String url) throws IOException {
        return HTML.read(InternetFile.open(url));
    }

    /**
     * Factory method for creating a {@link HTML} from a URL.
     *
     * @return A {@link HTML} document
     */
    public static HTML openURL(final URL url) throws FileNotFoundException {
        return HTML.read(InternetFile.open(url));
    }

    /**
     * Parse a {@link HTML} file from a {@link TextDataSource} instance such as
     * {@link org.exparity.io.classpath.JcpFile}
     */
    public static HTML read(final TextDataSource source) {
        return HTML.read(source.getStream());
    }

    /**
     * Read a {@link HTML} from an input stream
     */
    public static HTML read(final InputStream is) {
        return HTML.read(is, "UTF-8");
    }

    /**
     * Read a {@link HTML} from an input stream
     */
    public static HTML read(final InputStream is, final String charset) {
        Page page;
        try {
            page = new Page(is, charset);
        } catch (UnsupportedEncodingException e) {
            throw new BadFormatException(e);
        }

        Parser parser = new Parser(new Lexer(page));
        try {
            NodeList list = parser.parse(null);
            if (list == null || hasNoTagNodes(list) || startsWithoutAngleBracket(page)) {
                throw new BadFormatException("Data does not appear to be html", page.getText());
            }
            return new HTML(page, list);
        } catch (ParserException e) {
            throw new BadFormatException(e);
        } finally {
            parser = null;
        }
    }

    private static boolean startsWithoutAngleBracket(final Page page) {
        return !page.getText().trim().startsWith("<");
    }

    private static boolean hasNoTagNodes(final NodeList list) {
        for (SimpleNodeIterator i = list.elements(); i.hasMoreNodes();) {
            if (TagNode.class.isInstance(i.nextNode())) {
                return false;
            }
        }
        return true;
    }

    private final NodeList nodelist;
    private final String text;

    private HTML(final Page page, final NodeList list) {
        Validate.notNull(page, "Text cannot be null");
        Validate.notNull(list, "Node cannot be null");
        this.nodelist = list;
        this.text = page.getText();
    }

    /**
     * Extract a collection of {@link Tag} instances from the HTML document which match the {@link HtmlSelector}
     * predicates.
     */
    public List<Tag> findTags(final HtmlSelector... selectors) {
        NodeList found = findNodes(selectors);
        if (found == null) {
            return Collections.emptyList();
        }
        return HtmlParserTag.of(found);
    }

    /**
     * Extract a single {@link Tag} instance from the HTML document which match the {@link HtmlSelector} predicates. If
     * no match is found then <code>null</code> is returned.
     */
    public Tag findUnique(final HtmlSelector... selectors) {
        List<Tag> found = findTags(selectors);
        if (CollectionUtils.isEmpty(found)) {
            return null;
        } else
            if (found.size() == 1) {
                return found.get(0);
            } else {
                throw new DuplicateTagException("Found multiple tags when one or zero was expected");
            }
    }

    /**
     * Return the length of the HTML document
     */
    public int getLength() {
        return text.length();
    }

    /**
     * Return the text of the HTML document
     */
    @Override
    public String getText() {
        return text;
    }

    /**
     * Return if the document is empty
     */
    public boolean isEmpty() {
        return StringUtils.isBlank(text);
    }

    /**
     * Return the contents of the first TITLE tag from this HTML document, or null if one does not exist
     */
    public String getTitle() {
        List<Tag> titles = findTags(HtmlSelector.TITLE);
        return titles.isEmpty() ? null : titles.get(0).getText();
    }

    /**
     * Extract the TABLE elements from the HTML document which match the {@link HtmlSelector} predicates and return them
     * as zero or more {@link Table} instances
     */
    public List<Table> findTables(final HtmlSelector... selectors) {
        List<Table> tables = new ArrayList<>();
        for (Tag tag : findTags((HtmlSelector[]) ArrayUtils.add(selectors, HtmlSelector.TABLE))) {
            try {
                tables.add(tag.toTable());
            } catch (BadConversionException e) {
                LOG.warn("Unexpected non-table element " + tag + " when selecting tables");
            }
        }
        return tables;
    }

    public List<Array> findArrays(final HtmlSelector... selectors) {
        List<Array> arrays = new ArrayList<>();
        for (Tag tag : findTags((HtmlSelector[]) ArrayUtils.add(selectors, HtmlSelector.ARRAY))) {
            try {
                arrays.add(tag.toArray());
            } catch (BadConversionException e) {
                LOG.warn("Unexpected non-array element " + tag + " when selecting arrays");
            }
        }
        return arrays;
    }

    public List<Anchor> findAnchors(final HtmlSelector... selectors) {
        List<Anchor> anchors = new ArrayList<>();
        for (Tag tag : findTags((HtmlSelector[]) ArrayUtils.add(selectors, HtmlSelector.byTagName(HTML.ANCHOR_TAG)))) {
            final String href = tag.getAttribute(HTML.HREF_ATTRIBUTE);
            if (StringUtils.isNotEmpty(href)) {
                anchors.add(new Anchor(href));
            }
        }
        return anchors;
    }

    private NodeList findNodes(final HtmlSelector... selectors) {
        NodeList found = this.nodelist.extractAllNodesThatMatch(new HtmlParserSelector(selectors), true);
        if (found == null || found.size() == 0) {
            return null;
        }
        return found;
    }

    private class HtmlParserSelector implements NodeFilter {

        private static final long serialVersionUID = 1L;
        private final HtmlSelector[] selectors;

        public HtmlParserSelector(final HtmlSelector[] selectors) {
            this.selectors = selectors;
        }

        @Override
        public boolean accept(final org.htmlparser.Node node) {
            if (!(node instanceof org.htmlparser.Tag)) {
                return false;
            }

            org.htmlparser.Tag tag = (org.htmlparser.Tag) node;
            if (tag.isEndTag()) {
                return false;
            }

            Tag wrapped = HtmlParserTag.of(tag);
            for (HtmlSelector selector : selectors) {
                if (!selector.matches(wrapped)) {
                    return false;
                }
            }

            return true;
        }
    }
}