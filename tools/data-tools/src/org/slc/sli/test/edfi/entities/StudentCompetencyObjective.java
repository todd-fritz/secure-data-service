//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.12.06 at 10:00:50 AM EST 
//


package org.slc.sli.test.edfi.entities;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * This entity holds additional competencies for student achievement that are not associated with specific learning objectives (e.g., paying attention in class).
 * 
 * <p>Java class for StudentCompetencyObjective complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="StudentCompetencyObjective">
 *   &lt;complexContent>
 *     &lt;extension base="{http://ed-fi.org/0100}ComplexObjectType">
 *       &lt;sequence>
 *         &lt;element name="StudentCompetencyObjectiveId" type="{http://ed-fi.org/0100}IdentificationCode" minOccurs="0"/>
 *         &lt;element name="Objective" type="{http://ed-fi.org/0100}Objective"/>
 *         &lt;element name="Description" type="{http://ed-fi.org/0100}Description" minOccurs="0"/>
 *         &lt;element name="ObjectiveGradeLevel" type="{http://ed-fi.org/0100}GradeLevelType"/>
 *         &lt;element name="EducationOrganizationReference" type="{http://ed-fi.org/0100}EducationalOrgReferenceType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StudentCompetencyObjective", propOrder = {
    "studentCompetencyObjectiveId",
    "objective",
    "description",
    "objectiveGradeLevel",
    "educationOrganizationReference"
})
public class StudentCompetencyObjective
    extends ComplexObjectType
{

    @XmlElement(name = "StudentCompetencyObjectiveId")
    protected String studentCompetencyObjectiveId;
    @XmlElement(name = "Objective", required = true)
    protected String objective;
    @XmlElement(name = "Description")
    protected String description;
    @XmlElement(name = "ObjectiveGradeLevel", required = true)
    protected GradeLevelType objectiveGradeLevel;
    @XmlElement(name = "EducationOrganizationReference", required = true)
    protected EducationalOrgReferenceType educationOrganizationReference;

    /**
     * Gets the value of the studentCompetencyObjectiveId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStudentCompetencyObjectiveId() {
        return studentCompetencyObjectiveId;
    }

    /**
     * Sets the value of the studentCompetencyObjectiveId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStudentCompetencyObjectiveId(String value) {
        this.studentCompetencyObjectiveId = value;
    }

    /**
     * Gets the value of the objective property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getObjective() {
        return objective;
    }

    /**
     * Sets the value of the objective property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setObjective(String value) {
        this.objective = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the objectiveGradeLevel property.
     * 
     * @return
     *     possible object is
     *     {@link GradeLevelType }
     *     
     */
    public GradeLevelType getObjectiveGradeLevel() {
        return objectiveGradeLevel;
    }

    /**
     * Sets the value of the objectiveGradeLevel property.
     * 
     * @param value
     *     allowed object is
     *     {@link GradeLevelType }
     *     
     */
    public void setObjectiveGradeLevel(GradeLevelType value) {
        this.objectiveGradeLevel = value;
    }

    /**
     * Gets the value of the educationOrganizationReference property.
     * 
     * @return
     *     possible object is
     *     {@link EducationalOrgReferenceType }
     *     
     */
    public EducationalOrgReferenceType getEducationOrganizationReference() {
        return educationOrganizationReference;
    }

    /**
     * Sets the value of the educationOrganizationReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link EducationalOrgReferenceType }
     *     
     */
    public void setEducationOrganizationReference(EducationalOrgReferenceType value) {
        this.educationOrganizationReference = value;
    }

}
