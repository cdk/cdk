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

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IStereoElement;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.openscience.cdk.interfaces.ITetrahedralChirality.Stereo;

/**
 * Extended tetrahedral configuration. Defines the winding configuration in
 * a system with an even number of cumulated pi bonds. Examples include,
 * (R)-laballenic acid (CHEBI:38401) and (S)-laballenic acid (CHEBI:38402).
 *
 * <p/>
 *
 * The extended tetrahedral stereochemistry can be represented and handled the
 * same as normal tetrahedral stereochemistry. However the handling of the
 * neighbours is subtly different. To assist in the description here are how
 * atoms are referred to.
 *
 * <pre>{@code
 * p0           p2     p<i>: periphals
 *  \          /       t<i>: terminals
 *   t0 = f = t1       f:    focus
 *  /          \
 * p1           p3
 * }</pre>
 *
 * The data structure stores, the central 'focus' atom and the four peripheral
 * atoms. The peripheral atoms are stored in a single array, {@code {p0, p1,
 * p2, p3}}, the first two and last two entries should be attached to the same
 * terminal atom (t0 or t1). For convenience the terminal atoms can be found with
 * {@link #findTerminalAtoms(IAtomContainer)}.
 * <p/>
 * <pre>{@code
 * p0           p2          p0   p2
 *  \          /              \ /
 *   t0 = f = t1       -->     c       c: t0/f/t1
 *  /          \              / \
 * p1           p3           p1  p3
 * }</pre>
 * The configuration treats the focus and terminal atoms as a single atom, the
 * neighbours {@code {p1, p2, p3}} then proceeded either clockwise or
 * anti-clockwise when the centre (t0/f/t1) is viewed from the first peripheral
 * atom {@code p0}.
 *
 * <p/>
 * If any of the peripherals are implicit hydrogen atoms, then the terminal atom
 * to which the hydrogen is attached can be used as a placeholder.
 *
 * @author John May
 * @cdk.keywords extended tetrahedral
 * @cdk.keywords allene
 * @cdk.keywords axial chirality
 */
public final class ExtendedTetrahedral implements IStereoElement {

    private final IAtom   focus;
    private final IAtom[] peripherals;
    private final Stereo  winding;

    /**
     * Create an extended tetrahedral stereo element for the provided 'focus'
     * and 'peripherals' in the given 'winding'. See class documentation an
     * annotated storage description.
     *
     * @param focus       the central cumulated atom
     * @param peripherals atoms attached to the terminal atoms
     * @param winding     the configuration
     */
    public ExtendedTetrahedral(IAtom focus, IAtom[] peripherals, Stereo winding) {
        assert focus != null && peripherals != null && winding != null;
        assert peripherals.length == 4;
        this.focus = focus;
        this.peripherals = Arrays.copyOf(peripherals, 4);
        this.winding = winding;
    }

    /**
     * The central atom in the cumulated system.
     *
     * @return the focus
     */
    public IAtom focus() {
        return focus;
    }

    /**
     * The neighbouring peripherals atoms, these are attached to the terminal
     * atoms in the cumulated system.
     *
     * @return the peripheral atoms
     */
    public IAtom[] peripherals() {
        return Arrays.copyOf(peripherals, 4);
    }

    /**
     * The winding of the peripherals, when viewed from the first atom.
     *
     * @return winding configuration
     */
    public Stereo winding() {
        return winding;
    }

    /**
     * Helper method to locate two terminal atoms in a container for a given
     * focus.
     *
     * @param container structure representation
     * @param focus cumulated atom
     * @return the terminal atoms (unordered)
     */
    public static IAtom[] findTerminalAtoms(IAtomContainer container, IAtom focus) {
        List<IBond> focusBonds = container.getConnectedBondsList(focus);

        if (focusBonds.size() != 2) throw new IllegalArgumentException("focus must have exactly 2 neighbors");

        IAtom left = focusBonds.get(0).getConnectedAtom(focus);
        IAtom right = focusBonds.get(1).getConnectedAtom(focus);

        return new IAtom[]{left, right};
    }

    /**
     * Helper method to locate two terminal atoms in a container for this
     * extended tetrahedral element. The atoms are ordered such that the first
     * index is attached to the first two peripheral atoms and the second index
     * is attached to the second two peripheral atoms.
     *
     * @param container structure representation
     * @return the terminal atoms (ordered)
     */
    public IAtom[] findTerminalAtoms(IAtomContainer container) {
        List<IBond> focusBonds = container.getConnectedBondsList(focus);

        if (focusBonds.size() != 2) throw new IllegalArgumentException("focus must have exactly 2 neighbors");

        final IAtom left = focusBonds.get(0).getConnectedAtom(focus);
        final IAtom right = focusBonds.get(1).getConnectedAtom(focus);

        List<IAtom> leftAtoms = container.getConnectedAtomsList(left);

        if (leftAtoms.contains(peripherals[2]) || leftAtoms.contains(peripherals[3])) {
            return new IAtom[]{right, left};
        } else {
            return new IAtom[]{left, right};
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean contains(IAtom atom) {
        // no way to test terminals
        return focus.equals(atom) || peripherals[0].equals(atom) || peripherals[1].equals(atom)
                || peripherals[2].equals(atom) || peripherals[3].equals(atom);
    }

    /**
     * @inheritDoc
     */
    @Override
    public IStereoElement map(Map<IAtom, IAtom> atoms, Map<IBond, IBond> bonds) {
        return new ExtendedTetrahedral(atoms.get(focus), new IAtom[]{atoms.get(peripherals[0]),
                atoms.get(peripherals[1]), atoms.get(peripherals[2]), atoms.get(peripherals[3])}, winding);
    }

    /**
     * @inheritDoc
     */
    @Override
    public IChemObjectBuilder getBuilder() {
        throw new UnsupportedOperationException("non-domain object");
    }
}
