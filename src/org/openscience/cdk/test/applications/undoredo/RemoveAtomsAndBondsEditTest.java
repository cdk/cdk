package org.openscience.cdk.test.applications.undoredo;

import javax.swing.undo.UndoableEdit;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.SetOfMolecules;
import org.openscience.cdk.applications.undoredo.RemoveAtomsAndBondsEdit;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.test.CDKTestCase;

/**
 * Junit test for the RemoveAtomsAndBondsEdit class
 * 
 * @author tohel
 * @cdk.module test
 * 
 */
public class RemoveAtomsAndBondsEditTest extends CDKTestCase {

	private int atomCount;

	private int bondCount;

	private AtomContainer undoCont;

	public static Test suite() {
		return new TestSuite(RemoveAtomsAndBondsEditTest.class);
	}
	
	/*
	 * Test method for
	 * 'org.openscience.cdk.applications.undoredo.RemoveAtomsAndBondsEdit.redo()'
	 */
	public void testRedo() {
		ChemModel model = createAllRemovedMol();
		UndoableEdit edit = new RemoveAtomsAndBondsEdit(model, undoCont, "");
		edit.undo();
		edit.redo();
		assertTrue(model.getSetOfMolecules().getMoleculeCount() == 0);
	}

	/*
	 * Test method for
	 * 'org.openscience.cdk.applications.undoredo.RemoveAtomsAndBondsEdit.undo()'
	 */
	public void testUndo() {
		ChemModel model = createAllRemovedMol();
		UndoableEdit edit = new RemoveAtomsAndBondsEdit(model, undoCont, "");
		edit.undo();
		int newAtomCount = model.getSetOfMolecules().getMolecule(0)
				.getAtomCount();
		int newBondCount = model.getSetOfMolecules().getMolecule(0)
				.getBondCount();
		assertTrue(newAtomCount == atomCount && newBondCount == bondCount);
	}

	/**
	 * @return
	 */
	private ChemModel createAllRemovedMol() {
		undoCont = new org.openscience.cdk.AtomContainer();
		Molecule mol = MoleculeFactory.makeAlphaPinene();
		atomCount = mol.getAtomCount();
		bondCount = mol.getBondCount();
		org.openscience.cdk.interfaces.IAtom[] atoms = mol.getAtoms();
		org.openscience.cdk.interfaces.IBond[] bonds = mol.getBonds();
		for (int i = 0; i < atoms.length; i++) {
			undoCont.addAtom(atoms[i]);
		}
		for (int i = 0; i < bonds.length; i++) {
			undoCont.addBond(bonds[i]);
		}
		mol.removeAllElements();
		ChemModel model = new ChemModel();
		SetOfMolecules mols = new SetOfMolecules();
		mols.addMolecule(mol);
		model.setSetOfMolecules(mols);
		return model;
	}

}
