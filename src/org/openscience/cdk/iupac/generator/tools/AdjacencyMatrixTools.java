/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2002-2005  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
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
package org.openscience.cdk.iupac.generator.tools;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.graph.matrix.AdjacencyMatrix;

/**
 * Methods for playing with Adjacency Matrices
 *
 * @cdk.module experimental
 *
 * @author Egon Willighagen
 */
public class AdjacencyMatrixTools {

    /**
     * Returns an ajacency matrix with only carbon atoms in.
     *
     * @param ac The AtomContainer to strip down to Carbons.
     * @return An adjacency matrix for the carbon atoms in the given AtomContainer.
     */
    public static int[][] getCarbonOnly(AtomContainer ac) {
        AtomContainer copy = (AtomContainer)ac.clone();
        for (int i = 0; i < ac.getAtomCount(); i++) {
        	org.openscience.cdk.interfaces.IAtom a = copy.getAtomAt(i);
            if (!a.getSymbol().equals("C")) {
                copy.removeAtomAndConnectedElectronContainers(a);
            }
        }
        return AdjacencyMatrix.getMatrix(copy);
    }
}
