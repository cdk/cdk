/* Copyright (C) 2002-2008  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.config.atomtypes;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.config.OWLBasedAtomTypeConfigurator;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * XML Reader for the {@link OWLBasedAtomTypeConfigurator}.
 *
 * @cdk.module  core
 * @cdk.githash
 */
@TestClass("org.openscience.cdk.config.atomtypes.OWLAtomTypeReaderTest")
public class OWLAtomTypeReader {

    private XMLReader           parser;
    private Reader              input;
    private static ILoggingTool logger = LoggingToolFactory.createLoggingTool(OWLAtomTypeReader.class);

    /**
     * Instantiates the XML based AtomTypeReader.
     *
     * @param input The Reader to read the IAtomType's from.
     */
    @TestMethod("testAtomTypeReader_Reader")
    public OWLAtomTypeReader(Reader input) {
        this.init();
        this.input = input;
    }

    private void init() {
        boolean success = false;
        // If JAXP is preferred (comes with Sun JVM 1.4.0 and higher)
        if (!success) {
            try {
                javax.xml.parsers.SAXParserFactory spf = javax.xml.parsers.SAXParserFactory.newInstance();
                spf.setNamespaceAware(true);
                javax.xml.parsers.SAXParser saxParser = spf.newSAXParser();
                parser = saxParser.getXMLReader();
                logger.info("Using JAXP/SAX XML parser.");
                success = true;
            } catch (Exception exception) {
                logger.warn("Could not instantiate JAXP/SAX XML reader!");
                logger.debug(exception);
            }
        }
        // Xerces is an alternative
        if (!success) {
            try {
                parser = (XMLReader) this.getClass().getClassLoader().loadClass("org.apache.xerces.parsers.SAXParser")
                        .newInstance();
                logger.info("Using Xerces XML parser.");
                success = true;
            } catch (Exception exception) {
                logger.warn("Could not instantiate Xerces XML reader!");
                logger.debug(exception);
            }
        }
        if (!success) {
            logger.error("Could not instantiate any XML parser!");
        }
    }

    /**
     * Reads the atom types from the data file.
     *
     * @param  builder The IChemObjectBuilder used to create new IAtomType's.
     * @return         a List with atom types. Is empty if some reading error occurred.
     */
    @TestMethod("testReadAtomTypes_CDK,testReadAtomTypes_IChemObjectBuilder")
    public List<IAtomType> readAtomTypes(IChemObjectBuilder builder) {
        List<IAtomType> isotopes = new ArrayList<IAtomType>();
        try {
            parser.setFeature("http://xml.org/sax/features/validation", false);
            logger.info("Deactivated validation");
        } catch (SAXException exception) {
            logger.warn("Cannot deactivate validation: ", exception.getMessage());
            logger.debug(exception);
        }
        OWLAtomTypeHandler handler = new OWLAtomTypeHandler(builder);
        parser.setContentHandler(handler);
        try {
            parser.parse(new InputSource(input));
            isotopes = handler.getAtomTypes();
        } catch (IOException exception) {
            logger.error("IOException: ", exception.getMessage());
            logger.debug(exception);
        } catch (SAXException saxe) {
            logger.error("SAXException: ", saxe.getMessage());
            logger.debug(saxe);
        }
        return isotopes;
    }

}
