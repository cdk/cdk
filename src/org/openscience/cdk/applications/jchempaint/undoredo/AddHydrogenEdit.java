package org.openscience.cdk.applications.jchempaint.undoredo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.openscience.cdk.interfaces.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.SetOfMolecules;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;

/**
 * Undo/Redo Edit class for the AddHydrogenAction, containing the methods for
 * undoing and redoing the regarding changes
 * 
 * @author tohel
 * 
 */
public class AddHydrogenEdit extends AbstractUndoableEdit {

    private AtomContainer changedAtomsAndBonds = null;

    private ChemModel model;

    private HashMap hydrogenAtomMap = null;

    /**
     * Constructor for explicit hydrogen addition
     * 
     * @param model
     *            The chemModel been changed
     * @param changedAtomsAndBonds
     *            An Atomcontainer containing the changed atoms and bonds
     */
    public AddHydrogenEdit(ChemModel model, AtomContainer changedAtomsAndBonds) {
        this.changedAtomsAndBonds = changedAtomsAndBonds;
        this.model = model;
    }

    /**
     * Constructor for implicit hydrogen addition
     * 
     * @param model2
     *            The chemModel been changed
     * @param hydrogenAtomMap
     *            A HashMap containing the changed atoms as keys and an Array
     *            with the former and the new hydrogen count
     */
    public AddHydrogenEdit(ChemModel model2, HashMap hydrogenAtomMap) {
        this.model = model2;
        this.hydrogenAtomMap = hydrogenAtomMap;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.undo.UndoableEdit#redo()
     */
    public void redo() throws CannotRedoException {
        if (changedAtomsAndBonds != null) {
            redoExplicitHydrogenAdding();
        } else if (hydrogenAtomMap != null) {
            redoImplicitHydrogenAdding();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.undo.UndoableEdit#undo()
     */
    public void undo() throws CannotUndoException {
        if (changedAtomsAndBonds != null) {
            undoExplicitHydrogenAdding();
        } else if (hydrogenAtomMap != null) {
            undoImplicitHydrogenAdding();
        }
    }

    /**
     * Method realising the redo of implicit hydrogen addition
     */
    private void redoImplicitHydrogenAdding() {
        Set keys = hydrogenAtomMap.keySet();
        Iterator it = keys.iterator();
        while (it.hasNext()) {
            Atom atom = (Atom) it.next();
            int[] hydrogens = (int[]) hydrogenAtomMap.get(atom);
            atom.setHydrogenCount(hydrogens[1]);
        }

    }

    /**
     * Method realising the undo of implicit hydrogen addition
     */
    private void undoImplicitHydrogenAdding() {
        Set keys = hydrogenAtomMap.keySet();
        Iterator it = keys.iterator();
        while (it.hasNext()) {
            Atom atom = (Atom) it.next();
            int[] hydrogens = (int[]) hydrogenAtomMap.get(atom);
            atom.setHydrogenCount(hydrogens[0]);
        }
    }

    /**
     * Method realising the redo of explicit hydrogen addition
     */
    private void redoExplicitHydrogenAdding() {
        if (model.getSetOfMolecules() != null) {
            org.openscience.cdk.interfaces.AtomContainer container = ChemModelManipulator
                    .getAllInOneContainer(model);
            for (int i = 0; i < changedAtomsAndBonds.getAtomCount(); i++) {
                container.addAtom(changedAtomsAndBonds.getAtomAt(i));
            }
            for (int i = 0; i < changedAtomsAndBonds.getBondCount(); i++) {
                Bond bond = changedAtomsAndBonds.getBondAt(i);
                container.addBond(bond);
            }
            Molecule molecule = new Molecule((AtomContainer)container);
            SetOfMolecules moleculeSet = ConnectivityChecker
                    .partitionIntoMolecules(molecule);
            model.setSetOfMolecules(moleculeSet);
        }
    }

    /**
     * Method realising the undo of explicit hydrogen addition
     */
    private void undoExplicitHydrogenAdding() {
        if (model.getSetOfMolecules() != null) {
            for (int i = 0; i < changedAtomsAndBonds.getAtomCount(); i++) {
                org.openscience.cdk.interfaces.AtomContainer container = ChemModelManipulator
                        .getRelevantAtomContainer(model, changedAtomsAndBonds
                                .getAtomAt(i));
                container.removeAtom(changedAtomsAndBonds.getAtomAt(i));
            }
            for (int i = 0; i < changedAtomsAndBonds.getBondCount(); i++) {
                Bond bond = changedAtomsAndBonds.getBondAt(i);
                Atom[] atoms = bond.getAtoms();
                org.openscience.cdk.interfaces.AtomContainer container = ChemModelManipulator
                        .getRelevantAtomContainer(model, changedAtomsAndBonds
                                .getBondAt(i));
                container.removeBond(atoms[0], atoms[1]);
            }
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.undo.UndoableEdit#canRedo()
     */
    public boolean canRedo() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.undo.UndoableEdit#canUndo()
     */
    public boolean canUndo() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.undo.UndoableEdit#getPresentationName()
     */
    public String getPresentationName() {
        return "addHydrogen";
    }

}
