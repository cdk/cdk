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
 * of your sourceAtomCount code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received rBondCount copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.smsd;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.smsd.algorithm.mcsplus.MCSPlusHandler;
import org.openscience.cdk.smsd.algorithm.rgraph.CDKMCSHandler;
import org.openscience.cdk.smsd.algorithm.rgraph.CDKSubGraphHandler;
import org.openscience.cdk.smsd.algorithm.single.SingleMappingHandler;
import org.openscience.cdk.smsd.algorithm.vflib.VFlibMCSHandler;
import org.openscience.cdk.smsd.algorithm.vflib.VFlibSubStructureHandler;
import org.openscience.cdk.smsd.algorithm.vflib.VFlibTurboHandler;
import org.openscience.cdk.smsd.filters.ChemicalFilters;
import org.openscience.cdk.smsd.global.TimeOut;
import org.openscience.cdk.smsd.interfaces.AbstractMCS;
import org.openscience.cdk.smsd.interfaces.Algorithm;
import org.openscience.cdk.smsd.tools.MolHandler;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 *  <p>This class implements the Isomorphism- a multipurpose structure comparison tool.
 *  It allows users to, i) find the maximal common substructure(s) (MCS);
 *  ii) perform the mapping of a substructure in another structure, and;
 *  iii) map two isomorphic structures.</p>
 *
 *  <p>It also comes with various published algorithms. The user is free to
 *  choose his favorite algorithm to perform MCS or substructure search.
 *  For example 0: Isomorphism algorithm, 1: MCSPlus, 2: VFLibMCS, 3: CDKMCS, 4:
 *  Substructure</p>
 *
 *  <p>It also has a set of robust chemical filters (i.e. bond energy, fragment
 *  count, stereo &amp; bond match) to sort the reported MCS solutions in a chemically
 *  relevant manner. Each comparison can be made with or without using the bond
 *  sensitive mode and with implicit or explicit hydrogens.</p>
 *
 *  <p>If you are using <font color="#FF0000">Isomorphism, please cite Rahman <i>et.al. 2009</i></font>
 *  {@cdk.cite SMSD2009}. The Isomorphism algorithm is described in this paper.
 *  </p>
 *
 *
 * <p>An example for <b>Substructure search</b>:</p>
 *  <pre>{@code
 *  SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
 *  // Benzene
 *  IAtomContainer A1 = sp.parseSmiles("C1=CC=CC=C1");
 *  // Napthalene
 *  IAtomContainer A2 = sp.parseSmiles("C1=CC2=C(C=C1)C=CC=C2");
 *  //Turbo mode search
 *  //Bond Sensitive is set true
 *  Isomorphism comparison = new Isomorphism(Algorithm.SubStructure, true);
 *  // set molecules, remove hydrogens, clean and configure molecule
 *  comparison.init(A1, A2, true, true);
 *  // set chemical filter true
 *  comparison.setChemFilters(false, false, false);
 *  if (comparison.isSubgraph()) {
 *  //Get similarity score
 *   System.out.println("Tanimoto coefficient:  " + comparison.getTanimotoSimilarity());
 *   System.out.println("A1 is a subgraph of A2:  " + comparison.isSubgraph());
 *  //Get Modified AtomContainer
 *   IAtomContainer Mol1 = comparison.getReactantMolecule();
 *   IAtomContainer Mol2 = comparison.getProductMolecule();
 *  // Print the mapping between molecules
 *   System.out.println(" Mappings: ");
 *   for (Map.Entry <Integer, Integer> mapping : comparison.getFirstMapping().entrySet()) {
 *      System.out.println((mapping.getKey() + 1) + " " + (mapping.getValue() + 1));
 *
 *      IAtom eAtom = Mol1.getAtom(mapping.getKey());
 *      IAtom pAtom = Mol2.getAtom(mapping.getValue());
 *      System.out.println(eAtom.getSymbol() + " " + pAtom.getSymbol());
 *   }
 *   System.out.println("");
 *  }
 *  }</pre>
 *
 * <p>An example for <b>MCS search</b>:</p>
 *  <pre>{@code
 *  SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
 *  // Benzene
 *  IAtomContainer A1 = sp.parseSmiles("C1=CC=CC=C1");
 *  // Napthalene
 *  IAtomContainer A2 = sp.parseSmiles("C1=CC2=C(C=C1)C=CC=C2");
 *  //{ 0: Default Isomorphism Algorithm, 1: MCSPlus Algorithm, 2: VFLibMCS Algorithm, 3: CDKMCS Algorithm}
 *  //Bond Sensitive is set true
 *  Isomorphism comparison = new Isomorphism(Algorithm.DEFAULT, true);
 *  // set molecules, remove hydrogens, clean and configure molecule
 *  comparison.init(A1, A2, true, true);
 *  // set chemical filter true
 *  comparison.setChemFilters(true, true, true);
 *
 *  //Get similarity score
 *  System.out.println("Tanimoto coefficient:  " + comparison.getTanimotoSimilarity());
 *  System.out.println("A1 is a subgraph of A2:  " + comparison.isSubgraph());
 *  //Get Modified AtomContainer
 *  IAtomContainer Mol1 = comparison.getReactantMolecule();
 *  IAtomContainer Mol2 = comparison.getProductMolecule();
 *  // Print the mapping between molecules
 *  System.out.println(" Mappings: ");
 *  for (Map.Entry <Integer, Integer> mapping : comparison.getFirstMapping().entrySet()) {
 *      System.out.println((mapping.getKey() + 1) + " " + (mapping.getValue() + 1));
 *
 *      IAtom eAtom = Mol1.getAtom(mapping.getKey());
 *      IAtom pAtom = Mol2.getAtom(mapping.getValue());
 *      System.out.println(eAtom.getSymbol() + " " + pAtom.getSymbol());
 *  }
 *  System.out.println("");
 *  }</pre>
 *
 * @cdk.require java1.5+
 *
 * @cdk.module smsd
 * @cdk.githash
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 * @deprecated A more recent version of SMSD is available at <a href="http://github.com/asad/smsd">
 *             http://github.com/asad/smsd</a>
 */
@Deprecated
public final class Isomorphism extends AbstractMCS implements Serializable {

    static final long                   serialVersionUID       = 10278639972837495L;
    private List<Map<Integer, Integer>> allMCS                 = null;
    private Map<Integer, Integer>       firstSolution          = null;
    private List<Map<IAtom, IAtom>>     allAtomMCS             = null;
    private Map<IAtom, IAtom>           firstAtomMCS           = null;
    private List<Map<IBond, IBond>>     allBondMCS             = null;
    private Map<IBond, IBond>           firstBondMCS           = null;
    private MolHandler                  rMol                   = null;
    private IQueryAtomContainer         queryMol               = null;
    private MolHandler                  pMol                   = null;
    private IAtomContainer              pAC                    = null;
    private List<Double>                stereoScore            = null;
    private List<Integer>               fragmentSize           = null;
    private List<Double>                bEnergies              = null;
    private Algorithm                   algorithmType;
    private boolean                     removeHydrogen         = false;
    private final static ILoggingTool   LOGGER                 = LoggingToolFactory
                                                                       .createLoggingTool(Isomorphism.class);
    private double                      bondSensitiveTimeOut   = 0.15;                                        //mins
    private double                      bondInSensitiveTimeOut = 1.00;                                        //mins
    private boolean                     subGraph               = false;
    private boolean                     matchBonds             = false;

    /**
     * This is the algorithm factory and entry port for all the MCS algorithm in the Isomorphism
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
    public Isomorphism(Algorithm algorithmType, boolean bondTypeFlag) {
        this.algorithmType = algorithmType;
        firstSolution = new TreeMap<Integer, Integer>();
        allMCS = new ArrayList<Map<Integer, Integer>>();
        allAtomMCS = new ArrayList<Map<IAtom, IAtom>>();
        firstAtomMCS = new HashMap<IAtom, IAtom>();
        allBondMCS = new ArrayList<Map<IBond, IBond>>();
        firstBondMCS = new HashMap<IBond, IBond>();

        setTime(bondTypeFlag);
        setMatchBonds(bondTypeFlag);
    }

    private synchronized void mcsBuilder(MolHandler mol1, MolHandler mol2) {

        int rBondCount = mol1.getMolecule().getBondCount();
        int pBondCount = mol2.getMolecule().getBondCount();

        int rAtomCount = mol1.getMolecule().getAtomCount();
        int pAtomCount = mol2.getMolecule().getAtomCount();

        if ((rBondCount == 0 && rAtomCount > 0) || (pBondCount == 0 && pAtomCount > 0)) {
            singleMapping();
        } else {
            chooseAlgorithm(rBondCount, pBondCount);
        }

        if (!allAtomMCS.isEmpty() && !firstAtomMCS.isEmpty() && firstAtomMCS.size() > 1) {
            setAllBondMaps(makeBondMapsOfAtomMaps(mol1.getMolecule(), mol2.getMolecule(), allAtomMCS));
            if (getAllBondMaps().iterator().hasNext()) {
                setFirstBondMap(getAllBondMaps().iterator().next());
            }
        }
    }

    private synchronized void mcsBuilder(IQueryAtomContainer mol1, IAtomContainer mol2) {

        int rBondCount = mol1.getBondCount();
        int pBondCount = mol2.getBondCount();

        int rAtomCount = mol1.getAtomCount();
        int pAtomCount = mol2.getAtomCount();

        if ((rBondCount == 0 && rAtomCount > 0) || (pBondCount == 0 && pAtomCount > 0)) {
            singleMapping();
        } else {
            chooseAlgorithm(rBondCount, pBondCount);
        }

        if (!allAtomMCS.isEmpty() && !firstAtomMCS.isEmpty() && firstAtomMCS.size() > 1) {
            setAllBondMaps(makeBondMapsOfAtomMaps(mol1, mol2, allAtomMCS));
            if (getAllBondMaps().iterator().hasNext()) {
                setFirstBondMap(getAllBondMaps().iterator().next());
            }
        }
    }

    /**
     * Returns bond maps between source and target molecules based on the atoms
     * @param ac1 source molecule
     * @param ac2 target molecule
     * @param mappings mappings between source and target molecule atoms
     * @return bond maps between source and target molecules based on the atoms
     */
    public static List<Map<IBond, IBond>> makeBondMapsOfAtomMaps(IAtomContainer ac1, IAtomContainer ac2,
            List<Map<IAtom, IAtom>> mappings) {
        List<Map<IBond, IBond>> bondMaps = new ArrayList<Map<IBond, IBond>>();
        for (Map<IAtom, IAtom> mapping : mappings) {
            bondMaps.add(makeBondMapOfAtomMap(ac1, ac2, mapping));
        }
        return bondMaps;
    }

    /**
     *
     * Returns bond map between source and target molecules based on the atoms
     * @param ac1 source molecule
     * @param ac2 target molecule
     * @param mapping mappings between source and target molecule atoms
     * @return bond map between source and target molecules based on the atoms
     */
    public static Map<IBond, IBond> makeBondMapOfAtomMap(IAtomContainer ac1, IAtomContainer ac2,
            Map<IAtom, IAtom> mapping) {
        Map<IBond, IBond> maps = new HashMap<IBond, IBond>();

        for (Map.Entry<IAtom, IAtom> mapS : mapping.entrySet()) {
            IAtom indexI = mapS.getKey();
            IAtom indexJ = mapS.getValue();

            for (Map.Entry<IAtom, IAtom> mapD : mapping.entrySet()) {
                IAtom indexIPlus = mapD.getKey();
                IAtom indexJPlus = mapD.getValue();

                if (!indexI.equals(indexIPlus) && !indexJ.equals(indexJPlus)) {
                    IBond ac1Bond = ac1.getBond(indexI, indexIPlus);
                    if (ac1Bond != null) {
                        IBond ac2Bond = ac2.getBond(indexJ, indexJPlus);
                        if (ac2Bond != null) {
                            maps.put(ac1Bond, ac2Bond);
                        }
                    }
                }
            }
        }

        //        System.out.println("bond Map size:" + maps.size());

        return maps;

    }

    private void chooseAlgorithm(int rBondCount, int pBondCount) {

        switch (algorithmType) {
            case CDKMCS:
                cdkMCSAlgorithm();
                break;
            case DEFAULT:
                defaultMCSAlgorithm();
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
            case TurboSubStructure:
                turboSubStructureAlgorithm(rBondCount, pBondCount);
        }
    }

    private synchronized void cdkMCSAlgorithm() {
        CDKMCSHandler mcs = null;
        mcs = new CDKMCSHandler();

        if (queryMol == null) {
            mcs.set(rMol, pMol);
        } else {
            mcs.set(queryMol, pAC);
        }
        mcs.searchMCS(isMatchBonds());

        clearMaps();

        firstSolution.putAll(mcs.getFirstMapping());
        allMCS.addAll(mcs.getAllMapping());

        firstAtomMCS.putAll(mcs.getFirstAtomMapping());
        allAtomMCS.addAll(mcs.getAllAtomMapping());

    }

    private synchronized void cdkSubgraphAlgorithm() {
        CDKSubGraphHandler mcs = null;
        mcs = new CDKSubGraphHandler();

        if (queryMol == null) {
            mcs.set(rMol, pMol);
        } else {
            mcs.set(queryMol, pAC);
        }

        clearMaps();

        if (mcs.isSubgraph(isMatchBonds())) {
            firstSolution.putAll(mcs.getFirstMapping());
            allMCS.addAll(mcs.getAllMapping());

            firstAtomMCS.putAll(mcs.getFirstAtomMapping());
            allAtomMCS.addAll(mcs.getAllAtomMapping());
        }

    }

    private synchronized void mcsPlusAlgorithm() {
        MCSPlusHandler mcs = null;
        mcs = new MCSPlusHandler();

        if (queryMol == null) {
            mcs.set(rMol, pMol);
        } else {
            mcs.set(queryMol, pAC);
        }
        mcs.searchMCS(isMatchBonds());

        clearMaps();

        firstSolution.putAll(mcs.getFirstMapping());
        allMCS.addAll(mcs.getAllMapping());

        firstAtomMCS.putAll(mcs.getFirstAtomMapping());
        allAtomMCS.addAll(mcs.getAllAtomMapping());
    }

    private void vfLibMCS() {
        VFlibMCSHandler mcs = null;
        mcs = new VFlibMCSHandler();
        if (queryMol == null) {
            mcs.set(rMol, pMol);
        } else {
            mcs.set(queryMol, pAC);
        }
        mcs.searchMCS(isMatchBonds());

        clearMaps();
        firstSolution.putAll(mcs.getFirstMapping());
        allMCS.addAll(mcs.getAllMapping());

        firstAtomMCS.putAll(mcs.getFirstAtomMapping());
        allAtomMCS.addAll(mcs.getAllAtomMapping());
    }

    private void subStructureHandler() {
        VFlibSubStructureHandler subGraphTurboSearch = null;
        subGraphTurboSearch = new VFlibSubStructureHandler();
        if (queryMol == null) {
            subGraphTurboSearch.set(rMol, pMol);
        } else {
            subGraphTurboSearch.set(queryMol, pAC);
        }
        clearMaps();
        subGraph = subGraphTurboSearch.isSubgraph(isMatchBonds());
        if (subGraph) {
            firstSolution.putAll(subGraphTurboSearch.getFirstMapping());
            allMCS.addAll(subGraphTurboSearch.getAllMapping());
            firstAtomMCS.putAll(subGraphTurboSearch.getFirstAtomMapping());
            allAtomMCS.addAll(subGraphTurboSearch.getAllAtomMapping());
        }
    }

    private void turboSubStructureHandler() {
        VFlibTurboHandler subGraphTurboSearch = null;
        subGraphTurboSearch = new VFlibTurboHandler();
        if (queryMol == null) {
            subGraphTurboSearch.set(rMol, pMol);
        } else {
            subGraphTurboSearch.set(queryMol, pAC);
        }
        clearMaps();
        subGraph = subGraphTurboSearch.isSubgraph(isMatchBonds());
        if (subGraph) {
            firstSolution.putAll(subGraphTurboSearch.getFirstMapping());
            allMCS.addAll(subGraphTurboSearch.getAllMapping());
            firstAtomMCS.putAll(subGraphTurboSearch.getFirstAtomMapping());
            allAtomMCS.addAll(subGraphTurboSearch.getAllAtomMapping());
        }
    }

    private void singleMapping() {
        SingleMappingHandler mcs = null;

        mcs = new SingleMappingHandler(removeHydrogen);
        if (queryMol == null) {
            mcs.set(rMol, pMol);
        } else {
            mcs.set(queryMol, pAC);
        }
        mcs.searchMCS(isMatchBonds());

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

    private boolean isBondMatch(IAtomContainer reactant, IAtomContainer product) {
        int counter = 0;
        Object[] ketSet = firstAtomMCS.keySet().toArray();
        for (int i = 0; i < ketSet.length; i++) {
            for (int j = i + 1; j < ketSet.length; j++) {
                IAtom indexI = (IAtom) ketSet[i];
                IAtom indexJ = (IAtom) ketSet[j];
                IBond rBond = reactant.getBond(indexI, indexJ);
                if (rBond != null) {
                    counter++;
                }
            }
        }

        Object[] valueSet = firstAtomMCS.values().toArray();
        for (int i = 0; i < valueSet.length; i++) {
            for (int j = i + 1; j < valueSet.length; j++) {
                IAtom indexI = (IAtom) valueSet[i];
                IAtom indexJ = (IAtom) valueSet[j];
                IBond pBond = product.getBond(indexI, indexJ);
                if (pBond != null) {
                    counter--;
                }
            }
        }
        return counter == 0 ? true : false;
    }

    private void defaultMCSAlgorithm() {
        try {
            if (isMatchBonds()) {
                cdkMCSAlgorithm();
                if (getFirstMapping() == null || isTimeOut()) {
                    vfLibMCS();
                }
            } else {
                mcsPlusAlgorithm();
                if (getFirstMapping() == null || isTimeOut()) {
                    vfLibMCS();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void subStructureAlgorithm(int rBondCount, int pBondCount) {
        try {
            if (rBondCount > 0 && pBondCount > 0) {
                cdkSubgraphAlgorithm();
                if (getFirstMapping() == null || isTimeOut()) {
                    subStructureHandler();
                }
            } else {
                singleMapping();
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    private void turboSubStructureAlgorithm(int rBondCount, int pBondCount) {
        try {
            if (rBondCount > 0 && pBondCount > 0) {
                turboSubStructureHandler();
            } else {
                singleMapping();
            }
        } catch (Exception e) {
            e.getMessage();
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

    public boolean isTimeOut() {
        return TimeOut.getInstance().isTimeOutFlag();
    }

    public void resetTimeOut() {
        TimeOut.getInstance().setTimeOutFlag(false);
    }

    private void clearMaps() {
        this.firstSolution.clear();
        this.allMCS.clear();
        this.allAtomMCS.clear();
        this.firstAtomMCS.clear();
    }

    /**
     *
     * @param reactant
     * @param product
     *
     */
    private void init(MolHandler reactant, MolHandler product) throws CDKException {
        this.rMol = reactant;
        this.pMol = product;
        mcsBuilder(reactant, product);
    }

    /**
     *
     * @param reactant
     * @param product
     *
     */
    @Override
    public void init(IQueryAtomContainer reactant, IAtomContainer product) throws CDKException {
        this.queryMol = reactant;
        this.pAC = product;
        mcsBuilder(queryMol, pAC);
    }

    /**
     * {@inheritDoc}
     * @param reactant
     * @param product
     */
    @Override
    public void init(IAtomContainer reactant, IAtomContainer product, boolean removeHydrogen,
            boolean cleanAndConfigureMolecule) throws CDKException {
        this.removeHydrogen = removeHydrogen;
        init(new MolHandler(reactant, removeHydrogen, cleanAndConfigureMolecule), new MolHandler(product,
                removeHydrogen, cleanAndConfigureMolecule));
    }

    /**
     * Initialize the query and targetAtomCount mol via mol files
     * @param sourceMolFileName source mol file name
     * @param targetMolFileName target mol file name
     * @param removeHydrogen    set true to make hydrogens implicit before search
     * @param cleanAndConfigureMolecule eg: percieveAtomTypesAndConfigureAtoms, detect aromaticity etc
     * @throws CDKException
     */
    public void init(String sourceMolFileName, String targetMolFileName, boolean removeHydrogen,
            boolean cleanAndConfigureMolecule) throws CDKException {
        this.removeHydrogen = removeHydrogen;
        init(new MolHandler(sourceMolFileName, cleanAndConfigureMolecule, removeHydrogen), new MolHandler(
                targetMolFileName, cleanAndConfigureMolecule, removeHydrogen));
    }

    /** {@inheritDoc}
     */
    @Override
    public void setChemFilters(boolean stereoFilter, boolean fragmentFilter, boolean energyFilter) {

        if (firstAtomMCS != null) {
            ChemicalFilters chemFilter = new ChemicalFilters(allMCS, allAtomMCS, firstSolution, firstAtomMCS,
                    getReactantMolecule(), getProductMolecule());

            if (stereoFilter && firstAtomMCS.size() > 1) {
                try {
                    chemFilter.sortResultsByStereoAndBondMatch();
                    this.stereoScore = chemFilter.getStereoMatches();
                } catch (CDKException ex) {
                    LOGGER.error(Level.SEVERE, null, ex);
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
                    LOGGER.error(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /** {@inheritDoc}
     */
    @Override
    public synchronized Integer getFragmentSize(int key) {
        return (fragmentSize != null && !fragmentSize.isEmpty()) ? fragmentSize.get(key) : null;
    }

    /** {@inheritDoc}
     */
    @Override
    public synchronized Integer getStereoScore(int key) {
        return (stereoScore != null && !stereoScore.isEmpty()) ? stereoScore.get(key).intValue() : null;
    }

    /** {@inheritDoc}
     */
    @Override
    public synchronized Double getEnergyScore(int key) {
        return (bEnergies != null && !bEnergies.isEmpty()) ? bEnergies.get(key) : null;
    }

    /** {@inheritDoc}
     */
    @Override
    public synchronized Map<Integer, Integer> getFirstMapping() {
        return firstSolution.isEmpty() ? null : firstSolution;
    }

    /** {@inheritDoc}
     */
    @Override
    public synchronized List<Map<Integer, Integer>> getAllMapping() {
        return allMCS.isEmpty() ? null : allMCS;
    }

    /** {@inheritDoc}
     */
    @Override
    public synchronized Map<IAtom, IAtom> getFirstAtomMapping() {
        return firstAtomMCS.isEmpty() ? null : firstAtomMCS;
    }

    /** {@inheritDoc}
     */
    @Override
    public synchronized List<Map<IAtom, IAtom>> getAllAtomMapping() {
        return allAtomMCS.isEmpty() ? null : allAtomMCS;
    }

    /** {@inheritDoc}
     */
    @Override
    public IAtomContainer getReactantMolecule() {
        return queryMol == null ? rMol.getMolecule() : queryMol;
    }

    /** {@inheritDoc}
     */
    @Override
    public IAtomContainer getProductMolecule() {
        return pAC == null ? pMol.getMolecule() : pAC;
    }

    /** {@inheritDoc}
     */
    @Override
    public double getTanimotoSimilarity() throws IOException {
        double tanimoto = getTanimotoAtomSimilarity() + getTanimotoBondSimilarity();
        if (tanimoto > 0 && getReactantMolecule().getBondCount() > 0 && getProductMolecule().getBondCount() > 0) {
            tanimoto /= 2;
        }
        return tanimoto;
    }

    public double getTanimotoAtomSimilarity() throws IOException {
        int decimalPlaces = 4;
        int rAtomCount = 0;
        int pAtomCount = 0;
        double tanimotoAtom = 0.0;

        if (getFirstMapping() != null && !getFirstMapping().isEmpty()) {
            if (!removeHydrogen) {
                rAtomCount = getReactantMolecule().getAtomCount();
                pAtomCount = getProductMolecule().getAtomCount();
            } else {
                rAtomCount = getReactantMolecule().getAtomCount() - getHCount(getReactantMolecule());
                pAtomCount = getProductMolecule().getAtomCount() - getHCount(getProductMolecule());
            }
            double matchCount = getFirstMapping().size();
            tanimotoAtom = (matchCount) / (rAtomCount + pAtomCount - matchCount);
            BigDecimal tan = new BigDecimal(tanimotoAtom);
            tan = tan.setScale(decimalPlaces, BigDecimal.ROUND_HALF_UP);
            tanimotoAtom = tan.doubleValue();
        }
        return tanimotoAtom;
    }

    public double getTanimotoBondSimilarity() throws IOException {
        int decimalPlaces = 4;
        int rBondCount = 0;
        int pBondCount = 0;
        double tanimotoAtom = 0.0;

        if (getFirstBondMap() != null && !getFirstBondMap().isEmpty()) {
            rBondCount = getReactantMolecule().getBondCount();
            pBondCount = getProductMolecule().getBondCount();

            double matchCount = getFirstBondMap().size();
            tanimotoAtom = (matchCount) / (rBondCount + pBondCount - matchCount);
            BigDecimal tan = new BigDecimal(tanimotoAtom);
            tan = tan.setScale(decimalPlaces, BigDecimal.ROUND_HALF_UP);
            tanimotoAtom = tan.doubleValue();
        }
        return tanimotoAtom;
    }

    /** {@inheritDoc}
     *
     */
    @Override
    public boolean isStereoMisMatch() {
        boolean flag = false;
        IAtomContainer reactant = getReactantMolecule();
        IAtomContainer product = getProductMolecule();
        int score = 0;

        for (Map.Entry<IAtom, IAtom> mappingI : firstAtomMCS.entrySet()) {
            IAtom indexI = mappingI.getKey();
            IAtom indexJ = mappingI.getValue();
            for (Map.Entry<IAtom, IAtom> mappingJ : firstAtomMCS.entrySet()) {

                IAtom indexIPlus = mappingJ.getKey();
                IAtom indexJPlus = mappingJ.getValue();
                if (!indexI.equals(indexIPlus) && !indexJ.equals(indexJPlus)) {

                    IAtom sourceAtom1 = indexI;
                    IAtom sourceAtom2 = indexIPlus;

                    IBond rBond = reactant.getBond(sourceAtom1, sourceAtom2);

                    IAtom targetAtom1 = indexJ;
                    IAtom targetAtom2 = indexJPlus;
                    IBond pBond = product.getBond(targetAtom1, targetAtom2);

                    if ((rBond != null && pBond != null) && (rBond.getStereo() != pBond.getStereo())) {
                        score++;
                    }
                }
            }
        }
        if (score > 0) {
            flag = true;
        }
        return flag;
    }

    /** {@inheritDoc}
     *
     */
    @Override
    public boolean isSubgraph() {

        IAtomContainer reactant = getReactantMolecule();
        IAtomContainer product = getProductMolecule();

        float mappingSize = 0;
        if (firstSolution != null && !firstSolution.isEmpty()) {
            mappingSize = firstSolution.size();
        } else {
            return false;
        }
        int sourceAtomCount = reactant.getAtomCount();
        int targetAtomCount = product.getAtomCount();
        if (removeHydrogen) {
            sourceAtomCount -= getHCount(reactant);
            targetAtomCount -= getHCount(product);
        }
        if (mappingSize == sourceAtomCount && mappingSize <= targetAtomCount) {
            if (!getFirstBondMap().isEmpty() && getFirstBondMap().size() == reactant.getBondCount()) {
                return true;
            } else if (mappingSize == 1) {
                return true;
            }
        }
        return false;
    }

    /** {@inheritDoc}
     */
    @Override
    public double getEuclideanDistance() throws IOException {
        int decimalPlaces = 4;
        double source = 0;
        double target = 0;
        double euclidean = -1;

        if (getFirstMapping() != null || !getFirstMapping().isEmpty()) {
            if (!removeHydrogen) {
                source = getReactantMolecule().getAtomCount();
                target = getProductMolecule().getAtomCount();
            } else {
                source = getReactantMolecule().getAtomCount() - getHCount(getReactantMolecule());
                target = getProductMolecule().getAtomCount() - getHCount(getProductMolecule());
            }
            double common = getFirstMapping().size();
            euclidean = Math.sqrt(source + target - 2 * common);
            BigDecimal dist = new BigDecimal(euclidean);
            dist = dist.setScale(decimalPlaces, BigDecimal.ROUND_HALF_UP);
            euclidean = dist.doubleValue();
        }
        return euclidean;
    }

    /**
     * {@inheritDoc}
     * @return the bondSensitiveTimeOut
     */
    @Override
    public double getBondSensitiveTimeOut() {
        return bondSensitiveTimeOut;
    }

    /**
     * {@inheritDoc}
     * @param bondSensitiveTimeOut the bond Sensitive Timeout in mins (default 0.10 min)
     */
    @Override
    public void setBondSensitiveTimeOut(double bondSensitiveTimeOut) {
        this.bondSensitiveTimeOut = bondSensitiveTimeOut;
    }

    /**
     * {@inheritDoc}
     * @return the bondInSensitiveTimeOut
     */
    @Override
    public double getBondInSensitiveTimeOut() {
        return bondInSensitiveTimeOut;
    }

    /**
     * {@inheritDoc}
     * @param bondInSensitiveTimeOut the bond insensitive Timeout in mins (default 0.15 min)
     */
    @Override
    public void setBondInSensitiveTimeOut(double bondInSensitiveTimeOut) {
        this.bondInSensitiveTimeOut = bondInSensitiveTimeOut;
    }

    /**
     * @return the matchBonds
     */
    public boolean isMatchBonds() {
        return matchBonds;
    }

    /**
     * @param matchBonds the matchBonds to set
     */
    public void setMatchBonds(boolean matchBonds) {
        this.matchBonds = matchBonds;
    }

    /**
     * @return the allBondMCS
     */
    public List<Map<IBond, IBond>> getAllBondMaps() {
        return allBondMCS;
    }

    /**
     * @param allBondMCS the allBondMCS to set
     */
    private void setAllBondMaps(List<Map<IBond, IBond>> allBondMCS) {
        this.allBondMCS = allBondMCS;
    }

    /**
     * @return the firstBondMCS
     */
    public Map<IBond, IBond> getFirstBondMap() {
        return firstBondMCS;
    }

    /**
     * @param firstBondMCS the firstBondMCS to set
     */
    private void setFirstBondMap(Map<IBond, IBond> firstBondMCS) {
        this.firstBondMCS = firstBondMCS;
    }
}
