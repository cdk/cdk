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
 * Checks the funcitonality of the IsotopeFactory
 */
 
public class IsotopeFactoryTest extends TestCase
{
	public IsotopeFactoryTest(String name)
	{
		super(name);
	}
	
	public void setUp()
	{

	}
	
	public static Test suite() 
	{
		return new TestSuite(IsotopeFactoryTest.class);
	}

	public void testIsotopeFactory()
	{
		Isotope isotope = null;
		IsotopeFactory isofac = null;
		try
		{
			isofac = new IsotopeFactory();
		}
		catch(Exception exc)
		{
			throw new AssertionFailedError("Problem instantiating IsotopeFactory: " +  exc.toString());
		}
		
		assertTrue(isofac.getSize() > 0);
		
		try
		{
			isotope = isofac.getMajorIsotope("Te");
		}
		catch(Exception exc)
		{
			throw new AssertionFailedError("Problem getting isotope 'Te' from IsotopeFactory: "  +  exc.toString());
		}
		
		assertTrue(isotope.getExactMass() == 129.906229);

		try
		{
			isotope = isofac.getMajorIsotope(17);
		}
		catch(Exception exc)
		{
			throw new AssertionFailedError("Problem getting Isotope 'Cl' from IsotopeFactory by atomicNumber 17: "  +  exc.toString());
		}
		
		assertTrue(isotope.getSymbol().equals("Cl"));
		
		
	}
}
