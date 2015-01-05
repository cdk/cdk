/* Copyright (C) 2003-2007  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sf.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.config.isotopes;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Reads an isotope list in CML2 format. An example definition is:
 * <pre>
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
 * </pre>
 *
 * @cdk.module  extra
 * @cdk.githash
 */
public class IsotopeHandler extends DefaultHandler {

    private static ILoggingTool logger = LoggingToolFactory.createLoggingTool(IsotopeHandler.class);
    private String              currentChars;
    private List<IIsotope>      isotopes;

    private IIsotope            workingIsotope;
    private String              currentElement;
    private String              dictRef;

    private IChemObjectBuilder  builder;

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
        isotopes = new ArrayList<IIsotope>();
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
                    workingIsotope.setExactMass(Double.parseDouble(currentChars));
                } else if ("bo:atomicNumber".equals(dictRef)) {
                    workingIsotope.setAtomicNumber(Integer.parseInt(currentChars));
                } else if ("bo:relativeAbundance".equals(dictRef)) {
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
    public void characters(char chars[], int start, int length) {
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
