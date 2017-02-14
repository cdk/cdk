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

import static org.junit.Assert.assertEquals;

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
public class BinaryTreeTest {

    public BinaryTreeTest() {}

    @BeforeClass
    public static void setUpClass() throws Exception {}

    @AfterClass
    public static void tearDownClass() throws Exception {}

    @Before
    public void setUp() {}

    @After
    public void tearDown() {}

    /**
     * Test of getValue method, of class BinaryTree.
     */
    @Test
    public void testGetValue() {
        BinaryTree instance = new BinaryTree(15);
        int expResult = 15;
        int result = instance.getValue();
        assertEquals(expResult, result);
    }

    /**
     * Test of getEqual method, of class BinaryTree.
     */
    @Test
    public void testGetEqual() {
        BinaryTree instance = new BinaryTree(15);
        BinaryTree equal = new BinaryTree(15);
        instance.setEqual(equal);
        instance.setNotEqual(new BinaryTree(10));
        BinaryTree expResult = equal;
        BinaryTree result = instance.getEqual();
        assertEquals(expResult, result);
    }

    /**
     * Test of setEqual method, of class BinaryTree.
     */
    @Test
    public void testSetEqual() {
        BinaryTree instance = new BinaryTree(15);
        BinaryTree equal = new BinaryTree(15);
        instance.setEqual(equal);
        instance.setNotEqual(new BinaryTree(10));
        BinaryTree expResult = equal;
        BinaryTree result = instance.getEqual();
        assertEquals(expResult, result);
    }

    /**
     * Test of getNotEqual method, of class BinaryTree.
     */
    @Test
    public void testGetNotEqual() {
        BinaryTree instance = new BinaryTree(15);
        BinaryTree equal = new BinaryTree(15);
        BinaryTree notEqual = new BinaryTree(10);
        instance.setEqual(equal);
        instance.setNotEqual(notEqual);
        BinaryTree expResult = notEqual;
        BinaryTree result = instance.getNotEqual();
        assertEquals(expResult, result);
    }

    /**
     * Test of setNotEqual method, of class BinaryTree.
     */
    @Test
    public void testSetNotEqual() {
        BinaryTree instance = new BinaryTree(15);
        BinaryTree equal = new BinaryTree(15);
        BinaryTree notEqual = new BinaryTree(10);
        instance.setEqual(equal);
        instance.setNotEqual(notEqual);
        BinaryTree expResult = notEqual;
        BinaryTree result = instance.getNotEqual();
        assertEquals(expResult, result);
    }
}
