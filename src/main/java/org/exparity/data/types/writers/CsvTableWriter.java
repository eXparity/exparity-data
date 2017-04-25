/*
 *
 */

package org.exparity.data.types.writers;

import java.io.IOException;
import java.io.OutputStream;

import org.exparity.data.CSV;
import org.exparity.data.types.Table;
import org.exparity.data.types.Table.TableWriter;

/**
 * Writes the contents of {@link org.exparity.data.types.Table}
 *
 * @author Stewart Bissett
 */
public class CsvTableWriter implements TableWriter {

    @Override
    public void write(final Table table, final OutputStream target) throws IOException {
        CSV.of(table).writeTo(target);
    }

}
