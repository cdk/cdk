/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2002  The Chemistry Development Kit (CKD) project
 *
 *  Contact: steinbeck@ice.mpg.de
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All I ask is that proper credit is given for my work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.openscience.cdk.test;

import org.openscience.cdk.*;
import org.openscience.cdk.tools.*;
import org.openscience.cdk.io.*;
import org.openscience.cdk.renderer.*;
import org.openscience.cdk.layout.*;
import org.openscience.cdk.templates.*;
import org.openscience.cdk.geometry.*;
import org.openscience.cdk.aromaticity.*;
import org.openscience.cdk.smiles.*;

import java.io.*;
import javax.vecmath.*;
import javax.swing.*;
import javax.swing.tree.*;
import java.util.*;
import java.awt.*;

import junit.framework.*;

/**
 *  Tests the HOSECode genertor. This is *not* a JUnit test class!
 *
 *@author     steinbeck
 *@created    November 16, 2002
 */
public class HOSECodeTest extends TestCase
{
	
	static boolean standAlone = false;
	

	/**
	 *  Constructor for the HOSECodeTest object
	 *
	 *@param  name  Description of the Parameter
	 */
	public HOSECodeTest(String name) {
		super(name);
	}

		/**
	 *  A unit test suite for JUnit
	 *
	 *@return    The test suite
	 */
	public static Test suite()
	{
		return new TestSuite(HOSECodeTest.class);
	}

	
	
	/**
	 *  A unit test for JUnit
	 *
	 *@return    Description of the Return Value
	 */
	public void test1()
	{
		Molecule molecule;
		String s = null;
		boolean isAromatic = false;
		try
		{
			molecule = MoleculeFactory.makeIndole();
			//display(molecule);
			isAromatic = HueckelAromaticityDetector.detectAromaticity(molecule);
			HOSECodeGenerator hcg = new HOSECodeGenerator();
			//System.out.println("Listing 1-sphere HOSE codes for Indole:\n");
			for (int f = 0; f < molecule.getAtomCount(); f++)
			{
				s = hcg.getHOSECode(molecule, molecule.getAtomAt(f), 1);
				//System.out.println("Atom " + (f + 1) + ": " + s);
			}
		} catch (Exception exc)
		{
			exc.printStackTrace();
		}
	}
	


	/**
	 *  A unit test for JUnit
	 *
	 *@return    Description of the Return Value
	 */
	public void test2()
	{
		try
		{
			Molecule molecule = (new SmilesParser()).parseSmiles("O=c1ccc2ccc4c5ccccc5Oc3c(OC)cc1c2c34");
			//display(molecule);
			boolean isAromatic = HueckelAromaticityDetector.detectAromaticity(molecule);
			HOSECodeGenerator hcg = new HOSECodeGenerator();
			//System.out.println("Listing 1-sphere HOSE codes for Indole:\n");
			String s = null;
			for (int f = 0; f < molecule.getAtomCount(); f++)
			{
				s = hcg.getHOSECode(molecule, molecule.getAtomAt(f), 4);
				//System.out.println("Atom " + (f + 1) + ": " + s);
			}
		} catch (Exception exc)
		{
			exc.printStackTrace();
		}

	}

	/**
	 *  A unit test for JUnit
	 *
	 *@return    Description of the Return Value
	 */
	public void test3()
	{
		try
		{
			Molecule molecule = (new SmilesParser()).parseSmiles("C12=CC=CC=C1NC=C2");
			//display(molecule);
			boolean isAromatic = HueckelAromaticityDetector.detectAromaticity(molecule);
			HOSECodeGenerator hcg = new HOSECodeGenerator();
			String s = null;
			for (int f = 0; f < molecule.getAtomCount(); f++)
			{
				s = hcg.getHOSECode(molecule, molecule.getAtomAt(f), 4);
				//System.out.println("Atom " + (f + 1) + ": " + s);
			}
		} catch (Exception exc)
		{
			exc.printStackTrace();
		}
}

	/**
	 *  A unit test for JUnit
	 *
	 *@return    Description of the Return Value
	 */
	public void test4()
	{
		try
		{
			Molecule molecule = (new SmilesParser()).parseSmiles("C1(C=CN2)=C2C=CC=C1");
			//display(molecule);
			boolean isAromatic = HueckelAromaticityDetector.detectAromaticity(molecule);
			HOSECodeGenerator hcg = new HOSECodeGenerator();
			String s = null;
			for (int f = 0; f < molecule.getAtomCount(); f++)
			{
				s = hcg.getHOSECode(molecule, molecule.getAtomAt(f), 4);
				//System.out.println("Atom " + (f + 1) + ": " + s);
			}
		} catch (Exception exc)
		{
			exc.printStackTrace();

		}

	}
	
	
	public void test5()
	{
		Molecule molecule = null;
		HOSECodeGenerator hcg = null;
		String[] codes = {
			"C-4;CCO(CCC,CO,/CC,CCO,,&,/*C,CC,&CC,&O,)",
		     "C-4;CCO(CO,C,/CCC,,&CC/C&O,CC,,,)",
		     "C-4;CCCC(CC,CCO,CO,/*C,CC,&O,CCC,,&O,/*C*O,&C,&O,C,&,,,)",
		     "O-2;C(CC/CCC,CO/CCO,CC,,&,)",
		     "C-4;CC(CCC,CO/CCO,,,&O,/C&C,CO,,)",
		     "O-2;C(CC/CO,C/CCC,,&CC)",
		     "C-4;CCCO(OC,CCC,CCC,/C,CO,C&,CO,,&,,/=OC,&C,C,*C,&O,)",
		     "C-4;CCC(*C,CC,CCC/*C*O,&C,CO,C&O,CO,/*C&,*C,=O&,&O,&,CCC,,&O,)",
		     "C-4;C(CCC/CC,CCO,CO/*C,CC,CCC,&O,,&O,)",
		     "C-4;CCCC(CCO,C,,/CCC,CO,,&O/CC,&O,,&O,C,)",
		     "C-4;COC(OC,C,CCO/C,CC,=OC,&CC,CCC,/=O&,&C,C&,,,CO,,&,,)",
		     "O-2;C(CCC/CCC,CO,CCC/CC,CO,,&O,C,&,,)",
		     "C-4;CCC(CC,CC,OC/*C*C,=O&,*&,CCC,&,&O/*&*O,*C,,&CO,CO,,C)",
		     "C-4;*CC(*C*O,CC/*CC,*C,CCC,&C/*&,&C,*&,CCO,CO,,&O)",
		     "C-4;C(CCC/CCO,C,/CCC,CO,,&O)",
		     "C-4;C(CCC/CCO,C,/CCC,CO,,&O)",
		     "C-4;CCO(CC,OC,C/C&,CC,C,&CO,=O&/*C*C,*&,&CC,=OC,CCC,,)",
		     "O-2;CC(=OC,CC/,,CCO,CO/CCC,CCC,,&C,C)",
		     "C-4;CCC(*C*C,=OO,CC/*O*C,*C,,&,&C,C&/*&,&,*&,CCC,&O)",
		     "C-3;*C*O*C(*CC,*C,C/*&,CC,*&,C&/,=OO,&C,CCC)",
		     "O-2;CC(C=O,CC/C&,,C&,CO/*C*C,CC,&CO,C)",
		     "C-3;=OOC(,C,/CC/CCO,CO)",
		     "C-3;*C*CC(*O*C,*C,CC/*&,C,*&,=OO,&C/,C&,,&,C&)",
		     "C-3;C=OO(CC,,C/*C*C,C&,&C/*C*O,*C,C&,CO)",
		     "O-2;*C*C(*C*C,*C/*&C,C,*&/,CC,C&)",
		     "C-4;C(=OO/,C/CC)",
		     "O-1;=C(OC/C,/CC)",
		     "C-3;*C*C(*CC,*O/*&*C,CC,*&/,C,=OO,&C)",
		     "O-1;=C(CO/CC,C/*C*C,C&,C&)",
		     "C-3;*C*O(*C,*C/*&C,*&*C/,CC,C)"};

		try
		{
			String filename = "data/HoseCodeTest.mol";
			InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
			MDLReader reader = new MDLReader(new InputStreamReader(ins));
			molecule = (Molecule)reader.read((ChemObject)new Molecule());
			new SaturationChecker().addImplicitHydrogensToSatisfyValency(molecule);
    
			boolean isAromatic = HueckelAromaticityDetector.detectAromaticity(molecule);
			for (int f = 0; f < molecule.getAtomCount(); f++)
			{
				//System.out.println("Atom " + molecule.getAtomAt(f).getSymbol() + "-" + (f + 1) + " isAromatic? " + molecule.getAtomAt(f).flags[CDKConstants.ISAROMATIC]);
			}
			for (int f = 0; f < molecule.getBondCount(); f++)
			{
				//System.out.println("Bond " + "-" + (f + 1) + " isAromatic? " + molecule.getBondAt(f).flags[CDKConstants.ISAROMATIC]);
			}

			hcg = new HOSECodeGenerator();
			String s = null;
			for (int f = 0; f < molecule.getAtomCount(); f++)
			{
				//System.out.println("Atom " + molecule.getAtomAt(f).getSymbol() + "-" + (f + 1));
				s = hcg.getHOSECode(molecule, molecule.getAtomAt(f), 4);
				assertTrue(s.equals(codes[f]));
				//System.out.println(s);
			}
		} catch (Exception exc)
		{
			exc.printStackTrace();
		}
		if (standAlone)
		{
			JFrame frame = new JFrame("HOSECodeTest");
			frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			frame.getContentPane().setLayout(new BorderLayout());
			DefaultMutableTreeNode top = hcg.getRootNode();
			StructureDiagramGenerator sdg = new StructureDiagramGenerator();
			MoleculeViewer2D mv = new MoleculeViewer2D();
			Renderer2DModel r2dm = mv.getRenderer2DModel();
			r2dm.setDrawNumbers(true);
	
			try
			{
				sdg.setMolecule((Molecule) molecule.clone());
				sdg.generateCoordinates(new Vector2d(0, 1));
				mv.setAtomContainer(sdg.getMolecule());
				//mv.display();
			} catch (Exception exc)
			{
				//System.out.println("*** Exit due to an unexpected error during coordinate generation ***");
				exc.printStackTrace();
			}
			
			final JTree tree = new JTree(top);
			JScrollPane treeView = new JScrollPane(tree);
			frame.getContentPane().add("West", treeView);
			mv.setPreferredSize(new Dimension(400,400));
			frame.getContentPane().add("Center", mv);
			for (int f = 0; f < tree.getRowCount(); f ++)
			{
				tree.expandRow(f);	
			}
			frame.pack();
			frame.show();
		}
	}
	
	public void testBug655169()
	{
		Molecule molecule = null;
		HOSECodeGenerator hcg = null;

		try
		{
			molecule = (new SmilesParser()).parseSmiles("CC=CBr");
			boolean isAromatic = HueckelAromaticityDetector.detectAromaticity(molecule);
			hcg = new HOSECodeGenerator();
			String s = null;
			for (int f = 0; f < molecule.getAtomCount(); f++)
			{
				//System.out.println("Atom " + molecule.getAtomAt(f).getSymbol() + "-" + (f + 1));
				s = hcg.getHOSECode(molecule, molecule.getAtomAt(f), 4);
				//System.out.println(molecule.getAtomAt(f).flags[CDKConstants.ISAROMATIC]);
				//System.out.println(s);
			}
            Bond[] bonds = molecule.getBonds();
			for (int f = 0; f < bonds.length; f++)
			{
				//System.out.println(bonds[f].flags[CDKConstants.ISAROMATIC]);
			}

		} catch (Exception exc)
		{
			exc.printStackTrace();
		}
		
		JFrame frame = new JFrame("HOSECodeTest");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());
		DefaultMutableTreeNode top = hcg.getRootNode();
		StructureDiagramGenerator sdg = new StructureDiagramGenerator();
		MoleculeViewer2D mv = new MoleculeViewer2D();
		Renderer2DModel r2dm = mv.getRenderer2DModel();
		r2dm.setDrawNumbers(true);

		try
		{
			sdg.setMolecule((Molecule) molecule.clone());
			sdg.generateCoordinates(new Vector2d(0, 1));
			mv.setAtomContainer(sdg.getMolecule());
			//mv.display();
		} catch (Exception exc)
		{
			//System.out.println("*** Exit due to an unexpected error during coordinate generation ***");
			exc.printStackTrace();
		}
		
		final JTree tree = new JTree(top);
		JScrollPane treeView = new JScrollPane(tree);
		frame.getContentPane().add("West", treeView);
		mv.setPreferredSize(new Dimension(400,400));
		frame.getContentPane().add("Center", mv);
		for (int f = 0; f < tree.getRowCount(); f ++)
		{
			tree.expandRow(f);	
		}
		frame.pack();
		frame.show();
	}

	private void assembleNodes(DefaultMutableTreeNode top, HOSECodeGenerator hcg)
	{
		DefaultMutableTreeNode node = null;
		
	}
	
	
	/**
	 *  Description of the Method
	 *
	 *@param  molecule  Description of the Parameter
	 */
	private void display(Molecule molecule)
	{
		StructureDiagramGenerator sdg = new StructureDiagramGenerator();
		MoleculeViewer2D mv = new MoleculeViewer2D();
		Renderer2DModel r2dm = mv.getRenderer2DModel();
		r2dm.setDrawNumbers(true);

		try
		{
			sdg.setMolecule((Molecule) molecule.clone());
			sdg.generateCoordinates(new Vector2d(0, 1));
			mv.setAtomContainer(sdg.getMolecule());
			mv.display();
		} catch (Exception exc)
		{
			System.out.println("*** Exit due to an unexpected error during coordinate generation ***");
			exc.printStackTrace();
		}
	}


	/**
	 *  The main program for the HOSECodeTest class
	 *
	 *@param  args  The command line arguments
	 */
	public static void main(String[] args)
	{
		standAlone = true;
		HOSECodeTest hct = new HOSECodeTest("HOSECodeTest");
		//hct.test1();
		//hct.test2();
		//hct.test3();
		//hct.test4();
		hct.test5();
		//hct.testBug655169();
	}
}

