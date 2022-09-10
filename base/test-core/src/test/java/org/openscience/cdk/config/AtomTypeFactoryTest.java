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
package org.openscience.cdk.config;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IAtomType.Hybridization;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.test.CDKTestCase;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;

/**
 * Checks the functionality of the AtomTypeFactory.
 *
 * @cdk.module test-core
 */
public class AtomTypeFactoryTest extends CDKTestCase {

    final static AtomTypeFactory atf                  = AtomTypeFactory.getInstance(new ChemObject().getBuilder());

//    private static final String  JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
//
//    private static final String  W3C_XML_SCHEMA       = "http://www.w3.org/2001/XMLSchema";
    private static final String CML_XSD_FILENAME = "cml25b1.xsd";
    private static final String CML_XSD_ABSOLUTE_PATH = "/org/openscience/cdk/io/cml/data" + "/" + CML_XSD_FILENAME;

    @Test
    public void testAtomTypeFactory() {
        Assert.assertNotNull(atf);
        Assert.assertNotSame(atf.getSize(), 0);
    }

    @Test
    public void testGetInstance_InputStream_String_IChemObjectBuilder() {
        String configFile = "org/openscience/cdk/config/data/structgen_atomtypes.xml";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(configFile);
        AtomTypeFactory atf = AtomTypeFactory.getInstance(ins, "xml", new ChemObject().getBuilder());
        Assert.assertNotNull(atf);
        Assert.assertNotSame(0, atf.getSize());
    }

    @Test
    public void testGetInstance_String_IChemObjectBuilder() {
        String configFile = "org/openscience/cdk/config/data/structgen_atomtypes.xml";
        AtomTypeFactory atf = AtomTypeFactory.getInstance(configFile, new ChemObject().getBuilder());
        Assert.assertNotNull(atf);
        Assert.assertNotSame(0, atf.getSize());
    }

    @Test
    public void testGetInstance_IChemObjectBuilder() {
        AtomTypeFactory atf = AtomTypeFactory.getInstance(new ChemObject().getBuilder());
        Assert.assertNotNull(atf);
    }

    @Test
    public void testGetSize() {
        AtomTypeFactory atf = AtomTypeFactory.getInstance(new ChemObject().getBuilder());
        Assert.assertNotSame(0, atf.getSize());
    }

    @Test
    public void testGetAllAtomTypes() {
        AtomTypeFactory atf = AtomTypeFactory.getInstance(new ChemObject().getBuilder());
        IAtomType[] types = atf.getAllAtomTypes();
        Assert.assertNotNull(types);
        Assert.assertNotSame(0, types.length);
    }

    @Test
    public void testGetAtomType_String() throws Exception {
        IAtomType atomType = atf.getAtomType("C4");
        Assert.assertNotNull(atomType);
        Assert.assertEquals("C", atomType.getSymbol());
        Assert.assertEquals("C4", atomType.getAtomTypeName());
        Assert.assertEquals(4.0, atomType.getBondOrderSum(), 0.001);
        Assert.assertEquals(IBond.Order.TRIPLE, atomType.getMaxBondOrder());
    }

    @Test
    public void testGetAtomTypes_String() {
        IAtomType[] atomTypes = atf.getAtomTypes("C");

        Assert.assertNotNull(atomTypes);
        Assert.assertTrue(0 < atomTypes.length);
        Assert.assertEquals("C", atomTypes[0].getSymbol());
    }

    @Test
    public void testGetAtomTypeFromPDB() throws Exception {
        AtomTypeFactory factory = AtomTypeFactory.getInstance("org/openscience/cdk/config/data/pdb_atomtypes.xml",
                new ChemObject().getBuilder());
        IAtomType atomType = factory.getAtomType("ALA.CA");

        Assert.assertNotNull(atomType);
        Assert.assertEquals("C", atomType.getSymbol());
        Assert.assertEquals("ALA.CA", atomType.getAtomTypeName());
    }

    @Test
    public void testGetAtomTypeFromOWL() throws Exception {
        AtomTypeFactory factory = AtomTypeFactory.getInstance("org/openscience/cdk/dict/data/cdk-atom-types.owl",
                new ChemObject().getBuilder());
        IAtomType atomType = factory.getAtomType("C.sp3");
        Assert.assertNotNull(atomType);
        Assert.assertEquals("C", atomType.getSymbol());
        Assert.assertEquals("C.sp3", atomType.getAtomTypeName());
        Assert.assertEquals(IAtomType.Hybridization.SP3, atomType.getHybridization());
        Assert.assertEquals(0, atomType.getFormalCharge().intValue());
        Assert.assertEquals(4, atomType.getFormalNeighbourCount().intValue());
        Assert.assertNotNull(atomType.getProperty(CDKConstants.LONE_PAIR_COUNT));
        Assert.assertTrue(atomType.getProperty(CDKConstants.LONE_PAIR_COUNT) instanceof Integer);
        Assert.assertEquals(0, ((Integer) atomType.getProperty(CDKConstants.LONE_PAIR_COUNT)).intValue());
        Assert.assertNotNull(atomType.getProperty(CDKConstants.PI_BOND_COUNT));
        Assert.assertTrue(atomType.getProperty(CDKConstants.PI_BOND_COUNT) instanceof Integer);
        Assert.assertEquals(0, ((Integer) atomType.getProperty(CDKConstants.PI_BOND_COUNT)).intValue());
        Assert.assertEquals(Order.SINGLE, atomType.getMaxBondOrder());
        Assert.assertEquals(4.0, atomType.getBondOrderSum(), 0.1);

        atomType = factory.getAtomType("N.sp2.radical");
        Assert.assertNotNull(atomType);
        Assert.assertEquals("N", atomType.getSymbol());
        Assert.assertEquals("N.sp2.radical", atomType.getAtomTypeName());
        Assert.assertEquals(IAtomType.Hybridization.SP2, atomType.getHybridization());
        Assert.assertEquals(0, atomType.getFormalCharge().intValue());
        Assert.assertEquals(1, atomType.getFormalNeighbourCount().intValue());
        Assert.assertNotNull(atomType.getProperty(CDKConstants.LONE_PAIR_COUNT));
        Assert.assertTrue(atomType.getProperty(CDKConstants.LONE_PAIR_COUNT) instanceof Integer);
        Assert.assertEquals(1, ((Integer) atomType.getProperty(CDKConstants.LONE_PAIR_COUNT)).intValue());
        Assert.assertNotNull(atomType.getProperty(CDKConstants.PI_BOND_COUNT));
        Assert.assertTrue(atomType.getProperty(CDKConstants.PI_BOND_COUNT) instanceof Integer);
        Assert.assertEquals(1, ((Integer) atomType.getProperty(CDKConstants.PI_BOND_COUNT)).intValue());
        Assert.assertEquals(Order.DOUBLE, atomType.getMaxBondOrder());
        Assert.assertEquals(2.0, atomType.getBondOrderSum(), 0.1);

        atomType = factory.getAtomType("N.planar3");
        Assert.assertNotNull(atomType);
        Assert.assertEquals("N", atomType.getSymbol());
        Assert.assertEquals("N.planar3", atomType.getAtomTypeName());
        Assert.assertEquals(IAtomType.Hybridization.PLANAR3, atomType.getHybridization());
        Assert.assertEquals(0, atomType.getFormalCharge().intValue());
        Assert.assertEquals(3, atomType.getFormalNeighbourCount().intValue());
        Assert.assertNotNull(atomType.getProperty(CDKConstants.LONE_PAIR_COUNT));
        Assert.assertTrue(atomType.getProperty(CDKConstants.LONE_PAIR_COUNT) instanceof Integer);
        Assert.assertEquals(1, ((Integer) atomType.getProperty(CDKConstants.LONE_PAIR_COUNT)).intValue());
        Assert.assertNotNull(atomType.getProperty(CDKConstants.PI_BOND_COUNT));
        Assert.assertTrue(atomType.getProperty(CDKConstants.PI_BOND_COUNT) instanceof Integer);
        Assert.assertEquals(0, ((Integer) atomType.getProperty(CDKConstants.PI_BOND_COUNT)).intValue());
        Assert.assertEquals(Order.SINGLE, atomType.getMaxBondOrder());
        Assert.assertEquals(3.0, atomType.getBondOrderSum(), 0.1);
    }

    @Test
    public void testGetAtomTypeFromOWL_Sybyl() throws Exception {
        AtomTypeFactory factory = AtomTypeFactory.getInstance("org/openscience/cdk/dict/data/sybyl-atom-types.owl",
                new ChemObject().getBuilder());
        IAtomType atomType = factory.getAtomType("C.3");

        Assert.assertNotNull(atomType);
        Assert.assertEquals("C", atomType.getSymbol());
        Assert.assertEquals("C.3", atomType.getAtomTypeName());
        Assert.assertEquals(4, atomType.getFormalNeighbourCount().intValue());
        Assert.assertEquals(IAtomType.Hybridization.SP3, atomType.getHybridization());
        Assert.assertEquals(0, atomType.getFormalCharge().intValue());
        Assert.assertNotNull(atomType.getProperty(CDKConstants.LONE_PAIR_COUNT));
        Assert.assertTrue(atomType.getProperty(CDKConstants.LONE_PAIR_COUNT) instanceof Integer);
        Assert.assertEquals(0, ((Integer) atomType.getProperty(CDKConstants.LONE_PAIR_COUNT)).intValue());
        Assert.assertNotNull(atomType.getProperty(CDKConstants.PI_BOND_COUNT));
        Assert.assertTrue(atomType.getProperty(CDKConstants.PI_BOND_COUNT) instanceof Integer);
        Assert.assertEquals(0, ((Integer) atomType.getProperty(CDKConstants.PI_BOND_COUNT)).intValue());
    }

    @Test
    public void testGetAtomTypeFromJmol() throws Exception {
        AtomTypeFactory factory = AtomTypeFactory.getInstance("org/openscience/cdk/config/data/jmol_atomtypes.txt",
                new ChemObject().getBuilder());
        IAtomType atomType = factory.getAtomType("H");

        Assert.assertNotNull(atomType);
        Assert.assertEquals("H", atomType.getSymbol());
        Assert.assertEquals("H", atomType.getAtomTypeName());
    }

    @Test
    public void testConfigure_IAtom() throws Exception {
        IAtomType atomType;
        IAtom atom = new org.openscience.cdk.Atom();
        atom.setAtomTypeName("C.ar");
        AtomTypeFactory factory = AtomTypeFactory.getInstance("org/openscience/cdk/config/data/mol2_atomtypes.xml",
                new ChemObject().getBuilder());
        atomType = factory.configure(atom);
        Assert.assertNotNull(atomType);

        Assert.assertEquals("C", atom.getSymbol());
    }

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
    @Test
    public void testGetAtomTypeFromMM2() throws Exception {
        AtomTypeFactory factory;
        factory = AtomTypeFactory.getInstance("org/openscience/cdk/config/data/mm2_atomtypes.xml",
                new ChemObject().getBuilder());

        IAtomType atomType = factory.getAtomType("C");
        Assert.assertNotNull(atomType);
        Assert.assertEquals("C", atomType.getSymbol());
        Assert.assertEquals("C", atomType.getAtomTypeName());
        Assert.assertEquals("[CSP]-[0-4][-]?+;[A-Za-z\\+\\-&&[^=%]]{0,6}[(].*+",
                atomType.getProperty(CDKConstants.SPHERICAL_MATCHER));
        Assert.assertEquals(Hybridization.SP3, atomType.getHybridization());

        atomType = factory.getAtomType("Sthi");
        Assert.assertNotNull(atomType);
        Assert.assertEquals("S", atomType.getSymbol());
        Assert.assertEquals("Sthi", atomType.getAtomTypeName());
        Assert.assertEquals("S-[2];[H]{0,3}+=C.*+", atomType.getProperty(CDKConstants.SPHERICAL_MATCHER));
        Assert.assertEquals(Hybridization.SP2, atomType.getHybridization());
        Assert.assertTrue(atomType.getFlag(CDKConstants.IS_HYDROGENBOND_ACCEPTOR));
        Assert.assertEquals((Integer)5, atomType.getProperty(CDKConstants.PART_OF_RING_OF_SIZE));
    }

    @Test
    public void testCanReadCMLSchema() throws Exception {
        try (InputStream cmlSchema = getClass().getResourceAsStream(CML_XSD_ABSOLUTE_PATH)) {
            Assert.assertNotNull("Could not find the CML schema", cmlSchema);

            DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            // make sure the schema is read
            Document schemaDoc = parser.parse(cmlSchema);
            Assert.assertNotNull(schemaDoc.getFirstChild());
            Assert.assertEquals("xsd:schema", schemaDoc.getFirstChild().getNodeName());
        }
    }

    @Test
    public void testXMLValidityMM2() throws Exception {
        assertValidCML("/org/openscience/cdk/config/data/mm2_atomtypes.xml", "MM2");
    }

    @Test
    public void testXMLValidityMMFF94() throws Exception {
        assertValidCML("/org/openscience/cdk/config/data/mmff94_atomtypes.xml", "MMFF94");
    }

    @Test
    public void testXMLValidityMol2() throws Exception {
        assertValidCML("/org/openscience/cdk/config/data/mol2_atomtypes.xml", "Mol2");
    }

    @Test
    public void testXMLValidityPDB() throws Exception {
        assertValidCML("/org/openscience/cdk/config/data/pdb_atomtypes.xml", "PDB");
    }

    @Test
    public void testXMLValidityStructGen() throws Exception {
        assertValidCML("/org/openscience/cdk/config/data/structgen_atomtypes.xml", "StructGen");
    }

    private void assertValidCML(String atomTypeList, String shortcut) throws Exception {
        try (InputStream in = getClass().getResourceAsStream(atomTypeList);
             InputStream cmlSchema = getClass().getResourceAsStream(CML_XSD_ABSOLUTE_PATH)) {

            Assert.assertNotNull("Could not find the atom type list CML source", in);
            Assert.assertNotNull("Could not find the CML schema", cmlSchema);

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

        public SAXValidityErrorHandler(String atomTypeList) {
            this.atomTypeList = atomTypeList;
        }

        @Override
        public void error(SAXParseException arg0) {
            Assert.fail(atomTypeList + " is not valid on line " + arg0.getLineNumber() + ": " + arg0.getMessage());
        }

        @Override
        public void fatalError(SAXParseException arg0) {
            Assert.fail(atomTypeList + " is not valid on line " + arg0.getLineNumber() + ": " + arg0.getMessage());
        }

        @Override
        public void warning(SAXParseException arg0) {
            // warnings are fine
        }

    }

}
