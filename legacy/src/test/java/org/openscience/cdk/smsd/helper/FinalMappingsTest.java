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
package org.openscience.cdk.smsd.helper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 *
 * @cdk.require java1.6+
 */
class FinalMappingsTest {

    public FinalMappingsTest() {}

    @BeforeAll
    static void setUpClass() throws Exception {}

    @AfterAll
    static void tearDownClass() throws Exception {}

    @BeforeEach
    void setUp() {}

    @AfterEach
    void tearDown() {}

    /**
     * Test of getInstance method, of class FinalMappings.
     */
    @Test
    void testGetInstance() {
        FinalMappings result = FinalMappings.getInstance();
        Assertions.assertNotNull(result);
    }

    /**
     * Test of add method, of class FinalMappings.
     */
    @Test
    void testAdd() {
        Map<Integer, Integer> mapping = new TreeMap<>();
        mapping.put(1, 1);
        mapping.put(2, 2);
        mapping.put(3, 3);

        FinalMappings instance = new FinalMappings();
        instance.add(mapping);
        Assertions.assertEquals(1, instance.getSize());
    }

    /**
     * Test of set method, of class FinalMappings.
     */
    @Test
    void testSet() {
        Map<Integer, Integer> mapping1 = new TreeMap<>();
        mapping1.put(1, 1);
        mapping1.put(2, 2);
        mapping1.put(3, 3);
        Map<Integer, Integer> mapping2 = new TreeMap<>();
        mapping2.put(1, 2);
        mapping2.put(2, 1);
        mapping2.put(3, 3);

        List<Map<Integer, Integer>> mappings = new ArrayList<>(2);
        mappings.add(mapping1);
        mappings.add(mapping2);
        FinalMappings instance = new FinalMappings();
        instance.set(mappings);

        Assertions.assertEquals(2, instance.getSize());
    }

    /**
     * Test of getIterator method, of class FinalMappings.
     */
    @Test
    void testGetIterator() {
        Map<Integer, Integer> mapping1 = new TreeMap<>();
        mapping1.put(1, 1);
        mapping1.put(2, 2);
        mapping1.put(3, 3);
        Map<Integer, Integer> mapping2 = new TreeMap<>();
        mapping2.put(1, 2);
        mapping2.put(2, 1);
        mapping2.put(3, 3);

        List<Map<Integer, Integer>> mappings = new ArrayList<>(2);
        mappings.add(mapping1);
        mappings.add(mapping2);
        FinalMappings instance = new FinalMappings();
        instance.set(mappings);

        Iterator<Map<Integer, Integer>> result = instance.getIterator();
        Assertions.assertEquals(true, result.hasNext());
    }

    /**
     * Test of clear method, of class FinalMappings.
     */
    @Test
    void testClear() {
        Map<Integer, Integer> mapping1 = new TreeMap<>();
        mapping1.put(1, 1);
        mapping1.put(2, 2);
        mapping1.put(3, 3);
        Map<Integer, Integer> mapping2 = new TreeMap<>();
        mapping2.put(1, 2);
        mapping2.put(2, 1);
        mapping2.put(3, 3);

        List<Map<Integer, Integer>> mappings = new ArrayList<>(2);
        mappings.add(mapping1);
        mappings.add(mapping2);
        FinalMappings instance = new FinalMappings();
        instance.set(mappings);
        Assertions.assertEquals(2, instance.getSize());
        instance.clear();
        Assertions.assertEquals(0, instance.getSize());
    }

    /**
     * Test of getFinalMapping method, of class FinalMappings.
     */
    @Test
    void testGetFinalMapping() {
        Map<Integer, Integer> mapping1 = new TreeMap<>();
        mapping1.put(1, 1);
        mapping1.put(2, 2);
        mapping1.put(3, 3);
        Map<Integer, Integer> mapping2 = new TreeMap<>();
        mapping2.put(1, 2);
        mapping2.put(2, 1);
        mapping2.put(3, 3);

        List<Map<Integer, Integer>> mappings = new ArrayList<>(2);
        mappings.add(mapping1);
        mappings.add(mapping2);
        FinalMappings instance = new FinalMappings();
        instance.set(mappings);

        List<Map<Integer, Integer>> expResult = mappings;
        List<Map<Integer, Integer>> result = instance.getFinalMapping();
        Assertions.assertEquals(expResult, result);
    }

    /**
     * Test of getSize method, of class FinalMappings.
     */
    @Test
    void testGetSize() {
        Map<Integer, Integer> mapping1 = new TreeMap<>();
        mapping1.put(1, 1);
        mapping1.put(2, 2);
        mapping1.put(3, 3);
        Map<Integer, Integer> mapping2 = new TreeMap<>();
        mapping2.put(1, 2);
        mapping2.put(2, 1);
        mapping2.put(3, 3);

        List<Map<Integer, Integer>> mappings = new ArrayList<>(2);
        mappings.add(mapping1);
        mappings.add(mapping2);
        FinalMappings instance = new FinalMappings();
        instance.set(mappings);
        Assertions.assertEquals(2, instance.getSize());
    }
}
