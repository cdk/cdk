/*
 * $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2007  The Chemistry Development Kit (CKD) project
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All I ask is that proper credit is given for my work, which includes
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
 * 
 */

package org.openscience.cdk.test.fingerprint;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.fingerprint.StandardSubstructureSets;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.test.CDKTestCase;

/**
 * @cdk.module test-extra
 */
public class StandardSubstructureSetsTest extends CDKTestCase {
	
	public StandardSubstructureSetsTest(String name) {
		super(name);
	}
	
	public static Test suite() {
		return new TestSuite(StandardSubstructureSetsTest.class);
	}

	public void testGetFunctionalGroupSubstructureSet() {
		IAtomContainerSet set = null;
		try {
			set = StandardSubstructureSets.getFunctionalGroupSubstructureSet();

			assertNotNull(set);
			assertEquals(8, set.getAtomContainerCount());
		} catch (Exception exception) {
			exception.printStackTrace();
			fail(exception.getMessage());
		}

	}
	
}

