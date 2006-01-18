/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2005  The Chemistry Development Kit (CDK) project
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

import org.openscience.cdk.interfaces.IChemObject;

/**
 * A Mapping is an relation between two ChemObjects in a non-chemical
 * entity. It is not a Bond, nor a Association, merely a relation.
 * An example of such a mapping, is the mapping between corresponding atoms
 * in a Reaction.
 *
 * @cdk.module data
 *
 * @cdk.keyword reaction, atom mapping
 *
 * @author  Egon Willighagen
 * @cdk.created 2003-08-16
 */
public class Mapping extends ChemObject implements java.io.Serializable, Cloneable {

    /**
     * Determines if a de-serialized object is compatible with this class.
     *
     * This value must only be changed if and only if the new version
     * of this class is imcompatible with the old version. See Sun docs
     * for <a href=http://java.sun.com/products/jdk/1.1/docs/guide
     * /serialization/spec/version.doc.html>details</a>.
	 */
	private static final long serialVersionUID = -6541915644492043503L;

	private IChemObject[] relation;
    
    /**
     * Constructs an unconnected lone pair.
     *
     * @param objectOne The first IChemObject of the mapping
     * @param objectTwo The second IChemObject of the mapping
     */
    public Mapping(IChemObject objectOne, IChemObject objectTwo) {
        relation = new ChemObject[2];
        relation[0] = objectOne;
        relation[1] = objectTwo;
    }

    /**
     * Returns an array of the two IChemObject's.
     *
     * @return An array of two IChemObject's that define the mapping
     */
    public ChemObject[] getRelatedChemObjects() {
        return (ChemObject[])relation;
    }

	/**
	 * Clones this <code>Mapoing</code> and the mapped <code>IChemObject</code>s.
	 *
	 * @return  The cloned object
	 */
	public Object clone() {
		Mapping clone = (Mapping)super.clone();
        // clone the related IChemObject's
        if (relation != null) {
		    ((Mapping)clone).relation = new ChemObject[relation.length];
            for (int f = 0; f < relation.length; f++) {
                if (relation[f] != null) {
                    ((Mapping)clone).relation[f] = (ChemObject)relation[f].clone();
                }
            }
        }
		return clone;
	}
}


