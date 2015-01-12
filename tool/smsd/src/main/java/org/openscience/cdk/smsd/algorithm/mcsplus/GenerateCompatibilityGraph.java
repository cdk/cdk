/*
 *
 * Copyright (C) 2006-2010  Syed Asad Rahman <asad@ebi.ebi.ac.uk>
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
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.smsd.algorithm.matchers.AtomMatcher;
import org.openscience.cdk.smsd.algorithm.matchers.BondMatcher;
import org.openscience.cdk.smsd.algorithm.matchers.DefaultBondMatcher;
import org.openscience.cdk.smsd.algorithm.matchers.DefaultMCSPlusAtomMatcher;
import org.openscience.cdk.smsd.algorithm.matchers.DefaultMatcher;
import org.openscience.cdk.smsd.helper.LabelContainer;

/**
 * This class generates compatibility graph between query and target molecule.
 * It also markes edges in the compatibility graph as c-edges or d-edges.
 * @cdk.module smsd
 * @cdk.githash
 * @author Syed Asad Rahman <asad@ebi.ac.uk>
 */
public final class GenerateCompatibilityGraph {

    private List<Integer>  compGraphNodes      = null;
    private List<Integer>  compGraphNodesCZero = null;
    private List<Integer>  cEdges              = null;
    private List<Integer>  dEdges              = null;
    private int            cEdgesSize          = 0;
    private int            dEdgesSize          = 0;
    private IAtomContainer source              = null;
    private IAtomContainer target              = null;
    private boolean        shouldMatchBonds    = false;

    /**
    * Default constructor added
    */
    public GenerateCompatibilityGraph() {

    }

    /**
     * Generates a compatibility graph between two molecules
     * @param source
     * @param target
     * @param shouldMatchBonds
     * @throws java.io.IOException
     */
    public GenerateCompatibilityGraph(IAtomContainer source, IAtomContainer target, boolean shouldMatchBonds)
            throws IOException {
        setMatchBond(shouldMatchBonds);
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
        List<List<Integer>> labelList = new ArrayList<List<Integer>>();

        for (int i = 0; i < atomCont.getAtomCount(); i++) {
            LabelContainer labelContainer = LabelContainer.getInstance();
            ArrayList<Integer> label = new ArrayList<Integer>(7);
            //            label.setSize(7);

            for (int a = 0; a < 7; a++) {
                label.add(a, 0);
            }

            IAtom refAtom = atomCont.getAtom(i);
            String atom1Type = refAtom.getSymbol();

            label.set(0, labelContainer.getLabelID(atom1Type));

            int countNeighbors = 1;
            List<IAtom> connAtoms = atomCont.getConnectedAtomsList(refAtom);

            for (IAtom negAtom : connAtoms) {
                String atom2Type = negAtom.getSymbol();
                label.set(countNeighbors++, labelContainer.getLabelID(atom2Type));
            }

            bubbleSort(label);
            labelList.add(label);

        }
        return labelList;
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

        List<IAtom> basicAtoms = new ArrayList<IAtom>();
        for (IAtom atom : atomCont.atoms()) {
            basicAtoms.add(atom);
        }
        return basicAtoms;
    }

    /**
     * Generate Compatibility Graph Nodes
     *
     * @return
     * @throws IOException
     */
    protected int compatibilityGraphNodes() throws IOException {

        compGraphNodes.clear();
        List<IAtom> basicAtomVecA = null;
        List<IAtom> basicAtomVecB = null;
        IAtomContainer reactant = source;
        IAtomContainer product = target;

        basicAtomVecA = reduceAtomSet(reactant);
        basicAtomVecB = reduceAtomSet(product);

        List<List<Integer>> labelListMolA = labelAtoms(reactant);
        List<List<Integer>> labelListMolB = labelAtoms(product);

        int molANodes = 0;
        int countNodes = 1;

        for (List<Integer> labelA : labelListMolA) {
            int molBNodes = 0;
            for (List<Integer> labelB : labelListMolB) {
                if (labelA.equals(labelB)) {
                    compGraphNodes.add(reactant.getAtomNumber(basicAtomVecA.get(molANodes)));
                    compGraphNodes.add(product.getAtomNumber(basicAtomVecB.get(molBNodes)));
                    compGraphNodes.add(countNodes++);
                }
                molBNodes++;
            }
            molANodes++;
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
        int compGraphNodesListSize = compGraphNodes.size();

        cEdges = new ArrayList<Integer>(); //Initialize the cEdges List
        dEdges = new ArrayList<Integer>(); //Initialize the dEdges List

        for (int a = 0; a < compGraphNodesListSize; a += 3) {
            int indexA = compGraphNodes.get(a);
            int indexAPlus1 = compGraphNodes.get(a + 1);

            for (int b = a + 3; b < compGraphNodesListSize; b += 3) {
                int indexB = compGraphNodes.get(b);
                int indexBPlus1 = compGraphNodes.get(b + 1);

                // if element atomCont !=jIndex and atoms on the adjacent sides of the bonds are not equal
                if (a != b && indexA != indexB && indexAPlus1 != indexBPlus1) {

                    IBond reactantBond = null;
                    IBond productBond = null;

                    reactantBond = source.getBond(source.getAtom(indexA), source.getAtom(indexB));
                    productBond = target.getBond(target.getAtom(indexAPlus1), target.getAtom(indexBPlus1));
                    if (reactantBond != null && productBond != null) {
                        addEdges(reactantBond, productBond, a, b);
                    }
                }
            }
        }
        cEdgesSize = cEdges.size();
        dEdgesSize = dEdges.size();
        return 0;
    }

    private void addEdges(IBond reactantBond, IBond productBond, int iIndex, int jIndex) {
        //if (isMatchBond() && bondMatch(ReactantBond, ProductBond)) {
        if (isMatchFeasible(source, reactantBond, target, productBond, shouldMatchBonds)) {
            cEdges.add((iIndex / 3) + 1);
            cEdges.add((jIndex / 3) + 1);
        } else if (reactantBond == null && productBond == null) {
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

        int countNodes = 1;
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
                    compGraphNodesCZero.add(countNodes);
                    compGraphNodes.add(i);
                    compGraphNodes.add(j);
                    compGraphNodes.add(countNodes++);
                    map.add(i + "_" + j);
                }
            }
        }
        map.clear();
        return countNodes;
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
            int indexA = compGraphNodesCZero.get(a);
            int indexAPlus1 = compGraphNodesCZero.get(a + 1);
            for (int b = a + 4; b < compGraphNodesCZeroListSize; b += 4) {
                int indexB = compGraphNodesCZero.get(b);
                int indexBPlus1 = compGraphNodesCZero.get(b + 1);

                // if element atomCont !=jIndex and atoms on the adjacent sides of the bonds are not equal
                if ((a != b) && (indexA != indexB) && (indexAPlus1 != indexBPlus1)) {

                    IBond reactantBond = null;
                    IBond productBond = null;

                    reactantBond = source.getBond(source.getAtom(indexA), source.getAtom(indexB));
                    productBond = target.getBond(target.getAtom(indexAPlus1), target.getAtom(indexBPlus1));

                    if (reactantBond != null && productBond != null) {
                        addCZeroEdges(reactantBond, productBond, a, b);
                    }

                }
            }
        }

        //Size of C and D edges of the compatibility graph
        cEdgesSize = cEdges.size();
        dEdgesSize = dEdges.size();
        return 0;
    }

    private void addCZeroEdges(IBond reactantBond, IBond productBond, int indexI, int indexJ) {
        if (isMatchFeasible(source, productBond, target, productBond, shouldMatchBonds)) {
            //bondMatch(reactantBond, productBond)
            cEdges.add((indexI / 4) + 1);
            cEdges.add((indexJ / 4) + 1);
        }
        if (reactantBond == null && productBond == null) {
            dEdges.add((indexI / 4) + 1);
            dEdges.add((indexJ / 4) + 1);
        }
    }

    private static boolean isMatchFeasible(IAtomContainer ac1, IBond bondA1, IAtomContainer ac2, IBond bondA2,
            boolean shouldMatchBonds) {

        //Bond Matcher
        BondMatcher bondMatcher = new DefaultBondMatcher(ac1, bondA1, shouldMatchBonds);
        //Atom Matcher
        AtomMatcher atomMatcher1 = new DefaultMCSPlusAtomMatcher(ac1, bondA1.getAtom(0), shouldMatchBonds);
        //Atom Matcher
        AtomMatcher atomMatcher2 = new DefaultMCSPlusAtomMatcher(ac1, bondA1.getAtom(1), shouldMatchBonds);

        if (DefaultMatcher.isBondMatch(bondMatcher, ac2, bondA2, shouldMatchBonds)
                && DefaultMatcher.isAtomMatch(atomMatcher1, atomMatcher2, ac2, bondA2, shouldMatchBonds)) {
            return true;
        }
        return false;
    }

    public List<Integer> getCEgdes() {
        return Collections.unmodifiableList(cEdges);
    }

    protected List<Integer> getDEgdes() {
        return Collections.unmodifiableList(dEdges);
    }

    protected List<Integer> getCompGraphNodes() {
        return Collections.unmodifiableList(compGraphNodes);
    }

    protected int getCEdgesSize() {
        return cEdgesSize;
    }

    protected int getDEdgesSize() {
        return dEdgesSize;
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

    public void clear() {
        cEdges = null;
        dEdges = null;
        compGraphNodes = null;
        compGraphNodesCZero = null;
    }

    /**
     * @return the shouldMatchBonds
     */
    public boolean isMatchBond() {
        return shouldMatchBonds;
    }

    /**
     * @param shouldMatchBonds the shouldMatchBonds to set
     */
    public void setMatchBond(boolean shouldMatchBonds) {
        this.shouldMatchBonds = shouldMatchBonds;
    }
}
