/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 1997-2005  The Chemistry Development Kit (CDK) project
 * 
 * Contact: cdk-devel@lists.sourceforge.net
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA. 
 */

package org.openscience.cdk.test.controller;

import java.io.FileInputStream;

import org.openscience.cdk.Atom;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.SetOfMolecules;
import org.openscience.cdk.applications.swing.MoleculeViewer2D;
import org.openscience.cdk.controller.Controller2DModel;
import org.openscience.cdk.controller.PopupController2D;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.renderer.Renderer2DModel;

/**
 * @cdk.module test
 */
public class ControllerTest
{
	MDLReader mr;
	ChemFile chemFile;
	org.openscience.cdk.interfaces.ChemSequence chemSequence;
	org.openscience.cdk.interfaces.IChemModel chemModel;
	org.openscience.cdk.interfaces.SetOfMolecules setOfMolecules;
	org.openscience.cdk.interfaces.Molecule molecule;
	PopupController2D inputAdapter;
	
	public ControllerTest()
	{
//		molecule = buildFusedRings();
//		molecule = buildMolecule4x3();
//		molecule = buildMolecule2x3();
//		molecule = buildMolecule2x4();
//		molecule = buildSpiroRings();
		molecule = loadMolecule("data/mdl/reserpine.mol");
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
        ChemModel model = new ChemModel();
        SetOfMolecules moleculeSet = new SetOfMolecules();
        moleculeSet.addMolecule(molecule);
        model.setSetOfMolecules(moleculeSet);
		Controller2DModel c2dm = new Controller2DModel();
		inputAdapter = new PopupController2D(model, r2dm, c2dm);
		c2dm.setDrawMode(Controller2DModel.DRAWBOND);
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
	

	
	org.openscience.cdk.interfaces.Molecule loadMolecule(String inFile)
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
//			molecule.getAtomAt(i).setPoint2d(null);
//		}
		return molecule;
	}
}

