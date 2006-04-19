/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2004-2006  The Chemistry Development Kit (CDK) project
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

import java.util.Collection;
import java.util.Hashtable;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IMonomer;
import org.openscience.cdk.interfaces.IStrand;

/**
 * A Strand is an AtomContainer which stores additional strand specific
 * informations for a group of Atoms.
 *
 * @cdk.module  data
 * @cdk.created 2004-12-20
 * @author      Martin Eklund <martin.eklund@farmbio.uu.se>
 */
public class Strand extends AtomContainer implements java.io.Serializable, IStrand
{
	/**
     * Determines if a de-serialized object is compatible with this class.
     *
     * This value must only be changed if and only if the new version
     * of this class is imcompatible with the old version. See Sun docs
     * for <a href=http://java.sun.com/products/jdk/1.1/docs/guide
     * /serialization/spec/version.doc.html>details</a>.
	 */
	private static final long serialVersionUID = 4200943086350928356L;

	/** The list of all Monomers in the Strand.*/
	private Hashtable monomers;
	/** The name of this strand (e.g. A, B). */
	private String strandName;
	/** The type of this strand (e.g. PEPTIDE, DNA, RNA). */
	private String strandType;
	
	/**
	 * Contructs a new Strand.
	 */	
	public Strand () {
		super();
		// Stand stuff
		monomers = new Hashtable();
		Monomer oMonomer = new Monomer();
		oMonomer.setMonomerName("");
		oMonomer.setMonomerType("UNKNOWN");
		monomers.put("", oMonomer);
                strandName = "";
	}
	
	/**
	 * Retrieve the strand name.
	 *
	 * @return The name of the Strand object
	 */
	public String getStrandName() {
		return strandName;
	}
	
	/**
	 * Retrieve the strand type.
	 *
	 * @return The type of the Strand object
	 */
	public String getStrandType() {
		return strandType;
	}
	
	/**
	 * Set the name of the Strand object.
	 *
	 * @param cStrandName  The new name for this strand
	 */
	public void setStrandName(String cStrandName) {
		strandName = cStrandName;
	}
	
	/**
	 * Set the type of the Strand object.
	 *
	 * @param cStrandType  The new type for this strand
	 */
	public void setStrandType(String cStrandType) {
		strandType = cStrandType;
	}
	
	/**
	 *
	 * Adds the atom oAtom without specifying a Monomer or a Strand. Therefore the
	 * atom gets added to a Monomer of type UNKNOWN in a Strand of type UNKNOWN.
	 *
	 * @param oAtom  The atom to add
	 *
	 */
	public void addAtom(IAtom oAtom) {
		addAtom(oAtom, getMonomer(""));
	}
	
	/**
	 *
	 * Adds the atom oAtom to a specific Monomer.
	 *
	 * @param oAtom  The atom to add
	 * @param oMonomer  The monomer the atom belongs to
	 *
	 */
	public void addAtom(IAtom oAtom, IMonomer oMonomer) {
		
		if(!contains(oAtom))	{
			super.addAtom(oAtom);
			if (oMonomer == null) {
				oMonomer = getMonomer("");
			}
			
			oMonomer.addAtom(oAtom);
			if (! monomers.containsKey(oMonomer.getMonomerName())) {
				monomers.put(oMonomer.getMonomerName(), oMonomer);
			}
		}
	}
	
	/**
	 *
	 * Return the number of monomers present in the Strand.
	 *
	 * @return number of monomers
	 *
	 */
	public int getMonomerCount() {
		return monomers.size() - 1;
	}
	
	/**
	 *
	 * Retrieve a Monomer object by specifying its name.
	 *
	 * @param cName  The name of the monomer to look for
	 * @return The Monomer object which was asked for
	 *
	 */
	public IMonomer getMonomer(String cName) {
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
	 * 
	 * Adds a <code>Monomer</code> to this <code>Strand</code>. All atoms and
	 * bonds in the Monomer are added. NB: The <code>Monomer</code> will *not*
	 * "automatically" be connected to the <code>Strand</code>. That has to be
	 * done "manually" (as the "connection point" is not known). 
	 * @param monomer
	 */
	/*public void addMonomer(Monomer monomer)	{
		if (! monomers.contains(monomer.getMonomerName())) {
			monomers.put(monomer.getMonomerName(), monomer);	// Adderas atomer etc? Nope!
		}
	}*/
	
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
	
	/**
	 * 
	 * @return hashtable containing the monomers in the strand.
	 */
	public Hashtable getMonomers()	{
		return monomers;
	}
    
    public String toString() {
        StringBuffer stringContent = new StringBuffer();
        stringContent.append("Strand(");
        stringContent.append(this.hashCode()).append(", ");
        stringContent.append("N:").append(getStrandName()).append(", ");
        stringContent.append("T:").append(getStrandType()).append(", ");
        stringContent.append(super.toString());
        stringContent.append(")");
        return stringContent.toString();
    }
    
}
