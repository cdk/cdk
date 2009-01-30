/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2005-2007  Egon Willighagen <egonw@users.sf.net>
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
 */
package org.openscience.cdk.debug;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomParity;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Debugging data class.
 * 
 * @author     egonw
 * @cdk.module datadebug
 * @cdk.svnrev  $Revision$
 */
public class DebugAtomParity extends org.openscience.cdk.AtomParity
    implements IAtomParity {

    private static final long serialVersionUID = 6305428844566539948L;

    public DebugAtomParity(IAtom centralAtom, IAtom first, IAtom second, IAtom third, IAtom fourth, int parity) {
		super(centralAtom, first, second, third, fourth, parity);
	}

	LoggingTool logger = new LoggingTool(DebugAtomParity.class);

	public IAtom getAtom() {
		logger.debug("Getting atom: ", super.getAtom());
		return super.getAtom();
	}

	public IAtom[] getSurroundingAtoms() {
		logger.debug("Getting surrounding atoms: ", super.getSurroundingAtoms().length);
		return super.getSurroundingAtoms();
	}

	public int getParity() {
		logger.debug("Getting atom parity: ", super.getParity());
		return super.getParity();
	}

}
