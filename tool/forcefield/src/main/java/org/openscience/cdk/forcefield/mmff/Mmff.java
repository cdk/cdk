/*
 * Copyright (c) 2015 John May <jwmay@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version. All we ask is that proper credit is given
 * for our work, which includes - but is not limited to - adding the above
 * copyright notice to the beginning of your source code files, and to any
 * copyright notice that you may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */
package org.openscience.cdk.forcefield.mmff;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.config.Elements;
import org.openscience.cdk.graph.GraphUtil;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;

import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

/**
 * Facade to access Merck Molecular Force Field (MMFF) functions.
 * 
 * <ul>
 *     <li>{@cdk.cite Halgren96a}</li>
 *     <li>{@cdk.cite Halgren96b}</li>
 *     <li>{@cdk.cite Halgren96c}</li>
 *     <li>{@cdk.cite Halgren96d}</li>
 *     <li>{@cdk.cite Halgren96e}</li>     
 * </ul>
 *
 * <br>
 * <b>Atom Types</b>
 * 
 * Symbolic atom types are assigned with {@link Mmff#assignAtomTypes(IAtomContainer)}.
 * The atom type name can be accessed with {@link IAtom#getAtomTypeName()}.
 *
 * <br>
 * <b>Partial Charges</b>
 * 
 * Partial charges are assigned with {@link Mmff#partialCharges(IAtomContainer)}.
 * Atom types must be assigned before calling this function. Effective formal
 * charges can also be obtained with {@link Mmff#effectiveCharges(IAtomContainer)}
 * both charge values are accessed with {@link IAtom#getCharge()}. Atoms of
 * unknown type are assigned a neutral charge - to avoid this check the return
 * value of {@link Mmff#assignAtomTypes(IAtomContainer)}.
 * 
 * <pre>{@code
 * IAtomContainer mol = ...;
 * 
 * Mmff mmff = new Mmff();
 * mmff.assignAtomTypes(mol);
 * mmff.partialCharges(mol);
 * mmff.clearProps(mol); // optional
 * }</pre>
 * 
 * @author John May
 * @cdk.githash
 */
public class Mmff {

    private static final String MMFF_ADJLIST_CACHE = "mmff.adjlist.cache";
    private static final String MMFF_EDGEMAP_CACHE = "mmff.edgemap.cache";
    private static final String MMFF_AROM          = "mmff.arom";

    private final MmffAtomTypeMatcher mmffAtomTyper = new MmffAtomTypeMatcher();
    private final MmffParamSet        mmffParamSet  = MmffParamSet.INSTANCE;
    
    /**
     * Assign MMFF Symbolic atom types. The symbolic type can be accessed with
     * {@link IAtom#getAtomTypeName()}. An atom of unknown type is assigned the
     * symbolic type {@code 'UNK'}. 
     * All atoms, including hydrogens must be explicitly represented.
     *
     * @param mol molecule
     * @return all atoms had a type assigned
     */
    public boolean assignAtomTypes(IAtomContainer mol) {

        // preconditions need explicit hydrogens
        for (IAtom atom : mol.atoms()) {
            if (atom.getImplicitHydrogenCount() == null || atom.getImplicitHydrogenCount() > 0)
                throw new IllegalArgumentException("Hydrogens must be explicit nodes, each must have a zero (non-null) impl H count.");
        }

        // conversion to faster data structures
        GraphUtil.EdgeToBondMap edgeMap = GraphUtil.EdgeToBondMap.withSpaceFor(mol);
        int[][] adjList = GraphUtil.toAdjList(mol, edgeMap);

        mol.setProperty(MMFF_ADJLIST_CACHE, adjList);
        mol.setProperty(MMFF_EDGEMAP_CACHE, edgeMap);

        Set<IBond> aromBonds = new HashSet<>();

        Set<IChemObject> oldArom = getAromatics(mol);

        // note: for MMFF we need to remove current aromatic flags for type
        // assignment (they are restored after)
        for (IChemObject chemObj : oldArom)
            chemObj.setFlag(CDKConstants.ISAROMATIC, false);
        String[] atomTypes = mmffAtomTyper.symbolicTypes(mol, adjList, edgeMap, aromBonds);

        boolean hasUnkType = false;
        for (int i = 0; i < mol.getAtomCount(); i++) {
            if (atomTypes[i] == null) {
                mol.getAtom(i).setAtomTypeName("UNK");
                hasUnkType = true;
            }
            else {
                mol.getAtom(i).setAtomTypeName(atomTypes[i]);
            }
        }

        // restore aromatic flags and mark the MMFF aromatic bonds
        for (IChemObject chemObj : oldArom)
            chemObj.setFlag(CDKConstants.ISAROMATIC, true);
        for (IBond bond : aromBonds)
            bond.setProperty(MMFF_AROM, true);

        return !hasUnkType;
    }

    /**
     * Assign the effective formal charges used by MMFF in calculating the
     * final partial charge values. Atom types must be assigned first. All 
     * existing charges are cleared.
     * 
     * @param mol molecule
     * @return charges were assigned
     * @see #partialCharges(IAtomContainer) 
     * @see #assignAtomTypes(IAtomContainer) 
     */
    public boolean effectiveCharges(IAtomContainer mol) {

        int[][] adjList = mol.getProperty(MMFF_ADJLIST_CACHE);
        GraphUtil.EdgeToBondMap edgeMap = mol.getProperty(MMFF_EDGEMAP_CACHE);

        if (adjList == null || edgeMap == null)
            throw new IllegalArgumentException("Invoke assignAtomTypes first.");

        primaryCharges(mol, adjList, edgeMap);
        effectiveCharges(mol, adjList);

        return true;
    }

    /**
     * Assign the partial charges, all existing charges are cleared.
     * Atom types must be assigned first. 
     *
     * @param mol molecule
     * @return charges were assigned
     * @see #effectiveCharges(IAtomContainer)
     * @see #assignAtomTypes(IAtomContainer)
     */
    public boolean partialCharges(IAtomContainer mol) {

        int[][] adjList = mol.getProperty(MMFF_ADJLIST_CACHE);
        GraphUtil.EdgeToBondMap edgeMap = mol.getProperty(MMFF_EDGEMAP_CACHE);

        if (adjList == null || edgeMap == null)
            throw new IllegalArgumentException("Invoke assignAtomTypes first.");

        effectiveCharges(mol);
        
        for (int v = 0; v < mol.getAtomCount(); v++) {

            IAtom atom = mol.getAtom(v);
            String symbType = atom.getAtomTypeName();
            final int thisType = mmffParamSet.intType(symbType);

            // unknown
            if (thisType == 0)
                continue;

            double pbci = mmffParamSet.getPartialBondChargeIncrement(thisType).doubleValue();

            for (int w : adjList[v]) {

                int otherType = mmffParamSet.intType(mol.getAtom(w).getAtomTypeName());

                // unknown
                if (otherType == 0)
                    continue;

                IBond bond = edgeMap.get(v, w);
                int bondCls = mmffParamSet.getBondCls(thisType, otherType, bond.getOrder().numeric(), bond.getProperty(MMFF_AROM) != null);
                BigDecimal bci = mmffParamSet.getBondChargeIncrement(bondCls, thisType, otherType);
                if (bci != null) {
                    atom.setCharge(atom.getCharge() - bci.doubleValue());
                }
                else {
                    // empirical BCI
                    atom.setCharge(atom.getCharge() + (pbci - mmffParamSet.getPartialBondChargeIncrement(otherType).doubleValue()));
                }
            }
        }

        return true;
    }

    /**
     * Clear all transient properties assigned by this class. Assigned charges
     * and atom type names remain set.
     * 
     * @param mol molecule
     */
    public void clearProps(IAtomContainer mol) {
        mol.removeProperty(MMFF_EDGEMAP_CACHE);
        mol.removeProperty(MMFF_ADJLIST_CACHE);
        for (IBond bond : mol.bonds())
            bond.removeProperty(MMFF_AROM);
    }

    /**
     * Internal method, MMFF primary charges. Tabulated (MMFFFORMCHG.PAR) and
     * variable (assigned in code).
     * 
     * @param mol molecule
     * @param adjList adjacency list representation
     * @param edgeMap edge to bond mapping
     */
    void primaryCharges(IAtomContainer mol, int[][] adjList, GraphUtil.EdgeToBondMap edgeMap) {


        for (int v = 0; v < mol.getAtomCount(); v++) {
            IAtom atom = mol.getAtom(v);
            String symbType = atom.getAtomTypeName();
            BigDecimal fc = mmffParamSet.getFormalCharge(symbType);
            
            atom.setCharge(0d);

            if (fc != null) {
                atom.setCharge(fc.doubleValue());
            }
            // charge sharing between equivalent terminal oxygens
            else if (symbType.equals("O2S") || symbType.equals("O3S") || symbType.equals("O2P") || symbType.equals("O3P") || symbType.equals("O4P")) {

                // already handled
                if (atom.getCharge() != 0)
                    continue;

                // find the central atom (S or P)
                int focus = -1;
                for (int w : adjList[v]) {
                    int elem = mol.getAtom(w).getAtomicNumber();
                    if (elem == Elements.Sulfur.number() || elem == Elements.Phosphorus.number()) {
                        if (focus >= 0) {
                            focus = -2;
                            break;
                        }
                        focus = w;
                    }
                }

                // log - multiple or unfound focus
                if (focus < 0)
                    continue;

                // ensure [P+]-[O-] vs P=O are same by including the charge from
                // the focus
                double qSum = fCharge(mol.getAtom(focus));
                int nTerm = 0;

                for (int w : adjList[focus]) {
                    if (mol.getAtom(w).getAtomTypeName().equals(symbType)) {
                        qSum += fCharge(mol.getAtom(w));
                        nTerm++;
                    }
                }
                double qSplt = qSum / nTerm;

                for (int w : adjList[focus]) {
                    if (mol.getAtom(w).getAtomTypeName().equals(symbType)) {
                        atom.setCharge(qSplt);
                    }
                }

            }
            // charge sharing between nitrogen anions 
            else if (symbType.equals("N5M")) {

                if (atom.getCharge() != 0)
                    continue;

                Set<IAtom> eqiv = new HashSet<>();
                Set<Integer> visit = new HashSet<>();
                Deque<Integer> queue = new ArrayDeque<>();

                queue.add(v);

                while (!queue.isEmpty()) {
                    int w = queue.poll();
                    visit.add(w);

                    if (mol.getAtom(w).getAtomTypeName().equals("N5M"))
                        eqiv.add(mol.getAtom(w));

                    for (int u : adjList[w]) {
                        IBond bond = edgeMap.get(w, u);
                        if (bond.getProperty(MMFF_AROM) != null && !visit.contains(u)) {
                            queue.add(u);
                        }
                    }
                }

                double q = 0;
                for (IAtom eqivAtom : eqiv) {
                    q += fCharge(eqivAtom);
                }
                q /= eqiv.size();
                for (IAtom eqivAtom : eqiv) {
                    eqivAtom.setCharge(q);
                }
            }
        }
    }

    /**
     * Internal effective charges method.
     * 
     * @param mol molecule
     * @param adjList adjacency list representation
     * @see {@link #effectiveCharges(IAtomContainer)}                
     */
    void effectiveCharges(IAtomContainer mol, int[][] adjList) {
        double[] tmp = new double[mol.getAtomCount()];
        for (int v = 0; v < tmp.length; v++) {
            IAtom atom = mol.getAtom(v);
            int intType = mmffParamSet.intType(atom.getAtomTypeName());

            // unknown
            if (intType == 0) {
                continue;
            }

            int crd = mmffParamSet.getCrd(intType);
            BigDecimal fcAdj = mmffParamSet.getFormalChargeAdjustment(intType);


            double adjust = fcAdj.doubleValue();
            tmp[v] = atom.getCharge();

            // sharing when no formal charge adjustment - needed to match 
            // phosphate examples from paper V but documented?
            if (adjust == 0) {
                for (int w : adjList[v]) {
                    if (mol.getAtom(w).getCharge() < 0) {
                        tmp[v] += mol.getAtom(w).getCharge() / (2.0 * adjList[w].length);
                    }
                }   
            }

            // positive charge sharing - undocumented but inferred from validation suite
            if (atom.getAtomTypeName().equals("NM")) {
                for (int w : adjList[v]) {
                    if (mol.getAtom(w).getCharge() > 0) {
                        tmp[v] -= mol.getAtom(w).getCharge() / 2;
                    }
                }
            }

            // negative charge sharing
            if (adjust != 0) {
                double q = 0;
                for (int w : adjList[v]) {
                    q += mol.getAtom(w).getCharge();
                }
                tmp[v] = ((1 - (crd * adjust)) * tmp[v]) + (adjust * q);
            }
        }

        for (int v = 0; v < tmp.length; v++) {
            mol.getAtom(v).setCharge(tmp[v]);
        }
    }

    /**
     * Helper method to find all existing aromatic chem objects.
     * 
     * @param mol molecule
     * @return chem objects
     */
    private Set<IChemObject> getAromatics(IAtomContainer mol) {
        Set<IChemObject> oldArom = new HashSet<>();
        for (IAtom atom : mol.atoms())
            if (atom.getFlag(CDKConstants.ISAROMATIC))
                oldArom.add(atom);
        for (IBond bond : mol.bonds())
            if (bond.getFlag(CDKConstants.ISAROMATIC))
                oldArom.add(bond);
        return oldArom;
    }

    /**
     * Access the formal charge - if the charge is null 0 is returned.
     * @param atom atom
     * @return formal charge
     */
    int fCharge(IAtom atom) {
        if (atom.getFormalCharge() == null)
            return 0;
        return atom.getFormalCharge();
    }
}
