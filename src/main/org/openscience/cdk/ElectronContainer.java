/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
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
 * 
 */

package org.openscience.cdk;

import java.io.Serializable;

import org.openscience.cdk.interfaces.IElectronContainer;

/**
 * Base class for entities containing electrons, like bonds, orbitals, lone-pairs.
 *
 * @cdk.module data
 * @cdk.svnrev  $Revision$
 *
 * @cdk.keyword orbital
 * @cdk.keyword lone-pair
 * @cdk.keyword bond
 */
public class ElectronContainer extends ChemObject implements Serializable, IElectronContainer, Cloneable 
{
	/**
     * Determines if a de-serialized object is compatible with this class.
     *
     * This value must only be changed if and only if the new version
     * of this class is incompatible with the old version. See Sun docs
     * for <a href=http://java.sun.com/products/jdk/1.1/docs/guide
     * /serialization/spec/version.doc.html>details</a>.
	 */
	private static final long serialVersionUID = -2207894536767670743L;

	/** Number of electrons in the ElectronContainer. */
	protected Integer electronCount;

    /**
     * Constructs an empty ElectronContainer.
     */
    public ElectronContainer() {
        electronCount = 0;
    }
    
	/**
	 * Returns the number of electrons in this electron container.
	 *
	 * @return The number of electrons in this electron container.
     *
     * @see     #setElectronCount
	 */
	public Integer getElectronCount()
	{
		return this.electronCount;
	}


	/**
	 * Sets the number of electrons in this electron container.
	 *
	 * @param   electronCount The number of electrons in this electron container.
     *
     * @see     #getElectronCount
	 */
	public void setElectronCount(Integer electronCount)
	{
		this.electronCount = electronCount;
		notifyChanged();
	}

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String toString() {
        StringBuffer resultString = new StringBuffer(64);
        resultString.append("ElectronContainer(").append(hashCode());
        if (getElectronCount() > -1) {
        	resultString.append("EC:").append(getElectronCount());
        }
        resultString.append(')');
        return resultString.toString(); 
    }

}


