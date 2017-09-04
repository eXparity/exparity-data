/*
 *
 */

package org.exparity.io.internet;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Stewart Bissett
 */
public class InternetPhysicalImpl implements Internet {

    private static final int DEFAULT_CONNECTION_TIMEOUT = 5;
    private static final int DEFAULT_READ_TIMEOUT = 30;
    private static final Logger LOG = LoggerFactory.getLogger(InternetPhysicalImpl.class);

    private final HttpCommandBuilder builder = new HttpCommandBuilder(new HttpURLConnectionFactory());
    private final long readTimeout;
    private final long connTimeout;

    /**
     * @deprecated Use {@link Internets#newInstance()}
     */
    @Deprecated
    public static Internet getInstance() {
        return new InternetPhysicalImpl(DEFAULT_CONNECTION_TIMEOUT,
                TimeUnit.SECONDS,
                DEFAULT_READ_TIMEOUT,
                TimeUnit.SECONDS);
    }

    /**
     * @deprecated Use {@link Internets#newInstance()}
     */
    @Deprecated
    public static Internet getInstance(final int connTimeout,
            final TimeUnit connTimeUnit,
            final int readTimeout,
            final TimeUnit readTimeUnit) {
        return new InternetPhysicalImpl(connTimeout, connTimeUnit, readTimeout, readTimeUnit);
    }

    public InternetPhysicalImpl(final int connTimeout,
            final TimeUnit connTimeUnit,
            final int readTimeout,
            final TimeUnit readTimeUnit) {
        this.connTimeout = connTimeUnit.toMillis(connTimeout);
        this.readTimeout = readTimeUnit.toMillis(readTimeout);
    }

    public InternetPhysicalImpl() {
        this(DEFAULT_CONNECTION_TIMEOUT, TimeUnit.SECONDS, DEFAULT_READ_TIMEOUT, TimeUnit.SECONDS);
    }

    @Override
    public InternetFile open(final URL url, final HttpHeader... properties) throws FileNotFoundException {
        return open(url, UserAgents.IE8, properties);
    }

    @Override
    public InternetFile open(final URL url, final String userAgent, final HttpHeader... properties)
            throws FileNotFoundException {

        Validate.notNull(url, "URL cannot be null");
        Validate.notEmpty(userAgent, "User agent cannot be empty");

        try {
            LOG.debug("Downloading " + url);
            HttpCommand get = builder.newHttpGet(url.toExternalForm())
                    .setUserAgent(userAgent)
                    .setConnectionTimeout((int) connTimeout)
                    .setReadTimeout((int) readTimeout)
                    .addRequestHeaders(properties);
            InternetFile iFile = get.execute();
            LOG.debug("Downloaded " + url);
            return iFile;
        } catch (FileNotFoundException e) {
            throw e;
        } catch (TimeoutException e) {
            throw new InternetException(e);
        } catch (HttpCommandFailedException e) {
            throw new InternetException(e);
        }
    }

    @Override
    public InternetFile open(final URL url,
            final String userAgent,
            final int maxRetries,
            final HttpHeader... properties) throws MaxRetryException,
            FileNotFoundException {
        for (int remainingAttempts = maxRetries; remainingAttempts > 0; --remainingAttempts) {
            try {
                return open(url, userAgent, properties);
            } catch (InternetException e) {
                if (remainingAttempts == 0) {
                    break;
                }
                LOG.debug("Error downloading " + url + ". " + e + ". Retrying");
            } catch (FileNotFoundException e) {
                throw e;
            }
        }
        throw new MaxRetryException();
    }
}
