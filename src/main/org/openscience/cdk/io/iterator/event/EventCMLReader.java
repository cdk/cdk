/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2001-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.io.iterator.event;

import java.io.IOException;
import java.io.Reader;

import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.io.cml.CMLErrorHandler;
import org.openscience.cdk.io.cml.CMLResolver;
import org.openscience.cdk.io.formats.CMLFormat;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.listener.IReaderListener;
import org.openscience.cdk.tools.LoggingTool;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 * Reads a molecule in CML 1.x and 2.0 format.
 * CML is an XML based application {@cdk.cite PMR99}, and this Reader
 * applies the method described in {@cdk.cite WIL01}.
 *
 * @cdk.module io
 * @cdk.svnrev  $Revision$
 *
 * @author       Egon L. Willighagen
 * @cdk.created  2001-02-01
 *
 * @cdk.keyword file format, CML
 */
public class EventCMLReader extends DefaultEventChemObjectReader {

    private XMLReader parser;
    private Reader input;
    private IChemObjectBuilder builder;    
    private EventCMLHandler cdo;

    private LoggingTool logger;

    /**
     * Define this CMLReader to take the input from a java.io.Reader
     * class. Possible readers are (among others) StringReader and FileReader.
     * The given ReaderListener catches the events thrown to signal that a
     * a new molecule is read.
     *
     * @param input    Reader type input
     * @param listener ReaderListener that listens to newMolecule events. 
     */
    public EventCMLReader(Reader input, IReaderListener listener,
    		              IChemObjectBuilder builder) {
        this.init();
        this.input = input;
        this.cdo = new EventCMLHandler(this, builder);
        this.builder = builder;
        this.addChemObjectIOListener(listener);
    }
    
    @TestMethod("testGetFormat")
    public IResourceFormat getFormat() {
        return CMLFormat.getInstance();
    }

    public IAtomContainer getAtomContainer() {
        return cdo.getAtomContainer();
    }
    
    @TestMethod("testSetReader_Reader")
    public void setReader(Reader reader) throws CDKException {
        this.input = reader;
    }

    private void init() {
        logger = new LoggingTool(this);

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
                logger.warn("Could not instantiate JAXP/SAX XML reader: ", e.getMessage());
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
     * Starts the reading of the CML file. Whenever a new Molecule is read,
     * a event is thrown to the ReaderListener.
     */
    public void process() throws CDKException {
        logger.debug("Started parsing from input...");
        try {
            parser.setFeature("http://xml.org/sax/features/validation", false);
            logger.info("Deactivated validation");
        } catch (SAXException e) {
            logger.warn("Cannot deactivate validation.");
        }
        parser.setContentHandler(new EventCMLHandler(this, builder));
        parser.setEntityResolver(new CMLResolver());
        parser.setErrorHandler(new CMLErrorHandler());
        try {
            logger.debug("Parsing from Reader");
            parser.parse(new InputSource(input));
        } catch (IOException e) {
            String error = "Error while reading file: " + e.getMessage();
            logger.error(error);
            logger.debug(e);
            throw new CDKException(error, e);
        } catch (SAXParseException saxe) {
            SAXParseException spe = (SAXParseException)saxe;
            String error = "Found well-formedness error in line " + spe.getLineNumber();
            logger.error(error);
            logger.debug(saxe);
            throw new CDKException(error, saxe);
        } catch (SAXException saxe) {
            String error = "Error while parsing XML: " + saxe.getMessage();
            logger.error(error);
            logger.debug(saxe);
            throw new CDKException(error, saxe);
        }
    }

    @TestMethod("testClose")
    public void close() throws IOException {
        input.close();
    }

}

