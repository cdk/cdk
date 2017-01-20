/*
 * Copyright (c) 2013 John May <jwmay@users.sf.net>
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */

package org.openscience.cdk.hash;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;

/**
 * Enumeration of atom encoders for seeding atomic hash codes. Generally these
 * encoders return the direct value or a prime number if that value is null.
 * These encoders are considered <i>basic</i> as the values generated are all in
 * the same range. Better encoding can be achieved by assigning discrete values
 * a section of the prime number table. However, In practice using a
 * pseudorandom number generator to distribute the encoded values provides a
 * good distribution.
 *
 * @author John May
 * @cdk.module hash
 * @see ConjugatedAtomEncoder
 * @see <a href="http://www.bigprimes.net/archive/prime/">Prime numbers
 *      archive</a>
 * @cdk.githash
 */
public enum BasicAtomEncoder implements AtomEncoder {

    /**
     * Encode the atomic number of an atom.
     *
     * @see IAtom#getAtomicNumber()
     */
    ATOMIC_NUMBER {

        /**
         *{@inheritDoc}
         */
        @Override
        public int encode(IAtom atom, IAtomContainer container) {
            Integer atomicNumber = atom.getAtomicNumber();
            return atomicNumber != null ? atomicNumber : 32451169;
        }
    },
    /**
     * Encode the mass number of an atom, allowing distinction of isotopes.
     *
     * @see IAtom#getMassNumber()
     */
    MASS_NUMBER {

        /**
         *{@inheritDoc}
         */
        @Override
        public int encode(IAtom atom, IAtomContainer container) {
            Integer massNumber = atom.getMassNumber();
            return massNumber != null ? massNumber : 32451179;
        }
    },
    /**
     * Encode the formal charge of an atom, allowing distinction of different
     * protonation states.
     *
     * @see IAtom#getFormalCharge()
     */
    FORMAL_CHARGE {

        /**
         *{@inheritDoc}
         */
        @Override
        public int encode(IAtom atom, IAtomContainer container) {
            Integer formalCharge = atom.getFormalCharge();
            return formalCharge != null ? formalCharge : 32451193;
        }
    },
    /**
     * Encode the number of explicitly connected atoms (degree).
     *
     * @see IAtomContainer#getConnectedAtomsCount(IAtom)
     */
    N_CONNECTED_ATOMS {

        /**
         *{@inheritDoc}
         */
        @Override
        public int encode(IAtom atom, IAtomContainer container) {
            return container.getConnectedAtomsCount(atom);
        }
    },
    /**
     * Encode the explicit bond order sum of an atom.
     *
     * @see IAtomContainer#getBondOrderSum(IAtom)
     */
    BOND_ORDER_SUM {

        /**
         *{@inheritDoc}
         */
        @Override
        public int encode(IAtom atom, IAtomContainer container) {
            return ((Double) container.getBondOrderSum(atom)).hashCode();
        }
    },
    /**
     * Encode the orbital hybridization of an atom.
     *
     * @see IAtom#getHybridization()
     */
    ORBITAL_HYBRIDIZATION {

        /**
         *{@inheritDoc}
         */
        @Override
        public int encode(IAtom atom, IAtomContainer container) {
            IAtomType.Hybridization hybridization = atom.getHybridization();
            return hybridization != null ? hybridization.ordinal() : 32451301;
        }
    },
    /**
     * Encode the orbital hybridization of an atom.
     *
     * @see IAtomContainer#getConnectedSingleElectronsCount(IAtom)
     */
    FREE_RADICALS {

        /**
         *{@inheritDoc}
         */
        @Override
        public int encode(IAtom atom, IAtomContainer container) {
            return container.getConnectedSingleElectronsCount(atom);
        }
    };
}
