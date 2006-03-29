/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2006  The Chemistry Development Kit (CKD) project
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
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk.test.tools;

import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.aromaticity.HueckelAromaticityDetector;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.HOSECodeGenerator;

/**
 *  Tests the HOSECode genertor. This is *not* a JUnit test class!
 *
 * @cdk.module test
 *
 *@author     steinbeck
 *@cdk.created    2002-11-16
 */
public class HOSECodeTest extends CDKTestCase
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
	public void test968852()
	{
    try{
        String filename = "data/mdl/2,5-dimethyl-furan.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLReader reader = new MDLReader(new InputStreamReader(ins));
				Molecule mol1 = (Molecule) reader.read(new Molecule());
        HueckelAromaticityDetector.detectAromaticity(mol1);
        assertEquals(new HOSECodeGenerator().getHOSECode(mol1, mol1.getAtoms()[2], 6),new HOSECodeGenerator().getHOSECode(mol1, mol1.getAtoms()[3], 6));
		} catch (Exception exc) {
			exc.printStackTrace();
            fail(exc.getMessage());
		}
	}


	/**
	 *  A unit test for JUnit
	 *
	 *@return    Description of the Return Value
	 */
	public void testSecondSphere()
	{
    try{
        String filename = "data/mdl/isopropylacetate.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLReader reader = new MDLReader(new InputStreamReader(ins));
				Molecule mol1 = (Molecule) reader.read(new Molecule());
        String code1=new HOSECodeGenerator().getHOSECode(mol1, mol1.getAtoms()[0], 6);
        filename="data/mdl/testisopropylacetate.mol";
        InputStream ins2 = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLReader reader2 = new MDLReader(new InputStreamReader(ins2));
				Molecule mol2 = (Molecule) reader2.read(new Molecule());
        String code2=new HOSECodeGenerator().getHOSECode(mol2, mol2.getAtoms()[2], 6);
        assertFalse(code1.equals(code2));
		} catch (Exception exc) {
			exc.printStackTrace();
            fail(exc.getMessage());
		}
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
			"C-3;=CC(//)",
			"C-3;=CC(//)",
			"C-3;*C*CC(//)",
			"C-3;*C*C(//)",
			"C-3;*C*C(//)",
			"C-3;*C*CC(//)",
			"C-3;*C*CC(//)",
			"C-3;*C*C(//)",
			"C-3;*C*C(//)",
			"C-3;*C*C(//)",
			"C-3;*C*C(//)",
			"C-3;*C*CO(//)",
			"O-2;CC(//)",
			"C-3;*C*CO(//)",
			"C-3;*C*CO(//)",
			"O-2;CC(//)",
			"C-4;O(//)",
			"C-3;*C*C(//)",
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
        if (standAlone)
          System.out.print("|" + s + "| -> " + result[f]);
				assertEquals(result[f], s);
        if (standAlone)
          System.out.println("  OK");
			}
		} catch (Exception exc) {
			exc.printStackTrace();
            fail(exc.getMessage());
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
        if (standAlone)
          System.out.print("|" + s + "| -> " + result[f]);
				assertEquals(result[f], s);
        if (standAlone)
          System.out.println("  OK");
			}
		} catch (Exception exc)
		{
			exc.printStackTrace();
            fail(exc.getMessage());
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
"C-3;=OCC(,*C*C,=C/*C*C,*C,&/*C*C,*C&,*&O)",
"C-3;=CC(C,=OC/*C*C,,*&*C/*C*&,*C,*C)",
"C-3;=CC(C,*C*C/=OC,*C*&,*C/,*&*C,*C*C,*&)",
"C-3;*C*CC(*C*C,*C,=C/*C*C,*CC,*&,&/*C,*&C,O,*&,=O&)",
"C-3;*C*C(*CC,*C/*C*C,=C,*&C/*C*&,*CC,&,*C*C)",
"C-3;*C*C(*CC,*C/*C*C,*C*C,*&C/*C*&,*CO,*C&,*C,=C)",
"C-3;*C*CC(*C*C,*C,*C*C/*C*C,*CO,*&,*C&,*C/*CC,*&C,*&O,&,*C,*&)",
"C-3;*C*CC(*CO,*C,*C*C/*C,C,*&,*C*&,*C/*&,*&*C,*C*C,*&)",
"C-3;*C*C(*CC,*C/*CO,*C*C,*&/*&,C,*C*&,*C)",
"C-3;*C*C(*C,*C/*CC,*&/*&O,*C*C)",
"C-3;*C*C(*C,*C/*CO,*&/*&C,C)",
"C-3;*C*C(*CO,*C/*CC,C,*&/*&,*C*C,*&*C)",
"C-3;*C*CO(*CC,*C,C/*C,*C*C,*&,*&*C/*&,*C*&,*C,*CO)",
"O-2;CC(*C*C,*C*C/*C*C,*CO,*C&,*C/*C*C,*C&,*&,C,*C,*&)",
"C-3;*C*CO(*C*C,*CO,C/*C*C,*CC,*&,C,*&*C/*&C,*CC,*&,*&*C,,*C)",
"C-3;*C*CO(*CO,*C,C/*C*C,C,*&C,/*&*C,*CC,*&*C,=OC)",
"O-2;CC(*C*C,/*CO,*C/*C*C,C,*&C)",
"C-4;O(C/*C*C/*CO,*C)",
"C-3;*C*C(*CC,*CO/*C*C,=OC,*&O,C/*&*C,*CC,,=&,C,)",
"C-3;*C*CC(*C*C,*C,=OC/*C*C,*CC,*&O,,=&/*&,*CC,O,*&,=&,C)",
"C-3;*C*C*C(*C*C,*CC,*CC/*C,*CC,O,*&,=OC,*&,=&/*&O,*&,*C*C,&,,=&)",
"C-3;*C*C*C(*C*C,*C,*CC,O/*CC,*CC,*&O,*&,*C*C,&/*&,=OC,*&,=&,C,*C&,*C)"
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
        if (standAlone)
          System.out.println(f+"|" + s + "| -> " + result[f]);
				assertEquals(result[f], s);
        if (standAlone)
          System.out.println("  OK");
			}
		} catch (Exception exc)
		{
			exc.printStackTrace();
            fail(exc.getMessage());
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
     "C-3;*C*C(*C*C,*N/*C*&,*C,*&/*C,*&)",
     "C-3;*C*N(*C,*C/*&*C,*&*C/,*C,*C)",
     "N-3;*C*C(*C*C,*C/*C*&,*C,*&/*C,*&)",
     "C-3;*C*C*N(*C*C,*C,*C/*C,*&,*&,*&/*&)",
     "C-3;*C*C(*C*N,*C/*C*C,*C,*&/*&,*&,*&)",
     "C-3;*C*C(*C,*C/*C*N,*&/*&*C,*C)",
     "C-3;*C*C(*C,*C/*C*C,*&/*&*N,*C)",
     "C-3;*C*C(*C*C,*C/*C*N,*C,*&/*&,*&,*&)"};
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
        if (standAlone)
          System.out.println(f+"|" + s + "| -> " + result[f]);
				assertEquals(result[f], s);
        if (standAlone)
          System.out.println("  OK");
			}
		} catch (Exception exc)
		{
			exc.printStackTrace();
            fail(exc.getMessage());
		}

	}
	
	public void testBug655169()
	{
		Molecule molecule = null;
		HOSECodeGenerator hcg = null;
		String[] result = {
		    "C-4;C(=C/Y/)",
     "C-3;=CC(Y,//)",
     "C-3;=CY(C,//)",
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
        if (standAlone)
          System.out.print("|" + s + "| -> " + result[f]);
				assertEquals(result[f], s);
        if (standAlone)
          System.out.println("  OK");
			}

		} catch (Exception exc)
		{
			exc.printStackTrace();
            fail(exc.getMessage());
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

  public void testBug795480()
	{
		Molecule molecule = null;
		HOSECodeGenerator hcg = null;
		String[] result = {
		    "C-4-;C(=C/Y'+4'/)",
     "C-3;=CC-(Y'+4',//)",
     "C-3;=CY'+4'(C-,//)",
     "Br-1'+4';C(=C/C-/)"
		};

		try
		{
			molecule = (new SmilesParser()).parseSmiles("CC=CBr");
            boolean isAromatic = HueckelAromaticityDetector.detectAromaticity(molecule);
            assertFalse(isAromatic);
			molecule.getAtomAt(0).setFormalCharge(-1);
            molecule.getAtomAt(3).setFormalCharge(+4);
			hcg = new HOSECodeGenerator();
			String s = null;
			for (int f = 0; f < molecule.getAtomCount(); f++)
			{
				s = hcg.getHOSECode(molecule, molecule.getAtomAt(f), 4);
                if (standAlone)
                    System.out.print("|" + s + "| -> " + result[f]);
				assertEquals(result[f], s);
                if (standAlone)
                    System.out.println("  OK");
			}
            
		} catch (Exception exc)
		{
			exc.printStackTrace();
            fail(exc.getMessage());
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

