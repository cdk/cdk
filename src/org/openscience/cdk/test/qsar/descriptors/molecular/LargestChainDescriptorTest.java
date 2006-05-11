package org.openscience.cdk.test.qsar.descriptors.molecular;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.LargestChainDescriptor;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.CDKTestCase;

/**
 * TestSuite that runs all QSAR tests.
 * @author      chhoppe from EUROSCREEN
 * @cdk.module test-qsar
 */

public class LargestChainDescriptorTest extends CDKTestCase{
	public  LargestChainDescriptorTest() {}
    
	public static Test suite() {
		return new TestSuite(LargestChainDescriptorTest.class);
	}
    
	public void test1LargestChainDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
		IMolecularDescriptor descriptor = new LargestChainDescriptor();
		Object[] params = {new Boolean(true),new Boolean(true)};
		descriptor.setParameters(params);
		SmilesParser sp = new SmilesParser();
		IAtomContainer mol = sp.parseSmiles("c1ccccc1"); // benzol
		//System.out.println("test1>:"+((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
		assertEquals(0, ((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
	}
	public void test2LargestChainDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
		IMolecularDescriptor descriptor = new LargestChainDescriptor();
		Object[] params = {new Boolean(true),new Boolean(true)};
		descriptor.setParameters(params);
		SmilesParser sp = new SmilesParser();
		IAtomContainer mol = sp.parseSmiles("C=CC=Cc1ccccc1"); 
		//System.out.println("test2>"+((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
		assertEquals(4, ((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
	}
	public void test3LargestChainDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
		IMolecularDescriptor descriptor = new LargestChainDescriptor();
		Object[] params = {new Boolean(true),new Boolean(true)};
		descriptor.setParameters(params);
		SmilesParser sp = new SmilesParser();
		IAtomContainer mol = sp.parseSmiles("C=CC=CCc2ccc(Cc1ccncc1C=C)cc2"); 
		//System.out.println("test3>"+((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
		assertEquals(5, ((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
	}
	public void test4LargestChainDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
		IMolecularDescriptor descriptor = new LargestChainDescriptor();
		Object[] params = {new Boolean(true),new Boolean(true)};
		descriptor.setParameters(params);
		SmilesParser sp = new SmilesParser();
		IAtomContainer mol = sp.parseSmiles("CC=CNCC"); 
		//System.out.println("test4>"+((IntegerResult)descriptor.calculate(mol).getValue()).intValue());		
		assertEquals(6, ((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
	}
	public void test5LargestChainDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
		IMolecularDescriptor descriptor = new LargestChainDescriptor();
		Object[] params = {new Boolean(true),new Boolean(true)};
		descriptor.setParameters(params);
		SmilesParser sp = new SmilesParser();
		IAtomContainer mol = sp.parseSmiles("C=C[NH2+]CC"); 
		//System.out.println("test5>"+((IntegerResult)descriptor.calculate(mol).getValue()).intValue());		
		assertEquals(5, ((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
	}
	public void test6LargestChainDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
		IMolecularDescriptor descriptor = new LargestChainDescriptor();
		Object[] params = {new Boolean(true),new Boolean(true)};
		descriptor.setParameters(params);
		SmilesParser sp = new SmilesParser();
		IAtomContainer mol = sp.parseSmiles("CCNOC"); 
		//System.out.println("test6>"+((IntegerResult)descriptor.calculate(mol).getValue()).intValue());		
		assertEquals(5, ((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
	}
	public void test7LargestChainDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
		IMolecularDescriptor descriptor = new LargestChainDescriptor();
		Object[] params = {new Boolean(true),new Boolean(true)};
		descriptor.setParameters(params);
		SmilesParser sp = new SmilesParser();
		IAtomContainer mol = sp.parseSmiles("CC=CC(C)=O"); 
		//System.out.println("test7>"+((IntegerResult)descriptor.calculate(mol).getValue()).intValue());		
		//assertEquals(6, ((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
	}

}
