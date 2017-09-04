/*
 *
 */

package org.exparity.io.internet;

import java.io.FileNotFoundException;
import java.net.URL;

/**
 * The Internet class provides an abstraction for internet related functions and actions. Create instances of the
 * internet using the InternetFactory class
 *
 * @author Stewart Bissett
 */
public interface Internet {

    /**
     * Get a url using HTTP
     */
    public InternetFile open(URL url, HttpHeader... properties) throws FileNotFoundException;

    /**
     * Get a url using HTTP
     */
    public InternetFile open(URL url, String userAgent, HttpHeader... properties) throws FileNotFoundException;

    /**
     * Get a url using HTTP
     */
    public InternetFile open(URL url, String userAgent, int maxRetries, HttpHeader... properties)
            throws FileNotFoundException,
            MaxRetryException;
}
