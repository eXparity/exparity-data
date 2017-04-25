/**
 *
 */

package org.exparity.io.filesystem;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Class to abstract away file system operations to allow clean unit testing of components which are dependent on file
 * system related operations. Instantiate instances of FileSystem classes using the FileSystemFactory class;
 *
 * @author Stewart Bissett
 */
public interface FileSystem {

    public InputStream readFile(String filename);

    public OutputStream writeFile(String filename);

    public void createFile(String filename);

    public void deleteFile(String filename);

    public boolean fileExists(String filename);

    public boolean directoryExists(String directory);

    public void createDirectory(String directory);

    public void deleteDirectory(String directory);

    public OutputStream appendFile(String filename);

    public long fileSize(String filename);

    public List<String> listDirs(String path);

    public List<String> listFiles(String path);

    public String getTempDirectory();
}
