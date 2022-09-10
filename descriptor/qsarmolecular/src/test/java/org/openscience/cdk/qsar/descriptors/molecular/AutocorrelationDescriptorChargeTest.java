package org.openscience.cdk.qsar.descriptors.molecular;

import java.io.InputStream;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.DoubleArrayResult;

/**
 * @cdk.module test-qsarmolecular
 */
public class AutocorrelationDescriptorChargeTest extends MolecularDescriptorTest {

    public AutocorrelationDescriptorChargeTest() {
        super();
    }

    @BeforeEach
    public void setUp() throws Exception {
        setDescriptor(AutocorrelationDescriptorCharge.class);
    }

    @Test
    public void test1() throws Exception {
        String filename = "chlorobenzene.mol";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IAtomContainer container = reader.read(new AtomContainer());
        DescriptorValue count = descriptor.calculate(container);
        Assert.assertEquals(5, count.getValue().length());
        Assert.assertTrue(count.getValue() instanceof DoubleArrayResult);
        DoubleArrayResult result = (DoubleArrayResult) count.getValue();
        for (int i = 0; i < 5; i++) {
            Assert.assertFalse(Double.isNaN(result.get(i)));
            Assert.assertTrue(0.0 != result.get(i));
        }
    }

}
