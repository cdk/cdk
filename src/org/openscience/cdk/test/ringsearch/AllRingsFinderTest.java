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
 */
package org.openscience.cdk.test.ringsearch;

import org.openscience.cdk.*;
import org.openscience.cdk.ringsearch.*;
import org.openscience.cdk.io.*;
import org.openscience.cdk.tools.*;
import org.openscience.cdk.templates.*;
import org.openscience.cdk.renderer.*;
import org.openscience.cdk.layout.*;
import javax.vecmath.*;

import java.util.*;

import java.io.*;
import java.net.URL;
import junit.framework.*;

public class AllRingsFinderTest extends TestCase
{
	boolean standAlone = false;
	
	public AllRingsFinderTest(String name)
	{
		super(name);
	}

	public static Test suite() {
		return new TestSuite(AllRingsFinderTest.class);
	}

	public void setStandAlone(boolean standAlone)
	{
		this.standAlone = standAlone;
	}
	
	public void testAllRingsFinder()
	{
		RingSet ringSet = null;
		AllRingsFinder arf = new AllRingsFinder();
		if (standAlone) arf.debug = true;
		Molecule molecule = MoleculeFactory.makeEthylPropylPhenantren();
		//display(molecule);
		try
		{
			ringSet = arf.findAllRings(molecule);
		}
		catch(Exception exc)
		{
			System.out.println(exc);	
		}

		assertTrue(ringSet.size() == 6); 
	}
	
	
	// Bug #746067
	public void testBondsWithinRing()
	{
		RingSet ringSet = null;
		AllRingsFinder arf = new AllRingsFinder();
		if (standAlone) arf.debug = true;
		Molecule molecule = MoleculeFactory.makeEthylPropylPhenantren();
		//display(molecule);
		try
		{
			ringSet = arf.findAllRings(molecule);
			for (int i = 0; i < ringSet.size(); i++) 
			{
				Ring ring = (Ring)ringSet.elementAt(i);
				for (int j = 0; j < ring.getElectronContainerCount(); j++) 
				{
					ElectronContainer ec = ring.getElectronContainerAt(j);
					if (ec instanceof Bond)
					{
						Atom atom1 = ((Bond)ec).getAtomAt(0);
						Atom atom2 = ((Bond)ec).getAtomAt(1);
						assertTrue(ring.contains(atom1) && ring.contains(atom2));
					}
				}
			}
		
		}
		catch(Exception exc)
		{
			System.out.println(exc);
		}
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
		AllRingsFinderTest arft = new AllRingsFinderTest("AllRingsFinderTest");
		arft.setStandAlone(true);
		arft.testAllRingsFinder();
	}	
}

