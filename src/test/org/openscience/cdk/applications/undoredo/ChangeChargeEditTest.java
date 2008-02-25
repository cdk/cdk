package org.openscience.cdk.applications.undoredo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.swing.undo.UndoableEdit;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.applications.undoredo.ChangeChargeEdit;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.CDKTestCase;

/**
 * Junit test for the ChangeChargeEdit class
 * 
 * @author tohel
 * @cdk.module test-extra
 * 
 */
public class ChangeChargeEditTest extends CDKTestCase {

	/**
	 * @return
	 */
	public static Test suite() {
		return new TestSuite(ChangeChargeEditTest.class);
	}

	/*
	 * Test method for
	 * 'org.openscience.cdk.applications.undoredo.ChangeChargeEdit.redo()'
	 */
	public void testRedo() {
		HashMap atomChargeMap = createAllChargesPlus1Mol();
		Set atoms = atomChargeMap.keySet();
		Iterator it = atoms.iterator();
		while (it.hasNext()) {
			Atom atom = (Atom) it.next();
			int[] charges = (int[]) atomChargeMap.get(atom);
			UndoableEdit edit = new ChangeChargeEdit(atom, charges[0],
					charges[1]);
			edit.undo();
			edit.redo();
			assertTrue(atom.getFormalCharge() == charges[1]);
		}
	}

	/*
	 * Test method for
	 * 'org.openscience.cdk.applications.undoredo.ChangeChargeEdit.undo()'
	 */
	public void testUndo() {
		HashMap atomChargeMap = createAllChargesPlus1Mol();
		Set atoms = atomChargeMap.keySet();
		Iterator it = atoms.iterator();
		while (it.hasNext()) {
			Atom atom = (Atom) it.next();
			int[] charges = (int[]) atomChargeMap.get(atom);
			UndoableEdit edit = new ChangeChargeEdit(atom, charges[0],
					charges[1]);
			edit.undo();
			assertTrue(atom.getFormalCharge() == charges[0]);
		}
	}

	/**
	 * @return
	 */
	private HashMap createAllChargesPlus1Mol() {
		HashMap atomChargeMap = new HashMap();
		Molecule mol = MoleculeFactory.makeAlphaPinene();
		for (int i = 0; i < mol.getAtomCount(); i++) {
			org.openscience.cdk.interfaces.IAtom atom = mol.getAtom(i);
			int formerCharge = atom.getFormalCharge();
			atom.setFormalCharge(atom.getFormalCharge() + 1);
			int[] charges = new int[2];
			charges[0] = formerCharge;
			charges[1] = atom.getFormalCharge();
			atomChargeMap.put(atom, charges);
		}
		return atomChargeMap;
	}

}
