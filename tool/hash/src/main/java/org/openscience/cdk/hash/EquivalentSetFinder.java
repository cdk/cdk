/*
 * Copyright (c) 2013 John May <jwmay@users.sf.net>
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */

package org.openscience.cdk.hash;

import org.openscience.cdk.interfaces.IAtomContainer;

import java.util.Set;

/**
 * Describes a function which identifies a set of equivalent atoms base on the
 * provided invariants. Given some other pre-conditions this set is filtered
 * down and an array of length 0 to n is returned. It is important to note that
 * the atoms may not actually be equivalent and are only equivalent by the
 * provided invariants. An example of a pre-condition could be that we only
 * return the vertices which are present in rings (cyclic). This condition
 * removes all terminal atoms which although equivalent are not relevant.
 *
 * @author John May
 * @cdk.module hash
 * @cdk.githash
 */
abstract class EquivalentSetFinder {

    /**
     * Find a set of equivalent vertices (atoms) and return this set as an array
     * of indices.
     *
     * @param invariants the values for each vertex
     * @param container  the molecule which which the graph is based on
     * @param graph      adjacency list representation of the graph
     * @return set of equivalent vertices
     */
    abstract Set<Integer> find(long[] invariants, IAtomContainer container, int[][] graph);

}
