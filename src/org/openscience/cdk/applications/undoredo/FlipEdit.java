package org.openscience.cdk.applications.undoredo;

import java.util.HashMap;

/**
 * Undo/Redo Edit class for the FlipAction using the ChangeCoordsEdit superclass
 * for providing undo/redo functionality
 * 
 * @author tohel
 * 
 */
public class FlipEdit extends ChangeCoordsEdit {

	/**
	 * @param atomCoordsMap
	 *            A HashMap containing the changed atoms as key and an Array
	 *            with the former and the changed coordinates as Point2ds
	 */
	public FlipEdit(HashMap atomCoordsMap) {
		super(atomCoordsMap);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.undo.UndoableEdit#getPresentationName()
	 */
	public String getPresentationName() {
		return "Flip";
	}
}
