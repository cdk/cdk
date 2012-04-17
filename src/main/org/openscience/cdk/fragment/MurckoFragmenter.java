/* Copyright (C) 2010  Rajarshi Guha <rajarshi.guha@gmail.com>
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
package org.openscience.cdk.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.graph.PathTools;
import org.openscience.cdk.graph.SpanningTree;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.ringsearch.AllRingsFinder;
import org.openscience.cdk.smiles.SmilesGenerator;

/**
 * An implementation of the Murcko fragmenation method {@cdk.cite MURCKO96}.
 * <p/>
 * As an implementation of {@link IFragmenter} this class will return
 * the Murcko frameworks (i.e., ring systems + linkers) along with
 * the ring systems ia getFragments. The
 * class also provides methods to extract the ring systems and frameworks
 * separately. For all these methods, the user can retrieve the substructures
 * as canonical SMILES strings or as {@link IAtomContainer} objects.
 * <p/>
 * Note that in contrast to the original paper which implies that a single molecule
 * has a single framework, this class returns multiple frameworks consisting of all
 * combinations of ring systems and linkers. The "true" Murcko framework is simply
 * the largest framework.
 *
 * @author Rajarshi Guha
 * @cdk.module  fragment
 * @cdk.githash
 * @cdk.keyword fragment
 * @cdk.keyword framework
 * @see org.openscience.cdk.fragment.ExhaustiveFragmenter
 */
@TestClass("org.openscience.cdk.fragment.MurckoFragmenterTest")
public class MurckoFragmenter implements IFragmenter {
    private static final String IS_SIDECHAIN_ATOM = "sidechain";
    private static final String IS_LINKER_ATOM = "linker";

    SmilesGenerator smilesGenerator = new SmilesGenerator(true);
    IRingSet ringSet;

    Map<String, IAtomContainer> frameMap = new HashMap<String, IAtomContainer>();
    Map<String, IAtomContainer> ringMap = new HashMap<String, IAtomContainer>();

    SpanningTree spanningTree;

    boolean singleFrameworkOnly = false;
    int minimumFragmentSize = 5;

    /**
     * Instantiate Murcko fragmenter.
     * <p/>
     * Considers fragments with 5 or more atoms and generates multiple
     * frameworks if available.
     */
    @TestMethod("testMF1,testMF2,testMF3")
    public MurckoFragmenter() {
        this(false, 5);
    }

    /**
     * Instantiate Murcko fragmenter.
     *
     * @param singleFrameworkOnly if <code>true</code>, only the true Murcko framework is generated.
     * @param minimumFragmentSize the smallest size of fragment to consider
     */
    @TestMethod("testSingleFramework")
    public MurckoFragmenter(boolean singleFrameworkOnly, int minimumFragmentSize) {
        this.singleFrameworkOnly = singleFrameworkOnly;
        this.minimumFragmentSize = minimumFragmentSize;
    }

    /**
     * Perform the fragmentation procedure.
     *
     * @param atomContainer The input molecule
     * @throws CDKException
     */
    @TestMethod("testMF1, testMF2, testMF3, testMF4, testMF5, testMF6")
    public void generateFragments(IAtomContainer atomContainer) throws CDKException {
        frameMap.clear();
        ringMap.clear();
        run(atomContainer);
    }

    private void run(IAtomContainer atomContainer) throws CDKException {
        String smiles;

        // identify rings
        AllRingsFinder arf = new AllRingsFinder(false);
        arf.findAllRings(atomContainer);

        for (IAtom atom : atomContainer.atoms()) {
            atom.setProperty(IS_LINKER_ATOM, false);
            atom.setProperty(IS_SIDECHAIN_ATOM, false);
        }

        markLinkers(atomContainer);
        markSideChains(atomContainer);

        // need to keep the side chains somewhere
        IAtomContainer clone = removeSideChains(atomContainer);

        IAtomContainer currentFramework; // needed for recursion
        try {
            currentFramework = (IAtomContainer) clone.clone();
        } catch (CloneNotSupportedException exception) {
            throw new CDKException(exception.getMessage(), exception);
        }

        // only add this in if there is actually a framework
        // in some cases we might just have rings and sidechains
        if (hasframework(currentFramework)) {
            smiles = smilesGenerator.createSMILES(currentFramework);

            // if we only want the single framework according to Murcko, then
            // it was the first framework that is added, since subsequent recursive
            // calls will work on substructures of the original framework
            if (singleFrameworkOnly) {
                if (frameMap.size() == 0) {
                    frameMap.put(smiles, currentFramework);
                }
            } else frameMap.put(smiles, currentFramework);
        }

        // extract ring systems - we also delete pseudo linker bonds as described by
        // Murcko (since he notes that biphenyl has two separate ring systems)
        List<IAtom> atomsToDelete = new ArrayList<IAtom>();
        for (IAtom atom : clone.atoms()) {
            if (islinker(atom)) atomsToDelete.add(atom);
        }
        for (IAtom atom : atomsToDelete)
            clone.removeAtomAndConnectedElectronContainers(atom);

        List<IBond> bondsToDelete = new ArrayList<IBond>();
        for (IBond bond : clone.bonds()) {
            if (isZeroAtomLinker(bond, clone)) bondsToDelete.add(bond);
        }
        for (IBond bond : bondsToDelete) clone.removeBond(bond);

        // at this point, the ring systems are disconnected components
        IAtomContainerSet ringSystems = ConnectivityChecker.partitionIntoMolecules(clone);
        for (IAtomContainer ringSystem : ringSystems.atomContainers()) {
            if (ringSystem.getAtomCount() < minimumFragmentSize) continue;
            smiles = smilesGenerator.createSMILES(ringSystem);
            ringMap.put(smiles, ringSystem);
        }

        // if we didn't have a framework no sense going forward
        if (!hasframework(currentFramework)) return;

        // now we split this framework and recurse.
        assert currentFramework != null;
        for (IBond bond : currentFramework.bonds()) {
            if (islinker(bond) || isZeroAtomLinker(bond, currentFramework)) {
                List<IAtomContainer> candidates = FragmentUtils.splitMolecule(currentFramework, bond);
                for (IAtomContainer candidate : candidates) {

                    // clear any murcko related props we might have set in the molecule
                    // this candidate came from
                    for (IAtom atom : candidate.atoms()) {
                        atom.setProperty(IS_LINKER_ATOM, false);
                        atom.setProperty(IS_SIDECHAIN_ATOM, false);
                    }

                    markLinkers(candidate);
                    markSideChains(candidate);

                    // need to keep side chains at one ppint
                    candidate = removeSideChains(candidate);
                    if (hasframework(candidate) && candidate.getAtomCount() >= minimumFragmentSize) run(candidate);
                }
            }
        }
    }


    private IAtomContainer removeSideChains(IAtomContainer atomContainer) throws CDKException {
        IAtomContainer clone;
        try {
            clone = (IAtomContainer) atomContainer.clone();
        } catch (CloneNotSupportedException exception) {
            throw new CDKException("Error in clone" + exception.toString(), exception);
        }
        List<IAtom> atomsToDelete = new ArrayList<IAtom>();
        for (IAtom atom : clone.atoms()) {
            if (issidechain(atom)) atomsToDelete.add(atom);
        }
        for (IAtom anAtomsToDelete : atomsToDelete)
            clone.removeAtomAndConnectedElectronContainers(anAtomsToDelete);
        return (clone);
    }

    private boolean isConnectedToRing(IAtomContainer atomContainer, IAtom atom) {
        List<IAtom> connectedAtoms = atomContainer.getConnectedAtomsList(atom);
        for (IAtom connectedAtom : connectedAtoms) {
            if (connectedAtom.getFlag(CDKConstants.ISINRING)) return true;
        }
        return false;
    }

    private void markLinkers(IAtomContainer atomContainer) {
        // first we check for single atoms between rings - these are linker atoms
        // this is also the place where we need to check for something like PhC(C)Ph
        // sicne the central atom is a single atom between rings, but also has a non
        // ring attachment
        for (IAtom atom : atomContainer.atoms()) {
            if (atom.getFlag(CDKConstants.ISINRING)) continue; // only need to look at non-ring atoms
            List<IAtom> conatoms = atomContainer.getConnectedAtomsList(atom);
            if (conatoms.size() == 1) continue; // this is actually a terminal atom and so is a side chain
            boolean allRingAtoms = true;
            int nRingAtom = 0;
            for (IAtom conatom : conatoms) {
                if (conatom.getFlag(CDKConstants.ISINRING)) {
                    nRingAtom++;
                }
            }
            if (nRingAtom >= 2) atom.setProperty(IS_LINKER_ATOM, true);
        }

        // now lets look at linker paths
        for (IAtom atom1 : atomContainer.atoms()) {
            if (atom1.getFlag(CDKConstants.ISINRING) || !isConnectedToRing(atomContainer, atom1)) continue;
            for (IAtom atom2 : atomContainer.atoms()) {
                if (atom2.getFlag(CDKConstants.ISINRING) || !isConnectedToRing(atomContainer, atom2)) continue;

                // ok, get paths between these two non-ring atoms. Each of these atoms
                // is connected to a ring atom, and so if the atoms between these atoms
                // not ring atoms, this is a linker path
                List<List<IAtom>> paths = PathTools.getAllPaths(atomContainer, atom1, atom2);

                for (List<IAtom> path : paths) {
                    boolean allNonRing = true;
                    for (IAtom atom : path) {
                        if (atom.getFlag(CDKConstants.ISINRING)) {
                            allNonRing = false;
                            break;
                        }
                    }
                    if (allNonRing) { // mark them as linkers
                        for (IAtom atom : path) atom.setProperty(IS_LINKER_ATOM, true);
                    }
                }
            }
        }
    }

    private void markSideChains(IAtomContainer atomContainer) {
        for (IAtom atom : atomContainer.atoms()) {
            if (!isring(atom) && !islinker(atom)) atom.setProperty(IS_SIDECHAIN_ATOM, true);
        }
    }

    /**
     * This returns the frameworks and ring systems from a Murcko fragmentation.
     * <p/>
     * To get frameworks, ring systems and side chains seperately, use the
     * respective functions
     *
     * @return a String[] of the fragments.
     * @see #getRingSystems()
     * @see #getRingSystemsAsContainers()
     * @see #getFrameworks()
     * @see #getFrameworksAsContainers()
     */
    @TestMethod("testMF1")
    public String[] getFragments() {
        List<String> allfrags = new ArrayList<String>();
        allfrags.addAll(frameMap.keySet());
        allfrags.addAll(ringMap.keySet());
        return allfrags.toArray(new String[]{});
    }

    /**
     * Get all frameworks and ring systems as {@link IAtomContainer} objects.
     *
     * @return An array of structures representing frameworks and ring systems
     */
    public IAtomContainer[] getFragmentsAsContainers() {
        List<IAtomContainer> allfrags = new ArrayList<IAtomContainer>();
        allfrags.addAll(frameMap.values());
        allfrags.addAll(ringMap.values());
        return allfrags.toArray(new IAtomContainer[0]);
    }

    /**
     * Get the ring system fragments as SMILES strings.
     *
     * @return a String[] of the fragments.
     */
    @TestMethod("testMF1")
    public String[] getRingSystems() {
        return ringMap.keySet().toArray(new String[]{});
    }

    /**
     * Get rings systems as {@link IAtomContainer} objects.
     *
     * @return an array of ring systems.
     */
    @TestMethod("testMF2,testMF3")
    public IAtomContainer[] getRingSystemsAsContainers() {
        return ringMap.values().toArray(new IAtomContainer[0]);
    }

    /**
     * Get frameworks as SMILES strings.
     *
     * @return an array of SMILES strings
     */
    @TestMethod("testMF2,testMF3")
    public String[] getFrameworks() {
        return frameMap.keySet().toArray(new String[]{});
    }

    /**
     * Get frameworks as {@link IAtomContainer} as objects.
     *
     * @return an array of frameworks.
     */
    public IAtomContainer[] getFrameworksAsContainers() {
        return frameMap.values().toArray(new IAtomContainer[0]);
    }

    private boolean isring(IAtom atom) {
        return atom.getFlag(CDKConstants.ISINRING);
    }

    private boolean islinker(IAtom atom) {
        Boolean o = (Boolean) atom.getProperty(IS_LINKER_ATOM);
        return o != null && o;
    }

    private boolean issidechain(IAtom atom) {
        Boolean o = (Boolean) atom.getProperty(IS_SIDECHAIN_ATOM);
        return o != null && o;
    }

    private boolean islinker(IBond bond) {
        return islinker(bond.getAtom(0)) || islinker(bond.getAtom(1));
    }

    private boolean isZeroAtomLinker(IBond bond, IAtomContainer mol) {
        boolean isRingBond = (new SpanningTree(mol)).getCyclicFragmentsContainer().contains(bond);
        return isring(bond.getAtom(0)) && isring(bond.getAtom(1)) && !isRingBond;
    }

    private boolean hasframework(IAtomContainer atomContainer) {
        boolean hasLinker = false;
        boolean hasRing = false;
        for (IAtom atom : atomContainer.atoms()) {
            if (islinker(atom)) hasLinker = true;
            if (isring(atom)) hasRing = true;
            if (hasLinker && hasRing) break;
        }

        // but two rings may be connected by a single bond
        // in which case, the atoms of the bond are not
        // linker atoms, but the bond itself is a (pseudo) linker bond
        for (IBond bond : atomContainer.bonds()) {
            if (isZeroAtomLinker(bond, atomContainer)) {
                hasLinker = true;
                break;
            }
        }
        return hasLinker && hasRing;
    }
}
