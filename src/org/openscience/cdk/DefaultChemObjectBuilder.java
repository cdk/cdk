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
package org.openscience.cdk;

import org.openscience.cdk.interfaces.ChemObjectBuilder;

/**
 * A helper class to instantiate a ChemObject for a specific implementation.
 *
 * @author        egonw
 * @cdk.module    interfaces
 */
public class DefaultChemObjectBuilder implements ChemObjectBuilder {

	private static DefaultChemObjectBuilder instance = null;
	
	private DefaultChemObjectBuilder() {}

	public static DefaultChemObjectBuilder getInstance() {
		if (instance == null) {
			instance = new DefaultChemObjectBuilder();
		}
		return instance;
	}
	
	public org.openscience.cdk.interfaces.Atom newAtom() {
		return new Atom();
	}
	
    public org.openscience.cdk.interfaces.Atom newAtom(String elementSymbol) {
    	return new Atom(elementSymbol);
    }
    
    public org.openscience.cdk.interfaces.Atom newAtom(String elementSymbol, javax.vecmath.Point2d point2d) {
    	return new Atom(elementSymbol, point2d);
    }

    public org.openscience.cdk.interfaces.Atom newAtom(String elementSymbol, javax.vecmath.Point3d point3d) {
    	return new Atom(elementSymbol, point3d);
    }
		
}


