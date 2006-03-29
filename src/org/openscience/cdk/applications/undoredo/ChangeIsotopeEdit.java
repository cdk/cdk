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

/**
 * Undo/Redo Edit class for the ChangeIsotopeAction, containing the methods for
 * undoing and redoing the regarding changes
 * 
 * @author tohel
 * 
 */
public class ChangeIsotopeEdit extends AbstractUndoableEdit {

	private IAtom atom;

	private int formerIsotopeNumber;

	private int isotopeNumber;

	/**
	 * @param atom
	 *            The atom been changed
	 * @param formerIsotopeNumber
	 *            The former mass number
	 * @param isotopeNumber
	 *            The new mass number
	 */
	public ChangeIsotopeEdit(IAtom atom, int formerIsotopeNumber,
			int isotopeNumber) {
		this.atom = atom;
		this.formerIsotopeNumber = formerIsotopeNumber;
		this.isotopeNumber = isotopeNumber;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.undo.UndoableEdit#redo()
	 */
	public void redo() throws CannotRedoException {
		this.atom.setMassNumber(isotopeNumber);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.undo.UndoableEdit#undo()
	 */
	public void undo() throws CannotUndoException {
		this.atom.setMassNumber(formerIsotopeNumber);

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
		return "ChangeIsotope";
	}
}
