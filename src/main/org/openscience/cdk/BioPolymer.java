/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2007  Edgar Luttmann <edgar@uni-paderborn.de>
 * 
 *  Contact: cdk-devel@lists.sourceforge.net
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

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBioPolymer;
import org.openscience.cdk.interfaces.IMonomer;
import org.openscience.cdk.interfaces.IStrand;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

/**
 * A BioPolymer is a subclass of a Polymer which is supposed to store
 * additional informations about the Polymer which are connected to BioPolymers.
 *
 * @cdk.module  data
 * @cdk.svnrev  $Revision$
 *
 * @author      Edgar Luttmann <edgar@uni-paderborn.de>
 * @author      Martin Eklund
 * @cdk.created 2001-08-06 
 *
 * @cdk.keyword polymer
 * @cdk.keyword biopolymer
 */
public class BioPolymer extends Polymer implements java.io.Serializable, IBioPolymer
{

	/**
     * Determines if a de-serialized object is compatible with this class.
     *
     * This value must only be changed if and only if the new version
     * of this class is incompatible with the old version. See Sun docs
     * for <a href="http://java.sun.com/products/jdk/1.1/docs/guide/serialization/spec/version.doc.html">details</a>.
	 */
	private static final long serialVersionUID = -5001873073769634393L;

	private Map<String, IStrand> strands;	// the list of all the contained Strands.
	
	/**
	 * Constructs a new Polymer to store the Strands.
	 */	
	public BioPolymer() {
		super();
		// Strand stuff
		strands = new Hashtable<String, IStrand>();
	}
	
	/**
	 * Adds the atom oAtom to a specified Strand, whereas the Monomer is unspecified. Hence
	 * the atom will be added to a Monomer of type UNKNOWN in the specified Strand.
	 *
	 * @param oAtom   The atom to add
	 * @param oStrand The strand the atom belongs to
	 */
	public void addAtom(IAtom oAtom, IStrand oStrand) {
		
		int atomCount = super.getAtomCount();
		
		// Add atom to AtomContainer
		super.addAtom(oAtom);

		if (atomCount != super.getAtomCount() && 
		    oStrand != null) {	// Maybe better to throw null pointer exception here, so user realises that
								// Strand == null and Atom only gets added to this BioPolymer, but not to a Strand.
			oStrand.addAtom(oAtom);	
			if (!strands.containsKey(oStrand.getStrandName())) {
				strands.put(oStrand.getStrandName(), oStrand);
			}
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
	public void addAtom(IAtom oAtom, IMonomer oMonomer, IStrand oStrand)	{
		
		int atomCount = super.getAtomCount();
		
		// Add atom to AtomContainer
		super.addAtom(oAtom);

		if(atomCount != super.getAtomCount() && // OK, super did not yet contain the atom
			   // Add atom to Strand (also adds the atom to the monomer).
		       oStrand != null)	{
			oStrand.addAtom(oAtom, oMonomer);	// Same problem as above: better to throw nullpointer exception?
			if (!strands.containsKey(oStrand.getStrandName())) {
				strands.put(oStrand.getStrandName(), oStrand);
			}
		}
		/* The reasoning above is: 
		 * All Monomers have to belong to a Strand and all atoms belonging to strands have to belong to a Monomer =>
		 * ? oMonomer != null and oStrand != null, oAtom is added to BioPolymer and to oMonomer in oStrand
		 * ? oMonomer == null and oStrand != null, oAtom is added to BioPolymer and default Monomer in oStrand
		 * ? oMonomer != null and oStrand == null, oAtom is added to BioPolymer, but not to a Monomer or Strand (especially good to maybe throw exception in this case)
		 * ? oMonomer == null and oStrand == null, oAtom is added to BioPolymer, but not to a Monomer or Strand
		 * */
	}
	
	/**
	 * Returns the number of monomers present in BioPolymer.
	 *
	 * @return number of monomers
	 */
	public int getMonomerCount() {
		Iterator<String> keys = strands.keySet().iterator();
		int number = 0;
		
		while(keys.hasNext())	{
			Strand tmp = (Strand)strands.get(keys.next());	// Cast exception?!
			number += (tmp.getMonomers()).size() - 1;
		}
		return number;
	}
	
	/**
	 * Retrieves a Monomer object by specifying its name. [You have to specify the strand to enable
	 * monomers with the same name in different strands. There is at least one such case: every
	 * strand contains a monomer called "".]
	 *
	 * @param monName  The name of the monomer to look for
	 * @return The Monomer object which was asked for
	 *
	 */
	public IMonomer getMonomer(String monName, String strandName) {
	    Strand strand = (Strand)strands.get(strandName); 
	    
	    if(strand != null)	{
	        return (Monomer)strand.getMonomer(monName);
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
	 * BioPolymer.
	 *
	 * @return a <code>Collection</code> of all the monomer names.
	 */
	public Collection<String> getMonomerNames() {
		Iterator<String> keys = strands.keySet().iterator();
		Map<String, IMonomer> monomers = new Hashtable<String, IMonomer>();
		
		while(keys.hasNext())	{
			Strand oStrand = (Strand)strands.get(keys.next());
			monomers.putAll(oStrand.getMonomers());
		}		
		return monomers.keySet();
	}
	
	/**
	 *
	 * Returns the number of strands present in the BioPolymer.
	 *
	 * @return number of strands
	 *
	 */
	public int getStrandCount() {
		return strands.size();
	}
	
	/**
	 *
	 * Retrieves a Monomer object by specifying its name.
	 *
	 * @param cName  The name of the monomer to look for
	 * @return The Monomer object which was asked for
	 *
	 */
	public IStrand getStrand(String cName) {
		return (Strand)strands.get(cName);
	}
	
	/**
	 * Returns a collection of the names of all <code>Strand</code>s in this
	 * BioPolymer.
	 *
	 * @return a <code>Collection</code> of all the strand names.
	 */
	public Collection<String> getStrandNames() {
		return strands.keySet();
	}
	
	/**
	 * Removes a particular strand, specified by its name.
	 * 
	 * @param name name of the strand to remove
	 */
	public void removeStrand(String name)	{
		if (strands.containsKey(name))	{
			Strand strand = (Strand)strands.get(name);
			this.remove(strand);
			strands.remove(name);
		}
	}
	
	/**
	 * @return hashtable containing the monomers in the strand.
	 */
	public Map<String, IStrand> getStrands()	{
		return strands;
	}
	
	public String toString() {
        StringBuffer stringContent = new StringBuffer();
        stringContent.append("BioPolymer(");
        stringContent.append(this.hashCode()).append(", ");
        stringContent.append(super.toString());
        stringContent.append(')');
        return stringContent.toString();
    }

    public Object clone() throws CloneNotSupportedException {
    	BioPolymer clone = (BioPolymer)super.clone();
        clone.strands.clear();
        for (Iterator<String> strands = clone.getStrandNames().iterator(); strands.hasNext();) {
            Strand strand = (Strand)clone.getStrand(strands.next().toString()).clone();
            for (Iterator<String> iter = strand.getMonomerNames().iterator(); iter.hasNext();) {
            	IMonomer monomer = strand.getMonomer(iter.next().toString());
            	Iterator<IAtom> atoms = monomer.atoms().iterator();
            	while (atoms.hasNext()) {
                    clone.addAtom((IAtom)atoms.next(), monomer, strand);
                } 
            }
        }
        return clone;
    }
}
