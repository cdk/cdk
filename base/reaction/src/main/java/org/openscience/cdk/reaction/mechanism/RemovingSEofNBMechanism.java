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
import org.openscience.cdk.interfaces.ILonePair;
import org.openscience.cdk.interfaces.IMapping;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.ISingleElectron;
import org.openscience.cdk.reaction.IReactionMechanism;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.util.ArrayList;
import java.util.List;

/**
 * This mechanism extracts a single electron from a non-bonding orbital which located in
 * a ILonePair container. It returns the reaction mechanism which has been cloned the
 * {@link IAtomContainer} with an ILonPair electron less and an ISingleElectron more.
 *
 * @author         miguelrojasch
 * @cdk.created    2008-02-10
 * @cdk.module     reaction
 * @cdk.githash
 */
public class RemovingSEofNBMechanism implements IReactionMechanism {

    /**
     * Initiates the process for the given mechanism. The atoms to apply are mapped between
     * reactants and products.
     *
     *
     * @param atomContainerSet
     * @param atomList    The list of atoms taking part in the mechanism. Only allowed one atom
     * @param bondList    The list of bonds taking part in the mechanism. Only allowed one Bond
     * @return            The Reaction mechanism
     *
     */
    @Override
    public IReaction initiate(IAtomContainerSet atomContainerSet, ArrayList<IAtom> atomList, ArrayList<IBond> bondList)
            throws CDKException {
        CDKAtomTypeMatcher atMatcher = CDKAtomTypeMatcher.getInstance(atomContainerSet.getBuilder());
        if (atomContainerSet.getAtomContainerCount() != 1) {
            throw new CDKException("RemovingSEofNBMechanism only expects one IAtomContainer");
        }
        if (atomList.size() != 1) {
            throw new CDKException("RemovingSEofNBMechanism only expects one atom in the ArrayList");
        }
        if (bondList != null) {
            throw new CDKException("RemovingSEofNBMechanism don't expect any bond in the ArrayList");
        }
        IAtomContainer molecule = atomContainerSet.getAtomContainer(0);
        IAtomContainer reactantCloned;
        try {
            reactantCloned = (IAtomContainer) molecule.clone();
        } catch (CloneNotSupportedException e) {
            throw new CDKException("Could not clone IAtomContainer!", e);
        }

        // remove one lone pair electron and substitute with one single electron and charge 1.
        int posAtom = molecule.indexOf(atomList.get(0));
        List<ILonePair> lps = reactantCloned.getConnectedLonePairsList(reactantCloned.getAtom(posAtom));
        reactantCloned.removeLonePair(lps.get(lps.size() - 1));

        reactantCloned.addSingleElectron(molecule.getBuilder().newInstance(ISingleElectron.class,
                reactantCloned.getAtom(posAtom)));
        int charge = reactantCloned.getAtom(posAtom).getFormalCharge();
        reactantCloned.getAtom(posAtom).setFormalCharge(charge + 1);

        // check if resulting atom type is reasonable
        reactantCloned.getAtom(posAtom).setHybridization(null);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(reactantCloned);
        IAtomType type = atMatcher.findMatchingAtomType(reactantCloned, reactantCloned.getAtom(posAtom));
        if (type == null || type.getAtomTypeName().equals("X")) return null;

        IReaction reaction = molecule.getBuilder().newInstance(IReaction.class);
        reaction.addReactant(molecule);

        /* mapping */
        for (IAtom atom : molecule.atoms()) {
            IMapping mapping = molecule.getBuilder().newInstance(IMapping.class, atom,
                    reactantCloned.getAtom(molecule.indexOf(atom)));
            reaction.addMapping(mapping);
        }
        reaction.addProduct(reactantCloned);

        return reaction;
    }

}
