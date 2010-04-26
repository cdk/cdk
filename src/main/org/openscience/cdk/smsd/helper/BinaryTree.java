
/**
 *
 * Copyright (C) 2006-2010  Syed Asad Rahman {asad@ebi.ac.uk}
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

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;


/**
 * Class to construct a Binary tree for McGregor search
 * @cdk.module smsd
 * @cdk.githash
 * @author Syed Asad Rahman <asad@ebi.ac.uk>
 */
@TestClass("org.openscience.cdk.smsd.helper.BinaryTreeTest")
public class BinaryTree {

    /**
     * Creates a new instance of BinaryTree
     * the second part of the program extents the mapping by the McGregor algorithm in case
     * that not all atoms of molecule A and molecule B are mapped by the clique approach
     * @param value
     */
    @TestMethod("testGetEnergies")
    public BinaryTree(int value) {
        this.value = value;
    }
    /**
     * not equal is initialized as null
     */
    private BinaryTree equal = null;
    private BinaryTree notEqual = null;
    private int value = -1;

    /**
     * Return value of the node
     * @return get the value of the current node
     */
    @TestMethod("testGetValue")
    public int getValue() {
        return this.value;
    }

    /**
     * Returns equal node
     * @return the equal
     */
    @TestMethod("testGetEqual")
    public BinaryTree getEqual() {
        return equal;
    }

    /**
     * Set equal node
     * @param equal the equal to set
     */
    @TestMethod("testSetEqual")
    public void setEqual(BinaryTree equal) {
        this.equal = equal;
    }

    /**
     * Returns not equal node
     * @return the notEqual
     */
    @TestMethod("testGetNotEqual")
    public BinaryTree getNotEqual() {
        return notEqual;
    }

    /**
     * Set not equal node
     * @param notEqual the notEqual to set
     */
    @TestMethod("testSetNotEqual")
    public void setNotEqual(BinaryTree notEqual) {
        this.notEqual = notEqual;
    }
}
