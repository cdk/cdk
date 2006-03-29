/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2003-2006  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
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
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.test.ringsearch;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.ringsearch.RingPartitioner;
import org.openscience.cdk.ringsearch.SSSRFinder;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.LoggingTool;

/**
 * This class tests the RingPartitioner class.
 *
 * @cdk.module test-extra
 *
 * @author         kaihartmann
 * @cdk.created    2005-05-24
 */
public class RingPartitionerTest extends CDKTestCase
{

	static boolean standAlone = false;
	private LoggingTool logger = null;


	/**
	 *  Constructor for the RingPartitionerTest object
	 *
	 *@param  name  Description of the Parameter
	 */
	public RingPartitionerTest(String name)
	{
		super(name);
	}


	/**
	 *  The JUnit setup method
	 */
	public void setUp() throws Exception {
        super.setUp();
		logger = new LoggingTool(this);
	}


	/**
	 *  A unit test suite for JUnit
	 *
	 *@return    The test suite
	 */
	public static Test suite()
	{
		return new TestSuite(RingPartitionerTest.class);
	}


	/**
	 *  A unit test for JUnit
	 */
	public void testConvertToAtomContainer()
	{
		IMolecule molecule = MoleculeFactory.makeAlphaPinene();
		SSSRFinder sssrf = new SSSRFinder(molecule);

		IRingSet ringSet = sssrf.findSSSR();
		IAtomContainer ac = RingPartitioner.convertToAtomContainer(ringSet);
        assertEquals(7, ac.getAtomCount());
        assertEquals(8, ac.getBondCount());
	}
	
	/**
	 *  The main program for the RingPartitionerTest class
	 *
	 *@param  args  The command line arguments
	 */
	public static void main(String[] args)
	{
		RingPartitionerTest rpt = new RingPartitionerTest("RingPartitionerTest");
		standAlone = true;
        try {
            rpt.setUp();
            rpt.testConvertToAtomContainer();
        } catch(Exception exc) {
            System.err.println("Could setup the TestCase");
        }
	}
	
}

