/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 1997-2005  The Chemistry Development Kit (CDK) project
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

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.BioPolymer;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.PDBAtom;
import org.openscience.cdk.io.ChemObjectReader;
import org.openscience.cdk.io.PDBReader;

import com.baysmith.io.FileUtilities;

/**
 * TestCase for the PDBReader class.
 *
 * @cdk.module test
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

	public static void main(String[] args) {
		junit.textui.TestRunner.run(new TestSuite(PDBReaderTest.class));
	}

  /**
   * A directory to isolate testing files.
   */
  File testDirectory;

  public void setUp() {
    testDirectory = new File("PDBReaderTest.directory");
    FileUtilities.deleteAll(testDirectory);
    assertTrue("Error creating test directory \"" + testDirectory + "\"",
      testDirectory.mkdir());
  }

  public void tearDown() {
    assertTrue("Error removing test directory \"" + testDirectory + "\"",
      testDirectory.delete());
    testDirectory = null;
  }

	public void testPDBFileCoffein() {
        String filename = "data/pdb/coffeine.pdb";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		try {
            ChemObjectReader oReader = new PDBReader(new InputStreamReader(ins));
            assertNotNull(oReader);

            ChemFile oChemFile = (ChemFile)oReader.read(new ChemFile());
            assertNotNull(oChemFile);
            assertEquals(oChemFile.getChemSequenceCount(), 1);

            oChemFile.setProperty(new String("test.chemfile"), new String("test.chemfile")); 
            assertEquals(new String("test.chemfile"), oChemFile.getProperty("test.chemfile"));

            org.openscience.cdk.interfaces.ChemSequence oSeq = oChemFile.getChemSequence(0);
            assertNotNull(oSeq);			
            assertEquals(oSeq.getChemModelCount(), 1);

            oSeq.setProperty(new String("test.chemsequence"), new String("test.chemsequence")); 
            assertEquals(new String("test.chemsequence"), oSeq.getProperty("test.chemsequence"));

            org.openscience.cdk.interfaces.ChemModel oModel = oSeq.getChemModel(0);
            assertNotNull(oModel);
            assertEquals(1, oModel.getSetOfMolecules().getMoleculeCount());

            oModel.setProperty(new String("test.chemmodel"), new String("test.chemmodel")); 
            assertEquals(new String("test.chemmodel"), oModel.getProperty("test.chemmodel"));

            BioPolymer oMol = (BioPolymer)oModel.getSetOfMolecules().getMolecule(0);
            assertNotNull(oMol);
            assertEquals(oMol.getAtomCount(), 14);
            assertNotNull(oMol.getMonomer("MOLA1", "A"));

            oMol.setProperty(new String("test.molecule"), new String("test.molecule")); 
            assertEquals(new String("test.molecule"), oMol.getProperty("test.molecule"));

            org.openscience.cdk.interfaces.IAtom nAtom = oMol.getFirstAtom();
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

            nAtom = oMol.getAtoms()[3];
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
		} catch (Exception e) {
			fail(e.toString());
		}
	}

  /**
   * Tests reading a protein PDB file.
   */
  public void testProtein() {
    String filename = "data/pdb/Test-1crn.pdb";
    InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);

    try {
      ChemObjectReader reader = new PDBReader(new InputStreamReader(ins));
      assertNotNull(reader);

      ChemFile chemFile = (ChemFile) reader.read(new ChemFile());
      assertNotNull(chemFile);
      assertEquals(1, chemFile.getChemSequenceCount());

      org.openscience.cdk.interfaces.ChemSequence seq = chemFile.getChemSequence(0);
      assertNotNull(seq);
      assertEquals(1, seq.getChemModelCount());
      
      org.openscience.cdk.interfaces.ChemModel model = seq.getChemModel(0);
      assertNotNull(model);
      assertEquals(1, model.getSetOfMolecules().getMoleculeCount());

      BioPolymer mol = (BioPolymer) model.getSetOfMolecules().getMolecule(0);
      assertNotNull(mol);
      assertEquals(327, mol.getAtomCount());
      assertEquals(46, mol.getMonomerCount());
      assertNotNull(mol.getMonomer("THRA1", "A"));
      assertEquals(7, mol.getMonomer("THRA1", "A").getAtomCount());
      assertNotNull(mol.getMonomer("ILEA7", "A"));
      assertEquals(8, mol.getMonomer("ILEA7", "A").getAtomCount());
      
      org.openscience.cdk.interfaces.IAtom nAtom = mol.getAtomAt(94);
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
      
      org.openscience.cdk.interfaces.Bond bond = mol.getBondAt(93);
      assertNotNull(bond);
      assertEquals("Test failed. Bond order not the same.", 2.0, bond.getOrder(), 0.001);
      org.openscience.cdk.interfaces.IAtom[] atoms = bond.getAtoms();
      assertEquals("C", atoms[0].getSymbol());
      assertEquals("O", atoms[1].getSymbol());
      assertEquals(true, bond.getProperties().isEmpty());

    } catch (Exception ex) {
      fail(ex.toString());
    }
  }
}
