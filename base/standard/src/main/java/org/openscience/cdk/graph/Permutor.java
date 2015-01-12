/* Copyright (C) 2009  Gilleain Torrance <gilleain.torrance@gmail.com>
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
package org.openscience.cdk.graph;

import java.util.Random;


/**
 * General permutation generator, that uses orderly generation by ranking and
 * unranking. The basic idea is that all permutations of length N can be ordered
 * (lexicographically) like:
 * <pre>
 * 0 [0, 1, 2]
 * 1 [0, 2, 1]
 * 2 [1, 0, 2]
 * ...
 * </pre>
 * where the number to the left of each permutation is the <i>rank</i> - really
 * just the index in this ordered list. The list is created on demand, by a
 * process called <i>unranking</i> where the rank is converted to the
 * permutation that appears at that point in the list.
 *
 * <p>The algorithms used are from the book "Combinatorial Generation :
 * Algorithms, Generation, and Search" (or C.A.G.E.S.) by D.L. Kreher and D.R.
 * Stinson. CRC Press (18 Dec 1998). ISBN-13 : 978-0849339882.</p>
 *
 * @author         maclean
 * @cdk.created    2009-09-09
 * @cdk.keyword    permutation
 * @cdk.module     standard
 * @cdk.githash
 */
public class Permutor {

    /**
     * The current rank of the permutation to use
     */
    private int    currentRank;

    /**
     * The maximum rank possible, given the size
     */
    private int    maxRank;

    /**
     * The number of objects to permute
     */
    private int    size;

    /**
     * For accessing part of the permutation space
     */
    private Random random;

    /**
     * Create a permutor that will generate permutations of numbers up to
     * <code>size</code>.
     *
     * @param size the size of the permutations to generate
     */
    public Permutor(int size) {
        this.currentRank = 0;
        this.size = size;
        this.maxRank = this.calculateMaxRank();
        this.random = new Random();
    }

    public boolean hasNext() {
        return this.currentRank < this.maxRank;
    }

    /**
     * Set the permutation to use, given its rank.
     *
     * @param rank the order of the permutation in the list
     */
    public void setRank(int rank) {
        this.currentRank = rank;
    }

    /**
     * Set the currently used permutation.
     *
     * @param permutation the permutation to use, as an int array
     */
    public void setPermutation(int[] permutation) {
        this.currentRank = this.rankPermutationLexicographically(permutation);
    }

    /**
     * Get the current rank.
     *
     * @return the current rank
     */
    public int getRank() {
        return currentRank;
    }

    /**
     * Randomly skip ahead in the list of permutations.
     *
     * @return a permutation in the range (current, N!)
     */
    public int[] getRandomNextPermutation() {
        int d = maxRank - currentRank;
        int r = this.random.nextInt(d);
        this.currentRank += Math.max(1, r);
        return this.getCurrentPermutation();
    }

    /**
     * Get the next permutation in the list.
     *
     * @return the next permutation
     */
    public int[] getNextPermutation() {
        this.currentRank++;
        return this.getCurrentPermutation();
    }

    /**
     * Get the permutation that is currently being used.
     *
     * @return the permutation as an int array
     */
    public int[] getCurrentPermutation() {
        return this.unrankPermutationLexicographically(currentRank, size);
    }

    /**
     * Calculate the max possible rank for permutations of N numbers.
     *
     * @return the maximum number of permutations
     */
    public int calculateMaxRank() {
        return factorial(size) - 1;
    }

    // much much more efficient to pre-calculate this (or lazily calculate)
    // and store in an array, at the cost of memory.
    private int factorial(int i) {
        if (i > 0) {
            return i * factorial(i - 1);
        } else {
            return 1;
        }
    }

    /**
     * Convert a permutation (in the form of an int array) into a 'rank' - which
     * is just a single number that is the order of the permutation in a lexico-
     * graphically ordered list.
     *
     * @param permutation the permutation to use
     * @return the rank as a number
     */
    private int rankPermutationLexicographically(int[] permutation) {
        int rank = 0;
        int n = permutation.length;
        int[] counter = new int[n + 1];
        for (int i = 1; i < permutation.length; i++) {
            counter[i] = permutation[i - 1] + 1;
        }
        for (int j = 1; j <= n; j++) {
            rank += (counter[j] - 1) * factorial(n - j);
            for (int i = j + 1; i <= n; i++) {
                if (counter[i] > counter[j]) {
                    counter[i]--;
                }
            }
        }
        return rank + 1;
    }

    /**
     * Performs the opposite to the rank method, producing the permutation that
     * has the order <code>rank</code> in the lexicographically ordered list.
     *
     * As an implementation note, the algorithm assumes that the permutation is
     * in the form [1,...N] not the more usual [0,...N-1] for a list of size N.
     * This is why there is the final step of 'shifting' the permutation. The
     * shift also reduces the numbers by one to make them array indices.
     *
     * @param rank the order of the permutation to generate
     * @param size the length/size of the permutation
     * @return a permutation as an int array
     */
    private int[] unrankPermutationLexicographically(int rank, int size) {
        int[] permutation = new int[size + 1];
        permutation[size] = 1;
        for (int j = 1; j < size; j++) {
            int d = (rank % factorial(j + 1)) / factorial(j);
            rank = rank - d * factorial(j);
            permutation[size - j] = d + 1;
            for (int i = size - j + 1; i <= size; i++) {
                if (permutation[i] > d) {
                    permutation[i]++;
                }
            }
        }

        // convert an array of numbers like [1...n] to [0...n-1]
        int[] shiftedPermutation = new int[size];
        for (int i = 1; i < permutation.length; i++) {
            shiftedPermutation[i - 1] = permutation[i] - 1;
        }
        return shiftedPermutation;
    }

}
