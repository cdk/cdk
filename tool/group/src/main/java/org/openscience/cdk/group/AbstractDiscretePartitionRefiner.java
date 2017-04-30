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

import java.util.Set;


/**
 * Refines vertex partitions until they are discrete, and therefore equivalent
 * to permutations. These permutations are automorphisms of the graph that was
 * used during the refinement to guide the splitting of partition blocks.
 *
 * @author maclean
 * @cdk.module group
 */
abstract class AbstractDiscretePartitionRefiner implements DiscretePartitionRefiner {

    /**
     * The result of a comparison between the current partition
     * and the best permutation found so far.
     *
     */
    enum Result {
        WORSE, EQUAL, BETTER
    };

    /**
     * If true, then at least one partition has been refined
     * to a permutation (IE : to a discrete partition).
     */
    private boolean                    bestExist;

    /**
     * The best permutation is the one that gives the maximal
     * half-matrix string (so far) when applied to the graph.
     */
    private Permutation                best;

    /**
     * The first permutation seen when refining.
     */
    private Permutation                first;

    /**
     * An equitable refiner.
     */
    private EquitablePartitionRefiner equitableRefiner;

    /**
     * The automorphism group that is used to prune the search.
     */
    private PermutationGroup           group;

    /**
     * A refiner - it is necessary to call {@link #setup} before use.
     */
    public AbstractDiscretePartitionRefiner() {
        this.bestExist = false;
        this.best = null;
        this.equitableRefiner = null;
    }

    /**
     * Get the number of vertices in the graph to be refined.
     *
     * @return a count of the vertices in the underlying graph
     */
    protected abstract int getVertexCount();

    /**
     * Get the connectivity between two vertices as an integer, to allow
     * for multigraphs : so a single edge is 1, a double edge 2, etc. If
     * there is no edge, then 0 should be returned.
     *
     * @param vertexI a vertex of the graph
     * @param vertexJ a vertex of the graph
     * @return the multiplicity of the edge (0, 1, 2, 3, ...)
     */
    protected abstract int getConnectivity(int vertexI, int vertexJ);

    /**
     * Setup the group and refiner; it is important to call this method before
     * calling {@link #refine} otherwise the refinement process will fail.
     *
     * @param group a group (possibly empty) of automorphisms
     * @param refiner the equitable refiner
     */
    public void setup(PermutationGroup group, EquitablePartitionRefiner refiner) {
        this.bestExist = false;
        this.best = null;
        this.group = group;
        this.equitableRefiner = refiner;
    }

    /**
     * Check that the first refined partition is the identity.
     *
     * @return true if the first is the identity permutation
     */
    public boolean firstIsIdentity() {
        return this.first.isIdentity();
    }

    /**
     * The automorphism partition is a partition of the elements of the group.
     *
     * @return a partition of the elements of group
     */
    public Partition getAutomorphismPartition() {
        final int n = group.getSize();
        final DisjointSetForest forest = new DisjointSetForest(n);
        group.apply(new PermutationGroup.Backtracker() {

            boolean[]       inOrbit      = new boolean[n];
            private int     inOrbitCount = 0;
            private boolean isFinished;

            @Override
            public boolean isFinished() {
                return isFinished;
            }

            @Override
            public void applyTo(Permutation p) {
                for (int elementX = 0; elementX < n; elementX++) {
                    if (inOrbit[elementX]) {
                        continue;
                    } else {
                        int elementY = p.get(elementX);
                        while (elementY != elementX) {
                            if (!inOrbit[elementY]) {
                                inOrbitCount++;
                                inOrbit[elementY] = true;
                                forest.makeUnion(elementX, elementY);
                            }
                            elementY = p.get(elementY);
                        }
                    }
                }
                if (inOrbitCount == n) {
                    isFinished = true;
                }
            }
        });

        // convert to a partition
        Partition partition = new Partition();
        for (int[] set : forest.getSets()) {
            partition.addCell(set);
        }

        // necessary for comparison by string
        partition.order();
        return partition;
    }

    /**
     * Get the upper-half of the adjacency matrix under the permutation.
     *
     * @param permutation a permutation of the adjacency matrix
     * @return a string containing the permuted values of half the matrix
     */
    private String getHalfMatrixString(Permutation permutation) {
        StringBuilder builder = new StringBuilder(permutation.size());
        int size = permutation.size();
        for (int indexI = 0; indexI < size - 1; indexI++) {
            for (int indexJ = indexI + 1; indexJ < size; indexJ++) {
                builder.append(getConnectivity(permutation.get(indexI), permutation.get(indexJ)));
            }
        }
        return builder.toString();
    }

    /**
     * Get the half-matrix string under the first permutation.
     *
     * @return the upper-half adjacency matrix string permuted by the first
     */
    public String getFirstHalfMatrixString() {
        return getHalfMatrixString(first);
    }

    /**
     * Get the initial (unpermuted) half-matrix string.
     *
     * @return the upper-half adjacency matrix string
     */
    public String getHalfMatrixString() {
        return getHalfMatrixString(new Permutation(getVertexCount()));
    }

    /**
     * Get the automorphism group used to prune the search.
     *
     * @return the automorphism group
     */
    public PermutationGroup getAutomorphismGroup() {
        return this.group;
    }

    /**
     * Get the best permutation found.
     *
     * @return the permutation that gives the maximal half-matrix string
     */
    public Permutation getBest() {
        return this.best;
    }

    /**
     * Get the first permutation reached by the search.
     *
     * @return the first permutation reached
     */
    public Permutation getFirst() {
        return this.first;
    }

    /**
     * Check for a canonical graph, without generating the whole
     * automorphism group.
     *
     * @return true if the graph is canonical
     */
    public boolean isCanonical() {
        return best.isIdentity();
    }

    /**
     * Refine the partition. The main entry point for subclasses.
     *
     * @param partition the initial partition of the vertices
     */
    public void refine(Partition partition) {
        refine(this.group, partition);
    }

    /**
     * Does the work of the class, that refines a coarse partition into a finer
     * one using the supplied automorphism group to prune the search.
     *
     * @param group the automorphism group of the graph
     * @param coarser the partition to refine
     */
    private void refine(PermutationGroup group, Partition coarser) {
        int vertexCount = getVertexCount();

        Partition finer = equitableRefiner.refine(coarser);

        int firstNonDiscreteCell = finer.getIndexOfFirstNonDiscreteCell();
        if (firstNonDiscreteCell == -1) {
            firstNonDiscreteCell = vertexCount;
        }

        Permutation pi1 = new Permutation(firstNonDiscreteCell);

        Result result = Result.BETTER;
        if (bestExist) {
            pi1 = finer.setAsPermutation(firstNonDiscreteCell);
            result = compareRowwise(pi1);
        }

        // partition is discrete
        if (finer.size() == vertexCount) {
            if (!bestExist) {
                best = finer.toPermutation();
                first = finer.toPermutation();
                bestExist = true;
            } else {
                if (result == Result.BETTER) {
                    best = new Permutation(pi1);
                } else if (result == Result.EQUAL) {
                    group.enter(pi1.multiply(best.invert()));
                }
            }
        } else {
            if (result != Result.WORSE) {
                Set<Integer> blockCopy = finer.copyBlock(firstNonDiscreteCell);
                for (int vertexInBlock = 0; vertexInBlock < vertexCount; vertexInBlock++) {
                    if (blockCopy.contains(vertexInBlock)) {
                        Partition nextPartition = finer.splitBefore(firstNonDiscreteCell, vertexInBlock);

                        this.refine(group, nextPartition);

                        int[] permF = new int[vertexCount];
                        int[] invF = new int[vertexCount];
                        for (int i = 0; i < vertexCount; i++) {
                            permF[i] = i;
                            invF[i] = i;
                        }

                        for (int j = 0; j <= firstNonDiscreteCell; j++) {
                            int x = nextPartition.getFirstInCell(j);
                            int i = invF[x];
                            int h = permF[j];
                            permF[j] = x;
                            permF[i] = h;
                            invF[h] = i;
                            invF[x] = j;
                        }
                        Permutation pPermF = new Permutation(permF);
                        group.changeBase(pPermF);
                        for (int j = 0; j < vertexCount; j++) {
                            Permutation g = group.get(firstNonDiscreteCell, j);
                            if (g != null) {
                                blockCopy.remove(g.get(vertexInBlock));
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Check a permutation to see if it is better, equal, or worse than the
     * current best.
     *
     * @param perm the permutation to check
     * @return BETTER, EQUAL, or WORSE
     */
    private Result compareRowwise(Permutation perm) {
        int m = perm.size();
        for (int i = 0; i < m - 1; i++) {
            for (int j = i + 1; j < m; j++) {
                int x = getConnectivity(best.get(i), best.get(j));
                int y = getConnectivity(perm.get(i), perm.get(j));
                if (x > y) return Result.WORSE;
                if (x < y) return Result.BETTER;
            }
        }
        return Result.EQUAL;
    }

}
