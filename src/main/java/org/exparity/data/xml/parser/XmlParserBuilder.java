package org.exparity.data.xml.parser;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.Validate;
import org.exparity.data.BadFormatException;
import org.exparity.data.XML;
import org.exparity.data.xml.ClasspathEntityResolver;
import org.exparity.data.xml.XmlParser;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;

/**
 * Builder to instantiate and configure and XmlParser
 * @author Stewart Bissett
 */
public class XmlParserBuilder {

    public static XmlParserBuilder newParserFactory() {
        return new XmlParserBuilder();
    }

    private boolean namespaceAware = false;

    public XmlParserBuilder setNamespaceAware(final boolean namespaceAware) {
        this.namespaceAware = namespaceAware;
        return this;
    }

    public XmlParser build() {
        EntityResolver resolver = new ClasspathEntityResolver(JAXPParser.class);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(namespaceAware);
        return new JAXPParser(factory, resolver);
    }

    private static class JAXPParser implements XmlParser {

        private final DocumentBuilderFactory factory;
        private final EntityResolver resolver;

        private JAXPParser(final DocumentBuilderFactory factory, final EntityResolver resolver) {
            this.factory = factory;
            this.resolver = resolver;
        }

        @Override
        public XML parse(final InputStream is) throws BadFormatException {
            Validate.notNull(is, "Input stream cannot be null");
            try {
                DocumentBuilder builder = factory.newDocumentBuilder();
                builder.setEntityResolver(resolver);
                Document xml = builder.parse(is);
                return XML.of(xml);
            } catch (SAXException e) {
                throw new BadFormatException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ParserConfigurationException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
