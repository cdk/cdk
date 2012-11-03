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
 *
 */
package org.openscience.cdk.nonotify;

import org.openscience.cdk.AtomParity;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomParity;
import org.openscience.cdk.interfaces.IBond;

import java.util.Map;

/**
 * @cdk.module nonotify
 * @cdk.githash
 * @deprecated    Use the {@link org.openscience.cdk.silent.AtomParity} instead.
 */
public class NNAtomParity extends AtomParity  {
    
	private static final long serialVersionUID = -1361754858958386722L;

	public NNAtomParity(
    		IAtom centralAtom, 
    		IAtom first, 
    		IAtom second, 
    		IAtom third, 
    		IAtom fourth,
    		int parity) {
    	super(centralAtom, first, second, third, fourth, parity);
    }

    /**
     * @inheritDoc
     */
    @TestMethod("testMap_Map_Map,testMap_Null_Map,testMap_Map_Map_NullElement,testMap_Map_Map_EmptyMapping")
    @Override
    public IAtomParity map(Map<IAtom, IAtom> atoms, Map<IBond, IBond> bonds) {

        if(atoms == null) // not using bond mapping
            throw new IllegalArgumentException("null atom mapping provided");

        IAtom[] neighbors = getSurroundingAtoms();

        // could map neighbours with a for loop but we need to pull individuals
        // atoms for the constructor
        return new NNAtomParity(
                getAtom()  != null ? atoms.get(getAtom()) : null,
                neighbors[0] != null ? atoms.get(neighbors[0]) : null,
                neighbors[1] != null ? atoms.get(neighbors[1]) : null,
                neighbors[2] != null ? atoms.get(neighbors[2]) : null,
                neighbors[3] != null ? atoms.get(neighbors[3]) : null,
                getParity()
        );

    }
    
}





