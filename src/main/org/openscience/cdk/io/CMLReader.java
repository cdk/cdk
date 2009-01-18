/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2001-2007  Egon Willighagen <egonw@users.sf.net>
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
import java.util.HashMap;
import java.util.Map;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.io.cml.CMLErrorHandler;
import org.openscience.cdk.io.cml.CMLHandler;
import org.openscience.cdk.io.cml.CMLResolver;
import org.openscience.cdk.io.cml.ICMLModule;
import org.openscience.cdk.io.formats.CMLFormat;
import org.openscience.cdk.io.formats.IResourceFormat;
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
 * @author      Egon L. Willighagen
 * @cdk.created 2001-02-01
 * @cdk.module  io
 * @cdk.svnrev  $Revision$
 * @cdk.keyword file format, CML
 * @cdk.bug     1544406
 */
@TestClass("org.openscience.cdk.io.CMLReaderTest")
public class CMLReader extends DefaultChemObjectReader {

    private XMLReader parser;
    private InputStream input;
    private String url;
    
    private Map<String,ICMLModule> userConventions = new HashMap<String,ICMLModule>();

    private LoggingTool logger;

    /**
     * Reads CML from an java.io.InputStream, for example the FileInputStream.
     *
     * @param input InputStream type input
     */
    public CMLReader(InputStream input) {
        this.input = input;
        init();
    }
    
    public CMLReader() {
        this(new ByteArrayInputStream(new byte[0]));
    }
    
    public void registerConvention(String convention, ICMLModule conv) {
    	userConventions.put(convention, conv);
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

    @TestMethod("testGetFormat")
    public IResourceFormat getFormat() {
        return CMLFormat.getInstance();
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

    private void init() {
        logger = new LoggingTool(this);

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

	@TestMethod("testAccepts")
    public boolean accepts(Class classObject) {
		Class[] interfaces = classObject.getInterfaces();
		for (int i=0; i<interfaces.length; i++) {
			if (IChemFile.class.equals(interfaces[i])) return true;
		}
		
		if (IChemFile.class.equals(classObject))
			return true;
		return false;
	}

	/**
     * Read a IChemObject from input
     *
     * @return the content in a ChemFile object
     */
    public IChemObject read(IChemObject object) throws CDKException {
      if (object instanceof IChemFile) {
        return readChemFile((IChemFile)object);
      } else {
        throw new CDKException("Only supported is reading of ChemFile objects.");
      }
    }

    // private functions

    private IChemFile readChemFile(IChemFile file) throws CDKException {
        logger.debug("Started parsing from input...");
        try {
            parser.setFeature("http://xml.org/sax/features/validation", false);
            logger.info("Deactivated validation");
        } catch (SAXException e) {
            logger.warn("Cannot deactivate validation.");
            return null;
        }
        CMLHandler handler = new CMLHandler(file);
        // copy the manually added conventions
        for (String conv : userConventions.keySet()) {
        	handler.registerConvention(conv, userConventions.get(conv));
        }
        parser.setContentHandler(handler);
        parser.setEntityResolver(new CMLResolver());
        parser.setErrorHandler(new CMLErrorHandler());
        try {
            if (input == null) {
                logger.debug("Parsing from URL: ", url);
                parser.parse(url);
            } else {
                logger.debug("Parsing from Reader");
                parser.parse(new InputSource(input));
            }
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
        return file;
    }

    @TestMethod("testClose")
    public void close() throws IOException {
        input.close();
    }

}

