/*
 * 
 */

package org.exparity.io;

import java.io.InputStream;

/**
 * @author Stewart Bissett
 */
public interface BinaryDataSource
{
	public byte[] getBytes();

	public InputStream getStream();
}
