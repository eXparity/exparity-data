/*
 * 
 */

package org.exparity.io.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.Validate;

/**
 * Helper methods to manipulate filenames and filepaths
 * 
 * @author Stewart Bissett
 */
public class FilenameUtils {

    private static final char[] INVALID_FILENAME_CHARACTERS = { '\'',
            '?',
            '[',
            ']',
            '/',
            '\\',
            '=',
            '+',
            '<',
            '>',
            ':',
            ';',
            '\"',
            ',',
            '*',
            '|' };
    private static final char UNIX_SEPERATOR = '/';
    private static final char WINDOWS_SEPERATOR = '\\';

    /**
     * Replace invalid characters in a string to their escaped equivalents
     */
    public static String toValidFilename(final String string) {
        if (isValidFilename(string)) {
            return string;
        }

        String modified = string;
        for (char invalid : INVALID_FILENAME_CHARACTERS) {
            modified = modified.replace(CharUtils.toString(invalid), escapeCharacter(invalid));
        }
        return modified;
    }

    /**
     * Return the URL
     */
    public static String escapeCharacter(final char invalid) {
        return "%" + (int) invalid;
    }

    /**
     * Evaluate the filename to determine if it contains any characters which are not valid filename characters;
     */
    public static boolean isValidFilename(final String filename) {
        return StringUtils.containsNone(filename, INVALID_FILENAME_CHARACTERS);
    }

    /**
     * Parse the filename from a path
     */
    public static String getFilenameFromPath(final String fullpath) {
        String[] path = splitPath(fullpath);
        return path.length == 0 ? null : path[path.length - 1];
    }

    /**
     * Parse the directory from a path
     */
    public static String getDirectoryFromPath(final String fullpath) {
        return stripFilename(fullpath);
    }

    /**
     * Split a path into an array of string each containing part of the path or filename
     */
    public static String[] splitPath(final String fullpath) {
        if (StringUtils.isEmpty(fullpath)) {
            return new String[] {};
        }

        List<String> path = new ArrayList<String>();

        StringBuffer chunk = new StringBuffer();
        for (int i = 0; i < fullpath.length(); ++i) {
            char current = fullpath.charAt(i);
            if (UNIX_SEPERATOR == current || WINDOWS_SEPERATOR == current) {
                if (chunk.length() > 0) {
                    path.add(chunk.toString());
                    chunk.setLength(0);
                }
            } else {
                chunk.append(fullpath.charAt(i));
            }
        }

        if (chunk.length() > 0) {
            path.add(chunk.toString());
        }

        return path.toArray(new String[] {});
    }

    /**
     * Trim the end of a path if the value is a directory seperator
     */
    public static String trimEndSeperator(final String path) {
        if (StringUtils.isEmpty(path)) {
            return path;
        }

        char end = path.charAt(path.length() - 1);
        if (isSeperator(end)) {
            return path.substring(0, path.length() - 1);
        } else {
            return path;
        }
    }

    /**
     * Trim the start of a path if the value is a directory seperator
     */
    public static String trimStartSeperator(final String path) {
        if (StringUtils.isEmpty(path)) {
            return path;
        }

        char start = path.charAt(0);
        if (isSeperator(start)) {
            return path.substring(1);
        } else {
            return path;
        }
    }

    private static boolean isSeperator(final char c) {
        return UNIX_SEPERATOR == c || WINDOWS_SEPERATOR == c;
    }

    /**
     * Trim the both ends of a path if either end is a directory seperator
     */
    public static String trimSeperators(final String path) {
        return trimEndSeperator(trimStartSeperator(path));
    }

    /**
     * Return the path and convert any non-Unix seperators to Unix seperators
     */
    public static String toUnixPath(final String path) {
        return path.replace(WINDOWS_SEPERATOR, UNIX_SEPERATOR);
    }

    /**
     * Return the path and convert any non-Windows seperators to Windows seperators
     */
    public static String toWindowsPath(final String path) {
        return path.replace(UNIX_SEPERATOR, WINDOWS_SEPERATOR);
    }

    /**
     * Convert a full path to a relative path. The path will be relative to the root directory specified
     */
    public static String toRelativePath(final String root, final String fullpath) {
        if (StringUtils.isEmpty(root)) {
            return fullpath;
        }

        String[] chunksOfRoot = splitPath(root), chunksOfFullpath = splitPath(fullpath);

        if (chunksOfFullpath.length < chunksOfRoot.length) {
            throw new IllegalArgumentException("fullpath '" + fullpath
                    + "' must contain root '"
                    + root
                    + "' to be made relative");
        }

        int lastMatchingChunk = 0;
        for (; lastMatchingChunk < chunksOfRoot.length; ++lastMatchingChunk) {
            if (!chunksOfFullpath[lastMatchingChunk].equals(chunksOfRoot[lastMatchingChunk])) {
                break;
            }
        }

        if (lastMatchingChunk == 0) {
            throw new IllegalArgumentException("fullpath '" + fullpath
                    + "' must contain root '"
                    + root
                    + "' to be made relative");
        } else {
            return toPath((String[]) ArrayUtils.subarray(chunksOfFullpath, lastMatchingChunk, chunksOfFullpath.length));
        }
    }

    /**
     * Build a full path by combining al the elements supplied
     */
    public static String toPath(final String... elements) {
        StringBuffer fullpath = new StringBuffer();
        for (int i = 0; i < elements.length; ++i) {
            fullpath.append(trimSeperators(elements[i]));
            if (i < elements.length - 1) {
                fullpath.append(SystemUtils.FILE_SEPARATOR);
            }
        }
        return fullpath.toString();
    }

    /**
     * Remove the filename from a path
     */
    public static String stripFilename(final String path) {
        int lastSeperator = -1;
        for (int i = 0; i < path.length(); ++i) {
            if (isSeperator(path.charAt(i))) {
                lastSeperator = i;
            }
        }

        if (lastSeperator == -1) {
            return StringUtils.EMPTY;
        } else {
            return path.substring(0, lastSeperator);
        }
    }

    public static String getFileExtension(final String path) {
        Validate.notEmpty(path, "Path cannot be empty or null");

        String filename = getFilenameFromPath(path);
        String[] chunks = filename.split("\\.");
        if (chunks.length < 2) {
            return StringUtils.EMPTY;
        } else {
            return chunks[1];
        }
    }
}
