/*
 *
 */

package org.exparity.data.types.writers;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.exparity.data.types.Row;
import org.exparity.data.types.Table;
import org.exparity.data.types.Table.TableWriter;

/**
 * Writes the contents of {@link org.exparity.data.types.Table}
 *
 * @author Stewart Bissett
 */
public class HtmlTableWriter implements TableWriter {

    @Override
    public void write(final Table table, final OutputStream target) throws IOException {
        PrintStream ps = new PrintStream(target);
        ps.print("<table>");

        if (table.hasHeader()) {
            ps.print("<tr>");
            table.header().forEach(header -> ps.print("<th>" + header + "</th>"));
            ps.print("</tr>");
        }

        for (Row row : table.rowIterator()) {
            ps.print("<tr>");
            row.values().forEach(cell -> ps.print("<td>" + cell.getValueAsString() + "</td>"));
            ps.print("</tr>");

        }

        ps.print("</table>");
    }

}
