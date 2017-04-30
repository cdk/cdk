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
 * An {@link AtomContainerDiscretePartitionRefiner} for atoms.
 * 
 * @author maclean
 * @cdk.module group
 */
class AtomDiscretePartitionRefiner extends AtomContainerDiscretePartitionRefinerImpl {
    
    /**
     * Ignore the elements when creating the initial partition.
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
    protected Refinable createRefinable(IAtomContainer atomContainer) {
        return new AtomRefinable(atomContainer, ignoreElements, ignoreBondOrders);
    }

}
