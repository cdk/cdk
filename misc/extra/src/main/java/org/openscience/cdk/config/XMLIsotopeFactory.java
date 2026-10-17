/* Copyright (C) 2001-2007  Christoph Steinbeck <steinbeck@users.sf.net>
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.config;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Used to store and return data of a particular isotope. As this class is a
 * singleton class, one gets an instance with:
 * <pre>
 * IsotopeFactory ifac = IsotopFactory.getInstance(new IChemObject().getNewBuilder());
 * </pre>
 *
 * <p>Data about the isotopes are read from the file
 * org.openscience.cdk.config.isotopes.xml in the cdk-standard
 * module. Part of the data in this file was collected from
 * the website <a href="http://www.webelements.org">webelements.org</a>.
 *
 * <p>The use of this class is exemplified as follows. To get information
 * about the major isotope of hydrogen, one can use this code:
 * <pre>
 *   IsotopeFactory factory = XMLIsotopeFactory.getInstance(DefaultChemObjectBuilder.getInstance());
 *   Isotope major = factory.getMajorIsotope("H");
 * </pre>
 *
 *
 * @author     steinbeck
 * @cdk.created    2001-08-29
 * @cdk.keyword    isotope
 * @cdk.keyword    element
 */
public class XMLIsotopeFactory implements IsotopeFactory {

    // store the big decimal
    static final String ExactMass = "cdk::ExactMass";
    static final String NaturalAbundance = "cdk::NaturalAbundance";

    private static XMLIsotopeFactory ifac  = null;
    private final boolean                  debug = false;
    private final List<IIsotope> isotopes;

    /**
     * Private constructor for the IsotopeFactory object.
     *
     *@exception IOException             A problem with reading the isotopes.xml
     *      file
     * @param builder The builder from which we the factory will be generated
     */
    private XMLIsotopeFactory(IChemObjectBuilder builder) throws IOException {
        logger.info("Creating new IsotopeFactory");

        InputStream ins;
        // ObjIn in = null;
        String errorMessage = "There was a problem getting org.openscience.cdk." + "config.isotopes.xml as a stream";
        try {
            String configFile = "org/openscience/cdk/config/data/isotopes.xml";
            if (debug) logger.debug("Getting stream for ", configFile);
            ins = this.getClass().getClassLoader().getResourceAsStream(configFile);
        } catch (Exception exception) {
            logger.error(errorMessage);
            logger.debug(exception);
            throw new IOException(errorMessage);
        }
        if (ins == null) {
            logger.error(errorMessage);
            throw new IOException(errorMessage);
        }
        IsotopeReader reader = new IsotopeReader(ins, builder);
        //in = new ObjIn(ins, new Config().aliasID(false));
        isotopes = Collections.unmodifiableList(reader.readIsotopes());
        if (debug) logger.debug("Found #isotopes in file: ", isotopes.size());
        /*
         * for (int f = 0; f < isotopes.size(); f++) { Isotope isotope =
         * (Isotope)isotopes.elementAt(f); } What's this loop for??
         */
    }

    /**
     * Returns an IsotopeFactory instance.
     *
         * @param      builder                 ChemObjectBuilder used to construct the Isotope's
     * @return                             The instance value
     * @exception  IOException  if isotopic data files could not be read.
     */
    public static XMLIsotopeFactory getInstance(IChemObjectBuilder builder) throws IOException {
        if (ifac == null) {
            ifac = new XMLIsotopeFactory(builder);
        }
        return ifac;
    }

    @Override
    public List<IIsotope> isotopes() {
        // these isotopes are immutable so make a copy of them
        List<IIsotope> copy = new ArrayList<>();
        for (IIsotope isotope : isotopes) {
            try {
                copy.add((IIsotope)isotope.clone());
            } catch (CloneNotSupportedException e) {
                logger.error("Could not clone the isotope");
            }
        }
        return copy;
    }

    /**
     * Reads an isotope list in CML2 format. An example definition is:
     * <pre>{@code
     * <isotopeList id="H">
     *   <isotope id="H1" isotopeNumber="1" elementTyp="H">
     *     <abundance dictRef="cdk:relativeAbundance">100.0</abundance>
     *     <scalar dictRef="cdk:exactMass">1.00782504</scalar>
     *     <scalar dictRef="cdk:atomicNumber">1</scalar>
     *   </isotope>
     *   <isotope id="H2" isotopeNumber="2" elementTyp="H">
     *     <abundance dictRef="cdk:relativeAbundance">0.015</abundance>
     *     <scalar dictRef="cdk:exactMass">2.01410179</scalar>
     *     <scalar dictRef="cdk:atomicNumber">1</scalar>
     *   </isotope>
     * </isotopeList>
     * }</pre>
     *
     */
    public static class IsotopeHandler extends DefaultHandler {

        private static final ILoggingTool logger = LoggingToolFactory.createLoggingTool(IsotopeHandler.class);
        private String              currentChars;
        private List<IIsotope>      isotopes;

        private IIsotope            workingIsotope;
        private String              currentElement;
        private String              dictRef;

        private final IChemObjectBuilder  builder;

        /**
         * Constructs an IsotopeHandler used by the IsotopeReader.
         *
         * @param builder The IChemObjectBuilder used to create new IIsotope's.
         */
        public IsotopeHandler(IChemObjectBuilder builder) {
            this.builder = builder;
        }

        /**
         * Returns the isotopes read from the XML file.
         *
         * @return A List object with all isotopes
         */
        public List<IIsotope> getIsotopes() {
            return isotopes;
        }

        // SAX Parser methods

        /** {@inheritDoc} */
        @Override
        public void startDocument() {
            isotopes = new ArrayList<>();
        }

        /** {@inheritDoc} */
        @Override
        public void endElement(String uri, String local, String raw) {
            logger.debug("end element: ", raw);
            if ("isotope".equals(local)) {
                if (workingIsotope != null) isotopes.add(workingIsotope);
                workingIsotope = null;
            } else if ("isotopeList".equals(local)) {
                currentElement = null;
            } else if ("scalar".equals(local)) {
                try {
                    if ("bo:exactMass".equals(dictRef)) {
                        workingIsotope.setProperty(ExactMass, new BigDecimal(currentChars));
                        workingIsotope.setExactMass(Double.parseDouble(currentChars));
                    } else if ("bo:atomicNumber".equals(dictRef)) {
                        workingIsotope.setAtomicNumber(Integer.parseInt(currentChars));
                    } else if ("bo:relativeAbundance".equals(dictRef)) {
                        workingIsotope.setProperty(NaturalAbundance, new BigDecimal(currentChars));
                        workingIsotope.setNaturalAbundance(Double.parseDouble(currentChars));
                    }
                } catch (NumberFormatException exception) {
                    logger.error("The ", dictRef, " value is incorrect: ", currentChars);
                    logger.debug(exception);
                }
            }
        }

        /** {@inheritDoc} */
        @Override
        public void startElement(String uri, String local, String raw, Attributes atts) {
            currentChars = "";
            dictRef = "";
            logger.debug("startElement: ", raw);
            logger.debug("uri: ", uri);
            logger.debug("local: ", local);
            logger.debug("raw: ", raw);
            if ("isotope".equals(local)) {
                workingIsotope = createIsotopeOfElement(currentElement, atts);
            } else if ("isotopeList".equals(local)) {
                currentElement = getElementSymbol(atts);
            } else if ("scalar".equals(local)) {
                for (int i = 0; i < atts.getLength(); i++) {
                    if ("dictRef".equals(atts.getQName(i))) {
                        dictRef = atts.getValue(i);
                    }
                }
            }
        }

        /** {@inheritDoc} */
        @Override
        public void characters(char[] chars, int start, int length) {
            currentChars += new String(chars, start, length);
        }

        private IIsotope createIsotopeOfElement(String currentElement, Attributes atts) {
            IIsotope isotope = builder.newInstance(IIsotope.class, currentElement);

            for (int i = 0; i < atts.getLength(); i++) {
                try {
                    if ("id".equals(atts.getQName(i))) {
                        isotope.setID(atts.getValue(i));
                    } else if ("number".equals(atts.getQName(i))) {
                        isotope.setMassNumber(Integer.parseInt(atts.getValue(i)));
                    } else if ("elementType".equals(atts.getQName(i))) {
                        isotope.setSymbol(atts.getValue(i));
                    }
                } catch (NumberFormatException exception) {
                    logger.error("Value of isotope@", atts.getQName(i), " is not as expected.");
                    logger.debug(exception);
                }
            }

            // we set the natural abundance to 0, since the default is -1, but
            // some isotope entries have no entry for this field, so the values
            // stays at -1
            isotope.setNaturalAbundance(0.0);

            return isotope;
        }

        private String getElementSymbol(Attributes atts) {
            for (int i = 0; i < atts.getLength(); i++) {
                if ("id".equals(atts.getQName(i))) {
                    return atts.getValue(i);
                }
            }
            return "";
        }

    }

    /**
     * Reader that instantiates an XML parser and customized handler to process
     * the isotope information in the CML2 isotope data file. The Reader first
     * tries to instantiate a JAXP XML parser available from Sun JVM 1.4.0 and
     * later. If not found it tries the Aelfred2 parser, and as last try the
     * Xerces parser.
     *
     *
     * @author     Egon Willighagen
     */
    public static class IsotopeReader {

        private XMLReader parser;
        private final InputStream         input;

        private static final ILoggingTool logger = LoggingToolFactory.createLoggingTool(IsotopeReader.class);
        private final IChemObjectBuilder  builder;

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

            // Aelfred is first alternative.
            if (!success) {
                try {
                    parser = (XMLReader) this.getClass().getClassLoader()
                                             .loadClass("gnu.xml.aelfred2.XmlReader")
                                             .getDeclaredConstructor()
                                             .newInstance();
                    logger.info("Using Aelfred2 XML parser.");
                    success = true;
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    logger.warn("Could not instantiate Aelfred2 XML reader!");
                    logger.debug(e);
                }
            }
            // Xerces is second alternative
            if (!success) {
                try {
                    parser = (XMLReader) this.getClass().getClassLoader().loadClass("org.apache.xerces.parsers.SAXParser")
                                             .getDeclaredConstructor()
                                             .newInstance();
                    logger.info("Using Xerces XML parser.");
                    success = true;
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
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
            List<IIsotope> isotopes = new ArrayList<>();
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
}
