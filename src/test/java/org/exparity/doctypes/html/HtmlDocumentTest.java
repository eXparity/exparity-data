package org.exparity.doctypes.html;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.exparity.data.BadFormatException;
import org.exparity.data.HTML;
import org.exparity.data.html.HtmlSelector;
import org.exparity.data.html.Tag;
import org.exparity.io.classpath.JcpFile;
import org.junit.Test;

/**
 * @author Stewart Bissett
 */
public class HtmlDocumentTest {

    @Test
    public void canCreateHtml() throws Exception {
        HTML html = HTML.read(JcpFile.open("sample.html", HtmlDocumentTest.class));
        assertEquals(458, html.getLength());
        assertEquals("Sample Page", html.getTitle());
    }

    @Test(expected = BadFormatException.class)
    public void canThrowBadFormatExcceptionForInvalidText() throws Exception {
        HTML.read(JcpFile.open("sample.csv", HtmlDocumentTest.class));
    }

    @Test(expected = BadFormatException.class)
    public void canThrowBadFormatExcceptionForBinary() throws Exception {
        HTML.read(JcpFile.open("sample.gif", HtmlDocumentTest.class));
    }

    @Test
    public void canReadNonStandardCharacters2() throws Exception {
        HTML data = HTML.read(JcpFile.open("canon_xl2.htm", HtmlDocumentTest.class));
        List<Tag> tags =
                data.findTags(HtmlSelector.byTagName("meta"), HtmlSelector.byAttributeValue("name", "description"));
        assertEquals(1, tags.size());
        assertEquals(
                "With 3x 1/3” 800k progressive scan CCDs and full manual control over all image settings, the XL2 represents the last word in flexible, broadcast quality miniDV.",
                tags.get(0).getAttribute("content"));
    }

    @Test
    public void canNotBeEqualForTwoInstanceSameFile() throws Exception {
        HTML data = HTML.read(JcpFile.open("sample.html", HtmlDocumentTest.class));
        HTML other = HTML.read(JcpFile.open("sample.html", HtmlDocumentTest.class));
        assertNotSame(data, other);
        assertTrue(!data.equals(other));
    }

    @Test
    public void canNotBeEqualForDifferentFiles() throws Exception {
        HTML data = HTML.read(JcpFile.open("sample.html", HtmlDocumentTest.class));
        HTML other = HTML.read(JcpFile.open("canon_xl2.htm", HtmlDocumentTest.class));
        assertTrue(!data.equals(other));
    }
}
