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
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.tools.diff.tree.IDifference;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @cdk.module test-diff
 */
public class BondDiffTest extends CDKTestCase {

    @Test
    public void testMatchAgainstItself() {
        IBond bond1 = mock(IBond.class);
        String result = BondDiff.diff(bond1, bond1);
        assertZeroLength(result);
    }

    @Test
    public void testDiff() {

        IAtom carbon = mock(IAtom.class);
        IAtom oxygen = mock(IAtom.class);

        when(carbon.getSymbol()).thenReturn("C");
        when(oxygen.getSymbol()).thenReturn("O");

        IBond bond1 = mock(IBond.class);
        IBond bond2 = mock(IBond.class);

        when(bond1.getOrder()).thenReturn(IBond.Order.SINGLE);
        when(bond2.getOrder()).thenReturn(IBond.Order.DOUBLE);

        when(bond1.getAtomCount()).thenReturn(2);
        when(bond2.getAtomCount()).thenReturn(2);

        when(bond1.getAtom(0)).thenReturn(carbon);
        when(bond1.getAtom(1)).thenReturn(carbon);
        when(bond2.getAtom(0)).thenReturn(carbon);
        when(bond2.getAtom(1)).thenReturn(oxygen);

        bond1.setOrder(IBond.Order.SINGLE);
        bond2.setOrder(IBond.Order.DOUBLE);

        String result = BondDiff.diff(bond1, bond2);
        Assert.assertNotNull(result);
        Assert.assertNotSame(0, result.length());
        assertContains(result, "BondDiff");
        assertContains(result, "SINGLE/DOUBLE");
        assertContains(result, "AtomDiff");
        assertContains(result, "C/O");
    }

    @Test
    public void testDifference() {
        IAtom carbon = mock(IAtom.class);
        IAtom oxygen = mock(IAtom.class);

        when(carbon.getSymbol()).thenReturn("C");
        when(oxygen.getSymbol()).thenReturn("O");

        IBond bond1 = mock(IBond.class);
        IBond bond2 = mock(IBond.class);

        when(bond1.getOrder()).thenReturn(IBond.Order.SINGLE);
        when(bond2.getOrder()).thenReturn(IBond.Order.DOUBLE);

        when(bond1.getAtomCount()).thenReturn(2);
        when(bond2.getAtomCount()).thenReturn(2);

        when(bond1.getAtom(0)).thenReturn(carbon);
        when(bond1.getAtom(1)).thenReturn(carbon);
        when(bond2.getAtom(0)).thenReturn(carbon);
        when(bond2.getAtom(1)).thenReturn(oxygen);

        bond1.setOrder(IBond.Order.SINGLE);
        bond2.setOrder(IBond.Order.DOUBLE);

        IDifference difference = BondDiff.difference(bond1, bond2);
        Assert.assertNotNull(difference);
    }
}
