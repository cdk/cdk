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

package org.openscience.cdk.io.ichi;

import java.io.PrintStream;
import java.util.Hashtable;
import java.util.Vector;
import org.openscience.cdk.io.cml.cdopi.CDOInterface;
import org.openscience.cdk.tools.LoggingTool;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

/**
 * SAX2 implementation for IChi XML fragment parsing.
 * Only the framework is implemented at this moment: it does
 * not really work yet.
 *
 * @see org.openscience.cdk.io.IChIReader
 */
public class IChIHandler extends DefaultHandler {

    private LoggingTool logger;

    /**
     * Constructor for the CMLHandler.
     *
     * @param cdo The Chemical Document Object in which data is stored
     **/
    public IChIHandler() {
        logger = new LoggingTool(this.getClass().getName());
    }

    public void doctypeDecl(String name, String publicId, String systemId)
        throws Exception {
        logger.info("DocType root element: " + name);
        logger.info("DocType root PUBLIC: " + publicId);
        logger.info("DocType root SYSTEM: " + systemId);
    }

    /**
     * Implementation of the endDocument() procedure overwriting the
     * DefaultHandler interface.
     */
    public void startDocument() {
    }

    /**
     * Implementation of the endDocument() procedure overwriting the
     * DefaultHandler interface.
     */
    public void endDocument() {
    }

    /**
     * Implementation of the endElement() procedure overwriting the
     * DefaultHandler interface.
     *
     * @param uri       the Universal Resource Identifier
     * @param local     the local name (without namespace part)
     * @param raw       the complete element name (with namespace part)
     */
    public void endElement(String uri, String local, String raw) {
        logger.debug("end element: " + raw);
    }

    /**
     * Implementation of the startElement() procedure overwriting the
     * DefaultHandler interface.
     *
     * @param uri       the Universal Resource Identifier
     * @param local     the local name (without namespace part)
     * @param raw       the complete element name (with namespace part)
     * @param atts      the attributes of this element
     */
    public void startElement(String uri, String local, String raw, Attributes atts) {
        logger.debug("startElement: " + raw);
        logger.debug("uri: " + uri);
        logger.debug("local: " + local);
        logger.debug("raw: " + raw);
    }

    /**
     * Implementation of the characters() procedure overwriting the
     * DefaultHandler interface.
     *
     * @param ch        characters to handle
     */
    public void characters(char ch[], int start, int length) {
       logger.debug("character data");
    }

}
