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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received eAtom copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.smsd.filters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.openscience.cdk.smsd.tools.BondEnergies;
import org.openscience.cdk.smsd.helper.MolHandler;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryBond;
import org.openscience.cdk.ringsearch.SSSRFinder;
import org.openscience.cdk.tools.manipulator.RingSetManipulator;

/**
 * Class that ranks MCS final solution accrding to the chemical rules.
 *
 * @cdk.module smsd
 * @cdk.githash
 * @author Syed Asad Rahman <asad@ebi.ac.uk>
 */
@TestClass("org.openscience.cdk.smsd.filters.ChemicalFiltersTest")
public class ChemicalFilters {

    private List<Map<Integer, Integer>> allMCS = null;
    private Map<Integer, Integer> firstSolution = null;
    private List<Map<IAtom, IAtom>> allAtomMCS = null;
    private Map<IAtom, IAtom> firstAtomMCS = null;
    private List<Double> stereoScore = null;
    private List<Integer> fragmentSize = null;
    private List<Double> bEnergies = null;
    private MolHandler rMol = null;
    private MolHandler pMol = null;

    /**
     * This class has all the three chem filters supported by the SMSD.
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
    @TestMethod("ChemicalFiltersTest")
    public ChemicalFilters(List<Map<Integer, Integer>> allMCS,
            List<Map<IAtom, IAtom>> allAtomMCS,
            Map<Integer, Integer> firstSolution,
            Map<IAtom, IAtom> firstAtomMCS,
            MolHandler sourceMol,
            MolHandler targetMol) {
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
            Map<Integer, Map<IAtom, IAtom>> sortedAllAtomMCS,
            Map<Integer, Double> stereoScoreMap,
            Map<Integer, Integer> fragmentScoreMap,
            Map<Integer, Double> energySelectionMap) {


        sortedAllMCS.clear();
        sortedAllAtomMCS.clear();
        stereoScoreMap.clear();
        fragmentScoreMap.clear();
        energySelectionMap.clear();

    }

    private void addSolution(int counter, int key,
            Map<Integer, Map<IAtom, IAtom>> allFragmentAtomMCS,
            Map<Integer, Map<Integer, Integer>> allFragmentMCS,
            Map<Integer, Double> stereoScoreMap,
            Map<Integer, Double> energyScoreMap,
            Map<Integer, Integer> fragmentScoreMap) {

        allAtomMCS.add(counter, allFragmentAtomMCS.get(key));
        allMCS.add(counter, allFragmentMCS.get(key));
        stereoScore.add(counter, stereoScoreMap.get(key));
        fragmentSize.add(counter, fragmentScoreMap.get(key));
        bEnergies.add(counter, energyScoreMap.get(key));

    }

    private void initializeMaps(
            Map<Integer, Map<Integer, Integer>> sortedAllMCS,
            Map<Integer, Map<IAtom, IAtom>> sortedAllAtomMCS,
            Map<Integer, Double> stereoScoreMap,
            Map<Integer, Integer> fragmentScoreMap,
            Map<Integer, Double> energySelectionMap) {

        Integer Index = 0;
        for (Map<IAtom, IAtom> atomsMCS : allAtomMCS) {
            sortedAllAtomMCS.put(Index, atomsMCS);
            fragmentScoreMap.put(Index, 0);
            energySelectionMap.put(Index, 0.0);
            stereoScoreMap.put(Index, 0.0);
            Index++;
        }

        Index = 0;
        for (Map<Integer, Integer> MCS : allMCS) {
            sortedAllMCS.put(Index, MCS);
            Index++;
        }

        Index = 0;
        for (Double score : bEnergies) {
            energySelectionMap.put(Index, score);
            Index++;
        }

        Index = 0;
        for (Integer score : fragmentSize) {
            fragmentScoreMap.put(Index, score);
            Index++;
        }

        Index = 0;
        for (Double score : stereoScore) {
            stereoScoreMap.put(Index, score);
            Index++;
        }

    }

    /**
     * Sort MCS solution by stereo and bond type matches.
     * @throws CDKException
     */
    @TestMethod("testSortResultsByStereoAndBondMatch")
    public synchronized void sortResultsByStereoAndBondMatch() throws CDKException {

//        System.out.println("\nSort By ResultsByStereoAndBondMatch");

        Map<Integer, Map<Integer, Integer>> allStereoMCS = new HashMap<Integer, Map<Integer, Integer>>();
        Map<Integer, Map<IAtom, IAtom>> allStereoAtomMCS = new HashMap<Integer, Map<IAtom, IAtom>>();

        Map<Integer, Integer> fragmentScoreMap = new TreeMap<Integer, Integer>();
        Map<Integer, Double> energyScoreMap = new TreeMap<Integer, Double>();
        Map<Integer, Double> stereoScoreMap = new HashMap<Integer, Double>();

        initializeMaps(allStereoMCS,
                allStereoAtomMCS,
                stereoScoreMap,
                fragmentScoreMap,
                energyScoreMap);


        boolean stereoMatchFlag = getStereoMatch(
                stereoScoreMap,
                allStereoMCS,
                allStereoAtomMCS);

        boolean flag = false;
        if (stereoMatchFlag) {

            stereoScoreMap = sortMapByValueInDecendingOrder(stereoScoreMap);


            double higestStereoScore = 0.0;
            for (Integer key : stereoScoreMap.keySet()) {
                higestStereoScore = stereoScoreMap.get(key).doubleValue();
                flag = true;
                clear();
                break;
            }

            /*Put back the sorted solutions*/

            int counter = 0;
            for (Integer I : stereoScoreMap.keySet()) {
                if (higestStereoScore == stereoScoreMap.get(I).doubleValue()) {

                    addSolution(counter, I,
                            allStereoAtomMCS,
                            allStereoMCS,
                            stereoScoreMap,
                            energyScoreMap,
                            fragmentScoreMap);
                    counter++;
                }
//                System.out.println("Sorted Map Key " + key + " Sorted Value: " + sortedStereoScoreMap.get(key));
//                System.out.println("sortedAllMCS Key " + key + " Sorted Value: " + sortedAllMCS.get(key));

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
    @TestMethod("testSortResultsByFragments")
    public synchronized void sortResultsByFragments() {

//        System.out.println("\nSort By Fragment");
        Map<Integer, Map<Integer, Integer>> allFragmentMCS = new TreeMap<Integer, Map<Integer, Integer>>();
        Map<Integer, Map<IAtom, IAtom>> allFragmentAtomMCS = new TreeMap<Integer, Map<IAtom, IAtom>>();

        Map<Integer, Double> stereoScoreMap = new TreeMap<Integer, Double>();
        Map<Integer, Double> energyScoreMap = new TreeMap<Integer, Double>();
        Map<Integer, Integer> fragmentScoreMap = new TreeMap<Integer, Integer>();


        initializeMaps(allFragmentMCS,
                allFragmentAtomMCS,
                stereoScoreMap,
                fragmentScoreMap,
                energyScoreMap);


        int _minFragmentScore = 9999;
        for (Integer Key : allFragmentAtomMCS.keySet()) {
            Map<IAtom, IAtom> mcsAtom = allFragmentAtomMCS.get(Key);
            int FragmentCount = getMappedMoleculeFragmentSize(mcsAtom);
//            System.out.println("FragmentCount " + FragmentCount);
            fragmentScoreMap.put(Key, FragmentCount);
            if (_minFragmentScore > FragmentCount) {
                _minFragmentScore = FragmentCount;
            }
        }
        boolean flag = false;
        if (_minFragmentScore < 9999) {
            flag = true;
            clear();
        }


        int counter = 0;
        for (Map.Entry<Integer, Integer> map : fragmentScoreMap.entrySet()) {
            if (_minFragmentScore == map.getValue().intValue()) {
                addSolution(counter, map.getKey(),
                        allFragmentAtomMCS,
                        allFragmentMCS,
                        stereoScoreMap,
                        energyScoreMap,
                        fragmentScoreMap);
                counter++;
//                System.out.println("Fragment Key " + map.getKey() + " Size: " + fragmentScoreMap.get(map.getKey()));
//                System.out.println("Fragment MCS " + allFragmentMCS.get(map.getKey()) + " Fragment Value: "
//                + fragmentScoreMap.get(map.getKey()));
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
    @TestMethod("testSortResultsByEnergies")
    public synchronized void sortResultsByEnergies() throws CDKException {

//        System.out.println("\nSort By Energies");
        Map<Integer, Map<Integer, Integer>> allEnergyMCS = new TreeMap<Integer, Map<Integer, Integer>>();
        Map<Integer, Map<IAtom, IAtom>> allEnergyAtomMCS = new TreeMap<Integer, Map<IAtom, IAtom>>();

        Map<Integer, Double> stereoScoreMap = new TreeMap<Integer, Double>();
        Map<Integer, Integer> fragmentScoreMap = new TreeMap<Integer, Integer>();
        Map<Integer, Double> energySelectionMap = new TreeMap<Integer, Double>();

        initializeMaps(allEnergyMCS, allEnergyAtomMCS, stereoScoreMap, fragmentScoreMap, energySelectionMap);

        for (Integer Key : allEnergyMCS.keySet()) {
            Map<Integer, Integer> mcsAtom = allEnergyMCS.get(Key);
            Double Energies = getMappedMoleculeEnergies(mcsAtom);
            energySelectionMap.put(Key, Energies);
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
                addSolution(counter, map.getKey(),
                        allEnergyAtomMCS,
                        allEnergyMCS,
                        stereoScoreMap,
                        energySelectionMap,
                        fragmentScoreMap);
                counter++;

//            System.out.println("Energy Key " + key + " Size: " + fragmentScoreMap.get(key));
//            System.out.println("Energy " + allEnergyMCS.get(key) + " Sorted Energy Value: "
//                + sortedEnergyMap.get(key));

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
                            IBond ac2Bond = ac2.getBond(ac2.getAtom(ac2AtomNumber),
                                    ac2.getAtom(ac2ConnectedAtomNumber));

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

    private synchronized int getMappedMoleculeFragmentSize(Map<IAtom, IAtom> MCSAtomSolution) {

//      System.out.println("Mol Size Eorg: " + sourceMol.getMolecule().getAtomCount() + " , Mol Size Porg: " +
//        targetMol.getMolecule().getAtomCount());

        IAtomContainer Educt = DefaultChemObjectBuilder.getInstance().newInstance(IMolecule.class, rMol.getMolecule());
        IAtomContainer Product = DefaultChemObjectBuilder.getInstance().newInstance(IMolecule.class, pMol.getMolecule());


        if (MCSAtomSolution != null) {
            for (Map.Entry<IAtom, IAtom> map : MCSAtomSolution.entrySet()) {

                IAtom atomE = map.getKey();
                IAtom atomP = map.getValue();
                Educt.removeAtomAndConnectedElectronContainers(atomE);
                Product.removeAtomAndConnectedElectronContainers(atomP);

            }
        }

        return getFragmentCount(Educt) + getFragmentCount(Product);
    }

    private synchronized Double getMappedMoleculeEnergies(Map<Integer, Integer> MCSAtomSolution) throws CDKException {

//        System.out.println("\nSort By Energies");
        double totalBondEnergy = -9999.0;

        IAtomContainer Educt = DefaultChemObjectBuilder.getInstance().newInstance(IMolecule.class, rMol.getMolecule());
        IAtomContainer Product = DefaultChemObjectBuilder.getInstance().newInstance(IMolecule.class, pMol.getMolecule());

        for (IAtom eAtom : Educt.atoms()) {
            eAtom.setFlag(0, false);
        }

        for (IAtom pAtom : Product.atoms()) {
            pAtom.setFlag(0, false);
        }


        if (MCSAtomSolution != null) {
            for (Map.Entry<Integer, Integer> map : MCSAtomSolution.entrySet()) {
                int ENum = map.getKey();
                int PNum = map.getValue();

                IAtom eAtom = Educt.getAtom(ENum);
                IAtom pAtom = Product.getAtom(PNum);

                eAtom.setFlag(0, true);
                pAtom.setFlag(0, true);
            }
        }

        if (MCSAtomSolution != null) {
            totalBondEnergy = getEnergy(Educt, Product);
        }
        return totalBondEnergy;
    }

    @TestMethod("testSortMapByValueInAccendingOrder")
    static Map<Integer, Double> sortMapByValueInAccendingOrder(Map<Integer, Double> map) {
        List<Map.Entry<Integer, Double>> list = new LinkedList<Map.Entry<Integer, Double>>(map.entrySet());
        // Sort the list using an annonymous inner class implementing Comparator for the compare method
        java.util.Collections.sort(list, new Comparator<Map.Entry<Integer, Double>>() {

            @Override
            public int compare(Map.Entry<Integer, Double> entry, Map.Entry<Integer, Double> entry1) {
                // Return 0 for eAtom match, -1 for less than and +1 for more then (Aceending Order Sort)
                return (entry.getValue().equals(entry1.getValue()) ? 0 : (entry.getValue() > entry1.getValue() ? 1 : -1));
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

    @TestMethod("testSortMapByValueInDecendingOrder")
    static Map<Integer, Double> sortMapByValueInDecendingOrder(Map<Integer, Double> map) {
        List<Map.Entry<Integer, Double>> list = new LinkedList<Map.Entry<Integer, Double>>(map.entrySet());
        // Sort the list using an annonymous inner class implementing Comparator for the compare method
        java.util.Collections.sort(list, new Comparator<Map.Entry<Integer, Double>>() {

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
    @TestMethod("testGetSortedEnergy")
    public List<Double> getSortedEnergy() {
        return Collections.unmodifiableList(bEnergies);
    }

    /**
     * Return sorted fragment in ascending order of the size.
     * @return sorted fragment count
     */
    @TestMethod("testGetSortedFragment")
    public List<Integer> getSortedFragment() {
        return Collections.unmodifiableList(fragmentSize);
    }

    /**
     * Return Stereo matches in descending order.
     * @return sorted stereo matches
     */
    @TestMethod("testGetStereoMatches")
    public List<Double> getStereoMatches() {

        return Collections.unmodifiableList(stereoScore);
    }

    private IAtomContainer getMappedFragment(IAtomContainer molecule, Map<IAtom, IAtom> atomsMCS, int key) {
        IAtomContainer subgraphContainer = molecule.getBuilder().newInstance(IAtomContainer.class);
        atomsMCS.keySet();
        if (key == 1) {
            for (IAtom atoms : molecule.atoms()) {
                if (!atomsMCS.containsKey(atoms)) {
                    subgraphContainer.removeAtomAndConnectedElectronContainers(atoms);
                }
            }
        } else if (key == 2) {
            for (IAtom atoms : molecule.atoms()) {
                if (!atomsMCS.containsValue(atoms)) {
                    subgraphContainer.removeAtomAndConnectedElectronContainers(atoms);
                }
            }
        } else {

            System.out.println("1: Reactant, 2: Product " + key + "is invalid option");
        }

        return subgraphContainer;

    }

    private double getAtomScore(double score, Map<IAtom, IAtom> atomMapMCS, IAtomContainer Reactant,
            IAtomContainer Product) {
        for (Map.Entry<IAtom, IAtom> mappings : atomMapMCS.entrySet()) {
            IAtom rAtom = mappings.getKey();
            IAtom pAtom = mappings.getValue();

            int rHCount = 0;
            int pHCount = 0;
            double rBO = Reactant.getBondOrderSum(rAtom);
            double pBO = Product.getBondOrderSum(pAtom);

            if (rAtom.getHydrogenCount() != null) {
                rHCount = rAtom.getHydrogenCount();
            }
            if (pAtom.getHydrogenCount() != null) {
                pHCount = pAtom.getHydrogenCount();
            }

            int HScore = Math.abs(rHCount - pHCount);
            double BOScore = Math.abs(rBO - pBO);

            if (rHCount != pHCount) {
                score -= HScore;
            } else {
                score += HScore;
            }

            if (rBO != pBO) {
                score -= BOScore;
            } else {
                score += BOScore;
            }
        }

        return score;

    }

    private double getBondScore(double score, Map<IBond, IBond> bondMaps) {
        for (Map.Entry<IBond, IBond> matchedBonds : bondMaps.entrySet()) {

            IBond RBond = matchedBonds.getKey();
            IBond PBond = matchedBonds.getValue();

            score += getBondFormalChargeMatches(RBond, PBond);
            score += getBondTypeMatches(RBond, PBond);
        }
        return score;
    }

    private double getBondFormalChargeMatches(IBond RBond, IBond PBond) {

        double score = 0.0;
        if (RBond.getAtom(0).getFormalCharge() == PBond.getAtom(0).getFormalCharge()) {
            score += Math.abs(RBond.getAtom(0).getFormalCharge() - PBond.getAtom(0).getFormalCharge());

        } else {
            score -= Math.abs(RBond.getAtom(0).getFormalCharge() - PBond.getAtom(0).getFormalCharge());

        }


        if (RBond.getAtom(1).getFormalCharge() == PBond.getAtom(1).getFormalCharge()) {
            score += Math.abs(RBond.getAtom(1).getFormalCharge() - PBond.getAtom(1).getFormalCharge());

        } else {
            score -= Math.abs(RBond.getAtom(1).getFormalCharge() - PBond.getAtom(1).getFormalCharge());
        }

        return score;

    }

    private double getRingMatchScore(double score, IAtomContainer subgraphRContainer, IAtomContainer subgraphPContainer) {

        SSSRFinder ringFinderR = new SSSRFinder(subgraphRContainer);
        IRingSet rRings = ringFinderR.findRelevantRings();
        SSSRFinder ringFinderP = new SSSRFinder(subgraphPContainer);
        IRingSet pRings = ringFinderP.findRelevantRings();

        int rLength = RingSetManipulator.getAtomCount(rRings);
        int pLength = RingSetManipulator.getAtomCount(pRings);

        score += getRingMatch(rRings, 1);
        score += getRingMatch(pRings, 2);

        if (rLength > 0) {

            if (rLength == pLength) {
                score += rLength * 2;
            }

            if (rLength > pLength) {
                score += (rLength - pLength) * 2;
            }

        }
        return score;
    }

    private double getEnergy(IAtomContainer Educt, IAtomContainer Product) throws CDKException {
        Double eEnergy = 0.0;
        BondEnergies bondEnergy = BondEnergies.getInstance();
        for (int i = 0; i < Educt.getBondCount(); i++) {
            IBond bond = Educt.getBond(i);
            eEnergy += getBondEnergy(bond, bondEnergy);
        }
        Double pEnergy = 0.0;
        for (int j = 0; j < Product.getBondCount(); j++) {
            IBond bond = Product.getBond(j);
            pEnergy += getBondEnergy(bond, bondEnergy);
        }
        return (eEnergy + pEnergy);
    }

    private double getBondEnergy(IBond bond, BondEnergies bondEnergy) {
        double energy = 0.0;
        if ((bond.getAtom(0).getFlag(0) == true && bond.getAtom(1).getFlag(0) == false)
                || (bond.getAtom(0).getFlag(0) == false && bond.getAtom(1).getFlag(0) == true)) {
            Integer val = bondEnergy.getEnergies(bond.getAtom(0), bond.getAtom(1), bond.getOrder());
            if (val != null) {
                energy = val;
            }
        }
        return energy;
    }

    private double getRingMatch(IRingSet Rings, int type) {
        double score = 0.0;
        for (IAtomContainer ac : RingSetManipulator.getAllAtomContainers(Rings)) {
            boolean flag = true;
            for (IAtom a : ac.atoms()) {
                for (Map<IAtom, IAtom> aMCS : allAtomMCS) {
                    if ((type == 1 && !aMCS.containsKey(a)) || (type == 2 && !aMCS.containsValue(a))) {
                        flag = false;
                        break;
                    }
                }
            }

            if (flag) {
                score += 10;
            }
        }
        return score;
    }

    private boolean getStereoMatch(Map<Integer, Double> stereoScoreMap,
            Map<Integer, Map<Integer, Integer>> allStereoMCS,
            Map<Integer, Map<IAtom, IAtom>> allStereoAtomMCS) throws CDKException {

        boolean stereoMatchFlag = false;
        IAtomContainer Reactant = rMol.getMolecule();
        IAtomContainer Product = pMol.getMolecule();
        CDKHueckelAromaticityDetector.detectAromaticity(Reactant);
        CDKHueckelAromaticityDetector.detectAromaticity(Product);

        for (Integer Key : allStereoMCS.keySet()) {
            double score = 0.0;
//            System.out.println("\nStart score " + score);
            Map<Integer, Integer> atomsMCS = allStereoMCS.get(Key);
            Map<IAtom, IAtom> atomMapMCS = allStereoAtomMCS.get(Key);

            score = getAtomScore(score, atomMapMCS, Reactant, Product);
            Map<IBond, IBond> bondMaps = makeBondMapsOfAtomMaps(rMol.getMolecule(), pMol.getMolecule(), atomsMCS);
            IAtomContainer subgraphRContainer = getMappedFragment(rMol.getMolecule(), atomMapMCS, 1);
            IAtomContainer subgraphPContainer = getMappedFragment(pMol.getMolecule(), atomMapMCS, 2);

            score = getBondScore(score, bondMaps);

            score += getRingMatchScore(score, subgraphRContainer, subgraphPContainer);
            if (!stereoMatchFlag) {
                stereoMatchFlag = true;
            }
//            System.out.println("\nStart score " + score);
            stereoScoreMap.put(Key, score);
        }
        return stereoMatchFlag;
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
                score -= 2;
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
                score -= 2;
            }
        } else {

            int ReactantBondType = queryBond.getOrder().ordinal();
            int ProductBondType = targetBond.getOrder().ordinal();


            if ((queryBond.getFlag(CDKConstants.ISAROMATIC) == targetBond.getFlag(CDKConstants.ISAROMATIC))
                    && (ReactantBondType == ProductBondType)) {
                score += 2;
            }

            if (queryBond.getFlag(CDKConstants.ISAROMATIC) && targetBond.getFlag(CDKConstants.ISAROMATIC)) {
                score += 4;
            }
            if (queryBond.getStereo() != targetBond.getStereo()) {
                score -= 2;
            } else {
                score += 2;
            }
            if (queryBond != targetBond) {
                score -= Math.abs(ReactantBondType - ProductBondType);
            } else {
                score += Math.abs(ReactantBondType - ProductBondType);
            }
        }

        return score;
    }

    private int getFragmentCount(IAtomContainer molecule) {
        boolean fragmentFlag = true;

        IAtomContainerSet fragmentMolSet = DefaultChemObjectBuilder.getInstance().newInstance(IMoleculeSet.class);

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
}
