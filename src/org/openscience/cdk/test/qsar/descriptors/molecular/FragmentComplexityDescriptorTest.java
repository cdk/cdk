package org.openscience.cdk.test.qsar.descriptors.molecular;

import java.io.InputStream;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.FragmentComplexityDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.GenerateFragments;
import org.openscience.cdk.tools.HydrogenAdder;

/**
 * TestSuite that runs all QSAR tests.
 * @author      chhoppe from EUROSCREEN
 * @cdk.module test-qsar
 */
public class FragmentComplexityDescriptorTest extends CDKTestCase{
	
	public  FragmentComplexityDescriptorTest() {}
	
	public static Test suite() {
		return new TestSuite(FragmentComplexityDescriptorTest.class);
	}
	   
	public void test1FragmentComplexityDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
		IMolecularDescriptor descriptor = new FragmentComplexityDescriptor();
		String filename = "data/mdl/murckoTest1.mol";
    	//System.out.println("\nFragmentComplexityTest: " + filename);
    	InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
    	GenerateFragments gf=new GenerateFragments();
    	double Complexity=0;
    	HydrogenAdder hAdder = new HydrogenAdder();
        try {
        	MDLReader reader = new MDLReader(ins);
        	Molecule mol = (Molecule)reader.read(new Molecule());
        	gf.generateMurckoFragments(mol,false,false,4);
        	List setOfFragments=gf.getMurckoFrameworks();
        	for (int i=0;i<setOfFragments.size();i++){
        		hAdder.addExplicitHydrogensToSatisfyValency((IMolecule) setOfFragments.get(i));
        		Complexity=((DoubleResult)descriptor.calculate((IAtomContainer) setOfFragments.get(i)).getValue()).doubleValue();
        		//System.out.println("Complexity:"+Complexity);
        	}
    	 }catch (Exception e){
	        	System.out.println("Error in test1FragmentComplexityDescriptor:");
	        	e.printStackTrace();
	        }
    	 assertEquals(659.00,Complexity , 0.01);
	}
	
	public void test2FragmentComplexityDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
		IMolecularDescriptor descriptor = new FragmentComplexityDescriptor();
		String filename = "data/mdl/murckoTest10.mol";
    	//System.out.println("\nFragmentComplexityTest: " + filename);
    	InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
    	GenerateFragments gf=new GenerateFragments();
    	double Complexity=0;
    	HydrogenAdder hAdder = new HydrogenAdder();
        try {
        	MDLReader reader = new MDLReader(ins);
        	Molecule mol = (Molecule)reader.read(new Molecule());
        	gf.generateMurckoFragments(mol,false,false,4);
        	List setOfFragments=gf.getMurckoFrameworks();
        	for (int i=0;i<setOfFragments.size();i++){
        		hAdder.addExplicitHydrogensToSatisfyValency((IMolecule) setOfFragments.get(i));
        		Complexity=((DoubleResult)descriptor.calculate((IAtomContainer) setOfFragments.get(i)).getValue()).doubleValue();
        		//System.out.println("Complexity:"+Complexity);
        	}
    	 }catch (Exception e){
	        	System.out.println("Error in test1FragmentComplexityDescriptor:");
	        	e.printStackTrace();
	        }
    	 assertEquals(544.01,Complexity , 0.01);
	}
	
	
}

 