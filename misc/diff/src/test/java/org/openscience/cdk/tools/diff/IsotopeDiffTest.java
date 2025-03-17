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

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.tools.diff.tree.IDifference;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 */
class IsotopeDiffTest {

    @Test
    void testMatchAgainstItself() {
        IIsotope element1 = mock(IIsotope.class);
        String result = IsotopeDiff.diff(element1, element1);
        Assertions.assertEquals("", result);
    }

    @Test
    void testDiff() {
        IIsotope element1 = mock(IIsotope.class);
        IIsotope element2 = mock(IIsotope.class);
        when(element1.getSymbol()).thenReturn("H");
        when(element2.getSymbol()).thenReturn("C");

        String result = IsotopeDiff.diff(element1, element2);
        Assertions.assertNotNull(result);
        Assertions.assertNotSame(0, result.length());
        MatcherAssert.assertThat(result, containsString("IsotopeDiff"));
        MatcherAssert.assertThat(result, containsString("H/C"));
    }

    @Test
    void testDifference() {
        IIsotope element1 = mock(IIsotope.class);
        IIsotope element2 = mock(IIsotope.class);
        when(element1.getSymbol()).thenReturn("H");
        when(element2.getSymbol()).thenReturn("C");

        IDifference difference = IsotopeDiff.difference(element1, element2);
        Assertions.assertNotNull(difference);
    }
}
