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
package org.openscience.cdk.smsd.algorithm.rgraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.smsd.helper.FinalMappings;
import org.openscience.cdk.smsd.interfaces.AbstractSubGraph;
import org.openscience.cdk.smsd.interfaces.IMCSBase;
import org.openscience.cdk.smsd.tools.MolHandler;

/**
 * This class acts as a handler class for {@link CDKMCS} algorithm.
 *
 * @cdk.module smsd
 * @cdk.githash
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 * @deprecated This class is part of SMSD and either duplicates functionality elsewhere in the CDK or provides public
 *             access to internal implementation details. SMSD has been deprecated from the CDK with a newer, more recent
 *             version of SMSD is available at <a href="http://github.com/asad/smsd">http://github.com/asad/smsd</a>.
 */
@Deprecated
public class CDKSubGraphHandler extends AbstractSubGraph implements IMCSBase {

    //    //~--- fields -------------------------------------------------------------
    private IAtomContainer              source;
    private IAtomContainer              target;
    private boolean                     rOnPFlag     = false;
    private List<Map<IAtom, IAtom>>     allAtomMCS   = null;
    private Map<IAtom, IAtom>           firstAtomMCS = null;
    private Map<Integer, Integer>       firstMCS     = null;
    private List<Map<Integer, Integer>> allMCS       = null;

    //~--- constructors -------------------------------------------------------
    /*
     * Creates a new instance of MappingHandler
     */
    public CDKSubGraphHandler() {

        this.allAtomMCS = new ArrayList<Map<IAtom, IAtom>>();
        this.firstAtomMCS = new HashMap<IAtom, IAtom>();
        this.firstMCS = new TreeMap<Integer, Integer>();
        this.allMCS = new ArrayList<Map<Integer, Integer>>();
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
        this.source = source;
        this.target = target;
    }

    /** {@inheritDoc}
     *
     * @param shouldMatchBonds
     */
    @Override
    public boolean isSubgraph(boolean shouldMatchBonds) {

        CDKRMapHandler rmap = new CDKRMapHandler();

        try {

            if ((source.getAtomCount() == target.getAtomCount()) && source.getBondCount() == target.getBondCount()) {
                rOnPFlag = true;
                rmap.calculateIsomorphs(source, target, shouldMatchBonds);

            } else if (source.getAtomCount() > target.getAtomCount() && source.getBondCount() != target.getBondCount()) {
                rOnPFlag = true;
                rmap.calculateSubGraphs(source, target, shouldMatchBonds);

            } else {
                rOnPFlag = false;
                rmap.calculateSubGraphs(target, source, shouldMatchBonds);
            }

            setAllMapping();
            setAllAtomMapping();
            setFirstMapping();
            setFirstAtomMapping();

        } catch (CDKException e) {
            rmap = null;
            //            System.err.println("WARNING: graphContainer: most probably time out error ");
        }

        return !getFirstMapping().isEmpty();
    }

    /**
     *
     * @param mol
     * @param mcss
     * @param shouldMatchBonds
     * @return IMolecule Set
     * @throws CDKException
     */
    protected IAtomContainerSet getUncommon(IAtomContainer mol, IAtomContainer mcss, boolean shouldMatchBonds)
            throws CDKException {
        ArrayList<Integer> atomSerialsToDelete = new ArrayList<Integer>();

        List<List<CDKRMap>> matches = CDKMCS.getSubgraphAtomsMaps(mol, mcss, shouldMatchBonds);
        List<CDKRMap> mapList = matches.get(0);
        for (Object o : mapList) {
            CDKRMap rmap = (CDKRMap) o;
            atomSerialsToDelete.add(rmap.getId1());
        }

        // at this point we have the serial numbers of the bonds to delete
        // we should get the actual bonds rather than delete by serial numbers
        ArrayList<IAtom> atomsToDelete = new ArrayList<IAtom>();
        for (Integer serial : atomSerialsToDelete) {
            atomsToDelete.add(mol.getAtom(serial));
        }

        // now lets get rid of the bonds themselves
        for (IAtom atom : atomsToDelete) {
            mol.removeAtom(atom);
        }

        // now we probably have a set of disconnected components
        // so lets get a set of individual atom containers for
        // corresponding to each component
        return ConnectivityChecker.partitionIntoMolecules(mol);
    }

    //~--- get methods --------------------------------------------------------
    private synchronized void setAllMapping() {

        //int count_final_sol = 1;
        //System.out.println("Output of the final FinalMappings: ");
        try {
            List<Map<Integer, Integer>> sol = FinalMappings.getInstance().getFinalMapping();
            int counter = 0;
            for (Map<Integer, Integer> finalSolution : sol) {
                TreeMap<Integer, Integer> atomMappings = new TreeMap<Integer, Integer>();
                for (Map.Entry<Integer, Integer> solutions : finalSolution.entrySet()) {

                    int iIndex = solutions.getKey().intValue();
                    int jIndex = solutions.getValue().intValue();

                    if (rOnPFlag) {
                        atomMappings.put(iIndex, jIndex);
                    } else {
                        atomMappings.put(jIndex, iIndex);
                    }
                }
                if (!allMCS.contains(atomMappings)) {
                    allMCS.add(counter++, atomMappings);
                }
            }

        } catch (Exception ex) {
            ex.getCause();
        }

    }

    private synchronized void setAllAtomMapping() {
        List<Map<Integer, Integer>> sol = allMCS;

        int counter = 0;
        for (Map<Integer, Integer> finalSolution : sol) {
            Map<IAtom, IAtom> atomMappings = new HashMap<IAtom, IAtom>();
            for (Map.Entry<Integer, Integer> solutions : finalSolution.entrySet()) {

                int iIndex = solutions.getKey().intValue();
                int jIndex = solutions.getValue().intValue();

                IAtom sourceAtom = null;
                IAtom targetAtom = null;

                sourceAtom = source.getAtom(iIndex);
                targetAtom = target.getAtom(jIndex);
                atomMappings.put(sourceAtom, targetAtom);

            }
            allAtomMCS.add(counter++, atomMappings);
        }
    }

    private synchronized void setFirstMapping() {

        if (!allMCS.isEmpty()) {
            firstMCS = new TreeMap<Integer, Integer>(allMCS.get(0));
        }

    }

    private synchronized void setFirstAtomMapping() {
        if (!allAtomMCS.isEmpty()) {
            firstAtomMCS = new HashMap<IAtom, IAtom>(allAtomMCS.get(0));
        }

    }

    /** {@inheritDoc}
     */
    @Override
    public List<Map<Integer, Integer>> getAllMapping() {
        return Collections.unmodifiableList(allMCS);
    }

    /** {@inheritDoc}
     */
    @Override
    public Map<Integer, Integer> getFirstMapping() {
        return Collections.unmodifiableMap(firstMCS);
    }

    /** {@inheritDoc}
     */
    @Override
    public List<Map<IAtom, IAtom>> getAllAtomMapping() {
        return Collections.unmodifiableList(allAtomMCS);
    }

    /** {@inheritDoc}
     */
    @Override
    public Map<IAtom, IAtom> getFirstAtomMapping() {
        return Collections.unmodifiableMap(firstAtomMCS);
    }
}
