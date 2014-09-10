/* Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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
 *
 */
package org.openscience.cdk.ringsearch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.RingSetManipulator;

/**
 * Finds the Smallest Set of Smallest Rings.
 * This is an implementation of the algorithm published in
 * {@cdk.cite FIG96}.
 *
 * <p>The {@link SSSRFinder} is encouraged to be used, providing an exact
 * algorithm for finding the SSSR.
 *
 * @cdk.module extra
 * @cdk.githash
 * @cdk.keyword smallest-set-of-rings
 * @cdk.keyword ring search
 * @cdk.dictref blue-obelisk:findSmallestSetOfSmallestRings_Figueras
 *
 * @deprecated Use SSSRFinder instead (exact algorithm).
 */
public class FiguerasSSSRFinder {

    private static ILoggingTool logger      = LoggingToolFactory.createLoggingTool(FiguerasSSSRFinder.class);

    int                         trimCounter = 0;
    private static final String PATH        = "org.openscience.cdk.ringsearch.FiguerasSSSRFinderRFinder.PATH";

    /**
     * Finds the Smallest Set of Smallest Rings.
     *
     * @param   mol the molecule to be searched for rings
     * @return      a RingSet containing the rings in molecule
     */
    public IRingSet findSSSR(IAtomContainer mol) {
        IBond brokenBond = null;
        IChemObjectBuilder builder = mol.getBuilder();
        IRingSet sssr = builder.newInstance(IRingSet.class);
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.add(mol);
        IAtom smallest;
        int smallestDegree, nodesToBreakCounter, degree;
        IAtom[] rememberNodes;
        IRing ring;

        //Two Vectors - as defined in the article. One to hold the
        //full set of atoms in the structure and on to store the numbers
        //of the nodes that have been trimmed away.
        //Furhter there is a Vector nodesN2 to store the number of N2 nodes
        List<IAtom> fullSet = new ArrayList<IAtom>();
        List<IAtom> trimSet = new ArrayList<IAtom>();
        List<IAtom> nodesN2 = new ArrayList<IAtom>();

        initPath(molecule);
        logger.debug("molecule.getAtomCount(): " + molecule.getAtomCount());
        // load fullSet with the numbers of our atoms
        for (int f = 0; f < molecule.getAtomCount(); f++) {
            fullSet.add(molecule.getAtom(f));
        }
        logger.debug("fullSet.size(): " + fullSet.size());

        do {
            //Add nodes of degree zero to trimset.
            //Also add nodes of degree 2 to nodesN2.
            //In the same run, check, which node has the lowest degree
            //greater than zero.
            smallestDegree = 7;
            smallest = null;
            nodesN2.clear();
            for (int f = 0; f < molecule.getAtomCount(); f++) {
                IAtom atom = molecule.getAtom(f);
                degree = molecule.getConnectedBondsCount(atom);
                if (degree == 0) {
                    if (!trimSet.contains(atom)) {
                        logger.debug("Atom of degree 0");
                        trimSet.add(atom);
                    }
                }
                if (degree == 2) {
                    nodesN2.add(atom);
                }
                if (degree < smallestDegree && degree > 0) {
                    smallest = atom;
                    smallestDegree = degree;
                }
            }
            if (smallest == null) break;

            // If there are nodes of degree 1, trim them away
            if (smallestDegree == 1) {
                trimCounter++;
                trim(smallest, molecule);
                trimSet.add(smallest);
            }

            // if there are nodes of degree 2, find out of which rings
            // they are part of.
            else if (smallestDegree == 2) {
                rememberNodes = new IAtom[nodesN2.size()];
                nodesToBreakCounter = 0;
                for (int f = 0; f < nodesN2.size(); f++) {
                    ring = getRing((IAtom) nodesN2.get(f), molecule);
                    if (ring != null) {
                        // check, if this ring already is in SSSR
                        if (!RingSetManipulator.ringAlreadyInSet(ring, sssr)) {
                            sssr.addAtomContainer(ring);
                            rememberNodes[nodesToBreakCounter] = (IAtom) nodesN2.get(f);
                            nodesToBreakCounter++;
                        }
                    }
                }
                if (nodesToBreakCounter == 0) {
                    nodesToBreakCounter = 1;
                    rememberNodes[0] = (IAtom) nodesN2.get(0);
                }
                for (int f = 0; f < nodesToBreakCounter; f++) {
                    breakBond(rememberNodes[f], molecule);
                }
                if (brokenBond != null) {
                    molecule.addBond(brokenBond);
                    brokenBond = null;
                }
            }
            // if there are nodes of degree 3
            else if (smallestDegree == 3) {
                ring = getRing(smallest, molecule);
                if (ring != null) {

                    // check, if this ring already is in SSSR
                    if (!RingSetManipulator.ringAlreadyInSet(ring, sssr)) {
                        sssr.addAtomContainer(ring);
                    }
                    brokenBond = checkEdges(ring, molecule);
                    molecule.removeElectronContainer(brokenBond);
                }
            }
        } while (trimSet.size() < fullSet.size());
        logger.debug("fullSet.size(): " + fullSet.size());
        logger.debug("trimSet.size(): " + trimSet.size());
        logger.debug("trimCounter: " + trimCounter);
        //		molecule.setProperty(CDKConstants.SMALLEST_RINGS, sssr);
        return sssr;
    }

    /**
     * This routine is called 'getRing() in Figueras original article
     * finds the smallest ring of which rootNode is part of.
     *
     * @param   rootNode  The Atom to be searched for the smallest ring it is part of
     * @param   molecule  The molecule that contains the rootNode
     * @return     The smallest Ring rootnode is part of
     */
    private IRing getRing(IAtom rootNode, IAtomContainer molecule) {
        IAtom node, neighbor, mAtom;
        List neighbors, mAtoms;
        /** OKatoms is Figueras nomenclature, giving the number of
            atoms in the structure */
        int OKatoms = molecule.getAtomCount();
        /** queue for Breadth First Search of this graph */
        Queue queue = new Queue();
        /* Initialize a path Vector for each node */
        //Vector pfad1,pfad2;
        List<List<IAtom>> path = new ArrayList<List<IAtom>>(OKatoms);
        List<IAtom> intersection = new ArrayList<IAtom>();
        List<IAtom> ring = new ArrayList<IAtom>();
        for (final IAtom atom : molecule.atoms()) {
            path.add(new ArrayList<IAtom>());
            atom.getProperty(PATH, List.class).clear();
        }
        // Initialize the queue with nodes attached to rootNode
        neighbors = molecule.getConnectedAtomsList(rootNode);
        for (int f = 0; f < neighbors.size(); f++) {
            //if the degree of the f-st neighbor of rootNode is greater
            //than zero (i.e., it has not yet been deleted from the list)
            neighbor = (IAtom) neighbors.get(f);
            // push the f-st node onto our FIFO queue
            // after assigning rootNode as its source
            queue.push(neighbor);
            ((List<IAtom>) neighbor.getProperty(PATH)).add(rootNode);
            ((List<IAtom>) neighbor.getProperty(PATH)).add(neighbor);
        }
        while (queue.size() > 0) {
            node = (IAtom) queue.pop();
            mAtoms = molecule.getConnectedAtomsList(node);
            for (int f = 0; f < mAtoms.size(); f++) {
                mAtom = (IAtom) mAtoms.get(f);
                if (mAtom != ((List) node.getProperty(PATH)).get(((List<IAtom>) node.getProperty(PATH)).size() - 2)) {
                    if (((List) mAtom.getProperty(PATH)).size() > 0) {
                        intersection = getIntersection((List) node.getProperty(PATH), (List) mAtom.getProperty(PATH));
                        if (intersection.size() == 1) {
                            // we have found a valid ring closure
                            // now let's prepare the path to
                            // return in tempAtomSet
                            logger.debug("path1  ", ((List) node.getProperty(PATH)));
                            logger.debug("path2  ", ((List) mAtom.getProperty(PATH)));
                            logger.debug("rootNode  ", rootNode);
                            logger.debug("ring   ", ring);
                            ring = getUnion((List) node.getProperty(PATH), (List) mAtom.getProperty(PATH));
                            return prepareRing(ring, molecule);
                        }
                    } else {
                        // if path[mNumber] is null
                        // update the path[mNumber]
                        //pfad2 = (Vector)node.getProperty(PATH);
                        mAtom.setProperty(PATH, new ArrayList<IAtom>((List<IAtom>) node.getProperty(PATH)));
                        ((List<IAtom>) mAtom.getProperty(PATH)).add(mAtom);
                        //pfad1 = (Vector)mAtom.getProperty(PATH);
                        // now push the node m onto the queue
                        queue.push(mAtom);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Returns the ring that is formed by the atoms in the given vector.
     *
     * @param   vec  The vector that contains the atoms of the ring
     * @param   mol  The molecule this ring is a substructure of
     * @return     The ring formed by the given atoms
     */
    private IRing prepareRing(List vec, IAtomContainer mol) {
        // add the atoms in vec to the new ring
        int atomCount = vec.size();
        IRing ring = mol.getBuilder().newInstance(IRing.class, atomCount);
        IAtom[] atoms = new IAtom[atomCount];
        vec.toArray(atoms);
        ring.setAtoms(atoms);
        // add the bonds in mol to the new ring
        try {
            IBond b;
            for (int i = 0; i < atomCount - 1; i++) {
                b = mol.getBond(atoms[i], atoms[i + 1]);
                if (b != null) {
                    ring.addBond(b);
                } else {
                    logger.error("This should not happen.");
                }
            }
            b = mol.getBond(atoms[0], atoms[atomCount - 1]);
            if (b != null) {
                ring.addBond(b);
            } else {
                logger.error("This should not happen either.");
            }
        } catch (Exception exc) {
            logger.debug(exc);
        }
        logger.debug("found Ring  ", ring);
        return ring;
    }

    /**
     * removes all bonds connected to the given atom leaving it with degree zero.
     *
     * @param   atom  The atom to be disconnecred
     * @param   molecule  The molecule containing the atom
     */
    private void trim(IAtom atom, IAtomContainer molecule) {
        List<IBond> bonds = molecule.getConnectedBondsList(atom);
        for (int i = 0; i < bonds.size(); i++) {
            molecule.removeElectronContainer((IBond) bonds.get(i));
        }
        // you are erased! Har, har, har.....  >8-)
    }

    /**
     * initializes a path vector in every Atom of the given molecule
     *
     * @param   molecule  The given molecule
     */
    private void initPath(IAtomContainer molecule) {
        for (int i = 0; i < molecule.getAtomCount(); i++) {
            IAtom atom = molecule.getAtom(i);
            atom.setProperty(PATH, new ArrayList<IAtom>());
        }
    }

    /**
     * Returns a Vector that contains the intersection of Vectors vec1 and vec2
     *
     * @param   list1   The first vector
     * @param   list2   The second vector
     * @return the intersection of the two list
     */
    private List getIntersection(List<IAtom> list1, List<IAtom> list2) {
        List is = new ArrayList<IAtom>();
        for (int f = 0; f < list1.size(); f++) {
            if (list2.contains(list1.get(f))) is.add(list1.get(f));
        }
        return is;
    }

    /**
     * Returns a Vector that contains the union of Vectors vec1 and vec2
     *
     * @param   list1  The first vector
     * @param   list2  The second vector
     * @return the union of the two list
     */
    private List<IAtom> getUnion(List<IAtom> list1, List<IAtom> list2) {
        // FIXME: the JavaDoc does not describe what happens: that vec1 gets to be the union!
        // jm: pretty sure retainAll would do the trick here but don't want to change the
        //     functionality as item only present in list1 are not removed (i.e. not union)
        List<IAtom> is = new ArrayList<IAtom>(list1);
        for (int f = list2.size() - 1; f > -1; f--) {
            if (!list1.contains(list2.get(f))) is.add(list2.get(f));
        }
        return is;
    }

    /**
     * Eliminates one bond of this atom from the molecule
     *
     * @param   atom  The atom one bond is eliminated of
     * @param   molecule  The molecule that contains the atom
     */
    private void breakBond(IAtom atom, IAtomContainer molecule) {
        Iterator<IBond> bonds = molecule.bonds().iterator();
        while (bonds.hasNext()) {
            IBond bond = (IBond) bonds.next();
            if (bond.contains(atom)) {
                molecule.removeElectronContainer(bond);
                break;
            }
        }
    }

    /**
     * Selects an optimum edge for elimination in structures without N2 nodes.
     *
     * <p>This might be severely broken! Would have helped if there was an
     * explanation of how this algorithm worked.
     *
     * @param   ring
     * @param   molecule
     */
    private IBond checkEdges(IRing ring, IAtomContainer molecule) {
        IRing r1, r2;
        IRingSet ringSet = ring.getBuilder().newInstance(IRingSet.class);
        IBond bond;
        int minMaxSize = Integer.MAX_VALUE;
        int minMax = 0;
        logger.debug("Molecule: " + molecule);
        Iterator<IBond> bonds = ring.bonds().iterator();
        while (bonds.hasNext()) {
            bond = (IBond) bonds.next();
            molecule.removeElectronContainer(bond);
            r1 = getRing(bond.getAtom(0), molecule);
            r2 = getRing(bond.getAtom(1), molecule);
            logger.debug("checkEdges: " + bond);
            if (r1.getAtomCount() > r2.getAtomCount()) {
                ringSet.addAtomContainer(r1);
            } else {
                ringSet.addAtomContainer(r2);
            }
            molecule.addBond(bond);
        }
        for (int i = 0; i < ringSet.getAtomContainerCount(); i++) {
            if (((IRing) ringSet.getAtomContainer(i)).getBondCount() < minMaxSize) {
                minMaxSize = ((IRing) ringSet.getAtomContainer(i)).getBondCount();
                minMax = i;
            }
        }
        return (IBond) ring.getElectronContainer(minMax);
    }

}
