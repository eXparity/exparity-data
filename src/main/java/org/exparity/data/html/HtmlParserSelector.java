/*
 *
 */

package org.exparity.data.html;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Tag;

/**
 * @author Stewart Bissett
 */
class HtmlParserSelector implements NodeFilter {

    private static final long serialVersionUID = 1L;
    private final HtmlSelector[] selectors;

    public HtmlParserSelector(final HtmlSelector[] selectors) {
        this.selectors = selectors;
    }

    @Override
    public boolean accept(final Node node) {
        if (!(node instanceof Tag)) {
            return false;
        }

        Tag tag = (Tag) node;
        if (tag.isEndTag()) {
            return false;
        }

        org.exparity.data.html.Tag wrapped = HtmlParserTag.of(node);
        for (HtmlSelector selector : selectors) {
            if (!selector.matches(wrapped)) {
                return false;
            }
        }

        return true;
    }

}
