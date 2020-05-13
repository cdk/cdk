/*
 * Copyright (c) 2013. John May <jwmay@users.sf.net>
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */

package org.openscience.cdk.hash;

import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.openscience.cdk.interfaces.IBond.Order.DOUBLE;

/**
 * @author John May
 * @cdk.module test-hash
 */
public class MinimumEquivalentCyclicSetUnionTest {

    @Test
    public void testFind() throws Exception {
        IAtomContainer dummy = mock(IAtomContainer.class);
        int[][] g = new int[][]{{1, 5, 6}, {0, 2}, {1, 3}, {2, 4, 7}, {3, 5}, {0, 4}, {0}, {3}};

        // mock the invariants
        long[] values = new long[]{1, 4, 3, 1, 3, 5, 7, 8};

        EquivalentSetFinder finder = new AllEquivalentCyclicSet();
        Set<Integer> set = finder.find(values, dummy, g);

        assertThat(set.size(), is(4));
        // the first size vertex are all in a cycle
        assertTrue(set.contains(0));
        assertTrue(set.contains(2));
        assertTrue(set.contains(3));
        assertTrue(set.contains(4));
    }

    @Test
    public void testFind_Distinct() throws Exception {
        IAtomContainer dummy = mock(IAtomContainer.class);
        int[][] g = new int[][]{{1, 5, 6}, {0, 2}, {1, 3}, {2, 4, 7}, {3, 5}, {0, 4}, {0}, {3}};

        // mock the invariants
        long[] values = new long[]{1, 2, 3, 4, 5, 6, 7, 8};

        EquivalentSetFinder finder = new AllEquivalentCyclicSet();
        Set<Integer> set = finder.find(values, dummy, g);

        assertThat(set.size(), is(0));
    }

    /**
     * Test the method at perturbing the 2D representations of CID 44333798 and
     * CID 57170558. These molecules are similar but distinct. To tell these
     * apart we must use {@link org.openscience.cdk.hash.MinimumEquivalentCyclicSetUnion} opposed to the
     * faster method. This test serves to demonstrates the basic equivalent set
     * finder does not tell them apart but that a more comprehensive set finder
     * does.
     */
    @Test
    public void testScenario() {

        IAtomContainer cid4433798 = cid44333798();
        IAtomContainer cid57170558 = cid57170558();

        MoleculeHashGenerator basic = new HashGeneratorMaker().depth(12).elemental().perturbed().molecular();
        // basic equivalence method can't tell these apart
        assertThat(basic.generate(cid4433798), is(basic.generate(cid57170558)));

        MoleculeHashGenerator cmplx = new HashGeneratorMaker().depth(12).elemental()
                .perturbWith(new MinimumEquivalentCyclicSetUnion()).molecular();

        // complex equivalence method can tell these apart
        assertThat(cmplx.generate(cid4433798), is(not(cmplx.generate(cid57170558))));
    }

    /**
     * CC1=CC=C(C=C1)N2C3CCC2CC3
     *
     * @cdk.inchi InChI=1S/C13H17N/c1-10-2-4-11(5-3-10)14-12-6-7-13(14)9-8-12/h2-5,12-13H,6-9H2,1H3
     */
    private IAtomContainer cid44333798() {
        IAtomContainer m = new AtomContainer(14, 16, 0, 0);
        IAtom[] as = new IAtom[]{new Atom("C"), new Atom("C"), new Atom("C"), new Atom("C"), new Atom("C"),
                new Atom("C"), new Atom("C"), new Atom("N"), new Atom("C"), new Atom("C"), new Atom("C"),
                new Atom("C"), new Atom("C"), new Atom("C"),};
        IBond[] bs = new IBond[]{new Bond(as[1], as[0]), new Bond(as[2], as[1], DOUBLE), new Bond(as[3], as[2]),
                new Bond(as[4], as[3], DOUBLE), new Bond(as[5], as[4]), new Bond(as[6], as[5], DOUBLE),
                new Bond(as[6], as[1]), new Bond(as[7], as[4]), new Bond(as[8], as[7]), new Bond(as[9], as[8]),
                new Bond(as[10], as[9]), new Bond(as[11], as[10]), new Bond(as[11], as[7]), new Bond(as[12], as[11]),
                new Bond(as[13], as[12]), new Bond(as[13], as[8]),};
        m.setAtoms(as);
        m.setBonds(bs);
        return m;
    }

    /**
     * CC1=CC=C(C=C1)N(C2CC2)C3CC3
     *
     * @cdk.inchi InChI=1S/C13H17N/c1-10-2-4-11(5-3-10)14(12-6-7-12)13-8-9-13/h2-5,12-13H,6-9H2,1H3
     */
    private IAtomContainer cid57170558() {
        IAtomContainer m = new AtomContainer(14, 16, 0, 0);
        IAtom[] as = new IAtom[]{new Atom("C"), new Atom("C"), new Atom("C"), new Atom("C"), new Atom("C"),
                new Atom("C"), new Atom("C"), new Atom("N"), new Atom("C"), new Atom("C"), new Atom("C"),
                new Atom("C"), new Atom("C"), new Atom("C"),};
        IBond[] bs = new IBond[]{new Bond(as[1], as[0]), new Bond(as[2], as[1], DOUBLE), new Bond(as[3], as[2]),
                new Bond(as[4], as[3], DOUBLE), new Bond(as[5], as[4]), new Bond(as[6], as[5], DOUBLE),
                new Bond(as[6], as[1]), new Bond(as[7], as[4]), new Bond(as[8], as[7]), new Bond(as[9], as[8]),
                new Bond(as[10], as[9]), new Bond(as[10], as[8]), new Bond(as[11], as[7]), new Bond(as[12], as[11]),
                new Bond(as[13], as[12]), new Bond(as[13], as[11]),};
        m.setAtoms(as);
        m.setBonds(bs);
        return m;
    }
}
