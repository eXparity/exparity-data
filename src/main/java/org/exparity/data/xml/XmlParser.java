package org.exparity.data.xml;

import java.io.InputStream;

import org.exparity.data.XML;

/**
 * The {@link XmlParser} class is to allow consuming classes to replace the default {@link XmlParser} with one of their
 * own for example to setup a unit test or to setup a mock.
 *
 * @author Stewart Bissett
 */
public interface XmlParser {

    /**
     * Parse a XML file from an {@link InputStream}
     */
    public XML parse(final InputStream is);
}