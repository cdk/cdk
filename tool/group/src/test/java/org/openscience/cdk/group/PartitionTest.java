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

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;

/**
 * @author maclean
 * @cdk.module test-group
 *
 */
public class PartitionTest extends CDKTestCase {

    @Test
    public void emptyConstructor() {
        Partition p = new Partition();
        Assert.assertEquals(0, p.size());
    }

    @Test
    public void copyConstructor() {
        Partition p = new Partition();
        p.addCell(0, 1);
        p.addCell(2, 3);
        Partition q = new Partition(p);
        Assert.assertEquals(p, q);
    }

    @Test
    public void cellDataConstructor() {
        int[][] cellData = new int[][]{{0, 1}, {2, 3, 4}, {5, 6}};
        Partition p = new Partition(cellData);
        Assert.assertEquals(cellData.length, p.size());
        Assert.assertEquals(7, p.numberOfElements());
    }

    @Test
    public void unitStaticConstructor() {
        int size = 5;
        Partition p = Partition.unit(size);
        Assert.assertEquals(1, p.size());
        Assert.assertEquals(size, p.getCell(0).size());
    }

    @Test
    public void sizeTest() {
        Partition p = new Partition();
        p.addCell(0, 1);
        p.addCell(2, 3);
        Assert.assertEquals(2, p.size());
        Assert.assertEquals(2, p.getCell(0).size());
        Assert.assertEquals(2, p.getCell(1).size());
    }

    @Test
    public void numberOfElementsTest() {
        Partition p = new Partition();
        p.addCell(0, 1);
        p.addCell(2, 3);
        Assert.assertEquals(4, p.numberOfElements());
    }

    @Test
    public void isDiscreteTest() {
        int size = 5;
        Partition p = new Partition();
        for (int i = 0; i < size; i++) {
            p.addSingletonCell(i);
        }
        Assert.assertTrue(p.isDiscrete());
    }

    @Test
    public void toPermutationTest() {
        int size = 5;
        Partition partition = new Partition();
        for (int i = 0; i < size; i++) {
            partition.addSingletonCell(i);
        }
        Permutation permutation = partition.toPermutation();
        Assert.assertEquals(size, permutation.size());
        for (int i = 0; i < size; i++) {
            Assert.assertEquals(i, permutation.get(i));
        }
    }

    @Test
    public void inOrderTest() {
        int[][] cellData = new int[][]{{0, 1}, {2, 3, 4}, {5, 6}};
        Partition p = new Partition(cellData);
        Assert.assertTrue(p.inOrder());
    }

    @Test
    public void getFirstInCellTest() {
        int[][] cellData = new int[][]{{0, 1}, {2, 3, 4}, {5, 6}};
        Partition p = new Partition(cellData);
        for (int i = 0; i < cellData.length; i++) {
            Assert.assertEquals(cellData[i][0], p.getFirstInCell(i));
        }
    }

    @Test
    public void getCellTest() {
        int[][] cellData = new int[][]{{0, 1}, {2, 3, 4}, {5, 6}};
        Partition p = new Partition(cellData);
        for (int i = 0; i < cellData.length; i++) {
            Integer[] cell = p.getCell(i).toArray(new Integer[]{});
            Assert.assertEquals(cellData[i].length, cell.length);
            for (int j = 0; j < cell.length; j++) {
                Assert.assertEquals(cellData[i][j], (int) cell[j]);
            }
        }
    }

    @Test
    public void splitBeforeTest() {
        int[][] cellData = new int[][]{{0, 1}, {2, 3, 4}, {5, 6}};
        Partition p = new Partition(cellData);
        int cellIndex = 1;
        int splitElement = 3;
        Partition q = p.splitBefore(cellIndex, splitElement);
        Assert.assertEquals(p.numberOfElements(), q.numberOfElements());
        Assert.assertEquals(p.size() + 1, q.size());
        SortedSet<Integer> cell = q.getCell(cellIndex);
        Assert.assertTrue(cell.size() == 1);
        Assert.assertEquals(splitElement, (int) cell.first());
    }

    @Test
    public void splitAfterTest() {
        int[][] cellData = new int[][]{{0, 1}, {2, 3, 4}, {5, 6}};
        Partition p = new Partition(cellData);
        int cellIndex = 1;
        int splitElement = 3;
        Partition q = p.splitAfter(cellIndex, splitElement);
        Assert.assertEquals(p.numberOfElements(), q.numberOfElements());
        Assert.assertEquals(p.size() + 1, q.size());
        SortedSet<Integer> cell = q.getCell(cellIndex + 1);
        Assert.assertTrue(cell.size() == 1);
        Assert.assertEquals(splitElement, (int) cell.first());
    }

    @Test
    public void setAsPermutationTest() {
        int partitionSize = 5;
        int permutationSize = 3;
        Partition partition = new Partition();
        for (int i = 0; i < partitionSize; i++) {
            partition.addSingletonCell(i);
        }
        Permutation permutation = partition.setAsPermutation(permutationSize);
        for (int i = 0; i < permutationSize; i++) {
            Assert.assertEquals(i, permutation.get(i));
        }
    }

    @Test
    public void isDiscreteCellTest() {
        int[][] cellData = new int[][]{{0}, {1}, {2}, {3}, {4}, {5}};
        Partition p = new Partition(cellData);
        for (int i = 0; i < p.size(); i++) {
            Assert.assertTrue(p.isDiscreteCell(i));
        }
    }

    @Test
    public void getIndexOfFirstNonDiscreteCellTest() {
        int[][] cellData = new int[][]{{0}, {1}, {2, 3, 4}, {5, 6}};
        Partition p = new Partition(cellData);
        Assert.assertEquals(2, p.getIndexOfFirstNonDiscreteCell());
    }

    @Test
    public void addSingletonCellTest() {
        Partition p = new Partition();
        p.addSingletonCell(0);
        Assert.assertEquals(1, p.size());
        Assert.assertEquals(1, p.numberOfElements());
    }

    @Test
    public void removeCellTest() {
        int size = 5;
        Partition p = Partition.unit(size);
        p.removeCell(0);
        Assert.assertEquals(0, p.size());
    }

    @Test
    public void addCell_VarArgsTest() {
        Partition p = new Partition();
        p.addCell(0, 1, 2);
        Assert.assertEquals(1, p.size());
        Assert.assertEquals(3, p.numberOfElements());
    }

    @Test
    public void addCell_CollectionTest() {
        Partition p = new Partition();
        List<Integer> cell = new ArrayList<Integer>();
        cell.add(0);
        cell.add(1);
        cell.add(2);
        p.addCell(cell);
        Assert.assertEquals(1, p.size());
        Assert.assertEquals(3, p.numberOfElements());
    }

    @Test
    public void addToCellTest() {
        Partition p = new Partition();
        p.addToCell(0, 0);
        Assert.assertEquals(1, p.size());
        Assert.assertEquals(1, p.numberOfElements());
        p.addToCell(0, 1);
        Assert.assertEquals(1, p.size());
        Assert.assertEquals(2, p.numberOfElements());
    }

    @Test
    public void insertCellTest() {
        int[][] cellData = new int[][]{{0}, {2}, {3}};
        Partition p = new Partition(cellData);
        SortedSet<Integer> cell = new TreeSet<Integer>();
        cell.add(1);
        p.insertCell(1, cell);
        Assert.assertTrue(p.isDiscrete());
    }

    @Test
    public void copyBlockTest() {
        int[][] cellData = new int[][]{{0}, {1}, {2}};
        Partition p = new Partition(cellData);
        int cellIndex = 1;
        SortedSet<Integer> copyCell = p.copyBlock(cellIndex);
        SortedSet<Integer> refCell = p.getCell(cellIndex);
        Assert.assertTrue(copyCell != refCell);
    }

    @Test
    public void fromStringTest() {
        Partition p = Partition.fromString("[0,1|2,3]");
        Assert.assertEquals(2, p.size());
        Assert.assertEquals(4, p.numberOfElements());
    }

    @Test
    public void fromStringTest2() {
        Partition p = Partition.fromString("[0|1,2,3]");
        Assert.assertEquals(2, p.size());
        Assert.assertEquals(4, p.numberOfElements());
    }

    @Test
    public void equalsTest_null() {
        Partition p = new Partition(new int[][]{{0}, {1}});
        Assert.assertNotSame(p, null);
    }

    @Test
    public void equalsTest_different() {
        Partition p = new Partition(new int[][]{{0}, {1}});
        Partition o = new Partition(new int[][]{{1}, {0}});
        Assert.assertNotSame(p, o);
    }

    @Test
    public void equalsTest() {
        Partition p = new Partition(new int[][]{{0}, {1}});
        Partition o = new Partition(new int[][]{{0}, {1}});
        Assert.assertEquals(p, o);
    }

    @Test
    public void orderTest() {
        Partition p = new Partition(new int[][]{{1, 3}, {0, 2}});
        p.order();
        SortedSet<Integer> cell0 = p.getCell(0);
        SortedSet<Integer> cell1 = p.getCell(1);
        Assert.assertTrue(cell0.first() < cell1.first());
        Assert.assertTrue(cell0.last() < cell1.last());
    }

    @Test
    public void inSameCellTest() {
        Partition p = new Partition(new int[][]{{0, 2}, {1, 3}});
        Assert.assertTrue(p.inSameCell(1, 3));
    }

}
