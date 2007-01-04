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

package org.openscience.cdk.test.structgen;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.vecmath.Vector2d;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.applications.swing.MoleculeListViewer;
import org.openscience.cdk.applications.swing.MoleculeViewer2D;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.structgen.SingleStructureRandomGenerator;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.tools.MFAnalyser;

/**
 * @cdk.module test-extra
 */
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
		org.openscience.cdk.interfaces.IAtom atom;
		 for (int f = 0; f < mol.getAtomCount(); f++)
		{
			atom = mol.getAtom(f);
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
	
    /**
     * @cdk.module test
     */
	public class TestViewer extends MoleculeListViewer
	{
        private static final long serialVersionUID = 3834424739189272930L;
        
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
	    
        private static final long serialVersionUID = -7405706755621468840L;

        public void actionPerformed(ActionEvent e)
		{
      try{
        IAtomContainer ac = ssrg.generate();
        showIt((Molecule)ac, "Randomly generated for " + mf);
      }
      catch(CDKException ex){System.err.println(ex.getMessage());}
		}
	}
}

