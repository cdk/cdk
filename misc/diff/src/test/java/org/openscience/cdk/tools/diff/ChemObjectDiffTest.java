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
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.tools.diff.tree.IDifference;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 */
class ChemObjectDiffTest {

    @Test
    void testMatchAgainstItself() {
        IChemObject atom1 = mock(IChemObject.class);
        String result = ChemObjectDiff.diff(atom1, atom1);
        Assertions.assertEquals("", result);
    }

    @Test
    void testDiff() {
        IChemObject atom1 = mock(IChemObject.class);
        IChemObject atom2 = mock(IChemObject.class);
        when(atom1.getFlags()).thenReturn(new boolean[]{false, false, false});
        when(atom2.getFlags()).thenReturn(new boolean[]{false, true, false});

        String result = ChemObjectDiff.diff(atom1, atom2);
        Assertions.assertNotNull(result);
        Assertions.assertNotSame(0, result.length(), "Expected non-zero-length result");
        MatcherAssert.assertThat(result, containsString("ChemObjectDiff"));
        MatcherAssert.assertThat(result, containsString("F/T"));
    }

    @Test
    void testDifference() {
        IChemObject atom1 = mock(IChemObject.class);
        IChemObject atom2 = mock(IChemObject.class);
        when(atom1.getFlags()).thenReturn(new boolean[]{false, false, false});
        when(atom2.getFlags()).thenReturn(new boolean[]{false, true, false});

        IDifference difference = ChemObjectDiff.difference(atom1, atom2);
        Assertions.assertNotNull(difference);
    }
}
