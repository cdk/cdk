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
package org.openscience.cdk.smsd.filters;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 *
 * @cdk.module test-smsd
 * @cdk.require java1.6+
 */
public class PostFilterTest {

    public PostFilterTest() {}

    @BeforeClass
    public static void setUpClass() throws Exception {}

    @AfterClass
    public static void tearDownClass() throws Exception {}

    @Before
    public void setUp() {}

    @After
    public void tearDown() {}

    /**
     * Test of filter method, of class PostFilter.
     */
    @Test
    public void testFilter() {

        List<Integer> l1 = new ArrayList<Integer>(6);
        l1.add(1);
        l1.add(2);
        l1.add(3);
        l1.add(4);
        l1.add(5);
        l1.add(6);

        List<Integer> l2 = new ArrayList<Integer>(6);
        l2.add(1);
        l2.add(2);
        l2.add(3);
        l2.add(4);
        l2.add(5);
        l2.add(6);

        List<Integer> l3 = new ArrayList<Integer>(6);
        l3.add(1);
        l3.add(2);
        l3.add(5);
        l3.add(4);
        l3.add(3);
        l3.add(6);

        List<List<Integer>> mappings = new ArrayList<List<Integer>>(3);
        mappings.add(l1);
        mappings.add(l2);
        mappings.add(l3);

        assertEquals(3, mappings.size());
        List<Map<Integer, Integer>> expResult = PostFilter.filter(mappings);
        assertEquals(2, expResult.size());
    }
}
