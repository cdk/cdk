/**
 *
 * Copyright (C) 2006-2010  Syed Asad Rahman <asad@ebi.ac.uk>
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
 * You should have received eAtom copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.smsd.filters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryBond;
import org.openscience.cdk.smsd.ring.HanserRingFinder;
import org.openscience.cdk.smsd.tools.BondEnergies;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.RingSetManipulator;

/**
 * Class that ranks MCS final solution according to the chemical rules.
 *
 * @cdk.module smsd
 * @cdk.githash
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 * @deprecated SMSD has been deprecated from the CDK with a newer, more recent
 *             version of SMSD is available at <a href="http://github.com/asad/smsd">http://github.com/asad/smsd</a>.
 */
@Deprecated
public class ChemicalFilters {

    private List<Map<Integer, Integer>> allMCS        = null;
    private Map<Integer, Integer>       firstSolution = null;
    private List<Map<IAtom, IAtom>>     allAtomMCS    = null;
    private Map<IAtom, IAtom>           firstAtomMCS  = null;
    private List<Double>                stereoScore   = null;
    private List<Integer>               fragmentSize  = null;
    private List<Double>                bEnergies     = null;
    private IAtomContainer              rMol          = null;
    private IAtomContainer              pMol          = null;

    /**
     * This class has all the three chemical filters supported by the SMSD.
     * i.e ring matches, bond energy etc
     *
     * <OL>
     * <lI>a: Bond energy,
     * <lI>b: Fragment count,
     * <lI>c: Stereo matches
     * </OL>
     *
     * @param allMCS
     * @param allAtomMCS
     * @param firstSolution
     * @param firstAtomMCS
     * @param sourceMol
     * @param targetMol
     */
    public ChemicalFilters(List<Map<Integer, Integer>> allMCS, List<Map<IAtom, IAtom>> allAtomMCS,
            Map<Integer, Integer> firstSolution, Map<IAtom, IAtom> firstAtomMCS, IAtomContainer sourceMol,
            IAtomContainer targetMol) {
        this.allAtomMCS = allAtomMCS;
        this.allMCS = allMCS;
        this.firstAtomMCS = firstAtomMCS;
        this.firstSolution = firstSolution;
        this.pMol = targetMol;
        this.rMol = sourceMol;

        stereoScore = new ArrayList<Double>();
        fragmentSize = new ArrayList<Integer>();
        bEnergies = new ArrayList<Double>();

    }

    private void clear() {

        firstSolution.clear();
        allMCS.clear();
        allAtomMCS.clear();
        firstAtomMCS.clear();
        stereoScore.clear();
        fragmentSize.clear();
        bEnergies.clear();

    }

    private void clear(Map<Integer, Map<Integer, Integer>> sortedAllMCS,
            Map<Integer, Map<IAtom, IAtom>> sortedAllAtomMCS, Map<Integer, Double> stereoScoreMap,
            Map<Integer, Integer> fragmentScoreMap, Map<Integer, Double> energySelectionMap) {

        sortedAllMCS.clear();
        sortedAllAtomMCS.clear();
        stereoScoreMap.clear();
        fragmentScoreMap.clear();
        energySelectionMap.clear();

    }

    private void addSolution(int counter, int key, Map<Integer, Map<IAtom, IAtom>> allFragmentAtomMCS,
            Map<Integer, Map<Integer, Integer>> allFragmentMCS, Map<Integer, Double> stereoScoreMap,
            Map<Integer, Double> energyScoreMap, Map<Integer, Integer> fragmentScoreMap) {

        allAtomMCS.add(counter, allFragmentAtomMCS.get(key));
        allMCS.add(counter, allFragmentMCS.get(key));
        stereoScore.add(counter, stereoScoreMap.get(key));
        fragmentSize.add(counter, fragmentScoreMap.get(key));
        bEnergies.add(counter, energyScoreMap.get(key));

    }

    private void initializeMaps(Map<Integer, Map<Integer, Integer>> sortedAllMCS,
            Map<Integer, Map<IAtom, IAtom>> sortedAllAtomMCS, Map<Integer, Double> stereoScoreMap,
            Map<Integer, Integer> fragmentScoreMap, Map<Integer, Double> energySelectionMap) {

        Integer index = 0;
        for (Map<IAtom, IAtom> atomsMCS : allAtomMCS) {
            sortedAllAtomMCS.put(index, atomsMCS);
            fragmentScoreMap.put(index, 0);
            energySelectionMap.put(index, 0.0);
            stereoScoreMap.put(index, 0.0);
            index++;
        }

        index = 0;
        for (Map<Integer, Integer> mcs : allMCS) {
            sortedAllMCS.put(index, mcs);
            index++;
        }

        index = 0;
        for (Double score : bEnergies) {
            energySelectionMap.put(index, score);
            index++;
        }

        index = 0;
        for (Integer score : fragmentSize) {
            fragmentScoreMap.put(index, score);
            index++;
        }

        index = 0;
        for (Double score : stereoScore) {
            stereoScoreMap.put(index, score);
            index++;
        }

    }

    /**
     * Sort MCS solution by stereo and bond type matches.
     * @throws CDKException
     */
    public synchronized void sortResultsByStereoAndBondMatch() throws CDKException {

        //        System.out.println("\n\n\n\nSort By ResultsByStereoAndBondMatch");

        Map<Integer, Map<Integer, Integer>> allStereoMCS = new HashMap<Integer, Map<Integer, Integer>>();
        Map<Integer, Map<IAtom, IAtom>> allStereoAtomMCS = new HashMap<Integer, Map<IAtom, IAtom>>();

        Map<Integer, Integer> fragmentScoreMap = new TreeMap<Integer, Integer>();
        Map<Integer, Double> energyScoreMap = new TreeMap<Integer, Double>();
        Map<Integer, Double> stereoScoreMap = new HashMap<Integer, Double>();

        initializeMaps(allStereoMCS, allStereoAtomMCS, stereoScoreMap, fragmentScoreMap, energyScoreMap);

        boolean stereoMatchFlag = getStereoBondChargeMatch(stereoScoreMap, allStereoMCS, allStereoAtomMCS);

        boolean flag = false;
        if (stereoMatchFlag) {

            //Higher Score is mapped preferred over lower
            stereoScoreMap = sortMapByValueInDecendingOrder(stereoScoreMap);
            double higestStereoScore = stereoScoreMap.isEmpty() ? 0 : stereoScoreMap.values().iterator().next();
            double secondhigestStereoScore = higestStereoScore;
            for (Integer key : stereoScoreMap.keySet()) {
                if (secondhigestStereoScore < higestStereoScore && stereoScoreMap.get(key) > secondhigestStereoScore) {
                    secondhigestStereoScore = stereoScoreMap.get(key);
                } else if (secondhigestStereoScore == higestStereoScore
                        && stereoScoreMap.get(key) < secondhigestStereoScore) {
                    secondhigestStereoScore = stereoScoreMap.get(key);
                }
            }

            if (!stereoScoreMap.isEmpty()) {
                flag = true;
                clear();
            }

            /* Put back the sorted solutions */

            int counter = 0;
            for (Integer i : stereoScoreMap.keySet()) {
                //                System.out.println("Sorted Map key " + I + " Sorted Value: " + stereoScoreMap.get(I));
                //                System.out.println("Stereo MCS " + allStereoMCS.get(I) + " Stereo Value: "
                //                        + stereoScoreMap.get(I));
                if (higestStereoScore == stereoScoreMap.get(i).doubleValue()) {
                    //|| secondhigestStereoScore == stereoScoreMap.get(I).doubleValue()) {
                    addSolution(counter, i, allStereoAtomMCS, allStereoMCS, stereoScoreMap, energyScoreMap,
                            fragmentScoreMap);
                    counter++;

                    //                    System.out.println("Sorted Map key " + I + " Sorted Value: " + stereoScoreMap.get(I));
                    //                    System.out.println("Stereo MCS " + allStereoMCS.get(I) + " Stereo Value: "
                    //                            + stereoScoreMap.get(I));
                }
            }
            if (flag) {
                firstSolution.putAll(allMCS.get(0));
                firstAtomMCS.putAll(allAtomMCS.get(0));
                clear(allStereoMCS, allStereoAtomMCS, stereoScoreMap, fragmentScoreMap, energyScoreMap);
            }
        }

    }

    /**
     * Sort solution by ascending order of the fragment count.
     */
    public synchronized void sortResultsByFragments() {

        //        System.out.println("\nSort By Fragment");
        Map<Integer, Map<Integer, Integer>> allFragmentMCS = new TreeMap<Integer, Map<Integer, Integer>>();
        Map<Integer, Map<IAtom, IAtom>> allFragmentAtomMCS = new TreeMap<Integer, Map<IAtom, IAtom>>();

        Map<Integer, Double> stereoScoreMap = new TreeMap<Integer, Double>();
        Map<Integer, Double> energyScoreMap = new TreeMap<Integer, Double>();
        Map<Integer, Integer> fragmentScoreMap = new TreeMap<Integer, Integer>();

        initializeMaps(allFragmentMCS, allFragmentAtomMCS, stereoScoreMap, fragmentScoreMap, energyScoreMap);

        int minFragmentScore = 9999;
        for (Integer key : allFragmentAtomMCS.keySet()) {
            Map<IAtom, IAtom> mcsAtom = allFragmentAtomMCS.get(key);
            int fragmentCount = getMappedMoleculeFragmentSize(mcsAtom);
            fragmentScoreMap.put(key, fragmentCount);
            if (minFragmentScore > fragmentCount) {
                minFragmentScore = fragmentCount;
            }
        }
        boolean flag = false;
        if (minFragmentScore < 9999) {
            flag = true;
            clear();
        }
        int counter = 0;
        for (Map.Entry<Integer, Integer> map : fragmentScoreMap.entrySet()) {
            if (minFragmentScore == map.getValue().intValue()) {
                addSolution(counter, map.getKey(), allFragmentAtomMCS, allFragmentMCS, stereoScoreMap, energyScoreMap,
                        fragmentScoreMap);
                counter++;
                //                System.out.println("Fragment key " + map.getKey() + " Size: " + fragmentScoreMap.get(map.getKey()));
                //                System.out.println("Fragment MCS " + allFragmentMCS.get(map.getKey()) + " Stereo Value: "
                //                        + stereoScoreMap.get(map.getKey()));
            }
        }

        if (flag) {
            firstSolution.putAll(allMCS.get(0));
            firstAtomMCS.putAll(allAtomMCS.get(0));
            clear(allFragmentMCS, allFragmentAtomMCS, stereoScoreMap, fragmentScoreMap, energyScoreMap);
        }

    }

    /**
     * Sort MCS solution by bond breaking energy.
     *
     * @throws CDKException
     */
    public synchronized void sortResultsByEnergies() throws CDKException {

        //        System.out.println("\nSort By Energies");
        Map<Integer, Map<Integer, Integer>> allEnergyMCS = new TreeMap<Integer, Map<Integer, Integer>>();
        Map<Integer, Map<IAtom, IAtom>> allEnergyAtomMCS = new TreeMap<Integer, Map<IAtom, IAtom>>();

        Map<Integer, Double> stereoScoreMap = new TreeMap<Integer, Double>();
        Map<Integer, Integer> fragmentScoreMap = new TreeMap<Integer, Integer>();
        Map<Integer, Double> energySelectionMap = new TreeMap<Integer, Double>();

        initializeMaps(allEnergyMCS, allEnergyAtomMCS, stereoScoreMap, fragmentScoreMap, energySelectionMap);

        for (Integer key : allEnergyMCS.keySet()) {
            Map<Integer, Integer> mcsAtom = allEnergyMCS.get(key);
            Double energies = getMappedMoleculeEnergies(mcsAtom);
            energySelectionMap.put(key, energies);
        }

        energySelectionMap = sortMapByValueInAccendingOrder(energySelectionMap);
        boolean flag = false;

        double lowestEnergyScore = 99999999.99;
        for (Integer key : energySelectionMap.keySet()) {
            lowestEnergyScore = energySelectionMap.get(key).doubleValue();
            flag = true;
            clear();
            break;
        }

        int counter = 0;
        for (Map.Entry<Integer, Double> map : energySelectionMap.entrySet()) {
            if (lowestEnergyScore == map.getValue().doubleValue()) {
                addSolution(counter, map.getKey(), allEnergyAtomMCS, allEnergyMCS, stereoScoreMap, energySelectionMap,
                        fragmentScoreMap);
                counter++;
                //
                //                System.out.println("Energy key " + map.getKey() + "Energy MCS " + allEnergyMCS.get(map.getKey()));
                //                System.out.println("Frag Size: " + fragmentScoreMap.get(map.getKey()) + " Stereo Value: "
                //                        + stereoScoreMap.get(map.getKey()));

            }
        }

        if (flag) {
            firstSolution.putAll(allMCS.get(0));
            firstAtomMCS.putAll(allAtomMCS.get(0));
            clear(allEnergyMCS, allEnergyAtomMCS, stereoScoreMap, fragmentScoreMap, energySelectionMap);
        }
    }

    private Map<IBond, IBond> makeBondMapsOfAtomMaps(IAtomContainer ac1, IAtomContainer ac2,
            Map<Integer, Integer> mappings) {

        Map<IBond, IBond> maps = new HashMap<IBond, IBond>();

        for (IAtom atoms : ac1.atoms()) {

            int ac1AtomNumber = ac1.getAtomNumber(atoms);

            if (mappings.containsKey(ac1AtomNumber)) {

                int ac2AtomNumber = mappings.get(ac1AtomNumber);

                List<IAtom> connectedAtoms = ac1.getConnectedAtomsList(atoms);

                for (IAtom cAtoms : connectedAtoms) {
                    int ac1ConnectedAtomNumber = ac1.getAtomNumber(cAtoms);

                    if (mappings.containsKey(ac1ConnectedAtomNumber)) {
                        {
                            int ac2ConnectedAtomNumber = mappings.get(ac1ConnectedAtomNumber);

                            IBond ac1Bond = ac1.getBond(atoms, cAtoms);
                            IBond ac2Bond = ac2
                                    .getBond(ac2.getAtom(ac2AtomNumber), ac2.getAtom(ac2ConnectedAtomNumber));

                            if (ac2Bond == null) {
                                ac2Bond = ac2.getBond(ac2.getAtom(ac2ConnectedAtomNumber), ac2.getAtom(ac2AtomNumber));
                            }

                            if (ac1Bond != null && ac2Bond != null) {
                                maps.put(ac1Bond, ac2Bond);
                            }
                        }
                    }
                }
            }
        }
        //        System.out.println("Mol Map size:" + maps.size());
        return maps;

    }

    private synchronized int getMappedMoleculeFragmentSize(Map<IAtom, IAtom> mcsAtomSolution) {

        //      System.out.println("Mol Size Eorg: " + sourceMol.getMolecule().getAtomCount() + " , Mol Size Porg: " +
        //        targetMol.getMolecule().getAtomCount());

        IAtomContainer educt = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class, rMol);
        IAtomContainer product = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class, pMol);

        if (mcsAtomSolution != null) {
            for (Map.Entry<IAtom, IAtom> map : mcsAtomSolution.entrySet()) {
                IAtom atomE = map.getKey();
                IAtom atomP = map.getValue();
                educt.removeAtomAndConnectedElectronContainers(atomE);
                product.removeAtomAndConnectedElectronContainers(atomP);
            }
        }
        return getfragmentCount(educt) + getfragmentCount(product);
    }

    private synchronized Double getMappedMoleculeEnergies(Map<Integer, Integer> mcsAtomSolution) throws CDKException {

        //        System.out.println("\nSort By Energies");
        double totalBondEnergy = -9999.0;

        IAtomContainer educt = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class, rMol);
        IAtomContainer product = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class, pMol);

        for (IAtom eAtom : educt.atoms()) {
            eAtom.setFlag(CDKConstants.ISPLACED, false);
        }

        for (IAtom pAtom : product.atoms()) {
            pAtom.setFlag(CDKConstants.ISPLACED, false);
        }

        if (mcsAtomSolution != null) {
            for (Map.Entry<Integer, Integer> map : mcsAtomSolution.entrySet()) {
                int eNum = map.getKey();
                int pNum = map.getValue();

                IAtom eAtom = educt.getAtom(eNum);
                IAtom pAtom = product.getAtom(pNum);

                eAtom.setFlag(CDKConstants.ISPLACED, true);
                pAtom.setFlag(CDKConstants.ISPLACED, true);
            }
        }

        if (mcsAtomSolution != null) {
            totalBondEnergy = getEnergy(educt, product);
        }
        return totalBondEnergy;
    }

    static Map<Integer, Double> sortMapByValueInAccendingOrder(Map<Integer, Double> map) {
        List<Map.Entry<Integer, Double>> list = new LinkedList<Map.Entry<Integer, Double>>(map.entrySet());
        // Sort the list using an annonymous inner class implementing Comparator for the compare method
        Collections.sort(list, new Comparator<Map.Entry<Integer, Double>>() {

            @Override
            public int compare(Map.Entry<Integer, Double> entry, Map.Entry<Integer, Double> entry1) {
                // Return 0 for eAtom match, -1 for less than and +1 for more then (Aceending Order Sort)
                return (entry.getValue().equals(entry1.getValue()) ? 0
                        : (entry.getValue() > entry1.getValue() ? 1 : -1));
            }
        });
        // logger.info(list);
        Map<Integer, Double> result = new LinkedHashMap<Integer, Double>();
        for (Iterator<Map.Entry<Integer, Double>> it = list.iterator(); it.hasNext();) {
            Map.Entry<Integer, Double> entry = it.next();
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    static Map<Integer, Double> sortMapByValueInDecendingOrder(Map<Integer, Double> map) {
        List<Map.Entry<Integer, Double>> list = new LinkedList<Map.Entry<Integer, Double>>(map.entrySet());
        // Sort the list using an annonymous inner class implementing Comparator for the compare method
        Collections.sort(list, new Comparator<Map.Entry<Integer, Double>>() {

            @Override
            public int compare(Map.Entry<Integer, Double> entry, Map.Entry<Integer, Double> entry1) {
                // Return 0 for eAtom match, -1 for less than and +1 for more then (Decending Order Sort)
                return (entry.getValue().equals(entry1.getValue()) ? 0
                        : (entry.getValue() < entry1.getValue() ? 1 : -1));
            }
        });
        // logger.info(list);
        Map<Integer, Double> result = new LinkedHashMap<Integer, Double>();
        for (Iterator<Map.Entry<Integer, Double>> it = list.iterator(); it.hasNext();) {
            Map.Entry<Integer, Double> entry = it.next();
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    /**
     * Return sorted energy in ascending order.
     * @return sorted bond breaking energy
     */
    public List<Double> getSortedEnergy() {
        return Collections.unmodifiableList(bEnergies);
    }

    /**
     * Return sorted fragment in ascending order of the size.
     * @return sorted fragment count
     */
    public List<Integer> getSortedFragment() {
        return Collections.unmodifiableList(fragmentSize);
    }

    /**
     * Return Stereo matches in descending order.
     * @return sorted stereo matches
     */
    public List<Double> getStereoMatches() {
        return Collections.unmodifiableList(stereoScore);
    }

    private List<Object> getMappedFragment(IAtomContainer molecule, Collection<IAtom> atomsMCS)
            throws CloneNotSupportedException {
        IAtomContainer subgraphContainer = molecule.getBuilder().newInstance(IAtomContainer.class, molecule);
        List<IAtom> list = new ArrayList<IAtom>(atomsMCS.size());
        for (IAtom atom : atomsMCS) {
            int post = molecule.getAtomNumber(atom);
            //            System.out.println("Atom to be removed " + post);
            list.add(subgraphContainer.getAtom(post));
        }

        List<IAtom> rlist = new ArrayList<IAtom>();
        for (IAtom atoms : subgraphContainer.atoms()) {
            if (!list.contains(atoms)) {
                rlist.add(atoms);
            }
        }

        for (IAtom atoms : rlist) {
            subgraphContainer.removeAtomAndConnectedElectronContainers(atoms);
        }
        List<Object> l = new ArrayList<Object>();
        l.add(list);
        l.add(subgraphContainer);
        return l;
    }

    private double getAtomScore(double score, Map<IAtom, IAtom> atomMapMCS, IAtomContainer reactant,
            IAtomContainer product) {
        for (Map.Entry<IAtom, IAtom> mappings : atomMapMCS.entrySet()) {
            IAtom rAtom = mappings.getKey();
            IAtom pAtom = mappings.getValue();

            int rHCount = 0;
            int pHCount = 0;
            double rBO = reactant.getBondOrderSum(rAtom);
            double pBO = product.getBondOrderSum(pAtom);

            if (rAtom.getImplicitHydrogenCount() != null) {
                rHCount = rAtom.getImplicitHydrogenCount();
            }
            if (pAtom.getImplicitHydrogenCount() != null) {
                pHCount = pAtom.getImplicitHydrogenCount();
            }

            int hScore = Math.abs(rHCount - pHCount);
            double boScore = Math.abs(rBO - pBO);

            if (rHCount != pHCount) {
                score -= hScore;
            } else {
                score += hScore;
            }

            if (rBO != pBO) {
                score -= boScore;
            } else {
                score += boScore;
            }
        }
        return score;
    }

    private double getBondScore(double score, Map<IBond, IBond> bondMaps) {
        for (Map.Entry<IBond, IBond> matchedBonds : bondMaps.entrySet()) {

            IBond rBond = matchedBonds.getKey();
            IBond pBond = matchedBonds.getValue();

            score += getBondFormalChargeMatches(rBond, pBond);
            score += getBondTypeMatches(rBond, pBond);
        }
        return score;
    }

    private double getBondFormalChargeMatches(IBond rBond, IBond pBond) {
        double score = 0.0;
        if (rBond != null && pBond != null) {
            IAtom ratom1 = rBond.getAtom(0);
            IAtom ratom2 = rBond.getAtom(1);
            IAtom patom1 = pBond.getAtom(0);
            IAtom patom2 = pBond.getAtom(1);

            if (ratom1.getSymbol().equals(patom1.getSymbol()) && ratom1.getSymbol().equals(patom1.getSymbol())) {
                if ((ratom1.getFormalCharge() != patom1.getFormalCharge())
                        || ratom2.getFormalCharge() != patom2.getFormalCharge()) {
                    if (convertBondOrder(rBond) != convertBondOrder(pBond)) {
                        score += 5 * Math.abs(convertBondOrder(rBond) + convertBondOrder(pBond));
                    }
                }
                if (ratom1.getFormalCharge() == patom1.getFormalCharge()
                        && (convertBondOrder(rBond) - convertBondOrder(pBond)) == 0) {
                    score += 100;
                }
                if (ratom2.getFormalCharge() == patom2.getFormalCharge()
                        && (convertBondOrder(rBond) - convertBondOrder(pBond)) == 0) {
                    score += 100;
                }
            } else if (ratom1.getSymbol().equals(patom2.getSymbol()) && ratom2.getSymbol().equals(patom1.getSymbol())) {
                if ((ratom1.getFormalCharge() != patom2.getFormalCharge())
                        || ratom2.getFormalCharge() != patom1.getFormalCharge()) {
                    if (convertBondOrder(rBond) != convertBondOrder(pBond)) {
                        score += 5 * Math.abs(convertBondOrder(rBond) + convertBondOrder(pBond));
                    }
                }
                if (ratom1.getFormalCharge() == patom2.getFormalCharge()
                        && (convertBondOrder(rBond) - convertBondOrder(pBond)) == 0) {
                    score += 100;
                }
                if (ratom2.getFormalCharge() == patom1.getFormalCharge()
                        && (convertBondOrder(rBond) - convertBondOrder(pBond)) == 0) {
                    score += 100;
                }
            }
        }

        return score;
    }

    private double getBondTypeMatches(IBond queryBond, IBond targetBond) {
        double score = 0;

        if (targetBond instanceof IQueryBond && queryBond instanceof IBond) {
            IQueryBond bond = (IQueryBond) targetBond;
            IQueryAtom atom1 = (IQueryAtom) (targetBond.getAtom(0));
            IQueryAtom atom2 = (IQueryAtom) (targetBond.getAtom(1));
            if (bond.matches(queryBond)) {
                // ok, bonds match
                if (atom1.matches(queryBond.getAtom(0)) && atom2.matches(queryBond.getAtom(1))
                        || atom1.matches(queryBond.getAtom(1)) && atom2.matches(queryBond.getAtom(0))) {
                    // ok, atoms match in either order
                    score += 4;
                }
            } else {
                score -= 4;
            }
        } else if (queryBond instanceof IQueryBond && targetBond instanceof IBond) {
            IQueryBond bond = (IQueryBond) queryBond;
            IQueryAtom atom1 = (IQueryAtom) (queryBond.getAtom(0));
            IQueryAtom atom2 = (IQueryAtom) (queryBond.getAtom(1));
            if (bond.matches(targetBond)) {
                // ok, bonds match
                if (atom1.matches(targetBond.getAtom(0)) && atom2.matches(targetBond.getAtom(1))
                        || atom1.matches(targetBond.getAtom(1)) && atom2.matches(targetBond.getAtom(0))) {
                    // ok, atoms match in either order
                    score += 4;
                }
            } else {
                score -= 4;
            }
        } else {

            int reactantBondType = convertBondOrder(queryBond);
            int productBondType = convertBondOrder(targetBond);
            int rStereo = convertBondStereo(queryBond);
            int pStereo = convertBondStereo(targetBond);
            if ((queryBond.getFlag(CDKConstants.ISAROMATIC) == targetBond.getFlag(CDKConstants.ISAROMATIC))
                    && (reactantBondType == productBondType)) {
                score += 8;
            } else if (queryBond.getFlag(CDKConstants.ISAROMATIC) && targetBond.getFlag(CDKConstants.ISAROMATIC)) {
                score += 4;
            }

            if (reactantBondType == productBondType) {
                score += productBondType;
            } else {
                score -= 4 * Math.abs(reactantBondType - productBondType);
            }

            if (rStereo != 4 || pStereo != 4 || rStereo != 3 || pStereo != 3) {
                if (rStereo == pStereo) {
                    score += 1;
                } else {
                    score -= 1;
                }
            }

        }
        return score;
    }

    private double getRingMatchScore(List<Object> list) {
        double lScore = 0;
        List<IAtom> listMap = (List<IAtom>) list.get(0);
        IAtomContainer ac = (IAtomContainer) list.get(1);
        HanserRingFinder ringFinder = new HanserRingFinder();
        IRingSet rRings = null;
        try {
            rRings = ringFinder.getRingSet(ac);
        } catch (CDKException ex) {
            Logger.getLogger(ChemicalFilters.class.getName()).log(Level.SEVERE, null, ex);
        }
        RingSetManipulator.sort(rRings);
        //        System.out.println("Ring length " + );
        lScore = getRingMatch(rRings, listMap);
        return lScore;
    }

    private double getEnergy(IAtomContainer educt, IAtomContainer product) throws CDKException {
        Double eEnergy = 0.0;
        BondEnergies bondEnergy = BondEnergies.getInstance();
        for (int i = 0; i < educt.getBondCount(); i++) {
            IBond bond = educt.getBond(i);
            eEnergy += getBondEnergy(bond, bondEnergy);
        }
        Double pEnergy = 0.0;
        for (int j = 0; j < product.getBondCount(); j++) {
            IBond bond = product.getBond(j);
            pEnergy += getBondEnergy(bond, bondEnergy);
        }
        return (eEnergy + pEnergy);
    }

    private double getBondEnergy(IBond bond, BondEnergies bondEnergy) {
        double energy = 0.0;
        if ((bond.getAtom(0).getFlag(CDKConstants.ISPLACED) == true && bond.getAtom(1).getFlag(CDKConstants.ISPLACED) == false)
                || (bond.getAtom(0).getFlag(CDKConstants.ISPLACED) == false && bond.getAtom(1).getFlag(
                        CDKConstants.ISPLACED) == true)) {
            Integer val = bondEnergy.getEnergies(bond.getAtom(0), bond.getAtom(1), bond.getOrder());
            if (val != null) {
                energy = val;
            }
        }
        return energy;
    }

    private double getRingMatch(IRingSet rings, List<IAtom> atoms) {
        double score = 0.0;
        for (IAtom a : atoms) {
            for (IAtomContainer ring : rings.atomContainers()) {
                if (ring.contains(a)) {
                    score += 10;
                }
            }
        }
        return score;
    }

    private boolean getStereoBondChargeMatch(Map<Integer, Double> stereoScoreMap,
            Map<Integer, Map<Integer, Integer>> allStereoMCS, Map<Integer, Map<IAtom, IAtom>> allStereoAtomMCS)
            throws CDKException {

        boolean stereoMatchFlag = false;
        IAtomContainer reactant = rMol;
        IAtomContainer product = pMol;
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(reactant);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(product);
        Aromaticity.cdkLegacy().apply(reactant);
        Aromaticity.cdkLegacy().apply(product);

        for (Integer key : allStereoMCS.keySet()) {
            try {
                double score = 0.0;
                //            System.out.println("\nStart score " + score);
                Map<Integer, Integer> atomsMCS = allStereoMCS.get(key);
                Map<IAtom, IAtom> atomMapMCS = allStereoAtomMCS.get(key);
                score = getAtomScore(score, atomMapMCS, reactant, product);
                Map<IBond, IBond> bondMaps = makeBondMapsOfAtomMaps(rMol, pMol, atomsMCS);

                if (rMol.getBondCount() > 1 && pMol.getBondCount() > 1) {
                    List<Object> subgraphRList = getMappedFragment(rMol, atomMapMCS.keySet());

                    double rscore = getRingMatchScore(subgraphRList);
                    List<Object> subgraphPList = getMappedFragment(pMol, atomMapMCS.values());
                    double pscore = getRingMatchScore(subgraphPList);
                    score = rscore + pscore;
                }
                score = getBondScore(score, bondMaps);

                if (!stereoMatchFlag) {
                    stereoMatchFlag = true;
                }
                stereoScoreMap.put(key, score);
            } catch (CloneNotSupportedException ex) {
                Logger.getLogger(ChemicalFilters.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return stereoMatchFlag;
    }

    private int getfragmentCount(IAtomContainer molecule) {
        boolean fragmentFlag = true;
        IAtomContainerSet fragmentMolSet = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainerSet.class);
        int countFrag = 0;
        if (molecule.getAtomCount() > 0) {
            fragmentFlag = ConnectivityChecker.isConnected(molecule);
            if (!fragmentFlag) {
                fragmentMolSet.add(ConnectivityChecker.partitionIntoMolecules(molecule));
            } else {
                fragmentMolSet.addAtomContainer(molecule);
            }
            countFrag = fragmentMolSet.getAtomContainerCount();
        }
        return countFrag;
    }

    /**
     * Get bond order value as {@link Order}.
     *
     * @param  srcOrder numerical bond order
     * @return          the bond order type for the given numerical bond order
     */
    public static IBond.Order convertOrder(double srcOrder) {
        if (srcOrder > 3.5) {
            return Order.QUADRUPLE;
        }
        if (srcOrder > 2.5) {
            return Order.TRIPLE;
        }
        if (srcOrder > 1.5) {
            return Order.DOUBLE;
        }
        if (srcOrder > 0.5) {
            return Order.SINGLE;
        }
        return null;
    }

    /**
     * Get bond order value as {@code int} value.
     *
     * @param  bond The {@link IBond} for which the order is returned.
     * @return      1 for a single bond, 2 for a double bond, 3 for a triple bond, 4 for a quadruple bond,
     *              and 0 for any other bond type.
     */
    public static int convertBondOrder(IBond bond) {
        int value = 0;
        switch (bond.getOrder()) {
            case QUADRUPLE:
                value = 4;
                break;
            case TRIPLE:
                value = 3;
                break;
            case DOUBLE:
                value = 2;
                break;
            case SINGLE:
                value = 1;
                break;
            default:
                value = 0;
        }
        return value;
    }

    /**
     * Get stereo value as integer
     * @param bond
     */
    public static int convertBondStereo(IBond bond) {
        int value = 0;
        switch (bond.getStereo()) {
            case UP:
                value = 1;
                break;
            case UP_INVERTED:
                value = 1;
                break;
            case DOWN:
                value = 6;
                break;
            case DOWN_INVERTED:
                value = 6;
                break;
            case UP_OR_DOWN:
                value = 4;
                break;
            case UP_OR_DOWN_INVERTED:
                value = 4;
                break;
            case E_OR_Z:
                value = 3;
                break;
            default:
                value = 0;
        }
        return value;
    }

    /**
     * Get stereo value as Stereo enum
     * @param stereoValue
     */
    public static IBond.Stereo convertStereo(int stereoValue) {
        IBond.Stereo stereo = IBond.Stereo.NONE;
        if (stereoValue == 1) {
            // up bond
            stereo = IBond.Stereo.UP;
        } else if (stereoValue == 6) {
            // down bond
            stereo = IBond.Stereo.DOWN;
        } else if (stereoValue == 0) {
            // bond has no stereochemistry
            stereo = IBond.Stereo.NONE;
        } else if (stereoValue == 4) {
            //up or down bond
            stereo = IBond.Stereo.UP_OR_DOWN;
        } else if (stereoValue == 3) {
            //e or z undefined
            stereo = IBond.Stereo.E_OR_Z;
        }
        return stereo;
    }
}
