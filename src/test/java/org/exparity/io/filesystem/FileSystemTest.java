package org.exparity.io.filesystem;

import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

/**
 * @author Stewart Bissett
 *
 */
@RunWith(Parameterized.class)
public class FileSystemTest {

    @Parameters
    public static Collection<Object[]> data() {
        Object[] memory = { new FileSystemMemoryImpl(), new Memory() };
        Object[] filesystem = { new FileSystemPhysicalImpl(), new Physical() };
        return Arrays.asList(memory, filesystem);
    }

    @Parameter
    public FileSystem fs;
    @Parameter(1)
    public Configuration config;

    @Test
    public void canCreateDirectory() {
        final String directory = config.getReferenceDirectory();

        assertEquals(false, fs.directoryExists(directory));
        fs.createDirectory(directory);
        assertEquals(true, fs.directoryExists(directory));
    }

    @Test(expected = FileSystemOperationException.class)
    public void canCreateDirectoryOnExistingDirectory() {
        final String directory = config.getReferenceDirectory();

        assertEquals(false, fs.directoryExists(directory));
        fs.createDirectory(directory);
        assertEquals(true, fs.directoryExists(directory));

        fs.createDirectory(directory);
    }

    @Test
    public void canCreateNestedDirectory() {
        final String directory = config.getNestedSubdirectory();

        assertEquals(false, fs.directoryExists(directory));
        fs.createDirectory(directory);
        assertEquals(true, fs.directoryExists(directory));
    }

    @Test
    public void canCreateFile() {
        final String filename = config.getReferenceFilename();
        fs.createDirectory(config.getReferenceDirectory());

        assertEquals(false, fs.fileExists(filename));
        fs.createFile(filename);
        assertEquals(true, fs.fileExists(filename));
    }

    @Test(expected = FileSystemOperationException.class)
    public void canCreateFileOnExistingFile() {
        final String filename = config.getReferenceFilename();

        assertEquals(false, fs.fileExists(filename));
        fs.createFile(filename);
        assertEquals(true, fs.fileExists(filename));

        fs.createFile(filename);
    }

    @Test
    public void canDeleteDirectory() {
        final String directory = config.getReferenceDirectory();

        assertEquals(false, fs.directoryExists(directory));
        fs.createDirectory(directory);
        assertEquals(true, fs.directoryExists(directory));
        fs.deleteDirectory(directory);
        assertEquals(false, fs.directoryExists(directory));
    }

    @Test(expected = FileSystemOperationException.class)
    public void canDeleteNonExistentDirectory() {
        final String directory = config.getReferenceDirectory();
        fs.deleteDirectory(directory);
    }

    @Test
    public void canDeleteFile() {
        final String filename = config.getReferenceFilename();
        fs.createDirectory(config.getReferenceDirectory());

        assertEquals(false, fs.fileExists(filename));
        fs.createFile(filename);
        assertEquals(true, fs.fileExists(filename));
        fs.deleteFile(filename);
        assertEquals(false, fs.fileExists(filename));
    }

    @Test(expected = FileSystemOperationException.class)
    public void canDeleteNonExistentFile() {
        final String filename = config.getReferenceFilename();
        fs.deleteFile(filename);
    }

    @Test
    public void canWriteAppendReadFile() throws Exception {
        final String filename = config.getReferenceFilename();
        final byte[] bytes = { 0x01, 0x02, 0x03, 0x04 };
        final byte[] moreBytes = { 0x05, 0x06, 0x07, 0x08, 0x09 };

        fs.createDirectory(config.getReferenceDirectory());
        assertEquals(false, fs.fileExists(filename));
        fs.createFile(filename);

        OutputStream os = fs.writeFile(filename);
        os.write(bytes);
        os.close();

        assertEquals(bytes.length, fs.fileSize(filename));

        os = fs.appendFile(filename);
        os.write(moreBytes);
        os.close();

        assertEquals(bytes.length + moreBytes.length, fs.fileSize(filename));

        final byte[] allBytes = ArrayUtils.addAll(bytes, moreBytes);
        byte[] read = new byte[allBytes.length];
        InputStream is = fs.readFile(filename);
        int bytesRead = is.read(read);
        is.close();

        assertEquals(allBytes.length, bytesRead);
        assertEquals(true, ArrayUtils.isEquals(allBytes, read));
    }

    @Test(expected = FileSystemOperationException.class)
    public void canReadOnNonExistentFile() {
        final String filename = config.getReferenceFilename();
        assertEquals(false, fs.fileExists(filename));
        fs.readFile(filename);
    }

    @Test(expected = FileSystemOperationException.class)
    public void canWriteOnNonExistentFile() {
        final String filename = config.getReferenceFilename();
        assertEquals(false, fs.fileExists(filename));
        fs.writeFile(filename);
        fail("Excpected FileSystemOperationException");
    }

    @Test(expected = FileSystemOperationException.class)
    public void canAppendOnNonExistentFile() {
        final String filename = config.getReferenceFilename();
        assertEquals(false, fs.fileExists(filename));
        fs.appendFile(filename);
        fail("Excpected FileSystemOperationException");
    }

    @Test(expected = FileSystemOperationException.class)
    public void canFileSizeOnNonExistentFile() {
        final String filename = config.getReferenceFilename();
        assertEquals(false, fs.fileExists(filename));
        fs.fileSize(filename);
        fail("Excpected FileSystemOperationException");
    }

    @Test
    public void canListDirs() {
        final String filename = config.getSubdirectoryFilename();
        final String path = config.getReferenceDirectory();
        final String subdir = config.getSubdirectory();
        final String nested = config.getNestedSubdirectory();
        final String subdirFilename = config.getSubdirectoryName();

        fs.createDirectory(path);

        List<String> dirs = fs.listDirs(path);
        assertEquals(0, dirs.size());

        fs.createFile(filename);
        dirs = fs.listDirs(path);
        assertEquals(0, dirs.size());

        fs.createDirectory(subdir);
        dirs = fs.listDirs(path);
        assertEquals(1, dirs.size());
        assertEquals(subdirFilename, dirs.get(0));

        fs.createDirectory(nested);
        dirs = fs.listDirs(path);
        assertEquals(1, dirs.size());
        assertEquals(subdirFilename, dirs.get(0));
    }

    @Test
    public void canListFiles() {
        final String filenameWithPath = config.getSubdirectoryFilename();
        final String path = config.getReferenceDirectory();
        final String subdir = config.getSubdirectory();
        final String nested = config.getNestedSubdirectory();
        final String filename = config.getFilename();

        fs.createDirectory(path);

        List<String> files = fs.listFiles(path);
        assertEquals(0, files.size());

        fs.createDirectory(subdir);
        files = fs.listFiles(path);
        assertEquals(0, files.size());

        fs.createFile(filenameWithPath);
        files = fs.listFiles(path);
        assertEquals(1, files.size());
        assertEquals(filename, files.get(0));

        fs.createDirectory(nested);
        files = fs.listFiles(path);
        assertEquals(1, files.size());
        assertEquals(filename, files.get(0));
    }

    @After
    public void tearDown() throws Exception {
        config.tearDown(fs);
    }

    private static interface Configuration {

        String getReferenceFilename();

        String getSubdirectoryName();

        String getFilename();

        void tearDown(FileSystem fs) throws Exception;

        String getReferenceDirectory();

        String getSubdirectory();

        String getSubdirectoryFilename();

        String getNestedSubdirectory();
    }

    private static class Physical implements Configuration {

        private static String RANDOM_DIR = randomAlphabetic(5);
        private static String RANDOM_SUBDIR = randomAlphabetic(5);
        private static String RANDOM_NESTED_SUBDIR = randomAlphabetic(5);
        private static String RANDOM_FILENAME = randomAlphabetic(5) + ".txt";

        @Override
        public String getFilename() {
            return RANDOM_FILENAME;
        }

        @Override
        public String getSubdirectoryName() {
            return RANDOM_SUBDIR;
        }

        @Override
        public String getReferenceDirectory() {
            return Paths.get(getTempDir(), RANDOM_DIR).toString();
        }

        @Override
        public String getSubdirectory() {
            return Paths.get(getReferenceDirectory(), RANDOM_SUBDIR).toString();
        }

        @Override
        public String getSubdirectoryFilename() {
            return Paths.get(getReferenceDirectory(), getFilename()).toString();
        }

        @Override
        public String getNestedSubdirectory() {
            return Paths.get(getSubdirectory(), RANDOM_NESTED_SUBDIR).toString();
        }

        @Override
        public String getReferenceFilename() {
            return Paths.get(getReferenceDirectory(), getFilename()).toString();
        }

        private String getTempDir() {
            return System.getProperty("java.io.tmpdir");
        }

        @Override
        public void tearDown(final FileSystem fs) throws Exception {
            deleteFileIfPresent(getReferenceFilename());
            deleteDirectoryIfPresent(getNestedSubdirectory());
            deleteFileIfPresent(getSubdirectoryFilename());
            deleteDirectoryIfPresent(getSubdirectory());
            deleteDirectoryIfPresent(getReferenceDirectory());
        }

        private void deleteDirectoryIfPresent(final String directory) {
            File file = new File(directory);
            if (file.exists()) {
                if (!file.delete()) {
                    throw new RuntimeException("Failed to delete " + directory + " during teardown");
                }
            }
        }

        private void deleteFileIfPresent(final String filename) {
            File file = new File(filename);
            if (file.exists()) {
                if (!file.delete()) {
                    throw new RuntimeException("Failed to delete " + filename + " during teardown");
                }
            }
        }
    }

    private static class Memory implements Configuration {

        @Override
        public void tearDown(final FileSystem fs) throws Exception {
            deleteDirectory(getReferenceDirectory(), fs);
            deleteDirectory(getSubdirectory(), fs);
            deleteDirectory(getNestedSubdirectory(), fs);
            deleteFile(getSubdirectoryFilename(), fs);
            deleteFile(getReferenceFilename(), fs);
        }

        @Override
        public String getFilename() {
            return "sample.txt";
        }

        @Override
        public String getSubdirectoryName() {
            return "subdir";
        }

        @Override
        public String getReferenceDirectory() {
            return "/directory";
        }

        @Override
        public String getSubdirectory() {
            return "/directory/" + getSubdirectoryName();
        }

        @Override
        public String getSubdirectoryFilename() {
            return "/directory/" + getFilename();
        }

        @Override
        public String getNestedSubdirectory() {
            return "/directory/" + getSubdirectoryName() + "/nested";
        }

        @Override
        public String getReferenceFilename() {
            return "/directory/" + getFilename();
        }

        private void deleteDirectory(final String dir, final FileSystem fs) {
            if (fs.directoryExists(dir)) {
                fs.deleteDirectory(dir);
            }
        }

        private void deleteFile(final String file, final FileSystem fs) {
            if (fs.fileExists(file)) {
                fs.deleteFile(file);
            }
        }
    }
}
