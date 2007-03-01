package org.openscience.cdk.test.qsar.descriptors.molecular;

import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.descriptors.molecular.AutocorrelationDescriptorPolarizability;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.exception.CDKException;
import java.io.InputStream;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @cdk.module test-qsar
 */

public class AutocorrelationDescriptorPolarizabilityTest extends CDKTestCase {

	public AutocorrelationDescriptorPolarizabilityTest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(AutocorrelationDescriptorPolarizabilityTest.class);
	}
	
	public void testcalculate_IAtomContainer() throws CDKException {
		try{
			String filename = "data/mdl/clorobenzene.mol";
			InputStream ins = this.getClass().getClassLoader().getResourceAsStream(
					filename);
			MDLReader reader = new MDLReader(ins);
			IMolecule container = (Molecule) reader.read((ChemObject) new Molecule());
			DescriptorValue count = new AutocorrelationDescriptorPolarizability().calculate(container);
			System.out.println(count.getValue());
			
		}catch(Exception ex){
			fail(ex.getMessage());
		}
	}

}
