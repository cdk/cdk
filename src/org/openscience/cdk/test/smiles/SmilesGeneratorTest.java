/*
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

package org.openscience.cdk.test.smiles;


import org.openscience.cdk.*;
import org.openscience.cdk.smiles.*;
import org.openscience.cdk.io.*;
import org.openscience.cdk.tools.*;
import org.openscience.cdk.test.MoleculeFactory;
import org.openscience.cdk.renderer.*;
import org.openscience.cdk.layout.*;
import org.openscience.cdk.test.*;
import javax.vecmath.*;

import java.util.*;

import java.io.*;
import java.net.URL;
import junit.framework.*;

public class SmilesGeneratorTest extends TestCase
{
	boolean standAlone = false;
	
	public SmilesGeneratorTest(String name)
	{
		super(name);
	}

	public static Test suite()
	{
		return new TestSuite(SmilesGeneratorTest.class);
	}

	public void setStandAlone(boolean standAlone)
	{
		this.standAlone = standAlone;
	}
	
	public void testSmilesGenerator()
	{
		SmilesGenerator sg = new SmilesGenerator();
		Molecule mol1 = MoleculeFactory.makeEthylPropylPhenantren();
		Molecule mol2 = MoleculeFactory.makeAlphaPinene();
    Molecule mol3=null;
    try{
      String tillsmol="\n  Marvin  07230205422D\n\n 22 24  0  0  0  0  0  0  0  0999 V2000\n    9.1628   -4.1392    0.0000 C   0  0  2  0  0  0  0  0  0  0  0  0\n    9.0889   -4.9609    0.0000 C   0  0  1  0  0  0  0  0  0  0  0  0\n    9.8475   -5.2852    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n   10.3904   -4.6639    0.0000 C   0  0  2  0  0  0  0  0  0  0  0  0\n    9.9671   -3.9556    0.0000 C   0  0  1  0  0  0  0  0  0  0  0  0\n   10.3251   -3.2122    0.0000 C   0  0  1  0  0  0  0  0  0  0  0  0\n    9.9671   -2.4690    0.0000 C   0  0  1  0  0  0  0  0  0  0  0  0\n    9.1628   -2.2854    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n    8.5178   -2.7999    0.0000 C   0  0  1  0  0  0  0  0  0  0  0  0\n    8.5178   -3.6249    0.0000 C   0  0  1  0  0  0  0  0  0  0  0  0\n   10.7899   -2.5306    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n   11.5096   -2.6205    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n11.1000   -1.7750    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n    7.7744   -3.9827    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n   11.0155   -4.6201    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n   10.7492   -3.8213    0.0000 H   0  0  0  0  0  0  0  0  0  0  0  0\n   11.0868   -3.2791    0.0000 H   0  0  0  0  0  0  0  0  0  0  0  0\n   10.0900   -1.6532    0.0000 H   0  0  0  0  0  0  0  0  0  0  0  0\n    7.7819   -2.4455    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n    7.5501   -3.3171    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n    8.2711   -4.3697    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n    8.3877   -5.3798    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n  2  1  1  0  0  0  0\n  1  5  1  0  0  0  0\n  2  3  1  0  0  0  0\n  3  4  1  0  0  0  0\n  4  5  1  0  0  0  0\n  4 15  1  6  0  0  0\n  5  6  1  0  0  0  0\n  6  7  1  0  0  0  0\n  7  8  1  0  0  0  0\n  8  9  1  0  0  0  0\n  9 10  1  0  0  0  0\n 10  1  1  0  0  0  0\n 10 14  1  6  0  0  0\n 11  6  1  0  0  0  0\n 11  7  1  0  0  0  0\n 11 12  1  6  0  0  0\n 11 13  1  1  0  0  0\n  5 16  1  1  0  0  0\n  6 17  1  6  0  0  0\n  7 18  1  6  0  0  0\n  9 19  1  1  0  0  0\n 10 20  1  1  0  0  0\n  1 21  1  6  0  0  0\n  2 22  1  1  0  0  0\nM  END";
 			MDLReader mdlreader = new MDLReader(new StringReader(tillsmol));
			mol3 = (Molecule) mdlreader.read(new Molecule());
    }
    catch(Exception ex){
      ex.printStackTrace();
    }
		fixCarbonHCount(mol2);
		fixCarbonHCount(mol1);
		String smiles1 = null, smiles2 = null, smiles3 = null;
		if (standAlone) display(mol2);
		try
		{
			smiles1 = sg.createSMILES(mol1);
			smiles2 = sg.createSMILES(mol2);
      smiles3 = sg.createSMILES(mol3,true,true);
		}
		catch(Exception exc)
		{
			System.out.println(exc);	
		}
		if (standAlone) System.err.println("SMILES 1: " + smiles1);
		if (standAlone) System.err.println("SMILES 2: " + smiles2);
    if (standAlone) System.err.println("SMILES 3: " + smiles3);
		assertTrue(smiles1.equals("c2cc1c3ccc(cc3ccc1c(c2)CC)CCC"));
		assertTrue(smiles2.equals("C1=C(C)C2CC(C1)C2(C)(C)"));
    assertTrue(smiles3.equals("[H][C@]12(CC(O)[C@](O)(C)[C@]3(O)(C(O)CC(C)[C@]3([C@]2([C@]1(C)(C))([H]))([H])))"));
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
		SmilesGeneratorTest sgt = new SmilesGeneratorTest("AllRingsFinderTest");
		sgt.setStandAlone(true);
		sgt.testSmilesGenerator();
	}	
}

