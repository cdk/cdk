package org.openscience.cdk.applications.undoredo;

import java.util.HashMap;

import javax.vecmath.Point2d;

import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.applications.undoredo.ChangeCoordsEdit;
import org.openscience.cdk.CDKTestCase;

/**
 * Junit test for the ChangeCoordsEdit class
 * 
 * @author tohel
 * @cdk.module test-extra
 * 
 */
public class ChangeCoordsEditTest extends CDKTestCase {

	private IMolecule mol;

	private HashMap atomCoordsMap;

	/**
	 * @param map
	 * @param mol
	 */
	public ChangeCoordsEditTest(HashMap map, IMolecule mol) {
		this.mol = mol;
		this.atomCoordsMap = map;
	}

	/*
	 * Test method for
	 * 'org.openscience.cdk.applications.undoredo.ChangeCoordsEdit.redo()'
	 */
	public void testRedo() throws Exception {
		ChangeCoordsEdit edit = new ChangeCoordsEdit(atomCoordsMap);
		edit.undo();
		edit.redo();
		for (int i = 0; i < mol.getAtomCount(); i++) {
			org.openscience.cdk.interfaces.IAtom atom = mol.getAtom(i);
			if (atomCoordsMap.containsKey(atom)) {
				assertTrue(atom.getPoint2d().equals(
						((Point2d[]) atomCoordsMap.get(atom))[0]));
			}
		}
	}

	/*
	 * Test method for
	 * 'org.openscience.cdk.applications.undoredo.ChangeCoordsEdit.undo()'
	 */
	public void testUndo() throws Exception {
		ChangeCoordsEdit edit = new ChangeCoordsEdit(atomCoordsMap);
		edit.undo();
		for (int i = 0; i < mol.getAtomCount(); i++) {
			org.openscience.cdk.interfaces.IAtom atom = mol.getAtom(i);
			if (atomCoordsMap.containsKey(atom)) {
				assertTrue(atom.getPoint2d().equals(
						((Point2d[]) atomCoordsMap.get(atom))[1]));
			}
		}
	}

}
