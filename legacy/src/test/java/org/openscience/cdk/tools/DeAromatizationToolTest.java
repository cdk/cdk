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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.Ring;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * Tests the DeAromatizationTool.
 *
 * @cdk.module test-extra
 */
class DeAromatizationToolTest extends CDKTestCase {

    DeAromatizationToolTest() {
        super();
    }

    @Test
    void testBezene() {
        Ring benzene = new Ring(6, "C");
        for (IBond iBond : benzene.bonds()) iBond.setFlag(CDKConstants.ISAROMATIC, true);
        boolean success = DeAromatizationTool.deAromatize(benzene);
        Assertions.assertTrue(success);
        double bondOrderSum = AtomContainerManipulator.getSingleBondEquivalentSum(benzene);
        Assertions.assertEquals(9.0, bondOrderSum, 0.00001);
    }

    @Test
    void testPyridine() {
        Ring pyridine = new Ring(6, "C");
        pyridine.getAtom(0).setSymbol("N");
        for (IBond iBond : pyridine.bonds()) iBond.setFlag(CDKConstants.ISAROMATIC, true);
        boolean success = DeAromatizationTool.deAromatize(pyridine);
        Assertions.assertTrue(success);
        double bondOrderSum = AtomContainerManipulator.getSingleBondEquivalentSum(pyridine);
        Assertions.assertEquals(9.0, bondOrderSum, 0.00001);
    }

    @Test
    void testDeAromatize_IRing() {
        Ring butadiene = new Ring(4, "C");
        boolean success = DeAromatizationTool.deAromatize(butadiene);
        Assertions.assertFalse(success);
    }

}
