//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.3 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.01.04 at 05:29:59 PM EST 
//


package org.slc.sli.config;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.slc.sli.config package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.slc.sli.config
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ViewConfigSet }
     * 
     */
    public ViewConfigSet createViewConfigSet() {
        return new ViewConfigSet();
    }

    /**
     * Create an instance of {@link ViewConfig }
     * 
     */
    public ViewConfig createViewConfig() {
        return new ViewConfig();
    }

    /**
     * Create an instance of {@link Field }
     * 
     */
    public Field createField() {
        return new Field();
    }

    /**
     * Create an instance of {@link DisplaySet }
     * 
     */
    public DisplaySet createDisplaySet() {
        return new DisplaySet();
    }

}
