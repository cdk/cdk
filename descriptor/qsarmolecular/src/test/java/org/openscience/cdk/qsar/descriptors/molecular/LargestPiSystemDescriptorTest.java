package org.openscience.cdk.qsar.descriptors.molecular;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.smiles.SmilesParser;

/**
 * TestSuite that runs all QSAR tests.
 * @author      chhoppe from EUROSCREEN
 * @cdk.module test-qsarmolecular
 */

class LargestPiSystemDescriptorTest extends MolecularDescriptorTest {

    LargestPiSystemDescriptorTest() {}

    @BeforeEach
    void setUp() throws Exception {
        setDescriptor(LargestPiSystemDescriptor.class);
    }

    @Test
    void test1LargestPiSystemDescriptor() throws java.lang.Exception {
        Object[] params = {Boolean.FALSE};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("c1ccccc1"); // benzol
        Assertions.assertEquals(6, ((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
    }

    @Test
    void test2LargestPiSystemDescriptor() throws java.lang.Exception {
        Object[] params = {Boolean.FALSE};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C=CC=Cc1ccccc1");
        Assertions.assertEquals(10, ((IntegerResult) descriptor.calculate(mol).getValue()).intValue());
        //logger.debug("test2>"+((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
    }

    @Test
    void test3LargestPiSystemDescriptor() throws java.lang.Exception {
        Object[] params = {Boolean.FALSE};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C=CC=CCc2ccc(Cc1ccncc1C=C)cc2");
        //logger.debug("test3>"+((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
        Assertions.assertEquals(8, ((IntegerResult) descriptor.calculate(mol).getValue()).intValue());
    }

    @Test
    void test4LargestPiSystemDescriptor() throws java.lang.Exception {
        Object[] params = {Boolean.FALSE};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CC=CNCC");
        //logger.debug("test4>"+((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
        Assertions.assertEquals(3, ((IntegerResult) descriptor.calculate(mol).getValue()).intValue());
    }

    @Test
    void test5LargestPiSystemDescriptor() throws java.lang.Exception {
        Object[] params = {Boolean.FALSE};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C=C[NH2+]CC");
        //logger.debug("test5>"+((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
        Assertions.assertEquals(3, ((IntegerResult) descriptor.calculate(mol).getValue()).intValue());
    }

    @Test
    void test6LargestPiSystemDescriptor() throws java.lang.Exception {
        Object[] params = {Boolean.FALSE};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CCNOC");
        //logger.debug("test6>"+((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
        Assertions.assertEquals(2, ((IntegerResult) descriptor.calculate(mol).getValue()).intValue());
    }

    @Test
    void test7LargestPiSystemDescriptor() throws java.lang.Exception {
        Object[] params = {Boolean.FALSE};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CC=CC(C)=O");
        //logger.debug("test7>"+((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
        Assertions.assertEquals(4, ((IntegerResult) descriptor.calculate(mol).getValue()).intValue());
    }
}
