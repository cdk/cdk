package org.openscience.cdk.test.applications.undoredo;

import java.util.HashMap;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.SetOfMolecules;
import org.openscience.cdk.applications.undoredo.AddHydrogenEdit;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.layout.HydrogenPlacer;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Junit test for the ddHydrogenEditTest class
 * 
 * @author tohel
 * @cdk.module test-extra
 * 
 */
public class AddHydrogenEditTest extends TestCase {

	private HydrogenAdder hydrogenAdder;

	private HashMap hydrogenAtomMap;

	private IAtomContainer changedAtomsAndBonds;

	public AddHydrogenEditTest() {
		hydrogenAdder = new HydrogenAdder(
				"org.openscience.cdk.tools.ValencyChecker");
	}

	/**
	 * @return
	 */
	public static Test suite() {
		return new TestSuite(AddHydrogenEditTest.class);
	}

	/*
	 * Test method for
	 * 'org.openscience.cdk.applications.undoredo.addHydrogenEdit.redo()' for
	 * expicit hydrogens
	 */
	public void testRedoExplicitHydrogenAdding() throws Exception {
		Molecule molecule = addExplicitHydrogens();
		ChemModel model = new ChemModel();
		SetOfMolecules som = new SetOfMolecules();
		som.addMolecule(molecule);
		model.setSetOfMolecules(som);
		AddHydrogenEdit edit = new AddHydrogenEdit(model, changedAtomsAndBonds);
		edit.undo();
		edit.redo();
		IAtomContainer container = ChemModelManipulator
				.getAllInOneContainer(model);
		for (int i = 0; i < molecule.getAtomCount(); i++) {
			org.openscience.cdk.interfaces.IAtom atom = container.getAtom(i);
			org.openscience.cdk.interfaces.IAtom atom2 = changedAtomsAndBonds.getAtom(i);
			assertTrue(atom.getHydrogenCount() == atom2.getHydrogenCount());
		}
	}

	/*
	 * Test method for
	 * 'org.openscience.cdk.applications.undoredo.addHydrogenEdit.redo()'
	 * 
	 */
	public void testRedoImplicitHydrogenAdding() throws CDKException {
		Molecule molecule = addImplicitHydrogens();
		ChemModel model = new ChemModel();
		SetOfMolecules som = new SetOfMolecules();
		som.addMolecule(molecule);
		model.setSetOfMolecules(som);
		AddHydrogenEdit edit = new AddHydrogenEdit(model, hydrogenAtomMap);
		edit.undo();
		edit.redo();
		for (int i = 0; i < molecule.getAtomCount(); i++) {
			org.openscience.cdk.interfaces.IAtom atom = molecule.getAtom(i);
			int[] hydrogens = (int[]) hydrogenAtomMap.get(atom);
			assertTrue(atom.getHydrogenCount() == hydrogens[1]);
		}
	}

	/*
	 * Test method for
	 * 'org.openscience.cdk.applications.undoredo.addHydrogenEdit.undo()'
	 */
	public void testUndoExplicitHydrogenAdding() throws Exception {
		Molecule molecule = addExplicitHydrogens();
		ChemModel model = new ChemModel();
		SetOfMolecules som = new SetOfMolecules();
		som.addMolecule(molecule);
		model.setSetOfMolecules(som);
		AddHydrogenEdit edit = new AddHydrogenEdit(model, changedAtomsAndBonds);
		edit.undo();
		IAtomContainer container = ChemModelManipulator
				.getAllInOneContainer(model);
		for (int i = 0; i < molecule.getAtomCount(); i++) {
			org.openscience.cdk.interfaces.IAtom atom = container.getAtom(i);
			org.openscience.cdk.interfaces.IAtom atom2 = changedAtomsAndBonds.getAtom(i);
			assertTrue(atom.getHydrogenCount() == atom2.getHydrogenCount());
		}
	}

	/*
	 * Test method for
	 * 'org.openscience.cdk.applications.undoredo.addHydrogenEdit.undo()'
	 */
	public void testUndoImplicitHydrogenAdding() throws CDKException {
		Molecule molecule = addImplicitHydrogens();
		ChemModel model = new ChemModel();
		SetOfMolecules som = new SetOfMolecules();
		som.addMolecule(molecule);
		model.setSetOfMolecules(som);
		AddHydrogenEdit edit = new AddHydrogenEdit(model, hydrogenAtomMap);
		edit.undo();
		for (int i = 0; i < molecule.getAtomCount(); i++) {
			org.openscience.cdk.interfaces.IAtom atom = molecule.getAtom(i);
			int[] hydrogens = (int[]) hydrogenAtomMap.get(atom);
			assertTrue(atom.getHydrogenCount() == hydrogens[0]);
		}

	}

	/**
	 * @return
	 * @throws Exception
	 */
	private Molecule addExplicitHydrogens() throws Exception {
		Molecule explicitMolecule = MoleculeFactory.makeAlphaPinene();
		StructureDiagramGenerator generator = new StructureDiagramGenerator(
				explicitMolecule);
		generator.generateCoordinates();
		changedAtomsAndBonds = hydrogenAdder
				.addExplicitHydrogensToSatisfyValency(explicitMolecule);
		HydrogenPlacer hPlacer = new HydrogenPlacer();
		hPlacer.placeHydrogens2D(explicitMolecule, 1.0);
		return explicitMolecule;
	}

	/**
	 * @return
	 * @throws CDKException
	 */
	private Molecule addImplicitHydrogens() throws CDKException {
		Molecule implicitMolecule = MoleculeFactory.makeAlphaPinene();
		hydrogenAtomMap = hydrogenAdder
				.addImplicitHydrogensToSatisfyValency(implicitMolecule);
		return implicitMolecule;
	}

}
