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
package org.openscience.cdk.test.geometry;

import javax.vecmath.Point3d;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.geometry.ZMatrixTools;
import org.openscience.cdk.test.CDKTestCase;

/**
 * @cdk.module test-io
 */
public class ZMatrixToolsTest extends CDKTestCase {
    
    public ZMatrixToolsTest(String name) {
        super(name);
    }
    
	public static Test suite() {
		return new TestSuite(ZMatrixToolsTest.class);
	}

	public void testZmatrixToCartesian_arraydouble_arrayint_arraydouble_arrayint_arraydouble_arrayint() {
		// acetaldehyde example from http://www.shodor.org/chemviz/zmatrices/babelex.html
		int[]  first_atoms = { 0, 0,    0,    0,    3,    3,     3   };
		double[] distances = { 0, 1.2,  1.1,  1.5,  1.1,  1.1,   1.1 };
		int[] second_atoms = { 0, 0,    1,    1,    0,    0,     0   };
		double[]    angles = { 0, 0,  120,  120,  110,  110,   110   };
		int[]  third_atoms = { 0, 0,    0,    2,    1,    1,     1   };
		double[] dihedrals = { 0, 0,    0,  180,    0,  120,  -120   };
		Point3d points[] = ZMatrixTools.zmatrixToCartesian(distances,
				first_atoms, angles, second_atoms, dihedrals, third_atoms);
		assertEquals(-0.5500, points[2].x, 0.0001);
		assertEquals(-1.3664, points[5].y, 0.0001);
		assertEquals(-0.8952, points[6].z, 0.0001);
	}

}


