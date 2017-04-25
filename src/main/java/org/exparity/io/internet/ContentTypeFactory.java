/**
 * 
 */

package org.exparity.io.internet;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Stewart Bissett
 */
public class ContentTypeFactory {

	private static final Logger LOG = LoggerFactory.getLogger(ContentTypeFactory.class);

	private Map<String, ContentType> map = new HashMap<String, ContentType>();
	{
		map.put("text", new TextContentType());
		map.put("application/xhtml+xml", new TextContentType());
		map.put("application/xml", new TextContentType());
		map.put("image", new BinaryContentType());
	}

	public ContentType create(final String contentType) {
		if (StringUtils.isEmpty(contentType)) {
			LOG.debug("No content type supplied. Assuming text");
			return new TextContentType();
		}

		ContentType type = map.get(contentType);
		if (type != null) {
			return type;
		}

		String group = extractGroupNameContentType(contentType);
		type = map.get(group);
		if (type != null) {
			return type;
		}

		LOG.debug("Unmapped content type " + contentType + ". Assuming text");
		return new TextContentType();
	}

	private String extractGroupNameContentType(final String contentType) {
		return StringUtils.split(contentType, "/")[0];
	}
}
