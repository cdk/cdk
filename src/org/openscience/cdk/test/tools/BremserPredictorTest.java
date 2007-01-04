/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All I ask is that proper credit is given for my work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk.test.tools;

import java.io.InputStream;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.BremserOneSphereHOSECodePredictor;
import org.openscience.cdk.tools.HOSECodeGenerator;

/**
 * Tests the HOSECode genertor.
 *
 * @cdk.module test-extra
 *
 * @author     steinbeck
 * @cdk.created    2002-11-16
 */
public class BremserPredictorTest extends CDKTestCase
{
	
	static boolean standAlone = false;
	

	/**
	 *  Constructor for the HOSECodeTest object
	 *
	 *@param  name  Description of the Parameter
	 */
	public BremserPredictorTest(String name) {
		super(name);
	}

		/**
	 *  A unit test suite for JUnit
	 *
	 *@return    The test suite
	 */
	public static Test suite()
	{
		return new TestSuite(BremserPredictorTest.class);
	}

	

	/**
	 *  A unit test for JUnit
	 *
	 *@return    Description of the Return Value
	 */
	public void testConstructor()
	{
		BremserOneSphereHOSECodePredictor bp = new BremserOneSphereHOSECodePredictor();
		assertTrue(bp != null);
	}

	/**
	 *  A unit test for JUnit
	 *
	 *@return    Description of the Return Value
	 */
	public void testPrediction()
	{
		String[] data = { 
     "=C(//)",
     "=OCC(//)",
     "CC(//)",
     "CC(//)",
     "CCC(//)",
     "CC(//)",
     "CC(//)",
     "CCC(//)",
     "CCC(//)",
     "CC(//)",
     "CC(//)",
     "CC(//)",
     "CC(//)",
     "CCO(//)",
     "CC(//)",
     "CCO(//)",
     "CCO(//)",
     "CC(//)",
     "O(//)",
     "CC(//)",
     "CCC(//)",
     "CCC(//)",
     "CCC(//)"
		};
		
			double[] result = { 
     112.6,
     198.6,
     29.6,
     29.6,
     40.1,
     29.6,
     29.6,
     40.1,
     40.1,
     29.6,
     29.6,
     29.6,
     29.6,
     73.1,
     29.6,
     73.1,
     73.1,
     29.6,
     54.7,
     29.6,
     40.1,
     40.1,
     40.1
		};
		
		try{
			double prediction;
			BremserOneSphereHOSECodePredictor bp = new BremserOneSphereHOSECodePredictor();
			for (int f = 0; f < data.length; f++)
			{
				prediction = bp.predict(data[f]);
				//logger.debug("\"" + prediction + "\",");
				assertTrue(prediction == result[f]);	
			}
		}catch(org.openscience.cdk.exception.CDKException exc)
		{
			fail("CDKException thrown when trying to predict Shift with BremserOneSphereHOSECodePredictor");	
		}
		
	}

	/**
	 *  A unit test for JUnit
	 *
	 *@return    Description of the Return Value
	 */
	public void testGetConfidenceLimit()
	{
double[] result = { 
     28.5,
     25.7,
     28.5,
     34.9,
     28.5,
     25.7,
     25.4,
     28.5,
     28.5,
     14.8,
     13.3,
     23.0,
     34.9,
     25.7,
     25.7,
     28.5,
     25.7,
     25.7,
     13.3,
     14.4,
     14.4,
     8.9,
     14.8,
     14.8,
     13.3,
     13.3,
     13.3,
     14.4,
     14.4,
     13.3,
     14.4,
     14.4,
     8.9,
     14.8,
     14.8,
     13.3,
     13.3,
     13.3,
     14.4,
     14.4,
     13.3
		};
		Molecule molecule = null;
		try
		{
			String filename = "data/mdl/BremserPredictionTest.mol";
			InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
			MDLReader reader = new MDLReader(ins);
			molecule = (Molecule)reader.read((ChemObject)new Molecule());
			double prediction;
			BremserOneSphereHOSECodePredictor bp = new BremserOneSphereHOSECodePredictor();
			HOSECodeGenerator hcg = new HOSECodeGenerator();
			String s = null;
			removeHydrogens(molecule);
			//logger.debug("Molecule has " + molecule.getAtomCount() + " atoms.");
			for (int f = 0; f < molecule.getAtomCount(); f++)
			{
				s = hcg.getHOSECode(molecule, molecule.getAtom(f), 1);
				prediction = bp.getConfidenceLimit(hcg.makeBremserCompliant(s));
				//logger.debug("\"" + prediction + "\",");
				assertTrue(prediction == result[f]);	
			}
		}
		catch (Exception exc)
		{
			//logger.debug("failure");
			fail("CDKException thrown when trying to predict Shift with BremserOneSphereHOSECodePredictor");	
		}
		
	}

	public void testFailure1()
	{	
		boolean correct = false;
		BremserOneSphereHOSECodePredictor bp = new BremserOneSphereHOSECodePredictor();
		try
		{
			bp.predict("dumb code");
		}
		catch(Exception exc)
		{
			if (exc instanceof org.openscience.cdk.exception.CDKException)
			{
				correct = true;	
			}
		}
		assertTrue(correct);
	}

	public void testFailure2()
	{	
		boolean correct = false;
		BremserOneSphereHOSECodePredictor bp = new BremserOneSphereHOSECodePredictor();
		try
		{
			bp.getConfidenceLimit("dumb code");
		}
		catch(Exception exc)
		{
			if (exc instanceof org.openscience.cdk.exception.CDKException)
			{
				correct = true;	
			}
		}
		assertTrue(correct);
	}

	public void testFailure3()
	{	
		boolean correct = false;
		String test = null;
		BremserOneSphereHOSECodePredictor bp = new BremserOneSphereHOSECodePredictor();
		try
		{
			bp.predict(test);
		}
		catch(Exception exc)
		{
			if (exc instanceof org.openscience.cdk.exception.CDKException)
			{
				correct = true;	
			}
		}
		assertTrue(correct);
	}

	
	private void removeHydrogens(AtomContainer ac)
	{
		org.openscience.cdk.interfaces.IAtom atom = null;
		int f = ac.getAtomCount() - 1;
		
		do{
			atom = ac.getAtom(f);
			if (atom.getSymbol().equals("H"))
			{
				ac.removeAtomAndConnectedElectronContainers(atom);
			}
			f--;
		}
		while(f >= 0);
	}
	
	/**
	 *  The main program for the HOSECodeTest class
	 *
	 *@param  args  The command line arguments
	 */
	public static void main(String[] args)
	{
		standAlone = true;
		BremserPredictorTest bpt = new BremserPredictorTest("BremserPredictorTest");
		//bpt.testConstructor();
		//bpt.testPrediction();
		//bpt.testGetConfidenceLimit();
		//bpt.testFailure1();
		//bpt.testFailure2();
		bpt.testFailure3();
	}
}

