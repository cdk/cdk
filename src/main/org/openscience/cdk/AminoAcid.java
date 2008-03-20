/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2005-2007  Egon Willighagen <e.willighagen@science.ru.nl>
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

import org.openscience.cdk.interfaces.IAminoAcid;
import org.openscience.cdk.interfaces.IAtom;

/**
 * A AminoAcid is Monomer which stores additional amino acid specific 
 * informations, like the N-terminus atom.
 *
 * @cdk.module data
 * @cdk.svnrev  $Revision$
 *
 * @author      Egon Willighagen <e.willighagen@science.ru.nl>
 * @cdk.created 2005-08-11
 * @cdk.keyword amino acid
 */
public class AminoAcid extends Monomer implements IAminoAcid, Serializable, Cloneable {

    /**
     * Determines if a de-serialized object is compatible with this class.
     *
     * This value must only be changed if and only if the new version
     * of this class is incompatible with the old version. See Sun docs
     * for <a href=http://java.sun.com/products/jdk/1.1/docs/guide
     * /serialization/spec/version.doc.html>details</a>.
	 */
	private static final long serialVersionUID = -5032283549467862509L;
	
	/** The atom that constitutes the N-terminus. */
    private IAtom nTerminus;
    /** The atom that constitutes the C-terminus. */
    private IAtom cTerminus;

    /**
     * Constructs a new AminoAcid.
     */
    public AminoAcid() {
        super();
    }
    
    /**
     * Retrieves the N-terminus atom.
     *
     * @return The Atom that is the N-terminus
     *
     * @see    #addNTerminus(IAtom)
     */
    public IAtom getNTerminus() {
        return nTerminus;
    }

    /**
     * Add an Atom and makes it the N-terminus atom.
     *
     * @param atom  The Atom that is the N-terminus
     *
     * @see    #getNTerminus
     */
    public void addNTerminus(IAtom atom) {
        super.addAtom(atom);
        nTerminus = atom;
    }
    
    /**
     * Marks an Atom as being the N-terminus atom. It assumes that the Atom
     * is already added to the AminoAcid.
     *
     * @param atom  The Atom that is the N-terminus
     *
     * @see    #addNTerminus
     */
    private void setNTerminus(IAtom atom) {
        nTerminus = atom;
    }

    /**
     * Retrieves the C-terminus atom.
     *
     * @return The Atom that is the C-terminus
     *
     * @see    #addCTerminus(IAtom)
     */
    public IAtom getCTerminus() {
        return cTerminus;
    }

    /**
     * Add an Atom and makes it the C-terminus atom.
     *
     * @param atom  The Atom that is the C-terminus
     *
     * @see    #getCTerminus
     */
    public void addCTerminus(IAtom atom) {
        super.addAtom(atom);
        setCTerminus(atom);
    }

    /**
     * Marks an Atom as being the C-terminus atom. It assumes that the Atom
     * is already added to the AminoAcid.
     *
     * @param atom  The Atom that is the C-terminus
     *
     * @see    #addCTerminus
     */
    private void setCTerminus(IAtom atom) {
        cTerminus = atom;
    }

    /**
     * Clones this AminoAcid object.
     *
     * @return    The cloned object
     */
    public Object clone() throws CloneNotSupportedException {
        AminoAcid clone = (AminoAcid) super.clone();
        // copying the new N-terminus and C-terminus pointers
        if (getNTerminus() != null)
        	clone.setNTerminus(clone.getAtom(getAtomNumber(getNTerminus())));
        if (getCTerminus() != null)
        	clone.setCTerminus(clone.getAtom(getAtomNumber(getCTerminus())));
        return clone;
    }
    
    public String toString() {
        StringBuffer stringContent = new StringBuffer(32);
        stringContent.append("AminoAcid(");
        stringContent.append(this.hashCode());
        if (nTerminus != null) {
        	stringContent.append(", N:").append(nTerminus.toString());
        }
        if (cTerminus != null) {
        	stringContent.append(", C:").append(cTerminus.toString());
        }
        stringContent.append(", ").append(super.toString());
        stringContent.append(')');
        return stringContent.toString();
    }

}
