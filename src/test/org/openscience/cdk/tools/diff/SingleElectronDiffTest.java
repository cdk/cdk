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
import org.openscience.cdk.Bond;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.SingleElectron;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.ISingleElectron;
import org.openscience.cdk.tools.diff.tree.IDifference;

/**
 * @cdk.module test-diff
 */
public class SingleElectronDiffTest extends CDKTestCase {

    @Test public void testMatchAgainstItself() {
        ISingleElectron bond1 = new SingleElectron();
        String result = SingleElectronDiff.diff(bond1, bond1);
        assertZeroLength(result);
    }
    
    @Test public void testDiff() {
        ISingleElectron bond1 = new SingleElectron(new Atom("C"));
        ISingleElectron bond2 = new SingleElectron(new Atom("O"));
        
        String result = SingleElectronDiff.diff( bond1, bond2 );
        Assert.assertNotNull(result);
        Assert.assertNotSame(0, result.length());
        assertContains(result, "SingleElectronDiff");
        assertContains(result, "AtomDiff");
        assertContains(result, "C/O");
    }

    @Test public void testDifference() {
        IBond bond1 = new Bond(new Atom("C"), new Atom("C"));
        bond1.setOrder(IBond.Order.SINGLE);
        IBond bond2 = new Bond(new Atom("C"), new Atom("O"));
        bond2.setOrder(IBond.Order.DOUBLE);

        IDifference difference = BondDiff.difference(bond1, bond2);
        Assert.assertNotNull(difference);
    }
}
