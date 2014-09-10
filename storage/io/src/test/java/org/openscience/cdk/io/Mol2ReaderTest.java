/* Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@slists.sourceforge.net
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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.SlowTest;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.silent.AtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;

/**
 * TestCase for the reading SYBYL mol2 files using a test file.
 *
 * @cdk.module test-io
 *
 * @see org.openscience.cdk.io.Mol2Reader
 */
public class Mol2ReaderTest extends SimpleChemObjectReaderTest {

    private static ILoggingTool logger = LoggingToolFactory.createLoggingTool(Mol2ReaderTest.class);

    @BeforeClass
    public static void setup() {
        setSimpleChemObjectReader(new Mol2Reader(), "data/mol2/fromWebsite.mol2");
    }

    @Test
    public void testAccepts() {
        Mol2Reader reader = new Mol2Reader();
        Assert.assertTrue(reader.accepts(ChemFile.class));
        Assert.assertTrue(reader.accepts(ChemModel.class));
        Assert.assertTrue(reader.accepts(AtomContainer.class));
    }

    /**
     * Test example from website. See
     * <a href="http://www.tripos.com/custResources/mol2Files/mol2_format3.html">Tripos example</a>.
     */
    @Test
    public void testExampleFromWebsite() throws Exception {
        String filename = "data/mol2/fromWebsite.mol2";
        logger.info("Testing: ", filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        Mol2Reader reader = new Mol2Reader(ins);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        reader.close();

        Assert.assertNotNull(chemFile);
        Assert.assertEquals(1, chemFile.getChemSequenceCount());
        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        Assert.assertNotNull(seq);
        Assert.assertEquals(1, seq.getChemModelCount());
        org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
        Assert.assertNotNull(model);

        IAtomContainerSet som = model.getMoleculeSet();
        Assert.assertNotNull(som);
        Assert.assertEquals(1, som.getAtomContainerCount());
        IAtomContainer m = som.getAtomContainer(0);
        Assert.assertNotNull(m);
        Assert.assertEquals(12, m.getAtomCount());
        Assert.assertEquals(12, m.getBondCount());

        Assert.assertEquals("C.ar", m.getAtom(0).getAtomTypeName());
        Assert.assertEquals("C", m.getAtom(0).getSymbol());
        Assert.assertEquals("H", m.getAtom(6).getAtomTypeName());
        Assert.assertEquals("H", m.getAtom(6).getSymbol());
    }

    @Test
    public void testReadingIDs() throws Exception {
        String filename = "data/mol2/fromWebsite.mol2";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        Mol2Reader reader = new Mol2Reader(ins);
        IAtomContainer molecule = (IAtomContainer) reader.read(new AtomContainer());
        reader.close();
        Assert.assertNotNull(molecule);
        IAtomContainer reference = (IAtomContainer) molecule.clone();
        Assert.assertEquals("C1", reference.getAtom(0).getID());
    }

    /**
     * Tests the Mol2Reader with about 30% of the NCI molecules.
     *
     * @throws Exception if an error occurs
     */
    @Category(SlowTest.class)
    @Test
    public void testNCIfeb03_2D() throws Exception {
        Assume.assumeTrue(runSlowTests());

        String filename = "data/mol2/NCI_feb03_2D.mol2.gz";
        InputStream in = new GZIPInputStream(Mol2ReaderTest.class.getClassLoader().getResourceAsStream(filename));
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        StringBuilder buf = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            if (line.startsWith("@<TRIPOS>MOLECULE") && (buf.length() > 0)) {
                checkMol(buf);
                buf.delete(0, buf.length() - 1);
            }
            buf.append(line).append('\n');
        }
        if (buf.length() > 0) {
            checkMol(buf);
        }
    }

    @Test
    public void testMultiMol() throws Exception {
        Assume.assumeTrue(runSlowTests());
        String filename = "data/mol2/actives.mol2";
        logger.info("Testing: ", filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        Mol2Reader reader = new Mol2Reader(ins);
        IChemFile chemFile = reader.read(new ChemFile());
        reader.close();
        List<IAtomContainer> mols = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assert.assertEquals(30, mols.size());
        Assert.assertEquals(25, mols.get(0).getAtomCount());
        Assert.assertEquals(24, mols.get(29).getAtomCount());
    }

    @Test
    public void testMultiMolButSingle() throws Exception {
        Assume.assumeTrue(runSlowTests());
        String filename = "data/mol2/fromWebsite.mol2";
        logger.info("Testing: ", filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        Mol2Reader reader = new Mol2Reader(ins);
        IChemFile chemFile = reader.read(new ChemFile());
        reader.close();
        List<IAtomContainer> mols = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assert.assertEquals(1, mols.size());
        Assert.assertEquals(12, mols.get(0).getAtomCount());

    }

    @Test
    public void testIAtomContainer() throws Exception {
        String filename = "data/mol2/fromWebsite.mol2";
        InputStream in = Mol2ReaderTest.class.getClassLoader().getResourceAsStream(filename);
        Mol2Reader reader = new Mol2Reader(in);
        IAtomContainer mol = (IAtomContainer) reader.read(new AtomContainer());
        reader.close();
        Assert.assertNotNull(mol);
        Assert.assertEquals(12, mol.getAtomCount());
        Assert.assertEquals(12, mol.getBondCount());
    }

    @Test
    public void testBug1714794() throws Exception {
        String problematicMol2 = "@<TRIPOS>MOLECULE\n" + "mol_197219.smi\n" + " 129 135 0 0 0\n" + "SMALL\n"
                + "GASTEIGER\n" + "Energy = 0\n" + "\n" + "@<TRIPOS>ATOM\n"
                + "      1 N1          0.0000    0.0000    0.0000 N.am    1  <1>        -0.2782\n"
                + "      2 H1          0.0000    0.0000    0.0000 H       1  <1>         0.1552\n"
                + "      3 C1          0.0000    0.0000    0.0000 C.ar    1  <1>         0.0886\n"
                + "      4 C2          0.0000    0.0000    0.0000 C.ar    1  <1>         0.1500\n"
                + "      5 C3          0.0000    0.0000    0.0000 C.ar    1  <1>         0.0714\n"
                + "      6 C4          0.0000    0.0000    0.0000 C.ar    1  <1>         0.0456\n"
                + "      7 C5          0.0000    0.0000    0.0000 C.ar    1  <1>         0.0788\n"
                + "      8 C6          0.0000    0.0000    0.0000 C.ar    1  <1>         0.1435\n"
                + "      9 C7          0.0000    0.0000    0.0000 C.ar    1  <1>         0.0342\n"
                + "     10 C8          0.0000    0.0000    0.0000 C.ar    1  <1>         0.1346\n"
                + "     11 O1          0.0000    0.0000    0.0000 O.3     1  <1>        -0.5057\n"
                + "     12 H2          0.0000    0.0000    0.0000 H       1  <1>         0.2922\n"
                + "     13 C9          0.0000    0.0000    0.0000 C.3     1  <1>        -0.0327\n"
                + "     14 H3          0.0000    0.0000    0.0000 H       1  <1>         0.0280\n"
                + "     15 H4          0.0000    0.0000    0.0000 H       1  <1>         0.0280\n"
                + "     16 H5          0.0000    0.0000    0.0000 H       1  <1>         0.0280\n"
                + "     17 O2          0.0000    0.0000    0.0000 O.3     1  <1>        -0.4436\n"
                + "     18 C10         0.0000    0.0000    0.0000 C.3     1  <1>         0.3143\n"
                + "     19 O3          0.0000    0.0000    0.0000 O.2     1  <1>        -0.4528\n"
                + "     20 C11         0.0000    0.0000    0.0000 C.2     1  <1>         0.0882\n"
                + "     21 H6          0.0000    0.0000    0.0000 H       1  <1>         0.1022\n"
                + "     22 C12         0.0000    0.0000    0.0000 C.2     1  <1>        -0.0208\n"
                + "     23 H7          0.0000    0.0000    0.0000 H       1  <1>         0.0628\n"
                + "     24 C13         0.0000    0.0000    0.0000 C.3     1  <1>         0.0854\n"
                + "     25 H8          0.0000    0.0000    0.0000 H       1  <1>         0.0645\n"
                + "     26 C14         0.0000    0.0000    0.0000 C.3     1  <1>         0.0236\n"
                + "     27 H9          0.0000    0.0000    0.0000 H       1  <1>         0.0362\n"
                + "     28 C15         0.0000    0.0000    0.0000 C.3     1  <1>         0.1131\n"
                + "     29 H10         0.0000    0.0000    0.0000 H       1  <1>         0.0741\n"
                + "     30 C16         0.0000    0.0000    0.0000 C.3     1  <1>         0.0200\n"
                + "     31 H11         0.0000    0.0000    0.0000 H       1  <1>         0.0359\n"
                + "     32 C17         0.0000    0.0000    0.0000 C.3     1  <1>         0.0661\n"
                + "     33 H12         0.0000    0.0000    0.0000 H       1  <1>         0.0600\n"
                + "     34 C18         0.0000    0.0000    0.0000 C.3     1  <1>         0.0091\n"
                + "     35 H13         0.0000    0.0000    0.0000 H       1  <1>         0.0348\n"
                + "     36 C19         0.0000    0.0000    0.0000 C.3     1  <1>         0.0661\n"
                + "     37 H14         0.0000    0.0000    0.0000 H       1  <1>         0.0602\n"
                + "     38 C20         0.0000    0.0000    0.0000 C.3     1  <1>         0.0009\n"
                + "     39 H15         0.0000    0.0000    0.0000 H       1  <1>         0.0365\n"
                + "     40 C21         0.0000    0.0000    0.0000 C.2     1  <1>        -0.0787\n"
                + "     41 H16         0.0000    0.0000    0.0000 H       1  <1>         0.0576\n"
                + "     42 C22         0.0000    0.0000    0.0000 C.2     1  <1>        -0.0649\n"
                + "     43 H17         0.0000    0.0000    0.0000 H       1  <1>         0.0615\n"
                + "     44 C23         0.0000    0.0000    0.0000 C.2     1  <1>        -0.0542\n"
                + "     45 H18         0.0000    0.0000    0.0000 H       1  <1>         0.0622\n"
                + "     46 C24         0.0000    0.0000    0.0000 C.2     1  <1>         0.0115\n"
                + "     47 C25         0.0000    0.0000    0.0000 C.2     1  <1>         0.2441\n"
                + "     48 O4          0.0000    0.0000    0.0000 O.2     1  <1>        -0.2702\n"
                + "     49 C26         0.0000    0.0000    0.0000 C.3     1  <1>        -0.0348\n"
                + "     50 H19         0.0000    0.0000    0.0000 H       1  <1>         0.0279\n"
                + "     51 H20         0.0000    0.0000    0.0000 H       1  <1>         0.0279\n"
                + "     52 H21         0.0000    0.0000    0.0000 H       1  <1>         0.0279\n"
                + "     53 C27         0.0000    0.0000    0.0000 C.3     1  <1>        -0.0566\n"
                + "     54 H22         0.0000    0.0000    0.0000 H       1  <1>         0.0236\n"
                + "     55 H23         0.0000    0.0000    0.0000 H       1  <1>         0.0236\n"
                + "     56 H24         0.0000    0.0000    0.0000 H       1  <1>         0.0236\n"
                + "     57 O5          0.0000    0.0000    0.0000 O.3     1  <1>        -0.3909\n"
                + "     58 H25         0.0000    0.0000    0.0000 H       1  <1>         0.2098\n"
                + "     59 C28         0.0000    0.0000    0.0000 C.3     1  <1>        -0.0577\n"
                + "     60 H26         0.0000    0.0000    0.0000 H       1  <1>         0.0234\n"
                + "     61 H27         0.0000    0.0000    0.0000 H       1  <1>         0.0234\n"
                + "     62 H28         0.0000    0.0000    0.0000 H       1  <1>         0.0234\n"
                + "     63 O6          0.0000    0.0000    0.0000 O.3     1  <1>        -0.3910\n"
                + "     64 H29         0.0000    0.0000    0.0000 H       1  <1>         0.2098\n"
                + "     65 C29         0.0000    0.0000    0.0000 C.3     1  <1>        -0.0567\n"
                + "     66 H30         0.0000    0.0000    0.0000 H       1  <1>         0.0234\n"
                + "     67 H31         0.0000    0.0000    0.0000 H       1  <1>         0.0234\n"
                + "     68 H32         0.0000    0.0000    0.0000 H       1  <1>         0.0234\n"
                + "     69 O7          0.0000    0.0000    0.0000 O.3     1  <1>        -0.4608\n"
                + "     70 C30         0.0000    0.0000    0.0000 C.2     1  <1>         0.3042\n"
                + "     71 O8          0.0000    0.0000    0.0000 O.2     1  <1>        -0.2512\n"
                + "     72 C31         0.0000    0.0000    0.0000 C.3     1  <1>         0.0332\n"
                + "     73 H33         0.0000    0.0000    0.0000 H       1  <1>         0.0342\n"
                + "     74 H34         0.0000    0.0000    0.0000 H       1  <1>         0.0342\n"
                + "     75 H35         0.0000    0.0000    0.0000 H       1  <1>         0.0342\n"
                + "     76 C32         0.0000    0.0000    0.0000 C.3     1  <1>        -0.0564\n"
                + "     77 H36         0.0000    0.0000    0.0000 H       1  <1>         0.0234\n"
                + "     78 H37         0.0000    0.0000    0.0000 H       1  <1>         0.0234\n"
                + "     79 H38         0.0000    0.0000    0.0000 H       1  <1>         0.0234\n"
                + "     80 O9          0.0000    0.0000    0.0000 O.3     1  <1>        -0.3753\n"
                + "     81 C33         0.0000    0.0000    0.0000 C.3     1  <1>         0.0372\n"
                + "     82 H39         0.0000    0.0000    0.0000 H       1  <1>         0.0524\n"
                + "     83 H40         0.0000    0.0000    0.0000 H       1  <1>         0.0524\n"
                + "     84 H41         0.0000    0.0000    0.0000 H       1  <1>         0.0524\n"
                + "     85 C34         0.0000    0.0000    0.0000 C.2     1  <1>         0.2505\n"
                + "     86 O10         0.0000    0.0000    0.0000 O.2     1  <1>        -0.2836\n"
                + "     87 C35         0.0000    0.0000    0.0000 C.3     1  <1>         0.0210\n"
                + "     88 H42         0.0000    0.0000    0.0000 H       1  <1>         0.0309\n"
                + "     89 H43         0.0000    0.0000    0.0000 H       1  <1>         0.0309\n"
                + "     90 H44         0.0000    0.0000    0.0000 H       1  <1>         0.0309\n"
                + "     91 C36         0.0000    0.0000    0.0000 C.ar    1  <1>         0.1361\n"
                + "     92 C37         0.0000    0.0000    0.0000 C.ar    1  <1>         0.0613\n"
                + "     93 C38         0.0000    0.0000    0.0000 C.2     1  <1>         0.0580\n"
                + "     94 H45         0.0000    0.0000    0.0000 H       1  <1>         0.0853\n"
                + "     95 N2          0.0000    0.0000    0.0000 N.2     1  <1>        -0.1915\n"
                + "     96 N3          0.0000    0.0000    0.0000 N.pl3   1  <1>        -0.2525\n"
                + "     97 C39         0.0000    0.0000    0.0000 C.3     1  <1>         0.0525\n"
                + "     98 C40         0.0000    0.0000    0.0000 C.3     1  <1>        -0.0271\n"
                + "     99 H46         0.0000    0.0000    0.0000 H       1  <1>         0.0289\n"
                + "    100 H47         0.0000    0.0000    0.0000 H       1  <1>         0.0289\n"
                + "    101 C41         0.0000    0.0000    0.0000 C.3     1  <1>        -0.0385\n"
                + "    102 H48         0.0000    0.0000    0.0000 H       1  <1>         0.0302\n"
                + "    103 C42         0.0000    0.0000    0.0000 C.3     1  <1>        -0.0472\n"
                + "    104 H49         0.0000    0.0000    0.0000 H       1  <1>         0.0271\n"
                + "    105 H50         0.0000    0.0000    0.0000 H       1  <1>         0.0271\n"
                + "    106 C43         0.0000    0.0000    0.0000 C.3     1  <1>        -0.0385\n"
                + "    107 H51         0.0000    0.0000    0.0000 H       1  <1>         0.0302\n"
                + "    108 C44         0.0000    0.0000    0.0000 C.3     1  <1>        -0.0271\n"
                + "    109 H52         0.0000    0.0000    0.0000 H       1  <1>         0.0289\n"
                + "    110 H53         0.0000    0.0000    0.0000 H       1  <1>         0.0289\n"
                + "    111 C45         0.0000    0.0000    0.0000 C.3     1  <1>        -0.0472\n"
                + "    112 H54         0.0000    0.0000    0.0000 H       1  <1>         0.0271\n"
                + "    113 H55         0.0000    0.0000    0.0000 H       1  <1>         0.0271\n"
                + "    114 C46         0.0000    0.0000    0.0000 C.3     1  <1>        -0.0385\n"
                + "    115 H56         0.0000    0.0000    0.0000 H       1  <1>         0.0302\n"
                + "    116 C47         0.0000    0.0000    0.0000 C.3     1  <1>        -0.0271\n"
                + "    117 H57         0.0000    0.0000    0.0000 H       1  <1>         0.0289\n"
                + "    118 H58         0.0000    0.0000    0.0000 H       1  <1>         0.0289\n"
                + "    119 C48         0.0000    0.0000    0.0000 C.3     1  <1>        -0.0472\n"
                + "    120 H59         0.0000    0.0000    0.0000 H       1  <1>         0.0271\n"
                + "    121 H60         0.0000    0.0000    0.0000 H       1  <1>         0.0271\n"
                + "    122 C49         0.0000    0.0000    0.0000 C.3     1  <1>         0.0189\n"
                + "    123 H61         0.0000    0.0000    0.0000 H       1  <1>         0.0444\n"
                + "    124 H62         0.0000    0.0000    0.0000 H       1  <1>         0.0444\n"
                + "    125 H63         0.0000    0.0000    0.0000 H       1  <1>         0.0444\n"
                + "    126 O11         0.0000    0.0000    0.0000 O.3     1  <1>        -0.5054\n"
                + "    127 H64         0.0000    0.0000    0.0000 H       1  <1>         0.2922\n"
                + "    128 O12         0.0000    0.0000    0.0000 O.3     1  <1>        -0.5042\n"
                + "    129 H65         0.0000    0.0000    0.0000 H       1  <1>         0.2923\n" + "@<TRIPOS>BOND\n"
                + "     1     1     2    1\n" + "     2     1     3    1\n" + "     3     3     4   ar\n"
                + "     4     4     5   ar\n" + "     5     5     6   ar\n" + "     6     6     7   ar\n"
                + "     7     7     8   ar\n" + "     8     8     9   ar\n" + "     9     9    10   ar\n"
                + "    10     5    10   ar\n" + "    11    10    11    1\n" + "    12    11    12    1\n"
                + "    13     9    13    1\n" + "    14    13    14    1\n" + "    15    13    15    1\n"
                + "    16    13    16    1\n" + "    17     8    17    1\n" + "    18    17    18    1\n"
                + "    19    18    19    1\n" + "    20    19    20    1\n" + "    21    20    21    1\n"
                + "    22    20    22    2\n" + "    23    22    23    1\n" + "    24    22    24    1\n"
                + "    25    24    25    1\n" + "    26    24    26    1\n" + "    27    26    27    1\n"
                + "    28    26    28    1\n" + "    29    28    29    1\n" + "    30    28    30    1\n"
                + "    31    30    31    1\n" + "    32    30    32    1\n" + "    33    32    33    1\n"
                + "    34    32    34    1\n" + "    35    34    35    1\n" + "    36    34    36    1\n"
                + "    37    36    37    1\n" + "    38    36    38    1\n" + "    39    38    39    1\n"
                + "    40    38    40    1\n" + "    41    40    41    1\n" + "    42    40    42    2\n"
                + "    43    42    43    1\n" + "    44    42    44    1\n" + "    45    44    45    1\n"
                + "    46    44    46    2\n" + "    47    46    47    1\n" + "    48     1    47   am\n"
                + "    49    47    48    2\n" + "    50    46    49    1\n" + "    51    49    50    1\n"
                + "    52    49    51    1\n" + "    53    49    52    1\n" + "    54    38    53    1\n"
                + "    55    53    54    1\n" + "    56    53    55    1\n" + "    57    53    56    1\n"
                + "    58    36    57    1\n" + "    59    57    58    1\n" + "    60    34    59    1\n"
                + "    61    59    60    1\n" + "    62    59    61    1\n" + "    63    59    62    1\n"
                + "    64    32    63    1\n" + "    65    63    64    1\n" + "    66    30    65    1\n"
                + "    67    65    66    1\n" + "    68    65    67    1\n" + "    69    65    68    1\n"
                + "    70    28    69    1\n" + "    71    69    70    1\n" + "    72    70    71    2\n"
                + "    73    70    72    1\n" + "    74    72    73    1\n" + "    75    72    74    1\n"
                + "    76    72    75    1\n" + "    77    26    76    1\n" + "    78    76    77    1\n"
                + "    79    76    78    1\n" + "    80    76    79    1\n" + "    81    24    80    1\n"
                + "    82    80    81    1\n" + "    83    81    82    1\n" + "    84    81    83    1\n"
                + "    85    81    84    1\n" + "    86    18    85    1\n" + "    87     7    85    1\n"
                + "    88    85    86    2\n" + "    89    18    87    1\n" + "    90    87    88    1\n"
                + "    91    87    89    1\n" + "    92    87    90    1\n" + "    93     6    91   ar\n"
                + "    94    91    92   ar\n" + "    95     3    92   ar\n" + "    96    92    93    1\n"
                + "    97    93    94    1\n" + "    98    93    95    2\n" + "    99    95    96    1\n"
                + "   100    96    97    1\n" + "   101    97    98    1\n" + "   102    98    99    1\n"
                + "   103    98   100    1\n" + "   104    98   101    1\n" + "   105   101   102    1\n"
                + "   106   101   103    1\n" + "   107   103   104    1\n" + "   108   103   105    1\n"
                + "   109   103   106    1\n" + "   110   106   107    1\n" + "   111   106   108    1\n"
                + "   112   108   109    1\n" + "   113   108   110    1\n" + "   114    97   108    1\n"
                + "   115   106   111    1\n" + "   116   111   112    1\n" + "   117   111   113    1\n"
                + "   118   111   114    1\n" + "   119   114   115    1\n" + "   120   114   116    1\n"
                + "   121   116   117    1\n" + "   122   116   118    1\n" + "   123    97   116    1\n"
                + "   124   114   119    1\n" + "   125   119   120    1\n" + "   126   119   121    1\n"
                + "   127   101   119    1\n" + "   128    96   122    1\n" + "   129   122   123    1\n"
                + "   130   122   124    1\n" + "   131   122   125    1\n" + "   132    91   126    1\n"
                + "   133   126   127    1\n" + "   134     4   128    1\n" + "   135   128   129    1\n";
        Mol2Reader r = new Mol2Reader(new StringReader(problematicMol2));
        IChemModel model = (IChemModel) r.read(SilentChemObjectBuilder.getInstance().newInstance(IChemModel.class));
        r.close();
        Assert.assertNotNull(model);
        List<IAtomContainer> containers = ChemModelManipulator.getAllAtomContainers(model);
        Assert.assertEquals(1, containers.size());
        IAtomContainer molecule = containers.get(0);
        Assert.assertNotNull(molecule);
        Assert.assertEquals(129, molecule.getAtomCount());
        Assert.assertEquals(135, molecule.getBondCount());
        Iterator<IAtom> atoms = molecule.atoms().iterator();
        while (atoms.hasNext()) {
            IAtom atom = atoms.next();
            Assert.assertNotNull(atom.getAtomTypeName());
        }
    }

    private void checkMol(StringBuilder buf) throws Exception {
        StringReader sr = new StringReader(buf.toString());
        Mol2Reader reader = new Mol2Reader(sr);
        IChemFile mol = (IChemFile) reader.read(SilentChemObjectBuilder.getInstance().newInstance(IChemFile.class));
        reader.close();
        Assert.assertTrue(mol.getChemSequenceCount() > 0);
        Assert.assertTrue(mol.getChemSequence(0).getChemModelCount() > 0);
        Assert.assertTrue(mol.getChemSequence(0).getChemModel(0).getMoleculeSet().getAtomContainerCount() > 0);
        Assert.assertTrue(mol.getChemSequence(0).getChemModel(0).getMoleculeSet().getAtomContainer(0).getAtomCount() > 0);
    }

    // CL --> Cl, NA --> Na etc.. /cdk/bug/1346
    @Test
    public void unrecognisedAtomTypes() throws Exception {
        Mol2Reader mol2Reader = null;
        try {
            mol2Reader = new Mol2Reader(getClass().getResourceAsStream("CLMW1.mol2"));
            IAtomContainer container = mol2Reader.read(new AtomContainer());
            for (IAtom atom : container.atoms())
                Assert.assertNotNull(atom.getAtomicNumber());
        } finally {
            if (mol2Reader != null) mol2Reader.close();
        }
    }
}
