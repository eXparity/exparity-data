/*
 * 
 */

package org.exparity.data.types;

/**
 * @author Stewart Bissett
 */
public class Column extends Array {

    private static final int ROW_NOT_FOUND = -1;

    public Column(final Scalar[] values) {
        super(values);
    }

    public Column(final Column source) {
        super(source);
    }

    /**
     * Return the index of the column with the value specified or {@link Column#ROW_NOT_FOUND} if no column exists with
     * that value
     */
    public int getRowIndex(final String value) {
        for (int idx = 0; idx < getNumOfRows(); ++idx) {
            if (getValueAsString(idx).equals(value)) {
                return idx;
            }
        }
        return ROW_NOT_FOUND;
    }

    /**
     * Return the number of rows which comprise this column
     */
    public int getNumOfRows() {
        return super.getNumOfValues();
    }
}
