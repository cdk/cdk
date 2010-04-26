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
package org.openscience.cdk.smsd.algorithm.mcsplus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.smsd.filters.PostFilter;
import org.openscience.cdk.smsd.helper.FinalMappings;
import org.openscience.cdk.smsd.helper.MolHandler;
import org.openscience.cdk.smsd.interfaces.AbstractMCSAlgorithm;
import org.openscience.cdk.smsd.interfaces.IMCSBase;

/**
 * This class acts as a handler class for MCSPlus algorithm
 * {@link org.openscience.cdk.smsd.algorithm.mcsplus.MCSPlus}
 * @cdk.module smsd
 * @cdk.githash
 * @author Syed Asad Rahman <asad@ebi.ac.uk>
 */
@TestClass("org.openscience.cdk.smsd.SMSDBondSensitiveTest")
public class MCSPlusHandler extends AbstractMCSAlgorithm implements IMCSBase {

    private static List<Map<IAtom, IAtom>> allAtomMCS = null;
    private static Map<IAtom, IAtom> atomsMCS = null;
    private static Map<Integer, Integer> firstMCS = null;
    private static List<Map<Integer, Integer>> allMCS = null;
    private IAtomContainer source = null;
    private IAtomContainer target = null;
    private boolean flagExchange = false;

    /**
     * Constructor for the MCS Plus algorithm class
     */
    public MCSPlusHandler() {
        allAtomMCS = new ArrayList<Map<IAtom, IAtom>>();
        atomsMCS = new HashMap<IAtom, IAtom>();
        firstMCS = new TreeMap<Integer, Integer>();
        allMCS = new ArrayList<Map<Integer, Integer>>();
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
     * Function is called by the main program and serves as a starting point for the comparision procedure.
     *
     */
    @Override
    @TestMethod("testSearchMCS")
    public void searchMCS() {
        List<List<Integer>> mappings = null;
        try {
            if (source.getAtomCount() > target.getAtomCount()) {
                mappings = new MCSPlus().getOverlaps(source, target);
            } else {
                flagExchange = true;
                mappings = new MCSPlus().getOverlaps(target, source);

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

    private final void setAllMapping() {
        try {

            List<Map<Integer, Integer>> final_solution = FinalMappings.getInstance().getFinalMapping();
            int counter = 0;
            for (Map<Integer, Integer> solution : final_solution) {
//                System.out.println("Number of MCS solution: " + solution);
                TreeMap<Integer, Integer> validSolution = new TreeMap<Integer, Integer>();

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
            ex.printStackTrace();
        }

    }

    private final synchronized void setAllAtomMapping() {

        try {
            List<Map<Integer, Integer>> final_solution = FinalMappings.getInstance().getFinalMapping();

            int counter = 0;
            for (Map<Integer, Integer> solution : final_solution) {


                Map<IAtom, IAtom> atomMappings = new HashMap<IAtom, IAtom>();

                for (Map.Entry<Integer, Integer> map : solution.entrySet()) {

                    int IIndex = map.getKey();
                    int JIndex = map.getValue();


                    IAtom sourceAtom = null;
                    IAtom targetAtom = null;

                    if (!flagExchange) {
                        sourceAtom = source.getAtom(IIndex);
                        targetAtom = target.getAtom(JIndex);
                    } else {
                        sourceAtom = source.getAtom(JIndex);
                        targetAtom = target.getAtom(IIndex);
                    }

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
            firstMCS = new TreeMap<Integer, Integer>(allMCS.get(0));
        }
    }

    private synchronized void setFirstAtomMapping() {
        if (!allAtomMCS.isEmpty()) {
            atomsMCS = new HashMap<IAtom, IAtom>(allAtomMCS.get(0));
        }

    }

    /** {@inheritDoc}
     */
    @Override
    @TestMethod("testSearchMCS")
    public List<Map<Integer, Integer>> getAllMapping() {
        return Collections.unmodifiableList(allMCS);
    }

    /** {@inheritDoc}
     */
    @Override
    @TestMethod("testSearchMCS")
    public Map<Integer, Integer> getFirstMapping() {
        return Collections.unmodifiableMap(firstMCS);
    }

    /** {@inheritDoc}
     */
    @Override
    @TestMethod("testSearchMCS")
    public List<Map<IAtom, IAtom>> getAllAtomMapping() {
        return Collections.unmodifiableList(allAtomMCS);
    }

    /** {@inheritDoc}
     */
    @Override
    @TestMethod("testSearchMCS")
    public Map<IAtom, IAtom> getFirstAtomMapping() {
        return Collections.unmodifiableMap(atomsMCS);
    }
}
