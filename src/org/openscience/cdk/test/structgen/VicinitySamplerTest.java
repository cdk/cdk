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
 * All I ask is that proper credit is given for my work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA. 
 * 
 */

package org.openscience.cdk.test.structgen;

import java.util.Vector;

import javax.swing.JFrame;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.applications.swing.MoleculeListViewer;
import org.openscience.cdk.applications.swing.MoleculeViewer2D;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.structgen.VicinitySampler;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.test.CDKTestCase;


/**
 * @cdk.module test-extra
 */
public class VicinitySamplerTest extends CDKTestCase
{
	
	public VicinitySamplerTest(String name)
	{
		super(name);
	}

	
	public static Test suite() {
		return new TestSuite(VicinitySamplerTest.class);
	}

	public  void testVicinitySampler() throws Exception 
	{
		Molecule mol = MoleculeFactory.makeEthylPropylPhenantren();
		configureAtoms(mol);
		fixCarbonHCount(mol);
		
		//System.out.println("Initial Molecule: \n" + mol);
		VicinitySampler vs = new VicinitySampler();
		vs.setMolecule(mol);

		SmilesGenerator sg = null;
		Molecule temp = null;
		Vector structures = vs.sample((AtomContainer) mol);
		structures.addElement(mol);
		for (int f = 0; f < structures.size(); f++)
		{
			temp = (Molecule)structures.elementAt(f);
			sg = new SmilesGenerator(temp.getBuilder());
			//System.out.println(sg.createSMILES(temp) + " Structure " + (f + 1));
		}

		//System.out.println("There are " + structures.size() + " structures in Faulon-Distance 1 for EthylPropylPhenantren"); 
		display(structures);
        fail(); // Method does not test anything
	}
	
	private static void configureAtoms(Molecule mol)
	{
		try
		{
			IsotopeFactory.getInstance(mol.getBuilder()).configureAtoms(mol);
		}
		catch(Exception exc)
		{
			exc.printStackTrace();
		}
	}

	
	private void fixCarbonHCount(Molecule mol)
	{	
		/* the following line are just a quick fix for this
		   particluar carbon-only molecule until we have a proper 
		   hydrogen count configurator
		 */
		double bondCount = 0;
		org.openscience.cdk.interfaces.IAtom atom;
		 for (int f = 0; f < mol.getAtomCount(); f++)
		{
			atom = mol.getAtomAt(f);
			bondCount =  mol.getBondOrderSum(atom);
			if (bondCount > 4) System.out.println("bondCount: " + bondCount);
			atom.setHydrogenCount(4 - (int)bondCount - (int)atom.getCharge());
		}
	}

	
	private void display(Vector structures) throws Exception 
	{
		MoleculeListViewer moleculeListViewer = new MoleculeListViewer();
		moleculeListViewer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		StructureDiagramGenerator sdg = null;
		MoleculeViewer2D mv = null;
		Molecule mol = null;
		for (int f = 0; f < structures.size(); f++)
		{
			sdg = new StructureDiagramGenerator();
			mv = new MoleculeViewer2D();
			mv.getRenderer2DModel().setDrawNumbers(true);
			mol = (Molecule)structures.elementAt(f);
			sdg.setMolecule((Molecule)mol.clone());

			try
			{
				sdg.generateCoordinates();
				mv.setAtomContainer(sdg.getMolecule());
				moleculeListViewer.addStructure(mv, "no. " + (f + 1));

			}
			catch(Exception exc)
			{
				
				exc.printStackTrace();
			}
		}
	}

	
	public static void main(String[] args)
	{
		VicinitySamplerTest vst = new VicinitySamplerTest("VicinitySamplerTest");
		try {
			vst.testVicinitySampler();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
