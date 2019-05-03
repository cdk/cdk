/* Copyright (C) 2003-2007  Egon Willighagen <egonw@users.sf.net>
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
 */
package org.openscience.cdk.config.isotopes;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Reader that instantiates an XML parser and customized handler to process
 * the isotope information in the CML2 isotope data file. The Reader first
 * tries to instantiate a JAXP XML parser available from Sun JVM 1.4.0 and
 * later. If not found it tries the Aelfred2 parser, and as last try the
 * Xerces parser.
 *
 * @cdk.module  extra
 * @cdk.githash
 *
 * @author     Egon Willighagen
 */
public class IsotopeReader {

    private XMLReader           parser;
    private InputStream         input;

    private static ILoggingTool logger = LoggingToolFactory.createLoggingTool(IsotopeReader.class);
    private IChemObjectBuilder  builder;

    /**
     * Instantiates a new reader that parses the XML from the given <code>input</code>.
     *
     * @param input   InputStream with the XML source
     * @param builder The {@link IChemObjectBuilder} used to create new IIsotope's.
     */
    public IsotopeReader(InputStream input, IChemObjectBuilder builder) {
        this.init();
        this.input = input;
        this.builder = builder;
    }

    private void init() {
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
            } catch (ParserConfigurationException | SAXException exception) {
                logger.warn("Could not instantiate JAXP/SAX XML reader!");
                logger.debug(exception);
            }
        }
        // Aelfred is first alternative.
        if (!success) {
            try {
                parser = (XMLReader) this.getClass().getClassLoader().loadClass("gnu.xml.aelfred2.XmlReader")
                        .newInstance();
                logger.info("Using Aelfred2 XML parser.");
                success = true;
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                logger.warn("Could not instantiate Aelfred2 XML reader!");
                logger.debug(e);
            }
        }
        // Xerces is second alternative
        if (!success) {
            try {
                parser = (XMLReader) this.getClass().getClassLoader().loadClass("org.apache.xerces.parsers.SAXParser")
                        .newInstance();
                logger.info("Using Xerces XML parser.");
                success = true;
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                logger.warn("Could not instantiate Xerces XML reader!");
                logger.debug(e);
            }
        }
        if (!success) {
            logger.error("Could not instantiate any XML parser!");
        }
    }

    /**
     * Triggers the XML parsing of the data file and returns the read Isotopes.
     * It turns of XML validation before parsing.
     *
     * @return a List of Isotope's. Returns an empty list is some reading error
     *         occurred.
     */
    public List<IIsotope> readIsotopes() {
        List<IIsotope> isotopes = new ArrayList<IIsotope>();
        try {
            parser.setFeature("http://xml.org/sax/features/validation", false);
            logger.info("Deactivated validation");
        } catch (SAXException exception) {
            logger.warn("Cannot deactivate validation: ", exception.getMessage());
            logger.debug(exception);
        }
        IsotopeHandler handler = new IsotopeHandler(builder);
        parser.setContentHandler(handler);
        try {
            parser.parse(new InputSource(input));
            isotopes = handler.getIsotopes();
        } catch (IOException exception) {
            logger.error("IOException: ", exception.getMessage());
            logger.debug(exception);
        } catch (SAXException saxe) {
            logger.error("SAXException: ", saxe.getClass().getName());
            logger.error(saxe.getMessage());
            logger.debug(saxe);
        }
        return isotopes;
    }

}
