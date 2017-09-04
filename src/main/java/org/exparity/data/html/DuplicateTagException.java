/*
 * 
 */

package org.exparity.data.html;

/**
 * @author Stewart Bissett
 */
public class DuplicateTagException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DuplicateTagException(final String errorMessage) {
        super(errorMessage);
    }

}
