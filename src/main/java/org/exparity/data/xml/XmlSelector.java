/*
 *
 */

package org.exparity.data.xml;

import org.exparity.data.XML;

/**
 * @author Stewart Bissett
 */
public interface XmlSelector<T> {

    public T select(XML xml);

}
