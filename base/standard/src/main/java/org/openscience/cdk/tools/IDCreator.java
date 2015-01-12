/* Copyright (C) 2003-2007  Egon Willighagen <egonw@users.sf.net>
 *                    2008  Aleksey Tarkhov <bayern7105@yahoo.de>
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
 */
package org.openscience.cdk.tools;

import java.util.Iterator;
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.ICrystal;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.AtomContainerSetManipulator;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.cdk.tools.manipulator.ChemSequenceManipulator;
import org.openscience.cdk.tools.manipulator.ReactionManipulator;
import org.openscience.cdk.tools.manipulator.ReactionSetManipulator;

/**
 * Class that provides methods to give unique IDs to ChemObjects.
 * Methods are implemented for Atom, Bond, AtomContainer, AtomContainerSet
 * and Reaction. It will only create missing IDs. If you want to create new
 * IDs for all ChemObjects, you need to delete them first.
 *
 * @cdk.module standard
 * @cdk.githash
 *
 * @author   Egon Willighagen
 * @cdk.created  2003-04-01
 *
 * @cdk.keyword  id, creation
 */
public abstract class IDCreator {

    // counters for generated in current session IDs
    private static int          reactionCount           = 0;
    private static int          atomCount               = 0;
    private static int          bondCount               = 0;
    private static int          atomContainerCount      = 0;
    private static int          atomContainerSetCount   = 0;
    private static int          reactionSetCount        = 0;
    private static int          chemModelCount          = 0;
    private static int          chemSequenceCount       = 0;
    private static int          chemFileCount           = 0;

    // prefix to prepend every individual IDs
    private final static String REACTION_PREFIX         = "r";
    private final static String ATOM_PREFIX             = "a";
    private final static String BOND_PREFIX             = "b";
    private final static String ATOMCONTAINER_PREFIX    = "m";
    private final static String ATOMCONTAINERSET_PREFIX = "molSet";
    private final static String REACTIONSET_PREFIX      = "rset";
    private final static String CHEMMODEL_PREFIX        = "model";
    private final static String CHEMSEQUENCE_PREFIX     = "seq";
    private final static String CHEMFILE_PREFIX         = "file";

    /**
     * Old ID generation policy - to generate IDs unique over the entire set
     */
    public static final int     SET_UNIQUE_POLICY       = 0;

    /**
     * New ID generation policy - to generate IDs unique only in a molecule
     */
    public static final int     OBJECT_UNIQUE_POLICY    = 1;

    /**
     * Internal flag identifying the IDs generation policy. The old policy
     * is to generate IDs so that in a sequence of several molecules all the
     * atoms and bonds will receive the unique IDs even across molecules, i.e.
     * in a set of 2 molecules the first atom of the first molecule will be "a1"
     * while the first atom of the second molecule will be "aX" where X equals
     * to the number of atoms in the first molecule plus 1.
     * <br/>
     * The new policy is to keep the singularity of IDs only within a single
     * molecule, i.e. in a set of two molecules first atoms of each will be "a1".
     */
    private static int          policy                  = SET_UNIQUE_POLICY;

    /**
     * Alters the policy of ID generation. The IDCreator should in any case
     * preserve the already existing IDs therefore if one of objects already
     * has an ID set, this ID will be skipped in all the cases when attempting to
     * generate a new ID value
     * @param policy new policy to be used
     * @see #OBJECT_UNIQUE_POLICY
     * @see #SET_UNIQUE_POLICY
     */
    public static void setIDPolicy(int policy) {
        IDCreator.policy = policy;
    }

    /**
     * Labels the Atom's and Bond's in the AtomContainer using the a1, a2, b1, b2
     * scheme often used in CML. Supports IAtomContainer, IAtomContainerSet,
     * IChemFile, IChemModel, IChemSequence, IReaction, IReactionSet,
     * and derived interfaces.
     *
     * @param  chemObject IChemObject to create IDs for.
     */
    public static void createIDs(IChemObject chemObject) {
        if (chemObject == null) return;

        resetCounters();

        if (chemObject instanceof IAtomContainer) {
            createIDsForAtomContainer((IAtomContainer) chemObject, null);
        } else if (chemObject instanceof IAtomContainerSet) {
            createIDsForAtomContainerSet((IAtomContainerSet) chemObject, null);
        } else if (chemObject instanceof IReaction) {
            createIDsForReaction((IReaction) chemObject, null);
        } else if (chemObject instanceof IReactionSet) {
            createIDsForReactionSet((IReactionSet) chemObject, null);
        } else if (chemObject instanceof IChemFile) {
            createIDsForChemFile((IChemFile) chemObject, null);
        } else if (chemObject instanceof IChemSequence) {
            createIDsForChemSequence((IChemSequence) chemObject, null);
        } else if (chemObject instanceof IChemModel) {
            createIDsForChemModel((IChemModel) chemObject, null);
        }
    }

    /**
     * Reset the counters so that we keep generating simple IDs within
     * single chem object or a set of them
     */
    private static void resetCounters() {
        atomCount = 0;
        bondCount = 0;
        atomContainerCount = 0;
        atomContainerSetCount = 0;
        reactionCount  = 0;
        reactionSetCount = 0;
        chemModelCount = 0;
        chemSequenceCount = 0;
        chemFileCount = 0;
    }

    /**
     * Sets the ID on the object and adds it to the tabu list.
     *
     * @param object   IChemObject to set the ID for
     * @param tabuList Tabu list to add the ID to
     */
    private static int setID(String prefix, int identifier, IChemObject object, List<String> tabuList) {
        identifier += 1;
        while (tabuList.contains(prefix + identifier)) {
            identifier += 1;
        }
        object.setID(prefix + identifier);
        tabuList.add(prefix + identifier);
        return identifier;
    }

    /**
     * Labels the Atom's and Bond's in the AtomContainer using the a1, a2, b1, b2
     * scheme often used in CML.
     *
     * @see #createIDs(org.openscience.cdk.interfaces.IChemObject)
     */
    private static void createIDsForAtomContainer(IAtomContainer container, List<String> tabuList) {
        if (tabuList == null) tabuList = AtomContainerManipulator.getAllIDs(container);

        if (null == container.getID()) {
            // generate new ID and remember it
            atomContainerCount = setID(ATOMCONTAINER_PREFIX, atomContainerCount, container, tabuList);
        }

        // the tabu list for the container should force singularity
        // within a container only!
        List<String> internalTabuList = AtomContainerManipulator.getAllIDs(container);
        if (policy == OBJECT_UNIQUE_POLICY) {
            // start atom and bond indices within a container set always from 1
            atomCount = 0;
            bondCount = 0;
        } else {
            internalTabuList = tabuList;
        }

        Iterator<IAtom> atoms = container.atoms().iterator();
        while (atoms.hasNext()) {
            IAtom atom = atoms.next();
            if (null == atom.getID()) {
                atomCount = setID(ATOM_PREFIX, atomCount, atom, internalTabuList);
            }
        }

        Iterator<IBond> bonds = container.bonds().iterator();
        while (bonds.hasNext()) {
            IBond bond = bonds.next();
            if (null == bond.getID()) {
                bondCount = setID(BOND_PREFIX, bondCount, bond, internalTabuList);
            }
        }
    }

    /**
     * Labels the Atom's and Bond's in each AtomContainer using the a1, a2, b1, b2
     * scheme often used in CML. It will also set id's for all AtomContainers, naming
     * them m1, m2, etc.
     * It will not the AtomContainerSet itself.
     */
    private static void createIDsForAtomContainerSet(IAtomContainerSet containerSet, List<String> tabuList) {
        if (tabuList == null) tabuList = AtomContainerSetManipulator.getAllIDs(containerSet);

        if (null == containerSet.getID()) {
            atomContainerSetCount = setID(ATOMCONTAINERSET_PREFIX, atomContainerSetCount, containerSet, tabuList);
        }

        if (policy == OBJECT_UNIQUE_POLICY) {
            // start atom and bond indices within a container set always from 1
            atomCount = 0;
            bondCount = 0;
        }

        Iterator<IAtomContainer> acs = containerSet.atomContainers().iterator();
        while (acs.hasNext()) {
            createIDsForAtomContainer((IAtomContainer) acs.next(), tabuList);
        }
    }

    /**
     * Labels the reactants and products in the Reaction m1, m2, etc, and the atoms
     * accordingly, when no ID is given.
     */
    private static void createIDsForReaction(IReaction reaction, List<String> tabuList) {
        if (tabuList == null) tabuList = ReactionManipulator.getAllIDs(reaction);

        if (null == reaction.getID()) {
            // generate new ID
            reactionCount  = setID(REACTION_PREFIX, reactionCount , reaction, tabuList);
        }

        if (policy == OBJECT_UNIQUE_POLICY) {
            // start atom and bond indices within a reaction set always from 1
            atomCount = 0;
            bondCount = 0;
        }

        for (IAtomContainer reactant : reaction.getReactants().atomContainers()) {
            createIDsForAtomContainer(reactant, tabuList);
        }
        for (IAtomContainer product : reaction.getReactants().atomContainers()) {
            createIDsForAtomContainer(product, tabuList);
        }
        Iterator<IAtomContainer> agents = reaction.getAgents().atomContainers().iterator();
        while (agents.hasNext()) {
            createIDsForAtomContainer((IAtomContainer) agents.next(), tabuList);
        }
    }

    private static void createIDsForReactionSet(IReactionSet reactionSet, List<String> tabuList) {
        if (tabuList == null) tabuList = ReactionSetManipulator.getAllIDs(reactionSet);

        if (null == reactionSet.getID()) {
            // generate new ID for the set
            reactionSetCount = setID(REACTIONSET_PREFIX, reactionSetCount, reactionSet, tabuList);
        }

        for (Iterator<IReaction> reaction = reactionSet.reactions().iterator(); reaction.hasNext();) {
            createIDsForReaction(reaction.next(), tabuList);
        }
    }

    private static void createIDsForChemFile(IChemFile file, List<String> tabuList) {
        if (tabuList == null) tabuList = ChemFileManipulator.getAllIDs(file);

        if (null == file.getID()) {
            chemFileCount = setID(CHEMFILE_PREFIX, chemFileCount, file, tabuList);
        }

        if (policy == OBJECT_UNIQUE_POLICY) {
            // start indices within a chem file always from 1
            chemSequenceCount = 0;
        }

        for (IChemSequence chemSequence : file.chemSequences()) {
            createIDsForChemSequence(chemSequence, tabuList);
        }
    }

    private static void createIDsForChemSequence(IChemSequence sequence, List<String> tabuList) {
        if (tabuList == null) tabuList = ChemSequenceManipulator.getAllIDs(sequence);

        if (null == sequence.getID()) {
            chemSequenceCount = setID(CHEMSEQUENCE_PREFIX, chemSequenceCount, sequence, tabuList);
        }

        if (policy == OBJECT_UNIQUE_POLICY) {
            // start indices within a chem file always from 1
            chemSequenceCount = 0;
        }

        for (IChemModel chemModel : sequence.chemModels()) {
            createIDsForChemModel(chemModel, tabuList);
        }
    }

    private static void createIDsForChemModel(IChemModel model, List<String> tabuList) {
        if (tabuList == null) tabuList = ChemModelManipulator.getAllIDs(model);

        if (null == model.getID()) {
            chemModelCount = setID(CHEMMODEL_PREFIX, chemModelCount, model, tabuList);
        }

        ICrystal crystal = model.getCrystal();
        if (crystal != null) {
            if (policy == OBJECT_UNIQUE_POLICY) {
                atomCount = 0;
                bondCount = 0;
            }
            createIDsForAtomContainer(crystal, tabuList);
        }

        IAtomContainerSet moleculeSet = model.getMoleculeSet();
        if (moleculeSet != null) {
            if (policy == OBJECT_UNIQUE_POLICY) {
                atomContainerSetCount = 0;
                atomContainerCount = 0;
            }
            createIDsForAtomContainerSet(moleculeSet, tabuList);
        }

        IReactionSet reactionSet = model.getReactionSet();
        if (reactionSet != null) {
            if (policy == OBJECT_UNIQUE_POLICY) {
                reactionSetCount = 0;
                reactionCount  = 0;
            }
            createIDsForReactionSet(reactionSet, tabuList);
        }
    }

}
