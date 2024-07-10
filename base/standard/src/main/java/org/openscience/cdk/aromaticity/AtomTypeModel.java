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

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.exception.NoSuchAtomTypeException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.ringsearch.RingSearch;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.openscience.cdk.interfaces.IAtomType.Hybridization;

/**
 * Electron donation model using the CDK atom types. This model closely mirrors
 * the previously implementations {@code CDKHueckelAromaticityDetector} and
 * {@code DoubleBondAcceptingAromaticityDetector}. One can choose to allow
 * contribution from exocyclic pi bonds in the constructor. Allowing exocyclic
 * pi bonds results in molecules such as hexamethylidenecyclohexane ({@code
 * C=C1C(=C)C(=C)C(=C)C(=C)C1=C}) being considered aromatic.
 *
 * @author John May
 * @cdk.module standard
 */
// mores tests in - org.openscience.cdk.aromaticity.ExocyclicAtomTypeModelTest
final class AtomTypeModel extends ElectronDonation {

    // JDK 9+ has Map.of() which is more concise
    private static Map<String, Integer> typeToElectronContribMap() {
        Map<String,Integer> map = new HashMap<>();
        map.put("N.planar3", 2);
        map.put("N.thioamide", 2);
        map.put("N.minus.planar3", 2);
        map.put("N.amide", 2);
        map.put("N.sp3", 2);
        map.put("S.2", 2);
        map.put("S.3", 2);
        map.put("P.ine", 2);
        map.put("O.sp3", 2);
        map.put("S.planar3", 2);
        map.put("C.minus.planar", 2);
        map.put("O.planar3", 2);
        map.put("N.sp2.3", 1);
        map.put("C.sp2", 1);
        return Collections.unmodifiableMap(map);
    }

    /** Predefined electron contribution for several atom types. */
    private final static Map<String, Integer> TYPES = typeToElectronContribMap();

    /** Allow exocyclic pi bonds. */
    private final boolean                     exocyclic;

    /**
     * Create the electron donation model specifying whether exocyclic pi bonds
     * are allowed. Exocyclic pi bonds <i>sprout</i> from a ring, allowing these
     * bonds to contribute means structure such as hexamethylidenecyclohexane,
     * {@code C=C1C(=C)C(=C)C(=C)C(=C)C1=C} are considered <i>aromatic</i>.
     *
     * @param exocyclic allow exocyclic double bonds
     */
    AtomTypeModel(boolean exocyclic) {
        this.exocyclic = exocyclic;
    }

    /**{@inheritDoc} */
    @Override
    int[] contribution(IAtomContainer container) {

        final int nAtoms = container.getAtomCount();
        final int[] electrons = new int[nAtoms];

        Arrays.fill(electrons, -1);

        final Map<IAtom, Integer> indexMap = new HashMap<>(2*nAtoms);

        for (int i = 0; i < nAtoms; i++) {

            IAtom atom = container.getAtom(i);
            indexMap.put(atom, i);

            // acyclic atom skipped
            if (!atom.isInRing()) continue;

            Hybridization hyb = atom.getHybridization();

            Objects.requireNonNull(atom.getAtomTypeName(), "atom has unset atom type");

            // atom has been assigned an atom type but we don't know the hybrid state,
            // typically for atom type 'X' (unknown)
            if (hyb == null) continue;

            switch (hyb) {
                case SP2:
                case PLANAR3:
                    electrons[i] = electronsForAtomType(atom);
                    break;
                case SP3:
                    electrons[i] = electronsForAtomType(atom);
                    if (electrons[i] == 0)
                        electrons[i] = -1;
                    break;
            }
        }

        // exocyclic double bonds are allowed no further processing
        if (exocyclic) return electrons;

        // check for exocyclic double/triple bonds and disallow their contribution
        for (IBond bond : container.bonds()) {
            if (bond.getOrder() == IBond.Order.DOUBLE || bond.getOrder() == IBond.Order.TRIPLE) {

                IAtom a1 = bond.getBegin();
                IAtom a2 = bond.getEnd();

                String a1Type = a1.getAtomTypeName();
                String a2Type = a2.getAtomTypeName();

                int u = indexMap.get(a1);
                int v = indexMap.get(a2);

                if (!bond.isInRing()) {

                    // XXX: single exception - we could make this more general but
                    // for now this mirrors the existing behavior
                    if (a1Type.equals("N.sp2.3") && a2Type.equals("O.sp2") || a1Type.equals("O.sp2")
                            && a2Type.equals("N.sp2.3")) continue;

                    electrons[u] = electrons[v] = -1;
                }
            }
        }

        return electrons;
    }

    /**
     * The number of contributed electrons for the atom type of the specified
     * atom type.
     *
     * @param atom an atom to get the contribution of
     * @return the number of electrons
     */
    private static int electronsForAtomType(IAtom atom) {
        Integer electrons = TYPES.get(atom.getAtomTypeName());

        if (electrons != null) return electrons;

        try {
            IAtomType atomType = AtomTypeFactory.getInstance("org/openscience/cdk/dict/data/cdk-atom-types.owl",
                    atom.getBuilder()).getAtomType(atom.getAtomTypeName());
            electrons = atomType.getProperty(CDKConstants.PI_BOND_COUNT);
            return electrons != null ? electrons : 0;
        } catch (NoSuchAtomTypeException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Access to the number of lone-pairs (specified as a property of the
     * atom).
     *
     * @param atom the atom to get the lone pairs from
     * @return number of lone pairs
     */
    private static int lonePairCount(IAtom atom) {
        // XXX: LONE_PAIR_COUNT is not currently set!
        Integer count = atom.getProperty(CDKConstants.LONE_PAIR_COUNT);
        return count != null ? count : -1;
    }
}
