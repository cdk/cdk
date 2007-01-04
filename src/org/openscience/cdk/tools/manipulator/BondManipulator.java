/* $RCSfile$
 * $Author: egonw $ 
 * $Date: 2006-07-31 21:58:20 +0200 (Mon, 31 Jul 2006) $
 * $Revision: 6714 $
 * 
 * Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.tools.manipulator;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;

/**
 * Class with convenience methods that provide methods to manipulate
 * AtomContainer's. For example:
 * <pre>
 * AtomContainerManipulator.replaceAtomByAtom(container, atom1, atom2);
 * </pre>
 * will replace the Atom in the AtomContainer, but in all the ElectronContainer's
 * it participates too.
 *
 * @cdk.module standard
 *
 * @author  Egon Willighagen
 * @cdk.created 2003-08-07
 */
public class BondManipulator {
	
	/**
	 * Constructs an array of Atom objects from Bond.
	 * @param  container The Bond object.
	 * @return The array of Atom objects.
	 */
	public static IAtom[] getAtomArray(IBond container) {
		IAtom[] ret = new IAtom[container.getAtomCount()];
		for (int i = 0; i < ret.length; ++i) ret[i] = container.getAtom(i);
		return ret;
	}
	

}

