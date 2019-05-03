/* Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.tools.periodictable;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.CDKTestCase;

/**
 * @cdk.module test-core
 */
public class PeriodicTableTest extends CDKTestCase {

    public PeriodicTableTest() {
        super();
    }

    @Test
    public void testTable() {
        Assert.assertEquals(CDKConstants.UNSET, PeriodicTable.getVdwRadius("Co"));
        Assert.assertEquals(1.7, PeriodicTable.getVdwRadius("C"), 0.001);
        Assert.assertEquals(39, PeriodicTable.getAtomicNumber("Y"), 0.001);
        Assert.assertEquals(2.55, PeriodicTable.getPaulingElectronegativity("C"), 0.001);
        Assert.assertEquals(CDKConstants.UNSET, PeriodicTable.getPaulingElectronegativity("He"));
        Assert.assertEquals(CDKConstants.UNSET,
                org.openscience.cdk.tools.periodictable.PeriodicTable.getCovalentRadius("Pu"));
        Assert.assertEquals(0.32, PeriodicTable.getCovalentRadius("He"), 0.001);
        Assert.assertEquals(14, PeriodicTable.getGroup("C"), 0.01);

        Assert.assertEquals("H", PeriodicTable.getSymbol(1));
        Assert.assertEquals("C", PeriodicTable.getSymbol(6));
    }

}
