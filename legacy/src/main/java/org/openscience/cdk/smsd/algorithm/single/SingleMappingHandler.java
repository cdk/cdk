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
package org.openscience.cdk.smsd.algorithm.single;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.smsd.interfaces.AbstractMCSAlgorithm;
import org.openscience.cdk.smsd.interfaces.IMCSBase;
import org.openscience.cdk.smsd.tools.MolHandler;

/**
 * This is a handler class for single atom mapping
 * ({@link org.openscience.cdk.smsd.algorithm.single.SingleMapping}).
 * @cdk.module smsd
 * @cdk.githash
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 * @deprecated This class is part of SMSD and either duplicates functionality elsewhere in the CDK or provides public
 *             access to internal implementation details. SMSD has been deprecated from the CDK with a newer, more recent
 *             version of SMSD is available at <a href="http://github.com/asad/smsd">http://github.com/asad/smsd</a>.
 */
@Deprecated
public class SingleMappingHandler extends AbstractMCSAlgorithm implements IMCSBase {

    private static List<Map<IAtom, IAtom>>     allAtomMCS     = null;
    private static Map<IAtom, IAtom>           atomsMCS       = null;
    private static Map<Integer, Integer>       firstMCS       = null;
    private static List<Map<Integer, Integer>> allMCS         = null;
    private IAtomContainer                     source         = null;
    private IQueryAtomContainer                smartSource    = null;
    private IAtomContainer                     target         = null;
    private boolean                            removeHydrogen;

    /**
     *
     * @param removeH true
     */
    public SingleMappingHandler(boolean removeH) {

        this.removeHydrogen = removeH;
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
    public void set(MolHandler source, MolHandler target) {
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
        this.smartSource = source;
        this.source = source;
        this.target = target;
    }

    //Function is called by the main program and serves as a starting point for the comparision procedure.

    /** {@inheritDoc}
     *
     * @param bondTypeMatch
     */
    @Override
    public void searchMCS(boolean bondTypeMatch) {
        SingleMapping singleMapping = new SingleMapping();
        List<Map<IAtom, IAtom>> mappings = null;
        try {
            if (this.smartSource == null) {
                mappings = singleMapping.getOverLaps(source, target, removeHydrogen);
            } else {
                mappings = singleMapping.getOverLaps(smartSource, target, removeHydrogen);
            }
        } catch (CDKException ex) {
            Logger.getLogger(SingleMappingHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        setAllAtomMapping(mappings);
        setAllMapping(mappings);
        setFirstMapping();
        setFirstAtomMapping();
        //setStereoScore();
    }

    /** {@inheritDoc}
     *
     * Set the mappings
     */
    private synchronized void setAllMapping(List<Map<IAtom, IAtom>> mappings) {
        try {
            int counter = 0;
            for (Map<IAtom, IAtom> solution : mappings) {
                Map<Integer, Integer> atomMappings = new TreeMap<>();
                for (Map.Entry<IAtom, IAtom> map : solution.entrySet()) {
                    IAtom sourceAtom = map.getKey();
                    IAtom targetAtom = map.getValue();
                    atomMappings.put(source.indexOf(sourceAtom), target.indexOf(targetAtom));
                }
                allMCS.add(counter++, atomMappings);
            }
        } catch (Exception I) {
            I.getCause();
        }
    }

    private synchronized void setAllAtomMapping(List<Map<IAtom, IAtom>> mappings) {

        try {
            int counter = 0;
            for (Map<IAtom, IAtom> solution : mappings) {
                Map<IAtom, IAtom> atomMappings = new HashMap<>();
                for (Map.Entry<IAtom, IAtom> map : solution.entrySet()) {

                    IAtom sourceAtom = map.getKey();
                    IAtom targetAtom = map.getValue();
                    atomMappings.put(sourceAtom, targetAtom);
                }
                allAtomMCS.add(counter++, atomMappings);
            }
        } catch (Exception I) {
            I.getCause();
        }
    }

    private synchronized void setFirstMapping() {
        if (allMCS.size() > 0) {
            firstMCS = new TreeMap<>(allMCS.iterator().next());
        }

    }

    private synchronized void setFirstAtomMapping() {
        if (allAtomMCS.size() > 0) {
            atomsMCS = new HashMap<>(allAtomMCS.iterator().next());
        }
    }

    /** {@inheritDoc} */
    @Override
    public synchronized List<Map<Integer, Integer>> getAllMapping() {
        return Collections.unmodifiableList(allMCS);
    }

    /** {@inheritDoc} */
    @Override
    public Map<Integer, Integer> getFirstMapping() {
        return Collections.unmodifiableMap(firstMCS);
    }

    /** {@inheritDoc} */
    @Override
    public List<Map<IAtom, IAtom>> getAllAtomMapping() {
        return Collections.unmodifiableList(allAtomMCS);
    }

    /** {@inheritDoc} */
    @Override
    public Map<IAtom, IAtom> getFirstAtomMapping() {
        return Collections.unmodifiableMap(atomsMCS);
    }
}
