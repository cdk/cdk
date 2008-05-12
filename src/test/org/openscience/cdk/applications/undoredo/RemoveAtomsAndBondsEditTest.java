package org.openscience.cdk.applications.undoredo;

import java.util.Iterator;

import javax.swing.undo.UndoableEdit;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.MoleculeSet;
import org.openscience.cdk.applications.undoredo.RemoveAtomsAndBondsEdit;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.CDKTestCase;

/**
 * Junit test for the RemoveAtomsAndBondsEdit class
 * 
 * @author tohel
 * @cdk.module test-controlold
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
		Iterator atoms = mol.atoms();
		while (atoms.hasNext()) {
			undoCont.addAtom((IAtom)atoms.next());
		}
		Iterator bonds = mol.bonds();
		while (bonds.hasNext()) {
			undoCont.addBond((IBond)bonds.next());
		}
		mol.removeAllElements();
		ChemModel model = new ChemModel();
		MoleculeSet mols = new MoleculeSet();
		mols.addMolecule(mol);
		model.setMoleculeSet(mols);
		return model;
	}

}
