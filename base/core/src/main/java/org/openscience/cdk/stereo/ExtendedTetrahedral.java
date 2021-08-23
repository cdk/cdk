/*
 * Copyright (c) 2014 European Bioinformatics Institute (EMBL-EBI)
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

package org.openscience.cdk.stereo;

import static org.openscience.cdk.interfaces.ITetrahedralChirality.Stereo;

import java.util.List;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IStereoElement;

/**
 * Extended tetrahedral configuration. Defines the winding configuration in a system with an even
 * number of cumulated pi bonds. Examples include, (R)-laballenic acid (CHEBI:38401) and
 * (S)-laballenic acid (CHEBI:38402).
 *
 * <p>The extended tetrahedral stereochemistry can be represented and handled the same as normal
 * tetrahedral stereochemistry. However the handling of the neighbours is subtly different. To
 * assist in the description here are how atoms are referred to.
 *
 * <pre>{@code
 * p0           p2     p<i>: periphals
 *  \          /       t<i>: terminals
 *   t0 = f = t1       f:    focus
 *  /          \
 * p1           p3
 * }</pre>
 *
 * The data structure stores, the central 'focus' atom and the four peripheral atoms. The peripheral
 * atoms are stored in a single array, {@code {p0, p1, p2, p3}}, the first two and last two entries
 * should be attached to the same terminal atom (t0 or t1). For convenience the terminal atoms can
 * be found with {@link #findTerminalAtoms(IAtomContainer)}.
 *
 * <pre>{@code
 * p0           p2          p0   p2
 *  \          /              \ /
 *   t0 = f = t1       -->     c       c: t0/f/t1
 *  /          \              / \
 * p1           p3           p1  p3
 * }</pre>
 *
 * The configuration treats the focus and terminal atoms as a single atom, the neighbours {@code
 * {p1, p2, p3}} then proceeded either clockwise or anti-clockwise when the centre (t0/f/t1) is
 * viewed from the first peripheral atom {@code p0}.
 *
 * <p>If any of the peripherals are implicit hydrogen atoms, then the terminal atom to which the
 * hydrogen is attached can be used as a placeholder.
 *
 * @author John May
 * @cdk.keyword extended tetrahedral
 * @cdk.keyword allene
 * @cdk.keyword axial chirality
 */
public final class ExtendedTetrahedral extends AbstractStereo<IAtom, IAtom> {

    /**
     * Create an extended tetrahedral stereo element for the provided 'focus' and 'peripherals' in
     * the given 'winding'. See class documentation an annotated storage description.
     *
     * @param focus the central cumulated atom
     * @param peripherals atoms attached to the terminal atoms
     * @param winding the configuration
     */
    public ExtendedTetrahedral(IAtom focus, IAtom[] peripherals, Stereo winding) {
        this(focus, peripherals, Stereo.toConfig(winding));
    }

    public ExtendedTetrahedral(IAtom focus, IAtom[] peripherals, int config) {
        super(focus, peripherals, AL | (CFG_MASK & config));
    }

    /**
     * The central atom in the cumulated system.
     *
     * @return the focus
     */
    public IAtom focus() {
        return getFocus();
    }

    /**
     * The neighbouring peripherals atoms, these are attached to the terminal atoms in the cumulated
     * system.
     *
     * @return the peripheral atoms
     */
    public IAtom[] peripherals() {
        return getCarriers().toArray(new IAtom[4]);
    }

    /**
     * The winding of the peripherals, when viewed from the first atom.
     *
     * @return winding configuration
     */
    public Stereo winding() {
        return Stereo.toStereo(getConfigOrder());
    }

    private static IAtom getOtherNbr(IAtomContainer mol, IAtom atom, IAtom other) {
        IAtom res = null;
        for (IBond bond : mol.getConnectedBondsList(atom)) {
            if (bond.getOrder() != IBond.Order.DOUBLE) continue;
            IAtom nbr = bond.getOther(atom);
            if (!nbr.equals(other)) {
                if (res != null) return null;
                res = nbr;
            }
        }
        return res;
    }

    /**
     * Helper method to locate two terminal atoms in a container for a given focus.
     *
     * @param container structure representation
     * @param focus cumulated atom
     * @return the terminal atoms (unordered)
     */
    public static IAtom[] findTerminalAtoms(IAtomContainer container, IAtom focus) {
        List<IBond> focusBonds = container.getConnectedBondsList(focus);
        if (focusBonds.size() != 2)
            throw new IllegalArgumentException("focus must have exactly 2 neighbors");
        IAtom leftPrev = focus;
        IAtom rightPrev = focus;
        IAtom left = focusBonds.get(0).getOther(focus);
        IAtom right = focusBonds.get(1).getOther(focus);
        IAtom tmp;
        while (left != null && right != null) {
            tmp = getOtherNbr(container, left, leftPrev);
            leftPrev = left;
            left = tmp;
            tmp = getOtherNbr(container, right, rightPrev);
            rightPrev = right;
            right = tmp;
        }
        return new IAtom[] {leftPrev, rightPrev};
    }

    /**
     * Helper method to locate two terminal atoms in a container for this extended tetrahedral
     * element. The atoms are ordered such that the first index is attached to the first two
     * peripheral atoms and the second index is attached to the second two peripheral atoms.
     *
     * @param container structure representation
     * @return the terminal atoms (ordered)
     */
    public IAtom[] findTerminalAtoms(IAtomContainer container) {
        IAtom[] atoms = findTerminalAtoms(container, getFocus());
        List<IAtom> carriers = getCarriers();
        if (container.getBond(atoms[0], carriers.get(2)) != null
                || container.getBond(atoms[0], carriers.get(3)) != null) {
            IAtom tmp = atoms[0];
            atoms[0] = atoms[1];
            atoms[1] = tmp;
        }
        return atoms;
    }

    @Override
    protected IStereoElement<IAtom, IAtom> create(IAtom focus, List<IAtom> carriers, int cfg) {
        return new ExtendedTetrahedral(focus, carriers.toArray(new IAtom[4]), cfg);
    }
}
