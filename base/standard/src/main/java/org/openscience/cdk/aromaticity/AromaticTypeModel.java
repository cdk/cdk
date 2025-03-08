/*
 * Copyright (C) 2024 John Mayfield
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */

package org.openscience.cdk.aromaticity;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.openscience.cdk.aromaticity.AromaticType.*;

/**
 * A flexible and configurable aromaticity model based on aromatic atom types.
 * <br/>
 * The API for this class is currently only usable internally (package-private).
 *
 * @author John Mayfield
 */
final class AromaticTypeModel extends ElectronDonation {

    static final List<Map.Entry<AromaticType, Integer>> MDL
            = Arrays.asList(entry(C2_MINUS, 1),
                            entry(C2_PLUS, 1),
                            entry(C3, 1),
                            entry(N2, 1),
                            entry(N3_OXIDE, 1),
                            entry(N3_PLUS, 1));

    static final List<Map.Entry<AromaticType, Integer>> CDK_1x
            = Arrays.asList(entry(C2_MINUS, 1),
                            entry(C2_PLUS, 1),
                            entry(C3, 1),
                            entry(C3_MINUS, 2),
                            entry(C3_PLUS, 0),
                            entry(N2, 1),
                            entry(N2_MINUS, 2),
                            entry(N3, 2),
                            entry(N3_OXIDE, 1),
                            entry(N3_OXIDE_PLUS, 1),
                            entry(N3_PLUS, 1),
                            entry(O2, 2),
                            entry(O2_PLUS, 1),
                            entry(P2, 1),
                            entry(P2_MINUS, 2),
                            entry(P3, 2),
                            entry(P4, 1),
                            entry(P3_OXIDE, 1),
                            entry(P3_OXIDE_PLUS, 1),
                            entry(P3_PLUS, 1),
                            entry(S2, 2),
                            entry(S2_CUML, 2),
                            entry(S3, 1),
                            entry(S3_PLUS, 0),
                            entry(S2_PLUS, 1),
                            entry(S3_OXIDE_PLUS, 0), // ?
                            entry(Se3, 1),
                            entry(As3, 2));

    static final List<Map.Entry<AromaticType, Integer>> DAYLIGHT
            = Arrays.asList(entry(C2_MINUS, 1),
                            entry(C2_PLUS, 1),
                            entry(C3, 1),
                            entry(C3_EXO, 1),
                            entry(C3_ENEG_EXO, 0),
                            entry(C3_MINUS, 2),
                            entry(C3_PLUS, 0),
                            entry(N2, 1),
                            entry(N2_MINUS, 2),
                            entry(N3, 2),
                            entry(N3_OXIDE, 1),
                            entry(N3_OXIDE_PLUS, 1),
                            entry(N3_PLUS, 1),
                            entry(O2, 2),
                            entry(O2_PLUS, 1),
                            entry(P2, 1),
                            entry(P2_MINUS, 2),
                            entry(P3, 2),
                            entry(P3_OXIDE, 1),
                            entry(P3_OXIDE_PLUS, 1),
                            entry(P3_PLUS, 1),
                            entry(S2, 2),
                            entry(S2_PLUS, 1),
                            entry(S3_OXIDE, 2),
                            entry(Se2, 2),
                            entry(Se2_PLUS, 1),
                            entry(Se3_OXIDE, 2),
                            entry(As3, 2));

    @SuppressWarnings("unchecked")
    static final List<Map.Entry<AromaticType, Integer>> OPEN_SMILES
            = extend(DAYLIGHT,
                     entry(B2, 1),
                     entry(B3, 0),
                     entry(S3_OXIDE_PLUS, 2), // Sp3 but makes sense if you allow S2_OXIDE
                     entry(Se3_OXIDE_PLUS, 2), // Sp3 but makes sense if you allow Se2_OXIDE
                     entry(As2, 1),
                     entry(As3_PLUS, 1));

    @SuppressWarnings("unchecked")
    static final List<Map.Entry<AromaticType, Integer>> CDK_2x
            = extend(OPEN_SMILES,
                     entry(Te2, 2),
                     entry(Te2_PLUS, 1));

    private final static AromaticType[] types = AromaticType.values();
    private final int[] map;

    AromaticTypeModel(List<Map.Entry<AromaticType, Integer>> vals) {
        this.map = new int[types.length];
        Arrays.fill(this.map, -1);
        for (Map.Entry<AromaticType, Integer> e : vals)
            this.map[e.getKey().ordinal()] = e.getValue();
    }

    @Override
    int[] contribution(IAtomContainer mol) {
        int[] contrib = new int[mol.getAtomCount()];
        for (IAtom atom : mol.atoms()) {
            AromaticType type = AromaticTypeMatcher.getType(atom);
            contrib[atom.getIndex()] = map[type.ordinal()];
        }
        return contrib;
    }

    // Note: Map.entry and Map.ofEntries in Java 9+
    private static <K, V> Map.Entry<K, V> entry(K k, V v) {
        return new AbstractMap.SimpleImmutableEntry<>(k, v);
    }

    @SuppressWarnings("unchecked")
    private static List<Map.Entry<AromaticType, Integer>>
        extend(List<Map.Entry<AromaticType, Integer>> vals,
               Map.Entry<AromaticType, Integer> ... extra) {
        List<Map.Entry<AromaticType, Integer>> ret = new ArrayList<>(vals);
        Collections.addAll(ret, extra);
        return ret;
    }
}
