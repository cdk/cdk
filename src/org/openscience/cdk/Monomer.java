/* Monomer.java
 * 
 * $ author: 	Edgar Luttmann 			$ 
 * $ contact: 	edgar@uni-paderborn.de 	$
 * $ date: 		2001-08-06 				$
 * 
 * Copyright (C) 1997-2002  The Chemistry Development Kit (CDK) project
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
 *  */
package org.openscience.cdk;

import java.util.Vector;
import org.openscience.cdk.*;

/**
 *
 * A Monomer is an AtomContainer which stores additional monomer specific 
 * informations for a group of Atoms.
 *
 * @author     Edgar Luttmann
 * @created    2001-08-06 
 *
 * @keyword    monomer
 *
 */
public class Monomer extends AtomContainer {
	protected String _cMonomerName;		// the name of this monomer (e.g. Trp42)
	protected String _cMonomerType;		// the type of this monomer (e.g. TRP)
	
	/**
	 *
	 * Contructs a new Monomer.
	 *
	 */	
	public Monomer () {
		super();
	}
	
	/**
	 *
	 * Retrieve the monomer name.
	 *
	 * @return The name of the Monomer object
	 *
	 */
	public String getMonomerName() {
		return _cMonomerName;
	}

	/**
	 *
	 * Retrieve the monomer type.
	 *
	 * @return The type of the Monomer object
	 *
	 */
	public String getMonomerType() {
		return _cMonomerType;
	}
	
	/**
	 *
	 * Set the name of the Monomer object.
	 *
	 * @param cMonomerName  The new name for this monomer
	 *
	 */
	public void setMonomerName(String cMonomerName) {
		_cMonomerName = cMonomerName;
	}
	
	/**
	 *
	 * Set the type of the Monomer object.
	 *
	 * @param cMonomerType  The new type for this monomer
	 *
	 */
	public void setMonomerType(String cMonomerType) {
		_cMonomerType = cMonomerType;
	}
	
	/**
	 *
	 * Add an Atom object to this monomer.
	 *
	 * @param oAtom  The Atom to be added.
	 *
	 */
	public void addAtom(Atom oAtom) {
		if (! contains(oAtom)) {
			super.addAtom(oAtom);
		}
	}
}
