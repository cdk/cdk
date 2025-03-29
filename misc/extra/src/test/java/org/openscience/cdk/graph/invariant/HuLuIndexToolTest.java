/*
 * Copyright (C) 2022 John Mayfield
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
 *
 */

package org.openscience.cdk.graph.invariant;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

class HuLuIndexToolTest {

    @Test
    void testFigure2Weights() throws Exception {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        try (MDLV2000Reader mdlr = new MDLV2000Reader(getClass().getResourceAsStream("hulu_fig2.mol"))) {
            IAtomContainer mol = mdlr.read(bldr.newAtomContainer());
            double[] expected = new double[]{
                    8.092, 6.05, 3.581, 3.581, 4.6061, 4.6061, 6.8892, 1.6792
            };
            double[] actual = HuLuIndexTool.getAtomWeights(mol);
            Assertions.assertArrayEquals(expected, actual, 0.0001);
        }
    }

    @Test
    void testFigure2ExtendedMatrix() throws Exception {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        try (MDLV2000Reader mdlr = new MDLV2000Reader(getClass().getResourceAsStream("hulu_fig2.mol"))) {
            IAtomContainer mol = mdlr.read(bldr.newAtomContainer());
            // note the diagonal values for Carbon are different but match
            // the paper for sqrt(0.74)/6 - if we reverse the expected value
            // of (0.14634434*6)^2 we get 0.7709 which doesn't seem right
            double[][] expected = new double[][]{
                    {0.14634434, 0.3368635,  0,          0,          0,          0,          0.47293125, 0.44179165},
                    {0.3368635,  0.14634434, 0.34485798, 0.34485798, 0,          0,          0,          0},
                    {0,          0.34485798, 0.14634434, 0,          0.33597735, 0,          0,          0},
                    {0,          0.34485798, 0,          0.14634434, 0,          0.33597735, 0,          0},
                    {0,          0,          0.33597735, 0,          0.14634434, 0.47140452, 0,          0},
                    {0,          0,          0,          0.33597735, 0.47140452, 0.14634434, 0,          0},
                    {0.47293125, 0,          0,          0,          0,          0,          0.14337209, 0},
                    {0.44179165, 0,          0,          0,          0,          0,          0,          0.14634434}
            };
            double[][] actual = HuLuIndexTool.getExtendedAdjacencyMatrix(mol);
            assertMatrixEquals(expected, actual, 0.00001);
        }
    }

    @Test
    void testFigure2EAID() throws Exception {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        try (MDLV2000Reader mdlr = new MDLV2000Reader(getClass().getResourceAsStream("hulu_fig2.mol"))) {
            IAtomContainer mol = mdlr.read(bldr.newAtomContainer());
            Assertions.assertEquals(17.62199170, HuLuIndexTool.getEAIDNumber(mol), 0.0001);
        }
    }

    /**
     * https://github.com/cdk/cdk/issues/737
     */
    @Test
    void bug737() throws InvalidSmilesException {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        SmilesParser smipar = new SmilesParser(bldr);
        IAtomContainer mol = smipar.parseSmiles("Cl.O=S(=O)(C1=CC=C(NN)C(=C1)C)C");
        double[] cdkWeights = HuLuIndexTool.getAtomWeights(mol);
        double[] expected = new double[]{
                6.0, 7.6603, 16.403043, 7.6603, 10.42043, 5.80979, 5.6645, 8.91325, 5.639325, 3.4639325, 7.7065, 6.2803, 1.66065, 2.4203043
        };
        double[] actual = HuLuIndexTool.getAtomWeights(mol);
        Assertions.assertArrayEquals(expected, actual, 0.0001);
    }

    private void assertMatrixEquals(double[][] expected, double[][] actual, double epsilon) {
        Assertions.assertEquals(expected.length, actual.length);
        for (int i = 0; i < expected.length; i++) {
            Assertions.assertArrayEquals(expected[i], actual[i], epsilon);
        }
    }
}