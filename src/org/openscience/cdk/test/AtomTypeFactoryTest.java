/*
  * $RCSfile$    $Author$    $Date$    $Revision$
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
import java.util.*;
import junit.framework.*;

/**
 * Checks the funcitonality of the AtomTypeFactory
 */
 
public class AtomTypeFactoryTest extends TestCase
{
	public AtomTypeFactoryTest(String name)
	{
		super(name);
	}
	
	public void setUp()
	{

	}
	
	public static Test suite() {
		return new TestSuite(AtomTypeFactoryTest.class);
	}

	public void testAtomTypeFactory()
	{
		AtomType atomType = null;
		AtomTypeFactory atfac = null;
		try
		{
			atfac = new AtomTypeFactory();
		}
		catch(Exception exc)
		{
			throw new AssertionFailedError("Problem instantiating AtomTypeFactory: " +  exc.toString());
		}
		
		assertTrue(atfac.getSize() > 0);
		
		try
		{
			atomType = atfac.getAtomType("structgen.C4");
		}
		catch(Exception exc)
		{
			throw new AssertionFailedError("Problem getting AtomType for 'structgen.C4' from AtomTypeFactory: "  +  exc.toString());
		}
		
		assertTrue(atomType.getMaxBondOrderSum() == 4.0);
	}
}
