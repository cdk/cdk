/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2002-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.io.formats.INChIFormat;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.inchi.INChIHandler;
import org.openscience.cdk.tools.LoggingTool;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Reads the content of a IUPAC/NIST Chemical Identifier (INChI) document. See
 * {@cdk.cite HEL01}. Recently a new INChI format was introduced an files generated
 * with the latest INChI generator cannot be parsed with this class. This class
 * needs to be updated.
 *
 * <P>The elements that are read are given in the INChIHandler class.
 *
 * @cdk.module extra
 * @cdk.svnrev  $Revision$
 *
 * @author      Egon Willighagen <egonw@sci.kun.nl>
 * @cdk.created 2004-05-17
 *
 * @cdk.keyword file format, INChI
 * @cdk.keyword chemical identifier
 * @cdk.require java1.4+
 *
 * @see     org.openscience.cdk.io.inchi.INChIHandler
 */
@TestClass("org.openscience.cdk.io.INChIReaderTest")
public class INChIReader extends DefaultChemObjectReader {

    private XMLReader parser;
    private InputStream input;

    private LoggingTool logger;

    /**
     * Construct a INChI reader from a InputStream object.
     *
     * @param input the InputStream with the content
     */
    public INChIReader(InputStream input) {
        this.input = input;
        init();
    }
    
    public INChIReader() {
        this(new ByteArrayInputStream(new byte[0]));
    }
    
    @TestMethod("testGetFormat")
    public IResourceFormat getFormat() {
        return INChIFormat.getInstance();
    }
    
    /**
     * This method must not be used; XML reading requires the use of an InputStream.
     * Use setReader(InputStream) instead.
     */
    @TestMethod("testSetReader_Reader")
    public void setReader(Reader reader) throws CDKException {
        throw new CDKException("Invalid method call; use SetReader(InputStream) instead.");
    }

    @TestMethod("testSetReader_InputStream")
    public void setReader(InputStream input) throws CDKException {
        this.input = input;
    }

    /**
     * Initializes this reader.
     */
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

	@TestMethod("testAccepts")
    public boolean accepts(Class classObject) {
		Class[] interfaces = classObject.getInterfaces();
		for (int i=0; i<interfaces.length; i++) {
			if (IChemFile.class.equals(interfaces[i])) return true;
		}
    Class superClass = classObject.getSuperclass();
    if (superClass != null) return this.accepts(superClass);
		return false;
	}

    /**
     * Reads a IChemObject of type object from input.
     * Supported types are: ChemFile.
     *
     * @param  object type of requested IChemObject
     * @return the content in a ChemFile object
     */
    public IChemObject read(IChemObject object) throws CDKException {
      if (object instanceof IChemFile) {
        return (IChemObject)readChemFile();
      } else {
        throw new CDKException("Only supported is reading of ChemFile objects.");
      }
    }

    // private functions

    /**
     * Reads a ChemFile object from input.
     *
     * @return ChemFile with the content read from the input
     */
    private IChemFile readChemFile() {
        IChemFile cf = null;
        try {
            parser.setFeature("http://xml.org/sax/features/validation", false);
            logger.info("Deactivated validation");
        } catch (SAXException e) {
            logger.warn("Cannot deactivate validation.");
        }
        INChIHandler handler = new INChIHandler();
        parser.setContentHandler(handler);
        try {
            parser.parse(new InputSource(input));
            cf = handler.getChemFile();
        } catch (IOException e) {
            logger.error("IOException: ", e.getMessage());
            logger.debug(e);
        } catch (SAXException saxe) {
            logger.error("SAXException: ", saxe.getClass().getName());
            logger.debug(saxe);
        }
        return cf;
    }

    @TestMethod("testClose")
    public void close() throws IOException {
        input.close();
    }
}

