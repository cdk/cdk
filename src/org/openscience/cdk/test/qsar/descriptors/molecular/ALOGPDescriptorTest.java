package org.openscience.cdk.test.qsar.descriptors.molecular;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.descriptors.molecular.ALOGP;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.HydrogenAdder;

/**
 * Test suite for the alogp descriptor
 *
 * @cdk.module test-qsar
 */
public class ALOGPDescriptorTest extends CDKTestCase {

    public static Test suite() {
        return new TestSuite(ALOGPDescriptorTest.class);
    }

    public void test1() {
        try {
            DescriptorValue v = getALOGP("CCCCl");
            assertEquals(0.5192, ((DoubleArrayResult) v.getValue()).get(0), 1E-10);
            assertEquals(19.1381, ((DoubleArrayResult) v.getValue()).get(2), 1E-10);
        } catch (Exception x) {
            fail(x.getMessage());
        }
    }

    private DescriptorValue getALOGP(String smiles) throws Exception {
        IMolecule mol = null;
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        mol = sp.parseSmiles(smiles);
        HydrogenAdder ha = new HydrogenAdder();
        ha.addExplicitHydrogensToSatisfyValency(mol);

        ALOGP descr = new ALOGP();
        return descr.calculate(mol);

    }


}
