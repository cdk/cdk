/* $Revision$ $Author$ $Date$
 * 
 * Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
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
 *  */
package org.openscience.cdk.test.io;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.ChemFile;
import org.openscience.cdk.Monomer;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBioPolymer;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.IMonomer;
import org.openscience.cdk.interfaces.IStrand;
import org.openscience.cdk.io.IChemObjectReader;
import org.openscience.cdk.io.PDBReader;
import org.openscience.cdk.nonotify.NNChemFile;
import org.openscience.cdk.protein.data.PDBAtom;
import org.openscience.cdk.protein.data.PDBMonomer;
import org.openscience.cdk.protein.data.PDBPolymer;
import org.openscience.cdk.protein.data.PDBStrand;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

/**
 * TestCase for the PDBReader class.
 *
 * @cdk.module test-pdb
 *
 * @author      Edgar Luttmann <edgar@uni-paderborn.de>
 * @author      Martin Eklund <martin.eklund@farmbio.uu.se>
 * @cdk.created 2001-08-09
 */
public class PDBReaderTest extends TestCase {

	public PDBReaderTest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(PDBReaderTest.class);
	}

    public void testAccepts() {
    	PDBReader reader = new PDBReader();
    	assertTrue(reader.accepts(ChemFile.class));
    }

	public void testPDBFileCoffein() throws Exception {
        String filename = "data/pdb/coffeine.pdb";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);

        IChemObjectReader oReader = new PDBReader(ins);
        assertNotNull(oReader);

        IChemFile oChemFile = (IChemFile)oReader.read(new NNChemFile());
        assertNotNull(oChemFile);
        assertEquals(oChemFile.getChemSequenceCount(), 1);

        IChemSequence oSeq = oChemFile.getChemSequence(0);
        assertNotNull(oSeq);			
        assertEquals(oSeq.getChemModelCount(), 1);

        IChemModel oModel = oSeq.getChemModel(0);
        assertNotNull(oModel);
        assertEquals(1, oModel.getMoleculeSet().getMoleculeCount());

        IAtomContainer container = oModel.getMoleculeSet().getMolecule(0);
        assertFalse(container instanceof IBioPolymer);
        assertTrue(container instanceof IAtomContainer);
        IAtomContainer oMol = (IAtomContainer)container;
        assertNotNull(oMol);
        assertEquals(oMol.getAtomCount(), 14);

        IAtom nAtom = oMol.getFirstAtom();
        assertNotNull(nAtom);
        assertTrue(nAtom instanceof PDBAtom);
        PDBAtom oAtom = (PDBAtom)nAtom;
        assertEquals(new String("C"), oAtom.getSymbol());
        assertEquals(1, oAtom.getSerial());
        assertEquals("C1", oAtom.getName());
        assertEquals("MOL", oAtom.getResName());
        assertEquals("1", oAtom.getResSeq());
        assertEquals(1.0, oAtom.getOccupancy(), 0);
        assertEquals(0.0, oAtom.getTempFactor(), 0);

        nAtom = oMol.getAtom(3);
        assertNotNull(nAtom);
        assertTrue(nAtom instanceof PDBAtom);
        oAtom = (PDBAtom)nAtom;
        assertEquals("O", oAtom.getSymbol());
        assertEquals(4, oAtom.getSerial());
        assertEquals("O4", oAtom.getName());
        assertEquals("MOL", oAtom.getResName());
        assertEquals("1", oAtom.getResSeq());
        assertEquals(1.0, oAtom.getOccupancy(), 0);
        assertEquals(0.0, oAtom.getTempFactor(), 0);

        nAtom = oMol.getLastAtom();
        assertNotNull(nAtom);
        assertTrue(nAtom instanceof PDBAtom);
        oAtom = (PDBAtom)nAtom;
        assertEquals("N", oAtom.getSymbol());
        assertEquals(14, oAtom.getSerial());
        assertEquals("N14", oAtom.getName());
        assertEquals("MOL", oAtom.getResName());
        assertEquals("1", oAtom.getResSeq());
        assertEquals(1.0, oAtom.getOccupancy(), 0);
        assertEquals(0.0, oAtom.getTempFactor(), 0);
	}

	/**
	 * Tests reading a protein PDB file.
	 */
	public void testProtein() throws Exception {
		String filename = "data/pdb/Test-1crn.pdb";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);

		IChemObjectReader reader = new PDBReader(ins);
		assertNotNull(reader);

		ChemFile chemFile = (ChemFile) reader.read(new NNChemFile());
		assertNotNull(chemFile);
		assertEquals(1, chemFile.getChemSequenceCount());

		org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
		assertNotNull(seq);
		assertEquals(1, seq.getChemModelCount());

		IChemModel model = seq.getChemModel(0);
		assertNotNull(model);
		assertEquals(1, model.getMoleculeSet().getMoleculeCount());

		IAtomContainer container = model.getMoleculeSet().getMolecule(0);
		assertTrue(container instanceof IBioPolymer);
		IBioPolymer mol = (IBioPolymer)container;
		assertNotNull(mol);
		assertEquals(327, mol.getAtomCount());
		assertEquals(46, mol.getMonomerCount());
		assertNotNull(mol.getMonomer("THRA1", "A"));
		assertEquals(7, mol.getMonomer("THRA1", "A").getAtomCount());
		assertNotNull(mol.getMonomer("ILEA7", "A"));
		assertEquals(8, mol.getMonomer("ILEA7", "A").getAtomCount());

		IAtom nAtom = mol.getAtom(94);
		assertNotNull(nAtom);
		assertTrue(nAtom instanceof PDBAtom);
		PDBAtom atom = (PDBAtom)nAtom;
		assertEquals("C", atom.getSymbol());
		assertEquals(95, atom.getSerial());
		assertEquals("CZ", atom.getName());
		assertEquals("PHE", atom.getResName());
		assertEquals("13", atom.getResSeq());
		assertEquals(1.0, atom.getOccupancy(), 0.001);
		assertEquals(6.84, atom.getTempFactor(), 0.001);

	}
  
    public void test114D() throws Exception {
	    String filename = "data/pdb/114D.pdb";
	    InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);

	    IChemObjectReader reader = new PDBReader(ins);
	    assertNotNull(reader);

	    IChemFile chemFile = (IChemFile) reader.read(new NNChemFile());
	    assertNotNull(chemFile);
	    assertEquals(1, chemFile.getChemSequenceCount());

	    org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
	    assertNotNull(seq);
	    assertEquals(1, seq.getChemModelCount());

	    IChemModel model = seq.getChemModel(0);
	    assertNotNull(model);
	    assertEquals(1, model.getMoleculeSet().getMoleculeCount());

	    IAtomContainer container = model.getMoleculeSet().getMolecule(0);
	    assertTrue(container instanceof IBioPolymer);
	    IBioPolymer polymer = (IBioPolymer)container;

	    assertTrue(
	    	"Strand A is not a PDBStrand",
	    	polymer.getStrand("A") instanceof PDBStrand
	    );
	    PDBStrand strandA=(PDBStrand)polymer.getStrand("A");
	    List lst=(List)strandA.getMonomerNamesInSequentialOrder();
	    String monomer1=(String)lst.get(0);
	    IMonomer mono1=strandA.getMonomer(monomer1);
	    assertNotNull(mono1);
	    assertNotNull(mono1.getMonomerName());
	    assertTrue(
		    	"Monomer is not a PDBMonomer",
		    	mono1 instanceof PDBMonomer
		    	);
	    PDBMonomer pdbMonomer=(PDBMonomer)mono1;
	    assertEquals(pdbMonomer.getResSeq(), "1");

	    String monomer2=(String)lst.get(1);
	    IMonomer mono2=strandA.getMonomer(monomer2);
	    assertTrue(
		    	"Monomer is not a PDBMonomer",
		    	mono2 instanceof PDBMonomer
		    	);
	    PDBMonomer pdbMonomer2=(PDBMonomer)mono2;
	    assertEquals(pdbMonomer2.getResSeq(), "2");
	    
	    
	    // chemical validation
	    assertEquals(552, ChemFileManipulator.getAtomCount(chemFile));
	    assertEquals(2, polymer.getStrandCount());
	    assertEquals(24, polymer.getMonomerCount());

	      assertTrue(polymer.getStrandNames().contains("A"));
	      assertTrue(polymer.getStrandNames().contains("B"));
	      assertFalse(polymer.getStrandNames().contains("C"));
	      assertEquals(24, polymer.getMonomerCount());
	      
	    assertTrue(polymer instanceof PDBPolymer);
	    PDBPolymer pdb = (PDBPolymer)polymer;

	    // PDB validation
	    assertEquals(0, pdb.getStructures().size());
	      
    }
  
    public void test1SPX() throws Exception {
	    String filename = "data/pdb/1SPX.pdb";
	    InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);

	    IChemObjectReader reader = new PDBReader(ins);
	    assertNotNull(reader);

	    IChemFile chemFile = (IChemFile) reader.read(new NNChemFile());
	    assertNotNull(chemFile);
	    assertEquals(1, chemFile.getChemSequenceCount());

	    org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
	    assertNotNull(seq);
	    assertEquals(1, seq.getChemModelCount());

	    IChemModel model = seq.getChemModel(0);
	    assertNotNull(model);
	    assertEquals(1, model.getMoleculeSet().getMoleculeCount());

	    IAtomContainer container = model.getMoleculeSet().getMolecule(0);
	    assertTrue(container instanceof IBioPolymer);
	    IBioPolymer polymer = (IBioPolymer)container;

	    // chemical validation
	    assertEquals(1904, ChemFileManipulator.getAtomCount(chemFile));
	    assertEquals(1, polymer.getStrandCount());
	    assertEquals(237, polymer.getMonomerCount());

	    assertTrue(polymer instanceof PDBPolymer);
	    PDBPolymer pdb = (PDBPolymer)polymer;

	    // PDB validation
	    assertEquals(19, pdb.getStructures().size());
	      
    }

    public void test1XKQ() throws Exception {
	    String filename = "data/pdb/1XKQ.pdb";
	    InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);

	    IChemObjectReader reader = new PDBReader(ins);
	    assertNotNull(reader);

	    IChemFile chemFile = (IChemFile) reader.read(new NNChemFile());
	    assertNotNull(chemFile);
	    assertEquals(1, chemFile.getChemSequenceCount());

	    org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
	    assertNotNull(seq);
	    assertEquals(1, seq.getChemModelCount());

	    IChemModel model = seq.getChemModel(0);
	    assertNotNull(model);
	    assertEquals(1, model.getMoleculeSet().getMoleculeCount());

	    IAtomContainer container = model.getMoleculeSet().getMolecule(0);
	    assertTrue(container instanceof IBioPolymer);
	    IBioPolymer polymer = (IBioPolymer)container;

	    // chemical validation
	    assertEquals(8955, ChemFileManipulator.getAtomCount(chemFile));
	    assertEquals(4, polymer.getStrandCount());
	    assertEquals(1085, polymer.getMonomerCount());

	    assertTrue(polymer instanceof PDBPolymer);
	    PDBPolymer pdb = (PDBPolymer)polymer;

	    // PDB validation
	    assertEquals(90, pdb.getStructures().size());
	      
    }

    public void test1A00() throws Exception {
	    String filename = "data/pdb/1A00.pdb";
	    InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);

	    IChemObjectReader reader = new PDBReader(ins);
	    assertNotNull(reader);

	    IChemFile chemFile = (IChemFile) reader.read(new NNChemFile());
	    assertNotNull(chemFile);
	    assertEquals(1, chemFile.getChemSequenceCount());

	    org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
	    assertNotNull(seq);
	    assertEquals(1, seq.getChemModelCount());

	    IChemModel model = seq.getChemModel(0);
	    assertNotNull(model);
	    assertEquals(1, model.getMoleculeSet().getMoleculeCount());

	    IAtomContainer container = model.getMoleculeSet().getMolecule(0);
	    assertTrue(container instanceof IBioPolymer);
	    IBioPolymer polymer = (IBioPolymer)container;

	    // chemical validation
	    assertEquals(4770, ChemFileManipulator.getAtomCount(chemFile));
	    assertEquals(4, polymer.getStrandCount());
	    assertEquals(574, polymer.getMonomerCount());

	    assertTrue(polymer instanceof PDBPolymer);
	    PDBPolymer pdb = (PDBPolymer)polymer;

	    // PDB validation
	    assertEquals(35, pdb.getStructures().size());
	      
    }


    public void test1BOQ() throws Exception {
	    String filename = "data/pdb/1BOQ.pdb";
	    InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);

	    IChemObjectReader reader = new PDBReader(ins);
	    assertNotNull(reader);

	    IChemFile chemFile = (IChemFile) reader.read(new NNChemFile());
	    assertNotNull(chemFile);
	    assertEquals(1, chemFile.getChemSequenceCount());

	    org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
	    assertNotNull(seq);
	    assertEquals(1, seq.getChemModelCount());

	    IChemModel model = seq.getChemModel(0);
	    assertNotNull(model);
	    assertEquals(1, model.getMoleculeSet().getMoleculeCount());

	    IAtomContainer container = model.getMoleculeSet().getMolecule(0);
	    assertTrue(container instanceof IBioPolymer);
	    IBioPolymer polymer = (IBioPolymer)container;

	    // chemical validation
	    assertEquals(1538, ChemFileManipulator.getAtomCount(chemFile));
	    assertEquals(1, polymer.getStrandCount());
	    assertEquals(198, polymer.getMonomerCount());

	    assertTrue(polymer instanceof PDBPolymer);
	    PDBPolymer pdb = (PDBPolymer)polymer;

	    // PDB validation
	    assertEquals(21, pdb.getStructures().size());
	      
    }

    public void test1TOH() throws Exception {
	    String filename = "data/pdb/1TOH.pdb";
	    InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);

	    IChemObjectReader reader = new PDBReader(ins);
	    assertNotNull(reader);

	    IChemFile chemFile = (IChemFile) reader.read(new NNChemFile());
	    assertNotNull(chemFile);
	    assertEquals(1, chemFile.getChemSequenceCount());

	    org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
	    assertNotNull(seq);
	    assertEquals(1, seq.getChemModelCount());

	    IChemModel model = seq.getChemModel(0);
	    assertNotNull(model);
	    assertEquals(1, model.getMoleculeSet().getMoleculeCount());

	    IAtomContainer container = model.getMoleculeSet().getMolecule(0);
	    assertTrue(container instanceof IBioPolymer);
	    IBioPolymer polymer = (IBioPolymer)container;

	    // chemical validation
	    assertEquals(2804, ChemFileManipulator.getAtomCount(chemFile));
	    assertEquals(1, polymer.getStrandCount());
	    assertEquals(325, polymer.getMonomerCount());

	    assertTrue(polymer instanceof PDBPolymer);
	    PDBPolymer pdb = (PDBPolymer)polymer;

	    // PDB validation
	    assertEquals(23, pdb.getStructures().size());
    }
    
    public void test1CKV() throws Exception {
	    String filename = "data/pdb/1CKV.pdb";
	    InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);

	    IChemObjectReader reader = new PDBReader(ins);
	    assertNotNull(reader);

	    IChemFile chemFile = (IChemFile) reader.read(new NNChemFile());
	    assertNotNull(chemFile);
	    assertEquals(1, chemFile.getChemSequenceCount());

	    org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
	    assertNotNull(seq);
	    assertEquals(14, seq.getChemModelCount());

	    IChemModel model = seq.getChemModel(0);
	    assertNotNull(model);
	    assertEquals(1, model.getMoleculeSet().getMoleculeCount());

	    IAtomContainer container = model.getMoleculeSet().getMolecule(0);
	    assertTrue(container instanceof IBioPolymer);
	    IBioPolymer polymer = (IBioPolymer)container;

	    // chemical validation
	    assertEquals(31066, ChemFileManipulator.getAtomCount(chemFile));
	    assertEquals(1, polymer.getStrandCount());
	    assertEquals(141, polymer.getMonomerCount());

	    assertTrue(polymer instanceof PDBPolymer);
	    PDBPolymer pdb = (PDBPolymer)polymer;

	    // PDB validation
	    assertEquals(9, pdb.getStructures().size());
    }
}
