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
import org.openscience.cdk.ElectronContainer;
import org.openscience.cdk.NewCDKTestCase;
import org.openscience.cdk.interfaces.IElectronContainer;
import org.openscience.cdk.tools.diff.tree.IDifference;

/**
 * @cdk.module test-diff
 */
public class ElectronContainerDiffTest extends NewCDKTestCase {

    @Test public void testMatchAgainstItself() {
        IElectronContainer atom1 = new ElectronContainer();
        String result = ElectronContainerDiff.diff(atom1, atom1);
        assertZeroLength(result);
    }
    
    @Test public void testDiff() {
        IElectronContainer ec1 = new ElectronContainer();
        ec1.setElectronCount(2);
        IElectronContainer ec2 = new ElectronContainer();
        ec2.setElectronCount(3);
        String result = ElectronContainerDiff.diff( ec1, ec2 );
        Assert.assertNotNull(result);
        Assert.assertNotSame(0, result.length());
        assertContains(result, "ElectronContainerDiff");
        assertContains(result, "eCount");
        assertContains(result, "2/3");
    }

    @Test public void testDifference() {
        IElectronContainer ec1 = new ElectronContainer();
        ec1.setElectronCount(2);
        IElectronContainer ec2 = new ElectronContainer();
        ec2.setElectronCount(3);

        IDifference difference = ElectronContainerDiff.difference(ec1, ec2);
        Assert.assertNotNull(difference);
    }
}
