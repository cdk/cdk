package org.openscience.cdk.test.qsar.descriptors.molecular;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.IDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.LargestPiSystemDescriptor;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.CDKTestCase;

/**
 * TestSuite that runs all QSAR tests.
 * @author      chhoppe from EUROSCREEN
 * @cdk.module test
 */

public class LargestPiSystemDescriptorTest extends CDKTestCase{
	public  LargestPiSystemDescriptorTest() {}
    
	public static Test suite() {
		return new TestSuite(LargestPiSystemDescriptorTest.class);
	}
    
	public void test1LargestPiSystemDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
		IDescriptor descriptor = new LargestPiSystemDescriptor();
		Object[] params = {new Boolean(false)};
		descriptor.setParameters(params);
		SmilesParser sp = new SmilesParser();
		IAtomContainer mol = sp.parseSmiles("c1ccccc1"); // benzol
		//assertEquals(6, ((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
		System.out.println("test1>:"+((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
	}
	public void test2LargestPiSystemDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
		IDescriptor descriptor = new LargestPiSystemDescriptor();
		Object[] params = {new Boolean(false)};
		descriptor.setParameters(params);
		SmilesParser sp = new SmilesParser();
		IAtomContainer mol = sp.parseSmiles("C=CC=Cc1ccccc1"); 
		assertEquals(10, ((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
		//System.out.println("test2>"+((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
	}
	public void test3LargestPiSystemDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
		IDescriptor descriptor = new LargestPiSystemDescriptor();
		Object[] params = {new Boolean(false)};
		descriptor.setParameters(params);
		SmilesParser sp = new SmilesParser();
		IAtomContainer mol = sp.parseSmiles("C=CC=CCc2ccc(Cc1ccncc1C=C)cc2"); 
		//System.out.println("test3>"+((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
		assertEquals(8, ((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
	}
	public void test4LargestPiSystemDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
		IDescriptor descriptor = new LargestPiSystemDescriptor();
		Object[] params = {new Boolean(false)};
		descriptor.setParameters(params);
		SmilesParser sp = new SmilesParser();
		IAtomContainer mol = sp.parseSmiles("CC=CNCC"); 
		//System.out.println("test4>"+((IntegerResult)descriptor.calculate(mol).getValue()).intValue());		
		assertEquals(3, ((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
	}
	public void test5LargestPiSystemDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
		IDescriptor descriptor = new LargestPiSystemDescriptor();
		Object[] params = {new Boolean(false)};
		descriptor.setParameters(params);
		SmilesParser sp = new SmilesParser();
		IAtomContainer mol = sp.parseSmiles("C=C[NH2+]CC"); 
		//System.out.println("test5>"+((IntegerResult)descriptor.calculate(mol).getValue()).intValue());		
		assertEquals(3, ((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
	}
	public void test6LargestPiSystemDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
		IDescriptor descriptor = new LargestPiSystemDescriptor();
		Object[] params = {new Boolean(false)};
		descriptor.setParameters(params);
		SmilesParser sp = new SmilesParser();
		IAtomContainer mol = sp.parseSmiles("CCNOC"); 
		//System.out.println("test6>"+((IntegerResult)descriptor.calculate(mol).getValue()).intValue());		
		assertEquals(2, ((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
	}
	public void test7LargestPiSystemDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
		IDescriptor descriptor = new LargestPiSystemDescriptor();
		Object[] params = {new Boolean(false)};
		descriptor.setParameters(params);
		SmilesParser sp = new SmilesParser();
		IAtomContainer mol = sp.parseSmiles("CC=CC(C)=O"); 
		//System.out.println("test7>"+((IntegerResult)descriptor.calculate(mol).getValue()).intValue());		
		assertEquals(4, ((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
	}
}
