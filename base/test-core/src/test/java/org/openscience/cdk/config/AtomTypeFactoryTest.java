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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IAtomType.Hybridization;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Checks the functionality of the AtomTypeFactory.
 *
 * @cdk.module test-core
 */
public class AtomTypeFactoryTest extends CDKTestCase {

    final static AtomTypeFactory atf                  = AtomTypeFactory.getInstance(new ChemObject().getBuilder());

    private static final String  JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";

    private static final String  W3C_XML_SCHEMA       = "http://www.w3.org/2001/XMLSchema";

    static File                  tmpCMLSchema;

    static {
        try {
            InputStream in = AtomTypeFactoryTest.class.getClassLoader().getResourceAsStream(
                    "org/openscience/cdk/io/cml/data/cml25b1.xsd");
            tmpCMLSchema = copyFileToTmp("cml2.5.b1", ".xsd", in, null, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAtomTypeFactory() {
        Assert.assertNotNull(atf);
        Assert.assertNotSame(atf.getSize(), 0);
    }

    @Test
    public void testGetInstance_InputStream_String_IChemObjectBuilder() throws Exception {
        String configFile = "org/openscience/cdk/config/data/structgen_atomtypes.xml";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(configFile);
        AtomTypeFactory atf = AtomTypeFactory.getInstance(ins, "xml", new ChemObject().getBuilder());
        Assert.assertNotNull(atf);
        Assert.assertNotSame(0, atf.getSize());
    }

    @Test
    public void testGetInstance_String_IChemObjectBuilder() throws Exception {
        String configFile = "org/openscience/cdk/config/data/structgen_atomtypes.xml";
        AtomTypeFactory atf = AtomTypeFactory.getInstance(configFile, new ChemObject().getBuilder());
        Assert.assertNotNull(atf);
        Assert.assertNotSame(0, atf.getSize());
    }

    @Test
    public void testGetInstance_IChemObjectBuilder() throws Exception {
        AtomTypeFactory atf = AtomTypeFactory.getInstance(new ChemObject().getBuilder());
        Assert.assertNotNull(atf);
    }

    @Test
    public void testGetSize() throws Exception {
        AtomTypeFactory atf = AtomTypeFactory.getInstance(new ChemObject().getBuilder());
        Assert.assertNotSame(0, atf.getSize());
    }

    @Test
    public void testGetAllAtomTypes() throws Exception {
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
    public void testGetAtomTypes_String() throws Exception {
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
        Assert.assertEquals(5, atomType.getProperty(CDKConstants.PART_OF_RING_OF_SIZE));
    }

    @Test
    public void testCanReadCMLSchema() throws Exception {
        InputStream cmlSchema = new FileInputStream(tmpCMLSchema);
        Assert.assertNotNull("Could not find the CML schema", cmlSchema);

        DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        // make sure the schema is read
        Document schemaDoc = parser.parse(cmlSchema);
        Assert.assertNotNull(schemaDoc.getFirstChild());
        Assert.assertEquals("xsd:schema", schemaDoc.getFirstChild().getNodeName());
    }

    @Test
    public void testXMLValidityMM2() throws Exception {
        assertValidCML("org/openscience/cdk/config/data/mm2_atomtypes.xml", "MM2");
    }

    @Test
    public void testXMLValidityMMFF94() throws Exception {
        assertValidCML("org/openscience/cdk/config/data/mmff94_atomtypes.xml", "MMFF94");
    }

    @Test
    public void testXMLValidityMol2() throws Exception {
        assertValidCML("org/openscience/cdk/config/data/mol2_atomtypes.xml", "Mol2");
    }

    @Test
    public void testXMLValidityPDB() throws Exception {
        assertValidCML("org/openscience/cdk/config/data/pdb_atomtypes.xml", "PDB");
    }

    @Test
    public void testXMLValidityStructGen() throws Exception {
        assertValidCML("org/openscience/cdk/config/data/structgen_atomtypes.xml", "StructGen");
    }

    private void assertValidCML(String atomTypeList, String shortcut) throws Exception {
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(atomTypeList);
        File tmpInput = copyFileToTmp(shortcut, ".cmlinput", ins, "../../io/cml/data/cml25b1.xsd", "file://"
                + tmpCMLSchema.getAbsolutePath());
        Assert.assertNotNull("Could not find the atom type list CML source", ins);

        if (System.getProperty("java.version").indexOf("1.6") != -1
            || System.getProperty("java.version").indexOf("1.7") != -1
            || System.getProperty("java.version").indexOf("1.8") != -1) {

            InputStream cmlSchema = new FileInputStream(tmpCMLSchema);
            Assert.assertNotNull("Could not find the CML schema", cmlSchema);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setValidating(true);
            factory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
            factory.setAttribute(JAXP_SCHEMA_LANGUAGE, cmlSchema);
            factory.setFeature("http://apache.org/xml/features/validation/schema", true);

            DocumentBuilder parser = factory.newDocumentBuilder();
            parser.setErrorHandler(new SAXValidityErrorHandler(shortcut));
            parser.parse(new FileInputStream(tmpInput));
        } else if (System.getProperty("java.version").indexOf("1.5") != -1) {
            DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = parser.parse(tmpInput);
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Source schemaFile = new StreamSource(tmpCMLSchema);
            Schema schema = factory.newSchema(schemaFile);
            Validator validator = schema.newValidator();
            validator.validate(new DOMSource(document));
        } else {
            Assert.fail("Don't know how to validate with Java version: " + System.getProperty("java.version"));
        }
    }

    /**
     * Copies a file to TMP (whatever that is on your platform), and optionally
     * replaces a String on the fly. The temporary file will be named prefix+suffix
     *
     * @param prefix      Prefix of the temporary file name
     * @param suffix      Suffix of the temporary file name
     * @param in          InputStream to copy from
     * @param toReplace   String to replace. Null, if nothing needs to be replaced.
     * @param replaceWith String that replaces the toReplace. Null, if nothing needs to be replaced.
     *
     * @return            The temporary file/
     * @throws IOException   if the temp file cannot be created
     */
    private static File copyFileToTmp(String prefix, String suffix, InputStream in, String toReplace, String replaceWith)
            throws IOException {
        File tmpFile = File.createTempFile(prefix, suffix);
        FileOutputStream out = new FileOutputStream(tmpFile);
        byte[] buf = new byte[4096];
        int i;
        while ((i = in.read(buf)) != -1) {
            if (toReplace != null && replaceWith != null && i >= toReplace.length()
                    && new String(buf).contains(toReplace)) {
                // a replacement has been defined
                String newString = new String(buf).replaceAll(toReplace, replaceWith);
                out.write(newString.getBytes());
            } else {
                // no replacement needs to be done
                out.write(buf, 0, i);
            }
        }
        in.close();
        out.close();
        tmpFile.deleteOnExit();
        return tmpFile;
    }

    class SAXValidityErrorHandler implements ErrorHandler {

        private String atomTypeList;

        public SAXValidityErrorHandler(String atomTypeList) {
            this.atomTypeList = atomTypeList;
        }

        @Override
        public void error(SAXParseException arg0) throws SAXException {
            Assert.fail(atomTypeList + " is not valid on line " + arg0.getLineNumber() + ": " + arg0.getMessage());
        }

        @Override
        public void fatalError(SAXParseException arg0) throws SAXException {
            Assert.fail(atomTypeList + " is not valid on line " + arg0.getLineNumber() + ": " + arg0.getMessage());
        }

        @Override
        public void warning(SAXParseException arg0) throws SAXException {
            // warnings are fine
        }

    }

}
