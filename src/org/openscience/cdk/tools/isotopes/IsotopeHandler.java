/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2002  The Chemistry Development Kit (CDK) project
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

import java.io.PrintStream;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

public class IsotopeHandler extends DefaultHandler {

    private LoggingTool logger;
    private Isotope isotope;
    private String currentChars;
    private Vector isotopes;

    public IsotopeHandler() {
        logger = new LoggingTool(this.getClass().getName());
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
        logger.debug("end element: " + raw);
    }

    public void startElement(String uri, String local, 
                             String raw, Attributes atts) {
        currentChars = "";
        logger.debug("startElement: " + raw);
        logger.debug("uri: " + uri);
        logger.debug("local: " + local);
        logger.debug("raw: " + raw);
        if ("IChI".equals(local)) {
            // check version
            for (int i = 0; i < atts.getLength(); i++) {
                if (atts.getQName(i).equals("version"))
                    logger.info("IChI version: " + atts.getValue(i));
            }
        }
    }

    public void characters(char ch[], int start, int length) {
        logger.debug("character data");
        currentChars += new String(ch, start, length);
    }

    // private methods

}
