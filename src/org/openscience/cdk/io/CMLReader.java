/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2001-2004  The Chemistry Development Kit (CDK) project
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
 * Reads a molecule in CML 1.0 and 1.1 format from a Reader.
 * CML 2.0 is partly supported.
 *
 * <p>References:
 *   <a href="http://cdk.sf.net/biblio.html#PMR99">PMR99</a>,
 *   <a href="http://cdk.sf.net/biblio.html#WILLIGHAGEN2001">WILLIGHAGEN2001</a>
 *
 * @author     Egon L. Willighagen
 * @created    2001-02-01
 *
 * @keyword file format, CML
 */
public class CMLReader extends DefaultChemObjectReader {

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
    
    public CMLReader() {
        this(new StringReader(""));
    }

    public String getFormatName() {
        return "Chemical Markup Language";
    }
    /**
     * Define this CMLReader to take the input from a java.io.Reader
     * class. Possible readers are (among others) StringReader and FileReader.
     *
     * @param url String url which points to the file to be read
     */
    public CMLReader(String url) {
        this.init();
        this.url = url;
    }

    private void init() {
        logger = new org.openscience.cdk.tools.LoggingTool(this.getClass().getName());

        url = ""; // make sure it is not null

        boolean success = false;
        // If JAXP is prefered (comes with Sun JVM 1.4.0 and higher)
        if (!success) {
            try {
                javax.xml.parsers.SAXParserFactory spf = javax.xml.parsers.SAXParserFactory.newInstance();
                spf.setNamespaceAware(true);
                javax.xml.parsers.SAXParser saxParser = spf.newSAXParser();
                parser = saxParser.getXMLReader();
                logger.info("Using JAXP/SAX XML parser.");
                success = true;
            } catch (Exception e) {
                logger.warn("Could not instantiate JAXP/SAX XML reader!");
                logger.debug(e);
            }
        }
        // Aelfred is first alternative.
        if (!success) {
            try {
                parser = (XMLReader)this.getClass().getClassLoader().
                        loadClass("gnu.xml.aelfred2.XmlReader").
                        newInstance();
                logger.info("Using Aelfred2 XML parser.");
                success = true;
            } catch (Exception e) {
                logger.warn("Could not instantiate Aelfred2 XML reader!");
                logger.debug(e);
            }
        }
        // Xerces is second alternative
        if (!success) {
            try {
                parser = (XMLReader)this.getClass().getClassLoader().
                        loadClass("org.apache.xerces.parsers.SAXParser").
                        newInstance();
                logger.info("Using Xerces XML parser.");
                success = true;
            } catch (Exception e) {
                logger.warn("Could not instantiate Xerces XML reader!");
                logger.debug(e);
            }
        }
        if (!success) {
            logger.error("Could not instantiate any XML parser!");
        }
    }

    /**
     * Read a ChemObject from input
     *
     * @return the content in a ChemFile object
     */
    public ChemObject read(ChemObject object) throws CDKException {
      if (object instanceof ChemFile) {
        return (ChemObject)readChemFile();
      } else {
        throw new CDKException("Only supported is reading of ChemFile objects.");
      }
    }

    // private functions

    private ChemFile readChemFile() throws CDKException {
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
            String error = "Error while reading file: " + e.toString();
            logger.error(error);
            throw new CDKException(error);
        } catch (SAXParseException saxe) {
            SAXParseException spe = (SAXParseException)saxe;
            String error = "Found well-formedness error in line " + spe.getLineNumber();
            logger.error(error);
            throw new CDKException(error);
        } catch (SAXException saxe) {
            String error = "Error while parsing XML: " + saxe.toString();
            logger.error(error);
            throw new CDKException(error);
        }
        return cdo;
    }

    public void close() throws IOException {
        input.close();
    }
}

