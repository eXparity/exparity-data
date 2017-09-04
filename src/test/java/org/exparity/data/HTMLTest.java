package org.exparity.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.exparity.data.html.HtmlSelector;
import org.exparity.data.html.Tag;
import org.junit.Test;

/**
 * @author Stewart Bissett
 */
public class HTMLTest {

    @Test
    public void canCreateHtml() throws Exception {
        HTML html = HTML.openResource("/org/exparity/data/html/sample.html", HTMLTest.class);
        assertEquals(458, html.getLength());
        assertEquals("Sample Page", html.getTitle());
    }

    @Test(expected = BadFormatException.class)
    public void canThrowBadFormatExcceptionForInvalidText() throws Exception {
        HTML.openResource("/org/exparity/data/html/sample.csv", HTMLTest.class);
    }

    @Test(expected = BadFormatException.class)
    public void canThrowBadFormatExcceptionForBinary() throws Exception {
        HTML.openResource("/org/exparity/data/html/sample.gif", HTMLTest.class);
    }

    @Test
    public void canReadNonStandardCharacters2() throws Exception {
        HTML data = HTML.openResource("/org/exparity/data/html/canon_xl2.htm", HTMLTest.class);
        List<Tag> tags = data.findTags(HtmlSelector.byTagName("meta"),
                HtmlSelector.byAttributeValue("name", "description"));
        assertEquals(1, tags.size());
        assertEquals(
                "With 3x 1/3” 800k progressive scan CCDs and full manual control over all image settings, the XL2 represents the last word in flexible, broadcast quality miniDV.",
                tags.get(0).getAttribute("content"));
    }

    @Test
    public void canNotBeEqualForTwoInstanceSameFile() throws Exception {
        HTML data = HTML.openResource("/org/exparity/data/html/sample.html", HTMLTest.class);
        HTML other = HTML.openResource("/org/exparity/data/html/sample.html", HTMLTest.class);
        assertNotSame(data, other);
        assertTrue(!data.equals(other));
    }

    @Test
    public void canNotBeEqualForDifferentFiles() throws Exception {
        HTML data = HTML.openResource("/org/exparity/data/html/sample.html", HTMLTest.class);
        HTML other = HTML.openResource("/org/exparity/data/html/canon_xl2.htm", HTMLTest.class);
        assertTrue(!data.equals(other));
    }
}
