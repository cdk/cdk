/*
 * $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 *
 * Copyright (C) 1997-2002  The Chemistry Development Kit (CDK) project
 *
 * Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */

package org.openscience.cdk.test;

import org.openscience.cdk.*;
import org.openscience.cdk.tools.*;
import org.openscience.cdk.templates.*;
import java.util.*;
import junit.framework.*;

/**
 *  Checks the functionality of the ConnectivityChecker
 *
 * @author     steinbeck
 * @created    July 24, 2001
 */

public class ConnectivityCheckerTest extends TestCase {
	AtomContainer atomCon = null;
	Vector molecules = null;
	ConnectivityChecker cc = null;


	/**
	 *  Constructor for the ConnectivityCheckerTest object
	 *
	 * @param  name  A Name of the test
	 */
	public ConnectivityCheckerTest(String name) {
		super(name);
	}


	/**
	 *  The JUnit setup method
	 */
	public void setUp() {
		atomCon = new AtomContainer();
		molecules = new Vector();
		cc = new ConnectivityChecker();
	}


	/**
	 *  A unit test for JUnit
	 */
	public void testPartitioning() {
		System.out.println(atomCon);
		atomCon.add(MoleculeFactory.make4x3CondensedRings());
		atomCon.add(MoleculeFactory.makeAlphaPinene());
		atomCon.add(MoleculeFactory.makeSpiroRings());
		try {
			molecules = cc.partitionIntoMolecules(atomCon);
		}
		catch (Exception exc) {
			// Vector molecules is empty but initialized
			// so no need for any action here
		}
		assertTrue(molecules.size() == 3);
	}


	/**
	 *  A unit test suite for JUnit
	 *
	 * @return    The test suite
	 */
	public static Test suite() {
		return new TestSuite(ConnectivityCheckerTest.class);
	}
}

