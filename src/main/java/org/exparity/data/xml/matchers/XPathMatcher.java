/*
 *
 */

package org.exparity.data.xml.matchers;

import org.apache.commons.collections.CollectionUtils;
import org.exparity.data.XML;
import org.exparity.data.xml.XmlMatcher;

/**
 * @author Stewart Bissett
 */
public class XPathMatcher implements XmlMatcher {

    private final String xpath;

    public XPathMatcher(final String xpath) {
        this.xpath = xpath;
    }

    @Override
    public boolean matches(final XML document) {
        return CollectionUtils.isNotEmpty(document.findElementsByXpath(xpath));
    }
}
