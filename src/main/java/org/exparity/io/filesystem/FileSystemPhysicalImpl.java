/**
 * 
 */

package org.exparity.io.filesystem;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.exparity.io.utils.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Stewart Bissett
 */
public class FileSystemPhysicalImpl implements FileSystem
{
	private static final Logger LOG = LoggerFactory.getLogger(FileSystemPhysicalImpl.class);

	/**
	 * @deprecated - Use {@link FileSystems#newInstance()}
	 */
	@Deprecated
	public static FileSystem getInstance()
	{
		return FileSystems.newInstance();
	}

	@Override
	public void createDirectory(final String directory)
	{
		LOG.trace("Creating directory [" + directory + "]");

		if (StringUtils.isBlank(directory)) {
			throw new IllegalArgumentException("Directory argument must be supplied");
		}

		File f = new File(directory);
		if (f.exists()) {
			throw new FileSystemOperationException("Directory " + directory + " could not be created because it already exists");
		}

		String[] subdirs = FilenameUtils.splitPath(directory);
		StringBuffer buffer = new StringBuffer();
		for (String subdir : subdirs) {
			buffer.append(subdir);
			String path = buffer.toString();
			if (!directoryExists(path)) {
				if (!new File(path).mkdir()) {
					throw new FileSystemOperationException("Directory " + directory + " could not be created");
				}
				else {
					LOG.trace("Created directory " + directory);
				}
			}
			buffer.append(SystemUtils.FILE_SEPARATOR);
		}

		LOG.trace("Created directory [" + directory + "]");
	}

	@Override
	public void createFile(final String filename)
	{
		LOG.trace("Creating file [" + filename + "]");

		if (StringUtils.isBlank(filename)) {
			throw new IllegalArgumentException("Filename argument must be supplied");
		}

		File f = new File(filename);
		if (f.exists()) {
			throw new FileSystemOperationException("File " + filename + " could not be created because it already exists");
		}

		try {
			if (!f.createNewFile()) {
				throw new FileSystemOperationException("File " + filename + " could not be created");
			}
		}
		catch (IOException e) {
			throw new FileSystemOperationException("File " + filename + " could not be created", e);
		}

		LOG.trace("Created file [" + filename + "]");
	}

	@Override
	public void deleteDirectory(final String directory)
	{
		LOG.trace("Delete directory [" + directory + "]");

		File file = new File(directory);
		if (!file.exists()) {
			throw new FileSystemOperationException("Delete failed for directory " + directory + ". File could not be found");
		}

		if (!file.delete()) {
			throw new FileSystemOperationException("Delete failed for " + directory);
		}

		LOG.trace("Deleted directory [" + directory + "]");
	}

	@Override
	public void deleteFile(final String filename)
	{
		LOG.trace("Deleting file [" + filename + "]");

		File file = new File(filename);
		if (!file.exists()) {
			throw new FileSystemOperationException("Delete failed for file " + filename + ". File could not be found");
		}

		if (!file.delete()) {
			throw new FileSystemOperationException("Delete failed for " + filename);
		}

		LOG.trace("Deleted file [" + filename + "]");
	}

	@Override
	public boolean directoryExists(final String directory)
	{
		File file = new File(directory);
		return file.exists() && file.isDirectory();
	}

	@Override
	public boolean fileExists(final String filename)
	{
		File file = new File(filename);
		return file.exists() && file.isFile();
	}

	@Override
	public OutputStream writeFile(final String filename)
	{
		LOG.trace("Open write stream for file [" + filename + "]");

		File file = new File(filename);
		if (!file.exists()) {
			throw new FileSystemOperationException("Unable to write to file " + filename + ". File could not be found");
		}

		try {
			FileOutputStream os = new FileOutputStream(file);
			LOG.trace("Opened write stream for file [" + filename + "]");
			return os;
		}
		catch (FileNotFoundException e) {
			throw new FileSystemOperationException(e);
		}
	}

	@Override
	public InputStream readFile(final String filename)
	{
		LOG.trace("Open read stream for file [" + filename + "]");

		try {
			FileInputStream is = new FileInputStream(filename);
			LOG.trace("Opened read stream for file [" + filename + "]");
			return is;
		}
		catch (FileNotFoundException e) {
			throw new FileSystemOperationException("File " + filename + " not found");
		}
	}

	@Override
	public OutputStream appendFile(final String filename)
	{
		LOG.trace("Open append stream for file [" + filename + "]");

		File file = new File(filename);
		if (!file.exists()) {
			throw new FileSystemOperationException("Unable to append to file " + filename + ". File could not be found");
		}

		try {
			FileOutputStream os = new FileOutputStream(file, true);
			LOG.trace("Opened append stream for file [" + filename + "]");
			return os;
		}
		catch (FileNotFoundException e) {
			throw new FileSystemOperationException("File " + filename + " not found");
		}
	}

	@Override
	public long fileSize(final String filename)
	{
		File file = new File(filename);
		if (!file.exists()) {
			throw new FileSystemOperationException("File " + filename + " not found");
		}
		return file.length();
	}

	@Override
	public List<String> listDirs(final String path)
	{
		File directory = new File(path);
		if (!directory.exists()) {
			throw new FileSystemOperationException(path + " could not be found");
		}

		if (!directory.isDirectory()) {
			throw new FileSystemOperationException(path + " is a file, only directories can be listed");
		}

		return extractFilenames(directory.listFiles(new FileFilter()
		{
			@Override
			public boolean accept(final File file)
			{
				return file.isDirectory();
			}
		}));
	}

	private List<String> extractFilenames(final File[] files)
	{
		List<String> filenames = new ArrayList<String>();
		for (File file : files) {
			filenames.add(file.getName());
		}
		return filenames;
	}

	@Override
	public List<String> listFiles(final String path)
	{
		File directory = new File(path);
		if (!directory.exists()) {
			throw new FileSystemOperationException(path + " could not be found");
		}

		if (!directory.isDirectory()) {
			throw new FileSystemOperationException(path + " is a file, only directories can be listed");
		}

		return extractFilenames(directory.listFiles(new FileFilter()
		{
			@Override
			public boolean accept(final File file)
			{
				return file.isFile();
			}
		}));
	}

	@Override
	public String getTempDirectory()
	{
		return SystemUtils.getJavaIoTmpDir().getAbsolutePath();
	}

}
