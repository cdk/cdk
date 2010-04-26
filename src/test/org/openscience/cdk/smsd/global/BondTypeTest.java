
/* Copyright (C) 2009-2010 Syed Asad Rahman {asad@ebi.ac.uk}
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
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

package org.openscience.cdk.smsd.global;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Syed Asad Rahman <asad@ebi.ac.uk>
 *
 * @cdk.module test-smsd
 * @cdk.require java1.6+
 */
public class BondTypeTest {

    public BondTypeTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getInstance method, of class BondType.
     */
    @Test
    public void testGetInstance() {
        System.out.println("getInstance");
        BondType expResult = null;
        BondType result = BondType.getInstance();
        assertNotSame(expResult, result);
    }

    /**
     * Test of setBondSensitiveFlag method, of class BondType.
     */
    @Test
    public void testSetBondSensitiveFlag() {
        System.out.println("setBondSensitiveFlag");
        boolean isBondSensitive = true;
        BondType instance = new BondType();
        instance.setBondSensitiveFlag(isBondSensitive);
        assertNotSame(isBondSensitive, false);
    }

    /**
     * Test of isBondSensitive method, of class BondType.
     */
    @Test
    public void testIsBondSensitive() {
        System.out.println("isBondSensitive");
        BondType instance = new BondType();
        boolean expResult = true;
        instance.setBondSensitiveFlag(true);
        boolean result = instance.isBondSensitive();
        assertEquals(expResult, result);
    }

}