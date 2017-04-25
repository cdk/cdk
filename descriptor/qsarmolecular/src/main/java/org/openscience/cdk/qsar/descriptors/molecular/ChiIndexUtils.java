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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.openscience.cdk.config.Isotopes;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.mcss.RMap;
import org.openscience.cdk.qsar.AtomValenceTool;

/**
 * Utility methods for chi index calculations.
 * 
 * These methods are common to all the types of chi index calculations and can
 * be used to evaluate path, path-cluster, cluster and chain chi indices.
 *
 * @author     Rajarshi Guha
 * @cdk.module qsarmolecular
 * @cdk.githash
 */
class ChiIndexUtils {

    /**
     * Gets the fragments from a target <code>AtomContainer</code> matching a set of query fragments.
     * 
     * This method returns a list of lists. Each list contains the atoms of the target <code>AtomContainer</code>
     * that arise in the mapping of bonds in the target molecule to the bonds in the query fragment.
     * The query fragments should be constructed
     * using the <code>createAnyAtomAnyBondContainer</code> method of the <code>QueryAtomContainerCreator</code>
     * CDK class, since we are only interested in connectivity and not actual atom or bond type information.
     *
     * @param atomContainer The target <code>AtomContainer</code>
     * @param queries       An array of query fragments
     * @return A list of lists, each list being the atoms that match the query fragments
     */
    public static List<List<Integer>> getFragments(IAtomContainer atomContainer, QueryAtomContainer[] queries) {
        UniversalIsomorphismTester universalIsomorphismTester = new UniversalIsomorphismTester();
        List<List<Integer>> uniqueSubgraphs = new ArrayList<List<Integer>>();
        for (QueryAtomContainer query : queries) {
            List<List<RMap>> subgraphMaps = null;
            try {
                // we get the list of bond mappings
                subgraphMaps = universalIsomorphismTester.getSubgraphMaps(atomContainer, query);
            } catch (CDKException e) {
                e.printStackTrace();
            }
            if (subgraphMaps == null) continue;
            if (subgraphMaps.size() == 0) continue;

            // get the atom paths in the unique set of bond maps
            uniqueSubgraphs.addAll(getUniqueBondSubgraphs(subgraphMaps, atomContainer));
        }

        // lets run a check on the length of each returned fragment and delete
        // any that don't match the length of out query fragments. Note that since
        // sometimes a fragment might be a ring, it will have number of atoms
        // equal to the number of bonds, where as a fragment with no rings
        // will have number of atoms equal to the number of bonds+1. So we need to check
        // fragment size against all unique query sizes - I get lazy and don't check
        // unique query sizes, but the size of each query
        List<List<Integer>> retValue = new ArrayList<List<Integer>>();
        for (List<Integer> fragment : uniqueSubgraphs) {
            for (QueryAtomContainer query : queries) {
                if (fragment.size() == query.getAtomCount()) {
                    retValue.add(fragment);
                    break;
                }
            }
        }
        return retValue;
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
                int nconnected = atomContainer.getConnectedAtomsCount(atom);
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
     * @throws CDKException if the <code>IsotopeFactory</code> cannot be created
     */
    public static double evalValenceIndex(IAtomContainer atomContainer, List<List<Integer>> fragList) throws CDKException {
        try {
            IsotopeFactory ifac = Isotopes.getInstance();
            ifac.configureAtoms(atomContainer);
        } catch (IOException e) {
            throw new CDKException("IO problem occurred when using the CDK atom config\n" + e.getMessage(), e);
        }
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

    private static int getValenceElectronCount(IAtom atom) {
        int valency = AtomValenceTool.getValence(atom);
        return valency - atom.getFormalCharge();
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
        if (!atom.getSymbol().equals("S")) return -1;

        // check whether it's a S in S-S
        List<IAtom> connected = atomContainer.getConnectedAtomsList(atom);
        for (IAtom connectedAtom : connected) {
            if (connectedAtom.getSymbol().equals("S")
                    && atomContainer.getBond(atom, connectedAtom).getOrder() == IBond.Order.SINGLE) return .89;
        }

        int count = 0;
        for (IAtom connectedAtom : connected) {
            if (connectedAtom.getSymbol().equals("O")
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
        if (!atom.getSymbol().equals("P")) return -1;

        List<IAtom> connected = atomContainer.getConnectedAtomsList(atom);
        int conditions = 0;

        if (connected.size() == 4) conditions++;

        for (IAtom connectedAtom : connected) {
            if (connectedAtom.getSymbol().equals("O")
                    && atomContainer.getBond(atom, connectedAtom).getOrder() == IBond.Order.DOUBLE) conditions++;
            if (atomContainer.getBond(atom, connectedAtom).getOrder() == IBond.Order.SINGLE) conditions++;
        }
        if (conditions == 5) return 2.22;
        return -1;
    }

    /**
     * Converts a set of bond mappings to a unique set of atom paths.
     * 
     * This method accepts a <code>List</code> of bond mappings. It first
     * reduces the set to a unique set of bond maps and then for each bond map
     * converts it to a series of atoms making up the bonds.
     *
     * @param subgraphs A <code>List</code> of bon mappings
     * @param ac        The molecule we are examining
     * @return A unique <code>List</code> of atom paths
     */
    private static List<List<Integer>> getUniqueBondSubgraphs(List<List<RMap>> subgraphs, IAtomContainer ac) {
        List<List<Integer>> bondList = new ArrayList<List<Integer>>();
        for (List<RMap> subgraph : subgraphs) {
            List<RMap> current = subgraph;
            List<Integer> ids = new ArrayList<Integer>();
            for (RMap aCurrent : current) {
                RMap rmap = (RMap) aCurrent;
                ids.add(rmap.getId1());
            }
            Collections.sort(ids);
            bondList.add(ids);
        }

        // get the unique set of bonds
        HashSet<List<Integer>> hs = new HashSet<List<Integer>>(bondList);
        bondList = new ArrayList<List<Integer>>(hs);

        List<List<Integer>> paths = new ArrayList<List<Integer>>();
        for (List<Integer> aBondList1 : bondList) {
            List<Integer> aBondList = aBondList1;
            List<Integer> tmp = new ArrayList<Integer>();
            for (Object anABondList : aBondList) {
                int bondNumber = (Integer) anABondList;
                for (IAtom atom : ac.getBond(bondNumber).atoms()) {
                    Integer atomInt = ac.indexOf(atom);
                    if (!tmp.contains(atomInt)) tmp.add(atomInt);
                }
            }
            paths.add(tmp);
        }
        return paths;
    }

}
