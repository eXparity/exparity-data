/*
 *
 */

package org.exparity.data;

import static java.util.stream.Collectors.toList;
import static org.exparity.data.xml.parser.XmlParserBuilder.newParserFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

import javax.xml.namespace.NamespaceContext;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.exparity.data.html.HtmlCleanerXmlFactory;
import org.exparity.data.types.Pair;
import org.exparity.data.xml.MarshallFailedException;
import org.exparity.data.xml.TransformFailedException;
import org.exparity.data.xml.ValidationResult;
import org.exparity.data.xml.XmlFactory;
import org.exparity.data.xml.XmlMarshaller;
import org.exparity.data.xml.XmlMatcher;
import org.exparity.data.xml.XmlParser;
import org.exparity.data.xml.XmlSelector;
import org.exparity.data.xml.XmlTransformer;
import org.exparity.data.xml.XmlValidator;
import org.exparity.data.xml.transformers.StylesheetTransformer;
import org.exparity.data.xml.validators.SchemaValidator;
import org.exparity.io.TextDataSource;
import org.exparity.io.classpath.JcpFile;
import org.exparity.io.filesystem.FileSystemFile;
import org.exparity.io.internet.InternetFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * {@link XML} models a XML Document. Instantiate using the factory methods.
 *
 * @author Stewart Bissett
 */
public class XML extends Text {

    private static final String COMPACTING_XLST =
            "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">"
                    + "<xsl:output method=\"xml\" omit-xml-declaration=\"yes\"/>"
                    + "<xsl:strip-space elements=\"*\"/>"
                    + "<xsl:template match=\"@*|node()\">"
                    + "<xsl:copy>"
                    + "<xsl:apply-templates select=\"@*|node()\"/>"
                    + "</xsl:copy>"
                    + "</xsl:template>"
                    + "</xsl:stylesheet>";

    private static final Logger LOG = LoggerFactory.getLogger(XML.class);
    private static final int DEFAULT_INDENT = 2;
    private static final XmlParser DEFAULT_PARSER = newParserFactory().setNamespaceAware(false).build();
    private static final Function<Node, Element> NODE_TO_ELEMENT_TRANSFORMER = new Function<Node, Element>() {

        @Override
        public Element apply(final Node node) {
            return (Element) node;
        }
    };

    private final Document document;
    private ConcurrentMap<Integer, String> formattedText = new ConcurrentHashMap<>();
    private String text;

    public static XML empty() {
        try {
            return XML.openResource("empty.xml", XML.class);
        } catch (IOException e) {
            throw new RuntimeException("Unexpected error initialising EMPTY. Error [" + e.getClass().getSimpleName()
                    + ":"
                    + e.getMessage()
                    + "]");
        }
    }

    public static XML openResource(final String source) throws IOException {
        return XML.read(JcpFile.open(source));
    }

    public static XML openResource(final String source, final ClassLoader loader) throws IOException {
        return XML.read(JcpFile.open(source, loader));
    }

    public static XML openResource(final String source, final Class<?> klass) throws IOException {
        return XML.read(JcpFile.open(source, klass));
    }

    public static XML openFile(final String source) throws IOException {
        return XML.read(FileSystemFile.open(source));
    }

    public static XML openFile(final File source) throws IOException {
        return XML.read(FileSystemFile.open(source));
    }

    public static XML openURL(final String source) throws IOException {
        return XML.read(InternetFile.open(source));
    }

    public static XML openURL(final URL source) throws IOException {
        return XML.read(InternetFile.open(source));
    }

    public static XML read(final TextDataSource source) {
        return read(source, DEFAULT_PARSER);
    }

    public static XML read(final TextDataSource source, final XmlParser parser) {
        return read(source.getStream(), parser);
    }

    public static XML read(final InputStream source) {
        return read(source, DEFAULT_PARSER);
    }

    public static XML read(final InputStream source, final XmlParser parser) {
        return parser.parse(source);
    }

    public static XML of(final Document node) {
        return new XML(node);
    }

    public static <T> XML of(final T source, final XmlFactory<T> factory) {
        return factory.convert(source);
    }

    public static XML of(final HTML html) {
        return of(html, new HtmlCleanerXmlFactory(DEFAULT_PARSER));
    }

    public static XML of(final String xml) {
        return of(xml, DEFAULT_PARSER);
    }

    public static XML of(final String xml, final XmlParser parser) {
        return read(new ByteArrayInputStream(xml.getBytes()), parser);
    }

    /**
     * Construct an {@link XML} instance using a {@link Document} instance
     */
    private XML(final Document document) {
        this.document = document;
    }

    public List<Element> findElementsByTagName(final String elementName) {
        if (elementName == null) {
            return Collections.emptyList();
        }

        NodeList nodes = document.getElementsByTagName(elementName);

        List<Element> found = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); ++i) {
            found.add((Element) nodes.item(i));
        }
        return found;
    }

    public List<Node> findNodesByXpath(final String xpath) {
        List<Node> found = new ArrayList<>();
        try {
            NodeList nodes = (NodeList) XPathFactory.newInstance().newXPath().compile(xpath).evaluate(document,
                    XPathConstants.NODESET);
            for (int i = 0; i < nodes.getLength(); ++i) {
                found.add(nodes.item(i));
            }
        } catch (XPathExpressionException e) {
            LOG.error("Failed to evaluated xpath. Error [" + e.getClass().getSimpleName() + ":" + e.getMessage() + "]");
        }
        return found;
    }

    public List<Element> findElementsByXpath(final String xpath) {
        return findNodesByXpath(xpath).stream().map(NODE_TO_ELEMENT_TRANSFORMER).collect(toList());
    }

    public String findTextByXpath(final String xpath) {
        try {
            return XPathFactory.newInstance().newXPath().compile(xpath).evaluate(document);
        } catch (XPathExpressionException e) {
            Throwable rootCause = ExceptionUtils.getRootCause(e);
            LOG.error("Failed to evaluated xpath. Error [" + rootCause.getClass().getSimpleName()
                    + ":"
                    + rootCause.getMessage()
                    + "]");
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public String findTextByXpath(final String xpath, final Pair<String, String>... namespaces) {
        final XPath instance = XPathFactory.newInstance().newXPath();

        if (ArrayUtils.isNotEmpty(namespaces)) {
            instance.setNamespaceContext(new NamespaceContext() {

                @Override
                public Iterator<String> getPrefixes(final String namespaceURI) {
                    List<String> prefixes = new ArrayList<>();
                    for (Pair<String, String> namespace : namespaces) {
                        if (namespace.getValue2().equals(namespaceURI)) {
                            prefixes.add(namespace.getValue1());
                        }
                    }
                    return prefixes.iterator();
                }

                @Override
                public String getPrefix(final String namespaceURI) {
                    for (Pair<String, String> namespace : namespaces) {
                        if (namespace.getValue2().equals(namespaceURI)) {
                            return namespace.getValue1();
                        }
                    }
                    return null;
                }

                @Override
                public String getNamespaceURI(final String prefix) {
                    for (Pair<String, String> namespace : namespaces) {
                        if (namespace.getValue1().equals(prefix)) {
                            return namespace.getValue2();
                        }
                    }
                    return null;
                }
            });
        }

        try {
            final String result = instance.compile(xpath).evaluate(document);
            return StringUtils.isEmpty(result) ? null : result.trim();
        } catch (XPathExpressionException e) {
            LOG.error("Failed to evaluated xpath. Error [" + e.getClass().getSimpleName() + ":" + e.getMessage() + "]");
            return null;
        }
    }

    public Boolean findBooleanByXpath(final String xpath) {
        try {
            return (Boolean) XPathFactory.newInstance().newXPath().compile(xpath).evaluate(document,
                    XPathConstants.BOOLEAN);
        } catch (XPathExpressionException e) {
            LOG.error("Failed to evaluated xpath. Error [" + e.getClass().getSimpleName() + ":" + e.getMessage() + "]");
            return null;
        }
    }

    public boolean has(final XmlMatcher matcher) {
        return matcher.matches(this);
    }

    public <T> T find(final XmlSelector<T> selector) {
        return selector.select(this);
    }

    /**
     * Get the text of the XML document.
     */
    @Override
    public String getText() {
        if (text == null) {
            try {
                StringWriter writer = new StringWriter();
                TransformerFactory txFactory = TransformerFactory.newInstance();
                Transformer transformer = txFactory.newTransformer();
                transformer.transform(new DOMSource(document), new StreamResult(writer));
                text = writer.toString();
            } catch (TransformerConfigurationException e) {
                throw new RuntimeException(e);
            } catch (TransformerException e) {
                throw new RuntimeException(e);
            }
        }
        return text;
    }

    public String getCompactText() {
        try {
            StringWriter writer = new StringWriter();
            TransformerFactory txFactory = TransformerFactory.newInstance();
            Transformer transformer = txFactory.newTransformer(new StreamSource(new ByteArrayInputStream(COMPACTING_XLST
                    .getBytes())));
            transformer.transform(new DOMSource(document), new StreamResult(writer));
            return writer.toString();
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException(e);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    public String getFormattedText() {
        return getFormattedText(DEFAULT_INDENT);
    }

    public String getFormattedText(final int indent) {
        if (!formattedText.containsKey(indent)) {
            formattedText.putIfAbsent(indent, writeXmlToString(indent));
        }
        return formattedText.get(indent);
    }

    private String writeXmlToString(final int indent) throws TransformerFactoryConfigurationError {
        try {
            StringWriter writer = new StringWriter();
            TransformerFactory txFactory = TransformerFactory.newInstance();
            Transformer transformer = txFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", String.valueOf(indent));
            transformer.transform(new DOMSource(document), new StreamResult(writer));
            return writer.toString();
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException(e);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    public Document asDocument() {
        return document;
    }

    /**
     * Supply a resource which is parsed as an XSL stylesheet and then used to transform this document to another
     * {@link XML}
     */
    public XML transform(final String resource) throws TransformFailedException, BadFormatException {
        return transform(resource, getClass());
    }

    /**
     * Supply a resource which is parsed as an XSL stylesheet and then used to transform this document to another
     * {@link XML}
     */
    public XML transform(final String resource, final Class<?> klass) throws TransformFailedException,
            BadFormatException {
        return transform(klass.getResourceAsStream(resource));
    }

    /**
     * Supply a resource which is parsed as an XSL stylesheet and then used to transform this document to another
     * {@link XML}
     */
    public XML transform(final InputStream resource) throws TransformFailedException, BadFormatException {
        return transform(new StylesheetTransformer(resource, null));
    }

    /**
     * Supply a transformer which converts this XmlDocument into another format.
     */
    public <T> T transform(final XmlTransformer<T> transformer) throws TransformFailedException {
        return transformer.transform(this);
    }

    /**
     * Supply a resource which is parsed as an XSL schema and used to validate this document
     */
    public ValidationResult validate(final String resource) {
        return validate(resource, getClass());
    }

    /**
     * Supply a resource which is parsed as an XSL schema and used to validate this document
     */
    public ValidationResult validate(final String resource, final Class<?> klass) {
        return validate(klass.getResourceAsStream(resource));
    }

    /**
     * Supply a resource which is parsed as an XSL schema and used to validate this document
     */
    public ValidationResult validate(final InputStream resource) {
        return validate(new SchemaValidator(resource));
    }

    /**
     * Supply a validator which will check the integrity of this document
     */
    public ValidationResult validate(final XmlValidator validator) {
        return validator.validate(this);
    }

    /**
     * Supply a marshaller to convert this XML document into an object.
     */
    public <T> T unmarshall(final XmlMarshaller<T> marshaller) throws MarshallFailedException, BadFormatException {
        return marshaller.unmarshal(this);
    }
}
