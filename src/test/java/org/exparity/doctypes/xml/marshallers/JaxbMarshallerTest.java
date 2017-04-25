package org.exparity.doctypes.xml.marshallers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.exparity.data.BadFormatException;
import org.exparity.data.XML;
import org.exparity.data.xml.XmlMarshaller;
import org.exparity.data.xml.marshallers.JaxbMarshaller;
import org.junit.Test;

public class JaxbMarshallerTest {

    @Test
    public void canUnmarshall() throws Exception {
        XmlMarshaller<Entity> marshaller = new JaxbMarshaller<>(Entity.class);
        XML source = XML.openResource("entity.xml", JaxbMarshallerTest.class);

        Entity result = source.unmarshall(marshaller);

        assertEquals("elementA", result.getElementA());
        assertEquals(Boolean.TRUE, result.getElementB());
        assertEquals(1.0, result.getElementC(), 0.0);
    }

    @Test
    public void canUnmarshallWrongType() throws Exception {
        XmlMarshaller<Entity> marshaller = new JaxbMarshaller<>(Entity.class);
        XML source = XML.openResource("different.xml", JaxbMarshallerTest.class);

        try {
            source.unmarshall(marshaller);
            fail("Expected BadFormatException");
        } catch (BadFormatException e) {
            // Expected
        }
    }

    @Test
    public void canMarshall() throws Exception {
        final String elementAValue = "elementA";
        final boolean elementBValue = true;
        final double elementCValue = 1.0;

        Entity entity = new Entity();
        entity.setElementA(elementAValue);
        entity.setElementB(elementBValue);
        entity.setElementC(elementCValue);

        XmlMarshaller<Entity> marshaller = new JaxbMarshaller<>(Entity.class);
        XML result = marshaller.marshall(entity);

        assertEquals(elementAValue, result.findTextByXpath("/Entity/elementA"));
        assertEquals(String.valueOf(elementBValue), result.findTextByXpath("/Entity/elementB"));
        assertEquals(String.valueOf(elementCValue), result.findTextByXpath("/Entity/elementC"));
    }
}
