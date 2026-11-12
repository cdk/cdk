package org.openscience.cdk.renderer.generators.standard;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.openscience.cdk.renderer.generators.standard.DoubleBondAlign.Left;
import static org.openscience.cdk.renderer.generators.standard.DoubleBondAlign.Right;

class DoubleBondAlignerTest {

    static void assertAlignment(String smi, DoubleBondAlign ... expected) throws InvalidSmilesException {
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = smipar.parseSmiles(smi);
        Cycles.markRingAtomsAndBonds(mol);
        List<IBond> dbs = new ArrayList<>();
        for (IBond bond : mol.bonds()) {
            if (bond.getOrder() == IBond.Order.DOUBLE) {
                dbs.add(bond);
            }
        }

        DoubleBondAligner aligner = new DoubleBondAligner(mol,
                                                          new AtomSymbol[mol.getAtomCount()]);

        DoubleBondAlign[] actual = new DoubleBondAlign[dbs.size()];
        for (int i = 0; i < dbs.size(); i++)
            actual[i] = aligner.alignment(dbs.get(i));

        Assertions.assertArrayEquals(expected, actual,
                                     "Actual: " + Arrays.toString(actual));
    }

    @Test
    void testTrivialCases() throws InvalidSmilesException {
        assertAlignment("C=C", DoubleBondAlign.Center);
        assertAlignment("C=C(C)C", DoubleBondAlign.Center);
        assertAlignment("CC(C)=C(C)C", DoubleBondAlign.Center);
        assertAlignment("C=CC |(6.15,-10.92,;7.65,-10.92,;8.4,-9.62,)|",
                        Left);
        assertAlignment("C=CC |(6.15,-10.92,;7.65,-10.92,;8.4,-12.22,)|",
                        Right);
        // Left is also okay here, there is no preference
        assertAlignment("C(C)=CC |(6.15,-10.92,;5.4,-9.62,;7.65,-10.92,;8.4,-12.22,)|",
                        Right);
    }

    @Test
    void testBenzene() throws InvalidSmilesException {
        assertAlignment("C=1C=CC=CC1 |(9.74,-10.06,;11.03,-10.81,;12.33,-10.06,;12.33,-8.56,;11.03,-7.81,;9.74,-8.56,)|",
                        Left, Left, Right);
        // flipped coordinates
        assertAlignment("C=1C=CC=CC1 |(12.33,-10.06,;11.03,-10.81,;9.74,-10.06,;9.74,-8.56,;11.03,-7.81,;12.33,-8.56,)|",
                        Right, Right, Left);
    }

    @Test
    void test65fusion() throws InvalidSmilesException {
        assertAlignment("C1C2=C(CCCC2)CC1 |(13.76,-8.1,;12.33,-8.56,;12.33,-10.06,;11.03,-10.81,;9.74,-10.06,;9.74,-8.56,;11.03,-7.81,;13.76,-10.52,;14.64,-9.31,)|",
                        Right);
    }

    @Test
    void test66fusions() throws InvalidSmilesException {
        assertAlignment("C1CC2=C(CCCC2)CC1 |(14.93,-8.56,;13.63,-7.81,;12.33,-8.56,;12.33,-10.06,;11.03,-10.81,;9.74,-10.06,;9.74,-8.56,;11.03,-7.81,;13.63,-10.81,;14.93,-10.06,)|",
                        Left);
        // more double bonds flips the preference
        assertAlignment("C1=CC2=C(CCCC2)C=C1 |(12.39,-10.09,;11.09,-10.84,;9.8,-10.09,;9.8,-8.59,;8.5,-7.84,;7.2,-8.59,;7.2,-10.09,;8.5,-10.84,;11.09,-7.84,;12.39,-8.59,)|",
                        Right, Right, Right);
        // less hetero flips the preference
        assertAlignment("C1CC2=C(NCCC2)CC1 |(12.39,-10.09,;11.09,-10.84,;9.8,-10.09,;9.8,-8.59,;8.5,-7.84,;7.2,-8.59,;7.2,-10.09,;8.5,-10.84,;11.09,-7.84,;12.39,-8.59,)|",
                        Right);
    }

    @Test
    void test67fusion() throws InvalidSmilesException {
        assertAlignment("C1CCC2=C(CCCC2)CC1 |(12.45,-8.38,;11.8,-9.73,;10.34,-10.07,;9.17,-9.13,;9.17,-7.63,;7.87,-6.88,;6.57,-7.63,;6.57,-9.13,;7.87,-9.88,;10.34,-6.69,;11.8,-7.03,)|",
                        Left);
    }

    @Test
    void testAdenine() throws InvalidSmilesException {
        assertAlignment("N1=C2N=CNC2=C(N)N=C1 |(12.59,-11.07,;13.89,-10.32,;15.42,-10.7,;16.23,-9.45,;15.29,-8.28,;13.89,-8.82,;12.59,-8.07,;12.59,-6.57,;11.29,-8.82,;11.29,-10.32,)|",
                        Left, Left, Left, Left);
    }

    // we place the bond to the 5 member ring and not the 6
    @Test
    void testMetalPreference() throws InvalidSmilesException {
        assertAlignment("C=12C=CNC1C=C[Fe]C2 |(3.6,-1.75,;5.03,-1.29,;5.91,-2.5,;5.03,-3.71,;3.6,-3.25,;2.3,-4,;1,-3.25,;1,-1.75,;2.3,-1,)|",
                        Right, Left, Right);
    }

    @Test
    void testMacrocycle() throws InvalidSmilesException {
        assertAlignment("C=1C=CC=CCC=CCC=CC=CC1 |(13.25,-12.59,;14.55,-13.34,;14.55,-14.84,;13.25,-15.59,;11.95,-14.84,;11.95,-13.34,;10.65,-12.59,;10.65,-11.09,;11.95,-10.34,;11.95,-8.84,;13.25,-8.09,;14.55,-8.84,;14.55,-10.34,;13.25,-11.09,)|",
                        Right, Right, Right, Right, Right, Left);
        assertAlignment("C=1CC=CC=CC=CC=CC=CCC1 |(13.9,-9.17,;12.6,-8.42,;12.6,-6.92,;11.3,-6.17,;10.01,-6.92,;10.01,-8.42,;11.3,-9.17,;11.3,-10.67,;10.01,-11.42,;10.01,-12.92,;11.3,-13.67,;12.6,-12.92,;12.6,-11.42,;13.9,-10.67,)|",
                        Left, Left, Left, Left, Left, Right);
    }
}