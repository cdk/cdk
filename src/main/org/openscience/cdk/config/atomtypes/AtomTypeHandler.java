/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2007  Egon Willighagen <egonw@users.sf.net>
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

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IAtomType.Hybridization;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.tools.LoggingTool;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * SAX Handler for the AtomTypeReader.
 *
 * @see AtomTypeReader
 *
 * @cdk.module core
 * @cdk.svnrev  $Revision$
 */
@TestClass("org.openscience.cdk.config.atomtypes.AtomTypeHandlerTest")
public class AtomTypeHandler extends DefaultHandler {

    private final int SCALAR_UNSET = 0;
    private final int SCALAR_MAXBONDORDER = 1; 
    private final int SCALAR_BONDORDERSUM = 2;
    private final int SCALAR_HYBRIDIZATION = 3;
    private final int SCALAR_FORMALNEIGHBOURCOUNT = 4;
    private final int SCALAR_VALENCY = 5;
    private final int SCALAR_DA = 6;
    private final int SCALAR_SPHERICALMATCHER = 7;
    private final int SCALAR_CHEMICALGROUPCONSTANT = 8;
    private final int SCALAR_RINGSIZE = 9;
    private final int SCALAR_ISAROMATIC = 10;
    private final int SCALAR_FORMALCHARGE=11;
    private final int SCALAR_VANDERWAALSRADIUS=12;
    private final int SCALAR_PIBONDCOUNT=13;
    private final int SCALAR_LONEPAIRCOUNT=14;
    
    private LoggingTool logger;
    private String currentChars;
    private List<IAtomType> atomTypes;
    private int scalarType;
    private IAtomType atomType;

    private static IChemObjectBuilder builder;

    /**
     * Constructs a new AtomTypeHandler and will create IAtomType
     * implementations using the given IChemObjectBuilder.
     * 
     * @param build The IChemObjectBuilder used to create the IAtomType's.
     */
    public AtomTypeHandler(IChemObjectBuilder build) {
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

    /* public void doctypeDecl(String name, String publicId, String systemId) {
        logger.info("DocType root element: " + name);
        logger.info("DocType root PUBLIC: " + publicId);
        logger.info("DocType root SYSTEM: " + systemId);
    } */

    @TestMethod("testStartDocument")
    public void startDocument() {
        atomTypes = new ArrayList<IAtomType>();
        scalarType = SCALAR_UNSET;
        atomType = null;
    }

    @TestMethod("testEndElement_String_String_String")
    public void endElement(String uri, String local, String raw) {  //NOPMD
        logger.debug("END Element: ", raw);
        logger.debug("  uri: ", uri);
        logger.debug("  local: ", local);
        logger.debug("  raw: ", raw);
        logger.debug("  chars: ", currentChars.trim());
        if ("atomType".equals(local)) {
            atomTypes.add(atomType);
        } else if ("scalar".equals(local)) {
            currentChars.trim();
            try {
                if (scalarType == SCALAR_BONDORDERSUM) {
                    atomType.setBondOrderSum(Double.parseDouble(currentChars));
                } else if (scalarType == SCALAR_MAXBONDORDER) {
                	double scalarValue = Double.parseDouble(currentChars);
                	if (scalarValue == 1.0) {
                        atomType.setMaxBondOrder(IBond.Order.SINGLE);
                	} else if (scalarValue == 2.0) {
                		atomType.setMaxBondOrder(IBond.Order.DOUBLE);
                	} else if (scalarValue == 3.0) {
                		atomType.setMaxBondOrder(IBond.Order.TRIPLE);
                	} else if (scalarValue == 4.0) {
                		atomType.setMaxBondOrder(IBond.Order.QUADRUPLE);
                	} 
                } else if (scalarType == SCALAR_FORMALNEIGHBOURCOUNT) {
                    atomType.setFormalNeighbourCount(Integer.parseInt(currentChars));
                }else if (scalarType == SCALAR_VALENCY) {
                    atomType.setValency(Integer.parseInt(currentChars));
                }else if (scalarType == SCALAR_FORMALCHARGE) {
                    atomType.setFormalCharge(Integer.parseInt(currentChars)); 
                } else if (scalarType == SCALAR_HYBRIDIZATION) {
                    if ("sp1".equals(currentChars)) {
                        atomType.setHybridization(Hybridization.SP1);
                    } else if ("sp2".equals(currentChars)) {
                        atomType.setHybridization(Hybridization.SP2);
                    } else if ("sp3".equals(currentChars)) {
                        atomType.setHybridization(Hybridization.SP3);
                    } else if ("planar".equals(currentChars)) {
                        atomType.setHybridization(Hybridization.PLANAR3);
                    }else {
                    	logger.warn("Unrecognized hybridization in config file: ", currentChars);
                    }
                } else if (scalarType == SCALAR_DA){
                    if ("A".equals(currentChars)) {
                        atomType.setFlag(CDKConstants.IS_HYDROGENBOND_ACCEPTOR, true);
                    } else if ("D".equals(currentChars)){
                        atomType.setFlag(CDKConstants.IS_HYDROGENBOND_DONOR, true);
                    } else {
                    	logger.warn("Unrecognized H-bond donor/acceptor pattern in config file: ", currentChars);
                    }
                } else if (scalarType == SCALAR_SPHERICALMATCHER){	
                    atomType.setProperty(CDKConstants.SPHERICAL_MATCHER, currentChars);
                } else if (scalarType == SCALAR_RINGSIZE){	
                    atomType.setProperty(CDKConstants.PART_OF_RING_OF_SIZE, Integer.valueOf(currentChars));
                } else if (scalarType == SCALAR_CHEMICALGROUPCONSTANT){	
                    atomType.setProperty(CDKConstants.CHEMICAL_GROUP_CONSTANT, Integer.valueOf(currentChars));
                } else if (scalarType == SCALAR_ISAROMATIC){
                    atomType.setFlag(CDKConstants.ISAROMATIC, true);
                } else if (scalarType == SCALAR_PIBONDCOUNT){
                    atomType.setProperty(CDKConstants.PI_BOND_COUNT, Integer.valueOf(currentChars));
                } else if (scalarType == SCALAR_LONEPAIRCOUNT){
                    atomType.setProperty(CDKConstants.LONE_PAIR_COUNT, Integer.valueOf(currentChars));
                }
                
            } catch (Exception exception) {
                logger.error("Value (", currentChars, ") is not off the expected type: ", exception.getMessage());
                logger.debug(exception);
            }
            scalarType = SCALAR_UNSET;
        }
        currentChars = "";
    }

    @TestMethod("testStartElement_String_String_String_Attributes")
    public void startElement(String uri, String local,       //NOPMD
                             String raw, Attributes atts) {
        currentChars = "";
        logger.debug("START Element: ", raw);
        logger.debug("  uri: ", uri);
        logger.debug("  local: ", local);
        logger.debug("  raw: ", raw);
        
        if ("atomType".equals(local)) {
            atomType = builder.newAtomType("R");
            for (int i = 0; i < atts.getLength(); i++) {
                if ("id".equals(atts.getQName(i))) {
                    atomType.setAtomTypeName(atts.getValue(i));
               }
            }
        
        } else if ("atom".equals(local)) {
            for (int i = 0; i < atts.getLength(); i++) {
                if ("elementType".equals(atts.getQName(i))) {
                    atomType.setSymbol(atts.getValue(i));
                } else if ("formalCharge".equals(atts.getQName(i))) {
                    try {
                        atomType.setFormalCharge(Integer.parseInt(atts.getValue(i)));
                    } catch (NumberFormatException exception) {
                        logger.error("Value of <atom> @", atts.getQName(i), " is not an integer: ", atts.getValue(i));
                        logger.debug(exception);
                    }
                }
            }
        } else if ("label".equals(local)) {
        	// assume xpath atomType/label
        	for (int i = 0; i < atts.getLength(); i++) {
                if ("value".equals(atts.getQName(i))) {
                	if (atomType.getAtomTypeName() != null) {
                		atomType.setID(atomType.getAtomTypeName());
                	}
                	atomType.setAtomTypeName(atts.getValue(i));
                }
        	}
        } else if ("scalar".equals(local)) {
            for (int i = 0; i < atts.getLength(); i++) {
                if ("dictRef".equals(atts.getQName(i))) {
                    if ("cdk:maxBondOrder".equals(atts.getValue(i))) {
                        scalarType = SCALAR_MAXBONDORDER;
                    } else if ("cdk:bondOrderSum".equals(atts.getValue(i))) {
                        scalarType = SCALAR_BONDORDERSUM;
                    } else if ("cdk:hybridization".equals(atts.getValue(i))) {
                        scalarType = SCALAR_HYBRIDIZATION;
                    } else if ("cdk:formalNeighbourCount".equals(atts.getValue(i))) {
                        scalarType = SCALAR_FORMALNEIGHBOURCOUNT;
                    } else if ("cdk:valency".equals(atts.getValue(i))) {
                    	scalarType = SCALAR_VALENCY;
                    } else if ("cdk:formalCharge".equals(atts.getValue(i))) {
                    	scalarType = SCALAR_FORMALCHARGE;	
                    } else if ("cdk:DA".equals(atts.getValue(i))) {
                    	scalarType = SCALAR_DA;
                    } else if ("cdk:sphericalMatcher".equals(atts.getValue(i))) {
                    	scalarType = SCALAR_SPHERICALMATCHER;	
                    } else if ("cdk:ringSize".equals(atts.getValue(i))) {
                    	scalarType = SCALAR_RINGSIZE;	
                    } else if ("cdk:ringConstant".equals(atts.getValue(i))) {
                    	scalarType = SCALAR_CHEMICALGROUPCONSTANT;	
                    } else if ("cdk:aromaticAtom".equals(atts.getValue(i))) {
                    	scalarType = SCALAR_ISAROMATIC;	
                    } else if ("emboss:vdwrad".equals(atts.getValue(i))) {
                    	scalarType = SCALAR_VANDERWAALSRADIUS;
                    } else if ("cdk:piBondCount".equals(atts.getValue(i))) {
                    	scalarType = SCALAR_PIBONDCOUNT;
                    } else if ("cdk:lonePairCount".equals(atts.getValue(i))) {
                    	scalarType = SCALAR_LONEPAIRCOUNT;
                    }
                }
            }
        }
    }

    @TestMethod("testCharacters_arraychar_int_int")
    public void characters(char chars[], int start, int length) {
        logger.debug("character data");
        currentChars += new String(chars, start, length);
    }

}
