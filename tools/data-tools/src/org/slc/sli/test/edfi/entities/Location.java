//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.11.16 at 01:39:34 PM EST 
//


package org.slc.sli.test.edfi.entities;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * This entity represents the physical space where students gather for a particular class/section.  The location may be an indoor or outdoor area designated for the purpose of meeting the educational needs of students.
 * 
 * <p>Java class for Location complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Location">
 *   &lt;complexContent>
 *     &lt;extension base="{http://ed-fi.org/0100}ComplexObjectType">
 *       &lt;sequence>
 *         &lt;element name="ClassroomIdentificationCode" type="{http://ed-fi.org/0100}ClassroomIdentificationCode"/>
 *         &lt;element name="MaximumNumberOfSeats" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="OptimalNumberOfSeats" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Location", propOrder = {
    "classroomIdentificationCode",
    "maximumNumberOfSeats",
    "optimalNumberOfSeats"
})
public class Location
    extends ComplexObjectType
{

    @XmlElement(name = "ClassroomIdentificationCode", required = true)
    protected String classroomIdentificationCode;
    @XmlElement(name = "MaximumNumberOfSeats")
    protected Integer maximumNumberOfSeats;
    @XmlElement(name = "OptimalNumberOfSeats")
    protected Integer optimalNumberOfSeats;

    /**
     * Gets the value of the classroomIdentificationCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClassroomIdentificationCode() {
        return classroomIdentificationCode;
    }

    /**
     * Sets the value of the classroomIdentificationCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClassroomIdentificationCode(String value) {
        this.classroomIdentificationCode = value;
    }

    /**
     * Gets the value of the maximumNumberOfSeats property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMaximumNumberOfSeats() {
        return maximumNumberOfSeats;
    }

    /**
     * Sets the value of the maximumNumberOfSeats property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMaximumNumberOfSeats(Integer value) {
        this.maximumNumberOfSeats = value;
    }

    /**
     * Gets the value of the optimalNumberOfSeats property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getOptimalNumberOfSeats() {
        return optimalNumberOfSeats;
    }

    /**
     * Sets the value of the optimalNumberOfSeats property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setOptimalNumberOfSeats(Integer value) {
        this.optimalNumberOfSeats = value;
    }

}
