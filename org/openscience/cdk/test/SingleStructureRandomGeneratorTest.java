/* 
 * 
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

import org.openscience.cdk.controller.*;
import org.openscience.cdk.*;
import org.openscience.cdk.io.*;
import org.openscience.cdk.layout.*;
import org.openscience.cdk.renderer.*;
import org.openscience.cdk.structgen.*;
import org.openscience.cdk.tools.*;
import java.util.*;
import java.io.*;
import java.net.URL;
import javax.vecmath.Vector2d;

public class SingleStructureRandomGeneratorTest
{
	MoleculeListViewer moleculeListViewer = null;
	
	public SingleStructureRandomGeneratorTest() throws Exception
	{
		System.out.println("Instantiating MoleculeListViewer");
		moleculeListViewer = new MoleculeListViewer();
		System.out.println("Instantiating SingleStructureRandomGenerator");
		SingleStructureRandomGenerator ssrg = new SingleStructureRandomGenerator();
		System.out.println("Assining unbonded set of atoms");
		AtomContainer ac = getBunchOfUnbondedAtoms();
		String mf = new MFAnalyser(ac).getMolecularFormula();
		System.out.println("Molecular Formula is: " + mf);
		ssrg.setAtomContainer(ac);
		System.out.println("Generating a random structure");
		ac = ssrg.generate();
		System.out.println("Showing the structure");
		showIt((Molecule)ac, "A randomly generated molecule for " + mf);
	}


	private boolean showIt(Molecule molecule, String name)
	{
		StructureDiagramGenerator sdg = new StructureDiagramGenerator();
		MoleculeViewer2D mv = new MoleculeViewer2D();
		try
		{
			sdg.setMolecule((Molecule)molecule.clone());
			sdg.generateCoordinates(new Vector2d(0,1));
			mv.setAtomContainer(sdg.getMolecule());
			moleculeListViewer.addStructure(mv, name);
		}
		catch(Exception exc)
		{
			System.out.println("*** Exit due to an unexpected error during coordinate generation ***");
			exc.printStackTrace();
			return false;
		}
		return true;
	}

	private AtomContainer getBunchOfUnbondedAtoms()
	{
		Molecule molecule = MoleculeFactory.makeAlphaPinene();
		molecule.removeAllBonds();
		int[] hcounts = {1,0,1,2,1,2,1,0,3,3};
		for (int f = 0; f < hcounts.length; f++)
		{
			molecule.getAtomAt(f).setHydrogenCount(hcounts[f]);
		}
		return (AtomContainer)molecule;
	}
	
	public static void main(String[] args)
	{
		System.out.println("Yes!");
		try
		{
			new SingleStructureRandomGeneratorTest();
		}
		catch(Exception exc)
		{
			System.out.println("SingleStructureRandomGeneratorTest failed: ");
			exc.printStackTrace();
		}
		System.out.println("Done");
		
	}
}

