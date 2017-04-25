package org.exparity.io.utils;

import static org.junit.Assert.assertEquals;

import org.apache.commons.lang.StringUtils;
import org.exparity.io.utils.FilenameUtils;
import org.junit.Test;

/**
 * @author Stewart Bissett
 */
public class FilenameUtilsTest {

    @Test
    public void testForwardSlashesAreReplaced() {
        String sample = "/path/to/a/file", expected = "%47path%47to%47a%47file";
        String actual = FilenameUtils.toValidFilename(sample);
        assertEquals(expected, actual);
    }

    @Test
    public void testBackSlashesAreReplaced() {
        String sample = "\\path\\to\\a\\file", expected = "%92path%92to%92a%92file";
        String actual = FilenameUtils.toValidFilename(sample);
        assertEquals(expected, actual);
    }

    @Test
    public void testGetFilenameFromUnixPath() {
        final String filename = "test.txt", fullpath = "/var/tmp/" + filename;
        assertEquals(filename, FilenameUtils.getFilenameFromPath(fullpath));
    }

    @Test
    public void testGetFilenameFromWindowsPath() {
        final String filename = "test.txt", fullpath = "c:\\temp\\" + filename;
        assertEquals(filename, FilenameUtils.getFilenameFromPath(fullpath));
    }

    @Test
    public void testSplitUnixPath() {
        final String filename = "test.txt", rootDir = "var", subdir = "tmp",
                fullpath = "/" + rootDir + "/" + subdir + "/" + filename;
        String[] split = FilenameUtils.splitPath(fullpath);
        assertEquals(3, split.length);
        assertEquals(rootDir, split[0]);
        assertEquals(subdir, split[1]);
        assertEquals(filename, split[2]);
    }

    @Test
    public void testSplitWindowsPath() {
        final String filename = "test.txt", rootDir = "C:", subdir = "temp",
                fullpath = rootDir + "\\" + subdir + "\\" + filename;
        String[] split = FilenameUtils.splitPath(fullpath);
        assertEquals(3, split.length);
        assertEquals(rootDir, split[0]);
        assertEquals(subdir, split[1]);
        assertEquals(filename, split[2]);
    }

    @Test
    public void testTrimSeperatorsNoSeperatorsUnix() {
        final String path = "this/has/no/seperators/to/trim";
        assertEquals(path, FilenameUtils.trimSeperators(path));
    }

    @Test
    public void testTrimSeperatorsStartSeperatorsUnix() {
        final String path = "this/has/no/seperators/to/trim", fullpath = "/" + path;
        assertEquals(path, FilenameUtils.trimSeperators(fullpath));
    }

    @Test
    public void testTrimSeperatorsEndSeperatorsUnix() {
        final String path = "this/has/no/seperators/to/trim", fullpath = path + "/";
        assertEquals(path, FilenameUtils.trimSeperators(fullpath));
    }

    @Test
    public void testTrimSeperatorsBothSeperatorsUnix() {
        final String path = "this/has/no/seperators/to/trim", fullpath = "/" + path + "/";
        assertEquals(path, FilenameUtils.trimSeperators(fullpath));
    }

    @Test
    public void testTrimSeperatorsNoSeperatorsWindows() {
        final String path = "c:\\this\\has\\no\\seperators\\to\\trim.txt";
        assertEquals(path, FilenameUtils.trimSeperators(path));
    }

    @Test
    public void testTrimSeperatorsStartSeperatorsWindows() {
        final String path = "this\\has\\no\\seperators\\to\\trim.txt", fullpath = "\\" + path;
        assertEquals(path, FilenameUtils.trimSeperators(fullpath));
    }

    @Test
    public void testTrimSeperatorsEndSeperatorsWindows() {
        final String path = "c:\\this\\has\\no\\seperators\\to", fullpath = path + "\\";
        assertEquals(path, FilenameUtils.trimSeperators(fullpath));
    }

    @Test
    public void testTrimSeperatorsBothSeperatorsWindows() {
        final String path = "this\\has\\no\\seperators\\to", fullpath = "\\" + path + "\\";
        assertEquals(path, FilenameUtils.trimSeperators(fullpath));
    }

    @Test
    public void testToRelativePath() {
        final String root = "c:\\root", relative = "relative\\directory\\test.txt";
        assertEquals(relative, FilenameUtils.toRelativePath(root, root + "\\" + relative));
    }

    @Test
    public void testToRelativePathFilenameOnly() {
        final String root = "c:\\root\\relative\\directory", relative = "test.txt";
        assertEquals(relative, FilenameUtils.toRelativePath(root, root + "\\" + relative));
    }

    @Test
    public void testToRelativePathButSame() {
        final String path = "c:\\root\\relative\\directory\\test.txt";
        assertEquals(StringUtils.EMPTY, FilenameUtils.toRelativePath(path, path));
    }

    @Test
    public void testToRelativePathButDifferentSeperators() {
        final String root = "c:/root";
        final String path = "c:\\root\\relative\\directory\\test.txt";
        final String relative = "relative\\directory\\test.txt";
        assertEquals(relative, FilenameUtils.toRelativePath(root, path));
    }

    @Test
    public void testStripFilename() {
        final String directory = "c:\\root\\relative\\directory", filename = "test.txt";
        assertEquals(directory, FilenameUtils.stripFilename(directory + "\\" + filename));
    }

    @Test
    public void testStripFilenameFilenameOnly() {
        final String filename = "test.txt";
        assertEquals(StringUtils.EMPTY, FilenameUtils.stripFilename(filename));
    }

}
