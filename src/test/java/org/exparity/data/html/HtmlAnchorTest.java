
package org.exparity.data.html;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.List;

import org.exparity.data.HTML;
import org.exparity.io.classpath.JcpFile;
import org.junit.Test;

/**
 * @author Stewart Bissett
 */
public class HtmlAnchorTest {

    @Test
    public void canExtractLinks() throws Exception {

        HTML html = HTML.read(JcpFile.open("anchors.html", HtmlAnchorTest.class));
        List<Anchor> links = html.findAnchors();
        assertEquals(11, links.size());

        checkAnchorExists(links, "http://www.revuver.co.uk/page1.html");
        checkAnchorExists(links, "page2.html");
        checkAnchorExists(links, "/page3.html");
        checkAnchorExists(links, "./page4.html");
        checkAnchorExists(links, "http://www.revuver.co.uk/page5.html");
        checkAnchorExists(links, "http://www.revuver.co.uk/page6.html");
        checkAnchorExists(links, "http://www.revuver.co.uk/page7.html");
        checkAnchorExists(links, "mailto:stewart@revuver.co.uk");
        checkAnchorExists(links, "javascript:alert('hello');");
        checkAnchorExists(links, "#");
        checkAnchorExists(links, "http://www.revuver.co.uk/page8.html");
    }

    private void checkAnchorExists(final Collection<Anchor> links, final String href) {
        for (Anchor link : links) {
            if (link.getHref().equalsIgnoreCase(href)) {
                return;
            }
        }
        fail("Expected " + href);
    }
}
