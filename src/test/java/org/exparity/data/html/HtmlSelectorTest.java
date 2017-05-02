package org.exparity.data.html;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.regex.Pattern;

import org.exparity.data.HTML;
import org.exparity.io.classpath.JcpFile;
import org.junit.Test;

/**
 * @author Stewart Bissett
 */
public class HtmlSelectorTest {

    @Test
    public void canFindTagsByTagName() throws Exception {
        HTML data = HTML.read(JcpFile.open("sample.html", HtmlSelectorTest.class));

        List<Tag> found = data.findTags(HtmlSelector.byTagName(HTML.P_TAG));
        assertEquals(1, found.size());

        Tag tag = found.get(0);
        assertEquals("Some sample content", tag.getText());
    }

    @Test
    public void canFindTagsByTagRegex() throws Exception {
        HTML data = HTML.read(JcpFile.open("sample.html", HtmlSelectorTest.class));

        Pattern pattern = Pattern.compile("p|title");
        List<Tag> found = data.findTags(HtmlSelector.byTagRegex(pattern));
        assertEquals(2, found.size());

        assertEquals(HTML.TITLE_TAG, found.get(0).getName());
        assertEquals("Sample Page", found.get(0).getText());

        assertEquals(HTML.P_TAG, found.get(1).getName());
        assertEquals("Some sample content", found.get(1).getText());
    }

    @Test
    public void canFindTagsbyAttributeValue() throws Exception {
        HTML data = HTML.read(JcpFile.open("sample.html", HtmlSelectorTest.class));

        List<Tag> found = data.findTags(
                HtmlSelector.byAttributeValue(HTML.HTTP_EQUIV_ATTRIBUTE, HTML.CONTENT_TYPE_HEADER));
        assertEquals(1, found.size());

        Tag tag = found.get(0);
        assertEquals(HTML.META_TAG, tag.getName());
        assertEquals("Content-Type", tag.getAttribute(HTML.HTTP_EQUIV_ATTRIBUTE));
        assertEquals("text/html; charset=ISO-8859-1", tag.getAttribute(HTML.CONTENT_ATTRIBUTE));
    }

    @Test
    public void canFindTagsbyAttributeName() throws Exception {
        HTML data = HTML.read(JcpFile.open("sample.html", HtmlSelectorTest.class));

        List<Tag> found = data.findTags(HtmlSelector.byAttributeName(HTML.HTTP_EQUIV_ATTRIBUTE));
        assertEquals(1, found.size());

        Tag tag = found.get(0);
        assertEquals(HTML.META_TAG, tag.getName());
        assertEquals("Content-Type", tag.getAttribute(HTML.HTTP_EQUIV_ATTRIBUTE));
        assertEquals("text/html; charset=ISO-8859-1", tag.getAttribute(HTML.CONTENT_ATTRIBUTE));
    }

    @Test
    public void canFindTagsbyAttributeRegex() throws Exception {
        HTML html = HTML.read(JcpFile.open("sample.html", HtmlSelectorTest.class));

        Pattern pattern = Pattern.compile("x|y");
        List<Tag> found = html.findTags(HtmlSelector.byAttributeRegex("id", pattern));
        assertEquals(2, found.size());

        assertEquals(HTML.SPAN_TAG, found.get(0).getName());
        assertEquals("Span X", found.get(0).getText());

        assertEquals(HTML.SPAN_TAG, found.get(1).getName());
        assertEquals("Span Y", found.get(1).getText());
    }

}
