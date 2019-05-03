package org.openscience.cdk.qsar.descriptors.molecular;

import java.io.InputStream;

import org.junit.Assert;
import org.junit.Before;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.qsar.DescriptorValue;

/**
 * @cdk.module test-qsarmolecular
 */

public class AutocorrelationDescriptorPolarizabilityTest extends MolecularDescriptorTest {

    public AutocorrelationDescriptorPolarizabilityTest() {
        super();
    }

    @Before
    public void setUp() throws Exception {
        setDescriptor(AutocorrelationDescriptorPolarizability.class);
    }

    public void ignoreCalculate_IAtomContainer() throws Exception {
        String filename = "data/mdl/clorobenzene.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IAtomContainer container = reader.read(new AtomContainer());
        DescriptorValue count = descriptor.calculate(container);
        System.out.println(count.getValue());

        Assert.fail("Not validated yet");
    }

}
