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
package org.openscience.cdk.smsd.algorithm.vflib;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.Bond;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.smsd.algorithm.vflib.builder.TargetProperties;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.IMapper;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.INode;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.IQuery;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.IState;
import org.openscience.cdk.smsd.algorithm.vflib.map.Match;
import org.openscience.cdk.smsd.algorithm.vflib.map.VFMapper;
import org.openscience.cdk.smsd.algorithm.vflib.map.VFState;
import org.openscience.cdk.smsd.algorithm.vflib.query.QueryCompiler;
import org.openscience.cdk.smsd.tools.ExtAtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * Unit testing for the {@link VFMapper}, {@link VFState}, {@link Match} class.
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 * @cdk.module test-smsd
 */
public class VFLibTest extends CDKTestCase {

    private static IAtomContainer hexane;
    private static IQuery         hexaneQuery;
    private static IAtomContainer benzene;
    private static IQuery         benzeneQuery;

    @BeforeClass
    public static void setUp() throws CDKException {
        hexane = createHexane();
        Assert.assertEquals(6, hexane.getAtomCount());
        ExtAtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(hexane);
        hexane = ExtAtomContainerManipulator.removeHydrogensExceptSingleAndPreserveAtomID(hexane);
        Aromaticity.cdkLegacy().apply(hexane);
        hexaneQuery = new QueryCompiler(hexane, true).compile();
        Assert.assertEquals(6, hexaneQuery.countNodes());
        benzene = createBenzene();
        ExtAtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(benzene);
        hexane = ExtAtomContainerManipulator.removeHydrogensExceptSingleAndPreserveAtomID(benzene);
        Aromaticity.cdkLegacy().apply(benzene);
        benzeneQuery = new QueryCompiler(benzene, true).compile();
    }

    @Test
    public void testItShouldFindAllMatchCandidatesInTheRootState() {

        IState state = new VFState(benzeneQuery, new TargetProperties(benzene));
        int count = 0;

        while (state.hasNextCandidate()) {
            state.nextCandidate();
            count++;
        }
        Assert.assertEquals(benzene.getAtomCount() * benzene.getAtomCount(), count);
    }

    @Test
    public void testItShoudFindAllMatchCandidatesInThePrimaryState() {
        IState state = new VFState(benzeneQuery, new TargetProperties(benzene));
        Match match = new Match(benzeneQuery.getNode(0), benzene.getAtom(0));
        IState newState = state.nextState(match);
        List<Match> candidates = new ArrayList<Match>();

        while (newState.hasNextCandidate()) {
            candidates.add(newState.nextCandidate());
        }

        Assert.assertEquals(4, candidates.size());
    }

    @Test
    public void testItShouldFindAllMatchCandidatesInTheSecondaryState() {
        IState state0 = new VFState(benzeneQuery, new TargetProperties(benzene));
        Match match0 = new Match(benzeneQuery.getNode(0), benzene.getAtom(0));
        IState state1 = state0.nextState(match0);
        Match match1 = new Match(benzeneQuery.getNode(1), benzene.getAtom(1));
        IState state2 = state1.nextState(match1);
        List<Match> candidates = new ArrayList<Match>();

        while (state2.hasNextCandidate()) {
            candidates.add(state2.nextCandidate());
        }

        Assert.assertEquals(1, candidates.size());
    }

    @Test
    public void testItShouldMapAllAtomsInTheSecondaryState() {
        IState state0 = new VFState(benzeneQuery, new TargetProperties(benzene));
        Match match0 = new Match(benzeneQuery.getNode(0), benzene.getAtom(0));
        IState state1 = state0.nextState(match0);
        Match match1 = new Match(benzeneQuery.getNode(1), benzene.getAtom(1));
        IState state2 = state1.nextState(match1);

        Map<INode, IAtom> map = state2.getMap();

        Assert.assertEquals(2, map.size());
        Assert.assertEquals(benzene.getAtom(0), map.get(benzeneQuery.getNode(0)));
        Assert.assertEquals(benzene.getAtom(1), map.get(benzeneQuery.getNode(1)));
    }

    @Test
    public void testItShouldFindAllMatchCandidatesFromTheTeriaryState() {
        IState state0 = new VFState(benzeneQuery, new TargetProperties(benzene));
        Match match0 = new Match(benzeneQuery.getNode(0), benzene.getAtom(0));
        IState state1 = state0.nextState(match0);
        Match match1 = new Match(benzeneQuery.getNode(1), benzene.getAtom(1));
        IState state2 = state1.nextState(match1);
        Match match2 = new Match(benzeneQuery.getNode(2), benzene.getAtom(2));
        IState state3 = state2.nextState(match2);
        List<Match> candidates = new ArrayList<Match>();

        while (state3.hasNextCandidate()) {
            candidates.add(state3.nextCandidate());
        }

        Assert.assertEquals(1, candidates.size());
    }

    @Test
    public void testItShouldMapAllAtomsInTheTertiaryState() {
        IState state0 = new VFState(benzeneQuery, new TargetProperties(benzene));
        Match match0 = new Match(benzeneQuery.getNode(0), benzene.getAtom(0));
        IState state1 = state0.nextState(match0);
        Match match1 = new Match(benzeneQuery.getNode(1), benzene.getAtom(1));
        IState state2 = state1.nextState(match1);
        Match match2 = new Match(benzeneQuery.getNode(2), benzene.getAtom(2));
        IState state3 = state2.nextState(match2);
        Map<INode, IAtom> map = state3.getMap();

        Assert.assertEquals(3, map.size());
        Assert.assertEquals(benzene.getAtom(0), map.get(benzeneQuery.getNode(0)));
        Assert.assertEquals(benzene.getAtom(1), map.get(benzeneQuery.getNode(1)));
        Assert.assertEquals(benzene.getAtom(2), map.get(benzeneQuery.getNode(2)));
    }

    @Test
    public void testItShouldReachGoalWhenAllAtomsAreMapped() {
        IState state0 = new VFState(benzeneQuery, new TargetProperties(benzene));
        Match match0 = new Match(benzeneQuery.getNode(0), benzene.getAtom(0));
        IState state1 = state0.nextState(match0);
        Match match1 = new Match(benzeneQuery.getNode(1), benzene.getAtom(1));
        IState state2 = state1.nextState(match1);
        Match match2 = new Match(benzeneQuery.getNode(2), benzene.getAtom(2));
        IState state3 = state2.nextState(match2);
        Match match3 = new Match(benzeneQuery.getNode(3), benzene.getAtom(3));
        IState state4 = state3.nextState(match3);
        Match match4 = new Match(benzeneQuery.getNode(4), benzene.getAtom(4));
        IState state5 = state4.nextState(match4);

        Assert.assertFalse(state5.isGoal());

        Match match5 = new Match(benzeneQuery.getNode(5), benzene.getAtom(5));
        IState state6 = state5.nextState(match5);

        Assert.assertTrue(state6.isGoal());
    }

    @Test
    public void testItShouldHaveANextCandidateInTheSecondaryState() {
        IState state = new VFState(benzeneQuery, new TargetProperties(benzene));
        Match match = new Match(benzeneQuery.getNode(0), benzene.getAtom(0));
        IState nextState = state.nextState(match);
        Assert.assertTrue(nextState.hasNextCandidate());
    }

    /**
     *
     */
    @Test
    public void testItShouldMatchHexaneToHexaneWhenUsingMolecule() {
        IMapper mapper = new VFMapper(hexane, true);
        Assert.assertTrue(mapper.hasMap(hexane));
    }

    public static IAtomContainer createHexane() throws CDKException {
        IAtomContainer result = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtom c1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c1.setID("1");
        IAtom c2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c2.setID("2");
        IAtom c3 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c3.setID("3");
        IAtom c4 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c4.setID("4");
        IAtom c5 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c5.setID("5");
        IAtom c6 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c6.setID("6");

        result.addAtom(c1);
        result.addAtom(c2);
        result.addAtom(c3);
        result.addAtom(c4);
        result.addAtom(c5);
        result.addAtom(c6);

        IBond bond1 = new Bond(c1, c2, IBond.Order.SINGLE);
        IBond bond2 = new Bond(c2, c3, IBond.Order.SINGLE);
        IBond bond3 = new Bond(c3, c4, IBond.Order.SINGLE);
        IBond bond4 = new Bond(c4, c5, IBond.Order.SINGLE);
        IBond bond5 = new Bond(c5, c6, IBond.Order.SINGLE);

        result.addBond(bond1);
        result.addBond(bond2);
        result.addBond(bond3);
        result.addBond(bond4);
        result.addBond(bond5);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(result);

        return result;
    }

    public static IAtomContainer createBenzene() throws CDKException {
        IAtomContainer result = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);

        IAtom c1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c1.setID("1");
        IAtom c2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c2.setID("2");
        IAtom c3 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c3.setID("3");
        IAtom c4 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c4.setID("4");
        IAtom c5 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c5.setID("5");
        IAtom c6 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c6.setID("6");

        result.addAtom(c1);
        result.addAtom(c2);
        result.addAtom(c3);
        result.addAtom(c4);
        result.addAtom(c5);
        result.addAtom(c6);

        IBond bond1 = new Bond(c1, c2, IBond.Order.SINGLE);
        IBond bond2 = new Bond(c2, c3, IBond.Order.DOUBLE);
        IBond bond3 = new Bond(c3, c4, IBond.Order.SINGLE);
        IBond bond4 = new Bond(c4, c5, IBond.Order.DOUBLE);
        IBond bond5 = new Bond(c5, c6, IBond.Order.SINGLE);
        IBond bond6 = new Bond(c6, c1, IBond.Order.DOUBLE);

        result.addBond(bond1);
        result.addBond(bond2);
        result.addBond(bond3);
        result.addBond(bond4);
        result.addBond(bond5);
        result.addBond(bond6);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(result);
        return result;
    }
}
