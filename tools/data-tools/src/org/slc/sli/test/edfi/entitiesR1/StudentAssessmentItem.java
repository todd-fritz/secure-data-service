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
import javax.xml.bind.annotation.XmlType;


/**
 * This entity represents the student's response to an
 *                 assessment item and the item-level scores such as correct,
 *                 incorrect, or met standard.
 *             
 * 
 * <p>Java class for studentAssessmentItem complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="studentAssessmentItem">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="assessmentResponse" type="{http://slc-sli/ed-org/0.1}assessmentResponse" minOccurs="0"/>
 *         &lt;element name="responseIndicator" type="{http://slc-sli/ed-org/0.1}responseIndicatorType" minOccurs="0"/>
 *         &lt;element name="assessmentItemResult" type="{http://slc-sli/ed-org/0.1}assessmentItemResultType"/>
 *         &lt;element name="rawScoreResult" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="assessmentItem" type="{http://slc-sli/ed-org/0.1}assessmentItem"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "studentAssessmentItem", propOrder = {
    "assessmentResponse",
    "responseIndicator",
    "assessmentItemResult",
    "rawScoreResult",
    "assessmentItem"
})
public class StudentAssessmentItem {

    protected String assessmentResponse;
    protected ResponseIndicatorType responseIndicator;
    @XmlElement(required = true)
    protected AssessmentItemResultType assessmentItemResult;
    protected Integer rawScoreResult;
    @XmlElement(required = true)
    protected AssessmentItem assessmentItem;

    /**
     * Gets the value of the assessmentResponse property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAssessmentResponse() {
        return assessmentResponse;
    }

    /**
     * Sets the value of the assessmentResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAssessmentResponse(String value) {
        this.assessmentResponse = value;
    }

    /**
     * Gets the value of the responseIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link ResponseIndicatorType }
     *     
     */
    public ResponseIndicatorType getResponseIndicator() {
        return responseIndicator;
    }

    /**
     * Sets the value of the responseIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResponseIndicatorType }
     *     
     */
    public void setResponseIndicator(ResponseIndicatorType value) {
        this.responseIndicator = value;
    }

    /**
     * Gets the value of the assessmentItemResult property.
     * 
     * @return
     *     possible object is
     *     {@link AssessmentItemResultType }
     *     
     */
    public AssessmentItemResultType getAssessmentItemResult() {
        return assessmentItemResult;
    }

    /**
     * Sets the value of the assessmentItemResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link AssessmentItemResultType }
     *     
     */
    public void setAssessmentItemResult(AssessmentItemResultType value) {
        this.assessmentItemResult = value;
    }

    /**
     * Gets the value of the rawScoreResult property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getRawScoreResult() {
        return rawScoreResult;
    }

    /**
     * Sets the value of the rawScoreResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setRawScoreResult(Integer value) {
        this.rawScoreResult = value;
    }

    /**
     * Gets the value of the assessmentItem property.
     * 
     * @return
     *     possible object is
     *     {@link AssessmentItem }
     *     
     */
    public AssessmentItem getAssessmentItem() {
        return assessmentItem;
    }

    /**
     * Sets the value of the assessmentItem property.
     * 
     * @param value
     *     allowed object is
     *     {@link AssessmentItem }
     *     
     */
    public void setAssessmentItem(AssessmentItem value) {
        this.assessmentItem = value;
    }

}
