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

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import signature.AbstractVertexSignature;

/**
 * <p>
 * The signature {@cdk.cite FAU03, FAU04} for a molecule rooted at a particular
 * atom.
 * </p>
 *
 * <p>
 * A signature is a description of the connectivity of a molecule, in the form
 * of a tree-like structure called a directed acyclic graph (DAG). This DAG can
 * be written out as a string, for example ethane:
 * </p>
 *
 * <pre>
 *   [C]([C]([H][H][H])[H][H][H])
 * </pre>
 *
 * <p>
 * where each atom is represented by an atom symbol in square brackets. The
 * branching of the tree is indicated by round brackets. When the molecule has a
 * cycle, the signature string will have numbers after the atom symbol, like:
 * </p>
 *
 * <pre>
 * [C]([C]([C,0])[C]([C,0]))
 * </pre>
 *
 * <p>
 * these are known as 'colors' and indicate ring closures, in a roughly similar
 * way to SMILES notation. Note that the colors start from 0 in this
 * implementation, in contrast to the examples in {@cdk.cite FAU04}.
 * </p>
 *
 * <p>
 * Multiple bonds are represented by symbols in front of the opening square
 * bracket of an atom. Double bonds are '=', triple are '#'. Since there is a
 * defined direction for the signature tree, only the child node will have the
 * bond symbol, and the relevant bond is to the parent.
 * </p>
 *
 * @cdk.module signature
 * @author maclean
 * @cdk.githash
 */
public class AtomSignature extends AbstractVertexSignature {

    /**
     * The atom container to make signatures from.
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
     * Create an atom signature for the atom <code>atom</code>.
     *
     * @param atom the atom to make the signature for
     * @param molecule the molecule to create the signature from
     */
    public AtomSignature(IAtom atom, IAtomContainer molecule) {
        this(molecule.indexOf(atom), molecule);
    }

    /**
     * Create an atom signature starting at <code>atomIndex</code> and with a
     * maximum height of <code>height</code>.
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

    /**
     * Create an atom signature for the atom <code>atom</code> and with a
     * maximum height of <code>height</code>.
     *
     * @param atom     the index of the atom that roots this signature
     * @param height   the maximum height of the signature
     * @param molecule the molecule to create the signature from
     */
    public AtomSignature(IAtom atom, int height, IAtomContainer molecule) {
        this(molecule.indexOf(atom), height, molecule);
    }

    /**
     * Create an atom signature starting at <code>atomIndex</code>, with maximum
     * height of <code>height</code>, and using a particular invariant type.
     *
     * @param atomIndex the index of the atom that roots this signature
     * @param height the maximum height of the signature
     * @param invariantType the type of invariant (int, string, ...)
     * @param molecule the molecule to create the signature from
     */
    public AtomSignature(int atomIndex, int height, InvariantType invariantType, IAtomContainer molecule) {
        super(invariantType);
        this.molecule = molecule;
        super.create(atomIndex, molecule.getAtomCount(), height);
    }

    /**
     * Create an atom signature for the atom <code>atom</code>, with maximum
     * height of <code>height</code>, and using a particular invariant type.
     *
     * @param atom the index of the atom that roots this signature
     * @param height the maximum height of the signature
     * @param invariantType the type of invariant (int, string, ...)
     * @param molecule the molecule to create the signature from
     */
    public AtomSignature(IAtom atom, int height, InvariantType invariantType, IAtomContainer molecule) {
        this(molecule.indexOf(atom), height, invariantType, molecule);
    }

    @Override
    /** {@inheritDoc} */
    protected int getIntLabel(int vertexIndex) {
        IAtom atom = molecule.getAtom(vertexIndex);
        return atom.getMassNumber();
    }

    @Override
    /** {@inheritDoc} */
    protected int[] getConnected(int vertexIndex) {
        IAtom atom = this.molecule.getAtom(vertexIndex);
        List<IAtom> connected = this.molecule.getConnectedAtomsList(atom);
        int[] connectedIndices = new int[connected.size()];
        int indexCounter = 0;
        for (IAtom otherAtom : connected) {
            connectedIndices[indexCounter++] = this.molecule.indexOf(otherAtom);
        }
        return connectedIndices;
    }

    @Override
    /** {@inheritDoc} */
    protected String getEdgeLabel(int vertexIndex, int otherVertexIndex) {
        IAtom atomA = this.molecule.getAtom(vertexIndex);
        IAtom atomB = this.molecule.getAtom(otherVertexIndex);
        IBond bond = this.molecule.getBond(atomA, atomB);
        if (bond != null) {
            if (bond.getFlag(CDKConstants.ISAROMATIC)) {
                return "p";
            }
            switch (bond.getOrder()) {
            //                case SINGLE: return "-";
                case SINGLE:
                    return "";
                case DOUBLE:
                    return "=";
                case TRIPLE:
                    return "#";
                case QUADRUPLE:
                    return "$";
                default:
                    return "";
            }
        } else {
            return "";
        }
    }

    @Override
    /** {@inheritDoc} */
    protected String getVertexSymbol(int vertexIndex) {
        return this.molecule.getAtom(vertexIndex).getSymbol();
    }

    @Override
    /** {@inheritDoc} */
    protected int convertEdgeLabelToColor(String edgeLabel) {
        if (edgeLabel.equals("")) {
            return 1;
        } else if (edgeLabel.equals("=")) {
            return 2;
        } else if (edgeLabel.equals("#")) {
            return 3;
        } else if (edgeLabel.equals("$")) {
            return 4;
        } else if (edgeLabel.equals("p")) {
            return 5;
        }
        return 0;
    }

}
