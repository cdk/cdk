/* StructureDiagramGeneratorTest.java
 * 
 * $RCSfile$    $Author$    $Date$    $Revision$
 * 
 * Copyright (C) 1997-2000  The CompChem project
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


import org.openscience.cdk.*;
import org.openscience.cdk.io.*;
import org.openscience.cdk.layout.*;
import org.openscience.cdk.renderer.*;
import java.util.*;
import java.io.*;
import java.net.URL;


public class StructureDiagramGeneratorTest
{
	MDLReader mr;
	ChemFile chemFile;
	ChemSequence chemSequence;
	ChemModel chemModel;
	SetOfMolecules setOfMolecules;
	Molecule molecule;
	
	public StructureDiagramGeneratorTest(String inFile)
	{
//		molecule = buildPerhydroAzulen();
//		molecule = buildMolecule4x3();
		molecule = get5x10Squares(inFile);
		StructureDiagramGenerator sdg = new StructureDiagramGenerator();
		sdg.setMolecule(molecule);
		sdg.generateCoordinates();
		new MoleculeViewer2D(sdg.getMolecule());
	}

	/**
	 * The main method.
	 *
	 * @param   args    The Arguments from the commandline
	 */
	public static void main(String[] args)
	{
		new StructureDiagramGeneratorTest(args[0]);
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
		
		mol.addBond(0, 1, 1); // 1
		mol.addBond(1, 2, 1); // 2
		mol.addBond(2, 3, 1); // 3
		mol.addBond(3, 4, 1); // 4
		mol.addBond(4, 5, 1); // 5
		mol.addBond(5, 2, 1); // 6
		mol.addBond(2, 0, 1); // 7
		mol.addBond(1, 3, 1); // 8
		mol.addBond(4, 2, 1); // 9
		return mol;
	}
	
	Molecule buildPerhydroAzulen()
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
		mol.addBond(9, 0, 1); // 11				
		return mol;
	}
	
	Molecule get5x10Squares(String inFile)
	{
		try
		{
			FileInputStream fis = new FileInputStream(inFile);
			mr = new MDLReader(fis);
			chemFile = mr.readChemFile();
			fis.close();
			chemSequence = chemFile.getChemSequence(0);
			chemModel = chemSequence.getChemModel(0);
			setOfMolecules = chemModel.getSetOfMolecules(0);
			molecule = setOfMolecules.getMolecule(0);
		}
		catch(Exception exc)
		{
			exc.printStackTrace();		
		}
		for (int i = 0; i < molecule.getAtomCount(); i++)
		{
			molecule.getAtomAt(i).setPoint2D(null);
		}
		return molecule;
		
	}
}

