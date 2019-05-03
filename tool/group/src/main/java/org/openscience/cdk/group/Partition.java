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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;


/**
 * A partition of a set of integers, such as the discrete partition {{1}, {2},
 * {3}, {4}} or the unit partition {{1, 2, 3, 4}} or an intermediate like {{1,
 * 2}, {3, 4}}.
 *
 * @author maclean
 * @cdk.module group
 */
public class Partition {

    /**
     * The subsets of the partition, known as cells.
     */
    private List<SortedSet<Integer>> cells;

    /**
     * Creates a new, empty partition with no cells.
     */
    public Partition() {
        this.cells = new ArrayList<SortedSet<Integer>>();
    }

    /**
     * Copy constructor to make one partition from another.
     *
     * @param other the partition to copy
     */
    public Partition(Partition other) {
        this();
        for (SortedSet<Integer> block : other.cells) {
            this.cells.add(new TreeSet<Integer>(block));
        }
    }

    /**
     * Constructor to make a partition from an array of int arrays.
     *
     * @param cellData the partition to copy
     */
    public Partition(int[][] cellData) {
        this();
        for (int[] aCellData : cellData) {
            addCell(aCellData);
        }
    }

    /**
     * Create a unit partition - in other words, the coarsest possible partition
     * where all the elements are in one cell.
     *
     * @param size the number of elements
     * @return a new Partition with one cell containing all the elements
     */
    public static Partition unit(int size) {
        Partition unit = new Partition();
        unit.cells.add(new TreeSet<Integer>());
        for (int i = 0; i < size; i++) {
            unit.cells.get(0).add(i);
        }
        return unit;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Partition partition = (Partition) o;

        return cells != null ? cells.equals(partition.cells) : partition.cells == null;

    }

    /**
     *{@inheritDoc}
     */
    @Override
    public int hashCode() {
        return cells != null ? cells.hashCode() : 0;
    }

    /**
     * Gets the size of the partition, in terms of the number of cells.
     *
     * @return the number of cells in the partition
     */
    public int size() {
        return this.cells.size();
    }

    /**
     * Calculate the size of the partition as the sum of the sizes of the cells.
     *
     * @return the number of elements in the partition
     */
    public int numberOfElements() {
        int n = 0;
        for (SortedSet<Integer> cell : cells) {
            n += cell.size();
        }
        return n;
    }

    /**
     * Checks that all the cells are singletons - that is, they only have one
     * element. A discrete partition is equivalent to a permutation.
     *
     * @return true if all the cells are discrete
     */
    public boolean isDiscrete() {
        for (SortedSet<Integer> cell : cells) {
            if (cell.size() != 1) {
                return false;
            }
        }
        return true;
    }

    /**
     * Converts the whole partition into a permutation.
     *
     * @return the partition as a permutation
     */
    public Permutation toPermutation() {
        Permutation p = new Permutation(this.size());
        for (int i = 0; i < this.size(); i++) {
            p.set(i, this.cells.get(i).first());
        }
        return p;
    }

    /**
     * Check whether the cells are ordered such that for cells i and j,
     * first(j) &gt; first(i) and last(j) &gt; last(i).
     *
     * @return true if all cells in the partition are ordered
     */
    public boolean inOrder() {
        SortedSet<Integer> prev = null;
        for (SortedSet<Integer> cell : cells) {
            if (prev == null) {
                prev = cell;
            } else {
                int first = cell.first();
                int last = cell.last();
                if (first > prev.first() && last > prev.last()) {
                    prev = cell;
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Gets the first element in the specified cell.
     *
     * @param cellIndex the cell to use
     * @return the first element in this cell
     */
    public int getFirstInCell(int cellIndex) {
        return this.cells.get(cellIndex).first();
    }

    /**
     * Gets the cell at this index.
     *
     * @param cellIndex the index of the cell to return
     * @return the cell at this index
     */
    public SortedSet<Integer> getCell(int cellIndex) {
        return this.cells.get(cellIndex);
    }

    /**
     * Splits this partition by taking the cell at cellIndex and making two
     * new cells - the first with the singleton splitElement and the second
     * with the rest of the elements from that cell.
     *
     * @param cellIndex the index of the cell to split on
     * @param splitElement the element to put in its own cell
     * @return a new (finer) Partition
     */
    public Partition splitBefore(int cellIndex, int splitElement) {
        Partition r = new Partition();
        // copy the cells up to cellIndex
        for (int j = 0; j < cellIndex; j++) {
            r.addCell(this.copyBlock(j));
        }

        // split the block at block index
        r.addSingletonCell(splitElement);
        SortedSet<Integer> splitBlock = this.copyBlock(cellIndex);
        splitBlock.remove(splitElement);
        r.addCell(splitBlock);

        // copy the blocks after blockIndex, shuffled up by one
        for (int j = cellIndex + 1; j < this.size(); j++) {
            r.addCell(this.copyBlock(j));
        }
        return r;
    }

    /**
     * Splits this partition by taking the cell at cellIndex and making two
     * new cells - the first with the the rest of the elements from that cell
     * and the second with the singleton splitElement.
     *
     * @param cellIndex the index of the cell to split on
     * @param splitElement the element to put in its own cell
     * @return a new (finer) Partition
     */
    public Partition splitAfter(int cellIndex, int splitElement) {
        Partition r = new Partition();
        // copy the blocks up to blockIndex
        for (int j = 0; j < cellIndex; j++) {
            r.addCell(this.copyBlock(j));
        }

        // split the block at block index
        SortedSet<Integer> splitBlock = this.copyBlock(cellIndex);
        splitBlock.remove(splitElement);
        r.addCell(splitBlock);
        r.addSingletonCell(splitElement);

        // copy the blocks after blockIndex, shuffled up by one
        for (int j = cellIndex + 1; j < this.size(); j++) {
            r.addCell(this.copyBlock(j));
        }
        return r;
    }

    /**
     * Fill the elements of a permutation from the first element of each
     * cell, up to the point <code>upTo</code>.
     *
     * @param upTo take values from cells up to this one
     * @return the permutation representing the first element of each cell
     */
    public Permutation setAsPermutation(int upTo) {
        int[] p = new int[upTo];
        for (int i = 0; i < upTo; i++) {
            p[i] = this.cells.get(i).first();
        }
        return new Permutation(p);
    }

    /**
     * Check to see if the cell at <code>cellIndex</code> is discrete - that is,
     * it only has one element.
     *
     * @param cellIndex the index of the cell to check
     * @return true of the cell at this index is discrete
     */
    public boolean isDiscreteCell(int cellIndex) {
        return this.cells.get(cellIndex).size() == 1;
    }

    /**
     * Gets the index of the first cell in the partition that is discrete.
     *
     * @return the index of the first discrete cell
     */
    public int getIndexOfFirstNonDiscreteCell() {
        for (int i = 0; i < this.cells.size(); i++) {
            if (!isDiscreteCell(i)) return i;
        }
        return -1; // XXX
    }

    /**
     * Add a new singleton cell to the end of the partition containing only
     * this element.
     *
     * @param element the element to add in its own cell
     */
    public void addSingletonCell(int element) {
        SortedSet<Integer> cell = new TreeSet<Integer>();
        cell.add(element);
        this.cells.add(cell);
    }

    /**
     * Removes the cell at the specified index.
     *
     * @param index the index of the cell to remove
     */
    public void removeCell(int index) {
        this.cells.remove(index);
    }

    /**
     * Adds a new cell to the end of the partition containing these elements.
     *
     * @param elements the elements to add in a new cell
     */
    public void addCell(int... elements) {
        SortedSet<Integer> cell = new TreeSet<Integer>();
        for (int element : elements) {
            cell.add(element);
        }
        this.cells.add(cell);
    }

    /**
     * Adds a new cell to the end of the partition.
     *
     * @param elements the collection of elements to put in the cell
     */
    public void addCell(Collection<Integer> elements) {
        cells.add(new TreeSet<Integer>(elements));
    }

    /**
     * Add an element to a particular cell.
     *
     * @param index the index of the cell to add to
     * @param element the element to add
     */
    public void addToCell(int index, int element) {
        if (cells.size() < index + 1) {
            addSingletonCell(element);
        } else {
            cells.get(index).add(element);
        }
    }

    /**
     * Insert a cell into the partition at the specified index.
     *
     * @param index the index of the cell to add
     * @param cell the cell to add
     */
    public void insertCell(int index, SortedSet<Integer> cell) {
        this.cells.add(index, cell);
    }

    /**
     * Creates and returns a copy of the cell at cell index.
     *
     * @param cellIndex the cell to copy
     * @return the copy of the cell
     */
    public SortedSet<Integer> copyBlock(int cellIndex) {
        return new TreeSet<Integer>(this.cells.get(cellIndex));
    }

    /**
     * Sort the cells in increasing order.
     */
    public void order() {
        Collections.sort(cells, new Comparator<SortedSet<Integer>>() {

            @Override
            public int compare(SortedSet<Integer> cellA, SortedSet<Integer> cellB) {
                return cellA.first().compareTo(cellB.first());
            }

        });
    }

    /**
     * Check that two elements are in the same cell of the partition.
     *
     * @param elementI an element in the partition
     * @param elementJ an element in the partition
     * @return true if both elements are in the same cell
     */
    public boolean inSameCell(int elementI, int elementJ) {
        for (int cellIndex = 0; cellIndex < size(); cellIndex++) {
            SortedSet<Integer> cell = getCell(cellIndex);
            if (cell.contains(elementI) && cell.contains(elementJ)) {
                return true;
            }
        }
        return false;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int cellIndex = 0; cellIndex < cells.size(); cellIndex++) {
            SortedSet<Integer> cell = cells.get(cellIndex);
            int elementIndex = 0;
            for (int element : cell) {
                sb.append(element);
                if (cell.size() > 1 && elementIndex < cell.size() - 1) {
                    sb.append(',');
                }
                elementIndex++;
            }
            if (cells.size() > 1 && cellIndex < cells.size() - 1) {
                sb.append('|');
            }
        }
        sb.append(']');
        return sb.toString();
    }

    /**
     * Parse a string like "[0,2|1,3]" to form the partition; cells are
     * separated by '|' characters and elements within the cell by commas.
     *
     * @param strForm the partition in string form
     * @return the partition corresponding to the string
     * @throws IllegalArgumentException thrown if the provided strFrom is
     *         null or empty
     */
    public static Partition fromString(String strForm) {

        if (strForm == null || strForm.isEmpty()) throw new IllegalArgumentException("null or empty string provided");

        Partition p = new Partition();
        int index = 0;
        if (strForm.charAt(0) == '[') {
            index++;
        }
        int endIndex;
        if (strForm.charAt(strForm.length() - 1) == ']') {
            endIndex = strForm.length() - 2;
        } else {
            endIndex = strForm.length() - 1;
        }
        int currentCell = -1;
        int numStart = -1;
        while (index <= endIndex) {
            char c = strForm.charAt(index);
            if (Character.isDigit(c)) {
                if (numStart == -1) {
                    numStart = index;
                }
            } else if (c == ',') {
                int element = Integer.parseInt(strForm.substring(numStart, index));
                if (currentCell == -1) {
                    p.addCell(element);
                    currentCell = 0;
                } else {
                    p.addToCell(currentCell, element);
                }
                numStart = -1;
            } else if (c == '|') {
                int element = Integer.parseInt(strForm.substring(numStart, index));
                if (currentCell == -1) {
                    p.addCell(element);
                    currentCell = 0;
                } else {
                    p.addToCell(currentCell, element);
                }
                currentCell++;
                p.addCell();
                numStart = -1;
            }
            index++;
        }
        int lastElement = Integer.parseInt(strForm.substring(numStart, endIndex + 1));
        p.addToCell(currentCell, lastElement);
        return p;
    }

}
