/* Copyright (C) 2012  Gilleain Torrance <gilleain.torrance@gmail.com>
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

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.test.CDKTestCase;

/**
 * @author maclean
 * @cdk.module test-group
 *
 */
class PartitionTest extends CDKTestCase {

    @Test
    void emptyConstructor() {
        Partition p = new Partition();
        Assertions.assertEquals(0, p.size());
    }

    @Test
    void copyConstructor() {
        Partition p = new Partition();
        p.addCell(0, 1);
        p.addCell(2, 3);
        Partition q = new Partition(p);
        Assertions.assertEquals(p, q);
    }

    @Test
    void cellDataConstructor() {
        int[][] cellData = new int[][]{{0, 1}, {2, 3, 4}, {5, 6}};
        Partition p = new Partition(cellData);
        Assertions.assertEquals(cellData.length, p.size());
        Assertions.assertEquals(7, p.numberOfElements());
    }

    @Test
    void unitStaticConstructor() {
        int size = 5;
        Partition p = Partition.unit(size);
        Assertions.assertEquals(1, p.size());
        Assertions.assertEquals(size, p.getCell(0).size());
    }

    @Test
    void sizeTest() {
        Partition p = new Partition();
        p.addCell(0, 1);
        p.addCell(2, 3);
        Assertions.assertEquals(2, p.size());
        Assertions.assertEquals(2, p.getCell(0).size());
        Assertions.assertEquals(2, p.getCell(1).size());
    }

    @Test
    void numberOfElementsTest() {
        Partition p = new Partition();
        p.addCell(0, 1);
        p.addCell(2, 3);
        Assertions.assertEquals(4, p.numberOfElements());
    }

    @Test
    void isDiscreteTest() {
        int size = 5;
        Partition p = new Partition();
        for (int i = 0; i < size; i++) {
            p.addSingletonCell(i);
        }
        Assertions.assertTrue(p.isDiscrete());
    }

    @Test
    void toPermutationTest() {
        int size = 5;
        Partition partition = new Partition();
        for (int i = 0; i < size; i++) {
            partition.addSingletonCell(i);
        }
        Permutation permutation = partition.toPermutation();
        Assertions.assertEquals(size, permutation.size());
        for (int i = 0; i < size; i++) {
            Assertions.assertEquals(i, permutation.get(i));
        }
    }

    @Test
    void inOrderTest() {
        int[][] cellData = new int[][]{{0, 1}, {2, 3, 4}, {5, 6}};
        Partition p = new Partition(cellData);
        Assertions.assertTrue(p.inOrder());
    }

    @Test
    void getFirstInCellTest() {
        int[][] cellData = new int[][]{{0, 1}, {2, 3, 4}, {5, 6}};
        Partition p = new Partition(cellData);
        for (int i = 0; i < cellData.length; i++) {
            Assertions.assertEquals(cellData[i][0], p.getFirstInCell(i));
        }
    }

    @Test
    void getCellTest() {
        int[][] cellData = new int[][]{{0, 1}, {2, 3, 4}, {5, 6}};
        Partition p = new Partition(cellData);
        for (int i = 0; i < cellData.length; i++) {
            Integer[] cell = p.getCell(i).toArray(new Integer[]{});
            Assertions.assertEquals(cellData[i].length, cell.length);
            for (int j = 0; j < cell.length; j++) {
                Assertions.assertEquals(cellData[i][j], (int) cell[j]);
            }
        }
    }

    @Test
    void splitBeforeTest() {
        int[][] cellData = new int[][]{{0, 1}, {2, 3, 4}, {5, 6}};
        Partition p = new Partition(cellData);
        int cellIndex = 1;
        int splitElement = 3;
        Partition q = p.splitBefore(cellIndex, splitElement);
        Assertions.assertEquals(p.numberOfElements(), q.numberOfElements());
        Assertions.assertEquals(p.size() + 1, q.size());
        SortedSet<Integer> cell = q.getCell(cellIndex);
        Assertions.assertTrue(cell.size() == 1);
        Assertions.assertEquals(splitElement, (int) cell.first());
    }

    @Test
    void splitAfterTest() {
        int[][] cellData = new int[][]{{0, 1}, {2, 3, 4}, {5, 6}};
        Partition p = new Partition(cellData);
        int cellIndex = 1;
        int splitElement = 3;
        Partition q = p.splitAfter(cellIndex, splitElement);
        Assertions.assertEquals(p.numberOfElements(), q.numberOfElements());
        Assertions.assertEquals(p.size() + 1, q.size());
        SortedSet<Integer> cell = q.getCell(cellIndex + 1);
        Assertions.assertTrue(cell.size() == 1);
        Assertions.assertEquals(splitElement, (int) cell.first());
    }

    @Test
    void setAsPermutationTest() {
        int partitionSize = 5;
        int permutationSize = 3;
        Partition partition = new Partition();
        for (int i = 0; i < partitionSize; i++) {
            partition.addSingletonCell(i);
        }
        Permutation permutation = partition.setAsPermutation(permutationSize);
        for (int i = 0; i < permutationSize; i++) {
            Assertions.assertEquals(i, permutation.get(i));
        }
    }

    @Test
    void isDiscreteCellTest() {
        int[][] cellData = new int[][]{{0}, {1}, {2}, {3}, {4}, {5}};
        Partition p = new Partition(cellData);
        for (int i = 0; i < p.size(); i++) {
            Assertions.assertTrue(p.isDiscreteCell(i));
        }
    }

    @Test
    void getIndexOfFirstNonDiscreteCellTest() {
        int[][] cellData = new int[][]{{0}, {1}, {2, 3, 4}, {5, 6}};
        Partition p = new Partition(cellData);
        Assertions.assertEquals(2, p.getIndexOfFirstNonDiscreteCell());
    }

    @Test
    void addSingletonCellTest() {
        Partition p = new Partition();
        p.addSingletonCell(0);
        Assertions.assertEquals(1, p.size());
        Assertions.assertEquals(1, p.numberOfElements());
    }

    @Test
    void removeCellTest() {
        int size = 5;
        Partition p = Partition.unit(size);
        p.removeCell(0);
        Assertions.assertEquals(0, p.size());
    }

    @Test
    void addCell_VarArgsTest() {
        Partition p = new Partition();
        p.addCell(0, 1, 2);
        Assertions.assertEquals(1, p.size());
        Assertions.assertEquals(3, p.numberOfElements());
    }

    @Test
    void addCell_CollectionTest() {
        Partition p = new Partition();
        List<Integer> cell = new ArrayList<>();
        cell.add(0);
        cell.add(1);
        cell.add(2);
        p.addCell(cell);
        Assertions.assertEquals(1, p.size());
        Assertions.assertEquals(3, p.numberOfElements());
    }

    @Test
    void addToCellTest() {
        Partition p = new Partition();
        p.addToCell(0, 0);
        Assertions.assertEquals(1, p.size());
        Assertions.assertEquals(1, p.numberOfElements());
        p.addToCell(0, 1);
        Assertions.assertEquals(1, p.size());
        Assertions.assertEquals(2, p.numberOfElements());
    }

    @Test
    void insertCellTest() {
        int[][] cellData = new int[][]{{0}, {2}, {3}};
        Partition p = new Partition(cellData);
        SortedSet<Integer> cell = new TreeSet<>();
        cell.add(1);
        p.insertCell(1, cell);
        Assertions.assertTrue(p.isDiscrete());
    }

    @Test
    void copyBlockTest() {
        int[][] cellData = new int[][]{{0}, {1}, {2}};
        Partition p = new Partition(cellData);
        int cellIndex = 1;
        SortedSet<Integer> copyCell = p.copyBlock(cellIndex);
        SortedSet<Integer> refCell = p.getCell(cellIndex);
        Assertions.assertTrue(copyCell != refCell);
    }

    @Test
    void fromStringTest() {
        Partition p = Partition.fromString("[0,1|2,3]");
        Assertions.assertEquals(2, p.size());
        Assertions.assertEquals(4, p.numberOfElements());
    }

    @Test
    void fromStringTest2() {
        Partition p = Partition.fromString("[0|1,2,3]");
        Assertions.assertEquals(2, p.size());
        Assertions.assertEquals(4, p.numberOfElements());
    }

    @Test
    void equalsTest_null() {
        Partition p = new Partition(new int[][]{{0}, {1}});
        Assertions.assertNotSame(p, null);
    }

    @Test
    void equalsTest_different() {
        Partition p = new Partition(new int[][]{{0}, {1}});
        Partition o = new Partition(new int[][]{{1}, {0}});
        Assertions.assertNotSame(p, o);
    }

    @Test
    void equalsTest() {
        Partition p = new Partition(new int[][]{{0}, {1}});
        Partition o = new Partition(new int[][]{{0}, {1}});
        Assertions.assertEquals(p, o);
    }

    @Test
    void orderTest() {
        Partition p = new Partition(new int[][]{{1, 3}, {0, 2}});
        p.order();
        SortedSet<Integer> cell0 = p.getCell(0);
        SortedSet<Integer> cell1 = p.getCell(1);
        Assertions.assertTrue(cell0.first() < cell1.first());
        Assertions.assertTrue(cell0.last() < cell1.last());
    }

    @Test
    void inSameCellTest() {
        Partition p = new Partition(new int[][]{{0, 2}, {1, 3}});
        Assertions.assertTrue(p.inSameCell(1, 3));
    }

}
