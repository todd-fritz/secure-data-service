//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.08.31 at 10:43:34 AM EDT 
//


package org.slc.sli.test.edfi.entitiesR1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for personalInformationVerificationType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="personalInformationVerificationType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *     &lt;enumeration value="Baptismal or church certificate"/>
 *     &lt;enumeration value="Birth certificate"/>
 *     &lt;enumeration value="Drivers license"/>
 *     &lt;enumeration value="Entry in family Bible"/>
 *     &lt;enumeration value="Hospital certificate"/>
 *     &lt;enumeration value="Immigration document/visa"/>
 *     &lt;enumeration value="Life insurance policy"/>
 *     &lt;enumeration value="Other"/>
 *     &lt;enumeration value="Other non-official document"/>
 *     &lt;enumeration value="Other official document"/>
 *     &lt;enumeration value="Parents affidavit"/>
 *     &lt;enumeration value="Passport"/>
 *     &lt;enumeration value="Physicians certificate"/>
 *     &lt;enumeration value="Previously verified school records"/>
 *     &lt;enumeration value="State-issued ID"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "personalInformationVerificationType")
@XmlEnum
public enum PersonalInformationVerificationType {

    @XmlEnumValue("Baptismal or church certificate")
    BAPTISMAL_OR_CHURCH_CERTIFICATE("Baptismal or church certificate"),
    @XmlEnumValue("Birth certificate")
    BIRTH_CERTIFICATE("Birth certificate"),
    @XmlEnumValue("Drivers license")
    DRIVERS_LICENSE("Drivers license"),
    @XmlEnumValue("Entry in family Bible")
    ENTRY_IN_FAMILY_BIBLE("Entry in family Bible"),
    @XmlEnumValue("Hospital certificate")
    HOSPITAL_CERTIFICATE("Hospital certificate"),
    @XmlEnumValue("Immigration document/visa")
    IMMIGRATION_DOCUMENT_VISA("Immigration document/visa"),
    @XmlEnumValue("Life insurance policy")
    LIFE_INSURANCE_POLICY("Life insurance policy"),
    @XmlEnumValue("Other")
    OTHER("Other"),
    @XmlEnumValue("Other non-official document")
    OTHER_NON_OFFICIAL_DOCUMENT("Other non-official document"),
    @XmlEnumValue("Other official document")
    OTHER_OFFICIAL_DOCUMENT("Other official document"),
    @XmlEnumValue("Parents affidavit")
    PARENTS_AFFIDAVIT("Parents affidavit"),
    @XmlEnumValue("Passport")
    PASSPORT("Passport"),
    @XmlEnumValue("Physicians certificate")
    PHYSICIANS_CERTIFICATE("Physicians certificate"),
    @XmlEnumValue("Previously verified school records")
    PREVIOUSLY_VERIFIED_SCHOOL_RECORDS("Previously verified school records"),
    @XmlEnumValue("State-issued ID")
    STATE_ISSUED_ID("State-issued ID");
    private final String value;

    PersonalInformationVerificationType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PersonalInformationVerificationType fromValue(String v) {
        for (PersonalInformationVerificationType c: PersonalInformationVerificationType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
