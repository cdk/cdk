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
import org.openscience.cdk.exception.*;
import org.openscience.cdk.smiles.*;
import org.openscience.cdk.io.*;
import org.openscience.cdk.tools.*;
import org.openscience.cdk.aromaticity.*;
import org.openscience.cdk.ringsearch.SSSRFinder;
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
		Molecule mol2 = MoleculeFactory.makeAlphaPinene();
		fixCarbonHCount(mol2);
    String smiles2 = null;
		if (standAlone) display(mol2);
		try
		{
			smiles2 = sg.createSMILES(mol2);
		}
    catch(Exception exc) {
			System.out.println(exc);
            if (!standAlone) fail();
		}
		if (standAlone) System.err.println("SMILES 2: " + smiles2);
        assertNotNull(smiles2);
		assertTrue(smiles2.equals("C1=C(C)C2CC(C1)C2(C)(C)"));
	}

	public void testEthylPropylPhenantren() {
		SmilesGenerator sg = new SmilesGenerator();
		Molecule mol1 = MoleculeFactory.makeEthylPropylPhenantren();
		fixCarbonHCount(mol1);
    String smiles1 = null;
		if (standAlone) display(mol1);
		try
		{
			smiles1 = sg.createSMILES(mol1);
		}
    catch(Exception exc) {
			System.out.println(exc);
            if (!standAlone) fail();
		}
		if (standAlone) System.err.println("SMILES 1: " + smiles1);
        assertNotNull(smiles1);
		assertTrue(smiles1.equals("c2cc1c3ccc(cc3(ccc1c(c2)CC))CCC"));
	}

	public void testAlanin() {
		SmilesGenerator sg = new SmilesGenerator();
		Molecule mol1 = new Molecule();
 		mol1.addAtom(new Atom("N", new Point2d(1,0))); // 1
		mol1.addAtom(new Atom("C", new Point2d(1,2))); // 2
		mol1.addAtom(new Atom("F", new Point2d(1,2))); // 3
		mol1.addAtom(new Atom("C", new Point2d(0,0))); // 4
		mol1.addAtom(new Atom("C", new Point2d(1,4))); // 5
		mol1.addAtom(new Atom("O", new Point2d(1,5))); // 6
		mol1.addAtom(new Atom("O", new Point2d(1,6))); // 7
		mol1.addBond(0, 1, 1); // 1
		mol1.addBond(1, 2, 1, CDKConstants.STEREO_BOND_UP); // 2
		mol1.addBond(1, 3, 1, CDKConstants.STEREO_BOND_DOWN); // 3
		mol1.addBond(1, 4, 1); // 4
		mol1.addBond(4, 5, 1); // 5
		mol1.addBond(4, 6, 2); // 6
    try{
      new SaturationChecker().addHydrogensToSatisfyValency(mol1);
      IsotopeFactory ifac = IsotopeFactory.getInstance();
      ifac.configureAtoms(mol1);
    }
    catch(IOException ex){}
    catch(ClassNotFoundException ex){}

    String smiles1 = null;
		if (standAlone) display(mol1);
		try
		{
			smiles1 = sg.createSMILES(mol1,true,new boolean[mol1.getBondCount()]);
		}
    catch(Exception exc) {
			System.out.println(exc);
            if (!standAlone) fail();
		}
		if (standAlone) System.err.println("SMILES 1: " + smiles1);
        assertNotNull(smiles1);
		assertTrue(smiles1.equals("[H]OC(=O)[C@](F)(N([H])[H])C([H])([H])[H]"));
    mol1.getBondAt(1).setStereo(CDKConstants.STEREO_BOND_DOWN);
    mol1.getBondAt(2).setStereo(CDKConstants.STEREO_BOND_UP);
		try
		{
			smiles1 = sg.createSMILES(mol1,true,new boolean[mol1.getBondCount()]);
		}
    catch(Exception exc) {
			System.out.println(exc);
            if (!standAlone) fail();
		}
		if (standAlone) System.err.println("SMILES 1: " + smiles1);
        assertNotNull(smiles1);
		assertTrue(smiles1.equals("[H]OC(=O)[C@](F)(C([H])([H])[H])N([H])[H]"));
	}

	public void testCisResorcinol() {
		SmilesGenerator sg = new SmilesGenerator();
		Molecule mol1 = new Molecule();
		mol1.addAtom(new Atom("O", new Point2d(3,1))); // 1
		mol1.addAtom(new Atom("H", new Point2d(2,0))); // 2
		mol1.addAtom(new Atom("C", new Point2d(2,1))); // 3
		mol1.addAtom(new Atom("C", new Point2d(1,1))); // 4
		mol1.addAtom(new Atom("C", new Point2d(1,4))); // 5
		mol1.addAtom(new Atom("C", new Point2d(1,5))); // 6
		mol1.addAtom(new Atom("C", new Point2d(1,2))); // 7
		mol1.addAtom(new Atom("C", new Point2d(2,2))); // 1
		mol1.addAtom(new Atom("O", new Point2d(3,2))); // 2
		mol1.addAtom(new Atom("H", new Point2d(2,3))); // 3
		mol1.addBond(0, 2, 1, CDKConstants.STEREO_BOND_DOWN); // 1
		mol1.addBond(1, 2, 1, CDKConstants.STEREO_BOND_UP); // 2
		mol1.addBond(2, 3, 1); // 3
		mol1.addBond(3, 4, 1); // 4
		mol1.addBond(4, 5, 1); // 5
		mol1.addBond(5, 6, 1); // 6
		mol1.addBond(6, 7, 1); // 3
		mol1.addBond(7, 8, 1, CDKConstants.STEREO_BOND_UP); // 4
		mol1.addBond(7, 9, 1, CDKConstants.STEREO_BOND_DOWN); // 5
		mol1.addBond(7, 2, 1); // 6
    try{
      new SaturationChecker().addHydrogensToSatisfyValency(mol1);
      IsotopeFactory ifac = IsotopeFactory.getInstance();
      ifac.configureAtoms(mol1);
    }
    catch(IOException ex){}
    catch(ClassNotFoundException ex){}
    String smiles1 = null;
		if (standAlone) display(mol1);
		try
		{
			smiles1 = sg.createSMILES(mol1,true,new boolean[mol1.getBondCount()]);
		}
    catch(Exception exc) {
			System.out.println(exc);
            if (!standAlone) fail();
		}
		if (standAlone) System.err.println("SMILES 1: " + smiles1);
        assertNotNull(smiles1);
		assertTrue(smiles1.equals("[H]O[C@]1([H])(C([H])([H])C([H])([H])C([H])([H])C([H])([H])[C@]1([H])(O[H]))"));
    mol1=(Molecule)new MFAnalyser(mol1).removeHydrogens();
		try
		{
			smiles1 = sg.createSMILES(mol1);
		}
    catch(Exception exc) {
			System.out.println(exc);
            if (!standAlone) fail();
		}
		if (standAlone) System.err.println("SMILES 1: " + smiles1);
        assertNotNull(smiles1);
		assertTrue(smiles1.equals("OC1CCCCC1(O)"));
	}

	public void testCisDecalin() {
		SmilesGenerator sg = new SmilesGenerator();
		Molecule mol1 = new Molecule();
		mol1.addAtom(new Atom("H", new Point2d(1,0))); // 1
		mol1.addAtom(new Atom("C", new Point2d(1,2))); // 2
		mol1.addAtom(new Atom("C", new Point2d(1,2))); // 3
		mol1.addAtom(new Atom("C", new Point2d(0,0))); // 4
		mol1.addAtom(new Atom("C", new Point2d(1,4))); // 5
		mol1.addAtom(new Atom("C", new Point2d(1,5))); // 6
		mol1.addAtom(new Atom("C", new Point2d(1,6))); // 7
		mol1.addAtom(new Atom("H", new Point2d(1,0))); // 1
		mol1.addAtom(new Atom("C", new Point2d(1,2))); // 2
		mol1.addAtom(new Atom("C", new Point2d(1,2))); // 3
		mol1.addAtom(new Atom("C", new Point2d(1,2))); // 2
		mol1.addAtom(new Atom("C", new Point2d(1,2))); // 3
		mol1.addBond(0, 1, 1, CDKConstants.STEREO_BOND_DOWN); // 1
		mol1.addBond(1, 2, 1); // 2
		mol1.addBond(2, 3, 1); // 3
		mol1.addBond(3, 4, 1); // 4
		mol1.addBond(4, 5, 1); // 5
		mol1.addBond(5, 6, 1); // 6
		mol1.addBond(6, 7, 1, CDKConstants.STEREO_BOND_DOWN); // 3
		mol1.addBond(6, 8, 1); // 4
		mol1.addBond(8, 9, 1); // 5
		mol1.addBond(9, 10, 1); // 6
		mol1.addBond(10, 11, 1); // 6
		mol1.addBond(11, 1, 1); // 6
		mol1.addBond(1, 6, 1); // 6
    try{
      new SaturationChecker().addHydrogensToSatisfyValency(mol1);
      IsotopeFactory ifac = IsotopeFactory.getInstance();
      ifac.configureAtoms(mol1);
    }
    catch(IOException ex){}
    catch(ClassNotFoundException ex){}
    String smiles1 = null;
		if (standAlone) display(mol1);
		try
		{
			smiles1 = sg.createSMILES(mol1,true,new boolean[mol1.getBondCount()]);
		}
    catch(Exception exc) {
			System.out.println(exc);
            if (!standAlone) fail();
		}
		if (standAlone) System.err.println("SMILES 1: " + smiles1);
        assertNotNull(smiles1);
  	assertTrue(smiles1.equals("[H]C1([H])(C([H])([H])C([H])([H])[C@]2([H])(C([H])([H])C([H])([H])C([H])([H])C([H])([H])[C@]2([H])(C1([H])([H]))))"));
    mol1=(Molecule)new MFAnalyser(mol1).removeHydrogens();
		try
		{
			smiles1 = sg.createSMILES(mol1);
		}
    catch(Exception exc) {
			System.out.println(exc);
            if (!standAlone) fail();
		}
		if (standAlone) System.err.println("SMILES 1: " + smiles1);
        assertNotNull(smiles1);
		assertTrue(smiles1.equals("C1CCC2CCCCC2(C1)"));
	}

	public void testDoubleBondConfiguration() {
		SmilesGenerator sg = new SmilesGenerator();
		Molecule mol1 = new Molecule();
		mol1.addAtom(new Atom("S", new Point2d(0,0))); // 1
		mol1.addAtom(new Atom("C", new Point2d(1,1))); // 2
		mol1.addAtom(new Atom("F", new Point2d(2,0))); // 3
		mol1.addAtom(new Atom("C", new Point2d(1,2))); // 4
		mol1.addAtom(new Atom("F", new Point2d(2,3))); // 5
    mol1.addAtom(new Atom("S", new Point2d(0,3))); // 1
		
		mol1.addBond(0, 1, 1); // 1
		mol1.addBond(1, 2, 1); // 2
		mol1.addBond(1, 3, 2); // 3
		mol1.addBond(3, 4, 1); // 4
    mol1.addBond(3, 5, 1); // 4
    try{
      IsotopeFactory ifac = IsotopeFactory.getInstance();
      ifac.configureAtoms(mol1);
    }
    catch(IOException ex){}
    catch(ClassNotFoundException ex){}
    String smiles1 = null;
		if (standAlone) display(mol1);
    boolean[] bool=new boolean[mol1.getBondCount()];
    bool[2]=true;
		try
		{
			smiles1 = sg.createSMILES(mol1,true,bool);
		}
    catch(Exception exc) {
			System.out.println(exc);
            if (!standAlone) fail();
		}
		if (standAlone) System.err.println("SMILES 1: " + smiles1);
        assertNotNull(smiles1);
        System.err.println(smiles1);
  	assertTrue(smiles1.equals("F/C(=C/(F)S)S"));
    mol1.getAtomAt(4).setPoint2D(new Point2d(0,3));
    mol1.getAtomAt(5).setPoint2D(new Point2d(2,3));
    try
		{
			smiles1 = sg.createSMILES(mol1,true,bool);
		}
    catch(Exception exc) {
			System.out.println(exc);
            if (!standAlone) fail();
		}
		if (standAlone) System.err.println("SMILES 1: " + smiles1);
        assertNotNull(smiles1);
        System.err.println(smiles1);
		assertTrue(smiles1.equals("F/C(=C\\(F)S)S"));
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
        fixCarbonHCount(molecule);
        try {
            smiles = sg.createSMILES(molecule);
        } catch(Exception exc) {
            System.out.println(exc);
            if (!standAlone) fail();
        }
        if (standAlone) System.err.println("SMILES: " + smiles);
        assertEquals("N1(C)CCC1", smiles);
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
      if(atom.getSymbol().equals("C"))
        atom.setHydrogenCount(4 - (int)bondCount - (int)atom.getCharge());
      if(atom.getSymbol().equals("N"))
        atom.setHydrogenCount(3 - (int)bondCount - (int)atom.getCharge());
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

