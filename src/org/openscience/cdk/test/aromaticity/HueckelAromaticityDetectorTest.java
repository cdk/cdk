/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2002  The Chemistry Development Kit (CDK) project
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
 */
package org.openscience.cdk.test.aromaticity;

import org.openscience.cdk.aromaticity.*;
import org.openscience.cdk.*;
import org.openscience.cdk.smiles.*;
import org.openscience.cdk.io.*;
import org.openscience.cdk.tools.*;
import org.openscience.cdk.renderer.*;
import org.openscience.cdk.layout.*;
import org.openscience.cdk.templates.*;
import org.openscience.cdk.ringsearch.*;

import javax.vecmath.*;

import java.util.*;

import java.io.*;
import java.net.URL;
import junit.framework.*;

/**
 *  Description of the Class
 *
 *@author     steinbeck
 *@created    October 6, 2002
 */
public class HueckelAromaticityDetectorTest extends TestCase
{
	boolean standAlone = false;


	/**
	 *  Constructor for the HueckelAromaticityDetectorTest object
	 *
	 *@param  name  Description of the Parameter
	 */
	public HueckelAromaticityDetectorTest(String name)
	{
		super(name);
	}


	/**
	 *  A unit test suite for JUnit
	 *
	 *@return    The test suite
	 */
	public static Test suite()
	{
		return new TestSuite(HueckelAromaticityDetectorTest.class);
	}


	/**
	 *  Sets the standAlone attribute of the HueckelAromaticityDetectorTest object
	 *
	 *@param  standAlone  The new standAlone value
	 */
	public void setStandAlone(boolean standAlone)
	{
		this.standAlone = standAlone;
	}


	/**
	 *  A unit test for JUnit
	 */
	public void testAzulene()
	{
		Molecule mol = MoleculeFactory.makeAzulene();
		display(mol
				);
		System.out.println("Testing Azulene");
		boolean isAromatic = false;
		try
		{
			isAromatic = HueckelAromaticityDetector.detectAromaticity(mol);
		} catch (Exception exc)
		{
			if (standAlone)
			{
				exc.printStackTrace();
			}
		}
		if (standAlone && isAromatic)
		{
			System.out.println("Azulene is aromatic");
		}
		assertTrue(isAromatic == true);

	}


	/**
	 *  A unit test for JUnit
	 */
	public void testIndole()
	{
		Molecule mol = MoleculeFactory.makeIndole();
		display(mol);
		System.out.println("Testing Indole");
		boolean isAromatic = false;
		try
		{
			isAromatic = HueckelAromaticityDetector.detectAromaticity(mol);
		} catch (Exception exc)
		{
			if (standAlone)
			{
				exc.printStackTrace();
			}
		}
		if (standAlone && isAromatic)
		{
			System.out.println("Indole is aromatic");
		}
		assertTrue(isAromatic == true);

	}


	/**
	 *  A unit test for JUnit
	 */
	public void testPyrrole()
	{
		Molecule mol = MoleculeFactory.makePyrrole();
		display(mol);
		System.out.println("Testing Pyrrole");
		boolean isAromatic = false;
		try
		{
			isAromatic = HueckelAromaticityDetector.detectAromaticity(mol);
		} catch (Exception exc)
		{
			if (standAlone)
			{
				exc.printStackTrace();
			}
		}
		if (standAlone && isAromatic)
		{
			System.out.println("Pyrrole is aromatic");
		}
		assertTrue(isAromatic == true);
	}


	/**
	 *  A unit test for JUnit
	 */
	public void testBenzeneFromSMILES()
	{
		boolean isAromatic = false;
		try
		{

			SmilesParser sp = new SmilesParser();

			Molecule mol = sp.parseSmiles("C1CCCc2c1cccc2");
			StructureDiagramGenerator sdg = new
					StructureDiagramGenerator();
			sdg.setMolecule((Molecule) mol.clone());
			sdg.generateCoordinates(new Vector2d(0, 1));
			mol = sdg.getMolecule();
			RingSet rs = (new SSSRFinder()).findSSSR(mol);
			Iterator iter = rs.iterator();
			Ring r = null;
			int i = 0;
			while (iter.hasNext())
			{
				r = (Ring) iter.next();
				i++;
				isAromatic = HueckelAromaticityDetector.isAromatic(mol, rs, r);
				if (standAlone && isAromatic)
				{
					System.out.println("Ring " + i + " in test molecule is aromatic.");
				}
				else if (standAlone && !isAromatic)
				{
					System.out.println("Ring " + i + " in test molecule is not aromatic.");
				}
			}
		} catch (Exception exc)
		{
			if (standAlone)
			{
				exc.printStackTrace();
			}
		}
		assertTrue(isAromatic == true);
	}


	/**
	 *  Description of the Method
	 *
	 *@param  molecule  Description of the Parameter
	 */
	private void display(Molecule molecule)
	{
		StructureDiagramGenerator sdg = new StructureDiagramGenerator();
		MoleculeViewer2D mv = new MoleculeViewer2D();
		Renderer2DModel r2dm = mv.getRenderer2DModel();
		r2dm.setDrawNumbers(true);

		try
		{
			sdg.setMolecule((Molecule) molecule.clone());
			sdg.generateCoordinates(new Vector2d(0, 1));
			mv.setAtomContainer(sdg.getMolecule());
			mv.display();
		} catch (Exception exc)
		{
			System.out.println("*** Exit due to an unexpected error during coordinate generation ***");
			exc.printStackTrace();
		}
	}


	/**
	 *  The main program for the HueckelAromaticityDetectorTest class
	 *
	 *@param  args  The command line arguments
	 */
	public static void main(String[] args)
	{
		HueckelAromaticityDetectorTest hadt = new HueckelAromaticityDetectorTest("HueckelAromaticityDetectorTest");
		hadt.setStandAlone(true);
		//hadt.testBenzene();
		//hadt.testAlphaPinene();
		//hadt.testAzulene();
		hadt.testBenzeneFromSMILES();
		hadt.testIndole();
		hadt.testPyrrole();
		//hadt.testPorphyrine();
	}
}

