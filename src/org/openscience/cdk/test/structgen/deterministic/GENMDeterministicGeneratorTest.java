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
package org.openscience.cdk.test.structgen.deterministic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.vecmath.Vector2d;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.applications.swing.MoleculeListViewer;
import org.openscience.cdk.applications.swing.MoleculeViewer2D;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.structgen.IStructureGenerationListener;
import org.openscience.cdk.structgen.deterministic.GENMDeterministicGenerator;
import org.openscience.cdk.test.CDKTestCase;

/**
 * @cdk.module test-extra
 */
public class GENMDeterministicGeneratorTest extends CDKTestCase
{
	public boolean debug = false;
	boolean standAlone = false;

	public MoleculeListViewer moleculeListViewer = null;
	private SmilesGenerator smilesGenerator = new SmilesGenerator();
    
    public GENMDeterministicGeneratorTest(String name) {
        super(name);
    }
    
    public GENMDeterministicGeneratorTest() {
        this("GENMDeterministicGeneratorTest");
        moleculeListViewer = new MoleculeListViewer();
    }

	public void setStandAlone(boolean standAlone)
	{
		this.standAlone = standAlone;
	}

	public static Test suite()
	{
		return new TestSuite(GENMDeterministicGeneratorTest.class);
	}

	/** A complex alkaloid with two separate ring systems to 
	  * be laid out.
	  */
	public void testIt() throws Exception {
		if (runSlowTests()) {
			GENMDeterministicGenerator gdg = new GENMDeterministicGenerator("C6H10O1","");
			MyStructureGenerationListener myListener = new MyStructureGenerationListener(); 
			gdg.addListener(myListener);
			gdg.generate();
			List structures = myListener.getStructures();
			assertEquals(747, structures.size());
			assertOK(structures);
			assertUnique(structures);
		}
	}
	
	public void testAnotherOne() throws Exception {
		if (runSlowTests()) {	
			GENMDeterministicGenerator gdg = new GENMDeterministicGenerator("C6H13O2N1","");
			MyStructureGenerationListener myListener = new MyStructureGenerationListener();
			gdg.generate();
			assertEquals(23946, gdg.getNumberOfStructures());
		}
	}

	public void testEthane() throws Exception {
		GENMDeterministicGenerator gdg = new GENMDeterministicGenerator("C2H6","");
		MyStructureGenerationListener myListener = new MyStructureGenerationListener(); 
		gdg.addListener(myListener);
		gdg.generate();
		List structures = myListener.getStructures();
		assertEquals(1, structures.size());
		assertUnique(structures);
		assertOK(structures);
	}

	public void testEthanol() throws Exception {
		GENMDeterministicGenerator gdg = new GENMDeterministicGenerator("C2H6O","");
		MyStructureGenerationListener myListener = new MyStructureGenerationListener(); 
		gdg.addListener(myListener);
		gdg.generate();
		List structures = myListener.getStructures();
		assertEquals(2, structures.size());
		assertUnique(structures);
		assertOK(structures);
	}
	
	public void testPropene() throws Exception {
		GENMDeterministicGenerator gdg = new GENMDeterministicGenerator("C3H6","");
		MyStructureGenerationListener myListener = new MyStructureGenerationListener(); 
		gdg.addListener(myListener);
		gdg.generate();
		List structures = myListener.getStructures();
		assertEquals(2, structures.size());
		assertOK(structures);
		List uniqueSMILES = assertUnique(structures);
		assertTrue(uniqueSMILES.contains("C1CC1"));
		assertTrue(uniqueSMILES.contains("C=CC"));
	}

	public void testButene() throws Exception {
		GENMDeterministicGenerator gdg = new GENMDeterministicGenerator("C4H10","");
		MyStructureGenerationListener myListener = new MyStructureGenerationListener(); 
		gdg.addListener(myListener);
		gdg.generate();
		List structures = myListener.getStructures();
		assertEquals(2, structures.size());
		assertOK(structures);
		List uniqueSMILES = assertUnique(structures);
		assertTrue(uniqueSMILES.contains("CCCC"));
		assertTrue(uniqueSMILES.contains("CC(C)C"));
	}

	private List assertUnique(List structures) {
		List uniques = new ArrayList();
		Iterator structs = structures.iterator();
		while (structs.hasNext()) {
			IMolecule mol = (IMolecule)structs.next();
			String SMILES = smilesGenerator.createSMILES(mol);
			if (uniques.contains(SMILES)) {
				fail("Duplicate structure generated: " + SMILES);
			}
			uniques.add(SMILES);
		}
		return uniques;
	}

	private void assertOK(List structures) throws Exception {
		IMolecule mol = null;
		for (int f = 0; f<structures.size(); f++) {
			mol = (Molecule)structures.get(f);
			assertNotNull(mol);
			assertTrue(mol.getAtomCount() > 0);
			assertTrue(mol.getBondCount() > 0);
			// make sure the thing is connected
			IMoleculeSet molSet = ConnectivityChecker.partitionIntoMolecules(mol);
			assertEquals(1, molSet.getAtomContainerCount());
		}
	}
		
//	
//	
//	 /**  
//	  * For SMILES test
//	  */
//	public void testSMILES()
//	{
//		try
//		{	
//			GENMDeterministicGenerator gdg = new GENMDeterministicGenerator("C8H12","");
//			Vector smiles=gdg.getSMILES();
//			//for(int i=0;i<smiles.size();i++)
//			//	System.out.println(smiles.get(i));
//			//everythingOk(structures);
////			displaySMILES(smiles);
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
//	}
//
//	
//	private boolean displaySMILES(Vector structures)
//	{
//		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
//		StructureDiagramGenerator sdg = null;
//		MoleculeViewer2D mv = null;
//		Molecule mol = null;
//		for (int f = 0; f<structures.size(); f++)
//		{
//		//	System.out.println(structures.get(f));
//			try
//			{
//				mol=sp.parseSmiles((String)(structures.get(f)));
//				sdg = new StructureDiagramGenerator();
//
//			//mol = (Molecule)structures.elementAt(f);
//			//System.out.println(mol.getAtomCount());
//			//System.out.println(mol.getBondCount());
//				sdg.setMolecule((Molecule)mol.clone());
//			//sdg.setMolecule(mol);
//			
//				sdg.generateCoordinates(new Vector2d(0,1));
//			}
//			catch(Exception exc)
//			{
//				exc.printStackTrace();
//				fail("*** Exit due to an unexpected error during coordinate generation ***");
//			}
//            if (standAlone) {
//                
//                mv = new MoleculeViewer2D();
//		mv.setAtomContainer(sdg.getMolecule());
//                //			Renderer2DModel r2dm = new Renderer2DModel();
//                //			r2dm.setDrawNumbers(true);
//                //			mv.setRenderer2DModel(r2dm);
//                moleculeListViewer.addStructure(mv, "Structure no. " + (f + 1));
//            }
//		}
//		return true;
//	}
	
	private boolean drawMolecules(List structures) throws Exception {
		StructureDiagramGenerator sdg = null;
		MoleculeViewer2D mv = null;
		Molecule mol = null;
		for (int f = 0; f<structures.size(); f++)
		{
			sdg = new StructureDiagramGenerator();

			mol = (Molecule)structures.get(f);
			//System.out.println(mol.getAtomCount());
			//System.out.println(mol.getBondCount());
			sdg.setMolecule((Molecule)mol.clone());
			//sdg.setMolecule(mol);
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
                
                mv = new MoleculeViewer2D();
		mv.setAtomContainer(sdg.getMolecule());
                //			Renderer2DModel r2dm = new Renderer2DModel();
                //			r2dm.setDrawNumbers(true);
                //			mv.setRenderer2DModel(r2dm);
                moleculeListViewer.addStructure(mv, "Structure no. " + (f + 1));
            }
		}
		return true;
	}
	
	public static void main(String[] args)
	{
		GENMDeterministicGeneratorTest test = new GENMDeterministicGeneratorTest();
		test.setStandAlone(true);
		try {
			test.testIt();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//test.testSMILES();
	}
	
	class MyStructureGenerationListener implements IStructureGenerationListener {

		private List structures;
		
		public MyStructureGenerationListener() {
			structures = new ArrayList();
		}
		
		public void stateChanged(List list) throws Exception {
			structures.addAll(list);
		}
		
		public List getStructures() {
			return structures;
		}
		
	}
}


