/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2004  The Chemistry Development Kit (CDK) project
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

import java.io.IOException;
import java.io.Reader;

import org.openscience.cdk.ChemObject;
import org.openscience.cdk.ChemSequence;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.chemrss.RSSHandler;
import org.openscience.cdk.tools.LoggingTool;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 * Parses a RSS feed and extracts chemical content from it. Each RSS channel
 * item is parsed into a ChemModel. The whole channel is put into a 
 * ChemSequence. Each ChemModel has properties containing RSS specific stuff.
 * See the final statics of this class.
 *
 * @cdkPackage io
 *
 * @author     Egon Willighagen
 * @created    2003-09-07
 *
 * @cdk.keyword    news feed
 * @cdk.keyword    file format, RSS
 */
public class ChemicalRSSReader extends DefaultChemObjectReader {

    /** ChemModel property containing the channel item title */
    public final static String RSS_ITEM_TITLE = "org.openscience.cdk.io.chemrss.TITLE";
    /** ChemModel property containing the channel item date */
    public final static String RSS_ITEM_DATE = "org.openscience.cdk.io.chemrss.DATE";
    /** ChemModel property containing the channel item link */
    public final static String RSS_ITEM_LINK = "org.openscience.cdk.io.chemrss.LINK";
    /** ChemModel property containing the channel item description */
    public final static String RSS_ITEM_DESCRIPTION = "org.openscience.cdk.io.chemrss.DESCRIPTION";
    
    private XMLReader parser;
    private Reader input;

    private LoggingTool logger;

    /**
     * Define this CMLReader to take the input from a java.io.Reader
     * class. Possible readers are (among others) StringReader and FileReader.
     * FIXME: this can not be used in combination with Aelfred2 yet.
     *
     * @param input Reader type input
     */
    public ChemicalRSSReader(Reader input) {
        this.init();
        this.input = input;
    }

    public String getFormatName() {
        return "CMLRSS";
    }
    
    private void init() {
        logger = new org.openscience.cdk.tools.LoggingTool(this.getClass().getName());

        boolean success = false;
        // Aelfred is prefered.
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
        // If Aelfred is not available try Xerces
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
        // If Xerces is not available try JAXP
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
        if (!success) {
            logger.error("Could not instantiate an XML parser!");
        }
    }

    /**
     * Read a ChemObject from input
     *
     * @return the content in a ChemFile object
     */
    public ChemObject read(ChemObject object) throws CDKException {
        if (object instanceof ChemSequence) {
            return parseRSSFile();
        } else {
            throw new CDKException("Only supported is reading of ChemSequence objects.");
        }
    }

    // private functions

    private ChemSequence parseRSSFile() throws CDKException {
        try {
            parser.setFeature("http://xml.org/sax/features/validation", false);
            logger.info("Deactivated validation");
        } catch (SAXException e) {
            logger.warn("Cannot deactivate validation.");
            return null;
        }
        RSSHandler handler = new RSSHandler();
        parser.setContentHandler(handler);
        try {
            parser.parse(new InputSource(input));
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
        return handler.getChemSequence();
    }

    public void close() throws IOException {
        input.close();
    }
}

