/*
 *
 * Copyright (C) 2006-2010  Syed Asad Rahman {asad@ebi.ebi.ac.uk}
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
 * You should have received iIndex copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.smsd.algorithm.mcsplus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryBond;
import org.openscience.cdk.smsd.global.BondType;
import org.openscience.cdk.smsd.helper.LabelContainer;

/**
 * This class generates compatibility graph between query and target molecule.
 * It also markes edges in the compatibility graph as c-edges or d-edges.
 * @cdk.module smsd
 * @cdk.githash
 * @author Syed Asad Rahman <asad@ebi.ac.uk>
 */
@TestClass("org.openscience.cdk.smsd.SMSDBondSensitiveTest")
public final class GenerateCompatibilityGraph {

    /**
     * @return the bondTypeFlag
     */
    protected static boolean isBondTypeFlag() {
        return BondType.getInstance().isBondSensitive();
    }
    private List<Integer> compGraphNodes = null;
    private List<Integer> compGraphNodesCZero = null;
    private List<Integer> cEdges = null;
    private List<Integer> dEdges = null;
    private int cEdgesSize = 0;
    private int dEdgesSize = 0;
    private IAtomContainer source = null;
    private IAtomContainer target = null;

    /**
     * Generates a compatibility graph between two molecules
     * @param source
     * @param target
     * @throws java.io.IOException
     */
    protected GenerateCompatibilityGraph(IAtomContainer source, IAtomContainer target) throws IOException {
        this.source = source;
        this.target = target;
        compGraphNodes = new ArrayList<Integer>();
        compGraphNodesCZero = new ArrayList<Integer>();
        cEdges = new ArrayList<Integer>();
        dEdges = new ArrayList<Integer>();
        compatibilityGraphNodes();
        compatibilityGraph();


        if (getCEdgesSize() == 0) {
            clearCompGraphNodes();

            clearCEgdes();
            clearDEgdes();

            resetCEdgesSize();
            resetDEdgesSize();

            compatibilityGraphNodesIfCEdgeIsZero();
            compatibilityGraphCEdgeZero();
            clearCompGraphNodesCZero();
        }

    }

    private List<List<Integer>> labelAtoms(IAtomContainer atomCont) {
        List<List<Integer>> label_list = new ArrayList<List<Integer>>();

        for (int i = 0; i < atomCont.getAtomCount(); i++) {
            LabelContainer labelContainer = LabelContainer.getInstance();
            ArrayList<Integer> label = new ArrayList<Integer>(7);
//            label.setSize(7);

            for (int a = 0; a < 7; a++) {
                label.add(a, 0);
            }

            IAtom refAtom = atomCont.getAtom(i);
            String atom1_type = refAtom.getSymbol();

            label.set(0, labelContainer.getLabelID(atom1_type));

            int count_neighbors = 1;
            List<IAtom> connAtoms = atomCont.getConnectedAtomsList(refAtom);

            for (IAtom negAtom : connAtoms) {
                String atom2_type = negAtom.getSymbol();
                label.set(count_neighbors++, labelContainer.getLabelID(atom2_type));
            }

            bubbleSort(label);
            label_list.add(label);

        }
        return label_list;
    }

    private void bubbleSort(List<Integer> label) {

        boolean flag = true; // set flag to 1 to begin initial pass

        int temp; // holding variable

        for (int i = 0; i < 7 && flag; i++) {
            flag = false;
            for (int j = 0; j < 6; j++) {
                if (label.get(i) > label.get(j + 1)) {
                    // descending order simply changes to >
                    temp = label.get(i); // swap elements

                    label.set(i, label.get(j + 1));
                    label.set(j + 1, temp);
                    flag = true; // indicates that iIndex swap occurred.

                }
            }
        }

    }

    private List<IAtom> reduceAtomSet(IAtomContainer atomCont) {

        List<IAtom> basic_atoms = new ArrayList<IAtom>();
        for (IAtom atom : atomCont.atoms()) {
            basic_atoms.add(atom);
        }
        return basic_atoms;
    }

    /**
     * Generate Compatibility Graph Nodes
     *
     * @return
     * @throws IOException
     */
    protected int compatibilityGraphNodes() throws IOException {

        compGraphNodes.clear();
        List<IAtom> basic_atom_vec_A = null;
        List<IAtom> basic_atom_vec_B = null;
        IAtomContainer reactant = source;
        IAtomContainer product = target;

        basic_atom_vec_A = reduceAtomSet(reactant);
        basic_atom_vec_B = reduceAtomSet(product);

        List<List<Integer>> label_list_molA = labelAtoms(reactant);
        List<List<Integer>> label_list_molB = labelAtoms(product);



        int molA_nodes = 0;
        int count_nodes = 1;

        for (List<Integer> labelA : label_list_molA) {

            int molB_nodes = 0;

            for (List<Integer> labelB : label_list_molB) {
                if (labelA.equals(labelB)) {
                    compGraphNodes.add(reactant.getAtomNumber(basic_atom_vec_A.get(molA_nodes)));
                    compGraphNodes.add(product.getAtomNumber(basic_atom_vec_B.get(molB_nodes)));
                    compGraphNodes.add(count_nodes++);
                }
                molB_nodes++;
            }
            molA_nodes++;
        }
        return 0;
    }

    /**
     * Generate Compatibility Graph Nodes Bond Insensitive
     *
     * @return
     * @throws IOException
     */
    protected int compatibilityGraph() throws IOException {
        int comp_graph_nodes_List_size = compGraphNodes.size();

        cEdges = new ArrayList<Integer>(); //Initialize the cEdges List
        dEdges = new ArrayList<Integer>(); //Initialize the dEdges List

        for (int a = 0; a < comp_graph_nodes_List_size; a += 3) {
            int index_a = compGraphNodes.get(a);
            int index_aPlus1 = compGraphNodes.get(a + 1);

            for (int b = a + 3; b < comp_graph_nodes_List_size; b += 3) {
                int index_b = compGraphNodes.get(b);
                int index_bPlus1 = compGraphNodes.get(b + 1);

                // if element atomCont !=jIndex and atoms on the adjacent sides of the bonds are not equal
                if (a != b && index_a != index_b && index_aPlus1 != index_bPlus1) {

                    IBond ReactantBond = null;
                    IBond ProductBond = null;

                    ReactantBond = source.getBond(source.getAtom(index_a), source.getAtom(index_b));
                    ProductBond = target.getBond(target.getAtom(index_aPlus1), target.getAtom(index_bPlus1));
                    if (ReactantBond != null && ProductBond != null) {
                        addEdges(ReactantBond, ProductBond, a, b);
                    }
                }
            }
        }
        cEdgesSize = cEdges.size();
        dEdgesSize = dEdges.size();
        return 0;
    }

    private void addEdges(IBond ReactantBond, IBond ProductBond, int iIndex, int jIndex) {
        if (bondMatch(ReactantBond, ProductBond)) {
            cEdges.add((iIndex / 3) + 1);
            cEdges.add((jIndex / 3) + 1);
        } else if (ReactantBond == null && ProductBond == null) {
            dEdges.add((iIndex / 3) + 1);
            dEdges.add((jIndex / 3) + 1);
        }
    }

    /**
     * compGraphNodesCZero is used to build up of the edges of the compatibility graph
     * @return
     * @throws IOException
     */
    protected Integer compatibilityGraphNodesIfCEdgeIsZero() throws IOException {

        int count_nodes = 1;
        List<String> map = new ArrayList<String>();
        compGraphNodesCZero = new ArrayList<Integer>(); //Initialize the compGraphNodesCZero List
        LabelContainer labelContainer = LabelContainer.getInstance();
        compGraphNodes.clear();

        for (int i = 0; i < source.getAtomCount(); i++) {
            for (int j = 0; j < target.getAtomCount(); j++) {
                IAtom atom1 = source.getAtom(i);
                IAtom atom2 = target.getAtom(j);

                //You can also check object equal or charge, hydrogen count etc

                if (atom1.getSymbol().equalsIgnoreCase(atom2.getSymbol()) && (!map.contains(i + "_" + j))) {
                    compGraphNodesCZero.add(i);
                    compGraphNodesCZero.add(j);
                    compGraphNodesCZero.add(labelContainer.getLabelID(atom1.getSymbol())); //i.e C is label 1
                    compGraphNodesCZero.add(count_nodes);
                    compGraphNodes.add(i);
                    compGraphNodes.add(j);
                    compGraphNodes.add(count_nodes++);
                    map.add(i + "_" + j);
                }
            }
        }
        map.clear();
        return count_nodes;
    }

    /**
     * compatibilityGraphCEdgeZero is used to
     * build up of the edges of the
     * compatibility graph BIS
     * @return
     * @throws IOException
     */
    protected int compatibilityGraphCEdgeZero() throws IOException {

        int compGraphNodesCZeroListSize = compGraphNodesCZero.size();
        cEdges = new ArrayList<Integer>(); //Initialize the cEdges List
        dEdges = new ArrayList<Integer>(); //Initialize the dEdges List

        for (int a = 0; a < compGraphNodesCZeroListSize; a += 4) {
            int index_a = compGraphNodesCZero.get(a);
            int index_aPlus1 = compGraphNodesCZero.get(a + 1);
            for (int b = a + 4; b < compGraphNodesCZeroListSize; b += 4) {
                int index_b = compGraphNodesCZero.get(b);
                int index_bPlus1 = compGraphNodesCZero.get(b + 1);

                // if element atomCont !=jIndex and atoms on the adjacent sides of the bonds are not equal
                if ((a != b) && (index_a != index_b)
                        && (index_aPlus1 != index_bPlus1)) {


                    IBond ReactantBond = null;
                    IBond ProductBond = null;

                    ReactantBond = source.getBond(source.getAtom(index_a), source.getAtom(index_b));
                    ProductBond = target.getBond(target.getAtom(index_aPlus1), target.getAtom(index_bPlus1));

                    if (ReactantBond != null && ProductBond != null) {
                        addCZeroEdges(ReactantBond, ProductBond, a, b);
                    }

                }
            }
        }

        //Size of C and D edges of the compatibility graph
        cEdgesSize = cEdges.size();
        dEdgesSize = dEdges.size();


        return 0;
    }

    private void addCZeroEdges(IBond ReactantBond, IBond ProductBond, int indexI, int indexJ) {
        if (bondMatch(ReactantBond, ProductBond)) {
            cEdges.add((indexI / 4) + 1);
            cEdges.add((indexJ / 4) + 1);
        } else if (ReactantBond == null && ProductBond == null) {
            dEdges.add((indexI / 4) + 1);
            dEdges.add((indexJ / 4) + 1);
        }
    }

    /**
     *
     * @param ReactantBond
     * @param targetBond
     * @return
     */
    private boolean bondMatch(IBond queryBond, IBond targetBond) {
        if (isBondTypeFlag()) {
            if (targetBond instanceof IQueryBond && queryBond instanceof IBond) {
                IQueryBond bond = (IQueryBond) targetBond;
                IQueryAtom atom1 = (IQueryAtom) (targetBond.getAtom(0));
                IQueryAtom atom2 = (IQueryAtom) (targetBond.getAtom(1));
                if (bond.matches(queryBond)) {
                    // ok, bonds match
                    if (atom1.matches(queryBond.getAtom(0)) && atom2.matches(queryBond.getAtom(1))
                            || atom1.matches(queryBond.getAtom(1)) && atom2.matches(queryBond.getAtom(0))) {
                        // ok, atoms match in either order
                        return true;
                    }
                }
            } else if (queryBond instanceof IQueryBond && targetBond instanceof IBond) {
                IQueryBond bond = (IQueryBond) queryBond;
                IQueryAtom atom1 = (IQueryAtom) (queryBond.getAtom(0));
                IQueryAtom atom2 = (IQueryAtom) (queryBond.getAtom(1));
                if (bond.matches(targetBond)) {
                    // ok, bonds match
                    if (atom1.matches(targetBond.getAtom(0)) && atom2.matches(targetBond.getAtom(1))
                            || atom1.matches(targetBond.getAtom(1)) && atom2.matches(targetBond.getAtom(0))) {
                        // ok, atoms match in either order
                        return true;
                    }
                }
            } else {

                int ReactantBondType = queryBond.getOrder().ordinal();
                int ProductBondType = targetBond.getOrder().ordinal();


                if ((queryBond.getFlag(CDKConstants.ISAROMATIC) == targetBond.getFlag(CDKConstants.ISAROMATIC))
                        && (ReactantBondType == ProductBondType)) {
                    return true;
                }

                if (queryBond.getFlag(CDKConstants.ISAROMATIC) && targetBond.getFlag(CDKConstants.ISAROMATIC)) {
                    return true;
                }

            }
            return false;
        }
        return true;
    }

    protected List<Integer> getCEgdes() {
        return Collections.unmodifiableList(cEdges);
    }

    protected List<Integer> getDEgdes() {
        return Collections.unmodifiableList(dEdges);
    }

    protected int getCEdgesSize() {
        return cEdgesSize;
    }

    protected int getDEdgesSize() {
        return dEdgesSize;
    }

    protected List<Integer> getCompGraphNodes() {
        return compGraphNodes;
    }

    protected List<Integer> getCompGraphNodesCZero() {
        return compGraphNodesCZero;
    }

    protected void clearCEgdes() {

        cEdges.clear();
    }

    protected void clearDEgdes() {

        dEdges.clear();
    }

    protected void clearCompGraphNodes() {
        compGraphNodes.clear();
    }

    protected void clearCompGraphNodesCZero() {
        compGraphNodesCZero.clear();
    }

    protected void resetCEdgesSize() {
        cEdgesSize = 0;
    }

    protected void resetDEdgesSize() {
        dEdgesSize = 0;
    }

    protected void clear() {
        cEdges = null;
        dEdges = null;
        compGraphNodes = null;
        compGraphNodesCZero = null;
    }
}
