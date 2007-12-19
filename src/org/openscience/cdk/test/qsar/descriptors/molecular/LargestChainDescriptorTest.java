package org.openscience.cdk.test.qsar.descriptors.molecular;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.descriptors.molecular.LargestChainDescriptor;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.smiles.SmilesParser;

/**
 * TestSuite for the LargestChainDescriptor.
 * 
 * @author      chhoppe from EUROSCREEN
 * @cdk.module test-qsar
 */

public class LargestChainDescriptorTest extends MolecularDescriptorTest {
	
	public  LargestChainDescriptorTest() {}
    
	public static Test suite() {
		return new TestSuite(LargestChainDescriptorTest.class);
	}
    
    public void setUp() {
    	descriptor = new LargestChainDescriptor();
    }
	
	public void test1LargestChainDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
		Object[] params = {new Boolean(true),new Boolean(true)};
		descriptor.setParameters(params);
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IAtomContainer mol = sp.parseSmiles("c1ccccc1"); // benzol
		//logger.debug("test1>:"+((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
		assertEquals(0, ((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
	}
	public void test2LargestChainDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
		Object[] params = {new Boolean(true),new Boolean(true)};
		descriptor.setParameters(params);
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IAtomContainer mol = sp.parseSmiles("C=CC=Cc1ccccc1"); 
		//logger.debug("test2>"+((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
		assertEquals(4, ((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
	}
	public void test3LargestChainDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
		Object[] params = {new Boolean(true),new Boolean(true)};
		descriptor.setParameters(params);
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IAtomContainer mol = sp.parseSmiles("C=CC=CCc2ccc(Cc1ccncc1C=C)cc2"); 
		//logger.debug("test3>"+((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
		assertEquals(5, ((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
	}
	public void test4LargestChainDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
		Object[] params = {new Boolean(true),new Boolean(true)};
		descriptor.setParameters(params);
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IAtomContainer mol = sp.parseSmiles("CC=CNCC"); 
		//logger.debug("test4>"+((IntegerResult)descriptor.calculate(mol).getValue()).intValue());		
		assertEquals(6, ((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
	}
	public void test5LargestChainDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
		Object[] params = {new Boolean(true),new Boolean(true)};
		descriptor.setParameters(params);
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IAtomContainer mol = sp.parseSmiles("C=C[NH2+]CC"); 
		//logger.debug("test5>"+((IntegerResult)descriptor.calculate(mol).getValue()).intValue());		
		assertEquals(5, ((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
	}
	public void test6LargestChainDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
		Object[] params = {new Boolean(true),new Boolean(true)};
		descriptor.setParameters(params);
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IAtomContainer mol = sp.parseSmiles("CCNOC"); 
		//logger.debug("test6>"+((IntegerResult)descriptor.calculate(mol).getValue()).intValue());		
		assertEquals(5, ((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
	}
	public void test7LargestChainDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
		Object[] params = {new Boolean(true),new Boolean(true)};
		descriptor.setParameters(params);
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IAtomContainer mol = sp.parseSmiles("CC=CC(C)=O"); 
		//logger.debug("test7>"+((IntegerResult)descriptor.calculate(mol).getValue()).intValue());		
		assertEquals(6, ((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
	}

}
