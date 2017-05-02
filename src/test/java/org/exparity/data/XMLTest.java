package org.exparity.data;

import static org.exparity.data.xml.parser.XmlParserBuilder.newParserFactory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.exparity.data.types.Pair;
import org.exparity.data.xml.XmlParser;
import org.exparity.io.classpath.JcpFile;
import org.junit.Test;

/**
 * @author Stewart Bissett
 */
public class XMLTest {

    @Test
    public void canCreateXml() throws Exception {
        assertNotNull(XML.openResource("/org/exparity/data/xml/sample.xml", XML.class));
    }

    @Test(expected = BadFormatException.class)
    public void canFailOnInvalidXml() throws Exception {
        XML.of("<openTag></butNoMatchedCloseTag>");
    }

    @Test
    public void canFindByXPath() throws Exception {
        final XML xml = XML.openResource("/org/exparity/data/xml/sample.xml", XML.class);
        final String result = xml.findTextByXpath("//sampleChild");
        assertEquals("Some data", result);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void canFindByXPathWithNamespaces() throws Exception {
        final XmlParser parser = newParserFactory().setNamespaceAware(true).build();
        final XML xml = XML.read(JcpFile.open("/org/exparity/data/xml/sample.xml", XML.class), parser);
        final String result = xml.findTextByXpath("//test:sampleChild", Pair.create("test", "http://www.modularit.co.uk/schema/TEST-1.0"));
        assertEquals("Some data", result);
    }

    @Test
    public void canFindByXPathInvalidXPath() throws Exception {
        assertEquals(null, XML.openResource("/org/exparity/data/xml/sample.xml", XML.class).findTextByXpath(""));
    }
}
