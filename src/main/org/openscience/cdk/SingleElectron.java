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
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA. 
 * 
 */
package org.openscience.cdk;

import java.io.Serializable;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.ISingleElectron;

/**
 * A Single Electron is an orbital which is occupied by only one electron.
 * A radical in CDK is represented by an AtomContainer that contains an Atom
 * and a SingleElectron type ElectronContainer:
 * <pre>
 *   AtomContainer radical = new AtomContainer();
 *   Atom carbon = new Atom("C");
 *   carbon.setImplicitHydrogens(3);
 *   radical.addElectronContainer(new SingleElectron(carbon));
 * </pre> 
 *
 * @cdk.module data
 * @cdk.svnrev  $Revision$
 *
 * @cdk.keyword radical
 * @cdk.keyword electron, unpaired
 */
public class SingleElectron extends ElectronContainer implements Serializable, ISingleElectron, Cloneable
{

    /**
     * Determines if a de-serialized object is compatible with this class.
     *
     * This value must only be changed if and only if the new version
     * of this class is incompatible with the old version. See Sun docs
     * for <a href=http://java.sun.com/products/jdk/1.1/docs/guide
     * /serialization/spec/version.doc.html>details</a>.
	 */
	private static final long serialVersionUID = 7796574734668490940L;

	/** Number of electron for this class is defined as one. */
    protected final int electronCount = 1;

    /** The atom with which this single electron is associated. */
    protected IAtom atom;

    /**
     * Constructs an single electron orbital on an Atom.
     *
     * @param atom The atom to which the single electron belongs.
     */
    public SingleElectron(IAtom atom) {
        this.atom = atom;
    }

    /**
     * Constructs an single electron orbital with an associated Atom.
     */
    public SingleElectron() {
        this.atom = null;
    }
    /**
     * Returns the number of electrons in this SingleElectron.
     *
     * @return The number of electrons in this SingleElectron.
     */
    public Integer getElectronCount() {
        return this.electronCount;
    }

    /**
     * Returns the associated Atom.
     *
     * @return the associated Atom.
     *
     * @see    #setAtom
	 */
	public IAtom getAtom() {
		return (Atom)this.atom;
	}

	/**
	 * Sets the associated Atom.
	 *
	 * @param atom the Atom this SingleElectron will be associated with
     *
     * @see    #getAtom
	 */
	public void setAtom(IAtom atom) {
		this.atom = atom;
		notifyChanged();
	}

    /**
     * Returns true if the given atom participates in this SingleElectron.
     *
     * @param   atom  The atom to be tested if it participates in this bond
     * @return     true if this SingleElectron is associated with the atom
     */
    public boolean contains(IAtom atom)     {
        return (this.atom == atom) ? true : false;
    }

    /**
     * Returns a one line string representation of this SingleElectron.
     * This method is conform RFC #9.
     *
     * @return    The string representation of this SingleElectron
     */
    public String toString() {
        StringBuffer stringContent = new StringBuffer();
        stringContent.append("SingleElectron(");
        stringContent.append(this.hashCode());
        if (atom != null) {
            stringContent.append(", ");
            stringContent.append(atom.toString());
        }
        stringContent.append(')');
        return stringContent.toString();
    }

	/**
	 * Clones this SingleElectron object, including a clone of the atom for which the
     * SingleElectron is defined.
	 *
	 * @return    The cloned object
	 */
	public Object clone() throws CloneNotSupportedException {
		SingleElectron clone = (SingleElectron) super.clone();
        // clone the Atom
        if (atom != null) {
		    clone.atom = (IAtom)((IAtom)atom).clone(); 
        }
		return clone;
	}

}


