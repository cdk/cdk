/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2001-2007  Edgar Luttmann <edgar@uni-paderborn.de>
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

import java.io.Serializable;

import org.openscience.cdk.interfaces.IMonomer;

/**
 * A Monomer is an AtomContainer which stores additional monomer specific 
 * informations for a group of Atoms.
 *
 * @cdk.module data
 * @cdk.svnrev  $Revision$
 *
 * @author     Edgar Luttmann <edgar@uni-paderborn.de>
 * @cdk.created    2001-08-06 
 *
 * @cdk.keyword    monomer
 *
 */
public class Monomer extends AtomContainer implements Serializable, IMonomer, Cloneable
{

    /**
     * Determines if a de-serialized object is compatible with this class.
     *
     * This value must only be changed if and only if the new version
     * of this class is incompatible with the old version. See Sun docs
     * for <a href=http://java.sun.com/products/jdk/1.1/docs/guide
     * /serialization/spec/version.doc.html>details</a>.
	 */
	private static final long serialVersionUID = -6084164963937650703L;

	/** The name of this monomer (e.g. Trp42). */
    private String monomerName;
    /** The type of this monomer (e.g. TRP). */
    private String monomerType;

	/**
	 *
	 * Constructs a new Monomer.
	 *
	 */	
	public Monomer () {
		super();
	}
	
	/**
	 *
	 * Retrieves the monomer name.
	 *
	 * @return The name of the Monomer object
	 *
     * @see    #setMonomerName
	 */
	public String getMonomerName() {
		return monomerName;
	}

	/**
	 *
	 * Retrieves the monomer type.
	 *
	 * @return The type of the Monomer object
	 *
     * @see    #setMonomerType
	 */
	public String getMonomerType() {
		return monomerType;
	}
	
	/**
	 *
	 * Sets the name of the Monomer object.
	 *
	 * @param cMonomerName  The new name for this monomer
	 *
     * @see    #getMonomerName
	 */
	public void setMonomerName(String cMonomerName) {
		monomerName = cMonomerName;
		notifyChanged();
	}
	
	/**
	 *
	 * Sets the type of the Monomer object.
	 *
	 * @param cMonomerType  The new type for this monomer
	 *
     * @see    #getMonomerType
	 */
	public void setMonomerType(String cMonomerType) {
		monomerType = cMonomerType;
		notifyChanged();
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Monomer{").append(this.hashCode());
		if (getMonomerName() != null) {
			buffer.append(", N=").append(getMonomerName());
		}
		if (getMonomerType() != null) {
			buffer.append(", T=").append(getMonomerType());
		}
		buffer.append("}");
		return buffer.toString();
	}
	
}
