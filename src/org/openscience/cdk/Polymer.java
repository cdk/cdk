/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2001-2005  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk;

import java.util.Collection;
import java.util.Hashtable;

/**
 * Subclass of Molecule to store Polymer specific attributes that a Polymer has.
 *
 * @cdk.module data
 *
 * @author      Edgar Luttmann <edgar@uni-paderborn.de>
 * @author      Martin Eklund <martin.eklund@farmbio.uu.se>
 * @cdk.created 2001-08-06
 * @cdk.keyword polymer
 */
public class Polymer extends Molecule implements java.io.Serializable, org.openscience.cdk.interfaces.IPolymer
{ 
	/**
     * Determines if a de-serialized object is compatible with this class.
     *
     * This value must only be changed if and only if the new version
     * of this class is imcompatible with the old version. See Sun docs
     * for <a href=http://java.sun.com/products/jdk/1.1/docs/guide
     * /serialization/spec/version.doc.html>details</a>.
	 */
	private static final long serialVersionUID = -2596790658835319339L;

	private Hashtable monomers;	// the list of all the contained Monomers. 
	
	/**
	 *
	 * Contructs a new Polymer to store the Monomers.
	 *
	 */	
	public Polymer() {
		super();
		monomers = new Hashtable();
	}
	
	/**
	 *
	 * Adds the atom oAtom without specifying a Monomer. Therefore the
	 * atom to this AtomContainer, but not to a certain Monomer (intended
	 * e.g. for HETATMs).
	 *
	 * @param oAtom  The atom to add
	 *
	 */
	public void addAtom(org.openscience.cdk.interfaces.IAtom oAtom) {
		super.addAtom(oAtom);
		/* notifyChanged() is called by addAtom in
		 AtomContainer */
	}
	
	/**
	 *
	 * Adds the atom oAtom to a specified Monomer.
	 *
	 * @param oAtom  The atom to add
	 * @param oMonomer  The monomer the atom belongs to
	 *
	 */
	public void addAtom(org.openscience.cdk.interfaces.IAtom oAtom, org.openscience.cdk.interfaces.IMonomer oMonomer) {
		
		if(!contains(oAtom))	{
			super.addAtom(oAtom);
			
			if(oMonomer != null)	{	// Not sure what's better here...throw nullpointer exception?
				oMonomer.addAtom(oAtom);
				
				if (! monomers.containsKey(oMonomer.getMonomerName())) {
					monomers.put(oMonomer.getMonomerName(), oMonomer);
				}
			}
		}
		/* notifyChanged() is called by addAtom in
		 AtomContainer */
	}
	
	/**
	 *
	 * Return the number of monomers present in the Polymer.
	 *
	 * @return number of monomers
	 *
	 */
	public int getMonomerCount() {
		return monomers.size();
	}
	
	/**
	 *
	 * Retrieve a Monomer object by specifying its name.
	 *
	 * @param cName  The name of the monomer to look for
	 * @return The Monomer object which was asked for
	 *
	 */
	public org.openscience.cdk.interfaces.IMonomer getMonomer(String cName) {
		return (Monomer)monomers.get(cName);
	}
	
	/**
	 * Returns a collection of the names of all <code>Monomer</code>s in this
	 * polymer.
	 *
	 * @return a <code>Collection</code> of all the monomer names.
	 */
	public Collection getMonomerNames() {
		return monomers.keySet();
	}
	
	/**
	 * Removes a particular monomer, specified by its name.
	 * 
	 * @param name
	 */
	public void removeMonomer(String name)	{
		if (monomers.containsKey(name))	{
			Monomer monomer = (Monomer)monomers.get(name);
			this.remove(monomer);
			monomers.remove(name);
		}
	}

    public String toString() {
        StringBuffer stringContent = new StringBuffer();
        stringContent.append("Polymer(");
        stringContent.append(this.hashCode()).append(", ");
//        stringContent.append("N:").append(getStrandName()).append(", ");
//        stringContent.append("T:").append(getStrandType()).append(", ");
        stringContent.append(super.toString());
        stringContent.append(")");
        return stringContent.toString();
    }

}
