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

import java.util.List;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import signature.AbstractVertexSignature;

/**
 * The signature for a molecule rooted at a particular atom.
 * 
 * @cdk.module signature
 * @author maclean
 * @cdk.githash
 */
@TestClass("org.openscience.cdk.signature.AtomSignatureTest")
public class AtomSignature extends AbstractVertexSignature {
    
    /**
     * The atom container to make signatures from
     */
    private IAtomContainer molecule;
    
    /**
     * Create an atom signature starting at <code>atomIndex</code>.
     * 
     * @param atomIndex the index of the atom that roots this signature
     * @param molecule the molecule to create the signature from
     */
    public AtomSignature(int atomIndex, IAtomContainer molecule) {
        super();
        this.molecule = molecule;
        super.createMaximumHeight(atomIndex, molecule.getAtomCount());
    }
    
    /**
     * Create an atom signature starting at <code>atomIndex</code> and with a
     * maximum height of <code>h</code>.
     * 
     * @param atomIndex the index of the atom that roots this signature
     * @param height the maximum height of the signature 
     * @param molecule the molecule to create the signature from
     */
    public AtomSignature(int atomIndex, int height, IAtomContainer molecule) {
        super();
        this.molecule = molecule;
        super.create(atomIndex, molecule.getAtomCount(), height);
    }
    
    public AtomSignature(int atomIndex, int height, 
            InvariantType invariantType, IAtomContainer molecule) {
        super(invariantType);
    }
    
    public int getIntLabel(int vertexIndex) {
        IAtom atom = molecule.getAtom(vertexIndex);
        return atom.getMassNumber();
    }

    @Override /** {@inheritDoc} */
    public int[] getConnected(int vertexIndex) {
        IAtom atom  = this.molecule.getAtom(vertexIndex);
        List<IAtom> connected = this.molecule.getConnectedAtomsList(atom);
        int[] connectedIndices = new int[connected.size()];
        int indexCounter = 0;
        for (IAtom otherAtom : connected) {
            connectedIndices[indexCounter++] = this.molecule.getAtomNumber(otherAtom);
        }
        return connectedIndices;
    }

    @Override /** {@inheritDoc} */
    public String getEdgeLabel(int vertexIndex, int otherVertexIndex) {
        IAtom atomA = this.molecule.getAtom(vertexIndex);
        IAtom atomB = this.molecule.getAtom(otherVertexIndex);
        IBond bond = this.molecule.getBond(atomA, atomB);
        if (bond != null) {
            switch (bond.getOrder()) {
//                case SINGLE: return "-";
                case SINGLE: return "";
                case DOUBLE: return "=";
                case TRIPLE: return "#";
                case QUADRUPLE: return "$";
                default: return "";
            }
        } else {
            return "";
        }
    }

    @Override /** {@inheritDoc} */
    public String getVertexSymbol(int vertexIndex) {
        return this.molecule.getAtom(vertexIndex).getSymbol();
    }

}
