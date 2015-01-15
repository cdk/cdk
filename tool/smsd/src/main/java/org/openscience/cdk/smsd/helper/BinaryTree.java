/**
 *
 * Copyright (C) 2006-2010  Syed Asad Rahman <asad@ebi.ac.uk>
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


/**
 * Class to construct a Binary tree for McGregor search.
 * @cdk.module smsd
 * @cdk.githash
 * @author Syed Asad Rahman <asad@ebi.ac.uk>
 */
public class BinaryTree {

    /**
     * Creates a new instance of BinaryTree.
     * @param value node value
     */
    public BinaryTree(int value) {
        this.value = value;
    }

    /**
     * not equal is initialized as null
     */
    private BinaryTree equal    = null;
    private BinaryTree notEqual = null;
    private int        value    = -1;

    /**
     * Return value of the node
     * @return get the value of the current node
     */
    public int getValue() {
        return this.value;
    }

    /**
     * Returns equal node
     * @return the equal
     */
    public BinaryTree getEqual() {
        return equal;
    }

    /**
     * Set equal node
     * @param equal the equal to set
     */
    public void setEqual(BinaryTree equal) {
        this.equal = equal;
    }

    /**
     * Returns not equal node
     * @return the notEqual
     */
    public BinaryTree getNotEqual() {
        return notEqual;
    }

    /**
     * Set not equal node
     * @param notEqual the notEqual to set
     */
    public void setNotEqual(BinaryTree notEqual) {
        this.notEqual = notEqual;
    }
}
