package org.openscience.cdk.test.qsar.descriptors.molecular;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.IChemObjectReader;
import org.openscience.cdk.io.ReaderFactory;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.TaeAminoAcidDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

import java.io.InputStream;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsar
 */

public class TaeAminoAcidDescriptorTest extends CDKTestCase {

    public TaeAminoAcidDescriptorTest() {
    }

    public static Test suite() {
        return new TestSuite(TaeAminoAcidDescriptorTest.class);
    }

    public void testTaeAminoAcidDescriptor() throws ClassNotFoundException, CDKException, Exception {
        IMolecularDescriptor descriptor = new TaeAminoAcidDescriptor();

        // first molecule is nbutane, second is naphthalene
        String filename = "data/pdb/114D.pdb";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        IChemObjectReader reader = new ReaderFactory().createReader(ins);
        ChemFile content = (ChemFile) reader.read((ChemObject) new ChemFile());
        IAtomContainer[] c = ChemFileManipulator.getAllAtomContainers(content);
        IAtomContainer ac = c[0];

        DescriptorValue result = descriptor.calculate(ac);
        DoubleArrayResult dar = (DoubleArrayResult) result.getValue();

    }
}
