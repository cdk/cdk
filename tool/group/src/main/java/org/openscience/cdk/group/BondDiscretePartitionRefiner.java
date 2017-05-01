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
 * An {@link AtomContainerDiscretePartitionRefiner} for bonds.
 *
 * If two bonds are equivalent under an automorphism in the group, then
 * roughly speaking they are in symmetric positions in the molecule. For
 * example, the C-C bonds attaching two methyl groups to a benzene ring
 * are 'equivalent' in this sense.
 *
 *
 * @author maclean
 * @cdk.module group
 */
class BondDiscretePartitionRefiner extends AtomContainerDiscretePartitionRefinerImpl {

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
