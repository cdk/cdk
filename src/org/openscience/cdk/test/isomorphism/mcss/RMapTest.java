/* $Revision: 7691 $ $Author: egonw $ $Date: 2007-01-11 12:47:48 +0100 (Thu, 11 Jan 2007) $
 * 
 * Copyright (C) 2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.test.isomorphism.mcss;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.isomorphism.mcss.RMap;
import org.openscience.cdk.test.CDKTestCase;

/**
 * @cdk.module test-standard
 */
public class RMapTest extends CDKTestCase {
    
    public RMapTest(String name) {
        super(name);
    }
    
	public static Test suite() {
		return new TestSuite(RMapTest.class);
	}

	public void testRMap_int_int() {
		RMap node = new RMap(1,2);
		assertNotNull(node);
		assertEquals(1, node.getId1());
		assertEquals(2, node.getId2());
	}

}


