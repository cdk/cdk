/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2003  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.test.smiles;

import org.openscience.cdk.*;
import org.openscience.cdk.smiles.*;
import org.openscience.cdk.io.*;
import org.openscience.cdk.tools.*;
import org.openscience.cdk.renderer.*;
import org.openscience.cdk.layout.*;
import org.openscience.cdk.isomorphism.*;
import org.openscience.cdk.templates.*;
import javax.vecmath.*;
import java.util.*;
import java.awt.Dimension;
import java.io.*;
import java.net.URL;
import junit.framework.*;

/**
 *  Description of the Class
 *
 *@author     steinbeck
 *@created    September 19, 2003
 */
public class SmilesParserTest extends TestCase
{
	boolean standAlone = false;


	/**
	 *  Constructor for the SmilesParserTest object
	 *
	 *@param  name  Description of the Parameter
	 */
	public SmilesParserTest(String name)
	{
		super(name);
	}


	/**
	 *  A unit test suite for JUnit
	 *
	 *@return    The test suite
	 */
	public static Test suite()
	{
		return new TestSuite(SmilesParserTest.class);
	}


	/**
	 *  Sets the standAlone attribute of the SmilesParserTest object
	 *
	 *@param  standAlone  The new standAlone value
	 */
	public void setStandAlone(boolean standAlone)
	{
		this.standAlone = standAlone;
	}


	/**
	 *  A unit test for JUnit
	 */
	public void testSmilesParser()
	{
		SmilesParser sp = new SmilesParser();
		MoleculeListViewer mlv = null;
		if (standAlone)
		{
			mlv = new MoleculeListViewer();
		}
		String[] smiles =
				{
		//"C1c2c(c3c(c(O)cnc3)cc2)CC(=O)C1",
		//"O=C(O3)C1=COC(OC4OC(CO)C(O)C(O)C4O)C2C1C3C=C2COC(C)=O",
		//"CN1C=NC2=C1C(N(C)C(N2C)=O)=O",
		//"CN(C)CCC2=CNC1=CC=CC(OP(O)(O)=O)=C12",
		//"O=C(O)C1C(OC(C3=CC=CC=C3)=O)CC2N(C)C1CC2",
		//"C1(C2(C)(C))C(C)=CCC2C1",
		//"C1(C=C(C=C(C=C(C=C(C=CC%35=C%36)C%31=C%35C%32=C%33C%36=C%34)C%22=C%31C%23=C%32C%24=C%25C%33=C%26C%34=CC%27=CC%28=CC=C%29)C%14=C%22C%15=C%23C%16=C%24C%17=C%18C%25=C%19C%26=C%27C%20=C%28C%29=C%21)C6=C%14C7=C%15C8=C%16C9=C%17C%12=C%11C%18=C%10C%19=C%20C%21=CC%10=CC%11=CC(C=C%30)=C%12%13)=C(C6=C(C7=C(C8=C(C9=C%13C%30=C5)C5=C4)C4=C3)C3=C2)C2=CC=C1",
		//"CC1(C(=C(CC(C1)O)C)C=CC(=CC=CC(=CC=CC=C(C=CC=C(C=CC1=C(CC(CC1(C)C)O)C)C)C)C)C)C",
				"NC(C(C)C)C(NC(C(C)O)C(NC(C(C)C)C(NC(CCC(N)=O)C(NC(CC([O-])[O-])C(NCC(NC(CC(N)=O)C(NC(Cc1ccccc1)C(NC(CO)C(NC(Cc2ccccc2)C(NC(CO)C(NC(CC(C)C)C(NC(CCC([O-])[O-])C(NC(CO)C(NC(C(C)C)C(NC(CCCC[N+])C(NC(CCCC[N+])C(NC(CC(C)C)C(NC(CCCC[N+])C(NC(CC([O-])[O-])C(NC(CC(C)C)C(NC(CCC(N)=O)C(NC(CCC([O-])[O-])C(N3CCCC3C(NC(CCC(N)=O)C(NC(CCC([O-])[O-])C(N4CCCC4C(NC(CCCNC([N+])[N+])C(NC(C(C)C)C(NCC(NC(CCCC[N+])C(NC(CC(C)C)C(NC(CCCNC([N+])[N+])C(NC(CC(N)=O)C(NC(Cc5ccccc5)C(NC(C)C(N6CCCC6C(NC(C(C)CC)C(N7CCCC7C(NCC(NC(CCC([O-])[O-])C(N8CCCC8C(NC(C(C)C)C(NC(C(C)C)C(N9CCCC9C(NC(C(C)CC)C(NC(CC(C)C)C(NC%19C[S][S]CC(C(NC(CCCC[N+])C(NC(CCC([O-])[O-])C(N%10CCCC%10C(NC(CC(N)=O)C(NC(C)C(NC(CCC(N)=O)C(NC(CCC([O-])[O-])C(NC(C(C)CC)C(NC(CC(C)C)C(NC(CCC(N)=O)C(NC(CCCNC([N+])[N+])C(NC(CC(C)C)C(NC(CCC([O-])[O-])C(NC(CCC([O-])[O-])C(NC(C(C)CC)C(NC(C)C(NC(CCC([O-])[O-])C(NC(CC([O-])[O-])C(N%11CCCC%11C(NCC(NC(C(C)O)C(NC%14C[S][S]CC%13C(NC(C(C)O)C(NCC(NC(C[S][S]CC(C(NC(C)C(NC(Cc%12ccc(O)cc%12)C(NC(C)C(NC(C)C(N%13)=O)=O)=O)=O)=O)NC(=O)C(C(C)CC)NC(=O)C(CCC([O-])[O-])NC%14=O)C(O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)NC(=O)C(CC(C)C)NC(=O)C%15CCCN%15C(=O)C(CCCC[N+])NC(=O)C(CC(C)C)NC(=O)C(CCC([O-])[O-])NC(=O)C(CCC([O-])[O-])NC(=O)C%16CCCN%16C(=O)C(Cc%17ccccc%17)NC(=O)C(CC(N)=O)NC(=O)C%18CCCN%18C(=O)C(CC(N)=O)NC(=O)C(CO)NC%19=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O"
				};
		for (int f = 0; f < smiles.length; f++)
		{
			try
			{
				Molecule mol = sp.parseSmiles(smiles[f]);
				if (standAlone)
				{
					StructureDiagramGenerator sdg = new StructureDiagramGenerator();
					MoleculeViewer2D mv = new MoleculeViewer2D();
					sdg.setMolecule((Molecule) mol.clone());
					sdg.generateCoordinates();
					mv.setAtomContainer(sdg.getMolecule());
					mlv.addStructure(mv, "Structure " + (f + 1));
				}
			} catch (Exception exc)
			{
				exc.printStackTrace();
				fail(exc.toString());
			}
		}
		if (standAlone)
		{
			long l1 = System.currentTimeMillis();
			try
			{
				Molecule mol = sp.parseSmiles(smiles[6]);
				StructureDiagramGenerator sdg = new StructureDiagramGenerator();
				MoleculeViewer2D mv = new MoleculeViewer2D();
				sdg.setMolecule((Molecule) mol.clone());
				sdg.generateCoordinates();
				mv.setAtomContainer(sdg.getMolecule());
				mv.display();
				//mlv.addStructure(mv, "Structure " + (f + 1));
			} catch (Exception exc)
			{
				exc.printStackTrace();
			}
			long l2 = System.currentTimeMillis();
			//System.out.println(l2-l1);
		}
	}


	/**
	 *  A unit test for JUnit
	 */
	public void testSFBug630475()
	{
		SmilesParser sp = new SmilesParser();
		MoleculeListViewer mlv = null;
		if (standAlone)
		{
			mlv = new MoleculeListViewer();
			mlv.setMolViewDim(new Dimension(400, 600));
		}
		String[] smiles =
				{
				"CC1(C(=C(CC(C1)O)C)C=CC(=CC=CC(=CC=CC=C(C=CC=C(C=CC1=C(CC(CC1(C)C)O)C)C)C)C)C)C"
				};
		for (int f = 0; f < smiles.length; f++)
		{
			try
			{
				Molecule mol = sp.parseSmiles(smiles[f]);
				if (standAlone)
				{
					StructureDiagramGenerator sdg = new StructureDiagramGenerator();
					MoleculeViewer2D mv = new MoleculeViewer2D();
					//mv.getRenderer2DModel().setDrawNumbers(true);
					sdg.setMolecule((Molecule) mol.clone());
					sdg.generateCoordinates();
					mv.setAtomContainer(sdg.getMolecule());
					mlv.addStructure(mv, "Structure " + (f + 1));
				}
			} catch (Exception exc)
			{
				exc.printStackTrace();
				fail(exc.toString());
			}
		}
	}


	/**
	 *  A unit test for JUnit
	 */
	public void testSFBug585811()
	{
		SmilesParser sp = new SmilesParser();
		String[] smiles =
				{
				"CC(C(C8CCC(CC8)=O)C3C4C(CC5(CCC(C9=CC(C=CN%10)=C%10C=C9)CCCC5)C4)C2CCC1CCC7(CCC7)C6(CC6)C1C2C3)=O"
				};
		MoleculeListViewer mlv = null;
		if (standAlone)
		{
			mlv = new MoleculeListViewer();
			mlv.setMolViewDim(new Dimension(400, 600));
		}
		for (int f = 0; f < smiles.length; f++)
		{
			try
			{
				Molecule mol = sp.parseSmiles(smiles[f]);
				if (standAlone)
				{
					StructureDiagramGenerator sdg = new StructureDiagramGenerator();
					MoleculeViewer2D mv = new MoleculeViewer2D();
					//mv.getRenderer2DModel().setDrawNumbers(true);
					sdg.setMolecule((Molecule) mol.clone());
					sdg.generateCoordinates();
					mv.setAtomContainer(sdg.getMolecule());
					mlv.addStructure(mv, "Structure " + (f + 1));
				}
			} catch (Exception exc)
			{
				exc.printStackTrace();
				fail(exc.toString());
			}
		}
	}


	/**
	 *  This tests the fix made for bug #593648.
	 */
	public void testSFBug593648()
	{
		try
		{
			String smiles = "CC1=CCC2CC1C(C)2C";
			Molecule apinene = MoleculeFactory.makeAlphaPinene();
			SmilesParser sp = new SmilesParser();
			Molecule mol = sp.parseSmiles(smiles);
			IsomorphismTester it = new IsomorphismTester(apinene);
			assertTrue(it.isIsomorphic(mol));
		} catch (Exception e)
		{
			fail(e.toString());
		}
	}


	/**
	 *  A unit test for JUnit
	 */
	public void testReadingOfTwoCharElements()
	{
		try
		{
			String smiles = "[Na]";
			SmilesParser sp = new SmilesParser();
			Molecule mol = sp.parseSmiles(smiles);
			assertEquals(1, mol.getAtomCount());
			assertEquals("Na", mol.getAtomAt(0).getSymbol());
		} catch (Exception e)
		{
			fail(e.toString());
		}
	}


	/**
	 *  A unit test for JUnit
	 */
	public void testOrganicSubsetUnderstanding()
	{
		try
		{
			String smiles = "[Ni]";
			SmilesParser sp = new SmilesParser();
			Molecule mol = sp.parseSmiles(smiles);
			assertEquals(1, mol.getAtomCount());
			assertEquals("Ni", mol.getAtomAt(0).getSymbol());
		} catch (Exception e)
		{
			fail(e.toString());
		}
		try
		{
			String smiles = "Ni";
			SmilesParser sp = new SmilesParser();
			Molecule mol = sp.parseSmiles(smiles);
			assertEquals(2, mol.getAtomCount());
			assertEquals("N", mol.getAtomAt(0).getSymbol());
			assertEquals("I", mol.getAtomAt(1).getSymbol());
		} catch (Exception e)
		{
			fail(e.toString());
		}
	}


	/**
	 *  A unit test for JUnit
	 */
	public void testMassNumberReading()
	{
		try
		{
			String smiles = "[13C]";
			SmilesParser sp = new SmilesParser();
			Molecule mol = sp.parseSmiles(smiles);
			assertEquals(1, mol.getAtomCount());
			assertEquals("C", mol.getAtomAt(0).getSymbol());
			assertEquals(13, mol.getAtomAt(0).getMassNumber());
		} catch (Exception e)
		{
			fail(e.toString());
		}
	}


	/**
	 *  A unit test for JUnit
	 */
	public void testFormalChargeReading()
	{
		try
		{
			String smiles = "[OH-]";
			SmilesParser sp = new SmilesParser();
			Molecule mol = sp.parseSmiles(smiles);
			assertEquals(1, mol.getAtomCount());
			assertEquals("O", mol.getAtomAt(0).getSymbol());
			assertEquals(-1, mol.getAtomAt(0).getFormalCharge());
		} catch (Exception e)
		{
			fail(e.toString());
		}
	}


	/**
	 *  A unit test for JUnit
	 */
	public void testReadingPartionedMolecules()
	{
		try
		{
			String smiles = "[Na+].[OH-]";
			SmilesParser sp = new SmilesParser();
			Molecule mol = sp.parseSmiles(smiles);
			assertEquals(2, mol.getAtomCount());
			assertEquals(0, mol.getBondCount());
		} catch (Exception e)
		{
			fail(e.toString());
		}
	}


	/**
	 *  A unit test for JUnit
	 */
	public void testUnkownAtomType()
	{
		try
		{
			String smiles = "*C";
			SmilesParser sp = new SmilesParser();
			Molecule mol = sp.parseSmiles(smiles);
			assertEquals(2, mol.getAtomCount());
			assertEquals(1, mol.getBondCount());
			assertTrue(mol.getAtomAt(0) instanceof PseudoAtom);
			assertFalse(mol.getAtomAt(1) instanceof PseudoAtom);
		} catch (Exception e)
		{
			fail(e.toString());
		}
		try
		{
			String smiles = "[*]C";
			SmilesParser sp = new SmilesParser();
			Molecule mol = sp.parseSmiles(smiles);
			assertEquals(2, mol.getAtomCount());
			assertEquals(1, mol.getBondCount());
			assertTrue(mol.getAtomAt(0) instanceof PseudoAtom);
			assertFalse(mol.getAtomAt(1) instanceof PseudoAtom);
		} catch (Exception e)
		{
			fail(e.toString());
		}
	}


	/**
	 *  A unit test for JUnit
	 */
	public void testBondCreation()
	{
		try
		{
			String smiles = "CC";
			SmilesParser sp = new SmilesParser();
			Molecule mol = sp.parseSmiles(smiles);
			assertEquals(2, mol.getAtomCount());
			assertEquals(1, mol.getBondCount());
		} catch (Exception e)
		{
			fail(e.toString());
		}
		try
		{
			String smiles = "cc";
			SmilesParser sp = new SmilesParser();
			Molecule mol = sp.parseSmiles(smiles);
			assertEquals(2, mol.getAtomCount());
			assertEquals(1, mol.getBondCount());
		} catch (Exception e)
		{
			fail(e.toString());
		}
	}


	/**
	 *  A unit test for JUnit
	 */
	public void testSFBug784433()
	{
		try
		{
			String smiles = "c1cScc1";
			SmilesParser sp = new SmilesParser();
			Molecule mol = sp.parseSmiles(smiles);
			assertEquals(5, mol.getAtomCount());
			assertEquals(5, mol.getBondCount());
		} catch (Exception e)
		{
			fail(e.toString());
		}
	}


	/**
	 *  The main program for the SmilesParserTest class
	 *
	 *@param  args  The command line arguments
	 */
	public static void main(String[] args)
	{
		SmilesParserTest spt = new SmilesParserTest("SmilesParserTest");
		spt.setStandAlone(true);
		spt.testSmilesParser();
		spt.testSFBug630475();
		spt.testSFBug585811();
		spt.testSFBug593648();
	}
}

