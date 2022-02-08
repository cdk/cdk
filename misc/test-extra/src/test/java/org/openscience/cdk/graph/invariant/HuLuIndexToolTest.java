/*
 * =====================================
 *  Copyright (c) 2022 NextMove Software
 * =====================================
 */

package org.openscience.cdk.graph.invariant;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class HuLuIndexToolTest {

    @Test
    public void testFigure2Weights() throws Exception {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        try (MDLV2000Reader mdlr = new MDLV2000Reader(getClass().getResourceAsStream("hulu_fig2.mol"))) {
            IAtomContainer mol = mdlr.read(bldr.newAtomContainer());
            double[] expected = new double[]{
                    8.092, 6.05, 3.581, 3.581, 4.6061, 4.6061, 6.8892, 1.6792
            };
            double[] actual = HuLuIndexTool.getAtomWeights(mol);
            Assert.assertArrayEquals(expected, actual, 0.0001);
        }
    }

    @Test
    public void testFigure2ExtendedMatrix() throws Exception {
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
    public void testFigure2EAID() throws Exception {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        try (MDLV2000Reader mdlr = new MDLV2000Reader(getClass().getResourceAsStream("hulu_fig2.mol"))) {
            IAtomContainer mol = mdlr.read(bldr.newAtomContainer());
            Assert.assertEquals(17.62199170, HuLuIndexTool.getEAIDNumber(mol), 0.0001);
        }
    }

    /**
     * https://github.com/cdk/cdk/issues/737
     */
    @Test
    public void bug737() throws InvalidSmilesException {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        SmilesParser smipar = new SmilesParser(bldr);
        IAtomContainer mol = smipar.parseSmiles("Cl.O=S(=O)(C1=CC=C(NN)C(=C1)C)C");
        double[] cdkWeights = HuLuIndexTool.getAtomWeights(mol);
        double[] expected = new double[]{
                6.0, 7.6603, 16.403043, 7.6603, 10.42043, 5.80979, 5.6645, 8.91325, 5.639325, 3.4639325, 7.7065, 6.2803, 1.66065, 2.4203043
        };
        double[] actual = HuLuIndexTool.getAtomWeights(mol);
        Assert.assertArrayEquals(expected, actual, 0.0001);
    }

    private void assertMatrixEquals(double[][] expected, double[][] actual, double epsilon) {
        assertEquals(expected.length, actual.length);
        for (int i = 0; i < expected.length; i++) {
            assertArrayEquals(expected[i], actual[i], epsilon);
        }
    }
}