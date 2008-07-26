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

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMapping;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.reaction.IReactionMechanism;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.BondManipulator;

/**
 * <p>This mechanism produces the tautomerization chemical reaction between two tautomers. 
 * It returns the reaction mechanism which has been cloned the IMolecule.</p>
 * <p>This reaction could be represented as X=Y-Z-H => X(H)-Y=Z</p>
 * 
 * @author         miguelrojasch
 * @cdk.created    2008-02-10
 * @cdk.module     reaction
 *
 */
public class TautomerizationMechanism implements IReactionMechanism{

	/** 
     * Initiates the process for the given mechanism. The atoms and bonds to apply are mapped between
     * reactants and products. 
     *
     * @param moleculeSet The IMolecule to apply the mechanism
     * @param atomList    The list of atoms taking part in the mechanism. Only allowed fourth atoms.
     * @param bondList    The list of bonds taking part in the mechanism. Only allowed two bond.
     * 					  The first bond is the bond to decrease the order and the second is the bond
     * 				      to increase the order
     * 					  It is the bond which is moved
     * @return            The Reaction mechanism
     * 
	 */
	public IReaction initiate(IMoleculeSet moleculeSet, ArrayList<IAtom> atomList,ArrayList<IBond> bondList) throws CDKException {
		CDKAtomTypeMatcher atMatcher = CDKAtomTypeMatcher.getInstance(moleculeSet.getBuilder());
		if (moleculeSet.getMoleculeCount() != 1) {
			throw new CDKException("TautomerizationMechanism only expects one IMolecule");
		}
		if (atomList.size() != 4) {
			throw new CDKException("TautomerizationMechanism expects four atoms in the ArrayList");
		}
		if (bondList.size() != 3) {
			throw new CDKException("TautomerizationMechanism expects three bonds in the ArrayList");
		}
		IMolecule molecule = moleculeSet.getMolecule(0);
		IMolecule reactantCloned;
		try {
			reactantCloned = (IMolecule) molecule.clone();
		} catch (CloneNotSupportedException e) {
			throw new CDKException("Could not clone IMolecule!", e);
		}
		IAtom atom1 = atomList.get(0);// Atom to be added the hydrogen
		IAtom atom1C = reactantCloned.getAtom(molecule.getAtomNumber(atom1));
		IAtom atom2 = atomList.get(1);// Atom 2 
		IAtom atom2C = reactantCloned.getAtom(molecule.getAtomNumber(atom2));
		IAtom atom3 = atomList.get(2);// Atom 3
		IAtom atom3C = reactantCloned.getAtom(molecule.getAtomNumber(atom3));
		IAtom atom4 = atomList.get(3);// hydrogen Atom
		IAtom atom4C = reactantCloned.getAtom(molecule.getAtomNumber(atom4));
		IBond bond1 = bondList.get(0);// Bond with double bond
		int posBond1 = molecule.getBondNumber(bond1);
		IBond bond2 = bondList.get(1);// Bond with single bond
		int posBond2 = molecule.getBondNumber(bond2);
		IBond bond3 = bondList.get(2);// Bond to be removed
		int posBond3 = molecule.getBondNumber(bond3);
		
    	BondManipulator.decreaseBondOrder(reactantCloned.getBond(posBond1));
    	BondManipulator.increaseBondOrder(reactantCloned.getBond(posBond2));		
    	reactantCloned.removeBond(reactantCloned.getBond(posBond3));  
    	IBond newBond = molecule.getBuilder().newBond(atom1C, atom4C, IBond.Order.SINGLE);
    	reactantCloned.addBond(newBond);
    	
    	atom1C.setHybridization(null);
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(reactantCloned);
		IAtomType type = atMatcher.findMatchingAtomType(reactantCloned, atom1C);
		if (type == null) return null;

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
        mapping = DefaultChemObjectBuilder.getInstance().newMapping(atom4, atom4C);
        reaction.addMapping(mapping);
        mapping = DefaultChemObjectBuilder.getInstance().newMapping(bond1, reactantCloned.getBond(posBond1));
    	reaction.addMapping(mapping);
        mapping = DefaultChemObjectBuilder.getInstance().newMapping(bond2, reactantCloned.getBond(posBond2));
    	reaction.addMapping(mapping);
        mapping = DefaultChemObjectBuilder.getInstance().newMapping(bond3, newBond);
    	reaction.addMapping(mapping);
    	
    	reaction.addProduct(reactantCloned);
    	
		return reaction;
	}

}
