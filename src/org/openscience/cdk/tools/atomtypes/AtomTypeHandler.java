/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2004  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.tools.atomtypes;

import java.util.Vector;

import org.openscience.cdk.AtomType;
import org.openscience.cdk.tools.LoggingTool;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @cdk.module standard
 */
public class AtomTypeHandler extends DefaultHandler {

    private LoggingTool logger;
    private String currentChars;
    private Vector atomTypes;

    public AtomTypeHandler() {
        logger = new LoggingTool(this);
    }

    public Vector getAtomTypes() {
        return atomTypes;
    }

    // SAX Parser methods

    public void doctypeDecl(String name, String publicId, String systemId)
        throws Exception {
        logger.info("DocType root element: " + name);
        logger.info("DocType root PUBLIC: " + publicId);
        logger.info("DocType root SYSTEM: " + systemId);
    }

    public void startDocument() {
        atomTypes = new Vector();
    }

    public void endDocument() {
    }

    public void endElement(String uri, String local, String raw) {
        logger.debug("end element: " + raw);
    }

    public void startElement(String uri, String local, 
                             String raw, Attributes atts) {
        currentChars = "";
        logger.debug("startElement: " + raw);
        logger.debug("uri: " + uri);
        logger.debug("local: " + local);
        logger.debug("raw: " + raw);
        if ("org.openscience.cdk.AtomType".equals(local)) {
            // check version
            AtomType atomType = new AtomType("R");
            for (int i = 0; i < atts.getLength(); i++) {
                try {
                    if ("symbol".equals(atts.getQName(i))) {
                        atomType.setSymbol(atts.getValue(i));
                    } else if ("id".equals(atts.getQName(i))) {
                        atomType.setAtomTypeName(atts.getValue(i));
                    } else if ("maxBondOrder".equals(atts.getQName(i))) {
                        atomType.setMaxBondOrder(Double.parseDouble(atts.getValue(i)));
                    } else if ("bondOrderSum".equals(atts.getQName(i))) {
                        atomType.setBondOrderSum(Double.parseDouble(atts.getValue(i)));
                    } else if ("formalCharge".equals(atts.getQName(i))) {
                        atomType.setFormalCharge(Integer.parseInt(atts.getValue(i)));
                    }
                } catch (NumberFormatException exception) {
                    logger.error("Value of AtomType@" + atts.getQName(i) +
                        " is not as expected.");
                    logger.debug(exception);
                }
            }
            atomTypes.addElement(atomType);
        }
    }

    public void characters(char ch[], int start, int length) {
        logger.debug("character data");
        currentChars += new String(ch, start, length);
    }

    // private methods

}
