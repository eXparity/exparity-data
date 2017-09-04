/*
 *
 */

package org.exparity.data.xml.transformers;

import java.io.InputStream;
import java.io.Reader;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.exparity.data.BadFormatException;
import org.exparity.data.XML;
import org.exparity.data.xml.TransformFailedException;
import org.exparity.data.xml.XmlTransformer;
import org.exparity.io.TextDataSource;
import org.w3c.dom.Document;

/**
 * @author Stewart Bissett
 */
public class StylesheetTransformer implements XmlTransformer<XML> {

    private final Templates templates;
    private final ThreadLocal<Transformer> transformers = new ThreadLocal<Transformer>() {

        @Override
        protected Transformer initialValue() {
            try {
                return templates.newTransformer();
            } catch (TransformerConfigurationException e) {
                throw new RuntimeException(e);
            }
        }
    };

    public StylesheetTransformer(final Reader reader) throws BadFormatException {
        try {
            this.templates = TransformerFactory.newInstance().newTemplates(new StreamSource(reader));
        } catch (TransformerConfigurationException e) {
            throw new BadFormatException(e);
        }
    }

    public StylesheetTransformer(final TextDataSource source) throws BadFormatException {
        this(source.getReader());
    }

    public StylesheetTransformer(final InputStream is, final URIResolver resolver) throws BadFormatException {
        try {
            TransformerFactory factory = TransformerFactory.newInstance();
            factory.setURIResolver(resolver);
            this.templates = factory.newTemplates(new StreamSource(is));
        } catch (TransformerConfigurationException e) {
            throw new BadFormatException(e);
        }
    }

    public StylesheetTransformer(final XML stylesheet) throws BadFormatException {
        try {
            this.templates = TransformerFactory.newInstance().newTemplates(new DOMSource(stylesheet.asDocument()));
        } catch (TransformerConfigurationException e) {
            throw new BadFormatException(e);
        }
    }

    @Override
    public XML transform(final XML source) throws TransformFailedException {
        try {
            DOMResult result = new DOMResult();
            transformers.get().transform(new DOMSource(source.asDocument()), result);
            return XML.of((Document) result.getNode());
        } catch (TransformerException e) {
            throw new TransformFailedException(e);
        }
    }
}
