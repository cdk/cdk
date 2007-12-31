package org.openscience.cdk.test.qsar.descriptors.molecular;

import java.io.InputStream;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.descriptors.molecular.AutocorrelationDescriptorPolarizability;

/**
 * @cdk.module test-qsarmolecular
 */

public class AutocorrelationDescriptorPolarizabilityTest extends MolecularDescriptorTest {

	public AutocorrelationDescriptorPolarizabilityTest(String name) {
		super(name);
	}
	
	public void setUp() throws Exception {
		setDescriptor(AutocorrelationDescriptorPolarizability.class);
	}

	public static Test suite() {
		return new TestSuite(AutocorrelationDescriptorPolarizabilityTest.class);
	}
	
	public void testcalculate_IAtomContainer() throws Exception {
		String filename = "data/mdl/clorobenzene.mol";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(
				filename);
		MDLV2000Reader reader = new MDLV2000Reader(ins);
		IMolecule container = (Molecule) reader.read((ChemObject) new Molecule());
		DescriptorValue count = descriptor.calculate(container);
		System.out.println(count.getValue());

		fail("Not validated yet");
	}

}
