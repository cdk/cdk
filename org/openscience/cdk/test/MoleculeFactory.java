/* MoleculeFactory.java
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

import java.io.*;
import org.openscience.cdk.*;
import org.openscience.cdk.ringsearch.*;
import org.openscience.cdk.io.*;
import org.openscience.cdk.tools.*;

public class MoleculeFactory
{

	public static Molecule makeAlphaPinene()
	{
		Molecule mol = new Molecule();
		mol.addAtom(new Atom("C")); // 1
		mol.addAtom(new Atom("C")); // 2
		mol.addAtom(new Atom("C")); // 3
		mol.addAtom(new Atom("C")); // 4
		mol.addAtom(new Atom("C")); // 5
		mol.addAtom(new Atom("C")); // 6
		mol.addAtom(new Atom("C")); // 7
		mol.addAtom(new Atom("C")); // 8
		mol.addAtom(new Atom("C")); // 9 
		mol.addAtom(new Atom("C")); // 10
		
		mol.addBond(0, 1, 2); // 1
		mol.addBond(1, 2, 1); // 2
		mol.addBond(2, 3, 1); // 3
		mol.addBond(3, 4, 1); // 4
		mol.addBond(4, 5, 1); // 5
		mol.addBond(5, 0, 1); // 6
		mol.addBond(0, 6, 1); // 7
		mol.addBond(3, 7, 1); // 8
		mol.addBond(5, 7, 1); // 9
		mol.addBond(7, 8, 1); // 10
		mol.addBond(7, 9, 1); // 11
		return mol;
	}
	

	public static Molecule makeEthylCyclohexane()
	{
		Molecule mol = new Molecule();
		mol.addAtom(new Atom("C")); // 1
		mol.addAtom(new Atom("C")); // 2
		mol.addAtom(new Atom("C")); // 3
		mol.addAtom(new Atom("C")); // 4
		mol.addAtom(new Atom("C")); // 5
		mol.addAtom(new Atom("C")); // 6
		mol.addAtom(new Atom("C")); // 7
		mol.addAtom(new Atom("C")); // 8
		
		mol.addBond(0, 1, 2); // 1
		mol.addBond(1, 2, 1); // 2
		mol.addBond(2, 3, 1); // 3
		mol.addBond(3, 4, 1); // 4
		mol.addBond(4, 5, 1); // 5
		mol.addBond(5, 0, 1); // 6
		mol.addBond(0, 6, 1); // 7
		mol.addBond(6, 7, 1); // 8
		return mol;
	}


	public static Molecule makeBiphenyl()
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
		mol.addAtom(new Atom("C")); // 10
		mol.addAtom(new Atom("C")); // 11
	
		
		mol.addBond(0, 1, 2); // 1
		mol.addBond(1, 2, 1); // 2
		mol.addBond(2, 3, 2); // 3
		mol.addBond(3, 4, 1); // 4
		mol.addBond(4, 5, 2); // 5
		mol.addBond(5, 0, 1); // 6
	
		mol.addBond(0, 6, 1); // 7
		mol.addBond(6, 7, 1); // 8
		mol.addBond(7, 8, 2); // 5
		mol.addBond(8, 9, 1); // 6
		mol.addBond(9, 10, 2); // 7
		mol.addBond(10, 11, 1); // 8
		mol.addBond(11, 6, 2); // 5
		return mol;
	}
	

	public static Molecule makePhenylEthylBenzene()
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
		mol.addAtom(new Atom("C")); // 10
		mol.addAtom(new Atom("C")); // 11
		mol.addAtom(new Atom("C")); // 12
	
		
		mol.addBond(0, 1, 2); // 1
		mol.addBond(1, 2, 1); // 2
		mol.addBond(2, 3, 2); // 3
		mol.addBond(3, 4, 1); // 4
		mol.addBond(4, 5, 2); // 5
		mol.addBond(5, 0, 1); // 6
	
		mol.addBond(0, 6, 1); // 7
		mol.addBond(6, 7, 1); // 8
		mol.addBond(7, 8, 2); // 5
		mol.addBond(8, 9, 1); // 6
		mol.addBond(9, 10, 2); // 7
		mol.addBond(10, 11, 1); // 8
		mol.addBond(11, 12, 2); // 5
		mol.addBond(12, 7, 1); // 5
		return mol;
	}

	
	/* build a molecule from 4 condensed triangles */
	public static Molecule make4x3CondensedRings()
	{
		Molecule mol = new Molecule();
		mol.addAtom(new Atom("C")); // 1
		mol.addAtom(new Atom("C")); // 2
		mol.addAtom(new Atom("C")); // 3
		mol.addAtom(new Atom("C")); // 4
		mol.addAtom(new Atom("C")); // 5
		mol.addAtom(new Atom("C")); // 6
		mol.addAtom(new Atom("C")); // 7		
		
		mol.addBond(0, 1, 1); // 1
		mol.addBond(1, 2, 1); // 2
		mol.addBond(2, 0, 1); // 3
		mol.addBond(2, 3, 1); // 4
		mol.addBond(1, 3, 1); // 5
		mol.addBond(3, 4, 1); // 6
		mol.addBond(4, 2, 1); // 7
		mol.addBond(4, 5, 1); // 8
		mol.addBond(5, 6, 1); // 9
		mol.addBond(6, 4, 1); // 10		
		
		return mol;
	}
	
	public static Molecule makeSpiroRings()
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
	

	public static Molecule makeBicycloRings()
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
		
		
		mol.addBond(0, 1, 1); // 1
		mol.addBond(1, 2, 1); // 2
		mol.addBond(2, 3, 1); // 3
		mol.addBond(3, 4, 1); // 4
		mol.addBond(4, 5, 1); // 5
		mol.addBond(5, 0, 1); // 6
		mol.addBond(6, 0, 1); // 7
		mol.addBond(6, 7, 1); // 8
		mol.addBond(7, 3, 1); // 9
		return mol;
	}


	
	static Molecule make2x3CondensedRings()
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
	

	static Molecule make2x4CondensedRings()
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

	
	static Molecule makeFusedRings()
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

	static Molecule makeMethylDecaline()
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
		mol.addAtom(new Atom("C")); // 10

		
		mol.addBond(0, 1, 1); // 1
		mol.addBond(1, 2, 1); // 2
		mol.addBond(2, 3, 1); // 3
		mol.addBond(3, 4, 1); // 4
		mol.addBond(4, 5, 1); // 5
		mol.addBond(5, 0, 1); // 6
		mol.addBond(5, 6, 1); // 7
		mol.addBond(6, 7, 1); // 8
		mol.addBond(7, 8, 1); // 9
		mol.addBond(8, 9, 1); // 10
		mol.addBond(9, 0, 1); // 11		
		mol.addBond(5, 10, 1); // 12		
		
			
		return mol;
	}

	static Molecule makeSingleRing()
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
	

	static Molecule makeAdamantane()
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
		mol.addAtom(new Atom("C")); // 10
		mol.addAtom(new Atom("C")); // 11
		mol.addAtom(new Atom("C")); // 12
		mol.addAtom(new Atom("C")); // 13
		
		
		mol.addBond(0, 1, 1); // 1
		mol.addBond(1, 2, 1); // 2
		mol.addBond(2, 3, 1); // 3
		mol.addBond(3, 4, 1); // 4
		mol.addBond(4, 5, 1); // 5
		mol.addBond(5, 0, 1); // 6
		mol.addBond(5, 6, 1); // 7
		mol.addBond(6, 9, 1); // 8
		mol.addBond(1, 7, 1); // 9
		mol.addBond(7, 9, 1); // 10
		mol.addBond(3, 8, 1); // 11		
		mol.addBond(8, 9, 1); // 12
		mol.addBond(0, 10, 1); // 13
		mol.addBond(10, 13, 1); // 14
		mol.addBond(2, 11, 1); // 15
		mol.addBond(11, 13, 1); // 16
		mol.addBond(4, 12, 1); // 17		
		mol.addBond(12, 13, 1); // 18		
		


			
		return mol;
	}


	static Molecule makeBranchedAliphatic()
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
		mol.addAtom(new Atom("C")); // 10
		mol.addAtom(new Atom("C")); // 11
		mol.addAtom(new Atom("C")); // 12
		mol.addAtom(new Atom("C")); // 13
		mol.addAtom(new Atom("C")); // 14
		
		mol.addBond(0, 1, 1); // 1
		mol.addBond(1, 2, 1); // 2
		mol.addBond(2, 3, 2); // 3
		mol.addBond(3, 4, 1); // 4
		mol.addBond(4, 5, 1); // 5
		mol.addBond(2, 6, 1); // 6
		mol.addBond(6, 7, 1); // 7
		mol.addBond(7, 8, 1); // 8
		mol.addBond(6, 9, 1); // 9
		mol.addBond(6, 10, 1); // 10
		mol.addBond(10, 11, 1); // 11
		mol.addBond(8, 12, 1); // 12
		mol.addBond(12, 13, 1); // 13
		mol.addBond(11, 14, 1); // 14
		
		
		
			
		return mol;
	}



	
	static Molecule loadMolecule(String inFile)
	{
		MDLReader mr = null;
		ChemFile chemFile = null;
		ChemSequence chemSequence = null;
		ChemModel chemModel = null;
		SetOfMolecules setOfMolecules = null;
		Molecule molecule = null;
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
		for (int i = 0; i < molecule.getAtomCount(); i++)
		{
			molecule.getAtomAt(i).setPoint2D(null);
		}
		return molecule;
		
	}


}

