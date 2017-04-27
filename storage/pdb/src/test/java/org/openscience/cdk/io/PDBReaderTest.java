/* Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.io;

import java.io.InputStream;
import java.io.StringReader;
import java.util.Collection;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.SlowTest;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBioPolymer;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.IMonomer;
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
 * @author Edgar Luttmann &lt;edgar@uni-paderborn.de&gt;
 * @author Martin Eklund &lt;martin.eklund@farmbio.uu.se&gt;
 * @cdk.created 2001-08-09
 */
public class PDBReaderTest extends SimpleChemObjectReaderTest {

    @BeforeClass
    public static void setup() throws Exception {
        setSimpleChemObjectReader(new PDBReader(), "data/pdb/coffeine.pdb");
    }

    /**
     * Test to see if PDB files with CONECT records are handled properly.
     *
     * @throws Exception
     * @cdk.bug 2046633
     */
    @Test
    public void testConnectRecords() throws Exception {
        String data = "SEQRES    111111111111111111111111111111111111111111111111111111111111111     \n"
                + "ATOM      1  N   SER A 326     103.777  74.304  20.170  1.00 21.58           N\n"
                + "ATOM      2  CA  SER A 326     102.613  74.991  20.586  1.00 18.59           C\n"
                + "ATOM      3  C   SER A 326     101.631  74.211  21.431  1.00 17.75           C\n"
                + "ATOM      4  O   SER A 326     101.653  74.549  22.634  1.00 18.51           O\n"
                + "CONECT    1    4\n" + "CONECT    4    1\n" + "END    \n";

        StringReader stringReader = new StringReader(data);
        PDBReader reader = new PDBReader(stringReader);
        reader.getSetting("UseRebondTool").setSetting("false"); // UseRebondTool
        reader.getSetting("ReadConnectSection").setSetting("true"); // ReadConnectSection

        ChemObject object = new ChemFile();
        reader.read(object);
        reader.close();
        stringReader.close();
        Assert.assertNotNull(object);
        int bondCount = ((IChemFile) object).getChemSequence(0).getChemModel(0).getMoleculeSet().getAtomContainer(0)
                .getBondCount();
        /*
         * if ReadConnectSection=true and UseRebondTool=false then bondCount ==
         * 1 (from just the CONECT) else if ReadConnectSection=false and
         * UseRebondTool=true then bondCount == 3 (just atoms within bonding
         * distance)
         */
        Assert.assertEquals(1, bondCount);
    }

    @Test
    public void readCharge() throws Exception {
        String data = "HETATM 3486 MG    MG A 302      24.885  14.008  59.194  1.00 29.42          MG+2\n" + "END";
        IChemFile chemFile = getChemFileFromString(data);
        IAtomContainer atomContainer = getFirstAtomContainer(chemFile, 1, 1, 1);
        Assert.assertEquals(new Double(2.0), atomContainer.getAtom(0).getCharge());
    }

    @Test
    public void oldFormatNewFormatTest() throws Exception {
        String oldFormat = "ATOM      1 1HA  UNK A   1      20.662  36.632  23.475  1.00 10.00      114D  45\nEND";
        String newFormat = "ATOM      1 1HA  UNK A   1      20.662  36.632  23.475  1.00 10.00           H\nEND";

        IChemFile oldFormatFile = getChemFileFromString(oldFormat);
        IChemFile newFormatFile = getChemFileFromString(newFormat);
        IAtomContainer acOld = getFirstAtomContainer(oldFormatFile, 1, 1, 1);
        IAtomContainer acNew = getFirstAtomContainer(newFormatFile, 1, 1, 1);
        Assert.assertEquals("H", acOld.getAtom(0).getSymbol());
        Assert.assertEquals("H", acNew.getAtom(0).getSymbol());
    }

    @Test
    public void testAccepts() {
        PDBReader reader = new PDBReader();
        Assert.assertTrue(reader.accepts(ChemFile.class));
    }

    @Test
    public void testPDBFileCoffein() throws Exception {
        String filename = "data/pdb/coffeine.pdb";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);

        ISimpleChemObjectReader oReader = new PDBReader(ins);
        Assert.assertNotNull(oReader);

        IChemFile oChemFile = (IChemFile) oReader.read(new ChemFile());
        Assert.assertNotNull(oChemFile);
        Assert.assertEquals(oChemFile.getChemSequenceCount(), 1);

        IChemSequence oSeq = oChemFile.getChemSequence(0);
        Assert.assertNotNull(oSeq);
        Assert.assertEquals(oSeq.getChemModelCount(), 1);

        IChemModel oModel = oSeq.getChemModel(0);
        Assert.assertNotNull(oModel);
        Assert.assertEquals(1, oModel.getMoleculeSet().getAtomContainerCount());

        IAtomContainer container = oModel.getMoleculeSet().getAtomContainer(0);
        Assert.assertFalse(container instanceof IBioPolymer);
        Assert.assertTrue(container instanceof IAtomContainer);
        IAtomContainer oMol = (IAtomContainer) container;
        Assert.assertNotNull(oMol);
        Assert.assertEquals(oMol.getAtomCount(), 14);

        IAtom nAtom = oMol.getAtom(0);
        Assert.assertNotNull(nAtom);
        Assert.assertTrue(nAtom instanceof PDBAtom);
        PDBAtom oAtom = (PDBAtom) nAtom;
        Assert.assertEquals(new String("C"), oAtom.getSymbol());
        Assert.assertEquals(1, oAtom.getSerial().intValue());
        Assert.assertEquals("C1", oAtom.getName());
        Assert.assertEquals("MOL", oAtom.getResName());
        Assert.assertEquals("1", oAtom.getResSeq());
        Assert.assertEquals(1.0, oAtom.getOccupancy(), 0);
        Assert.assertEquals(0.0, oAtom.getTempFactor(), 0);

        nAtom = oMol.getAtom(3);
        Assert.assertNotNull(nAtom);
        Assert.assertTrue(nAtom instanceof PDBAtom);
        oAtom = (PDBAtom) nAtom;
        Assert.assertEquals("O", oAtom.getSymbol());
        Assert.assertEquals(4, oAtom.getSerial().intValue());
        Assert.assertEquals("O4", oAtom.getName());
        Assert.assertEquals("MOL", oAtom.getResName());
        Assert.assertEquals("1", oAtom.getResSeq());
        Assert.assertEquals(1.0, oAtom.getOccupancy(), 0);
        Assert.assertEquals(0.0, oAtom.getTempFactor(), 0);

        nAtom = oMol.getAtom(oMol.getAtomCount()-1);
        Assert.assertNotNull(nAtom);
        Assert.assertTrue(nAtom instanceof PDBAtom);
        oAtom = (PDBAtom) nAtom;
        Assert.assertEquals("N", oAtom.getSymbol());
        Assert.assertEquals(14, oAtom.getSerial().intValue());
        Assert.assertEquals("N14", oAtom.getName());
        Assert.assertEquals("MOL", oAtom.getResName());
        Assert.assertEquals("1", oAtom.getResSeq());
        Assert.assertEquals(1.0, oAtom.getOccupancy(), 0);
        Assert.assertEquals(0.0, oAtom.getTempFactor(), 0);
    }

    /**
     * Tests reading a protein PDB file.
     */
    @Test
    public void testProtein() throws Exception {
        String filename = "data/pdb/Test-1crn.pdb";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);

        ISimpleChemObjectReader reader = new PDBReader(ins);
        Assert.assertNotNull(reader);

        ChemFile chemFile = (ChemFile) reader.read(new ChemFile());
        Assert.assertNotNull(chemFile);
        Assert.assertEquals(1, chemFile.getChemSequenceCount());

        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        Assert.assertNotNull(seq);
        Assert.assertEquals(1, seq.getChemModelCount());

        IChemModel model = seq.getChemModel(0);
        Assert.assertNotNull(model);
        Assert.assertEquals(1, model.getMoleculeSet().getAtomContainerCount());

        IAtomContainer container = model.getMoleculeSet().getAtomContainer(0);
        Assert.assertTrue(container instanceof IBioPolymer);
        IBioPolymer mol = (IBioPolymer) container;
        Assert.assertNotNull(mol);
        Assert.assertEquals(327, mol.getAtomCount());
        Assert.assertEquals(46, mol.getMonomerCount());
        Assert.assertNotNull(mol.getMonomer("THRA1", "A"));
        Assert.assertEquals(7, mol.getMonomer("THRA1", "A").getAtomCount());
        Assert.assertNotNull(mol.getMonomer("ILEA7", "A"));
        Assert.assertEquals(8, mol.getMonomer("ILEA7", "A").getAtomCount());

        IAtom nAtom = mol.getAtom(94);
        Assert.assertNotNull(nAtom);
        Assert.assertTrue(nAtom instanceof PDBAtom);
        PDBAtom atom = (PDBAtom) nAtom;
        Assert.assertEquals("C", atom.getSymbol());
        Assert.assertEquals(95, atom.getSerial().intValue());
        Assert.assertEquals("CZ", atom.getName());
        Assert.assertEquals("PHE", atom.getResName());
        Assert.assertEquals("13", atom.getResSeq());
        Assert.assertEquals(1.0, atom.getOccupancy(), 0.001);
        Assert.assertEquals(6.84, atom.getTempFactor(), 0.001);

    }

    public IChemFile getChemFileFromString(String data) throws Exception {
        StringReader stringReader = new StringReader(data);
        PDBReader reader = new PDBReader(stringReader);
        Assert.assertNotNull(reader);
        return getChemFile(reader);
    }

    public IChemFile getChemFile(String filename) throws Exception {
        return getChemFile(filename, false);
    }

    public IChemFile getChemFile(ISimpleChemObjectReader reader) throws Exception {
        return getChemFile(reader, false);
    }

    public IChemFile getChemFile(String filename, boolean useRebond) throws Exception {
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        return getChemFile(new PDBReader(ins), useRebond);
    }

    public IChemFile getChemFile(ISimpleChemObjectReader reader, boolean useRebond) throws Exception {
        Assert.assertNotNull(reader);

        reader.getSetting("UseRebondTool").setSetting(String.valueOf(useRebond));

        IChemFile chemFile = (IChemFile) reader.read(new ChemFile());
        Assert.assertNotNull(chemFile);
        return chemFile;
    }

    public IAtomContainer getFirstAtomContainer(IChemFile chemFile, int chemSequenceCount, int chemModelCount,
            int moleculeCount) {
        Assert.assertNotNull(chemFile);
        Assert.assertEquals(chemSequenceCount, chemFile.getChemSequenceCount());

        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        Assert.assertNotNull(seq);
        Assert.assertEquals(chemModelCount, seq.getChemModelCount());

        IChemModel model = seq.getChemModel(0);
        Assert.assertNotNull(model);
        Assert.assertEquals(moleculeCount, model.getMoleculeSet().getAtomContainerCount());
        return model.getMoleculeSet().getAtomContainer(0);
    }

    public void testObjectCountsChemFile(IChemFile chemFile, int chemSequenceCount, int chemModelCount,
            int moleculeCount, int atomCount, int strandCount, int monomerCount, int structureCount) throws Exception {
        IAtomContainer container = getFirstAtomContainer(chemFile, chemSequenceCount, chemModelCount, moleculeCount);
        Assert.assertTrue(container instanceof IBioPolymer);
        IBioPolymer polymer = (IBioPolymer) container;

        // chemical validation
        Assert.assertEquals(atomCount, ChemFileManipulator.getAtomCount(chemFile));
        Assert.assertEquals(strandCount, polymer.getStrandCount());
        Assert.assertEquals(monomerCount, polymer.getMonomerCount());

        Assert.assertTrue(polymer instanceof PDBPolymer);
        PDBPolymer pdb = (PDBPolymer) polymer;

        // PDB validation
        Assert.assertEquals(structureCount, pdb.getStructures().size());
    }

    @Test
    public void test114D() throws Exception {
        String filename = "data/pdb/114D.pdb";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);

        ISimpleChemObjectReader reader = new PDBReader(ins);
        Assert.assertNotNull(reader);

        IChemFile chemFile = (IChemFile) reader.read(new ChemFile());
        Assert.assertNotNull(chemFile);
        Assert.assertEquals(1, chemFile.getChemSequenceCount());

        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        Assert.assertNotNull(seq);
        Assert.assertEquals(1, seq.getChemModelCount());

        IChemModel model = seq.getChemModel(0);
        Assert.assertNotNull(model);
        Assert.assertEquals(1, model.getMoleculeSet().getAtomContainerCount());

        IAtomContainer container = model.getMoleculeSet().getAtomContainer(0);
        Assert.assertTrue(container instanceof IBioPolymer);
        IBioPolymer polymer = (IBioPolymer) container;

        Assert.assertTrue("Strand A is not a PDBStrand", polymer.getStrand("A") instanceof PDBStrand);
        PDBStrand strandA = (PDBStrand) polymer.getStrand("A");
        Iterator<String> lst = strandA.getMonomerNamesInSequentialOrder().iterator();
        String monomer1 = lst.next();
        IMonomer mono1 = strandA.getMonomer(monomer1);
        Assert.assertNotNull(mono1);
        Assert.assertNotNull(mono1.getMonomerName());
        Assert.assertTrue("Monomer is not a PDBMonomer", mono1 instanceof PDBMonomer);
        PDBMonomer pdbMonomer = (PDBMonomer) mono1;
        Assert.assertEquals(pdbMonomer.getResSeq(), "1");

        String monomer2 = lst.next();
        IMonomer mono2 = strandA.getMonomer(monomer2);
        Assert.assertTrue("Monomer is not a PDBMonomer", mono2 instanceof PDBMonomer);
        PDBMonomer pdbMonomer2 = (PDBMonomer) mono2;
        Assert.assertEquals(pdbMonomer2.getResSeq(), "2");

        // chemical validation
        Assert.assertEquals(552, ChemFileManipulator.getAtomCount(chemFile));
        Assert.assertEquals(2, polymer.getStrandCount());
        Assert.assertEquals(24, polymer.getMonomerCount());

        Assert.assertTrue(polymer.getStrandNames().contains("A"));
        Assert.assertTrue(polymer.getStrandNames().contains("B"));
        Assert.assertFalse(polymer.getStrandNames().contains("C"));
        Assert.assertEquals(24, polymer.getMonomerCount());

        Assert.assertTrue(polymer instanceof PDBPolymer);
        PDBPolymer pdb = (PDBPolymer) polymer;

        // PDB validation
        Assert.assertEquals(0, pdb.getStructures().size());

    }

    @Test
    public void testUnk() throws Exception {
        String filename = "data/pdb/unk.pdb";
        IChemFile chemFile = getChemFile(filename);
        IAtomContainer atomContainer = getFirstAtomContainer(chemFile, 1, 1, 1);
        Assert.assertEquals(5, atomContainer.getAtomCount());
        for (IAtom atom : atomContainer.atoms()) {
            Assert.assertFalse("Improper element symbol " + atom.getSymbol(), atom.getSymbol().equalsIgnoreCase("1h"));
        }
    }

    @Test
    public void testHetatmOnly() throws Exception {
        String filename = "data/pdb/hetatm_only.pdb";
        IChemFile chemFile = getChemFile(filename, true);
        IAtomContainer atomContainer = getFirstAtomContainer(chemFile, 1, 1, 1);
        Assert.assertTrue(atomContainer instanceof IAtomContainer);
        Assert.assertEquals(14, atomContainer.getAtomCount());
        Assert.assertEquals(15, atomContainer.getBondCount());
    }

    @Test
    public void test1SPX() throws Exception {
        String filename = "data/pdb/1SPX.pdb";
        IChemFile chemFile = getChemFile(filename);
        testObjectCountsChemFile(chemFile, 1, 1, 1, 1904, 1, 237, 19);
    }

    @Test
    public void test1XKQ() throws Exception {
        String filename = "data/pdb/1XKQ.pdb";
        IChemFile chemFile = getChemFile(filename);
        testObjectCountsChemFile(chemFile, 1, 1, 1, 8955, 4, 1085, 90);
    }

    @Test
    public void test1A00() throws Exception {
        String filename = "data/pdb/1A00.pdb";
        IChemFile chemFile = getChemFile(filename);
        testObjectCountsChemFile(chemFile, 1, 1, 1, 4770, 4, 574, 35);
    }

    @Test
    public void test1BOQ() throws Exception {
        String filename = "data/pdb/1BOQ.pdb";
        IChemFile chemFile = getChemFile(filename);
        testObjectCountsChemFile(chemFile, 1, 1, 1, 1538, 1, 198, 21);
    }

    @Test
    public void test1TOH() throws Exception {
        String filename = "data/pdb/1TOH.pdb";
        IChemFile chemFile = getChemFile(filename);
        testObjectCountsChemFile(chemFile, 1, 1, 1, 2804, 1, 325, 23);
    }

    @Category(SlowTest.class)
    @Test
    public void test1CKV() throws Exception {
        String filename = "data/pdb/1CKV.pdb";
        IChemFile chemFile = getChemFile(filename);
        testObjectCountsChemFile(chemFile, 1, 14, 1, 31066, 1, 141, 9);
    }

    @Test
    public void test1D66() throws Exception {
        String filename = "data/pdb/1D66.pdb";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);

        ISimpleChemObjectReader reader = new PDBReader(ins);
        Assert.assertNotNull(reader);

        IChemFile chemFile = (IChemFile) reader.read(new ChemFile());
        Assert.assertNotNull(chemFile);
        Assert.assertEquals(1, chemFile.getChemSequenceCount());

        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        Assert.assertNotNull(seq);
        Assert.assertEquals(1, seq.getChemModelCount());

        IChemModel model = seq.getChemModel(0);
        Assert.assertNotNull(model);
        Assert.assertEquals(1, model.getMoleculeSet().getAtomContainerCount());

        IAtomContainer container = model.getMoleculeSet().getAtomContainer(0);
        Assert.assertTrue(container instanceof IBioPolymer);
        IBioPolymer polymer = (IBioPolymer) container;

        Assert.assertTrue(polymer instanceof PDBPolymer);
        PDBPolymer pdb = (PDBPolymer) polymer;
        Assert.assertEquals(4, pdb.getStrandCount());

        Assert.assertTrue(polymer.getStrandNames().contains("D"));
        Assert.assertTrue("Strand D is not a PDBStrand", polymer.getStrand("D") instanceof PDBStrand);
        Assert.assertTrue(polymer.getStrandNames().contains("E"));
        Assert.assertTrue("Strand E is not a PDBStrand", polymer.getStrand("E") instanceof PDBStrand);
        Assert.assertTrue(polymer.getStrandNames().contains("A"));
        Assert.assertTrue("Strand A is not a PDBStrand", polymer.getStrand("A") instanceof PDBStrand);
        Assert.assertTrue(polymer.getStrandNames().contains("B"));
        Assert.assertTrue("Strand B is not a PDBStrand", polymer.getStrand("B") instanceof PDBStrand);

        //Check to pick up all 4 strands
        Assert.assertEquals(polymer.getStrands().size(), 4);

        //The following check is to see that the first monomers in a strand
        //can be accessed consecutively
        //i.e. their resSeq numbering follows that in the File

        //Strand A
        PDBStrand strandA = (PDBStrand) polymer.getStrand("A");
        Collection<String> lst = strandA.getMonomerNamesInSequentialOrder();

        //Should be 57 monomers in strand A
        Assert.assertEquals(57, lst.size());
        Iterator<String> lstIter = lst.iterator();

        String monomer1 = lstIter.next();
        IMonomer mono1 = strandA.getMonomer(monomer1);
        Assert.assertNotNull(mono1);
        Assert.assertNotNull(mono1.getMonomerName());
        Assert.assertTrue("Monomer is not a PDBMonomer", mono1 instanceof PDBMonomer);
        PDBMonomer pdbMonomer = (PDBMonomer) mono1;
        Assert.assertEquals("A", pdbMonomer.getChainID());
        Assert.assertEquals("8", pdbMonomer.getResSeq());

        monomer1 = lstIter.next();
        mono1 = strandA.getMonomer(monomer1);
        pdbMonomer = (PDBMonomer) mono1;
        Assert.assertEquals("A", pdbMonomer.getChainID());
        Assert.assertEquals("9", pdbMonomer.getResSeq());

        monomer1 = lstIter.next();
        mono1 = strandA.getMonomer(monomer1);
        pdbMonomer = (PDBMonomer) mono1;
        Assert.assertEquals("A", pdbMonomer.getChainID());
        Assert.assertEquals("10", pdbMonomer.getResSeq());

        //Strand B
        PDBStrand strandB = (PDBStrand) polymer.getStrand("B");
        lst = strandB.getMonomerNamesInSequentialOrder();

        //Should be 57 monomers in strand B
        Assert.assertEquals(57, lst.size());
        lstIter = lst.iterator();

        monomer1 = lstIter.next();
        mono1 = strandB.getMonomer(monomer1);
        Assert.assertNotNull(mono1);
        Assert.assertNotNull(mono1.getMonomerName());
        Assert.assertTrue("Monomer is not a PDBMonomer", mono1 instanceof PDBMonomer);
        pdbMonomer = (PDBMonomer) mono1;
        Assert.assertEquals("B", pdbMonomer.getChainID());
        Assert.assertEquals("8", pdbMonomer.getResSeq());

        monomer1 = lstIter.next();
        mono1 = strandB.getMonomer(monomer1);
        pdbMonomer = (PDBMonomer) mono1;
        Assert.assertEquals("B", pdbMonomer.getChainID());
        Assert.assertEquals("9", pdbMonomer.getResSeq());

        monomer1 = lstIter.next();
        mono1 = strandB.getMonomer(monomer1);
        pdbMonomer = (PDBMonomer) mono1;
        Assert.assertEquals("B", pdbMonomer.getChainID());
        Assert.assertEquals("10", pdbMonomer.getResSeq());

        //Strand E
        PDBStrand strandE = (PDBStrand) polymer.getStrand("E");
        lst = strandE.getMonomerNamesInSequentialOrder();

        //Should be 19 monomers in strand E
        Assert.assertEquals(19, lst.size());
        lstIter = lst.iterator();

        monomer1 = lstIter.next();
        mono1 = strandE.getMonomer(monomer1);
        Assert.assertNotNull(mono1);
        Assert.assertNotNull(mono1.getMonomerName());
        Assert.assertTrue("Monomer is not a PDBMonomer", mono1 instanceof PDBMonomer);
        pdbMonomer = (PDBMonomer) mono1;
        Assert.assertEquals("E", pdbMonomer.getChainID());
        Assert.assertEquals("20", pdbMonomer.getResSeq());

        monomer1 = lstIter.next();
        mono1 = strandE.getMonomer(monomer1);
        pdbMonomer = (PDBMonomer) mono1;
        Assert.assertEquals("E", pdbMonomer.getChainID());
        Assert.assertEquals("21", pdbMonomer.getResSeq());

        monomer1 = lstIter.next();
        mono1 = strandE.getMonomer(monomer1);
        pdbMonomer = (PDBMonomer) mono1;
        Assert.assertEquals("E", pdbMonomer.getChainID());
        Assert.assertEquals("22", pdbMonomer.getResSeq());

        //Chain D should be 1,2,3...19
        PDBStrand strandD = (PDBStrand) polymer.getStrand("D");
        lst = strandD.getMonomerNamesInSequentialOrder();

        //Should be 19 monomers in strand D
        Assert.assertEquals(19, lst.size());
        lstIter = lst.iterator();

        monomer1 = lstIter.next();
        mono1 = strandD.getMonomer(monomer1);
        Assert.assertNotNull(mono1);
        Assert.assertNotNull(mono1.getMonomerName());
        Assert.assertTrue("Monomer is not a PDBMonomer", mono1 instanceof PDBMonomer);
        pdbMonomer = (PDBMonomer) mono1;
        Assert.assertEquals("D", pdbMonomer.getChainID());
        Assert.assertEquals("1", pdbMonomer.getResSeq());

        monomer1 = lstIter.next();
        mono1 = strandD.getMonomer(monomer1);
        pdbMonomer = (PDBMonomer) mono1;
        Assert.assertEquals("D", pdbMonomer.getChainID());
        Assert.assertEquals("2", pdbMonomer.getResSeq());

        monomer1 = lstIter.next();
        mono1 = strandD.getMonomer(monomer1);
        pdbMonomer = (PDBMonomer) mono1;
        Assert.assertEquals("D", pdbMonomer.getChainID());
        Assert.assertEquals("3", pdbMonomer.getResSeq());

        // PDB Structures validation
        //Should have 6 helices
        Assert.assertEquals(6, pdb.getStructures().size());

    }

    /**
     * @cdk.bug 489
     */
    @Category(SlowTest.class)
    @Test public void readFinalPump() throws Exception {
        IChemFile chemFile = new PDBReader(getClass().getResourceAsStream("finalPump96.09.06.pdb")).read(new ChemFile());
    }

}
