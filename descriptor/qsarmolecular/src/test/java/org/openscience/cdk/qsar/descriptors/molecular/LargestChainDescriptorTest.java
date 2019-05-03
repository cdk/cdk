package org.openscience.cdk.qsar.descriptors.molecular;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.smiles.SmilesParser;

/**
 * TestSuite for the LargestChainDescriptor.
 *
 * @author      chhoppe from EUROSCREEN
 * @cdk.module test-qsarmolecular
 */

public class LargestChainDescriptorTest extends MolecularDescriptorTest {

    public LargestChainDescriptorTest() {}

    @Before
    public void setUp() throws Exception {
        setDescriptor(LargestChainDescriptor.class);
    }

    @Test
    public void test1LargestChainDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
        Object[] params = {new Boolean(true), new Boolean(true)};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("c1ccccc1"); // benzol
        //logger.debug("test1>:"+((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
        Assert.assertEquals(0, ((IntegerResult) descriptor.calculate(mol).getValue()).intValue());
    }

    @Test
    public void test2LargestChainDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
        Object[] params = {new Boolean(true), new Boolean(true)};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C=CC=Cc1ccccc1");
        //logger.debug("test2>"+((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
        Assert.assertEquals(4, ((IntegerResult) descriptor.calculate(mol).getValue()).intValue());
    }

    @Test
    public void test3LargestChainDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
        Object[] params = {new Boolean(true), new Boolean(true)};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C=CC=CCc2ccc(Cc1ccncc1C=C)cc2");
        //logger.debug("test3>"+((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
        Assert.assertEquals(5, ((IntegerResult) descriptor.calculate(mol).getValue()).intValue());
    }

    @Test
    public void test4LargestChainDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
        Object[] params = {new Boolean(true), new Boolean(true)};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CC=CNCC");
        //logger.debug("test4>"+((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
        Assert.assertEquals(6, ((IntegerResult) descriptor.calculate(mol).getValue()).intValue());
    }

    @Test
    public void test5LargestChainDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
        Object[] params = {new Boolean(true), new Boolean(true)};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C=C[NH2+]CC");
        //logger.debug("test5>"+((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
        Assert.assertEquals(5, ((IntegerResult) descriptor.calculate(mol).getValue()).intValue());
    }

    @Test
    public void test6LargestChainDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
        Object[] params = {new Boolean(true), new Boolean(true)};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CCNOC");
        //logger.debug("test6>"+((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
        Assert.assertEquals(5, ((IntegerResult) descriptor.calculate(mol).getValue()).intValue());
    }

    @Test
    public void test7LargestChainDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
        Object[] params = {new Boolean(true), new Boolean(true)};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CC=CC(C)=O");
        //logger.debug("test7>"+((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
        Assert.assertEquals(5, ((IntegerResult) descriptor.calculate(mol).getValue()).intValue());
    }

    @Test
    public void testSingleCAtom() throws ClassNotFoundException, CDKException, java.lang.Exception {
        Object[] params = {new Boolean(true), new Boolean(true)};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C");
        //logger.debug("test7>"+((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
        Assert.assertEquals(0, ((IntegerResult) descriptor.calculate(mol).getValue()).intValue());
    }

    @Test
    public void testSingleOAtom() throws ClassNotFoundException, CDKException, java.lang.Exception {
        Object[] params = {new Boolean(true), new Boolean(true)};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("O");
        //logger.debug("test7>"+((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
        Assert.assertEquals(0, ((IntegerResult) descriptor.calculate(mol).getValue()).intValue());
    }

    @Test
    public void test8LargestChainDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
        Object[] params = {new Boolean(true), new Boolean(true)};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("Cc1nn(c(c1)N)c1nc2c(s1)cccc2");
        Assert.assertEquals(0, ((IntegerResult) descriptor.calculate(mol).getValue()).intValue());
    }

    @Test
    public void test9LargestChainDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
        Object[] params = {new Boolean(true), new Boolean(true)};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("Nc1c(cn[nH]1)C#N");
        Assert.assertEquals(2, ((IntegerResult) descriptor.calculate(mol).getValue()).intValue());
    }

    @Test
    public void test10LargestChainDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
        Object[] params = {new Boolean(true), new Boolean(true)};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("OCc1ccccc1CN");
        Assert.assertEquals(2, ((IntegerResult) descriptor.calculate(mol).getValue()).intValue());
    }

    @Test
    public void test11LargestChainDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
        Object[] params = {new Boolean(true), new Boolean(true)};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("COc1ccc(cc1)c1noc(c1)Cn1nc(C)c(c(c1=O)C#N)C");
        Assert.assertEquals(2, ((IntegerResult) descriptor.calculate(mol).getValue()).intValue());
    }

}
