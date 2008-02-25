/* $RCSfile$ 
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.structgen;

import java.util.Iterator;
import java.util.Vector;

import javax.vecmath.Vector2d;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.applications.swing.MoleculeListViewer;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.structgen.RandomGenerator;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.CDKTestCase;

/**
 * @cdk.module test-structgen
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
	
	public void testTwentyRandomStructures() {
		Molecule molecule = MoleculeFactory.makeAlphaPinene();
		RandomGenerator rg = new RandomGenerator(molecule);
		IMolecule result = null;
		for (int f = 0; f < 50; f++) {
			result = rg.proposeStructure();
			assertEquals(molecule.getAtomCount(), result.getAtomCount());
			assertEquals(1, ConnectivityChecker.partitionIntoMolecules(result).getAtomContainerCount());
		}
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
		//logger.debug(molecule);
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
					//logger.debug(s);
					s = "BondOrderSums: ";
					for (int g = 0; g < mol.getAtomCount(); g++)
					{
						s += mol.getBondOrderSum(mol.getAtom(g)) + " ";
					}
					//logger.debug(s);
					s = "Bonds: ";
					Iterator bonds = mol.bonds();
					while (bonds.hasNext()) {
						s += ((IBond)bonds.next()).getOrder() + " ";
					}
					//logger.debug(s);
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
	private boolean everythingOk(Vector structures) throws Exception
	{
		StructureDiagramGenerator sdg = null; 
		Molecule mol = null;
		if (debug) System.out.println("number of structures in vector: " + structures.size());
		for (int f = 0; f < structures.size(); f++)
		{
			sdg = new StructureDiagramGenerator();

			mol = (Molecule)structures.elementAt(f);
			sdg.setMolecule(mol);

			sdg.generateCoordinates(new Vector2d(0,1));
            if (standAlone) {
            	 listviewer.addStructure(mol, true, false, "");
            }
		}
		return true;
	}
	
}

