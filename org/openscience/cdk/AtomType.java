/*
 * $RCSfile$    $Author$    $Date$    $Revision$
 *
 * Copyright (C) 1997-2001  The Chemistry Development Kit (CDK) project
 *
 * Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
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
 *
 */

package org.openscience.cdk;

/**
 *  The base class for atom types. Atom types are typically used to describe
 *  the behaviour of an atom of a particular element in different environment
 *  like sp3 hybridized carbon C3, etc., in some molecular modelling
 *  applications.
 *
 *@author     steinbeck
 *@created    8. August 2001
 */
public abstract class AtomType 
{
	/** An id for this atom type, like C3 for sp3 carbon */	
	private String atomTypeID;
	
	/** The element symbol identifying the element to which this atom type applies */
	private String elementSymbol;

	/**
	 *  Constructor for the AtomType object
	 */
	public AtomType() {
	}

	/**
	 *  Constructor for the AtomType object
	 *
	 *@param  atomTypeID     An id for this atom type, like C3 for sp3 carbon
	 *@param  elementSymbol  The element symbol identifying the element to which this atom type applies
	 */
	public AtomType(String atomTypeID, String elementSymbol) {
		this.atomTypeID = atomTypeID;
		this.elementSymbol = elementSymbol;
	}


}

