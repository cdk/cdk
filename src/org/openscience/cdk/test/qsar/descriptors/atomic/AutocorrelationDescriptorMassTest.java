package org.openscience.cdk.test.qsar.descriptors.atomic;

import java.io.InputStream;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.test.CDKTestCase;

import org.openscience.cdk.graph.matrix.TopologicalMatrix;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.qsar.descriptors.atomic.AutocorrelationDescriptorMass;

import java.util.ArrayList;

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

	public void testScaledAtomicMasses_IElement() throws Exception {
		String filename = "data/mdl/clorobenzene.mol";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(
				filename);
		MDLReader reader = new MDLReader(ins);
		IMolecule container = (Molecule) reader.read((ChemObject) new Molecule());
		double cont = AutocorrelationDescriptorMass.ScaledAtomicMasses(container.getAtom(0));
		double cont2 = AutocorrelationDescriptorMass.ScaledAtomicMasses(container.getAtom(6));
		assertEquals(1,.0001, cont);
		assertEquals(2.952, .001, cont2);
		System.out.println(cont);
	}
	
	public void testListFormat_IAtomContainer() throws Exception {
		String filename = "data/mdl/clorobenzene.mol";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(
				filename);
		MDLReader reader = new MDLReader(ins);
		IMolecule container = (Molecule) reader.read((ChemObject) new Molecule());
		ArrayList list = AutocorrelationDescriptorMass.ListFormat(container);
		System.out.println("The element in position 6 of the list is: " + list.get(6));
		System.out.println(list);
	}	
		
	public void testAutocorrelationMass_IAtomContainer() throws Exception {
		String filename = "data/mdl/clorobenzene.mol";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(
				filename);
		MDLReader reader = new MDLReader(ins);
		IMolecule container = (Molecule) reader.read((ChemObject) new Molecule());
		int[][] matrix = TopologicalMatrix.getMatrix(container);
		assertEquals(12, matrix.length);
		for (int i = 0; i < matrix.length; i++) {

			System.out.println("");

			for (int j = 0; j < matrix.length; j++) {
				System.out.print(matrix[i][j] + " ");
			}
		}
		int count = container.getAtomCount();
		double[] mass = AutocorrelationDescriptorMass.AutocorrelationMass(container);
		System.out.println();
		System.out.println();
		System.out.println(mass[0]+" "+mass[1] +" "+mass[2]+" "+mass[3]+" "+mass[4]);
		System.out.println(count);
	}
}