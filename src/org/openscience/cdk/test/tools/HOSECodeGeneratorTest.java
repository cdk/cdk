/*  $Revision$ $Author$ $Date$
 *
 *  Copyright (C) 1997-2007  The Chemistry Development Kit (CKD) project
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
import java.util.List;

import javax.vecmath.Point2d;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.aromaticity.HueckelAromaticityDetector;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.HOSECodeGenerator;

/**
 * Tests the HOSECode generator.
 *
 * @cdk.module  test-extra
 * @author      steinbeck
 * @cdk.created 2002-11-16
 */
public class HOSECodeGeneratorTest extends CDKTestCase
{
	
	static boolean standAlone = false;
	

	/**
	 *  Constructor for the HOSECodeTest object
	 *
	 *@param  name  Description of the Parameter
	 */
	public HOSECodeGeneratorTest(String name) {
		super(name);
	}

		/**
	 *  A unit test suite for JUnit
	 *
	 *@return    The test suite
	 */
	public static Test suite()
	{
		return new TestSuite(HOSECodeGeneratorTest.class);
	}

	
	/**
	 * @cdk.bug 968852
	 */
	public void test968852() throws Exception {
        String filename = "data/mdl/2,5-dimethyl-furan.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLReader reader = new MDLReader(ins);
				Molecule mol1 = (Molecule) reader.read(new Molecule());
        HueckelAromaticityDetector.detectAromaticity(mol1);
        assertEquals(new HOSECodeGenerator().getHOSECode(mol1, mol1.getAtom(2), 6),new HOSECodeGenerator().getHOSECode(mol1, mol1.getAtom(3), 6));
	}


	/**
	 *  A unit test for JUnit
	 *
	 *@return    Description of the Return Value
	 */
	public void testSecondSphere() throws Exception {
        String filename = "data/mdl/isopropylacetate.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLReader reader = new MDLReader(ins);
				Molecule mol1 = (Molecule) reader.read(new Molecule());
        String code1=new HOSECodeGenerator().getHOSECode(mol1, mol1.getAtom(0), 6);
        filename="data/mdl/testisopropylacetate.mol";
        InputStream ins2 = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLReader reader2 = new MDLReader(ins2);
				Molecule mol2 = (Molecule) reader2.read(new Molecule());
        String code2=new HOSECodeGenerator().getHOSECode(mol2, mol2.getAtom(2), 6);
        assertFalse(code1.equals(code2));
	}


	/**
	 *  A unit test for JUnit
	 *
	 *@return    Description of the Return Value
	 */
	public void test1Sphere() throws Exception
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
		
		  IMolecule mol = new Molecule();
		  IAtom a1 = mol.getBuilder().newAtom("O");
		  a1.setPoint2d(new Point2d(502.88457268119913, 730.4999999999999));  mol.addAtom(a1);
		  IAtom a2 = mol.getBuilder().newAtom("C");
		  a2.setPoint2d(new Point2d(502.8845726811991, 694.4999999999999));  mol.addAtom(a2);
		  IAtom a3 = mol.getBuilder().newAtom("C");
		  a3.setPoint2d(new Point2d(534.0614872174388, 676.4999999999999));  mol.addAtom(a3);
		  IAtom a4 = mol.getBuilder().newAtom("C");
		  a4.setPoint2d(new Point2d(534.0614872174388, 640.4999999999999));  mol.addAtom(a4);
		  IAtom a5 = mol.getBuilder().newAtom("C");
		  a5.setPoint2d(new Point2d(502.8845726811991, 622.4999999999999));  mol.addAtom(a5);
		  IAtom a6 = mol.getBuilder().newAtom("C");
		  a6.setPoint2d(new Point2d(502.8845726811991, 586.4999999999999));  mol.addAtom(a6);
		  IAtom a7 = mol.getBuilder().newAtom("C");
		  a7.setPoint2d(new Point2d(471.7076581449593, 568.4999999999999));  mol.addAtom(a7);
		  IAtom a8 = mol.getBuilder().newAtom("C");
		  a8.setPoint2d(new Point2d(440.5307436087194, 586.5));  mol.addAtom(a8);
		  IAtom a9 = mol.getBuilder().newAtom("C");
		  a9.setPoint2d(new Point2d(409.35382907247964, 568.5));  mol.addAtom(a9);
		  IAtom a10 = mol.getBuilder().newAtom("C");
		  a10.setPoint2d(new Point2d(409.3538290724796, 532.5));  mol.addAtom(a10);
		  IAtom a11 = mol.getBuilder().newAtom("C");
		  a11.setPoint2d(new Point2d(378.1769145362398, 514.5));  mol.addAtom(a11);
		  IAtom a12 = mol.getBuilder().newAtom("C");
		  a12.setPoint2d(new Point2d(347.0, 532.5));  mol.addAtom(a12);
		  IAtom a13 = mol.getBuilder().newAtom("C");
		  a13.setPoint2d(new Point2d(347.0, 568.5));  mol.addAtom(a13);
		  IAtom a14 = mol.getBuilder().newAtom("C");
		  a14.setPoint2d(new Point2d(378.17691453623985, 586.5));  mol.addAtom(a14);
		  IAtom a15 = mol.getBuilder().newAtom("O");
		  a15.setPoint2d(new Point2d(378.17691453623985, 622.5));  mol.addAtom(a15);
		  IAtom a16 = mol.getBuilder().newAtom("C");
		  a16.setPoint2d(new Point2d(409.3538290724797, 640.5));  mol.addAtom(a16);
		  IAtom a17 = mol.getBuilder().newAtom("C");
		  a17.setPoint2d(new Point2d(409.3538290724797, 676.5));  mol.addAtom(a17);
		  IAtom a18 = mol.getBuilder().newAtom("O");
		  a18.setPoint2d(new Point2d(378.17691453623996, 694.5));  mol.addAtom(a18);
		  IAtom a19 = mol.getBuilder().newAtom("C");
		  a19.setPoint2d(new Point2d(378.17691453624, 730.5));  mol.addAtom(a19);
		  IAtom a20 = mol.getBuilder().newAtom("C");
		  a20.setPoint2d(new Point2d(440.5307436087195, 694.4999999999999));  mol.addAtom(a20);
		  IAtom a21 = mol.getBuilder().newAtom("C");
		  a21.setPoint2d(new Point2d(471.7076581449593, 676.4999999999999));  mol.addAtom(a21);
		  IAtom a22 = mol.getBuilder().newAtom("C");
		  a22.setPoint2d(new Point2d(471.7076581449593, 640.4999999999999));  mol.addAtom(a22);
		  IAtom a23 = mol.getBuilder().newAtom("C");
		  a23.setPoint2d(new Point2d(440.53074360871943, 622.4999999999999));  mol.addAtom(a23);
		  IBond b1 = mol.getBuilder().newBond(a2, a1, 2.0);
		  mol.addBond(b1);
		  IBond b2 = mol.getBuilder().newBond(a3, a2, 1.0);
		  mol.addBond(b2);
		  IBond b3 = mol.getBuilder().newBond(a4, a3, 2.0);
		  mol.addBond(b3);
		  IBond b4 = mol.getBuilder().newBond(a5, a4, 1.0);
		  mol.addBond(b4);
		  IBond b5 = mol.getBuilder().newBond(a6, a5, 1.0);
		  mol.addBond(b5);
		  IBond b6 = mol.getBuilder().newBond(a7, a6, 2.0);
		  mol.addBond(b6);
		  IBond b7 = mol.getBuilder().newBond(a8, a7, 1.0);
		  mol.addBond(b7);
		  IBond b8 = mol.getBuilder().newBond(a9, a8, 1.0);
		  mol.addBond(b8);
		  IBond b9 = mol.getBuilder().newBond(a10, a9, 1.0);
		  mol.addBond(b9);
		  IBond b10 = mol.getBuilder().newBond(a11, a10, 2.0);
		  mol.addBond(b10);
		  IBond b11 = mol.getBuilder().newBond(a12, a11, 1.0);
		  mol.addBond(b11);
		  IBond b12 = mol.getBuilder().newBond(a13, a12, 2.0);
		  mol.addBond(b12);
		  IBond b13 = mol.getBuilder().newBond(a14, a13, 1.0);
		  mol.addBond(b13);
		  IBond b14 = mol.getBuilder().newBond(a14, a9, 2.0);
		  mol.addBond(b14);
		  IBond b15 = mol.getBuilder().newBond(a15, a14, 1.0);
		  mol.addBond(b15);
		  IBond b16 = mol.getBuilder().newBond(a16, a15, 1.0);
		  mol.addBond(b16);
		  IBond b17 = mol.getBuilder().newBond(a17, a16, 2.0);
		  mol.addBond(b17);
		  IBond b18 = mol.getBuilder().newBond(a18, a17, 1.0);
		  mol.addBond(b18);
		  IBond b19 = mol.getBuilder().newBond(a19, a18, 1.0);
		  mol.addBond(b19);
		  IBond b20 = mol.getBuilder().newBond(a20, a17, 1.0);
		  mol.addBond(b20);
		  IBond b21 = mol.getBuilder().newBond(a21, a20, 2.0);
		  mol.addBond(b21);
		  IBond b22 = mol.getBuilder().newBond(a21, a2, 1.0);
		  mol.addBond(b22);
		  IBond b23 = mol.getBuilder().newBond(a22, a21, 1.0);
		  mol.addBond(b23);
		  IBond b24 = mol.getBuilder().newBond(a22, a5, 2.0);
		  mol.addBond(b24);
		  IBond b25 = mol.getBuilder().newBond(a23, a22, 1.0);
		  mol.addBond(b25);
		  IBond b26 = mol.getBuilder().newBond(a23, a16, 1.0);
		  mol.addBond(b26);
		  IBond b27 = mol.getBuilder().newBond(a23, a8, 2.0);
		  mol.addBond(b27);
		  
		  addImplicitHydrogens(mol);

		//MoleculeViewer2D.display(molecule, true);
		HueckelAromaticityDetector.detectAromaticity(mol);
		HOSECodeGenerator hcg = new HOSECodeGenerator();
		String s = null;
		for (int f = 0; f < 23; f++)
		{
			s = hcg.getHOSECode(mol, mol.getAtom(f), 1);
			if (standAlone)
				System.out.print("|" + s + "| -> " + result[f]);
			assertEquals(result[f], s);
			if (standAlone)
				System.out.println("  OK");
		}
	}

	/**
	 *  A unit test for JUnit
	 *
	 *@return    Description of the Return Value
	 */
	public void testMakeBremserCompliant() throws Exception
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
	}	
	
	/**
	 *  A unit test for JUnit
	 *
	 *@return    Description of the Return Value
	 */
	public void test4Sphere() throws Exception
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
		
		  IMolecule mol = new Molecule();
		  IAtom a1 = mol.getBuilder().newAtom("O");
		  a1.setPoint2d(new Point2d(502.88457268119913, 730.4999999999999));  mol.addAtom(a1);
		  IAtom a2 = mol.getBuilder().newAtom("C");
		  a2.setPoint2d(new Point2d(502.8845726811991, 694.4999999999999));  mol.addAtom(a2);
		  IAtom a3 = mol.getBuilder().newAtom("C");
		  a3.setPoint2d(new Point2d(534.0614872174388, 676.4999999999999));  mol.addAtom(a3);
		  IAtom a4 = mol.getBuilder().newAtom("C");
		  a4.setPoint2d(new Point2d(534.0614872174388, 640.4999999999999));  mol.addAtom(a4);
		  IAtom a5 = mol.getBuilder().newAtom("C");
		  a5.setPoint2d(new Point2d(502.8845726811991, 622.4999999999999));  mol.addAtom(a5);
		  IAtom a6 = mol.getBuilder().newAtom("C");
		  a6.setPoint2d(new Point2d(502.8845726811991, 586.4999999999999));  mol.addAtom(a6);
		  IAtom a7 = mol.getBuilder().newAtom("C");
		  a7.setPoint2d(new Point2d(471.7076581449593, 568.4999999999999));  mol.addAtom(a7);
		  IAtom a8 = mol.getBuilder().newAtom("C");
		  a8.setPoint2d(new Point2d(440.5307436087194, 586.5));  mol.addAtom(a8);
		  IAtom a9 = mol.getBuilder().newAtom("C");
		  a9.setPoint2d(new Point2d(409.35382907247964, 568.5));  mol.addAtom(a9);
		  IAtom a10 = mol.getBuilder().newAtom("C");
		  a10.setPoint2d(new Point2d(409.3538290724796, 532.5));  mol.addAtom(a10);
		  IAtom a11 = mol.getBuilder().newAtom("C");
		  a11.setPoint2d(new Point2d(378.1769145362398, 514.5));  mol.addAtom(a11);
		  IAtom a12 = mol.getBuilder().newAtom("C");
		  a12.setPoint2d(new Point2d(347.0, 532.5));  mol.addAtom(a12);
		  IAtom a13 = mol.getBuilder().newAtom("C");
		  a13.setPoint2d(new Point2d(347.0, 568.5));  mol.addAtom(a13);
		  IAtom a14 = mol.getBuilder().newAtom("C");
		  a14.setPoint2d(new Point2d(378.17691453623985, 586.5));  mol.addAtom(a14);
		  IAtom a15 = mol.getBuilder().newAtom("O");
		  a15.setPoint2d(new Point2d(378.17691453623985, 622.5));  mol.addAtom(a15);
		  IAtom a16 = mol.getBuilder().newAtom("C");
		  a16.setPoint2d(new Point2d(409.3538290724797, 640.5));  mol.addAtom(a16);
		  IAtom a17 = mol.getBuilder().newAtom("C");
		  a17.setPoint2d(new Point2d(409.3538290724797, 676.5));  mol.addAtom(a17);
		  IAtom a18 = mol.getBuilder().newAtom("O");
		  a18.setPoint2d(new Point2d(378.17691453623996, 694.5));  mol.addAtom(a18);
		  IAtom a19 = mol.getBuilder().newAtom("C");
		  a19.setPoint2d(new Point2d(378.17691453624, 730.5));  mol.addAtom(a19);
		  IAtom a20 = mol.getBuilder().newAtom("C");
		  a20.setPoint2d(new Point2d(440.5307436087195, 694.4999999999999));  mol.addAtom(a20);
		  IAtom a21 = mol.getBuilder().newAtom("C");
		  a21.setPoint2d(new Point2d(471.7076581449593, 676.4999999999999));  mol.addAtom(a21);
		  IAtom a22 = mol.getBuilder().newAtom("C");
		  a22.setPoint2d(new Point2d(471.7076581449593, 640.4999999999999));  mol.addAtom(a22);
		  IAtom a23 = mol.getBuilder().newAtom("C");
		  a23.setPoint2d(new Point2d(440.53074360871943, 622.4999999999999));  mol.addAtom(a23);
		  IBond b1 = mol.getBuilder().newBond(a2, a1, 2.0);
		  mol.addBond(b1);
		  IBond b2 = mol.getBuilder().newBond(a3, a2, 1.0);
		  mol.addBond(b2);
		  IBond b3 = mol.getBuilder().newBond(a4, a3, 2.0);
		  mol.addBond(b3);
		  IBond b4 = mol.getBuilder().newBond(a5, a4, 1.0);
		  mol.addBond(b4);
		  IBond b5 = mol.getBuilder().newBond(a6, a5, 1.0);
		  mol.addBond(b5);
		  IBond b6 = mol.getBuilder().newBond(a7, a6, 2.0);
		  mol.addBond(b6);
		  IBond b7 = mol.getBuilder().newBond(a8, a7, 1.0);
		  mol.addBond(b7);
		  IBond b8 = mol.getBuilder().newBond(a9, a8, 1.0);
		  mol.addBond(b8);
		  IBond b9 = mol.getBuilder().newBond(a10, a9, 1.0);
		  mol.addBond(b9);
		  IBond b10 = mol.getBuilder().newBond(a11, a10, 2.0);
		  mol.addBond(b10);
		  IBond b11 = mol.getBuilder().newBond(a12, a11, 1.0);
		  mol.addBond(b11);
		  IBond b12 = mol.getBuilder().newBond(a13, a12, 2.0);
		  mol.addBond(b12);
		  IBond b13 = mol.getBuilder().newBond(a14, a13, 1.0);
		  mol.addBond(b13);
		  IBond b14 = mol.getBuilder().newBond(a14, a9, 2.0);
		  mol.addBond(b14);
		  IBond b15 = mol.getBuilder().newBond(a15, a14, 1.0);
		  mol.addBond(b15);
		  IBond b16 = mol.getBuilder().newBond(a16, a15, 1.0);
		  mol.addBond(b16);
		  IBond b17 = mol.getBuilder().newBond(a17, a16, 2.0);
		  mol.addBond(b17);
		  IBond b18 = mol.getBuilder().newBond(a18, a17, 1.0);
		  mol.addBond(b18);
		  IBond b19 = mol.getBuilder().newBond(a19, a18, 1.0);
		  mol.addBond(b19);
		  IBond b20 = mol.getBuilder().newBond(a20, a17, 1.0);
		  mol.addBond(b20);
		  IBond b21 = mol.getBuilder().newBond(a21, a20, 2.0);
		  mol.addBond(b21);
		  IBond b22 = mol.getBuilder().newBond(a21, a2, 1.0);
		  mol.addBond(b22);
		  IBond b23 = mol.getBuilder().newBond(a22, a21, 1.0);
		  mol.addBond(b23);
		  IBond b24 = mol.getBuilder().newBond(a22, a5, 2.0);
		  mol.addBond(b24);
		  IBond b25 = mol.getBuilder().newBond(a23, a22, 1.0);
		  mol.addBond(b25);
		  IBond b26 = mol.getBuilder().newBond(a23, a16, 1.0);
		  mol.addBond(b26);
		  IBond b27 = mol.getBuilder().newBond(a23, a8, 2.0);
		  mol.addBond(b27);
		  
		  addImplicitHydrogens(mol);
		HueckelAromaticityDetector.detectAromaticity(mol);
		HOSECodeGenerator hcg = new HOSECodeGenerator();
		String s = null;
		for (int f = 0; f < mol.getAtomCount(); f++)
		{
			s = hcg.getHOSECode(mol, mol.getAtom(f), 4);
			if (standAlone)
				System.out.println(f+"|" + s + "| -> " + result[f]);
			assertEquals(result[f], s);
			if (standAlone)
				System.out.println("  OK");
		}
	}


	/**
	 *  A unit test for JUnit
	 *
	 *@return    Description of the Return Value
	 */
	public void test4() throws Exception
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

		IMolecule molecule = (new SmilesParser(org.openscience.cdk.DefaultChemObjectBuilder.getInstance())).parseSmiles("C1(C=CN2)=C2C=CC=C1");
		//display(molecule);
		HueckelAromaticityDetector.detectAromaticity(molecule);
		HOSECodeGenerator hcg = new HOSECodeGenerator();
		String s = null;
		for (int f = 0; f < molecule.getAtomCount(); f++)
		{
			s = hcg.getHOSECode(molecule, molecule.getAtom(f), 4);
			if (standAlone)
				System.out.println(f+"|" + s + "| -> " + result[f]);
			assertEquals(result[f], s);
			if (standAlone)
				System.out.println("  OK");
		}
	}

	/**
	 * @cdk.bug 655169
	 */
	public void testBug655169() throws Exception
	{
		IMolecule molecule = null;
		HOSECodeGenerator hcg = null;
		String[] result = {
		    "C-4;C(=C/Y/)",
     "C-3;=CC(Y,//)",
     "C-3;=CY(C,//)",
     "Br-1;C(=C/C/)"
		};

		molecule = (new SmilesParser(org.openscience.cdk.DefaultChemObjectBuilder.getInstance())).parseSmiles("CC=CBr");
		HueckelAromaticityDetector.detectAromaticity(molecule);
		hcg = new HOSECodeGenerator();
		String s = null;
		for (int f = 0; f < molecule.getAtomCount(); f++)
		{
			s = hcg.getHOSECode(molecule, molecule.getAtom(f), 4);
			if (standAlone)
				System.out.print("|" + s + "| -> " + result[f]);
			assertEquals(result[f], s);
			if (standAlone)
				System.out.println("  OK");
		}

		/*JFrame frame = new JFrame("HOSECodeTest");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());
		DefaultMutableTreeNode top = hcg.getRootNode();
		StructureDiagramGenerator sdg = new StructureDiagramGenerator();
		MoleculeViewer2D mv = new MoleculeViewer2D();
		Renderer2DModel r2dm = mv.getRenderer2DModel();
		r2dm.setDrawNumbers(true);

		sdg.setMolecule((Molecule) molecule.clone());
		sdg.generateCoordinates(new Vector2d(0, 1));
		mv.setAtomContainer(sdg.getMolecule());
		
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

	/**
	 * @cdk.bug 795480
	 */
	public void testBug795480() throws Exception
	{
		IMolecule molecule = null;
		HOSECodeGenerator hcg = null;
		String[] result = {
		    "C-4-;C(=C/Y'+4'/)",
     "C-3;=CC-(Y'+4',//)",
     "C-3;=CY'+4'(C-,//)",
     "Br-1'+4';C(=C/C-/)"
		};

		molecule = (new SmilesParser(org.openscience.cdk.DefaultChemObjectBuilder.getInstance())).parseSmiles("CC=CBr");
		boolean isAromatic = HueckelAromaticityDetector.detectAromaticity(molecule);
		assertFalse(isAromatic);
		molecule.getAtom(0).setFormalCharge(-1);
		molecule.getAtom(3).setFormalCharge(+4);
		hcg = new HOSECodeGenerator();
		String s = null;
		for (int f = 0; f < molecule.getAtomCount(); f++)
		{
			s = hcg.getHOSECode(molecule, molecule.getAtom(f), 4);
			if (standAlone)
				System.out.print("|" + s + "| -> " + result[f]);
			assertEquals(result[f], s);
			if (standAlone)
				System.out.println("  OK");
		}
    }
	
  	public void testGetAtomsOfSphere() throws Exception {
  		IMolecule molecule = (new SmilesParser(org.openscience.cdk.DefaultChemObjectBuilder.getInstance())).parseSmiles("CC=CBr");
  		HueckelAromaticityDetector.detectAromaticity(molecule);
  		HOSECodeGenerator hcg = new HOSECodeGenerator();

  		hcg.getSpheres((Molecule) molecule, molecule.getAtom(0), 4, true);
  		List atoms = hcg.getNodesInSphere(3);

  		assertEquals(1, atoms.size());
  		assertEquals("Br", ((IAtom)atoms.get(0)).getSymbol());
	}
  	
  	public void testGetAtomsOfSphereWithHydr() throws Exception {
  		IMolecule molecule = (new SmilesParser(org.openscience.cdk.DefaultChemObjectBuilder.getInstance())).parseSmiles("C([H])([H])([H])C([H])=C([H])Br");
  		HueckelAromaticityDetector.detectAromaticity(molecule);
  		HOSECodeGenerator hcg = new HOSECodeGenerator();

  		hcg.getSpheres((Molecule) molecule, molecule.getAtom(0), 3, true);
  		List atoms = hcg.getNodesInSphere(3);

  		assertEquals(2, atoms.size());

  		assertEquals("H", ((IAtom)atoms.get(0)).getSymbol());
  		assertEquals("Br", ((IAtom)atoms.get(1)).getSymbol());
	}

}

