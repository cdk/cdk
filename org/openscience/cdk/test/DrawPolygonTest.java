/* DrawPolygonTest.java
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


import org.openscience.cdk.*;
import org.openscience.cdk.io.*;
import org.openscience.cdk.layout.*;
import org.openscience.cdk.renderer.*;
import java.util.*;
import java.io.*;
import javax.vecmath.*;


public class DrawPolygonTest
{
	Molecule molecule;
	
	public DrawPolygonTest(String inFile)
	{
		test();
	}

	/**
	 * The main method.
	 *
	 * @param   args    The Arguments from the commandline
	 */
	public static void main(String[] args)
	{
		new DrawPolygonTest(args[0]);
	}


	void test()
	{
		int segments = 13;
		double rotangle = 0;
		Vector2d bondVector;
		int f = 0;
//		for (int f = 0; f < segments; f++)
//		{
			rotangle = Math.PI * 2 / segments * f;
			bondVector = new Vector2d(Math.cos(rotangle), Math.sin(rotangle));
			nextMolecule(bondVector);
			System.out.println(rotangle);
//		}

	}
	
	
	
	void nextMolecule(Vector2d bondVector)
	{
		Molecule molecule = buildRing();
	
		StructureDiagramGenerator sdg = new StructureDiagramGenerator();
		sdg.setMolecule(molecule, true);
	
		try
		{
			sdg.generateCoordinates(bondVector);
		}
		catch(Exception exc)
		{
			System.out.println("*** Exit due to an unexpected error during coordinate generation ***");
			exc.printStackTrace();
			System.exit(1);
		}
		molecule = (Molecule)sdg.getMolecule();
		Renderer2DModel r2dm = new Renderer2DModel();
		MoleculeViewer2D mv = new MoleculeViewer2D(molecule, r2dm);
		r2dm.setDrawNumbers(true);
		mv.display();
	
	
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
		
		mol.addBond(0, 1, 1); // 1
		mol.addBond(1, 2, 1); // 2
		mol.addBond(2, 3, 1); // 3
		mol.addBond(3, 4, 1); // 4
		mol.addBond(4, 5, 1); // 5
		mol.addBond(5, 0, 1); // 6
//		mol.addBond(5, 6, 1); // 7
//		mol.addBond(6, 7, 1); // 8
//		mol.addBond(7, 4, 1); // 9
		
			
		return mol;
	}

}