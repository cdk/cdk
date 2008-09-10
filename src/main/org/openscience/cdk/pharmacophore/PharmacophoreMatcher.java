/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004-2008  Rajarshi Guha <rajarshi.guha@gmail.com>
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
package org.openscience.cdk.pharmacophore;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.annotations.TestClass;
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
 * class.  See the example below.
 * <p/>
 * Currently this class will allow you to perform pharmacophore searches using triads, quads or any number
 * of pharmacophore groups. However, only distances and angles between pharmacophore groups are considered, so
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
 * ConformerContainer conformers;
 * if (reader.hasNext()) conformers = (ConformerContainer) reader.next();
 * <p/>
 * boolean firstTime = true;
 * for (IAtomContainer conf : conformers) {
 *   boolean status;
 *   if (firstTime) {
 *     status = matcher.matches(conf, true);
 *     firstTime = false;
 *   } else status = matcher.matches(conf, false);
 *   if (status) {
 *     // OK, matched. Do something
 *   }
 * }
 * </pre>
 *
 * <h3>Extensions to SMARTS</h3>
 *
 * The pharmacophore supports some extentions to the SMARTS language that lead
 * to flexible pharmacophore definitions  Note that these extensions are specific to
 * pharmacophore usage and are not generally provided by the SMARTS parser itself.
 * <p>
 * <ul>
 * <li> | - this allows one to perform a logical OR between two or more SMARTS patterns. An example might
 * be a pharmacophore group that is meant to match a 5 membered ring or a 6 membered ring. This cannot be
 * written in a single ordinary SMARTS pattern. However using this one extension one can write
 * <pre>A1AAAA1|A1AAAAA1</pre>
 * </ul>
 *
 * @author Rajarshi Guha
 * @cdk.module pcore
 * @cdk.svnrev $Revision$
 * @cdk.keyword pharmacophore
 * @cdk.keyword 3D isomorphism
 * @see org.openscience.cdk.pharmacophore.PharmacophoreAtom
 * @see org.openscience.cdk.pharmacophore.PharmacophoreBond
 * @see org.openscience.cdk.pharmacophore.PharmacophoreQueryAtom
 * @see org.openscience.cdk.pharmacophore.PharmacophoreQueryBond
 */
@TestClass("org.openscience.cdk.pharmacophore.PharmacophoreMatcherTest")
public class PharmacophoreMatcher {
    private LoggingTool logger = new LoggingTool(PharmacophoreMatcher.class);
    private IQueryAtomContainer pharmacophoreQuery = null;
    private List<List<PharmacophoreAtom>> matchingPAtoms = null;
    private List<List<IBond>> matchingPBonds = null;

    private List<List<RMap>> bondMapping;
    private IAtomContainer pharmacophoreMolecule = null;

    private List<HashMap<IBond, IBond>> bondMapHash = null;

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
     * <p/>
     * This method will analyze the specified target molecule to identify pharmacophore
     * groups. If dealing with conformer data it is probably more efficient to use
     * the other form of this method which allows one to skip the pharmacophore group
     * identification step after the first conformer.
     *
     * @param atomContainer The target molecule. Must have 3D coordinates
     * @return true is the target molecule contains the query pharmacophore
     * @throws org.openscience.cdk.exception.CDKException
     *          if the query pharmacophore was not set or the query is invalid or if the molecule
     *          does not have 3D coordinates
     * @see #matches(org.openscience.cdk.interfaces.IAtomContainer, boolean)
     */
    @TestMethod("testCNSPcore")
    public boolean matches(IAtomContainer atomContainer) throws CDKException {
        return matches(atomContainer, true);
    }

    /**
     * Performs the pharmacophore matching.
     *
     * @param atomContainer    The target molecule. Must have 3D coordinates
     * @param initializeTarget If <i>true</i>, the target molecule specified in the
     *                         first argument will be analyzed to identify matching pharmacophore groups. If <i>false</i>
     *                         this is not performed. The latter case is only useful when dealing with conformers
     *                         since for a given molecule, all conformers will have the same pharmacophore groups
     *                         and only the constraints will change from one conformer to another.
     * @return true is the target molecule contains the query pharmacophore
     * @throws org.openscience.cdk.exception.CDKException
     *          if the query pharmacophore was not set or the query is invalid or if the molecule
     *          does not have 3D coordinates
     */
    @TestMethod("testMatcherQuery1")
    public boolean matches(IAtomContainer atomContainer, boolean initializeTarget) throws CDKException {
        if (!GeometryTools.has3DCoordinates(atomContainer)) throw new CDKException("Molecule must have 3D coordinates");
        if (pharmacophoreQuery == null) throw new CDKException("Must set the query pharmacophore before matching");
        if (!checkQuery(pharmacophoreQuery))
            throw new CDKException("A problem in the query. Make sure all pharmacophore groups of the same symbol have the same same SMARTS");
        String title = (String) atomContainer.getProperty(CDKConstants.TITLE);

        if (initializeTarget) pharmacophoreMolecule = getPharmacophoreMolecule(atomContainer);
        else {
            // even though the atoms comprising the pcore groups are
            // constant, their coords will differ, so we need to make
            // sure we get the latest set of effective coordinates
            Iterator<IAtom> atoms = pharmacophoreMolecule.atoms().iterator();
            while (atoms.hasNext()) {
                PharmacophoreAtom patom = (PharmacophoreAtom) atoms.next();
                List<Integer> tmpList = new ArrayList<Integer>();
                for (int idx : patom.getMatchingAtoms()) tmpList.add(idx);
                Point3d coords = getEffectiveCoordinates(atomContainer, tmpList);
                patom.setPoint3d(coords);
            }
        }

        if (pharmacophoreMolecule.getAtomCount() < pharmacophoreQuery.getAtomCount()) {
            logger.debug("Target [" + title + "] did not match the query SMARTS. Skipping constraints");
            return false;
        }
        bondMapping = UniversalIsomorphismTester.getSubgraphMaps(pharmacophoreMolecule, pharmacophoreQuery);
        logger.debug("  Got " + bondMapping.size() + " hits");
        return bondMapping.size() > 0;
    }


    /**
     * Get the matching pharmacophore constraints.
     * <p/>
     * The method should be called after performing the match, otherwise the return value is null.
     * The method returns a List of List's. Each List represents the pharmacophore constraints in the
     * target molecule that matched the query. Since constraints are conceptually modeled on bonds
     * the result is a list of list of IBond. You should coerce these to the appropriate pharmacophore
     * bond to get at the underlying grops.
     *
     * @return a List of a List of pharmacophore constraints in the target molecule that match the query
     * @see org.openscience.cdk.pharmacophore.PharmacophoreBond
     * @see org.openscience.cdk.pharmacophore.PharmacophoreAngleBond
     */
    @TestMethod("testMatchedBonds")
    public List<List<IBond>> getMatchingPharmacophoreBonds() {
        if (bondMapping == null) return null;
        matchingPBonds = new ArrayList<List<IBond>>();
        bondMapHash = new ArrayList<HashMap<IBond, IBond>>();

        for (Object aBondMapping : bondMapping) {
            List list = (List) aBondMapping;
            List<IBond> bondList = new ArrayList<IBond>();
            HashMap<IBond, IBond> tmphash = new HashMap<IBond, IBond>();
            for (Object aList : list) {
                RMap map = (RMap) aList;
                int bondID = map.getId1();
                bondList.add(pharmacophoreMolecule.getBond(bondID));

                tmphash.put(pharmacophoreMolecule.getBond(map.getId1()),
                        pharmacophoreQuery.getBond(map.getId2()));
            }
            bondMapHash.add(tmphash);
            matchingPBonds.add(bondList);
        }
        return matchingPBonds;
    }

    /**
     * Return a list of HashMap's that allows one to get the query constraint for a given pharmacophore bond.
     * <p/>
     * This should be called after calling {@link #getMatchingPharmacophoreBonds()}, otherwise the
     * return value is null. If the matching is successfull, the return value is a List of HashMaps, each
     * HashMap corresponding to a seperate match. Each HashMap is keyed on the {@link org.openscience.cdk.pharmacophore.PharmacophoreBond}
     * in the target molecule that matched a contstraint ({@link org.openscience.cdk.pharmacophore.PharmacophoreQueryBond} or
     * {@link org.openscience.cdk.pharmacophore.PharmacophoreQueryAngleBond}. The value is the corresponding query bond.
     *
     * @return A List of HashMaps, identifying the query constraint corresponding to a matched constraint in the target
     *         molecule.
     */
    @TestMethod("testMatchedBonds")
    public List<HashMap<IBond, IBond>> getTargetQueryBondMappings() {
        return bondMapHash;
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
    @TestMethod("testMatchedAtoms")
    public List<List<PharmacophoreAtom>> getMatchingPharmacophoreAtoms() {
        if (pharmacophoreMolecule == null || bondMapping == null) return null;
        matchingPAtoms = getAtomMappings(bondMapping, pharmacophoreMolecule);
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
    @TestMethod("testMatchedAtoms")
    public List<List<PharmacophoreAtom>> getUniqueMatchingPharmacophoreAtoms() {
        getMatchingPharmacophoreAtoms();
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
    @TestMethod("testGetterSetter")
    public IQueryAtomContainer getPharmacophoreQuery() {
        return pharmacophoreQuery;
    }

    /**
     * Set a pharmacophore query
     *
     * @param query The query
     */
    @TestMethod("testGetterSetter")
    public void setPharmacophoreQuery(IQueryAtomContainer query) {
        pharmacophoreQuery = query;
    }

    private IAtomContainer getPharmacophoreMolecule(IAtomContainer atomContainer) throws CDKException {

        SMARTSQueryTool sqt = new SMARTSQueryTool("C");
        IAtomContainer pharmacophoreMolecule = DefaultChemObjectBuilder.getInstance().newAtomContainer();

        // lets loop over each pcore query atom
        HashMap<String, String> map = new HashMap<String, String>();

        logger.debug("Converting [" + atomContainer.getProperty(CDKConstants.TITLE) + "] to a pcore molecule");

        Iterator qatoms = pharmacophoreQuery.atoms().iterator();
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

            // Note that we allow a special form of SMARTS where the | operator
            // represents logical or of multi-atom groups (as opposed to ','
            // which is for single atom matches)
            String[] subSmarts = smarts.split("\\|");

            for (String subSmart : subSmarts) {
                sqt.setSmarts(subSmart);
                if (sqt.matches(atomContainer)) {
                    List<List<Integer>> mappings = sqt.getUniqueMatchingAtoms();
                    for (List<Integer> atomIndices : mappings) {
                        Point3d coords = getEffectiveCoordinates(atomContainer, atomIndices);
                        PharmacophoreAtom patom = new PharmacophoreAtom(smarts, qatom.getSymbol(), coords);
                        patom.setMatchingAtoms(intIndices(atomIndices));
                        if (!pharmacophoreMolecule.contains(patom)) pharmacophoreMolecule.addAtom(patom);
                    }
                }
            }
            logger.debug("\tFound " + sqt.getUniqueMatchingAtoms().size() + " unique matches for " + smarts);
        }

        // now that we have added all the pcore atoms to the container
        // we need to join all atoms with pcore bonds   (i.e. distance constraints)
        if (hasDistanceConstraints(pharmacophoreQuery)) {
            int npatom = pharmacophoreMolecule.getAtomCount();
            for (int i = 0; i < npatom - 1; i++) {
                for (int j = i + 1; j < npatom; j++) {
                    PharmacophoreAtom atom1 = (PharmacophoreAtom) pharmacophoreMolecule.getAtom(i);
                    PharmacophoreAtom atom2 = (PharmacophoreAtom) pharmacophoreMolecule.getAtom(j);
                    PharmacophoreBond bond = new PharmacophoreBond(atom1, atom2);
                    pharmacophoreMolecule.addBond(bond);
                }
            }
        }

        // if we have angle constraints, generate only the valid
        // possible angle relationships, rather than all possible
        if (hasAngleConstraints(pharmacophoreQuery)) {
            int nangleDefs = 0;

            Iterator<IBond> qbonds = pharmacophoreQuery.bonds().iterator();
            while (qbonds.hasNext()) {
                IBond bond = qbonds.next();
                if (!(bond instanceof PharmacophoreQueryAngleBond)) continue;

                IAtom startQAtom = bond.getAtom(0);
                IAtom middleQAtom = bond.getAtom(1);
                IAtom endQAtom = bond.getAtom(2);

                // make a list of the patoms in the target that match
                // each type of angle atom
                List<IAtom> startl = new ArrayList<IAtom>();
                List<IAtom> middlel = new ArrayList<IAtom>();
                List<IAtom> endl = new ArrayList<IAtom>();

                Iterator<IAtom> tatoms = pharmacophoreMolecule.atoms().iterator();
                while (tatoms.hasNext()) {
                    IAtom tatom = tatoms.next();
                    if (tatom.getSymbol().equals(startQAtom.getSymbol())) startl.add(tatom);
                    if (tatom.getSymbol().equals(middleQAtom.getSymbol())) middlel.add(tatom);
                    if (tatom.getSymbol().equals(endQAtom.getSymbol())) endl.add(tatom);
                }

                // now we form the relevant angles, but we will
                // have reversed repeats
                List<IAtom[]> tmpl = new ArrayList<IAtom[]>();
                for (IAtom middle : middlel) {
                    for (IAtom start : startl) {
                        if (middle.equals(start)) continue;
                        for (IAtom end : endl) {
                            if (start.equals(end) || middle.equals(end)) continue;
                            tmpl.add(new IAtom[]{start, middle, end});
                        }
                    }
                }

                // now clean up reversed repeats
                List<IAtom[]> unique = new ArrayList<IAtom[]>();
                for (int i = 0; i < tmpl.size(); i++) {
                    IAtom[] seq1 = tmpl.get(i);
                    boolean isRepeat = false;
                    for (int j = 0; j < unique.size(); j++) {
                        if (i == j) continue;
                        IAtom[] seq2 = unique.get(j);
                        if (seq1[1] == seq2[1] && seq1[0] == seq2[2] && seq1[2] == seq2[0]) {
                            isRepeat = true;
                        }
                    }
                    if (!isRepeat) unique.add(seq1);
                }

                // finally we can add the unique angle to the target
                for (IAtom[] seq : unique) {
                    PharmacophoreAngleBond pbond = new PharmacophoreAngleBond(
                            (PharmacophoreAtom) seq[0],
                            (PharmacophoreAtom) seq[1],
                            (PharmacophoreAtom) seq[2]);
                    pharmacophoreMolecule.addBond(pbond);
                    nangleDefs++;
                }

            }
            logger.debug("Added " + nangleDefs + " defs to the target pcore molecule");
        }


        return pharmacophoreMolecule;
    }

    private boolean hasDistanceConstraints(IQueryAtomContainer query) {
        Iterator<IBond> bonds = query.bonds().iterator();
        while (bonds.hasNext()) {
            IBond bond = bonds.next();
            if (bond instanceof PharmacophoreQueryBond) return true;
        }
        return false;
    }

    private boolean hasAngleConstraints(IQueryAtomContainer query) {
        Iterator<IBond> bonds = query.bonds().iterator();
        while (bonds.hasNext()) {
            IBond bond = bonds.next();
            if (bond instanceof PharmacophoreQueryAngleBond) return true;
        }
        return false;
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
