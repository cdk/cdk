/* Copyright (c) 2026 The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.fingerprint;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.smarts.SmartsElements;

/**
 * A set of elements (atomic numbers 0-127) as a pair of bit masks. Used to
 * cheaply skip substructure searches that cannot succeed: a molecule can only
 * be matched by a SMARTS if the molecule contains every element the SMARTS
 * requires, see {@link SmartsElements}. Both sides count the elements of
 * <em>atoms</em> only (not implicit hydrogens), as the matcher does.
 */
final class ElementMask {

    /** Requires nothing (or can't be represented, so don't filter). */
    static final ElementMask NONE = new ElementMask(0, 0);

    private final long lo, hi;

    private ElementMask(long lo, long hi) {
        this.lo = lo;
        this.hi = hi;
    }

    /**
     * The elements of the atoms in a molecule. Atoms without an atomic number
     * can't be matched by an element and are ignored.
     */
    static ElementMask of(IAtomContainer container) {
        long lo = 0, hi = 0;
        for (IAtom atom : container.atoms()) {
            Integer z = atom.getAtomicNumber();
            if (z == null || z < 0 || z >= 128)
                continue;
            if (z < 64) lo |= 1L << z;
            else        hi |= 1L << (z - 64);
        }
        return new ElementMask(lo, hi);
    }

    /**
     * The elements a molecule must contain to be matched by a SMARTS. If an
     * element can't be represented the result requires nothing, which is
     * always safe.
     *
     * @throws IllegalArgumentException the SMARTS can not be parsed
     */
    static ElementMask required(String smarts) {
        long lo = 0, hi = 0;
        for (int z : SmartsElements.requiredElements(smarts)) {
            if (z < 0 || z >= 128)
                return NONE;
            if (z < 64) lo |= 1L << z;
            else        hi |= 1L << (z - 64);
        }
        return new ElementMask(lo, hi);
    }

    /** All the elements of the provided mask are in this mask. */
    boolean containsAll(ElementMask required) {
        return (required.lo & ~lo) == 0 && (required.hi & ~hi) == 0;
    }
}
