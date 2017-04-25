/*
 *
 */

package org.exparity.data.xml;

import org.exparity.data.BadFormatException;
import org.exparity.data.XML;

/**
 * @author Stewart Bissett
 */
public interface XmlMarshaller<T> {

    public T unmarshal(final XML xml) throws BadFormatException, MarshallFailedException;

    public XML marshall(final T raw) throws MarshallFailedException;

}
