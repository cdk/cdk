package org.openscience.cdk.qsar.descriptors.molecular;

import java.io.InputStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.qsar.DescriptorValue;

/**
 * @cdk.module test-qsarmolecular
 */

class AutocorrelationDescriptorPolarizabilityTest extends MolecularDescriptorTest {

    AutocorrelationDescriptorPolarizabilityTest() {
        super();
    }

    @BeforeEach
    void setUp() throws Exception {
        setDescriptor(AutocorrelationDescriptorPolarizability.class);
    }

    void ignoreCalculate_IAtomContainer() throws Exception {
        String filename = "data/mdl/chlorobenzene.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IAtomContainer container = reader.read(new AtomContainer());
        DescriptorValue count = descriptor.calculate(container);
        System.out.println(count.getValue());

        Assertions.fail("Not validated yet");
    }

}
