package org.openscience.cdk.applications.undoredo;

import java.io.IOException;
import java.io.OptionalDataException;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.openscience.cdk.Atom;
import org.openscience.cdk.config.IsotopeFactory;

/**
 * Undo/Redo Edit class for the ChangeAtomSymbolAction, containing the methods
 * for undoing and redoing the regarding changes
 * 
 * @author tohel
 * 
 */
public class ChangeAtomSymbolEdit extends AbstractUndoableEdit {

	private Atom atom;

	private String formerSymbol;

	private String symbol;

	/**
	 * @param atomInRange
	 *            The atom been changed
	 * @param formerSymbol
	 *            The atom symbol before change
	 * @param symbol
	 *            The atom symbol past change
	 */
	public ChangeAtomSymbolEdit(Atom atomInRange, String formerSymbol,
			String symbol) {
		this.atom = atomInRange;
		this.formerSymbol = formerSymbol;
		this.symbol = symbol;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.undo.UndoableEdit#redo()
	 */
	public void redo() throws CannotRedoException {
		this.atom.setSymbol(symbol);
		try {
			IsotopeFactory.getInstance().configure(atom);
		} catch (OptionalDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.undo.UndoableEdit#undo()
	 */
	public void undo() throws CannotUndoException {
		this.atom.setSymbol(formerSymbol);
		try {
			IsotopeFactory.getInstance().configure(atom);
		} catch (OptionalDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		return "ChangeAtomSymbol";
	}

}
