/* Copyright (C) 2009-2010  Syed Asad Rahman <asad@ebi.ac.uk>
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.smsd.algorithm.vflib;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.Expr;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.IQueryBond;
import org.openscience.cdk.isomorphism.matchers.QueryAtom;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryBond;
import org.openscience.cdk.silent.Atom;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.smsd.tools.MolHandler;
import org.openscience.cdk.smsd.algorithm.mcsplus.MCSPlusHandler;
import org.openscience.cdk.smsd.interfaces.AbstractMCSAlgorithm;
import org.openscience.cdk.smsd.interfaces.IMCSBase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VFlibMCSRegressionTest {

    @Test
    void handlerResultsAreReadonlyAndRemainStableAcrossReuse() throws Exception {
        for (AbstractMCSAlgorithm algorithm : new AbstractMCSAlgorithm[]{new VFlibMCSHandler(), new MCSPlusHandler()}) {
            IMCSBase handler = (IMCSBase) algorithm;
            handler.set(new MolHandler(molecule("CC"), false, false),
                    new MolHandler(molecule("CCC"), false, false));
            algorithm.searchMCS(true);
            List<Map<Integer, Integer>> indices = handler.getAllMapping();
            List<Map<IAtom, IAtom>> atoms = handler.getAllAtomMapping();
            Map<Integer, Integer> first = handler.getFirstMapping();
            int count = indices.size();
            assertThrows(UnsupportedOperationException.class, () -> first.put(-1, -1));
            assertThrows(UnsupportedOperationException.class, () -> indices.get(0).clear());
            assertThrows(UnsupportedOperationException.class, () -> atoms.get(0).clear());
            handler.set(new MolHandler(molecule("N"), false, false),
                    new MolHandler(molecule("O"), false, false));
            algorithm.searchMCS(true);
            assertTrue(handler.getFirstMapping().isEmpty());
            assertEquals(count, indices.size());
            assertEquals(count, atoms.size());
            assertEquals(2, first.size());
            assertEquals(2, indices.get(0).size());
        }
    }

    @Test
    void handlersRejectUninitializedSearchAndAtomicNullReplacement() throws Exception {
        for (AbstractMCSAlgorithm algorithm : new AbstractMCSAlgorithm[]{new VFlibMCSHandler(), new MCSPlusHandler()}) {
            IMCSBase handler = (IMCSBase) algorithm;
            assertThrows(IllegalStateException.class, () -> algorithm.searchMCS(true));
            MolHandler source = new MolHandler(molecule("CC"), false, false);
            MolHandler target = new MolHandler(molecule("CCC"), false, false);
            handler.set(source, target);
            assertThrows(NullPointerException.class, () -> handler.set(source, (MolHandler) null));
            algorithm.searchMCS(true);
            assertEquals(2, handler.getFirstMapping().size());
        }
    }

    @Test
    void ordinaryTopologyMatchingDoesNotIntroduceUnrequestedChemicalConstraints() throws Exception {
        // Charge, isotope, radicals and mapping-level stereo require explicit options/predicates.
        assertPair("[13CH3]", "C", 1, 0, true);
        assertPair("[Na+]", "[Na]", 1, 0, true);
        assertPair("[CH3]", "C", 1, 0, true);
        assertPair("F[C@](Cl)(Br)I", "F[C@@](Cl)(Br)I", 5, 4, true);
    }

    @Test
    void wrappedPredicatesRemainOnTheSourceSideWhenItIsLarger() throws Exception {
        IQueryAtomContainer source = new QueryAtomContainer(DefaultChemObjectBuilder.getInstance());
        source.addAtom(new org.openscience.cdk.AtomRef(new QueryAtom(new Expr(Expr.Type.ELEMENT, 8))));
        source.addAtom(new org.openscience.cdk.AtomRef(new QueryAtom(new Expr(Expr.Type.ELEMENT, 6))));
        source.addBond(new QueryBond(source.getAtom(0), source.getAtom(1), new Expr(Expr.Type.TRUE)));
        for (AbstractMCSAlgorithm algorithm : new AbstractMCSAlgorithm[]{new VFlibMCSHandler(), new MCSPlusHandler()}) {
            IMCSBase handler = (IMCSBase) algorithm;
            handler.set(source, molecule("O"));
            algorithm.searchMCS(false);
            assertEquals(java.util.Collections.singletonMap(0, 0), handler.getFirstMapping());
        }
    }

    @Test
    void chainAndBranchedChainHaveTheSameMaximumInBothDirections() throws Exception {
        for (boolean matchBonds : new boolean[]{false, true}) {
            assertPair("CCCCCCCCN", "CCCC(C)CCCC", 8, 7, matchBonds);
            assertPair("CCCC(C)CCCC", "CCCCCCCCN", 8, 7, matchBonds);
        }
    }

    @Test
    void unequalRingsCanShareAPathWithARingBondOmitted() throws Exception {
        assertPair("C1CCCCC1", "C1CCCC1", 5, 4, true);
        assertPair("C1CCCC1", "C1CCCCC1", 5, 4, true);
    }

    @Test
    void equalAtomMaximaPreferMoreCompatibleCommonBonds() throws Exception {
        assertPair("CCC.C1CC1", "C1CC1.CCC", 3, 3, true);
        assertPair("C1CC1.CCC", "CCC.C1CC1", 3, 3, true);
    }

    @Test
    void disconnectedInputsReturnConnectedMaximumComponents() throws Exception {
        assertPair("CC.O", "CC.O", 2, 1, true);
        assertPair("CC.NNN", "CC.NN", 2, 1, true);
        assertPair("CC.NN", "CC.NNN", 2, 1, true);
    }

    @Test
    void sourceIndicesAndAtomsRemainConsistentWhenTheSearchReverses() throws Exception {
        assertPair("CCO", "OCCN", 3, 2, true);
        assertPair("OCCN", "CCO", 3, 2, true);
    }

    @Test
    void explicitQueryPredicatesRemainDirectional() throws Exception {
        IQueryAtomContainer query = new QueryAtomContainer(DefaultChemObjectBuilder.getInstance());
        query.addAtom(new QueryAtom(new Expr(Expr.Type.ELEMENT, 8)));
        query.addAtom(new QueryAtom(new Expr(Expr.Type.ELEMENT, 6)));
        query.addAtom(new QueryAtom(new Expr(Expr.Type.ELEMENT, 6)));
        query.addBond(new QueryBond(query.getAtom(0), query.getAtom(1), new Expr(Expr.Type.TRUE)));
        query.addBond(new QueryBond(query.getAtom(1), query.getAtom(2), new Expr(Expr.Type.TRUE)));
        for (boolean matchBonds : new boolean[]{false, true}) {
            IAtomContainer target = molecule("OC");
            VFlibMCSHandler handler = new VFlibMCSHandler();
            handler.set(query, target);
            handler.searchMCS(matchBonds);
            assertMappings(handler, query, target, 2, 1, matchBonds);
        }
    }

    @Test
    void plainContainersRetainAtomPredicatesWhenLargerThanTheTarget() throws Exception {
        IAtomContainer query = molecule("");
        query.addAtom(new QueryAtom(new Expr(Expr.Type.ELEMENT, 8)));
        query.addAtom(new Atom("C"));
        query.addAtom(new Atom("C"));
        query.addBond(0, 1, IBond.Order.SINGLE);
        query.addBond(1, 2, IBond.Order.SINGLE);
        for (boolean matchBonds : new boolean[]{false, true}) {
            assertPair(query, molecule("OC"), 2, 1, matchBonds);
        }
    }

    @Test
    void bondPredicatesRemainAuthoritativeWhenOrdinaryOrdersAreIgnored() throws Exception {
        IAtomContainer query = molecule("CCO");
        query.removeBond(0);
        query.addBond(new QueryBond(query.getAtom(0), query.getAtom(1),
                new Expr(Expr.Type.ALIPHATIC_ORDER, 2)));
        assertPair(query, molecule("CC"), 1, 0, false);
        assertPair(query, molecule("C=C"), 2, 1, false);
    }

    @Test
    void emptyInputsAndReuseClearAllMappingViews() throws Exception {
        VFlibMCSHandler handler = new VFlibMCSHandler();
        for (String[] pair : new String[][]{{"CC", "CCC"}, {"", "CC"},
                {"CC", ""}, {"", ""}, {"N", "NN"}, {"N", "C"}}) {
            MolHandler source = new MolHandler(molecule(pair[0]), false, false);
            MolHandler target = new MolHandler(molecule(pair[1]), false, false);
            handler.set(source, target);
            handler.searchMCS(true);
            int atoms = pair[0].equals("CC") && pair[1].equals("CCC") ? 2
                    : pair[0].equals("N") && pair[1].equals("NN") ? 1 : 0;
            assertMappings(handler, source.getMolecule(), target.getMolecule(), atoms,
                    atoms == 2 ? 1 : 0, true);
        }
        IQueryAtomContainer query = new QueryAtomContainer(DefaultChemObjectBuilder.getInstance());
        query.addAtom(new QueryAtom(new Expr(Expr.Type.ELEMENT, 8)));
        handler.set(query, molecule("O"));
        handler.searchMCS(true);
        assertEquals(1, handler.getFirstMapping().size());
        handler.set(new MolHandler(molecule("N"), false, false),
                new MolHandler(molecule("NN"), false, false));
        handler.searchMCS(false);
        assertEquals(1, handler.getFirstMapping().size());
        assertFalse(handler.isBondMatchFlag());
    }

    private static void assertPair(String source, String target, int atoms, int bonds,
                                   boolean matchBonds) throws Exception {
        assertPair(molecule(source), molecule(target), atoms, bonds, matchBonds);
    }

    private static void assertPair(IAtomContainer source, IAtomContainer target, int atoms,
                                   int bonds, boolean matchBonds) {
        MolHandler sourceHandler = new MolHandler(source, false, false);
        MolHandler targetHandler = new MolHandler(target, false, false);
        VFlibMCSHandler handler = new VFlibMCSHandler();
        handler.set(sourceHandler, targetHandler);
        handler.searchMCS(matchBonds);
        assertMappings(handler, sourceHandler.getMolecule(), targetHandler.getMolecule(),
                atoms, bonds, matchBonds);
    }

    private static void assertMappings(VFlibMCSHandler handler, IAtomContainer source,
                                       IAtomContainer target, int atoms, int bonds, boolean matchBonds) {
        assertEquals(atoms, handler.getFirstMapping().size());
        assertEquals(atoms, handler.getFirstAtomMapping().size());
        List<Map<Integer, Integer>> mappings = handler.getAllMapping();
        assertEquals(mappings.size(), handler.getAllAtomMapping().size());
        assertEquals(atoms == 0, mappings.isEmpty());
        for (int i = 0; i < mappings.size(); i++) {
            Map<Integer, Integer> mapping = mappings.get(i);
            Map<IAtom, IAtom> atomMapping = handler.getAllAtomMapping().get(i);
            assertEquals(atoms, mapping.size());
            assertEquals(atoms, new HashSet<>(mapping.values()).size());
            for (Map.Entry<Integer, Integer> entry : mapping.entrySet()) {
                IAtom queryAtom = source.getAtom(entry.getKey());
                IAtom targetAtom = target.getAtom(entry.getValue());
                assertEquals(targetAtom, atomMapping.get(queryAtom));
                assertTrue(queryAtom instanceof IQueryAtom ? ((IQueryAtom) queryAtom).matches(targetAtom)
                        : org.openscience.cdk.isomorphism.AtomMatcher.forElement().matches(queryAtom, targetAtom));
            }
            int commonBonds = 0;
            for (IBond queryBond : source.bonds()) {
                if (compatibleBond(source, target, mapping, queryBond, matchBonds)) commonBonds++;
            }
            assertEquals(bonds, commonBonds);
            assertConnected(source, target, mapping, matchBonds);
        }
    }

    private static boolean compatibleBond(IAtomContainer source, IAtomContainer target,
                                           Map<Integer, Integer> mapping, IBond queryBond, boolean matchBonds) {
        Integer begin = mapping.get(source.indexOf(queryBond.getBegin()));
        Integer end = mapping.get(source.indexOf(queryBond.getEnd()));
        if (begin == null || end == null) return false;
        IBond targetBond = target.getBond(target.getAtom(begin), target.getAtom(end));
        return targetBond != null && (queryBond instanceof IQueryBond
                ? ((IQueryBond) queryBond).matches(targetBond)
                : !matchBonds || org.openscience.cdk.isomorphism.BondMatcher.forStrictOrder()
                        .matches(queryBond, targetBond));
    }

    private static void assertConnected(IAtomContainer source, IAtomContainer target,
                                         Map<Integer, Integer> mapping, boolean matchBonds) {
        Set<Integer> visited = new HashSet<>();
        ArrayDeque<Integer> frontier = new ArrayDeque<>();
        Integer root = mapping.keySet().iterator().next();
        visited.add(root);
        frontier.add(root);
        while (!frontier.isEmpty()) {
            int current = frontier.remove();
            for (IBond bond : source.getConnectedBondsList(source.getAtom(current))) {
                if (!compatibleBond(source, target, mapping, bond, matchBonds)) continue;
                int adjacent = source.indexOf(bond.getOther(source.getAtom(current)));
                if (visited.add(adjacent)) frontier.add(adjacent);
            }
        }
        assertEquals(mapping.size(), visited.size());
    }

    private static IAtomContainer molecule(String smiles) throws Exception {
        return new SmilesParser(DefaultChemObjectBuilder.getInstance()).parseSmiles(smiles);
    }
}
