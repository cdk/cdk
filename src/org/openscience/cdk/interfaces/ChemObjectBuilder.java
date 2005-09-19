/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2005  The Chemistry Development Kit (CDK) project
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
 */
package org.openscience.cdk.interfaces;

/**
 * A helper class to instantiate a ChemObject for a specific implementation.
 *
 * @author        egonw
 * @cdk.module    interfaces
 */
public interface ChemObjectBuilder {

    /**
     * Constructs an completely unset Atom.
     */
	public Atom newAtom();
	
    /**
     * Constructs an Atom from a String containing an element symbol.
     *
     * @param   elementSymbol  The String describing the element for the Atom
     */
    public Atom newAtom(String elementSymbol);
    
    /**
     * Constructs an Atom from an Element and a Point2d.
     *
     * @param   elementSymbol   The Element
     * @param   point2d         The Point
     */
    public Atom newAtom(String elementSymbol, javax.vecmath.Point2d point2d);

    /**
     * Constructs an Atom from an Element and a Point3d.
     *
     * @param   elementSymbol   The symbol of the atom
     * @param   point3d         The 3D coordinates of the atom
     */
    public Atom newAtom(String elementSymbol, javax.vecmath.Point3d point3d);
		
}


