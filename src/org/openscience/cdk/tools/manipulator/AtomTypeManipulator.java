/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2003-2005  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.tools.manipulator;

import org.openscience.cdk.atomtype.*;
import org.openscience.cdk.AtomType;
import org.openscience.cdk.Atom;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Class with utilities for the <code>AtomType</code> class.
 *
 * @author     mfe4
 * @author     egonw
 * @cdk.module standard
 */
public class AtomTypeManipulator {
	
	/**
	 * Method that assign properties to an atom given a particular atomType.
	 *
	 * @param  atom  Atom to configure
	 * @param  at    AtomType
	 */
	public static void configure(Atom atom, AtomType atomType) {
        atom.setMaxBondOrder(atomType.getMaxBondOrder());
        atom.setBondOrderSum(atomType.getBondOrderSum());
        atom.setVanderwaalsRadius(atomType.getVanderwaalsRadius());
        atom.setCovalentRadius(atomType.getCovalentRadius());
        atom.setFormalCharge(atomType.getFormalCharge());
	atom.setHybridization(atomType.getHybridization());
        atom.setFormalNeighbourCount(atomType.getFormalNeighbourCount());
	}
}

