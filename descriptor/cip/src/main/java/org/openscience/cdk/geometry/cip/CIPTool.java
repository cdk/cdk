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

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.geometry.cip.rules.CIPLigandRule;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IDoubleBondStereochemistry;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.interfaces.ITetrahedralChirality;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.interfaces.ITetrahedralChirality.Stereo;
import org.openscience.cdk.stereo.StereoTool;

import static org.openscience.cdk.interfaces.IDoubleBondStereochemistry.Conformation;

/**
 * Tool to help determine the R,S and stereochemistry definitions of a subset of the
 * CIP rules {@cdk.cite Cahn1966}. The used set up sub rules are specified in the
 * {@link CIPLigandRule} class.
 *
 * <p>Basic use starts from a {@link ITetrahedralChirality} and therefore
 * assumes atoms with four neighbours:
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
public class CIPTool {

    /**
     * IAtom index to indicate an implicit hydrogen, not present in the chemical graph.
     */
    public static final int      HYDROGEN = -1;

    private static CIPLigandRule cipRule  = new CIPLigandRule();

    /**
     * Enumeration with the two tetrahedral chiralities defined by the CIP schema.
     *
     * @author egonw
     */
    public enum CIP_CHIRALITY {
        R, S, E, Z, NONE
    }

    /**
     * Returns the R or S chirality according to the CIP rules, based on the given
     * chirality information.
     *
     * @param  stereoCenter Chiral center for which the CIP chirality is to be
     *                      determined as {@link LigancyFourChirality} object.
     * @return A {@link CIP_CHIRALITY} value.
     */
    public static CIP_CHIRALITY getCIPChirality(LigancyFourChirality stereoCenter) {
        ILigand[] ligands = order(stereoCenter.getLigands());
        LigancyFourChirality rsChirality = stereoCenter.project(ligands);

        boolean allAreDifferent = checkIfAllLigandsAreDifferent(ligands);
        if (!allAreDifferent) return CIP_CHIRALITY.NONE;

        if (rsChirality.getStereo() == Stereo.CLOCKWISE) return CIP_CHIRALITY.R;

        return CIP_CHIRALITY.S;
    }

    /**
     * Convenience method for labelling all stereo elements. The {@link
     * CIP_CHIRALITY} is determined for each element and stored as as {@link
     * String} on the {@link CDKConstants#CIP_DESCRIPTOR} property key.
     * Atoms/bonds that are not stereocenters have no label assigned and the
     * property will be null.
     *
     * @param container structure to label
     */
    public static void label(IAtomContainer container) {

        for (IStereoElement stereoElement : container.stereoElements()) {
            if (stereoElement instanceof ITetrahedralChirality) {
                ITetrahedralChirality tc = (ITetrahedralChirality) stereoElement;
                tc.getChiralAtom().setProperty(CDKConstants.CIP_DESCRIPTOR, getCIPChirality(container, tc).toString());
            } else if (stereoElement instanceof IDoubleBondStereochemistry) {
                IDoubleBondStereochemistry dbs = (IDoubleBondStereochemistry) stereoElement;
                dbs.getStereoBond()
                        .setProperty(CDKConstants.CIP_DESCRIPTOR, getCIPChirality(container, dbs).toString());
            }
        }

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
    public static CIP_CHIRALITY getCIPChirality(IAtomContainer container, ITetrahedralChirality stereoCenter) {

        // the LigancyFourChirality is kind of redundant but we keep for an
        // easy way to get the ILigands array
        LigancyFourChirality tmp = new LigancyFourChirality(container, stereoCenter);
        Stereo stereo = stereoCenter.getStereo();

        int parity = permParity(tmp.getLigands());

        if (parity == 0) return CIP_CHIRALITY.NONE;
        if (parity < 0) stereo = stereo.invert();

        if (stereo == Stereo.CLOCKWISE) return CIP_CHIRALITY.R;
        if (stereo == Stereo.ANTI_CLOCKWISE) return CIP_CHIRALITY.S;

        return CIP_CHIRALITY.NONE;
    }

    public static CIP_CHIRALITY getCIPChirality(IAtomContainer container, IDoubleBondStereochemistry stereoCenter) {

        IBond stereoBond = stereoCenter.getStereoBond();
        IBond leftBond = stereoCenter.getBonds()[0];
        IBond rightBond = stereoCenter.getBonds()[1];

        // the following variables are usd to label the atoms - makes things
        // a little more concise
        //
        // x       y       x
        //  \     /         \
        //   u = v    or     u = v
        //                        \
        //                         y
        //
        IAtom u = stereoBond.getBeg();
        IAtom v = stereoBond.getEnd();
        IAtom x = leftBond.getConnectedAtom(u);
        IAtom y = rightBond.getConnectedAtom(v);

        Conformation conformation = stereoCenter.getStereo();

        ILigand[] leftLigands = getLigands(u, container, v);
        ILigand[] rightLigands = getLigands(v, container, u);

        if (leftLigands.length > 2 || rightLigands.length > 2) return CIP_CHIRALITY.NONE;

        // invert if x/y aren't in the first position
        if (leftLigands[0].getLigandAtom() != x) conformation = conformation.invert();
        if (rightLigands[0].getLigandAtom() != y) conformation = conformation.invert();

        int p = permParity(leftLigands) * permParity(rightLigands);

        if (p == 0) return CIP_CHIRALITY.NONE;

        if (p < 0) conformation = conformation.invert();

        if (conformation == Conformation.TOGETHER) return CIP_CHIRALITY.Z;
        if (conformation == Conformation.OPPOSITE) return CIP_CHIRALITY.E;

        return CIP_CHIRALITY.NONE;
    }

    /**
     * Obtain the ligands connected to the 'atom' excluding 'exclude'. This is
     * mainly meant as a utility for double-bond labelling.
     *
     * @param atom      an atom
     * @param container a structure to which 'atom' belongs
     * @param exclude   exclude this atom - can not be null
     * @return the ligands
     */
    private static ILigand[] getLigands(IAtom atom, IAtomContainer container, IAtom exclude) {

        List<IAtom> neighbors = container.getConnectedAtomsList(atom);

        ILigand[] ligands = new ILigand[neighbors.size() - 1];

        int i = 0;
        for (IAtom neighbor : neighbors) {
            if (neighbor != exclude) ligands[i++] = new Ligand(container, new VisitedAtoms(), atom, neighbor);
        }

        return ligands;
    }

    /**
     * Checks if each next {@link ILigand} is different from the previous
     * one according to the {@link CIPLigandRule}. It assumes that the input
     * is sorted based on that rule.
     *
     * @param ligands array of {@link ILigand} to check
     * @return true, if all ligands are different
     */
    public static boolean checkIfAllLigandsAreDifferent(ILigand[] ligands) {
        for (int i = 0; i < (ligands.length - 1); i++) {
            if (cipRule.compare(ligands[i], ligands[i + 1]) == 0) return false;
        }
        return true;
    }

    /**
     * Reorders the {@link ILigand} objects in the array according to the CIP rules.
     *
     * @param ligands Array of {@link ILigand}s to be reordered.
     * @return        Reordered array of {@link ILigand}s.
     */
    public static ILigand[] order(ILigand[] ligands) {
        ILigand[] newLigands = new ILigand[ligands.length];
        System.arraycopy(ligands, 0, newLigands, 0, ligands.length);

        Arrays.sort(newLigands, cipRule);
        return newLigands;
    }

    /**
     * Obtain the permutation parity (-1,0,+1) to put the ligands in descending
     * order (highest first). A parity of 0 indicates two or more ligands were
     * equivalent.
     *
     * @param ligands the ligands to sort
     * @return parity, odd (-1), even (+1) or none (0)
     */
    private static int permParity(final ILigand[] ligands) {

        // count the number of swaps made by insertion sort - if duplicates
        // are fount the parity is 0
        int swaps = 0;

        for (int j = 1, hi = ligands.length; j < hi; j++) {
            ILigand ligand = ligands[j];
            int i = j - 1;
            int cmp = 0;
            while ((i >= 0) && (cmp = cipRule.compare(ligand, ligands[i])) > 0) {
                ligands[i + 1] = ligands[i--];
                swaps++;
            }
            if (cmp == 0) // identical entries
                return 0;
            ligands[i + 1] = ligand;
        }

        // odd (-1) or even (+1)
        return (swaps & 0x1) == 0x1 ? -1 : +1;
    }

    /**
     * Creates a ligancy for chirality around a single chiral atom, where the involved
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
    public static LigancyFourChirality defineLigancyFourChirality(IAtomContainer container, int chiralAtom,
            int ligand1, int ligand2, int ligand3, int ligand4, Stereo stereo) {
        int[] atomIndices = {ligand1, ligand2, ligand3, ligand4};
        VisitedAtoms visitedAtoms = new VisitedAtoms();
        ILigand[] ligands = new ILigand[4];
        for (int i = 0; i < 4; i++) {
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
     * @param visitedAtoms a list of atoms already visited in the analysis
     * @param chiralAtom an integer pointing to the {@link IAtom} index of the chiral atom
     * @param ligandAtom an integer pointing to the {@link IAtom} index of the {@link ILigand}
     * @return           the created {@link ILigand}
     */
    public static ILigand defineLigand(IAtomContainer container, VisitedAtoms visitedAtoms, int chiralAtom,
            int ligandAtom) {
        if (ligandAtom == HYDROGEN) {
            return new ImplicitHydrogenLigand(container, visitedAtoms, container.getAtom(chiralAtom));
        } else {
            return new Ligand(container, visitedAtoms, container.getAtom(chiralAtom), container.getAtom(ligandAtom));
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
                    for (int i = 1; i <= duplication; i++) {
                        ligands.add(new TerminalLigand(container, visitedAtoms, ligandAtom, centralAtom));
                    }
                }
            } else {
                int duplication = getDuplication(bond.getOrder());
                IAtom connectedAtom = bond.getConnectedAtom(ligandAtom);
                if (visitedAtoms.isVisited(connectedAtom)) {
                    ligands.add(new TerminalLigand(container, visitedAtoms, ligandAtom, connectedAtom));
                } else {
                    ligands.add(new Ligand(container, visitedAtoms, ligandAtom, connectedAtom));
                }
                for (int i = 2; i <= duplication; i++) {
                    ligands.add(new TerminalLigand(container, visitedAtoms, ligandAtom, connectedAtom));
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
        return order == null ? 0 : order.numeric();
    }
}
