package org.openscience.cdk.qsar.descriptors.molecular;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.smiles.SmilesParser;

/**
 * TestSuite for the LargestChainDescriptor.
 *
 * @author      chhoppe from EUROSCREEN
 * @cdk.module test-qsarmolecular
 */

class LargestChainDescriptorTest extends MolecularDescriptorTest {

    LargestChainDescriptorTest() {}

    @BeforeEach
    void setUp() throws Exception {
        setDescriptor(LargestChainDescriptor.class);
    }

    @Test
    void test1LargestChainDescriptor() throws java.lang.Exception {
        Object[] params = {Boolean.TRUE, Boolean.TRUE};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("c1ccccc1"); // benzol
        //logger.debug("test1>:"+((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
        Assertions.assertEquals(0, ((IntegerResult) descriptor.calculate(mol).getValue()).intValue());
    }

    @Test
    void test2LargestChainDescriptor() throws java.lang.Exception {
        Object[] params = {Boolean.TRUE, Boolean.TRUE};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C=CC=Cc1ccccc1");
        //logger.debug("test2>"+((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
        Assertions.assertEquals(4, ((IntegerResult) descriptor.calculate(mol).getValue()).intValue());
    }

    @Test
    void test3LargestChainDescriptor() throws java.lang.Exception {
        Object[] params = {Boolean.TRUE, Boolean.TRUE};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C=CC=CCc2ccc(Cc1ccncc1C=C)cc2");
        //logger.debug("test3>"+((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
        Assertions.assertEquals(5, ((IntegerResult) descriptor.calculate(mol).getValue()).intValue());
    }

    @Test
    void test4LargestChainDescriptor() throws java.lang.Exception {
        Object[] params = {Boolean.TRUE, Boolean.TRUE};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CC=CNCC");
        //logger.debug("test4>"+((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
        Assertions.assertEquals(6, ((IntegerResult) descriptor.calculate(mol).getValue()).intValue());
    }

    @Test
    void test5LargestChainDescriptor() throws java.lang.Exception {
        Object[] params = {Boolean.TRUE, Boolean.TRUE};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C=C[NH2+]CC");
        //logger.debug("test5>"+((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
        Assertions.assertEquals(5, ((IntegerResult) descriptor.calculate(mol).getValue()).intValue());
    }

    @Test
    void test6LargestChainDescriptor() throws java.lang.Exception {
        Object[] params = {Boolean.TRUE, Boolean.TRUE};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CCNOC");
        //logger.debug("test6>"+((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
        Assertions.assertEquals(5, ((IntegerResult) descriptor.calculate(mol).getValue()).intValue());
    }

    @Test
    void test7LargestChainDescriptor() throws java.lang.Exception {
        Object[] params = {Boolean.TRUE, Boolean.TRUE};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CC=CC(C)=O");
        //logger.debug("test7>"+((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
        Assertions.assertEquals(5, ((IntegerResult) descriptor.calculate(mol).getValue()).intValue());
    }

    @Test
    void testSingleCAtom() throws java.lang.Exception {
        Object[] params = {Boolean.TRUE, Boolean.TRUE};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C");
        //logger.debug("test7>"+((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
        Assertions.assertEquals(0, ((IntegerResult) descriptor.calculate(mol).getValue()).intValue());
    }

    @Test
    void testSingleOAtom() throws java.lang.Exception {
        Object[] params = {Boolean.TRUE, Boolean.TRUE};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("O");
        //logger.debug("test7>"+((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
        Assertions.assertEquals(0, ((IntegerResult) descriptor.calculate(mol).getValue()).intValue());
    }

    @Test
    void test8LargestChainDescriptor() throws java.lang.Exception {
        Object[] params = {Boolean.TRUE, Boolean.TRUE};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("Cc1nn(c(c1)N)c1nc2c(s1)cccc2");
        Assertions.assertEquals(0, ((IntegerResult) descriptor.calculate(mol).getValue()).intValue());
    }

    @Test
    void test9LargestChainDescriptor() throws java.lang.Exception {
        Object[] params = {Boolean.TRUE, Boolean.TRUE};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("Nc1c(cn[nH]1)C#N");
        Assertions.assertEquals(2, ((IntegerResult) descriptor.calculate(mol).getValue()).intValue());
    }

    @Test
    void test10LargestChainDescriptor() throws java.lang.Exception {
        Object[] params = {Boolean.TRUE, Boolean.TRUE};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("OCc1ccccc1CN");
        Assertions.assertEquals(2, ((IntegerResult) descriptor.calculate(mol).getValue()).intValue());
    }

    @Test
    void test11LargestChainDescriptor() throws java.lang.Exception {
        Object[] params = {Boolean.TRUE, Boolean.TRUE};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("COc1ccc(cc1)c1noc(c1)Cn1nc(C)c(c(c1=O)C#N)C");
        Assertions.assertEquals(2, ((IntegerResult) descriptor.calculate(mol).getValue()).intValue());
    }

}
