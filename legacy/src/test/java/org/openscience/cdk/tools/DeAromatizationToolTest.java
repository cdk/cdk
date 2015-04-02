/* Copyright (C) 1997-2007  The Chemistry Development Kit (CKD) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All I ask is that proper credit is given for my work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk.tools;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.Ring;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.tools.DeAromatizationTool;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.util.Iterator;

/**
 * Tests the DeAromatizationTool.
 *
 * @cdk.module test-extra
 */
public class DeAromatizationToolTest extends CDKTestCase {

    public DeAromatizationToolTest() {
        super();
    }

    @Test
    public void testBezene() {
        Ring benzene = new Ring(6, "C");
        Iterator<IBond> bonds = benzene.bonds().iterator();
        while (bonds.hasNext())
            bonds.next().setFlag(CDKConstants.ISAROMATIC, true);
        boolean success = DeAromatizationTool.deAromatize(benzene);
        Assert.assertTrue(success);
        double bondOrderSum = AtomContainerManipulator.getSingleBondEquivalentSum(benzene);
        Assert.assertEquals(9.0, bondOrderSum, 0.00001);
    }

    @Test
    public void testPyridine() {
        Ring pyridine = new Ring(6, "C");
        pyridine.getAtom(0).setSymbol("N");
        Iterator<IBond> bonds = pyridine.bonds().iterator();
        while (bonds.hasNext())
            bonds.next().setFlag(CDKConstants.ISAROMATIC, true);
        boolean success = DeAromatizationTool.deAromatize(pyridine);
        Assert.assertTrue(success);
        double bondOrderSum = AtomContainerManipulator.getSingleBondEquivalentSum(pyridine);
        Assert.assertEquals(9.0, bondOrderSum, 0.00001);
    }

    @Test
    public void testDeAromatize_IRing() {
        Ring butadiene = new Ring(4, "C");
        boolean success = DeAromatizationTool.deAromatize(butadiene);
        Assert.assertFalse(success);
    }

}
