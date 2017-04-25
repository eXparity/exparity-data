/*
 *
 */

package org.exparity.data.types.readers;

import java.io.IOException;
import java.io.InputStream;

import org.exparity.data.BadFormatException;
import org.exparity.data.CSV;
import org.exparity.data.types.Table;

/**
 * @author Stewart Bissett
 */
public class CsvTableReader implements org.exparity.data.types.Table.TableReader {
    @Override
    public Table read(final InputStream is, final boolean hasHeader) throws BadFormatException, IOException {
        return CSV.read(is, hasHeader).asTable();
    }
}
