/*
 * 
 */

package org.exparity.io.internet;

import org.exparity.data.types.KeyValue;

/**
 * @author Stewart Bissett
 */
public class HttpHeader extends KeyValue<String, String> {

    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_ENCODING = "Content-Encoding";
    public static final String USER_AGENT = "User-Agent";
    public static final String COOKIE = "Cookie";
    public static final String SET_COOKIE = "Set-Cookie";
    public static final String LOCATION = "Location";
    public static final String REFERER = "Referer";

    public HttpHeader(final String key, final String value) {
        super(key, value);
    }
}
