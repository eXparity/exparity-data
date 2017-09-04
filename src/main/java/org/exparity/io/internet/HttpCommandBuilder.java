/*
 * 
 */

package org.exparity.io.internet;

import java.net.URL;

/**
 * @author Stewart Bissett
 */
public class HttpCommandBuilder {

    private final HttpURLConnectionFactory factory;

    public HttpCommandBuilder(final HttpURLConnectionFactory factory) {
        this.factory = factory;
    }

    public HttpCommand newHttpGet(final String file) {
        return new HttpGet(file).setCommandBuilder(this).setConnectionFactory(factory);
    }

    public HttpCommand newHttpGet(final URL url) {
        return new HttpGet(url).setCommandBuilder(this).setConnectionFactory(factory);
    }

    public HttpCommand newHttpPost(final String file) {
        return new HttpPost(file).setCommandBuilder(this).setConnectionFactory(factory);
    }

}
