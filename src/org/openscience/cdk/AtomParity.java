/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2000-2004  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.openscience.cdk;

/**
 * Represents the concept of an atom parity identifying the stereochemistry
 * around an atom, given four neighbouring atoms.
 * @cdk.module core
 *
 * @author      steinbeck
 * @cdk.created 2000-10-02
 * @cdk.keyword atom parity
 * @cdk.keyword stereochemistry
 */
public class AtomParity implements java.io.Serializable, Cloneable  {
    
    private Atom centralAtom;
    private Atom[] neighbors;
    private int parity;
    
    /**
     * Constructs an completely unset AtomParity.
     *
     * @param centralAtom Atom for which the parity is defined
     * @param first       First Atom of four that define the stereochemistry
     * @param second      Second Atom of four that define the stereochemistry
     * @param third       Third Atom of four that define the stereochemistry
     * @param fourth      Fourth Atom of four that define the stereochemistry
     * @param int         +1 or -1, defining the parity
     */
    public AtomParity(Atom centralAtom, 
                      Atom first, Atom second, Atom third, Atom fourth,
                      int parity) {
        this.centralAtom = centralAtom;
        this.neighbors = new Atom[4];
        this.neighbors[0] = first;
        this.neighbors[1] = second;
        this.neighbors[2] = third;
        this.neighbors[3] = fourth;
        this.parity = parity;
    }
    
    /**
     * Returns the atom for which this parity is defined.
     */
    public Atom getAtom() { 
        return centralAtom;
    }
    
    /**
     * Returns the four atoms that define the stereochemistry for
     * this parity.
     */
    public Atom[] getSurroundingAtoms() {
        return neighbors;
    }
    
    /**
     * Returns the parity value.
     */
    public int getParity() {
        return parity;
    }

    /**
     * Returns a one line string representation of this AtomParity.
     * Methods is conform RFC #9.
     *
     * @return  The string representation of this AtomParity
     */
    public String toString() {
        StringBuffer resultString = new StringBuffer();
        resultString.append("AtomParity(");
        resultString.append(this.hashCode() + ", ");
        resultString.append(centralAtom.getID() + ", ");
        resultString.append("F:[" + neighbors[0].getID() + ", ");
        resultString.append(neighbors[1].getID() + ", ");
        resultString.append(neighbors[2].getID() + ", ");
        resultString.append(neighbors[3].getID() + "], ");
        resultString.append(parity);
        resultString.append(")");
        return resultString.toString();
    }

    /**
     * Clones this AtomParity object.
     *
     * @return  The cloned object   
     */
    public Object clone() {
        // is this correct? (taken from Atom, but seems wrong)
        Object clone = null;
        try {
            clone = super.clone();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return clone;
    }
    
}





