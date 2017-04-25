/*
 *
 */

package org.exparity.data;

/**
 * @author Stewart Bissett
 */
public class BadConversionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public BadConversionException(final String errorMessage) {
        super(errorMessage);
    }

}
