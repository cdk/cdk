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

package org.openscience.cdk.test;


import org.openscience.cdk.*;
import org.openscience.cdk.smiles.*;
import org.openscience.cdk.io.*;
import org.openscience.cdk.tools.*;
import org.openscience.cdk.templates.*;
import org.openscience.cdk.renderer.*;
import org.openscience.cdk.layout.*;
import org.openscience.cdk.structgen.deterministic.*;

import javax.vecmath.*;

import java.util.*;

import java.io.*;
import java.net.URL;
import junit.framework.*;

public class EquivalentClassesDeterministicGeneratorTest extends TestCase
{
	boolean standAlone = false;
	
	public EquivalentClassesDeterministicGeneratorTest(String name)
	{
		super(name);
	}

	public static Test suite()
	{
		return new TestSuite(EquivalentClassesDeterministicGeneratorTest.class);
	}

	public void setStandAlone(boolean standAlone)
	{
		this.standAlone = standAlone;
	}
	
	public void testEquivalentClassesDeterministicGenerator()
	{
		Molecule mol = MoleculeFactory.makeAlphaPinene();
		fixCarbonHCount(mol);
		EquivalentClassesDeterministicGenerator ecdg = new EquivalentClassesDeterministicGenerator();
		ecdg.setAtomContainer(mol);
		
		assertTrue(1==1);
	}

	private void fixCarbonHCount(Molecule mol)
	{	
		/* the following line are just a quick fix for this
		   particluar carbon-only molecule until we have a proper 
		   hydrogen count configurator
		 */
		int bondCount = 0;
		Atom atom;
		 for (int f = 0; f < mol.getAtomCount(); f++)
		{
			atom = mol.getAtomAt(f);
			bondCount =  mol.getBondOrderSum(atom);
			atom.setHydrogenCount(4 - bondCount - (int)atom.getCharge());
			if (standAlone) System.out.println("Hydrogen count for atom " + f + ": " + atom.getHydrogenCount());
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
		EquivalentClassesDeterministicGeneratorTest ecdgt = new EquivalentClassesDeterministicGeneratorTest("EquivalentClassesDeterministicGeneratorTest");
		ecdgt.setStandAlone(true);
		ecdgt.testEquivalentClassesDeterministicGenerator();
	}	
}

