/* $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2003  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
 */

package org.openscience.cdk.test.smiles;


import org.openscience.cdk.*;
import org.openscience.cdk.smiles.*;
import org.openscience.cdk.io.*;
import org.openscience.cdk.tools.*;
import org.openscience.cdk.templates.MoleculeFactory;
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
	
	public void testSmilesGenerator() {
		SmilesGenerator sg = new SmilesGenerator();
		Molecule mol1 = MoleculeFactory.makeEthylPropylPhenantren();
		Molecule mol2 = MoleculeFactory.makeAlphaPinene();
		fixCarbonHCount(mol2);
		fixCarbonHCount(mol1);
		String smiles1 = null, smiles2 = null, smiles3 = null;
		if (standAlone) display(mol2);
		try
		{
			smiles1 = sg.createSMILES(mol1);
			smiles2 = sg.createSMILES(mol2);
		}
		catch(Exception exc) {
			System.out.println(exc);
            if (!standAlone) fail();
		}
		if (standAlone) System.err.println("SMILES 1: " + smiles1);
		if (standAlone) System.err.println("SMILES 2: " + smiles2);
        assertNotNull(smiles1);
        assertNotNull(smiles2);
		assertTrue(smiles1.equals("c2cc1c3ccc(cc3ccc1c(c2)CC)CCC"));
		assertTrue(smiles2.equals("C1=C(C)C2CC(C1)C2(C)(C)"));
	}

    public void testPartitioning() {
        SmilesGenerator sg = new SmilesGenerator();
        String smiles = "";
        Molecule molecule = new Molecule();
        Atom sodium = new Atom("Na");
        sodium.setFormalCharge(+1);
        Atom hydroxyl = new Atom("O");
        hydroxyl.setHydrogenCount(1);
        hydroxyl.setFormalCharge(-1);
        molecule.addAtom(sodium);
        molecule.addAtom(hydroxyl);
        try {
            smiles = sg.createSMILES(molecule);
        } catch(Exception exc) {
            System.out.println(exc);
            if (!standAlone) fail();
        }
        if (standAlone) System.err.println("SMILES: " + smiles);
        assertTrue(smiles.indexOf(".") != -1);
    }
    
    public void testBug791091() {
        SmilesGenerator sg = new SmilesGenerator();
        String smiles = "";
        Molecule molecule = new Molecule();
        molecule.addAtom(new Atom("C"));
        molecule.addAtom(new Atom("C"));
        molecule.addAtom(new Atom("C"));
        molecule.addAtom(new Atom("C"));
        molecule.addAtom(new Atom("N"));
        molecule.addBond(0,1,1.0);
        molecule.addBond(1,2,1.0);
        molecule.addBond(2,4,1.0);
        molecule.addBond(4,0,1.0);
        molecule.addBond(4,3,1.0);
        try {
            smiles = sg.createSMILES(molecule);
        } catch(Exception exc) {
            System.out.println(exc);
            if (!standAlone) fail();
        }
        if (standAlone) System.err.println("SMILES: " + smiles);
        assertEquals("CN1CCC1", smiles);
    }
    
	private void fixCarbonHCount(Molecule mol)
	{	
		/* the following line are just a quick fix for this
		   particluar carbon-only molecule until we have a proper 
		   hydrogen count configurator
		 */
		double bondCount = 0;
		Atom atom;
		 for (int f = 0; f < mol.getAtomCount(); f++)
		{
			atom = mol.getAtomAt(f);
			bondCount =  mol.getBondOrderSum(atom);
			atom.setHydrogenCount(4 - (int)bondCount - (int)atom.getCharge());
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

