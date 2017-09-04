/*
 * 
 */

package org.exparity.io.internet;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.exparity.io.utils.UrlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Stewart Bissett
 */
public abstract class HttpCommand {

    private static final String DEFAULT_USER_AGENT =
            "Mozilla/5.0 (Windows; U; Windows NT 6.0; en-GB; rv:1.9.0.4) Gecko/2008102920 Firefox/3.0.4 (.NET CLR 3.5.30729)";
    private static final int DEFAULT_CONNECTION_TIMEOUT = (int) TimeUnit.SECONDS.toMillis(10);
    private static final int DEFAULT_READ_TIME_OUT = (int) TimeUnit.SECONDS.toMillis(10);
    private static final int DEFAULT_RETRIES = 3;

    private static final Logger LOG = LoggerFactory.getLogger(HttpCommand.class);

    private final List<HttpCommandParameter> params = new ArrayList<>();
    private final List<HttpHeader> requestHeaders = new ArrayList<>();
    private HttpCommandBuilder builder;
    private HttpURLConnectionFactory connectionFactory;
    private int connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
    private int readTimeout = DEFAULT_READ_TIME_OUT;
    private String userAgent = DEFAULT_USER_AGENT;
    private final String file;
    private int maxRetries = DEFAULT_RETRIES;
    private boolean followRedirects = true;

    protected HttpCommand(final String file) {
        Validate.notNull(file, "File cannot be null");
        this.file = file;
    }

    public InternetFile execute() throws TimeoutException, FileNotFoundException, HttpCommandFailedException {
        LOG.debug("Executing " + getRequestMethod() + " on " + file);
        List<TimeoutException> timeouts = new ArrayList<>();
        while (timeouts.size() < maxRetries) {
            try {
                return doCommand();
            } catch (TimeoutException e) {
                timeouts.add(e);
            }
        }
        throw timeouts.get(maxRetries - 1);
    }

    public InternetFile executeOrEmpty() {
        try {
            return execute();
        } catch (FileNotFoundException e) {
            LOG.error("Failed to execute " + this + "]. File not found.");
        } catch (TimeoutException e) {
            LOG.error("Failed to execute " + this + "]. Timeout");
        } catch (HttpCommandFailedException e) {
            LOG.error("Failed to execute " + this + "]. Http command failed [" + e + "]");
        } catch (Exception e) {
            LOG.error("Failed to execute " + this + "]. Unexpected exception [" + e + "]", e);
        }
        return new InternetFile.Empty();
    }

    private InternetFile doCommand() throws TimeoutException, HttpCommandFailedException, FileNotFoundException {
        try {
            String query = buildQueryString(params);
            URL url = setUpURL(file, query);
            if (!url.toExternalForm().equals(file)) {
                LOG.debug("Executing " + getRequestMethod() + " on " + url);
            }

            HttpURLConnection con = connectionFactory.newConnection(url);
            try {

                setRequestHeaders(con);
                if (isUploadingData()) {
                    uploadData(query, con);
                }

                InputStream stream = con.getInputStream();
                if (stream == null) {
                    throw new FileNotFoundException("Failed to fetch internet file " + file
                            + ". Failed to open stream");
                }

                try {
                    HttpHeaders responseHeaders = HttpHeaders.getFrom(con);
                    if (isRedirect(con.getResponseCode()) && followRedirects) {
                        return followRedirection(responseHeaders);
                    } else
                        if (isSuccessful(con.getResponseCode())) {
                            return createInternetFile(url, stream, responseHeaders);
                        } else {
                            throw new HttpCommandFailedException(con.getResponseCode(), con.getResponseMessage());
                        }
                } finally {
                    stream.close();
                }
            } finally {
                con.disconnect();
            }
        } catch (FileNotFoundException e) {
            throw e;
        } catch (SocketTimeoutException e) {
            throw new TimeoutException(e);
        } catch (IOException e) {
            throw new InternetException(e);
        } catch (URISyntaxException e) {
            throw new InternetException(e);
        }
    }

    private InternetFile createInternetFile(final URL url, final InputStream stream, final HttpHeaders responseHeaders)
            throws IOException {
        byte[] data = org.apache.commons.io.IOUtils.toByteArray(stream);
        if (data.length <= 0) {
            throw new InternetException("Failed to fetch internet file " + file + ". No data");
        }

        LOG.debug("Executed " + getRequestMethod() + " on " + file);
        InternetFile iFile = new InternetFile(url, data, responseHeaders);
        if (LOG.isTraceEnabled()) {
            iFile.writeTo(System.out);
        }
        return iFile;
    }

    private void uploadData(final String query, final HttpURLConnection con) throws IOException {
        con.setRequestProperty(HttpHeader.CONTENT_TYPE, "application/x-www-form-urlencoded");
        con.setRequestProperty(HttpHeader.CONTENT_LENGTH, "" + Integer.toString(query.getBytes().length));
        con.setDoOutput(true);
        OutputStreamWriter os = new OutputStreamWriter(con.getOutputStream());
        os.write(query);
        os.flush();
        os.close();
    }

    private void setRequestHeaders(final HttpURLConnection con) throws ProtocolException {
        con.setRequestMethod(getRequestMethod());
        con.setInstanceFollowRedirects(false);
        con.setConnectTimeout(connectionTimeout);
        con.setReadTimeout(readTimeout);
        con.setDoInput(true);
        con.setUseCaches(false);
        for (HttpHeader header : requestHeaders) {
            con.setRequestProperty(header.getKey(), header.getValue());
        }

        con.setRequestProperty(HttpHeader.USER_AGENT, userAgent);
        if (LOG.isTraceEnabled()) {
            for (Map.Entry<String, List<String>> requestProperty : con.getRequestProperties().entrySet()) {
                LOG.trace("Request header " + requestProperty.getKey()
                        + "="
                        + StringUtils.join(requestProperty.getValue(), ","));
            }
        }
    }

    private boolean isSuccessful(final int code) {
        return HttpURLConnection.HTTP_OK == code;
    }

    protected abstract boolean isUploadingData();

    protected abstract String getRequestMethod();

    protected abstract URL setUpURL(String file, String query) throws IOException;

    private String buildQueryString(final List<HttpCommandParameter> params) {
        LOG.trace("Building request data");
        if (CollectionUtils.isEmpty(params)) {
            LOG.trace("No query parameters");
            return "";
        }
        String seperator = "";
        StringBuilder requestData = new StringBuilder();
        for (HttpCommandParameter param : params) {
            requestData.append(seperator).append(param.getKey()).append("=").append(param.getValue());
            seperator = "&";
        }
        LOG.trace("Built request data [" + requestData.toString() + "]");
        return requestData.toString();
    }

    protected InternetFile followRedirection(final HttpHeaders responseHeaders) throws FileNotFoundException,
            TimeoutException,
            MalformedURLException,
            HttpCommandFailedException,
            URISyntaxException {

        String redirect = responseHeaders.getHeaderValue(HttpHeader.LOCATION);
        if (StringUtils.isBlank(redirect)) {
            throw new InternetException("Unable to redirect. No redirect location specified");
        }

        LOG.debug("Following redirection to " + redirect);
        URL expanded = UrlUtils.parse(this.file, redirect);

        HttpCommand redirection = builder.newHttpGet(expanded.toExternalForm())
                .addRequestHeaders(requestHeaders)
                .setConnectionTimeout(connectionTimeout)
                .setUserAgent(userAgent)
                .setReadTimeout(readTimeout)
                .setFollowRedirects(false)
                .addRequestHeader(HttpHeader.REFERER, file);

        String setCookie = responseHeaders.getHeaderValue(HttpHeader.SET_COOKIE);
        try {
            if (!StringUtils.isBlank(setCookie)) {
                for (HttpCookie cookie : HttpCookie.parse(setCookie)) {
                    redirection.addRequestHeader(HttpHeader.COOKIE, cookie.toString());
                }
            }
        } catch (Exception e) {
            LOG.warn("Failed to extract cookie from " + setCookie);
        }

        return redirection.execute();
    }

    private HttpCommand setFollowRedirects(final boolean followRedirects) {
        this.followRedirects = followRedirects;
        return this;
    }

    private boolean isRedirect(final int code) {
        return code == HttpURLConnection.HTTP_MOVED_TEMP || code == HttpURLConnection.HTTP_MOVED_PERM;
    }

    public HttpCommand addParameter(final String name, final String value) {
        try {
            params.add(new HttpCommandParameter(name, value));
            return this;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public HttpCommand addParameter(final String name, final byte[] value) {
        params.add(new HttpCommandParameter(name, value));
        return this;
    }

    public HttpCommand addRequestHeader(final String header, final String value) {
        return addRequestHeader(new HttpHeader(header, value));
    }

    public HttpCommand addRequestHeader(final HttpHeader header) {
        this.requestHeaders.add(header);
        return this;
    }

    public HttpCommand addRequestHeaders(final HttpHeader... headers) {
        for (HttpHeader header : headers) {
            this.requestHeaders.add(header);
        }
        return this;
    }

    public HttpCommand addRequestHeaders(final List<HttpHeader> headers) {
        this.requestHeaders.addAll(headers);
        return this;
    }

    public HttpCommand setConnectionFactory(final HttpURLConnectionFactory factory) {
        Validate.notNull(factory, "Connection factory cannot be null");
        this.connectionFactory = factory;
        return this;
    }

    protected HttpCommand setCommandBuilder(final HttpCommandBuilder builder) {
        Validate.notNull(builder, "Command builder cannot be null");
        this.builder = builder;
        return this;
    }

    public HttpCommand setConnectionTimeout(final int timeout) {
        this.connectionTimeout = timeout;
        return this;
    }

    public HttpCommand setReadTimeout(final int timeout) {
        this.readTimeout = timeout;
        return this;
    }

    public HttpCommand setUserAgent(final String userAgent) {
        Validate.notEmpty(userAgent, "User agent cannot be empty or null");
        this.userAgent = userAgent;
        return this;
    }

    public HttpCommand setMaxRetries(final int maxRetries) {
        this.maxRetries = maxRetries;
        return this;
    }

    @Override
    public String toString() {
        return "HttpCommand [" + getRequestMethod() + ":" + this.file + "]";
    }
}
