/* Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *  */
package org.openscience.cdk.tools.manipulator;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ReactionRole;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.stereo.ExtendedTetrahedral;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

/**
 * Provides a variety of methods to manipulate and convert from/to {@link IReaction}.
 * @cdk.module standard
 * @cdk.githash
 *
 * @see ChemModelManipulator
 * @author uli-f
 */
public class ReactionManipulator {

    /**
     * Returns the total number of atoms for all components of the given <code>reaction</code>.
     * @param reaction the number of atoms is summed up for all components of this reaction
     * @return total number of atoms for all {@link IAtomContainer} that are part of this reaction
     */
    public static int getAtomCount(IReaction reaction) {
        return getCount(reaction, IAtomContainer::getAtomCount);
    }

    /**
     * Returns the total number of atoms for all components of the given <code>reaction</code>.
     * @param reaction the number of atoms is summed up for all components of this reaction
     * @return total number of atoms for all {@link IAtomContainer} that are part of this reaction
     */
    public static int getBondCount(IReaction reaction) {
        return getCount(reaction, IAtomContainer::getBondCount);
    }

    private static int getCount(IReaction reaction, ToIntFunction<IAtomContainer> chemObjectToIntFunction) {
        int count = 0;
        IAtomContainerSet reactants = reaction.getReactants();
        for (int i = 0; i < reactants.getAtomContainerCount(); i++) {
            count += chemObjectToIntFunction.applyAsInt(reactants.getAtomContainer(i));
        }
        IAtomContainerSet agents = reaction.getAgents();
        for (int i = 0; i < agents.getAtomContainerCount(); i++) {
            count += chemObjectToIntFunction.applyAsInt(agents.getAtomContainer(i));
        }
        IAtomContainerSet products = reaction.getProducts();
        for (int i = 0; i < products.getAtomContainerCount(); i++) {
            count += chemObjectToIntFunction.applyAsInt(products.getAtomContainer(i));
        }
        return count;
    }

    /**
     * Removes the provided <code>atom</code> and its connected electron containers from the reaction.
     * @param reaction reaction the atom is removed from
     * @param atom atom that is removed
     */
    public static void removeAtomAndConnectedElectronContainers(IReaction reaction, IAtom atom) {
        removeIChemObject(reaction, atomContainer -> atomContainer.contains(atom), atomContainer -> atomContainer.removeAtom(atom));
    }

    /**
     * Removes the provided <code>electrons</code> from the reaction.
     * @param reaction reaction the electron container is removed from
     * @param electrons electron container that is removed
     */
    public static void removeElectronContainer(IReaction reaction, IElectronContainer electrons) {
        removeIChemObject(reaction, atomContainer -> atomContainer.contains(electrons), atomContainer -> atomContainer.removeElectronContainer(electrons));
    }

    private static void removeIChemObject(IReaction reaction, Predicate<IAtomContainer> containsChemObjectPredicate, Consumer<IAtomContainer> removeChemObjectConsumer) {
        IAtomContainerSet reactants = reaction.getReactants();
        for (int i = 0; i < reactants.getAtomContainerCount(); i++) {
            IAtomContainer mol = reactants.getAtomContainer(i);
            if (containsChemObjectPredicate.test(mol)) {
                removeChemObjectConsumer.accept(mol);
            }
        }
        IAtomContainerSet agents = reaction.getReactants();
        for (int i = 0; i < agents.getAtomContainerCount(); i++) {
            IAtomContainer mol = agents.getAtomContainer(i);
            if (containsChemObjectPredicate.test(mol)) {
                removeChemObjectConsumer.accept(mol);
            }
        }
        IAtomContainerSet products = reaction.getProducts();
        for (int i = 0; i < products.getAtomContainerCount(); i++) {
            IAtomContainer mol = products.getAtomContainer(i);
            if (containsChemObjectPredicate.test(mol)) {
                removeChemObjectConsumer.accept(mol);
            }
        }
    }

    /**
     * Returns all components of an {@link IReaction}, that is, reactants, agents and products.
     *
     * @param reaction all components of this reaction are returned
     * @return IAtomContainerSet containing all components of the reaction provided as an argument
     */
    public static IAtomContainerSet getAllMolecules(IReaction reaction) {
        IAtomContainerSet moleculeSet = reaction.getBuilder().newInstance(IAtomContainerSet.class);

        moleculeSet.add(getAllReactants(reaction));
        moleculeSet.add(getAllAgents(reaction));
        moleculeSet.add(getAllProducts(reaction));

        return moleculeSet;
    }

    /**
     * Returns all products of an {@link IReaction}.
     *
     * @param reaction all products of this reaction are returned
     * @return IAtomContainerSet containing all products of the reaction provided as an argument
     */
    public static IAtomContainerSet getAllProducts(IReaction reaction) {
        IAtomContainerSet moleculeSet = reaction.getBuilder().newInstance(IAtomContainerSet.class);
        IAtomContainerSet products = reaction.getProducts();
        for (int i = 0; i < products.getAtomContainerCount(); i++) {
            moleculeSet.addAtomContainer(products.getAtomContainer(i));
        }
        return moleculeSet;
    }

    /**
     * Returns all reactants of an {@link IReaction}.
     *
     * @param reaction all reactants of this reaction are returned
     * @return IAtomContainerSet containing all reactants of the reaction provided as an argument
     */
    public static IAtomContainerSet getAllReactants(IReaction reaction) {
        IAtomContainerSet moleculeSet = reaction.getBuilder().newInstance(IAtomContainerSet.class);
        IAtomContainerSet reactants = reaction.getReactants();
        for (int i = 0; i < reactants.getAtomContainerCount(); i++) {
            moleculeSet.addAtomContainer(reactants.getAtomContainer(i));
        }
        return moleculeSet;
    }

    /**
     * Returns all agents of an {@link IReaction}.
     *
     * @param reaction all agents of this reaction are returned
     * @return IAtomContainerSet containing all agents of the reaction provided as an argument
     */
    public static IAtomContainerSet getAllAgents(IReaction reaction) {
        IAtomContainerSet moleculeSet = reaction.getBuilder().newInstance(IAtomContainerSet.class);
        IAtomContainerSet agents = reaction.getAgents();
        for (int i = 0; i < agents.getAtomContainerCount(); i++) {
            moleculeSet.addAtomContainer(agents.getAtomContainer(i));
        }
        return moleculeSet;
    }

    /**
     * Returns a new {@link IReaction} which is the reverse of the given reaction.
     * The {@link IAtomContainer}s of the reversed reaction that is returned are
     * the same that are part of the <code>reaction</code> provided as an argument.
     * @param reaction the reaction being considered
     * @return the reverse reaction
     */
    public static IReaction reverse(IReaction reaction) {
        IReaction reversedReaction = reaction.getBuilder().newReaction();

        switch(reaction.getDirection()) {
            case BIDIRECTIONAL: reversedReaction.setDirection(IReaction.Direction.BIDIRECTIONAL);
            case FORWARD: reversedReaction.setDirection(IReaction.Direction.BACKWARD);
            case BACKWARD: reversedReaction.setDirection(IReaction.Direction.FORWARD);
        }

        IAtomContainerSet reactants = reaction.getReactants();
        for (int i = 0; i < reactants.getAtomContainerCount(); i++) {
            double coefficient = reaction.getReactantCoefficient(reactants.getAtomContainer(i));
            reversedReaction.addProduct(reactants.getAtomContainer(i), coefficient);
        }
        IAtomContainerSet products = reaction.getProducts();

        for (int i = 0; i < products.getAtomContainerCount(); i++) {
            double coefficient = reaction.getProductCoefficient(products.getAtomContainer(i));
            reversedReaction.addReactant(products.getAtomContainer(i), coefficient);
        }

        return reversedReaction;
    }

    /**
     * Returns all {@link IAtomContainer IAtomContainers} of the given reaction.
     *
     * @param reaction the reaction whose <code>IAtomContainers</code> are returned
     * @return list of <code>IAtomContainers</code> comprising the reaction
     */
    public static List<IAtomContainer> getAllAtomContainers(IReaction reaction) {
        return MoleculeSetManipulator.getAllAtomContainers(getAllMolecules(reaction));
    }

    public static List<String> getAllIDs(IReaction reaction) {
        List<String> idList = new ArrayList<>();
        if (reaction.getID() != null) idList.add(reaction.getID());
        IAtomContainerSet reactants = reaction.getReactants();
        for (int i = 0; i < reactants.getAtomContainerCount(); i++) {
            IAtomContainer mol = reactants.getAtomContainer(i);
            idList.addAll(AtomContainerManipulator.getAllIDs(mol));
        }
        IAtomContainerSet products = reaction.getProducts();
        for (int i = 0; i < products.getAtomContainerCount(); i++) {
            IAtomContainer mol = products.getAtomContainer(i);
            idList.addAll(AtomContainerManipulator.getAllIDs(mol));
        }
        return idList;
    }

    public static IAtomContainer getRelevantAtomContainer(IReaction reaction, IAtom atom) {
        IAtomContainer result = MoleculeSetManipulator.getRelevantAtomContainer(reaction.getReactants(), atom);
        if (result != null) {
            return result;
        }
        return MoleculeSetManipulator.getRelevantAtomContainer(reaction.getProducts(), atom);
    }

    public static IAtomContainer getRelevantAtomContainer(IReaction reaction, IBond bond) {
        IAtomContainer result = MoleculeSetManipulator.getRelevantAtomContainer(reaction.getReactants(), bond);
        if (result != null) {
            return result;
        }
        return MoleculeSetManipulator.getRelevantAtomContainer(reaction.getProducts(), bond);
    }

    public static void setAtomProperties(IReaction reaction, Object propKey, Object propVal) {
        IAtomContainerSet reactants = reaction.getReactants();
        for (int j = 0; j < reactants.getAtomContainerCount(); j++) {
            AtomContainerManipulator.setAtomProperties(reactants.getAtomContainer(j), propKey, propVal);
        }
        IAtomContainerSet products = reaction.getProducts();
        for (int j = 0; j < products.getAtomContainerCount(); j++) {
            AtomContainerManipulator.setAtomProperties(products.getAtomContainer(j), propKey, propVal);
        }
    }

    public static List<IChemObject> getAllChemObjects(IReaction reaction) {
        ArrayList<IChemObject> list = new ArrayList<>();
        list.add(reaction);
        IAtomContainerSet reactants = reaction.getReactants();
        for (int i = 0; i < reactants.getAtomContainerCount(); i++) {
            list.add(reactants.getAtomContainer(i));
        }
        IAtomContainerSet products = reaction.getProducts();
        for (int i = 0; i < products.getAtomContainerCount(); i++) {
            list.add(products.getAtomContainer(i));
        }
        return list;
    }

    /**
     * Returns the {@link IAtom} that is mapped.
     *
     * @param reaction   reaction that contains the mapping
     * @param chemObject IChemObject which will be searched for the mapped IChemObject
     * @return           mapped IChemObject
     */
    public static IChemObject getMappedChemObject(IReaction reaction, IChemObject chemObject) {
        for (IMapping mapping : reaction.mappings()) {
            if (mapping.getChemObject(0).equals(chemObject)) {
                return mapping.getChemObject(1);
            } else if (mapping.getChemObject(1).equals(chemObject))
                return mapping.getChemObject(0);
        }
        return null;
    }

    /**
     * Assigns a reaction role and group id to all atoms in a molecule.
     *
     * @param mol the molecule whose atoms are assigned to a role and a group id.
     * @param role role to assign
     * @param grpId group id to assign
     */
    private static void assignRoleAndGrp(IAtomContainer mol, ReactionRole role, int grpId) {
        for (IAtom atom : mol.atoms()) {
            atom.setProperty(CDKConstants.REACTION_ROLE, role);
            atom.setProperty(CDKConstants.REACTION_GROUP, grpId);
        }
    }

    /**
     * Converts a reaction to an 'inlined' reaction stored as a molecule.
     *
     * <p>
     * All reactants, agents, products are added to the molecule as disconnected
     * components with atoms flagged as to their role {@link ReactionRole} and
     * component group.
     * </p>
     * <p>
     * The inlined reaction, stored in a molecule can be converted back to an explicit
     * reaction with {@link #toReaction}. Data stored on the individual components (e.g.
     * titles is lost in the conversion).
     * </p>
     *
     * @param rxn reaction to convert
     * @return inlined reaction stored in a molecule
     * @see #toReaction
     */
    public static IAtomContainer toMolecule(IReaction rxn) {
        if (rxn == null)
            throw new IllegalArgumentException("Null reaction provided");
        final IChemObjectBuilder bldr = rxn.getBuilder();
        final IAtomContainer mol = bldr.newInstance(IAtomContainer.class);
        mol.setProperties(rxn.getProperties());
        mol.setID(rxn.getID());
        int grpId = 0;
        for (IAtomContainer comp : rxn.getReactants().atomContainers()) {
            assignRoleAndGrp(comp, ReactionRole.Reactant, ++grpId);
            mol.add(comp);
        }
        for (IAtomContainer comp : rxn.getAgents().atomContainers()) {
            assignRoleAndGrp(comp, ReactionRole.Agent, ++grpId);
            mol.add(comp);
        }
        for (IAtomContainer comp : rxn.getProducts().atomContainers()) {
            assignRoleAndGrp(comp, ReactionRole.Product, ++grpId);
            mol.add(comp);
        }
        return mol;
    }

    /**
     * Converts an 'inlined' reaction stored in a molecule back to a reaction.
     *
     * @param mol molecule to convert
     * @return reaction
     * @see #toMolecule(IReaction)
     */
    public static IReaction toReaction(IAtomContainer mol) {
        if (mol == null)
            throw new IllegalArgumentException("Null molecule provided");
        final IChemObjectBuilder bldr = mol.getBuilder();
        final IReaction          rxn  = bldr.newInstance(IReaction.class);
        rxn.setProperties(mol.getProperties());
        rxn.setID(mol.getID());

        Map<Integer,IAtomContainer> components = new HashMap<>();

        // split atoms
        for (IAtom atom : mol.atoms()) {
            ReactionRole role   = atom.getProperty(CDKConstants.REACTION_ROLE);
            Integer      grpIdx = atom.getProperty(CDKConstants.REACTION_GROUP);

            if (role == null || role == ReactionRole.None)
                throw new IllegalArgumentException("Atom " + mol.indexOf(atom) + " had undefined role");
            if (grpIdx == null)
                throw new IllegalArgumentException("Atom " + mol.indexOf(atom) + " had no reaction group id");

            IAtomContainer comp = components.get(grpIdx);

            // new component, and add to appropriate role
            if (comp == null) {
                components.put(grpIdx, comp = bldr.newInstance(IAtomContainer.class, 20, 20, 0, 0));
                switch (role) {
                    case Reactant:
                        rxn.addReactant(comp);
                        break;
                    case Product:
                        rxn.addProduct(comp);
                        break;
                    case Agent:
                        rxn.addAgent(comp);
                        break;
                }
            }

            comp.addAtom(atom);
        }

        // split bonds
        for (IBond bond : mol.bonds()) {
            IAtom beg = bond.getBegin();
            IAtom end = bond.getEnd();
            Integer begIdx = beg.getProperty(CDKConstants.REACTION_GROUP);
            Integer endIdx = end.getProperty(CDKConstants.REACTION_GROUP);
            if (begIdx == null || endIdx == null)
                throw new IllegalArgumentException("Bond " + mol.indexOf(bond) + " had atoms with no reaction group id");
            if (!begIdx.equals(endIdx))
                throw new IllegalArgumentException("Bond " + mol.indexOf(bond) + " had atoms with different reaction group id");
            components.get(begIdx).addBond(bond);
        }

        // split stereochemistry
        for (IStereoElement se : mol.stereoElements()) {
            IAtom focus = null;
            if (se instanceof ITetrahedralChirality) {
                focus = ((ITetrahedralChirality) se).getChiralAtom();
            } else if (se instanceof IDoubleBondStereochemistry) {
                focus = ((IDoubleBondStereochemistry) se).getStereoBond().getBegin();
            } else if (se instanceof ExtendedTetrahedral) {
                focus = ((ExtendedTetrahedral) se).focus();
            }
            if (focus == null)
                throw new IllegalArgumentException("Stereochemistry had no focus");
            Integer grpIdx = focus.getProperty(CDKConstants.REACTION_GROUP);
            components.get(grpIdx).addStereoElement(se);
        }

        for (ISingleElectron se : mol.singleElectrons()) {
            Integer grpIdx = se.getAtom().getProperty(CDKConstants.REACTION_GROUP);
            components.get(grpIdx).addSingleElectron(se);
        }

        for (ILonePair lp : mol.lonePairs()) {
            Integer grpIdx = lp.getAtom().getProperty(CDKConstants.REACTION_GROUP);
            components.get(grpIdx).addLonePair(lp);
        }

        return rxn;
    }

    /**
     * Bi-direction int-tuple for looking up bonds by index.
     */
    private static final class IntTuple {
        private final int beg, end;

        public IntTuple(int beg, int end) {
            this.beg = beg;
            this.end = end;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            IntTuple that = (IntTuple) o;
            return (this.beg == that.beg && this.end == that.end) ||
                   (this.beg == that.end && this.end == that.beg);
        }

        @Override
        public int hashCode() {
            return beg ^ end;
        }
    }

    /**
     * Collect the set of bonds that mapped in both a reactant and a product.
     * The method uses the {@link CDKConstants#ATOM_ATOM_MAPPING} property of atoms.
     *
     * @param reaction reaction
     * @return mapped bonds
     */
    public static Set<IBond> findMappedBonds(IReaction reaction) {
        Set<IBond> mapped = new HashSet<>();

        // first we collect the occurrance of mapped bonds from reacants then products
        Set<IntTuple> mappedReactantBonds = new HashSet<>();
        Set<IntTuple> mappedProductBonds  = new HashSet<>();
        for (IAtomContainer reactant : reaction.getReactants().atomContainers()) {
            for (IBond bond : reactant.bonds()) {
                Integer begidx = bond.getBegin().getProperty(CDKConstants.ATOM_ATOM_MAPPING);
                Integer endidx = bond.getEnd().getProperty(CDKConstants.ATOM_ATOM_MAPPING);
                if (begidx != null && endidx != null)
                    mappedReactantBonds.add(new IntTuple(begidx, endidx));
            }
        }
        // fail fast
        if (mappedReactantBonds.isEmpty())
            return Collections.emptySet();

        for (IAtomContainer product : reaction.getProducts().atomContainers()) {
            for (IBond bond : product.bonds()) {
                Integer begidx = bond.getBegin().getProperty(CDKConstants.ATOM_ATOM_MAPPING);
                Integer endidx = bond.getEnd().getProperty(CDKConstants.ATOM_ATOM_MAPPING);
                if (begidx != null && endidx != null)
                    mappedProductBonds.add(new IntTuple(begidx, endidx));
            }
        }
        // fail fast
        if (mappedProductBonds.isEmpty())
            return Collections.emptySet();

        // repeat above but now store any that are different or unmapped as being mapped
        for (IAtomContainer reactant : reaction.getReactants().atomContainers()) {
            for (IBond bond : reactant.bonds()) {
                Integer begidx = bond.getBegin().getProperty(CDKConstants.ATOM_ATOM_MAPPING);
                Integer endidx = bond.getEnd().getProperty(CDKConstants.ATOM_ATOM_MAPPING);
                if (begidx != null && endidx != null && mappedProductBonds.contains(new IntTuple(begidx, endidx)))
                    mapped.add(bond);
            }
        }
        for (IAtomContainer product : reaction.getProducts().atomContainers()) {
            for (IBond bond : product.bonds()) {
                Integer begidx = bond.getBegin().getProperty(CDKConstants.ATOM_ATOM_MAPPING);
                Integer endidx = bond.getEnd().getProperty(CDKConstants.ATOM_ATOM_MAPPING);
                if (begidx != null && endidx != null && mappedReactantBonds.contains(new IntTuple(begidx, endidx)))
                    mapped.add(bond);
            }
        }
        return mapped;
    }

    /**
     * Convenience method to perceive atom types for all {@link IAtom IAtoms} of all components of the provided {@link IReaction}.
     * This method uses the {@link CDKAtomTypeMatcher}. If the
     * matcher finds a matching atom type, the <code>IAtom</code> will be configured
     * to have the same properties as the <code>IAtomType</code>. If no matching atom
     * type is found, no configuration is performed.
     * <br>
     * <b>This method overwrites existing values.</b>
     *
     * @param reaction the reaction whose atom types are to be perceived
     * @throws CDKException thrown if an error is encountered when finding matching atom types
     * @see AtomTypeManipulator#configure(IAtom, IAtomType)
     */
    public static void perceiveAtomTypesAndConfigureAtoms(IReaction reaction) throws CDKException {
        if (reaction == null) {
            return;
        }

        for (IAtomContainer atomContainer: getAllMolecules(reaction).atomContainers()) {
            AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(atomContainer);
        }
    }

    /**
     * Convenience method to perceive atom types for all {@link IAtom IAtoms} of all components of the provided {@link IReaction}.
     * This method uses the {@link CDKAtomTypeMatcher}. If the
     * matcher finds a matching atom type, the <code>IAtom</code> will be configured
     * to have the same properties as the <code>IAtomType</code>. If no matching atom
     * type is found, no configuration is performed.
     * <br>
     * <b>This method only sets <code>null</code> values.</b>
     *
     * @param reaction the reaction whose atom types are to be perceived
     * @throws CDKException thrown if an error is encountered when finding matching atom types
     * @see AtomTypeManipulator#configureUnsetProperties(IAtom, IAtomType)
     */
    public static void perceiveAtomTypesAndConfigureUnsetProperties(IReaction reaction) throws CDKException {
        if (reaction == null) {
            return;
        }

        for (IAtomContainer atomContainer: getAllMolecules(reaction).atomContainers()) {
            AtomContainerManipulator.percieveAtomTypesAndConfigureUnsetProperties(atomContainer);
        }
    }

    /**
     * This method will reset all atom properties related to atom configuration to the value {@link CDKConstants#UNSET}.
     * <br>
     * This method reverses most of the effects of
     * {@link #perceiveAtomTypesAndConfigureAtoms(org.openscience.cdk.interfaces.IReaction)}
     * and after a call to this method all atoms will be "unconfigured".
     * <br>
     * Note that this method is not a complete reversal of {@link #perceiveAtomTypesAndConfigureAtoms(org.openscience.cdk.interfaces.IReaction)}
     * since the atomic symbol of the atoms remain unchanged. Also, all flags that were set
     * by the configuration method (such as {@link CDKConstants#IS_HYDROGENBOND_ACCEPTOR} or
     * {@link CDKConstants#ISAROMATIC}) will be set to False.
     *
     * @param reaction the reaction whose atoms confiuration properties are to be cleared
     * @see #perceiveAtomTypesAndConfigureAtoms(org.openscience.cdk.interfaces.IReaction)
     */
    public static void clearAtomConfigurations(IReaction reaction) {
        if (reaction == null) {
            return;
        }

        for (IAtomContainer atomContainer: ReactionManipulator.getAllMolecules(reaction).atomContainers()) {
            AtomContainerManipulator.clearAtomConfigurations(atomContainer);
        }
    }

}
