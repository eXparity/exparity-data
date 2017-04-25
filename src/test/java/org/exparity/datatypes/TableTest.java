package org.exparity.datatypes;

import static java.time.format.DateTimeFormatter.ofPattern;
import static org.exparity.dates.en.FluentLocalDate.AUG;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;

import org.exparity.data.BadFormatException;
import org.exparity.data.types.Scalar;
import org.exparity.data.types.Table;
import org.junit.Test;

/**
 * @author Stewart Bissett
 */
public class TableTest {

    final Scalar[][] data = { { new Scalar(1), new Scalar(2.0), new Scalar(Boolean.FALSE) },
            { new Scalar("A"), new Scalar("12:35"), new Scalar("01/08/2010") } };
    final String[] header = { "column1", "column2", "column3" };

    @Test
    public void canBuildTableNoHeader() throws ParseException {
        Table table = Table.withRows(data);

        verifyNoHeader(table);
        verifyTableData(table);
    }

    @Test
    public void canBuildTableWithHeader() throws ParseException {
        Table table = Table.withHeader(header).addRows(data);

        verifyTableHeader(table);
        verifyTableData(table);
    }

    @Test
    public void canSerializeNoHeader() throws BadFormatException, ParseException, IOException {
        final Table original = Table.withRows(data);
        verifyTableData(original);
        verifyNoHeader(original);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        original.writeTo(os);

        final Table restored = Table.from(new ByteArrayInputStream(os.toByteArray()), false);
        verifyTableData(restored);
        verifyNoHeader(restored);
    }

    @Test
    public void canSerializeWithHeader() throws BadFormatException, ParseException, IOException {
        final Table original = Table.withHeader(header).addRows(data);
        verifyTableData(original);
        verifyTableHeader(original);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        original.writeTo(os);

        final Table restored = Table.from(new ByteArrayInputStream(os.toByteArray()), true);
        verifyTableData(restored);
        verifyTableHeader(restored);
    }

    @Test
    public void canSupportMixedSizedRows() {
        Object[][] data = { { 1, "2", "three" }, { "4", 5 } };
        Table table = Table.withRows(data);
        assertEquals(new Integer(1), table.getValueAsInteger(0, 0));
        assertEquals("2", table.getValueAsString(0, 1));
        assertEquals("three", table.getValueAsString(0, 2));
        assertEquals("4", table.getValueAsString(1, 0));
        assertEquals(new Integer(5), table.getValueAsInteger(1, 1));
        assertNull("Expected null at index 1,2", table.getValue(1, 2));
    }

    @Test
    public void canSupportSortData() {
        final Scalar[][] data =
                { { new Scalar(4), new Scalar(5), new Scalar(6) }, { new Scalar(1), new Scalar(2), new Scalar(3) } };
        final String[] header = { "A", "B", "C" };
        final Table table = Table.withHeader(header).addRows(data);

        assertEquals("A", table.getHeaderAsString(0));
        assertEquals("B", table.getHeaderAsString(1));
        assertEquals("C", table.getHeaderAsString(2));
        assertEquals(new Integer(4), table.getValueAsInteger(0, 0));
        assertEquals(new Integer(5), table.getValueAsInteger(0, 1));
        assertEquals(new Integer(6), table.getValueAsInteger(0, 2));
        assertEquals(new Integer(1), table.getValueAsInteger(1, 0));
        assertEquals(new Integer(2), table.getValueAsInteger(1, 1));
        assertEquals(new Integer(3), table.getValueAsInteger(1, 2));

        Table sorted = table.sort(0);

        assertEquals("A", sorted.getHeaderAsString(0));
        assertEquals("B", sorted.getHeaderAsString(1));
        assertEquals("C", sorted.getHeaderAsString(2));
        assertEquals(new Integer(1), sorted.getValueAsInteger(0, 0));
        assertEquals(new Integer(2), sorted.getValueAsInteger(0, 1));
        assertEquals(new Integer(3), sorted.getValueAsInteger(0, 2));
        assertEquals(new Integer(4), sorted.getValueAsInteger(1, 0));
        assertEquals(new Integer(5), sorted.getValueAsInteger(1, 1));
        assertEquals(new Integer(6), sorted.getValueAsInteger(1, 2));
    }

    private void verifyTableData(final Table table) throws ParseException {
        assertEquals(false, table.isEmpty());
        assertEquals(2, table.getNumOfRows());
        assertEquals(3, table.getNumOfColumns());

        assertEquals(Long.valueOf(1L), table.getValueAsLong(0, 0));
        assertEquals(Integer.valueOf(1), table.getValueAsInteger(0, 0));
        assertEquals(2.0, table.getValueAsDouble(0, 1), 0.0);
        assertEquals(false, table.getValueAsBoolean(0, 2));
        assertEquals("A", table.getValueAsString(1, 0));
        assertEquals(AUG(1, 2010), table.getValueAsDate(1, 2, ofPattern("dd/MM/yyyy")));
    }

    private void verifyTableHeader(final Table table) {
        assertEquals(true, table.hasHeader());

        assertEquals("column1", table.getHeaderAsString(0));
        assertEquals(0, table.getHeaderIndex("column1"));
        assertEquals("column2", table.getHeaderAsString(1));
        assertEquals(1, table.getHeaderIndex("column2"));
        assertEquals("column3", table.getHeaderAsString(2));
        assertEquals(2, table.getHeaderIndex("column3"));
    }

    private void verifyNoHeader(final Table table) {
        assertEquals(false, table.hasHeader());
    }

}
