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

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.tools.LoggingTool;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * SAX Handler for the {@link OWLAtomTypeReader}.
 *
 * @cdk.module  core
 * @cdk.svnrev  $Revision$
 */
@TestClass("org.openscience.cdk.config.atomtypes.OWLAtomTypeHandlerTest")
public class OWLAtomTypeHandler extends DefaultHandler {

	private final String NS_ATOMTYPE = "http://cdk.sf.net/ontologies/atomtypes#";
	
    private LoggingTool logger;
    private String currentChars;
    private List<IAtomType> atomTypes;
    private IAtomType currentAtomType;

    private static IChemObjectBuilder builder;

    /**
     * Constructs a new AtomTypeHandler and will create IAtomType
     * implementations using the given IChemObjectBuilder.
     * 
     * @param build The IChemObjectBuilder used to create the IAtomType's.
     */
    public OWLAtomTypeHandler(IChemObjectBuilder build) {
        logger = new LoggingTool(this);
        builder = build;
    }

    /**
     * Returns a List with read IAtomType's.
     * 
     * @return The read IAtomType's.
     */
    @TestMethod("testGetAtomTypes")
    public List<IAtomType> getAtomTypes() {
        return atomTypes;
    }

    // SAX Parser methods

    @TestMethod("testStartDocument")
    public void startDocument() {
        atomTypes = new ArrayList<IAtomType>();
        currentAtomType = null;
    }

    @TestMethod("testedByOtherClass")
    public void endElement(String uri, String local, String raw) {
        if (NS_ATOMTYPE.equals(uri)) {
        	endAtomTypeElement(local);
        } // ignore other namespaces
        currentChars = "";
    }

	private void endAtomTypeElement(String local) {
    	if ("AtomType".equals(local)) {
    		atomTypes.add(currentAtomType);
    	} else if ("formalCharge".equals(local)) {
    		if (currentChars.charAt(0) == '+') {
    			currentChars = currentChars.substring(1);
    		}
    		currentAtomType.setFormalCharge(Integer.parseInt(currentChars));
    	} else if ("formalNeighbourCount".equals(local)) {
    		currentAtomType.setFormalNeighbourCount(Integer.parseInt(currentChars));
    	} else if ("lonePairCount".equals(local)) {
    		currentAtomType.setProperty(CDKConstants.LONE_PAIR_COUNT, Integer.parseInt(currentChars));
    	} else if ("piBondCount".equals(local)) {
    		currentAtomType.setProperty(CDKConstants.PI_BOND_COUNT, Integer.parseInt(currentChars));
    	}
	}

	@TestMethod("testedByOtherClass")
    public void startElement(String uri, String local,
                             String raw, Attributes atts) {
        currentChars = "";        
        if (NS_ATOMTYPE.equals(uri)) {
        	startAtomTypeElement(local, atts);
        } // ignore other namespaces
    }

	private void startAtomTypeElement(String local, Attributes atts) {
    	if ("AtomType".equals(local)) {
    		currentAtomType = builder.newAtomType("H");
    		currentAtomType.setAtomTypeName(atts.getValue("rdf:ID"));
    	} else if ("hasElement".equals(local)) {
    		String attrValue = atts.getValue("rdf:resource");
    		currentAtomType.setSymbol(attrValue.substring(attrValue.indexOf("#")+1));
    	} else if ("hybridization".equals(local)) {
    		String attrValue = atts.getValue("rdf:resource");
    		String hybridization = attrValue.substring(attrValue.indexOf("#")+1);
    		if ("sp3".equals(hybridization)) {
    			currentAtomType.setHybridization(IAtomType.Hybridization.SP3);
    		} else if ("sp2".equals(hybridization)) {
    			currentAtomType.setHybridization(IAtomType.Hybridization.SP2);
    		} else if ("sp1".equals(hybridization)) {
    			currentAtomType.setHybridization(IAtomType.Hybridization.SP1);
        } else if ("s".equals(hybridization)) {
            currentAtomType.setHybridization(IAtomType.Hybridization.S);
    		} else if ("planar".equals(hybridization)) {
    			currentAtomType.setHybridization(IAtomType.Hybridization.PLANAR3);
    		}
    	}
	}

    @TestMethod("testedByOtherClass")
    public void characters(char chars[], int start, int length) {
        logger.debug("character data");
        currentChars += new String(chars, start, length);
    }

}
