/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2002-2007  Bradley A. Smith <yeldar@home.com>
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A molecular vibration composed of a set of atom vectors.
 * The atom vectors represent forces acting on the atoms. They
 * are specified by double[3] arrays containing the components
 * of the vector.
 *
 * @author Bradley A. Smith <yeldar@home.com>
 * @cdk.svnrev  $Revision$
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
    private List<double[]> atomVectors = new ArrayList<double[]>();
    
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
        atomVectors.add(atomVector);
    }
    
    /**
     * Gets a atom vector at index given.
     *
     * @param index number for the atom vector to be returned
     * @return atom vector in double[3] array
     */
    public double[] getAtomVector(int index) {
        return atomVectors.get(index);
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
    public Iterator<double[]> getAtomVectors() {
        return atomVectors.iterator();
    }
    
    /**
     * Removes all atom vectors from this vibration.
     */
    public void removeAtomVectors() {
        atomVectors.clear();
    }
}
