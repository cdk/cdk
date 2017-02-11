/*  Copyright (C) 2009  Gilleain Torrance <gilleain.torrance@gmail.com>
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk.graph;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * An atom container atom permutor that uses ranking and unranking to calculate
 * the next permutation in the series.
 *
 * <p>Typical use:</p><pre>
 * AtomContainerAtomPermutor permutor = new AtomContainerAtomPermutor(container);
 * while (permutor.hasNext()) {
 *   IAtomContainer permutedContainer = permutor.next();
 *   ...
 * }</pre>
 *
 * @author maclean
 * @cdk.created 2009-09-09
 * @cdk.keyword permutation
 * @cdk.module standard
 * @cdk.githash
 */
public class AtomContainerAtomPermutor extends AtomContainerPermutor {

    /**
     * A permutor wraps the original atom container, and produces cloned
     * (and permuted!) copies on demand.
     *
     * @param atomContainer the atom container to permute
     */
    public AtomContainerAtomPermutor(IAtomContainer atomContainer) {
        super(atomContainer.getAtomCount(), atomContainer);
    }

    /**
     * Generate the atom container with this permutation of the atoms.
     *
     * @param permutation the permutation to use
     * @return the
     */
    @Override
    public IAtomContainer containerFromPermutation(int[] permutation) {
        try {
            IAtomContainer permutedContainer = (IAtomContainer) atomContainer.clone();
            IAtom[] atoms = new IAtom[atomContainer.getAtomCount()];
            for (int i = 0; i < atomContainer.getAtomCount(); i++) {
                atoms[permutation[i]] = permutedContainer.getAtom(i);
            }
            permutedContainer.setAtoms(atoms);
            return permutedContainer;
        } catch (CloneNotSupportedException c) {
            return null;
        }
    }

}
