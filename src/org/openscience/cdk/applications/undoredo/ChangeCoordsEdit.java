package org.openscience.cdk.applications.undoredo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.vecmath.Point2d;

import org.openscience.cdk.applications.jchempaint.JChemPaintPanel;
import org.openscience.cdk.interfaces.Atom;

/**
 * Undo/Redo Edit superclass for all edit classes for coordinate changing
 * actions, containing the methods for undoing and redoing the regarding changes
 * 
 * @author tohel
 * 
 */
public class ChangeCoordsEdit extends AbstractUndoableEdit {

    private HashMap atomCoordsMap;

    private JChemPaintPanel jcpPanel;

    /**
     * @param atomCoordsMap
     *            A HashMap containing the changed atoms as key and an Array
     *            with the former and the changed coordinates as Point2ds
     * @param jcpPanel
     */
    public ChangeCoordsEdit(HashMap atomCoordsMap, JChemPaintPanel jcpPanel) {
        this.atomCoordsMap = atomCoordsMap;
        this.jcpPanel = jcpPanel;
    }

    /**
     * @param atomCoordsMap
     */
    public ChangeCoordsEdit(HashMap atomCoordsMap) {
        this.atomCoordsMap = atomCoordsMap;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.undo.UndoableEdit#redo()
     */
    public void redo() throws CannotRedoException {
        Set keys = atomCoordsMap.keySet();
        Iterator it = keys.iterator();
        while (it.hasNext()) {
            Atom atom = (Atom) it.next();
            Point2d[] coords = (Point2d[]) atomCoordsMap.get(atom);
            atom.setPoint2d(coords[0]);
        }
        if (jcpPanel != null) {
            jcpPanel.scaleAndCenterMolecule(jcpPanel.getChemModel());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.undo.UndoableEdit#undo()
     */
    public void undo() throws CannotUndoException {
        Set keys = atomCoordsMap.keySet();
        Iterator it = keys.iterator();
        while (it.hasNext()) {
            Atom atom = (Atom) it.next();
            Point2d[] coords = (Point2d[]) atomCoordsMap.get(atom);
            atom.setPoint2d(coords[1]);
        }
        if (jcpPanel != null) {
            jcpPanel.scaleAndCenterMolecule(jcpPanel.getChemModel());
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
        return "ChangeCoords";
    }
}
