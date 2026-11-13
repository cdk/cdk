/*  Copyright (C) 2004-2007  Rajarshi Guha <rajarshi@users.sourceforge.net>
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.qsar.descriptors.molecular;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.isomorphism.Pattern;
import org.openscience.cdk.qsar.AtomValenceTool;
import org.openscience.cdk.smarts.SmartsPattern;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods for chi index calculations.
 * 
 * These methods are common to all the types of chi index calculations and can
 * be used to evaluate path, path-cluster, cluster and chain chi indices.
 *
 * @author     Rajarshi Guha
 */
class ChiIndexUtils {

    /**
     * Creates the patterns of fragment queries given as SMARTS, e.g. "*~*~*" for a path of three atoms. A pattern
     * can be matched by several threads at the same time, so the patterns can be created once and reused.
     *
     * @param smarts the queries; only any atom ('*') and any bond ('~') are expected
     * @return the patterns, in the order of the queries
     */
    static Pattern[] createPatterns(String... smarts) {
        Pattern[] patterns = new Pattern[smarts.length];
        for (int i = 0; i < smarts.length; i++) {
            // any atom/any bond queries need no ring or aromaticity perception of the target
            patterns[i] = SmartsPattern.create(smarts[i]).setPrepare(false);
        }
        return patterns;
    }

    /**
     * Gets the fragments from a target matching the given patterns, see {@link #createPatterns(String...)}.
     * Every set of bonds that matches a pattern is one fragment.
     *
     * @param atomContainer The target <code>AtomContainer</code>
     * @param patterns      The patterns of the fragment queries
     * @return A list of lists, each list being the atoms that match a query fragment
     */
    static List<List<Integer>> getFragments(IAtomContainer atomContainer, Pattern[] patterns) {
        List<List<Integer>> fragments = new ArrayList<>();
        for (Pattern pattern : patterns) {
            for (int[] mapping : pattern.matchAll(atomContainer).uniqueBonds()) {
                List<Integer> fragment = new ArrayList<>(mapping.length);
                for (int atomIndex : mapping)
                    fragment.add(atomIndex);
                fragments.add(fragment);
            }
        }
        return fragments;
    }

    /**
     * Evaluates the simple chi index for a set of fragments.
     *
     * @param atomContainer The target <code>AtomContainer</code>
     * @param fragLists      A list of fragments
     * @return The simple chi index
     */
    public static double evalSimpleIndex(IAtomContainer atomContainer, List<List<Integer>> fragLists) {
        double sum = 0;
        for (List<Integer> fragList : fragLists) {
            double prod = 1.0;
            for (Integer atomSerial : fragList) {
                IAtom atom = atomContainer.getAtom(atomSerial);
                int nconnected = atomContainer.getConnectedBondsCount(atom);
                prod = prod * nconnected;
            }
            if (prod != 0) sum += 1.0 / Math.sqrt(prod);
        }
        return sum;
    }

    /**
     * Evaluates the valence corrected chi index for a set of fragments.
     * 
     * This method takes into account the S and P atom types described in
     * Kier & Hall (1986), page 20 for which empirical delta V values are used.
     *
     * @param atomContainer The target <code>AtomContainer</code>
     * @param fragList      A list of fragments
     * @return The valence corrected chi index
     * @throws CDKException if no valence is defined in AtomValenceTool for an atom of a fragment, i.e. for an
     *                      unsupported element or a pseudo atom
     */
    public static double evalValenceIndex(IAtomContainer atomContainer, List<List<Integer>> fragList) throws CDKException {

        double sum = 0;
        for (List<Integer> aFragList : fragList) {
            List<Integer> frag = aFragList;
            double prod = 1.0;
            for (Object aFrag : frag) {
                int atomSerial = (Integer) aFrag;
                IAtom atom = atomContainer.getAtom(atomSerial);

                String sym = atom.getSymbol();

                if (sym.equals("S")) { // check for some special S environments
                    double tmp = deltavSulphur(atom, atomContainer);
                    if (tmp != -1) {
                        prod = prod * tmp;
                        continue;
                    }
                }
                if (sym.equals("P")) { // check for some special P environments
                    double tmp = deltavPhosphorous(atom, atomContainer);
                    if (tmp != -1) {
                        prod = prod * tmp;
                        continue;
                    }
                }

                int z = atom.getAtomicNumber();

                // TODO there should be a neater way to get the valence electron count
                int zv = getValenceElectronCount(atom);

                int hsupp = atom.getImplicitHydrogenCount();
                double deltav = (double) (zv - hsupp) / (double) (z - zv - 1);

                prod = prod * deltav;
            }
            if (prod != 0) sum += 1.0 / Math.sqrt(prod);
        }
        return sum;
    }

    private static int getValenceElectronCount(IAtom atom) throws CDKException {
        if (!AtomValenceTool.hasValence(atom))
            throw new CDKException(noValenceMessage(atom));
        int valency = AtomValenceTool.getValence(atom);
        return valency - atom.getFormalCharge();
    }

    /**
     * Returns the message for an atom without a valence in AtomValenceTool. Pseudo atoms (e.g. '*' or R groups) are
     * named as such, because their symbol 'R' is no element.
     *
     * @param atom the atom without a valence
     * @return the message
     */
    private static String noValenceMessage(IAtom atom) {
        if (atom instanceof IPseudoAtom || atom.getAtomicNumber() == null || atom.getAtomicNumber() == 0) {
            String label = atom instanceof IPseudoAtom ? ((IPseudoAtom) atom).getLabel() : null;
            return "No valence defined for pseudo atom " + (label != null ? label : atom.getSymbol());
        }
        return "No valence defined for element " + atom.getSymbol();
    }

    /**
     * Evaluates the empirical delt V for some S environments.
     * 
     * The method checks to see whether a S atom is in a -S-S-,
     * -SO-, -SO2- group and returns the empirical values noted
     * in Kier & Hall (1986), page 20.
     *
     * @param atom          The S atom in question
     * @param atomContainer The molecule containing the S
     * @return The empirical delta V if it is present in one of the above
     *         environments, -1 otherwise
     */
    protected static double deltavSulphur(IAtom atom, IAtomContainer atomContainer) {
        if (atom.getAtomicNumber() != IElement.S) return -1;

        // check whether it's a S in S-S
        List<IAtom> connected = atomContainer.getConnectedAtomsList(atom);
        for (IAtom connectedAtom : connected) {
            if (connectedAtom.getAtomicNumber() == IElement.S
                    && atomContainer.getBond(atom, connectedAtom).getOrder() == IBond.Order.SINGLE) return .89;
        }

        int count = 0;
        for (IAtom connectedAtom : connected) {
            if (connectedAtom.getAtomicNumber() == IElement.O
                    && atomContainer.getBond(atom, connectedAtom).getOrder() == IBond.Order.DOUBLE) count++;
        }
        if (count == 1)
            return 1.33; // check whether it's a S in -SO-
        else if (count == 2) return 2.67; // check whether it's a S in -SO2-

        return -1;
    }

    /**
     * Checks whether the P atom is in a PO environment.
     * 
     * This environment is noted in Kier & Hall (1986), page 20
     *
     * @param atom          The P atom in question
     * @param atomContainer The molecule containing the P atom
     * @return The empirical delta V if present in the above environment,
     *         -1 otherwise
     */
    private static double deltavPhosphorous(IAtom atom, IAtomContainer atomContainer) {
        if (atom.getAtomicNumber() != IElement.P) return -1;

        List<IAtom> connected = atomContainer.getConnectedAtomsList(atom);
        int conditions = 0;

        if (connected.size() == 4) conditions++;

        for (IAtom connectedAtom : connected) {
            if (connectedAtom.getAtomicNumber() == IElement.O
                    && atomContainer.getBond(atom, connectedAtom).getOrder() == IBond.Order.DOUBLE) conditions++;
            if (atomContainer.getBond(atom, connectedAtom).getOrder() == IBond.Order.SINGLE) conditions++;
        }
        if (conditions == 5) return 2.22;
        return -1;
    }
}
