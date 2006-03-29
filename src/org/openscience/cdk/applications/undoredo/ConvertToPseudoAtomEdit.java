/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2005-2006  The Chemistry Development Kit (CDK) project
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

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * Undo/Redo Edit class for the ConvertToPseudoAtomAction, containing the
 * methods for undoing and redoing the regarding changes
 * 
 * @author tohel
 * 
 */
public class ConvertToPseudoAtomEdit extends AbstractUndoableEdit {

	private IAtom atom;

	private PseudoAtom pseudoAtom;

	private IAtomContainer container;

	/**
	 * @param relevantContainer
	 *            The container containing the atom been changed
	 * @param atom
	 *            The atom been changed
	 * @param pseudo
	 *            The pseudoAtom
	 */
	public ConvertToPseudoAtomEdit(IAtomContainer relevantContainer, IAtom atom,
			PseudoAtom pseudo) {
		this.atom = atom;
		this.pseudoAtom = pseudo;
		this.container = relevantContainer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.undo.UndoableEdit#redo()
	 */
	public void redo() throws CannotRedoException {
		AtomContainerManipulator.replaceAtomByAtom(container, atom, pseudoAtom);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.undo.UndoableEdit#undo()
	 */
	public void undo() throws CannotUndoException {
		AtomContainerManipulator.replaceAtomByAtom(container, pseudoAtom, atom);

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
		return "ConvertToPseudoAtom";
	}

}
