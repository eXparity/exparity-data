
package org.exparity.data.html;

import static org.exparity.data.HTML.openResource;
import static org.junit.Assert.assertEquals;

import org.exparity.data.HTML;
import org.exparity.data.XML;
import org.junit.Test;

/**
 * @author Stewart Bissett
 */
public class HtmlToXmlConverterHtmlCleanerImplTest {

    @Test
    public void canConvertHtmlToXml() throws Exception {
        final HTML html = openResource("canon-legegria-hf-m31-review.htm", HtmlToXmlConverterHtmlCleanerImplTest.class);
        final String extract = XML.of(html).findTextByXpath("//meta[@name='description']/@content");
        assertEquals(
                "In the right circumstances, the Canon Legria HF M31's picture quality can easily impress. But poor low-light performance and a sorely misjudged control system let down this otherwise decent camcorder",
                extract);
    }

    @Test
    public void canParsingHandleXHTML() throws Exception {
        final HTML html = openResource("trustedreviews.html", HtmlToXmlConverterHtmlCleanerImplTest.class);
        final String extract = XML.of(html).findTextByXpath("//meta[@name='description']/@content");
        assertEquals(
                "Sony's HDR-HC9E camcorder remains loyal to the tape-based HDV format, so how does it stack up against the competition?",
                extract);
    }

    @Test
    public void canParsingHandleMulticaseScriptTags() throws Exception {
        final HTML html = openResource("kodak_p850.htm", HtmlToXmlConverterHtmlCleanerImplTest.class);
        final String extract = XML.of(html).findTextByXpath("//title/text()");
        assertEquals("KODAK EASYSHARE P850 Zoom Digital Camera", extract);
    }

    @Test
    public void canParsePageWithDodgyStyles() throws Exception {
        final HTML html = openResource("samsung_gx10.html", HtmlToXmlConverterHtmlCleanerImplTest.class);
        final String extract = XML.of(html).findTextByXpath("//title/text()");
        assertEquals("GX-10 | GX Series | Samsung Digital Camera - UK", extract);
    }
}
