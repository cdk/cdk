/*
 * $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 1997-2000  The CompChem project
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
 *  */
package org.openscience.cdk.io;

import org.openscience.cdk.*;
import org.openscience.cdk.exception.*;
import org.openscience.cdk.io.cml.*;
import org.openscience.cdk.io.cml.cdopi.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import java.io.*;

/**
 * Reads a molecule in CML format from a Reader
 *
 * @author     Egon L. Willighagen
 * @created    February 2001
 */
public class CMLReader implements CDKConstants, ChemObjectReader {

    private XMLReader parser;
    private ContentHandler handler;
    private EntityResolver resolver;
    private Reader input;

    private org.openscience.cdk.tools.LoggingTool logger;

    /**
     * Define this CMLReader to take the input from a java.io.Reader
     * class. Possible readers are (among others) StringReader and FileReader.
     *
     * @param input Reader type input
     */
    public CMLReader(Reader input) {
        logger = new org.openscience.cdk.tools.LoggingTool(this.getClass().getName());

		boolean success = false;
		if (!success) {
          try {
            parser = new org.apache.xerces.parsers.SAXParser();
		    logger.info("Using Xerces XML parser.");
		    success = true;
          } catch (Exception e) {
            logger.warn("Could not instantiate Xerces XML reader!");
          }
		}
		// Xerces is prefered. Aelfred2 seems to ignore the entity handler. Removal of the
		// DocType line will make Aelfred2 work properly.
		if (!success) {
          try {
		    parser = new gnu.xml.aelfred2.XmlReader();
		    logger.info("Using Aelfred2 XML parser.");
		    success = true;
          } catch (Exception e) {
            logger.warn("Could not instantiate Aelfred2 XML reader!");
          }
		}
		if (!success) {
		  logger.error("Could not instantiate any XML parser!");
		}
        this.input = input;
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

    private ChemFile readChemFile()
	{
		ChemFileCDO cdo = new ChemFileCDO();
		handler = new CMLHandler((CDOInterface)cdo);
		try {
		    parser.setFeature("http://xml.org/sax/features/validation", false);
		} catch (SAXException e) {
		    logger.warn("Cannot activate validation.");
		    return cdo;
		}
		resolver = new CMLResolver();
		parser.setContentHandler(handler);
		parser.setEntityResolver(resolver);
		try {
		    parser.parse(new InputSource(input));
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

