/* Copyright (C) 2004-2008  Rajarshi Guha <rajarshi.guha@gmail.com>
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.vecmath.Point3d;

import com.google.common.collect.HashBiMap;
import org.openscience.cdk.AtomRef;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.aromaticity.ElectronDonation;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.GeometryUtil;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.Mappings;
import org.openscience.cdk.isomorphism.Pattern;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.smarts.SmartsPattern;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * Identifies atoms whose 3D arrangement matches a specified pharmacophore query.
 * 
 * A pharmacophore is defined by a set of atoms and distances between them. More generically
 * we can restate this as a set of pharmacophore groups and the distances between them. Note
 * that a pharmacophore group may consist of one or more atoms and the distances can be
 * specified as a distance range rather than an exact distance.
 * 
 * The goal of a pharmacophore query is to identify atom in a molecule whose 3D arrangement
 * match a specified query.
 * 
 * To perform a query one must first create a set of pharmacophore groups and specify the
 * distances between them. Each pharmacophore group is represented by a {@link org.openscience.cdk.pharmacophore.PharmacophoreAtom}
 * and the distances between them are represented by a {@link org.openscience.cdk.pharmacophore.PharmacophoreBond}.
 * These are collected in a {@link org.openscience.cdk.isomorphism.matchers.QueryAtomContainer}.
 * 
 * Given the query pharmacophore one can use this class to check with it occurs in a specified molecule.
 * Note that for full generality pharmacophore searches are performed using conformations of molecules.
 * This can easily be accomplished using this class together with the {@link org.openscience.cdk.ConformerContainer}
 * class.  See the example below.
 * 
 * Currently this class will allow you to perform pharmacophore searches using triads, quads or any number
 * of pharmacophore groups. However, only distances and angles between pharmacophore groups are considered, so
 * alternative constraints such as torsions and so on cannot be considered at this point.
 * 
 * After a query has been performed one can retrieve the matching groups (as opposed to the matching atoms
 * of the target molecule). However since a pharmacophore group (which is an object of class {@link PharmacophoreAtom})
 * allows you to access the indices of the corresponding atoms in the target molecule, this is not very
 * difficult.
 * Example usage:
 * <pre>
 * QueryAtomContainer query = new QueryAtomContainer();
 * 
 * PharmacophoreQueryAtom o = new PharmacophoreQueryAtom("D", "[OX1]");
 * PharmacophoreQueryAtom n1 = new PharmacophoreQueryAtom("A", "[N]");
 * PharmacophoreQueryAtom n2 = new PharmacophoreQueryAtom("A", "[N]");
 * 
 * query.addAtom(o);
 * query.addAtom(n1);
 * query.addAtom(n2);
 * 
 * PharmacophoreQueryBond b1 = new PharmacophoreQueryBond(o, n1, 4.0, 4.5);
 * PharmacophoreQueryBond b2 = new PharmacophoreQueryBond(o, n2, 4.0, 5.0);
 * PharmacophoreQueryBond b3 = new PharmacophoreQueryBond(n1, n2, 5.4, 5.8);
 * 
 * query.addBond(b1);
 * query.addBond(b2);
 * query.addBond(b3);
 * 
 * String filename = "/Users/rguha/pcore1.sdf";
 * IteratingMDLConformerReader reader = new IteratingMDLConformerReader(
 *      new FileReader(new File(filename)), DefaultChemObjectBuilder.getInstance());
 * 
 * ConformerContainer conformers;
 * if (reader.hasNext()) conformers = (ConformerContainer) reader.next();
 * 
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
 * @cdk.githash
 * @cdk.keyword pharmacophore
 * @cdk.keyword 3D isomorphism
 * @see org.openscience.cdk.pharmacophore.PharmacophoreAtom
 * @see org.openscience.cdk.pharmacophore.PharmacophoreBond
 * @see org.openscience.cdk.pharmacophore.PharmacophoreQueryAtom
 * @see org.openscience.cdk.pharmacophore.PharmacophoreQueryBond
 */
public class PharmacophoreMatcher {

    private ILoggingTool                  logger                = LoggingToolFactory
                                                                        .createLoggingTool(PharmacophoreMatcher.class);
    private PharmacophoreQuery            pharmacophoreQuery    = null;
    private IAtomContainer                pharmacophoreMolecule = null;
    
    private Mappings mappings = null;

    /**
     * An empty constructor.
     * 
     * You should set the query before performing a match
     */
    public PharmacophoreMatcher() {}

    /**
     * Initialize the matcher with a query.
     *
     * @param pharmacophoreQuery The query pharmacophore
     * @see org.openscience.cdk.pharmacophore.PharmacophoreQueryAtom
     * @see org.openscience.cdk.pharmacophore.PharmacophoreQueryBond
     */
    public PharmacophoreMatcher(PharmacophoreQuery pharmacophoreQuery) {
        this.pharmacophoreQuery = pharmacophoreQuery;
    }

    /**
     * Performs the pharmacophore matching.
     * 
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
    public boolean matches(IAtomContainer atomContainer, boolean initializeTarget) throws CDKException {
        if (!GeometryUtil.has3DCoordinates(atomContainer)) throw new CDKException("Molecule must have 3D coordinates");
        if (pharmacophoreQuery == null) throw new CDKException("Must set the query pharmacophore before matching");
        if (!checkQuery(pharmacophoreQuery))
            throw new CDKException(
                    "A problem in the query. Make sure all pharmacophore groups of the same symbol have the same same SMARTS");
        String title = (String) atomContainer.getTitle();

        if (initializeTarget)
            pharmacophoreMolecule = getPharmacophoreMolecule(atomContainer);
        else {
            // even though the atoms comprising the pcore groups are
            // constant, their coords will differ, so we need to make
            // sure we get the latest set of effective coordinates
            for (IAtom iAtom : pharmacophoreMolecule.atoms()) {
                PharmacophoreAtom patom = PharmacophoreAtom.get(iAtom);
                List<Integer> tmpList = new ArrayList<Integer>();
                for (int idx : patom.getMatchingAtoms())
                    tmpList.add(idx);
                Point3d coords = getEffectiveCoordinates(atomContainer, tmpList);
                patom.setPoint3d(coords);
            }
        }

        if (pharmacophoreMolecule.getAtomCount() < pharmacophoreQuery.getAtomCount()) {
            logger.debug("Target [" + title + "] did not match the query SMARTS. Skipping constraints");
            return false;
        }

        mappings = Pattern.findSubstructure(pharmacophoreQuery)
                          .matchAll(pharmacophoreMolecule);

        // XXX: doing one search then discarding
        return mappings.atLeast(1);
    }

    /**
     * Get the matching pharmacophore constraints.
     * 
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
    public List<List<IBond>> getMatchingPharmacophoreBonds() {
        if (mappings == null) return null;

        // XXX: re-subsearching the query
        List<List<IBond>> bonds = new ArrayList<>();
        for (Map<IBond,IBond> map : mappings.toBondMap()) {
            bonds.add(new ArrayList<>(map.values()));
        }
        
        return bonds;
    }

    /**
     * Return a list of HashMap's that allows one to get the query constraint for a given pharmacophore bond.
     * 
     * If the matching is successful, the return value is a List of HashMaps, each
     * HashMap corresponding to a separate match. Each HashMap is keyed on the {@link org.openscience.cdk.pharmacophore.PharmacophoreBond}
     * in the target molecule that matched a constraint ({@link org.openscience.cdk.pharmacophore.PharmacophoreQueryBond} or
     * {@link org.openscience.cdk.pharmacophore.PharmacophoreQueryAngleBond}. The value is the corresponding query bond.
     *
     * @return A List of HashMaps, identifying the query constraint corresponding to a matched constraint in the target
     *         molecule.
     */
    public List<HashMap<IBond, IBond>> getTargetQueryBondMappings() {
        if (mappings == null) return null;
        
        List<HashMap<IBond,IBond>> bondMap = new ArrayList<>();
        
        // query -> target so need to inverse the mapping
        // XXX: re-subsearching the query
        for (Map<IBond,IBond> map : mappings.toBondMap()) {
            bondMap.add(new HashMap<>(HashBiMap.create(map).inverse()));
        }
        
        return bondMap;
    }

    /**
     * Get the matching pharmacophore groups.
     * 
     * The method should be called after performing the match, otherwise the return value is null.
     * The method returns a List of List's. Each List represents the pharmacophore groups in the
     * target molecule that matched the query. Each pharmacophore group contains the indices of the
     * atoms (in the target molecule) that correspond to the group.
     *
     * @return a List of a List of pharmacophore groups in the target molecule that match the query
     * @see org.openscience.cdk.pharmacophore.PharmacophoreAtom
     */
    public List<List<PharmacophoreAtom>> getMatchingPharmacophoreAtoms() {
        if (pharmacophoreMolecule == null || mappings == null) return null;
        return getPCoreAtoms(mappings);
    }

    /**
     * Get the uniue matching pharmacophore groups.
     * 
     * The method should be called after performing the match, otherwise the return value is null.
     * The method returns a List of List's. Each List represents the pharmacophore groups in the
     * target molecule that matched the query. Each pharmacophore group contains the indices of the
     * atoms (in the target molecule) that correspond to the group.
     * 
     * This is analogous to the USA form of return value from a SMARTS match.
     *
     * @return a List of a List of pharmacophore groups in the target molecule that match the query
     * @see org.openscience.cdk.pharmacophore.PharmacophoreAtom
     */
    public List<List<PharmacophoreAtom>> getUniqueMatchingPharmacophoreAtoms() {
        if (pharmacophoreMolecule == null || mappings == null) return null;
        return getPCoreAtoms(mappings.uniqueAtoms());
    }

    private List<List<PharmacophoreAtom>> getPCoreAtoms(Mappings mappings) {
        List<List<PharmacophoreAtom>> atoms = new ArrayList<>();
        // XXX: re-subsearching the query
        for (Map<IAtom,IAtom> map : mappings.toAtomMap()) {
            List<PharmacophoreAtom> pcoreatoms = new ArrayList<>();
            for (IAtom atom : map.values())
                pcoreatoms.add((PharmacophoreAtom) AtomRef.deref(atom));
            atoms.add(pcoreatoms);
        }
        return atoms;
    }

    /**
     * Get the query pharmacophore.
     *
     * @return The query
     */
    public PharmacophoreQuery getPharmacophoreQuery() {
        return pharmacophoreQuery;
    }

    /**
     * Set a pharmacophore query.
     *
     * @param query The query
     */
    public void setPharmacophoreQuery(PharmacophoreQuery query) {
        pharmacophoreQuery = query;
    }

    /**
     * Convert the input into a pcore molecule.
     * 
     * @param input the compound being converted from
     * @return pcore molecule 
     * @throws CDKException match failed
     */
    private IAtomContainer getPharmacophoreMolecule(IAtomContainer input) throws CDKException {

        // XXX: prepare query, to be moved
        prepareInput(input);
        
        IAtomContainer pharmacophoreMolecule = input.getBuilder().newInstance(IAtomContainer.class,0,0,0,0);

        final Set<String>            matched     = new HashSet<>();
        final Set<PharmacophoreAtom> uniqueAtoms = new LinkedHashSet<>();

        logger.debug("Converting [" + input.getTitle() + "] to a pcore molecule");
        
        // lets loop over each pcore query atom
        for (IAtom atom : pharmacophoreQuery.atoms()) {
            final PharmacophoreQueryAtom qatom = (PharmacophoreQueryAtom) atom;
            final String smarts = qatom.getSmarts();
            
            // a pcore query might have multiple instances of a given pcore atom (say
            // 2 hydrophobic groups separated by X unit). In such a case we want to find
            // the atoms matching the pgroup SMARTS just once, rather than redoing the
            // matching for each instance of the pcore query atom.
            if (!matched.add(qatom.getSymbol()))
                continue;

            // see if the smarts for this pcore query atom gets any matches
            // in our query molecule. If so, then collect each set of
            // matching atoms and for each set make a new pcore atom and
            // add it to the pcore atom container object
            int count = 0;
            for (final SmartsPattern query : qatom.getCompiledSmarts()) {
                
                // create the lazy mappings iterator
                final Mappings mappings = query.matchAll(input)
                                               .uniqueAtoms();
                
                for (final int[] mapping : mappings) {
                    uniqueAtoms.add(newPCoreAtom(input, qatom, smarts, mapping));
                    count++;
                }
            }
            logger.debug("\tFound " + count + " unique matches for " + smarts);
        }

        pharmacophoreMolecule.setAtoms(uniqueAtoms.toArray(new IAtom[uniqueAtoms.size()]));

        // now that we have added all the pcore atoms to the container
        // we need to join all atoms with pcore bonds   (i.e. distance constraints)
        if (hasDistanceConstraints(pharmacophoreQuery)) {
            int npatom = pharmacophoreMolecule.getAtomCount();
            for (int i = 0; i < npatom - 1; i++) {
                for (int j = i + 1; j < npatom; j++) {
                    PharmacophoreAtom atom1 = PharmacophoreAtom.get(pharmacophoreMolecule.getAtom(i));
                    PharmacophoreAtom atom2 = PharmacophoreAtom.get(pharmacophoreMolecule.getAtom(j));
                    PharmacophoreBond bond = new PharmacophoreBond(atom1, atom2);
                    pharmacophoreMolecule.addBond(bond);
                }
            }
        }

        // if we have angle constraints, generate only the valid
        // possible angle relationships, rather than all possible
        if (hasAngleConstraints(pharmacophoreQuery)) {
            int nangleDefs = 0;

            for (IBond bond : pharmacophoreQuery.bonds()) {
                if (!(bond instanceof PharmacophoreQueryAngleBond)) continue;

                IAtom startQAtom = bond.getAtom(0);
                IAtom middleQAtom = bond.getAtom(1);
                IAtom endQAtom = bond.getAtom(2);

                // make a list of the patoms in the target that match
                // each type of angle atom
                List<IAtom> startl = new ArrayList<IAtom>();
                List<IAtom> middlel = new ArrayList<IAtom>();
                List<IAtom> endl = new ArrayList<IAtom>();

                for (IAtom tatom : pharmacophoreMolecule.atoms()) {
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
                        if (Objects.equals(seq1[1],seq2[1]) && Objects.equals(seq1[0], seq2[2]) && Objects.equals(seq1[2], seq2[0])) {
                            isRepeat = true;
                        }
                    }
                    if (!isRepeat) unique.add(seq1);
                }

                // finally we can add the unique angle to the target
                for (IAtom[] seq : unique) {
                    PharmacophoreAngleBond pbond = new PharmacophoreAngleBond(PharmacophoreAtom.get(seq[0]),
                            PharmacophoreAtom.get(seq[1]), PharmacophoreAtom.get(seq[2]));
                    pharmacophoreMolecule.addBond(pbond);
                    nangleDefs++;
                }

            }
            logger.debug("Added " + nangleDefs + " defs to the target pcore molecule");
        }

        return pharmacophoreMolecule;
    }

    private PharmacophoreAtom newPCoreAtom(IAtomContainer input, PharmacophoreQueryAtom qatom, String smarts, int[] mapping) {
        final Point3d coords = getEffectiveCoordinates(input, mapping);
        PharmacophoreAtom patom = new PharmacophoreAtom(smarts, qatom.getSymbol(), coords);
        // n.b. mapping[] copy is mad by pcore atom 
        patom.setMatchingAtoms(mapping);
        return patom;
    }

    private void prepareInput(IAtomContainer input) throws CDKException {
        SmartsPattern.prepare(input);
    }

    private boolean hasDistanceConstraints(IQueryAtomContainer query) {
        for (IBond bond : query.bonds()) {
            if (bond instanceof PharmacophoreQueryBond) return true;
        }
        return false;
    }

    private boolean hasAngleConstraints(IQueryAtomContainer query) {
        for (IBond bond : query.bonds()) {
            if (bond instanceof PharmacophoreQueryAngleBond) return true;
        }
        return false;
    }

    private int[] intIndices(List<Integer> atomIndices) {
        int[] ret = new int[atomIndices.size()];
        for (int i = 0; i < atomIndices.size(); i++)
            ret[i] = atomIndices.get(i);
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
    
    private Point3d getEffectiveCoordinates(IAtomContainer atomContainer, int[] atomIndices) {
        Point3d ret = new Point3d(0, 0, 0);
        for (int i : atomIndices) {
            Point3d coord = atomContainer.getAtom(i).getPoint3d();
            ret.x += coord.x;
            ret.y += coord.y;
            ret.z += coord.z;
        }
        ret.x /= atomIndices.length;
        ret.y /= atomIndices.length;
        ret.z /= atomIndices.length;
        return ret;
    }

    private boolean checkQuery(IQueryAtomContainer query) {
        if (!(query instanceof PharmacophoreQuery)) return false;
        HashMap<String, String> map = new HashMap<String, String>();
        for (int i = 0; i < query.getAtomCount(); i++) {
            IQueryAtom atom = (IQueryAtom) query.getAtom(i);
            if (!(atom instanceof PharmacophoreQueryAtom)) return false;

            PharmacophoreQueryAtom pqatom = (PharmacophoreQueryAtom) atom;
            String label = pqatom.getSymbol();
            String smarts = pqatom.getSmarts();

            if (!map.containsKey(label))
                map.put(label, smarts);
            else {
                if (!map.get(label).equals(smarts)) return false;
            }
        }
        return true;
    }

}
