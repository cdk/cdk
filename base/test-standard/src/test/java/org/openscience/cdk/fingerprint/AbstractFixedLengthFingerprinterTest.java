/* Copyright (C) 1997-2009,2011  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All I ask is that proper credit is given for my work, which includes
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
package org.openscience.cdk.fingerprint;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.aromaticity.Kekulization;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.io.InputStream;
import java.util.BitSet;

import static org.hamcrest.CoreMatchers.is;

/**
 * @cdk.module test-standard
 */
public abstract class AbstractFixedLengthFingerprinterTest extends AbstractFingerprinterTest {

    // logical 'AND' or two bit sets (orginals are not modified)
    static BitSet and(BitSet a, BitSet b) {
        BitSet c = (BitSet) a.clone();
        c.and(b);
        return c;
    }

    /**
     * @cdk.bug 706786
     */
    @Test
    public void testBug706786() throws Exception {
        // inlined molecules - note this test fails if implicit hydrogens are
        // included. generally MACCS and ESTATE can't be used for substructure filter
        // check those subclasses which check the bits are set
        IAtomContainer superStructure = bug706786_1();
        IAtomContainer subStructure = bug706786_2();

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(superStructure);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(subStructure);
        addImplicitHydrogens(superStructure);
        addImplicitHydrogens(subStructure);

        IFingerprinter fingerprinter = getBitFingerprinter();
        BitSet superBS = fingerprinter.getBitFingerprint(superStructure).asBitSet();
        BitSet subBS = fingerprinter.getBitFingerprint(subStructure).asBitSet();

        org.hamcrest.MatcherAssert.assertThat(and(superBS, subBS), is(subBS));
    }

    /**
     * @cdk.bug 853254
     */
    @Test
    public void testBug853254() throws Exception {
        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
        String filename = "data/mdl/bug853254-2.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        IAtomContainer superstructure = reader.read(builder.newAtomContainer());

        filename = "data/mdl/bug853254-1.mol";
        ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        reader = new MDLV2000Reader(ins, Mode.STRICT);
        IAtomContainer substructure = reader.read(builder.newAtomContainer());

        // these molecules are different resonance forms of the same molecule
        // make sure aromaticity is detected. although some fingerprinters do this
        // one should not expected all implementations to do so.
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(superstructure);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(substructure);
        Aromaticity.cdkLegacy().apply(superstructure);
        Aromaticity.cdkLegacy().apply(substructure);

        IFingerprinter fingerprinter = getBitFingerprinter();
        BitSet superBS = fingerprinter.getBitFingerprint(superstructure).asBitSet();
        BitSet subBS = fingerprinter.getBitFingerprint(substructure).asBitSet();
        boolean isSubset = FingerprinterTool.isSubset(superBS, subBS);
        Assert.assertTrue(isSubset);
    }

    /**
     * Fingerprint not subset.
     *
     * @cdk.bug 934819
     */
    @Test
    public void testBug934819() throws Exception {
        // inlined molecules - note this test fails if implicit hydrogens are
        // included. generally PubCheMFingerprint can't be used for substructure filter
        IAtomContainer superStructure = bug934819_2();
        IAtomContainer subStructure = bug934819_1();

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(superStructure);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(subStructure);
        addImplicitHydrogens(superStructure);
        addImplicitHydrogens(subStructure);

        IFingerprinter fingerprinter = getBitFingerprinter();
        BitSet superBS = fingerprinter.getBitFingerprint(superStructure).asBitSet();
        BitSet subBS = fingerprinter.getBitFingerprint(subStructure).asBitSet();

        org.hamcrest.MatcherAssert.assertThat(and(superBS, subBS), is(subBS));
    }

    /**
     * Problems with different aromaticity concepts.
     *
     * @cdk.bug 771485
     */
    @Test
    public void testBug771485() throws Exception {
        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
        String filename = "data/mdl/bug771485-1.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        IAtomContainer structure1 = (IAtomContainer) reader.read(builder.newAtomContainer());

        filename = "data/mdl/bug771485-2.mol";
        ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        reader = new MDLV2000Reader(ins, Mode.STRICT);
        IAtomContainer structure2 = (IAtomContainer) reader.read(builder.newAtomContainer());

        // these molecules are different resonance forms of the same molecule
        // make sure aromaticity is detected. although some fingerprinters do this
        // one should not expected all implementations to do so.
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(structure1);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(structure2);
        Aromaticity.cdkLegacy().apply(structure1);
        Aromaticity.cdkLegacy().apply(structure2);
        addImplicitHydrogens(structure1);
        addImplicitHydrogens(structure2);

        Kekulization.kekulize(structure1);
        Kekulization.kekulize(structure2);

        // hydrogens loaded from MDL mol files if non-query. Structure 2 has
        // query aromatic bonds and the hydrogen counts are not assigned - ensure
        // this is done here.
        CDKHydrogenAdder.getInstance(structure1.getBuilder()).addImplicitHydrogens(structure1);
        CDKHydrogenAdder.getInstance(structure2.getBuilder()).addImplicitHydrogens(structure2);

        IFingerprinter fingerprinter = getBitFingerprinter();
        BitSet superBS = fingerprinter.getBitFingerprint(structure2).asBitSet();
        BitSet subBS = fingerprinter.getBitFingerprint(structure1).asBitSet();
        boolean isSubset = FingerprinterTool.isSubset(superBS, subBS);
        Assert.assertTrue(isSubset);
    }

    /**
     * Fingerprinter gives different fingerprints for same molecule.
     *
     * @cdk.bug 931608
     * @cdk.bug 934819
     */
    @Test
    public void testBug931608() throws Exception {
        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
        String filename = "data/mdl/bug931608-1.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        IAtomContainer structure1 = reader.read(builder.newAtomContainer());

        filename = "data/mdl/bug931608-2.mol";
        ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        reader = new MDLV2000Reader(ins, Mode.STRICT);
        IAtomContainer structure2 = reader.read(builder.newAtomContainer());

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(structure1);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(structure2);

        IFingerprinter fingerprinter = getBitFingerprinter();
        BitSet bs1 = fingerprinter.getBitFingerprint(structure1).asBitSet();
        BitSet bs2 = fingerprinter.getBitFingerprint(structure2).asBitSet();
        // now we do the boolean XOR on the two bitsets, leading
        // to a bitset that has all the bits set to "true" which differ
        // between the two original bitsets
        bs1.xor(bs2);
        // cardinality gives us the number of "true" bits in the
        // result of the XOR operation.
        int cardinality = bs1.cardinality();
        Assert.assertEquals(0, cardinality);
    }

    /**
     * data/mdl/bug70786-1.mol
     * CC(=O)C1=CC2=C(OC(C)(C)[C@@H](O)[C@@H]2O)C=C1
     * @cdk.inchi InChI=1/C13H16O4/c1-7(14)8-4-5-10-9(6-8)11(15)12(16)13(2,3)17-10/h4-6,11-12,15-16H,1-3H3/t11-,12+/s2
     */
    static IAtomContainer bug706786_1() throws CDKException {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "C");
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "O");
        mol.addAtom(a3);
        IAtom a4 = builder.newInstance(IAtom.class, "C");
        a4.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addAtom(a4);
        IAtom a5 = builder.newInstance(IAtom.class, "C");
        a5.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addAtom(a5);
        IAtom a6 = builder.newInstance(IAtom.class, "C");
        a6.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addAtom(a6);
        IAtom a7 = builder.newInstance(IAtom.class, "C");
        a7.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addAtom(a7);
        IAtom a8 = builder.newInstance(IAtom.class, "O");
        mol.addAtom(a8);
        IAtom a9 = builder.newInstance(IAtom.class, "C");
        mol.addAtom(a9);
        IAtom a10 = builder.newInstance(IAtom.class, "C");
        mol.addAtom(a10);
        IAtom a11 = builder.newInstance(IAtom.class, "C");
        mol.addAtom(a11);
        IAtom a12 = builder.newInstance(IAtom.class, "H");
        mol.addAtom(a12);
        IAtom a13 = builder.newInstance(IAtom.class, "C");
        mol.addAtom(a13);
        IAtom a14 = builder.newInstance(IAtom.class, "O");
        mol.addAtom(a14);
        IAtom a15 = builder.newInstance(IAtom.class, "H");
        mol.addAtom(a15);
        IAtom a16 = builder.newInstance(IAtom.class, "C");
        mol.addAtom(a16);
        IAtom a17 = builder.newInstance(IAtom.class, "O");
        mol.addAtom(a17);
        IAtom a18 = builder.newInstance(IAtom.class, "C");
        a18.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addAtom(a18);
        IAtom a19 = builder.newInstance(IAtom.class, "C");
        a19.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addAtom(a19);
        IBond b1 = builder.newInstance(IBond.class, a2, a1, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a3, a2, IBond.Order.DOUBLE);
        mol.addBond(b2);
        IBond b3 = builder.newInstance(IBond.class, a4, a2, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = builder.newInstance(IBond.class, a5, a4, IBond.Order.DOUBLE);
        b4.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addBond(b4);
        IBond b5 = builder.newInstance(IBond.class, a6, a5, IBond.Order.SINGLE);
        b5.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addBond(b5);
        IBond b6 = builder.newInstance(IBond.class, a7, a6, IBond.Order.DOUBLE);
        b6.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addBond(b6);
        IBond b7 = builder.newInstance(IBond.class, a8, a7, IBond.Order.SINGLE);
        mol.addBond(b7);
        IBond b8 = builder.newInstance(IBond.class, a9, a8, IBond.Order.SINGLE);
        mol.addBond(b8);
        IBond b9 = builder.newInstance(IBond.class, a10, a9, IBond.Order.SINGLE);
        mol.addBond(b9);
        IBond b10 = builder.newInstance(IBond.class, a11, a9, IBond.Order.SINGLE);
        mol.addBond(b10);
        IBond b11 = builder.newInstance(IBond.class, a13, a12, IBond.Order.SINGLE);
        mol.addBond(b11);
        IBond b12 = builder.newInstance(IBond.class, a13, a9, IBond.Order.SINGLE);
        mol.addBond(b12);
        IBond b13 = builder.newInstance(IBond.class, a14, a13, IBond.Order.SINGLE);
        mol.addBond(b13);
        IBond b14 = builder.newInstance(IBond.class, a16, a15, IBond.Order.SINGLE);
        mol.addBond(b14);
        IBond b15 = builder.newInstance(IBond.class, a16, a13, IBond.Order.SINGLE);
        mol.addBond(b15);
        IBond b16 = builder.newInstance(IBond.class, a16, a6, IBond.Order.SINGLE);
        mol.addBond(b16);
        IBond b17 = builder.newInstance(IBond.class, a17, a16, IBond.Order.SINGLE);
        mol.addBond(b17);
        IBond b18 = builder.newInstance(IBond.class, a18, a7, IBond.Order.SINGLE);
        b18.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addBond(b18);
        IBond b19 = builder.newInstance(IBond.class, a19, a18, IBond.Order.DOUBLE);
        b19.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addBond(b19);
        IBond b20 = builder.newInstance(IBond.class, a19, a4, IBond.Order.SINGLE);
        b20.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addBond(b20);
        return mol;
    }

    /**
     * data/mdl/bug706786-2.mol
     * C1COC2=CC=CC=C2C1
     * @cdk.inchi InChI=1/C9H10O/c1-2-6-9-8(4-1)5-3-7-10-9/h1-2,4,6H,3,5,7H2
     */
    static IAtomContainer bug706786_2() throws CDKException {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "C");
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "O");
        mol.addAtom(a3);
        IAtom a4 = builder.newInstance(IAtom.class, "C");
        a4.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addAtom(a4);
        IAtom a5 = builder.newInstance(IAtom.class, "C");
        a5.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addAtom(a5);
        IAtom a6 = builder.newInstance(IAtom.class, "C");
        a6.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addAtom(a6);
        IAtom a7 = builder.newInstance(IAtom.class, "C");
        a7.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addAtom(a7);
        IAtom a8 = builder.newInstance(IAtom.class, "C");
        a8.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addAtom(a8);
        IAtom a9 = builder.newInstance(IAtom.class, "C");
        a9.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addAtom(a9);
        IAtom a10 = builder.newInstance(IAtom.class, "C");
        mol.addAtom(a10);
        IBond b1 = builder.newInstance(IBond.class, a2, a1, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a3, a2, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = builder.newInstance(IBond.class, a4, a3, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = builder.newInstance(IBond.class, a5, a4, IBond.Order.DOUBLE);
        b4.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addBond(b4);
        IBond b5 = builder.newInstance(IBond.class, a6, a5, IBond.Order.SINGLE);
        b5.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addBond(b5);
        IBond b6 = builder.newInstance(IBond.class, a7, a6, IBond.Order.DOUBLE);
        b6.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addBond(b6);
        IBond b7 = builder.newInstance(IBond.class, a8, a7, IBond.Order.SINGLE);
        b7.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addBond(b7);
        IBond b8 = builder.newInstance(IBond.class, a9, a8, IBond.Order.DOUBLE);
        b8.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addBond(b8);
        IBond b9 = builder.newInstance(IBond.class, a9, a4, IBond.Order.SINGLE);
        b9.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addBond(b9);
        IBond b10 = builder.newInstance(IBond.class, a10, a9, IBond.Order.SINGLE);
        mol.addBond(b10);
        IBond b11 = builder.newInstance(IBond.class, a10, a1, IBond.Order.SINGLE);
        mol.addBond(b11);
        return mol;
    }

    /**
     * /data/mdl/bug934819_1.mol
     * [O-][N+](=O)C1=CC=CS1
     * @cdk.inchi InChI=1/C4H3NO2S/c6-5(7)4-2-1-3-8-4/h1-3H
     */
    static IAtomContainer bug934819_1() {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "O");
        a1.setFormalCharge(-1);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "N");
        a2.setFormalCharge(1);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "O");
        mol.addAtom(a3);
        IAtom a4 = builder.newInstance(IAtom.class, "C");
        a4.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addAtom(a4);
        IAtom a5 = builder.newInstance(IAtom.class, "C");
        a5.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addAtom(a5);
        IAtom a6 = builder.newInstance(IAtom.class, "C");
        a6.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addAtom(a6);
        IAtom a7 = builder.newInstance(IAtom.class, "C");
        a7.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addAtom(a7);
        IAtom a8 = builder.newInstance(IAtom.class, "S");
        a8.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addAtom(a8);
        IBond b1 = builder.newInstance(IBond.class, a2, a1, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a3, a2, IBond.Order.DOUBLE);
        mol.addBond(b2);
        IBond b3 = builder.newInstance(IBond.class, a4, a2, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = builder.newInstance(IBond.class, a5, a4, IBond.Order.DOUBLE);
        b4.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addBond(b4);
        IBond b5 = builder.newInstance(IBond.class, a6, a5, IBond.Order.SINGLE);
        b5.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addBond(b5);
        IBond b6 = builder.newInstance(IBond.class, a7, a6, IBond.Order.DOUBLE);
        b6.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addBond(b6);
        IBond b7 = builder.newInstance(IBond.class, a8, a7, IBond.Order.SINGLE);
        b7.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addBond(b7);
        IBond b8 = builder.newInstance(IBond.class, a8, a4, IBond.Order.SINGLE);
        b8.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addBond(b8);
        return mol;
    }

    /**
     * /data/mdl/bug934819-2.mol
     * CCCCSC1=CC=C(S1)C#CC1=CC=C(S1)[N+]([O-])=O
     * @cdk.inchi InChI=1/C14H13NO2S3/c1-2-3-10-18-14-9-7-12(20-14)5-4-11-6-8-13(19-11)15(16)17/h6-9H,2-3,10H2,1H3
     */
    static IAtomContainer bug934819_2() {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "C");
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        mol.addAtom(a3);
        IAtom a4 = builder.newInstance(IAtom.class, "C");
        mol.addAtom(a4);
        IAtom a5 = builder.newInstance(IAtom.class, "S");
        mol.addAtom(a5);
        IAtom a6 = builder.newInstance(IAtom.class, "C");
        a6.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addAtom(a6);
        IAtom a7 = builder.newInstance(IAtom.class, "C");
        a7.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addAtom(a7);
        IAtom a8 = builder.newInstance(IAtom.class, "C");
        a8.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addAtom(a8);
        IAtom a9 = builder.newInstance(IAtom.class, "C");
        a9.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addAtom(a9);
        IAtom a10 = builder.newInstance(IAtom.class, "S");
        a10.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addAtom(a10);
        IAtom a11 = builder.newInstance(IAtom.class, "C");
        mol.addAtom(a11);
        IAtom a12 = builder.newInstance(IAtom.class, "C");
        mol.addAtom(a12);
        IAtom a13 = builder.newInstance(IAtom.class, "C");
        a13.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addAtom(a13);
        IAtom a14 = builder.newInstance(IAtom.class, "C");
        a14.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addAtom(a14);
        IAtom a15 = builder.newInstance(IAtom.class, "C");
        a15.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addAtom(a15);
        IAtom a16 = builder.newInstance(IAtom.class, "C");
        a16.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addAtom(a16);
        IAtom a17 = builder.newInstance(IAtom.class, "S");
        a17.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addAtom(a17);
        IAtom a18 = builder.newInstance(IAtom.class, "N");
        a18.setFormalCharge(1);
        mol.addAtom(a18);
        IAtom a19 = builder.newInstance(IAtom.class, "O");
        a19.setFormalCharge(-1);
        mol.addAtom(a19);
        IAtom a20 = builder.newInstance(IAtom.class, "O");
        mol.addAtom(a20);
        IBond b1 = builder.newInstance(IBond.class, a2, a1, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a3, a2, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = builder.newInstance(IBond.class, a4, a3, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = builder.newInstance(IBond.class, a5, a4, IBond.Order.SINGLE);
        mol.addBond(b4);
        IBond b5 = builder.newInstance(IBond.class, a6, a5, IBond.Order.SINGLE);
        mol.addBond(b5);
        IBond b6 = builder.newInstance(IBond.class, a7, a6, IBond.Order.DOUBLE);
        b6.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addBond(b6);
        IBond b7 = builder.newInstance(IBond.class, a8, a7, IBond.Order.SINGLE);
        b7.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addBond(b7);
        IBond b8 = builder.newInstance(IBond.class, a9, a8, IBond.Order.DOUBLE);
        b8.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addBond(b8);
        IBond b9 = builder.newInstance(IBond.class, a10, a9, IBond.Order.SINGLE);
        b9.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addBond(b9);
        IBond b10 = builder.newInstance(IBond.class, a10, a6, IBond.Order.SINGLE);
        b10.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addBond(b10);
        IBond b11 = builder.newInstance(IBond.class, a11, a9, IBond.Order.SINGLE);
        mol.addBond(b11);
        IBond b12 = builder.newInstance(IBond.class, a12, a11, IBond.Order.TRIPLE);
        mol.addBond(b12);
        IBond b13 = builder.newInstance(IBond.class, a13, a12, IBond.Order.SINGLE);
        mol.addBond(b13);
        IBond b14 = builder.newInstance(IBond.class, a14, a13, IBond.Order.DOUBLE);
        b14.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addBond(b14);
        IBond b15 = builder.newInstance(IBond.class, a15, a14, IBond.Order.SINGLE);
        b15.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addBond(b15);
        IBond b16 = builder.newInstance(IBond.class, a16, a15, IBond.Order.DOUBLE);
        b16.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addBond(b16);
        IBond b17 = builder.newInstance(IBond.class, a17, a16, IBond.Order.SINGLE);
        b17.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addBond(b17);
        IBond b18 = builder.newInstance(IBond.class, a17, a13, IBond.Order.SINGLE);
        b18.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addBond(b18);
        IBond b19 = builder.newInstance(IBond.class, a18, a16, IBond.Order.SINGLE);
        mol.addBond(b19);
        IBond b20 = builder.newInstance(IBond.class, a19, a18, IBond.Order.SINGLE);
        mol.addBond(b20);
        IBond b21 = builder.newInstance(IBond.class, a20, a18, IBond.Order.DOUBLE);
        mol.addBond(b21);
        return mol;
    }

    static BitSet asBitSet(int... xs) {
        BitSet bs = new BitSet();
        for (int x : xs)
            bs.set(x);
        return bs;
    }
}
