/*
 * 
 */

package org.exparity.io.internet;

import java.io.IOException;
import java.net.URL;

import org.apache.commons.lang.StringUtils;

/**
 * @author Stewart Bissett
 */
class HttpGet extends HttpCommand {

    HttpGet(final String file) {
        super(file);
    }

    HttpGet(final URL url) {
        super(url.toExternalForm());
    }

    @Override
    protected URL setUpURL(final String file, final String query) throws IOException {
        return StringUtils.isBlank(query) ? new URL(file) : new URL(file + "?" + query);
    }

    @Override
    protected String getRequestMethod() {
        return "GET";
    }

    @Override
    protected boolean isUploadingData() {
        return false;
    }
}
