/* Copyright (C) 2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.atomtype;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.io.ISimpleChemObjectReader;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.io.PDBReader;
import org.openscience.cdk.silent.AtomContainer;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

import java.io.InputStream;
import java.util.List;

/**
 * This class tests the matching of atom types defined in the
 * cdk atom type list, using the test files in <code>src/test/data</code>.
 *
 * @cdk.module test-core
 *
 * @see org.openscience.cdk.atomtype.CDKAtomTypeMatcher
 */
public class CDKAtomTypeMatcherTestFileReposTest extends CDKTestCase {

    @Ignore
    @Test
    public void testPDBfiles() throws Exception {
        final String DIRNAME = "data/pdb/";
        String[] testFiles = {"114D.pdb", "1CRN.pdb", "1D66.pdb", "1IHA.pdb", "1PN8.pdb",};
        int tested = 0;
        int failed = 0;
        ISimpleChemObjectReader reader = new PDBReader();
        for (String testFile : testFiles) {
            TestResults results = testFile(DIRNAME, testFile, reader);
            tested += results.tested;
            failed += results.failed;
        }
        Assert.assertEquals("Could not match all atom types!", tested, (tested - failed));
    }

    @Test
    public void testMOL2files() throws Exception {
        final String DIRNAME = "data/mol2/";
        String[] testFiles = {"fromWebsite.mol2",};
        int tested = 0;
        int failed = 0;
        ISimpleChemObjectReader reader = new PDBReader();
        for (String testFile : testFiles) {
            TestResults results = testFile(DIRNAME, testFile, reader);
            tested += results.tested;
            failed += results.failed;
        }
        Assert.assertEquals("Could not match all atom types!", tested, (tested - failed));
    }

    @Test
    public void testASNfiles() throws Exception {
        final String DIRNAME = "data/asn/pubchem/";
        String[] testFiles = {"cid1.asn",};
        int tested = 0;
        int failed = 0;
        ISimpleChemObjectReader reader = new PDBReader();
        for (String testFile : testFiles) {
            TestResults results = testFile(DIRNAME, testFile, reader);
            tested += results.tested;
            failed += results.failed;
        }
        Assert.assertEquals("Could not match all atom types!", tested, (tested - failed));
    }

    @Test
    public void testMDLMolfiles() throws Exception {
        final String DIRNAME = "data/mdl/";
        String[] testFiles = {"2,5-dimethyl-furan.mol", "5SD.mol", "9553.mol", "9554.mol", "ADN.mol", "allmol231.mol",
                "allmol232.mol", "a-pinene.mol", "azulene.mol", "big.mol", "BremserPredictionTest.mol",
                "bug1014344-1.mol", "bug1089770-1.mol", "bug1089770-2.mol", "bug1328739.mol", "bug_1750968.mol",
                "bug1772609.mol", "bug682233.mol", "bug698152.mol", "bug706786-1.mol", "bug706786-2.mol",
                "bug716259.mol", "bug771485-1.mol", "bug771485-2.mol", "bug853254-1.mol", "bug853254-2.mol",
                "bug931608-1.mol", "bug931608-2.mol", "bug934819-1.mol", "bug934819-2.mol", "Butane-TestFF.mol",
                "Butane-TestFF-output.mol", "butanoic_acid.mol", "C12308.mol", "carbocations.mol", "choloylcoa.mol",
                "clorobenzene.mol", "cyclooctadien.mol", "cyclooctan.mol", "cycloocten.mol", "cyclopropane.mol",
                "d-ala.mol", "decalin.mol", "D+-glucose.mol", "D-mannose.mol", "Ethane-TestFF.mol",
                "Ethane-TestFF-output.mol", "figueras-test-buried.mol", "figueras-test-inring.mol",
                "figueras-test-sep3D.mol", "four-ring-5x10.mol", "heptane_almost_cyclic.mol",
                "heptane_almost_cyclic-output.mol",
                "heptane-modelbuilder.mol",
                "heptane-modelbuilder-output.mol",
                "heptane.mol",
                "Heptane-TestFF.mol",
                "Heptane-TestFF-output.mol",
                "hydroxyamino.mol",
                "isopropylacetate.mol",
                "l-ala.mol",
                "methylbenzol.mol",
                // "molV3000.mol", // can't be read with MDLV2000Reader!
                "murckoTest10.mol", "murckoTest11.mol", "murckoTest1.mol", "murckoTest2.mol", "murckoTest3.mol",
                "murckoTest4.mol", "murckoTest5.mol", "murckoTest6_3d_2.mol", "murckoTest6_3d.mol", "murckoTest6.mol",
                "murckoTest7.mol", "murckoTest8.mol", "murckoTest9.mol", "NN_dimethylaniline.mol",
                "nonConnectedPiSystems.mol", "Ooporphyrin.mol", "piSystemCumulative.mol",
                "piSystemWithCarbokation.mol", "polycarpol.mol", "porphyrin.mol", "prediction-test.mol",
                "reserpine.mol", "ring_03419.mol", "saturationcheckertest.mol", "sdg_test.mol",
                "shortest_path_test.mol", "six-ring-4x4.mol", "sulfurCompound.mol", "superspiro.mol",
                "testdoublebondconfig.mol", "testisopropylacetate.mol", "thiamin.mol", "withcharges.mol",
                "zinc_1309609.sdf", "noxide.sdf", "noxide2.sdf", "noxide3.sdf"};
        int tested = 0;
        int failed = 0;
        ISimpleChemObjectReader reader = new MDLV2000Reader();
        for (String testFile : testFiles) {
            try {
                TestResults results = testFile(DIRNAME, testFile, reader);
                tested += results.tested;
                failed += results.failed;
            } catch (Exception e) {
                Assert.fail(testFile + " caused an error: " + e);
            }
        }
        Assert.assertEquals("Could not match all atom types!", tested, (tested - failed));
    }

    private TestResults testFile(String dir, String filename, ISimpleChemObjectReader reader) throws Exception {
        CDKAtomTypeMatcher matcher = CDKAtomTypeMatcher.getInstance(DefaultChemObjectBuilder.getInstance());
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(dir + filename);
        reader.setReader(ins);
        IAtomContainer mol = null;
        if (reader.accepts(AtomContainer.class)) {
            mol = reader.read(new AtomContainer());
        } else if (reader.accepts(ChemFile.class)) {
            IChemFile cf = reader.read(new ChemFile());
            mol = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
            List<IAtomContainer> containers = ChemFileManipulator.getAllAtomContainers(cf);
            for (IAtomContainer container : containers)
                mol.add(container);
        }

        Assert.assertNotNull("Could not read the file into a IAtomContainer: " + filename, mol);

        TestResults results = new TestResults();
        assert mol != null;
        for (IAtom atom : mol.atoms()) {
            results.tested++;
            IAtomType matched = matcher.findMatchingAtomType(mol, atom);
            if (matched == null) {
                results.failed++;
                System.out.println("Could not match atom: " + results.tested + " in file " + filename);
            } else
            // OK, the matcher did find something. Now, let's see of the
            // atom type properties are consistent with those of the atom
            if (!atom.getSymbol().equals(matched.getSymbol())) {
                // OK, OK, that's very basic indeed, but why not
                results.failed++;
                System.out.println("Symbol does not match: " + results.tested + " in file " + filename);
                System.out.println("Found: " + atom.getSymbol() + ", expected: " + matched.getSymbol());
            } else if (atom.getHybridization() != CDKConstants.UNSET
                    && atom.getHybridization() != matched.getHybridization()) {
                results.failed++;
                System.out.println("Hybridization does not match: " + results.tested + " in file " + filename);
                System.out.println("Found: " + atom.getHybridization() + ", expected: " + matched.getHybridization()
                        + " (" + matched.getAtomTypeName() + ")");
            } else if (atom.getFormalCharge().intValue() != matched.getFormalCharge().intValue()) {
                results.failed++;
                System.out.println("Formal charge does not match: " + results.tested + " in file " + filename);
                System.out.println("Found: " + atom.getFormalCharge() + ", expected: " + matched.getFormalCharge()
                        + " (" + matched.getAtomTypeName() + ")");
            } else {
                List<IBond> connections = mol.getConnectedBondsList(atom);
                int connectionCount = connections.size();
                //        		int piBondsFound = (int)mol.getBondOrderSum(atom) - connectionCount;
                // there might be missing hydrogens, so: found <= expected
                if (matched.getFormalNeighbourCount() != CDKConstants.UNSET
                        && connectionCount > matched.getFormalNeighbourCount()
                        && !"X".equals(matched.getAtomTypeName())) {
                    results.failed++;
                    System.out.println("Number of neighbors is too high: " + results.tested + " in file " + filename);
                    System.out.println("Found: " + connectionCount + ", expected (max): "
                            + matched.getFormalNeighbourCount() + " (" + matched.getAtomTypeName() + ")");
                }
                // there might be missing double bonds, so: found <= expected
                //        		if (piBondsFound > matched.getXXXX()) {
                //            		results.failed++;
                //            		System.out.println("Number of neighbors is too high: " + results.tested + " in file " + filename);
                //            		System.out.println("Found: " + atom.getFormalNeighbourCount() +
                //            				           ", expected (max): " + matched.getFormalNeighbourCount());
                //        		}
            }
        }
        return results;
    }

    class TestResults {

        int tested;
        int failed;

        TestResults() {
            tested = 0;
            failed = 0;
        }

    }

}
