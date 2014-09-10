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
import org.openscience.cdk.interfaces.IBond;

/**
 * @cdk.module test-standard
 */
public class AtomContainerPermutorTest extends CDKTestCase {

    @Test
    public void testAtomPermutation() {
        AtomContainer ac = new org.openscience.cdk.AtomContainer();
        AtomContainer result;
        String atoms = new String("");
        ac.addAtom(new Atom("C"));
        ac.addAtom(new Atom("N"));
        ac.addAtom(new Atom("P"));
        ac.addAtom(new Atom("O"));
        ac.addAtom(new Atom("S"));
        ac.addAtom(new Atom("Br"));
        ac.addBond(0, 1, IBond.Order.SINGLE);
        ac.addBond(1, 2, IBond.Order.SINGLE);
        ac.addBond(2, 3, IBond.Order.SINGLE);
        ac.addBond(3, 4, IBond.Order.SINGLE);
        ac.addBond(4, 5, IBond.Order.SINGLE);
        AtomContainerAtomPermutor acap = new AtomContainerAtomPermutor(ac);
        int counter = 0;
        while (acap.hasNext()) {
            counter++;
            atoms = "";
            result = (AtomContainer) acap.next();
            for (int f = 0; f < result.getAtomCount(); f++) {
                atoms += result.getAtom(f).getSymbol();
            }
        }
        Assert.assertEquals(719, counter);
    }

    @Test
    public void testBondPermutation() {
        AtomContainer ac = new org.openscience.cdk.AtomContainer();
        AtomContainer result;
        String bonds = new String("");
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
            bonds = "";
            result = (AtomContainer) acap.next();
            for (int f = 0; f < result.getBondCount(); f++) {
                bonds += result.getBond(f).getOrder();
            }
            //logger.debug(bonds);
        }
        Assert.assertEquals(119, counter);
    }

}
