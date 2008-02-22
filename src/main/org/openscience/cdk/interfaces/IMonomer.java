/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2006-2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.interfaces;

/**
 * A Monomer is an AtomContainer which stores additional monomer specific 
 * informations for a group of Atoms.
 *
 * @cdk.module  interfaces
 * @cdk.svnrev  $Revision$
 *
 * @author      Edgar Luttmann <edgar@uni-paderborn.de>
 * @cdk.created 2001-08-06 
 *
 * @cdk.keyword monomer
 */
public interface IMonomer extends IAtomContainer {

	/**
	 * Retrieve the monomer name.
	 *
	 * @return The name of the Monomer object
     * @see    #setMonomerName
	 */
	public String getMonomerName();

	/**
	 * Retrieve the monomer type.
	 *
	 * @return The type of the Monomer object
     * @see    #setMonomerType
	 */
	public String getMonomerType();
	
	/**
	 * Set the name of the Monomer object.
	 *
	 * @param cMonomerName  The new name for this monomer
     * @see    #getMonomerName
	 */
	public void setMonomerName(String cMonomerName);
	
	/**
	 * Set the type of the Monomer object.
	 *
	 * @param cMonomerType  The new type for this monomer
     * @see    #getMonomerType
	 */
	public void setMonomerType(String cMonomerType);
}
