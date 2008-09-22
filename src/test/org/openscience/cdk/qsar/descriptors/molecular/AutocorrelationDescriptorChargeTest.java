package org.openscience.cdk.qsar.descriptors.molecular;

import java.io.InputStream;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.descriptors.molecular.AutocorrelationDescriptorCharge;

/**
 * @cdk.module test-qsarmolecular
 */
public class AutocorrelationDescriptorChargeTest extends MolecularDescriptorTest {

	public AutocorrelationDescriptorChargeTest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(AutocorrelationDescriptorChargeTest.class);
	}

	public void setUp() throws Exception {
		setDescriptor(AutocorrelationDescriptorCharge.class);
	}
	
	public void ignoreCalculate_IAtomContainer() throws Exception {
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
