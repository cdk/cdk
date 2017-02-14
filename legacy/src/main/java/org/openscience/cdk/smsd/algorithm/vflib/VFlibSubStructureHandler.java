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
 * You should have received a copy of the GNU Lesser General Public License
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
import java.util.logging.Level;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.IMapper;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.INode;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.IQuery;
import org.openscience.cdk.smsd.algorithm.vflib.map.VFMapper;
import org.openscience.cdk.smsd.algorithm.vflib.query.QueryCompiler;
import org.openscience.cdk.smsd.interfaces.AbstractSubGraph;
import org.openscience.cdk.smsd.interfaces.IMCSBase;
import org.openscience.cdk.smsd.tools.MolHandler;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * This is an ultra fast method to report if query
 * is a substructure for target molecule. If this case is true
 * then it returns only all mapping.
 *
 * This is much faster than {@link
 * org.openscience.cdk.smsd.algorithm.vflib.VFlibMCSHandler} class
 * as it only reports first match and backtracks.
 *
 * This class should only be used to report if a query
 * graph is a substructure of the target graph.
 *
 * @cdk.module smsd
 * @cdk.githash
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 * @deprecated SMSD has been deprecated from the CDK with a newer, more recent
 *             version of SMSD is available at <a href="http://github.com/asad/smsd">http://github.com/asad/smsd</a>.
 */
@Deprecated
public class VFlibSubStructureHandler extends AbstractSubGraph implements IMCSBase {

    private static       List<Map<IAtom, IAtom>>     allAtomMCS     = null;
    private static       Map<IAtom, IAtom>           atomsMCS       = null;
    private static       List<Map<IAtom, IAtom>>     allAtomMCSCopy = null;
    private static       Map<Integer, Integer>       firstMCS       = null;
    private static       List<Map<Integer, Integer>> allMCS         = null;
    private static       List<Map<Integer, Integer>> allMCSCopy     = null;
    private              IQueryAtomContainer         queryMol       = null;
    private              IAtomContainer              mol1           = null;
    private              IAtomContainer              mol2           = null;
    private              List<Map<INode, IAtom>>     vfLibSolutions = null;
    private              int                         vfMCSSize      = -1;
    private              boolean                     bondMatchFlag  = false;
    private final static ILoggingTool                LOGGER         = LoggingToolFactory
            .createLoggingTool(VFlibSubStructureHandler.class);

    /**
     * Constructor for an extended VF Algorithm for the MCS search
     */
    public VFlibSubStructureHandler() {
        allAtomMCS = new ArrayList<Map<IAtom, IAtom>>();
        allAtomMCSCopy = new ArrayList<Map<IAtom, IAtom>>();
        atomsMCS = new HashMap<IAtom, IAtom>();
        firstMCS = new TreeMap<Integer, Integer>();
        allMCS = new ArrayList<Map<Integer, Integer>>();
        allMCSCopy = new ArrayList<Map<Integer, Integer>>();
    }

    private void setFirstMappings() {
        if (!allAtomMCS.isEmpty()) {
            atomsMCS.putAll(allAtomMCS.get(0));
            firstMCS.putAll(allMCS.get(0));
        }
    }

    private boolean mcgregorFlag() {
        int commonAtomCount = checkCommonAtomCount(getReactantMol(), getProductMol());
        if (commonAtomCount > vfMCSSize && commonAtomCount > vfMCSSize) {
            return true;
        }
        return false;
    }

    /** {@inheritDoc}
     *
     * Set the VFLib MCS software
     *
     * @param reactant
     * @param product
     */
    @Override
    public void set(MolHandler reactant, MolHandler product) {
        mol1 = reactant.getMolecule();
        mol2 = product.getMolecule();
    }

    /** {@inheritDoc}
     *
     * @param source
     * @param target
     */
    @Override
    public void set(IQueryAtomContainer source, IAtomContainer target) {
        queryMol = source;
        mol2 = target;
    }

    private boolean hasMap(Map<Integer, Integer> map, List<Map<Integer, Integer>> mapGlobal) {
        for (Map<Integer, Integer> test : mapGlobal) {
            if (test.equals(map)) {
                return true;
            }
        }
        return false;
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
        return Collections.unmodifiableMap(atomsMCS);
    }

    /** {@inheritDoc} */
    @Override
    public Map<Integer, Integer> getFirstMapping() {
        return Collections.unmodifiableMap(firstMCS);
    }

    private int checkCommonAtomCount(IAtomContainer reactantMolecule, IAtomContainer productMolecule) {
        ArrayList<String> atoms = new ArrayList<String>();
        for (int i = 0; i < reactantMolecule.getAtomCount(); i++) {
            atoms.add(reactantMolecule.getAtom(i).getSymbol());
        }
        int common = 0;
        for (int i = 0; i < productMolecule.getAtomCount(); i++) {
            String symbol = productMolecule.getAtom(i).getSymbol();
            if (atoms.contains(symbol)) {
                atoms.remove(symbol);
                common++;
            }
        }
        return common;
    }

    private void searchVFMappings() {
        //        System.out.println("searchVFMappings ");
        IQuery query = null;
        IMapper mapper = null;
        vfLibSolutions = new ArrayList<Map<INode, IAtom>>();
        if (queryMol != null) {
            query = new QueryCompiler(queryMol).compile();
            mapper = new VFMapper(query);
            if (mapper.hasMap(getProductMol())) {
                List<Map<INode, IAtom>> maps = mapper.getMaps(getProductMol());
                if (maps != null) {
                    vfLibSolutions.addAll(maps);
                }
            }
            setVFMappings(true, query);
        } else if (getReactantMol().getAtomCount() <= getProductMol().getAtomCount()) {
            query = new QueryCompiler(mol1, isBondMatchFlag()).compile();
            mapper = new VFMapper(query);
            if (mapper.hasMap(getProductMol())) {
                List<Map<INode, IAtom>> maps = mapper.getMaps(getProductMol());
                if (maps != null) {
                    vfLibSolutions.addAll(maps);
                }
            }
            setVFMappings(true, query);
        } else {
            query = new QueryCompiler(getProductMol(), isBondMatchFlag()).compile();
            mapper = new VFMapper(query);
            if (mapper.hasMap(getReactantMol())) {
                List<Map<INode, IAtom>> maps = mapper.getMaps(getReactantMol());
                if (maps != null) {
                    vfLibSolutions.addAll(maps);
                }
            }
            setVFMappings(false, query);
        }
    }

    private void setVFMappings(boolean ronp, IQuery query) {
        int counter = 0;
        for (Map<INode, IAtom> solution : vfLibSolutions) {
            Map<IAtom, IAtom> atomatomMapping = new HashMap<IAtom, IAtom>();
            Map<Integer, Integer> indexindexMapping = new TreeMap<Integer, Integer>();
            if (solution.size() > vfMCSSize) {
                this.vfMCSSize = solution.size();
                allAtomMCSCopy.clear();
                allMCSCopy.clear();
                counter = 0;
            }
            for (Map.Entry<INode, IAtom> mapping : solution.entrySet()) {
                IAtom qAtom = null;
                IAtom tAtom = null;
                if (ronp) {
                    qAtom = query.getAtom(mapping.getKey());
                    tAtom = mapping.getValue();
                } else {
                    tAtom = query.getAtom(mapping.getKey());
                    qAtom = mapping.getValue();
                }
                Integer qIndex = Integer.valueOf(getReactantMol().getAtomNumber(qAtom));
                Integer tIndex = Integer.valueOf(getProductMol().getAtomNumber(tAtom));
                if (qIndex != -1 && tIndex != -1) {
                    atomatomMapping.put(qAtom, tAtom);
                    indexindexMapping.put(qIndex, tIndex);
                } else {
                    try {
                        throw new CDKException("Atom index pointing to NULL");
                    } catch (CDKException ex) {
                        LOGGER.error(Level.SEVERE, null, ex);
                    }
                }
            }
            //            System.out.println("indexindexMapping " + indexindexMapping.size());
            //            System.out.println("MCS Size " + vfMCSSize);
            if (!atomatomMapping.isEmpty() && !hasMap(indexindexMapping, allMCSCopy)
                    && indexindexMapping.size() == vfMCSSize) {
                allAtomMCSCopy.add(counter, atomatomMapping);
                allMCSCopy.add(counter, indexindexMapping);
                counter++;
            }
        }
        //        System.out.println("allMCSCopy " + allMCSCopy.size());
    }

    @Override
    public boolean isSubgraph(boolean shouldMatchBonds) {
        setBondMatchFlag(shouldMatchBonds);
        searchVFMappings();
        //        boolean flag = mcgregorFlag();
        //        if (flag && !vfLibSolutions.isEmpty()) {
        //            try {
        //                searchMcGregorMapping();
        //            } catch (CDKException ex) {
        //                LOGGER.error(Level.SEVERE, null, ex);
        //            } catch (IOException ex) {
        //                LOGGER.error(Level.SEVERE, null, ex);
        //            }
        //
        //        } else

        if (!allAtomMCSCopy.isEmpty()) {
            allAtomMCS.addAll(allAtomMCSCopy);
            allMCS.addAll(allMCSCopy);
        }
        setFirstMappings();
        return (!allMCS.isEmpty() && allMCS.iterator().next().size() == getReactantMol().getAtomCount()) ? true : false;
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

    private IAtomContainer getReactantMol() {
        return queryMol == null ? mol1 : queryMol;
    }

    private IAtomContainer getProductMol() {
        return mol2;
    }
}
