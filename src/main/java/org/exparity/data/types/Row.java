/*
 *
 */

package org.exparity.data.types;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;

/**
 * @author Stewart Bissett
 */
public class Row extends Array {

    public static final int COLUMN_NOT_FOUND = -1;

    /**
     * Transform a row object into another type
     */
    public interface RowTransform<T> {

        /**
         * Parse a row object into another type
         */
        public T apply(final Row row);
    }

    /**
     * Factory method to create a list of rows from a 2-dimensional scalar array
     * @param data a 2-dimensional {@link Scalar} array
     * @return a {@link List} of {@link Row}
     */
    public static List<Row> of(final Scalar[][] data) {
        return Arrays.stream(data).map(Row::of).collect(toList());
    }

    /**
     * Factory method to create a row from an array of scalars
     * @param data a {@link Scalar} array
     * @return a {@link Row}
     */
    public static Row of(final Scalar[] data) {
        return of(Array.of(data));
    }

    /**
     * Factory method to create a row from an array of scalars
     * @param data a {@link Scalar} array
     * @return a {@link Row}
     */
    public static Row of(final Object[] data) {
        return of(Array.of(data));
    }

    /**
     * Factory method to create a row from an array of scalars
     * @param data a {@link Scalar} array
     * @return a {@link Row}
     */
    public static Row of(final String[] data) {
        return of(Array.of(data));
    }

    /**
     * Factory method to create a row from an array
     * @param data a {@link Array}
     * @return a {@link Row}
     */
    public static Row of(final Array data) {
        return new Row(data);
    }

    private Row(final Row source) {
        super(source);
    }

    private Row(final Array source) {
        super(source);
    }

    /**
     * @return the number of columns
     */
    public int getNumOfColumns() {
        return super.getNumOfValues();
    }

    /**
     * Return the index of the column with the value specified or {@link Row#COLUMN_NOT_FOUND}
     */
    public int getColumnIndex(final String value) {
        for (int idx = 0; idx < getNumOfColumns(); ++idx) {
            if (getValueAsString(idx).equals(value)) {
                return idx;
            }
        }
        return COLUMN_NOT_FOUND;
    }

    /**
     * Return the value for a given cell by it's colum name or throw an exception if the column is not found
     */
    public Scalar getValue(final String columnName) {
        int columnIndex = this.getColumnIndex(columnName);
        if (columnIndex == COLUMN_NOT_FOUND) {
            throw new RuntimeException("No column with name '" + columnName + " found");
        } else {
            return super.getValue(columnIndex);
        }
    }

    public boolean isValidColumnIndex(final int index) {
        return super.getNumOfValues() > index;
    }

    public <T> T as(final RowTransform<T> transformer) {
        return transformer.apply(this);
    }
}
