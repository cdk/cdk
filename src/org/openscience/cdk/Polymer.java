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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *  */
package org.openscience.cdk;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Subclass of Molecule to store Polymer specific attributes a Polymer has.
 *
 * @cdk.module data
 *
 * @author      Edgar Luttmann <edgar@uni-paderborn.de>
 * @author      Martin Eklund <martin.eklund@farmbio.uu.se>
 * @cdk.created 2001-08-06
 * @cdk.keyword polymer
 */
public class Polymer extends Molecule implements java.io.Serializable, Cloneable
{ 
	private Hashtable strands;	// the list of all the contained Strands.
	
	/**
	 * Contructs a new Polymer to store the Strands.
	 */	
	public Polymer() {
		super();
		// Strand stuff
		strands = new Hashtable();
		Strand oStrand = new Strand();
		oStrand.setStrandName("");
		oStrand.setStrandType("UNKNOWN");
		strands.put("", oStrand);		
	}
	
	/**
	 * Adds the atom oAtom without specifying a Monomer or a Strand. Therefore the
	 * atom gets added to a Monomer of type UNKNOWN in a Strand of type UNKNOWN.
	 *
	 * @param oAtom  The atom to add
	 *
	 */
	public void addAtom(Atom oAtom) {
		addAtom(oAtom, getStrand(""));
		/* notifyChanged() is called by addAtom in
		 AtomContainer */
	}
	
	/**
	 * Adds the atom oAtom to a specified Strand, whereas the Monomer is unspecified. Hence
	 * the atom will be added to a Monomer of type UNKNOWN in the specified Strand.
	 *
	 * @param oAtom  The atom to add
	 * @param oStrand  The strand the atom belongs to
	 *
	 */
	public void addAtom(Atom oAtom, Strand oStrand) {
		super.addAtom(oAtom);
		if (oStrand == null) {
			oStrand = getStrand("");
		}
		oStrand.addAtom(oAtom);
		if (!strands.contains(oStrand.getStrandName())) {
			strands.put(oStrand.getStrandName(), oStrand);
		}
		/* notifyChanged() is called by addAtom in
		 AtomContainer */
	}
	
	/**
	 * Adds the atom to a specified Strand and a specified Monomer.
	 * 
	 * @param oAtom
	 * @param oMonomer
	 * @param oStrand
	 */
	public void addAtom(Atom oAtom, Monomer oMonomer, Strand oStrand)	{
		// Add atom to AtomContainer
		super.addAtom(oAtom);
		
		if(oStrand == null)	{
			oStrand = getStrand("");
		}
		
		if (oMonomer == null) {
			oMonomer = getMonomer("", "");
		}
		// Add atom to Strand (also adds the atom to the monomer).
		oStrand.addAtom(oAtom, oMonomer);
		if (!strands.containsKey(oStrand.getStrandName())) {
			strands.put(oStrand.getStrandName(), oStrand);
		}
	}
	
	/**
	 * Return the number of monomers present in Polymer.
	 *
	 * @return number of monomers
	 *
	 */
	public int getMonomerCount() {
		Enumeration keys = strands.keys();
		int number = 0;
		
		while(keys.hasMoreElements())	{
			Strand tmp = (Strand)strands.get(keys.nextElement());	// Cast exception?!
			number += (tmp.getMonomers()).size() - 1;
		}
		return number;
	}
	
	/**
	 * Retrieve a Monomer object by specifying its name. [You have to specify the strand to enable
	 * monomers with the same name in different strands. There is at least one such case: every
	 * strand contains a monomer called "".]
	 *
	 * @param monName  The name of the monomer to look for
	 * @param strandName  The name of the strand to search the monomer in
	 * @return The Monomer object which was asked for
	 *
	 */
	public Monomer getMonomer(String monName, String strandName) {
	    Strand strand = (Strand)strands.get(strandName); 
	    
	    if(strand != null)	{
	        return strand.getMonomer(monName);
	    }
	    else	{
	        return null;
	    } 
	}
	
	/*	Could look like this if you ensured individual name giving for ALL monomers:
	 * 	
	 public Monomer getMonomer(String cName) {
	 Enumeration keys = strands.keys();
	 Monomer oMonomer = null;
	 
	 while(keys.hasMoreElements())	{
	 
	 if(((Strand)strands.get(keys.nextElement())).getMonomers().containsKey(cName))	{
	 Strand oStrand = (Strand)strands.get(keys.nextElement());
	 oMonomer = oStrand.getMonomer(cName);
	 break;
	 }
	 }
	 return oMonomer;
	 }
	 */	
	
	/**
	 * Returns a collection of the names of all <code>Monomer</code>s in this
	 * polymer.
	 *
	 * @return a <code>Collection</code> of all the monomer names.
	 */
	public Collection getMonomerNames() {
		Enumeration keys = strands.keys();
		Hashtable monomers = new Hashtable();
		
		while(keys.hasMoreElements())	{
			Strand oStrand = (Strand)strands.get(keys.nextElement());
			monomers.putAll(oStrand.getMonomers());
		}		
		return monomers.keySet();
	}
	
	/**
	 *
	 * Return the number of strands present in the Polymer.
	 *
	 * @return number of strands
	 *
	 */
	public int getStrandCount() {
		return strands.size() - 1;
	}
	
	/**
	 *
	 * Retrieve a Monomer object by specifying its name.
	 *
	 * @param cName  The name of the monomer to look for
	 * @return The Monomer object which was asked for
	 *
	 */
	public Strand getStrand(String cName) {
		return (Strand)strands.get(cName);
	}
	
	/**
	 * Returns a collection of the names of all <code>Strand</code>s in this
	 * polymer.
	 *
	 * @return a <code>Collection</code> of all the strand names.
	 */
	public Collection getStrandNames() {
		return strands.keySet();
	}
	
	/**
	 * 
	 * Removes a particular monomer, specified by its name.
	 * @param name
	 */
	public void removeStrand(String name)	{
		strands.remove(name);
	}
	
	/**
	 * 
	 * @return hashtable containing the monomers in the strand.
	 */
	public Hashtable getStrands()	{
		return strands;
	}

    public String toString() {
        StringBuffer stringContent = new StringBuffer();
        stringContent.append("Polymer(");
        stringContent.append(this.hashCode()).append(", ");
        stringContent.append("#S:").append(getStrandCount()).append(", ");
        stringContent.append(super.toString());
        stringContent.append(")");
        return stringContent.toString();
    }

}
