/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2002-2003  The Jmol Development Team
 * Copyright (C) 2004-2006  The Chemistry Development Kit (CDK) Project
 *
 * Contact: jmol-developers@lists.sf.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA. 
 */
package org.openscience.cdk;

import java.util.Enumeration;
import java.util.Vector;

/**
 * A molecular vibration composed of a set of atom vectors.
 * The atom vectors represent forces acting on the atoms. They
 * are specified by double[3] arrays containing the components
 * of the vector.
 *
 * @author Bradley A. Smith <yeldar@home.com>
 */
public class Vibration {

    /**
     * Label identifying this vibration. For example, the
     * frequency in reciprocal centimeters could be used.
     */
    private String label;
    
    /**
     * List of atom vectors of type double[3]
     */
    private Vector atomVectors = new Vector();
    
    /**
     * Create a vibration identified by the label.
     *
     * @param label identification for this vibration
     */
    public Vibration(String label) {
        this.label = label;
    }
    
    /**
     * Gets the label identifying this vibration.
     *
     * @return label identifying this vibration
     */
    public String getLabel() {
        return label;
    }
    
    /**
     * Adds a atom vector to the vibration.
     *
     * @param atomVector atom vector in double[3] array
     */
    public void addAtomVector(double[] atomVector) {
        atomVectors.addElement(atomVector);
    }
    
    /**
     * Gets a atom vector at index given.
     *
     * @param index number for the atom vector to be returned
     * @return atom vector in double[3] array
     */
    public double[] getAtomVector(int index) {
        return (double[]) atomVectors.elementAt(index);
    }
    
    /**
     * Gets the number of atom vectors in the vibration.
     *
     * @return number of atom vectors
     */
    public int getAtomVectorCount() {
        return atomVectors.size();
    }
    
    /**
     * Returns an Enumeration of the atom vectors of this vibration.
     *
     * @return an enumeration of the atom vectors of this vibration
     */
    public Enumeration getAtomVectors() {
        return atomVectors.elements();
    }
    
    /**
     * Removes all atom vectors from this vibration.
     */
    public void removeAtomVectors() {
        atomVectors.removeAllElements();
    }
}
