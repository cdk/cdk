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
import org.openscience.cdk.interfaces.IElectronContainer;
import org.openscience.cdk.tools.diff.tree.IDifference;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @cdk.module test-diff
 */
class ElectronContainerDiffTest {

    @Test
    void testMatchAgainstItself() {
        IElectronContainer atom1 = mock(IElectronContainer.class);
        String result = ElectronContainerDiff.diff(atom1, atom1);
        Assertions.assertEquals("", result);
    }

    @Test
    void testDiff() {
        IElectronContainer ec1 = mock(IElectronContainer.class);
        IElectronContainer ec2 = mock(IElectronContainer.class);
        when(ec1.getElectronCount()).thenReturn(2);
        when(ec2.getElectronCount()).thenReturn(3);

        String result = ElectronContainerDiff.diff(ec1, ec2);
        Assertions.assertNotNull(result);
        Assertions.assertNotSame(0, result.length());
        MatcherAssert.assertThat(result, containsString("ElectronContainerDiff"));
        MatcherAssert.assertThat(result, containsString("eCount"));
        MatcherAssert.assertThat(result, containsString("2/3"));
    }

    @Test
    void testDifference() {
        IElectronContainer ec1 = mock(IElectronContainer.class);
        IElectronContainer ec2 = mock(IElectronContainer.class);
        when(ec1.getElectronCount()).thenReturn(2);
        when(ec2.getElectronCount()).thenReturn(3);

        IDifference difference = ElectronContainerDiff.difference(ec1, ec2);
        Assertions.assertNotNull(difference);
    }
}
