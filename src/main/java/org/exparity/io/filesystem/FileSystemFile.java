/*
 *
 */

package org.exparity.io.filesystem;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.exparity.io.BinaryDataSource;
import org.exparity.io.TextDataSource;
import org.exparity.io.utils.FilenameUtils;

/**
 * @author Stewart Bissett
 */
public class FileSystemFile implements TextDataSource, BinaryDataSource {

    /**
     * Open a file on the filesystem supplied
     */
    public static FileSystemFile open(final String filename, final FileSystem fs) throws IOException {
        InputStream is = fs.readFile(filename);
        if (is == null) {
            throw new FileNotFoundException(filename);
        }
        final InputStream stream = is;
        return new FileSystemFile(filename, org.apache.commons.io.IOUtils.toByteArray(stream));
    }

    /**
     * Open a file on the filesystem accessible to this JVM
     */
    public static FileSystemFile open(final String filename) throws IOException {
        return open(filename, FileSystems.newInstance());
    }

    /**
     * Open a file on the filesystem accessible to this JVM
     */
    public static FileSystemFile open(final File file) throws IOException {
        return open(file.getAbsolutePath(), FileSystems.newInstance());
    }

    private final String absolute, filename, dir;
    private final byte[] bytes;

    private FileSystemFile(final String absolute, final byte[] bytes) {
        this.absolute = absolute;
        this.filename = FilenameUtils.getFilenameFromPath(absolute);
        this.dir = FilenameUtils.getDirectoryFromPath(absolute);
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

    public String getFilename() {
        return filename;
    }

    public String getDirectory() {
        return dir;
    }

    public String getAbsoluteFilename() {
        return absolute;
    }

    /**
     * Open an input stream from a filename
     *
     * @param filename the file
     *
     * @return the input stream
     * @throws FileNotFoundException
     */
    public static InputStream openFileStream(final String filename) throws FileNotFoundException {
        if (filename == null) {
            throw new RuntimeException("Filename is null");
        }
        return new FileInputStream(filename);
    }

}
