package org.openscience.cdk.pharmacophore;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ConformerContainer;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.isomorphism.mcss.RMap;
import org.openscience.cdk.smiles.smarts.SMARTSQueryTool;
import org.openscience.cdk.tools.LoggingTool;

import javax.vecmath.Point3d;
import java.util.*;

/**
 * Identifies atoms whose 3D arrangement matches a specified pharmacophore query.
 * <p/>
 * A pharmacophore is defined by a set of atoms and distances between them. More generically
 * we can restate this as a set of pharmacophore groups and the distances between them. Note
 * that a pharmacophore group may consist of one or more atoms and the distances can be
 * specified as a distance range rather than an exact distance.
 * <p/>
 * The goal of a pharmacophore query is to identify atom in a molecule whose 3D arrangement
 * match a specified query.
 * <p/>
 * To perform a query one must first create a set of pharmacophore groups and specify the
 * distances between them. Each pharmacophore group is represented by a {@link org.openscience.cdk.pharmacophore.PharmacophoreAtom}
 * and the distances between them are represented by a {@link org.openscience.cdk.pharmacophore.PharmacophoreBond}.
 * These are collected in a {@link org.openscience.cdk.isomorphism.matchers.QueryAtomContainer}.
 * <p/>
 * Given the query pharmacophore one can use this class to check with it occurs in a specified molecule.
 * Note that for full generality pharmacophore searches are performed using conformations of molecules.
 * This can easily be accomplished using this class together with the {@link org.openscience.cdk.ConformerContainer}
 * class. See {@link #matches(org.openscience.cdk.ConformerContainer)}.
 * <p/>
 * Currently this class will allow you to perform pharmacophore searches using triads, quads or any number
 * of pharmacophore groups. However, only distances between pharmacophore groups are considered, so
 * alternative constraints such as torsions and so on cannot be considered at this point.
 * <p/>
 * After a query has been performed one can retrieve the matching groups (as opposed to the matching atoms
 * of the target molecule). However since a pharmacophore group (which is an object of class {@link PharmacophoreAtom})
 * allows you to access the indices of the corresponding atoms in the target molecule, this is not very
 * difficult.
 * Example usage:
 * <pre>
 * QueryAtomContainer query = new QueryAtomContainer();
 * <p/>
 * PharmacophoreQueryAtom o = new PharmacophoreQueryAtom("D", "[OX1]");
 * PharmacophoreQueryAtom n1 = new PharmacophoreQueryAtom("A", "[N]");
 * PharmacophoreQueryAtom n2 = new PharmacophoreQueryAtom("A", "[N]");
 * <p/>
 * query.addAtom(o);
 * query.addAtom(n1);
 * query.addAtom(n2);
 * <p/>
 * PharmacophoreQueryBond b1 = new PharmacophoreQueryBond(o, n1, 4.0, 4.5);
 * PharmacophoreQueryBond b2 = new PharmacophoreQueryBond(o, n2, 4.0, 5.0);
 * PharmacophoreQueryBond b3 = new PharmacophoreQueryBond(n1, n2, 5.4, 5.8);
 * <p/>
 * query.addBond(b1);
 * query.addBond(b2);
 * query.addBond(b3);
 * <p/>
 * String filename = "/Users/rguha/pcore1.sdf";
 * IteratingMDLConformerReader reader = new IteratingMDLConformerReader(
 *      new FileReader(new File(filename)), DefaultChemObjectBuilder.getInstance());
 * <p/>
 * ConformerContainer cc;
 * if (reader.hasNext()) cc = (ConformerContainer) reader.next();
 * <p/>
 * PharmacophoreMatcher matcher = new PharmacophoreMatcher(query);
 * boolean[] statuses = matcher.matches(cc);
 * </pre>
 *
 * @author Rajarshi Guha
 * @cdk.module pcore
 * @cdk.svnrev $Revision: 9162 $
 * @cdk.keyword pharmacophore
 * @cdk.keyword 3D isomorphism
 * @see org.openscience.cdk.pharmacophore.PharmacophoreAtom
 * @see org.openscience.cdk.pharmacophore.PharmacophoreBond
 * @see org.openscience.cdk.pharmacophore.PharmacophoreQueryAtom
 * @see org.openscience.cdk.pharmacophore.PharmacophoreQueryBond
 */
public class PharmacophoreMatcher {
    private LoggingTool logger = new LoggingTool(PharmacophoreMatcher.class);
    private IQueryAtomContainer pharmacophoreQuery = null;
    private List<List<PharmacophoreAtom>> matchingPAtoms = null;

    /**
     * An empty constructor.
     * <p/>
     * You should set the query before performing a match
     */
    public PharmacophoreMatcher() {
    }

    /**
     * Initialize the matcher with a query.
     *
     * @param pharmacophoreQuery The query pharmacophore
     * @see org.openscience.cdk.pharmacophore.PharmacophoreQueryAtom
     * @see org.openscience.cdk.pharmacophore.PharmacophoreQueryBond
     */
    public PharmacophoreMatcher(IQueryAtomContainer pharmacophoreQuery) {
        this.pharmacophoreQuery = pharmacophoreQuery;
    }

    /**
     * Performs the pharmacophore matching.
     *
     * @param atomContainer The target molecule. Must have 3D coordinates
     * @return true is the target molecule contains the query pharmacophore
     * @throws org.openscience.cdk.exception.CDKException
     *          if the query pharmacophore was not set or the query is invalid or if the molecule
     *          does not have 3D coordinates
     * @see #matches(org.openscience.cdk.ConformerContainer)
     */
    @TestMethod("org.openscience.cdk.test.pharmacophore.PharmacophoreMatcherTest#testCNSPcore")
    public boolean matches(IAtomContainer atomContainer) throws CDKException {
        if (!GeometryTools.has3DCoordinates(atomContainer)) throw new CDKException("Molecule must have 3D coordinates");
        if (pharmacophoreQuery == null) throw new CDKException("Must set the query pharmacophore before matching");
        if (!checkQuery(pharmacophoreQuery))
            throw new CDKException("A problem in the query. Make sure all pharmacophore groups of the same symbol have the same same SMARTS");
        String title = (String) atomContainer.getProperty(CDKConstants.TITLE);

        IAtomContainer pharmacophoreMolecule = getPharmacophoreMolecule(atomContainer);
        if (pharmacophoreMolecule.getAtomCount() < pharmacophoreQuery.getAtomCount()) {
            logger.debug("Target [" + title + "] did not match the query SMARTS. Skipping constraints");
            return false;
        }

        List bondMapping = UniversalIsomorphismTester.getSubgraphMaps(pharmacophoreMolecule, pharmacophoreQuery);
        matchingPAtoms = getAtomMappings(bondMapping, pharmacophoreMolecule);
        logger.debug("  Got " + matchingPAtoms.size() + " hits");

        return matchingPAtoms.size() > 0;
    }


    /**
     * Performs pharmacophore matching for a group of conformers.
     * <p/>
     * Though you can use the other version of this method, this form is much
     * more efficient when performing matching against a collection of conformers
     * for a given molecule.
     *
     * @param conformerContainer The conformers to match against
     * @return An array of boolens. If the i'th element is true it indicates that the i'th conformer
     *         contained the pharmacophore query
     * @throws CDKException if the query pharmacophore was not set or the query is invalid
     * @see #matches(org.openscience.cdk.interfaces.IAtomContainer)
     * @see org.openscience.cdk.ConformerContainer
     */
    @TestMethod("org.openscience.cdk.test.pharmacophore.PharmacophoreMatcherTest#testMatcherQuery1")
    public boolean[] matches(ConformerContainer conformerContainer) throws CDKException {
        if (conformerContainer.size() == 0) throw new CDKException("Must supply at least 1 conformer");
        if (pharmacophoreQuery == null) throw new CDKException("Must set the query pharmacophore before matching");
        if (!checkQuery(pharmacophoreQuery))
            throw new CDKException("A problem in the query. Make sure all pharmacophore groups of the same symbol have the same same SMARTS");

        String title = conformerContainer.getTitle();
        boolean[] ret = new boolean[conformerContainer.size()];
        for (int i = 0; i < conformerContainer.size(); i++) ret[i] = false;

        IAtomContainer pharmacophoreMolecule = getPharmacophoreMolecule(conformerContainer.get(0));
        if (pharmacophoreMolecule.getAtomCount() < pharmacophoreQuery.getAtomCount()) {
            logger.debug("Target [" + title + "] did not match the query SMARTS. Skipping constraints");
            return ret;
        }

        int i = 0;
        for (IAtomContainer conf : conformerContainer) {

            // copy the coordinates from this container into the pcore molecule
            Iterator patoms = pharmacophoreMolecule.atoms();
            while (patoms.hasNext()) {
                PharmacophoreAtom patom = (PharmacophoreAtom) patoms.next();
                int[] indices = patom.getMatchingAtoms();
                Point3d tmp = new Point3d(0, 0, 0);
                for (int index : indices) {
                    Point3d coord = conf.getAtom(index).getPoint3d();
                    tmp.x += coord.x;
                    tmp.y += coord.y;
                    tmp.z += coord.z;
                }
                tmp.x /= indices.length;
                tmp.y /= indices.length;
                tmp.z /= indices.length;
                patom.setPoint3d(tmp);
            }

            // now do a match with these coordinates
            List bondMapping = UniversalIsomorphismTester.getSubgraphMaps(pharmacophoreMolecule, pharmacophoreQuery);
            List<List<PharmacophoreAtom>> tmp = getAtomMappings(bondMapping, pharmacophoreMolecule);
            ret[i++] = tmp.size() > 0;

            logger.debug("  Conformer got " + tmp.size() + " hits");

            if (matchingPAtoms == null && tmp.size() > 0) {
                matchingPAtoms = new ArrayList<List<PharmacophoreAtom>>(tmp);
            }

        }

        return ret;
    }

    /**
     * Get the matching pharmacophore groups.
     * <p/>
     * The method should be called after performing the match, otherwise the return value is null.
     * The method returns a List of List's. Each List represents the pharmacophore groups in the
     * target molecule that matched the query. Each pharmacophore group contains the indices of the
     * atoms (in the target molecule) that correspond to the group.
     *
     * @return a List of a List of pharmacophore groups in the target molecule that match the query
     * @see org.openscience.cdk.pharmacophore.PharmacophoreAtom
     */
    @TestMethod("org.openscience.cdk.test.pharmacophore.PharmacophoreMatcherTest#testMatchedAtoms")
    public List<List<PharmacophoreAtom>> getMatchingPharmacophoreAtoms() {
        return matchingPAtoms;
    }

    /**
     * Get the uniue matching pharmacophore groups.
     * <p/>
     * The method should be called after performing the match, otherwise the return value is null.
     * The method returns a List of List's. Each List represents the pharmacophore groups in the
     * target molecule that matched the query. Each pharmacophore group contains the indices of the
     * atoms (in the target molecule) that correspond to the group.
     * <p/>
     * This is analogous to the USA form of return value from a SMARTS match.
     *
     * @return a List of a List of pharmacophore groups in the target molecule that match the query
     * @see org.openscience.cdk.pharmacophore.PharmacophoreAtom
     */
    @TestMethod("org.openscience.cdk.test.pharmacophore.PharmacophoreMatcherTest#testMatchedAtoms")
    public List<List<PharmacophoreAtom>> getUniqueMatchingPharmacophoreAtoms() {
        List<List<PharmacophoreAtom>> ret = new ArrayList<List<PharmacophoreAtom>>();

        List<String> tmp = new ArrayList<String>();
        for (List<PharmacophoreAtom> pmatch : matchingPAtoms) {
            List<Integer> ilist = new ArrayList<Integer>();
            for (PharmacophoreAtom patom : pmatch) {
                int[] indices = patom.getMatchingAtoms();
                for (int i : indices) {
                    if (!ilist.contains(i)) ilist.add(i);
                }
            }
            Collections.sort(ilist);

            // convert the list of ints to a string
            String s = "";
            for (int i : ilist) s += i;
            tmp.add(s);
        }

        // now we go through our integer list and see if we can get rid of duplicates
        List<String> utmp = new ArrayList<String>();
        for (int i = 0; i < tmp.size(); i++) {
            String ilist = tmp.get(i);
            if (!utmp.contains(ilist)) {
                utmp.add(ilist);
                ret.add(matchingPAtoms.get(i));
            }
        }
        return ret;
    }

    /**
     * Get the query pharmacophore
     *
     * @return The query
     */
    @TestMethod("org.openscience.cdk.test.pharmacophore.PharmacophoreMatcherTest#testGetterSetter")
    public IQueryAtomContainer getPharmacophoreQuery() {
        return pharmacophoreQuery;
    }

    /**
     * Set a pharmacophore query
     *
     * @param query The query
     */
    @TestMethod("org.openscience.cdk.test.pharmacophore.PharmacophoreMatcherTest#testGetterSetter")
    public void setPharmacophoreQuery(IQueryAtomContainer query) {
        pharmacophoreQuery = query;
    }

    private IAtomContainer getPharmacophoreMolecule(IAtomContainer atomContainer) throws CDKException {

        SMARTSQueryTool sqt = new SMARTSQueryTool("C", true);
        IAtomContainer pharmacophoreMolecule = DefaultChemObjectBuilder.getInstance().newAtomContainer();

        // lets loop over each pcore query atom
        HashMap<String, String> map = new HashMap<String, String>();

        logger.debug("Converting [" + atomContainer.getProperty(CDKConstants.TITLE) + "] to a pcore molecule");

        Iterator qatoms = pharmacophoreQuery.atoms();
        while (qatoms.hasNext()) {
            PharmacophoreQueryAtom qatom = (PharmacophoreQueryAtom) qatoms.next();
            String smarts = qatom.getSmarts();

            // a pcore query might have multiple instances of a given pcore atom (say
            // 2 hydrophobic groups separated by X unit). In such a case we want to find
            // the atoms matching the pgroup SMARTS just once, rather than redoing the
            // matching for each instance of the pcore query atom.
            if (!map.containsKey(qatom.getSymbol())) map.put(qatom.getSymbol(), smarts);
            else if (map.get(qatom.getSymbol()).equals(smarts)) {
                continue;
            }

            // see if the smarts for this pcore query atom gets any matches
            // in our query molecule. If so, then cllect each set of
            // matching atoms and for each set make a new pcore atom and
            // add it to the pcore atom container object
            sqt.setSmarts(smarts);
            if (sqt.matches(atomContainer)) {
                List<List<Integer>> mappings = sqt.getUniqueMatchingAtoms();
                for (int i = 0; i < mappings.size(); i++) {
                    List<Integer> atomIndices = mappings.get(i);
                    Point3d coords = getEffectiveCoordinates(atomContainer, atomIndices);
                    PharmacophoreAtom patom = new PharmacophoreAtom(smarts, qatom.getSymbol(), coords);
                    patom.setMatchingAtoms(intIndices(atomIndices));
                    if (!pharmacophoreMolecule.contains(patom)) pharmacophoreMolecule.addAtom(patom);
                }
            }
            logger.debug("\tFound " + sqt.getUniqueMatchingAtoms().size() + " unique matches for " + smarts);
        }

        // now that we have added all the pcore atoms to the container
        // we need to join all atoms with pcore bonds
        int npatom = pharmacophoreMolecule.getAtomCount();
        for (int i = 0; i < npatom - 1; i++) {
            for (int j = i + 1; j < npatom; j++) {
                PharmacophoreAtom atom1 = (PharmacophoreAtom) pharmacophoreMolecule.getAtom(i);
                PharmacophoreAtom atom2 = (PharmacophoreAtom) pharmacophoreMolecule.getAtom(j);
                PharmacophoreBond bond = new PharmacophoreBond(atom1, atom2);
                pharmacophoreMolecule.addBond(bond);
            }
        }

        return pharmacophoreMolecule;
    }

    private int[] intIndices(List<Integer> atomIndices) {
        int[] ret = new int[atomIndices.size()];
        for (int i = 0; i < atomIndices.size(); i++) ret[i] = atomIndices.get(i);
        return ret;
    }

    private Point3d getEffectiveCoordinates(IAtomContainer atomContainer, List<Integer> atomIndices) {
        Point3d ret = new Point3d(0, 0, 0);
        for (Object atomIndice : atomIndices) {
            int atomIndex = (Integer) atomIndice;
            Point3d coord = atomContainer.getAtom(atomIndex).getPoint3d();
            ret.x += coord.x;
            ret.y += coord.y;
            ret.z += coord.z;
        }
        ret.x /= atomIndices.size();
        ret.y /= atomIndices.size();
        ret.z /= atomIndices.size();
        return ret;
    }

    private List<List<PharmacophoreAtom>> getAtomMappings(List bondMapping, IAtomContainer atomContainer) {
        List<List<PharmacophoreAtom>> atomMapping = new ArrayList<List<PharmacophoreAtom>>();

        // loop over each mapping
        for (Object aBondMapping : bondMapping) {
            List list = (List) aBondMapping;

            List<Integer> tmp = new ArrayList<Integer>();
            List<PharmacophoreAtom> atomList = new ArrayList<PharmacophoreAtom>();

            // loop over this mapping
            for (Object aList : list) {
                RMap map = (RMap) aList;
                int bondID = map.getId1();

                // get the atoms in this bond
                IBond bond = atomContainer.getBond(bondID);
                IAtom atom1 = bond.getAtom(0);
                IAtom atom2 = bond.getAtom(1);

                Integer idx1 = atomContainer.getAtomNumber(atom1);
                Integer idx2 = atomContainer.getAtomNumber(atom2);

                if (!tmp.contains(idx1)) {
                    tmp.add(idx1);
                    atomList.add(new PharmacophoreAtom((PharmacophoreAtom) atom1));
                }
                if (!tmp.contains(idx2)) {
                    tmp.add(idx2);
                    atomList.add(new PharmacophoreAtom((PharmacophoreAtom) atom2));
                }
            }
            if (tmp.size() > 0) atomMapping.add(atomList);
        }
        return atomMapping;
    }

    private boolean checkQuery(IQueryAtomContainer query) {
        HashMap<String, String> map = new HashMap<String, String>();
        for (int i = 0; i < query.getAtomCount(); i++) {
            IQueryAtom atom = (IQueryAtom) query.getAtom(i);
            if (!(atom instanceof PharmacophoreQueryAtom)) return false;

            PharmacophoreQueryAtom pqatom = (PharmacophoreQueryAtom) atom;
            String label = pqatom.getSymbol();
            String smarts = pqatom.getSmarts();

            if (!map.containsKey(label)) map.put(label, smarts);
            else {
                if (!map.get(label).equals(smarts)) return false;
            }
        }
        return true;
    }

}
