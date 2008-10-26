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
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.tools.diff.tree.IDifference;

/**
 * @cdk.module test-diff
 */
public class AtomDiffTest extends CDKTestCase {

    @Test public void testMatchAgainstItself() {
        IAtom atom1 = new Atom();
        String result = AtomDiff.diff(atom1, atom1);
        assertZeroLength(result);
    }
    
    @Test public void testDiff() {
        IAtom atom1 = new Atom();
        atom1.setSymbol("H");
        IAtom atom2 = new Atom();
        atom2.setSymbol("C");
        
        String result = AtomDiff.diff( atom1, atom2 );
        Assert.assertNotNull(result);
        Assert.assertNotSame(0, result.length());
        assertContains(result, "AtomDiff");
        assertContains(result, "H/C");
    }

    @Test public void testDifference() {
        IAtom atom1 = new Atom();
        atom1.setSymbol("H");
        IAtom atom2 = new Atom();
        atom2.setSymbol("C");

        IDifference difference = AtomDiff.difference(atom1, atom2);
        Assert.assertNotNull(difference);
    }
}
