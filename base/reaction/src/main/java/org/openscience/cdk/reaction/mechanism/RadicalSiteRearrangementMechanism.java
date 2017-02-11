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
import org.openscience.cdk.interfaces.ISingleElectron;
import org.openscience.cdk.reaction.IReactionMechanism;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>This mechanism displaces an Atom or substructure (R) from one position to an other.
 * It returns the reaction mechanism which has been cloned the {@link IAtomContainer}.</p>
 * <p>This reaction could be represented as [A*]-(X)_n-Y-Z =&gt; A(Z)-(X)_n-[Y*]</p>
 *
 * @author         miguelrojasch
 * @cdk.created    2008-02-10
 * @cdk.module     reaction
 * @cdk.githash
 */
public class RadicalSiteRearrangementMechanism implements IReactionMechanism {

    /**
     * Initiates the process for the given mechanism. The atoms to apply are mapped between
     * reactants and products.
     *
     *
     * @param atomContainerSet
     * @param atomList    The list of atoms taking part in the mechanism. Only allowed two atoms.
     *                    The first atom is the atom which must be moved and the second
     *                    is the atom which receives the atom1 and the third is the atom which loss
     *                    the first atom
     * @param bondList    The list of bonds taking part in the mechanism. Only allowed one bond.
     * 					  It is the bond which is moved
     * @return            The Reaction mechanism
     *
     */
    @Override
    public IReaction initiate(IAtomContainerSet atomContainerSet, ArrayList<IAtom> atomList, ArrayList<IBond> bondList)
            throws CDKException {
        CDKAtomTypeMatcher atMatcher = CDKAtomTypeMatcher.getInstance(atomContainerSet.getBuilder());
        if (atomContainerSet.getAtomContainerCount() != 1) {
            throw new CDKException("RadicalSiteRearrangementMechanism only expects one IAtomContainer");
        }
        if (atomList.size() != 3) {
            throw new CDKException("RadicalSiteRearrangementMechanism expects three atoms in the ArrayList");
        }
        if (bondList.size() != 1) {
            throw new CDKException("RadicalSiteRearrangementMechanism only expect one bond in the ArrayList");
        }
        IAtomContainer molecule = atomContainerSet.getAtomContainer(0);
        IAtomContainer reactantCloned;
        try {
            reactantCloned = (IAtomContainer) molecule.clone();
        } catch (CloneNotSupportedException e) {
            throw new CDKException("Could not clone IAtomContainer!", e);
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
        IBond newBond = atom1.getBuilder().newInstance(IBond.class, atom1C, atom2C, IBond.Order.SINGLE);
        reactantCloned.addBond(newBond);

        List<ISingleElectron> selectron = reactantCloned.getConnectedSingleElectronsList(atom2C);
        reactantCloned.removeSingleElectron(selectron.get(selectron.size() - 1));
        atom2C.setHybridization(null);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(reactantCloned);
        IAtomType type = atMatcher.findMatchingAtomType(reactantCloned, atom2C);
        if (type == null || type.getAtomTypeName().equals("X")) return null;

        reactantCloned.addSingleElectron(atom2C.getBuilder().newInstance(ISingleElectron.class, atom3C));
        atom3C.setHybridization(null);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(reactantCloned);
        type = atMatcher.findMatchingAtomType(reactantCloned, atom3C);
        if (type == null || type.getAtomTypeName().equals("X")) return null;

        IReaction reaction = atom2C.getBuilder().newInstance(IReaction.class);
        reaction.addReactant(molecule);

        /* mapping */
        for (IAtom atom : molecule.atoms()) {
            IMapping mapping = atom2C.getBuilder().newInstance(IMapping.class, atom,
                    reactantCloned.getAtom(molecule.getAtomNumber(atom)));
            reaction.addMapping(mapping);
        }

        reaction.addProduct(reactantCloned);

        return reaction;
    }

}
