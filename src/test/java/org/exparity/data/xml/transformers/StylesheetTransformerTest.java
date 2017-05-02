package org.exparity.data.xml.transformers;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.exparity.data.BadFormatException;
import org.exparity.data.XML;
import org.exparity.data.xml.transformers.StylesheetTransformer;
import org.exparity.io.classpath.JcpFile;
import org.junit.Test;
import org.w3c.dom.Element;

public class StylesheetTransformerTest {

    @Test
    public void canTransform() throws Exception {
        final StylesheetTransformer stylesheet =
                new StylesheetTransformer(JcpFile.open("sample.xsl", StylesheetTransformerTest.class));
        final XML xml = XML.openResource("sample.xml", StylesheetTransformerTest.class);

        List<Element> elements = xml.findElementsByTagName("sampleElement");
        assertEquals(2, elements.size());

        XML transformed = xml.transform(stylesheet);

        elements = transformed.findElementsByTagName("sampleElement");
        assertEquals(1, elements.size());
        Element e = elements.get(0);
        assertEquals("2", e.getAttribute("elementIndex"));

        elements = transformed.findElementsByTagName("sampleChild");
        assertEquals(1, elements.size());
        e = elements.get(0);
        assertEquals("Some data", e.getTextContent());
    }

    @Test(expected = BadFormatException.class)
    public void canThrowBadFormatOnInvalidFile() throws Exception {
        new StylesheetTransformer(JcpFile.open("sample.xml", StylesheetTransformerTest.class));
    }
}
