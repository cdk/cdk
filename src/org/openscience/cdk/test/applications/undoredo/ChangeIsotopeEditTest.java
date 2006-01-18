package org.openscience.cdk.test.applications.undoredo;

import java.io.IOException;
import java.io.OptionalDataException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.swing.undo.UndoableEdit;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.applications.undoredo.ChangeIsotopeEdit;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.test.CDKTestCase;

/**
 * Junit test for the ChangeIsotopeEdit class
 * 
 * @author tohel
 * @cdk.module test
 */
public class ChangeIsotopeEditTest extends CDKTestCase {

	private Molecule mol;

	/**
	 * 
	 */
	public ChangeIsotopeEditTest() {
	}

	/**
	 * @return
	 */
	public static Test suite() {
		return new TestSuite(ChangeIsotopeEditTest.class);
	}

	/*
	 * Test method for
	 * 'org.openscience.cdk.applications.undoredo.ChangeIsotopeEdit.redo()'
	 */
	public void testRedo() throws OptionalDataException, IOException,
			ClassNotFoundException {
		HashMap isotopesMap = createAllPlus1Molecule();
		Set atoms = isotopesMap.keySet();
		Iterator it = atoms.iterator();
		while (it.hasNext()) {
			Atom atom = (Atom) it.next();
			int[] isotopes = (int[]) isotopesMap.get(atom);
			int isotopeNumber = isotopes[1];
			int formerIsotopeNumber = isotopes[0];
			UndoableEdit edit = new ChangeIsotopeEdit(atom,
					formerIsotopeNumber, isotopeNumber);
			edit.undo();
			edit.redo();
		}
		for (int i = 0; i < mol.getAtomCount(); i++) {
			org.openscience.cdk.interfaces.IAtom atom = mol.getAtomAt(i);
			assertTrue(atom.getMassNumber() == ((int[]) isotopesMap.get(atom))[1]);
		}
	}

	/*
	 * Test method for
	 * 'org.openscience.cdk.applications.undoredo.ChangeIsotopeEdit.undo()'
	 */
	public void testUndo() throws OptionalDataException, IOException,
			ClassNotFoundException {
		HashMap isotopesMap = createAllPlus1Molecule();
		Set atoms = isotopesMap.keySet();
		Iterator it = atoms.iterator();
		while (it.hasNext()) {
			Atom atom = (Atom) it.next();
			int[] isotopes = (int[]) isotopesMap.get(atom);
			int isotopeNumber = isotopes[1];
			int formerIsotopeNumber = isotopes[0];
			UndoableEdit edit = new ChangeIsotopeEdit(atom,
					formerIsotopeNumber, isotopeNumber);
			edit.undo();
		}
		for (int i = 0; i < mol.getAtomCount(); i++) {
			org.openscience.cdk.interfaces.IAtom atom = mol.getAtomAt(i);
			assertTrue(atom.getMassNumber() == ((int[]) isotopesMap.get(atom))[0]);
		}
	}

	/**
	 * @return
	 * @throws OptionalDataException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private HashMap createAllPlus1Molecule() throws OptionalDataException,
			IOException, ClassNotFoundException {
		mol = MoleculeFactory.makeAlphaPinene();
		HashMap isotopesMap = new HashMap();
		for (int i = 0; i < mol.getAtomCount(); i++) {
			org.openscience.cdk.interfaces.IAtom atom = mol.getAtomAt(i);
			IIsotope isotope = IsotopeFactory.getInstance(atom.getBuilder()).getMajorIsotope(
					atom.getSymbol());
			int isotopeNumber = isotope.getMassNumber();
			int formerIsotopeNumber = isotopeNumber;
			isotopeNumber++;
			atom.setMassNumber(isotopeNumber);
			int[] isotopes = new int[2];
			isotopes[0] = formerIsotopeNumber;
			isotopes[1] = isotopeNumber;
			isotopesMap.put(atom, isotopes);
		}
		return isotopesMap;
	}

}
