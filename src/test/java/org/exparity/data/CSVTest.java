package org.exparity.data;

import static org.junit.Assert.assertEquals;

import org.exparity.io.classpath.JcpFile;
import org.junit.Test;

/**
 * @author Stewart Bissett
 */
public class CSVTest {

    @Test
    public void canCreateCSV() throws Exception {

        CSV data = CSV.read(JcpFile.open("/org/exparity/data/csv/sample.csv", CSVTest.class), false);

        assertEquals(2, data.getNumOfRows());
        assertEquals(6, data.getNumOfColumns());
        assertEquals(1.0956, data.getValueAsDouble(1, 0), 0.0);
        assertEquals(Integer.valueOf(1), data.getValueAsInteger(1, 1));
        assertEquals(Long.valueOf(1), data.getValueAsLong(1, 1));
        assertEquals("text", data.getValueAsString(1, 2));
        assertEquals("unquoted string", data.getValueAsString(1, 3));
        assertEquals("\"quoted string\"", data.getValueAsString(1, 4));
        assertEquals(Boolean.TRUE, data.getValueAsBoolean(1, 5));
    }
}
