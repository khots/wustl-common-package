/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v1.0.4-b18-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2005.08.23 at 10:44:28 GMT+05:30 
//

package edu.wustl.common.cde.xml.impl.runtime;

import javax.xml.bind.JAXBException;
import javax.xml.bind.ValidationEvent;

import org.xml.sax.SAXException;

import com.sun.xml.bind.unmarshaller.InterningXMLReader;

/**
 * Filter {@link SAXUnmarshallerHandler} that interns all the Strings
 * in the SAX events. 
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
final class InterningUnmarshallerHandler extends InterningXMLReader implements SAXUnmarshallerHandler {
    
    private final SAXUnmarshallerHandler core;
    
    InterningUnmarshallerHandler( SAXUnmarshallerHandler core ) {
        super();
        setContentHandler(core);
        this.core = core;
    }
    
    public void handleEvent(ValidationEvent event, boolean canRecover) throws SAXException {
        core.handleEvent(event,canRecover);
    }

    public Object getResult() throws JAXBException, IllegalStateException {
        return core.getResult();
    }

}
