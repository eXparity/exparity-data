package org.exparity.doctypes.html;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.exparity.data.HTML;
import org.exparity.data.html.HtmlSelector;
import org.exparity.data.types.Table;
import org.exparity.io.classpath.JcpFile;
import org.junit.Test;

/**
 * @author Stewart Bissett
 */
public class HtmlTableTest {

    @Test
    public void canReadSimpleTable() throws Exception {
        HTML data = HTML.read(JcpFile.open("tables.htm", HtmlDocumentTest.class));
        List<Table> tables = data.findTables(HtmlSelector.byAttributeValue("id", "simple"));
        assertEquals(1, tables.size());

        Table table = tables.get(0);
        assertEquals(false, table.hasHeader());
        assertEquals(2, table.getNumOfColumns());
        assertEquals(2, table.getNumOfRows());
        assertEquals("row0column0", table.getValueAsString(0, 0));
        assertEquals("row0column1", table.getValueAsString(0, 1));
        assertEquals("row1column0", table.getValueAsString(1, 0));
        assertEquals("row1column1", table.getValueAsString(1, 1));
    }

    @Test
    public void canReadTableWithDataInHeader() throws Exception {
        HTML data = HTML.read(JcpFile.open("tables.htm", HtmlDocumentTest.class));
        List<Table> tables = data.findTables(HtmlSelector.byAttributeValue("id", "headerWithTableData"));
        assertEquals(1, tables.size());

        Table table = tables.get(0);
        table.writeTo(System.out);
        assertEquals(false, table.hasHeader());
        assertEquals(2, table.getNumOfColumns());
        assertEquals(2, table.getNumOfRows());
        assertEquals("row0column0", table.getValueAsString(0, 0));
        assertEquals("row0column1", table.getValueAsString(0, 1));
        assertEquals("row1column0", table.getValueAsString(1, 0));
        assertEquals("row1column1", table.getValueAsString(1, 1));
    }

    @Test
    public void canReadSimpleTableWithNestedTags() throws Exception {
        HTML data = HTML.read(JcpFile.open("tables.htm", HtmlDocumentTest.class));
        List<Table> tables = data.findTables(HtmlSelector.byAttributeValue("id", "nested"));
        assertEquals(1, tables.size());

        Table table = tables.get(0);
        assertEquals(false, table.hasHeader());
        assertEquals(2, table.getNumOfColumns());
        assertEquals(2, table.getNumOfRows());
        assertEquals("row0column0", table.getValueAsString(0, 0));
        assertEquals("row0column1", table.getValueAsString(0, 1));
        assertEquals("row1column0", table.getValueAsString(1, 0));
        assertEquals("row1column1", table.getValueAsString(1, 1));
    }

    @Test
    public void canReadSimpleTableWithMixedRowSizes() throws Exception {
        HTML data = HTML.read(JcpFile.open("tables.htm", HtmlDocumentTest.class));
        List<Table> tables = data.findTables(HtmlSelector.byAttributeValue("id", "unbalanced"));
        assertEquals(1, tables.size());

        Table table = tables.get(0);
        assertEquals(false, table.hasHeader());
        assertEquals(2, table.getNumOfColumns());
        assertEquals(2, table.getNumOfRows());
        assertEquals("row0column0", table.getValueAsString(0, 0));
        assertEquals("", table.getValueAsString(0, 1));
        assertEquals("row1column0", table.getValueAsString(1, 0));
        assertEquals("row1column1", table.getValueAsString(1, 1));
    }

    @Test
    public void canReadComplexTable() throws Exception {
        HTML data = HTML.read(JcpFile.open("tables.htm", HtmlDocumentTest.class));
        List<Table> tables = data.findTables(HtmlSelector.byAttributeValue("id", "complex"));
        assertEquals(1, tables.size());

        Table table = tables.get(0);
        assertEquals(false, table.hasHeader());
        assertEquals(2, table.getNumOfColumns());
        assertEquals(3, table.getNumOfRows());
        assertEquals("row0column0", table.getValueAsString(0, 0));
        assertEquals("row0column1", table.getValueAsString(0, 1));
        assertEquals("row0column0", table.getValueAsString(1, 0));
        assertEquals("row1column1", table.getValueAsString(1, 1));
        assertEquals("row2column0", table.getValueAsString(2, 0));
        assertEquals("row2column0", table.getValueAsString(2, 1));
    }

    @Test
    public void canReadTableWithHeaderAndFooter() throws Exception {
        HTML data = HTML.read(JcpFile.open("tables.htm", HtmlDocumentTest.class));
        List<Table> tables = data.findTables(HtmlSelector.byAttributeValue("id", "sections"));
        assertEquals(1, tables.size());

        Table table = tables.get(0);
        assertEquals(2, table.getNumOfColumns());
        assertEquals(2, table.getNumOfRows());
        assertEquals(true, table.hasHeader());

        assertEquals("header", table.getHeaderAsString(0));
        assertEquals("header", table.getHeaderAsString(1));
        assertEquals("row0column0", table.getValueAsString(0, 0));
        assertEquals("row0column1", table.getValueAsString(0, 1));
        assertEquals("row1column0", table.getValueAsString(1, 0));
        assertEquals("row1column1", table.getValueAsString(1, 1));
    }
}
