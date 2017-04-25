package org.exparity.io.internet;

import static org.junit.Assert.assertNotNull;

import org.exparity.io.internet.InternetFile;
import org.exparity.io.internet.UserAgents;
import org.junit.Test;

/**
 * @author Stewart Bissett
 */
public class InternetFileTest {

    @Test
    public void canReturnEmptyInternetFile() {
        InternetFile file = InternetFile.openOrEmpty("A bad url", UserAgents.IE8);
        assertNotNull(file.getUrl());
    }

}
