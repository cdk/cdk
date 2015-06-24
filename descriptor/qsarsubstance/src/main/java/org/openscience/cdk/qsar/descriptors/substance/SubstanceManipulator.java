/* Copyright (C) 2014-2015  Egon Willighagen <egonw@users.sf.net>
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.qsar.descriptors.substance;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.interfaces.ISubstance;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

public class SubstanceManipulator {

	public static IMolecularFormula getChemicalComposition(ISubstance substance) {
		if (substance.getAtomContainerCount() == 0) return null;
		IMolecularFormula chemicalComposition = substance.getBuilder().newInstance(IMolecularFormula.class);
		for (IAtomContainer container : substance.atomContainers()) {
			chemicalComposition.add(
				MolecularFormulaManipulator.getMolecularFormula(container)
			);
		}
		return chemicalComposition;
	}

}
