/*
 * 
 */

package org.exparity.io.filesystem;

/**
 * Factory for obtaining instances of {@link FileSystem}
 * 
 * @author Stewart Bissett
 */
public abstract class FileSystems
{
	/**
	 * Obtain an instance of a {@link FileSystem} which is backed by the local filesytem on the the machine
	 */
	public static FileSystem newInstance()
	{
		return new FileSystemPhysicalImpl();
	}

	/**
	 * Obtain an instance of a {@link FileSystem} which operates purely in memory with no connection to the filesytem
	 * and useful for testing
	 */
	public static FileSystem newInMemoryInstance()
	{
		return new FileSystemMemoryImpl();
	}

}
