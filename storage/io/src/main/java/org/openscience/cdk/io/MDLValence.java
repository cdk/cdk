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
 *
 * Additionally the 'MDLValence' method has the following licence/copyright.
 *
 * Copyright (C) 2012 NextMove Software
 *
 * @@ All Rights Reserved @@ This file is part of the RDKit. The contents
 * are covered by the terms of the BSD license which is included in the file
 * license.txt, found at the root of the RDKit source tree.
 */

package org.openscience.cdk.io;

import com.google.common.collect.Maps;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import java.util.Map;

/**
 * Adds implicit hydrogens and specifies valency using the MDL valence model.
 *
 * @author John May
 * @cdk.module io
 * @see <a href="http://nextmovesoftware.com/blog/2013/02/27/explicit-and-implicit-hydrogens-taking-liberties-with-valence/">Explicit
 *      and Implicit Hydrogens: taking liberties with valence</a>
 */
final class MDLValence {

    private MDLValence() {}

    /**
     * Apply the MDL valence model to the provided atom container.
     *
     * @param container an atom container loaded from an MDL format
     * @return the container (for convenience)
     */
    static IAtomContainer apply(IAtomContainer container) {

        int n = container.getAtomCount();

        int[] valences = new int[n];

        Map<IAtom, Integer> atomToIndex = Maps.newHashMapWithExpectedSize(n);
        for (IAtom atom : container.atoms())
            atomToIndex.put(atom, atomToIndex.size());

        // compute the bond order sums
        for (IBond bond : container.bonds()) {
            int u = atomToIndex.get(bond.getBegin());
            int v = atomToIndex.get(bond.getEnd());

            int bondOrder = bond.getOrder().numeric();

            valences[u] += bondOrder;
            valences[v] += bondOrder;
        }

        for (int i = 0; i < n; i++) {

            IAtom atom = container.getAtom(i);
            Integer charge = atom.getFormalCharge();
            Integer element = atom.getAtomicNumber();

            if (element == null) continue;

            // unset = 0 in this case
            charge = charge == null ? 0 : charge;

            int explicit = valences[i];

            // if there was a valence read from the mol file use that otherwise
            // use the default value from the valence model to set the correct
            // number of implied hydrogens
            if (atom.getValency() != null) {
                atom.setImplicitHydrogenCount(atom.getValency() - explicit);
            } else {
                int implicit = implicitValence(element, charge, valences[i]);
                atom.setImplicitHydrogenCount(implicit - explicit);
                atom.setValency(implicit);
            }
        }

        return container;
    }

    /**
     * Given an element (atomic number) its charge and the explicit valence
     * (bond order sum) return the implicit valence for that atom. This valence
     * is from the MDL valence model which was decoded by NextMove Software and
     * licenced as below.
     *
     * <blockquote> $Id: MDLValence.h 2288 2012-11-26 03:39:27Z glandrum $
     *
     * Copyright (C) 2012 NextMove Software
     *
     * @@ All Rights Reserved @@ This file is part of the RDKit. The contents
     * are covered by the terms of the BSD license which is included in the file
     * license.txt, found at the root of the RDKit source tree. </blockquote>
     * @see <a href="http://nextmovesoftware.com/blog/2013/02/27/explicit-and-implicit-hydrogens-taking-liberties-with-valence/">Explicit
     *      and Implicit Hydrogens taking liberties with valence</a>
     */
    static int implicitValence(int elem, int q, int val) {
        switch (elem) {
            case 1: // H
            case 3: // Li
            case 11: // Na
            case 19: // K
            case 37: // Rb
            case 55: // Cs
            case 87: // Fr
                if (q == 0 && val <= 1) return 1;
                break;

            case 4: // Be
            case 12: // Mg
            case 20: // Ca
            case 38: // Sr
            case 56: // Ba
            case 88: // Ra
                switch (q) {
                    case 0:
                        if (val <= 2) return 2;
                        break;
                    case 1:
                        if (val <= 1) return 1;
                        break;
                }
                break;

            case 5: // B
                switch (q) {
                    case -4:
                        if (val <= 1) return 1;
                        break;
                    case -3:
                        if (val <= 2) return 2;
                        break;
                    case -2:
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        break;
                    case -1:
                        if (val <= 4) return 4;
                        break;
                    case 0:
                        if (val <= 3) return 3;
                        break;
                    case 1:
                        if (val <= 2) return 2;
                        break;
                    case 2:
                        if (val <= 1) return 1;
                        break;
                }
                break;

            case 6: // C
                switch (q) {
                    case -3:
                        if (val <= 1) return 1;
                        break;
                    case -2:
                        if (val <= 2) return 2;
                        break;
                    case -1:
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        break;
                    case 0:
                        if (val <= 4) return 4;
                        break;
                    case 1:
                        if (val <= 3) return 3;
                        break;
                    case 2:
                        if (val <= 2) return 2;
                        break;
                    case 3:
                        if (val <= 1) return 1;
                        break;
                }
                break;

            case 7: // N
                switch (q) {
                    case -2:
                        if (val <= 1) return 1;
                        break;
                    case -1:
                        if (val <= 2) return 2;
                        break;
                    case 0:
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        break;
                    case 1:
                        if (val <= 4) return 4;
                        break;
                    case 2:
                        if (val <= 3) return 3;
                        break;
                    case 3:
                        if (val <= 2) return 2;
                        break;
                    case 4:
                        if (val <= 1) return 1;
                        break;
                }
                break;

            case 8: // O
                switch (q) {
                    case -1:
                        if (val <= 1) return 1;
                        break;
                    case 0:
                        if (val <= 2) return 2;
                        break;
                    case 1:
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        break;
                    case 2:
                        if (val <= 4) return 4;
                        break;
                    case 3:
                        if (val <= 3) return 3;
                        break;
                    case 4:
                        if (val <= 2) return 2;
                        break;
                    case 5:
                        if (val <= 1) return 1;
                        break;
                }
                break;

            case 9: // F
                switch (q) {
                    case 0:
                        if (val <= 1) return 1;
                        break;
                    case 1:
                        if (val <= 2) return 2;
                        break;
                    case 2:
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        break;
                    case 3:
                        if (val <= 4) return 4;
                        break;
                    case 4:
                        if (val <= 3) return 3;
                        break;
                    case 5:
                        if (val <= 2) return 2;
                        break;
                    case 6:
                        if (val <= 1) return 1;
                        break;
                }
                break;

            case 13: // Al
                switch (q) {
                    case -4:
                        if (val <= 1) return 1;
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        if (val <= 7) return 7;
                        break;
                    case -3:
                        if (val <= 2) return 2;
                        if (val <= 4) return 4;
                        if (val <= 6) return 6;
                        break;
                    case -2:
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        break;
                    case -1:
                        if (val <= 4) return 4;
                        break;
                    case 0:
                        if (val <= 3) return 3;
                        break;
                    case 1:
                        if (val <= 2) return 2;
                        break;
                    case 2:
                        if (val <= 1) return 1;
                        break;
                }
                break;

            case 14: // Si
                switch (q) {
                    case -3:
                        if (val <= 1) return 1;
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        if (val <= 7) return 7;
                        break;
                    case -2:
                        if (val <= 2) return 2;
                        if (val <= 4) return 4;
                        if (val <= 6) return 6;
                        break;
                    case -1:
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        break;
                    case 0:
                        if (val <= 4) return 4;
                        break;
                    case 1:
                        if (val <= 3) return 3;
                        break;
                    case 2:
                        if (val <= 2) return 2;
                        break;
                    case 3:
                        if (val <= 1) return 1;
                        break;
                }
                break;

            case 15: // P
                switch (q) {
                    case -2:
                        if (val <= 1) return 1;
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        if (val <= 7) return 7;
                        break;
                    case -1:
                        if (val <= 2) return 2;
                        if (val <= 4) return 4;
                        if (val <= 6) return 6;
                        break;
                    case 0:
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        break;
                    case 1:
                        if (val <= 4) return 4;
                        break;
                    case 2:
                        if (val <= 3) return 3;
                        break;
                    case 3:
                        if (val <= 2) return 2;
                        break;
                    case 4:
                        if (val <= 1) return 1;
                        break;
                }
                break;

            case 16: // S
                switch (q) {
                    case -1:
                        if (val <= 1) return 1;
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        if (val <= 7) return 7;
                        break;
                    case 0:
                        if (val <= 2) return 2;
                        if (val <= 4) return 4;
                        if (val <= 6) return 6;
                        break;
                    case 1:
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        break;
                    case 2:
                        if (val <= 4) return 4;
                        break;
                    case 3:
                        if (val <= 3) return 3;
                        break;
                    case 4:
                        if (val <= 2) return 2;
                        break;
                    case 5:
                        if (val <= 1) return 1;
                        break;
                }
                break;

            case 17: // Cl
                switch (q) {
                    case 0:
                        if (val <= 1) return 1;
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        if (val <= 7) return 7;
                        break;
                    case 1:
                        if (val <= 2) return 2;
                        if (val <= 4) return 4;
                        if (val <= 6) return 6;
                        break;
                    case 2:
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        break;
                    case 3:
                        if (val <= 4) return 4;
                        break;
                    case 4:
                        if (val <= 3) return 3;
                        break;
                    case 5:
                        if (val <= 2) return 2;
                        break;
                    case 6:
                        if (val <= 1) return 1;
                        break;
                }
                break;

            case 31: // Ga
                switch (q) {
                    case -4:
                        if (val <= 1) return 1;
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        if (val <= 7) return 7;
                        break;
                    case -3:
                        if (val <= 2) return 2;
                        if (val <= 4) return 4;
                        if (val <= 6) return 6;
                        break;
                    case -2:
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        break;
                    case -1:
                        if (val <= 4) return 4;
                        break;
                    case 0:
                        if (val <= 3) return 3;
                        break;
                    case 2:
                        if (val <= 1) return 1;
                        break;
                }
                break;

            case 32: // Ge
                switch (q) {
                    case -3:
                        if (val <= 1) return 1;
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        if (val <= 7) return 7;
                        break;
                    case -2:
                        if (val <= 2) return 2;
                        if (val <= 4) return 4;
                        if (val <= 6) return 6;
                        break;
                    case -1:
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        break;
                    case 0:
                        if (val <= 4) return 4;
                        break;
                    case 1:
                        if (val <= 3) return 3;
                        break;
                    case 3:
                        if (val <= 1) return 1;
                        break;
                }
                break;

            case 33: // As
                switch (q) {
                    case -2:
                        if (val <= 1) return 1;
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        if (val <= 7) return 7;
                        break;
                    case -1:
                        if (val <= 2) return 2;
                        if (val <= 4) return 4;
                        if (val <= 6) return 6;
                        break;
                    case 0:
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        break;
                    case 1:
                        if (val <= 4) return 4;
                        break;
                    case 2:
                        if (val <= 3) return 3;
                        break;
                    case 4:
                        if (val <= 1) return 1;
                        break;
                }
                break;

            case 34: // Se
                switch (q) {
                    case -1:
                        if (val <= 1) return 1;
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        if (val <= 7) return 7;
                        break;
                    case 0:
                        if (val <= 2) return 2;
                        if (val <= 4) return 4;
                        if (val <= 6) return 6;
                        break;
                    case 1:
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        break;
                    case 2:
                        if (val <= 4) return 4;
                        break;
                    case 3:
                        if (val <= 3) return 3;
                        break;
                    case 5:
                        if (val <= 1) return 1;
                        break;
                }
                break;

            case 35: // Br
                switch (q) {
                    case 0:
                        if (val <= 1) return 1;
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        if (val <= 7) return 7;
                        break;
                    case 1:
                        if (val <= 2) return 2;
                        if (val <= 4) return 4;
                        if (val <= 6) return 6;
                        break;
                    case 2:
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        break;
                    case 3:
                        if (val <= 4) return 4;
                        break;
                    case 4:
                        if (val <= 3) return 3;
                        break;
                    case 6:
                        if (val <= 1) return 1;
                        break;
                }
                break;

            case 49: // In
                switch (q) {
                    case -4:
                        if (val <= 1) return 1;
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        if (val <= 7) return 7;
                        break;
                    case -3:
                        if (val <= 2) return 2;
                        if (val <= 4) return 4;
                        if (val <= 6) return 6;
                        break;
                    case -2:
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        break;
                    case -1:
                        if (val <= 2) return 2;
                        if (val <= 4) return 4;
                        break;
                    case 0:
                        if (val <= 3) return 3;
                        break;
                    case 2:
                        if (val <= 1) return 1;
                        break;
                }
                break;

            case 50: // Sn
            case 82: // Pb
                switch (q) {
                    case -3:
                        if (val <= 1) return 1;
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        if (val <= 7) return 7;
                        break;
                    case -2:
                        if (val <= 2) return 2;
                        if (val <= 4) return 4;
                        if (val <= 6) return 6;
                        break;
                    case -1:
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        break;
                    case 0:
                        if (val <= 2) return 2;
                        if (val <= 4) return 4;
                        break;
                    case 1:
                        if (val <= 3) return 3;
                        break;
                    case 3:
                        if (val <= 1) return 1;
                        break;
                }
                break;

            case 51: // Sb
            case 83: // Bi
                switch (q) {
                    case -2:
                        if (val <= 1) return 1;
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        if (val <= 7) return 7;
                        break;
                    case -1:
                        if (val <= 2) return 2;
                        if (val <= 4) return 4;
                        if (val <= 6) return 6;
                        break;
                    case 0:
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        break;
                    case 1:
                        if (val <= 2) return 2;
                        if (val <= 4) return 4;
                        break;
                    case 2:
                        if (val <= 3) return 3;
                        break;
                    case 4:
                        if (val <= 1) return 1;
                        break;
                }
                break;

            case 52: // Te
            case 84: // Po
                switch (q) {
                    case -1:
                        if (val <= 1) return 1;
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        if (val <= 7) return 7;
                        break;
                    case 0:
                        if (val <= 2) return 2;
                        if (val <= 4) return 4;
                        if (val <= 6) return 6;
                        break;
                    case 1:
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        break;
                    case 2:
                        if (val <= 2) return 2;
                        if (val <= 4) return 4;
                        break;
                    case 3:
                        if (val <= 3) return 3;
                        break;
                    case 5:
                        if (val <= 1) return 1;
                        break;
                }
                break;

            case 53: // I
            case 85: // At
                switch (q) {
                    case 0:
                        if (val <= 1) return 1;
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        if (val <= 7) return 7;
                        break;
                    case 1:
                        if (val <= 2) return 2;
                        if (val <= 4) return 4;
                        if (val <= 6) return 6;
                        break;
                    case 2:
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        break;
                    case 3:
                        if (val <= 2) return 2;
                        if (val <= 4) return 4;
                        break;
                    case 4:
                        if (val <= 3) return 3;
                        break;
                    case 6:
                        if (val <= 1) return 1;
                        break;
                }
                break;

            case 81: // Tl
                switch (q) {
                    case -4:
                        if (val <= 1) return 1;
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        if (val <= 7) return 7;
                        break;
                    case -3:
                        if (val <= 2) return 2;
                        if (val <= 4) return 4;
                        if (val <= 6) return 6;
                        break;
                    case -2:
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        break;
                    case -1:
                        if (val <= 2) return 2;
                        if (val <= 4) return 4;
                        break;
                    case 0:
                        if (val <= 1) return 1;
                        if (val <= 3) return 3;
                        break;
                }
                break;

        }
        return val;
    }
}
