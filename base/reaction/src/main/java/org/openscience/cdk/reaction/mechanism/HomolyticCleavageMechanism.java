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
import org.openscience.cdk.graph.ConnectivityChecker;
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
import org.openscience.cdk.tools.manipulator.BondManipulator;

import java.util.ArrayList;

/**
 * This mechanism breaks the chemical bond between atoms. Generating two atoms with
 * attached radicals.
 * It returns the reaction mechanism which has been cloned the {@link IAtomContainer}.
 *
 * @author         miguelrojasch
 * @cdk.created    2008-02-10
 * @cdk.module     reaction
 * @cdk.githash
 */
public class HomolyticCleavageMechanism implements IReactionMechanism {

    /**
     * Initiates the process for the given mechanism. The atoms to apply are mapped between
     * reactants and products.
     *
     *
     * @param atomContainerSet
     * @param atomList    The list of atoms taking part in the mechanism. Only allowed two atoms.
     *                    Both atoms acquire a ISingleElectron
     * @param bondList    The list of bonds taking part in the mechanism. Only allowed one bond
     * @return            The Reaction mechanism
     *
     */
    @Override
    public IReaction initiate(IAtomContainerSet atomContainerSet, ArrayList<IAtom> atomList, ArrayList<IBond> bondList)
            throws CDKException {
        CDKAtomTypeMatcher atMatcher = CDKAtomTypeMatcher.getInstance(atomContainerSet.getBuilder());
        if (atomContainerSet.getAtomContainerCount() != 1) {
            throw new CDKException("TautomerizationMechanism only expects one IAtomContainer");
        }
        if (atomList.size() != 2) {
            throw new CDKException("HomolyticCleavageMechanism expects two atoms in the ArrayList");
        }
        if (bondList.size() != 1) {
            throw new CDKException("HomolyticCleavageMechanism only expect one bond in the ArrayList");
        }
        IAtomContainer molecule = atomContainerSet.getAtomContainer(0);
        IAtomContainer reactantCloned;
        try {
            reactantCloned = (IAtomContainer) molecule.clone();
        } catch (CloneNotSupportedException e) {
            throw new CDKException("Could not clone IAtomContainer!", e);
        }
        IAtom atom1 = atomList.get(0);
        IAtom atom1C = reactantCloned.getAtom(molecule.indexOf(atom1));
        IAtom atom2 = atomList.get(1);
        IAtom atom2C = reactantCloned.getAtom(molecule.indexOf(atom2));
        IBond bond1 = bondList.get(0);
        int posBond1 = molecule.indexOf(bond1);

        if (bond1.getOrder() == IBond.Order.SINGLE)
            reactantCloned.removeBond(reactantCloned.getBond(posBond1));
        else
            BondManipulator.decreaseBondOrder(reactantCloned.getBond(posBond1));

        reactantCloned.addSingleElectron(bond1.getBuilder().newInstance(ISingleElectron.class, atom1C));
        reactantCloned.addSingleElectron(bond1.getBuilder().newInstance(ISingleElectron.class, atom2C));
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(reactantCloned);

        // check if resulting atom type is reasonable
        atom1C.setHybridization(null);
        IAtomType type = atMatcher.findMatchingAtomType(reactantCloned, atom1C);
        if (type == null || type.getAtomTypeName().equals("X")) return null;

        // check if resulting atom type is reasonable: an acceptor atom cannot be charged positive*/
        atom2C.setHybridization(null);
        type = atMatcher.findMatchingAtomType(reactantCloned, atom2C);
        if (type == null || type.getAtomTypeName().equals("X")) return null;

        IReaction reaction = atom2C.getBuilder().newInstance(IReaction.class);
        reaction.addReactant(molecule);

        /* mapping */
        for (IAtom atom : molecule.atoms()) {
            IMapping mapping = atom2C.getBuilder().newInstance(IMapping.class, atom,
                    reactantCloned.getAtom(molecule.indexOf(atom)));
            reaction.addMapping(mapping);
        }
        if (bond1.getOrder() != IBond.Order.SINGLE) {
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
