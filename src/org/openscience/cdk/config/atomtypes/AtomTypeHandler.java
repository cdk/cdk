/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2005  The Chemistry Development Kit (CDK) project
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
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 *  02111-1307  USA.
 */
package org.openscience.cdk.config.atomtypes;

import java.util.Vector;

import org.openscience.cdk.AtomType;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.tools.LoggingTool;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * SAX Handler for the AtomTypeReader.
 *
 * @see AtomTypeReader
 *
 * @cdk.module core
 */
public class AtomTypeHandler extends DefaultHandler { //NOPMD

    private final int SCALAR_UNSET = 0;
    private final int SCALAR_MAXBONDORDER = 1; 
    private final int SCALAR_BONDORDERSUM = 2;
    private final int SCALAR_HYBRIDIZATION = 3;
    private final int SCALAR_FORMALNEIGHBOURCOUNT = 4;
    private final int SCALAR_VALENCY = 5;
    
    private LoggingTool logger;
    private String currentChars;
    private Vector atomTypes;
    private int scalarType;
    private AtomType atomType;

    public AtomTypeHandler() {
        logger = new LoggingTool(this);
    }

    public Vector getAtomTypes() {
        return atomTypes;
    }

    // SAX Parser methods

    /* public void doctypeDecl(String name, String publicId, String systemId) {
        logger.info("DocType root element: " + name);
        logger.info("DocType root PUBLIC: " + publicId);
        logger.info("DocType root SYSTEM: " + systemId);
    } */

    public void startDocument() {
        atomTypes = new Vector();
        scalarType = SCALAR_UNSET;
        atomType = null;
    }

    public void endElement(String uri, String local, String raw) {  //NOPMD
        logger.debug("END Element: ", raw);
        logger.debug("  uri: ", uri);
        logger.debug("  local: ", local);
        logger.debug("  raw: ", raw);
        logger.debug("  chars: ", currentChars.trim());
        if ("atomType".equals(local)) {
            atomTypes.addElement(atomType);
        } else if ("scalar".equals(local)) {
            currentChars.trim();
            try {
                if (scalarType == SCALAR_BONDORDERSUM) {
                    atomType.setBondOrderSum(Double.parseDouble(currentChars));
                } else if (scalarType == SCALAR_MAXBONDORDER) {
                    atomType.setMaxBondOrder(Double.parseDouble(currentChars));
                } else if (scalarType == SCALAR_FORMALNEIGHBOURCOUNT) {
                    atomType.setFormalNeighbourCount(Integer.parseInt(currentChars));
		} else if (scalarType == SCALAR_VALENCY) {
                    atomType.setValency(Integer.parseInt(currentChars));
                } else if (scalarType == SCALAR_HYBRIDIZATION) {
                    if ("sp1".equals(currentChars)) {
                        atomType.setHybridization(CDKConstants.HYBRIDIZATION_SP1);
                    } else if ("sp2".equals(currentChars)) {
                        atomType.setHybridization(CDKConstants.HYBRIDIZATION_SP2);
                    } else if ("sp3".equals(currentChars)) {
                        atomType.setHybridization(CDKConstants.HYBRIDIZATION_SP3);
                    }
                }
            } catch (Exception exception) {
                logger.error("Value (", currentChars, ") is not off the expected type: ", exception.getMessage());
                logger.debug(exception);
            }
            scalarType = SCALAR_UNSET;
        }
        currentChars = "";
    }

    public void startElement(String uri, String local,       //NOPMD
                             String raw, Attributes atts) {
        currentChars = "";
        logger.debug("START Element: ", raw);
        logger.debug("  uri: ", uri);
        logger.debug("  local: ", local);
        logger.debug("  raw: ", raw);
        if ("atomType".equals(local)) {
            atomType = new AtomType("R");
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
                    }
                }
            }
        }
    }

    public void characters(char chars[], int start, int length) {
        logger.debug("character data");
        currentChars += new String(chars, start, length);
    }

}
