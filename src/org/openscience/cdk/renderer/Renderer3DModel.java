/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 1997-2005  The Chemistry Development Kit (CDK) project
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *  */
package org.openscience.cdk.renderer;

import java.awt.Color;
import java.util.EventObject;
import java.util.Vector;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.event.CDKChangeListener;
import org.openscience.cdk.renderer.color.AtomColorer;
import org.openscience.cdk.renderer.color.CDKAtomColors;


/**
 * A model for Renderer3D, which determines how things are drawn
 *
 * @author Egon Willighagen
 */
public class Renderer3DModel {

    private Color backColor = Color.white;
    private AtomColorer atomColorer = new CDKAtomColors();

    private Vector listeners = new Vector();

    /**
     * Adds a change listener to the list of listeners
     *
     * @param   listener  The listener added to the list
     */

    public void addCDKChangeListener(CDKChangeListener listener) {
        listeners.add(listener);
    }


    /**
     * Removes a change listener from the list of listeners
     *
     * @param   listener  The listener removed from the list
     */
    public void removeCDKChangeListener(CDKChangeListener listener) {
        listeners.remove(listener);
    }


    /**
     * Notifies registered listeners of certain changes
     * that have occurred in this model.
     */
    public void fireChange() {
        EventObject event = new EventObject(this);
        for (int i = 0; i < listeners.size(); i++) {
            ((CDKChangeListener)listeners.get(i)).stateChanged(event);
        }
    }

    /**
     * Returns the background color
     *
     * @return the background color
     */
    public Color getBackColor() {
        return this.backColor;
    }


    /**
     * Sets the background color
     *
     * @param   backColor the background color
     */
    public void setBackColor(Color backColor) {
        this.backColor = backColor;
    }

    /**
     * Returns the atom colorer
     *
     * @return the atom colorer
     */
    public Color getAtomColor(IAtom a) {
        return atomColorer.getAtomColor(a);
    }


    /**
     * Returns the current atom colorer.
     *
     * @return  The AtomColorer.
     */
    public AtomColorer getAtomColorer()
    {
        return atomColorer;
    }

    /**
     * Sets the atom colorer.
     *
     * @param ac  the new colorer.
     */
    public void setAtomColorer(final AtomColorer ac)
    {
        atomColorer = ac;
    }
}
