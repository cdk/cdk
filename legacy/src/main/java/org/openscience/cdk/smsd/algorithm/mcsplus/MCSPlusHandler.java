/* Copyright (C) 2006-2010  Syed Asad Rahman <asad@ebi.ac.uk>
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
 * MERCHANTABILITY or FITNESS FOR sourceAtom PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.smsd.algorithm.mcsplus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.smsd.filters.PostFilter;
import org.openscience.cdk.smsd.helper.FinalMappings;
import org.openscience.cdk.smsd.interfaces.AbstractMCSAlgorithm;
import org.openscience.cdk.smsd.interfaces.IMCSBase;
import org.openscience.cdk.smsd.tools.MolHandler;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * This class acts as a handler class for MCSPlus algorithm.
 * {@link org.openscience.cdk.smsd.algorithm.mcsplus.MCSPlus}
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 * @deprecated SMSD has been deprecated from the CDK with a newer, more recent
 *             version of SMSD is available at <a href="http://github.com/asad/smsd">http://github.com/asad/smsd</a>.
 */
@Deprecated
public class MCSPlusHandler extends AbstractMCSAlgorithm implements IMCSBase {

    private static List<Map<IAtom, IAtom>>     allAtomMCS   = null;
    private static Map<IAtom, IAtom>           atomsMCS     = null;
    private static Map<Integer, Integer>       firstMCS     = null;
    private static List<Map<Integer, Integer>> allMCS       = null;
    private IAtomContainer                     source       = null;
    private IAtomContainer                     target       = null;
    private boolean                            flagExchange = false;

    /**
     * Constructor for the MCS Plus algorithm class
     */
    public MCSPlusHandler() {
        allAtomMCS = new ArrayList<>();
        atomsMCS = new HashMap<>();
        firstMCS = new TreeMap<>();
        allMCS = new ArrayList<>();
    }

    /** {@inheritDoc}
     *
     * @param source
     * @param target
     */
    @Override
    public synchronized void set(MolHandler source, MolHandler target) {
        this.source = source.getMolecule();
        this.target = target.getMolecule();
    }

    /** {@inheritDoc}
     *
     * @param source
     * @param target
     */
    @Override
    public void set(IQueryAtomContainer source, IAtomContainer target) {
        this.source = source;
        this.target = target;
    }

    /** {@inheritDoc}
     * Function is called by the main program and serves as a starting point for the comparison procedure.
     *
     * @param shouldMatchBonds
     */
    @Override
    public synchronized void searchMCS(boolean shouldMatchBonds) {
        List<List<Integer>> mappings;
        try {
            if (source.getAtomCount() >= target.getAtomCount()) {
                mappings = new MCSPlus().getOverlaps(source, target, shouldMatchBonds);
            } else {
                flagExchange = true;
                mappings = new MCSPlus().getOverlaps(target, source, shouldMatchBonds);
            }
            PostFilter.filter(mappings);
            setAllMapping();
            setAllAtomMapping();
            setFirstMapping();
            setFirstAtomMapping();
        } catch (CDKException e) {
            mappings = null;
        }
    }

    private synchronized void setAllMapping() {
        try {

            List<Map<Integer, Integer>> finalSolution = FinalMappings.getInstance().getFinalMapping();
            int counter = 0;
            for (Map<Integer, Integer> solution : finalSolution) {
                //                System.out.println("Number of MCS solution: " + solution);
                Map<Integer, Integer> validSolution = new TreeMap<>();
                if (!flagExchange) {
                    for (Map.Entry<Integer, Integer> map : solution.entrySet()) {
                        validSolution.put(map.getKey(), map.getValue());
                    }
                } else {
                    for (Map.Entry<Integer, Integer> map : solution.entrySet()) {
                        validSolution.put(map.getValue(), map.getKey());
                    }
                }
                allMCS.add(counter++, validSolution);
            }

        } catch (Exception ex) {
            LoggingToolFactory.createLoggingTool(MCSPlusHandler.class)
                              .warn("Unexpected Error:", ex);
        }

    }

    private synchronized void setAllAtomMapping() {
        try {

            int counter = 0;
            for (Map<Integer, Integer> solution : allMCS) {
                Map<IAtom, IAtom> atomMappings = new HashMap<>();
                for (Map.Entry<Integer, Integer> map : solution.entrySet()) {

                    int iIndex = map.getKey();
                    int jIndex = map.getValue();

                    IAtom sourceAtom;
                    IAtom targetAtom;

                    sourceAtom = source.getAtom(iIndex);
                    targetAtom = target.getAtom(jIndex);
                    atomMappings.put(sourceAtom, targetAtom);
                }
                allAtomMCS.add(counter++, atomMappings);
            }
        } catch (Exception I) {
            I.getCause();
        }

    }

    private synchronized void setFirstMapping() {
        if (!allMCS.isEmpty()) {
            firstMCS = new TreeMap<>(allMCS.iterator().next());
        }
    }

    private synchronized void setFirstAtomMapping() {
        if (!allAtomMCS.isEmpty()) {
            atomsMCS = new HashMap<>(allAtomMCS.iterator().next());
        }
    }

    /** {@inheritDoc}
     */
    @Override
    public synchronized List<Map<Integer, Integer>> getAllMapping() {
        return allMCS;
    }

    /** {@inheritDoc}
     */
    @Override
    public synchronized Map<Integer, Integer> getFirstMapping() {
        return firstMCS;
    }

    /** {@inheritDoc}
     */
    @Override
    public synchronized List<Map<IAtom, IAtom>> getAllAtomMapping() {
        return allAtomMCS;
    }

    /** {@inheritDoc}
     */
    @Override
    public synchronized Map<IAtom, IAtom> getFirstAtomMapping() {
        return atomsMCS;
    }
}
