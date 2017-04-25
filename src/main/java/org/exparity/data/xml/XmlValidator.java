/*
 *
 */

package org.exparity.data.xml;

import org.exparity.data.XML;

/**
 * @author Stewart Bissett
 */
public interface XmlValidator {
    public ValidationResult validate(final XML document);
}
