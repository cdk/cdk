/* Copyright (C) 2008  Egon Willighagen <egonw@users.sf.net>
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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IAtomType;

/**
 * @cdk.module test-diff
 */
class AtomTypeHybridizationDifferenceTest {

    @Test
    void testDiff() {
        IDifference result = AtomTypeHybridizationDifference.construct("Foo", IAtomType.Hybridization.SP1,
                IAtomType.Hybridization.SP2);
        Assertions.assertNotNull(result);
    }

    @Test
    void testSame() {
        IDifference result = AtomTypeHybridizationDifference.construct("Foo", IAtomType.Hybridization.SP1,
                IAtomType.Hybridization.SP1);
        Assertions.assertNull(result);
    }

    @Test
    void testTwoNull() {
        IDifference result = AtomTypeHybridizationDifference.construct("Foo", null, null);
        Assertions.assertNull(result);
    }

    @Test
    void testOneNull() {
        IDifference result = AtomTypeHybridizationDifference.construct("Foo", null, IAtomType.Hybridization.SP1);
        Assertions.assertNotNull(result);

        result = AtomTypeHybridizationDifference.construct("Foo", IAtomType.Hybridization.SP1, null);
        Assertions.assertNotNull(result);
    }

    @Test
    void testToString() {
        IDifference result = AtomTypeHybridizationDifference.construct("Foo", null, IAtomType.Hybridization.SP1);
        String diffString = result.toString();
        Assertions.assertNotNull(diffString);
        StringDifferenceTest.assertOneLiner(diffString);
    }
}
