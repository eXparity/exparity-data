/**
 *
 */

package org.exparity.io.filesystem;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.SystemUtils;
import org.exparity.io.utils.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Stewart Bissett
 *
 */
public class FileSystemMemoryImpl implements FileSystem {

    private static final Logger LOG = LoggerFactory.getLogger(FileSystemMemoryImpl.class);

    private Set<String> directories = new HashSet<>();
    private Map<String, ByteArrayOutputStream> files = new HashMap<>();

    /**
     * @deprecated Use {@link FileSystems#newInMemoryInstance()}
     */
    @Deprecated
    public static FileSystem getInstance() {
        return FileSystems.newInMemoryInstance();
    }

    @Override
    public OutputStream appendFile(final String filename) {
        LOG.trace("Opening '" + filename + "' for append");
        String standardised = standardisePath(filename);
        ByteArrayOutputStream os = files.get(standardised);
        if (os == null) {
            throw new FileSystemOperationException("File " + filename + " not found");
        }
        return os;
    }

    @Override
    public InputStream readFile(final String filename) {
        LOG.trace("Opening '" + filename + "' for reading");
        String standardised = standardisePath(filename);
        ByteArrayOutputStream os = files.get(standardised);
        if (os == null) {
            throw new FileSystemOperationException("File " + filename + " not found");
        }
        return new ByteArrayInputStream(os.toByteArray());
    }

    @Override
    public OutputStream writeFile(final String filename) {
        LOG.trace("Opening '" + filename + "' for writing");
        String standardised = standardisePath(filename);
        ByteArrayOutputStream os = files.get(standardised);
        if (os == null) {
            throw new FileSystemOperationException("File " + filename + " not found");
        }
        os.reset();
        return os;
    }

    @Override
    public void createDirectory(final String directory) {
        LOG.trace("Creating directory '" + directory + "'");
        String standardised = standardisePath(directory);
        if (directories.contains(standardised)) {
            throw new FileSystemOperationException(
                    "Directory " + directory + " could not be created because it already exists");
        }
        directories.add(standardised);
    }

    @Override
    public void createFile(final String filename) {
        LOG.trace("Creating file '" + filename + "'");
        String standardised = standardisePath(filename);
        if (files.containsKey(standardised)) {
            throw new FileSystemOperationException(
                    "File " + filename + " could not be created because it already exists");
        }
        files.put(standardised, new ByteArrayOutputStream());
    }

    @Override
    public void deleteDirectory(final String directory) {
        LOG.trace("Deleting directory '" + directory + "'");
        String standardised = standardisePath(directory);
        if (!directories.contains(standardised)) {
            throw new FileSystemOperationException(
                    "Directory " + directory + " could not be deleted because it doesn't exist");
        }
        directories.remove(standardised);
    }

    @Override
    public void deleteFile(final String filename) {
        LOG.trace("Deleting file '" + filename + "'");
        String standardised = standardisePath(filename);
        if (!files.containsKey(standardised)) {
            throw new FileSystemOperationException(
                    "File " + filename + " could not be deleted because it doesn't exist");
        }
        files.remove(standardised);
    }

    @Override
    public boolean directoryExists(final String directory) {
        LOG.trace("Checking directory '" + directory + "' exists");
        String standardised = standardisePath(directory);
        return directories.contains(standardised);
    }

    @Override
    public boolean fileExists(final String filename) {
        LOG.trace("Checking file '" + filename + "' exists");
        return files.containsKey(standardisePath(filename));
    }

    @Override
    public long fileSize(final String filename) {
        String standardised = standardisePath(filename);
        ByteArrayOutputStream os = files.get(standardised);
        if (os == null) {
            throw new FileSystemOperationException("File " + filename + " not found");
        }
        return os.toByteArray().length;
    }

    @Override
    public List<String> listDirs(final String path) {
        String standardised = standardisePath(path);

        List<String> dirs = new ArrayList<>();
        for (String directory : directories) {
            if (!directory.startsWith(standardised) || directory.equals(standardised)) {
                continue;
            }
            String relative = FilenameUtils.toRelativePath(path, directory);
            if (FilenameUtils.splitPath(relative).length == 1) {
                dirs.add(relative);
            }
        }
        return dirs;
    }

    @Override
    public List<String> listFiles(final String path) {
        String standardised = standardisePath(path);

        List<String> files = new ArrayList<>();
        for (String filename : this.files.keySet()) {
            if (!filename.startsWith(standardised)) {
                continue;
            }
            files.add(FilenameUtils.getFilenameFromPath(filename));
        }
        return files;
    }

    private String standardisePath(final String path) {
        return FilenameUtils.toPath(FilenameUtils.splitPath(path));
    }

    @Override
    public String getTempDirectory() {
        return SystemUtils.getJavaIoTmpDir().getAbsolutePath();
    }
}
