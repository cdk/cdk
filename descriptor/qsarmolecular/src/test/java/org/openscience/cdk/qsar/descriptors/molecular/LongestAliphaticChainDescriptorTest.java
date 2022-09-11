package org.openscience.cdk.qsar.descriptors.molecular;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

import static org.hamcrest.CoreMatchers.is;

/**
 * TestSuite that runs all QSAR tests.
 * @author      chhoppe from EUROSCREEN
 * @cdk.module test-qsarmolecular
 */

class LongestAliphaticChainDescriptorTest extends MolecularDescriptorTest {

    LongestAliphaticChainDescriptorTest() {}

    @BeforeEach
    void setUp() throws Exception {
        setDescriptor(LongestAliphaticChainDescriptor.class);
    }

    @Test
    void test1LongestAliphaticChainDescriptor() throws java.lang.Exception {
        Object[] params = {Boolean.TRUE};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CCCCc1ccccc1"); // benzol
        //logger.debug("test1>:"+((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
        Assertions.assertEquals(4, ((IntegerResult) descriptor.calculate(mol).getValue()).intValue());
    }

    @Test
    void test2LargestChainDescriptor() throws java.lang.Exception {
        Object[] params = {Boolean.TRUE};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C=CC=Cc1ccccc1");
        //logger.debug("test2>"+((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
        Assertions.assertEquals(4, ((IntegerResult) descriptor.calculate(mol).getValue()).intValue());
    }

    @Test
    void test3LargestChainDescriptor() throws java.lang.Exception {
        Object[] params = {Boolean.TRUE};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C=C(CCC1CC1C(C)C(C)C)C(C)CC2CCCC2");
        //logger.debug("test3>"+((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
        Assertions.assertEquals(5, ((IntegerResult) descriptor.calculate(mol).getValue()).intValue());
    }

    @Test
    void test4LargestChainDescriptor() throws java.lang.Exception {
        Object[] params = {Boolean.TRUE};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CCCCNCC");
        //logger.debug("test4>"+((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
        Assertions.assertEquals(4, ((IntegerResult) descriptor.calculate(mol).getValue()).intValue());
    }

    @Test
    void test5LargestChainDescriptor() throws java.lang.Exception {
        Object[] params = {Boolean.TRUE};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CC(C)(C)c1ccccc1");
        //logger.debug("test5>"+((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
        Assertions.assertEquals(3, ((IntegerResult) descriptor.calculate(mol).getValue()).intValue());
    }

    @Test
    void test6LargestChainDescriptor() throws java.lang.Exception {
        Object[] params = {Boolean.TRUE};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CC(C)(C)c2ccc(OCCCC(=O)Nc1nccs1)cc2");
        //logger.debug("test6>"+((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
        Assertions.assertEquals(4, ((IntegerResult) descriptor.calculate(mol).getValue()).intValue());
    }

    @Test
    void test7LargestChainDescriptor() throws java.lang.Exception {
        Object[] params = {Boolean.TRUE};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CC(=O)N1CCN(CC1)c2ccc(NC(=O)COc3ccc(cc3)C(C)(C)C)cc2");
        //logger.debug("test7>"+((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
        Assertions.assertEquals(3, ((IntegerResult) descriptor.calculate(mol).getValue()).intValue());
    }

    @Test
    void ethanol() throws Exception {
        assertSmiles("CCO", 2);
        assertSmiles("OCC", 2);
    }

    private void assertSmiles(String smi, int expected) throws Exception {
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = smipar.parseSmiles(smi);
        descriptor.setParameters(new Object[]{true});
        org.hamcrest.MatcherAssert.assertThat(descriptor.calculate(mol).getValue().toString(), is(Integer.toString(expected)));
    }
}
