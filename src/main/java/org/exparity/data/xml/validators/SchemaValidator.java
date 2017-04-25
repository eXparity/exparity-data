/*
 * 
 */

package org.exparity.data.xml.validators;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.collections.CollectionUtils;
import org.exparity.data.XML;
import org.exparity.data.xml.ValidationResult;
import org.exparity.data.xml.ValidatorConfigurationError;
import org.exparity.data.xml.XmlValidator;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author Stewart Bissett
 */
public class SchemaValidator implements XmlValidator
{
	private final Schema schema;

	public SchemaValidator(final InputStream schemaAsStream)
	{
		try {
			this.schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
					.newSchema(new StreamSource(schemaAsStream));
		}
		catch (SAXException e) {
			throw new ValidatorConfigurationError(e);
		}
	}

	@Override
	public ValidationResult validate(final XML document)
	{
		final List<String> warnings = new ArrayList<String>(), errors = new ArrayList<String>();

		try {
			final Validator validator = schema.newValidator();
			validator.setErrorHandler(new ErrorHandler()
			{
				@Override
				public void warning(final SAXParseException exception) throws SAXException
				{
					warnings.add(exception.getMessage());
				}

				@Override
				public void fatalError(final SAXParseException exception) throws SAXException
				{
					errors.add(exception.getMessage());
				}

				@Override
				public void error(final SAXParseException exception) throws SAXException
				{
					errors.add(exception.getMessage());
				}
			});
			validator.validate(new DOMSource(document.asDocument()));
			return new ValidationResult(CollectionUtils.isEmpty(errors), errors);
		}
		catch (SAXException e) {
			return new ValidationResult(false, e.getMessage());
		}
		catch (IOException e) {
			return new ValidationResult(false, e.getMessage());
		}
	}
}
