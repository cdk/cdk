/* Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
 *                    2010  Stefan Kuhn <Stefan.Kuhn@ebi.ac.uk>
 *
 * Contact: cdk-devel@slists.sourceforge.net
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
package org.openscience.cdk.io;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.*;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.ISingleElectron;
import org.openscience.cdk.interfaces.ITetrahedralChirality;
import org.openscience.cdk.io.listener.PropertiesListener;
import org.openscience.cdk.sgroup.Sgroup;
import org.openscience.cdk.sgroup.SgroupKey;
import org.openscience.cdk.sgroup.SgroupType;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.templates.TestMoleculeFactory;
import org.openscience.cdk.test.io.ChemObjectIOTest;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.openscience.cdk.interfaces.IChemObject.AROMATIC;

/**
 * TestCase for the writer MDL mol files using one test file.
 *
 * @see org.openscience.cdk.io.MDLV2000Writer
 */
class MDLV2000WriterTest extends ChemObjectIOTest {

    private static IChemObjectBuilder builder;

    @BeforeAll
    static void setup() {
        builder = DefaultChemObjectBuilder.getInstance();
        setChemObjectIO(new MDLV2000Writer());
    }

    @Test
    void testAccepts() throws Exception {
        MDLV2000Writer reader = new MDLV2000Writer();
        Assertions.assertTrue(reader.accepts(ChemFile.class));
        Assertions.assertTrue(reader.accepts(ChemModel.class));
        Assertions.assertTrue(reader.accepts(IAtomContainer.class));
    }

    /**
     * @cdk.bug 890456
     * @cdk.bug 1524466
     */
    @Test
    void testBug890456() throws Exception {
        StringWriter   writer   = new StringWriter();
        IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        molecule.addAtom(new PseudoAtom("*"));
        molecule.addAtom(new Atom("C"));
        molecule.addAtom(new Atom("C"));

        MDLV2000Writer mdlWriter = new MDLV2000Writer(writer);
        mdlWriter.write(molecule);
        mdlWriter.close();
        Assertions.assertTrue(writer.toString().contains("M  END"));
    }

    /**
     * @cdk.bug 1212219
     */
    @Test
    void testBug1212219() throws Exception {
        StringWriter   writer   = new StringWriter();
        IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        Atom           atom     = new Atom("C");
        atom.setMassNumber(14);
        molecule.addAtom(atom);

        MDLV2000Writer mdlWriter = new MDLV2000Writer(writer);
        mdlWriter.write(molecule);
        mdlWriter.close();
        String output = writer.toString();
        //logger.debug("MDL output for testBug1212219: " + output);
        Assertions.assertTrue(output.contains("M  ISO  1   1  14"));
    }

    @Test
    void testWriteValence() throws Exception {
        StringWriter   writer   = new StringWriter();
        IAtomContainer molecule = TestMoleculeFactory.makeAlphaPinene();
        molecule.getAtom(0).setValency(1);
        molecule.getAtom(1).setValency(0);
        MDLV2000Writer mdlWriter      = new MDLV2000Writer(writer);
        Properties     customSettings = new Properties();
        customSettings.setProperty("WriteQueryFormatValencies", "true");
        mdlWriter.addChemObjectIOListener(new PropertiesListener(customSettings));
        mdlWriter.write(molecule);
        mdlWriter.close();
        String output = writer.toString();
        Assertions.assertTrue(output.contains("0  0  0  0  0  1  0  0  0  0  0  0"));
        Assertions.assertTrue(output.contains("0  0  0  0  0 15  0  0  0  0  0  0"));
    }

    @Test
    void nonDefaultValence_fe_iii() throws Exception {
        IAtomContainer container = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom          fe1       = new Atom("Fe");
        fe1.setImplicitHydrogenCount(3);
        container.addAtom(fe1);
        StringWriter   writer    = new StringWriter();
        MDLV2000Writer mdlWriter = new MDLV2000Writer(writer);
        mdlWriter.write(container);
        mdlWriter.close();
        String output = writer.toString();
        Assertions.assertTrue(output.contains("Fe  0  0  0  0  0  3  0  0  0  0  0  0"));
    }

    @Test
    void testWriteAtomAtomMapping() throws Exception {
        StringWriter   writer   = new StringWriter();
        IAtomContainer molecule = TestMoleculeFactory.makeAlphaPinene();
        molecule.getAtom(0).setProperty(CDKConstants.ATOM_ATOM_MAPPING, 1);
        molecule.getAtom(1).setProperty(CDKConstants.ATOM_ATOM_MAPPING, 15);
        MDLV2000Writer mdlWriter = new MDLV2000Writer(writer);
        mdlWriter.write(molecule);
        mdlWriter.close();
        String output = writer.toString();
        Assertions.assertTrue(output.contains("0  0  0  0  0  0  0  0  0  1  0  0"));
        Assertions.assertTrue(output.contains("0  0  0  0  0  0  0  0  0 15  0  0"));
    }

    /**
     * Tests if String atom atom mappings are parsed correctly
     */
    @Test
    void testWriteStringAtomAtomMapping() throws Exception {
        StringWriter   writer   = new StringWriter();
        IAtomContainer molecule = TestMoleculeFactory.makeAlphaPinene();
        molecule.getAtom(0).setProperty(CDKConstants.ATOM_ATOM_MAPPING, "1");
        molecule.getAtom(1).setProperty(CDKConstants.ATOM_ATOM_MAPPING, "15");
        MDLV2000Writer mdlWriter = new MDLV2000Writer(writer);
        mdlWriter.write(molecule);
        mdlWriter.close();
        String output = writer.toString();
        Assertions.assertTrue(output.contains("0  0  0  0  0  0  0  0  0  1  0  0"));
        Assertions.assertTrue(output.contains("0  0  0  0  0  0  0  0  0 15  0  0"));
    }

    /**
     * Tests if non-valid atom atom mappings are ignored by the reader.
     */
    @Test
    void testWriteInvalidAtomAtomMapping() throws Exception {
        StringWriter   writer   = new StringWriter();
        IAtomContainer molecule = TestMoleculeFactory.makeAlphaPinene();
        molecule.getAtom(0).setProperty(CDKConstants.ATOM_ATOM_MAPPING, "1a");
        molecule.getAtom(1).setProperty(CDKConstants.ATOM_ATOM_MAPPING, "15");
        MDLV2000Writer mdlWriter = new MDLV2000Writer(writer);
        mdlWriter.write(molecule);
        mdlWriter.close();
        String output = writer.toString();
        Pattern p = Pattern.compile(".*V2000.*    0.0000    0.0000    0.0000 C   0  0  0  0  0  0  0  0  0  0  "
                                    + "0  0.*    0.0000    0.0000    0.0000 C   0  0  0  0  0  0  0  0  0 15  0  0.*", Pattern.MULTILINE
                                                                                                                       | Pattern.DOTALL);
        Matcher m = p.matcher(output);
        Assertions.assertTrue(m.matches());
    }

    /**
     * Test for bug #1778479 "MDLWriter writes empty PseudoAtom label string".
     * When a molecule contains an IPseudoAtom without specifying the atom label
     * the MDLWriter generates invalid output as it prints the zero-length atom
     * label.
     * This was fixed with letting PseudoAtom have a default label of '*'.
     * <p>
     * Author: Andreas Schueller <a.schueller@chemie.uni-frankfurt.de>
     *
     * @cdk.bug 1778479
     */
    @Test
    void testBug1778479() throws Exception {
        StringWriter   writer   = new StringWriter();
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        IAtom          atom1    = builder.newInstance(IPseudoAtom.class);
        IAtom          atom2    = builder.newInstance(IAtom.class, "C");
        IBond          bond     = builder.newInstance(IBond.class, atom1, atom2);
        molecule.addAtom(atom1);
        molecule.addAtom(atom2);
        molecule.addBond(bond);

        MDLV2000Writer mdlWriter = new MDLV2000Writer(writer);
        mdlWriter.write(molecule);
        mdlWriter.close();
        String output = writer.toString();
        Assertions.assertEquals(-1, output.indexOf("0.0000    0.0000    0.0000     0  0  0  0  0  0  0  0  0  0  0  0"), "Test for zero length pseudo atom label in MDL file");
    }

    @Test
    void testNullFormalCharge() throws Exception {
        StringWriter   writer   = new StringWriter();
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        IAtom          atom     = builder.newInstance(IAtom.class, "C");
        atom.setFormalCharge(null);
        molecule.addAtom(atom);

        MDLV2000Writer mdlWriter = new MDLV2000Writer(writer);
        mdlWriter.write(molecule);
        mdlWriter.close();
        String output = writer.toString();
        // test ensures that the writer does not throw an exception on
        // null formal charges, so a mere assert on output being non-zero
        // length is enough
        Assertions.assertNotNull(output);
        Assertions.assertNotSame(0, output.length());
    }

    @Test
    void testPrefer3DCoordinateOutput() throws Exception {
        StringWriter   writer   = new StringWriter();
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        IAtom          atom     = builder.newInstance(IAtom.class, "C");
        atom.setPoint2d(new Point2d(1.0, 2.0));
        atom.setPoint3d(new Point3d(3.0, 4.0, 5.0));
        molecule.addAtom(atom);

        MDLV2000Writer mdlWriter = new MDLV2000Writer(writer);
        mdlWriter.write(molecule);
        mdlWriter.close();
        String output = writer.toString();
        // the current behavior is that if both 2D and 3D coordinates
        // are available, the 3D is outputted, and the 2D not
        Assertions.assertTrue(output.contains("3.0"));
        Assertions.assertTrue(output.contains("4.0"));
        Assertions.assertTrue(output.contains("5.0"));
    }

    @Test
    void testForce2DCoordinates() throws Exception {
        StringWriter   writer   = new StringWriter();
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        IAtom          atom     = builder.newInstance(IAtom.class, "C");
        atom.setPoint2d(new Point2d(1.0, 2.0));
        atom.setPoint3d(new Point3d(3.0, 4.0, 5.0));
        molecule.addAtom(atom);

        MDLV2000Writer mdlWriter = new MDLV2000Writer(writer);
        Properties     prop      = new Properties();
        prop.setProperty("ForceWriteAs2DCoordinates", "true");
        PropertiesListener listener = new PropertiesListener(prop);
        mdlWriter.addChemObjectIOListener(listener);
        mdlWriter.customizeJob();
        mdlWriter.write(molecule);
        mdlWriter.close();
        String output = writer.toString();
        // the current behavior is that if both 2D and 3D coordinates
        // are available, the 3D is outputted, and the 2D not
        Assertions.assertTrue(output.contains("1.0"));
        Assertions.assertTrue(output.contains("2.0"));
    }

    @Test
    void testUndefinedStereo() throws Exception {
        IAtomContainer mol = TestMoleculeFactory.makeAlphaPinene();
        mol.getBond(0).setStereo(IBond.Stereo.UP_OR_DOWN);
        mol.getBond(1).setStereo(IBond.Stereo.E_OR_Z);
        StringWriter   writer    = new StringWriter();
        MDLV2000Writer mdlWriter = new MDLV2000Writer(writer);
        mdlWriter.write(mol);
        mdlWriter.close();
        String output = writer.toString();
        Assertions.assertTrue(output.contains("1  2  2  4  0  0  0"));
        Assertions.assertTrue(output.contains("2  3  1  3  0  0  0"));
    }

    @Test
    void testUnsupportedBondOrder() throws Exception {
        IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        molecule.addAtom(new Atom("C"));
        molecule.addAtom(new Atom("C"));
        molecule.addBond(new Bond(molecule.getAtom(0), molecule.getAtom(1), Order.QUADRUPLE));
        MDLV2000Writer mdlWriter = new MDLV2000Writer(new StringWriter());
        Assertions.assertThrows(CDKException.class,
                                () -> {
                                    mdlWriter.write(molecule);
                                });
        mdlWriter.close();
    }

    @Test
    void testTwoFragmentsWithTitle() throws Exception {
        IAtomContainer mol1 = TestMoleculeFactory.makeAlphaPinene();
        mol1.setTitle("title1");
        IAtomContainer mol2 = TestMoleculeFactory.makeAlphaPinene();
        mol2.setTitle("title2");
        IChemModel model = mol1.getBuilder().newInstance(IChemModel.class);
        model.setMoleculeSet(mol1.getBuilder().newInstance(IAtomContainerSet.class));
        model.getMoleculeSet().addAtomContainer(mol1);
        model.getMoleculeSet().addAtomContainer(mol2);
        StringWriter   writer    = new StringWriter();
        MDLV2000Writer mdlWriter = new MDLV2000Writer(writer);
        mdlWriter.write(model);
        mdlWriter.close();
        String output = writer.toString();
        Assertions.assertTrue(output.contains("title1; title2"));
    }

    /**
     * Test correct output of R-groups, using the hash (#) and a separate RGP line.
     */
    @Test
    void testRGPLine() throws Exception {
        StringWriter   writer   = new StringWriter();
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        IPseudoAtom    atom1    = builder.newInstance(IPseudoAtom.class);
        atom1.setSymbol("R");
        atom1.setLabel("R12");

        IAtom atom2 = builder.newInstance(IAtom.class, "C");
        IBond bond  = builder.newInstance(IBond.class, atom1, atom2);

        IPseudoAtom atom3 = builder.newInstance(IPseudoAtom.class);
        atom3.setSymbol("A");
        atom3.setLabel("A");
        IBond bond2 = builder.newInstance(IBond.class, atom3, atom2);

        molecule.addAtom(atom1);
        molecule.addAtom(atom2);
        molecule.addAtom(atom3);
        molecule.addBond(bond);
        molecule.addBond(bond2);

        MDLV2000Writer mdlWriter = new MDLV2000Writer(writer);
        mdlWriter.write(molecule);
        mdlWriter.close();
        String output = writer.toString();

        Assertions.assertTrue(output.contains("R#"), "Test for R#");
        Assertions.assertTrue(output.contains("M  RGP  1   1  12"), "Test for RGP line");
    }

    /**
     * Test writing of comments made on individual atoms into an Atom Value lines.
     */
    @Test
    void testAtomValueLine() throws Exception {
        IAtom carbon = builder.newInstance(IAtom.class, "C");
        carbon.setProperty(CDKConstants.COMMENT, "Carbon comment");
        IAtom oxygen = builder.newInstance(IAtom.class, "O");
        oxygen.setProperty(CDKConstants.COMMENT, "Oxygen comment");
        IBond bond = builder.newInstance(IBond.class, carbon, oxygen, Order.DOUBLE);

        IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        molecule.addAtom(oxygen);
        molecule.addAtom(carbon);
        molecule.addBond(bond);

        StringWriter   writer    = new StringWriter();
        MDLV2000Writer mdlWriter = new MDLV2000Writer(writer);
        mdlWriter.write(molecule);
        mdlWriter.close();

        Assertions.assertTrue(writer.toString().contains("V    1 Oxygen comment"));
        Assertions.assertTrue(writer.toString().contains("V    2 Carbon comment"));

    }

    /**
     * Test option to write aromatic bonds with bond type "4".
     * Please note: bond type values 4 through 8 are for SSS queries only.
     *
     * @throws Exception
     */
    @Test
    void testAromaticBondType4() throws Exception {

        IAtomContainer benzene = TestMoleculeFactory.makeBenzene();
        for (IAtom atom : benzene.atoms()) {
            atom.setFlag(AROMATIC, true);
        }
        for (IBond bond : benzene.bonds()) {
            bond.setFlag(AROMATIC, true);
        }

        StringWriter   writer    = new StringWriter();
        MDLV2000Writer mdlWriter = new MDLV2000Writer(writer);
        mdlWriter.write(benzene);
        mdlWriter.close();
        Assertions.assertTrue(writer.toString().contains("1  2  1  0  0  0  0"));

        writer = new StringWriter();
        mdlWriter = new MDLV2000Writer(writer);
        Properties prop = new Properties();
        prop.setProperty("WriteAromaticBondTypes", "true");
        PropertiesListener listener = new PropertiesListener(prop);
        mdlWriter.addChemObjectIOListener(listener);
        mdlWriter.customizeJob();
        mdlWriter.write(benzene);
        mdlWriter.close();
        Assertions.assertTrue(writer.toString().contains("1  2  4  0  0  0  0"));
    }

    @Test
    void testAtomParity() throws CDKException, IOException {

        InputStream    in       = getClass().getResourceAsStream("mol_testAtomParity.mol");
        MDLV2000Reader reader   = new MDLV2000Reader(in);
        IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        molecule = reader.read(molecule);
        reader.close();

        StringWriter   sw     = new StringWriter();
        MDLV2000Writer writer = new MDLV2000Writer(sw);
        writer.write(molecule);
        writer.close();

        Assertions.assertTrue(sw.toString().contains(
            "   -1.1749    0.1436    0.0000 C   0  0  1  0  0  0  0  0  0  0  0  0"));

    }

    @Test
    void testWritePseudoAtoms() throws Exception {
        InputStream    in       = getClass().getResourceAsStream("pseudoatoms.sdf");
        MDLV2000Reader reader   = new MDLV2000Reader(in);
        IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        molecule = reader.read(molecule);
        reader.close();

        StringWriter   writer  = new StringWriter();
        MDLV2000Writer mwriter = new MDLV2000Writer(writer);
        mwriter.write(molecule);
        mwriter.close();

        String output = writer.toString();
        Assertions.assertTrue(output.contains("Gln"));
        Assertions.assertTrue(output.contains("Leu"));
    }

    /**
     * @throws Exception
     * @cdk.bug 1263
     */
    @Test
    void testWritePseudoAtoms_LongLabel() throws Exception {

        IChemObjectBuilder builder   = DefaultChemObjectBuilder.getInstance();
        IAtomContainer     container = builder.newInstance(IAtomContainer.class);

        IAtom c1   = builder.newInstance(IAtom.class, "C");
        IAtom tRNA = builder.newInstance(IPseudoAtom.class, "tRNA");

        container.addAtom(c1);
        container.addAtom(tRNA);

        StringWriter   sw     = new StringWriter();
        MDLV2000Writer writer = new MDLV2000Writer(sw);
        writer.write(container);
        writer.close();

        String output = sw.toString();

        Assertions.assertTrue(output.contains("A    2"));
        Assertions.assertTrue(output.contains("tRNA"));

    }

    /**
     * Checks that null atom labels are handled correctly.
     */
    @Test
    void testWritePseudoAtoms_nullLabel() throws Exception {

        IChemObjectBuilder builder   = DefaultChemObjectBuilder.getInstance();
        IAtomContainer     container = builder.newInstance(IAtomContainer.class);

        IAtom       c1       = builder.newInstance(IAtom.class, "C");
        IPseudoAtom nullAtom = builder.newInstance(IPseudoAtom.class, "");
        nullAtom.setLabel(null);

        container.addAtom(c1);
        container.addAtom(nullAtom);

        StringWriter   sw     = new StringWriter();
        MDLV2000Writer writer = new MDLV2000Writer(sw);
        writer.write(container);
        writer.close();

        String output = sw.toString();
        Assertions.assertTrue(output.contains("R"));

    }

    /**
     * When there are more then 16 R Groups these should be wrapped
     *
     * @throws Exception
     */
    @Test
    void testRGPLine_Multiline() throws Exception {

        IChemObjectBuilder builder   = DefaultChemObjectBuilder.getInstance();
        IAtomContainer     container = builder.newInstance(IAtomContainer.class);

        for (int i = 1; i < 20; i++)
            container.addAtom(builder.newInstance(IPseudoAtom.class, "R" + i));

        StringWriter   sw     = new StringWriter();
        MDLV2000Writer writer = new MDLV2000Writer(sw);
        writer.write(container);
        writer.close();

        String output = sw.toString();
        Assertions.assertTrue(output.contains("M  RGP  8   1   1   2   2   3   3   4   4   5   5   6   6   7   7   8   8"));
        Assertions.assertTrue(output.contains("M  RGP  8   9   9  10  10  11  11  12  12  13  13  14  14  15  15  16  16"));
        Assertions.assertTrue(output.contains("M  RGP  3  17  17  18  18  19  19"));

    }

    @Test
    void testAlias_TruncatedLabel() throws Exception {

        IChemObjectBuilder builder   = DefaultChemObjectBuilder.getInstance();
        IAtomContainer     container = builder.newInstance(IAtomContainer.class);

        String label = "This is a very long label - almost too long. it should be cut here -> and the rest is truncated";

        container.addAtom(builder.newInstance(IPseudoAtom.class, label));

        StringWriter   sw     = new StringWriter();
        MDLV2000Writer writer = new MDLV2000Writer(sw);
        writer.write(container);
        writer.close();

        String output = sw.toString();

        Assertions.assertTrue(output.contains("This is a very long label - almost too long. it should be cut here ->"));
        // make sure the full label wasn't output
        Assertions.assertFalse(output.contains(label));

    }

    @Test
    void testSingleSingletRadical() throws Exception {

        InputStream    in       = getClass().getResourceAsStream("singleSingletRadical.mol");
        MDLV2000Reader reader   = new MDLV2000Reader(in);
        IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        molecule = reader.read(molecule);
        reader.close();

        StringWriter   sw     = new StringWriter();
        MDLV2000Writer writer = new MDLV2000Writer(sw);
        writer.write(molecule);
        writer.close();

        String[] lines = sw.toString().split("\n");

        assertThat("incorrect file length", lines.length, is(9));
        assertThat("incorrect radical output", lines[7], is("M  RAD  1   2   1"));
    }

    @Test
    void testSingleDoubletRadical() throws Exception {

        InputStream    in       = getClass().getResourceAsStream("singleDoubletRadical.mol");
        MDLV2000Reader reader   = new MDLV2000Reader(in);
        IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        molecule = reader.read(molecule);
        reader.close();

        StringWriter   sw     = new StringWriter();
        MDLV2000Writer writer = new MDLV2000Writer(sw);
        writer.write(molecule);
        writer.close();

        String[] lines = sw.toString().split("\n");

        assertThat("incorrect file length", lines.length, is(9));
        assertThat("incorrect radical output", lines[7], is("M  RAD  1   2   2"));

    }

    @Test
    void testSingleTripletRadical() throws Exception {

        InputStream    in       = getClass().getResourceAsStream("singleTripletRadical.mol");
        MDLV2000Reader reader   = new MDLV2000Reader(in);
        IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        molecule = reader.read(molecule);
        reader.close();

        StringWriter   sw     = new StringWriter();
        MDLV2000Writer writer = new MDLV2000Writer(sw);
        writer.write(molecule);
        writer.close();

        String[] lines = sw.toString().split("\n");

        assertThat("incorrect file length", lines.length, is(9));
        assertThat("incorrect radical output", lines[7], is("M  RAD  1   2   3"));
    }

    @Test
    void testMultipleRadicals() throws Exception {

        InputStream    in       = getClass().getResourceAsStream("multipleRadicals.mol");
        MDLV2000Reader reader   = new MDLV2000Reader(in);
        IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        molecule = reader.read(molecule);
        reader.close();

        StringWriter   sw     = new StringWriter();
        MDLV2000Writer writer = new MDLV2000Writer(sw);
        writer.write(molecule);
        writer.close();

        String[] lines = sw.toString().split("\n");

        assertThat("incorrect file length", lines.length, is(24));
        assertThat("incorrect radical output on line 22", lines[21],
                   is("M  RAD  8   1   2   2   2   3   2   4   2   5   2   6   2   7   2   8   2"));
        assertThat("incorrect radical output on line 23", lines[22], is("M  RAD  1   9   2"));

    }

    @Test
    void testSgroupAtomListWrapping() throws Exception {
        IAtomContainer mol = TestMoleculeFactory.makeEthylPropylPhenantren();

        Sgroup sgroup = new Sgroup();
        for (IAtom atom : mol.atoms())
            sgroup.addAtom(atom);
        mol.setProperty(CDKConstants.CTAB_SGROUPS,
                        Collections.singletonList(sgroup));

        StringWriter sw = new StringWriter();
        try (MDLV2000Writer mdlw = new MDLV2000Writer(sw)) {
            mdlw.write(mol);
            String output = sw.toString();
            assertThat(output, containsString("M  SAL   1 15"));
            assertThat(output, containsString("M  SAL   1  4"));
        }
    }

    @Test
    void sgroupRepeatUnitRoundTrip() throws Exception {
        StringWriter sw = new StringWriter();
        try (MDLV2000Reader mdlr = new MDLV2000Reader(getClass().getResourceAsStream("sgroup-sru.mol"));
             MDLV2000Writer mdlw = new MDLV2000Writer(sw)) {
            mdlw.write(mdlr.read(DefaultChemObjectBuilder.getInstance().newAtomContainer()));
            String output = sw.toString();
            assertThat(output, containsString("M  STY  1   1 SRU"));
            assertThat(output, containsString("M  SMT   1 n"));
            assertThat(output, containsString("M  SCN  1   1 HT"));
        }
    }

    @Test
    void sgroupBracketStylesRoundTrip() throws Exception {
        StringWriter sw = new StringWriter();
        try (MDLV2000Reader mdlr = new MDLV2000Reader(getClass().getResourceAsStream("sgroup-sru-bracketstyles.mol"));
             MDLV2000Writer mdlw = new MDLV2000Writer(sw)) {
            mdlw.write(mdlr.read(DefaultChemObjectBuilder.getInstance().newAtomContainer()));
            String output = sw.toString();
            assertThat(output, containsString("M  STY  2   1 SRU   2 SRU"));
            assertThat(output, containsString("M  SBT  1   1   1"));
        }
    }

    @Test
    void sgroupUnorderedMixtureRoundTrip() throws Exception {
        StringWriter sw = new StringWriter();
        try (MDLV2000Reader mdlr = new MDLV2000Reader(getClass().getResourceAsStream("sgroup-unord-mixture.mol"));
             MDLV2000Writer mdlw = new MDLV2000Writer(sw)) {
            mdlw.write(mdlr.read(DefaultChemObjectBuilder.getInstance().newAtomContainer()));
            String output = sw.toString();
            assertThat(output, containsString("M  STY  3   1 COM   2 COM   3 MIX"));
            assertThat(output, containsString("M  SPL  2   1   3   2   3"));
        }
    }

    @Test
    void sgroupCopolymerRoundTrip() throws Exception {
        StringWriter sw = new StringWriter();
        try (MDLV2000Reader mdlr = new MDLV2000Reader(getClass().getResourceAsStream("sgroup-ran-copolymer.mol"));
             MDLV2000Writer mdlw = new MDLV2000Writer(sw)) {
            mdlw.write(mdlr.read(DefaultChemObjectBuilder.getInstance().newAtomContainer()));
            String output = sw.toString();
            assertThat(output, containsString("M  SST  1   1 RAN"));
            assertThat(output, containsString("M  STY  3   1 COP   2 SRU   3 SRU"));
        }
    }

    @Test
    void sgroupExpandedAbbreviationRoundTrip() throws Exception {
        StringWriter sw = new StringWriter();
        try (MDLV2000Reader mdlr = new MDLV2000Reader(getClass().getResourceAsStream("triphenyl-phosphate-expanded.mol"));
             MDLV2000Writer mdlw = new MDLV2000Writer(sw)) {
            mdlw.write(mdlr.read(DefaultChemObjectBuilder.getInstance().newAtomContainer()));
            String output = sw.toString();
            assertThat(output, containsString("M  STY  3   1 SUP   2 SUP   3 SUP\n"));
            assertThat(output, containsString("M  SDS EXP  1   1"));
        }
    }

    @Test
    void sgroupParentAtomListRoundTrip() throws Exception {
        StringWriter sw = new StringWriter();
        try (MDLV2000Reader mdlr = new MDLV2000Reader(getClass().getResourceAsStream("ChEBI_81539.mol"));
             MDLV2000Writer mdlw = new MDLV2000Writer(sw)) {
            mdlw.write(mdlr.read(DefaultChemObjectBuilder.getInstance().newAtomContainer()));
            String output = sw.toString();
            assertThat(output, containsString("M  STY  5   1 MUL   2 SRU"));
            assertThat(output, containsString("M  SPA   1 12"));
        }
    }

    @Test
    void sgroupOrderedMixtureRoundTrip() throws Exception {
        StringWriter sw = new StringWriter();
        try (MDLV2000Reader mdlr = new MDLV2000Reader(getClass().getResourceAsStream("sgroup-ord-mixture.mol"));
             MDLV2000Writer mdlw = new MDLV2000Writer(sw)) {
            mdlw.write(mdlr.read(DefaultChemObjectBuilder.getInstance().newAtomContainer()));
            String output = sw.toString();
            assertThat(output, containsString("M  STY  3   1 COM   2 COM   3 FOR"));
            assertThat(output, containsString("M  SNC  1   1   1"));
            assertThat(output, containsString("M  SNC  1   2   2"));
        }
    }

    @Test
    void roundtripAtomParityExpH() throws Exception {
        StringWriter sw = new StringWriter();
        try (MDLV2000Reader mdlr = new MDLV2000Reader(getClass().getResourceAsStream("tetrahedral-parity-withExpH.mol"));
             MDLV2000Writer mdlw = new MDLV2000Writer(sw)) {
            mdlw.write(mdlr.read(DefaultChemObjectBuilder.getInstance().newAtomContainer()));
            String output = sw.toString();
            assertThat(output, containsString("    0.0000    0.0000    0.0000 C   0  0  1  0  0  0  0  0  0  0  0  0\n"));
        }
    }

    @Test
    void roundtripAtomParityImplH() throws Exception {
        StringWriter sw = new StringWriter();
        try (MDLV2000Reader mdlr = new MDLV2000Reader(getClass().getResourceAsStream("tetrahedral-parity-withImplH.mol"));
             MDLV2000Writer mdlw = new MDLV2000Writer(sw)) {
            mdlw.write(mdlr.read(DefaultChemObjectBuilder.getInstance().newAtomContainer()));
            String output = sw.toString();
            assertThat(output, containsString("    0.0000    0.0000    0.0000 C   0  0  1  0  0  0  0  0  0  0  0  0\n"));
        }
    }

    @Test
    void roundtripAtomParityImplModified() throws Exception {
        StringWriter sw = new StringWriter();
        try (MDLV2000Reader mdlr = new MDLV2000Reader(getClass().getResourceAsStream("tetrahedral-parity-withImplH.mol"));
             MDLV2000Writer mdlw = new MDLV2000Writer(sw)) {
            IAtomContainer mol = mdlr.read(DefaultChemObjectBuilder.getInstance().newAtomContainer());
            ITetrahedralChirality tc  = (ITetrahedralChirality) mol.stereoElements().iterator().next();
            tc.setStereo(tc.getStereo().invert());
            mdlw.write(mol);
            String output = sw.toString();
            assertThat(output, containsString("    0.0000    0.0000    0.0000 C   0  0  2  0  0  0  0  0  0  0  0  0\n"));
        }
    }

    @Test
    void aromaticBondTypes() throws Exception {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        IBond bond = builder.newInstance(IBond.class, mol.getAtom(0), mol.getAtom(1), Order.UNSET);
        bond.setIsAromatic(true);
        mol.addBond(bond);
        StringWriter sw = new StringWriter();
        Assertions.assertThrows(CDKException.class,
                                () -> {
                                    try (MDLV2000Writer mdlw = new MDLV2000Writer(sw)) {
                                        mdlw.write(mol);
                                    }
                                });
    }

    @Test
    void aromaticBondTypesEnabled() throws Exception {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        IBond bond = builder.newInstance(IBond.class, mol.getAtom(0), mol.getAtom(1), Order.UNSET);
        bond.setIsAromatic(true);
        mol.addBond(bond);
        StringWriter sw = new StringWriter();
        try (MDLV2000Writer mdlw = new MDLV2000Writer(sw)) {
            mdlw.setWriteAromaticBondTypes(true);
            mdlw.write(mol);
        }
        assertThat(sw.toString(), containsString("  1  2  4  0  0  0  0\n"));
    }

    @Test
    void writeDimensionField() throws Exception {
        IAtomContainer mol  = builder.newAtomContainer();
        IAtom          atom = builder.newAtom();
        atom.setSymbol("C");
        atom.setImplicitHydrogenCount(4);
        atom.setPoint2d(new Point2d(0.5, 0.5));
        mol.addAtom(atom);
        StringWriter sw = new StringWriter();
        try (MDLV2000Writer mdlw = new MDLV2000Writer(sw)) {
            mdlw.write(mol);
        }
        assertThat(sw.toString(), containsString("2D"));
    }

    @Test
    void writeDimensionField3D() throws Exception {
        IAtomContainer mol  = builder.newAtomContainer();
        IAtom          atom = builder.newAtom();
        atom.setSymbol("C");
        atom.setImplicitHydrogenCount(4);
        atom.setPoint3d(new Point3d(0.5, 0.5, 0.1));
        mol.addAtom(atom);
        StringWriter sw = new StringWriter();
        try (MDLV2000Writer mdlw = new MDLV2000Writer(sw)) {
            mdlw.write(mol);
        }
        assertThat(sw.toString(), containsString("3D"));
    }

    @Test
    void writeMoreThan8Radicals() throws Exception {
        IAtomContainer mol = builder.newAtomContainer();
        for (int i = 0; i < 20; i++) {
            IAtom atom = builder.newAtom();
            atom.setSymbol("C");
            mol.addAtom(atom);
            mol.addSingleElectron(builder.newInstance(ISingleElectron.class, atom));
        }
        StringWriter sw = new StringWriter();
        try (MDLV2000Writer mdlw = new MDLV2000Writer(sw)) {
            mdlw.write(mol);
        }
        assertThat(sw.toString(),
                   containsString("M  RAD  8   9   2  10   2  11   2  12   2  13   2  14   2  15   2  16   2"));
    }

    @Test
    void writeCarbon12() throws Exception {
        IAtomContainer mol  = builder.newAtomContainer();
        IAtom          atom = builder.newAtom();
        atom.setSymbol("C");
        atom.setMassNumber(12);
        mol.addAtom(atom);
        StringWriter sw = new StringWriter();
        try (MDLV2000Writer mdlw = new MDLV2000Writer(sw)) {
            mdlw.write(mol);
        }
        assertThat(sw.toString(),
                   containsString("M  ISO  1   1  12"));
    }

    @Test
    void ignoreCarbon12() throws Exception {
        IAtomContainer mol  = builder.newAtomContainer();
        IAtom          atom = builder.newAtom();
        atom.setSymbol("C");
        atom.setMassNumber(12);
        mol.addAtom(atom);
        StringWriter sw = new StringWriter();
        try (MDLV2000Writer mdlw = new MDLV2000Writer(sw)) {
            mdlw.getSetting(MDLV2000Writer.OptWriteMajorIsotopes)
                .setSetting("false");
            mdlw.write(mol);
        }
        assertThat(sw.toString(),
                   not(containsString("M  ISO  1   1  12")));
    }

    @Test
    void writeCarbon13AtomProps() throws Exception {
        IAtomContainer mol  = builder.newAtomContainer();
        IAtom          atom = builder.newAtom();
        atom.setSymbol("C");
        atom.setMassNumber(13);
        mol.addAtom(atom);
        StringWriter sw = new StringWriter();
        try (MDLV2000Writer mdlw = new MDLV2000Writer(sw)) {
            mdlw.write(mol);
        }
        assertThat(sw.toString(),
                   containsString("C   1"));
    }

    @Test
    void writeChargeAtomProps() throws Exception {
        IAtomContainer mol  = builder.newAtomContainer();
        IAtom          atom = builder.newAtom();
        atom.setSymbol("C");
        atom.setFormalCharge(+1);
        mol.addAtom(atom);
        StringWriter sw = new StringWriter();
        try (MDLV2000Writer mdlw = new MDLV2000Writer(sw)) {
            mdlw.write(mol);
        }
        assertThat(sw.toString(),
                   containsString("C   0  3"));
    }

    @Test
    void skipDefaultProps() throws Exception {
        StringWriter sw = new StringWriter();
        try (MDLV2000Reader mdlr = new MDLV2000Reader(getClass().getResourceAsStream("tetrahedral-parity-withImplH.mol"));
             MDLV2000Writer mdlw = new MDLV2000Writer(sw)) {
            mdlw.getSetting(MDLV2000Writer.OptWriteDefaultProperties)
                .setSetting("false");
            mdlw.write(mdlr.read(DefaultChemObjectBuilder.getInstance().newAtomContainer()));
            String output = sw.toString();
            assertThat(output, containsString("\n"
                                              + "  5  4  0  0  0  0  0  0  0  0999 V2000\n"
                                              + "    0.0000    0.0000    0.0000 C   0  0  1  0  0  0\n"
                                              + "    0.0000    0.0000    0.0000 C   0  0\n"
                                              + "    0.0000    0.0000    0.0000 C   0  0\n"
                                              + "    0.0000    0.0000    0.0000 O   0  0\n"
                                              + "    0.0000    0.0000    0.0000 C   0  0\n"
                                              + "  1  2  1  0\n"
                                              + "  2  3  1  0\n"
                                              + "  1  4  1  0\n"
                                              + "  1  5  1  0\n"
                                              + "M  END"));
        }
    }

    @Test
    void writeParentAtomSgroupAsList() throws Exception{
        IAtomContainer mol  = builder.newAtomContainer();
        IAtom          atom = builder.newAtom();
        atom.setSymbol("C");
        mol.addAtom(atom);
        // build multiple group Sgroup
        Sgroup sgroup = new Sgroup();
        sgroup.setType(SgroupType.CtabMultipleGroup);
        sgroup.addAtom(atom);
        List<IAtom> patoms = new ArrayList<>();

            patoms.add(atom);

        sgroup.putValue(SgroupKey.CtabParentAtomList, patoms);
        mol.setProperty(CDKConstants.CTAB_SGROUPS,
                Collections.singletonList(sgroup));
        StringWriter sw = new StringWriter();
        try (MDLV2000Writer mdlw = new MDLV2000Writer(sw)) {
            mdlw.write(mol);
        }
        assertThat(sw.toString(), containsString("SPA   1  1"));

    }

    @Test
    void roundTripWithNotAtomList() throws Exception {
        try (InputStream in = getClass().getResourceAsStream("query_notatomlist.mol");
             MDLV2000Reader mdlr = new MDLV2000Reader(in)) {

            IAtomContainer mol = mdlr.read(SilentChemObjectBuilder.getInstance().newAtomContainer());

            StringWriter sw = new StringWriter();
            try (MDLV2000Writer mdlw = new MDLV2000Writer(sw)) {
                mdlw.write(mol);
            }
            String writtenMol = sw.toString();
            assertThat(writtenMol, containsString(
                    "  1 T    3   9   7   8\n" +
                    "M  ALS   1  3 T F   N   O"));
        }
    }
    @Test
    void roundTripWithAtomList() throws Exception {
        try (InputStream in = getClass().getResourceAsStream("query_atomlist.mol");
             MDLV2000Reader mdlr = new MDLV2000Reader(in)) {

            IAtomContainer mol = mdlr.read(SilentChemObjectBuilder.getInstance().newAtomContainer());

            StringWriter sw = new StringWriter();
            try (MDLV2000Writer mdlw = new MDLV2000Writer(sw)) {
                mdlw.write(mol);
            }
            String writtenMol = sw.toString();

            assertThat(writtenMol, containsString(
                    "  1 F    3   9   7   8\n"+
                    "M  ALS   1  3 F F   N   O"));
        }
    }
    @Test
    void roundTripWithMultipleLegacyAtomLists() throws Exception {
        try (InputStream in = getClass().getResourceAsStream("query_manylegacyatomlist.mol");
             MDLV2000Reader mdlr = new MDLV2000Reader(in)) {

            IAtomContainer mol = mdlr.read(SilentChemObjectBuilder.getInstance().newAtomContainer());

            StringWriter sw = new StringWriter();
            try (MDLV2000Writer mdlw = new MDLV2000Writer(sw)) {
                mdlw.write(mol);
            }
            String writtenMol = sw.toString();

            assertThat(writtenMol, containsString(
                            "  4 F    2   8   7\n" +
                            "  5 F    2   7   8\n" +
                            "  6 F    2   7   8\n"+
                            "M  ALS   4  2 F O   N   \n" +
                            "M  ALS   5  2 F N   O   \n" +
                            "M  ALS   6  2 F N   O"));
        }
    }

    @Test
    void dataSgroupRoundTrip() {
      String path = "hbr_acoh_mix.mol";
      try (InputStream in = getClass().getResourceAsStream(path)) {
        MDLV2000Reader     mdlr    = new MDLV2000Reader(in);
        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
        IAtomContainer     mol     = mdlr.read(builder.newAtomContainer());
        try (StringWriter sw = new StringWriter();
             MDLV2000Writer writer = new MDLV2000Writer(sw)) {
          writer.write(mol);
          String output = sw.toString();
          assertThat(output,
                     CoreMatchers.containsString("M  SDT   3 WEIGHT_PERCENT                N %"));
          assertThat(output,
                     CoreMatchers.containsString("M  SED   3 33%"));
        }
      } catch (IOException | CDKException e) {
        Assertions.fail(e.getMessage());
      }
    }

    @Test
    void testNoChiralFlag() throws Exception {
        final String input = "\n" +
                "  Mrv1810 02052112282D          \n" +
                "\n" +
                "  7  7  0  0  0  0            999 V2000\n" +
                "   -1.1468    6.5972    0.0000 C   0  0  2  0  0  0  0  0  0  0  0  0\n" +
                "   -1.8613    6.1847    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "   -1.8613    5.3597    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "   -1.1468    4.9472    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "   -0.4323    5.3597    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "   -0.4323    6.1847    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "   -1.1468    7.4222    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "  1  2  1  0  0  0  0\n" +
                "  2  3  1  0  0  0  0\n" +
                "  3  4  1  0  0  0  0\n" +
                "  4  5  1  0  0  0  0\n" +
                "  5  6  1  0  0  0  0\n" +
                "  1  6  1  0  0  0  0\n" +
                "  1  7  1  1  0  0  0\n" +
                "M  END\n";
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        StringWriter sw = new StringWriter();
        try (MDLV2000Reader mdlr = new MDLV2000Reader(new StringReader(input));
             MDLV2000Writer mdlw = new MDLV2000Writer(sw)) {
            mdlw.write(mdlr.read(bldr.newAtomContainer()));
        }
        assertThat(sw.toString(), containsString("  7  7  0  0  0  0"));
    }

    @Test
    void testChiralFlag() throws Exception {
        final String input = "\n" +
                "  Mrv1810 02052112282D          \n" +
                "\n" +
                "  7  7  0  0  1  0            999 V2000\n" +
                "   -1.1468    6.5972    0.0000 C   0  0  2  0  0  0  0  0  0  0  0  0\n" +
                "   -1.8613    6.1847    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "   -1.8613    5.3597    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "   -1.1468    4.9472    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "   -0.4323    5.3597    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "   -0.4323    6.1847    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "   -1.1468    7.4222    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "  1  2  1  0  0  0  0\n" +
                "  2  3  1  0  0  0  0\n" +
                "  3  4  1  0  0  0  0\n" +
                "  4  5  1  0  0  0  0\n" +
                "  5  6  1  0  0  0  0\n" +
                "  1  6  1  0  0  0  0\n" +
                "  1  7  1  1  0  0  0\n" +
                "M  END\n";
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        StringWriter sw = new StringWriter();
        try (MDLV2000Reader mdlr = new MDLV2000Reader(new StringReader(input));
             MDLV2000Writer mdlw = new MDLV2000Writer(sw)) {
            mdlw.write(mdlr.read(bldr.newAtomContainer()));
        }
        assertThat(sw.toString(), containsString("  7  7  0  0  1  0"));
    }
}
