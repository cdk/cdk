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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.ISimpleChemObjectReader;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @cdk.module test-standard
 */
class GraphOnlyFingerprinterTest extends AbstractFixedLengthFingerprinterTest {

    private static final ILoggingTool logger = LoggingToolFactory.createLoggingTool(FingerprinterTest.class);

    @Override
    public IFingerprinter getBitFingerprinter() {
        return new GraphOnlyFingerprinter();
    }

    @Test
    void testFingerprint() throws Exception {
        SmilesParser parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IFingerprinter printer = new GraphOnlyFingerprinter();

        IBitFingerprint bs1 = printer.getBitFingerprint(parser.parseSmiles("C=C-C#N"));
        IBitFingerprint bs2 = printer.getBitFingerprint(parser.parseSmiles("CCCN"));

        Assertions.assertEquals(bs1, bs2);
    }

    /* ethanolamine */
    private static final String ethanolamine    = "\n\n\n  4  3  0     0  0  0  0  0  0  1 V2000\n    2.5187   -0.3500    0.0000 N   0  0  0  0  0  0  0  0  0  0\n    0.0938   -0.3500    0.0000 C   0  0  0  0  0  0  0  0  0  0\n    1.3062    0.3500    0.0000 C   0  0  0  0  0  0  0  0  0  0\n   -1.1187    0.3500    0.0000 O   0  0  0  0  0  0  0  0  0  0\n  2  3  1  0  0  0  0\n  2  4  1  0  0  0  0\n  1  3  1  0  0  0  0\nM  END\n";

    /* 2,4-diamino-5-hydroxypyrimidin-dihydrochlorid */
    private static final String molecule_test_2 = "\n\n\n 13 11  0     0  0  0  0  0  0  1 V2000\n   -0.5145   -1.0500    0.0000 C   0  0  0  0  0  0  0  0  0  0\n   -1.7269   -1.7500    0.0000 N   0  0  0  0  0  0  0  0  0  0\n   -2.9393   -1.0500    0.0000 C   0  0  0  0  0  0  0  0  0  0\n   -2.9393    0.3500    0.0000 C   0  0  0  0  0  0  0  0  0  0\n   -1.7269    1.0500    0.0000 C   0  0  0  0  0  0  0  0  0  0\n   -0.5145    0.3500    0.0000 N   0  0  0  0  0  0  0  0  0  0\n   -4.1518    1.0500    0.0000 O   0  0  0  0  0  0  0  0  0  0\n   -4.1518   -1.7500    0.0000 N   0  0  0  0  0  0  0  0  0  0\n    0.6980   -1.7500    0.0000 N   0  0  0  0  0  0  0  0  0  0\n   -4.1518    2.4500    0.0000 H   0  0  0  0  0  1  0  0  0  0\n   -5.3642    3.1500    0.0000 Cl  0  0  0  0  0  0  0  0  0  0\n   -4.1518   -3.1500    0.0000 H   0  0  0  0  0  1  0  0  0  0\n   -5.3642   -3.8500    0.0000 Cl  0  0  0  0  0  0  0  0  0  0\n  1  2  1  0  0  0  0\n  2  3  2  0  0  0  0\n  3  4  1  0  0  0  0\n  4  5  2  0  0  0  0\n  5  6  1  0  0  0  0\n  1  6  2  0  0  0  0\n  4  7  1  0  0  0  0\n  3  8  1  0  0  0  0\n  1  9  1  0  0  0  0\n 10 11  1  0  0  0  0\n 12 13  1  0  0  0  0\nM  END\n";

    /**
     * This basic test case shows that some molecules will not be considered
     * as a subset of each other by Fingerprint.isSubset(), for the getBitFingerprint(),
     * despite the fact that they are a sub graph of each other according to the
     * UniversalIsomorphismTester.isSubgraph().
     *
     * @author Hugo Lafayette &lt;hugo.lafayette@laposte.net&gt;
     *
     * @throws  CloneNotSupportedException
     * @throws  Exception
     *
     * @cdk.bug 1626894
     *
     */
    @Test
    void testFingerPrint() throws Exception {
        IFingerprinter printer = new GraphOnlyFingerprinter();

        IAtomContainer mol1 = createMolecule(molecule_test_2);
        IAtomContainer mol2 = createMolecule(ethanolamine);
        Assertions.assertTrue(new UniversalIsomorphismTester().isSubgraph(mol1, mol2), "SubGraph does NOT match");

        BitSet bs1 = printer.getBitFingerprint(mol1.clone()).asBitSet();
        BitSet bs2 = printer.getBitFingerprint(mol2.clone()).asBitSet();

        Assertions.assertTrue(FingerprinterTool.isSubset(bs1, bs2), "Subset (with fingerprint) does NOT match");

        // Match OK
        logger.debug("Subset (with fingerprint) does match");
    }

    private static IAtomContainer createMolecule(String molecule) throws IOException, CDKException {
        IAtomContainer structure = null;
        if (molecule != null) {
            ISimpleChemObjectReader reader = new MDLV2000Reader(new StringReader(molecule));
            Assertions.assertNotNull(reader, "Could not create reader");
            if (reader.accepts(AtomContainer.class)) {
                structure = reader.read(DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class));
            }
        }
        return structure;
    }

    @Test
    public void testGetRawFingerprint() throws CDKException {
        final SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        GraphOnlyFingerprinter fpr = new GraphOnlyFingerprinter(1024, 7); // 7 bonds
        fpr.setPathLimit(2000);
        final String smi  = "CC(=O)OC1=CC=CC=C1C(=O)O";
        IAtomContainer mol  = smipar.parseSmiles(smi);
        Map<String,Integer> actual = fpr.getRawFingerprint(mol);
        Map<String,Integer> expected = new HashMap<>();
        expected.put("C", 9);
        expected.put("O", 4);
        expected.put("OC", 5);
        expected.put("CC", 8);
        expected.put("COC", 1);
        expected.put("CCC", 8);
        expected.put("OCC", 6);
        expected.put("OCO", 2);
        expected.put("OCOC", 1);
        expected.put("OCCC", 7);
        expected.put("COCC", 3);
        expected.put("CCCC", 8);
        expected.put("CCOCC", 2);
        expected.put("CCCCC", 8);
        expected.put("COCCC", 3);
        expected.put("OCOCC", 2);
        expected.put("OCCCO", 2);
        expected.put("OCCCC", 6);
        expected.put("CCCCCC", 8);
        expected.put("OCCCCC", 6);
        expected.put("OCCCOC", 2);
        expected.put("COCCCC", 2);
        expected.put("OCOCCC", 3);
        expected.put("CCOCCC", 3);
        expected.put("CCCCCCC", 2);
        expected.put("COCCCCC", 2);
        expected.put("OCCCOCC", 2);
        expected.put("OCOCCCO", 2);
        expected.put("OCCCCCC", 6);
        expected.put("CCOCCCC", 2);
        expected.put("OCOCCCC", 2);
        expected.put("COCCCCCC", 2);
        expected.put("OCCCCCCC", 5);
        expected.put("OCOCCCCC", 2);
        expected.put("CCOCCCCC", 2);
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
        GraphOnlyFingerprinter fpr = new GraphOnlyFingerprinter(1024, 7); // 7 bonds
        fpr.setPathLimit(2000);
        final String smi  = "CC(=O)OC1=CC=CC=C1C(=O)O";
        IAtomContainer mol  = smipar.parseSmiles(smi);
        ICountFingerprint cntFp = fpr.getCountFingerprint(mol);
        IBitFingerprint expBitset = fpr.getBitFingerprint(mol);
        Assertions.assertEquals(35, cntFp.numOfPopulatedbins());
        BitSet actBitset = foldFp(cntFp, 1024);
        Assertions.assertEquals(expBitset.asBitSet(), actBitset);
    }
}
