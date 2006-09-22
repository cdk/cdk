package org.openscience.cdk.test.applications.undoredo;

import javax.swing.undo.UndoableEdit;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.MoleculeSet;
import org.openscience.cdk.applications.undoredo.RemoveAtomsAndBondsEdit;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.test.CDKTestCase;

/**
 * Junit test for the RemoveAtomsAndBondsEdit class
 * 
 * @author tohel
 * @cdk.module test-extra
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
		assertTrue(model.getMoleculeSet().getMoleculeCount() == 0);
	}

	/*
	 * Test method for
	 * 'org.openscience.cdk.applications.undoredo.RemoveAtomsAndBondsEdit.undo()'
	 */
	public void testUndo() {
		ChemModel model = createAllRemovedMol();
		UndoableEdit edit = new RemoveAtomsAndBondsEdit(model, undoCont, "");
		edit.undo();
		int newAtomCount = model.getMoleculeSet().getMolecule(0)
				.getAtomCount();
		int newBondCount = model.getMoleculeSet().getMolecule(0)
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
		java.util.Iterator atoms = mol.atoms();
		org.openscience.cdk.interfaces.IBond[] bonds = mol.getBonds();
		while (atoms.hasNext()) {
			undoCont.addAtom((IAtom)atoms.next());
		}
		for (int i = 0; i < bonds.length; i++) {
			undoCont.addBond(bonds[i]);
		}
		mol.removeAllElements();
		ChemModel model = new ChemModel();
		MoleculeSet mols = new MoleculeSet();
		mols.addMolecule(mol);
		model.setMoleculeSet(mols);
		return model;
	}

}
