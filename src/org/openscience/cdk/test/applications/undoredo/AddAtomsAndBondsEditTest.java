package org.openscience.cdk.test.applications.undoredo;

import javax.swing.undo.UndoableEdit;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.SetOfMolecules;
import org.openscience.cdk.applications.undoredo.AddAtomsAndBondsEdit;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.test.CDKTestCase;

/**
 * Junit test for the RemoveAtomsAndBondsEdit class
 * 
 * @author tohel
 * @cdk.module test-extra
 * 
 */
public class AddAtomsAndBondsEditTest extends CDKTestCase {

	private AtomContainer undoCont;

	private int atomCount;

	private int bondCount;

	public static Test suite() {
		return new TestSuite(AddAtomsAndBondsEditTest.class);
	}
	
	/*
	 * Test method for
	 * 'org.openscience.cdk.applications.undoredo.AddAtomsAndBondsEdit.redo()'
	 */
	public void testRedo() {
		ChemModel model = createMol();
		UndoableEdit edit = new AddAtomsAndBondsEdit(model, undoCont, "");
		edit.undo();
		edit.redo();
		int newAtomCount = model.getSetOfMolecules().getMolecule(0)
				.getAtomCount();
		int newBondCount = model.getSetOfMolecules().getMolecule(0)
				.getBondCount();
		assertTrue(newAtomCount == atomCount + 1
				&& newBondCount == bondCount + 1);
	}

	/*
	 * Test method for
	 * 'org.openscience.cdk.applications.undoredo.AddAtomsAndBondsEdit.undo()'
	 */
	public void testUndo() {
		ChemModel model = createMol();
		UndoableEdit edit = new AddAtomsAndBondsEdit(model, undoCont, "");
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
	private ChemModel createMol() {
		undoCont = new org.openscience.cdk.AtomContainer();
		Molecule mol = MoleculeFactory.makeAlphaPinene();
		atomCount = mol.getAtomCount();
		bondCount = mol.getBondCount();
		Atom atom = new Atom("C");
		Bond bond = new Bond(atom, mol.getAtomAt(2));
		undoCont.addAtom(atom);
		undoCont.addBond(bond);
		mol.addAtom(atom);
		mol.addBond(bond);
		ChemModel model = new ChemModel();
		SetOfMolecules mols = new SetOfMolecules();
		mols.addMolecule(mol);
		model.setSetOfMolecules(mols);
		return model;
	}

}
