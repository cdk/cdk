/* Copyright (C) 2009-2011  Egon Willighagen <egonw@users.sf.net>
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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.fingerprint.AbstractFingerprinterTest;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @cdk.module test-standard
 */
class HybridizationFingerprinterTest extends AbstractFingerprinterTest {

    @Override
    public IFingerprinter getBitFingerprinter() {
        return new HybridizationFingerprinter();
    }

    @Test
    public void testGetRawFingerprint() throws CDKException {
        final SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        HybridizationFingerprinter fpr = new HybridizationFingerprinter(1024, 7); // 7 bonds
        fpr.setPathLimit(2000);
        final String smi  = "CC(=O)OC1=CC=CC=C1C(=O)O";
        IAtomContainer mol  = smipar.parseSmiles(smi);
        Map<String,Integer> actual = fpr.getRawFingerprint(mol);
        Map<String,Integer> expected = new HashMap<>();
        expected.put("C", 9);
        expected.put("O", 4);
        expected.put("C-C", 1);
        expected.put("O-C", 3);
        expected.put("C:C", 7);
        expected.put("O:C", 2);
        expected.put("O-C:C", 3);
        expected.put("O:C:C", 1);
        expected.put("O-C-C", 1);
        expected.put("C:C:C", 8);
        expected.put("O:C-O", 2);
        expected.put("O:C-C", 1);
        expected.put("C-O-C", 1);
        expected.put("C:C:C:C", 8);
        expected.put("O:C-O-C", 1);
        expected.put("O-C:C:C", 5);
        expected.put("C:C-O-C", 2);
        expected.put("C-O-C-C", 1);
        expected.put("O:C:C:C", 2);
        expected.put("O:C-O-C:C", 2);
        expected.put("C:C:C:C:C", 8);
        expected.put("O-C:C:C:C", 4);
        expected.put("C:C-O-C-C", 2);
        expected.put("O-C:C:C-O", 1);
        expected.put("O:C:C:C-O", 1);
        expected.put("O:C:C:C:C", 2);
        expected.put("C:C:C-O-C", 3);
        expected.put("C:C:C:C:C:C", 8);
        expected.put("O:C:C:C-O-C", 1);
        expected.put("O-C:C:C:C:C", 4);
        expected.put("O:C:C:C:C:C", 2);
        expected.put("O-C:C:C-O-C", 1);
        expected.put("C:C:C-O-C-C", 3);
        expected.put("O:C-O-C:C:C", 3);
        expected.put("C:C:C:C-O-C", 2);
        expected.put("O-C:C:C-O-C-C", 1);
        expected.put("C:C:C:C:C-O-C", 2);
        expected.put("C:C:C:C-O-C-C", 2);
        expected.put("O:C-O-C:C:C:C", 2);
        expected.put("C:C:C:C:C:C:C", 2);
        expected.put("O:C:C:C-O-C-C", 1);
        expected.put("O:C-O-C:C:C-O", 1);
        expected.put("O:C:C:C-O-C:O", 1);
        expected.put("O:C:C:C:C:C:C", 2);
        expected.put("O-C:C:C:C:C:C", 4);
        expected.put("O-C:C:C:C:C:C:C", 3);
        expected.put("O:C-O-C:C:C:C:C", 2);
        expected.put("O:C:C:C:C:C:C:C", 2);
        expected.put("C:C:C:C:C-O-C-C", 2);
        expected.put("C:C:C:C:C:C-O-C", 2);
        Assertions.assertEquals(expected, actual);
    }

    private BitSet foldFp(ICountFingerprint fp, int size) {
        BitSet bs = new BitSet();
        Random rand = new Random();
        for (int i = 0; i < fp.numOfPopulatedbins(); i++) {
            int hash = fp.getHash(i);
            rand.setSeed(hash);
            int hashFolded = rand.nextInt(1024);
            bs.set(hashFolded);
        }
        return bs;
    }

    @Test public void testGetCountFingerprint() throws CDKException {
        final SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        HybridizationFingerprinter fpr = new HybridizationFingerprinter(1024, 7); // 7 bonds
        fpr.setPathLimit(2000);
        final String smi  = "CC(=O)OC1=CC=CC=C1C(=O)O";
        IAtomContainer mol  = smipar.parseSmiles(smi);
        ICountFingerprint cntFp = fpr.getCountFingerprint(mol);
        IBitFingerprint expBitset = fpr.getBitFingerprint(mol);
        Assertions.assertEquals(50, cntFp.numOfPopulatedbins());
        BitSet actBitset = foldFp(cntFp, 1024);
        Assertions.assertEquals(expBitset.asBitSet(), actBitset);
    }

}
