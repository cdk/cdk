package org.openscience.cdk.test.applications.undoredo;

import java.util.ArrayList;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.ElectronContainer;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.SingleElectron;
import org.openscience.cdk.applications.undoredo.ConvertToRadicalEdit;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.test.CDKTestCase;

/**
 * Junit test for the ConvertToRadicalEdit class
 * 
 * @author tohel
 * @cdk.module test
 */
public class ConvertToRadicalEditTest extends CDKTestCase {

	private ArrayList electronContainerList;

	/**
	 * 
	 */
	public ConvertToRadicalEditTest() {
	}

	/**
	 * @return
	 */
	public static Test suite() {
		return new TestSuite(ConvertToRadicalEditTest.class);
	}

	/*
	 * Test method for
	 * 'org.openscience.cdk.applications.undoredo.ConvertToRadicalEdit.redo()'
	 */
	public void testRedo() {
		Molecule mol = MoleculeFactory.makeAlphaPinene();
		for (int i = 0; i < mol.getAtomCount(); i++) {
			org.openscience.cdk.interfaces.IAtom atom = mol.getAtomAt(i);
			ElectronContainer electronContainer = new SingleElectron(atom);
			ConvertToRadicalEdit edit = new ConvertToRadicalEdit(mol,
					electronContainer);
			edit.redo();
		}
		int singleElectronContainerCount = 0;
		for (int i = 0; i < mol.getElectronContainerCount(); i++) {
			org.openscience.cdk.interfaces.IElectronContainer container = mol.getElectronContainerAt(i);
			if (container.getClass() == SingleElectron.class) {
				singleElectronContainerCount += 1;
			}
		}
		assertTrue(singleElectronContainerCount == mol.getAtomCount());
	}

	/*
	 * Test method for
	 * 'org.openscience.cdk.applications.undoredo.ConvertToRadicalEdit.undo()'
	 */
	public void testUndo() {
		Molecule mol = MoleculeFactory.makeAlphaPinene();
		Molecule allRadicalsMol = createAllRadicalsMol(mol);
		for (int i = 0; i < electronContainerList.size(); i++) {
			ElectronContainer container = (ElectronContainer) electronContainerList
					.get(i);
			ConvertToRadicalEdit edit = new ConvertToRadicalEdit(
					allRadicalsMol, container);
			edit.undo();
		}
		int singleElectronContainerCount = 0;
		for (int i = 0; i < allRadicalsMol.getElectronContainerCount(); i++) {
			org.openscience.cdk.interfaces.IElectronContainer container = allRadicalsMol
					.getElectronContainerAt(i);
			if (container instanceof SingleElectron) {
				singleElectronContainerCount += 1;
			}
		}
		assertTrue(singleElectronContainerCount == 0);
	}

	private Molecule createAllRadicalsMol(Molecule mol) {
		Molecule allRadicalsMol = (Molecule) mol.clone();
		electronContainerList = new ArrayList();
		for (int i = 0; i < allRadicalsMol.getAtomCount(); i++) {
			org.openscience.cdk.interfaces.IAtom atom = allRadicalsMol.getAtomAt(i);
			ElectronContainer electronContainer = new SingleElectron(atom);
			allRadicalsMol.addElectronContainer(electronContainer);
			electronContainerList.add(electronContainer);
		}
		return allRadicalsMol;

	}

}
