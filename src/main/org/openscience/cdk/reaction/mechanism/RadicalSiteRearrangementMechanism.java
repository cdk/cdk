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
import org.openscience.cdk.interfaces.IMapping;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.ISingleElectron;
import org.openscience.cdk.reaction.IReactionMechanism;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * <p>This mechanism displaces an Atom or substructure (R) from one position to an other. 
 * It returns the reaction mechanism which has been cloned the IMolecule.</p>
 * <p>This reaction could be represented as [A*]-(X)_n-Y-Z => A(Z)-(X)_n-[Y*]</p>
 * 
 * @author         miguelrojasch
 * @cdk.created    2008-02-10
 * @cdk.module     reaction
 *
 */
public class RadicalSiteRearrangementMechanism implements IReactionMechanism{

	/** 
     * Initiates the process for the given mechanism. The atoms to apply are mapped between
     * reactants and products. 
     *
     * @param moleculeSet The IMolecule to apply the mechanism
     * @param atomList    The list of atoms taking part in the mechanism. Only allowed two atoms.
     *                    The first atom is the atom which must be moved and the second 
     *                    is the atom which receives the atom1 and the third is the atom which loss 
     *                    the first atom
     * @param bondList    The list of bonds taking part in the mechanism. Only allowed one bond.
     * 					  It is the bond which is moved
     * @return            The Reaction mechanism
     * 
	 */
	public IReaction initiate(IMoleculeSet moleculeSet, ArrayList<IAtom> atomList,ArrayList<IBond> bondList) throws CDKException {
		CDKAtomTypeMatcher atMatcher = CDKAtomTypeMatcher.getInstance(moleculeSet.getBuilder());
		if (moleculeSet.getMoleculeCount() != 1) {
			throw new CDKException("RadicalSiteRearrangementMechanism only expects one IMolecule");
		}
		if (atomList.size() != 3) {
			throw new CDKException("RadicalSiteRearrangementMechanism expects three atoms in the ArrayList");
		}
		if (bondList.size() != 1) {
			throw new CDKException("RadicalSiteRearrangementMechanism only expect one bond in the ArrayList");
		}
		IMolecule molecule = moleculeSet.getMolecule(0);
		IMolecule reactantCloned;
		try {
			reactantCloned = (IMolecule) molecule.clone();
		} catch (CloneNotSupportedException e) {
			throw new CDKException("Could not clone IMolecule!", e);
		}
		IAtom atom1 = atomList.get(0);// Atom to be moved
		IAtom atom1C = reactantCloned.getAtom(molecule.getAtomNumber(atom1));
		IAtom atom2 = atomList.get(1);// Atom to receive the new bonding with a ISingleElectron
		IAtom atom2C = reactantCloned.getAtom(molecule.getAtomNumber(atom2));
		IAtom atom3 = atomList.get(2);// Atom which loss the atom
		IAtom atom3C = reactantCloned.getAtom(molecule.getAtomNumber(atom3));
		IBond bond1 = bondList.get(0);// Bond to move
		int posBond1 = molecule.getBondNumber(bond1);
		

		reactantCloned.removeBond(reactantCloned.getBond(posBond1));
		IBond newBond = atom1.getBuilder().newBond(atom1C, 
				atom2C, IBond.Order.SINGLE);
		reactantCloned.addBond(newBond);
		
		List<ISingleElectron> selectron = reactantCloned.getConnectedSingleElectronsList(atom2C);
		reactantCloned.removeSingleElectron(selectron.get(selectron.size() -1));		
		atom2C.setHybridization(null);
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(reactantCloned);
		IAtomType type = atMatcher.findMatchingAtomType(reactantCloned, atom2C);
		if (type == null) return null;
		
		reactantCloned.addSingleElectron(new SingleElectron(atom3C));
		atom3C.setHybridization(null);
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(reactantCloned);
		type = atMatcher.findMatchingAtomType(reactantCloned, atom3C);
		if (type == null) return null;
		
		IReaction reaction = DefaultChemObjectBuilder.getInstance().newReaction();
		reaction.addReactant(molecule);
		
		/* mapping */
		IMapping mapping = DefaultChemObjectBuilder.getInstance().newMapping(atom1, atom1C);
        reaction.addMapping(mapping);
        mapping = DefaultChemObjectBuilder.getInstance().newMapping(atom2, atom2C);
        reaction.addMapping(mapping);
        mapping = DefaultChemObjectBuilder.getInstance().newMapping(atom3, atom3C);
        reaction.addMapping(mapping);
        mapping = DefaultChemObjectBuilder.getInstance().newMapping(bond1, newBond);
    	reaction.addMapping(mapping);
    	
    	reaction.addProduct(reactantCloned);
    	
		return reaction;
	}

}
