/*
 * 
 */

package org.exparity.io.internet;

/**
 * @author Stewart Bissett
 */
public class InternetException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InternetException(String errorMessage) {
        super(errorMessage);
    }

    public InternetException(Exception e) {
        super(e);
    }

    public InternetException(String errorMessage, Exception e) {
        super(errorMessage + " [" + e.toString() + "]", e);
    }
}
