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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * A tool for determining the automorphism group of the atoms in a molecule, or
 * for checking for a canonical form of a molecule.
 *
 * If two atoms are equivalent under an automorphism in the group, then
 * roughly speaking they are in symmetric positions in the molecule. For
 * example, the C atoms in two methyl groups attached to a benzene ring
 * are 'equivalent' in this sense.
 *
 * <p>There are a couple of ways to use it - firstly, get the automorphisms.</p>
 *
 * <pre>
 *     IAtomContainer ac = ... // get an atom container somehow
 *     AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner();
 *     PermutationGroup autG = refiner.getAutomorphismGroup(ac);
 *     for (Permutation automorphism : autG.all()) {
 *         ... // do something with the permutation
 *     }
 * </pre>
 *
 * <p>Another is to check an atom container to see if it is canonical:</p>
 *
 * <pre>
 *     IAtomContainer ac = ... // get an atom container somehow
 *     AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner();
 *     if (refiner.isCanonical(ac)) {
 *         ... // do something with the atom container
 *     }
 * </pre>
 *
 * Note that it is not necessary to call {@link #refine(IAtomContainer)} before
 * either of these methods. However if both the group and the canonical check
 * are required, then the code should be:
 *
 * <pre>
 *     AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner();
 *     refiner.refine(ac);
 *     boolean isCanon = refiner.isCanonical();
 *     PermutationGroup autG = refiner.getAutomorphismGroup();
 * </pre>
 *
 * This way, the refinement is not carried out multiple times.
 *
 * @author maclean
 * @cdk.module group
 */
public class AtomDiscretePartitionRefiner extends AtomContainerDiscretePartitionRefiner {

    private Refinable refinable;

    /**
     * Specialised option to allow generating automorphisms
     * that ignore the element symbols.
     */
    private boolean ignoreElements;

    /**
     * Specialised option to allow generating automorphisms
     * that ignore the bond order.
     */
    private boolean ignoreBondOrders;

    /**
     * Default constructor - does not ignore elements or bond orders
     * or bond orders.
     */
    public AtomDiscretePartitionRefiner() {
        this(false, false);
    }

    /**
     * Make a refiner with various advanced options.
     *
     * @param ignoreElements ignore element symbols when making automorphisms
     * @param ignoreBondOrders ignore bond order when making automorphisms
     */
    public AtomDiscretePartitionRefiner(boolean ignoreElements, boolean ignoreBondOrders) {
        this.ignoreElements = ignoreElements;
        this.ignoreBondOrders = ignoreBondOrders;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected int getVertexCount() {
        return refinable.getVertexCount();
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected int getConnectivity(int vertexI, int vertexJ) {
       return refinable.getConnectivity(vertexI, vertexJ);
    }

    /**
     * Get the element partition from an atom container, which is simply a list
     * of sets of atom indices where all atoms in one set have the same element
     * symbol.
     *
     * So for atoms [C0, N1, C2, P3, C4, N5] the partition would be
     * [{0, 2, 4}, {1, 5}, {3}] with cells for elements C, N, and P.
     *
     * @param atomContainer the atom container to get element symbols from
     * @return a partition of the atom indices based on the element symbols
     */
    public Partition getInitialPartition(IAtomContainer atomContainer) {
        if (ignoreElements) {
            int n = atomContainer.getAtomCount();
            return Partition.unit(n);
        }

        Map<String, SortedSet<Integer>> cellMap = new HashMap<String, SortedSet<Integer>>();
        int numberOfAtoms = atomContainer.getAtomCount();
        for (int atomIndex = 0; atomIndex < numberOfAtoms; atomIndex++) {
            String symbol = atomContainer.getAtom(atomIndex).getSymbol();
            SortedSet<Integer> cell;
            if (cellMap.containsKey(symbol)) {
                cell = cellMap.get(symbol);
            } else {
                cell = new TreeSet<Integer>();
                cellMap.put(symbol, cell);
            }
            cell.add(atomIndex);
        }

        List<String> atomSymbols = new ArrayList<String>(cellMap.keySet());
        Collections.sort(atomSymbols);

        Partition elementPartition = new Partition();
        for (String key : atomSymbols) {
            SortedSet<Integer> cell = cellMap.get(key);
            elementPartition.addCell(cell);
        }

        return elementPartition;
    }

    protected Refinable getRefinable(IAtomContainer atomContainer) {
        refinable = new AtomRefinable(atomContainer, ignoreBondOrders);
        return refinable;
    }

}
