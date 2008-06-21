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
import org.openscience.cdk.NewCDKTestCase;
import org.openscience.cdk.interfaces.IBond;

/**
 * @cdk.module test-diff
 */
public class BondDiffTest extends NewCDKTestCase {

    @Test public void testMatchAgainstItself() {
        IBond bond1 = new Bond();
        String result = BondDiff.diff(bond1, bond1);
        assertZeroLength(result);
    }
    
    @Test public void testDiff() {
        IBond bond1 = new Bond(new Atom("C"), new Atom("C"));
        bond1.setOrder(IBond.Order.SINGLE);
        IBond bond2 = new Bond(new Atom("C"), new Atom("O"));
        bond2.setOrder(IBond.Order.DOUBLE);
        
        String result = BondDiff.diff( bond1, bond2 );
        Assert.assertNotNull(result);
        Assert.assertNotSame(0, result.length());
        Assert.assertTrue(result.contains("BondDiff"));
        Assert.assertTrue(result.contains("SINGLE/DOUBLE"));
        Assert.assertTrue(result.contains("AtomDiff"));
        Assert.assertTrue(result.contains("C/O"));
    }

}
