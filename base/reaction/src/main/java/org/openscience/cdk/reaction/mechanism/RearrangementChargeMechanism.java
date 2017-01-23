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

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.ILonePair;
import org.openscience.cdk.interfaces.IMapping;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.ISingleElectron;
import org.openscience.cdk.reaction.IReactionMechanism;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.BondManipulator;

/**
 * <p>This mechanism displaces the charge(radical, charge + or charge -) because of
 * a double bond which is associated.
 * It returns the reaction mechanism which has been cloned the {@link IAtomContainer}.</p>
 * <p>This reaction could be represented as [A*]-Y=Z =&gt; A=Z-[Y*]</p>
 *
 * @author         miguelrojasch
 * @cdk.created    2008-02-10
 * @cdk.module     reaction
 * @cdk.githash
 */
public class RearrangementChargeMechanism implements IReactionMechanism {

    /**
     * Initiates the process for the given mechanism. The atoms to apply are mapped between
     * reactants and products.
     *
     *
     * @param atomContainerSet
     * @param atomList    The list of atoms taking part in the mechanism. Only allowed two three.
     *                    The first atom is the atom which must contain the charge to be moved, the second
     *                    is the atom which is in the middle and the third is the atom which acquires the new charge
     * @param bondList    The list of bonds taking part in the mechanism. Only allowed two bond.
     * 					  The first bond is the bond to increase the order and the second is the bond
     * 				      to decrease the order
     * 					  It is the bond which is moved
     * @return            The Reaction mechanism
     *
     */
    @Override
    public IReaction initiate(IAtomContainerSet atomContainerSet, ArrayList<IAtom> atomList, ArrayList<IBond> bondList)
            throws CDKException {
        CDKAtomTypeMatcher atMatcher = CDKAtomTypeMatcher.getInstance(atomContainerSet.getBuilder());
        if (atomContainerSet.getAtomContainerCount() != 1) {
            throw new CDKException("RearrangementChargeMechanism only expects one IAtomContainer");
        }
        if (atomList.size() != 3) {
            throw new CDKException("RearrangementChargeMechanism expects three atoms in the ArrayList");
        }
        if (bondList.size() != 2) {
            throw new CDKException("RearrangementChargeMechanism only expect one bond in the ArrayList");
        }
        IAtomContainer molecule = atomContainerSet.getAtomContainer(0);
        IAtomContainer reactantCloned;
        try {
            reactantCloned = (IAtomContainer) molecule.clone();
        } catch (CloneNotSupportedException e) {
            throw new CDKException("Could not clone IAtomContainer!", e);
        }
        IAtom atom1 = atomList.get(0);// Atom with the charge
        IAtom atom1C = reactantCloned.getAtom(molecule.getAtomNumber(atom1));
        IAtom atom3 = atomList.get(2);// Atom which acquires the charge
        IAtom atom3C = reactantCloned.getAtom(molecule.getAtomNumber(atom3));
        IBond bond1 = bondList.get(0);// Bond with single bond
        int posBond1 = molecule.getBondNumber(bond1);
        IBond bond2 = bondList.get(1);// Bond with double bond
        int posBond2 = molecule.getBondNumber(bond2);

        BondManipulator.increaseBondOrder(reactantCloned.getBond(posBond1));
        if (bond2.getOrder() == IBond.Order.SINGLE)
            reactantCloned.removeBond(reactantCloned.getBond(posBond2));
        else
            BondManipulator.decreaseBondOrder(reactantCloned.getBond(posBond2));

        //Depending of the charge moving (radical, + or -) there is a different situation
        if (reactantCloned.getConnectedSingleElectronsCount(atom1C) > 0) {
            List<ISingleElectron> selectron = reactantCloned.getConnectedSingleElectronsList(atom1C);
            reactantCloned.removeSingleElectron(selectron.get(selectron.size() - 1));

            reactantCloned.addSingleElectron(bond2.getBuilder().newInstance(ISingleElectron.class, atom3C));

        } else if (atom1C.getFormalCharge() > 0) {
            int charge = atom1C.getFormalCharge();
            atom1C.setFormalCharge(charge - 1);

            charge = atom3C.getFormalCharge();
            atom3C.setFormalCharge(charge + 1);

        } else if (atom1C.getFormalCharge() < 1) {
            int charge = atom1C.getFormalCharge();
            atom1C.setFormalCharge(charge + 1);
            List<ILonePair> ln = reactantCloned.getConnectedLonePairsList(atom1C);
            reactantCloned.removeLonePair(ln.get(ln.size() - 1));
            atom1C.setFlag(CDKConstants.ISAROMATIC, false);

            charge = atom3C.getFormalCharge();
            atom3C.setFormalCharge(charge - 1);
            reactantCloned.addLonePair(bond2.getBuilder().newInstance(ILonePair.class, atom3C));
            atom3C.setFlag(CDKConstants.ISAROMATIC, false);
        } else
            return null;

        atom1C.setHybridization(null);
        atom3C.setHybridization(null);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(reactantCloned);

        IAtomType type = atMatcher.findMatchingAtomType(reactantCloned, atom1C);
        if (type == null || type.getAtomTypeName().equals("X")) return null;

        type = atMatcher.findMatchingAtomType(reactantCloned, atom3C);
        if (type == null || type.getAtomTypeName().equals("X")) return null;

        IReaction reaction = bond2.getBuilder().newInstance(IReaction.class);
        reaction.addReactant(molecule);

        /* mapping */
        for (IAtom atom : molecule.atoms()) {
            IMapping mapping = bond2.getBuilder().newInstance(IMapping.class, atom,
                    reactantCloned.getAtom(molecule.getAtomNumber(atom)));
            reaction.addMapping(mapping);
        }
        if (bond2.getOrder() != IBond.Order.SINGLE) {
            reaction.addProduct(reactantCloned);
        } else {
            IAtomContainerSet moleculeSetP = ConnectivityChecker.partitionIntoMolecules(reactantCloned);
            for (int z = 0; z < moleculeSetP.getAtomContainerCount(); z++) {
                reaction.addProduct((IAtomContainer) moleculeSetP.getAtomContainer(z));
            }
        }

        return reaction;
    }

}
