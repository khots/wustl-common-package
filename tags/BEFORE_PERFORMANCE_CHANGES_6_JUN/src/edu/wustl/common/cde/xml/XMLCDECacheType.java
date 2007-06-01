//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v1.0.4-b18-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2005.08.23 at 10:44:28 GMT+05:30 
//


package edu.wustl.common.cde.xml;


/**
 * Java content class for XMLCDECacheType complex type.
 * <p>The following schema fragment specifies the expected content contained within this java content object. (defined at file:/D:/prg/XML/CDE22AUG05/CDECONFIG.xsd line 3)
 * <p>
 * <pre>
 * &lt;complexType name="XMLCDECacheType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="XMLCDE" type="{}XMLCDE" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="lazyLoading" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="refreshTime" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 */
public interface XMLCDECacheType {


    /**
     * Gets the value of the refreshTime property.
     * 
     */
    int getRefreshTime();

    /**
     * Sets the value of the refreshTime property.
     * 
     */
    void setRefreshTime(int value);

    /**
     * Gets the value of the XMLCDE property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the XMLCDE property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getXMLCDE().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link edu.wustl.common.cde.xml.XMLCDE}
     * 
     */
    java.util.List getXMLCDE();

    /**
     * Gets the value of the lazyLoading property.
     * 
     */
    boolean isLazyLoading();

    /**
     * Sets the value of the lazyLoading property.
     * 
     */
    void setLazyLoading(boolean value);

}
