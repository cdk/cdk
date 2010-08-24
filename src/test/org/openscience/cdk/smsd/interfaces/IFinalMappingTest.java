
/* Copyright (C) 2009-2010 Syed Asad Rahman <asad@ebi.ac.uk>
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
package org.openscience.cdk.smsd.interfaces;

import java.util.Iterator;
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
 */
public class IFinalMappingTest {

    public IFinalMappingTest() {
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
     * Test of add method, of class IFinalMapping.
     */
    @Test
    public void testAdd() {
        System.out.println("add");
        Map<Integer, Integer> mapping = null;
        IFinalMapping instance = new IFinalMappingImpl();
        instance.add(mapping);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of set method, of class IFinalMapping.
     */
    @Test
    public void testSet() {
        System.out.println("set");
        List<Map<Integer, Integer>> mappings = null;
        IFinalMapping instance = new IFinalMappingImpl();
        instance.set(mappings);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getIterator method, of class IFinalMapping.
     */
    @Test
    public void testGetIterator() {
        System.out.println("getIterator");
        IFinalMapping instance = new IFinalMappingImpl();
        Iterator expResult = null;
        Iterator result = instance.getIterator();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of clear method, of class IFinalMapping.
     */
    @Test
    public void testClear() {
        System.out.println("clear");
        IFinalMapping instance = new IFinalMappingImpl();
        instance.clear();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getFinalMapping method, of class IFinalMapping.
     */
    @Test
    public void testGetFinalMapping() {
        System.out.println("getFinalMapping");
        IFinalMapping instance = new IFinalMappingImpl();
        List expResult = null;
        List result = instance.getFinalMapping();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSize method, of class IFinalMapping.
     */
    @Test
    public void testGetSize() {
        System.out.println("getSize");
        IFinalMapping instance = new IFinalMappingImpl();
        int expResult = 0;
        int result = instance.getSize();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    public class IFinalMappingImpl implements IFinalMapping {

        public void add(Map<Integer, Integer> mapping) {
        }

        public void set(List<Map<Integer, Integer>> mappings) {
        }

        public Iterator<Map<Integer, Integer>> getIterator() {
            return null;
        }

        public void clear() {
        }

        public List<Map<Integer, Integer>> getFinalMapping() {
            return null;
        }

        public int getSize() {
            return 0;
        }
    }

}