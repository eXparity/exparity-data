/*
 * 
 */

package org.exparity.io.utils;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.exparity.data.types.KeyValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Stewart Bissett
 */
public class UrlUtils {

    private static final Logger LOG = LoggerFactory.getLogger(UrlUtils.class);

    public static URL parse(final URL base, final String unformatted) throws URISyntaxException, MalformedURLException {
        LOG.trace("Parsing raw URL [" + unformatted + "] to URL relative to [" + base + "]");
        URI uri = base.toURI().resolve(unformatted);
        uri = uri.normalize();
        LOG.trace("Parsed raw URL [" + unformatted + "] to [" + uri + "]");
        return uri.toURL();
    }

    public static URL parse(final String file, final String redirect) throws MalformedURLException, URISyntaxException {
        return parse(new URL(file), redirect);
    }

    public static List<KeyValue<String, String>> getQueryArgumentsFromQueryString(final String queryString)
            throws UnsupportedEncodingException {
        List<KeyValue<String, String>> args = new ArrayList<>();

        String decoded = URLDecoder.decode(queryString, "UTF-8");
        String[] pairs = StringUtils.split(decoded, "&");
        for (String pair : pairs) {
            String[] keyValue = StringUtils.split(pair, "=");
            if (keyValue.length == 1) {
                args.add(KeyValue.create(keyValue[0], StringUtils.EMPTY));
            } else if (keyValue.length == 2) {
                args.add(KeyValue.create(keyValue[0], keyValue[1]));
            }
        }

        return args;
    }

}
