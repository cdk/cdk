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

package org.openscience.cdk.aromaticity;

import com.google.common.collect.Maps;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.ringsearch.RingSearch;

import java.util.Arrays;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Electron donation model closely mirroring the Daylight model for use in
 * generating SMILES. The model was interpreted from various resources and as
 * such may not match exactly. If you find an inconsistency please add a request
 * for enhancement to the patch tracker. One known limitation is that this model
 * does not currently consider unknown/pseudo atoms '*'. 
 *
 * The model makes a couple of assumptions which it will not correct for.
 * Checked assumptions cause the model to throw a runtime exception. <ul>
 * <li>there should be no valence errors (unchecked)</li> <li>every atom has a
 * set implicit hydrogen count (checked)</li> <li>every bond has defined order,
 * single, double etc (checked)</li> <li>atomic number of non-pseudo atoms is
 * set (checked)</li> </ul> 
 *
 * The aromaticity model in SMILES was designed to simplify canonicalisation and
 * express symmetry in a molecule. The contributed electrons can be summarised
 * as follows (refer to code for exact specification): <ul> <li>carbon,
 * nitrogen, oxygen, phosphorus, sulphur, arsenic and selenium are allow to be
 * aromatic</li> <li>atoms should be Sp2 hybridised - not actually computed</li>
 * <li>atoms adjacent to a single cyclic pi bond contribute 1 electron</li>
 * <li>neutral or negatively charged atoms with a lone pair contribute 2
 * electrons</li> <li>exocyclic pi bonds are allowed but if the exocyclic atom
 * is more electronegative it consumes an electron. As an example ketone groups
 * contribute '0' electrons.</li></ul>
 *
 * @author John May
 * @cdk.module standard
 * @cdk.githash
 */
final class DaylightModel extends ElectronDonation {

    private static final int CARBON     = 6;
    private static final int NITROGEN   = 7;
    private static final int OXYGEN     = 8;
    private static final int PHOSPHORUS = 15;
    private static final int SULPHUR    = 16;
    private static final int ARSENIC    = 33;
    private static final int SELENIUM   = 34;

    /**{@inheritDoc} */
    @Override
    int[] contribution(IAtomContainer container, RingSearch ringSearch) {

        int n = container.getAtomCount();

        // we compute values we need for all atoms and then make the decisions
        // - this avoids costly operations such as looking up connected
        // bonds on each atom at the cost of memory
        int[] degree = new int[n];
        int[] bondOrderSum = new int[n];
        int[] nCyclicPiBonds = new int[n];
        int[] exocyclicPiBond = new int[n];
        int[] electrons = new int[n];

        Arrays.fill(exocyclicPiBond, -1);

        // index atoms and set the degree to the number of implicit hydrogens
        Map<IAtom, Integer> atomIndex = Maps.newHashMapWithExpectedSize(n);
        for (int i = 0; i < n; i++) {
            IAtom a = container.getAtom(i);
            atomIndex.put(a, i);
            degree[i] = checkNotNull(a.getImplicitHydrogenCount(),
                    "Aromaticity model requires implicit hydrogen count is set.");
        }

        // for each bond we increase the degree count and check for cyclic and
        // exocyclic pi bonds. if there is a cyclic pi bond the atom is marked.
        // if there is an exocyclic pi bond we store the adjacent atom for
        // lookup later.
        for (IBond bond : container.bonds()) {
            int u = atomIndex.get(bond.getAtom(0));
            int v = atomIndex.get(bond.getAtom(1));
            degree[u]++;
            degree[v]++;

            IBond.Order order = checkNotNull(bond.getOrder(), "Aromaticity model requires that bond orders must be set");

            switch (order) {
                case UNSET:
                    throw new IllegalArgumentException("Aromaticity model requires that bond orders must be set");
                case DOUBLE:
                    if (ringSearch.cyclic(u, v)) {
                        nCyclicPiBonds[u]++;
                        nCyclicPiBonds[v]++;
                    } else {
                        exocyclicPiBond[u] = v;
                        exocyclicPiBond[v] = u;
                    }
                    // note - fall through
                case SINGLE:
                case TRIPLE:
                case QUADRUPLE:
                    bondOrderSum[u] += order.numeric();
                    bondOrderSum[v] += order.numeric();
            }
        }

        // now make a decision on how many electrons each atom contributes
        for (int i = 0; i < n; i++) {

            int element = element(container.getAtom(i));
            int charge = charge(container.getAtom(i));

            // abnormal valence, usually indicated a radical. these cause problems
            // with kekulisations
            int bondedValence = bondOrderSum[i] + container.getAtom(i).getImplicitHydrogenCount();
            if (!normal(element, charge, bondedValence)) {
                electrons[i] = -1;
            }

            // non-aromatic element, acyclic atoms, atoms with more than three
            // neighbors and atoms with more than 1 cyclic pi bond are not
            // considered
            else if (!aromaticElement(element) || !ringSearch.cyclic(i) || degree[i] > 3 || nCyclicPiBonds[i] > 1) {
                electrons[i] = -1;
            }

            // exocyclic bond contributes 0 or 1 electrons depending on
            // preset electronegativity - check the exocyclicContribution method
            else if (exocyclicPiBond[i] >= 0) {
                electrons[i] = exocyclicContribution(element, element(container.getAtom(exocyclicPiBond[i])), charge,
                        nCyclicPiBonds[i]);
            }

            // any atom (except arsenic) with one cyclic pi bond contributes a
            // single electron
            else if (nCyclicPiBonds[i] == 1) {
                electrons[i] = element == ARSENIC ? -1 : 1;
            }

            // a anion with a lone pair contributes 2 electrons - simplification
            // here is we count the number free valence electrons but also
            // check if the bonded valence is okay (i.e. not a radical)
            else if (charge <= 0 && charge > -3) {
                if (valence(element, charge) - bondOrderSum[i] >= 2)
                    electrons[i] = 2;
                else
                    electrons[i] = -1;
            }

            else {
                // cation with no double bonds - single exception?
                if (element == CARBON && charge > 0)
                    electrons[i] = 0;
                else
                    electrons[i] = -1;
            }
        }

        return electrons;
    }

    /**
     * Defines the number of electrons contributed when a pi bond is exocyclic
     * (spouting). When an atom is connected to an more electronegative atom
     * then the electrons are 'pulled' from the ring. The preset conditions are
     * as follows:
     *
     * <ul> <li>A cyclic carbon with an exocyclic pi bond to anything but carbon
     * contributes 0 electrons. If the exocyclic atom is also a carbon then 1
     * electron is contributed.</li> <li>A cyclic 4 valent nitrogen or
     * phosphorus cation with an exocyclic pi bond will always contribute 1
     * electron. A 5 valent neutral nitrogen or phosphorus with an exocyclic
     * bond to an oxygen contributes 1 electron. </li> <li>A neutral sulphur
     * connected to an oxygen contributes 2 electrons</li><li>If none of the
     * previous conditions are met the atom is not considered as being able to
     * participate in an aromatic system and -1 is returned.</li> </ul>
     *
     * @param element      the element of the cyclic atom
     * @param otherElement the element of the exocyclic atom which is connected
     *                     to the cyclic atom by a pi bond
     * @param charge       the charge of the cyclic atom
     * @param nCyclic      the number of cyclic pi bonds adjacent to cyclic
     *                     atom
     * @return number of contributed electrons
     */
    private static int exocyclicContribution(int element, int otherElement, int charge, int nCyclic) {
        switch (element) {
            case CARBON:
                return otherElement != CARBON ? 0 : 1;
            case NITROGEN:
            case PHOSPHORUS:
                if (charge == 1)
                    return 1;
                else if (charge == 0 && otherElement == OXYGEN && nCyclic == 1) return 1;
                return -1;
            case SULPHUR:
                // quirky but try - O=C1C=CS(=O)C=C1
                return charge == 0 && otherElement == OXYGEN ? 2 : -1;
        }
        return -1;
    }

    /**
     * Is the element specified by the atomic number, allowed to be aromatic by
     * the daylight specification. Allowed elements are C, N, O, P, S, As, Se
     * and *. This model allows all except for the unknown ('*') element.
     *
     * @param element atomic number of element
     * @return the element can be aromatic
     */
    private static boolean aromaticElement(int element) {
        switch (element) {
            case CARBON:
            case NITROGEN:
            case OXYGEN:
            case PHOSPHORUS:
            case SULPHUR:
            case ARSENIC:
            case SELENIUM:
                return true;
        }
        return false;
    }

    /**
     * The element has normal valence for the specified charge.
     *
     * @param element atomic number
     * @param charge  formal charge
     * @param valence bonded electrons
     * @return acceptable for this model
     */
    private static boolean normal(int element, int charge, int valence) {
        switch (element) {
            case CARBON:
                if (charge == -1 || charge == +1) return valence == 3;
                return charge == 0 && valence == 4;
            case NITROGEN:
            case PHOSPHORUS:
            case ARSENIC:
                if (charge == -1) return valence == 2;
                if (charge == +1) return valence == 4;
                return charge == 0 && (valence == 3 || (valence == 5 && element == NITROGEN));
            case OXYGEN:
                if (charge == +1) return valence == 3;
                return charge == 0 && valence == 2;
            case SULPHUR:
            case SELENIUM:
                if (charge == +1) return valence == 3;
                return charge == 0 && (valence == 2 || valence == 4 || valence == 6);
        }
        return false;
    }

    /**
     * Lookup of the number of valence electrons for the element at a given
     * charge.
     *
     * @param element the atomic number of an element
     * @param charge  the formal charge on the atom
     * @return the valence
     * @throws UnsupportedOperationException encountered an element which the
     *                                       valence was not encoded for
     */
    private int valence(int element, int charge) {
        return valence(element - charge);
    }

    /**
     * Lookup of the number of valence electrons for elements near those which
     * this model considers aromatic. As only the {@link #aromaticElement(int)}
     * are checked we need only consider elements within a charge range.
     *
     * @param element the atomic number of an element
     * @return the valence
     * @throws UnsupportedOperationException encountered an element which the
     *                                       valence was not encoded for
     */
    private int valence(int element) {
        switch (element) {
            case 5: // boron
            case 13: // aluminium
            case 31: // gallium
                return 3;
            case CARBON:
            case 14: // silicon
            case 32: // germanium
                return 4;
            case NITROGEN:
            case PHOSPHORUS:
            case ARSENIC:
                return 5;
            case OXYGEN:
            case SULPHUR:
            case SELENIUM:
                return 6;
            case 9: // fluorine
            case 17: // chlorine
            case 35: // bromine
                return 7;
        }
        throw new UnsupportedOperationException("Valence not yet handled for element with atomic number " + element);
    }

    /**
     * Get the atomic number as an non-null integer value. Although pseudo atoms
     * are not considered by this model the pseudo atoms are intercepted to have
     * non-null atomic number (defaults to 0).
     *
     * @param atom atom to get the element from
     * @return the formal charge
     */
    private int element(IAtom atom) {
        Integer element = atom.getAtomicNumber();
        if (element != null) return element;
        if (atom instanceof IPseudoAtom) return 0;
        throw new IllegalArgumentException("Aromaiticty model requires atomic numbers to be set");
    }

    /**
     * Get the formal charge as an integer value - null defaults to 0.
     *
     * @param atom the atom to get the charge of
     * @return the formal charge
     */
    private int charge(IAtom atom) {
        return atom.getFormalCharge() != null ? atom.getFormalCharge() : 0;
    }
}
