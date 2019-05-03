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
import java.util.List;

/**
 * <p>This mechanism extracts an atom because of the stabilization of a radical.
 * It returns the reaction mechanism which has been cloned the IAtomContainer.</p>
 * <p>This reaction could be represented as Y-B-[C*] =&gt; [Y*] + B=C</p>
 *
 * @author         miguelrojasch
 * @cdk.created    2008-02-10
 * @cdk.module     reaction
 * @cdk.githash
 */
public class RadicalSiteIonizationMechanism implements IReactionMechanism {

    /**
     * Initiates the process for the given mechanism. The atoms to apply are mapped between
     * reactants and products.
     *
     *
     * @param atomContainerSet
     * @param atomList    The list of atoms taking part in the mechanism. Only allowed two atoms.
     *                    The first atom is the atom which contains the ISingleElectron and the second
     *                    third is the atom which will be removed
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
            throw new CDKException("RadicalSiteIonizationMechanism only expects one IAtomContainer");
        }
        if (atomList.size() != 3) {
            throw new CDKException("RadicalSiteIonizationMechanism expects three atoms in the ArrayList");
        }
        if (bondList.size() != 2) {
            throw new CDKException("RadicalSiteIonizationMechanism only expect one bond in the ArrayList");
        }
        IAtomContainer molecule = atomContainerSet.getAtomContainer(0);
        IAtomContainer reactantCloned;
        try {
            reactantCloned = (IAtomContainer) molecule.clone();
        } catch (CloneNotSupportedException e) {
            throw new CDKException("Could not clone IAtomContainer!", e);
        }
        IAtom atom1 = atomList.get(0);// Atom containing the ISingleElectron
        IAtom atom1C = reactantCloned.getAtom(molecule.indexOf(atom1));
        IAtom atom2 = atomList.get(1);// Atom
        IAtom atom2C = reactantCloned.getAtom(molecule.indexOf(atom2));
        IAtom atom3 = atomList.get(2);// Atom to be saved
        IAtom atom3C = reactantCloned.getAtom(molecule.indexOf(atom3));
        IBond bond1 = bondList.get(0);// Bond to increase the order
        int posBond1 = molecule.indexOf(bond1);
        IBond bond2 = bondList.get(1);// Bond to remove
        int posBond2 = molecule.indexOf(bond2);

        BondManipulator.increaseBondOrder(reactantCloned.getBond(posBond1));
        reactantCloned.removeBond(reactantCloned.getBond(posBond2));

        List<ISingleElectron> selectron = reactantCloned.getConnectedSingleElectronsList(atom1C);
        reactantCloned.removeSingleElectron(selectron.get(selectron.size() - 1));
        atom1C.setHybridization(null);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(reactantCloned);
        IAtomType type = atMatcher.findMatchingAtomType(reactantCloned, atom1C);
        if (type == null || type.getAtomTypeName().equals("X")) return null;

        atom2C.setHybridization(null);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(reactantCloned);
        type = atMatcher.findMatchingAtomType(reactantCloned, atom2C);
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
                    reactantCloned.getAtom(molecule.indexOf(atom)));
            reaction.addMapping(mapping);
        }

        IAtomContainerSet moleculeSetP = ConnectivityChecker.partitionIntoMolecules(reactantCloned);
        for (int z = 0; z < moleculeSetP.getAtomContainerCount(); z++)
            reaction.addProduct((IAtomContainer) moleculeSetP.getAtomContainer(z));

        return reaction;
    }

}
