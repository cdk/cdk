/* Copyright (C) 1997-2007  The Chemistry Development Kit (CKD) project
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
 *
 */

package org.openscience.cdk.similarity;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.fingerprint.BitSetFingerprint;
import org.openscience.cdk.fingerprint.Fingerprinter;
import org.openscience.cdk.fingerprint.ICountFingerprint;
import org.openscience.cdk.fingerprint.IntArrayCountFingerprint;
import org.openscience.cdk.fingerprint.IntArrayFingerprint;
import org.openscience.cdk.fingerprint.LingoFingerprinter;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.templates.TestMoleculeFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @cdk.module test-fingerprint
 */
public class TanimotoTest extends CDKTestCase {

    boolean standAlone = false;

    @Test
    public void testTanimoto1() throws java.lang.Exception {
        IAtomContainer mol1 = TestMoleculeFactory.makeIndole();
        IAtomContainer mol2 = TestMoleculeFactory.makePyrrole();
        Fingerprinter fingerprinter = new Fingerprinter(1024, 8);
        BitSet bs1 = fingerprinter.getBitFingerprint(mol1).asBitSet();
        BitSet bs2 = fingerprinter.getBitFingerprint(mol2).asBitSet();
        float tanimoto = Tanimoto.calculate(bs1, bs2);
        if (standAlone) System.out.println("Tanimoto: " + tanimoto);
        if (!standAlone) Assert.assertEquals(0.3939, tanimoto, 0.01);
    }

    @Test
    public void testTanimoto2() throws java.lang.Exception {
        IAtomContainer mol1 = TestMoleculeFactory.makeIndole();
        IAtomContainer mol2 = TestMoleculeFactory.makeIndole();
        Fingerprinter fingerprinter = new Fingerprinter();
        BitSet bs1 = fingerprinter.getBitFingerprint(mol1).asBitSet();
        BitSet bs2 = fingerprinter.getBitFingerprint(mol2).asBitSet();
        float tanimoto = Tanimoto.calculate(bs1, bs2);
        if (standAlone) System.out.println("Tanimoto: " + tanimoto);
        if (!standAlone) Assert.assertEquals(1.0, tanimoto, 0.001);
    }

    @Test
    public void testCalculate_BitFingerprint() throws java.lang.Exception {
        IAtomContainer mol1 = TestMoleculeFactory.makeIndole();
        IAtomContainer mol2 = TestMoleculeFactory.makePyrrole();
        Fingerprinter fp = new Fingerprinter(1024, 8);
        double similarity = Tanimoto.calculate(fp.getBitFingerprint(mol1), fp.getBitFingerprint(mol2));
        Assert.assertEquals(0.3939, similarity, 0.01);
    }

    @Test
    public void testExactMatch() throws Exception {
        IAtomContainer mol1 = TestMoleculeFactory.makeIndole();
        IAtomContainer mol2 = TestMoleculeFactory.makeIndole();
        addImplicitHydrogens(mol1);
        addImplicitHydrogens(mol2);
        LingoFingerprinter fingerprinter = new LingoFingerprinter();
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol1);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol2);
        Map<String, Integer> feat1 = fingerprinter.getRawFingerprint(mol1);
        Map<String, Integer> feat2 = fingerprinter.getRawFingerprint(mol2);
        float tanimoto = Tanimoto.calculate(feat1, feat2);
        Assert.assertEquals(1.0, tanimoto, 0.001);

    }

    @Test
    public void testTanimoto3() throws java.lang.Exception {
        double[] f1 = {1, 2, 3, 4, 5, 6, 7};
        double[] f2 = {1, 2, 3, 4, 5, 6, 7};
        float tanimoto = Tanimoto.calculate(f1, f2);
        if (standAlone) System.out.println("Tanimoto: " + tanimoto);
        if (!standAlone) Assert.assertEquals(1.0, tanimoto, 0.001);
    }

    @Test
    public void keggR00258() throws java.lang.Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        String smiles1 = "O=C(O)CCC(=O)C(=O)O";
        String smiles2 = "O=C(O)C(N)CCC(=O)O";
        String smiles3 = "O=C(O)C(N)C";
        String smiles4 = "CC(=O)C(=O)O";
        IAtomContainer molecule1 = sp.parseSmiles(smiles1);
        IAtomContainer molecule2 = sp.parseSmiles(smiles2);
        IAtomContainer molecule3 = sp.parseSmiles(smiles3);
        IAtomContainer molecule4 = sp.parseSmiles(smiles4);
        Fingerprinter fingerprinter = new Fingerprinter(1024, 6);
        BitSet bs1 = fingerprinter.getBitFingerprint(molecule1).asBitSet();
        BitSet bs2 = fingerprinter.getBitFingerprint(molecule2).asBitSet();
        BitSet bs3 = fingerprinter.getBitFingerprint(molecule3).asBitSet();
        BitSet bs4 = fingerprinter.getBitFingerprint(molecule4).asBitSet();

        assertThat((double) Tanimoto.calculate(bs1, bs2), is(closeTo(0.75, 0.1)));
        assertThat((double) Tanimoto.calculate(bs1, bs3), is(closeTo(0.46, 0.1)));
        assertThat((double) Tanimoto.calculate(bs1, bs4), is(closeTo(0.52, 0.1)));
        assertThat((double) Tanimoto.calculate(bs2, bs3), is(closeTo(0.53, 0.1)));
        assertThat((double) Tanimoto.calculate(bs2, bs4), is(closeTo(0.42, 0.1)));
        assertThat((double) Tanimoto.calculate(bs3, bs4), is(closeTo(0.8, 0.1)));
    }

    @Test
    public void method1() throws CDKException {
        ICountFingerprint fp1 = new IntArrayCountFingerprint(new HashMap<String, Integer>() {

            {
                put("A", 3);
            }
        });
        ICountFingerprint fp2 = new IntArrayCountFingerprint(new HashMap<String, Integer>() {

            {
                put("A", 4);
            }
        });
        Assert.assertEquals(0.923, Tanimoto.method1(fp1, fp2), 0.001);
    }

    @Test
    public void method2() throws CDKException {
        ICountFingerprint fp1 = new IntArrayCountFingerprint(new HashMap<String, Integer>() {

            {
                put("A", 3);
            }
        });
        ICountFingerprint fp2 = new IntArrayCountFingerprint(new HashMap<String, Integer>() {

            {
                put("A", 4);
            }
        });
        Assert.assertEquals(0.75, Tanimoto.method2(fp1, fp2), 0.001);
    }

    @Test
    public void testCompareBitSetandBitFingerprintTanimoto() throws Exception {
        IAtomContainer mol1 = TestMoleculeFactory.make123Triazole();
        IAtomContainer mol2 = TestMoleculeFactory.makeImidazole();
        Fingerprinter fingerprinter = new Fingerprinter(1024, 8);
        BitSet bs1 = fingerprinter.getBitFingerprint(mol1).asBitSet();
        BitSet bs2 = fingerprinter.getBitFingerprint(mol2).asBitSet();
        float tanimoto = Tanimoto.calculate(bs1, bs2);

        BitSetFingerprint fp1 = new BitSetFingerprint(bs1);
        BitSetFingerprint fp2 = new BitSetFingerprint(bs2);

        double tanimoto2 = Tanimoto.calculate(fp1, fp2);
        Assert.assertEquals(tanimoto, tanimoto2, 0.01);

        IntArrayFingerprint ifp1 = new IntArrayFingerprint(fp1);
        IntArrayFingerprint ifp2 = new IntArrayFingerprint(fp2);

        tanimoto2 = Tanimoto.calculate(ifp1, ifp2);
        Assert.assertEquals(tanimoto, tanimoto2, 0.01);
    }

}
