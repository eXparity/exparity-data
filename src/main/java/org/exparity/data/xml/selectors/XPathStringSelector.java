/*
 *
 */

package org.exparity.data.xml.selectors;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.exparity.data.XML;
import org.exparity.data.xml.XmlSelector;
import org.w3c.dom.Node;

/**
 * @author Stewart Bissett
 */
public class XPathStringSelector implements XmlSelector<List<String>> {
    private final String xpath;

    public XPathStringSelector(final String xpath) {
        this.xpath = xpath;
    }

    @Override
    public List<String> select(final XML document) {
        return document.findNodesByXpath(xpath).stream().map(Node::getTextContent).collect(toList());
    }
}
