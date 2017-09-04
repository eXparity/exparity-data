/*
 * 
 */

package org.exparity.data.xml;

/**
 * @author Stewart Bissett
 */
public class MarshallerConfigurationError extends Error {

    private static final long serialVersionUID = 1L;

    public MarshallerConfigurationError(final Exception e) {
        super(e);
    }

}
