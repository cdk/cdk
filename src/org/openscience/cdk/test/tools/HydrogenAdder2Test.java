/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2003-2004  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.test.tools;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.tools.HydrogenAdder;

/**
 * Tests CDK's hydrogen adding capabilities in terms of
 * example molecules.
 *
 * @cdk.module test
 *
 * @author     egonw
 * @created    2003-06-18
 */
public class HydrogenAdder2Test extends HydrogenAdderTest {

    public HydrogenAdder2Test(String name) {
        super(name);
    }

    /**
     * The JUnit setup method
     */
    public void setUp() {
        adder = new HydrogenAdder("org.openscience.cdk.tools.ValencyChecker");
    }

    /**
     * A unit test suite for JUnit
     *
     * @return    The test suite
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(HydrogenAdder2Test.class);
        return suite;
    }
    /**
     * Na is not in the list. Don't try to figure this one out.
     *
     * @see org.openscience.cdk.test.tools.HydrogenAdderTest#testNaCl
     */
    public void testNaCl() {}
    
    /**
     * I don't think aromaticity should be taking into account as done in
     * the SaturationChecker.
     *
     * @see org.openscience.cdk.test.tools.HydrogenAdderTest#testAromaticSaturation
     */
    public void testAromaticSaturation() {}


}

