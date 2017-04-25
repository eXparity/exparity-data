/*
 *
 */

package org.exparity.data.types;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.Validate;
import org.exparity.data.BadFormatException;
import org.exparity.data.types.Row.RowTransform;
import org.exparity.data.types.readers.CsvTableReader;
import org.exparity.data.types.transforms.TableToMapTransform;
import org.exparity.data.types.writers.CsvTableWriter;

/**
 * The Table class represents tabular data comprising rows and columns. If available a header row is also present
 *
 * @author Stewart Bissett
 */
public class Table {

    /**
     * Reader to read a {@link Table} for classes to implement who want to restore it's content from an input stream.
     */
    public interface TableReader {

        /**
         * Read the contents of a {@link Table} from an {@link InputStream}
         */
        public Table read(final InputStream is, final boolean hasReader) throws BadFormatException, IOException;
    }

    /**
     * Writer to write a {@link Table} in an output stream
     */
    public interface TableWriter {

        /**
         * Write the contents of a {@link Table} to a {@link OutputStream}
         */
        public void write(final Table table, final OutputStream target) throws IOException;
    }

    public interface TableTransform<T> {

        /**
         * Map the contents of a Table to a new data type
         */
        public T apply(Table table);
    }

    /**
     * Constant returned when a header is not found
     */
    public static final int HEADER_NOT_FOUND = -1;

    private static final TableWriter DEFAULT_TABLE_WRITER = new CsvTableWriter();
    private static final TableReader DEFAULT_TABLE_READER = new CsvTableReader();

    /**
     * Restore a {@link Table} from an {@link InputStream} using the default {@link TableReader}
     */
    public static Table from(final InputStream is, final boolean hasHeader) throws BadFormatException, IOException {
        return from(is, hasHeader, DEFAULT_TABLE_READER);
    }

    /**
     * Restore a {@link Table} from an {@link InputStream} using the specified {@link TableReader}
     */
    public static Table from(final InputStream is, final boolean hasHeader, final TableReader reader)
            throws BadFormatException, IOException {
        return reader.read(is, hasHeader);
    }

    /**
     * Static factory method to create an empty table
     */
    public static Table empty() {
        return new Table();
    }

    /**
     * Static factory method to create a new table with a header
     * @param header a {@link String} array of header values
     * @return a {@link Table} instance
     */
    public static Table withHeader(final String[] header) {
        return new Table().setHeader(header);
    }

    /**
     * Static factory method to create a new table with a header
     * @param header a {@link Collection} of {@link String} header values
     * @return a {@link Table} instance
     */
    public static Table withHeader(final Collection<String> header) {
        return new Table().addHeader(header);
    }

    /**
     * Static factory method to create a new table with a header
     * @param header a {@link Collection} of {@link String} header values
     * @return a {@link Table} instance
     */
    public static Table withHeaders(final Collection<String> header) {
        return new Table().addHeader(header);
    }

    public static Table withRow(final Collection<String> row) {
        return new Table().addRow(row);
    }

    public static Table withRow(final Row row) {
        return new Table().addRow(row);
    }

    public static Table withRow(final Array row) {
        return new Table().addRow(row);
    }

    public static Table withRow(final Scalar[] row) {
        return new Table().addRow(row);
    }

    public static Table withRow(final String[] row) {
        return new Table().addRow(row);
    }

    public static Table withRow(final Object[] row) {
        return new Table().addRow(row);
    }

    public static Table withRows(final List<Row> rows) {
        return new Table().addRows(rows);
    }

    public static Table withRows(final Scalar[][] rows) {
        return new Table().addRows(rows);
    }

    public static Table withRows(final Object[][] rows) {
        return new Table().addRows(rows);
    }

    public static Table withRows(final String[][] rows) {
        return new Table().addRows(rows);
    }

    private final List<Row> rows;
    private final List<String> header;

    private Table(final List<String> header, final List<Row> rows) {
        Validate.notNull(header, "Header cannot be null");
        Validate.notNull(rows, "Rows cannot be null");
        this.header = header;
        this.rows = rows;
    }

    private Table() {
        this(new ArrayList<String>(), new ArrayList<Row>());
    }

    /**
     * @return If this table has a header or not
     */
    public boolean hasHeader() {
        return CollectionUtils.isNotEmpty(header);
    }

    /**
     * Finds the header in this table which matches the supplied name. The match is case specific.
     *
     * @param name The name of the column to find
     *
     * @return The index of the header or -1 if the header is not found or the table has no header at all
     */
    public int getHeaderIndex(final String name) {
        return header.indexOf(name);
    }

    /**
     * @param headerId The index of the header to return.
     *
     * @return The value of the header field or throws null if header column not found.
     */
    public String getHeaderAsString(final int headerId) {
        return (headerId >= 0 && headerId < header.size()) ? header.get(headerId) : null;
    }

    /**
     * @return A copy of the header or an empty collection if the header is null.
     */
    public List<String> getHeader() {
        return Collections.unmodifiableList(header);
    }

    /**
     * @param rowId The row id of the cell value to get.
     * @param columnId The column id of the cell value to get.
     *
     * @return The value of the field at the specified row and column or null if the table does not contain the request
     *         cell
     */
    public Scalar getValue(final int rowId, final int columnId) {
        return (rowId >= 0 && rowId < rows.size()) ? rows.get(rowId).getValue(columnId) : null;
    }

    /**
     * @param rowId The row id of the cell value to get.
     * @param columnId The column id of the cell value to get.
     *
     * @return The value of the field at the specified row and column or null if the table does not contain the request
     *         cell
     */
    public String getValueAsString(final int rowId, final int columnId) {
        return (rowId >= 0 && rowId < rows.size()) ? rows.get(rowId).getValueAsString(columnId) : null;
    }

    /**
     * @param rowId The row id of the cell value to get.
     * @param columnId The column id of the cell value to get.
     *
     * @return The value of the field at the specified row and column or null if the table does not contain the request
     *         cell
     */
    public boolean getValueAsBoolean(final int rowId, final int columnId) {
        return (rowId >= 0 && rowId < rows.size()) ? rows.get(rowId).getValueAsBoolean(columnId) : null;
    }

    /**
     * @param rowId The row id of the cell value to get.
     * @param columnId The column id of the cell value to get.
     *
     * @return The value of the field at the specified row and column or null if the table does not contain the request
     *         cell
     */
    public double getValueAsDouble(final int rowId, final int columnId) {
        return (rowId >= 0 && rowId < rows.size()) ? rows.get(rowId).getValueAsDouble(columnId) : null;
    }

    /**
     * @param rowId The row id of the cell value to get.
     * @param columnId The column id of the cell value to get.
     *
     * @return The value of the field at the specified row and column or null if the table does not contain the request
     *         cell
     */
    public Long getValueAsLong(final int rowId, final int columnId) {
        return (rowId >= 0 && rowId < rows.size()) ? rows.get(rowId).getValueAsLong(columnId) : null;
    }

    /**
     * @param rowId The row id of the cell value to get.
     * @param columnId The column id of the cell value to get.
     *
     * @return The value of the field at the specified row and column or null if the table does not contain the request
     *         cell
     */
    public Integer getValueAsInteger(final int rowId, final int columnId) {
        return (rowId >= 0 && rowId < rows.size()) ? rows.get(rowId).getValueAsInteger(columnId) : null;
    }

    /**
     * @param rowId The row id of the cell value to get.
     * @param columnId The column id of the cell value to get.
     * @param format date formats which will be used to parse the cell value
     *
     * @return The value of the field at the specified row and column or null if the table does not contain the request
     *         cell
     */
    public LocalDate getValueAsDate(final int rowId, final int columnId, final DateTimeFormatter format) {
        return (rowId >= 0 && rowId < rows.size()) ? rows.get(rowId).getValueAsDate(columnId, format) : null;
    }

    /**
     * @param rowId The row id of the cell value to get.
     * @param columnId The column id of the cell value to get.
     *
     * @return The value of the field at the specified row and column or null if the table does not contain the request
     *         cell
     */
    public LocalTime getValueAsTime(final int rowId, final int columnId) {
        return (rowId >= 0 && rowId < rows.size()) ? rows.get(rowId).getValueAsTime(columnId) : null;
    }

    /**
     * @return An iterable collection comprising one entry per {@link Row} from this table
     */
    public Iterable<Row> rowIterator() {
        return Collections.unmodifiableList(rows);
    }

    /**
     * @return An the {@link Row} at the specified index or null if there is no row with the specified index.
     */
    public Row getRow(final int rowId) {
        return (rowId >= 0 && rowId < rows.size()) ? rows.get(rowId) : null;
    }

    public <T> T getRow(final int rowId, final RowTransform<T> transform) {
        return getRow(rowId).as(transform);
    }

    /**
     * @return The number of columns
     */
    public int getNumOfColumns() {
        return rows.isEmpty() ? 0 : rows.get(0).getNumOfValues();
    }

    /**
     * @return The number of rows.
     */
    public int getNumOfRows() {
        return rows.size();
    }

    /**
     * @return <code>true</code> if the table has no data and <code>false</code> if has data.
     */
    public boolean isEmpty() {
        return rows.isEmpty() && header.isEmpty();
    }

    /**
     * Return the header as a stream of strings
     * @return a {@link Stream} of {@link String}
     */
    public Stream<String> header() {
        return header.stream();
    }

    /**
     * Return the data rows as a stream of rows
     * @return a {@link Stream} of {@link Row}
     */
    public Stream<Row> rows() {
        return rows.stream();
    }

    /**
     * Write the contents of the table to the specified stream using a new line beween rows and a comma between columns
     */
    public void writeTo(final OutputStream out) throws IOException {
        writeTo(out, DEFAULT_TABLE_WRITER);
    }

    /**
     * Write the contents of the table to the specified stream using the supplied TableWriter
     *
     * @param out The stream to write the {@link Table} to.
     */
    public void writeTo(final OutputStream out, final TableWriter writer) throws IOException {
        writer.write(this, out);
    }

    public Table sort(final int columnIdToSortBy) {
        List<Row> sortable = new ArrayList<>(this.rows);
        Collections.sort(sortable, new CompareRowsAtColumnId(columnIdToSortBy));
        return new Table(header, sortable);
    }

    public Table addHeader(final Collection<String> header) {
        return new Table(new ArrayList<>(header), rows);
    }

    public Table setHeader(final String[] header) {
        return addHeader(Arrays.asList(header));
    }

    public Table addRow(final Row row) {
        List<Row> newRows = new ArrayList<>(this.rows);
        newRows.add(row);
        return new Table(header, newRows);
    }

    public Table addRow(final Collection<String> row) {
        return new Table(header, rows);
    }

    public Table addRow(final Array data) {
        return addRow(Row.of(data));
    }

    public Table addRow(final Scalar[] row) {
        return addRow(Row.of(row));
    }

    public Table addRow(final String[] row) {
        return addRow(Row.of(row));
    }

    public Table addRow(final Object[] row) {
        return addRow(Row.of(row));
    }

    public Table addRows(final List<Row> rows) {
        List<Row> newRows = new ArrayList<>(this.rows);
        newRows.addAll(rows);
        return new Table(header, newRows);
    }

    public Table addRows(final Scalar[][] row) {
        return addRows(Arrays.stream(row).map(Array::of).map(Row::of).collect(toList()));
    }

    public Table addRows(final Object[][] row) {
        return addRows(Arrays.stream(row).map(Array::of).map(Row::of).collect(toList()));
    }

    public Table addRows(final String[][] row) {
        return addRows(Arrays.stream(row).map(Array::of).map(Row::of).collect(toList()));
    }

    public boolean hasColumn(final int index) {
        return getNumOfColumns() > index;
    }

    public <T> T as(final TableTransform<T> transform) {
        return transform.apply(this);
    }

    public Map<String, String> asMap(final int keyIndex, final int valueIndex) {
        return as(new TableToMapTransform(keyIndex, valueIndex));
    }


    @Override
    public String toString() {
        return "Table [" + getNumOfRows() + "x" + getNumOfColumns() + "]";
    }

    /**
     * @author Stewart Bissett
     */
    private final class CompareRowsAtColumnId implements Comparator<Row> {
        private final int columnIdToSortBy;

        private CompareRowsAtColumnId(final int columnIdToSortBy) {
            this.columnIdToSortBy = columnIdToSortBy;
        }

        @Override
        public int compare(final Row o1, final Row o2) {
            Scalar valueA = o1.getValue(columnIdToSortBy);
            Scalar valueB = o2.getValue(columnIdToSortBy);
            return valueA != null ? valueA.compareTo(valueB) : valueB != null ? -1 : 0;
        }
    }
}
