/*
 *
 */

package org.exparity.data.xml.selectors;

import java.util.List;

import org.exparity.data.XML;
import org.exparity.data.xml.XmlSelector;
import org.w3c.dom.Element;

/**
 * @author Stewart Bissett
 */
public class XPathElementSelector implements XmlSelector<List<Element>> {

    private final String xpath;

    public XPathElementSelector(final String xpath) {
        this.xpath = xpath;
    }

    @Override
    public List<Element> select(final XML xml) {
        return xml.findElementsByXpath(xpath);
    }
}
