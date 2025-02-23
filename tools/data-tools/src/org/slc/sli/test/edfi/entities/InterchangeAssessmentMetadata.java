//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.12.06 at 10:00:50 AM EST 
//


package org.slc.sli.test.edfi.entities;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded">
 *         &lt;element name="AssessmentFamily" type="{http://ed-fi.org/0100}AssessmentFamily"/>
 *         &lt;element name="Assessment" type="{http://ed-fi.org/0100}SLC-Assessment"/>
 *         &lt;element name="AssessmentPeriodDescriptor" type="{http://ed-fi.org/0100}AssessmentPeriodDescriptor"/>
 *         &lt;element name="PerformanceLevelDescriptor" type="{http://ed-fi.org/0100}PerformanceLevelDescriptor"/>
 *         &lt;element name="ObjectiveAssessment" type="{http://ed-fi.org/0100}SLC-ObjectiveAssessment"/>
 *         &lt;element name="AssessmentItem" type="{http://ed-fi.org/0100}SLC-AssessmentItem"/>
 *         &lt;element name="LearningObjective" type="{http://ed-fi.org/0100}SLC-LearningObjective"/>
 *         &lt;element name="LearningStandard" type="{http://ed-fi.org/0100}LearningStandard"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "assessmentFamilyOrAssessmentOrAssessmentPeriodDescriptor"
})
@XmlRootElement(name = "InterchangeAssessmentMetadata")
public class InterchangeAssessmentMetadata {

    @XmlElements({
        @XmlElement(name = "PerformanceLevelDescriptor", type = PerformanceLevelDescriptor.class),
        @XmlElement(name = "Assessment", type = SLCAssessment.class),
        @XmlElement(name = "ObjectiveAssessment", type = SLCObjectiveAssessment.class),
        @XmlElement(name = "LearningStandard", type = LearningStandard.class),
        @XmlElement(name = "AssessmentItem", type = SLCAssessmentItem.class),
        @XmlElement(name = "AssessmentFamily", type = AssessmentFamily.class),
        @XmlElement(name = "AssessmentPeriodDescriptor", type = AssessmentPeriodDescriptor.class),
        @XmlElement(name = "LearningObjective", type = SLCLearningObjective.class)
    })
    protected List<ComplexObjectType> assessmentFamilyOrAssessmentOrAssessmentPeriodDescriptor;

    /**
     * Gets the value of the assessmentFamilyOrAssessmentOrAssessmentPeriodDescriptor property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the assessmentFamilyOrAssessmentOrAssessmentPeriodDescriptor property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAssessmentFamilyOrAssessmentOrAssessmentPeriodDescriptor().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PerformanceLevelDescriptor }
     * {@link SLCAssessment }
     * {@link SLCObjectiveAssessment }
     * {@link LearningStandard }
     * {@link SLCAssessmentItem }
     * {@link AssessmentFamily }
     * {@link AssessmentPeriodDescriptor }
     * {@link SLCLearningObjective }
     * 
     * 
     */
    public List<ComplexObjectType> getAssessmentFamilyOrAssessmentOrAssessmentPeriodDescriptor() {
        if (assessmentFamilyOrAssessmentOrAssessmentPeriodDescriptor == null) {
            assessmentFamilyOrAssessmentOrAssessmentPeriodDescriptor = new ArrayList<ComplexObjectType>();
        }
        return this.assessmentFamilyOrAssessmentOrAssessmentPeriodDescriptor;
    }

}
