package org.openscience.cdk.group;

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
        public Partition getInitialPartition() {
            return Partition.unit(getVertexCount());
        }

        @Override
        public Invariant neighboursInBlock(Set<Integer> block, int vertexIndex) {
            int neighbours = 0;
            for (int connected : getConnectedIndices(vertexIndex)) {
                if (block.contains(connected)) {
                    neighbours++;
                }
            }
            return new IntegerInvariant(neighbours);
        }

    }

    @Test
    public void constructorTest() {
        EquitablePartitionRefiner refiner = new EquitablePartitionRefiner(makeExampleTable());
        Assert.assertNotNull(refiner);
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
