/* $RCSfile$ 
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2006  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.test.structgen;

import java.util.Vector;

import javax.vecmath.Vector2d;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.applications.swing.MoleculeListViewer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.structgen.RandomGenerator;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.test.CDKTestCase;

/**
 * @cdk.module test-extra
 */
public class RandomStructureGeneratorTest extends CDKTestCase
{
	public boolean debug = false;
	boolean standAlone = false;
	MoleculeListViewer listviewer;
	
    public RandomStructureGeneratorTest(String name) {
        super(name);
    }
    
    public RandomStructureGeneratorTest() {
        this("RandomStructureGeneratorTest");
    }

	public void setStandAlone(boolean standAlone)
	{
		this.standAlone = standAlone;
	}

	public static Test suite()
	{
		return new TestSuite(RandomStructureGeneratorTest.class);
	}

	/** A complex alkaloid with two separate ring systems to 
	  * be laid out.
	  */
	public void visualTestIt() throws Exception 
	{
		String s = null;
		Vector structures = new Vector();	
		IMolecule mol = null;
		Molecule molecule = MoleculeFactory.makeAlphaPinene();
		listviewer = new MoleculeListViewer();
		//System.out.println(molecule);
		RandomGenerator rg = new RandomGenerator(molecule);
	
		for (int f = 0; f < 20; f++)
		{
			if (debug) System.out.println("Proposing structure no. " + f);
			if (debug) System.out.println("Entering rg.proposeStructure()");
			mol = rg.proposeStructure();
			if ((double)f/(double)50 == f/50)
			{
				structures.addElement(mol.clone());
				if (debug)
				{
					s = "BondCounts:    ";
					for (int g = 0; g < mol.getAtomCount(); g++)
					{
						s += mol.getConnectedBondsCount(mol.getAtom(g)) + " ";
					}
					//System.out.println(s);
					s = "BondOrderSums: ";
					for (int g = 0; g < mol.getAtomCount(); g++)
					{
						s += mol.getBondOrderSum(mol.getAtom(g)) + " ";
					}
					//System.out.println(s);
					s = "Bonds: ";
					org.openscience.cdk.interfaces.IBond[] bonds = mol.getBonds();
					for (int g = 0; g < bonds.length; g++)
					{
						s += bonds[g].getOrder() + " ";
					}
					//System.out.println(s);
				}
			}
			rg.acceptStructure();
		}
		everythingOk(structures);
	}


	/**
	 * @param structures
	 * @return
	 */
	private boolean everythingOk(Vector structures)
	{
		StructureDiagramGenerator sdg = null; 
		Molecule mol = null;
		if (debug) System.out.println("number of structures in vector: " + structures.size());
		for (int f = 0; f < structures.size(); f++)
		{
			sdg = new StructureDiagramGenerator();

			mol = (Molecule)structures.elementAt(f);
			sdg.setMolecule(mol);

			try
			{
				sdg.generateCoordinates(new Vector2d(0,1));
			}
			catch(Exception exc)
			{
				exc.printStackTrace();
				fail("*** Exit due to an unexpected error during coordinate generation ***");
			}
            if (standAlone) {
            	 listviewer.addStructure(mol, true, false, "");
            }
		}
		return true;
	}
	
	public static void main(String[] args)
	{
		RandomStructureGeneratorTest test = new RandomStructureGeneratorTest();
		test.setStandAlone(true);
        try {
			test.visualTestIt();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

