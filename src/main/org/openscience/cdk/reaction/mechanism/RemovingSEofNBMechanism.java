/* $Revision$ $Author$ $Date$
 * 
 * Copyright (C) 2008  Miguel Rojas <miguelrojasch@yahoo.es>
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
package org.openscience.cdk.reaction.mechanism;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.SingleElectron;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.ILonePair;
import org.openscience.cdk.interfaces.IMapping;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.reaction.IReactionMechanism;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * This mechanism extracts a single electron from a non-bonding orbital which located in
 * a ILonePair container. It returns the reaction mechanism which has been cloned the
 * IMolecule with an ILonPair electron less and an ISingleElectron more.
 * 
 * @author         miguelrojasch
 * @cdk.created    2008-02-10
 * @cdk.module     reaction
 *
 */
public class RemovingSEofNBMechanism implements IReactionMechanism{

	/** 
     * Initiates the process for the given mechanism. The atoms to apply are mapped between
     * reactants and products.
     *
     * @param moleculeSet The IMolecule to apply the mechanism
     * @param atomList    The list of atoms taking part in the mechanism. Only allowed one atom
     * @param bondList    The list of bonds taking part in the mechanism. Only allowed one Bond
     * @return            The Reaction mechanism
     * 
	 */
	public IReaction initiate(IMoleculeSet moleculeSet, ArrayList<IAtom> atomList,ArrayList<IBond> bondList) throws CDKException {
		CDKAtomTypeMatcher atMatcher = CDKAtomTypeMatcher.getInstance(moleculeSet.getBuilder());
		if (moleculeSet.getMoleculeCount() != 1) {
			throw new CDKException("RemovingSEofNBMechanism only expects one IMolecule");
		}
		if (atomList.size() != 1) {
			throw new CDKException("RemovingSEofNBMechanism only expects one atom in the ArrayList");
		}
		if (bondList != null) {
			throw new CDKException("RemovingSEofNBMechanism don't expect any bond in the ArrayList");
		}
		IMolecule molecule = moleculeSet.getMolecule(0);
		IMolecule reactantCloned;
		try {
			reactantCloned = (IMolecule) molecule.clone();
		} catch (CloneNotSupportedException e) {
			throw new CDKException("Could not clone IMolecule!", e);
		}
		
		// remove one lone pair electron and substitute with one single electron and charge 1.
		int posAtom = molecule.getAtomNumber(atomList.get(0));
		List<ILonePair> lps = reactantCloned.getConnectedLonePairsList(reactantCloned.getAtom(posAtom));
		reactantCloned.removeLonePair(lps.get(lps.size() - 1));

		reactantCloned.addSingleElectron(new SingleElectron(reactantCloned.getAtom(posAtom)));
		int charge = reactantCloned.getAtom(posAtom).getFormalCharge();
		reactantCloned.getAtom(posAtom).setFormalCharge(charge+1);

		// check if resulting atom type is reasonable
		reactantCloned.getAtom(posAtom).setHybridization(null);
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(reactantCloned);
		IAtomType type = atMatcher.findMatchingAtomType(reactantCloned, reactantCloned.getAtom(posAtom));
		if (type == null)
			return null;
		
		IReaction reaction = DefaultChemObjectBuilder.getInstance().newReaction();
		reaction.addReactant(molecule);
		
		/* mapping */
		IMapping mapping = DefaultChemObjectBuilder.getInstance().newMapping(atomList.get(0), reactantCloned.getAtom(posAtom));
        reaction.addMapping(mapping);
		
		reaction.addProduct(reactantCloned);
		
		return reaction;
	}

}
