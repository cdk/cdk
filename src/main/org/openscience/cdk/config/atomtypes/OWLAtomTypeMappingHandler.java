/* $Revision$ $Author$ $Date$
 * 
 * Copyright (C) 2003-2008  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sf.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA. 
 */
package org.openscience.cdk.config.atomtypes;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Hashtable;
import java.util.Map;

/**
 * SAX Handler for the {@link OWLAtomTypeMappingReader}.
 *
 * @cdk.module  atomtype
 * @cdk.svnrev  $Revision$
 */
@TestClass("org.openscience.cdk.config.atomtypes.OWLAtomTypeMappingHandlerTest")
public class OWLAtomTypeMappingHandler extends DefaultHandler {

	private final String NS_ATOMTYPE_MAPPING = "http://cdk.sf.net/ontologies/atomtypemappings#";
	private final String NS_OWL = "http://www.w3.org/2002/07/owl#";
	
    private Map<String,String> atomTypeMappings;
    
    private String fromType;
    private String toType;

    /**
     * Constructs a new OWLAtomTypeMappingHandler.
     */
    public OWLAtomTypeMappingHandler() {}

    /**
     * Returns a {@link Map} with atom type mappings.
     */
    @TestMethod("testGetAtomTypeMappings")
    public Map<String,String> getAtomTypeMappings() {
        return atomTypeMappings;
    }

    // SAX Parser methods

    @TestMethod("testStartDocument")
    public void startDocument() {
    	atomTypeMappings = new Hashtable<String,String>();
    }

    @TestMethod("testEndElement_String_String_String")
    public void endElement(String uri, String local, String raw) {
        if (NS_OWL.equals(uri)) {
        	endAtomTypeElement(local);
        } // ignore other namespaces
    }

	private void endAtomTypeElement(String local) {
    	if ("Thing".equals(local) && toType != null && fromType != null) {
    		atomTypeMappings.put(fromType, toType);
    	}
	}

	@TestMethod("testStartElement_String_String_String_Attributes")
    public void startElement(String uri, String local,
                             String raw, Attributes atts) {
        if (NS_OWL.equals(uri)) {
        	startOWLElement(local, atts);
        } else if (NS_ATOMTYPE_MAPPING.equals(uri)) {
        	startAtomTypeMappingElement(local, atts);
        } // ignore other namespaces
    }

	private void startOWLElement(String local, Attributes atts) {
    	if ("Thing".equals(local)) {
    		toType = null;
    		fromType = atts.getValue("rdf:about");
    		fromType = fromType.substring(fromType.indexOf('#')+1);
    	}
	}

	private void startAtomTypeMappingElement(String local, Attributes atts) {
    	if ("mapsToType".equals(local) || "equivalentAsType".equals(local)) {
    		toType = atts.getValue("rdf:resource");
    		toType = toType.substring(toType.indexOf('#')+1);
    	}
	}

    @TestMethod("testCharacters_arraychar_int_int")
    public void characters(char chars[], int start, int length) {}

}
