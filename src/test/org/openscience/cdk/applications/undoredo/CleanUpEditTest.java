package org.openscience.cdk.applications.undoredo;

import java.util.HashMap;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.templates.MoleculeFactory;

/**
 * Junit test for the CleanUpEdit class
 * 
 * @author tohel
 * @cdk.module test-extra
 */
public class CleanUpEditTest extends ChangeCoordsEditTest {

	private static IMolecule mol = MoleculeFactory.makeAlphaPinene();

	private static StructureDiagramGenerator diagramGenerator = new StructureDiagramGenerator();

	/**
	 * @throws Exception
	 */
	public CleanUpEditTest() throws Exception {
		super(getCleanMolecule(), mol);
	}

	/**
	 * @return
	 */
	public static Test suite() {
		return new TestSuite(CleanUpEditTest.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openscience.cdk.applications.undoredo.ChangeCoordsEditTest#testUndo()
	 */
	public void testUndo() throws Exception {
		super.testUndo();
	}

	/*
	 * (non-Javadoc).
	 * 
	 * @see org.openscience.cdk.applications.undoredo.ChangeCoordsEditTest#testRedo()
	 */
	public void testRedo() throws Exception {
		super.testRedo();
	}

	/**
	 * @return
	 * @throws Exception
	 */
	private static HashMap getCleanMolecule() throws Exception {
		HashMap atomCoordsMap = new HashMap();
		StructureDiagramGenerator generator = new StructureDiagramGenerator(mol);
		generator.generateCoordinates();
		mol = relayoutMolecule(mol);
		// Commented. Reason: atomCoordsMap is never filled, oldCoord.equals(newCoord) := true
//		org.openscience.cdk.interfaces.IAtom[] atoms = mol.getAtoms();
//		org.openscience.cdk.interfaces.IAtom[] newAtoms = mol.getAtoms();
//		for (int j = 0; j < atoms.length; j++) {
//			Point2d oldCoord = atoms[j].getPoint2d();
//			Point2d newCoord = newAtoms[j].getPoint2d();
//			if (!oldCoord.equals(newCoord)) {
//				Point2d[] coords = new Point2d[2];
//				coords[0] = newCoord;
//				coords[1] = oldCoord;
//				atomCoordsMap.put(newAtoms[j], coords);
//			}
//		}
		
		return atomCoordsMap;
	}

	/**
	 * @param molecule
	 * @return
	 * @throws Exception
	 */
	private static IMolecule relayoutMolecule(IMolecule molecule)
			throws Exception {
		IMolecule cleanedMol = molecule;
		if (molecule != null) {
			if (molecule.getAtomCount() > 2) {
				diagramGenerator.setMolecule(molecule);
				diagramGenerator.generateExperimentalCoordinates(
						new Vector2d(0, 1));
				cleanedMol = diagramGenerator.getMolecule();
			}
		}
		return cleanedMol;
	}
}
