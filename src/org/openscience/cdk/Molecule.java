/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 1997-2003  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
 */
package org.openscience.cdk;


/**
 * Represents the concept of a chemical molecule, an object composed of 
 * atoms connected by bonds.
 *
 * @cdkPackage core
 *
 * @author     steinbeck
 * @created    2000-10-02
 *
 * @keyword    molecule
 */
public class Molecule extends AtomContainer implements java.io.Serializable, Cloneable
{

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
	 */
	public Molecule(int atomCount, int bondCount)
	{
		super(atomCount, bondCount);
	}

	/**
	 * Constructs a Molecule with
	 * a shallow copy of the atoms and bonds of an AtomContainer.
	 *
	 * @param   ac  An Molecule to copy the atoms and bonds from
	 */
	public Molecule(AtomContainer ac)
	{
		super(ac);
	}

       /**
         * Clones this molecule object.
         *
         * @return  The cloned molecule object
         */
        public Object clone()
        {
                Object o = null;
                try
                {
                        o = super.clone();
                }
                catch (Exception e)
                {
                        e.printStackTrace(System.err);
                }
                return o;
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
        description.append(getID() + ", ");
        description.append(super.toString());
        description.append(")");
        return description.toString();
    }
}


