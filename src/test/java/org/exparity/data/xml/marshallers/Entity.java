/*
 * 
 */

package org.exparity.data.xml.marshallers;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "Entity")
@XmlType(name = "Entity")
@XmlAccessorType(XmlAccessType.FIELD)
class Entity {

    @XmlElement(required = true)
    private String elementA;
    @XmlElement(required = true)
    private Boolean elementB;
    @XmlElement(required = true)
    private Double elementC;

    public String getElementA() {
        return elementA;
    }

    public void setElementA(final String elementA) {
        this.elementA = elementA;
    }

    public Boolean getElementB() {
        return elementB;
    }

    public void setElementB(final Boolean elementB) {
        this.elementB = elementB;
    }

    public Double getElementC() {
        return elementC;
    }

    public void setElementC(final Double elementC) {
        this.elementC = elementC;
    }

}