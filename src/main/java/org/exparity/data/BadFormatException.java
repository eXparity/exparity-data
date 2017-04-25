/**
 *
 */

package org.exparity.data;

/**
 * Exception thrown when the format of the data being parsed does not match the expectation of the parser
 *
 * @author Stewart Bissett
 */
public class BadFormatException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private String raw;

    public BadFormatException(final String errorMessage, final String raw) {
        super(errorMessage);
        this.raw = raw;
    }

    public BadFormatException(final String errorMessage) {
        super(errorMessage);
    }

    public BadFormatException(final Exception exception) {
        super(exception);
    }

    public BadFormatException(final String errorMessage, final Exception exception) {
        super(errorMessage, exception);
    }

    public String getRaw() {
        return raw;
    }

}
