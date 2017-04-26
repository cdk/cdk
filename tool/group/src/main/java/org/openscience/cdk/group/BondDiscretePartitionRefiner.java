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

import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * A tool for determining the automorphism group of the atoms in a molecule, or
 * for checking for a canonical form of a molecule.
 *
 * If two bonds are equivalent under an automorphism in the group, then
 * roughly speaking they are in symmetric positions in the molecule. For
 * example, the C-C bonds attaching two methyl groups to a benzene ring
 * are 'equivalent' in this sense.
 *
 * <p>There are a couple of ways to use it - firstly, get the automorphisms.</p>
 *
 * <pre>
 *     IAtomContainer ac = ... // get an atom container somehow
 *     BondDiscretePartitionRefiner refiner = new BondDiscretePartitionRefiner();
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
 *     BondDiscretePartitionRefiner refiner = new BondDiscretePartitionRefiner();
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
 *     BondDiscretePartitionRefiner refiner = new BondDiscretePartitionRefiner();
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
public class BondDiscretePartitionRefiner extends AtomContainerDiscretePartitionRefiner {

    /**
     * Specialised option to allow generating automorphisms that ignore the bond order.
     */
    private boolean ignoreBondOrders;

    /**
     * Make a bond partition refiner that takes bond-orders into account.
     */
    public BondDiscretePartitionRefiner() {
        this(false);
    }

    /**
     * Make a bond partition refiner and specify whether bonds-orders should be
     * considered when calculating the automorphisms.
     *
     * @param ignoreBondOrders if true, ignore the bond orders
     */
    public BondDiscretePartitionRefiner(boolean ignoreBondOrders) {
        this.ignoreBondOrders = ignoreBondOrders;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected Refinable createRefinable(IAtomContainer atomContainer) {
        return new BondRefinable(atomContainer, ignoreBondOrders);
    }
}
