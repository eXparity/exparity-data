/*
 *
 */

package org.exparity.data;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.exparity.data.types.Row;
import org.exparity.data.types.Row.RowTransform;
import org.exparity.data.types.Scalar;
import org.exparity.data.types.Table;
import org.exparity.io.TextDataSource;
import org.exparity.io.classpath.JcpFile;
import org.exparity.io.filesystem.FileSystemFile;
import org.exparity.io.internet.InternetFile;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

/**
 * Encapsulation of data held in comma-seperator or CSV format
 *
 * @author Stewart Bissett
 */
public class CSV {

    /**
     * Factory method to create an empty CsvDocument
     */
    public static CSV empty() {
        return new CSV(Table.empty());
    }

    /**
     * Factory method to instantiate a {@link CSV} instance from a URL.
     *
     * @param source The location of the classpatch resource
     * @param hasHeader <code>true</code> if there is a header within the data, <code>false</code> if there isn't
     * @return A Csv instance
     */
    public static CSV openResource(final String source, final boolean hasHeader) throws IOException {
        return read(JcpFile.open(source), hasHeader);
    }

    /**
     * Factory method to instantiate a {@link CSV} instance from a URL.
     *
     * @param source The location of the classpatch resource
     * @param hasHeader <code>true</code> if there is a header within the data, <code>false</code> if there isn't
     * @return A Csv instance
     */
    public static CSV openResource(final String source, final Class<?> klass, final boolean hasHeader)
            throws IOException {
        return read(JcpFile.open(source, klass), hasHeader);
    }

    /**
     * Factory method to instantiate a {@link CSV} instance from a URL.
     *
     * @param source The location of the classpatch resource
     * @param hasHeader <code>true</code> if there is a header within the data, <code>false</code> if there isn't
     * @return A Csv instance
     */
    public static CSV openResource(final String source, final ClassLoader loader, final boolean hasHeader)
            throws IOException {
        return read(JcpFile.open(source, loader), hasHeader);
    }

    /**
     * Factory method to instantiate a {@link CSV} instance from a URL.
     *
     * @param source The location of the classpatch resource
     * @param hasHeader <code>true</code> if there is a header within the data, <code>false</code> if there isn't
     * @return A Csv instance
     */
    public static CSV openURL(final String source, final boolean hasHeader) throws IOException {
        return read(InternetFile.open(source), hasHeader);
    }

    /**
     * Factory method to instantiate a {@link CSV} instance from a URL.
     *
     * @param source The location of the classpatch resource
     * @param hasHeader <code>true</code> if there is a header within the data, <code>false</code> if there isn't
     * @return A Csv instance
     */
    public static CSV openURL(final URL source, final boolean hasHeader) throws IOException {
        return read(InternetFile.open(source), hasHeader);
    }

    /**
     * Factory method to instantiate a {@link CSV} instance from a URL.
     *
     * @param source The location of the classpatch resource
     * @param hasHeader <code>true</code> if there is a header within the data, <code>false</code> if there isn't
     * @return A Csv instance
     */
    public static CSV openFile(final String source, final boolean hasHeader) throws IOException {
        return read(FileSystemFile.open(source), hasHeader);
    }

    /**
     * Factory method to instantiate a {@link CSV} instance from a URL.
     *
     * @param source The location of the classpatch resource
     * @param hasHeader <code>true</code> if there is a header within the data, <code>false</code> if there isn't
     * @return A Csv instance
     */
    public static CSV openFile(final File source, final boolean hasHeader) throws IOException {
        return read(FileSystemFile.open(source), hasHeader);
    }

    /**
     * Factory method to instantiate a {@link CSV} instance from a {@link String}.
     *
     * @param source The data encoded in CSV format
     * @param hasHeader <code>true</code> if there is a header within the data, <code>false</code> if there isn't
     * @return A Csv instance
     */
    public static CSV read(final Reader source, final boolean hasHeader) throws IOException {
        CSVReader reader = new CSVReader(source);
        try {
            return read(reader, hasHeader);
        } finally {
            reader.close();
        }
    }

    /**
     * Factory method to instantiate a {@link CSV} instance from an {@link InputStream}
     *
     * @param source The data encoded in CSV format
     * @param hasHeader <code>true</code> if there is a header within the data, <code>false</code> if there isn't
     * @return A Csv instance
     */
    public static CSV read(final InputStream source, final boolean hasHeader) throws IOException {
        CSVReader reader = new CSVReader(new InputStreamReader(source));
        try {
            return read(reader, hasHeader);
        } finally {
            reader.close();
        }
    }

    /**
     * Factory method to instantiate a {@link CSV} instance from a {@link TextDataSource}.
     *
     * @param source The data encoded in CSV format
     * @param hasHeader <code>true</code> if there is a header within the data, <code>false</code> if there isn't
     * @return A {@link CSV} instance
     * @throws BadFormatException Thrown if data is not valid CSV data
     */
    public static CSV read(final TextDataSource source, final boolean hasHeader) throws IOException {
        CSVReader reader = new CSVReader(source.getReader());
        try {
            return read(reader, hasHeader);
        } finally {
            reader.close();
        }
    }

    private static CSV read(final CSVReader reader, final boolean hasHeader) throws IOException {
        Table table = Table.empty();
        while (true) {
            String[] row = reader.readNext();
            if (row == null) {
                break;
            } else if (row.length > 0) {
                if (hasHeader && !table.hasHeader()) {
                    table = table.setHeader(row);
                } else {
                    table = table.addRow(row);
                }
            }
        }
        return new CSV(table);
    }

    public static CSV of(final Table table) {
        return new CSV(table);
    }

    private final Table table;

    private CSV(final Table table) {
        this.table = table;
    }

    /**
     * Write the contents of the {@link CSV} out to the target {@link OutputStream}
     */
    public void writeTo(final OutputStream target) throws IOException {
        try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(target))) {
            if (table.hasHeader()) {
                writer.writeNext(table.getHeader().toArray(new String[0]));
            }
            table.rows().forEach(row -> writer.writeNext(row.asStringArray()));
        }
    }

    public boolean isEmpty() {
        return table.isEmpty();
    }

    public int getNumOfRows() {
        return table.getNumOfRows();
    }

    public int getNumOfColumns() {
        return table.getNumOfColumns();
    }

    public String getValueAsString(final int rowId, final int columnId) {
        return table.getValueAsString(rowId, columnId);
    }

    /**
     * @param rowId The row id of the cell value to get.
     * @param columnId The column id of the cell value to get.
     * @return The value of the field at the specified row and column or {@link ArrayIndexOutOfBoundsException} if the
     *         table does not contain the request cell
     */
    public Long getValueAsLong(final int rowId, final int columnId) {
        return table.getValueAsLong(rowId, columnId);
    }

    /**
     * @param rowId The row id of the cell value to get.
     * @param columnId The column id of the cell value to get.
     * @return The value of the field at the specified row and column or {@link ArrayIndexOutOfBoundsException} if the
     *         table does not contain the request cell
     */
    public Integer getValueAsInteger(final int rowId, final int columnId) {
        return table.getValueAsInteger(rowId, columnId);
    }

    /**
     * Get the specified row parsed into the objects defined by the row parser
     */
    public <T> T getRow(final int rowId, final RowTransform<T> parser) {
        return table.getRow(rowId, parser);
    }

    /**
     * Return the value of the header for the given index or return null if this document does not have a header
     */
    public String getHeader(final int headerId) {
        if (table.hasHeader()) {
            return table.getHeaderAsString(headerId);
        } else {
            return null;
        }
    }

    /**
     * Return the value of the header for the given index or return null if this document does not have a header
     */
    public int getColumnIndexByHeader(final String label) {
        if (table.hasHeader()) {
            int idx = 0;
            for (String header : getHeaders()) {
                if (header.equalsIgnoreCase(label)) {
                    return idx;
                }
                ++idx;
            }
            return Row.COLUMN_NOT_FOUND;
        } else {
            return Row.COLUMN_NOT_FOUND;
        }
    }

    /**
     * Return the header
     */
    public String[] getHeaders() {
        if (!table.hasHeader()) {
            return null;
        }

        final String[] header = new String[table.getNumOfColumns()];
        for (int i = 0; i < table.getNumOfColumns(); ++i) {
            header[i] = table.getHeaderAsString(i);
        }
        return header;
    }

    /**
     * Return an iterator which iterates over each row in the csv
     */
    public Iterable<Row> rowIterator() {
        return table.rowIterator();
    }

    public boolean hasColumn(final int index) {
        return table.hasColumn(index);
    }

    public Table asTable() {
        return table;
    }

    public boolean hasHeader() {
        return table.hasHeader();
    }

    public int getHeaderIndex(final String name) {
        return table.getHeaderIndex(name);
    }

    public String getHeaderAsString(final int headerId) {
        return table.getHeaderAsString(headerId);
    }

    public List<String> getHeader() {
        return table.getHeader();
    }

    public Scalar getValue(final int rowId, final int columnId) {
        return table.getValue(rowId, columnId);
    }

    public boolean getValueAsBoolean(final int rowId, final int columnId) {
        return table.getValueAsBoolean(rowId, columnId);
    }

    public double getValueAsDouble(final int rowId, final int columnId) {
        return table.getValueAsDouble(rowId, columnId);
    }

    public LocalDate getValueAsDate(final int rowId, final int columnId, final DateTimeFormatter format) {
        return table.getValueAsDate(rowId, columnId, format);
    }

    public LocalTime getValueAsTime(final int rowId, final int columnId) {
        return table.getValueAsTime(rowId, columnId);
    }

    public Row getRow(final int rowId) {
        return table.getRow(rowId);
    }

    public Stream<String> header() {
        return table.header();
    }

    public Stream<Row> rows() {
        return table.rows();
    }

    public Map<String, String> asMap(final int keyIndex, final int valueIndex) {
        return table.asMap(keyIndex, valueIndex);
    }
}
