/*
 *
 */

package org.exparity.io.classpath;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.exparity.io.BinaryDataSource;
import org.exparity.io.TextDataSource;

/**
 * @author Stewart Bissett
 */
public class JcpFile implements TextDataSource, BinaryDataSource {

    private final byte[] bytes;

    /**
     * Factory method to open a classpath resource
     * @param resource the full path to the resource
     * @return a {@link JcpFile}
     * @throws IOException
     */
    public static JcpFile open(final String resource) throws IOException {
        return open(resource, Thread.currentThread().getContextClassLoader());
    }

    /**
     * Factory method to open a classpath resource using a given classes classloader
     * @param resource the full path to the resource
     * @param klass a {@link Class} to use the class loader from
     * @return a {@link JcpFile}
     * @throws IOException
     */
    public static JcpFile open(final String resource, final Class<?> klass) throws IOException {
        return open(resource, klass.getClassLoader());
    }

    /**
     * Factory method to open a classpath resource using a given classloader
     * @param resource the full path to the resource
     * @param loader the {@link ClassLoader} to use to resolve the resource
     * @return a {@link JcpFile}
     * @throws IOException
     */
    public static JcpFile open(final String resource, final ClassLoader loader) throws IOException {
        InputStream is = loader.getResourceAsStream(resource);
        if (is == null) {
            throw new FileNotFoundException(resource);
        } else {
            try (final InputStream stream = is) {
                return new JcpFile(org.apache.commons.io.IOUtils.toByteArray(stream));
            }
        }
    }

    private JcpFile(final byte[] bytes) {
        this.bytes = bytes;
    }

    @Override
    public byte[] getBytes() {
        return bytes;
    }

    @Override
    public String getText() {
        return new String(bytes);
    }

    @Override
    public Reader getReader() {
        return new BufferedReader(new InputStreamReader(getStream()));
    }

    @Override
    public InputStream getStream() {
        return new ByteArrayInputStream(bytes);
    }

    public Boolean isEmpty() {
        return bytes.length == 0;
    }

    /**
     * Open an input stream from a java class path resource relative to a given class
     *
     * @param location the jcp location
     *
     * @return the input sream
     * @throws FileNotFoundException
     */
    public static InputStream openJCPStream(final String location, final Class<?> klass) throws FileNotFoundException {
        return openJCPStream(location, klass.getClassLoader());
    }

    /**
     * Open an input stream from a java class path resource relative to a given class
     *
     * @param location the jcp location
     *
     * @return the input sream
     * @throws FileNotFoundException
     */
    public static InputStream openJCPStream(final String location, final ClassLoader classLoader)
            throws FileNotFoundException {
        InputStream stream = classLoader.getResourceAsStream(location);
        if (stream == null) {
            throw new FileNotFoundException(location);
        }
        return stream;
    }

    /**
     * Open an input stream from a java class path resource
     *
     * @param location the jcp location
     *
     * @return the input sream
     * @throws FileNotFoundException
     */
    public static InputStream openJCPStream(final String location) throws FileNotFoundException {
        return JcpFile.openJCPStream(location, ClassLoader.getSystemClassLoader());
    }
}
