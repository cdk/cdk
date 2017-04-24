package org.openscience.cdk.group;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;

import org.junit.Test;
import org.openscience.cdk.CDKTestCase;

/**
 * @author maclean
 * @cdk.module test-group
 */
public class EquitablePartitionRefinerTest extends CDKTestCase {

    public MockRefinable makeExampleTable() {
        int[][] table = new int[4][];
        table[0] = new int[]{1, 2};
        table[1] = new int[]{0, 3};
        table[2] = new int[]{0, 3};
        table[3] = new int[]{1, 2};
        return new MockRefinable(table);
    }

    public class MockRefinable implements Refinable {

        public int[][] connections;

        public MockRefinable(int[][] connections) {
            this.connections = connections;
        }

        @Override
        public int getVertexCount() {
            return connections.length;
        }

        @Override
        public int[] getConnectedIndices(int vertexI) {
            return connections[vertexI];
        }

        @Override
        public int getConnectivity(int vertexI, int vertexJ) {
            for (int connected : connections[vertexI]) {
                if (connected == vertexJ) {
                    return 1;
                }
            }
            return 0;
        }

        @Override
        public int getMaxConnectivity() {
            return 1;
        }

    }

    @Test
    public void constructorTest() {
        EquitablePartitionRefiner refiner = new EquitablePartitionRefiner(makeExampleTable());
        Assert.assertNotNull(refiner);
    }

    @Test
    public void getVertexCountTest() {
        EquitablePartitionRefiner refiner = new EquitablePartitionRefiner(makeExampleTable());
        Assert.assertEquals(4, refiner.getVertexCount());
    }

    @Test
    public void neighboursInBlockTest() {
        EquitablePartitionRefiner refiner = new EquitablePartitionRefiner(makeExampleTable());
        Set<Integer> block = new HashSet<Integer>();
        block.add(1);
        block.add(2);
        block.add(3);
        Assert.assertEquals(new IntegerInvariant(2), refiner.neighboursInBlock(block, 0));
    }

    @Test
    public void refineTest() {
        EquitablePartitionRefiner refiner = new EquitablePartitionRefiner(makeExampleTable());
        Partition coarser = Partition.fromString("[0|1,2,3]");
        Partition finer = refiner.refine(coarser);
        Partition expected = Partition.fromString("[0|1,2|3]");
        Assert.assertEquals(expected, finer);
    }

}
