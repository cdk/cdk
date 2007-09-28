package org.openscience.cdk.test.applications.undoredo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.MoleculeSet;
import org.openscience.cdk.applications.undoredo.AddHydrogenEdit;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.layout.HydrogenPlacer;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;
import org.openscience.cdk.tools.manipulator.MoleculeSetManipulator;

/**
 * Junit test for the ddHydrogenEditTest class
 * 
 * @author tohel
 * @cdk.module test-extra
 * 
 */
public class AddHydrogenEditTest extends CDKTestCase {

//	private Map<IAtom,int[]> hydrogenAtomMap;
	private IAtomContainer changedAtomsAndBonds;

	public AddHydrogenEditTest() {}

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
		MoleculeSet som = new MoleculeSet();
		som.addMolecule(molecule);
		model.setMoleculeSet(som);
		AddHydrogenEdit edit = new AddHydrogenEdit(model, changedAtomsAndBonds);
		edit.undo();
		edit.redo();
		IAtomContainer container = model.getBuilder().newAtomContainer();
		Iterator containers = MoleculeSetManipulator.getAllAtomContainers(model.getMoleculeSet()).iterator();
		while (containers.hasNext()) container.add((IAtomContainer)containers.next());
		for (int i = 0; i < molecule.getAtomCount(); i++) {
			IAtom atom = container.getAtom(i);
			IAtom atom2 = changedAtomsAndBonds.getAtom(i);
			assertEquals(atom.getHydrogenCount(), atom2.getHydrogenCount());
		}
	}

	/*
	 * Test method for
	 * 'org.openscience.cdk.applications.undoredo.addHydrogenEdit.redo()'
	 * 
	 */
	public void testRedoImplicitHydrogenAdding() throws Exception {
		Molecule molecule = new Molecule();
		Map<IAtom,int[]> hydrogenAtomMap = addImplicitHydrogens(molecule);
		ChemModel model = new ChemModel();
		MoleculeSet som = new MoleculeSet();
		som.addMolecule(molecule);
		model.setMoleculeSet(som);
		AddHydrogenEdit edit = new AddHydrogenEdit(model, hydrogenAtomMap);
		edit.undo();
		edit.redo();
		for (int i = 0; i < molecule.getAtomCount(); i++) {
			IAtom atom = molecule.getAtom(i);
			assertNotNull(atom);
			int[] hydrogens = (int[]) hydrogenAtomMap.get(atom);
			assertNotNull(hydrogens);
			assertEquals(atom.getHydrogenCount(), new Integer(hydrogens[1]));
		}
	}

	/*
	 * Test method for
	 * 'org.openscience.cdk.applications.undoredo.addHydrogenEdit.undo()'
	 */
	public void testUndoExplicitHydrogenAdding() throws Exception {
		Molecule molecule = addExplicitHydrogens();
		ChemModel model = new ChemModel();
		MoleculeSet som = new MoleculeSet();
		som.addMolecule(molecule);
		model.setMoleculeSet(som);
		AddHydrogenEdit edit = new AddHydrogenEdit(model, changedAtomsAndBonds);
		edit.undo();
		IAtomContainer container = model.getBuilder().newAtomContainer();
		Iterator containers = MoleculeSetManipulator.getAllAtomContainers(model.getMoleculeSet()).iterator();
		while (containers.hasNext()) container.add((IAtomContainer)containers.next());
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
	public void testUndoImplicitHydrogenAdding() throws Exception {
		Molecule molecule = new Molecule();
		Map<IAtom,int[]> hydrogenAtomMap = addImplicitHydrogens(molecule);
		ChemModel model = new ChemModel();
		MoleculeSet som = new MoleculeSet();
		som.addMolecule(molecule);
		model.setMoleculeSet(som);
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
		changedAtomsAndBonds = addExplicitHydrogens_ReturnChanges(explicitMolecule);
		HydrogenPlacer hPlacer = new HydrogenPlacer();
		hPlacer.placeHydrogens2D(explicitMolecule, 1.0);
		return explicitMolecule;
	}

	/**
	 * @return
	 * @throws CDKException
	 */
	private Map<IAtom,int[]> addImplicitHydrogens(IMolecule toAddTo) throws Exception {
		Molecule implicitMolecule = MoleculeFactory.makeAlphaPinene();
		CDKAtomTypeMatcher matcher = CDKAtomTypeMatcher.getInstance(implicitMolecule.getBuilder());
		Iterator<IAtom> atoms = implicitMolecule.atoms();
		while (atoms.hasNext()) {
		  IAtom atom = atoms.next();
		  IAtomType type = matcher.findMatchingAtomType(implicitMolecule, atom);
		  AtomTypeManipulator.configure(atom, type);
		}
		CDKHydrogenAdder hAdder = CDKHydrogenAdder.getInstance(implicitMolecule.getBuilder());
		toAddTo.add(implicitMolecule);
		return addImplicitHydrogens_ReturnChanges(implicitMolecule);
	}

    protected IAtomContainer addExplicitHydrogens_ReturnChanges(IAtomContainer container) throws Exception {
    	IAtomContainer changeContainer = container.getBuilder().newAtomContainer();
    	Map<IAtom,int[]> changes = addImplicitHydrogens_ReturnChanges(container);
    	Iterator<IAtom> atoms = changes.keySet().iterator();
        while (atoms.hasNext()) {
            IAtom atom = atoms.next();
            Integer hCount = atom.getHydrogenCount();
            if (hCount != null) {
            	for (int i=0; i<hCount.intValue(); i++) {
            		IAtom hydrogen = atom.getBuilder().newAtom("H");
            		hydrogen.setHydrogenCount(0);
            		IBond newBond = atom.getBuilder().newBond(
            			atom, hydrogen, 
            			CDKConstants.BONDORDER_SINGLE
            		); 
            		container.addAtom(hydrogen);
            		container.addBond(newBond);
            		changeContainer.addAtom(hydrogen);
            		changeContainer.addBond(newBond);
            	}
            	atom.setHydrogenCount(0);
            }
        }
        return changeContainer;
    }

    /**
     * Convenience method that perceives atom types (CDK scheme) and
     * adds implicit hydrogens accordingly. It does not create 2D or 3D
     * coordinates for the new hydrogens.
     * 
     * @param container to which implicit hydrogens are added.
     */
    protected Map<IAtom,int[]> addImplicitHydrogens_ReturnChanges(IAtomContainer container) throws Exception {
    	Map<IAtom,int[]> changes = new HashMap<IAtom,int[]>();
    	CDKAtomTypeMatcher matcher = CDKAtomTypeMatcher.getInstance(container.getBuilder());
    	CDKHydrogenAdder hAdder = CDKHydrogenAdder.getInstance(container.getBuilder());
    	Iterator<IAtom> atoms = container.atoms();
    	while (atoms.hasNext()) {
    		IAtom atom = atoms.next();
    		IAtomType type = matcher.findMatchingAtomType(container, atom);
    		AtomTypeManipulator.configure(atom, type);
    		int oldHCount = atom.getHydrogenCount() == null ? 0 : atom.getHybridization().intValue();
    		hAdder.addImplicitHydrogens(container, atom);
    		changes.put(atom, new int[]{oldHCount, atom.getHydrogenCount().intValue()});
    	}
    	return changes;
    }

}
