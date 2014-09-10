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
public class BondEquitablePartitionRefinerTest extends CDKTestCase {

    public MockBondRefiner makeExampleTable() {
        int[][] table = new int[4][];
        table[0] = new int[]{1, 2};
        table[1] = new int[]{0, 3};
        table[2] = new int[]{0, 3};
        table[3] = new int[]{1, 2};
        return new MockBondRefiner(table);
    }

    public class MockBondRefiner extends BondDiscretePartitionRefiner {

        public int[][] connections;

        public MockBondRefiner(int[][] connections) {
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

    }

    @Test
    public void constructorTest() {
        BondEquitablePartitionRefiner refiner = new BondEquitablePartitionRefiner(makeExampleTable());
        Assert.assertNotNull(refiner);
    }

    @Test
    public void getVertexCountTest() {
        BondEquitablePartitionRefiner refiner = new BondEquitablePartitionRefiner(makeExampleTable());
        Assert.assertEquals(4, refiner.getVertexCount());
    }

    @Test
    public void neighboursInBlockTest() {
        BondEquitablePartitionRefiner refiner = new BondEquitablePartitionRefiner(makeExampleTable());
        Set<Integer> block = new HashSet<Integer>();
        block.add(1);
        block.add(2);
        block.add(3);
        Assert.assertEquals(2, refiner.neighboursInBlock(block, 0));
    }

    @Test
    public void refineTest() {
        BondEquitablePartitionRefiner refiner = new BondEquitablePartitionRefiner(makeExampleTable());
        Partition coarser = Partition.fromString("[0|1,2,3]");
        Partition finer = refiner.refine(coarser);
        Partition expected = Partition.fromString("[0|1,2|3]");
        Assert.assertEquals(expected, finer);
    }

}
