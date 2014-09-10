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
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.tools.diff.tree.IDifference;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @cdk.module test-diff
 */
public class ElementDiffTest extends CDKTestCase {

    @Test
    public void testMatchAgainstItself() {
        IElement element1 = mock(IElement.class);
        String result = ElementDiff.diff(element1, element1);
        assertZeroLength(result);
    }

    @Test
    public void testDiff() {
        IElement element1 = mock(IElement.class);
        IElement element2 = mock(IElement.class);
        when(element1.getSymbol()).thenReturn("H");
        when(element2.getSymbol()).thenReturn("C");

        String result = ElementDiff.diff(element1, element2);
        Assert.assertNotNull(result);
        Assert.assertNotSame(0, result.length());
        assertContains(result, "ElementDiff");
        assertContains(result, "H/C");
    }

    @Test
    public void testDifference() {
        IElement element1 = mock(IElement.class);
        IElement element2 = mock(IElement.class);
        when(element1.getSymbol()).thenReturn("H");
        when(element2.getSymbol()).thenReturn("C");

        IDifference difference = ElementDiff.difference(element1, element2);
        Assert.assertNotNull(difference);
    }
}
