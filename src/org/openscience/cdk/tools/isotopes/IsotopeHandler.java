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
package org.openscience.cdk.tools.isotopes;

import java.util.Vector;

import org.openscience.cdk.Isotope;
import org.openscience.cdk.tools.LoggingTool;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @cdkPackage standard
 */
public class IsotopeHandler extends DefaultHandler {

    private LoggingTool logger;
    private String currentChars;
    private Vector isotopes;
    private boolean debug = false;

    public IsotopeHandler() {
        logger = new LoggingTool(this);
    }

    public Vector getIsotopes() {
        return isotopes;
    }

    // SAX Parser methods

    public void doctypeDecl(String name, String publicId, String systemId)
        throws Exception {
        logger.info("DocType root element: " + name);
        logger.info("DocType root PUBLIC: " + publicId);
        logger.info("DocType root SYSTEM: " + systemId);
    }

    public void startDocument() {
        isotopes = new Vector();
    }

    public void endDocument() {
    }

    public void endElement(String uri, String local, String raw) {
        if (debug) logger.debug("end element: " + raw);
    }

    public void startElement(String uri, String local, 
                             String raw, Attributes atts) {
        currentChars = "";
        if (debug) logger.debug("startElement: " + raw);
        if (debug) logger.debug("uri: " + uri);
        if (debug) logger.debug("local: " + local);
        if (debug) logger.debug("raw: " + raw);
        if ("org.openscience.cdk.Isotope".equals(local)) {
            // check version
            Isotope isotope = new Isotope("R");
            for (int i = 0; i < atts.getLength(); i++) {
                try {
                    if ("symbol".equals(atts.getQName(i))) {
                        isotope.setSymbol(atts.getValue(i));
                    } else if ("atomicNumber".equals(atts.getQName(i))) {
                        isotope.setAtomicNumber(Integer.parseInt(atts.getValue(i)));
                    } else if ("massNumber".equals(atts.getQName(i))) {
                        isotope.setMassNumber(Integer.parseInt(atts.getValue(i)));
                    } else if ("exactMass".equals(atts.getQName(i))) {
                        isotope.setExactMass(Double.parseDouble(atts.getValue(i)));
                    } else if ("naturalAbundance".equals(atts.getQName(i))) {
                        isotope.setNaturalAbundance(Double.parseDouble(atts.getValue(i)));
                    }
                } catch (NumberFormatException exception) {
                    logger.error("Value of Isotope@" + atts.getQName(i) +
                        " is not as expected.");
                    logger.debug(exception);
                }
            }
            isotopes.addElement(isotope);
        }
    }

    public void characters(char ch[], int start, int length) {
        if (debug) logger.debug("character data");
        currentChars += new String(ch, start, length);
    }

    // private methods

}
