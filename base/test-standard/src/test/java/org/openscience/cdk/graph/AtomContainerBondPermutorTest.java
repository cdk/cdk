/* Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesserf General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.graph;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

/**
 * @cdk.module test-standard
 */
public class AtomContainerBondPermutorTest extends CDKTestCase {

    public AtomContainerBondPermutorTest() {
        super();
    }

    @Test
    public void constructorTest() {
        IAtomContainer atomContainer = new AtomContainer();
        atomContainer.addAtom(new Atom("C"));
        atomContainer.addAtom(new Atom("O"));
        atomContainer.addAtom(new Atom("S"));
        atomContainer.addBond(0, 1, IBond.Order.SINGLE);
        atomContainer.addBond(0, 2, IBond.Order.SINGLE);
        AtomContainerBondPermutor acbp = new AtomContainerBondPermutor(atomContainer);
        Assert.assertNotNull(acbp);
    }

    @Test
    public void containerFromPermutationTest() {
        IAtomContainer atomContainer = new AtomContainer();
        atomContainer.addAtom(new Atom("C"));
        atomContainer.addAtom(new Atom("O"));
        atomContainer.addAtom(new Atom("S"));
        atomContainer.addBond(0, 1, IBond.Order.SINGLE);
        atomContainer.addBond(0, 2, IBond.Order.SINGLE);
        AtomContainerBondPermutor acbp = new AtomContainerBondPermutor(atomContainer);
        IAtomContainer permuted = acbp.containerFromPermutation(new int[]{1, 0, 2});
        Assert.assertNotNull(permuted);
        Assert.assertEquals(atomContainer.getAtomCount(), permuted.getAtomCount());
        Assert.assertEquals(atomContainer.getBondCount(), permuted.getBondCount());
    }

    @Test
    public void testBondPermutation() {
        AtomContainer ac = new AtomContainer();
        ac.addAtom(new Atom("C"));
        ac.addAtom(new Atom("N"));
        ac.addAtom(new Atom("P"));
        ac.addAtom(new Atom("O"));
        ac.addAtom(new Atom("S"));
        ac.addAtom(new Atom("Br"));
        ac.addBond(0, 1, IBond.Order.SINGLE);
        ac.addBond(1, 2, IBond.Order.DOUBLE);
        ac.addBond(2, 3, IBond.Order.TRIPLE);
        ac.addBond(3, 4, IBond.Order.QUADRUPLE);
        ac.addBond(4, 5, IBond.Order.SINGLE); // was 5.0 !
        AtomContainerBondPermutor acap = new AtomContainerBondPermutor(ac);
        int counter = 0;
        while (acap.hasNext()) {
            counter++;
            acap.next();
        }
        Assert.assertEquals(119, counter);
    }

}
