package org.exparity.io.filesystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.exparity.io.filesystem.FileSystem;
import org.exparity.io.filesystem.FileSystemMemoryImpl;
import org.exparity.io.filesystem.FileSystemOperationException;
import org.exparity.io.filesystem.FileSystemPhysicalImpl;
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
        return Arrays.asList(new Object[][] { { new FileSystemMemoryImpl(), new Memory() },
                { new FileSystemPhysicalImpl(), new Physical() } });
    }

    @Parameter
    public FileSystem fs;
    @Parameter(1)
    public Configuration config;

    @Test
    public void canCreateDirectory() {
        final String directory = getReferenceDirectory();

        assertEquals(false, fs.directoryExists(directory));
        fs.createDirectory(directory);
        assertEquals(true, fs.directoryExists(directory));
    }

    @Test(expected = FileSystemOperationException.class)
    public void canCreateDirectoryOnExistingDirectory() {
        final String directory = getReferenceDirectory();

        assertEquals(false, fs.directoryExists(directory));
        fs.createDirectory(directory);
        assertEquals(true, fs.directoryExists(directory));

        fs.createDirectory(directory);
    }

    @Test
    public void canCreateNestedDirectory() {
        final String directory = getNestedSubdirectory();

        assertEquals(false, fs.directoryExists(directory));
        fs.createDirectory(directory);
        assertEquals(true, fs.directoryExists(directory));
    }

    @Test
    public void canCreateFile() {
        final String filename = getReferenceFilename();

        assertEquals(false, fs.fileExists(filename));
        fs.createFile(filename);
        assertEquals(true, fs.fileExists(filename));
    }

    @Test(expected = FileSystemOperationException.class)
    public void canCreateFileOnExistingFile() {
        final String filename = getReferenceFilename();

        assertEquals(false, fs.fileExists(filename));
        fs.createFile(filename);
        assertEquals(true, fs.fileExists(filename));

        fs.createFile(filename);
    }

    @Test
    public void canDeleteDirectory() {
        final String directory = getReferenceDirectory();

        assertEquals(false, fs.directoryExists(directory));
        fs.createDirectory(directory);
        assertEquals(true, fs.directoryExists(directory));
        fs.deleteDirectory(directory);
        assertEquals(false, fs.directoryExists(directory));
    }

    @Test
    public void canDeleteNonExistentDirectory() {
        final String directory = getReferenceDirectory();

        try {
            fs.deleteDirectory(directory);
            fail("Excpected FileSystemOperationException");
        } catch (FileSystemOperationException e) {
        }
    }

    @Test
    public void canDeleteFile() {
        final String filename = getReferenceFilename();

        assertEquals(false, fs.fileExists(filename));
        fs.createFile(filename);
        assertEquals(true, fs.fileExists(filename));
        fs.deleteFile(filename);
        assertEquals(false, fs.fileExists(filename));
    }

    @Test
    public void canDeleteNonExistentFile() {
        final String filename = getReferenceFilename();

        try {
            fs.deleteFile(filename);
            fail("Excpected FileSystemOperationException");
        } catch (FileSystemOperationException e) {
        }
    }

    @Test
    public void canWriteAppendReadFile() throws Exception {
        final String filename = getReferenceFilename();
        final byte[] bytes = { 0x01, 0x02, 0x03, 0x04 };
        final byte[] moreBytes = { 0x05, 0x06, 0x07, 0x08, 0x09 };

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

    @Test
    public void canReadOnNonExistentFile() {
        final String filename = getReferenceFilename();

        try {
            assertEquals(false, fs.fileExists(filename));
            fs.readFile(filename);
            fail("Excpected FileSystemOperationException");
        } catch (FileSystemOperationException e) {
        }
    }

    @Test
    public void canWriteOnNonExistentFile() {
        final String filename = getReferenceFilename();

        try {
            assertEquals(false, fs.fileExists(filename));
            fs.writeFile(filename);
            fail("Excpected FileSystemOperationException");
        } catch (FileSystemOperationException e) {
        }
    }

    @Test
    public void canAppendOnNonExistentFile() {
        final String filename = getReferenceFilename();

        try {
            assertEquals(false, fs.fileExists(filename));
            fs.appendFile(filename);
            fail("Excpected FileSystemOperationException");
        } catch (FileSystemOperationException e) {
        }
    }

    @Test
    public void canFileSizeOnNonExistentFile() {
        final String filename = getReferenceFilename();

        try {
            assertEquals(false, fs.fileExists(filename));
            fs.fileSize(filename);
            fail("Excpected FileSystemOperationException");
        } catch (FileSystemOperationException e) {
        }
    }

    @Test
    public void canListDirs() {
        final String filename = getSubdirectoryFilename(), path = getReferenceDirectory(), subdir = getSubdirectory(),
                nested = getNestedSubdirectory();
        final String subdirFilename = StringUtils.substringAfterLast(subdir, "/");

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
        final String filenameWithPath = getSubdirectoryFilename(), path = getReferenceDirectory(), subdir =
                getSubdirectory(), nested = getNestedSubdirectory();
        final String filenameWithoutPath = StringUtils.substringAfterLast(filenameWithPath, "/");

        fs.createDirectory(path);

        List<String> files = fs.listFiles(path);
        assertEquals(0, files.size());

        fs.createDirectory(subdir);
        files = fs.listFiles(path);
        assertEquals(0, files.size());

        fs.createFile(filenameWithPath);
        files = fs.listFiles(path);
        assertEquals(1, files.size());
        assertEquals(filenameWithoutPath, files.get(0));

        fs.createDirectory(nested);
        files = fs.listFiles(path);
        assertEquals(1, files.size());
        assertEquals(filenameWithoutPath, files.get(0));
    }

    @After
    public void tearDown() throws Exception {
        config.tearDown(fs);
    }

    public String getReferenceFilename() {
        return config.getReferenceFilename();
    }

    public String getReferenceDirectory() {
        return config.getReferenceDirectory();
    }

    public String getSubdirectory() {
        return config.getSubdirectory();
    }

    public String getSubdirectoryFilename() {
        return config.getSubdirectoryFilename();
    }

    public String getNestedSubdirectory() {
        return config.getNestedSubdirectory();
    }

    private static interface Configuration {

        String getReferenceFilename();

        void tearDown(FileSystem fs) throws Exception;

        String getReferenceDirectory();

        String getSubdirectory();

        String getSubdirectoryFilename();

        String getNestedSubdirectory();
    }

    private static class Physical implements Configuration {

        @Override
        public String getReferenceDirectory() {
            return SystemUtils.getJavaIoTmpDir() + "/testFileSystemPhysicalImpl";
        }

        @Override
        public String getSubdirectory() {
            return SystemUtils.getJavaIoTmpDir() + "/testFileSystemPhysicalImpl/subDir";
        }

        @Override
        public String getSubdirectoryFilename() {
            return SystemUtils.getJavaIoTmpDir() + "/testFileSystemPhysicalImpl/sample.txt";
        }

        @Override
        public String getNestedSubdirectory() {
            return SystemUtils.getJavaIoTmpDir() + "/testFileSystemPhysicalImpl/subDir/nestedDir";
        }

        @Override
        public String getReferenceFilename() {
            return SystemUtils.getJavaIoTmpDir() + "/testFileSystemPhysicalImpl.txt";
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
        public String getReferenceDirectory() {
            return "/directory";
        }

        @Override
        public String getSubdirectory() {
            return "/directory/subdir";
        }

        @Override
        public String getSubdirectoryFilename() {
            return "/directory/sample.txt";
        }

        @Override
        public String getNestedSubdirectory() {
            return "/directory/subdir/nested";
        }

        @Override
        public String getReferenceFilename() {
            return "sample.file";
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
