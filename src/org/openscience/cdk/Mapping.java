/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2004  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
 * 
 */
package org.openscience.cdk;

/**
 * A Mapping is an relation between two ChemObjects in a non-chemical
 * entity. It is not a Bond, nor a Association, merely a relation.
 * An example of such a mapping, is the mapping between corresponding atoms
 * in a Reaction.
 *
 * @cdk.module core
 *
 * @cdk.keyword reaction, atom mapping
 *
 * @author  Egon Willighagen
 * @cdk.created 2003-08-16
 */
public class Mapping extends ChemObject implements java.io.Serializable, Cloneable {

    private ChemObject[] relation;
    
    /**
     * Constructs an unconnected lone pair.
     *
     * @param objectOne The first ChemObject of the mapping
     * @param objectTwo The second ChemObject of the mapping
     */
    public Mapping(ChemObject objectOne, ChemObject objectTwo) {
        relation = new ChemObject[2];
        relation[0] = objectOne;
        relation[1] = objectTwo;
    }

    /**
     * Returns an array of the two ChemObject's.
     *
     * @return An array of two ChemObject's that define the mapping
     */
    public ChemObject[] getRelatedChemObjects() {
        return relation;
    }

}


