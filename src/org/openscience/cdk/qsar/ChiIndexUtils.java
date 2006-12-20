package org.openscience.cdk.qsar;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.mcss.RMap;
import org.openscience.cdk.qsar.descriptors.atomic.AtomValenceDescriptor;

import java.io.IOException;
import java.util.*;

/**
 * Utility methods for chi index calculations.
 * <p/>
 * These methods are common to all the types of chi index calculations and can
 * be used to evaluate path, path-cluster, cluster and chain chi indices.
 *
 * @author Rajarshi Guha
 * @cdk.module qsar
 */
public class ChiIndexUtils {

    /**
     * Gets the fragments from a target <code>AtomContainer</code> matching a set of query fragments.
     * <p/>
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
    public static List getFragments(IAtomContainer atomContainer, QueryAtomContainer[] queries) {
        List uniqueSubgraphs = new ArrayList();
        for (int i = 0; i < queries.length; i++) {
            List subgraphMaps = null;
            try {
                // we get the list of bond mappings
                subgraphMaps = UniversalIsomorphismTester.getSubgraphMaps(atomContainer, queries[i]);
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
        ArrayList retValue = new ArrayList();
        for (Iterator iter = uniqueSubgraphs.iterator(); iter.hasNext();) {
            ArrayList fragment = (ArrayList) iter.next();
            for (int i = 0; i < queries.length; i++) {
                if (fragment.size() == queries[i].getAtomCount()) {
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
     * @param fragList      A list of fragments
     * @return The simple chi index
     */
    public static double evalSimpleIndex(IAtomContainer atomContainer, List fragList) {
        double sum = 0;
        for (int i = 0; i < fragList.size(); i++) {
            ArrayList frag = (ArrayList) fragList.get(i);
            double prod = 1.0;
            for (int j = 0; j < frag.size(); j++) {
                int atomSerial = ((Integer) frag.get(j)).intValue();
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
     * <p/>
     * This method takes into account the S and P atom types described in
     * Kier & Hall (1986), page 20 for which empirical delta V values are used.
     *
     * @param atomContainer The target <code>AtomContainer</code>
     * @param fragList      A list of fragments
     * @return The valence corrected chi index
     * @throws CDKException if the <code>IsotopeFactory</code> cannot be created
     */
    public static double evalValenceIndex(IAtomContainer atomContainer, List fragList) throws CDKException {
        try {
            IsotopeFactory ifac = IsotopeFactory.getInstance(DefaultChemObjectBuilder.getInstance());
            ifac.configureAtoms(atomContainer);
        } catch (IOException e) {
            throw new CDKException("IO problem occured when using the CDK atom config");
        }
        double sum = 0;
        for (int i = 0; i < fragList.size(); i++) {
            ArrayList frag = (ArrayList) fragList.get(i);
            double prod = 1.0;
            for (int j = 0; j < frag.size(); j++) {
                int atomSerial = ((Integer) frag.get(j)).intValue();
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

                int hsupp = atom.getHydrogenCount();
                double deltav = (double) (zv - hsupp) / (double) (z - zv - 1);                               

                prod = prod * deltav;
            }
            if (prod != 0) sum += 1.0 / Math.sqrt(prod);
        }
        return sum;
    }

    // TODO there should be a neater way to get the valence electron count
    private static int getValenceElectronCount(IAtom atom) {
        Map valenceMap;
        AtomValenceDescriptor avd = new AtomValenceDescriptor();
        valenceMap = avd.valencesTable;
        int valency = ((Integer) valenceMap.get(atom.getSymbol())).intValue();
        return valency - atom.getFormalCharge();
    }


    /**
     * Evaluates the empirical delt V for some S environments.
     * <p/>
     * The method checks to see whether a S atom is in a -S-S-,
     * -SO-, -SO2- group and returns the empirical values noted
     * in Kier & Hall (1986), page 20.
     *
     * @param atom          The S atom in question
     * @param atomContainer The molecule containing the S
     * @return The empirical delta V if it is present in one of the above
     *         environments, -1 otherwise
     */
    private static double deltavSulphur(IAtom atom, IAtomContainer atomContainer) {
        if (!atom.getSymbol().equals("S")) return -1;

        // check whether it's a S in S-S
        List connected = atomContainer.getConnectedAtomsList(atom);
        for (int i = 0; i < connected.size(); i++) {
            IAtom connectedAtom = (IAtom) connected.get(i);
            if (connectedAtom.getSymbol().equals("S")
                    && atomContainer.getBond(atom, connectedAtom).getOrder() == 1.0)
                return .89;
        }

        // check whether it's a S in -SO-
        for (int i = 0; i < connected.size(); i++) {
            IAtom connectedAtom = (IAtom) connected.get(i);
            if (connectedAtom.getSymbol().equals("O")
                    && atomContainer.getBond(atom, connectedAtom).getOrder() == 2.0)
                return 1.33;
        }

        // check whether it's a S in -SO2-
        int count = 0;
        for (int i = 0; i < connected.size(); i++) {
            IAtom connectedAtom = (IAtom) connected.get(i);
            if (connectedAtom.getSymbol().equals("O")
                    && atomContainer.getBond(atom, connectedAtom).getOrder() == 2.0)
                count++;
        }
        if (count == 2) return 2.67;

        return -1;
    }

    /**
     * Checks whether the P atom is in a PO environment.
     * <p/>
     * This environment is noted in Kier & Hall (1986), page 20
     *
     * @param atom          The P atom in question
     * @param atomContainer The molecule containing the P atom
     * @return The empirical delta V if present in the above environment,
     *         -1 otherwise
     */
    private static double deltavPhosphorous(IAtom atom, IAtomContainer atomContainer) {
        if (!atom.getSymbol().equals("P")) return -1;

        List connected = atomContainer.getConnectedAtomsList(atom);
        int conditions = 0;

        if (connected.size() == 4) conditions++;

        for (int i = 0; i < connected.size(); i++) {
            IAtom connectedAtom = (IAtom) connected.get(i);
            if (connectedAtom.getSymbol().equals("O")
                    && atomContainer.getBond(atom, connectedAtom).getOrder() == 2.0)
                conditions++;
            if (atomContainer.getBond(atom, connectedAtom).getOrder() == 1.0)
                conditions++;
        }
        if (conditions == 5) return 2.22;
        return -1;
    }

    /**
     * Converts a set of bond mappings to a unique set of atom paths.
     * <p/>
     * This method accepts a <code>List</code> of bond mappings. It first
     * reduces the set to a unique set of bond maps and then for each bond map
     * converts it to a series of atoms making up the bonds.
     *
     * @param subgraphs A <code>List</code> of bon mappings
     * @param ac        The molecule we are examining
     * @return A unique <code>List</code> of atom paths
     */
    private static List getUniqueBondSubgraphs(List subgraphs, IAtomContainer ac) {
        ArrayList bondList = new ArrayList();
        for (int i = 0; i < subgraphs.size(); i++) {
            List current = (List) subgraphs.get(i);
            ArrayList ids = new ArrayList();
            for (Iterator iter = current.iterator(); iter.hasNext();) {
                RMap rmap = (RMap) iter.next();
                ids.add(new Integer(rmap.getId1()));
            }
            Collections.sort(ids);
            bondList.add(ids);
        }

        // get the unique set of bonds
        HashSet hs = new HashSet(bondList);
        bondList = new ArrayList(hs);

        List paths = new ArrayList();
        for (Iterator iter = bondList.iterator(); iter.hasNext();) {
            List aBondList = (List) iter.next();
            List tmp = new ArrayList();
            for (int i = 0; i < aBondList.size(); i++) {
                int bondNumber = ((Integer) aBondList.get(i)).intValue();
                Iterator atomIterator = ac.getBond(bondNumber).atoms();
                while (atomIterator.hasNext()) {
                    IAtom atom = (IAtom) atomIterator.next();
                    Integer atomInt = new Integer(ac.getAtomNumber(atom));
                    if (!tmp.contains(atomInt)) tmp.add(atomInt);
                }
            }
            paths.add(tmp);
        }
        return paths;
    }

}

