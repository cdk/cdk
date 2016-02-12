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
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.fingerprint.IBitFingerprint;
import org.openscience.cdk.fingerprint.ICountFingerprint;
import org.openscience.cdk.fingerprint.IntArrayCountFingerprint;
import org.openscience.cdk.fingerprint.SignatureFingerprinter;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.templates.TestMoleculeFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @cdk.module test-signature
 */
public class SignatureFingerprintTanimotoTest extends CDKTestCase {

    /**
     * @throws Exception
     * @cdk.bug 3310138
     */
    @Test
    public void testRawTanimotoBetween0and1() throws Exception {
        SmilesParser smilesParser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol1 = smilesParser.parseSmiles("Cc1nc(C(=O)NC23CC4CC(CC(C4)C2)C3)c(C)n1C5CCCCC5");
        IAtomContainer mol2 = smilesParser
                .parseSmiles("CS(=O)(=O)Nc1ccc(Cc2onc(n2)c3ccc(cc3)S(=O)(=O)Nc4ccc(CCNC[C@H](O)c5cccnc5)cc4)cc1");
        SignatureFingerprinter fingerprinter = new SignatureFingerprinter(0);
        Map<String, Integer> fp1 = fingerprinter.getRawFingerprint(mol1);
        Map<String, Integer> fp2 = fingerprinter.getRawFingerprint(mol2);
        float tanimoto = Tanimoto.calculate(fp1, fp2);
        Assert.assertTrue("Tanimoto expected to be between 0 and 1, was:" + tanimoto, tanimoto > 0 && tanimoto < 1);
    }

    @Test
    public void testICountFingerprintComparison() throws Exception {
        IAtomContainer mol1 = TestMoleculeFactory.makeIndole();
        IAtomContainer mol2 = TestMoleculeFactory.makeIndole();
        SignatureFingerprinter fingerprinter = new SignatureFingerprinter();
        ICountFingerprint fp1 = fingerprinter.getCountFingerprint(mol1);
        ICountFingerprint fp2 = fingerprinter.getCountFingerprint(mol2);
        double tanimoto = Tanimoto.calculate(fp1, fp2);
        Assert.assertEquals(1.0, tanimoto, 0.001);

    }

    @Test
    public void compareCountFingerprintAndRawFingerprintTanimoto() throws CDKException {
        IAtomContainer mol1 = TestMoleculeFactory.make123Triazole();
        IAtomContainer mol2 = TestMoleculeFactory.makeImidazole();
        SignatureFingerprinter fingerprinter = new SignatureFingerprinter(1);
        ICountFingerprint countFp1 = fingerprinter.getCountFingerprint(mol1);
        ICountFingerprint countFp2 = fingerprinter.getCountFingerprint(mol2);
        Map<String, Integer> feat1 = fingerprinter.getRawFingerprint(mol1);
        Map<String, Integer> feat2 = fingerprinter.getRawFingerprint(mol2);
        float rawTanimoto = Tanimoto.calculate(feat1, feat2);
        double countTanimoto = Tanimoto.method1(countFp1, countFp2);
        Assert.assertEquals(rawTanimoto, countTanimoto, 0.001);
    }

    @Test
    public void testCountMethod1and2() throws CDKException {
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
        Assert.assertEquals(0.75, Tanimoto.method2(fp1, fp2), 0.001);

        IAtomContainer mol1 = TestMoleculeFactory.makeIndole();
        IAtomContainer mol2 = TestMoleculeFactory.makeIndole();
        SignatureFingerprinter fingerprinter = new SignatureFingerprinter();
        fp1 = fingerprinter.getCountFingerprint(mol1);
        fp2 = fingerprinter.getCountFingerprint(mol2);
        Assert.assertEquals(1.0, Tanimoto.method1(fp1, fp2), 0.001);
        Assert.assertEquals(1.0, Tanimoto.method2(fp1, fp2), 0.001);
    }

    @Test
    public void testComparingBitFingerprintAndCountBehavingAsBit() throws Exception {
        IAtomContainer mol1 = TestMoleculeFactory.make123Triazole();
        IAtomContainer mol2 = TestMoleculeFactory.makeImidazole();

        SignatureFingerprinter fingerprinter = new SignatureFingerprinter(1);
        ICountFingerprint countFp1 = fingerprinter.getCountFingerprint(mol1);
        ICountFingerprint countFp2 = fingerprinter.getCountFingerprint(mol2);
        countFp1.setBehaveAsBitFingerprint(true);
        countFp2.setBehaveAsBitFingerprint(true);
        IBitFingerprint bitFp1 = fingerprinter.getBitFingerprint(mol1);
        IBitFingerprint bitFp2 = fingerprinter.getBitFingerprint(mol2);
        double bitTanimoto = Tanimoto.calculate(bitFp1, bitFp2);
        double countTanimoto1 = Tanimoto.method1(countFp1, countFp2);
        double countTanimoto2 = Tanimoto.method2(countFp1, countFp2);

        Assert.assertEquals(countTanimoto1, countTanimoto2, 0.001);
        Assert.assertEquals(bitTanimoto, countTanimoto1, 0.001);
    }
}
