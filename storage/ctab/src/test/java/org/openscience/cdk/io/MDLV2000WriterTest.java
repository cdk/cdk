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

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.PseudoAtom;
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
import org.openscience.cdk.templates.TestMoleculeFactory;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.openscience.cdk.CDKConstants.ISAROMATIC;

/**
 * TestCase for the writer MDL mol files using one test file.
 *
 * @cdk.module test-io
 * @see org.openscience.cdk.io.MDLV2000Writer
 */
public class MDLV2000WriterTest extends ChemObjectIOTest {

    private static IChemObjectBuilder builder;

    @BeforeClass
    public static void setup() {
        builder = DefaultChemObjectBuilder.getInstance();
        setChemObjectIO(new MDLV2000Writer());
    }

    @Test
    public void testAccepts() throws Exception {
        MDLV2000Writer reader = new MDLV2000Writer();
        Assert.assertTrue(reader.accepts(ChemFile.class));
        Assert.assertTrue(reader.accepts(ChemModel.class));
        Assert.assertTrue(reader.accepts(AtomContainer.class));
    }

    /**
     * @cdk.bug 890456
     * @cdk.bug 1524466
     */
    @Test
    public void testBug890456() throws Exception {
        StringWriter   writer   = new StringWriter();
        IAtomContainer molecule = new AtomContainer();
        molecule.addAtom(new PseudoAtom("*"));
        molecule.addAtom(new Atom("C"));
        molecule.addAtom(new Atom("C"));

        MDLV2000Writer mdlWriter = new MDLV2000Writer(writer);
        mdlWriter.write(molecule);
        mdlWriter.close();
        Assert.assertTrue(writer.toString().indexOf("M  END") != -1);
    }

    /**
     * @cdk.bug 1212219
     */
    @Test
    public void testBug1212219() throws Exception {
        StringWriter   writer   = new StringWriter();
        IAtomContainer molecule = new AtomContainer();
        Atom           atom     = new Atom("C");
        atom.setMassNumber(14);
        molecule.addAtom(atom);

        MDLV2000Writer mdlWriter = new MDLV2000Writer(writer);
        mdlWriter.write(molecule);
        mdlWriter.close();
        String output = writer.toString();
        //logger.debug("MDL output for testBug1212219: " + output);
        Assert.assertTrue(output.indexOf("M  ISO  1   1  14") != -1);
    }

    @Test
    public void testWriteValence() throws Exception {
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
        Assert.assertTrue(output.indexOf("0  0  0  0  0  1  0  0  0  0  0  0") != -1);
        Assert.assertTrue(output.indexOf("0  0  0  0  0 15  0  0  0  0  0  0") != -1);
    }

    @Test
    public void nonDefaultValence_fe_iii() throws Exception {
        IAtomContainer container = new AtomContainer();
        IAtom          fe1       = new Atom("Fe");
        fe1.setImplicitHydrogenCount(3);
        container.addAtom(fe1);
        StringWriter   writer    = new StringWriter();
        MDLV2000Writer mdlWriter = new MDLV2000Writer(writer);
        mdlWriter.write(container);
        mdlWriter.close();
        String output = writer.toString();
        Assert.assertTrue(output.contains("Fe  0  0  0  0  0  3  0  0  0  0  0  0"));
    }

    @Test
    public void testWriteAtomAtomMapping() throws Exception {
        StringWriter   writer   = new StringWriter();
        IAtomContainer molecule = TestMoleculeFactory.makeAlphaPinene();
        molecule.getAtom(0).setProperty(CDKConstants.ATOM_ATOM_MAPPING, 1);
        molecule.getAtom(1).setProperty(CDKConstants.ATOM_ATOM_MAPPING, 15);
        MDLV2000Writer mdlWriter = new MDLV2000Writer(writer);
        mdlWriter.write(molecule);
        mdlWriter.close();
        String output = writer.toString();
        Assert.assertTrue(output.indexOf("0  0  0  0  0  0  0  0  0  1  0  0") != -1);
        Assert.assertTrue(output.indexOf("0  0  0  0  0  0  0  0  0 15  0  0") != -1);
    }

    /**
     * Tests if String atom atom mappings are parsed correctly
     */
    @Test
    public void testWriteStringAtomAtomMapping() throws Exception {
        StringWriter   writer   = new StringWriter();
        IAtomContainer molecule = TestMoleculeFactory.makeAlphaPinene();
        molecule.getAtom(0).setProperty(CDKConstants.ATOM_ATOM_MAPPING, "1");
        molecule.getAtom(1).setProperty(CDKConstants.ATOM_ATOM_MAPPING, "15");
        MDLV2000Writer mdlWriter = new MDLV2000Writer(writer);
        mdlWriter.write(molecule);
        mdlWriter.close();
        String output = writer.toString();
        Assert.assertTrue(output.contains("0  0  0  0  0  0  0  0  0  1  0  0"));
        Assert.assertTrue(output.contains("0  0  0  0  0  0  0  0  0 15  0  0"));
    }

    /**
     * Tests if non-valid atom atom mappings are ignored by the reader.
     */
    @Test
    public void testWriteInvalidAtomAtomMapping() throws Exception {
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
        Assert.assertTrue(m.matches());
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
    public void testBug1778479() throws Exception {
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
        Assert.assertEquals("Test for zero length pseudo atom label in MDL file", -1,
                            output.indexOf("0.0000    0.0000    0.0000     0  0  0  0  0  0  0  0  0  0  0  0"));
    }

    @Test
    public void testNullFormalCharge() throws Exception {
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
        Assert.assertNotNull(output);
        Assert.assertNotSame(0, output.length());
    }

    @Test
    public void testPrefer3DCoordinateOutput() throws Exception {
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
        Assert.assertTrue(output.contains("3.0"));
        Assert.assertTrue(output.contains("4.0"));
        Assert.assertTrue(output.contains("5.0"));
    }

    @Test
    public void testForce2DCoordinates() throws Exception {
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
        Assert.assertTrue(output.contains("1.0"));
        Assert.assertTrue(output.contains("2.0"));
    }

    @Test
    public void testUndefinedStereo() throws Exception {
        IAtomContainer mol = TestMoleculeFactory.makeAlphaPinene();
        mol.getBond(0).setStereo(IBond.Stereo.UP_OR_DOWN);
        mol.getBond(1).setStereo(IBond.Stereo.E_OR_Z);
        StringWriter   writer    = new StringWriter();
        MDLV2000Writer mdlWriter = new MDLV2000Writer(writer);
        mdlWriter.write(mol);
        mdlWriter.close();
        String output = writer.toString();
        Assert.assertTrue(output.indexOf("1  2  2  4  0  0  0") > -1);
        Assert.assertTrue(output.indexOf("2  3  1  3  0  0  0") > -1);
    }

    @Test(expected = CDKException.class)
    public void testUnsupportedBondOrder() throws Exception {
        IAtomContainer molecule = new AtomContainer();
        molecule.addAtom(new Atom("C"));
        molecule.addAtom(new Atom("C"));
        molecule.addBond(new Bond(molecule.getAtom(0), molecule.getAtom(1), Order.QUADRUPLE));
        MDLV2000Writer mdlWriter = new MDLV2000Writer(new StringWriter());
        mdlWriter.write(molecule);
        mdlWriter.close();
    }

    @Test
    public void testTwoFragmentsWithTitle() throws Exception {
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
        Assert.assertTrue(output.contains("title1; title2"));
    }

    /**
     * Test correct output of R-groups, using the hash (#) and a separate RGP line.
     */
    @Test
    public void testRGPLine() throws Exception {
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

        Assert.assertTrue("Test for R#", -1 != output.indexOf("R#"));
        Assert.assertTrue("Test for RGP line", -1 != output.indexOf("M  RGP  1   1  12"));
    }

    /**
     * Test writing of comments made on individual atoms into an Atom Value lines.
     */
    @Test
    public void testAtomValueLine() throws Exception {
        IAtom carbon = builder.newInstance(IAtom.class, "C");
        carbon.setProperty(CDKConstants.COMMENT, "Carbon comment");
        IAtom oxygen = builder.newInstance(IAtom.class, "O");
        oxygen.setProperty(CDKConstants.COMMENT, "Oxygen comment");
        IBond bond = builder.newInstance(IBond.class, carbon, oxygen, Order.DOUBLE);

        IAtomContainer molecule = new AtomContainer();
        molecule.addAtom(oxygen);
        molecule.addAtom(carbon);
        molecule.addBond(bond);

        StringWriter   writer    = new StringWriter();
        MDLV2000Writer mdlWriter = new MDLV2000Writer(writer);
        mdlWriter.write(molecule);
        mdlWriter.close();

        Assert.assertTrue(writer.toString().indexOf("V    1 Oxygen comment") != -1);
        Assert.assertTrue(writer.toString().indexOf("V    2 Carbon comment") != -1);

    }

    /**
     * Test option to write aromatic bonds with bond type "4".
     * Please note: bond type values 4 through 8 are for SSS queries only.
     *
     * @throws Exception
     */
    @Test
    public void testAromaticBondType4() throws Exception {

        IAtomContainer benzene = TestMoleculeFactory.makeBenzene();
        for (IAtom atom : benzene.atoms()) {
            atom.setFlag(ISAROMATIC, true);
        }
        for (IBond bond : benzene.bonds()) {
            bond.setFlag(ISAROMATIC, true);
        }

        StringWriter   writer    = new StringWriter();
        MDLV2000Writer mdlWriter = new MDLV2000Writer(writer);
        mdlWriter.write(benzene);
        mdlWriter.close();
        Assert.assertTrue(writer.toString().indexOf("1  2  1  0  0  0  0") != -1);

        writer = new StringWriter();
        mdlWriter = new MDLV2000Writer(writer);
        Properties prop = new Properties();
        prop.setProperty("WriteAromaticBondTypes", "true");
        PropertiesListener listener = new PropertiesListener(prop);
        mdlWriter.addChemObjectIOListener(listener);
        mdlWriter.customizeJob();
        mdlWriter.write(benzene);
        mdlWriter.close();
        Assert.assertTrue(writer.toString().indexOf("1  2  4  0  0  0  0") != -1);
    }

    @Test
    public void testAtomParity() throws CDKException, IOException {

        InputStream    in       = ClassLoader.getSystemResourceAsStream("data/mdl/mol_testAtomParity.mol");
        MDLV2000Reader reader   = new MDLV2000Reader(in);
        IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        molecule = reader.read(molecule);
        reader.close();

        StringWriter   sw     = new StringWriter();
        MDLV2000Writer writer = new MDLV2000Writer(sw);
        writer.write(molecule);
        writer.close();


        System.out.println(sw.toString());

        Assert.assertTrue(sw.toString().contains(
            "   -1.1749    0.1436    0.0000 C   0  0  1  0  0  0  0  0  0  0  0  0"));

    }

    @Test
    public void testWritePseudoAtoms() throws Exception {
        InputStream    in       = ClassLoader.getSystemResourceAsStream("data/mdl/pseudoatoms.sdf");
        MDLV2000Reader reader   = new MDLV2000Reader(in);
        IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        molecule = reader.read(molecule);
        reader.close();

        StringWriter   writer  = new StringWriter();
        MDLV2000Writer mwriter = new MDLV2000Writer(writer);
        mwriter.write(molecule);
        mwriter.close();

        String output = writer.toString();
        Assert.assertTrue(output.indexOf("Gln") != -1);
        Assert.assertTrue(output.indexOf("Leu") != -1);
    }

    /**
     * @throws Exception
     * @cdk.bug 1263
     */
    @Test
    public void testWritePseudoAtoms_LongLabel() throws Exception {

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

        Assert.assertTrue(output.contains("A    2"));
        Assert.assertTrue(output.contains("tRNA"));

    }

    /**
     * Checks that null atom labels are handled correctly.
     */
    @Test
    public void testWritePseudoAtoms_nullLabel() throws Exception {

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
        Assert.assertTrue(output.contains("R"));

    }

    /**
     * When there are more then 16 R Groups these should be wrapped
     *
     * @throws Exception
     */
    @Test
    public void testRGPLine_Multiline() throws Exception {

        IChemObjectBuilder builder   = DefaultChemObjectBuilder.getInstance();
        IAtomContainer     container = builder.newInstance(IAtomContainer.class);

        for (int i = 1; i < 20; i++)
            container.addAtom(builder.newInstance(IPseudoAtom.class, "R" + i));

        StringWriter   sw     = new StringWriter();
        MDLV2000Writer writer = new MDLV2000Writer(sw);
        writer.write(container);
        writer.close();

        String output = sw.toString();
        Assert.assertTrue(output.contains("M  RGP  8   1   1   2   2   3   3   4   4   5   5   6   6   7   7   8   8"));
        Assert.assertTrue(output.contains("M  RGP  8   9   9  10  10  11  11  12  12  13  13  14  14  15  15  16  16"));
        Assert.assertTrue(output.contains("M  RGP  3  17  17  18  18  19  19"));

    }

    @Test
    public void testAlias_TruncatedLabel() throws Exception {

        IChemObjectBuilder builder   = DefaultChemObjectBuilder.getInstance();
        IAtomContainer     container = builder.newInstance(IAtomContainer.class);

        String label = "This is a very long label - almost too long. it should be cut here -> and the rest is truncated";

        container.addAtom(builder.newInstance(IPseudoAtom.class, label));

        StringWriter   sw     = new StringWriter();
        MDLV2000Writer writer = new MDLV2000Writer(sw);
        writer.write(container);
        writer.close();

        String output = sw.toString();

        Assert.assertTrue(output.contains("This is a very long label - almost too long. it should be cut here ->"));
        // make sure the full label wasn't output
        Assert.assertFalse(output.contains(label));

    }

    @Test
    public void testSingleSingletRadical() throws Exception {

        InputStream    in       = ClassLoader.getSystemResourceAsStream("data/mdl/singleSingletRadical.mol");
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
    public void testSingleDoubletRadical() throws Exception {

        InputStream    in       = ClassLoader.getSystemResourceAsStream("data/mdl/singleDoubletRadical.mol");
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

    // XXX: information loss, CDK does not distinquish between divalence
    //      singlet and triplet and only stores the unpaired electrons
    @Test
    public void testSingleTripletRadical() throws Exception {

        InputStream    in       = ClassLoader.getSystemResourceAsStream("data/mdl/singleTripletRadical.mol");
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
    public void testMultipleRadicals() throws Exception {

        InputStream    in       = ClassLoader.getSystemResourceAsStream("data/mdl/multipleRadicals.mol");
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
    public void testSgroupAtomListWrapping() throws Exception {
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
    public void sgroupRepeatUnitRoundTrip() throws Exception {
        StringWriter sw = new StringWriter();
        try (MDLV2000Reader mdlr = new MDLV2000Reader(getClass().getResourceAsStream("/data/mdl/sgroup-sru.mol"));
             MDLV2000Writer mdlw = new MDLV2000Writer(sw)) {
            mdlw.write(mdlr.read(new AtomContainer()));
            String output = sw.toString();
            assertThat(output, containsString("M  STY  1   1 SRU"));
            assertThat(output, containsString("M  SMT   1 n"));
            assertThat(output, containsString("M  SCN  1   1 HT"));
        }
    }

    @Test
    public void sgroupBracketStylesRoundTrip() throws Exception {
        StringWriter sw = new StringWriter();
        try (MDLV2000Reader mdlr = new MDLV2000Reader(getClass().getResourceAsStream("/data/mdl/sgroup-sru-bracketstyles.mol"));
             MDLV2000Writer mdlw = new MDLV2000Writer(sw)) {
            mdlw.write(mdlr.read(new AtomContainer()));
            String output = sw.toString();
            assertThat(output, containsString("M  STY  2   1 SRU   2 SRU"));
            assertThat(output, containsString("M  SBT  1   1   1"));
        }
    }

    @Test
    public void sgroupUnorderedMixtureRoundTrip() throws Exception {
        StringWriter sw = new StringWriter();
        try (MDLV2000Reader mdlr = new MDLV2000Reader(getClass().getResourceAsStream("/data/mdl/sgroup-unord-mixture.mol"));
             MDLV2000Writer mdlw = new MDLV2000Writer(sw)) {
            mdlw.write(mdlr.read(new AtomContainer()));
            String output = sw.toString();
            assertThat(output, containsString("M  STY  3   1 COM   2 COM   3 MIX"));
            assertThat(output, containsString("M  SPL  1   1   3"));
        }
    }

    @Test
    public void sgroupCopolymerRoundTrip() throws Exception {
        StringWriter sw = new StringWriter();
        try (MDLV2000Reader mdlr = new MDLV2000Reader(getClass().getResourceAsStream("/data/mdl/sgroup-ran-copolymer.mol"));
             MDLV2000Writer mdlw = new MDLV2000Writer(sw)) {
            mdlw.write(mdlr.read(new AtomContainer()));
            String output = sw.toString();
            assertThat(output, containsString("M  SST  1   1 RAN"));
            assertThat(output, containsString("M  STY  3   1 COP   2 SRU   3 SRU"));
        }
    }

    @Test
    public void sgroupExpandedAbbreviationRoundTrip() throws Exception {
        StringWriter sw = new StringWriter();
        try (MDLV2000Reader mdlr = new MDLV2000Reader(getClass().getResourceAsStream("/data/mdl/triphenyl-phosphate-expanded.mol"));
             MDLV2000Writer mdlw = new MDLV2000Writer(sw)) {
            mdlw.write(mdlr.read(new AtomContainer()));
            String output = sw.toString();
            assertThat(output, containsString("M  STY  3   1 SUP   2 SUP   3 SUP\n"));
            assertThat(output, containsString("M  SDS EXP  1   1"));
        }
    }

    @Test
    public void sgroupParentAtomListRoundTrip() throws Exception {
        StringWriter sw = new StringWriter();
        try (MDLV2000Reader mdlr = new MDLV2000Reader(getClass().getResourceAsStream("/data/mdl/ChEBI_81539.mol"));
             MDLV2000Writer mdlw = new MDLV2000Writer(sw)) {
            mdlw.write(mdlr.read(new AtomContainer()));
            String output = sw.toString();
            assertThat(output, containsString("M  STY  5   1 MUL   2 SRU"));
            assertThat(output, containsString("M  SPA   1 12"));
        }
    }

    @Test
    public void sgroupOrderedMixtureRoundTrip() throws Exception {
        StringWriter sw = new StringWriter();
        try (MDLV2000Reader mdlr = new MDLV2000Reader(getClass().getResourceAsStream("/data/mdl/sgroup-ord-mixture.mol"));
             MDLV2000Writer mdlw = new MDLV2000Writer(sw)) {
            mdlw.write(mdlr.read(new AtomContainer()));
            String output = sw.toString();
            assertThat(output, containsString("M  STY  3   1 COM   2 COM   3 FOR"));
            assertThat(output, containsString("M  SNC  1   1   1"));
            assertThat(output, containsString("M  SNC  1   2   2"));
        }
    }

    @Test
    public void roundtripAtomParityExpH() throws Exception {
        StringWriter sw = new StringWriter();
        try (MDLV2000Reader mdlr = new MDLV2000Reader(getClass().getResourceAsStream("/data/mdl/tetrahedral-parity-withExpH.mol"));
             MDLV2000Writer mdlw = new MDLV2000Writer(sw)) {
            mdlw.write(mdlr.read(new AtomContainer()));
            String output = sw.toString();
            assertThat(output, containsString("    0.0000    0.0000    0.0000 C   0  0  1  0  0  0  0  0  0  0  0  0\n"));
        }
    }

    @Test
    public void roundtripAtomParityImplH() throws Exception {
        StringWriter sw = new StringWriter();
        try (MDLV2000Reader mdlr = new MDLV2000Reader(getClass().getResourceAsStream("/data/mdl/tetrahedral-parity-withImplH.mol"));
             MDLV2000Writer mdlw = new MDLV2000Writer(sw)) {
            mdlw.write(mdlr.read(new AtomContainer()));
            String output = sw.toString();
            assertThat(output, containsString("    0.0000    0.0000    0.0000 C   0  0  1  0  0  0  0  0  0  0  0  0\n"));
        }
    }

    @Test
    public void roundtripAtomParityImplModified() throws Exception {
        StringWriter sw = new StringWriter();
        try (MDLV2000Reader mdlr = new MDLV2000Reader(getClass().getResourceAsStream("/data/mdl/tetrahedral-parity-withImplH.mol"));
             MDLV2000Writer mdlw = new MDLV2000Writer(sw)) {
            AtomContainer         mol = mdlr.read(new AtomContainer());
            ITetrahedralChirality tc  = (ITetrahedralChirality) mol.stereoElements().iterator().next();
            tc.setStereo(tc.getStereo().invert());
            mdlw.write(mol);
            String output = sw.toString();
            assertThat(output, containsString("    0.0000    0.0000    0.0000 C   0  0  2  0  0  0  0  0  0  0  0  0\n"));
        }
    }

    @Test(expected = CDKException.class)
    public void aromaticBondTypes() throws Exception {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        IBond bond = builder.newInstance(IBond.class, mol.getAtom(0), mol.getAtom(1), Order.UNSET);
        bond.setIsAromatic(true);
        mol.addBond(bond);
        StringWriter sw = new StringWriter();
        try (MDLV2000Writer mdlw = new MDLV2000Writer(sw)) {
            mdlw.write(mol);
        }
    }

    @Test
    public void aromaticBondTypesEnabled() throws Exception {
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
        assertThat(sw.toString(), containsString("  1  2  4  0  0  0  0 \n"));
    }

    @Test
    public void writeDimensionField() throws Exception {
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
    public void writeDimensionField3D() throws Exception {
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
    public void writeMoreThan8Radicals() throws Exception {
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
    public void writeCarbon12() throws Exception {
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
    public void ignoreCarbon12() throws Exception {
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
    public void writeCarbon13AtomProps() throws Exception {
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
    public void writeChargeAtomProps() throws Exception {
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
    public void skipDefaultProps() throws Exception {
        StringWriter sw = new StringWriter();
        try (MDLV2000Reader mdlr = new MDLV2000Reader(getClass().getResourceAsStream("/data/mdl/tetrahedral-parity-withImplH.mol"));
             MDLV2000Writer mdlw = new MDLV2000Writer(sw)) {
            mdlw.getSetting(MDLV2000Writer.OptWriteDefaultProperties)
                .setSetting("false");
            mdlw.write(mdlr.read(new AtomContainer()));
            String output = sw.toString();
            assertThat(output, containsString("\n"
                                              + "  5  4  0  0  1  0  0  0  0  0999 V2000\n"
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
}
