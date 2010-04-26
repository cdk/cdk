/* Copyright (C) 2006-2010  Syed Asad Rahman {asad@ebi.ac.uk}
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
package org.openscience.cdk.smsd;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.smsd.factory.SubStructureSearchAlgorithms;
import org.openscience.cdk.smsd.interfaces.AbstractMCS;
import org.openscience.cdk.smsd.interfaces.Algorithm;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 *  <p>This class implements the SMSD- a multipurpose structure comparison tool.
 *  It allows users to, i) find the maximal common substructure(s) (MCS);
 *  ii) perform the mapping of a substructure in another structure, and;
 *  iii) map two isomorphic structures.</p>
 *
 *  <p>It also comes with various published algorithms. The user is free to
 *  choose his favorite algorithm to perform MCS or substructure search.
 *  For example 0: SMSD algorithm, 1: MCSPlus, 2: VFLibMCS, 3: CDKMCS, 4:
 *  Substructure</p>
 *
 *  <p>It also has a set of robust chemical filters (i.e. bond energy, fragment
 *  count, stereo & bond match) to sort the reported MCS solutions in a chemically
 *  relevant manner. Each comparison can be made with or without using the bond
 *  sensitive mode and with implicit or explicit hydrogens.</p>
 *  
 *  <p>If you are using <font color="#FF0000">SMSD, please cite Rahman <i>et.al. 2009</i></font>
 *  {@cdk.cite SMSD2009}. The SMSD algorithm is described in this paper.
 *  </p>
 *
 *
 * <p>An example for <b>Substructure search</b>:</p>
 *  <font color="#003366">
 *  <pre>
 *  SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
 *  // Benzene
 *  IAtomContainer A1 = sp.parseSmiles("C1=CC=CC=C1");
 *  // Napthalene
 *  IAtomContainer A2 = sp.parseSmiles("C1=CC2=C(C=C1)C=CC=C2");
 *  //Turbo mode search
 *  //Bond Sensitive is set true
 *  SMSD comparison = new SMSD(Algorithm.SubStructure, true);
 *  // set molecules and remove hydrogens
 *  comparison.init(A1, A2, true);
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
 *
 *  </pre>
 *  </font>
 *
 * <p>An example for <b>MCS search</b>:</p>
 *  <font color="#003366">
 *  <pre>
 *  SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
 *  // Benzene
 *  IAtomContainer A1 = sp.parseSmiles("C1=CC=CC=C1");
 *  // Napthalene
 *  IAtomContainer A2 = sp.parseSmiles("C1=CC2=C(C=C1)C=CC=C2");
 *  //{ 0: Default SMSD Algorithm, 1: MCSPlus Algorithm, 2: VFLibMCS Algorithm, 3: CDKMCS Algorithm}
 *  //Bond Sensitive is set true
 *  SMSD comparison = new SMSD(Algorithm.DEFAULT, true);
 *  // set molecules and remove hydrogens
 *  comparison.init(A1, A2, true);
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
 *
 *  </pre>
 *  </font>
 * 
 * @cdk.require java1.5+
 *
 * @cdk.module smsd
 * @cdk.githash
 * @author Syed Asad Rahman <asad@ebi.ac.uk>
 *
 */
@TestClass("org.openscience.cdk.smsd.SMSDTest")
public class SMSD extends SubStructureSearchAlgorithms {

    private final static ILoggingTool logger =
            LoggingToolFactory.createLoggingTool(SMSD.class);
    private AbstractMCS comparison = null;

    /**
     * Algorithm {@link org.openscience.cdk.smsd.interfaces.Algorithm}
     * choice for find MCS or performing substructure searches
     * <OL>
     * <lI>0: default,
     * <lI>1: MCSPlus,
     * <lI>2: VFLibMCS,
     * <lI>3: CDKMCS,
     * <lI>4: SubStructure
     * </OL>
     * @param algorithmType
     * <OL>
     * <lI>0: default,
     * <lI>1: MCSPlus,
     * <lI>2: VFLibMCS,
     * <lI>3: CDKMCS,
     * <lI>4: SubStructure
     * </OL> Mode
     * @param bondSensitiveFlag true will activate bond order match else false
     */
    public SMSD(Algorithm algorithmType, boolean bondSensitiveFlag) {
        super(algorithmType, bondSensitiveFlag);
        comparison = new SubStructureSearchAlgorithms(algorithmType, bondSensitiveFlag);
    }

    /** {@inheritDoc}
     *
     * initialize query and target molecules
     * @param Query query molecule
     * @param Target target molecule
     */
    @Override
    @TestMethod("testInit_3args_1")
    public synchronized void init(IMolecule Query, IMolecule Target, boolean removeHydrogen) {

        if (Query.getAtomCount() > 0 && Target.getAtomCount() > 0) {
            try {
                getComparison().init(Query, Target, removeHydrogen);
            } catch (CDKException ex) {
                logger.error(Level.SEVERE, null, ex);
            }
        } else {
            try {
                throw new CDKException("Each molecule should have atleast one atom to compare");
            } catch (CDKException ex) {
                logger.error(Level.SEVERE, null, ex);
            }
        }
    }

    /** {@inheritDoc}
     *
     * initialize query and target molecules
     * @param Query Atomcontainer
     * @param Target Atomcontainer
     */
    @Override
    @TestMethod("testInit_3args_2")
    public synchronized void init(IAtomContainer Query, IAtomContainer Target, boolean removeHydrogen) {

        if (Query.getAtomCount() > 0 && Target.getAtomCount() > 0) {
            try {
                getComparison().init(Query, Target, removeHydrogen);
            } catch (CDKException ex) {
                logger.error(Level.SEVERE, null, ex);
            }
        } else {
            try {
                throw new CDKException("Each molecule should have atleast one atom to compare");
            } catch (CDKException ex) {
                logger.error(Level.SEVERE, null, ex);
            }
        }
    }

    /** {@inheritDoc}
     *
     * These are important set of paraments which will refine and rank the
     * mcs solutions. The precedence of stereoFilter > fragmentFilter > energyFilter
     * @param stereoFilter   true if stereo match is considered else false
     * @param fragmentFilter true if fragement filter is switched on else false
     * @param energyFilter   true if bond energy filter is switched on else false
     */
    @Override
    @TestMethod("testSetChemFilters")
    public void setChemFilters(boolean stereoFilter, boolean fragmentFilter, boolean energyFilter) {
        getComparison().setChemFilters(stereoFilter, fragmentFilter, energyFilter);
    }

    /** {@inheritDoc}
     *
     * Returns all plausible mappings between query and target molecules
     * Each map in the list has atom-atom equivalence of the mappings
     * between query and target molecule i.e. map.getKey() for the query
     * and map.getValue() for the target molecule
     * @return A List of all possible mapped MCS atoms.
     *
     */
    @Override
    @TestMethod("testGetAllAtomMapping")
    public List<Map<IAtom, IAtom>> getAllAtomMapping() {
        return getComparison().getAllAtomMapping();
    }

    /** {@inheritDoc}
    (*
     * Returns all plausible mappings between query and target molecules
     * Each map in the list has atom-atom equivalence index of the mappings
     * between query and target molecule i.e. map.getKey() for the query
     * and map.getValue() for the target molecule
     * @return A List of all possible mapped MCS atom index.
     *
     */
    @Override
    @TestMethod("testGetAllMapping")
    public List<Map<Integer, Integer>> getAllMapping() {
        return getComparison().getAllMapping();
    }

    /** {@inheritDoc}
     *
     * Returns one of the best matches with atoms mapped
     * @return One of the best MCS solution(s) atoms
     *
     */
    @Override
    @TestMethod("testGetFirstAtomMapping")
    public Map<IAtom, IAtom> getFirstAtomMapping() {
        return getComparison().getFirstAtomMapping();
    }

    /** {@inheritDoc}
     *
     * Returns one of the best matches with atom indexes mapped
     * @return One of the best MCS solution(s) index
     */
    @Override
    @TestMethod("testGetFirstMapping")
    public Map<Integer, Integer> getFirstMapping() {
        return getComparison().getFirstMapping();
    }

    /** {@inheritDoc}
     *
     * Returns a number which denotes the quality of the mcs.
     * A solution with highest stereo score is preferred over other
     * scores
     * @param Key position
     * @return Stereo matching score for the mapped part
     *
     */
    @Override
    @TestMethod("testGetStereoScore")
    public Integer getStereoScore(int Key) {
        return getComparison().getStereoScore(Key);
    }

    /** {@inheritDoc}
     *
     * Returns number of fragment generated in the solution space,
     * if the MCS is removed from the target and query graph.
     * Amongst the solutions, a solution with lowest fragment size
     * is preferred
     *
     * @param Key position
     * @return Fragment count(s) generated after removing the mapped parts
     *
     */
    @Override
    @TestMethod("testGetFragmentSize")
    public Integer getFragmentSize(int Key) {
        return getComparison().getFragmentSize(Key);
    }

    /** {@inheritDoc}
     *
     * Returns summation energy score of the disorder if the MCS is removed
     * from the target and query graph. Amongst the solutions, a solution
     * with lowest energy score is preferred
     *
     * @param Key position
     * @return Total bond breaking energy required to remove the mapped part
     *
     */
    @Override
    @TestMethod("testGetEnergyScore")
    public Double getEnergyScore(int Key) {
        return getComparison().getEnergyScore(Key);
    }

    /** {@inheritDoc}
     *
     * Returns modified query molecule on which mapping was
     * performed
     * @return Query Molecule AtomContainer
     */
    @Override
    @TestMethod("testGetReactantMolecule")
    public IAtomContainer getReactantMolecule() {
        return getComparison().getReactantMolecule();
    }

    /** {@inheritDoc}
     *
     * Returns modified target molecule on which mapping was
     * performed
     * @return Target Molecule AtomContainer
     */
    @Override
    @TestMethod("testGetProductMolecule")
    public IAtomContainer getProductMolecule() {
        return getComparison().getProductMolecule();
    }

    /** {@inheritDoc}
     *
     * Returns Tanimoto similarity between query and target molecules
     * (Score is between 0-min and 1-max)
     * @return Tanimoto similarity between query and target molecules
     * @throws java.io.IOException
     */
    @Override
    @TestMethod("testGetTanimotoSimilarity")
    public double getTanimotoSimilarity() throws IOException {
        return getComparison().getTanimotoSimilarity();
    }

    /** {@inheritDoc}
     *
     *
     * @return true if no stereo mismatch occurs
     * else false if stereo mismatch occurs
     */
    @Override
    @TestMethod("testIsStereoMisMatch")
    public boolean isStereoMisMatch() {
        return getComparison().isStereoMisMatch();
    }

    /** {@inheritDoc}
     *
     * Checks if query is a subgraph of the target
     * @return true if query is a subgraph of the target
     * else false
     */
    @Override
    @TestMethod("testIsSubgraph")
    public boolean isSubgraph() {
        return getComparison().isSubgraph();

    }

    /** {@inheritDoc}
     *
     * Returns Euclidean Distance between query and target molecule
     * @return Euclidean Distance between query and target molecule
     * @throws IOException
     */
    @Override
    @TestMethod("testGetEuclideanDistance")
    public double getEuclideanDistance() throws IOException {
        return getComparison().getEuclideanDistance();
    }

    /**
     * Returns algorithm class instance in process
     * @return the comparison
     */
    @TestMethod("testGetComparison")
    public AbstractMCS getComparison() {
        return comparison;
    }

    /**
     * Sets algorithm class initiated for search process
     * @param comparison the comparison to set
     */
    @TestMethod("testSetComparison")
    public void setComparison(AbstractMCS comparison) {
        this.comparison = comparison;
    }
}
