/*
 * $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 1997-2002  The Chemistry Development Kit (CDK) project
 *
 * Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
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
package org.openscience.cdk.io;

import org.openscience.cdk.exception.*;
import org.openscience.cdk.io.cml.cdopi.*;
import org.openscience.cdk.io.cml.*;
import org.openscience.cdk.*;
import org.xml.sax.helpers.*;
import org.xml.sax.*;
import java.io.*;
import java.net.*;

/**
 * Reads a molecule in CML format from a Reader.
 *
 * References:
 *   <a href="http://cdk.sf.net/biblio.html#PMR99">PMR99</a>,
 *   <a href="http://cdk.sf.net/biblio.html#WILLIGHAGEN2001">WILLIGHAGEN2001</a>
 *
 * @author     Egon L. Willighagen
 * @created    February 2001
 *
 * @keyword file format, CML
 */
public class CMLReader implements CDKConstants, ChemObjectReader {

    private XMLReader parser;
    private Reader input;
    private String url;

    private org.openscience.cdk.tools.LoggingTool logger;

    /**
     * Define this CMLReader to take the input from a java.io.Reader
     * class. Possible readers are (among others) StringReader and FileReader.
     * FIXME: this can not be used in combination with Aelfred2 yet.
     *
     * @param input Reader type input
     */
    public CMLReader(Reader input) {
        this.init();
        this.input = input;
    }

    /**
     * Define this CMLReader to take the input from a java.io.Reader
     * class. Possible readers are (among others) StringReader and FileReader.
     *
     * @param input String url which points to the file to be read
     */
    public CMLReader(String url) {
        this.init();
        this.url = url;
    }

    private void init() {
        logger = new org.openscience.cdk.tools.LoggingTool(this.getClass().getName());

        url = ""; // make sure it is not null

        boolean success = false;
        // Aelfred is prefered.
        if (!success) {
            try {
                parser = new gnu.xml.aelfred2.XmlReader();
                logger.info("Using Aelfred2 XML parser.");
                success = true;
            } catch (Exception e) {
                logger.warn("Could not instantiate Aelfred2 XML reader!");
            }
        }
        // If Aelfred is not available try Xerces
        if (!success) {
            try {
                parser = new org.apache.xerces.parsers.SAXParser();
                logger.info("Using Xerces XML parser.");
                success = true;
            } catch (Exception e) {
                logger.warn("Could not instantiate Xerces XML reader!");
            }
        }
        if (!success) {
            logger.error("Could not instantiate any XML parser!");
        }
    }

    /**
     * Read a ChemFile from input
     *
     * @return the content in a ChemFile object
     */
    public ChemObject read(ChemObject object) throws UnsupportedChemObjectException {
      if (object instanceof ChemFile) {
        return (ChemObject)readChemFile();
      } else {
        throw new UnsupportedChemObjectException(
          "Only supported is ChemFile.");
      }
    }

    // private functions

    private ChemFile readChemFile() {
        ChemFileCDO cdo = new ChemFileCDO();
        try {
            parser.setFeature("http://xml.org/sax/features/validation", false);
            logger.info("Deactivated validation");
        } catch (SAXException e) {
            logger.warn("Cannot deactivate validation.");
            return cdo;
        }
        parser.setContentHandler(new CMLHandler((CDOInterface)cdo));
        parser.setEntityResolver(new CMLResolver());
        parser.setErrorHandler(new CMLErrorHandler());
        try {
            if (input == null) {
                parser.parse(url);
            } else {
                parser.parse(new InputSource(input));
            }
        } catch (IOException e) {
            logger.warn("IOException: " + e.toString());
        } catch (SAXException saxe) {
            logger.warn("SAXException: " + saxe.getClass().getName());
            logger.warn(saxe.toString());
            // e.printStackTrace();
        }
        return cdo;
    }

}

