/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2002-2003  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.tools.atomtypes;

import org.openscience.cdk.exception.*;
import org.openscience.cdk.*;
import org.openscience.cdk.tools.LoggingTool;
import org.xml.sax.helpers.*;
import org.xml.sax.*;
import java.io.*;
import java.util.Vector;

public class AtomTypeReader {

    private XMLReader parser;
    private Reader input;
    private LoggingTool logger;

    public AtomTypeReader(Reader input) {
        this.init();
        this.input = input;
    }

    private void init() {
        logger = new LoggingTool(this.getClass().getName());
        try {
            parser = new gnu.xml.aelfred2.XmlReader();
            logger.info("Using Aelfred2 XML parser.");
        } catch (Exception e) {
            logger.error("Could not instantiate Aelfred2 XML reader!");
        }
    }

    public Vector readAtomTypes() {
        Vector isotopes = null;
        try {
            parser.setFeature("http://xml.org/sax/features/validation", false);
            logger.info("Deactivated validation");
        } catch (SAXException e) {
            logger.warn("Cannot deactivate validation.");
        }
        AtomTypeHandler handler = new AtomTypeHandler();
        parser.setContentHandler(handler);
        try {
            parser.parse(new InputSource(input));
            isotopes = handler.getAtomTypes();
        } catch (IOException e) {
            logger.error("IOException: " + e.toString());
        } catch (SAXException saxe) {
            logger.error("SAXException: " + saxe.getClass().getName());
            logger.error(saxe.toString());
        }
        return isotopes;
    }

}

