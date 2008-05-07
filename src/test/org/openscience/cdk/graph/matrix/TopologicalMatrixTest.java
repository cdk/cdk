package org.openscience.cdk.graph.matrix;

import java.io.InputStream;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.graph.matrix.TopologicalMatrix;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.io.ISimpleChemObjectReader.Mode;
import org.openscience.cdk.CDKTestCase;

/**
 * @cdk.module test-standard
 */

public class TopologicalMatrixTest extends CDKTestCase {

	public TopologicalMatrixTest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(TopologicalMatrixTest.class);
	}

	public void testTopologicalMatrix_IAtomContainer() throws Exception {
		String filename = "data/mdl/clorobenzene.mol";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(
				filename);
		MDLReader reader = new MDLReader(ins, Mode.STRICT);
		IMolecule container = (Molecule) reader
				.read((ChemObject) new Molecule());
		int[][] matrix = TopologicalMatrix.getMatrix(container);
		assertEquals(12, matrix.length);
		for (int i = 0; i < matrix.length; i++) {

			System.out.println("");

			for (int j = 0; j < matrix.length; j++) {
				System.out.print(matrix[i][j] + " ");
			}
		}
	}
}