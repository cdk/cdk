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
 * You should have received rAtomCount copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.smsd.factory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.smsd.algorithm.cdk.CDKMCSHandler;
import org.openscience.cdk.smsd.algorithm.mcsplus.MCSPlusHandler;
import org.openscience.cdk.smsd.algorithm.single.SingleMappingHandler;
import org.openscience.cdk.smsd.algorithm.vflib.VFlibMCSHandler;
import org.openscience.cdk.smsd.algorithm.vflib.VFlibTurboHandler;
import org.openscience.cdk.smsd.filters.ChemicalFilters;
import org.openscience.cdk.smsd.global.BondType;
import org.openscience.cdk.smsd.global.TimeOut;
import org.openscience.cdk.smsd.helper.MolHandler;
import org.openscience.cdk.smsd.interfaces.Algorithm;
import org.openscience.cdk.smsd.interfaces.AbstractMCS;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * This is the algorithm factory and entry port for all the MCS algorithm in the SMSD
 * supported algorithm {@link org.openscience.cdk.smsd.interfaces.Algorithm} types.
 * <OL>
 * <lI>0: Default SMSD,
 * <lI>1: MCSPlus,
 * <lI>2: VFLibMCS,
 * <lI>3: CDKMCS,
 * <lI>4: SubStructure
 * </OL>
 * @cdk.module smsd
 * @cdk.githash
 * @author Syed Asad Rahman <asad@ebi.ac.uk>
 */
@TestClass("org.openscience.cdk.smsd.factory.SubStructureSearchAlgorithmsTest")
public class SubStructureSearchAlgorithms extends AbstractMCS {

    private List<Map<Integer, Integer>> allMCS = null;
    private Map<Integer, Integer> firstSolution = null;
    private List<Map<IAtom, IAtom>> allAtomMCS = null;
    private Map<IAtom, IAtom> firstAtomMCS = null;
    private MolHandler rMol = null;
    private MolHandler pMol = null;
    private IAtomContainerSet rFrag = null;
    private IAtomContainerSet pFrag = null;
    private List<Double> stereoScore = null;
    private List<Integer> fragmentSize = null;
    private List<Double> bEnergies = null;
    private Algorithm algorithmType;
    private boolean removeHydrogen = false;
    private final static ILoggingTool Logger =
            LoggingToolFactory.createLoggingTool(SubStructureSearchAlgorithms.class);
    private double bondSensitiveTimeOut = 0.10;//mins
    private double bondInSensitiveTimeOut = 0.15;//mins

    /**
     * This is the algorithm factory and entry port for all the MCS algorithm in the SMSD
     * supported algorithm {@link org.openscience.cdk.smsd.interfaces.Algorithm} types:
     * <OL>
     * <lI>0: Default,
     * <lI>1: MCSPlus,
     * <lI>2: VFLibMCS,
     * <lI>3: CDKMCS,
     * <lI>4: SubStructure
     * </OL>
     * @param algorithmType {@link org.openscience.cdk.smsd.interfaces.Algorithm}
     * @param bondTypeFlag
     */
    @TestMethod("testSubStructureSearchAlgorithms")
    public SubStructureSearchAlgorithms(Algorithm algorithmType, boolean bondTypeFlag) {
        this.algorithmType = algorithmType;
        firstSolution = new TreeMap<Integer, Integer>();
        allMCS = new ArrayList<Map<Integer, Integer>>();
        allAtomMCS = new ArrayList<Map<IAtom, IAtom>>();
        firstAtomMCS = new HashMap<IAtom, IAtom>();

        setTime(bondTypeFlag);
        BondType bondType = BondType.getInstance();
        bondType.setBondSensitiveFlag(bondTypeFlag);
//        System.out.println(bondType.isBondSensitive());
    }

    private synchronized void mcsBuilder() {

        int rBondCount = rMol.getMolecule().getBondCount();
        int pBondCount = pMol.getMolecule().getBondCount();

        int rAtomCount = rMol.getMolecule().getAtomCount();
        int pAtomCount = pMol.getMolecule().getAtomCount();
        if (rBondCount == 0 || rAtomCount == 1 || pBondCount == 0 || pAtomCount == 1) {
            singleMapping();
        } else {
            chooseAlgorithm(rBondCount, pBondCount);
        }
    }

    private void chooseAlgorithm(int rBondCount, int pBondCount) {

        switch (algorithmType) {
            case CDKMCS:
                cdkMCSAlgorithm();
                break;
            case DEFAULT:
                defaultAlgorithm();
                break;
            case MCSPlus:
                mcsPlusAlgorithm();
                break;
            case SubStructure:
                subStructureAlgorithm(rBondCount, pBondCount);
                break;
            case VFLibMCS:
                vfLibMCSAlgorithm();
                break;
            case TURBOMCS:
                try {
                    throw new CDKException("This mode is no longer supported");
                } catch (CDKException ex) {
                    Logger.error(Level.SEVERE, null, ex);
                }
        }
    }

    private synchronized void fragmentBuilder() {

        FragmentMatcher fragmentMatcher = new FragmentMatcher(rFrag, pFrag, removeHydrogen);
        fragmentMatcher.searchMCS();

        clearMaps();
        firstSolution.putAll(fragmentMatcher.getFirstMapping());
        allMCS.addAll(fragmentMatcher.getAllMapping());

        firstAtomMCS.putAll(fragmentMatcher.getFirstAtomMapping());
        allAtomMCS.addAll(fragmentMatcher.getAllAtomMapping());

    }

    private synchronized void cdkMCSAlgorithm() {
        CDKMCSHandler mcs = null;
        mcs = new CDKMCSHandler();

        mcs.set(rMol, pMol);
        mcs.searchMCS();

        clearMaps();

        firstSolution.putAll(mcs.getFirstMapping());
        allMCS.addAll(mcs.getAllMapping());

        firstAtomMCS.putAll(mcs.getFirstAtomMapping());
        allAtomMCS.addAll(mcs.getAllAtomMapping());



    }

    private synchronized void mcsPlusAlgorithm() {
        MCSPlusHandler mcs = null;
        mcs = new MCSPlusHandler();

        mcs.set(rMol, pMol);
        mcs.searchMCS();

        clearMaps();

        firstSolution.putAll(mcs.getFirstMapping());
        allMCS.addAll(mcs.getAllMapping());

        firstAtomMCS.putAll(mcs.getFirstAtomMapping());
        allAtomMCS.addAll(mcs.getAllAtomMapping());
    }

    private void vfTurboHandler() {
        VFlibTurboHandler subGraphTurboSearch = null;
        subGraphTurboSearch = new VFlibTurboHandler();
        subGraphTurboSearch.set(rMol, pMol);
        clearMaps();
        if (subGraphTurboSearch.isSubgraph()) {
            firstSolution.putAll(subGraphTurboSearch.getFirstMapping());
            allMCS.addAll(subGraphTurboSearch.getAllMapping());
            firstAtomMCS.putAll(subGraphTurboSearch.getFirstAtomMapping());
            allAtomMCS.addAll(subGraphTurboSearch.getAllAtomMapping());
        }
    }

    private void vfLibMCS() {
        VFlibMCSHandler mcs = null;
        mcs = new VFlibMCSHandler();
        mcs.set(rMol, pMol);
        mcs.searchMCS();

        clearMaps();
        firstSolution.putAll(mcs.getFirstMapping());
        allMCS.addAll(mcs.getAllMapping());

        firstAtomMCS.putAll(mcs.getFirstAtomMapping());
        allAtomMCS.addAll(mcs.getAllAtomMapping());
    }

    private void singleMapping() {
        SingleMappingHandler mcs = null;

        mcs = new SingleMappingHandler(removeHydrogen);
        mcs.set(rMol, pMol);
        mcs.searchMCS();

        clearMaps();
        firstSolution.putAll(mcs.getFirstMapping());
        allMCS.addAll(mcs.getAllMapping());

        firstAtomMCS.putAll(mcs.getFirstAtomMapping());
        allAtomMCS.addAll(mcs.getAllAtomMapping());

    }

    private int getHCount(IAtomContainer molecule) {
        int count = 0;
        for (IAtom atom : molecule.atoms()) {
            if (atom.getSymbol().equalsIgnoreCase("H")) {
                ++count;
            }
        }

        return count;
    }

    private boolean isBondMatch(IAtomContainer Reactant, IAtomContainer Product) {
        int counter = 0;
        Object[] ketSet = firstAtomMCS.keySet().toArray();
        for (int i = 0; i < ketSet.length; i++) {
            for (int j = i + 1; j < ketSet.length; j++) {
                IAtom indexI = (IAtom) ketSet[i];
                IAtom indexJ = (IAtom) ketSet[j];
                IBond RBond = Reactant.getBond(indexI, indexJ);
                if (RBond != null) {
                    counter++;
                }
            }
        }

        Object[] valueSet = firstAtomMCS.values().toArray();
        for (int i = 0; i < valueSet.length; i++) {
            for (int j = i + 1; j < valueSet.length; j++) {
                IAtom indexI = (IAtom) valueSet[i];
                IAtom indexJ = (IAtom) valueSet[j];
                IBond RBond = Product.getBond(indexI, indexJ);
                if (RBond != null) {
                    counter--;
                }
            }
        }
        return counter == 0 ? true : false;
    }

    private void defaultAlgorithm() {
        if (BondType.getInstance().isBondSensitive()) {
            cdkMCSAlgorithm();
        } else {
            mcsPlusAlgorithm();
        }
        if (isTimeOut() || getFirstAtomMapping() == null) {
            vfLibMCS();
        }
    }

    private void subStructureAlgorithm(int rBondCount, int pBondCount) {
        if (rBondCount > 0 && pBondCount > 0) {
            vfTurboHandler();
        } else {
            singleMapping();
        }
    }

    private void vfLibMCSAlgorithm() {
        vfLibMCS();
    }

    private void setTime(boolean bondTypeFlag) {
        if (bondTypeFlag) {
            TimeOut tmo = TimeOut.getInstance();
            tmo.setTimeOut(getBondSensitiveTimeOut());
        } else {
            TimeOut tmo = TimeOut.getInstance();
            tmo.setTimeOut(getBondInSensitiveTimeOut());
        }
    }

    private boolean isTimeOut() {
        return TimeOut.getInstance().isTimeOutFlag();
    }

    private void clearMaps() {
        this.firstSolution.clear();
        this.allMCS.clear();
        this.allAtomMCS.clear();
        this.firstAtomMCS.clear();
    }

    /**
     *
     * @param Reactant
     * @param Product
     * @param removeHydrogen
     *
     */
    private void init(MolHandler Reactant, MolHandler Product, boolean removeHydrogen) throws CDKException {
        this.removeHydrogen = removeHydrogen;
        this.rMol = new MolHandler(Reactant.getMolecule(), false, removeHydrogen);
        this.pMol = new MolHandler(Product.getMolecule(), false, removeHydrogen);

        if (rMol.getConnectedFlag() && pMol.getConnectedFlag()) {
            mcsBuilder();
        } else {
            this.rFrag = rMol.getFragmentedMolecule();
            this.pFrag = pMol.getFragmentedMolecule();
            fragmentBuilder();
        }

    }

    /**
     * {@inheritDoc}
     * @param Reactant
     * @param Product
     */
    @Override
    @TestMethod("testInit_3args_1")
    public void init(IMolecule Reactant, IMolecule Product, boolean removeHydrogen) throws CDKException {
        this.removeHydrogen = removeHydrogen;
        this.rMol = new MolHandler(Reactant, false, removeHydrogen);
        this.pMol = new MolHandler(Product, false, removeHydrogen);
        init(rMol, pMol, removeHydrogen);
    }

    /**
     * {@inheritDoc}
     * @param Reactant
     * @param Product
     */
    @Override
    @TestMethod("testInit_3args_2")
    public void init(IAtomContainer Reactant, IAtomContainer Product, boolean removeHydrogen) throws CDKException {
        this.removeHydrogen = removeHydrogen;
        this.rMol = new MolHandler(Reactant, false, removeHydrogen);
        this.pMol = new MolHandler(Product, false, removeHydrogen);
        init(rMol, pMol, removeHydrogen);
    }

    /**
     * Initialize the query and target mol via mol files
     * @param sourceMolFileName
     * @param targetMolFileName
     * @param removeHydrogen
     * @throws CDKException
     */
    @TestMethod("testInit_3args_3")
    public void init(String sourceMolFileName, String targetMolFileName, boolean removeHydrogen) throws CDKException {
        String mol1 = sourceMolFileName;
        String mol2 = targetMolFileName;

        this.removeHydrogen = removeHydrogen;
        MolHandler Reactant = new MolHandler(mol1, false);
        MolHandler Product = new MolHandler(mol2, false);
        init(Reactant, Product, removeHydrogen);
    }

    /** {@inheritDoc}
     */
    @Override
    @TestMethod("testSetChemFilters")
    public void setChemFilters(boolean stereoFilter, boolean fragmentFilter, boolean energyFilter) {

        if (firstAtomMCS != null) {
            ChemicalFilters chemFilter = new ChemicalFilters(allMCS, allAtomMCS, firstSolution, firstAtomMCS, rMol, pMol);

            if (stereoFilter && firstAtomMCS.size() > 1) {
                try {
                    chemFilter.sortResultsByStereoAndBondMatch();
                    this.stereoScore = chemFilter.getStereoMatches();
                } catch (CDKException ex) {
                    Logger.error(Level.SEVERE, null, ex);
                }
            }
            if (fragmentFilter) {
                chemFilter.sortResultsByFragments();
                this.fragmentSize = chemFilter.getSortedFragment();
            }

            if (energyFilter) {
                try {
                    chemFilter.sortResultsByEnergies();
                    this.bEnergies = chemFilter.getSortedEnergy();
                } catch (CDKException ex) {
                    Logger.error(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /** {@inheritDoc}
     */
    @Override
    @TestMethod("testGetFragmentSize")
    public synchronized Integer getFragmentSize(int Key) {
        return (fragmentSize != null && !fragmentSize.isEmpty())
                ? fragmentSize.get(Key) : null;
    }

    /** {@inheritDoc}
     */
    @Override
    @TestMethod("testGetStereoScore")
    public synchronized Integer getStereoScore(int Key) {
        return (stereoScore != null && !stereoScore.isEmpty()) ? stereoScore.get(Key).intValue() : null;
    }

    /** {@inheritDoc}
     */
    @Override
    @TestMethod("testGetEnergyScore")
    public synchronized Double getEnergyScore(int Key) {
        return (bEnergies != null && !bEnergies.isEmpty()) ? bEnergies.get(Key) : null;
    }

    /** {@inheritDoc}
     */
    @Override
    @TestMethod("testGetFirstMapping")
    public synchronized Map<Integer, Integer> getFirstMapping() {
        return firstSolution.isEmpty() ? null : firstSolution;
    }

    /** {@inheritDoc}
     */
    @Override
    @TestMethod("testGetAllMapping")
    public synchronized List<Map<Integer, Integer>> getAllMapping() {
        return allMCS.isEmpty() ? null : allMCS;
    }

    /** {@inheritDoc}
     */
    @Override
    @TestMethod("testGetFirstAtomMapping")
    public synchronized Map<IAtom, IAtom> getFirstAtomMapping() {
        return firstAtomMCS.isEmpty() ? null : firstAtomMCS;
    }

    /** {@inheritDoc}
     */
    @Override
    @TestMethod("testGetAllAtomMapping")
    public synchronized List<Map<IAtom, IAtom>> getAllAtomMapping() {
        return allAtomMCS.isEmpty() ? null : allAtomMCS;
    }

    /** {@inheritDoc}
     */
    @Override
    @TestMethod("testGetReactantMolecule")
    public IAtomContainer getReactantMolecule() {
        return rMol.getMolecule();
    }

    /** {@inheritDoc}
     */
    @Override
    @TestMethod("testGetProductMolecule")
    public IAtomContainer getProductMolecule() {
        return pMol.getMolecule();
    }

    /** {@inheritDoc}
     */
    @Override
    @TestMethod("testGetTanimotoSimilarity")
    public double getTanimotoSimilarity() throws IOException {
        int decimalPlaces = 4;
        int rAtomCount = 0;
        int pAtomCount = 0;
        if (!removeHydrogen) {
            rAtomCount = rMol.getMolecule().getAtomCount();
            pAtomCount = pMol.getMolecule().getAtomCount();
        } else {
            rAtomCount = rMol.getMolecule().getAtomCount() - getHCount(rMol.getMolecule());
            pAtomCount = pMol.getMolecule().getAtomCount() - getHCount(pMol.getMolecule());
        }
        double matchCount = getFirstMapping().size();
        double tanimoto = (matchCount) / (rAtomCount + pAtomCount - matchCount);

        BigDecimal tan = new BigDecimal(tanimoto);

        tan = tan.setScale(decimalPlaces, BigDecimal.ROUND_HALF_UP);
        tanimoto = tan.doubleValue();
        return tanimoto;
    }

    /** {@inheritDoc}
     * Returns true if mols have different stereo
     * chemistry else flase if no stereo mismatch.
     * @return true if mols have different stereo
     * chemistry else flase if no stereo mismatch.
     */
    @Override
    @TestMethod("testIsStereoMisMatch")
    public boolean isStereoMisMatch() {
        boolean flag = false;
        IAtomContainer Reactant = rMol.getMolecule();
        IAtomContainer Product = pMol.getMolecule();
        int Score = 0;

        for (Map.Entry<IAtom, IAtom> mappingI : firstAtomMCS.entrySet()) {
            IAtom indexI = mappingI.getKey();
            IAtom indexJ = mappingI.getValue();
            for (Map.Entry<IAtom, IAtom> mappingJ : firstAtomMCS.entrySet()) {

                IAtom indexIPlus = mappingJ.getKey();
                IAtom indexJPlus = mappingJ.getValue();
                if (!indexI.equals(indexIPlus) && !indexJ.equals(indexJPlus)) {

                    IAtom sourceAtom1 = indexI;
                    IAtom sourceAtom2 = indexIPlus;

                    IBond RBond = Reactant.getBond(sourceAtom1, sourceAtom2);

                    IAtom targetAtom1 = indexJ;
                    IAtom targetAtom2 = indexJPlus;
                    IBond PBond = Product.getBond(targetAtom1, targetAtom2);

                    if ((RBond != null && PBond != null) && (RBond.getStereo() != PBond.getStereo())) {
                        Score++;
                    }
                }

            }
        }
        if (Score > 0) {
            flag = true;
        }
        return flag;
    }

    /** {@inheritDoc}
     * Returns true if query is a subgraph of target else false
     * @return true if query is a subgraph of target
     */
    @Override
    @TestMethod("testIsSubgraph")
    public boolean isSubgraph() {

        IAtomContainer Reactant = rMol.getMolecule();
        IAtomContainer Product = pMol.getMolecule();
        if (firstAtomMCS == null || firstAtomMCS.isEmpty()) {
            return false;
        }
        float mappingSize = firstSolution.size();
        int source = Reactant.getAtomCount();
        int target = Product.getAtomCount();
        if (removeHydrogen) {
            source -= getHCount(Reactant);
            target -= getHCount(Product);
        }
        if (mappingSize == source && target >= mappingSize && isBondMatch(Reactant, Product)) {
            return true;
        }
        return false;
    }

    /** {@inheritDoc}
     */
    @Override
    @TestMethod("testGetEuclideanDistance")
    public double getEuclideanDistance() throws IOException {
        int decimalPlaces = 4;
        double source = 0;
        double target = 0;
        if (!removeHydrogen) {
            source = rMol.getMolecule().getAtomCount();
            target = pMol.getMolecule().getAtomCount();
        } else {
            source = rMol.getMolecule().getAtomCount() - getHCount(rMol.getMolecule());
            target = pMol.getMolecule().getAtomCount() - getHCount(pMol.getMolecule());
        }
        double common = getFirstMapping().size();
        double euclidean = Math.sqrt(source + target - 2 * common);

        BigDecimal dist = new BigDecimal(euclidean);
        dist = dist.setScale(decimalPlaces, BigDecimal.ROUND_HALF_UP);
        euclidean = dist.doubleValue();
        return euclidean;
    }

    /**
     * @return the bondSensitiveTimeOut
     */
    public double getBondSensitiveTimeOut() {
        return bondSensitiveTimeOut;
    }

    /**
     * @param bondSensitiveTimeOut the bond Sensitive Timeout in mins (default 0.15 min)
     */
    public void setBondSensitiveTimeOut(double bondSensitiveTimeOut) {
        this.bondSensitiveTimeOut = bondSensitiveTimeOut;
    }

    /**
     * @return the bondInSensitiveTimeOut
     */
    public double getBondInSensitiveTimeOut() {
        return bondInSensitiveTimeOut;
    }

    /**
     * @param bondInSensitiveTimeOut the bond insensitive Timeout in mins (default 0.15 min)
     */
    public void setBondInSensitiveTimeOut(double bondInSensitiveTimeOut) {
        this.bondInSensitiveTimeOut = bondInSensitiveTimeOut;
    }
}
