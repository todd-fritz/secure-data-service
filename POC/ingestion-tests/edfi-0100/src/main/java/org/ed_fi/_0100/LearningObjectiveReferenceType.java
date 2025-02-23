//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.01.22 at 01:42:02 PM EST 
//


package org.ed_fi._0100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Provides alternative references for Learning Objective reference during interchange. Use XML IDREF to reference a learning standard record that is included in the interchange
 * 
 * <p>Java class for LearningObjectiveReferenceType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LearningObjectiveReferenceType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://ed-fi.org/0100}ReferenceType">
 *       &lt;sequence>
 *         &lt;element name="LearningObjectiveIdentity" type="{http://ed-fi.org/0100}LearningObjectiveIdentityType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LearningObjectiveReferenceType", propOrder = {
    "learningObjectiveIdentity"
})
public class LearningObjectiveReferenceType
    extends ReferenceType
{

    @XmlElement(name = "LearningObjectiveIdentity")
    protected LearningObjectiveIdentityType learningObjectiveIdentity;

    /**
     * Gets the value of the learningObjectiveIdentity property.
     * 
     * @return
     *     possible object is
     *     {@link LearningObjectiveIdentityType }
     *     
     */
    public LearningObjectiveIdentityType getLearningObjectiveIdentity() {
        return learningObjectiveIdentity;
    }

    /**
     * Sets the value of the learningObjectiveIdentity property.
     * 
     * @param value
     *     allowed object is
     *     {@link LearningObjectiveIdentityType }
     *     
     */
    public void setLearningObjectiveIdentity(LearningObjectiveIdentityType value) {
        this.learningObjectiveIdentity = value;
    }

}
