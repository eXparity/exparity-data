/*
 *
 */

package org.exparity.data.xml;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.exparity.io.classpath.JcpFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Stewart Bissett
 */
public class ClasspathEntityResolver implements EntityResolver {

    private static final Logger LOG = LoggerFactory.getLogger(ClasspathEntityResolver.class);

    private final Class<?> klass;

    public ClasspathEntityResolver(final Class<?> klass) {
        this.klass = klass;
    }

    @Override
    public InputSource resolveEntity(final String publicId, final String systemId) throws SAXException, IOException {
        LOG.trace("Resolving entity " + publicId + ":" + systemId);
        String localSystemId = StringUtils.substringAfterLast(systemId, "/");
        return new InputSource(JcpFile.openJCPStream(localSystemId, klass));
    }
}
