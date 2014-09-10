package org.openscience.cdk.qsar.descriptors.molecular;

import java.io.InputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.fragment.MurckoFragmenter;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;

/**
 * @author      chhoppe from EUROSCREEN
 * @cdk.module  test-qsarmolecular
 */
public class FragmentComplexityDescriptorTest extends MolecularDescriptorTest {

    public FragmentComplexityDescriptorTest() {}

    @Before
    public void setup() throws Exception {
        setDescriptor(FragmentComplexityDescriptor.class);
    }

    @Test
    public void test1FragmentComplexityDescriptor() throws Exception {
        IMolecularDescriptor descriptor = new FragmentComplexityDescriptor();
        String filename = "data/mdl/murckoTest1.mol";
        //System.out.println("\nFragmentComplexityTest: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MurckoFragmenter gf = new MurckoFragmenter();
        double Complexity = 0;
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        IAtomContainer mol = reader.read(new AtomContainer());
        gf.generateFragments(mol);
        IAtomContainer[] setOfFragments = gf.getFrameworksAsContainers();
        for (int i = 0; i < setOfFragments.length; i++) {
            addExplicitHydrogens(setOfFragments[i]);
            Complexity = ((DoubleResult) descriptor.calculate(setOfFragments[i]).getValue()).doubleValue();
            //System.out.println("Complexity:"+Complexity);
        }
        Assert.assertEquals(659.00, Complexity, 0.01);
    }

    @Test
    public void test2FragmentComplexityDescriptor() throws Exception {
        IMolecularDescriptor descriptor = new FragmentComplexityDescriptor();
        String filename = "data/mdl/murckoTest10.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MurckoFragmenter gf = new MurckoFragmenter();
        double Complexity = 0;
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        IAtomContainer mol = reader.read(DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class));
        gf.generateFragments(mol);
        IAtomContainer[] setOfFragments = gf.getFrameworksAsContainers();
        for (IAtomContainer setOfFragment : setOfFragments) {
            addExplicitHydrogens(setOfFragment);
            Complexity = ((DoubleResult) descriptor.calculate(setOfFragment).getValue()).doubleValue();
        }
        Assert.assertEquals(544.01, Complexity, 0.01);
    }

}
