package org.exparity.doctypes.xml;

import static org.exparity.data.xml.parser.XmlParserBuilder.newParserFactory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;

import org.exparity.data.BadFormatException;
import org.exparity.data.XML;
import org.exparity.data.types.Pair;
import org.exparity.data.xml.XmlParser;
import org.exparity.io.classpath.JcpFile;
import org.junit.Test;

/**
 * @author Stewart Bissett
 */
public class XmlDocumentTest {

    @Test
    public void canCreateXml() throws Exception {
        assertNotNull(XML.openResource("sample.xml", XmlDocumentTest.class));
    }

    @Test(expected = BadFormatException.class)
    public void canFailOnInvalidXml() throws Exception {
        ByteArrayInputStream is = new ByteArrayInputStream("<openTag></butNoMatchedCloseTag>".getBytes());
        XML.read(is);
    }

    @Test
    public void canFindByXPath() throws Exception {
        final XML xml = XML.openResource("sample.xml", XmlDocumentTest.class);
        final String result = xml.findTextByXpath("//sampleChild");
        assertEquals("Some data", result);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void canFindByXPathWithNamespaces() throws Exception {
        final XmlParser parser = newParserFactory().setNamespaceAware(true).build();
        final XML xml = XML.read(JcpFile.open("sample.xml", XmlDocumentTest.class), parser);
        final String result = xml.findTextByXpath("//test:sampleChild", Pair.create("test", "http://www.modularit.co.uk/schema/TEST-1.0"));
        assertEquals("Some data", result);
    }

    @Test
    public void canFindByXPathInvalidXPath() throws Exception {
        assertEquals(null, XML.openResource("sample.xml", XmlDocumentTest.class).findTextByXpath(""));
    }
}
