/* ControllerTest.java
 * 
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
 */

package org.openscience.cdk.test;

import org.openscience.cdk.controller.*;
import org.openscience.cdk.*;
import org.openscience.cdk.io.*;
import org.openscience.cdk.layout.*;
import org.openscience.cdk.renderer.*;
import java.util.*;
import java.io.*;
import java.net.URL;
import javax.vecmath.Vector2d;


public class ControllerTest
{
	MDLReader mr;
	ChemFile chemFile;
	ChemSequence chemSequence;
	ChemModel chemModel;
	SetOfMolecules setOfMolecules;
	Molecule molecule;
	CDKInputAdapter inputAdapter;
	
	public ControllerTest()
	{
//		molecule = buildFusedRings();
//		molecule = buildMolecule4x3();
//		molecule = buildMolecule2x3();
//		molecule = buildMolecule2x4();
//		molecule = buildSpiroRings();
		molecule = loadMolecule("data/reserpine.mol");
//		molecule = buildRing();
//		molecule = new Molecule();

//
//		StructureDiagramGenerator sdg = new StructureDiagramGenerator();
//		sdg.setMolecule(molecule);
//		try
//		{
//			sdg.generateCoordinates(new Vector2d(0,1));
//		}
//		catch(Exception exc)
//		{
//			System.out.println("*** Exit due to an unexpected error during coordinate generation ***");
//			exc.printStackTrace();
//			System.exit(1);
//		}
//		molecule = sdg.getMolecule();
//

		Renderer2DModel r2dm = new Renderer2DModel();
		MoleculeViewer2D mv = new MoleculeViewer2D(molecule, r2dm);
//		r2dm.setDrawNumbers(true);
		mv.display();
		JCPController2DModel c2dm = new JCPController2DModel();
		inputAdapter = new CDKInputAdapter(molecule, r2dm, c2dm);
		c2dm.setDrawMode(JCPController2DModel.DRAWBOND);
		mv.addMouseMotionListener(inputAdapter);
		mv.addMouseListener(inputAdapter);
		mv.addKeyListener(inputAdapter);
	
	}

	/**
	 * The main method.
	 *
	 * @param   args    The Arguments from the commandline
	 */
	public static void main(String[] args)
	{
		new ControllerTest();
	}
	
	/* build a molecule from 4 condensed triangles */
	
	Molecule buildMolecule4x3()
	{
		Molecule mol = new Molecule();
		mol.addAtom(new Atom("C")); // 0
		mol.addAtom(new Atom("C")); // 1
		mol.addAtom(new Atom("C")); // 2
		mol.addAtom(new Atom("C")); // 3
		mol.addAtom(new Atom("C")); // 4
		mol.addAtom(new Atom("C")); // 5
		mol.addAtom(new Atom("C")); // 6		
		
		mol.addBond(0, 1, 1); // 1
		mol.addBond(1, 2, 1); // 2
		mol.addBond(2, 3, 1); // 3
		mol.addBond(3, 4, 1); // 4
		mol.addBond(4, 5, 1); // 5
		mol.addBond(5, 6, 1); // 6
		mol.addBond(2, 0, 1); // 7
		mol.addBond(1, 3, 1); // 8
		mol.addBond(4, 2, 1); // 9
		mol.addBond(6, 4, 1); // 9		
		
		return mol;
	}
	
	Molecule buildMolecule2x3()
	{
		Molecule mol = new Molecule();
		mol.addAtom(new Atom("C")); // 0
		mol.addAtom(new Atom("C")); // 1
		mol.addAtom(new Atom("C")); // 2
		mol.addAtom(new Atom("C")); // 3
		
		mol.addBond(0, 1, 1); // 1
		mol.addBond(1, 2, 1); // 2
		mol.addBond(2, 0, 1); // 3
		mol.addBond(2, 3, 1); // 7
		mol.addBond(1, 3, 1); // 8
		return mol;
	}
	

	Molecule buildMolecule2x4()
	{
		Molecule mol = new Molecule();
		mol.addAtom(new Atom("C")); // 0
		mol.addAtom(new Atom("C")); // 1
		mol.addAtom(new Atom("C")); // 2
		mol.addAtom(new Atom("C")); // 3
		mol.addAtom(new Atom("C")); // 2
		mol.addAtom(new Atom("C")); // 3
		
		mol.addBond(0, 1, 1); // 1
		mol.addBond(1, 2, 1); // 2
		mol.addBond(2, 3, 1); // 3
		mol.addBond(3, 0, 1); // 7
		mol.addBond(1, 4, 1); // 8
		mol.addBond(4, 5, 1); // 7
		mol.addBond(5, 2, 1); // 8
		return mol;
	}

	
	Molecule buildFusedRings()
	{
		Molecule mol = new Molecule();
		mol.addAtom(new Atom("C")); // 0
		mol.addAtom(new Atom("C")); // 1
		mol.addAtom(new Atom("C")); // 2
		mol.addAtom(new Atom("C")); // 3
		mol.addAtom(new Atom("C")); // 4
		mol.addAtom(new Atom("C")); // 5
		mol.addAtom(new Atom("C")); // 6
		mol.addAtom(new Atom("C")); // 7
		mol.addAtom(new Atom("C")); // 8
		mol.addAtom(new Atom("C")); // 9
		
		mol.addBond(0, 1, 1); // 1
		mol.addBond(1, 2, 1); // 2
		mol.addBond(2, 3, 1); // 3
		mol.addBond(3, 4, 1); // 4
		mol.addBond(4, 5, 1); // 5
		mol.addBond(5, 0, 1); // 6
		mol.addBond(5, 6, 1); // 7
		mol.addBond(6, 7, 1); // 8
		mol.addBond(7, 4, 1); // 9
		mol.addBond(8, 0, 1); // 10
		mol.addBond(9, 1, 1); // 11		
		mol.addBond(9, 8, 1); // 11		
		
			
		return mol;
	}

	Molecule buildSpiroRings()
	{
		Molecule mol = new Molecule();
		mol.addAtom(new Atom("C")); // 0
		mol.addAtom(new Atom("C")); // 1
		mol.addAtom(new Atom("C")); // 2
		mol.addAtom(new Atom("C")); // 3
		mol.addAtom(new Atom("C")); // 4
		mol.addAtom(new Atom("C")); // 5
		mol.addAtom(new Atom("C")); // 6
		mol.addAtom(new Atom("C")); // 7
		mol.addAtom(new Atom("C")); // 8
		mol.addAtom(new Atom("C")); // 9

		
		
		mol.addBond(0, 1, 1); // 1
		mol.addBond(1, 2, 1); // 2
		mol.addBond(2, 3, 1); // 3
		mol.addBond(3, 4, 1); // 4
		mol.addBond(4, 5, 1); // 5
		mol.addBond(5, 6, 1); // 6
		mol.addBond(6, 0, 1); // 7
		mol.addBond(6, 7, 1); // 8
		mol.addBond(7, 8, 1); // 9
		mol.addBond(8, 9, 1); // 10
		mol.addBond(9, 6, 1); // 11				
		return mol;
	}
	
	
	Molecule buildRing()
	{
		Molecule mol = new Molecule();
		mol.addAtom(new Atom("C")); // 0
		mol.addAtom(new Atom("C")); // 1
		mol.addAtom(new Atom("C")); // 2
		mol.addAtom(new Atom("C")); // 3
		mol.addAtom(new Atom("C")); // 4
		mol.addAtom(new Atom("C")); // 5
//		mol.addAtom(new Atom("C")); // 6
//		mol.addAtom(new Atom("C")); // 7
//		mol.addAtom(new Atom("C")); // 8
//		mol.addAtom(new Atom("C")); // 9
		
		mol.addBond(0, 1, 1); // 1
		mol.addBond(1, 2, 1); // 2
		mol.addBond(2, 3, 1); // 3
		mol.addBond(3, 4, 1); // 4
		mol.addBond(4, 5, 1); // 5
		mol.addBond(5, 0, 1); // 6
//		mol.addBond(5, 6, 1); // 7
//		mol.addBond(6, 7, 1); // 8
//		mol.addBond(7, 4, 1); // 9
//		mol.addBond(8, 0, 1); // 10
//		mol.addBond(9, 1, 1); // 11		
		
			
		return mol;
	}
	

	
	Molecule loadMolecule(String inFile)
	{
		try
		{
			FileInputStream fis = new FileInputStream(inFile);
			mr = new MDLReader(fis);
			chemFile = (ChemFile)mr.read((ChemObject)new ChemFile());
			fis.close();
			chemSequence = chemFile.getChemSequence(0);
			chemModel = chemSequence.getChemModel(0);
			setOfMolecules = chemModel.getSetOfMolecules();
			molecule = setOfMolecules.getMolecule(0);
		}
		catch(Exception exc)
		{
			exc.printStackTrace();		
		}
//		for (int i = 0; i < molecule.getAtomCount(); i++)
//		{
//			molecule.getAtomAt(i).setPoint2D(null);
//		}
		return molecule;
	}
}

