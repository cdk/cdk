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
package org.openscience.cdk.smsd;

import org.junit.jupiter.api.Test;
import org.openscience.cdk.AtomRef;
import org.openscience.cdk.BondRef;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.Expr;
import org.openscience.cdk.isomorphism.matchers.QueryAtom;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryBond;
import org.openscience.cdk.silent.Atom;
import org.openscience.cdk.silent.Bond;
import org.openscience.cdk.silent.PseudoAtom;
import org.openscience.cdk.smsd.algorithm.matchers.DefaultBondMatcher;
import org.openscience.cdk.smsd.algorithm.matchers.DefaultMCSPlusAtomMatcher;
import org.openscience.cdk.smsd.algorithm.matchers.DefaultVFAtomMatcher;
import org.openscience.cdk.smsd.algorithm.matchers.DefaultVFBondMatcher;
import org.openscience.cdk.smsd.algorithm.vflib.builder.TargetProperties;
import org.openscience.cdk.smsd.algorithm.vflib.map.VFMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MatcherAlignmentTest {

    @Test
    void rejectsDifferentOrdersWithUnsetValency() {
        IAtomContainer single = edge(IBond.Order.SINGLE, false);
        IAtomContainer doubled = edge(IBond.Order.DOUBLE, false);
        assertBondMatches(false, single, doubled, true);
        assertBondMatches(false, doubled, single, true);
        assertFalse(new VFMapper(single, true).hasMap(doubled));
        assertFalse(new VFMapper(doubled, true).hasMap(single));
    }

    @Test
    void ordinaryBondMatchingDoesNotDependOnHydrogensOrValency() {
        IAtomContainer single = edge(IBond.Order.SINGLE, false);
        IAtomContainer doubled = edge(IBond.Order.DOUBLE, false);
        for (IAtom atom : single.atoms()) {
            atom.setValency(4);
            atom.setImplicitHydrogenCount(1);
        }
        for (IAtom atom : doubled.atoms()) {
            atom.setValency(4);
            atom.setImplicitHydrogenCount(0);
        }
        assertBondMatches(false, single, doubled, true);
        assertBondMatches(false, doubled, single, true);
    }

    @Test
    void ignoresOrdinaryBondOrdersWhenDisabled() {
        IAtomContainer single = edge(IBond.Order.SINGLE, false);
        IAtomContainer doubled = edge(IBond.Order.DOUBLE, false);
        assertBondMatches(true, single, doubled, false);
        assertBondMatches(true, doubled, single, false);
    }

    @Test
    void aromaticBondsMatchWithoutAssignedOrders() {
        IAtomContainer unset = edge(null, true);
        IAtomContainer doubled = edge(IBond.Order.DOUBLE, true);
        assertBondMatches(true, unset, unset, true);
        assertBondMatches(true, unset, doubled, true);
        assertBondMatches(true, doubled, unset, true);
        assertTrue(new VFMapper(unset, true).hasMap(doubled));
    }

    @Test
    void aromaticAndAliphaticBondsRemainDistinct() {
        IAtomContainer ordinary = edge(IBond.Order.SINGLE, false);
        IAtomContainer aromatic = edge(IBond.Order.SINGLE, true);
        assertBondMatches(false, ordinary, aromatic, true);
        assertBondMatches(false, aromatic, ordinary, true);
    }

    @Test
    void bondCompatibilityAgreesWithStrictOrderInBothDirections() {
        IBond.Order[] orders = {null, IBond.Order.SINGLE, IBond.Order.DOUBLE,
                IBond.Order.TRIPLE, IBond.Order.UNSET};
        org.openscience.cdk.isomorphism.BondMatcher supported =
                org.openscience.cdk.isomorphism.BondMatcher.forStrictOrder();
        for (IBond.Order first : orders) {
            for (IBond.Order second : orders) {
                for (boolean aromaticFirst : new boolean[]{false, true}) {
                    for (boolean aromaticSecond : new boolean[]{false, true}) {
                        IAtomContainer query = edge(first, aromaticFirst);
                        IAtomContainer target = edge(second, aromaticSecond);
                        boolean expected = supported.matches(query.getBond(0), target.getBond(0));
                        assertBondMatches(expected, query, target, true);
                        assertBondMatches(expected, target, query, true);
                    }
                }
            }
        }
    }

    @Test
    void ordinaryAtomsMatchElementsWithoutExtraChemistryConstraints() {
        IAtomContainer query = molecule();
        IAtom carbon = new Atom("C");
        carbon.setMassNumber(13);
        carbon.setFormalCharge(1);
        carbon.setIsAromatic(true);
        query.addAtom(carbon);
        IAtomContainer target = molecule();
        target.addAtom(new Atom("C"));
        assertAtomMatches(true, query, query.getAtom(0), target, target.getAtom(0), true);
        assertAtomMatches(true, target, target.getAtom(0), query, query.getAtom(0), true);
    }

    @Test
    void pseudoatomsAndUnsetElementsFollowTheElementMatcherContract() {
        IAtomContainer query = molecule();
        query.addAtom(new PseudoAtom("R"));
        IAtomContainer target = molecule();
        target.addAtom(new PseudoAtom("X"));
        target.addAtom(new Atom("C"));
        assertAtomMatches(true, query, query.getAtom(0), target, target.getAtom(0), true);
        assertAtomMatches(false, query, query.getAtom(0), target, target.getAtom(1), true);
        IAtom unset = new Atom("C");
        unset.setAtomicNumber(null);
        assertThrows(NullPointerException.class,
                () -> new DefaultMCSPlusAtomMatcher(query, unset, true).matches(target, target.getAtom(1)));
        assertThrows(NullPointerException.class,
                () -> new DefaultVFAtomMatcher(query, unset, true).matches(new TargetProperties(target), target.getAtom(1)));
    }

    @Test
    void explicitSymbolOverridesRemainAvailable() {
        IAtomContainer query = molecule();
        query.addAtom(new Atom("C"));
        IAtomContainer target = molecule();
        target.addAtom(new Atom("N"));
        target.addAtom(new Atom("C"));
        DefaultMCSPlusAtomMatcher ordinary = new DefaultMCSPlusAtomMatcher(query, query.getAtom(0), true);
        DefaultVFAtomMatcher vf = new DefaultVFAtomMatcher(query, query.getAtom(0), true);
        ordinary.setSymbol("N");
        vf.setSymbol("N");
        TargetProperties properties = new TargetProperties(target);
        assertTrue(ordinary.matches(target, target.getAtom(0)));
        assertTrue(vf.matches(properties, target.getAtom(0)));
        assertFalse(ordinary.matches(target, target.getAtom(1)));
        assertFalse(vf.matches(properties, target.getAtom(1)));
        ordinary.setSymbol(null);
        vf.setSymbol(null);
        assertFalse(ordinary.matches(target, target.getAtom(0)));
        assertFalse(vf.matches(properties, target.getAtom(0)));
    }

    @Test
    void publicDegreeLimitsRetainTheirRespectiveBounds() {
        IAtomContainer target = edge(IBond.Order.SINGLE, false);
        DefaultMCSPlusAtomMatcher ordinary = new DefaultMCSPlusAtomMatcher();
        ordinary.setSymbol("C");
        ordinary.setMaximumNeighbors(2);
        ordinary.setBondMatchFlag(true);
        assertFalse(ordinary.matches(target, target.getAtom(0)));
        ordinary.setMaximumNeighbors(1);
        assertTrue(ordinary.matches(target, target.getAtom(0)));
        DefaultVFAtomMatcher vf = new DefaultVFAtomMatcher();
        vf.setSymbol("C");
        vf.setMaximumNeighbors(0);
        vf.setBondMatchFlag(true);
        TargetProperties properties = new TargetProperties(target);
        assertFalse(vf.matches(properties, target.getAtom(0)));
        vf.setMaximumNeighbors(1);
        assertTrue(vf.matches(properties, target.getAtom(0)));
    }

    @Test
    void queryAtomPredicatesOverrideHeuristicSymbolsAndDegrees() {
        IAtomContainer query = molecule();
        query.addAtom(new QueryAtom(new Expr(Expr.Type.ELEMENT, 8)));
        IAtomContainer target = molecule();
        target.addAtom(new Atom("O"));
        target.addAtom(new Atom("C"));
        for (boolean flag : new boolean[]{false, true}) {
            DefaultMCSPlusAtomMatcher ordinary = new DefaultMCSPlusAtomMatcher(query, query.getAtom(0), flag);
            DefaultVFAtomMatcher vf = new DefaultVFAtomMatcher(query, query.getAtom(0), flag);
            ordinary.setSymbol("C");
            vf.setSymbol("C");
            ordinary.setMaximumNeighbors(999);
            vf.setMaximumNeighbors(-2);
            TargetProperties properties = new TargetProperties(target);
            assertTrue(ordinary.matches(target, target.getAtom(0)));
            assertTrue(vf.matches(properties, target.getAtom(0)));
            assertFalse(ordinary.matches(target, target.getAtom(1)));
            assertFalse(vf.matches(properties, target.getAtom(1)));
        }
    }

    @Test
    void explicitAtomPredicatesCanConstrainChargeAndIsotope() {
        IAtomContainer query = molecule();
        query.addAtom(new QueryAtom(new Expr(Expr.Type.ELEMENT, 6)
                .and(new Expr(Expr.Type.FORMAL_CHARGE, 1))
                .and(new Expr(Expr.Type.ISOTOPE, 13))));
        IAtomContainer target = molecule();
        IAtom atom = new Atom("C");
        atom.setFormalCharge(1);
        atom.setMassNumber(13);
        target.addAtom(atom);
        assertAtomMatches(true, query, query.getAtom(0), target, target.getAtom(0), false);
        target.getAtom(0).setFormalCharge(0);
        assertAtomMatches(false, query, query.getAtom(0), target, target.getAtom(0), false);
    }

    @Test
    void queryBondPredicatesApplyWhenOrdinaryBondMatchingIsDisabled() {
        IAtomContainer query = edge(IBond.Order.SINGLE, false);
        query.removeBond(0);
        query.addBond(new QueryBond(query.getAtom(0), query.getAtom(1),
                new Expr(Expr.Type.ALIPHATIC_ORDER, 2)));
        IAtomContainer single = edge(IBond.Order.SINGLE, false);
        IAtomContainer doubled = edge(IBond.Order.DOUBLE, false);
        for (boolean flag : new boolean[]{false, true}) {
            assertBondMatches(false, query, single, flag);
            assertBondMatches(true, query, doubled, flag);
        }
    }

    @Test
    void mixedContainersCompileTheirAtomAndBondPredicates() {
        IAtomContainer query = molecule();
        query.addAtom(new QueryAtom(new Expr(Expr.Type.ELEMENT, 8)));
        query.addAtom(new Atom("C"));
        query.addBond(new QueryBond(query.getAtom(0), query.getAtom(1), new Expr(Expr.Type.TRUE)));
        IAtomContainer target = molecule();
        target.addAtom(new Atom("O"));
        target.addAtom(new Atom("C"));
        target.addBond(0, 1, IBond.Order.DOUBLE);
        assertTrue(new VFMapper(query, true).hasMap(target));
        target.getAtom(0).setSymbol("N");
        assertFalse(new VFMapper(query, true).hasMap(target));
    }

    @Test
    void queryContainersRetainPredicatesThroughOrdinaryContainerReferences() {
        IAtomContainer query = new QueryAtomContainer(DefaultChemObjectBuilder.getInstance());
        query.addAtom(new QueryAtom(new Expr(Expr.Type.TRUE)));
        IAtomContainer target = molecule();
        target.addAtom(new Atom("C"));
        assertTrue(new VFMapper(query, true).hasMap(target));
        assertTrue(new VFMapper(query, false).hasMap(target));
    }

    @Test
    void wrappedAtomPredicatesRetainAuthorityAndTargetAdjacency() {
        IAtomContainer query = molecule();
        IAtom wrapped = new AtomRef(new AtomRef(new QueryAtom(new Expr(Expr.Type.ELEMENT, 8)
                .and(new Expr(Expr.Type.DEGREE, 1)))));
        query.addAtom(wrapped);
        IAtomContainer target = molecule();
        target.addAtom(new Atom("O"));
        target.addAtom(new Atom("C"));
        target.addAtom(new Atom("O"));
        target.addBond(0, 1, IBond.Order.SINGLE);
        TargetProperties properties = new TargetProperties(target);
        for (boolean flag : new boolean[]{false, true}) {
            DefaultMCSPlusAtomMatcher ordinary = new DefaultMCSPlusAtomMatcher(query, wrapped, flag);
            DefaultVFAtomMatcher vf = new DefaultVFAtomMatcher(query, wrapped, flag);
            ordinary.setSymbol("C");
            vf.setSymbol("C");
            ordinary.setMaximumNeighbors(999);
            vf.setMaximumNeighbors(-2);
            assertTrue(ordinary.matches(target, target.getAtom(0)));
            assertTrue(vf.matches(properties, target.getAtom(0)));
            assertFalse(ordinary.matches(target, target.getAtom(1)));
            assertFalse(vf.matches(properties, target.getAtom(1)));
            assertFalse(ordinary.matches(target, target.getAtom(2)));
            assertFalse(vf.matches(properties, target.getAtom(2)));
        }
    }

    @Test
    void wrappedBondPredicatesApplyWithEitherOrdinaryMatchingFlag() {
        IAtomContainer query = edge(IBond.Order.SINGLE, false);
        IBond wrapped = new BondRef(new BondRef(new QueryBond(query.getAtom(0), query.getAtom(1),
                new Expr(Expr.Type.ALIPHATIC_ORDER, 2))));
        IAtomContainer single = edge(IBond.Order.SINGLE, false);
        IAtomContainer doubled = edge(IBond.Order.DOUBLE, false);
        for (boolean flag : new boolean[]{false, true}) {
            DefaultBondMatcher ordinary = new DefaultBondMatcher(query, wrapped, flag);
            DefaultVFBondMatcher vf = new DefaultVFBondMatcher(query, wrapped, flag);
            assertFalse(ordinary.matches(single, new BondRef(single.getBond(0))));
            assertFalse(vf.matches(new TargetProperties(single), new BondRef(single.getBond(0))));
            assertTrue(ordinary.matches(doubled, new BondRef(doubled.getBond(0))));
            assertTrue(vf.matches(new TargetProperties(doubled), new BondRef(doubled.getBond(0))));
        }
    }

    private static void assertBondMatches(boolean expected, IAtomContainer query,
                                          IAtomContainer target, boolean matchBonds) {
        assertEquals(expected, new DefaultBondMatcher(query, query.getBond(0), matchBonds)
                .matches(target, target.getBond(0)));
        assertEquals(expected, new DefaultVFBondMatcher(query, query.getBond(0), matchBonds)
                .matches(new TargetProperties(target), target.getBond(0)));
    }

    private static void assertAtomMatches(boolean expected, IAtomContainer query, IAtom queryAtom,
                                          IAtomContainer target, IAtom targetAtom, boolean matchBonds) {
        assertEquals(expected, new DefaultMCSPlusAtomMatcher(query, queryAtom, matchBonds)
                .matches(target, targetAtom));
        assertEquals(expected, new DefaultVFAtomMatcher(query, queryAtom, matchBonds)
                .matches(new TargetProperties(target), targetAtom));
    }

    private static IAtomContainer molecule() {
        return DefaultChemObjectBuilder.getInstance().newAtomContainer();
    }

    private static IAtomContainer edge(IBond.Order order, boolean aromatic) {
        IAtomContainer molecule = molecule();
        molecule.addAtom(new Atom("C"));
        molecule.addAtom(new Atom("C"));
        molecule.addBond(new Bond(molecule.getAtom(0), molecule.getAtom(1), order));
        molecule.getBond(0).setIsAromatic(aromatic);
        return molecule;
    }
}
