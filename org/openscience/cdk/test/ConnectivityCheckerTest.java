/* ConnectivityCheckerTest.java
 *
 * $RCSfile$    $Author$    $Date$    $Revision$
 * 
 * Copyright (C) 1997-2000  The CompChem project
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
import java.util.*;
import junit.framework.*;

/**
 * Checks the funcitonality of the ConnectivityChecker
 */
 
public class ConnectivityCheckerTest extends TestCase
{
	AtomContainer atomCon = null;
	Vector molecules = null;
	ConnectivityChecker cc = null;

	public ConnectivityCheckerTest(String name)
	{
		super(name);
	}
	
	public void setUp()
	{
		atomCon = new AtomContainer();
		molecules = new Vector();
		cc = new ConnectivityChecker();
	}
	
	public static Test suite() {
		return new TestSuite(ConnectivityCheckerTest.class);
	}

	public void testPartitioning()
	{
		System.out.println(atomCon);
		atomCon.add(MoleculeFactory.make4x3CondensedRings());
		atomCon.add(MoleculeFactory.makeAlphaPinene());
		atomCon.add(MoleculeFactory.makeSpiroRings());
		try
		{
			molecules = cc.partitionIntoMolecules(atomCon);
		}
		catch(Exception exc)
		{
			// Vector molecules is empty but initialized
			// so no need for any action here
		}
		assert(molecules.size() == 3);	
	}
}