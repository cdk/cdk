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
 * MERCHANTABILITY or FITNESS FOR sourceAtom PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.smsd.algorithm.cdk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.smsd.helper.FinalMappings;
import org.openscience.cdk.smsd.helper.MolHandler;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.smsd.interfaces.AbstractMCSAlgorithm;
import org.openscience.cdk.smsd.interfaces.IMCSBase;

/**
 * This class acts as a handler class for CDKMCS algorithm {@link org.openscience.cdk.smsd.algorithm.cdk.CDKMCS}.
 * 
 * @cdk.module smsd
 * @cdk.githash
 * @author Syed Asad Rahman <asad@ebi.ac.uk>
 */
@TestClass("org.openscience.cdk.smsd.algorithm.cdk.CDKMCSHandlerTest")
public class CDKMCSHandler extends AbstractMCSAlgorithm implements IMCSBase {

//    //~--- fields -------------------------------------------------------------
    private IAtomContainer source;
    private IAtomContainer target;
    private boolean rOnPFlag = false;
    private List<Map<IAtom, IAtom>> allAtomMCS = null;
    private Map<IAtom, IAtom> firstAtomMCS = null;
    private Map<Integer, Integer> firstMCS = null;
    private List<Map<Integer, Integer>> allMCS = null;

    //~--- constructors -------------------------------------------------------
    /*
     * Creates a new instance of MappingHandler
     */
    public CDKMCSHandler() {

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
    @TestMethod("testSet_IAtomContainer_IAtomContainer")
    public void set(IAtomContainer source, IAtomContainer target) {

        IAtomContainer mol1 = source;
        IAtomContainer mol2 = target;

        MolHandler Reactant = new MolHandler(mol1, false);
        MolHandler Product = new MolHandler(mol2, false);

        set(Reactant, Product);

    }

    /** {@inheritDoc}
     *
     * @param source
     * @param target
     */
    @Override
    @TestMethod("testSet_IMolecule_IMolecule")
    public void set(IMolecule source, IMolecule target) throws CDKException {

        IMolecule mol1 = source;
        IMolecule mol2 = target;

        MolHandler Reactant = new MolHandler(mol1, false);
        MolHandler Product = new MolHandler(mol2, false);

        set(Reactant, Product);
    }

    /** {@inheritDoc}
     *
     * @param sourceMolFileName
     * @param targetMolFileName
     */
    @Override
    @TestMethod("testSet_String_String")
    public void set(String sourceMolFileName, String targetMolFileName) {

        String mol1 = sourceMolFileName;
        String mol2 = targetMolFileName;

        MolHandler Reactant = new MolHandler(mol1, false);
        MolHandler Product = new MolHandler(mol2, false);
        set(Reactant, Product);


    }

    /** {@inheritDoc}
     *
     * @param source
     * @param target
     */
    @Override
    @TestMethod("testSet_MolHandler_MolHandler")
    public void set(MolHandler source, MolHandler target) {
        this.source = source.getMolecule();
        this.target = target.getMolecule();
    }

    /** {@inheritDoc}
     *
     */
    @Override
    @TestMethod("testSearchMCS")
    public void searchMCS() {

        CDKRMapHandler rmap = new CDKRMapHandler();

        try {

            if ((source.getAtomCount() == target.getAtomCount()) && source.getBondCount() == target.getBondCount()) {
                rOnPFlag = true;
                rmap.calculateOverlapsAndReduceExactMatch(source, target);

            } else if (source.getAtomCount() > target.getAtomCount() && source.getBondCount() != target.getBondCount()) {
                rOnPFlag = true;
                rmap.calculateOverlapsAndReduce(source, target);

            } else {
                rOnPFlag = false;
                rmap.calculateOverlapsAndReduce(target, source);
            }

            setAllMapping();
            setAllAtomMapping();
            setFirstMapping();
            setFirstAtomMapping();

        } catch (CDKException e) {
            rmap = null;
//            System.err.println("WARNING: graphContainer: most probably time out error ");
        }
    }

    /**
     * 
     * @param mol
     * @param mcss
     * @return IMolecule Set
     * @throws CDKException 
     */
    protected IMoleculeSet getUncommon(IAtomContainer mol, IAtomContainer mcss) throws CDKException {
        ArrayList<Integer> atomSerialsToDelete = new ArrayList<Integer>();

        List<List<CDKRMap>> matches = CDKMCS.getSubgraphAtomsMaps(mol, mcss);
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
            mol.removeAtomAndConnectedElectronContainers(atom);
        }

        // now we probably have a set of disconnected components
        // so lets get a set of individual atom containers for
        // corresponding to each component
        return ConnectivityChecker.partitionIntoMolecules(mol);
    }

    //~--- get methods --------------------------------------------------------
    private final synchronized void setAllMapping() {

        //int count_final_sol = 1;
        //System.out.println("Output of the final FinalMappings: ");
        try {
            List<Map<Integer, Integer>> sol = FinalMappings.getInstance().getFinalMapping();
            int counter = 0;
            for (Map<Integer, Integer> final_solution : sol) {
                TreeMap<Integer, Integer> atomMappings = new TreeMap<Integer, Integer>();
                for (Map.Entry<Integer, Integer> Solutions : final_solution.entrySet()) {

                    int IIndex = Solutions.getKey().intValue();
                    int JIndex = Solutions.getValue().intValue();

                    if (rOnPFlag) {
                        atomMappings.put(IIndex, JIndex);
                    } else {
                        atomMappings.put(JIndex, IIndex);
                    }
                }
                if (!allMCS.contains(atomMappings)) {
                    allMCS.add(counter++, atomMappings);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private final synchronized void setAllAtomMapping() {
        List<Map<Integer, Integer>> sol = allMCS;

        int counter = 0;
        for (Map<Integer, Integer> final_solution : sol) {
            Map<IAtom, IAtom> atomMappings = new HashMap<IAtom, IAtom>();
            for (Map.Entry<Integer, Integer> Solutions : final_solution.entrySet()) {

                int IIndex = Solutions.getKey().intValue();
                int JIndex = Solutions.getValue().intValue();

                IAtom sourceAtom = null;
                IAtom targetAtom = null;

                sourceAtom = source.getAtom(IIndex);
                targetAtom = target.getAtom(JIndex);
                atomMappings.put(sourceAtom, targetAtom);

            }
            allAtomMCS.add(counter++, atomMappings);
        }
    }

    private final synchronized void setFirstMapping() {

        if (!allMCS.isEmpty()) {
            firstMCS = new TreeMap<Integer, Integer>(allMCS.get(0));
        }

    }

    private final synchronized void setFirstAtomMapping() {
        if (!allAtomMCS.isEmpty()) {
            firstAtomMCS = new HashMap<IAtom, IAtom>(allAtomMCS.get(0));
        }

    }

    /** {@inheritDoc}
     */
    @Override
    @TestMethod("testGetAllMapping")
    public List<Map<Integer, Integer>> getAllMapping() {
        return Collections.unmodifiableList(allMCS);
    }

    /** {@inheritDoc}
     */
    @Override
    @TestMethod("testGetFirstMapping")
    public Map<Integer, Integer> getFirstMapping() {
        return Collections.unmodifiableMap(firstMCS);
    }

    /** {@inheritDoc}
     */
    @Override
    @TestMethod("testGetAllAtomMapping")
    public List<Map<IAtom, IAtom>> getAllAtomMapping() {
        return Collections.unmodifiableList(allAtomMCS);
    }

    /** {@inheritDoc}
     */
    @Override
    @TestMethod("testGetFirstAtomMapping")
    public Map<IAtom, IAtom> getFirstAtomMapping() {
        return Collections.unmodifiableMap(firstAtomMCS);
    }
}
