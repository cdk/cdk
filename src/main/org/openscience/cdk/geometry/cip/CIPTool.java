/* Copyright (C) 2010  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.geometry.cip;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.geometry.cip.rules.CIPLigandRule;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.ITetrahedralChirality;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.interfaces.ITetrahedralChirality.Stereo;
import org.openscience.cdk.stereo.StereoTool;

/**
 * Tool to help determine the R,S and stereochemistry definitions of a subset of the
 * CIP rules {@cdk.cite Cahn1966}. The used set up sub rules are specified in the
 * {@link CIPLigandRule} class.
 *
 * <p>Basic use starts from a {@link ITetrahedralChirality} and therefore
 * assumes atoms with four neighbors:
 * <pre>
 * IAtom[] ligandAtoms =
 *   mol.getConnectedAtomsList(centralAtom).toArray(new IAtom[4]);
 * ITetrahedralChirality tetraStereo = new TetrahedralChirality(
 *   centralAtom, ligandAtoms, Stereo.ANTI_CLOCKWISE
 * );
 * CIP_CHIRALITY cipChirality = CIPTool.getCIPChirality(mol, tetraStereo);
 * </pre>
 * The {@link org.openscience.cdk.interfaces.IBond.Stereo} value can be
 * reconstructed from 3D coordinates with the {@link StereoTool}. 
 *
 * @cdk.module cip
 * @cdk.githash
 */
@TestClass("org.openscience.cdk.geometry.cip.CIPToolTest")
public class CIPTool {

    /**
     * IAtom index to indicate an implicit hydrogen, not present in the chemical graph.
     */
    public static final int HYDROGEN = -1;
    
    private static CIPLigandRule cipRule = new CIPLigandRule();

    public enum CIP_CHIRALITY {
        R, S, NONE
    }

    /**
     * Returns the R or S chirality according to the CIP rules, based on the given
     * chirality information.
     *
     * @param  stereoCenter Chiral center for which the CIP chirality is to be 
     *                      determined as {@link LigancyFourChirality} object.
     * @return A {@link CIP_CHIRALITY} value.
     */
    @TestMethod("testGetCIPChirality,testGetCIPChirality_Anti")
    public static CIP_CHIRALITY getCIPChirality(LigancyFourChirality stereoCenter) {
        ILigand[] ligands = order(stereoCenter.getLigands());
        LigancyFourChirality rsChirality = stereoCenter.project(ligands);

        boolean allAreDifferent = checkIfAllLigandsAreDifferent(ligands);
        if (!allAreDifferent) return CIP_CHIRALITY.NONE;

        if (rsChirality.getStereo() == Stereo.CLOCKWISE)
            return CIP_CHIRALITY.R;

        return CIP_CHIRALITY.S;
    }

    /**
     * Returns the R or S chirality according to the CIP rules, based on the given
     * chirality information.
     *
     * @param  container    {@link IAtomContainer} to which the <code>stereoCenter</code>
     *                      belongs.
     * @param  stereoCenter Chiral center for which the CIP chirality is to be 
     *                      determined as {@link ITetrahedralChirality} object.
     * @return A {@link CIP_CHIRALITY} value.
     */
    @TestMethod("testGetCIPChirality_ILigancyFourChirality,testGetCIPChirality_Anti_ILigancyFourChirality")
    public static CIP_CHIRALITY getCIPChirality(
            IAtomContainer container, ITetrahedralChirality stereoCenter) {
        LigancyFourChirality cipLigancy = new LigancyFourChirality(container, stereoCenter);
        ILigand[] ligands = order(cipLigancy.getLigands());
        LigancyFourChirality rsChirality = cipLigancy.project(ligands);

        boolean allAreDifferent = checkIfAllLigandsAreDifferent(ligands);
        if (!allAreDifferent) return CIP_CHIRALITY.NONE;

        if (rsChirality.getStereo() == Stereo.CLOCKWISE)
            return CIP_CHIRALITY.R;

        return CIP_CHIRALITY.S;
    }

    /**
     * Checks if each next {@link ILigand} is different from the previous
     * one according to the {@link CIPLigandRule}. It assumes that the input
     * is sorted based on that rule.
     *
     * @param ligands array of {@link ILigand} to check
     * @return true, if all ligands are different
     */
    @TestMethod("testCheckIfAllLigandsAreDifferent,testCheckIfAllLigandsAreDifferent_False")
    public static boolean checkIfAllLigandsAreDifferent(ILigand[] ligands) {
        for (int i=0; i<(ligands.length-1); i++) {
            if (cipRule.compare(ligands[i], ligands[i+1]) == 0) return false;
        }
        return true;
    }

    /**
     * Reorders the {@link ILigand} objects in the array according to the CIP rules.
     *
     * @param ligands Array of {@link ILigand}s to be reordered.
     * @return        Reorderd array of {@link ILigand}s.
     */
    @TestMethod("testOrder")
    public static ILigand[] order(ILigand[] ligands) {
        ILigand[] newLigands = new ILigand[ligands.length];
        System.arraycopy(ligands, 0, newLigands, 0, ligands.length);

        Arrays.sort(newLigands, cipRule);
        return newLigands;
    }

    /**
     * Creates a ligancy four chirality around a single chiral atom, where the involved
     * atoms are identified by there index in the {@link IAtomContainer}. For the four ligand
     * atoms, {@link #HYDROGEN} can be passed as index, which will indicate the presence of
     * an implicit hydrogen, not explicitly present in the chemical graph of the
     * given <code>container</code>.
     *
     * @param container  {@link IAtomContainer} for which the returned {@link ILigand}s are defined
     * @param chiralAtom int pointing to the {@link IAtom} index of the chiral atom
     * @param ligand1    int pointing to the {@link IAtom} index of the first {@link ILigand}
     * @param ligand2    int pointing to the {@link IAtom} index of the second {@link ILigand}
     * @param ligand3    int pointing to the {@link IAtom} index of the third {@link ILigand}
     * @param ligand4    int pointing to the {@link IAtom} index of the fourth {@link ILigand}
     * @param stereo     {@link Stereo} for the chirality
     * @return           the created {@link LigancyFourChirality}
     */
    @TestMethod("testDefineLigancyFourChirality")
    public static LigancyFourChirality defineLigancyFourChirality(
        IAtomContainer container, int chiralAtom,
        int ligand1, int ligand2, int ligand3, int ligand4,
        Stereo stereo) {
        int[] atomIndices = {ligand1, ligand2, ligand3, ligand4};
        VisitedAtoms visitedAtoms = new VisitedAtoms();
        ILigand[] ligands = new ILigand[4];
        for (int i=0; i<4; i++) {
            ligands[i] = defineLigand(container, visitedAtoms, chiralAtom, atomIndices[i]);
        }
        return new LigancyFourChirality(container.getAtom(chiralAtom), ligands, stereo);
    }

    /**
     * Creates a ligand attached to a single chiral atom, where the involved
     * atoms are identified by there index in the {@link IAtomContainer}. For ligand
     * atom, {@link #HYDROGEN} can be passed as index, which will indicate the presence of
     * an implicit hydrogen, not explicitly present in the chemical graph of the
     * given <code>container</code>.
     *
     * @param container  {@link IAtomContainer} for which the returned {@link ILigand}s are defined
     * @param chiralAtom int pointing to the {@link IAtom} index of the chiral atom
     * @param ligandAtom int pointing to the {@link IAtom} index of the {@link ILigand}
     * @return           the created {@link ILigand}
     */
    @TestMethod("testDefineLigand")
    public static ILigand defineLigand(IAtomContainer container,
            VisitedAtoms visitedAtoms, int chiralAtom, int ligandAtom) {
        if (ligandAtom == HYDROGEN) {
            return new ImplicitHydrogenLigand(
                container, visitedAtoms,
                container.getAtom(chiralAtom)
            );
        } else {
            return new Ligand(
                container, visitedAtoms,
                container.getAtom(chiralAtom), container.getAtom(ligandAtom)
            );
        }
    }

    /**
     * Returns a CIP-expanded array of side chains of a ligand. If the ligand atom is only connected to
     * the chiral atom, the method will return an empty list. The expansion involves the CIP rules,
     * so that a double bonded oxygen will be represented twice in the list. 
     *
     * @param ligand     the {@link ILigand} for which to return the ILigands
     * @return           a {@link ILigand} array with the side chains of the ligand atom
     */
    @TestMethod("testGetLigandLigands")
    public static ILigand[] getLigandLigands(ILigand ligand) {
        if (ligand instanceof TerminalLigand) return new ILigand[0];

        IAtomContainer container = ligand.getAtomContainer();
        IAtom ligandAtom = ligand.getLigandAtom();
        IAtom centralAtom = ligand.getCentralAtom();
        VisitedAtoms visitedAtoms = ligand.getVisitedAtoms();
        List<IBond> bonds = container.getConnectedBondsList(ligandAtom);
        // duplicate ligands according to bond order, following the CIP rules
        List<ILigand> ligands = new ArrayList<ILigand>();
        for (IBond bond : bonds) {
            if (bond.contains(centralAtom)) {
                if (Order.SINGLE == bond.getOrder()) continue;
                int duplication = getDuplication(bond.getOrder()) - 1;
                if (duplication > 0) {
                    for (int i=1; i<=duplication; i++) {
                        ligands.add(new TerminalLigand(
                            container, visitedAtoms, ligandAtom, centralAtom
                        ));
                    }
                }
            } else {
                int duplication = getDuplication(bond.getOrder());
                IAtom connectedAtom = bond.getConnectedAtom(ligandAtom);
                if (visitedAtoms.isVisited(connectedAtom)) {
                    ligands.add(new TerminalLigand(
                        container, visitedAtoms, ligandAtom, connectedAtom
                    ));
                } else {
                    ligands.add(new Ligand(
                        container, visitedAtoms, ligandAtom, connectedAtom
                    ));
                }
                for (int i=2; i<=duplication; i++) {
                    ligands.add(new TerminalLigand(
                        container, visitedAtoms, ligandAtom, connectedAtom
                    ));
                }
            }
        }
        return ligands.toArray(new ILigand[0]);
    }

    /**
     * Returns the number of times the side chain should end up as the CIP-expanded ligand list. The CIP
     * rules prescribe that a double bonded oxygen should be represented twice in the list.
     *
     * @param  order {@link Order} of the bond
     * @return int reflecting the duplication number
     */
    private static int getDuplication(Order order) {
        if (order == Order.SINGLE) return 1;
        if (order == Order.DOUBLE) return 2;
        if (order == Order.TRIPLE) return 3;
        if (order == Order.QUADRUPLE) return 4;
        return 0;
    }
}
