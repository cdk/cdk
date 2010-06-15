/* Copyright (C) 2009-2010 maclean {gilleain.torrance@gmail.com}
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
*/
package org.openscience.cdk.signature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;


/**
 * A list of atom indices, and the label of the orbit.
 * 
 * @cdk.module signature
 * @author maclean
 * @cdk.githash
 */
@TestClass("org.openscience.cdk.signature.OrbitTest")
public class Orbit implements Iterable<Integer>, Cloneable {
    
    /**
     * The atom indices in this orbit
     */
    private List<Integer> atomIndices;
    
    /**
     * The label that all the atoms in the orbit share
     */
    private String label;
    
    /**
     * The maximum height of the signature string
     */
    private int height;
    
    /**
     * @param label
     * @param height
     */
    public Orbit(String label, int height) {
        this.label = label;
        this.atomIndices = new ArrayList<Integer>();
        this.height = height;
    }
    
    /** {@inheritDoc} */
    @TestMethod("iteratorTest")
    public Iterator<Integer> iterator() {
        return this.atomIndices.iterator();
    }
    
    /** {@inheritDoc} */
    @TestMethod("testClone")
    public Object clone() {
        Orbit orbit = new Orbit(this.label, this.height);
        for (Integer i : this.atomIndices) {
            orbit.atomIndices.add(Integer.valueOf(i));
        }
        return orbit;
    }
    
    /**
     * Sorts the atom indices in this orbit
     */
    @TestMethod("sortTest")
    public void sort() {
        // TODO : change the list to a sorted set?
        Collections.sort(this.atomIndices);
    }
    
    /**
     * Gets the height of the signature label.
     * 
     * @return the height of the signature of this orbit
     */
    @TestMethod("getHeightTest")
    public int getHeight() {
        return this.height;
    }
    
    /**
     * Gets all the atom indices as a list.
     * 
     * @return the atom indices
     */
    @TestMethod("getAtomIndicesTest")
    public List<Integer> getAtomIndices() {
        return this.atomIndices;
    }
    
    /**
     * Adds an atom index to the orbit.
     * 
     * @param atomIndex the atom index
     */
    @TestMethod("addAtomTest")
    public void addAtom(int atomIndex) {
        this.atomIndices.add(atomIndex);
    }
    
    /**
     * Checks to see if the orbit has this string as a label.
     * 
     * @param otherLabel the label to compare with
     * @return true if it has this label
     */
    @TestMethod("hasLabelTest")
    public boolean hasLabel(String otherLabel) {
        return this.label.equals(otherLabel);
    }
    
    /**
     * Checks to see if the orbit is empty.
     * 
     * @return true if there are no atom indices in the orbit
     */
    @TestMethod("isEmptyTest")
    public boolean isEmpty() {
        return this.atomIndices.isEmpty();
    }

    /**
     * Gets the first atom index of the orbit.
     * 
     * @return the first atom index
     */
    @TestMethod("getFirstAtomTest")
    public int getFirstAtom() {
        return this.atomIndices.get(0);
    }
    
    /**
     * Removes an atom index from the orbit.
     * 
     * @param atomIndex the atom index to remove
     */
    @TestMethod("removeTest")
    public void remove(int atomIndex) {
        this.atomIndices.remove(this.atomIndices.indexOf(atomIndex));
    }
    

    /**
     * Gets the label of the orbit.
     * 
     * @return the orbit's string label
     */
    @TestMethod("getLabelTest")
    public String getLabel() {
        return this.label;
    }

    /**
     * Checks to see if the orbit contains this atom index.
     * 
     * @param atomIndex the atom index to look for
     * @return true if the orbit contains this atom index
     */
    @TestMethod("containsTest")
    public boolean contains(int atomIndex) {
        return this.atomIndices.contains(atomIndex);
    }
    
    /** {@inheritDoc} */
    @TestMethod("toStringTest")
    public String toString() {
        return label + " " +
        Arrays.deepToString(atomIndices.toArray()); 
    }

}
