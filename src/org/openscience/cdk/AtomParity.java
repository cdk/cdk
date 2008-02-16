/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2004-2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk;

import java.io.Serializable;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomParity;

/**
 * Represents the concept of an atom parity identifying the stereochemistry
 * around an atom, given four neighbouring atoms.
 * 
 * @cdk.module data
 * @cdk.svnrev  $Revision$
 *
 * @cdk.created 2000-10-02
 * @cdk.keyword atom parity
 * @cdk.keyword stereochemistry
 */
public class AtomParity extends ChemObject implements IAtomParity, Serializable, Cloneable  {
    
    /**
     * Determines if a de-serialized object is compatible with this class.
     *
     * This value must only be changed if and only if the new version
     * of this class is incompatible with the old version. See Sun docs
     * for <a href=http://java.sun.com/products/jdk/1.1/docs/guide
     * /serialization/spec/version.doc.html>details</a>.
	 */
	private static final long serialVersionUID = -2031408037315976637L;

	private IAtom centralAtom;
    private IAtom[] neighbors;
    private int parity;
    
    /**
     * Constructs an completely unset AtomParity.
     *
     * @param centralAtom Atom for which the parity is defined
     * @param first       First Atom of four that define the stereochemistry
     * @param second      Second Atom of four that define the stereochemistry
     * @param third       Third Atom of four that define the stereochemistry
     * @param fourth      Fourth Atom of four that define the stereochemistry
     * @param parity      +1 or -1, defining the parity
     */
    public AtomParity(
    		IAtom centralAtom, 
    		IAtom first, 
    		IAtom second, 
    		IAtom third, 
    		IAtom fourth,
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
     *
     * @return The atom for which this parity is defined
     */
    public IAtom getAtom() { 
        return centralAtom;
    }
    
    /**
     * Returns the four atoms that define the stereochemistry for
     * this parity.
     *
     * @return The four atoms that define the stereochemistry for
     *         this parity
     */
    public IAtom[] getSurroundingAtoms() {
        return neighbors;
    }
    
    /**
     * Returns the parity value.
     *
     * @return The parity value
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
        StringBuffer resultString = new StringBuffer(32);
        resultString.append("AtomParity(");
        resultString.append(this.hashCode()).append(", ");
        resultString.append(centralAtom.getID());
        resultString.append(", F:[").append(neighbors[0].getID()).append(", ");
        resultString.append(neighbors[1].getID()).append(", ");
        resultString.append(neighbors[2].getID()).append(", ");
        resultString.append(neighbors[3].getID()).append("], ");
        resultString.append(parity);
        resultString.append(')');
        return resultString.toString();
    }

    /**
     * Clones this AtomParity object.
     *
     * @return  The cloned object   
     */
    public Object clone() throws CloneNotSupportedException {
        AtomParity clone = (AtomParity)super.clone();
        // clone Atom's
        clone.centralAtom  = (IAtom)centralAtom.clone();
        clone.neighbors = new IAtom[4];
        clone.neighbors[0] = (IAtom)(neighbors[0].clone());
        clone.neighbors[1] = (IAtom)(neighbors[1].clone());
        clone.neighbors[2] = (IAtom)(neighbors[2].clone());
        clone.neighbors[3] = (IAtom)(neighbors[3].clone());
        return clone;
    }
    
}





