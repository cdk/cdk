/* Copyright (C) 2009-2010 Syed Asad Rahman <asad@ebi.ac.uk>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your container code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.smsd;

import java.util.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.isomorphism.*;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.*;
import org.openscience.cdk.smsd.algorithm.vflib.map.VFMapper;
import org.openscience.cdk.smsd.algorithm.vflib.query.QueryCompiler;

class VfDifferentialTest {
    @Test
    void enumeratesTheSameMappingsAsTheSupportedVfImplementation() throws Exception {
        SmilesParser parser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        String[][] pairs = {{"CCC", "CCCCC"}, {"CC(C)C", "CC(C)(C)C"},
                {"c1ccccc1", "Cc1ccccc1"}, {"C1CC1", "C1CCC1"},
                {"C.N", "CCN"}, {"NC(=O)C", "CC(=O)NC"}};
        for (String[] pair : pairs) {
            IAtomContainer query = parser.parseSmiles(pair[0]);
            IAtomContainer target = parser.parseSmiles(pair[1]);
            IQuery compiled = new QueryCompiler(query, false).compile();
            Set<String> expected = new HashSet<>();
            for (int[] mapping : VentoFoggia.findSubstructure(query,
                    org.openscience.cdk.isomorphism.AtomMatcher.forElement(),
                    org.openscience.cdk.isomorphism.BondMatcher.forAny()).matchAll(target)) {
                expected.add(Arrays.toString(mapping));
            }
            Set<String> actual = new HashSet<>();
            for (Map<INode, IAtom> mapping : new VFMapper(compiled).getMaps(target)) {
                int[] indices = new int[query.getAtomCount()];
                for (Map.Entry<INode, IAtom> entry : mapping.entrySet()) {
                    indices[query.indexOf(compiled.getAtom(entry.getKey()))] = target.indexOf(entry.getValue());
                }
                actual.add(Arrays.toString(indices));
            }
            Assertions.assertEquals(expected, actual, pair[0] + " in " + pair[1]);
        }
    }
    @Test
    void partialMcsAgreesWithExhaustiveConnectedSubsets() throws Exception {
        Random random = new Random(734021L);
        for (int sample = 0; sample < 30; sample++) {
            IAtomContainer query = randomTree(random, 2 + random.nextInt(4));
            IAtomContainer target = randomTree(random, 2 + random.nextInt(4));
            int expected = 0;
            for (int mask = 1; mask < (1 << query.getAtomCount()); mask++) {
                if (Integer.bitCount(mask) <= expected) continue;
                IAtomContainer subset = DefaultChemObjectBuilder.getInstance().newAtomContainer();
                int[] indices = new int[query.getAtomCount()];
                Arrays.fill(indices, -1);
                for (int i = 0; i < indices.length; i++) {
                    if ((mask & (1 << i)) != 0) {
                        indices[i] = subset.getAtomCount();
                        subset.addAtom(new org.openscience.cdk.Atom(query.getAtom(i).getSymbol()));
                    }
                }
                for (IBond bond : query.bonds()) {
                    int first = indices[query.indexOf(bond.getBegin())];
                    int second = indices[query.indexOf(bond.getEnd())];
                    if (first >= 0 && second >= 0) subset.addBond(first, second, IBond.Order.SINGLE);
                }
                if (org.openscience.cdk.graph.ConnectivityChecker.isConnected(subset)
                        && VentoFoggia.findSubstructure(subset,
                        org.openscience.cdk.isomorphism.AtomMatcher.forElement(),
                        org.openscience.cdk.isomorphism.BondMatcher.forAny()).matches(target)) {
                    expected = subset.getAtomCount();
                }
            }
            java.util.List<Map<INode, IAtom>> actual =
                    new org.openscience.cdk.smsd.algorithm.vflib.map.VFMCSMapper(query, false).getMaps(target);
            int maximum = actual.stream().mapToInt(Map::size).max().orElse(0);
            Assertions.assertEquals(expected, maximum, "sample " + sample);
            Assertions.assertTrue(actual.stream().allMatch(map -> map.size() == maximum));
        }
    }

    private IAtomContainer randomTree(Random random, int size) {
        IAtomContainer graph = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        String[] elements = {"C", "C", "N", "O"};
        for (int i = 0; i < size; i++) {
            graph.addAtom(new org.openscience.cdk.Atom(elements[random.nextInt(elements.length)]));
            if (i > 0) graph.addBond(random.nextInt(i), i, IBond.Order.SINGLE);
        }
        return graph;
    }

}
