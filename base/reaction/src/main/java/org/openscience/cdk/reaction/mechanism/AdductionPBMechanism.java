/* Copyright (C) 2008  Miguel Rojas <miguelrojasch@yahoo.es>
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

import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMapping;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.reaction.IReactionMechanism;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.BondManipulator;

import java.util.ArrayList;

/**
 * <p>This mechanism adduct together two fragments due to a double bond.
 * The second fragment will be deficient in charge.
 * It returns the reaction mechanism which has been cloned the {@link IAtomContainer}.</p>
 * <p>This reaction could be represented as A=B + [C+] =&gt; [A+]-B-C</p>
 *
 *
 * @author         miguelrojasch
 * @cdk.created    2008-02-10
 * @cdk.module     reaction
 * @cdk.githash
 */
public class AdductionPBMechanism implements IReactionMechanism {

    /**
     * Initiates the process for the given mechanism. The atoms and bonds to apply are mapped between
     * reactants and products.
     *
     *
     * @param atomContainerSet
     * @param atomList    The list of atoms taking part in the mechanism. Only allowed three atoms
     * @param bondList    The list of bonds taking part in the mechanism. Only allowed one bond
     *
     * @return            The Reaction mechanism
     *
     */
    @Override
    public IReaction initiate(IAtomContainerSet atomContainerSet, ArrayList<IAtom> atomList, ArrayList<IBond> bondList)
            throws CDKException {
        CDKAtomTypeMatcher atMatcher = CDKAtomTypeMatcher.getInstance(atomContainerSet.getBuilder());
        if (atomContainerSet.getAtomContainerCount() != 2) {
            throw new CDKException("AdductionPBMechanism expects two IAtomContainer's");
        }
        if (atomList.size() != 3) {
            throw new CDKException("AdductionPBMechanism expects two atoms in the ArrayList");
        }
        if (bondList.size() != 1) {
            throw new CDKException("AdductionPBMechanism don't expect bonds in the ArrayList");
        }
        IAtomContainer molecule1 = atomContainerSet.getAtomContainer(0);
        IAtomContainer molecule2 = atomContainerSet.getAtomContainer(1);

        IAtomContainer reactantCloned;
        try {
            reactantCloned = (IAtomContainer) atomContainerSet.getAtomContainer(0).clone();
            reactantCloned.add((IAtomContainer) atomContainerSet.getAtomContainer(1).clone());
        } catch (CloneNotSupportedException e) {
            throw new CDKException("Could not clone IAtomContainer!", e);
        }
        IAtom atom1 = atomList.get(0);// Atom 1: to be deficient in charge
        IAtom atom1C = reactantCloned.getAtom(molecule1.getAtomNumber(atom1));
        IAtom atom2 = atomList.get(1);// Atom 2: receive the adduct
        IAtom atom2C = reactantCloned.getAtom(molecule1.getAtomNumber(atom2));
        IAtom atom3 = atomList.get(2);// Atom 2: deficient in charge
        IAtom atom3C = reactantCloned.getAtom(molecule1.getAtomCount() + molecule2.getAtomNumber(atom3));
        IBond bond1 = bondList.get(0);
        int posBond1 = atomContainerSet.getAtomContainer(0).getBondNumber(bond1);

        BondManipulator.decreaseBondOrder(reactantCloned.getBond(posBond1));
        IBond newBond = molecule1.getBuilder().newInstance(IBond.class, atom2C, atom3C, IBond.Order.SINGLE);
        reactantCloned.addBond(newBond);

        int charge = atom1C.getFormalCharge();
        atom1C.setFormalCharge(charge + 1);
        atom1C.setHybridization(null);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(reactantCloned);
        IAtomType type = atMatcher.findMatchingAtomType(reactantCloned, atom1C);
        if (type == null || type.getAtomTypeName().equals("X")) return null;

        atom2C.setHybridization(null);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(reactantCloned);
        type = atMatcher.findMatchingAtomType(reactantCloned, atom2C);
        if (type == null || type.getAtomTypeName().equals("X")) return null;

        charge = atom3C.getFormalCharge();
        atom3C.setFormalCharge(charge - 1);
        atom3C.setHybridization(null);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(reactantCloned);
        type = atMatcher.findMatchingAtomType(reactantCloned, atom3C);
        if (type == null || type.getAtomTypeName().equals("X")) return null;

        IReaction reaction = atom1C.getBuilder().newInstance(IReaction.class);
        reaction.addReactant(molecule1);

        /* mapping */
        for (IAtom atom : molecule1.atoms()) {
            IMapping mapping = atom1C.getBuilder().newInstance(IMapping.class, atom,
                    reactantCloned.getAtom(molecule1.getAtomNumber(atom)));
            reaction.addMapping(mapping);
        }
        for (IAtom atom : molecule2.atoms()) {
            IMapping mapping = atom1C.getBuilder().newInstance(IMapping.class, atom,
                    reactantCloned.getAtom(molecule2.getAtomNumber(atom)));
            reaction.addMapping(mapping);
        }

        reaction.addProduct(reactantCloned);

        return reaction;
    }

}
