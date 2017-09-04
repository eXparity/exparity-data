/**
 * 
 */

package org.exparity.io.internet;

/**
 * @author Stewart Bissett
 */
public interface Content {

    public String getText() throws ContentConversionException;

    public byte[] getBytes();
}
