//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.08.31 at 10:43:34 AM EDT 
//


package org.slc.sli.test.edfi.entitiesR1;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 *                 This entity represents any type of list of
 *                 designated students for tracking, analysis, or intervention.
 *             
 * 
 * <p>Java class for cohort complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cohort">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="cohortIdentifier" type="{http://slc-sli/ed-org/0.1}cohortIdentifier"/>
 *         &lt;element name="cohortDescription" type="{http://slc-sli/ed-org/0.1}cohortDescription" minOccurs="0"/>
 *         &lt;element name="cohortType" type="{http://slc-sli/ed-org/0.1}cohortType"/>
 *         &lt;element name="cohortScope" type="{http://slc-sli/ed-org/0.1}cohortScopeType" minOccurs="0"/>
 *         &lt;element name="academicSubject" type="{http://slc-sli/ed-org/0.1}descriptorReferenceType" minOccurs="0"/>
 *         &lt;element name="educationOrgId" type="{http://slc-sli/ed-org/0.1}reference"/>
 *         &lt;element name="programIds" type="{http://slc-sli/ed-org/0.1}reference" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="staffAssociations" type="{http://slc-sli/ed-org/0.1}staffCohortAssociation" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cohort", propOrder = {
    "cohortIdentifier",
    "cohortDescription",
    "cohortType",
    "cohortScope",
    "academicSubject",
    "educationOrgId",
    "programIds",
    "staffAssociations"
})
public class Cohort {

    @XmlElement(required = true)
    protected String cohortIdentifier;
    protected String cohortDescription;
    @XmlElement(required = true)
    protected CohortType cohortType;
    protected CohortScopeType cohortScope;
    protected DescriptorReferenceType academicSubject;
    @XmlElement(required = true)
    protected String educationOrgId;
    protected List<String> programIds;
    protected List<StaffCohortAssociation> staffAssociations;

    /**
     * Gets the value of the cohortIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCohortIdentifier() {
        return cohortIdentifier;
    }

    /**
     * Sets the value of the cohortIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCohortIdentifier(String value) {
        this.cohortIdentifier = value;
    }

    /**
     * Gets the value of the cohortDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCohortDescription() {
        return cohortDescription;
    }

    /**
     * Sets the value of the cohortDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCohortDescription(String value) {
        this.cohortDescription = value;
    }

    /**
     * Gets the value of the cohortType property.
     * 
     * @return
     *     possible object is
     *     {@link CohortType }
     *     
     */
    public CohortType getCohortType() {
        return cohortType;
    }

    /**
     * Sets the value of the cohortType property.
     * 
     * @param value
     *     allowed object is
     *     {@link CohortType }
     *     
     */
    public void setCohortType(CohortType value) {
        this.cohortType = value;
    }

    /**
     * Gets the value of the cohortScope property.
     * 
     * @return
     *     possible object is
     *     {@link CohortScopeType }
     *     
     */
    public CohortScopeType getCohortScope() {
        return cohortScope;
    }

    /**
     * Sets the value of the cohortScope property.
     * 
     * @param value
     *     allowed object is
     *     {@link CohortScopeType }
     *     
     */
    public void setCohortScope(CohortScopeType value) {
        this.cohortScope = value;
    }

    /**
     * Gets the value of the academicSubject property.
     * 
     * @return
     *     possible object is
     *     {@link DescriptorReferenceType }
     *     
     */
    public DescriptorReferenceType getAcademicSubject() {
        return academicSubject;
    }

    /**
     * Sets the value of the academicSubject property.
     * 
     * @param value
     *     allowed object is
     *     {@link DescriptorReferenceType }
     *     
     */
    public void setAcademicSubject(DescriptorReferenceType value) {
        this.academicSubject = value;
    }

    /**
     * Gets the value of the educationOrgId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEducationOrgId() {
        return educationOrgId;
    }

    /**
     * Sets the value of the educationOrgId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEducationOrgId(String value) {
        this.educationOrgId = value;
    }

    /**
     * Gets the value of the programIds property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the programIds property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProgramIds().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getProgramIds() {
        if (programIds == null) {
            programIds = new ArrayList<String>();
        }
        return this.programIds;
    }

    /**
     * Gets the value of the staffAssociations property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the staffAssociations property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStaffAssociations().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link StaffCohortAssociation }
     * 
     * 
     */
    public List<StaffCohortAssociation> getStaffAssociations() {
        if (staffAssociations == null) {
            staffAssociations = new ArrayList<StaffCohortAssociation>();
        }
        return this.staffAssociations;
    }

}
