
package org.exparity.data.html;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.exparity.data.BadFormatException;
import org.exparity.data.HTML;
import org.exparity.data.html.HtmlSelector;
import org.exparity.data.html.Tag;
import org.exparity.io.classpath.JcpFile;
import org.junit.Test;

/**
 * @author Stewart Bissett
 */
public class HtmlTagTest {

    @Test
    public void canGetText() throws BadFormatException, IOException {
        final HTML html = HTML.read(JcpFile.open("tags.html", HtmlTableTest.class));
        final Tag tag = html.findUnique(HtmlSelector.byId("test1"));
        assertNotNull(tag);
        assertEquals("Tag Contents", tag.getText());
    }

    @Test
    public void canGetTextWithLineBreak() throws BadFormatException, IOException {
        final HTML html = HTML.read(JcpFile.open("tags.html", HtmlTableTest.class));
        final Tag tag = html.findUnique(HtmlSelector.byId("test2"));
        assertNotNull(tag);
        assertEquals("TagContents", tag.getText());
    }

    @Test
    public void canGetFormattedTextWithLineBreak() throws BadFormatException, IOException {
        final HTML html = HTML.read(JcpFile.open("tags.html", HtmlTableTest.class));
        final Tag tag = html.findUnique(HtmlSelector.byId("test2"));
        assertNotNull(tag);
        assertEquals("Tag" + SystemUtils.LINE_SEPARATOR + "Contents", tag.getFormattedText());
    }

    @Test
    public void canGetTextFromNested() throws BadFormatException, IOException {
        final HTML html = HTML.read(JcpFile.open("tags.html", HtmlTableTest.class));
        final Tag tag = html.findUnique(HtmlSelector.byId("test3"));
        assertNotNull(tag);
        assertEquals("TagContents", tag.getText());
    }

    @Test
    public void canGetFormattedTextFromNested() throws BadFormatException, IOException {
        final HTML html = HTML.read(JcpFile.open("tags.html", HtmlTableTest.class));
        final Tag tag = html.findUnique(HtmlSelector.byId("test3"));
        assertNotNull(tag);
        assertEquals("Tag" + SystemUtils.LINE_SEPARATOR + "Contents", tag.getFormattedText());
    }

    @Test
    public void canGetTextFromPartiallyNested() throws BadFormatException, IOException {
        final HTML html = HTML.read(JcpFile.open("tags.html", HtmlTableTest.class));
        final Tag tag = html.findUnique(HtmlSelector.byId("test4"));
        assertNotNull(tag);
        assertEquals("TagContents", tag.getText());
    }

    @Test
    public void canGetFormattedTextFromPartiallyNested() throws BadFormatException, IOException {
        final HTML html = HTML.read(JcpFile.open("tags.html", HtmlTableTest.class));
        final Tag tag = html.findUnique(HtmlSelector.byId("test4"));
        assertNotNull(tag);
        assertEquals("Tag" + SystemUtils.LINE_SEPARATOR + "Contents", tag.getFormattedText());
    }

    @Test
    public void canGetTextFromEmpty() throws BadFormatException, IOException {
        final HTML html = HTML.read(JcpFile.open("tags.html", HtmlTableTest.class));
        final Tag tag = html.findUnique(HtmlSelector.byId("test5"));
        assertNotNull(tag);
        assertEquals(StringUtils.EMPTY, tag.getText());
    }

    @Test
    public void canGetTextWithNestedParagraph() throws BadFormatException, IOException {
        final HTML html = HTML.read(JcpFile.open("tags.html", HtmlTableTest.class));
        final Tag tag = html.findUnique(HtmlSelector.byId("test6"));
        assertNotNull(tag);
        assertEquals("TagContents", tag.getText());
    }

    @Test
    public void canGetFormattedTextWithNestedParagraph() throws BadFormatException, IOException {
        final HTML html = HTML.read(JcpFile.open("tags.html", HtmlTableTest.class));
        final Tag tag = html.findUnique(HtmlSelector.byId("test6"));
        assertNotNull(tag);
        assertEquals(SystemUtils.LINE_SEPARATOR + "TagContents", tag.getFormattedText());
    }

}
