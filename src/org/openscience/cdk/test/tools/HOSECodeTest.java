/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2003  The Chemistry Development Kit (CKD) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
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
package org.openscience.cdk.test.tools;

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
 *@created    2002-11-16
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
	public void test1Sphere()
	{
		String[] result = 
		{ 
			"O-1;=C(//)",
			"C-3;=OCC(//)",
			"C-2;=CC(//)",
			"C-2;=CC(//)",
			"C-3;*C*CC(//)",
			"C-2;*C*C(//)",
			"C-2;*C*C(//)",
			"C-3;*C*CC(//)",
			"C-3;*C*CC(//)",
			"C-2;*C*C(//)",
			"C-2;*C*C(//)",
			"C-2;*C*C(//)",
			"C-2;*C*C(//)",
			"C-3;*C*CO(//)",
			"O-2;CC(//)",
			"C-3;*C*CO(//)",
			"C-3;*C*CO(//)",
			"O-2;CC(//)",
			"C-1;O(//)",
			"C-2;*C*C(//)",
			"C-3;*C*CC(//)",
			"C-3;*C*C*C(//)",
			"C-3;*C*C*C(//)"
		};
		
		try
		{
			Molecule molecule = (new SmilesParser()).parseSmiles("O=C1C=Cc2ccc4c5ccccc5Oc3c(OC)cc1c2c34");
			

			//MoleculeViewer2D.display(molecule, true);
			boolean isAromatic = HueckelAromaticityDetector.detectAromaticity(molecule);
			HOSECodeGenerator hcg = new HOSECodeGenerator();
			String s = null;
			for (int f = 0; f < molecule.getAtomCount(); f++)
			{
				s = hcg.getHOSECode(molecule, molecule.getAtomAt(f), 1);
				//System.out.println("|" + s + "|,");
				assertTrue(result[f].equals(s));
				
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
	public void testMakeBremserCompliant()
	{
		String[] startData = { 
     "O-1;=C(//)",
     "C-3;=OCC(//)",
     "C-2;CC(//)",
     "C-2;CC(//)",
     "C-3;CCC(//)",
     "C-2;CC(//)",
     "C-2;CC(//)",
     "C-3;CCC(//)",
     "C-3;CCC(//)",
     "C-2;CC(//)",
     "C-2;CC(//)",
     "C-2;CC(//)",
     "C-2;CC(//)",
     "C-3;CCO(//)",
     "O-2;CC(//)",
     "C-3;CCO(//)",
     "C-3;CCO(//)",
     "O-2;CC(//)",
     "C-1;O(//)",
     "C-2;CC(//)",
     "C-3;CCC(//)",
     "C-3;CCC(//)",
     "C-3;CCC(//)"};

		String[] result = { 
     "=C(//)",
     "=OCC(//)",
     "CC(//)",
     "CC(//)",
     "CCC(//)",
     "CC(//)",
     "CC(//)",
     "CCC(//)",
     "CCC(//)",
     "CC(//)",
     "CC(//)",
     "CC(//)",
     "CC(//)",
     "CCO(//)",
     "CC(//)",
     "CCO(//)",
     "CCO(//)",
     "CC(//)",
     "O(//)",
     "CC(//)",
     "CCC(//)",
     "CCC(//)",
     "CCC(//)"
		};
		
		try
		{
			String s = null;
			HOSECodeGenerator hcg = new HOSECodeGenerator();
			for (int f = 0; f < startData.length; f++)
			{
				s = hcg.makeBremserCompliant(startData[f]);
				assertTrue(result[f].equals(s));
				
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
	public void test4Sphere()
	{
		String[] result = { 
			
"O-1;=C(CC/*C*C,=C/*C*C,*C,&)",
"C-3;CC=O(*C*C,=C,/*C*C,*C,&/*C*C,*C&,*&O)",
"C-2;C=C(C=O,C/*C*C,,*&*C/*C*&,*C,*C)",
"C-2;C=C(*C*C,C/*C*C,*C,&=O/*C*C,*C&,*&,)",
"C-3;*C*CC(*C*C,*C,=C/*C*C,C*C,*&,&/*&C,*CO,=O&,*&)",
"C-2;*C*C(*CC,*C/*C*C,=C,*&C/*C*&,*CC,&,*C*C)",
"C-2;*C*C(*CC,*C/*C*C,*C*C,*&C/*C*&,*CO,*C&,*C,=C)",
"C-3;*CC*C(*C*C,*C*C,*C/*C*C,*CO,*C&,*C,*&/*CC,*&C,*&O,&,*C,*&)",
"C-3;C*C*C(*C*C,*CO,*C/*C*C,*C,*C,&,*&/*C*C,*C&,*&,*&)",
"C-2;*C*C(C*C,*C/*C*C,*CO,*&/*C*C,*C,*&,&)",
"C-2;*C*C(*C,*C/C*C,*&/*C*C,*&O)",
"C-2;*C*C(*C,*C/*CO,*&/*&C,C)",
"C-2;*C*C(*CO,*C/C*C,C,*&/*C*C,*&,*&*C)",
"C-3;*CO*C(C*C,C,*C/*C*C,*C,*&*C,*&/*C*&,*C,*&,*CO)",
"O-2;CC(*C*C,*C*C/*C*C,*CO,&*C,*C/*C*C,*C&,*&,C,*C,*&)",
"C-3;*C*CO(*C*C,*CO,C/*C*C,C*C,*&,C,*&*C/*&C,*CC,*&*C,*&,,*C)",
"C-3;*C*CO(*CO,*C,C/*C*C,C,*&C,/*&*C,*CC,*&*C,=OC)",
"O-2;CC(*C*C,/*CO,*C/*C*C,C,*&C)",
"C-1;O(C/*C*C/*CO,*C)",
"C-2;*C*C(*CC,*CO/*C*C,C=O,*&O,C/*&*C,*CC,=&,,C,)",
"C-3;*C*CC(*C*C,*C,C=O/*C*C,*CC,*&O,=&,/*CC,*&O,*&,=&,C)",
"C-3;*C*C*C(*C*C,C*C,*CC/C*C,*CO,C=O,*&,*&,=&/*C*C,*&,*&O,&,=&,)",
"C-3;*C*C*C(*C*C,C*C,*CO/C*C,*CC,*C*C,*&,*&O,&/=OC,*&,*&,=&,*C&,*C,C)"
		};
		
		try
		{
			Molecule molecule = (new SmilesParser()).parseSmiles("O=C1C=Cc2ccc4c5ccccc5Oc3c(OC)cc1c2c34");
			boolean isAromatic = HueckelAromaticityDetector.detectAromaticity(molecule);
			HOSECodeGenerator hcg = new HOSECodeGenerator();
			String s = null;
			for (int f = 0; f < molecule.getAtomCount(); f++)
			{
				s = hcg.getHOSECode(molecule, molecule.getAtomAt(f), 4);
				assertTrue(result[f].equals(s));
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
		String[] result = {
		     "C-3;*C*C*C(*C*N,*C,*C/*C,*&,*&,*&/*&)",
     "C-2;*C*C(*C*C,*N/*C*&,*C,*&/*C,*&)",
     "C-2;*C*N(*C,*C/*&*C,*&*C/,*C,*C)",
     "N-2;*C*C(*C*C,*C/*&*C,*C,*&/,*C,*&)",
     "C-3;*C*C*N(*C*C,*C,*C/*C,*&,*&,*&/*&)",
     "C-2;*C*C(*C*N,*C/*C*C,*C,*&/*&,*&,*&)",
     "C-2;*C*C(*C,*C/*C*N,*&/*C*&,*C)",
     "C-2;*C*C(*C,*C/*C*C,*&/*&*N,*C)",
     "C-2;*C*C(*C*C,*C/*C*N,*C,*&/*&,*&,*&)"};
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
				//System.out.println("\"" + s + "\",");
				assertTrue(result[f].equals(s));
			}
		} catch (Exception exc)
		{
			exc.printStackTrace();

		}

	}
	
	public void testBug655169()
	{
		Molecule molecule = null;
		HOSECodeGenerator hcg = null;
		String[] result = {
		    "C-1;C(=C/Y/)",
     "C-2;=CC(Y,//)",
     "C-2;=CY(C,//)",
     "Br-1;C(=C/C/)"
		};

		try
		{
			molecule = (new SmilesParser()).parseSmiles("CC=CBr");
			boolean isAromatic = HueckelAromaticityDetector.detectAromaticity(molecule);
			hcg = new HOSECodeGenerator();
			String s = null;
			for (int f = 0; f < molecule.getAtomCount(); f++)
			{
				s = hcg.getHOSECode(molecule, molecule.getAtomAt(f), 4);
				assertTrue(result[f].equals(s));
			}

		} catch (Exception exc)
		{
			exc.printStackTrace();
		}
		
		/*JFrame frame = new JFrame("HOSECodeTest");
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
		frame.show(); */
	}

	private void assembleNodes(DefaultMutableTreeNode top, HOSECodeGenerator hcg)
	{
		DefaultMutableTreeNode node = null;
		
	}
	
	
  public void testBug795480()
	{
		Molecule molecule = null;
		HOSECodeGenerator hcg = null;
		String[] result = {
		    "C-1-;C(=C/Y'+4'/)",
     "C-2;=CC-(Y'+4',//)",
     "C-2;=CY'+4'(C-,//)",
     "Br-1'+4';C(=C/C-/)"
		};

		try
		{
			molecule = (new SmilesParser()).parseSmiles("CC=CBr");
      boolean isAromatic = HueckelAromaticityDetector.detectAromaticity(molecule);
			molecule.getAtomAt(0).setFormalCharge(-1);
      molecule.getAtomAt(3).setFormalCharge(+4);
			hcg = new HOSECodeGenerator();
			String s = null;
			for (int f = 0; f < molecule.getAtomCount(); f++)
			{
				s = hcg.getHOSECode(molecule, molecule.getAtomAt(f), 4);
      	assertTrue(result[f].equals(s));
			}

		} catch (Exception exc)
		{
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
		hct.test4Sphere();
		//hct.testBug655169();
	}
}

