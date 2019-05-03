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

package org.openscience.cdk.isomorphism;

import org.junit.Test;
import org.openscience.cdk.graph.GraphUtil;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.templates.TestMoleculeFactory;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * These are isolated tests - really difficult to isolate the behaviour here but
 * will add.
 *
 * @author John May
 * @cdk.module test-isomorphism
 */
public class VFStateTest {

    // 0-look-ahead
    @Test
    public void infeasibleAtoms() throws Exception {
        AtomMatcher mock = mock(AtomMatcher.class);
        when(mock.matches(any(IAtom.class), any(IAtom.class))).thenReturn(false);
        VFState state = createBenzeneToNaphthalene(mock, mock(BondMatcher.class));
        for (int i = 0; i < state.nMax(); i++) {
            for (int j = 0; j < state.mMax(); j++) {
                assertFalse(state.feasible(i, j));
            }
        }
    }

    // 0-look-ahead
    @Test
    public void infeasibleBonds() throws Exception {
        BondMatcher mock = mock(BondMatcher.class);
        when(mock.matches(any(IBond.class), any(IBond.class))).thenReturn(false);
        VFState state = createBenzeneToNaphthalene(AtomMatcher.forAny(), mock);
        state.m1[0] = 0;
        state.m1[1] = 1;
        state.m1[2] = 2;
        state.m1[3] = 3;
        state.m1[4] = 4;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                assertFalse(state.feasible(i, j));
            }
        }
    }

    // 1-look-ahead
    @Test
    public void infeasibleTerminalCount() throws Exception {
        VFState state = createBenzeneToNaphthalene(AtomMatcher.forAny(), BondMatcher.forAny());
        assertTrue(state.feasible(4, 4)); // 4, 4 is feasible
        state.add(0, 0);
        state.add(1, 1);
        state.add(2, 2);
        state.add(3, 3);
        assertFalse(state.feasible(4, 4)); // 4, 4 is infeasible
    }

    /**
     * Create a sub state for matching benzene to naphthalene Benzene:
     * InChI=1/C6H6/c1-2-4-6-5-3-1/h1-6H Naphthalene: InChI=1/C10H8/c1-2-6-10-8-4-3-7-9(10)5-1/h1-8H
     */
    VFState createBenzeneToNaphthalene(AtomMatcher atomMatcher, BondMatcher bondMatcher) throws Exception {
        IAtomContainer container1 = TestMoleculeFactory.makeBenzene();
        IAtomContainer container2 = TestMoleculeFactory.makeNaphthalene();
        GraphUtil.EdgeToBondMap bonds1 = GraphUtil.EdgeToBondMap.withSpaceFor(container1);
        GraphUtil.EdgeToBondMap bonds2 = GraphUtil.EdgeToBondMap.withSpaceFor(container2);
        int[][] g1 = GraphUtil.toAdjList(container1, bonds1);
        int[][] g2 = GraphUtil.toAdjList(container2, bonds2);
        return new VFState(container1, container2, g1, g2, bonds1, bonds2, atomMatcher, bondMatcher);
    }

}
