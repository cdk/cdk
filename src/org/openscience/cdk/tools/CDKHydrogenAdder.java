/* $Revision: 8399 $ $Author: rajarshi $ $Date: 2007-06-24 06:34:35 +0200 (Sun, 24 Jun 2007) $
 *  
 * Copyright (C) 2007  Egon Willighagen
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
 */
package org.openscience.cdk.tools;

import java.util.Iterator;
import java.util.Map;
import java.util.Hashtable;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

/**
 * Assumes CDK atom types to be detected and adds missing hydrogens based on the
 * atom typing.
 * 
 * @author     egonw
 * @cdk.module valencycheck
 */
public class CDKHydrogenAdder {

    private AtomTypeFactory atomTypeList;
    private final static String ATOM_TYPE_LIST = "org/openscience/cdk/config/data/cdk_atomtypes.xml";

    private static Map<String,CDKHydrogenAdder> tables = new Hashtable<String,CDKHydrogenAdder>(3);

    private CDKHydrogenAdder(IChemObjectBuilder builder) {
        if (atomTypeList == null)
            atomTypeList = AtomTypeFactory.getInstance(ATOM_TYPE_LIST, builder);
    }

    public static CDKHydrogenAdder getInstance(IChemObjectBuilder builder) {
        if (!tables.containsKey(builder.getClass().getName()))
            tables.put(builder.getClass().getName(), new CDKHydrogenAdder(builder));
        return tables.get(builder.getClass().getName());
    }


	/**
	 * Sets implicit hydrogen counts for all atoms in the given IAtomContainer.
	 * 
	 * @param  container
	 * @throws CDKException Throws if insufficient information is present
	 */
	public void addImplicitHydrogens(IAtomContainer container) throws CDKException {
		Iterator<IAtom> atoms = container.atoms();
		while (atoms.hasNext()) {
			addImplicitHydrogens(container, atoms.next());
		}
	}
	
	/**
	 * Sets the implicit hydrogen count for the indicated IAtom in the given IAtomContainer.
	 * 
	 * @param  container
	 * @param  atom         IAtom to set the implicit hydrogen count for
	 * @throws CDKException Throws if insufficient information is present
	 */
	public void addImplicitHydrogens(IAtomContainer container, IAtom atom) throws CDKException {
		if (atom.getAtomTypeName() == null)
			throw new CDKException("IAtom is not typed! " + atom.getSymbol());
		
		IAtomType type =  atomTypeList.getAtomType(atom.getAtomTypeName());
		if (type == null)
			throw new CDKException("Atom type is not a recognized CDK atom type: " + atom.getAtomTypeName());
		
		if (type.getFormalNeighbourCount() == CDKConstants.UNSET)
			throw new CDKException("Atom type is too general; cannot decide the number of implicit hydrogen to add for: " + atom.getAtomTypeName());
		
		// very simply counting: each missing explicit neighbor is a missing hydrogen
		atom.setHydrogenCount(
			type.getFormalNeighbourCount() - container.getConnectedAtomsCount(atom)
		);
	}
}
