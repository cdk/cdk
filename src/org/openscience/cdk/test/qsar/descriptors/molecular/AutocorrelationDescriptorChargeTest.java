package org.openscience.cdk.test.qsar.descriptors.molecular;

import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.exception.CDKException;

import junit.framework.Test;
import junit.framework.TestSuite;

import java.io.InputStream;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.test.CDKTestCase;

import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.qsar.descriptors.molecular.AutocorrelationDescriptorCharge;

/**
 * @cdk.module test-qsar
 */

public class AutocorrelationDescriptorChargeTest extends CDKTestCase {

	public AutocorrelationDescriptorChargeTest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(AutocorrelationDescriptorChargeTest.class);
	}
	
	public void testcalculate_IAtomContainer() throws CDKException {
		try{
			String filename = "data/mdl/clorobenzene.mol";
			InputStream ins = this.getClass().getClassLoader().getResourceAsStream(
					filename);
			MDLReader reader = new MDLReader(ins);
			IMolecule container = (Molecule) reader.read((ChemObject) new Molecule());
			DescriptorValue count = new AutocorrelationDescriptorCharge().calculate(container);
			System.out.println(count.getValue());
			
		}catch(Exception ex){
			fail(ex.getMessage());
		}
	}

}
