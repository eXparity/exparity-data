/*
 *
 */

package org.exparity.data.html;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.XMLConstants;

import org.apache.commons.lang.SystemUtils;
import org.exparity.data.HTML;
import org.exparity.data.XML;
import org.exparity.data.xml.XmlFactory;
import org.exparity.data.xml.XmlParser;
import org.exparity.data.xml.parser.XmlParserBuilder;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.SimpleXmlSerializer;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XmlSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Stewart Bissett
 */
public class HtmlCleanerXmlFactory implements XmlFactory<HTML> {

    private static final Logger LOG = LoggerFactory.getLogger(HtmlCleanerXmlFactory.class);
    private static final String HTML_LANG_ATTR = "lang";
    private static final String XML_LANG_ATTR = "xml:lang";

    private final HtmlCleaner cleaner;
    private final XmlSerializer serializer;
    private final XmlParser parser;

    public HtmlCleanerXmlFactory(final XmlParser parser, final HtmlCleaner cleaner, final XmlSerializer serializer) {
        this.parser = parser;
        this.cleaner = cleaner;
        this.serializer = serializer;
    }

    public HtmlCleanerXmlFactory(final CleanerProperties properties, final XmlParser parser) {
        this(parser, new HtmlCleaner(properties), new SimpleXmlSerializer(properties));
    }

    public HtmlCleanerXmlFactory(final XmlParser parser) {
        this(new CleanerProperties(), parser);
    }

    public HtmlCleanerXmlFactory() {
        this(XmlParserBuilder.newParserFactory().build());
    }

    @Override
    public XML convert(final HTML html) {
        try {
            TagNode result = cleanUpHtml(html);
            ByteArrayOutputStream xmlAsBytes = new ByteArrayOutputStream();
            serializer.writeToStream(result, xmlAsBytes);
            if (LOG.isTraceEnabled()) {
                LOG.trace("Parse HTML into XML" + SystemUtils.LINE_SEPARATOR + xmlAsBytes.toString());
            }
            return XML.read(new ByteArrayInputStream(xmlAsBytes.toByteArray()), parser);
        } catch (IOException e) {
            LOG.error("Unexpected IOException whilst parsing HTML " + e, e);
            throw new RuntimeException(e);
        }
    }

    private TagNode cleanUpHtml(final HTML html) throws IOException {
        TagNode cleanedHtml = cleaner.clean(html.asStream());
        cleanedHtml.addNamespaceDeclaration(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI);
        removeXmlLangAttribute(cleanedHtml);
        removeHtmlLangAttribute(cleanedHtml);
        return cleanedHtml;
    }

    private void removeXmlLangAttribute(final TagNode result) {
        result.removeAttribute(XML_LANG_ATTR);
    }

    private void removeHtmlLangAttribute(final TagNode result) {
        result.removeAttribute(HTML_LANG_ATTR);
    }

}
