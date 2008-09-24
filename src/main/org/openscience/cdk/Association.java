/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2004-2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.interfaces.IAtom;

/**
 * Base class for storing interactions like hydrogen bonds and ionic interactions.
 * The ElectronContainer contains zero electrons by default.
 *
 * @cdk.module extra
 * @cdk.svnrev  $Revision$
 *
 * @cdk.keyword orbital
 * @cdk.keyword association
 * @cdk.keyword bond
 */
@TestClass("org.openscience.cdk.AssociationTest")
public class Association extends ElectronContainer implements java.io.Serializable, Cloneable {

    /**
     * Determines if a de-serialized object is compatible with this class.
     *
     * This value must only be changed if and only if the new version
     * of this class is incompatible with the old version. See Sun docs
     * for <a href=http://java.sun.com/products/jdk/1.1/docs/guide
     * /serialization/spec/version.doc.html>details</a>.
	 */
	private static final long serialVersionUID = -9138919895942850167L;

	/** Number of electrons in the association. */
    protected final int electronCount = 0;

    /** The atoms which take part in the association. */
    protected IAtom[] atoms;
    protected int atomCount;

    /**
     * Constructs an association between two Atom's.
     *
     * @param atom1 An atom to be associated with another atom
     * @param atom2 An atom to be associated with another atom
     * @see org.openscience.cdk.Atom
     */
    @TestMethod("testAssociation_IAtom_IAtom")
    public Association(IAtom atom1, IAtom atom2) {
        atoms = new IAtom[2];
        atoms[0] = atom1;
        atoms[1] = atom2;
        atomCount = 2;
    }

    /**
     * Constructs an empty association.
     *
     * @see org.openscience.cdk.Atom
     */
    @TestMethod("testAssociation")
    public Association() {
        atoms = new Atom[2];
        atomCount = 0;
    }

	/**
	 * Returns the array of atoms making up this Association.
	 *
	 * @return An array of atoms participating in this Association
     *
     * @see    #setAtoms
	 */
    @TestMethod("testGetAtoms")
    public IAtom[] getAtoms() {
        IAtom[] returnAtoms = new Atom[atomCount];
        System.arraycopy(this.atoms, 0, returnAtoms, 0, returnAtoms.length);
        return returnAtoms;
    }
	
	/**
	 * Sets the array of atoms making up this Association.
	 *
	 * @param   atoms An array of atoms that forms this Association
     *
     * @see    #getAtoms
	 */
    @TestMethod("testSetAtoms")
    public void setAtoms(IAtom[] atoms) {
		this.atoms = atoms;
		notifyChanged();
	}


	/**
	 * Returns the number of Atoms in this Association.
	 *
	 * @return The number of Atoms in this Association    
	 */
    @TestMethod("testGetAtomCount")
    public int getAtomCount() {
		return atomCount;
	}

    /**
     * Returns the number of electrons in a Association.
     *
     * @return The number of electrons in a Association.
     */
    @TestMethod("testGetAtomAt")
    public Integer getElectronCount() {
        return 0;
    }

	/**
	 * Returns an Atom from this Association.
	 *
	 * @param   position  The position in this bond where the atom is
     * @return            The atom at the specified position
     *
     * @see     #setAtomAt
	 */
    @TestMethod("testGetAtomAt")
    public IAtom getAtomAt(int position) {
		return atoms[position];
	}

	/**
	 * Returns true if the given atom participates in this Association.
	 *
	 * @param   atom  The atom to be tested if it participates in this Association
	 * @return     true if the atom participates in this Association
	 */
    @TestMethod("testContains")
    public boolean contains(IAtom atom) {
        for (IAtom atom1 : atoms) {
            if (atom1 == atom) {
                return true;
            }
        }
        return false;
	}

	/**
	 * Sets an Atom in this Association.
	 *
	 * @param   atom  The atom to be set
	 * @param   position  The position in this Association where the atom is to be inserted
     *
     * @see     #getAtomAt
	 */
    @TestMethod("testSetAtomAt")
    public void setAtomAt(IAtom atom, int position) {
		atoms[position] = atom;
		notifyChanged();
	}

    /**
     * Returns a one line string representation of this Container.
     * This method is conform RFC #9.
     *
     * @return    The string representation of this Container
     */
    @TestMethod("testToString")
    public String toString() {
        StringBuffer s = new StringBuffer();
        s.append("Association(");
        s.append(this.hashCode());
        for (int i=0; i<atomCount; i++) {
            s.append(", ").append(atoms[i].toString());
        }
        s.append(")");
        return s.toString();
    }
}


