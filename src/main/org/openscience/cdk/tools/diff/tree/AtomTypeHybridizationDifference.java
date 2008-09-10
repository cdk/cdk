/* $Revision$ $Author$ $Date$
 * 
 * Copyright (C) 2008  Egon Willighagen <egonw@users.sf.net>
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
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
package org.openscience.cdk.tools.diff.tree;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.interfaces.IAtomType;

/**
 * {@link org.openscience.cdk.tools.diff.tree.IDifference} between two {@link org.openscience.cdk.interfaces.IAtomType.Hybridization}s.
 * 
 * @author     egonw
 * @cdk.module diff
 */
@TestClass("org.openscience.cdk.tools.diff.tree.AtomTypeHybridizationDifferenceTest")
public class AtomTypeHybridizationDifference implements IDifference {

    private String name;
    private IAtomType.Hybridization first;
    private IAtomType.Hybridization second;
    
    private AtomTypeHybridizationDifference(String name, IAtomType.Hybridization first, IAtomType.Hybridization second) {
        this.name = name;
        this.first = first;
        this.second = second;
    }
    
    @TestMethod("testDiff,testSame,testTwoNull,testOneNull")
    public static IDifference construct(String name, IAtomType.Hybridization first, IAtomType.Hybridization second) {
        if (first == second) {
            return null;
        }
        return new AtomTypeHybridizationDifference(name, first, second);
    }

    @TestMethod("testToString")
    public String toString() {
        return name + ":" + 
            (first == null ? "NA" : first) +
            "/" +
            (second == null ? "NA" : second);        
    }
}
