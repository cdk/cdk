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

package org.openscience.cdk.isomorphism;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;

/**
 * Defines compatibility checking of atoms for (subgraph)-isomorphism mapping.
 *
 * @author John May
 * @cdk.module isomorphism
 */
public abstract class AtomMatcher {

    /**
     * Are the semantics of {@code atom1} compatible with {@code atom2}.
     *
     * @param atom1 an atom from a query container
     * @param atom2 an atom from the target container
     * @return the atom1 can be paired with atom2
     */
    public abstract boolean matches(IAtom atom1, IAtom atom2);

    /**
     * Atoms are always compatible.
     *
     * @return a matcher for which all atoms match
     */
    public static AtomMatcher forAny() {
        return new AnyMatcher();
    }

    /**
     * Atoms are compatible if they are the same element.
     *
     * @return a matcher which checks element compatibility
     */
    public static AtomMatcher forElement() {
        return new ElementMatcher();
    }

    /**
     * Atoms are compatible if the second atom ({@code atom2}) is accepted by
     * the {@link IQueryAtom}, {@code atom1}.
     *
     * @return a matcher which checks query atom compatibility
     */
    public static AtomMatcher forQuery() {
        return new QueryMatcher();
    }

    /** A matcher defines all atoms as compatible. */
    private static final class AnyMatcher extends AtomMatcher {

        /**{@inheritDoc} */
        @Override
        public boolean matches(IAtom atom1, IAtom atom2) {
            return true;
        }
    }

    /**
     * A matcher to use when all atoms are {@link IQueryAtom}s. {@code atom1} is
     * cast to a query atom and matched against {@code atom2}.
     */
    private static final class QueryMatcher extends AtomMatcher {

        /**{@inheritDoc} */
        @Override
        public boolean matches(IAtom atom1, IAtom atom2) {
            return ((IQueryAtom) atom1).matches(atom2);
        }
    }

    /**
     * A matcher to use when all atoms are {@link IQueryAtom}s. {@code atom1} is
     * cast to a query atom and matched against {@code atom2}.
     */
    private static final class ElementMatcher extends AtomMatcher {

        /**{@inheritDoc} */
        @Override
        public boolean matches(IAtom atom1, IAtom atom2) {
            return atomicNumber(atom1) == atomicNumber(atom2);
        }

        /**
         * Null safe atomic number access.
         *
         * @param atom an atom
         * @return the atomic number
         */
        private int atomicNumber(IAtom atom) {
            Integer elem = atom.getAtomicNumber();
            if (elem != null) return elem;
            if (atom instanceof IPseudoAtom) return 0;
            throw new NullPointerException("an atom had unset atomic number");
        }
    }
}
