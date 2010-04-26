
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
package org.openscience.cdk.smsd.algorithm.mcgregor;

import java.util.List;
import java.util.Map;
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
public class McGregorTest {

    public McGregorTest() {
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
     * Test of startMcGregorIteration method, of class McGregor.
     */
    @Test
    public void testStartMcGregorIteration_int_Map() throws Exception {
        System.out.println("startMcGregorIteration");
        int largestMappingSize = 0;
        Map<Integer, Integer> present_Mapping = null;
        McGregor instance = null;
        instance.startMcGregorIteration(largestMappingSize, present_Mapping);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of startMcGregorIteration method, of class McGregor.
     */
    @Test
    public void testStartMcGregorIteration_3args() throws Exception {
        System.out.println("startMcGregorIteration");
        int largestMappingSize = 0;
        List<Integer> clique_vector = null;
        List<Integer> comp_graph_nodes = null;
        McGregor instance = null;
        instance.startMcGregorIteration(largestMappingSize, clique_vector, comp_graph_nodes);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMappings method, of class McGregor.
     */
    @Test
    public void testGetMappings() {
        System.out.println("getMappings");
        McGregor instance = null;
        List expResult = null;
        List result = instance.getMappings();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMCSSize method, of class McGregor.
     */
    @Test
    public void testGetMCSSize() {
        System.out.println("getMCSSize");
        McGregor instance = null;
        int expResult = 0;
        int result = instance.getMCSSize();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isNewMatrix method, of class McGregor.
     */
    @Test
    public void testIsNewMatrix() {
        System.out.println("isNewMatrix");
        McGregor instance = null;
        boolean expResult = false;
        boolean result = instance.isNewMatrix();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setNewMatrix method, of class McGregor.
     */
    @Test
    public void testSetNewMatrix() {
        System.out.println("setNewMatrix");
        boolean newMatrix = false;
        McGregor instance = null;
        instance.setNewMatrix(newMatrix);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}
