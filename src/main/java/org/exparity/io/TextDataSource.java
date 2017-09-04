/*
 * 
 */

package org.exparity.io;

import java.io.InputStream;
import java.io.Reader;

/**
 * Use the {@link TextDataSource} interface to mark which IO sources represent plain text data sources
 * 
 * @author Stewart Bissett
 */
public interface TextDataSource {

    public String getText();

    public Reader getReader();

    public InputStream getStream();
}
