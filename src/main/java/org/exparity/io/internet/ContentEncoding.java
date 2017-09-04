/**
 * 
 */

package org.exparity.io.internet;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;

import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Encapsulate the encoding functions of content encoding supplied in http requests.
 * 
 * <pre>
 * ContentEncoding.forName(&quot;UTF-8&quot;).decode(bytes)
 * </pre>
 * 
 * @author Stewart Bissett
 */
public class ContentEncoding {

    private static final Logger LOG = LoggerFactory.getLogger(ContentEncoding.class);
    private final Charset charset;

    /**
     * Default encoding assumes ISO_8859_1
     */
    public static final ContentEncoding DEFAULT_ENCODING = ContentEncoding.forName(CharEncoding.ISO_8859_1);

    /**
     * Factory method to obtain a {@link ContentEncoding} instance for the given character set name. If null or empty
     * character set is supplied then the DEFAULT_ENCONDING is used.
     */
    public static ContentEncoding forName(final String charsetName) {
        if (StringUtils.isEmpty(charsetName)) {
            LOG.trace("No content encoding supplied. Assuming " + DEFAULT_ENCODING);
            return DEFAULT_ENCODING;
        } else {
            return ContentEncoding.forCharset(Charset.forName(charsetName));
        }
    }

    /**
     * Factory method to obtain a {@link ContentEncoding} instance for the given character set. The character set cannot
     * be null
     */
    public static ContentEncoding forCharset(final Charset charset) {
        return new ContentEncoding(charset);
    }

    private ContentEncoding(final Charset charset) {
        Validate.notNull(charset, "Character set cannot be null");
        this.charset = charset;
    }

    /**
     * Decode the binary data and convert to a text string
     */
    public String decode(final byte[] data) throws CharacterCodingException {
        return charset.newDecoder().decode(ByteBuffer.wrap(data)).toString();
    }
}
