/*
 * 
 */

package org.exparity.data.html;

import org.apache.commons.lang.StringUtils;

/**
 * The {@link Anchor} class represents a HTML anchor
 * 
 * @author Stewart Bissett
 */
public class Anchor
{
	private static final String HREF_PROTOCOL_RESOURCE_SEPERATOR = ":";

	private final String href, protocol, resource;

	/**
	 * Construct an {@link Anchor} from the href attribute of an HTML anchor tag
	 */
	public Anchor(final String href)
	{
		this.href = href;

		int seperatorPos = StringUtils.indexOf(href, HREF_PROTOCOL_RESOURCE_SEPERATOR);
		if (seperatorPos == StringUtils.INDEX_NOT_FOUND) {
			this.protocol = null;
			this.resource = href;
		}
		else {
			this.protocol = StringUtils.defaultString(StringUtils.substringBefore(href, HREF_PROTOCOL_RESOURCE_SEPERATOR), null);
			this.resource = StringUtils.removeStart(StringUtils.substringAfter(href, HREF_PROTOCOL_RESOURCE_SEPERATOR), "//");
		}

	}

	/**
	 * Test if the protocol matches one of those supplied. If no protocol is defined then <code>false</code> is
	 * returned.
	 */
	public boolean isProtocol(final String... protocols)
	{
		if (StringUtils.isNotEmpty(protocol)) {
			for (String protocol : protocols) {
				if (StringUtils.equalsIgnoreCase(this.protocol, protocol)) {
					return true;
				}
			}
		}
		return false;
	}

	public String getHref()
	{
		return href;
	}

	public String getProtocol()
	{
		return protocol;
	}

	public String getResource()
	{
		return resource;
	}

	@Override
	public String toString()
	{
		return "Anchor [" + protocol + ":" + resource + "]";
	}
}
