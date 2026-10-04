/* Copyright (C) 2009-2010  Syed Asad Rahman <asad@ebi.ac.uk>
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
 * You should have received commonAtomList copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.smsd.algorithm.vflib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.IQueryBond;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.INode;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.IQuery;
import org.openscience.cdk.smsd.algorithm.vflib.map.VFMCSMapper;
import org.openscience.cdk.smsd.algorithm.vflib.query.QueryCompiler;
import org.openscience.cdk.smsd.interfaces.AbstractMCSAlgorithm;
import org.openscience.cdk.smsd.interfaces.IMCSBase;
import org.openscience.cdk.smsd.tools.MolHandler;

/**
 * This class should be used to find MCS between query
 * graph and target graph.
 * <p>
 * The search is done by {@link VFMCSMapper}. It finds the connected common
 * substructures with the most atoms and, among those, the most bonds. At
 * most {@link VFMCSMapper#MAX_MAPPINGS} mappings are kept. If the search
 * times out, the best mappings found so far are returned and
 * {@link org.openscience.cdk.smsd.global.TimeOut#isTimeOutFlag()} is true.
 * <p>
 * The search is exact, so it can take a long time when many mappings are
 * nearly as good as the best one, e.g. for some large polycyclic molecules
 * (such as coronene against C60).
 * {@link org.openscience.cdk.smsd.Isomorphism} sets a time limit; when this
 * class is used directly, set one with
 * {@code TimeOut.getInstance().setTimeOut(minutes)}.
 *
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 * @deprecated SMSD has been deprecated from the CDK with a newer, more recent
 *             version of SMSD is available at <a href="http://github.com/asad/smsd">http://github.com/asad/smsd</a>.
 */
@Deprecated
public class VFlibMCSHandler extends AbstractMCSAlgorithm implements IMCSBase {

    private final List<Map<IAtom, IAtom>>     allAtomMCS    = new ArrayList<>();
    private final List<Map<Integer, Integer>> allMCS        = new ArrayList<>();
    private IAtomContainer                    source        = null;
    private IAtomContainer                    target        = null;
    private boolean                           bondMatchFlag = false;

    /**
     * Creates a handler for the VF MCS search.
     */
    public VFlibMCSHandler() {
    }

    /**
     * {@inheritDoc}
     *
     * @param bondTypeMatch true to match bonds by order and aromaticity
     */
    @Override
    public void searchMCS(boolean bondTypeMatch) {
        setBondMatchFlag(bondTypeMatch);
        allAtomMCS.clear();
        allMCS.clear();

        // search with the smaller molecule as the query, but keep the given
        // order if either molecule has query atoms or bonds
        boolean swap = source.getAtomCount() > target.getAtomCount()
                && !hasQueryFeatures(source) && !hasQueryFeatures(target);
        IAtomContainer queryMol = swap ? target : source;
        IAtomContainer targetMol = swap ? source : target;

        IQuery query = new QueryCompiler(queryMol, bondTypeMatch).compile();
        for (Map<INode, IAtom> solution : new VFMCSMapper(query).getMaps(targetMol)) {
            if (solution.isEmpty()) {
                continue;
            }
            Map<IAtom, IAtom> atomMapping = new HashMap<>();
            Map<Integer, Integer> indexMapping = new TreeMap<>();
            for (Map.Entry<INode, IAtom> entry : solution.entrySet()) {
                IAtom sourceAtom = swap ? entry.getValue() : query.getAtom(entry.getKey());
                IAtom targetAtom = swap ? query.getAtom(entry.getKey()) : entry.getValue();
                atomMapping.put(sourceAtom, targetAtom);
                indexMapping.put(source.indexOf(sourceAtom), target.indexOf(targetAtom));
            }
            allAtomMCS.add(atomMapping);
            allMCS.add(indexMapping);
        }
    }

    private static boolean hasQueryFeatures(IAtomContainer mol) {
        if (mol instanceof IQueryAtomContainer) {
            return true;
        }
        for (IAtom atom : mol.atoms()) {
            if (atom instanceof IQueryAtom) {
                return true;
            }
        }
        for (IBond bond : mol.bonds()) {
            if (bond instanceof IQueryBond) {
                return true;
            }
        }
        return false;
    }

    /** {@inheritDoc}
     *
     * @param source source molecule
     * @param target target molecule
     */
    @Override
    public void set(MolHandler source, MolHandler target) {
        this.source = source.getMolecule();
        this.target = target.getMolecule();
    }

    /** {@inheritDoc}
     *
     * @param source source molecule
     * @param target target molecule
     */
    @Override
    public void set(IQueryAtomContainer source, IAtomContainer target) {
        this.source = source;
        this.target = target;
    }

    /** {@inheritDoc} */
    @Override
    public List<Map<IAtom, IAtom>> getAllAtomMapping() {
        return Collections.unmodifiableList(allAtomMCS);
    }

    /** {@inheritDoc} */
    @Override
    public List<Map<Integer, Integer>> getAllMapping() {
        return Collections.unmodifiableList(allMCS);
    }

    /** {@inheritDoc} */
    @Override
    public Map<IAtom, IAtom> getFirstAtomMapping() {
        return allAtomMCS.isEmpty() ? Collections.<IAtom, IAtom>emptyMap()
                : Collections.unmodifiableMap(allAtomMCS.get(0));
    }

    /** {@inheritDoc} */
    @Override
    public Map<Integer, Integer> getFirstMapping() {
        return allMCS.isEmpty() ? Collections.<Integer, Integer>emptyMap()
                : Collections.unmodifiableMap(allMCS.get(0));
    }

    /**
     * @return the shouldMatchBonds
     */
    public boolean isBondMatchFlag() {
        return bondMatchFlag;
    }

    /**
     * @param shouldMatchBonds the shouldMatchBonds to set
     */
    public void setBondMatchFlag(boolean shouldMatchBonds) {
        this.bondMatchFlag = shouldMatchBonds;
    }
}
