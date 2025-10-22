/* Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
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
package org.openscience.cdk.atomtype;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IAtomType.Hybridization;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.test.CDKTestCase;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;

/**
 * Checks the functionality of the AtomTypeFactory for MM2/MMFF94 legacy atom
 * type matchers.
 */
class LegacyAtomTypeFactoryTest {

    private final static AtomTypeFactory atf                  = AtomTypeFactory.getInstance(new ChemObject().getBuilder());

//    private static final String  JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
//
//    private static final String  W3C_XML_SCHEMA       = "http://www.w3.org/2001/XMLSchema";
    private static final String CML_XSD_FILENAME = "cml25b1.xsd";
    private static final String CML_XSD_ABSOLUTE_PATH = "/org/openscience/cdk/io/cml/data" + "/" + CML_XSD_FILENAME;


    /**
     * Test reading from a XML config file with content like:
     * <pre>
     *   <atomType id="C">
     *    <!-- for example in CC-->
     *    <atom elementType="C" formalCharge="0">
     *      <scalar dataType="xsd:double" dictRef="cdk:maxBondOrder">1.0</scalar>
     *      <scalar dataType="xsd:double" dictRef="cdk:bondOrderSum">4.0</scalar>
     *      <scalar dataType="xsd:integer" dictRef="cdk:formalNeighbourCount">4</scalar>
     *      <scalar dataType="xsd:integer" dictRef="cdk:valency">4</scalar>
     *    </atom>
     *    <scalar dataType="xsd:string" dictRef="cdk:hybridization">sp3</scalar>
     *    <scalar dataType="xsd:string" dictRef="cdk:DA">-</scalar>
     *    <scalar dataType="xsd:string" dictRef="cdk:sphericalMatcher">[CSP]-[0-4][-]?+;[A-Za-z\+\-&amp;&amp;[^=%]]{0,6}[(].*+</scalar>
     *  </atomType>
     * </pre>
     *
     * @throws Exception if the atom typ info cannot be loaded
     */
    // moved to cdk-legacy as no longer used in core codebase
    @Test
    void testGetAtomTypeFromMM2() throws Exception {
        AtomTypeFactory factory;
        factory = AtomTypeFactory.getInstance("org/openscience/cdk/config/data/mm2_atomtypes.xml",
                                              new ChemObject().getBuilder());

        IAtomType atomType = factory.getAtomType("C");
        Assertions.assertNotNull(atomType);
        Assertions.assertEquals("C", atomType.getSymbol());
        Assertions.assertEquals("C", atomType.getAtomTypeName());
        Assertions.assertEquals("[CSP]-[0-4][-]?+;[A-Za-z\\+\\-&&[^=%]]{0,6}[(].*+", atomType.getProperty(CDKConstants.SPHERICAL_MATCHER));
        Assertions.assertEquals(Hybridization.SP3, atomType.getHybridization());

        atomType = factory.getAtomType("Sthi");
        Assertions.assertNotNull(atomType);
        Assertions.assertEquals("S", atomType.getSymbol());
        Assertions.assertEquals("Sthi", atomType.getAtomTypeName());
        Assertions.assertEquals("S-[2];[H]{0,3}+=C.*+", atomType.getProperty(CDKConstants.SPHERICAL_MATCHER));
        Assertions.assertEquals(Hybridization.SP2, atomType.getHybridization());
        Assertions.assertTrue(atomType.getFlag(IChemObject.HYDROGEN_BOND_ACCEPTOR));
        Assertions.assertEquals((Integer)5, atomType.getProperty(CDKConstants.PART_OF_RING_OF_SIZE));
    }

    // moved to cdk-legacy as no longer used
    @Test
    void testXMLValidityMM2() throws Exception {
        assertValidCML("/org/openscience/cdk/config/data/mm2_atomtypes.xml", "MM2");
    }

    // moved to cdk-legacy as no longer used by the more correct MmffAtomTypeMatcher (cdk-forcefield)
    @Test
    void testXMLValidityMMFF94() throws Exception {
        assertValidCML("/org/openscience/cdk/config/data/mmff94_atomtypes.xml", "MMFF94");
    }

    private void assertValidCML(String atomTypeList, String shortcut) throws Exception {
        try (InputStream in = getClass().getResourceAsStream(atomTypeList);
             InputStream cmlSchema = getClass().getResourceAsStream(CML_XSD_ABSOLUTE_PATH)) {

            Assertions.assertNotNull(in, "Could not find the atom type list CML source");
            Assertions.assertNotNull(cmlSchema, "Could not find the CML schema");

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setValidating(true);
            // JWM not needed?
            // factory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
            // factory.setAttribute(JAXP_SCHEMA_LANGUAGE, cmlSchema);
            factory.setFeature("http://apache.org/xml/features/validation/schema", true);

            DocumentBuilder parser = factory.newDocumentBuilder();
            parser.setErrorHandler(new SAXValidityErrorHandler(shortcut));
            parser.setEntityResolver((publicId, systemId) -> {
                if (systemId.endsWith(CML_XSD_FILENAME))
                    return new InputSource(cmlSchema);
                return null;
            });
            parser.parse(in);
        }
    }

    static class SAXValidityErrorHandler implements ErrorHandler {

        private final String atomTypeList;

        SAXValidityErrorHandler(String atomTypeList) {
            this.atomTypeList = atomTypeList;
        }

        @Override
        public void error(SAXParseException arg0) {
            Assertions.fail(atomTypeList + " is not valid on line " + arg0.getLineNumber() + ": " + arg0.getMessage());
        }

        @Override
        public void fatalError(SAXParseException arg0) {
            Assertions.fail(atomTypeList + " is not valid on line " + arg0.getLineNumber() + ": " + arg0.getMessage());
        }

        @Override
        public void warning(SAXParseException arg0) {
            // warnings are fine
        }

    }

}
