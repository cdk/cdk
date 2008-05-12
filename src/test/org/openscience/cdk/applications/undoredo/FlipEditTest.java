package org.openscience.cdk.applications.undoredo;

import java.util.HashMap;

import javax.vecmath.Point2d;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.templates.MoleculeFactory;

/**
 * Junit test for the FlipEdit class
 * 
 * @author tohel
 * @cdk.module test-controlold
 */
public class FlipEditTest extends ChangeCoordsEditTest {

	private static Molecule mol = MoleculeFactory.makeAlphaPinene();

	/**
	 * @throws Exception
	 * @throws Exception
	 * 
	 */
	public FlipEditTest() throws Exception {
		super(flipMolecule(), mol);
	}

	/**
	 * @return
	 */
	public static Test suite() {
		return new TestSuite(FlipEditTest.class);
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
	 * (non-Javadoc)
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
	private static HashMap flipMolecule() throws Exception {
		HashMap atomCoordsMap = new HashMap();
		;
		StructureDiagramGenerator generator = new StructureDiagramGenerator(mol);
		generator.generateCoordinates();
		Point2d center = GeometryTools.get2DCenter(mol);
		java.util.Iterator atoms = mol.atoms();
		while (atoms.hasNext()) {
			IAtom a = (IAtom)atoms.next();
			Point2d atom = a.getPoint2d();
			Point2d oldCoord = new Point2d(atom.x, atom.y);
			atom.y = 2.0 * center.y - atom.y;
			Point2d newCoord = atom;
			if (!oldCoord.equals(newCoord)) {
				Point2d[] coords = new Point2d[2];
				coords[0] = newCoord;
				coords[1] = oldCoord;
				atomCoordsMap.put(a, coords);
			}
		}
		return atomCoordsMap;
	}

}
