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

package org.openscience.cdk.test.structgen;

import org.openscience.cdk.controller.*;
import org.openscience.cdk.*;
import org.openscience.cdk.exception.*;
import org.openscience.cdk.io.*;
import org.openscience.cdk.layout.*;
import org.openscience.cdk.renderer.*;
import org.openscience.cdk.structgen.*;
import org.openscience.cdk.tools.*;
import org.openscience.cdk.templates.*;
import java.util.*;
import java.io.*;
import java.net.URL;
import javax.vecmath.Vector2d;
import javax.swing.*;
import java.awt.event.*;



public class SingleStructureRandomGeneratorTest
{
	TestViewer testViewer = null;
	String mf;
	SingleStructureRandomGenerator ssrg;
	
	public SingleStructureRandomGeneratorTest() throws Exception
	{
		System.out.println("Instantiating MoleculeListViewer");
		testViewer = new TestViewer();
		System.out.println("Instantiating SingleStructureRandomGenerator");
		ssrg = new SingleStructureRandomGenerator();
		System.out.println("Assining unbonded set of atoms");
		AtomContainer ac = getBunchOfUnbondedAtoms();
		mf = new MFAnalyser(ac).getMolecularFormula();
		System.out.println("Molecular Formula is: " + mf);
		ssrg.setAtomContainer(ac);
	}


	private boolean showIt(Molecule molecule, String name)
	{
		StructureDiagramGenerator sdg = new StructureDiagramGenerator();
		MoleculeViewer2D mv = new MoleculeViewer2D();
		try
		{
			sdg.setMolecule((Molecule)molecule.clone());
			sdg.generateCoordinates(new Vector2d(0,1));
			mv.setAtomContainer(sdg.getMolecule());
			testViewer.addStructure(mv, name);
		}
		catch(Exception exc)
		{
			System.out.println("*** Exit due to an unexpected error during coordinate generation ***");
			exc.printStackTrace();
			return false;
		}
		return true;
	}

	private AtomContainer getBunchOfUnbondedAtoms()
	{
		Molecule molecule = MoleculeFactory.makeAlphaPinene();
		fixCarbonHCount(molecule);
		molecule.removeAllElectronContainers();
		return (AtomContainer)molecule;
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
			if (bondCount > 4) System.out.println("bondCount: " + bondCount);
			atom.setHydrogenCount(4 - (int)bondCount - (int)atom.getCharge());
		}
	}
	
	public static void main(String[] args)
	{
		System.out.println("Yes!");
		try
		{
			new SingleStructureRandomGeneratorTest();
		}
		catch(Exception exc)
		{
			System.out.println("SingleStructureRandomGeneratorTest failed: ");
			exc.printStackTrace();
		}
		System.out.println("Done");
		
	}
	
	public class TestViewer extends MoleculeListViewer
	{
		JButton more;
		public TestViewer()
		{
			super();
			more = new JButton("One more");
			more.addActionListener(new MoreAction());
			getContentPane().add("South", more);
			pack();
		}
		
	}
	
	class MoreAction extends AbstractAction
	{
		public void actionPerformed(ActionEvent e)
		{
      try{
        AtomContainer ac = ssrg.generate();
        showIt((Molecule)ac, "Randomly generated for " + mf);
      }
      catch(CDKException ex){System.err.println(ex.getMessage());}
		}
	}
}

