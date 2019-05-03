/*
 * Copyright (c) 2013 European Bioinformatics Institute (EMBL-EBI)
 * 			  John May <jwmay@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version. All we ask is that proper credit is given
 * for our work, which includes - but is not limited to - adding the above
 * copyright notice to the beginning of your source code files, and to any
 * copyright notice that you may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */

package org.openscience.cdk.graph;

import java.util.List;

/**
 * Describes a path graph (<b>P-Graph</b>) to find all cycles in a graph
 * {@cdk.cite HAN96}.
 *
 * @author John May
 * @cdk.keyword cycle
 * @cdk.keyword ring
 * @cdk.keyword all cycles
 * @cdk.keyword all rings
 * @cdk.keyword path graph
 * @cdk.keyword p-graph
 * @cdk.module core
 */
abstract class PathGraph {

    /**
     * The current degree of vertex <i>x</i>. The degree provides an upper bound
     * on the number of new edges that could be introduced by {@link
     * #remove(int, java.util.List)}. The number of new edges is at most
     * <i>d</i>(<i>d</i>-1)/2 where d is the degree of the vertex being
     * removed.
     *
     * @param x a vertex
     * @return degree
     * @see #remove(int, java.util.List)
     */
    abstract int degree(int x);

    /**
     * Remove vertex <i>x</i> from the P-graph and reduce all incident edges.Any
     * newly discovered cycles are added to the provided list of <i>cycles</i>.
     * Removing a vertex with a large number of incident edges may create many
     * edges in the graph. To avoid generating a potentially exponential number
     * of cycles checking the {@link #degree(int)} of a vertex before removal
     * can provide a fast fail approach.
     *
     * @param x      a vertex
     * @param cycles a list to add newly discovered cycles to
     * @see #degree(int)
     */
    abstract void remove(int x, List<int[]> cycles);

}
