package org.openscience.cdk.test.qsar.descriptors.molecular;

import java.io.InputStream;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.test.CDKTestCase;

import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.qsar.descriptors.molecular.AutocorrelationDescriptorMass;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.qsar.DescriptorValue;


/**
 * @cdk.module test-qsar
 */

public class AutocorrelationDescriptorMassTest extends CDKTestCase{

	public AutocorrelationDescriptorMassTest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(AutocorrelationDescriptorMassTest.class);
	}

//	public void testscaledAtomicMasses_IElement(){
//		try{
//		String filename = "data/mdl/clorobenzene.mol";
//		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(
//				filename);
//		MDLReader reader = new MDLReader(ins);
//		IMolecule container = (Molecule) reader.read((ChemObject) new Molecule());
//		double cont = AutocorrelationDescriptorMass.scaledAtomicMasses(container.getAtom(0));
//		double cont2 = AutocorrelationDescriptorMass.scaledAtomicMasses(container.getAtom(6));
//		assertEquals(1,.0001, cont);
//		assertEquals(2.952, .001, cont2);
//		System.out.println(cont);
//		}catch(Exception ex){
//			fail(ex.getMessage());
//		}
//	
//	}
//	
//	public void testlistconvertion_IAtomContainer(){
//		try{
//		String filename = "data/mdl/clorobenzene.mol";
//		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(
//				filename);
//		MDLReader reader = new MDLReader(ins);
//		IMolecule container = (Molecule) reader.read((ChemObject) new Molecule());
//		List list = AutocorrelationDescriptorMass.listconvertion(container);
//		System.out.println("The element in position 6 of the list is: " + list.get(6));
//		System.out.println(list);
//		}catch(Exception ex){
//			fail(ex.getMessage());
//		}
//	}	
		
	public void testcalculate_IAtomContainer() throws CDKException {
		try{
			String filename = "data/mdl/clorobenzene.mol";
			InputStream ins = this.getClass().getClassLoader().getResourceAsStream(
					filename);
			MDLReader reader = new MDLReader(ins);
			IMolecule container = (Molecule) reader.read((ChemObject) new Molecule());
			DescriptorValue count = new AutocorrelationDescriptorMass().calculate(container);
			System.out.println(count.getValue());
			
		}catch(Exception ex){
			fail(ex.getMessage());
		}
	}
}