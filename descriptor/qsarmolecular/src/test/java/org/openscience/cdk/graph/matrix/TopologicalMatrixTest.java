package org.openscience.cdk.graph.matrix;

import java.io.InputStream;

import org.junit.Assert;
import org.junit.Ignore;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.io.MDLV2000Reader;

/**
 * @cdk.module test-qsarmolecular
 */
public class TopologicalMatrixTest extends CDKTestCase {

    @Ignore // not actually asserting anything
    public void testTopologicalMatrix_IAtomContainer() throws Exception {
        String filename = "data/mdl/chlorobenzene.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        IAtomContainer container = reader.read(new AtomContainer());
        int[][] matrix = TopologicalMatrix.getMatrix(container);
        Assert.assertEquals(12, matrix.length);
        for (int[] ints : matrix) {

            System.out.println("");

            for (int j = 0; j < matrix.length; j++) {
                System.out.print(ints[j] + " ");
            }
        }
    System.out.print("\n");
    }
}
