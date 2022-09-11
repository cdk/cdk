/* Copyright (C) 2018  Jeffrey Plante (Lhasa Limited)  <Jeffrey.Plante@lhasalimited.org>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.qsar.descriptors.molecular;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

class JPlogPDescriptorTest {

	private static SmilesParser parser = null;

	@Test
    void testPyridine() throws CDKException {
		parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
		IAtomContainer struct = parseSmiles("c1ncccc1");
		JPlogPDescriptor desc = new JPlogPDescriptor();
		DescriptorValue answer = desc.calculate(struct);
		DoubleResult result = (DoubleResult) answer.getValue();
		double output = result.doubleValue();
		Assertions.assertEquals(0.9, output, 0.1);
	}

	@Test
    void testPropionicAcid() throws CDKException {
		parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
		IAtomContainer struct = parseSmiles("CCC(=O)O");
		JPlogPDescriptor desc = new JPlogPDescriptor();
		DescriptorValue answer = desc.calculate(struct);
		DoubleResult result = (DoubleResult) answer.getValue();
		double output = result.doubleValue();
		Assertions.assertEquals(0.3, output, 0.1);
	}

	@Test
    void testAcetonitrile() throws CDKException {
		parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
		IAtomContainer struct = parseSmiles("CC#N");
		JPlogPDescriptor desc = new JPlogPDescriptor();
		DescriptorValue answer = desc.calculate(struct);
		DoubleResult result = (DoubleResult) answer.getValue();
		double output = result.doubleValue();
		Assertions.assertEquals(0.4, output, 0.1);
	}

	@Test
    void testAniline() throws CDKException {
		parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
		IAtomContainer struct = parseSmiles("Nc1ccccc1");
		JPlogPDescriptor desc = new JPlogPDescriptor();
		DescriptorValue answer = desc.calculate(struct);
		DoubleResult result = (DoubleResult) answer.getValue();
		double output = result.doubleValue();
		Assertions.assertEquals(1.2, output, 0.1);
	}

	@Test
    void testFluorobenzene() throws CDKException {
		parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
		IAtomContainer struct = parseSmiles("Fc1ccccc1");
		JPlogPDescriptor desc = new JPlogPDescriptor();
		DescriptorValue answer = desc.calculate(struct);
		DoubleResult result = (DoubleResult) answer.getValue();
		double output = result.doubleValue();
		Assertions.assertEquals(2.0, output, 0.1);
	}

	@Test
    void testSimpleTextFields() {
		JPlogPDescriptor desc = new JPlogPDescriptor();
		DescriptorSpecification specification = desc.getSpecification();
		String name = desc.getDescriptorNames()[0];
		Assertions.assertEquals("JPlogP developed at Lhasa Limited www.lhasalimited.org", specification.getSpecificationReference());
		Assertions.assertEquals("Jeffrey Plante - Lhasa Limited", specification.getImplementationVendor());
		Assertions.assertEquals("JPLogP", name);
		Assertions.assertEquals(1, desc.getParameterNames().length);
		Assertions.assertEquals("addImplicitH", desc.getParameterNames()[0]);
	}

	@Test
    void testGetHologram() throws CDKException {
		JPlogPDescriptor desc = new JPlogPDescriptor();
		parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
		IAtomContainer molecule = parseSmiles("c1ccccc1");
		Map<Integer, Integer> holo = desc.jplogp.getMappedHologram(molecule);
		Assertions.assertEquals(2, holo.keySet().size());
		Assertions.assertEquals(6, holo.get(106204).intValue());
	}



	private static IAtomContainer parseSmiles(String smiles) throws CDKException {
		parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
		IAtomContainer molecule = parser.parseSmiles(smiles);
		AtomContainerManipulator.percieveAtomTypesAndConfigureUnsetProperties(molecule);
		AtomContainerManipulator.convertImplicitToExplicitHydrogens(molecule);
		Aromaticity.cdkLegacy().apply(molecule);
		return molecule;
	}

}
