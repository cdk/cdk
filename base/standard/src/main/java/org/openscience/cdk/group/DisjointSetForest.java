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

import java.util.Arrays;


/**
 * Implementation of a union-find data structure, largely copied from
 * code due to Derrick Stolee.
 *
 * @author maclean
 * @cdk.module standard
 * @cdk.keyword union-find
 */
public class DisjointSetForest {

    /**
     * The sets stored as pointers to their parents. The root of each
     * set is stored as the negated size of the set - ie a set of size
     * 5 with a root element 2 will mean forest[2] = -5.
     */
    private int[] forest;

    /**
     * Initialize a disjoint set forest with a number of elements.
     *
     * @param numberOfElements the number of elements in the forest
     */
    public DisjointSetForest(int numberOfElements) {
        forest = new int[numberOfElements];
        for (int i = 0; i < numberOfElements; i++) {
            forest[i] = -1;
        }
    }

    /**
     * Get the value of the forest at this index - note that this will <i>not</i>
     * necessarily give the set for that element : use {@link #getSets} after
     * union-ing elements.
     *
     * @param i the index in the forest
     * @return the value at this index
     */
    public int get(int i) {
        return forest[i];
    }

    /**
     * Travel up the tree that this element is in, until the root of the set
     * is found, and return that root.
     *
     * @param element the starting point
     * @return the root of the set containing element
     */
    public int getRoot(int element) {
        if (forest[element] < 0) {
            return element;
        } else {
            return getRoot(forest[element]);
        }
    }

    /**
     * Union these two elements - in other words, put them in the same set.
     *
     * @param elementX an element
     * @param elementY an element
     */
    public void makeUnion(int elementX, int elementY) {
        int xRoot = getRoot(elementX);
        int yRoot = getRoot(elementY);

        if (xRoot == yRoot) {
            return;
        }

        if (forest[xRoot] < forest[yRoot]) {
            forest[yRoot] = forest[yRoot] + forest[xRoot];
            forest[xRoot] = yRoot;
        } else {
            forest[xRoot] = forest[xRoot] + forest[yRoot];
            forest[yRoot] = xRoot;
        }
    }

    /**
     * Retrieve the sets as 2D-array of ints.
     *
     * @return the sets
     */
    public int[][] getSets() {
        int n = 0;
        for (int i = 0; i < forest.length; i++) {
            if (forest[i] < 0) {
                n++;
            }
        }
        int[][] sets = new int[n][];
        int currentSet = 0;
        for (int i = 0; i < forest.length; i++) {
            if (forest[i] < 0) {
                int setSize = 1 - forest[i] - 1;
                sets[currentSet] = new int[setSize];
                int currentIndex = 0;
                for (int element = 0; element < forest.length; element++) {
                    if (getRoot(element) == i) {
                        sets[currentSet][currentIndex] = element;
                        currentIndex++;
                    }
                }
                currentSet++;
            }
        }
        return sets;
    }

    @Override
    public String toString() {
        return Arrays.toString(forest);
    }

}
