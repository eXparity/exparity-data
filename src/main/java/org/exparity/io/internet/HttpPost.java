/*
 * 
 */

package org.exparity.io.internet;

import java.io.IOException;
import java.net.URL;

/**
 * @author Stewart Bissett
 */
class HttpPost extends HttpCommand
{
	HttpPost(final String file)
	{
		super(file);
	}

	@Override
	protected URL setUpURL(final String file, final String query) throws IOException
	{
		return new URL(file);
	}

	@Override
	protected String getRequestMethod()
	{
		return "POST";
	}

	@Override
	protected boolean isUploadingData()
	{
		return true;
	}
}
