/* Copyright (c) 2026  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version. All we ask is that proper credit is given
 * for our work, which includes - but is not limited to - adding the above
 * copyright notice to the beginning of your source code files, and to any
 * copyright notice that you may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */
package org.openscience.cdk.fingerprint;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smarts.SmartsPattern;
import org.openscience.cdk.smiles.SmilesParser;

/**
 * The fingerprinter skips SMARTS needing an element the molecule lacks. This
 * must never change a result: the bits and the counts are compared with
 * matching every SMARTS directly (no skipping).
 */
class SubstructureFingerprinterElementFilterTest {

    private static final String[] SMARTS = {
        "C", "N", "O", "S", "[Cl]", "Br", "[#0]", "[H]", "*", "a", "[R]",
        "[C,N]", "[C,c]", "[Cl,Br,I]", "[!C]", "[#6;!#6]",
        "C=O", "C(=O)[OH]", "[NX3][CX3]=[OX1]", "c1ccccc1", "c1ccncc1", "[nH]",
        "[$(C=O)]", "[C;!$(C=O)]", "[$(C=O),N]", "[$(C=O),$(CN)]", "[$([N,O]C)]",
        "[#7;$(N-C=O),$(N-S=O)]", "[Na+]", "[O-]", "[#26]", "[#127]", "[#128]",
        "C.N", "CC", "[N+](=O)[O-]", "S(=O)(=O)N", "P", "[Si]", "[Se,se]"
    };

    private static final String[] MOLECULES = {
        "C", "CC", "CCO", "CC(=O)O", "CC(=O)N", "NC(=O)C", "c1ccccc1", "c1ccccc1O",
        "c1ccncc1", "c1cc[nH]c1", "c1ccsc1", "ClC(Cl)Cl", "BrCCBr", "ICC",
        "CS(=O)(=O)N", "CP(=O)(O)O", "C[Si](C)(C)C", "C[Se]C", "[Na+].[Cl-]",
        "[Fe+2]", "[H]C([H])([H])O", "[H][H]", "[O-][N+](=O)c1ccccc1", "N#N",
        "O", "S", "CN(C)C=O", "OC(=O)c1ccccc1N", "[Na+].[O-]C"
    };

    @Test
    void bitsAndCountsMatchDirectMatching() throws Exception {
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SubstructureFingerprinter fpr = new SubstructureFingerprinter(SMARTS);
        SmartsPattern[] oracle = new SmartsPattern[SMARTS.length];
        for (int i = 0; i < SMARTS.length; i++)
            oracle[i] = SmartsPattern.create(SMARTS[i], SilentChemObjectBuilder.getInstance());
        int set = 0;
        for (String smi : MOLECULES) {
            IAtomContainer mol = smipar.parseSmiles(smi);
            java.util.BitSet bits = fpr.getBitFingerprint(mol).asBitSet();
            ICountFingerprint counts = fpr.getCountFingerprint(mol);
            for (int i = 0; i < SMARTS.length; i++) {
                boolean expectBit = oracle[i].matches(mol);
                int expectCount = oracle[i].matchAll(mol).countUnique();
                Assertions.assertEquals(expectBit, bits.get(i), SMARTS[i] + " on " + smi);
                Assertions.assertEquals(expectCount, counts.getCountForHash(i), SMARTS[i] + " (count) on " + smi);
                if (expectBit) set++;
            }
        }
        Assertions.assertTrue(set > 100, "too few set bits to be a meaningful test: " + set);
    }

    @Test
    void moleculeWithoutAtoms() throws Exception {
        SubstructureFingerprinter fpr = new SubstructureFingerprinter(new String[]{"C", "*", "[#0]"});
        IAtomContainer empty = SilentChemObjectBuilder.getInstance().newAtomContainer();
        Assertions.assertTrue(fpr.getBitFingerprint(empty).asBitSet().isEmpty());
    }
}
