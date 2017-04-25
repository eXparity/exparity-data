/*
 *
 */

package org.exparity.io.internet;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.exparity.data.types.KeyValue;
import org.exparity.io.BinaryDataSource;
import org.exparity.io.TextDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link InternetFile} represents a file loaded loaded via a {@link URL} and exposes factory for open files from the
 * internet. The factory methods execute HTTP GET requests.
 *
 * @author Stewart Bissett
 */
public class InternetFile implements TextDataSource, BinaryDataSource {

    private static final String DEFAULT_USER_AGENT = UserAgents.IE8;
    private static final int READ_TIMEOUT = 10000;
    private static final int CONNECTION_TIMEOUT = 5000;
    private static final Logger LOG = LoggerFactory.getLogger(InternetFile.class);
    private static final ContentTypeFactory CONTENT_TYPE_FACTORY = new ContentTypeFactory();

    private final byte[] bytes;
    private final HttpHeaders headers;
    private final URL url;
    private final ContentType contentType;
    private ContentEncoding encoding;
    private String text;

    /**
     * Open a file using the supplied {@link URL}.
     *
     * @param url The {@link URL} identifying the file to open
     * @param userAgent The user agent to use when sending the request to fetch the file
     * @param headers Any HTTP headers to be sent with the request
     *
     * @return The file or an exception is thrown if the file could not be loaded
     */
    public static InternetFile open(final Internet iNet,
            final URL url,
            final String userAgent,
            final HttpHeader... headers) throws FileNotFoundException {
        return iNet.open(url, userAgent, headers);
    }

    /**
     * Open a file using the supplied {@link URL}.
     *
     * @param url The {@link URL} identifying the file to open
     * @param userAgent The user agent to use when sending the request to fetch the file
     * @param headers Any HTTP headers to be sent with the request
     *
     * @return The file or an exception is thrown if the file could not be loaded
     */
    public static InternetFile open(final Internet iNet, final URL url, final HttpHeader... headers)
            throws FileNotFoundException {
        return open(iNet, url, DEFAULT_USER_AGENT, headers);
    }

    /**
     * Open a file using the supplied {@link URL}.
     *
     * @param url The {@link URL} identifying the file to open
     * @param userAgent The user agent to use when sending the request to fetch the file
     * @param headers Any HTTP headers to be sent with the request
     *
     * @return The file or an exception is thrown if the file could not be loaded
     */
    public static InternetFile open(final URL url, final String userAgent, final HttpHeader... headers)
            throws FileNotFoundException {
        return open(Internets.newInstance(), url, userAgent, headers);
    }

    /**
     * Open a file using the supplied {@link URL}.
     *
     * @param url The {@link URL} identifying the file to open
     * @param headers Any HTTP headers to be sent with the request
     *
     * @return The file or an exception is thrown if the file could not be loaded
     */
    public static InternetFile open(final URL url, final HttpHeader... headers) throws FileNotFoundException {
        return open(url, DEFAULT_USER_AGENT, headers);
    }

    /**
     * Open a file from an unparsed URL
     *
     * @param url The URL identifying the file to open
     * @param userAgent The user agent to use when sending the request to fetch the file
     * @param headers Any HTTP headers to be sent with the request
     *
     * @return The file or an exception is thrown if the file could not be loaded
     */
    public static InternetFile open(final String url, final String userAgent, final HttpHeader... headers)
            throws IOException {
        return open(new URL(url), userAgent, headers);
    }

    /**
     * Open a file from an unparsed URL
     *
     * @param url The {@link URL} identifying the file to open
     * @param headers Any HTTP headers to be sent with the request
     *
     * @return The file or an exception is thrown if the file could not be loaded
     */
    public static InternetFile open(final String url, final HttpHeader... headers) throws IOException {
        return open(url, DEFAULT_USER_AGENT, headers);
    }

    /**
     * Open a file from an unparsed URL. If an error is encountered whilst loading the file an empty file will be
     * returned.
     *
     * @param url The URL identifying the file to open
     * @param userAgent The user agent to use when sending the request to fetch the file
     * @param headers Any HTTP headers to be sent with the request
     *
     * @return The file or an empty file if the file could not be loaded.
     */
    public static InternetFile openOrEmpty(final URL url, final String userAgent, final HttpHeader... headers) {
        try {
            return open(url, userAgent, headers);
        } catch (FileNotFoundException e) {
            LOG.error("Failed to load URL [" + url + "]. File not found.");
        } catch (Exception e) {
            LOG.error("Failed to load URL [" + url + "]. Unexpected exception [" + e + "]", e);
        }
        return new InternetFile.Empty();
    }

    /**
     * Open a file from an unparsed URL. If an error is encountered whilst loading the file an empty file will be
     * returned.
     *
     * @param url The URL identifying the file to open
     * @param userAgent The user agent to use when sending the request to fetch the file
     * @param headers Any HTTP headers to be sent with the request
     *
     * @return The file or an empty file if the file could not be loaded.
     */
    public static InternetFile openOrEmpty(final String url, final String userAgent, final HttpHeader... headers) {
        try {
            return openOrEmpty(new URL(url), userAgent, headers);
        } catch (MalformedURLException e) {
            LOG.error("Failed to load URL [" + url + "]. Malformed URL");
        } catch (Exception e) {
            LOG.error("Failed to load URL [" + url + "]. Unexpected exception [" + e + "]", e);
        }
        return new InternetFile.Empty();
    }

    /**
     * Factory method for building {@link HttpHeader} instances
     */
    public static HttpHeader getRequestHeader(final String name, final String value) {
        return new HttpHeader(name, value);
    }

    public InternetFile(final URL url, final byte[] data, final HttpHeaders headers) {
        Validate.notNull(data, "Data cannot be null");
        Validate.notNull(headers, "Headers cannot be null");
        Validate.notNull(url, "URL cannot be null");

        this.url = url;
        this.bytes = data;
        this.headers = headers;

        String contentType = headers.getHeaderValue(HttpHeader.CONTENT_TYPE);
        this.contentType = CONTENT_TYPE_FACTORY.create(contentType);
    }

    public InternetFile(final URL url, final byte[] data, final Collection<HttpHeader> headers) {
        this(url, data, new HttpHeaders(headers));
    }

    public InternetFile(final URL url, final byte[] data) {
        this(url, data, new HttpHeaders());
    }

    public String getContentType() {
        return headers.getHeaderValue(HttpHeader.CONTENT_TYPE);
    }

    public String getContentEncoding() {
        return headers.getHeaderValue(HttpHeader.CONTENT_ENCODING);
    }

    public InputStream getInputStream() {
        return new ByteArrayInputStream(bytes);
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public URL getUrl() {
        return url;
    }

    @Override
    public String getText() {
        if (!isText()) {
            LOG.error("Unable to return text of file " + this + ", file is binary. Returning empty string");
            text = StringUtils.EMPTY;
        }

        if (text == null) {
            text = contentType.getText(bytes, getEncoding());
        }
        return text;
    }

    @Override
    public byte[] getBytes() {
        return bytes;
    }

    public boolean isText() {
        return contentType.isText();
    }

    public boolean isEmpty() {
        return ArrayUtils.isEmpty(bytes);
    }

    private ContentEncoding getEncoding() {
        if (encoding == null) {
            encoding = ContentEncoding.forName(headers.getHeaderValue(HttpHeader.CONTENT_ENCODING));
        }
        return encoding;
    }

    public String getHost() {
        return url.getHost();
    }

    @Override
    public Reader getReader() {
        return new BufferedReader(new InputStreamReader(getStream()));
    }

    @Override
    public InputStream getStream() {
        return new ByteArrayInputStream(bytes);
    }

    public void writeTo(final OutputStream os) {
        try {
            os.write(bytes);
        } catch (IOException e) {
            LOG.error("Failed to write " + this + " to stream");
        }
    }

    /**
     * Open an input stream from a URL
     *
     * @param url the url
     *
     * @return the input stream
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public static InputStream openURLStream(final URL url,
            final String agentName,
            final KeyValue<String, String>... properties) throws IOException {
        if (url == null) {
            throw new IllegalArgumentException("URL cannot be null");
        }

        URLConnection con = url.openConnection();

        con.setRequestProperty(HttpHeader.USER_AGENT, agentName);
        for (KeyValue<String, String> kv : properties) {
            con.setRequestProperty(kv.getKey(), kv.getValue());
        }

        con.setConnectTimeout(CONNECTION_TIMEOUT);
        con.setReadTimeout(READ_TIMEOUT);

        InputStream stream = con.getInputStream();
        if (stream == null) {
            throw new FileNotFoundException(url.toExternalForm());
        }
        return stream;
    }

    /**
     * Open an input stream from a URL
     *
     * @param url the url
     *
     * @return the input stream
     * @throws MalformedURLException
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public static InputStream openURLStream(final String url,
            final String agentName,
            final KeyValue<String, String>... properties) throws MalformedURLException, IOException {
        return InternetFile.openURLStream(new URL(url), agentName, properties);
    }

    static class Empty extends InternetFile {
        public Empty() {
            super(setUpUrl(), new byte[0], new ArrayList<HttpHeader>());
        }

        private static URL setUpUrl() {
            try {
                return new URL("file:////dummy");
            } catch (MalformedURLException e) {
                return null;
            }
        }
    }
}
