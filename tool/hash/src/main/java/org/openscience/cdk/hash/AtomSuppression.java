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

package org.openscience.cdk.hash;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IPseudoAtom;

import java.util.BitSet;

/**
 * Defines a method of suppressing certain atoms from an {@link IAtomContainer}
 * when computing the hash codes for the molecule or its atoms.
 *
 * @author John May
 * @cdk.module hash
 */
abstract class AtomSuppression {

    /**
     * Returns a new instance indicating which atoms are suppressed for this
     * suppression method.
     *
     * @param container molecule with 0 or more atoms
     * @return the vertices (atom index) which should be suppressed
     */
    abstract Suppressed suppress(IAtomContainer container);

    /** Default implementation - don't suppress anything. */
    private static final class Unsuppressed extends AtomSuppression {

        @Override
        Suppressed suppress(IAtomContainer container) {
            return Suppressed.none();
        }
    }

    /**
     * Suppresses any explicit hydrogen regardless of whether the atom is a
     * hydrogen ion or isotope.
     */
    private static final class AnyHydrogens extends AtomSuppression {

        /**{@inheritDoc} */
        @Override
        Suppressed suppress(IAtomContainer container) {
            BitSet hydrogens = new BitSet();
            for (int i = 0; i < container.getAtomCount(); i++) {
                IAtom atom = container.getAtom(i);
                hydrogens.set(i, "H".equals(atom.getSymbol()));
            }
            return Suppressed.fromBitSet(hydrogens);
        }
    }

    /** Suppresses any pseudo atom. */
    private static final class AnyPseudos extends AtomSuppression {

        /**{@inheritDoc} */
        @Override
        Suppressed suppress(IAtomContainer container) {
            BitSet hydrogens = new BitSet();
            for (int i = 0; i < container.getAtomCount(); i++) {
                IAtom atom = container.getAtom(i);
                hydrogens.set(i, atom instanceof IPseudoAtom);
            }
            return Suppressed.fromBitSet(hydrogens);
        }
    }

    /** internal reference for factory. */
    private static final AtomSuppression unsuppressed = new Unsuppressed();
    /** internal reference for factory. */
    private static final AtomSuppression anyHydrogens = new AnyHydrogens();
    /** internal reference for factory. */
    private static final AtomSuppression anyPseudos   = new AnyPseudos();

    /**
     * Do not suppress any atoms.
     *
     * @return a suppression which wont' suppress anything.
     */
    static AtomSuppression unsuppressed() {
        return unsuppressed;
    }

    /**
     * Suppress all hydrogens even if they are charged or an isotope.
     *
     * @return a suppression which will mark 'all' explicit hydrogens
     */
    static AtomSuppression anyHydrogens() {
        return anyHydrogens;
    }

    /**
     * Suppress all pseudo atoms regardless of what their label is.
     *
     * @return a suppression which will mark 'all' pseudo atoms
     */
    static AtomSuppression anyPseudos() {
        return anyPseudos;
    }
}
