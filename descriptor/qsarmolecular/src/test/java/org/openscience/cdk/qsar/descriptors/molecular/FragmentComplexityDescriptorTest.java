package org.openscience.cdk.qsar.descriptors.molecular;

import java.io.InputStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
class FragmentComplexityDescriptorTest extends MolecularDescriptorTest {

    FragmentComplexityDescriptorTest() {}

    @BeforeEach
    void setup() throws Exception {
        setDescriptor(FragmentComplexityDescriptor.class);
    }

    @Test
    void test1FragmentComplexityDescriptor() throws Exception {
        IMolecularDescriptor descriptor = new FragmentComplexityDescriptor();
        String filename = "murckoTest1.mol";
        //System.out.println("\nFragmentComplexityTest: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MurckoFragmenter gf = new MurckoFragmenter();
        double Complexity = 0;
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        IAtomContainer mol = reader.read(new AtomContainer());
        gf.generateFragments(mol);
        IAtomContainer[] setOfFragments = gf.getFrameworksAsContainers();
        for (IAtomContainer setOfFragment : setOfFragments) {
            addExplicitHydrogens(setOfFragment);
            Complexity = ((DoubleResult) descriptor.calculate(setOfFragment).getValue()).doubleValue();
            //System.out.println("Complexity:"+Complexity);
        }
        Assertions.assertEquals(659.00, Complexity, 0.01);
    }

    @Test
    void test2FragmentComplexityDescriptor() throws Exception {
        IMolecularDescriptor descriptor = new FragmentComplexityDescriptor();
        String filename = "murckoTest10.mol";
        InputStream ins = this.getClass().getResourceAsStream(filename);
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
        Assertions.assertEquals(544.01, Complexity, 0.01);
    }

}
