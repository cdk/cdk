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

/**
 * @author John May
 * @cdk.module test-hash
 */
public class AllEquivalentCyclicSetTest {

    @Test
    public void testFind() throws Exception {
        IAtomContainer dummy = mock(IAtomContainer.class);
        int[][] g = new int[][]{{1, 5, 6}, {0, 2}, {1, 3}, {2, 4, 7}, {3, 5}, {0, 4}, {0}, {3}};

        // this mock the invariants
        long[] values = new long[]{1, 0, 0, 1, 0, 0, 2, 2};

        EquivalentSetFinder finder = new AllEquivalentCyclicSet();
        Set<Integer> set = finder.find(values, dummy, g);

        assertThat(set.size(), is(6));
        // the first size vertex are all in a cycle
        assertTrue(set.contains(0));
        assertTrue(set.contains(1));
        assertTrue(set.contains(2));
        assertTrue(set.contains(3));
        assertTrue(set.contains(4));
        assertTrue(set.contains(5));

    }

    @Test
    public void testFind_Distinct() throws Exception {
        IAtomContainer dummy = mock(IAtomContainer.class);
        int[][] g = new int[][]{{1, 5, 6}, {0, 2}, {1, 3}, {2, 4, 7}, {3, 5}, {0, 4}, {0}, {3}};

        // all values distinct
        long[] values = new long[]{10, 11, 12, 13, 14, 15, 16, 17};

        EquivalentSetFinder finder = new AllEquivalentCyclicSet();
        Set<Integer> set = finder.find(values, dummy, g);

        assertThat(set.size(), is(0));
    }

    /**
     * Test the method at perturbing the 2D representations of CID 138898 and
     * CID 241107. These molecules are very similar but distinct. To tell these
     * apart we must use {@link AllEquivalentCyclicSet} opposed to the faster
     * methods. This test also serves to demonstrates the basic equivalent set
     * finder does not tell them apart but that this more complex finder does.
     */
    @Test
    public void testScenario() throws Exception {

        IAtomContainer cid138898 = cid138898();
        IAtomContainer cid241107 = cid241107();

        MoleculeHashGenerator basic = new HashGeneratorMaker().depth(12).elemental().perturbed().molecular();
        // basic equivalence method can't tell these apart
        assertThat(basic.generate(cid138898), is(basic.generate(cid241107)));

        MoleculeHashGenerator cmplx = new HashGeneratorMaker().depth(12).elemental()
                .perturbWith(new AllEquivalentCyclicSet()).molecular();

        // complex equivalence method can tell these apart
        assertThat(cmplx.generate(cid138898), is(not(cmplx.generate(cid241107))));

    }

    /**
     * PubChem-Compound CID 241107 CC12CC3(SC(S3)(CC(S1)(S2)C)C)C
     *
     * @cdk.inchi InChI=1S/C10H16S4/c1-7-5-8(2)13-10(4,14-8)6-9(3,11-7)12-7/h5-6H2,1-4H3
     */
    private IAtomContainer cid241107() {
        IAtomContainer m = new AtomContainer(14, 16, 0, 0);
        IAtom[] as = new IAtom[]{new Atom("C"), new Atom("C"), new Atom("C"), new Atom("C"), new Atom("S"),
                new Atom("C"), new Atom("S"), new Atom("C"), new Atom("C"), new Atom("S"), new Atom("S"),
                new Atom("C"), new Atom("C"), new Atom("C"),};
        IBond[] bs = new IBond[]{new Bond(as[1], as[0]), new Bond(as[2], as[1]), new Bond(as[3], as[2]),
                new Bond(as[4], as[3]), new Bond(as[5], as[4]), new Bond(as[6], as[5]), new Bond(as[6], as[3]),
                new Bond(as[7], as[5]), new Bond(as[8], as[7]), new Bond(as[9], as[8]), new Bond(as[9], as[1]),
                new Bond(as[10], as[8]), new Bond(as[10], as[1]), new Bond(as[11], as[8]), new Bond(as[12], as[5]),
                new Bond(as[13], as[3]),};
        m.setAtoms(as);
        m.setBonds(bs);
        return m;
    }

    /**
     * PubChem-Compound CID 138898 CC12CC3(SC(S1)(CC(S2)(S3)C)C)C
     *
     * @cdk.inchi InChI=1S/C10H16S4/c1-7-5-8(2)13-9(3,11-7)6-10(4,12-7)14-8/h5-6H2,1-4H3
     */
    private IAtomContainer cid138898() {
        IAtomContainer m = new AtomContainer(14, 16, 0, 0);
        IAtom[] as = new IAtom[]{new Atom("C"), new Atom("C"), new Atom("C"), new Atom("C"), new Atom("S"),
                new Atom("C"), new Atom("S"), new Atom("C"), new Atom("C"), new Atom("S"), new Atom("S"),
                new Atom("C"), new Atom("C"), new Atom("C"),};
        IBond[] bs = new IBond[]{new Bond(as[1], as[0]), new Bond(as[2], as[1]), new Bond(as[3], as[2]),
                new Bond(as[4], as[3]), new Bond(as[5], as[4]), new Bond(as[6], as[5]), new Bond(as[6], as[1]),
                new Bond(as[7], as[5]), new Bond(as[8], as[7]), new Bond(as[9], as[8]), new Bond(as[9], as[1]),
                new Bond(as[10], as[8]), new Bond(as[10], as[3]), new Bond(as[11], as[8]), new Bond(as[12], as[5]),
                new Bond(as[13], as[3]),};
        m.setAtoms(as);
        m.setBonds(bs);
        return m;
    }

}
