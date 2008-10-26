/* $Revision$ $Author$ $Date$
 * 
 * Copyright (C) 2008  Egon Willighagen <egonw@users.sf.net>
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
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA. 
 */
package org.openscience.cdk.tools.diff;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.tools.diff.tree.IDifference;

/**
 * @cdk.module test-diff
 */
public class AtomContainerDiffTest extends CDKTestCase {

    @Test public void testMatchAgainstItself() {
        IAtomContainer container = new AtomContainer();
        IBond bond1 = new Bond();
        container.addBond(bond1);
        String result = AtomContainerDiff.diff(container, container);
        assertZeroLength(result);
    }
    
    @Test public void testDiff() {
        IAtomContainer container1 = new AtomContainer();
        IBond bond1 = new Bond(new Atom("C"), new Atom("C"));
        bond1.setOrder(IBond.Order.SINGLE);
        container1.addBond(bond1);
        IAtomContainer container2 = new AtomContainer();
        IBond bond2 = new Bond(new Atom("C"), new Atom("O"));
        bond2.setOrder(IBond.Order.DOUBLE);
        container2.addBond(bond2);

        String result = AtomContainerDiff.diff(container1, container2);
        Assert.assertNotNull(result);
        Assert.assertNotSame(0, result.length());
        assertContains(result, "AtomContainerDiff");
        assertContains(result, "BondDiff");
        assertContains(result, "SINGLE/DOUBLE");
        assertContains(result, "AtomDiff");
        assertContains(result, "C/O");
    }

    @Test public void testDifference() {
        IAtomContainer container1 = new AtomContainer();
        IBond bond1 = new Bond(new Atom("C"), new Atom("C"));
        bond1.setOrder(IBond.Order.SINGLE);
        container1.addBond(bond1);
        IAtomContainer container2 = new AtomContainer();
        IBond bond2 = new Bond(new Atom("C"), new Atom("O"));
        bond2.setOrder(IBond.Order.DOUBLE);
        container2.addBond(bond2);

        IDifference difference = AtomContainerDiff.difference(container1, container2);
        Assert.assertNotNull(difference);
    }
}
