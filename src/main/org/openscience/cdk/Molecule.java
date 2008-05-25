/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 1997-2007  Christoph Steinbeck <steinbeck@users.sf.net>
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

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;

import java.io.Serializable;


/**
 * Represents the concept of a chemical molecule, an object composed of 
 * atoms connected by bonds.
 *
 * @cdk.module  data
 * @cdk.svnrev  $Revision$
 *
 * @author      steinbeck
 * @cdk.created 2000-10-02
 *
 * @cdk.keyword molecule
 */
public class Molecule extends AtomContainer implements Serializable, IMolecule, Cloneable
{

	/**
     * Determines if a de-serialized object is compatible with this class.
     *
     * This value must only be changed if and only if the new version
     * of this class is incompatible with the old version. See Sun docs
     * for <a href=http://java.sun.com/products/jdk/1.1/docs/guide
     * /serialization/spec/version.doc.html>details</a>.
	 */
	private static final long serialVersionUID = 6451193093484831136L;

	/**
	 *  Creates an Molecule without Atoms and Bonds.
	 */
	public Molecule() {
		super();
	}

	/**
	 *  Constructor for the Molecule object. The parameters define the
     *  initial capacity of the arrays.
	 *
	 * @param  atomCount  init capacity of Atom array
	 * @param  bondCount  init capacity of Bond array
     * @param lonePairCount number of lone pairs
     * @param singleElectronCount number of single electrons
	 */
	public Molecule(int atomCount, int bondCount, int lonePairCount, int singleElectronCount)
	{
		super(atomCount, bondCount, lonePairCount, singleElectronCount);
	}

	/**
	 * Constructs a Molecule with
	 * a shallow copy of the atoms and bonds of an AtomContainer.
	 *
	 * @param   container  An Molecule to copy the atoms and bonds from
	 */
	public Molecule(IAtomContainer container)
	{
		super(container);
	}

    /**
     * Returns a one line string representation of this Atom.
     * Methods is conform RFC #9.
     *
     * @return  The string representation of this Atom
     */
    public String toString() {
        StringBuffer description = new StringBuffer();
        description.append("Molecule(");
        description.append(hashCode());
        if (getID() != null) {
        	description.append(", ID=").append(getID());
        }
        description.append(", ").append(super.toString());
        description.append(')');
        return description.toString();
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}


