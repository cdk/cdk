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
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.tools.LoggingTool;

/**
 *  Class with utilities for the atomtype package (example: assign an
 *  hybridization state to an atom given an atom type).
 *
 *@author        mfe4
 *@author        egonw
 *@cdk.module standard
 */
public class AtomTypeManipulator {
	
	/**
	 *  Method that assign properties to an atom given a particular atomType.
	 *
	 *@param  atom  Atom
	 *@param  at    AtomType matched with atomtype.HybridizationStateATMatcher
	 */
	public static void configure(Atom atom, AtomType at) throws CDKException {
		AtomTypeFactory factory = null;
		LoggingTool logger = null;
		int hybr;
		try {
			factory = AtomTypeFactory.getInstance("org/openscience/cdk/config/data/hybridization_atomtypes.xml");
			hybr = at.getHybridization();
			atom.setHybridization(hybr);
		} catch (Exception ex1) {
			logger.error(ex1.getMessage());
			logger.debug(ex1);
			throw new CDKException("Problems with AtomTypeFactory due to " + ex1.toString());
		}
	}
}

