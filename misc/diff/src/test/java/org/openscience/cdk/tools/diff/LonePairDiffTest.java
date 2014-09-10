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
package org.openscience.cdk.tools.diff;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.ILonePair;
import org.openscience.cdk.tools.diff.tree.IDifference;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @cdk.module test-diff
 */
public class LonePairDiffTest extends CDKTestCase {

    @Test
    public void testMatchAgainstItself() {
        ILonePair bond1 = mock(ILonePair.class);
        String result = LonePairDiff.diff(bond1, bond1);
        assertZeroLength(result);
    }

    @Test
    public void testDiff() {

        IAtom carbon = mock(IAtom.class);
        IAtom oxygen = mock(IAtom.class);

        when(carbon.getSymbol()).thenReturn("C");
        when(oxygen.getSymbol()).thenReturn("O");

        ILonePair bond1 = mock(ILonePair.class);
        ILonePair bond2 = mock(ILonePair.class);

        when(bond1.getAtom()).thenReturn(carbon);
        when(bond2.getAtom()).thenReturn(oxygen);

        String result = LonePairDiff.diff(bond1, bond2);
        Assert.assertNotNull(result);
        Assert.assertNotSame(0, result.length());
        assertContains(result, "LonePairDiff");
        assertContains(result, "AtomDiff");
        assertContains(result, "C/O");
    }

    @Test
    public void testDifference() {
        IAtom carbon = mock(IAtom.class);
        IAtom oxygen = mock(IAtom.class);

        when(carbon.getSymbol()).thenReturn("C");
        when(oxygen.getSymbol()).thenReturn("O");

        ILonePair bond1 = mock(ILonePair.class);
        ILonePair bond2 = mock(ILonePair.class);

        when(bond1.getAtom()).thenReturn(carbon);
        when(bond2.getAtom()).thenReturn(oxygen);

        IDifference difference = LonePairDiff.difference(bond1, bond2);
        Assert.assertNotNull(difference);
    }
}
