/* $Revision$ $Author$ $Date$
 * 
 * Copyright (C) 2007  Egon Willighagen <egonw@users.sf.ne>
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
package org.openscience.cdk.qsar.result;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;

/**
 * IDescriptorResult type for booleans. 
 *
 * @cdk.module standard
 * @cdk.svnrev $Revision$
 */
@TestClass("org.openscience.cdk.qsar.result.IntegerArrayResultTypeTest")
public class IntegerArrayResultType implements IDescriptorResult {
    
	private int size;
	
	public IntegerArrayResultType(int size) {
		this.size = size;
	}

    @TestMethod("testToString")
    public String toString() {
        return "IntegerArrayResultType";
    }

    @TestMethod("testLength")
    public int length() {
    	return size;
    }
}

