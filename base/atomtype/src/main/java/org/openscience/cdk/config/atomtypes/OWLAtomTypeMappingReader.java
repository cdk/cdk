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
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.openscience.cdk.config.OWLBasedAtomTypeConfigurator;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * XML Reader for the {@link OWLBasedAtomTypeConfigurator}.
 *
 */
public class OWLAtomTypeMappingReader {

    private XMLReader           parser;
    private final Reader              input;
    private static final ILoggingTool logger = LoggingToolFactory.createLoggingTool(OWLAtomTypeReader.class);

    /**
     * Instantiates the XML based AtomTypeReader.
     *
     * @param input The Reader to read the IAtomType's from.
     */
    public OWLAtomTypeMappingReader(Reader input) {
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
            } catch (ParserConfigurationException | SAXException exception) {
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
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException exception) {
                logger.warn("Could not instantiate Xerces XML reader!");
                logger.debug(exception);
            }
        }
        if (!success) {
            logger.error("Could not instantiate any XML parser!");
        }
    }

    /**
     * Reads the atom type mappings from the data file.
     *
     * @return         a Map with atom type mappings. Null, if some reading error occurred.
     */
    public Map<String, String> readAtomTypeMappings() {
        Map<String, String> mappings = null;
        try {
            parser.setFeature("http://xml.org/sax/features/validation", false);
            logger.info("Deactivated validation");
        } catch (SAXException exception) {
            logger.warn("Cannot deactivate validation: ", exception.getMessage());
            logger.debug(exception);
        }
        OWLAtomTypeMappingHandler handler = new OWLAtomTypeMappingHandler();
        parser.setContentHandler(handler);
        try {
            parser.parse(new InputSource(input));
            mappings = handler.getAtomTypeMappings();
        } catch (IOException exception) {
            logger.error("IOException: ", exception.getMessage());
            logger.debug(exception);
        } catch (SAXException saxe) {
            logger.error("SAXException: ", saxe.getMessage());
            logger.debug(saxe);
        }
        return mappings == null ? new HashMap<>() : mappings;
    }

}
