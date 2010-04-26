/**
 *
 * Copyright (C) 2006-2010  Syed Asad Rahman {asad@ebi.ac.uk}
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
 * MERCHANTABILITY or FITNESS FOR source PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.smsd.factory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.openscience.cdk.smsd.algorithm.mcsplus.MCSPlusHandler;
import org.openscience.cdk.smsd.algorithm.single.SingleMappingHandler;
import org.openscience.cdk.smsd.algorithm.vflib.VFlibMCSHandler;
import org.openscience.cdk.smsd.helper.MolHandler;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;

/**
 * This class handles MCS search if molecules are fragmented.
 * It returns MCS amongst the fragments.
 * @cdk.module smsd
 * @cdk.githash
 * @author Syed Asad Rahman <asad@ebi.ac.uk>
 */
@TestClass("org.openscience.cdk.smsd.factory.FragmentMatcherTest")
public class FragmentMatcher {

    private MolHandler rMol;
    private MolHandler pMol;
    private IAtomContainerSet reactantSet = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainerSet.class);
    private IAtomContainerSet productSet = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainerSet.class);
    private static List<Map<IAtom, IAtom>> allAtomMCS = null;
    private static Map<IAtom, IAtom> atomsMCS = null;
    private static Map<Integer, Integer> firstMCS = null;
    private static List<Map<Integer, Integer>> allMCS = null;
    private List<Map<Integer, Integer>> gAllMCS = null;
    private Map<Integer, Integer> gFirstSolution = null;
    private List<Map<IAtom, IAtom>> gAllAtomMCS = null;
    private Map<IAtom, IAtom> gFirstAtomMCS = null;
    private boolean removeHydrogen = false;

    /**
     * Finds the largest MCS in a fragmented molecule.
     */
    @TestMethod("testSearchMCS")
    public void searchMCS() {
        int SolutionSize = 0;
        for (int i = 0; i < reactantSet.getAtomContainerCount(); i++) {

            IAtomContainer source = reactantSet.getAtomContainer(i);
            rMol = new MolHandler(source, false);
            for (int j = 0; j < productSet.getAtomContainerCount(); j++) {

                IAtomContainer target = productSet.getAtomContainer(j);
                pMol = new MolHandler(target, false);

                builder();

                if (SolutionSize < firstMCS.size()) {
                    clear();
                    gFirstSolution.putAll(firstMCS);
                    gAllMCS.addAll(allMCS);
                    gFirstAtomMCS.putAll(atomsMCS);
                    gAllAtomMCS.addAll(allAtomMCS);
                    SolutionSize = firstMCS.size();
                } else if (SolutionSize == firstMCS.size()) {
                    gAllMCS.addAll(allMCS);
                    gAllAtomMCS.addAll(allAtomMCS);
                }
            }
        }
    }

    private synchronized void builder() {

        int rBondCount = rMol.getMolecule().getBondCount();
        int pBondCount = pMol.getMolecule().getBondCount();

        int rAtomCount = rMol.getMolecule().getAtomCount();
        int pAtomCount = pMol.getMolecule().getAtomCount();

        if (rBondCount == 0 || rAtomCount == 1 || pBondCount == 0 || pAtomCount == 1) {
//                System.out.println("Single Mapping");
            singleMapping();
        } else {
            if (rBondCount >= 6 && rBondCount >= 6) {
                vfLibMCS();
//                    System.out.println("Mapped with vfLibMCS");
            } else {
                mcsPlus();
//                    System.out.println("Mapped with mcsPlus");
            }
        }
    }

    private synchronized void mcsPlus() {

        MCSPlusHandler mcs = new MCSPlusHandler();
        mcs.set(rMol, pMol);
        mcs.searchMCS();

        firstMCS = mcs.getFirstMapping();
        allMCS = mcs.getAllMapping();
        allAtomMCS = mcs.getAllAtomMapping();
        atomsMCS = mcs.getFirstAtomMapping();

    }

    private void vfLibMCS() {
        VFlibMCSHandler mcs = new VFlibMCSHandler();
        mcs.set(rMol, pMol);
        mcs.searchMCS();

        firstMCS = mcs.getFirstMapping();
        allMCS = mcs.getAllMapping();
        allAtomMCS = mcs.getAllAtomMapping();
        atomsMCS = mcs.getFirstAtomMapping();
    }

    /**
     *
     * @param source
     * @param target
     * @param removeHydrogen
     */
    @TestMethod("testFragmentMatcher")
    public FragmentMatcher(IAtomContainerSet source, IAtomContainerSet target, boolean removeHydrogen) {

        this.removeHydrogen = removeHydrogen;
        gAllMCS = new ArrayList<Map<Integer, Integer>>();
        gFirstSolution = new TreeMap<Integer, Integer>();
        gAllAtomMCS = new ArrayList<Map<IAtom, IAtom>>();
        gFirstAtomMCS = new HashMap<IAtom, IAtom>();
        this.reactantSet = source;
        this.productSet = target;
    }

    private void singleMapping() {

        SingleMappingHandler mcs = new SingleMappingHandler(removeHydrogen);
        mcs.set(rMol, pMol);
        mcs.searchMCS();

        firstMCS = mcs.getFirstMapping();
        allMCS = mcs.getAllMapping();
        allAtomMCS = mcs.getAllAtomMapping();
        atomsMCS = mcs.getFirstAtomMapping();

    }

    /**
     * Returns all plausible mappings between query and target molecules
     * Each map in the list has atom-atom equivalence of the mappings
     * between query and target molecule i.e. map.getKey() for the query.
     * @return all atom mappings
     */
    @TestMethod("testGetAllAtomMapping")
    public List<Map<IAtom, IAtom>> getAllAtomMapping() {
        return Collections.unmodifiableList(gAllAtomMCS);
    }

    /**
     * Returns all plausible mappings between query and target molecules
     * Each map in the list has atom-atom equivalence index of the mappings
     * between query and target molecule i.e. map.getKey() for the query
     * and map.getValue() for the target molecule.
     * @return all mapping indexes
     */
    @TestMethod("testGetAllMapping")
    public List<Map<Integer, Integer>> getAllMapping() {
        return Collections.unmodifiableList(gAllMCS);
    }

    /**
     * Returns one of the best matches with atoms mapped.
     * @return First Atom Mapping
     */
    @TestMethod("testGetFirstAtomMapping")
    public Map<IAtom, IAtom> getFirstAtomMapping() {
        return Collections.unmodifiableMap(gFirstAtomMCS);
    }

    /**
     * Returns one of the best matches with atom indexes mapped.
     * @return First Integer Mapping
     */
    @TestMethod("testGetFirstMapping")
    public Map<Integer, Integer> getFirstMapping() {
        return Collections.unmodifiableMap(gFirstSolution);
    }

    private void clear() {

        gFirstSolution.clear();
        gFirstAtomMCS.clear();
        gAllAtomMCS.clear();
        gAllMCS.clear();

    }
}
