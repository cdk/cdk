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
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
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
 * <p>This mechanism adduct together two fragments. The second fragment will be deficient in charge.
 * It returns the reaction mechanism which has been cloned the IMolecule.</p>
 * <p>This reaction could be represented as A + [B+] => A-B</p>
 * 
 * 
 * @author         miguelrojasch
 * @cdk.created    2008-02-10
 * @cdk.module     reaction
 *
 */
@TestClass(value="org.openscience.cdk.reaction.mechanism.AdductionLPMechanismTest")
public class AdductionLPMechanism implements IReactionMechanism{

	/** 
     * Initiates the process for the given mechanism. The atoms and bonds to apply are mapped between
     * reactants and products. 
     *
     * @param moleculeSet The IMolecule to apply the mechanism
     * @param atomList    The list of atoms taking part in the mechanism. Only allowed two atoms.
     * @param bondList    The list of bonds taking part in the mechanism. not allowed bonds.
     * 
     * @return            The Reaction mechanism
     * 
	 */
    @TestMethod(value="testInitiate_IMoleculeSet_ArrayList_ArrayList")
	public IReaction initiate(IMoleculeSet moleculeSet, ArrayList<IAtom> atomList,ArrayList<IBond> bondList) throws CDKException {
		CDKAtomTypeMatcher atMatcher = CDKAtomTypeMatcher.getInstance(moleculeSet.getBuilder());
		if (moleculeSet.getMoleculeCount() != 2) {
			throw new CDKException("AdductionLPMechanism expects two IMolecule's");
		}
		if (atomList.size() != 2) {
			throw new CDKException("AdductionLPMechanism expects two atoms in the ArrayList");
		}
		if (bondList != null) {
			throw new CDKException("AdductionLPMechanism don't expect bonds in the ArrayList");
		}
		IMolecule molecule = moleculeSet.getMolecule(0);
		IMolecule reactantCloned;
		try {
			reactantCloned = (IMolecule) moleculeSet.getMolecule(0).clone();
			reactantCloned.add((IAtomContainer) moleculeSet.getMolecule(1).clone());
		} catch (CloneNotSupportedException e) {
			throw new CDKException("Could not clone IMolecule!", e);
		}
		IAtom atom1 = atomList.get(0);// Atom 1: excess in charge
		IAtom atom1C = reactantCloned.getAtom(molecule.getAtomNumber(atom1));
		IAtom atom2 = atomList.get(1);// Atom 2: deficient in charge
		IAtom atom2C = reactantCloned.getAtom(molecule.getAtomNumber(atom2)+moleculeSet.getMolecule(0).getAtomCount()+1);
		
		IBond newBond = molecule.getBuilder().newBond(atom1C, atom2C, IBond.Order.SINGLE);
    	reactantCloned.addBond(newBond);
    	
    	int charge = atom1C.getFormalCharge();
    	atom1C.setFormalCharge(charge+1);
    	List<ILonePair> lps = reactantCloned.getConnectedLonePairsList(atom1C);
		reactantCloned.removeLonePair(lps.get(lps.size() - 1));
    	atom1C.setHybridization(null);
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(reactantCloned);
		IAtomType type = atMatcher.findMatchingAtomType(reactantCloned, atom1C);
		if (type == null) return null;

		charge = atom2C.getFormalCharge();
    	atom2C.setFormalCharge(charge-1);
		atom2C.setHybridization(null);
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(reactantCloned);
		type = atMatcher.findMatchingAtomType(reactantCloned, atom2C);
		if (type == null) return null;
		
		IReaction reaction = DefaultChemObjectBuilder.getInstance().newReaction();
		reaction.addReactant(molecule);
		
		/* mapping */
		IMapping mapping = DefaultChemObjectBuilder.getInstance().newMapping(atom1, atom1C);
        reaction.addMapping(mapping);
        mapping = DefaultChemObjectBuilder.getInstance().newMapping(atom2, atom2C);
        reaction.addMapping(mapping);
    	
    	reaction.addProduct(reactantCloned);
    	
		return reaction;
	}

}
