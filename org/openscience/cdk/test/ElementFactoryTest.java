/*
 *  $RCSfile$    $Author$    $Date$    $Revision$
 *
 *  Copyright (C) 1997-2001  The Chemistry Development Kit (CDK) project
 *
 *  Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.openscience.cdk.test;

import org.openscience.cdk.*;
import org.openscience.cdk.tools.*;
import java.util.*;
import junit.framework.*;

/**
 *  Checks the functionality of the ElementFactory
 *
 * @author     steinbeck
 * @created    October 17, 2001
 */

public class ElementFactoryTest extends TestCase
{
	/**
	 *  Constructor for the ElementFactoryTest object
	 *
	 * @param  name  Description of Parameter
	 */
	public ElementFactoryTest(String name)
	{
		super(name);
	}


	/**
	 *  The JUnit setup method
	 */
	public void setUp() { }


	/**
	 *  A unit test for JUnit
	 */
	public void testElementFactory()
	{
		Element element = null;
		ElementFactory elfac = null;
		String findThis = "Br";
		try
		{
			elfac = new ElementFactory();
		}
		catch (Exception exc)
		{
			throw new AssertionFailedError("Problem instantiating ElementFactory: " + exc.toString());
		}

		assert(elfac.getSize() > 0);

		try
		{
			element = elfac.getElement(findThis);
		}
		catch (Exception exc)
		{
			throw new AssertionFailedError("Problem getting isotope " + findThis + " from ElementFactory: " + exc.toString());
		}

		assert(element.getAtomicNumber() == 35);
	}

	/**
	 *  A unit test suite for JUnit
	 *
	 * @return    The test suite
	 */
	public static Test suite()
	{
		return new TestSuite(ElementFactoryTest.class);
	}
}

