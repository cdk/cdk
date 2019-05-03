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
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.tools.diff.tree.IDifference;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @cdk.module test-diff
 */
public class ChemObjectDiffTest extends CDKTestCase {

    @Test
    public void testMatchAgainstItself() {
        IChemObject atom1 = mock(IChemObject.class);
        String result = ChemObjectDiff.diff(atom1, atom1);
        assertZeroLength(result);
    }

    @Test
    public void testDiff() {
        IChemObject atom1 = mock(IChemObject.class);
        IChemObject atom2 = mock(IChemObject.class);
        when(atom1.getFlags()).thenReturn(new boolean[]{false, false, false});
        when(atom2.getFlags()).thenReturn(new boolean[]{false, true, false});

        String result = ChemObjectDiff.diff(atom1, atom2);
        Assert.assertNotNull(result);
        Assert.assertNotSame("Expected non-zero-length result", 0, result.length());
        assertContains(result, "ChemObjectDiff");
        assertContains(result, "F/T");
    }

    @Test
    public void testDifference() {
        IChemObject atom1 = mock(IChemObject.class);
        IChemObject atom2 = mock(IChemObject.class);
        when(atom1.getFlags()).thenReturn(new boolean[]{false, false, false});
        when(atom2.getFlags()).thenReturn(new boolean[]{false, true, false});

        IDifference difference = ChemObjectDiff.difference(atom1, atom2);
        Assert.assertNotNull(difference);
    }
}
