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
package org.openscience.cdk.tools.diff.tree;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.NewCDKTestCase;
import org.openscience.cdk.interfaces.IAtomType;

/**
 * @cdk.module test-diff
 */
public class AtomTypeHybridizationDifferenceTest extends NewCDKTestCase {

    @Test public void testDiff() {
        IDifference result = AtomTypeHybridizationDifference.construct("Foo", IAtomType.Hybridization.SP1, IAtomType.Hybridization.SP2);
        Assert.assertNotNull(result);
    }

    @Test public void testSame() {
        IDifference result = AtomTypeHybridizationDifference.construct("Foo", IAtomType.Hybridization.SP1, IAtomType.Hybridization.SP1);
        Assert.assertNull(result);
    }

    @Test public void testTwoNull() {
        IDifference result = AtomTypeHybridizationDifference.construct("Foo", null, null);
        Assert.assertNull(result);
    }

    @Test public void testOneNull() {
        IDifference result = AtomTypeHybridizationDifference.construct("Foo", null, IAtomType.Hybridization.SP1);
        Assert.assertNotNull(result);
        
        result = AtomTypeHybridizationDifference.construct("Foo", IAtomType.Hybridization.SP1, null);
        Assert.assertNotNull(result);
    }
}
