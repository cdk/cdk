/*
 * $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 1997-2002  The Chemistry Development Kit (CDK) project
 *
 * Contact: egonw@sci.kun.nl
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.openscience.cdk.io.cml;

import org.xml.sax.helpers.*;
import org.xml.sax.*;

public class CMLErrorHandler implements ErrorHandler {

    private org.openscience.cdk.tools.LoggingTool logger;

    public boolean reportErrors = true;
    public boolean abortOnErrors = false;

    public CMLErrorHandler() {
        logger = new org.openscience.cdk.tools.LoggingTool(this.getClass().getName());
        logger.info("instantiated");
    }

    private void print (String label, SAXParseException e) {
        logger.error("** " + label + ": " + e.getMessage ());
        logger.error("   URI  = " + e.getSystemId ());
        logger.error("   line = " + e.getLineNumber ());
    }

    // for recoverable errors, like validity problems

    public void error (SAXParseException e) throws SAXException {
        if (reportErrors) print("error", e);
        if (abortOnErrors) throw e;
    }

    public void fatalError (SAXParseException e) throws SAXException {
        if (reportErrors) print("fatal", e);
        if (abortOnErrors) throw e;
    }

    public void warning (SAXParseException e) throws SAXException {
        if (reportErrors) print("warning", e);
    }

}

