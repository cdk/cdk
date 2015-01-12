/* Copyright (C) 2009-2010 maclean {gilleain.torrance@gmail.com}
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
package org.openscience.cdk.signature;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

import signature.AbstractQuotientGraph;

/**
 * A signature quotient graph has a vertex for every signature symmetry class
 * and an edge for each bond in the molecule between atoms in their class.
 *
 * So a structure where all the atoms are in the same symmetry class will have a
 * quotient graph with one vertex and one loop edge. At the other extreme, a
 * structure where every atom is in a different class will have a quotient
 * graph the same as the molecule.
 *
 * @cdk.module signature
 * @author maclean
 * @cdk.githash
 */
public class SignatureQuotientGraph extends AbstractQuotientGraph {

    /**
     * The atom container to work on
     */
    private IAtomContainer atomContainer;

    /**
     * Construct a quotient graph from the symmetry classes generated from the
     * atom container.
     *
     * @param atomContainer the structure to use
     */
    public SignatureQuotientGraph(IAtomContainer atomContainer) {
        this(atomContainer, -1);
    }

    /**
     * Construct a quotient graph using symmetry classes defined by signatures
     * of height <code>height</code>.
     *
     * @param atomContainer the structure to use
     * @param height the height of the signatures
     */
    public SignatureQuotientGraph(IAtomContainer atomContainer, int height) {
        this.atomContainer = atomContainer;
        MoleculeSignature moleculeSignature = new MoleculeSignature(atomContainer);
        super.construct(moleculeSignature.getSymmetryClasses(height));
    }

    @Override
    /** {@inheritDoc} */
    public boolean isConnected(int index1, int index2) {
        IAtom atom1 = atomContainer.getAtom(index1);
        IAtom atom2 = atomContainer.getAtom(index2);
        return atomContainer.getBond(atom1, atom2) != null;
    }

}
