/* PDBReaderTest.java
 * 
 * $ author: 	Edgar Luttmann 			$ 
 * $ contact: 	edgar@uni-paderborn.de 	$
 * $ date: 		2001-08-09 				$
 * 
 * Copyright (C) 1997-2002  The Chemistry Development Kit (CDK) project
 * 
 * Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *  */
package org.openscience.cdk.test;

import org.openscience.cdk.*;
import org.openscience.cdk.io.*;
import java.io.*;
import junit.framework.*;
import com.baysmith.io.FileUtilities;
import java.util.Iterator;

/**
 *
 * TestCase for the PDBReader class.
 *
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
    testDirectory = null;
  }
  
	public void testPDBFileCoffein() {
		try {
			if (new File("data/coffeine.pdb").canRead()) {
				ChemObjectReader oReader = new PDBReader(new FileInputStream("data/coffeine.pdb"));
				assertNotNull(oReader);
				
				ChemFile oChemFile = (ChemFile)oReader.read(new ChemFile());
				assertNotNull(oChemFile);
				assertEquals(oChemFile.getChemSequenceCount(), 1);

				oChemFile.setProperty(new String("test.chemfile"), new String("test.chemfile")); 
				assertEquals(new String("test.chemfile"), oChemFile.getProperty("test.chemfile"));

				ChemSequence oSeq = oChemFile.getChemSequence(0);
				assertNotNull(oSeq);			
				assertEquals(oSeq.getChemModelCount(), 1);

				oSeq.setProperty(new String("test.chemsequence"), new String("test.chemsequence")); 
				assertEquals(new String("test.chemsequence"), oSeq.getProperty("test.chemsequence"));
				
				ChemModel oModel = oSeq.getChemModel(0);
				assertNotNull(oModel);
				assertEquals(oModel.getSetOfMolecules().getMoleculeCount(), 1);

				oModel.setProperty(new String("test.chemmodel"), new String("test.chemmodel")); 
				assertEquals(new String("test.chemmodel"), oModel.getProperty("test.chemmodel"));
				
				BioPolymer oMol = (BioPolymer)oModel.getSetOfMolecules().getMolecule(0);
				assertNotNull(oMol);
				assertEquals(oMol.getAtomCount(), 14);
				assertNotNull(oMol.getMonomer("MOL1"));

				oMol.setProperty(new String("test.molecule"), new String("test.molecule")); 
				assertEquals(new String("test.molecule"), oMol.getProperty("test.molecule"));

				Atom oAtom = oMol.getFirstAtom();
				assertNotNull(oAtom);
				assertEquals(new String("C"), oAtom.getSymbol());
				assertEquals(new Integer(1), oAtom.getProperty("pdb.serial"));
				assertEquals(new String("C1"), oAtom.getProperty("pdb.name"));
				assertEquals(new String(""), oAtom.getProperty("pdb.altLoc"));
				assertEquals(new String("MOL"), oAtom.getProperty("pdb.resName"));
				assertEquals(new String(""), oAtom.getProperty("pdb.chainID"));
				assertEquals(new String("1"), oAtom.getProperty("pdb.resSeq"));
				assertEquals(new String(""), oAtom.getProperty("pdb.iCode"));
				assertEquals(1.0, ((Double)oAtom.getProperty("pdb.occupancy")).doubleValue(), 0);
				assertEquals(0.0, ((Double)oAtom.getProperty("pdb.tempFactor")).doubleValue(), 0);
				assertEquals(new String(""), oAtom.getProperty("pdb.segID"));
				assertEquals(new String(""), oAtom.getProperty("pdb.element"));
				assertEquals(new String(""), oAtom.getProperty("pdb.charge"));

				oAtom = oMol.getAtoms()[3];
				assertNotNull(oAtom);
				assertEquals(new String("O"), oAtom.getSymbol());
				assertEquals(new Integer(4), oAtom.getProperty("pdb.serial"));
				assertEquals(new String("O4"), oAtom.getProperty("pdb.name"));
				assertEquals(new String(""), oAtom.getProperty("pdb.altLoc"));
				assertEquals(new String("MOL"), oAtom.getProperty("pdb.resName"));
				assertEquals(new String(""), oAtom.getProperty("pdb.chainID"));
				assertEquals(new String("1"), oAtom.getProperty("pdb.resSeq"));
				assertEquals(new String(""), oAtom.getProperty("pdb.iCode"));
				assertEquals(1.0, ((Double)oAtom.getProperty("pdb.occupancy")).doubleValue(), 0);
				assertEquals(0.0, ((Double)oAtom.getProperty("pdb.tempFactor")).doubleValue(), 0);
				assertEquals(new String(""), oAtom.getProperty("pdb.segID"));
				assertEquals(new String(""), oAtom.getProperty("pdb.element"));
				assertEquals(new String(""), oAtom.getProperty("pdb.charge"));

				oAtom = oMol.getLastAtom();
				assertNotNull(oAtom);
				assertEquals(new String("N"), oAtom.getSymbol());
				assertEquals(new Integer(14), oAtom.getProperty("pdb.serial"));
				assertEquals(new String("N14"), oAtom.getProperty("pdb.name"));
				assertEquals(new String(""), oAtom.getProperty("pdb.altLoc"));
				assertEquals(new String("MOL"), oAtom.getProperty("pdb.resName"));
				assertEquals(new String(""), oAtom.getProperty("pdb.chainID"));
				assertEquals(new String("1"), oAtom.getProperty("pdb.resSeq"));
				assertEquals(new String(""), oAtom.getProperty("pdb.iCode"));
				assertEquals(1.0, ((Double)oAtom.getProperty("pdb.occupancy")).doubleValue(), 0);
				assertEquals(0.0, ((Double)oAtom.getProperty("pdb.tempFactor")).doubleValue(), 0);
				assertEquals(new String(""), oAtom.getProperty("pdb.segID"));
				assertEquals(new String(""), oAtom.getProperty("pdb.element"));
				assertEquals(new String(""), oAtom.getProperty("pdb.charge"));
			} else {
				System.out.println("The PDBReader was not tested with a PDB file.");
				System.out.println("Due to missing file: coffeine.pdb");
			}
		} catch (Exception e) {
			fail(e.toString());
		}
	}

  /**
   * Tests reading a protein PDB file.
   */
  public void testProtein() {
    File testPdbFile = null;
    try {
      testPdbFile = new File(testDirectory, "1crn.pdb");
      FileUtilities
          .copyStreamToFile(getClass()
              .getResourceAsStream("Test-"
                  + testPdbFile.getName()), testPdbFile);
    
    } catch (IOException ex) {
      fail(ex.toString());
    }
    
    try {
      ChemObjectReader reader = new PDBReader(new FileInputStream(testPdbFile));
      assertNotNull(reader);
      
      ChemFile chemFile = (ChemFile) reader.read(new ChemFile());
      assertNotNull(chemFile);
      assertEquals(1, chemFile.getChemSequenceCount());

      ChemSequence seq = chemFile.getChemSequence(0);
      assertNotNull(seq);
      assertEquals(1, seq.getChemModelCount());
      
      ChemModel model = seq.getChemModel(0);
      assertNotNull(model);
      assertEquals(1, model.getSetOfMolecules().getMoleculeCount());

      BioPolymer mol = (BioPolymer) model.getSetOfMolecules().getMolecule(0);
      assertNotNull(mol);
      assertEquals(327, mol.getAtomCount());
      assertEquals(46, mol.getMonomerCount());
      assertNotNull(mol.getMonomer("THR1"));
      assertEquals(7, mol.getMonomer("THR1").getAtomCount());
      assertNotNull(mol.getMonomer("ILE7"));
      assertEquals(8, mol.getMonomer("ILE7").getAtomCount());
      
      Atom atom = mol.getAtomAt(94);
      assertNotNull(atom);
      assertEquals("C", atom.getSymbol());
      assertEquals(new Integer(95), atom.getProperty("pdb.serial"));
      assertEquals("CZ", atom.getProperty("pdb.name"));
      assertEquals("", atom.getProperty("pdb.altLoc"));
      assertEquals("PHE", atom.getProperty("pdb.resName"));
      assertEquals("", atom.getProperty("pdb.chainID"));
      assertEquals("13", atom.getProperty("pdb.resSeq"));
      assertEquals("", atom.getProperty("pdb.iCode"));
      assertEquals(1.0, ((Double) atom.getProperty("pdb.occupancy")).doubleValue(), 0.001);
      assertEquals(6.84, ((Double) atom.getProperty("pdb.tempFactor")).doubleValue(), 0.001);
      assertEquals(null, atom.getProperty("pdb.segID"));
      assertEquals(null, atom.getProperty("pdb.element"));
      assertEquals(null, atom.getProperty("pdb.charge"));

    } catch (Exception ex) {
      fail(ex.toString());
    }
  }
}
