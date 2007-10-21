/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 1997-2007  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk.io.cml;

import org.openscience.cdk.tools.LoggingTool;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * CDK's SAX2 ErrorHandler for giving feedback on XML errors in the CML document.
 * Output is redirected to org.openscience.cdk.tools.LoggingTool.
 *
 * @cdk.module io
 * @cdk.svnrev  $Revision$
 *
 * @author Egon Willighagen <egonw@sci.kun.nl>
 **/
public class CMLErrorHandler implements ErrorHandler {

    private LoggingTool logger;

    public boolean reportErrors = true;
    public boolean abortOnErrors = false;

    /**
     * Constructor a SAX2 ErrorHandler that uses the cdk.tools.LoggingTool
     * class to output errors and warnings to.
     **/
    public CMLErrorHandler() {
        logger = new LoggingTool(this);
        logger.info("instantiated");
    }

    /**
     * Internal procedure that outputs an SAXParseException with a significance level
     * to the cdk.tools.LoggingTool logger.
     *
     * @param level     significance level
     * @param exception Exception to output
     */
    private void print (String level, SAXParseException exception) {
        if (level.equals("warning")) {
            logger.warn("** " + level + ": " + exception.getMessage ());
            logger.warn("   URI  = " + exception.getSystemId ());
            logger.warn("   line = " + exception.getLineNumber ());
        } else {
            logger.error("** " + level + ": " + exception.getMessage ());
            logger.error("   URI  = " + exception.getSystemId ());
            logger.error("   line = " + exception.getLineNumber ());
        }
    }

    // for recoverable errors, like validity problems

    /**
     * Outputs a SAXParseException error to the logger.
     *
     * @param exception   Exception to output
     **/
    public void error (SAXParseException exception) throws SAXException {
        if (reportErrors) print("error", exception);
        if (abortOnErrors) throw exception;
    }

    /**
     * Outputs as fatal SAXParseException error to the logger.
     *
     * @param exception   Exception to output
     **/
    public void fatalError (SAXParseException exception) throws SAXException {
        if (reportErrors) print("fatal", exception);
        if (abortOnErrors) throw exception;
    }

    /**
     * Outputs a SAXParseException warning to the logger.
     *
     * @param exception   Exception to output
     **/
    public void warning (SAXParseException exception) throws SAXException {
        if (reportErrors) print("warning", exception);
    }

}

