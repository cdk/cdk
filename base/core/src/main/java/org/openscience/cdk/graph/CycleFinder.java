/*
 * Copyright (c) 2013 European Bioinformatics Institute (EMBL-EBI)
 *                    John May <jwmay@users.sf.net>
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

import org.openscience.cdk.exception.Intractable;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * Defines a method to find the cycles of a molecule. The distinction between a
 * cycle and a ring is that cycles are stored as indices (int[]) while rings are
 * stored as atoms ({@link org.openscience.cdk.interfaces.IAtom}[]) in a {@link
 * org.openscience.cdk.interfaces.IRing}.
 *
 * @author John May
 * @cdk.module core
 * @cdk.githash
 */
public interface CycleFinder {

    /**
     * Find the cycles of the provided molecule.
     *
     * @param molecule a molecule, can be disconnected.
     * @throws Intractable thrown if problem could not be solved within some
     *                     predefined bounds.
     * @return an instance for querying the cycles (rings) in the molecule
     */
    Cycles find(IAtomContainer molecule) throws Intractable;

    /**
     * Find the cycles of the provided molecule.
     *
     * @param molecule a molecule, can be disconnected.
     * @param length maximum length cycle to find (set to
     *               molecule.getAtomCount() for all)
     * @throws Intractable thrown if problem could not be solved within some
     *                     predefined bounds.
     * @return an instance for querying the cycles (rings) in the molecule
     */
    Cycles find(IAtomContainer molecule, int length) throws Intractable;

    /**
     * Find the cycles of the provided molecule when an adjacent relation
     * (graph) is already available. The graph can be obtained through {@link
     * GraphUtil#toAdjList(IAtomContainer)}, for convenience {@link
     * #find(IAtomContainer, int)} will automatically create the graph.
     *
     * @param molecule input structure
     * @param graph adjacency list representation for fast traversal
     * @param length maximum length cycle to find (set to
     *               molecule.getAtomCount() for all)
     * @return an instance for querying the cycles (rings) in the molecule
     * @throws Intractable thrown if problem could not be solved within some
     *                     predefined bounds.
     */
    Cycles find(IAtomContainer molecule, int[][] graph, int length) throws Intractable;
}
