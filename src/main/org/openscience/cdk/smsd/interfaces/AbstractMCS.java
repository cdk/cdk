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
package org.openscience.cdk.smsd.interfaces;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;

/**
 * Interface for all MCS algorithms.
 * @cdk.module smsd
 * @cdk.githash
 * @author Syed Asad Rahman <asad@ebi.ac.uk>
 */
@TestClass("org.openscience.cdk.smsd.interfaces.AbstractMCSTest")
public abstract class AbstractMCS {

    /** 
     * initialize query and target molecules.
     *
     * @param source
     * @param target
     * @param removeHydrogen true if remove H before mapping
     * @throws CDKException
     */
    public abstract void init(IMolecule source, IMolecule target, boolean removeHydrogen) throws CDKException;

    /**
     * initialize query and target molecules.
     *
     * @param source
     * @param target
     * @param removeHydrogen true if remove H before mapping
     * @throws CDKException
     */
    public abstract void init(IAtomContainer source, IAtomContainer target, boolean removeHydrogen) throws CDKException;

    /**
     * initialize query and target molecules.
     *
     * @param stereoFilter
     * @param fragmentFilter
     * @param energyFilter
     */
    public abstract void setChemFilters(boolean stereoFilter, boolean fragmentFilter, boolean energyFilter);

    /** 
     * Returns summation energy score of the disorder if the MCS is removed
     * from the target and query graph. Amongst the solutions, a solution
     * with lowest energy score is preferred.
     * 
     * @param Key Index of the mapping solution
     * @return Total bond breaking energy required to remove the mapped part
     */
    public abstract Double getEnergyScore(int Key);

    /** 
     * Returns number of fragment generated in the solution space,
     * if the MCS is removed from the target and query graph.
     * Amongst the solutions, a solution with lowest fragment size
     * is preferred.
     *
     * @param Key Index of the mapping solution
     * @return Fragment count(s) generated after removing the mapped parts
     */
    public abstract Integer getFragmentSize(int Key);

    /** 
     * 
     * Returns modified target molecule on which mapping was
     * performed.
     *
     *
     * @return return modified Product Molecule
     */
    public abstract IAtomContainer getProductMolecule();

    /** 
     * Returns modified query molecule on which mapping was
     * performed.
     *
     * @return return modified Reactant Molecule
     */
    public abstract IAtomContainer getReactantMolecule();

    /** 
     * Returns a number which denotes the quality of the mcs.
     * A solution with highest stereo score is preferred over other
     * scores.
     * @param Key Index of the mapping solution
     * @return true if no stereo mismatch occures
     * else false if stereo mismatch occures
     */
    public abstract Integer getStereoScore(int Key);

    /**
     * true if stereo mismatch occurs
     * else true if stereo mismatch occurs.
     *
     * @return true if two molecules have different stereo match
     */
    public abstract boolean isStereoMisMatch();

    /** 
     * Checks if query is a subgraph of the target.
     *
     * @return true if query molecule is a subgraph of the target molecule
     */
    public abstract boolean isSubgraph();

    /** 
     * Returns Tanimoto similarity between query and target molecules
     * (Score is between 0-min and 1-max).
     *
     * @return Tanimoto Similarity between 0 and 1
     * @throws IOException
     */
    public abstract double getTanimotoSimilarity() throws IOException;

    /** 
     * Returns Euclidean Distance between query and target molecule.
     * @return Euclidean Distance (lower the score, better the match)
     * @throws IOException
     */
    public abstract double getEuclideanDistance() throws IOException;

    /**
     * Returns all plausible mappings between query and target molecules
     * Each map in the list has atom-atom equivalence of the mappings
     * between query and target molecule i.e. map.getKey() for the query
     * and map.getValue() for the target molecule.
     * @return All possible MCS atom Mappings
     */
    public abstract List<Map<IAtom, IAtom>> getAllAtomMapping();

    /**
     * Returns all plausible mappings between query and target molecules
     * Each map in the list has atom-atom equivalence index of the mappings
     * between query and target molecule i.e. map.getKey() for the query
     * and map.getValue() for the target molecule.
     * @return All possible MCS Mapping Index
     */
    public abstract List<Map<Integer, Integer>> getAllMapping();

    /**
     * Returns one of the best matches with atoms mapped.
     * @return Best Atom Mapping
     */
    public abstract Map<IAtom, IAtom> getFirstAtomMapping();

    /**
     * Returns one of the best matches with atom indexes mapped.
     * @return Best Mapping Index
     */
    public abstract Map<Integer, Integer> getFirstMapping();

    /**
     * @return the bondSensitive TimeOut
     */
    public abstract double getBondSensitiveTimeOut();

    /**
     * @param bondSensitiveTimeOut the bond Sensitive Timeout in mins (default 0.15 min)
     */
    public abstract void setBondSensitiveTimeOut(double bondSensitiveTimeOut);

    /**
     * @return the bondInSensitive TimeOut
     */
    public abstract double getBondInSensitiveTimeOut();

    /**
     * @param bondInSensitiveTimeOut the bond insensitive Timeout in mins (default 0.15 min)
     */
    public abstract void setBondInSensitiveTimeOut(double bondInSensitiveTimeOut);
}
