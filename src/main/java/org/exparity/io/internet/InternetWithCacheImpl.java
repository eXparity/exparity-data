/*
 *
 */

package org.exparity.io.internet;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;
import org.exparity.io.filesystem.FileSystem;
import org.exparity.io.filesystem.FileSystemPhysicalImpl;
import org.exparity.io.utils.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Decorate and existing Internet instance so that all read/writes happen through a cache
 *
 * @author Stewart Bissett
 */
public class InternetWithCacheImpl implements Internet {

    private static final Logger LOG = LoggerFactory.getLogger(InternetWithCacheImpl.class);

    private boolean disableCache = false;
    private final FileSystem fs;
    private final Internet iNet;
    private final String rootDir;
    private final String suffix;

    public InternetWithCacheImpl(final FileSystem fs, final Internet iNet, final String rootDir, final String suffix) {
        this.fs = fs;
        this.iNet = iNet;
        this.rootDir = rootDir;
        this.suffix = suffix;
    }

    public InternetWithCacheImpl(final Internet iNet, final String rootDir, final String suffix) {
        this(new FileSystemPhysicalImpl(), iNet, rootDir, suffix);
    }

    public InternetWithCacheImpl(final Internet iNet, final String rootDir) {
        this(new FileSystemPhysicalImpl(), iNet, rootDir, "htm");
    }

    @Override
    public InternetFile open(final URL url, final HttpHeader... properties) throws FileNotFoundException {
        return open(url, UserAgents.IE8, properties);
    }

    @Override
    public InternetFile open(final URL url, final String userAgent, final HttpHeader... properties)
            throws FileNotFoundException {
        InternetFile iFile = readFromCache(url);
        if (iFile != null) {
            return iFile;
        }

        iFile = iNet.open(url, userAgent, properties);
        if (iFile != null) {
            writeToCache(url, iFile);
        }
        return iFile;
    }

    @Override
    public InternetFile open(final URL url,
            final String userAgent,
            final int maxRetries,
            final HttpHeader... properties) throws FileNotFoundException,
            MaxRetryException {
        InternetFile iFile = readFromCache(url);
        if (iFile != null) {
            return iFile;
        }

        iFile = iNet.open(url, userAgent, maxRetries, properties);
        if (iFile != null) {
            writeToCache(url, iFile);
        }
        return iFile;
    }

    private void writeToCache(final URL url, final InternetFile iFile) {
        if (disableCache) {
            return;
        }

        String filename = createFilenameForUrl(url);
        if (StringUtils.isBlank(filename)) {
            return;
        }

        fs.createFile(filename);
        OutputStream os = fs.writeFile(filename);
        iFile.writeTo(os);
        final Closeable closeable = os;
        org.apache.commons.io.IOUtils.closeQuietly(closeable);
        LOG.debug("Cached URL [" + url + "] to [" + filename + "]");
    }

    private InternetFile readFromCache(final URL url) {
        if (disableCache) {
            return null;
        }

        String filename = createFilenameForUrl(url);
        if (StringUtils.isBlank(filename) || !fs.fileExists(filename)) {
            return null;
        }

        InputStream is = null;
        try {
            is = fs.readFile(filename);
            final InputStream stream = is;
            InternetFile iFile = new InternetFile(url, org.apache.commons.io.IOUtils.toByteArray(stream));
            LOG.debug("Read cache of [" + url + "] from [" + filename + "]");
            return iFile;
        } catch (IOException e) {
            LOG.warn("Failed to read [" + filename + "] from cache");
        } finally {
            final Closeable closeable = is;
            org.apache.commons.io.IOUtils.closeQuietly(closeable);
        }

        return null;
    }

    private String createFilenameForUrl(final URL url) {
        String urlHash = createHashForUrl(url);
        return StringUtils.isNotBlank(urlHash) ? FilenameUtils.toPath(rootDir, urlHash + "." + suffix) : null;
    }

    private String createHashForUrl(final URL url) {
        try {
            return Hex.encodeHexString(MessageDigest.getInstance("MD5").digest(url.toExternalForm().getBytes()));
        } catch (NoSuchAlgorithmException e) {
            LOG.debug("Caching disabled. Unable to find hashing algorithm [MD5]");
            this.disableCache = true;
            return null;
        }
    }
}
