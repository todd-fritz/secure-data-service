//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.12.06 at 10:00:50 AM EST 
//


package org.slc.sli.test.edfi.entities;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SeparationReasonType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="SeparationReasonType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *     &lt;enumeration value="Employment in education"/>
 *     &lt;enumeration value="Employment outside of education"/>
 *     &lt;enumeration value="Retirement"/>
 *     &lt;enumeration value="Family/personal relocation"/>
 *     &lt;enumeration value="Change of assignment"/>
 *     &lt;enumeration value="Formal study or research"/>
 *     &lt;enumeration value="Illness/disability"/>
 *     &lt;enumeration value="Homemaking/caring for a family member"/>
 *     &lt;enumeration value="Layoff due to budgetary reduction"/>
 *     &lt;enumeration value="Layoff due to organizational restructuring"/>
 *     &lt;enumeration value="Layoff due to decreased workload"/>
 *     &lt;enumeration value="Discharge due to unsuitability"/>
 *     &lt;enumeration value="Discharge due to misconduct"/>
 *     &lt;enumeration value="Discharge due to continued absence or tardiness"/>
 *     &lt;enumeration value="Discharge due to a falsified application form"/>
 *     &lt;enumeration value="Discharge due to credential revoked or suspended"/>
 *     &lt;enumeration value="Discharge due to unsatisfactory work performance"/>
 *     &lt;enumeration value="Death"/>
 *     &lt;enumeration value="Personal reason"/>
 *     &lt;enumeration value="Lay off due to lack of funding"/>
 *     &lt;enumeration value="Lost credential"/>
 *     &lt;enumeration value="Unknown"/>
 *     &lt;enumeration value="Other"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "SeparationReasonType")
@XmlEnum
public enum SeparationReasonType {

    @XmlEnumValue("Employment in education")
    EMPLOYMENT_IN_EDUCATION("Employment in education"),
    @XmlEnumValue("Employment outside of education")
    EMPLOYMENT_OUTSIDE_OF_EDUCATION("Employment outside of education"),
    @XmlEnumValue("Retirement")
    RETIREMENT("Retirement"),
    @XmlEnumValue("Family/personal relocation")
    FAMILY_PERSONAL_RELOCATION("Family/personal relocation"),
    @XmlEnumValue("Change of assignment")
    CHANGE_OF_ASSIGNMENT("Change of assignment"),
    @XmlEnumValue("Formal study or research")
    FORMAL_STUDY_OR_RESEARCH("Formal study or research"),
    @XmlEnumValue("Illness/disability")
    ILLNESS_DISABILITY("Illness/disability"),
    @XmlEnumValue("Homemaking/caring for a family member")
    HOMEMAKING_CARING_FOR_A_FAMILY_MEMBER("Homemaking/caring for a family member"),
    @XmlEnumValue("Layoff due to budgetary reduction")
    LAYOFF_DUE_TO_BUDGETARY_REDUCTION("Layoff due to budgetary reduction"),
    @XmlEnumValue("Layoff due to organizational restructuring")
    LAYOFF_DUE_TO_ORGANIZATIONAL_RESTRUCTURING("Layoff due to organizational restructuring"),
    @XmlEnumValue("Layoff due to decreased workload")
    LAYOFF_DUE_TO_DECREASED_WORKLOAD("Layoff due to decreased workload"),
    @XmlEnumValue("Discharge due to unsuitability")
    DISCHARGE_DUE_TO_UNSUITABILITY("Discharge due to unsuitability"),
    @XmlEnumValue("Discharge due to misconduct")
    DISCHARGE_DUE_TO_MISCONDUCT("Discharge due to misconduct"),
    @XmlEnumValue("Discharge due to continued absence or tardiness")
    DISCHARGE_DUE_TO_CONTINUED_ABSENCE_OR_TARDINESS("Discharge due to continued absence or tardiness"),
    @XmlEnumValue("Discharge due to a falsified application form")
    DISCHARGE_DUE_TO_A_FALSIFIED_APPLICATION_FORM("Discharge due to a falsified application form"),
    @XmlEnumValue("Discharge due to credential revoked or suspended")
    DISCHARGE_DUE_TO_CREDENTIAL_REVOKED_OR_SUSPENDED("Discharge due to credential revoked or suspended"),
    @XmlEnumValue("Discharge due to unsatisfactory work performance")
    DISCHARGE_DUE_TO_UNSATISFACTORY_WORK_PERFORMANCE("Discharge due to unsatisfactory work performance"),
    @XmlEnumValue("Death")
    DEATH("Death"),
    @XmlEnumValue("Personal reason")
    PERSONAL_REASON("Personal reason"),
    @XmlEnumValue("Lay off due to lack of funding")
    LAY_OFF_DUE_TO_LACK_OF_FUNDING("Lay off due to lack of funding"),
    @XmlEnumValue("Lost credential")
    LOST_CREDENTIAL("Lost credential"),
    @XmlEnumValue("Unknown")
    UNKNOWN("Unknown"),
    @XmlEnumValue("Other")
    OTHER("Other");
    private final String value;

    SeparationReasonType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static SeparationReasonType fromValue(String v) {
        for (SeparationReasonType c: SeparationReasonType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
