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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.experimental.categories.Category;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.test.SlowTest;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBioPolymer;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.IMonomer;
import org.openscience.cdk.interfaces.IPDBAtom;
import org.openscience.cdk.protein.data.PDBAtom;
import org.openscience.cdk.protein.data.PDBMonomer;
import org.openscience.cdk.protein.data.PDBPolymer;
import org.openscience.cdk.protein.data.PDBStrand;
import org.openscience.cdk.test.io.SimpleChemObjectReaderTest;
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

    @BeforeAll
    public static void setup() throws Exception {
        setSimpleChemObjectReader(new PDBReader(), "org/openscience/cdk/io/coffeine.pdb");
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
        Assertions.assertNotNull(object);
        int bondCount = ((IChemFile) object).getChemSequence(0).getChemModel(0).getMoleculeSet().getAtomContainer(0)
                .getBondCount();
        /*
         * if ReadConnectSection=true and UseRebondTool=false then bondCount ==
         * 1 (from just the CONECT) else if ReadConnectSection=false and
         * UseRebondTool=true then bondCount == 3 (just atoms within bonding
         * distance)
         */
        Assertions.assertEquals(1, bondCount);
    }

    @Test
    public void readCharge() throws Exception {
        String data = "HETATM 3486 MG    MG A 302      24.885  14.008  59.194  1.00 29.42          MG+2\n" + "END";
        IChemFile chemFile = getChemFileFromString(data);
        IAtomContainer atomContainer = getFirstAtomContainer(chemFile, 1, 1, 1);
        Assertions.assertEquals(new Double(2.0), atomContainer.getAtom(0).getCharge());
    }

    @Test
    public void oldFormatNewFormatTest() throws Exception {
        String oldFormat = "ATOM      1 1HA  UNK A   1      20.662  36.632  23.475  1.00 10.00      114D  45\nEND";
        String newFormat = "ATOM      1 1HA  UNK A   1      20.662  36.632  23.475  1.00 10.00           H\nEND";

        IChemFile oldFormatFile = getChemFileFromString(oldFormat);
        IChemFile newFormatFile = getChemFileFromString(newFormat);
        IAtomContainer acOld = getFirstAtomContainer(oldFormatFile, 1, 1, 1);
        IAtomContainer acNew = getFirstAtomContainer(newFormatFile, 1, 1, 1);
        Assertions.assertEquals("H", acOld.getAtom(0).getSymbol());
        Assertions.assertEquals("H", acNew.getAtom(0).getSymbol());
    }

    @Test
    public void emptyFields() throws Exception {
        String data = "HEADER\n" +
                "TITLE\n" +
                "COMPND\n" +
                "KEYWDS\n" +
                "EXPDTA\n" +
                "AUTHOR\n" +
                "REVDAT\n" +
                "HET             1   13\n" +
                "HETNAM\n" +
                "FORMUL\n" +
                "END\n";
        IChemFile chemFile = getChemFileFromString(data);
        IAtomContainer atomContainer = getFirstAtomContainer(chemFile, 1, 1, 1);
    }


    @Test
    public void testAccepts() {
        PDBReader reader = new PDBReader();
        Assertions.assertTrue(reader.accepts(ChemFile.class));
    }

    @Test
    public void testPDBFileCoffein() throws Exception {
        String filename = "coffeine.pdb";
        InputStream ins = this.getClass().getResourceAsStream(filename);

        ISimpleChemObjectReader oReader = new PDBReader(ins);
        Assertions.assertNotNull(oReader);

        IChemFile oChemFile = oReader.read(new ChemFile());
        Assertions.assertNotNull(oChemFile);
        Assertions.assertEquals(oChemFile.getChemSequenceCount(), 1);

        IChemSequence oSeq = oChemFile.getChemSequence(0);
        Assertions.assertNotNull(oSeq);
        Assertions.assertEquals(oSeq.getChemModelCount(), 1);

        IChemModel oModel = oSeq.getChemModel(0);
        Assertions.assertNotNull(oModel);
        Assertions.assertEquals(1, oModel.getMoleculeSet().getAtomContainerCount());

        IAtomContainer container = oModel.getMoleculeSet().getAtomContainer(0);
        Assertions.assertFalse(container instanceof IBioPolymer);
        Assertions.assertTrue(container instanceof IAtomContainer);
        IAtomContainer oMol = container;
        Assertions.assertNotNull(oMol);
        Assertions.assertEquals(oMol.getAtomCount(), 14);

        IAtom nAtom = oMol.getAtom(0);
        Assertions.assertNotNull(nAtom);
        Assertions.assertTrue(nAtom instanceof IPDBAtom);
        IPDBAtom oAtom = (IPDBAtom) nAtom;
        Assertions.assertEquals("C", oAtom.getSymbol());
        Assertions.assertEquals(1, oAtom.getSerial().intValue());
        Assertions.assertEquals("C1", oAtom.getName());
        Assertions.assertEquals("MOL", oAtom.getResName());
        Assertions.assertEquals("1", oAtom.getResSeq());
        Assertions.assertEquals(1.0, oAtom.getOccupancy(), 0);
        Assertions.assertEquals(0.0, oAtom.getTempFactor(), 0);

        nAtom = oMol.getAtom(3);
        Assertions.assertNotNull(nAtom);
        Assertions.assertTrue(nAtom instanceof IPDBAtom);
        oAtom = (IPDBAtom) nAtom;
        Assertions.assertEquals("O", oAtom.getSymbol());
        Assertions.assertEquals(4, oAtom.getSerial().intValue());
        Assertions.assertEquals("O4", oAtom.getName());
        Assertions.assertEquals("MOL", oAtom.getResName());
        Assertions.assertEquals("1", oAtom.getResSeq());
        Assertions.assertEquals(1.0, oAtom.getOccupancy(), 0);
        Assertions.assertEquals(0.0, oAtom.getTempFactor(), 0);

        nAtom = oMol.getAtom(oMol.getAtomCount()-1);
        Assertions.assertNotNull(nAtom);
        Assertions.assertTrue(nAtom instanceof IPDBAtom);
        oAtom = (IPDBAtom) nAtom;
        Assertions.assertEquals("N", oAtom.getSymbol());
        Assertions.assertEquals(14, oAtom.getSerial().intValue());
        Assertions.assertEquals("N14", oAtom.getName());
        Assertions.assertEquals("MOL", oAtom.getResName());
        Assertions.assertEquals("1", oAtom.getResSeq());
        Assertions.assertEquals(1.0, oAtom.getOccupancy(), 0);
        Assertions.assertEquals(0.0, oAtom.getTempFactor(), 0);
    }

    /**
     * Tests reading a protein PDB file.
     */
    @Test
    public void testProtein() throws Exception {
        String filename = "Test-1crn.pdb";
        InputStream ins = this.getClass().getResourceAsStream(filename);

        ISimpleChemObjectReader reader = new PDBReader(ins);
        Assertions.assertNotNull(reader);

        ChemFile chemFile = reader.read(new ChemFile());
        Assertions.assertNotNull(chemFile);
        Assertions.assertEquals(1, chemFile.getChemSequenceCount());

        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        Assertions.assertNotNull(seq);
        Assertions.assertEquals(1, seq.getChemModelCount());

        IChemModel model = seq.getChemModel(0);
        Assertions.assertNotNull(model);
        Assertions.assertEquals(1, model.getMoleculeSet().getAtomContainerCount());

        IAtomContainer container = model.getMoleculeSet().getAtomContainer(0);
        Assertions.assertTrue(container instanceof IBioPolymer);
        IBioPolymer mol = (IBioPolymer) container;
        Assertions.assertNotNull(mol);
        Assertions.assertEquals(327, mol.getAtomCount());
        Assertions.assertEquals(46, mol.getMonomerCount());
        Assertions.assertNotNull(mol.getMonomer("THRA1", "A"));
        Assertions.assertEquals(7, mol.getMonomer("THRA1", "A").getAtomCount());
        Assertions.assertNotNull(mol.getMonomer("ILEA7", "A"));
        Assertions.assertEquals(8, mol.getMonomer("ILEA7", "A").getAtomCount());

        IAtom nAtom = mol.getAtom(94);
        Assertions.assertNotNull(nAtom);
        Assertions.assertTrue(nAtom instanceof PDBAtom);
        PDBAtom atom = (PDBAtom) nAtom;
        Assertions.assertEquals("C", atom.getSymbol());
        Assertions.assertEquals(95, atom.getSerial().intValue());
        Assertions.assertEquals("CZ", atom.getName());
        Assertions.assertEquals("PHE", atom.getResName());
        Assertions.assertEquals("13", atom.getResSeq());
        Assertions.assertEquals(1.0, atom.getOccupancy(), 0.001);
        Assertions.assertEquals(6.84, atom.getTempFactor(), 0.001);

    }

    public IChemFile getChemFileFromString(String data) throws Exception {
        StringReader stringReader = new StringReader(data);
        PDBReader reader = new PDBReader(stringReader);
        Assertions.assertNotNull(reader);
        return getChemFile(reader);
    }

    public IChemFile getChemFile(String filename) throws Exception {
        return getChemFile(filename, false);
    }

    public IChemFile getChemFile(ISimpleChemObjectReader reader) throws Exception {
        return getChemFile(reader, false, false);
    }

    public IChemFile getChemFile(String filename, boolean useRebond) throws Exception {
        InputStream ins = this.getClass().getResourceAsStream(filename);
        return getChemFile(new PDBReader(ins), useRebond, false);
    }

    public IChemFile getChemFile(String filename, boolean useRebond, boolean useHetAtmDict) throws Exception {
        InputStream ins = this.getClass().getResourceAsStream(filename);
        return getChemFile(new PDBReader(ins), useRebond, useHetAtmDict);
    }

    public IChemFile getChemFile(ISimpleChemObjectReader reader, boolean useRebond, boolean useHetAtmDict) throws Exception {
        Assertions.assertNotNull(reader);

        reader.getSetting("UseRebondTool").setSetting(String.valueOf(useRebond));
        reader.getSetting("UseHetDictionary").setSetting(String.valueOf(useHetAtmDict));

        IChemFile chemFile = reader.read(new ChemFile());
        Assertions.assertNotNull(chemFile);
        return chemFile;
    }

    public IAtomContainer getFirstAtomContainer(IChemFile chemFile, int chemSequenceCount, int chemModelCount,
            int moleculeCount) {
        Assertions.assertNotNull(chemFile);
        Assertions.assertEquals(chemSequenceCount, chemFile.getChemSequenceCount());

        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        Assertions.assertNotNull(seq);
        Assertions.assertEquals(chemModelCount, seq.getChemModelCount());

        IChemModel model = seq.getChemModel(0);
        Assertions.assertNotNull(model);
        Assertions.assertEquals(moleculeCount, model.getMoleculeSet().getAtomContainerCount());
        return model.getMoleculeSet().getAtomContainer(0);
    }

    public void testObjectCountsChemFile(IChemFile chemFile, int chemSequenceCount, int chemModelCount,
            int moleculeCount, int atomCount, int strandCount, int monomerCount, int structureCount) throws Exception {
        IAtomContainer container = getFirstAtomContainer(chemFile, chemSequenceCount, chemModelCount, moleculeCount);
        Assertions.assertTrue(container instanceof IBioPolymer);
        IBioPolymer polymer = (IBioPolymer) container;

        // chemical validation
        Assertions.assertEquals(atomCount, ChemFileManipulator.getAtomCount(chemFile));
        Assertions.assertEquals(strandCount, polymer.getStrandCount());
        Assertions.assertEquals(monomerCount, polymer.getMonomerCount());

        Assertions.assertTrue(polymer instanceof PDBPolymer);
        PDBPolymer pdb = (PDBPolymer) polymer;

        // PDB validation
        Assertions.assertEquals(structureCount, pdb.getStructures().size());
    }

    @Test
    public void test114D() throws Exception {
        String filename = "114D.pdb";
        InputStream ins = this.getClass().getResourceAsStream(filename);

        ISimpleChemObjectReader reader = new PDBReader(ins);
        Assertions.assertNotNull(reader);

        IChemFile chemFile = reader.read(new ChemFile());
        Assertions.assertNotNull(chemFile);
        Assertions.assertEquals(1, chemFile.getChemSequenceCount());

        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        Assertions.assertNotNull(seq);
        Assertions.assertEquals(1, seq.getChemModelCount());

        IChemModel model = seq.getChemModel(0);
        Assertions.assertNotNull(model);
        Assertions.assertEquals(1, model.getMoleculeSet().getAtomContainerCount());

        IAtomContainer container = model.getMoleculeSet().getAtomContainer(0);
        Assertions.assertTrue(container instanceof IBioPolymer);
        IBioPolymer polymer = (IBioPolymer) container;

        Assertions.assertTrue(polymer.getStrand("A") instanceof PDBStrand, "Strand A is not a PDBStrand");
        PDBStrand strandA = (PDBStrand) polymer.getStrand("A");
        Iterator<String> lst = strandA.getMonomerNamesInSequentialOrder().iterator();
        String monomer1 = lst.next();
        IMonomer mono1 = strandA.getMonomer(monomer1);
        Assertions.assertNotNull(mono1);
        Assertions.assertNotNull(mono1.getMonomerName());
        Assertions.assertTrue(mono1 instanceof PDBMonomer, "Monomer is not a PDBMonomer");
        PDBMonomer pdbMonomer = (PDBMonomer) mono1;
        Assertions.assertEquals(pdbMonomer.getResSeq(), "1");

        String monomer2 = lst.next();
        IMonomer mono2 = strandA.getMonomer(monomer2);
        Assertions.assertTrue(mono2 instanceof PDBMonomer, "Monomer is not a PDBMonomer");
        PDBMonomer pdbMonomer2 = (PDBMonomer) mono2;
        Assertions.assertEquals(pdbMonomer2.getResSeq(), "2");

        // chemical validation
        Assertions.assertEquals(552, ChemFileManipulator.getAtomCount(chemFile));
        Assertions.assertEquals(2, polymer.getStrandCount());
        Assertions.assertEquals(24, polymer.getMonomerCount());

        Assertions.assertTrue(polymer.getStrandNames().contains("A"));
        Assertions.assertTrue(polymer.getStrandNames().contains("B"));
        Assertions.assertFalse(polymer.getStrandNames().contains("C"));
        Assertions.assertEquals(24, polymer.getMonomerCount());

        Assertions.assertTrue(polymer instanceof PDBPolymer);
        PDBPolymer pdb = (PDBPolymer) polymer;

        // PDB validation
        Assertions.assertEquals(0, pdb.getStructures().size());

    }

    @Test
    public void testUnk() throws Exception {
        String filename = "unk.pdb";
        IChemFile chemFile = getChemFile(filename);
        IAtomContainer atomContainer = getFirstAtomContainer(chemFile, 1, 1, 1);
        Assertions.assertEquals(5, atomContainer.getAtomCount());
        for (IAtom atom : atomContainer.atoms()) {
            Assertions.assertFalse(atom.getSymbol().equalsIgnoreCase("1h"), "Improper element symbol " + atom.getSymbol());
        }
    }

    @Test
    public void testHetatmOnly() throws Exception {
        String filename = "hetatm_only.pdb";
        IChemFile chemFile = getChemFile(filename, true);
        IAtomContainer atomContainer = getFirstAtomContainer(chemFile, 1, 1, 1);
        Assertions.assertTrue(atomContainer instanceof IAtomContainer);
        Assertions.assertEquals(14, atomContainer.getAtomCount());
        Assertions.assertEquals(15, atomContainer.getBondCount());
    }

    @Test
    public void test1SPX() throws Exception {
        String filename = "1SPX.pdb";
        IChemFile chemFile = getChemFile(filename);
        testObjectCountsChemFile(chemFile, 1, 1, 1, 1904, 1, 237, 19);
    }

    @Test
    public void test1XKQ() throws Exception {
        String filename = "1XKQ.pdb";
        IChemFile chemFile = getChemFile(filename);
        testObjectCountsChemFile(chemFile, 1, 1, 1, 8955, 4, 1085, 90);
    }

    @Test
    public void test1A00() throws Exception {
        String filename = "1A00.pdb";
        IChemFile chemFile = getChemFile(filename);
        testObjectCountsChemFile(chemFile, 1, 1, 1, 4770, 4, 574, 35);
    }

    @Test
    public void test1BOQ() throws Exception {
        String filename = "1BOQ.pdb";
        IChemFile chemFile = getChemFile(filename);
        testObjectCountsChemFile(chemFile, 1, 1, 1, 1538, 1, 198, 21);
    }

    @Test
    public void test1TOH() throws Exception {
        String filename = "1TOH.pdb";
        IChemFile chemFile = getChemFile(filename);
        testObjectCountsChemFile(chemFile, 1, 1, 1, 2804, 1, 325, 23);
    }

    public void assertHetAtmTypes(IChemFile chemFile,
                                  String resName,
                                  String ... expected)
    {
        List<String> actual = new ArrayList<>();
        for (IAtomContainer mol : ChemFileManipulator.getAllAtomContainers(chemFile)) {
            for (IAtom atom : mol.atoms()) {
                if (((IPDBAtom)atom).getResName().equals(resName))
                    actual.add(atom.getAtomTypeName());
            }
        }
        Assertions.assertArrayEquals(expected, actual.toArray(new String[0]), "Unexpected HETATOM types for res=" + resName + " was=" + actual);
    }

    @Test
    public void test3PTY() throws Exception {
        String filename = "3PTY.pdb";
        IChemFile chemFile = getChemFile(filename, false, true);
        testObjectCountsChemFile(chemFile, 1, 1, 1, 2258, 1, 284, 32);
        assertHetAtmTypes(chemFile,
                          "AFO",
                          "C.sp3", "O.sp3", "C.sp3", "O.sp3", "C.sp3", "O.sp3", "C.sp3", "O.sp3", "C.sp3", "O.sp3",
                          "C.sp3", "C.sp3", "C.sp3", "C.sp3", "C.sp3", "C.sp3", "C.sp3", "C.sp3");
    }

    @Category(SlowTest.class)
    @Test
    public void test1CKV() throws Exception {
        String filename = "1CKV.pdb";
        IChemFile chemFile = getChemFile(filename);
        testObjectCountsChemFile(chemFile, 1, 14, 1, 31066, 1, 141, 9);
    }

    @Test
    public void test1D66() throws Exception {
        String filename = "1D66.pdb";
        InputStream ins = this.getClass().getResourceAsStream(filename);

        ISimpleChemObjectReader reader = new PDBReader(ins);
        Assertions.assertNotNull(reader);

        IChemFile chemFile = reader.read(new ChemFile());
        Assertions.assertNotNull(chemFile);
        Assertions.assertEquals(1, chemFile.getChemSequenceCount());

        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        Assertions.assertNotNull(seq);
        Assertions.assertEquals(1, seq.getChemModelCount());

        IChemModel model = seq.getChemModel(0);
        Assertions.assertNotNull(model);
        Assertions.assertEquals(1, model.getMoleculeSet().getAtomContainerCount());

        IAtomContainer container = model.getMoleculeSet().getAtomContainer(0);
        Assertions.assertTrue(container instanceof IBioPolymer);
        IBioPolymer polymer = (IBioPolymer) container;

        Assertions.assertTrue(polymer instanceof PDBPolymer);
        PDBPolymer pdb = (PDBPolymer) polymer;
        Assertions.assertEquals(4, pdb.getStrandCount());

        Assertions.assertTrue(polymer.getStrandNames().contains("D"));
        Assertions.assertTrue(polymer.getStrand("D") instanceof PDBStrand, "Strand D is not a PDBStrand");
        Assertions.assertTrue(polymer.getStrandNames().contains("E"));
        Assertions.assertTrue(polymer.getStrand("E") instanceof PDBStrand, "Strand E is not a PDBStrand");
        Assertions.assertTrue(polymer.getStrandNames().contains("A"));
        Assertions.assertTrue(polymer.getStrand("A") instanceof PDBStrand, "Strand A is not a PDBStrand");
        Assertions.assertTrue(polymer.getStrandNames().contains("B"));
        Assertions.assertTrue(polymer.getStrand("B") instanceof PDBStrand, "Strand B is not a PDBStrand");

        //Check to pick up all 4 strands
        Assertions.assertEquals(polymer.getStrands().size(), 4);

        //The following check is to see that the first monomers in a strand
        //can be accessed consecutively
        //i.e. their resSeq numbering follows that in the File

        //Strand A
        PDBStrand strandA = (PDBStrand) polymer.getStrand("A");
        Collection<String> lst = strandA.getMonomerNamesInSequentialOrder();

        //Should be 57 monomers in strand A
        Assertions.assertEquals(57, lst.size());
        Iterator<String> lstIter = lst.iterator();

        String monomer1 = lstIter.next();
        IMonomer mono1 = strandA.getMonomer(monomer1);
        Assertions.assertNotNull(mono1);
        Assertions.assertNotNull(mono1.getMonomerName());
        Assertions.assertTrue(mono1 instanceof PDBMonomer, "Monomer is not a PDBMonomer");
        PDBMonomer pdbMonomer = (PDBMonomer) mono1;
        Assertions.assertEquals("A", pdbMonomer.getChainID());
        Assertions.assertEquals("8", pdbMonomer.getResSeq());

        monomer1 = lstIter.next();
        mono1 = strandA.getMonomer(monomer1);
        pdbMonomer = (PDBMonomer) mono1;
        Assertions.assertEquals("A", pdbMonomer.getChainID());
        Assertions.assertEquals("9", pdbMonomer.getResSeq());

        monomer1 = lstIter.next();
        mono1 = strandA.getMonomer(monomer1);
        pdbMonomer = (PDBMonomer) mono1;
        Assertions.assertEquals("A", pdbMonomer.getChainID());
        Assertions.assertEquals("10", pdbMonomer.getResSeq());

        //Strand B
        PDBStrand strandB = (PDBStrand) polymer.getStrand("B");
        lst = strandB.getMonomerNamesInSequentialOrder();

        //Should be 57 monomers in strand B
        Assertions.assertEquals(57, lst.size());
        lstIter = lst.iterator();

        monomer1 = lstIter.next();
        mono1 = strandB.getMonomer(monomer1);
        Assertions.assertNotNull(mono1);
        Assertions.assertNotNull(mono1.getMonomerName());
        Assertions.assertTrue(mono1 instanceof PDBMonomer, "Monomer is not a PDBMonomer");
        pdbMonomer = (PDBMonomer) mono1;
        Assertions.assertEquals("B", pdbMonomer.getChainID());
        Assertions.assertEquals("8", pdbMonomer.getResSeq());

        monomer1 = lstIter.next();
        mono1 = strandB.getMonomer(monomer1);
        pdbMonomer = (PDBMonomer) mono1;
        Assertions.assertEquals("B", pdbMonomer.getChainID());
        Assertions.assertEquals("9", pdbMonomer.getResSeq());

        monomer1 = lstIter.next();
        mono1 = strandB.getMonomer(monomer1);
        pdbMonomer = (PDBMonomer) mono1;
        Assertions.assertEquals("B", pdbMonomer.getChainID());
        Assertions.assertEquals("10", pdbMonomer.getResSeq());

        //Strand E
        PDBStrand strandE = (PDBStrand) polymer.getStrand("E");
        lst = strandE.getMonomerNamesInSequentialOrder();

        //Should be 19 monomers in strand E
        Assertions.assertEquals(19, lst.size());
        lstIter = lst.iterator();

        monomer1 = lstIter.next();
        mono1 = strandE.getMonomer(monomer1);
        Assertions.assertNotNull(mono1);
        Assertions.assertNotNull(mono1.getMonomerName());
        Assertions.assertTrue(mono1 instanceof PDBMonomer, "Monomer is not a PDBMonomer");
        pdbMonomer = (PDBMonomer) mono1;
        Assertions.assertEquals("E", pdbMonomer.getChainID());
        Assertions.assertEquals("20", pdbMonomer.getResSeq());

        monomer1 = lstIter.next();
        mono1 = strandE.getMonomer(monomer1);
        pdbMonomer = (PDBMonomer) mono1;
        Assertions.assertEquals("E", pdbMonomer.getChainID());
        Assertions.assertEquals("21", pdbMonomer.getResSeq());

        monomer1 = lstIter.next();
        mono1 = strandE.getMonomer(monomer1);
        pdbMonomer = (PDBMonomer) mono1;
        Assertions.assertEquals("E", pdbMonomer.getChainID());
        Assertions.assertEquals("22", pdbMonomer.getResSeq());

        //Chain D should be 1,2,3...19
        PDBStrand strandD = (PDBStrand) polymer.getStrand("D");
        lst = strandD.getMonomerNamesInSequentialOrder();

        //Should be 19 monomers in strand D
        Assertions.assertEquals(19, lst.size());
        lstIter = lst.iterator();

        monomer1 = lstIter.next();
        mono1 = strandD.getMonomer(monomer1);
        Assertions.assertNotNull(mono1);
        Assertions.assertNotNull(mono1.getMonomerName());
        Assertions.assertTrue(mono1 instanceof PDBMonomer, "Monomer is not a PDBMonomer");
        pdbMonomer = (PDBMonomer) mono1;
        Assertions.assertEquals("D", pdbMonomer.getChainID());
        Assertions.assertEquals("1", pdbMonomer.getResSeq());

        monomer1 = lstIter.next();
        mono1 = strandD.getMonomer(monomer1);
        pdbMonomer = (PDBMonomer) mono1;
        Assertions.assertEquals("D", pdbMonomer.getChainID());
        Assertions.assertEquals("2", pdbMonomer.getResSeq());

        monomer1 = lstIter.next();
        mono1 = strandD.getMonomer(monomer1);
        pdbMonomer = (PDBMonomer) mono1;
        Assertions.assertEquals("D", pdbMonomer.getChainID());
        Assertions.assertEquals("3", pdbMonomer.getResSeq());

        // PDB Structures validation
        //Should have 6 helices
        Assertions.assertEquals(6, pdb.getStructures().size());

    }

    /**
     * @cdk.bug 489
     */
    @Category(SlowTest.class)
    @Test
    public void readFinalPump() throws Exception {
        IChemFile chemFile = new PDBReader(getClass().getResourceAsStream("finalPump96.09.06.pdb")).read(new ChemFile());
    }

}
