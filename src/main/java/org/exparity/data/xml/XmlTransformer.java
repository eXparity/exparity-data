/*
 *
 */

package org.exparity.data.xml;

import org.exparity.data.XML;

/**
 * @author Stewart Bissett
 */
public interface XmlTransformer<T> {

    public T transform(final XML xml) throws TransformFailedException;
}
