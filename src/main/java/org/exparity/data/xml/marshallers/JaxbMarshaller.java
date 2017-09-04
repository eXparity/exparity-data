/*
 *
 */

package org.exparity.data.xml.marshallers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.exparity.data.BadFormatException;
import org.exparity.data.XML;
import org.exparity.data.xml.MarshallFailedException;
import org.exparity.data.xml.MarshallerConfigurationError;
import org.exparity.data.xml.XmlMarshaller;

/**
 * @author Stewart Bissett
 */
public class JaxbMarshaller<T> implements XmlMarshaller<T> {

    private final JAXBContext ctx;

    public JaxbMarshaller(final Class<?> klass) throws MarshallerConfigurationError {
        try {
            this.ctx = JAXBContext.newInstance(klass);
        } catch (JAXBException e) {
            throw new MarshallerConfigurationError(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public T unmarshal(final XML source) throws BadFormatException, MarshallFailedException {
        try {
            final Unmarshaller unmarshaller = ctx.createUnmarshaller();
            return (T) unmarshaller.unmarshal(source.asDocument());
        } catch (UnmarshalException e) {
            throw new BadFormatException(ExceptionUtils.getRootCauseMessage(e), e);
        } catch (JAXBException e) {
            throw new MarshallFailedException(e);
        }
    }

    @Override
    public XML marshall(final T raw) throws MarshallFailedException {
        try {
            Marshaller marshaller = ctx.createMarshaller();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            marshaller.marshal(raw, os);
            return XML.read(new ByteArrayInputStream(os.toByteArray()));
        } catch (JAXBException e) {
            throw new MarshallFailedException(e);
        } catch (BadFormatException e) {
            throw new MarshallFailedException(e);
        }
    }
}
