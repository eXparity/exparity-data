/*
 *
 */

package org.exparity.data.xml;

import org.exparity.data.XML;

/**
 * @author Stewart Bissett
 */
public interface XmlFactory<T> {

    /**
     * Create an XML document from a source
     */
    XML convert(T source);
}
