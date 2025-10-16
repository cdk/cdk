package org.openscience.cdk.io;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.silent.AtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

import java.io.InputStream;
import java.util.List;
import java.util.Objects;

public class CDKAtomTypeMatcherTestFileReposPDB_Test {

    @Test
    void testPDBfiles() throws Exception {
        final String DIRNAME = "org/openscience/cdk/io/";
        String[] testFiles = {"114D.pdb", "1CRN.pdb", "1D66.pdb"
                              // "1IHA.pdb", "1PN8.pdb", // now missing
        };
        int tested = 0;
        int failed = 0;
        ISimpleChemObjectReader reader = new PDBReader();
        for (String testFile : testFiles) {
            CDKAtomTypeMatcherTestFileReposPDB_Test.TestResults results = testFile(DIRNAME, testFile, reader);
            tested += results.tested;
            failed += results.failed;
        }
        Assertions.assertEquals(tested, (tested - failed), "Could not match all atom types!");
    }

    private TestResults testFile(String dir, String filename, ISimpleChemObjectReader reader) throws Exception {
        CDKAtomTypeMatcher matcher = CDKAtomTypeMatcher.getInstance(DefaultChemObjectBuilder.getInstance());
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(dir + filename);
        if (ins == null)
            System.err.println(filename + " no found!");
        reader.setReader(ins);
        IAtomContainer mol = null;
        if (reader.accepts(AtomContainer.class)) {
            mol = reader.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
        } else if (reader.accepts(ChemFile.class)) {
            IChemFile cf = reader.read(new ChemFile());
            mol = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
            List<IAtomContainer> containers = ChemFileManipulator.getAllAtomContainers(cf);
            for (IAtomContainer container : containers)
                mol.add(container);
        }

        Assertions.assertNotNull(mol, "Could not read the file into a IAtomContainer: " + filename);

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
                if (!Objects.equals(atom.getSymbol(), matched.getSymbol())) {
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
