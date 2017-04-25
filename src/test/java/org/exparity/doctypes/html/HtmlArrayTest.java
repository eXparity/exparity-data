package org.exparity.doctypes.html;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.exparity.data.HTML;
import org.exparity.data.html.HtmlSelector;
import org.exparity.data.types.Array;
import org.exparity.io.classpath.JcpFile;
import org.junit.Test;

/**
 * @author Stewart Bissett
 */
public class HtmlArrayTest {

    @Test
    public void canSelectArrays() throws Exception {
        HTML data = HTML.read(JcpFile.open("arrays.htm", HtmlDocumentTest.class));
        List<Array> arrays = data.findArrays(HtmlSelector.byAttributeValue("id", "select"));
        assertEquals(1, arrays.size());

        Array array = arrays.get(0);
        assertEquals(3, array.getNumOfValues());
        assertEquals("value1", array.getValueAsString(0));
        assertEquals("value2", array.getValueAsString(1));
        assertEquals("value3", array.getValueAsString(2));
    }

    @Test
    public void canSelectArrayFromUnorderedList() throws Exception {
        HTML data = HTML.read(JcpFile.open("arrays.htm", HtmlDocumentTest.class));
        List<Array> arrays = data.findArrays(HtmlSelector.byAttributeValue("id", "unordered"));
        assertEquals(1, arrays.size());

        Array array = arrays.get(0);
        assertEquals(3, array.getNumOfValues());
        assertEquals("value1", array.getValueAsString(0));
        assertEquals("value2", array.getValueAsString(1));
        assertEquals("value3", array.getValueAsString(2));
    }

    @Test
    public void canselectArrayFromOrderedList() throws Exception {
        HTML data = HTML.read(JcpFile.open("arrays.htm", HtmlDocumentTest.class));
        List<Array> arrays = data.findArrays(HtmlSelector.byAttributeValue("id", "ordered"));
        assertEquals(1, arrays.size());

        Array array = arrays.get(0);
        assertEquals(3, array.getNumOfValues());
        assertEquals("value1", array.getValueAsString(0));
        assertEquals("value2", array.getValueAsString(1));
        assertEquals("value3", array.getValueAsString(2));
    }

    @Test
    public void canSelectArrayFromDefinitionList() throws Exception {
        HTML data = HTML.read(JcpFile.open("arrays.htm", HtmlDocumentTest.class));
        List<Array> arrays = data.findArrays(HtmlSelector.byAttributeValue("id", "definition"));
        assertEquals(1, arrays.size());

        Array array = arrays.get(0);
        assertEquals(3, array.getNumOfValues());
        assertEquals("value1", array.getValueAsString(0));
        assertEquals("value2", array.getValueAsString(1));
        assertEquals("value3", array.getValueAsString(2));
    }
}
