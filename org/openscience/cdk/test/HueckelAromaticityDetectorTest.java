/*
 * $RCSfile$    $Author$    $Date$    $Revision$
 * 
 * Copyright (C) 1997-2001  The Chemistry Development Kit (CDK) project
 * 
 * Contact: steinbeck@ice.mpg.de, geelter@maul.chem.nd.edu, egonw@sci.kun.nl
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
 */

package org.openscience.cdk.test;



import org.openscience.cdk.aromaticity.*;
import org.openscience.cdk.*;
import org.openscience.cdk.smiles.*;
import org.openscience.cdk.io.*;
import org.openscience.cdk.tools.*;
import org.openscience.cdk.renderer.*;
import org.openscience.cdk.layout.*;

import javax.vecmath.*;

import java.util.*;

import java.io.*;
import java.net.URL;
import junit.framework.*;

public class HueckelAromaticityDetectorTest extends TestCase
{
	boolean standAlone = false;
	
	public HueckelAromaticityDetectorTest(String name)
	{
		super(name);
	}

	public static Test suite() {
		return new TestSuite(HueckelAromaticityDetectorTest.class);
	}

	public void setStandAlone(boolean standAlone)
	{
		this.standAlone = standAlone;
	}
	
	public void testBenzene()
	{
		Molecule mol = MoleculeFactory.makeBenzene();
		System.out.println("Testing benzene");
		boolean isAromatic = false;
		try
		{
			isAromatic = HueckelAromaticityDetector.detectAromaticity(mol);
		}
		catch(Exception exc)
		{
			if (standAlone)
			{
				exc.printStackTrace();	
			}
		}
		if (standAlone && isAromatic)
		{
			System.out.println("Benzene is aromatic");
		}
		assertTrue(isAromatic == true);
	}	

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
		}
		catch(Exception exc)
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


	public void testIndole()
	{
		Molecule mol = MoleculeFactory.makeIndole();
		display(mol);
		System.out.println("Testing Indole");
		boolean isAromatic = false;
		try
		{
			//isAromatic = HueckelAromaticityDetector.detectAromaticity(mol);
		}
		catch(Exception exc)
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
	
	public void testAlphaPinene()
	{
	
	}	


	public void testPorphyrine()
	{
	
	}	

	
	private void display(Molecule molecule)
	{	
		StructureDiagramGenerator sdg = new StructureDiagramGenerator();
		MoleculeViewer2D mv = new MoleculeViewer2D();
		Renderer2DModel r2dm = mv.getRenderer2DModel();
		r2dm.setDrawNumbers(true);
		
		try
		{
			sdg.setMolecule((Molecule)molecule.clone());
			sdg.generateCoordinates(new Vector2d(0,1));
			mv.setAtomContainer(sdg.getMolecule());
			mv.display();
		}
		catch(Exception exc)
		{
			System.out.println("*** Exit due to an unexpected error during coordinate generation ***");
			exc.printStackTrace();
		}
	}

	
	public static void main(String[] args)
	{
		HueckelAromaticityDetectorTest hadt = new HueckelAromaticityDetectorTest("HueckelAromaticityDetectorTest");
		hadt.setStandAlone(true);
		hadt.testBenzene();
		//hadt.testAlphaPinene();
		hadt.testAzulene();
		//hadt.testIndole();
		//hadt.testPorphyrine();
	}	
}

