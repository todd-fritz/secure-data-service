//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.08.31 at 10:43:34 AM EDT 
//


package org.slc.sli.test.edfi.entitiesR1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * 
 *                 This entity captures significant postsecondary
 *                 events during a student's high school tenure (e.g.,
 *                 FASFSA
 *                 application, or college application, acceptance, and enrollment).
 *             
 * 
 * <p>Java class for postSecondaryEvent complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="postSecondaryEvent">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="eventDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="postSecondaryEventCategory" type="{http://slc-sli/ed-org/0.1}postSecondaryEventCategoryType"/>
 *         &lt;element name="nameOfInstitution" type="{http://slc-sli/ed-org/0.1}nameOfInstitution" minOccurs="0"/>
 *         &lt;element name="institutionId" type="{http://slc-sli/ed-org/0.1}reference" minOccurs="0"/>
 *         &lt;element name="studentId" type="{http://slc-sli/ed-org/0.1}reference"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "postSecondaryEvent", propOrder = {
    "eventDate",
    "postSecondaryEventCategory",
    "nameOfInstitution",
    "institutionId",
    "studentId"
})
public class PostSecondaryEvent {

    @XmlElement(required = true)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "date")
    protected String eventDate;
    @XmlElement(required = true)
    protected PostSecondaryEventCategoryType postSecondaryEventCategory;
    protected String nameOfInstitution;
    protected String institutionId;
    @XmlElement(required = true)
    protected String studentId;

    /**
     * Gets the value of the eventDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEventDate() {
        return eventDate;
    }

    /**
     * Sets the value of the eventDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEventDate(String value) {
        this.eventDate = value;
    }

    /**
     * Gets the value of the postSecondaryEventCategory property.
     * 
     * @return
     *     possible object is
     *     {@link PostSecondaryEventCategoryType }
     *     
     */
    public PostSecondaryEventCategoryType getPostSecondaryEventCategory() {
        return postSecondaryEventCategory;
    }

    /**
     * Sets the value of the postSecondaryEventCategory property.
     * 
     * @param value
     *     allowed object is
     *     {@link PostSecondaryEventCategoryType }
     *     
     */
    public void setPostSecondaryEventCategory(PostSecondaryEventCategoryType value) {
        this.postSecondaryEventCategory = value;
    }

    /**
     * Gets the value of the nameOfInstitution property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNameOfInstitution() {
        return nameOfInstitution;
    }

    /**
     * Sets the value of the nameOfInstitution property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNameOfInstitution(String value) {
        this.nameOfInstitution = value;
    }

    /**
     * Gets the value of the institutionId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInstitutionId() {
        return institutionId;
    }

    /**
     * Sets the value of the institutionId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInstitutionId(String value) {
        this.institutionId = value;
    }

    /**
     * Gets the value of the studentId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStudentId() {
        return studentId;
    }

    /**
     * Sets the value of the studentId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStudentId(String value) {
        this.studentId = value;
    }

}
