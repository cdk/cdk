/* Copyright (C) 2017  Gilleain Torrance <gilleain.torrance@gmail.com>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
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
