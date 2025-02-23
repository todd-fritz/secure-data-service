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
 * Definition of the performance levels and the
 *                 associated cut scores. Three styles are supported:
 *                 1. Specification
 *                 of performance level by min and max score
 *                 2. Specification of
 *                 performance level by cut score - min only
 *                 3. Specification of
 *                 performance level without any mapping to scores
 *             
 * 
 * <p>Java class for assessmentPerformanceLevel complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="assessmentPerformanceLevel">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="performanceLevelDescriptor" type="{http://slc-sli/ed-org/0.1}performanceLevelDescriptor"/>
 *         &lt;element name="assessmentReportingMethod" type="{http://slc-sli/ed-org/0.1}assessmentReportingMethodType"/>
 *         &lt;element name="minimumScore" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="maximumScore" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "assessmentPerformanceLevel", propOrder = {
    "performanceLevelDescriptor",
    "assessmentReportingMethod",
    "minimumScore",
    "maximumScore"
})
public class AssessmentPerformanceLevel {

    @XmlElement(required = true)
    protected PerformanceLevelDescriptor performanceLevelDescriptor;
    @XmlElement(required = true)
    protected AssessmentReportingMethodType assessmentReportingMethod;
    protected Integer minimumScore;
    protected Integer maximumScore;

    /**
     * Gets the value of the performanceLevelDescriptor property.
     * 
     * @return
     *     possible object is
     *     {@link PerformanceLevelDescriptor }
     *     
     */
    public PerformanceLevelDescriptor getPerformanceLevelDescriptor() {
        return performanceLevelDescriptor;
    }

    /**
     * Sets the value of the performanceLevelDescriptor property.
     * 
     * @param value
     *     allowed object is
     *     {@link PerformanceLevelDescriptor }
     *     
     */
    public void setPerformanceLevelDescriptor(PerformanceLevelDescriptor value) {
        this.performanceLevelDescriptor = value;
    }

    /**
     * Gets the value of the assessmentReportingMethod property.
     * 
     * @return
     *     possible object is
     *     {@link AssessmentReportingMethodType }
     *     
     */
    public AssessmentReportingMethodType getAssessmentReportingMethod() {
        return assessmentReportingMethod;
    }

    /**
     * Sets the value of the assessmentReportingMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link AssessmentReportingMethodType }
     *     
     */
    public void setAssessmentReportingMethod(AssessmentReportingMethodType value) {
        this.assessmentReportingMethod = value;
    }

    /**
     * Gets the value of the minimumScore property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMinimumScore() {
        return minimumScore;
    }

    /**
     * Sets the value of the minimumScore property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMinimumScore(Integer value) {
        this.minimumScore = value;
    }

    /**
     * Gets the value of the maximumScore property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMaximumScore() {
        return maximumScore;
    }

    /**
     * Sets the value of the maximumScore property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMaximumScore(Integer value) {
        this.maximumScore = value;
    }

}
