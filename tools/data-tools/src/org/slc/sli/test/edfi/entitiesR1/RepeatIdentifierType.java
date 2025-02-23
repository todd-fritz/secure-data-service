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
 * <p>Java class for repeatIdentifierType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="repeatIdentifierType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *     &lt;enumeration value="Repeated, counted in grade point average"/>
 *     &lt;enumeration value="Repeated, not counted in grade point average"/>
 *     &lt;enumeration value="Not repeated"/>
 *     &lt;enumeration value="Other"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "repeatIdentifierType")
@XmlEnum
public enum RepeatIdentifierType {

    @XmlEnumValue("Repeated, counted in grade point average")
    REPEATED_COUNTED_IN_GRADE_POINT_AVERAGE("Repeated, counted in grade point average"),
    @XmlEnumValue("Repeated, not counted in grade point average")
    REPEATED_NOT_COUNTED_IN_GRADE_POINT_AVERAGE("Repeated, not counted in grade point average"),
    @XmlEnumValue("Not repeated")
    NOT_REPEATED("Not repeated"),
    @XmlEnumValue("Other")
    OTHER("Other");
    private final String value;

    RepeatIdentifierType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static RepeatIdentifierType fromValue(String v) {
        for (RepeatIdentifierType c: RepeatIdentifierType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
