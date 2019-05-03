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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.exception.NoSuchAtomTypeException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.ringsearch.RingSearch;

import java.util.Arrays;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
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

    /** Predefined electron contribution for several atom types. */
    private final static Map<String, Integer> TYPES = ImmutableMap.<String, Integer> builder().put("N.planar3", 2)
                                                            .put("N.minus.planar3", 2).put("N.amide", 2).put("S.2", 2)
                                                            .put("S.planar3", 2).put("C.minus.planar", 2)
                                                            .put("O.planar3", 2).put("N.sp2.3", 1).put("C.sp2", 1)
                                                            .build();

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
    int[] contribution(IAtomContainer container, RingSearch ringSearch) {

        final int nAtoms = container.getAtomCount();
        final int[] electrons = new int[nAtoms];

        Arrays.fill(electrons, -1);

        final Map<IAtom, Integer> indexMap = Maps.newHashMapWithExpectedSize(nAtoms);

        for (int i = 0; i < nAtoms; i++) {

            IAtom atom = container.getAtom(i);
            indexMap.put(atom, i);

            // acyclic atom skipped
            if (!ringSearch.cyclic(i)) continue;

            Hybridization hyb = atom.getHybridization();

            checkNotNull(atom.getAtomTypeName(), "atom has unset atom type");

            // atom has been assigned an atom type but we don't know the hybrid state,
            // typically for atom type 'X' (unknown)
            if (hyb == null) continue;

            switch (hyb) {
                case SP2:
                case PLANAR3:
                    electrons[i] = electronsForAtomType(atom);
                    break;
                case SP3:
                    electrons[i] = lonePairCount(atom) > 0 ? 2 : -1;
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

                if (!ringSearch.cyclic(u, v)) {

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
