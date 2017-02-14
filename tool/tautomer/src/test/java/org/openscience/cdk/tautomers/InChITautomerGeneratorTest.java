/* Copyright (C) 2011 Mark Rijnbeek <markr@ebi.ac.uk>
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
package org.openscience.cdk.tautomers;

import java.io.StringReader;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.SlowTest;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * Tests generation of tautomers.
 * @author Mark Rijnbeek
 *
 * @cdk.module test-tautomer
 */

public class InChITautomerGeneratorTest extends CDKTestCase {

    private SmilesParser           smilesParser;
    private InChITautomerGenerator tautomerGenerator = new InChITautomerGenerator();

    public InChITautomerGeneratorTest() throws CDKException {
        super();
        smilesParser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        tautomerGenerator = new InChITautomerGenerator();
    }

    private List<IAtomContainer> unitTestWithInchiProvided(String smiles, String inchi, int tautCountExpected)
            throws Exception {
        IAtomContainer container = smilesParser.parseSmiles(smiles);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(container);
        List<IAtomContainer> tautomers = tautomerGenerator.getTautomers(container, inchi);
        Assert.assertEquals(tautCountExpected, tautomers.size());
        return tautomers;
    }

    private List<IAtomContainer> unitTestWithoutInchiProvided(String smiles, int flags, int tautCountExpected)
            throws Exception {
        IAtomContainer container = smilesParser.parseSmiles(smiles);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(container);
        InChITautomerGenerator tautegen = new InChITautomerGenerator(flags);
        List<IAtomContainer> tautomers = tautegen.getTautomers(container);
        Assert.assertEquals(tautCountExpected, tautomers.size());
        return tautomers;
    }

    @Test
    public void test1() throws Exception {
        unitTestWithInchiProvided("NC1=CC(N)=NC(O)=N1", "InChI=1S/C4H6N4O/c5-2-1-3(6)8-4(9)7-2/h1H,(H5,5,6,7,8,9)", 5);
    }

    @Test
    public void test2() throws Exception {
        unitTestWithInchiProvided("CCCN1C2=C(NC=N2)C(=O)NC1=O",
                "InChI=1S/C8H10N4O2/c1-2-3-12-6-5(9-4-10-6)7(13)11-8(12)14/h4H,2-3H2,1H3,(H,9,10)(H,11,13,14)", 8);
    }

    @Test
    public void test3() throws Exception {
        unitTestWithInchiProvided("CCNC(=N)NC", "InChI=1S/C4H11N3/c1-3-7-4(5)6-2/h3H2,1-2H3,(H3,5,6,7)", 3);
    }

    @Test
    public void test4() throws Exception {
        unitTestWithInchiProvided("O=C1NC=CC(=O)N1", "InChI=1S/C4H4N2O2/c7-3-1-2-5-4(8)6-3/h1-2H,(H2,5,6,7,8)", 6);
    }

    @Test
    public void test5() throws Exception {
        unitTestWithInchiProvided("CCN1CCOC2=CC(NC3=NCCN3)=CC=C12",
                "InChI=1S/C13H18N4O/c1-2-17-7-8-18-12-9-10(3-4-11(12)17)16-13-14-5-6-15-13/"
                        + "h3-4,9H,2,5-8H2,1H3,(H2,14,15,16)", 2);
    }

    @Test
    @Category(SlowTest.class)
    public void test6() throws Exception {
        //Warfarin: not you need to create the InChI with option KET to get the ketone/hydroxyl tautomerism
        unitTestWithInchiProvided("CC(=O)CC(C1=CC=CC=C1)C1=C(O)C2=C(OC1=O)C=CC=C2",
                "InChI=1/C19H16O4/c1-12(20)11-15(13-7-3-2-4-8-13)17-18(21)14-9-5-6-10-16(14)23-19(17)22/"
                        + "h2-10,15H,1H3,(H2,11,20)(H,17,21,22)", 6);
    }

    @Test
    public void test1_fast() throws Exception {
        unitTestWithoutInchiProvided("NC1=CC(N)=NC(O)=N1", 0, 5);
    }

    @Test
    public void test2_fast() throws Exception {
        unitTestWithoutInchiProvided("CCCN1C2=C(NC=N2)C(=O)NC1=O", 0, 8);
    }

    @Test
    public void test3_fast() throws Exception {
        unitTestWithoutInchiProvided("CCNC(=N)NC", 0, 3);
    }

    @Test
    public void test4_fast() throws Exception {
        unitTestWithoutInchiProvided("O=C1NC=CC(=O)N1", 0, 6);
    }

    @Test
    public void test5_fast() throws Exception {
        unitTestWithoutInchiProvided("CCN1CCOC2=CC(NC3=NCCN3)=CC=C12", 0, 2);
    }

    @Test
    public void test6_fast() throws Exception {
        //Warfarin: not you need to create the InChI with option KET to get the ketone/hydroxyl tautomerism
        unitTestWithoutInchiProvided("CC(=O)CC(C1=CC=CC=C1)C1=C(O)C2=C(OC1=O)C=CC=C2", InChITautomerGenerator.KETO_ENOL,  6);
    }

    @Test(expected = CDKException.class)
    // bail out on dots in formula
    public void testFail1() throws Exception {
        unitTestWithInchiProvided("[I-].CCN1CCOC2=CC(NC3=NCCN3)=CC=C12",
                "InChI=1S/C13H18N4O.HI/c1-2-17-7-8-18-12-9-10(3-4-11(12)17)16-13-14-5-6-15-13;"
                        + "/h3-4,9H,2,5-8H2,1H3,(H2,14,15,16);1H/p-1", 2);
    }

    @Test(expected = CDKException.class)
    // bail out on dots in formula
    public void testFail2() throws Exception {
        unitTestWithInchiProvided("CN1C=C(C)C(=O)N2C1O[Pt]([NH3+])([NH3+])OC3N(C)C=C(C)C(=O)N3[Pt]2([NH3+])[NH3+]",
                "InChI=1S/2C6H9N2O2.4H3N.2Pt/c2*1-4-3-8(2)6(10)7-5(4)9;;;;;;"
                        + "/h2*3,6H,1-2H3,(H,7,9);4*1H3;;/q2*-1;;;;;2*+4/p-2", 10);
    }

    @Test
    public void test_withJniInchi() throws Exception {
        String mdlInput = // same as NC1=CC(N)=NC(O)=N1
        "\n" + "  Mrv0541 02151109592D\n" + "\n" + "  9  9  0  0  0  0            999 V2000\n"
                + "    2.1434   -0.4125    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0\n"
                + "    1.4289   -0.0000    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
                + "    0.7145   -0.4125    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
                + "    0.0000   -0.0000    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
                + "   -0.7145   -0.4125    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0\n"
                + "    0.0000    0.8250    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0\n"
                + "    0.7145    1.2375    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"
                + "    0.7145    2.0625    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n"
                + "    1.4289    0.8250    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0\n" + "  1  2  1  0  0  0  0\n"
                + "  2  3  2  0  0  0  0\n" + "  3  4  1  0  0  0  0\n" + "  4  5  1  0  0  0  0\n"
                + "  4  6  2  0  0  0  0\n" + "  6  7  1  0  0  0  0\n" + "  7  8  1  0  0  0  0\n"
                + "  7  9  2  0  0  0  0\n" + "  2  9  1  0  0  0  0\n" + "M  END\n";

        MDLV2000Reader reader = new MDLV2000Reader(new StringReader(mdlInput));
        IAtomContainer molecule = reader.read(new AtomContainer());
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        CDKHydrogenAdder hAdder = CDKHydrogenAdder.getInstance(molecule.getBuilder());
        hAdder.addImplicitHydrogens(molecule);

        List<IAtomContainer> tautomers = tautomerGenerator.getTautomers(molecule); // InChI will be calculated
        Assert.assertEquals(5, tautomers.size());
    }

    @Test
    public void testAdenine() throws CDKException, CloneNotSupportedException {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "N");
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "N");
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "N");
        mol.addAtom(a3);
        IAtom a4 = builder.newInstance(IAtom.class, "N");
        mol.addAtom(a4);
        IAtom a5 = builder.newInstance(IAtom.class, "N");
        mol.addAtom(a5);
        IAtom a6 = builder.newInstance(IAtom.class, "C");
        mol.addAtom(a6);
        IAtom a7 = builder.newInstance(IAtom.class, "C");
        mol.addAtom(a7);
        IAtom a8 = builder.newInstance(IAtom.class, "C");
        mol.addAtom(a8);
        IAtom a9 = builder.newInstance(IAtom.class, "C");
        mol.addAtom(a9);
        IAtom a10 = builder.newInstance(IAtom.class, "C");
        mol.addAtom(a10);
        IAtom a11 = builder.newInstance(IAtom.class, "H");
        mol.addAtom(a11);
        IAtom a12 = builder.newInstance(IAtom.class, "H");
        mol.addAtom(a12);
        IAtom a13 = builder.newInstance(IAtom.class, "H");
        mol.addAtom(a13);
        IAtom a14 = builder.newInstance(IAtom.class, "H");
        mol.addAtom(a14);
        IAtom a15 = builder.newInstance(IAtom.class, "H");
        mol.addAtom(a15);
        IBond b1 = builder.newInstance(IBond.class, a1, a6, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a1, a9, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = builder.newInstance(IBond.class, a1, a11, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = builder.newInstance(IBond.class, a2, a7, IBond.Order.SINGLE);
        mol.addBond(b4);
        IBond b5 = builder.newInstance(IBond.class, a2, a9, IBond.Order.DOUBLE);
        mol.addBond(b5);
        IBond b6 = builder.newInstance(IBond.class, a3, a7, IBond.Order.DOUBLE);
        mol.addBond(b6);
        IBond b7 = builder.newInstance(IBond.class, a3, a10, IBond.Order.SINGLE);
        mol.addBond(b7);
        IBond b8 = builder.newInstance(IBond.class, a4, a8, IBond.Order.SINGLE);
        mol.addBond(b8);
        IBond b9 = builder.newInstance(IBond.class, a4, a10, IBond.Order.DOUBLE);
        mol.addBond(b9);
        IBond b10 = builder.newInstance(IBond.class, a5, a8, IBond.Order.SINGLE);
        mol.addBond(b10);
        IBond b11 = builder.newInstance(IBond.class, a5, a14, IBond.Order.SINGLE);
        mol.addBond(b11);
        IBond b12 = builder.newInstance(IBond.class, a5, a15, IBond.Order.SINGLE);
        mol.addBond(b12);
        IBond b13 = builder.newInstance(IBond.class, a6, a7, IBond.Order.SINGLE);
        mol.addBond(b13);
        IBond b14 = builder.newInstance(IBond.class, a6, a8, IBond.Order.DOUBLE);
        mol.addBond(b14);
        IBond b15 = builder.newInstance(IBond.class, a9, a12, IBond.Order.SINGLE);
        mol.addBond(b15);
        IBond b16 = builder.newInstance(IBond.class, a10, a13, IBond.Order.SINGLE);
        mol.addBond(b16);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);

        List<IAtomContainer> tautomers = tautomerGenerator.getTautomers(mol);
        Assert.assertEquals(8, tautomers.size());
    }

}
