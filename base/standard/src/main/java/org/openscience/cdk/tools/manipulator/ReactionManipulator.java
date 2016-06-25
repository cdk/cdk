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
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IDoubleBondStereochemistry;
import org.openscience.cdk.interfaces.IElectronContainer;
import org.openscience.cdk.interfaces.IMapping;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.interfaces.ITetrahedralChirality;
import org.openscience.cdk.stereo.ExtendedTetrahedral;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @cdk.module standard
 * @cdk.githash
 *
 * @see ChemModelManipulator
 */
public class ReactionManipulator {

    public static int getAtomCount(IReaction reaction) {
        int count = 0;
        IAtomContainerSet reactants = reaction.getReactants();
        for (int i = 0; i < reactants.getAtomContainerCount(); i++) {
            count += reactants.getAtomContainer(i).getAtomCount();
        }
        IAtomContainerSet products = reaction.getProducts();
        for (int i = 0; i < products.getAtomContainerCount(); i++) {
            count += products.getAtomContainer(i).getAtomCount();
        }
        return count;
    }

    public static int getBondCount(IReaction reaction) {
        int count = 0;
        IAtomContainerSet reactants = reaction.getReactants();
        for (int i = 0; i < reactants.getAtomContainerCount(); i++) {
            count += reactants.getAtomContainer(i).getBondCount();
        }
        IAtomContainerSet products = reaction.getProducts();
        for (int i = 0; i < products.getAtomContainerCount(); i++) {
            count += products.getAtomContainer(i).getBondCount();
        }
        return count;
    }

    public static void removeAtomAndConnectedElectronContainers(IReaction reaction, IAtom atom) {
        IAtomContainerSet reactants = reaction.getReactants();
        for (int i = 0; i < reactants.getAtomContainerCount(); i++) {
            IAtomContainer mol = reactants.getAtomContainer(i);
            if (mol.contains(atom)) {
                mol.removeAtomAndConnectedElectronContainers(atom);
            }
        }
        IAtomContainerSet products = reaction.getProducts();
        for (int i = 0; i < products.getAtomContainerCount(); i++) {
            IAtomContainer mol = products.getAtomContainer(i);
            if (mol.contains(atom)) {
                mol.removeAtomAndConnectedElectronContainers(atom);
            }
        }
    }

    public static void removeElectronContainer(IReaction reaction, IElectronContainer electrons) {
        IAtomContainerSet reactants = reaction.getReactants();
        for (int i = 0; i < reactants.getAtomContainerCount(); i++) {
            IAtomContainer mol = reactants.getAtomContainer(i);
            if (mol.contains(electrons)) {
                mol.removeElectronContainer(electrons);
            }
        }
        IAtomContainerSet products = reaction.getProducts();
        for (int i = 0; i < products.getAtomContainerCount(); i++) {
            IAtomContainer mol = products.getAtomContainer(i);
            if (mol.contains(electrons)) {
                mol.removeElectronContainer(electrons);
            }
        }
    }

    /**
     * Get all molecule of a {@link IReaction}: reactants + products.
     *
     * @param reaction  The IReaction
     * @return The IAtomContainerSet
     */
    public static IAtomContainerSet getAllMolecules(IReaction reaction) {
        IAtomContainerSet moleculeSet = reaction.getBuilder().newInstance(IAtomContainerSet.class);

        moleculeSet.add(getAllReactants(reaction));
        moleculeSet.add(getAllProducts(reaction));

        return moleculeSet;
    }

    /**
     * get all products of a IReaction
     *
     * @param reaction  The IReaction
     * @return The IAtomContainerSet
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
     * get all reactants of a IReaction
     *
     * @param reaction  The IReaction
     * @return The IAtomContainerSet
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
     * Returns a new Reaction object which is the reverse of the given
     * Reaction.
     * @param reaction the reaction being considered
     * @return the reverse reaction
     */
    public static IReaction reverse(IReaction reaction) {
        IReaction reversedReaction = reaction.getBuilder().newInstance(IReaction.class);
        if (reaction.getDirection() == IReaction.Direction.BIDIRECTIONAL) {
            reversedReaction.setDirection(IReaction.Direction.BIDIRECTIONAL);
        } else if (reaction.getDirection() == IReaction.Direction.FORWARD) {
            reversedReaction.setDirection(IReaction.Direction.BACKWARD);
        } else if (reaction.getDirection() == IReaction.Direction.BACKWARD) {
            reversedReaction.setDirection(IReaction.Direction.FORWARD);
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
     * Returns all the AtomContainer's of a Reaction.
     * @param reaction The reaction being considered
     * @return a list of the IAtomContainer objects comprising the reaction
     */
    public static List<IAtomContainer> getAllAtomContainers(IReaction reaction) {
        return MoleculeSetManipulator.getAllAtomContainers(getAllMolecules(reaction));
    }

    public static List<String> getAllIDs(IReaction reaction) {
        List<String> idList = new ArrayList<String>();
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
        ArrayList<IChemObject> list = new ArrayList<IChemObject>();
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
     * get the IAtom which is mapped
     *
     * @param reaction   The IReaction which contains the mapping
     * @param chemObject The IChemObject which will be searched its mapped IChemObject
     * @return           The mapped IChemObject
     */
    public static IChemObject getMappedChemObject(IReaction reaction, IChemObject chemObject) {
        for (IMapping mapping : reaction.mappings()) {
            if (mapping.getChemObject(0).equals(chemObject)) {
                return mapping.getChemObject(1);
            } else if (mapping.getChemObject(1).equals(chemObject)) return mapping.getChemObject(0);
        }
        return null;
    }

    /**
     * Assigns a reaction role and group id to all atoms in a molecule.
     *
     * @param mol molecule
     * @param role role to assign
     * @param grpId group id
     */
    private static void assignRoleAndGrp(IAtomContainer mol, ReactionRole role, int grpId) {
        for (IAtom atom : mol.atoms()) {
            atom.setProperty(CDKConstants.REACTION_ROLE, role);
            atom.setProperty(CDKConstants.REACTION_GROUP, grpId);
        }
    }

    /**
     * <p>Converts a reaction to an 'inlined' reaction stored as a molecule. All
     * reactants, agents, products are added to the molecule as disconnected
     * components with atoms flagged as to their role {@link ReactionRole} and
     * component group.</p>
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
     * <p>Converts an 'inlined' reaction stored in a molecule back to a reaction.</p>
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
                throw new IllegalArgumentException("Atom " + mol.getAtomNumber(atom) + " had undefined role");
            if (grpIdx == null)
                throw new IllegalArgumentException("Atom " + mol.getAtomNumber(atom) + " had no reaction group id");

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
            IAtom beg = bond.getAtom(0);
            IAtom end = bond.getAtom(1);
            Integer begIdx = beg.getProperty(CDKConstants.REACTION_GROUP);
            Integer endIdx = end.getProperty(CDKConstants.REACTION_GROUP);
            if (begIdx == null || endIdx == null)
                throw new IllegalArgumentException("Bond " + mol.getBondNumber(bond) + " had atoms with no reaction group id");
            if (!begIdx.equals(endIdx))
                throw new IllegalArgumentException("Bond " + mol.getBondNumber(bond) + " had atoms with different reaction group id");
            components.get(begIdx).addBond(bond);
        }

        // split stereochemistry
        for (IStereoElement se : mol.stereoElements()) {
            IAtom focus = null;
            if (se instanceof ITetrahedralChirality) {
                focus = ((ITetrahedralChirality) se).getChiralAtom();
            } else if (se instanceof IDoubleBondStereochemistry) {
                focus = ((IDoubleBondStereochemistry) se).getStereoBond().getAtom(0);
            } else if (se instanceof ExtendedTetrahedral) {
                focus = ((ExtendedTetrahedral) se).focus();
            }
            if (focus == null)
                throw new IllegalArgumentException("Stereochemistry had no focus");
            Integer grpIdx = focus.getProperty(CDKConstants.REACTION_GROUP);
            components.get(grpIdx).addStereoElement(se);
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
     * Collect the set of bonds that mapped in both a reactant and a product. The method uses
     * the {@link CDKConstants#ATOM_ATOM_MAPPING} property of atoms.
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
                Integer begidx = bond.getAtom(0).getProperty(CDKConstants.ATOM_ATOM_MAPPING);
                Integer endidx = bond.getAtom(1).getProperty(CDKConstants.ATOM_ATOM_MAPPING);
                if (begidx != null && endidx != null)
                    mappedReactantBonds.add(new IntTuple(begidx, endidx));
            }
        }
        // fail fast
        if (mappedReactantBonds.isEmpty())
            return Collections.emptySet();

        for (IAtomContainer product : reaction.getProducts().atomContainers()) {
            for (IBond bond : product.bonds()) {
                Integer begidx = bond.getAtom(0).getProperty(CDKConstants.ATOM_ATOM_MAPPING);
                Integer endidx = bond.getAtom(1).getProperty(CDKConstants.ATOM_ATOM_MAPPING);
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
                Integer begidx = bond.getAtom(0).getProperty(CDKConstants.ATOM_ATOM_MAPPING);
                Integer endidx = bond.getAtom(1).getProperty(CDKConstants.ATOM_ATOM_MAPPING);
                if (begidx != null && endidx != null && mappedProductBonds.contains(new IntTuple(begidx, endidx)))
                    mapped.add(bond);
            }
        }
        for (IAtomContainer product : reaction.getProducts().atomContainers()) {
            for (IBond bond : product.bonds()) {
                Integer begidx = bond.getAtom(0).getProperty(CDKConstants.ATOM_ATOM_MAPPING);
                Integer endidx = bond.getAtom(1).getProperty(CDKConstants.ATOM_ATOM_MAPPING);
                if (begidx != null && endidx != null && mappedReactantBonds.contains(new IntTuple(begidx, endidx)))
                    mapped.add(bond);
            }
        }
        return mapped;
    }
}
