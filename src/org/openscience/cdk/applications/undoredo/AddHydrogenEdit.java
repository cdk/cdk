/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2005-2007  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.applications.undoredo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;

/**
 * Undo/Redo Edit class for the AddHydrogenAction, containing the methods for
 * undoing and redoing the regarding changes
 * 
 * @author tohel
 * @cdk.module control
 */
public class AddHydrogenEdit extends AbstractUndoableEdit {

    private static final long serialVersionUID = -6007429589296415034L;

    private IAtomContainer changedAtomsAndBonds = null;

	private IChemModel model;

	private HashMap hydrogenAtomMap = null;

	/**
	 * Constructor for explicit hydrogen addition
	 * 
	 * @param model
	 *            The chemModel been changed
	 * @param changedAtomsAndBonds
	 *            An Atomcontainer containing the changed atoms and bonds
	 */
	public AddHydrogenEdit(IChemModel model, IAtomContainer changedAtomsAndBonds) {
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
	public AddHydrogenEdit(IChemModel model2, HashMap hydrogenAtomMap) {
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
			IAtom atom = (IAtom) it.next();
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
			IAtom atom = (IAtom) it.next();
			int[] hydrogens = (int[]) hydrogenAtomMap.get(atom);
			atom.setHydrogenCount(hydrogens[0]);
		}
	}

	/**
	 * Method realising the redo of explicit hydrogen addition
	 */
	private void redoExplicitHydrogenAdding() {
		if (model.getMoleculeSet() != null) {
			IAtomContainer container = model.getBuilder().newAtomContainer();
        	Iterator containers = ChemModelManipulator.getAllAtomContainers(model).iterator();
        	while (containers.hasNext()) {
        		container.add((IAtomContainer)containers.next());
        	}
			for (int i = 0; i < changedAtomsAndBonds.getAtomCount(); i++) {
				container.addAtom(changedAtomsAndBonds.getAtom(i));
			}
			for (int i = 0; i < changedAtomsAndBonds.getBondCount(); i++) {
				IBond bond = changedAtomsAndBonds.getBond(i);
				container.addBond(bond);
			}
			IMolecule molecule = container.getBuilder().newMolecule(container);
			IMoleculeSet moleculeSet = ConnectivityChecker
					.partitionIntoMolecules(molecule);
			model.setMoleculeSet(moleculeSet);
		}
	}

	/**
	 * Method realising the undo of explicit hydrogen addition
	 */
	private void undoExplicitHydrogenAdding() {
		if (model.getMoleculeSet() != null) {
			for (int i = 0; i < changedAtomsAndBonds.getAtomCount(); i++) {
				IAtomContainer container = ChemModelManipulator
						.getRelevantAtomContainer(model, changedAtomsAndBonds
								.getAtom(i));
				container.removeAtom(changedAtomsAndBonds.getAtom(i));
			}
			for (int i = 0; i < changedAtomsAndBonds.getBondCount(); i++) {
				IBond bond = changedAtomsAndBonds.getBond(i);
				IAtomContainer container = ChemModelManipulator
						.getRelevantAtomContainer(model, changedAtomsAndBonds
								.getBond(i));
				container.removeBond(bond.getAtom(0), bond.getAtom(1));
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
