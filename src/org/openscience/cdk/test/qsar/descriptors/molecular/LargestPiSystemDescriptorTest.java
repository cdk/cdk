package org.openscience.cdk.test.qsar.descriptors.molecular;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.descriptors.molecular.LargestPiSystemDescriptor;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.smiles.SmilesParser;

/**
 * TestSuite that runs all QSAR tests.
 * @author      chhoppe from EUROSCREEN
 * @cdk.module test-qsar
 */

public class LargestPiSystemDescriptorTest extends MolecularDescriptorTest {
	public  LargestPiSystemDescriptorTest() {}
    
	public static Test suite() {
		return new TestSuite(LargestPiSystemDescriptorTest.class);
	}
    
    public void setUp() {
    	descriptor = new LargestPiSystemDescriptor();
    }
	
	public void test1LargestPiSystemDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
		Object[] params = {new Boolean(false)};
		descriptor.setParameters(params);
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IAtomContainer mol = sp.parseSmiles("c1ccccc1"); // benzol
		//assertEquals(6, ((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
		System.out.println("test1>:"+((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
	}
	public void test2LargestPiSystemDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
		Object[] params = {new Boolean(false)};
		descriptor.setParameters(params);
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IAtomContainer mol = sp.parseSmiles("C=CC=Cc1ccccc1"); 
		assertEquals(10, ((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
		//logger.debug("test2>"+((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
	}
	public void test3LargestPiSystemDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
		Object[] params = {new Boolean(false)};
		descriptor.setParameters(params);
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IAtomContainer mol = sp.parseSmiles("C=CC=CCc2ccc(Cc1ccncc1C=C)cc2"); 
		//logger.debug("test3>"+((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
		assertEquals(8, ((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
	}
	public void test4LargestPiSystemDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
		Object[] params = {new Boolean(false)};
		descriptor.setParameters(params);
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IAtomContainer mol = sp.parseSmiles("CC=CNCC"); 
		//logger.debug("test4>"+((IntegerResult)descriptor.calculate(mol).getValue()).intValue());		
		assertEquals(3, ((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
	}
	public void test5LargestPiSystemDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
		Object[] params = {new Boolean(false)};
		descriptor.setParameters(params);
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IAtomContainer mol = sp.parseSmiles("C=C[NH2+]CC"); 
		//logger.debug("test5>"+((IntegerResult)descriptor.calculate(mol).getValue()).intValue());		
		assertEquals(3, ((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
	}
	public void test6LargestPiSystemDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
		Object[] params = {new Boolean(false)};
		descriptor.setParameters(params);
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IAtomContainer mol = sp.parseSmiles("CCNOC"); 
		//logger.debug("test6>"+((IntegerResult)descriptor.calculate(mol).getValue()).intValue());		
		assertEquals(2, ((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
	}
	public void test7LargestPiSystemDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
		Object[] params = {new Boolean(false)};
		descriptor.setParameters(params);
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IAtomContainer mol = sp.parseSmiles("CC=CC(C)=O"); 
		//logger.debug("test7>"+((IntegerResult)descriptor.calculate(mol).getValue()).intValue());		
		assertEquals(4, ((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
	}
}
