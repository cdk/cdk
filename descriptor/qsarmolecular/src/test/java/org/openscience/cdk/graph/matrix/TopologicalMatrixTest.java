package org.openscience.cdk.graph.matrix;

import java.io.InputStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.io.MDLV2000Reader;

/**
 * @cdk.module test-qsarmolecular
 */
class TopologicalMatrixTest extends CDKTestCase {

    @Disabled // not actually asserting anything
    void testTopologicalMatrix_IAtomContainer() throws Exception {
        String filename = "data/mdl/chlorobenzene.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        IAtomContainer container = reader.read(new AtomContainer());
        int[][] matrix = TopologicalMatrix.getMatrix(container);
        Assertions.assertEquals(12, matrix.length);
        for (int[] ints : matrix) {

            System.out.println("");

            for (int j = 0; j < matrix.length; j++) {
                System.out.print(ints[j] + " ");
            }
        }
    System.out.print("\n");
    }
}
