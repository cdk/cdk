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

import org.openscience.cdk.AtomParity;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomParity;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * Debugging data class.
 * 
 * @author     egonw
 * @cdk.module datadebug
 * @cdk.githash
 */
public class DebugAtomParity extends AtomParity
    implements IAtomParity {


    private static final long serialVersionUID = 6305428844566539948L;

    public DebugAtomParity(IAtom centralAtom, IAtom first, IAtom second, IAtom third, IAtom fourth, int parity) {
		super(centralAtom, first, second, third, fourth, parity);
	}

	ILoggingTool logger =
        LoggingToolFactory.createLoggingTool(DebugAtomParity.class);

    /** {@inheritDoc}} */ @Override
	public IAtom getAtom() {
		logger.debug("Getting atom: ", super.getAtom());
		return super.getAtom();
	}

    /** {@inheritDoc}} */ @Override
	public IAtom[] getSurroundingAtoms() {
		logger.debug("Getting surrounding atoms: ", super.getSurroundingAtoms().length);
		return super.getSurroundingAtoms();
	}

    /**
     * @inheritDoc
     */
    @TestMethod("testMap_Map_Map,testMap_Null_Map,testMap_Map_Map_NullElement,testMap_Map_Map_EmptyMapping")
    @Override
    public IAtomParity map(Map<IAtom, IAtom> atoms, Map<IBond, IBond> bonds) {

        logger.debug("Mapping atom parity: " + atoms);

        if(atoms == null) // not using bond mapping
            throw new IllegalArgumentException("null atom mapping provided");

        IAtom[] neighbors = getSurroundingAtoms();

        // could map neighbours with a for loop but we need to pull individuals
        // atoms for the constructor
        return new DebugAtomParity(
                getAtom()  != null ? atoms.get(getAtom()) : null,
                neighbors[0] != null ? atoms.get(neighbors[0]) : null,
                neighbors[1] != null ? atoms.get(neighbors[1]) : null,
                neighbors[2] != null ? atoms.get(neighbors[2]) : null,
                neighbors[3] != null ? atoms.get(neighbors[3]) : null,
                getParity()
        );

    }

    /** {@inheritDoc}} */ @Override
    public int getParity() {
		logger.debug("Getting atom parity: ", super.getParity());
		return super.getParity();
	}

}
