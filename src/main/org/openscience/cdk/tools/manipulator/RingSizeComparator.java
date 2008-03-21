/* $RCSfile$
 * $Author$ 
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.tools.manipulator;

import java.util.Comparator;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.interfaces.IRing;

/**
 * @cdk.module standard
 * @cdk.svnrev  $Revision$
 */
@TestClass("org.openscience.cdk.tools.manipulator.RingSizeComparatorTest")
public class RingSizeComparator implements Comparator<IRing> {
    
    /** Flag to denote that the set is order with the largest ring first */
    public final static int LARGE_FIRST = 1;
    /** Flag to denote that the set is order with the smallest ring first */
    public final static int SMALL_FIRST = 2;
    
    int sortOrder = SMALL_FIRST;
    
    /**
    * Constructs a new comparator to sort rings by size.
    *
    * @param   order  Sort order: either RingSet.SMALL_FIRST or
    *                                 RingSet.LARGE_FIRST.
    */
    public RingSizeComparator(int order) {
        sortOrder = order;
    }

    @TestMethod("testCompare")
    public int compare(IRing object1, IRing object2) throws ClassCastException
    {
        int size1 = object1.getAtomCount();
        int size2 = object2.getAtomCount();
        if (size1 == size2) return 0;
        if (size1 > size2 && sortOrder == SMALL_FIRST) {
            return 1;
        }
        if (size1 > size2 && sortOrder == LARGE_FIRST) {
            return -1;
        }
        if (size1 < size2 && sortOrder == SMALL_FIRST) {
            return -1;
        }
        if (size1 < size2 && sortOrder == LARGE_FIRST) {
            return 1;
        }
        return 0;
    }
}

