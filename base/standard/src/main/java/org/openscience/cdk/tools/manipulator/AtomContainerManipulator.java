/* 
 * Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
 *                    2014  Mark B Vine (orcid:0000-0002-7794-0426)
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
 *  */
package org.openscience.cdk.tools.manipulator;

import static org.openscience.cdk.CDKConstants.SINGLE_OR_DOUBLE;
import static org.openscience.cdk.interfaces.IDoubleBondStereochemistry.Conformation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.config.Isotopes;
import org.openscience.cdk.config.Elements;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.GraphUtil;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IDoubleBondStereochemistry;
import org.openscience.cdk.interfaces.IElectronContainer;
import org.openscience.cdk.interfaces.ILonePair;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.ISingleElectron;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.interfaces.ITetrahedralChirality;
import org.openscience.cdk.ringsearch.RingSearch;
import org.openscience.cdk.sgroup.Sgroup;
import org.openscience.cdk.sgroup.SgroupKey;
import org.openscience.cdk.stereo.DoubleBondStereochemistry;
import org.openscience.cdk.stereo.TetrahedralChirality;

/**
 * Class with convenience methods that provide methods to manipulate
 * AtomContainer's. For example:
 * <pre>
 * AtomContainerManipulator.replaceAtomByAtom(container, atom1, atom2);
 * </pre>
 * will replace the Atom in the AtomContainer, but in all the ElectronContainer's
 * it participates too.
 *
 * @cdk.module standard
 * @cdk.githash
 *
 * @author  Egon Willighagen
 * @cdk.created 2003-08-07
 */
public class AtomContainerManipulator {

    /**
     * Extract a substructure from an atom container, in the form of a new
     * cloned atom container with only the atoms with indices in atomIndices and
     * bonds that connect these atoms.
     * 
     * Note that this may result in a disconnected atom container.
     *
     * @param atomContainer the source container to extract from
     * @param atomIndices the indices of the substructure
     * @return a cloned atom container with a substructure of the source
     * @throws CloneNotSupportedException if the source container cannot be cloned
     */
    public static IAtomContainer extractSubstructure(IAtomContainer atomContainer, int... atomIndices)
            throws CloneNotSupportedException {
        IAtomContainer substructure = (IAtomContainer) atomContainer.clone();
        int numberOfAtoms = substructure.getAtomCount();
        IAtom[] atoms = new IAtom[numberOfAtoms];
        for (int atomIndex = 0; atomIndex < numberOfAtoms; atomIndex++) {
            atoms[atomIndex] = substructure.getAtom(atomIndex);
        }

        Arrays.sort(atomIndices);
        for (int index = 0; index < numberOfAtoms; index++) {
            if (Arrays.binarySearch(atomIndices, index) < 0) {
                IAtom atom = atoms[index];
                substructure.removeAtomAndConnectedElectronContainers(atom);
            }
        }

        return substructure;
    }

    /**
     * Returns an atom in an atomcontainer identified by id
     *
     * @param ac The AtomContainer to search in
     * @param id The id to search for
     * @return An atom having id id
     * @throws CDKException There is no such atom
     */
    public static IAtom getAtomById(IAtomContainer ac, String id) throws CDKException {
        for (int i = 0; i < ac.getAtomCount(); i++) {
            if (ac.getAtom(i).getID() != null && ac.getAtom(i).getID().equals(id)) return ac.getAtom(i);
        }
        throw new CDKException("no suc atom");
    }

    /**
     * Substitute one atom in a container for another adjusting bonds, single electrons, lone pairs, and stereochemistry
     * as required.
     *
     * @param container the container to replace the atom of
     * @param oldAtom the atom to replace
     * @param newAtom the atom to insert
     * @return whether replacement was made
     */
    public static boolean replaceAtomByAtom(final IAtomContainer container, final IAtom oldAtom, final IAtom newAtom) {

        Map<IAtom,IAtom> atomremap = new HashMap<>();

        for (int i = 0; i < container.getAtomCount(); i++) {
            IAtom atom = container.getAtom(i);
            if (atom == oldAtom) {
                container.setAtom(i, newAtom);
                atomremap.put(oldAtom, newAtom);
            } else {
                if (atom == newAtom)
                    throw new IllegalArgumentException("Cannot replace atom with one from the same molecule.");
                atomremap.put(atom, atom);
            }
        }

        if (!atomremap.containsKey(oldAtom))
            return false;

        Map<IBond,IBond> bondremap = new HashMap<>();
        for (IBond bond : container.bonds()) {
            bondremap.put(bond, bond);
            for (int i = 0; i < bond.getAtomCount(); i++)
                if (bond.getAtom(i) == oldAtom)
                    bond.setAtom(newAtom, i);
        }
        for (ISingleElectron ec : container.singleElectrons())
            if (ec.getAtom() == oldAtom)
                ec.setAtom(newAtom);
        for (ILonePair lp : container.lonePairs())
            if (lp.getAtom() == oldAtom)
                lp.setAtom(newAtom);

        List<IStereoElement> stereoremapped = new ArrayList<>();
        for (IStereoElement se : container.stereoElements())
            stereoremapped.add(se.map(atomremap, bondremap));
        container.setStereoElements(stereoremapped);

        List<Sgroup> sgrougs = container.getProperty(CDKConstants.CTAB_SGROUPS);
        if (sgrougs != null) {
            boolean updated = false;
            List<Sgroup> replaced = new ArrayList<>();
            for (Sgroup org : sgrougs) {
                if (org.getAtoms().contains(oldAtom)) {
                    updated = true;
                    Sgroup cpy = new Sgroup();
                    for (IAtom atom : org.getAtoms()) {
                        if (atom != oldAtom)
                            cpy.addAtom(atom);
                        else
                            cpy.addAtom(newAtom);
                    }
                    for (IBond bond : org.getBonds())
                        cpy.addBond(bond);
                    for (Sgroup parent : org.getParents())
                        cpy.addParent(parent);
                    for (SgroupKey key : org.getAttributeKeys())
                        cpy.putValue(key, org.getValue(key));
                    replaced.add(cpy);
                } else {
                    replaced.add(org);
                }
            }
            if (updated) {
                container.setProperty(CDKConstants.CTAB_SGROUPS,
                                      Collections.unmodifiableList(replaced));
            }
        }

        return true;
    }

    /**
     * Get the summed charge of all atoms in an AtomContainer
     *
     * @param  atomContainer The IAtomContainer to manipulate
     * @return The summed charges of all atoms in this AtomContainer.
     */
    public static double getTotalCharge(IAtomContainer atomContainer) {
        double charge = 0.0;
        for (IAtom atom : atomContainer.atoms()) {
            // we assume CDKConstant.UNSET is equal to 0
            Double thisCharge = atom.getCharge();
            if (thisCharge != CDKConstants.UNSET) charge += thisCharge;
        }
        return charge;
    }

    /**
     * Get the summed exact mass of all atoms in an AtomContainer. It
     * requires isotope information for all atoms to be set. Either set
     * this information using the {@link IsotopeFactory}, or use the
     * {@link MolecularFormulaManipulator#getMajorIsotopeMass(org.openscience.cdk.interfaces.IMolecularFormula)}
     * method, after converting the {@link IAtomContainer} to a
     * {@link IMolecularFormula} with the {@link MolecularFormulaManipulator}.
     *
     * @param  atomContainer The IAtomContainer to manipulate
     * @return The summed exact mass of all atoms in this AtomContainer.
     */
    public static double getTotalExactMass(IAtomContainer atomContainer) {
        try {

            Isotopes isotopes = Isotopes.getInstance();
            double mass = 0.0;
            double hExactMass = isotopes.getMajorIsotope(1).getExactMass();
            for (IAtom atom : atomContainer.atoms()) {
                if (atom.getImplicitHydrogenCount() == null)
                    throw new IllegalArgumentException("an atom had with unknown (null) implicit hydrogens");
                mass += atom.getExactMass();
                mass += atom.getImplicitHydrogenCount() * hExactMass;
            }
            return mass;
        } catch (IOException e) {
            throw new RuntimeException("Isotopes definitions could not be loaded", e);
        }
    }

    /**
     * Returns the molecular mass of the IAtomContainer. For the calculation it
     * uses the masses of the isotope mixture using natural abundances.
     *
     * @param atomContainer
     * @cdk.keyword mass, molecular
     */
    public static double getNaturalExactMass(IAtomContainer atomContainer) {
        try {
            Isotopes isotopes = Isotopes.getInstance();
            double hydgrogenMass = isotopes.getNaturalMass(Elements.HYDROGEN);

            double mass = 0.0;
            for (final IAtom atom : atomContainer.atoms()) {

                if (atom.getAtomicNumber() == null)
                    throw new IllegalArgumentException("an atom had with unknown (null) atomic number");
                if (atom.getImplicitHydrogenCount() == null)
                    throw new IllegalArgumentException("an atom had with unknown (null) implicit hydrogens");

                mass += isotopes.getNaturalMass(Elements.ofNumber(atom.getAtomicNumber()).toIElement());
                mass += hydgrogenMass * atom.getImplicitHydrogenCount();
            }
            return mass;

        } catch (IOException e) {
            throw new RuntimeException("Isotopes definitions could not be loaded", e);
        }
    }

    /**
     * Get the summed natural abundance of all atoms in an AtomContainer
     *
     * @param  atomContainer The IAtomContainer to manipulate
     * @return The summed natural abundance of all atoms in this AtomContainer.
     */
    public static double getTotalNaturalAbundance(IAtomContainer atomContainer) {
        try {
            Isotopes isotopes = Isotopes.getInstance();
            double abundance = 1.0;
            double hAbundance = isotopes.getMajorIsotope(1).getNaturalAbundance();

            int nImplH = 0;

            for (IAtom atom : atomContainer.atoms()) {
                if (atom.getImplicitHydrogenCount() == null)
                    throw new IllegalArgumentException("an atom had with unknown (null) implicit hydrogens");
                abundance *= atom.getNaturalAbundance();
                for (int h = 0; h < atom.getImplicitHydrogenCount(); h++)
                    abundance *= hAbundance;
                nImplH += atom.getImplicitHydrogenCount();
            }
            return abundance / Math.pow(100, nImplH + atomContainer.getAtomCount());
        } catch (IOException e) {
            throw new RuntimeException("Isotopes definitions could not be loaded", e);
        }
    }

    /**
     * Get the total formal charge on a molecule.
     *
     * @param atomContainer the atom container to consider
     * @return The summed formal charges of all atoms in this AtomContainer.
     */
    public static int getTotalFormalCharge(IAtomContainer atomContainer) {
        int chargeP = getTotalNegativeFormalCharge(atomContainer);
        int chargeN = getTotalPositiveFormalCharge(atomContainer);

        return chargeP + chargeN;
    }

    /**
     * Get the total formal negative charge on a molecule.
     *
     * @param atomContainer the atom container to consider
     * @return The summed negative formal charges of all atoms in this AtomContainer.
     */
    public static int getTotalNegativeFormalCharge(IAtomContainer atomContainer) {
        int charge = 0;
        for (int i = 0; i < atomContainer.getAtomCount(); i++) {
            double chargeI = atomContainer.getAtom(i).getFormalCharge();
            if (chargeI < 0) charge += chargeI;
        }
        return charge;
    }

    /**
     * Get the total positive formal charge on a molecule.
     *
     * @param atomContainer the atom container to consider
     * @return The summed positive formal charges of all atoms in this AtomContainer.
     */
    public static int getTotalPositiveFormalCharge(IAtomContainer atomContainer) {
        int charge = 0;
        for (int i = 0; i < atomContainer.getAtomCount(); i++) {
            double chargeI = atomContainer.getAtom(i).getFormalCharge();
            if (chargeI > 0) charge += chargeI;
        }
        return charge;
    }

    /**
     * Counts the number of hydrogens on the provided IAtomContainer. As this
     * method will sum all implicit hydrogens on each atom it is important to
     * ensure the atoms have already been perceived (and thus have an implicit
     * hydrogen count) (see. {@link #percieveAtomTypesAndConfigureAtoms}).
     *
     * @param container the container to count the hydrogens on
     * @return the total number of hydrogens
     * @see org.openscience.cdk.interfaces.IAtom#getImplicitHydrogenCount()
     * @see #percieveAtomTypesAndConfigureAtoms
     * @throws IllegalArgumentException if the provided container was null
     */
    public static int getTotalHydrogenCount(IAtomContainer container) {
        if (container == null) throw new IllegalArgumentException("null container provided");
        int hydrogens = 0;
        for (IAtom atom : container.atoms()) {

            if (Elements.HYDROGEN.getSymbol().equals(atom.getSymbol())) {
                hydrogens++;
            }

            // rare but a hydrogen may have an implicit hydrogen so we don't use 'else'
            Integer implicit = atom.getImplicitHydrogenCount();
            if (implicit != null) {
                hydrogens += implicit;
            }

        }
        return hydrogens;
    }

    /**
     * Counts the number of implicit hydrogens on the provided IAtomContainer.
     * As this method will sum all implicit hydrogens on each atom it is
     * important to ensure the atoms have already been perceived (and thus have
     * an implicit hydrogen count) (see.
     * {@link #percieveAtomTypesAndConfigureAtoms}).
     *
     * @param container the container to count the implicit hydrogens on
     * @return the total number of implicit hydrogens
     * @see org.openscience.cdk.interfaces.IAtom#getImplicitHydrogenCount()
     * @see #percieveAtomTypesAndConfigureAtoms
     * @throws IllegalArgumentException if the provided container was null
     */
    public static int getImplicitHydrogenCount(IAtomContainer container) {
        if (container == null) throw new IllegalArgumentException("null container provided");
        int count = 0;
        for (IAtom atom : container.atoms()) {
            Integer implicit = atom.getImplicitHydrogenCount();
            if (implicit != null) {
                count += implicit;
            }
        }
        return count;
    }

    /**
     * Count explicit hydrogens.
     *
     * @param atomContainer the atom container to consider
     * @return The number of explicit hydrogens on the given IAtom.
     * @throws IllegalArgumentException if either the container or atom were null
     */
    public static int countExplicitHydrogens(IAtomContainer atomContainer, IAtom atom) {
        if (atomContainer == null || atom == null)
            throw new IllegalArgumentException("null container or atom provided");
        int hCount = 0;
        for (IAtom connected : atomContainer.getConnectedAtomsList(atom)) {
            if (Elements.HYDROGEN.getSymbol().equals(connected.getSymbol())) {
                hCount++;
            }
        }
        return hCount;
    }

    /**
     * Adds explicit hydrogens (without coordinates) to the IAtomContainer,
     * equaling the number of set implicit hydrogens.
     *
     * @param atomContainer the atom container to consider
     * @cdk.keyword hydrogens, adding
     */
    public static void convertImplicitToExplicitHydrogens(IAtomContainer atomContainer) {
        List<IAtom> hydrogens = new ArrayList<IAtom>();
        List<IBond> newBonds = new ArrayList<IBond>();

        // store a single explicit hydrogen of each original neighbor
        Map<IAtom, IAtom> hNeighbor = Maps.newHashMapWithExpectedSize(atomContainer.getAtomCount());

        for (IAtom atom : atomContainer.atoms()) {
            if (!atom.getSymbol().equals("H")) {
                Integer hCount = atom.getImplicitHydrogenCount();
                if (hCount != null) {
                    for (int i = 0; i < hCount; i++) {

                        IAtom hydrogen = atom.getBuilder().newInstance(IAtom.class, "H");
                        hydrogen.setAtomTypeName("H");
                        hydrogen.setImplicitHydrogenCount(0);
                        hydrogens.add(hydrogen);
                        newBonds.add(atom.getBuilder().newInstance(IBond.class, atom, hydrogen,
                                Order.SINGLE));

                        if (hNeighbor.get(atom) == null) hNeighbor.put(atom, hydrogen);

                    }
                    atom.setImplicitHydrogenCount(0);
                }
            }
        }
        for (IAtom atom : hydrogens)
            atomContainer.addAtom(atom);
        for (IBond bond : newBonds)
            atomContainer.addBond(bond);

        // update tetrahedral elements with an implicit part
        List<IStereoElement> stereos = new ArrayList<>();
        for (IStereoElement stereo : atomContainer.stereoElements()) {
            if (stereo instanceof ITetrahedralChirality) {
                ITetrahedralChirality tc = (ITetrahedralChirality) stereo;

                IAtom focus = tc.getChiralAtom();
                IAtom[] neighbors = tc.getLigands();
                IAtom hydrogen = hNeighbor.get(focus);

                // in sulfoxide - the implicit part of the tetrahedral centre
                // is a lone pair

                if (hydrogen != null) {
                    for (int i = 0; i < 4; i++) {
                        if (neighbors[i] == focus) {
                            neighbors[i] = hydrogen;
                            break;
                        }
                    }
                    // neighbors is a copy so need to create a new stereocenter
                    stereos.add(new TetrahedralChirality(focus, neighbors, tc.getStereo()));
                } else {
                    stereos.add(stereo);
                }
            } else {
                stereos.add(stereo);
            }
        }
        atomContainer.setStereoElements(stereos);

    }

    /**
     * @return The summed implicit + explicit hydrogens of the given IAtom.
     */
    public static int countHydrogens(IAtomContainer atomContainer, IAtom atom) {
        int hCount = atom.getImplicitHydrogenCount() == CDKConstants.UNSET ? 0 : atom.getImplicitHydrogenCount();
        hCount += countExplicitHydrogens(atomContainer, atom);
        return hCount;
    }

    public static List<String> getAllIDs(IAtomContainer mol) {
        List<String> idList = new ArrayList<String>();
        if (mol != null) {
            if (mol.getID() != null) idList.add(mol.getID());
            for (IAtom atom : mol.atoms()) {
                if (atom.getID() != null) idList.add(atom.getID());
            }

            for (IBond bond : mol.bonds()) {
                if (bond.getID() != null) idList.add(bond.getID());
            }
        }
        return idList;
    }

    /**
     * Produces an AtomContainer without explicit non stereo-relevant Hs but with H count from one with Hs.
     * The new molecule is a deep copy.
     *
     * @param org The AtomContainer from which to remove the hydrogens
     * @return              The molecule without non stereo-relevant Hs.
     * @cdk.keyword         hydrogens, removal
     */
    public static IAtomContainer removeNonChiralHydrogens(IAtomContainer org) {

        Map<IAtom, IAtom> map = new HashMap<IAtom, IAtom>(); // maps original atoms to clones.
        List<IAtom> remove = new ArrayList<IAtom>(); // lists removed Hs.

        // Clone atoms except those to be removed.
        IAtomContainer cpy = org.getBuilder().newInstance(IAtomContainer.class);
        int count = org.getAtomCount();

        for (int i = 0; i < count; i++) {

            // Clone/remove this atom?
            IAtom atom = org.getAtom(i);
            boolean addToRemove = false;
            if (suppressibleHydrogen(org, atom)) {
                // test whether connected to a single hetero atom only, otherwise keep
                if (org.getConnectedAtomsList(atom).size() == 1) {
                    IAtom neighbour = org.getConnectedAtomsList(atom).get(0);
                    // keep if the neighbouring hetero atom has stereo information, otherwise continue checking
                    Integer stereoParity = neighbour.getStereoParity();
                    if (stereoParity == null || stereoParity == 0) {
                        addToRemove = true;
                        // keep if any of the bonds of the hetero atom have stereo information
                        for (IBond bond : org.getConnectedBondsList(neighbour)) {
                            IBond.Stereo bondStereo = bond.getStereo();
                            if (bondStereo != null && bondStereo != IBond.Stereo.NONE) addToRemove = false;
                            IAtom neighboursNeighbour = bond.getOther(neighbour);
                            // remove in any case if the hetero atom is connected to more than one hydrogen
                            if (neighboursNeighbour.getSymbol().equals("H") && neighboursNeighbour != atom) {
                                addToRemove = true;
                                break;
                            }
                        }
                    }
                }
            }

            if (addToRemove)
                remove.add(atom);
            else
                addClone(atom, cpy, map);
        }

        // rescue any false positives, i.e., hydrogens that are stereo-relevant
        // the use of IStereoElement is not fully integrated yet to describe stereo information
        for (IStereoElement stereoElement : org.stereoElements()) {
            if (stereoElement instanceof ITetrahedralChirality) {
                ITetrahedralChirality tetChirality = (ITetrahedralChirality) stereoElement;
                for (IAtom atom : tetChirality.getLigands()) {
                    if (atom.getSymbol().equals("H") && remove.contains(atom)) {
                        remove.remove(atom);
                        addClone(atom, cpy, map);
                    }
                }
            } else if (stereoElement instanceof IDoubleBondStereochemistry) {
                IDoubleBondStereochemistry dbs = (IDoubleBondStereochemistry) stereoElement;
                IBond stereoBond = dbs.getStereoBond();
                for (IAtom neighbor : org.getConnectedAtomsList(stereoBond.getBeg())) {
                    if (remove.remove(neighbor)) addClone(neighbor, cpy, map);
                }
                for (IAtom neighbor : org.getConnectedAtomsList(stereoBond.getEnd())) {
                    if (remove.remove(neighbor)) addClone(neighbor, cpy, map);
                }
            }
        }

        // Clone bonds except those involving removed atoms.
        count = org.getBondCount();
        for (int i = 0; i < count; i++) {
            // Check bond.
            final IBond bond = org.getBond(i);
            boolean removedBond = false;
            final int length = bond.getAtomCount();
            for (int k = 0; k < length; k++) {
                if (remove.contains(bond.getAtom(k))) {
                    removedBond = true;
                    break;
                }
            }

            // Clone/remove this bond?
            if (!removedBond) {
                IBond clone = null;
                try {
                    clone = (IBond) org.getBond(i).clone();
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
                assert clone != null;
                clone.setAtoms(new IAtom[]{map.get(bond.getBeg()), map.get(bond.getEnd())});
                cpy.addBond(clone);
            }
        }

        // Recompute hydrogen counts of neighbours of removed Hydrogens.
        for (IAtom aRemove : remove) {
            // Process neighbours.
            for (IAtom iAtom : org.getConnectedAtomsList(aRemove)) {
                final IAtom neighb = map.get(iAtom);
                if (neighb == null) continue; // since for the case of H2, neight H has a heavy atom neighbor
                neighb.setImplicitHydrogenCount((neighb.getImplicitHydrogenCount() == null ? 0 : neighb
                        .getImplicitHydrogenCount()) + 1);
            }
        }
        for (IAtom atom : cpy.atoms()) {
            if (atom.getImplicitHydrogenCount() == null) atom.setImplicitHydrogenCount(0);
        }
        cpy.addProperties(org.getProperties());
        cpy.setFlags(org.getFlags());

        return (cpy);
    }

    private static void addClone(IAtom atom, IAtomContainer mol, Map<IAtom, IAtom> map) {

        IAtom clonedAtom = null;
        try {
            clonedAtom = (IAtom) atom.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        mol.addAtom(clonedAtom);
        map.put(atom, clonedAtom);
    }

    /**
     * Copy the input container and suppress any explicit hydrogens. Only
     * hydrogens that can be represented as a hydrogen count value on the atom
     * are suppressed. If a copy is not needed please use {@link
     * #suppressHydrogens}.
     *
     * @param org the container from which to remove hydrogens
     * @return a copy of the input with suppressed hydrogens
     * @see #suppressHydrogens
     */
    public static IAtomContainer copyAndSuppressedHydrogens(IAtomContainer org) {
        try {
            return suppressHydrogens(org.clone());
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException("atom container could not be cloned");
        }
    }

    /**
     * Suppress any explicit hydrogens in the provided container. Only hydrogens
     * that can be represented as a hydrogen count value on the atom are
     * suppressed. The container is updated and no elements are copied, please
     * use either {@link #copyAndSuppressedHydrogens} if you would to preserve
     * the old instance.
     *
     * @param org the container from which to remove hydrogens
     * @return the input for convenience
     * @see #copyAndSuppressedHydrogens
     */
    public static IAtomContainer suppressHydrogens(IAtomContainer org) {

        boolean anyHydrogenPresent = false;
        for (IAtom atom : org.atoms()) {
            if ("H".equals(atom.getSymbol())) {
                anyHydrogenPresent = true;
                break;
            }
        }

        if (!anyHydrogenPresent) return org;

        // we need fast adjacency checks (to check for suppression and
        // update hydrogen counts)
        GraphUtil.EdgeToBondMap bondmap = GraphUtil.EdgeToBondMap.withSpaceFor(org);
        final int[][] graph = GraphUtil.toAdjList(org, bondmap);

        final int nOrgAtoms = org.getAtomCount();
        final int nOrgBonds = org.getBondCount();

        int nCpyAtoms = 0;
        int nCpyBonds = 0;

        final Set<IAtom> hydrogens = new HashSet<IAtom>(nOrgAtoms);
        final IAtom[] cpyAtoms = new IAtom[nOrgAtoms];

        // filter the original container atoms for those that can/can't
        // be suppressed
        for (int v = 0; v < nOrgAtoms; v++) {
            final IAtom atom = org.getAtom(v);
            if (suppressibleHydrogen(org, graph, bondmap, v)) {
                hydrogens.add(atom);
                incrementImplHydrogenCount(org.getAtom(graph[v][0]));
            } else {
                cpyAtoms[nCpyAtoms++] = atom;
            }
        }

        // none of the hydrogens could be suppressed - no changes need to be made
        if (hydrogens.isEmpty()) return org;

        org.setAtoms(Arrays.copyOf(cpyAtoms, nCpyAtoms));

        // we now update the bonds - we have auxiliary variable remaining that
        // bypasses the set membership checks if all suppressed bonds are found
        IBond[] cpyBonds = new IBond[nOrgBonds - hydrogens.size()];
        int remaining = hydrogens.size();

        for (final IBond bond : org.bonds()) {
            if (remaining > 0 && (hydrogens.contains(bond.getBeg()) || hydrogens.contains(bond.getEnd()))) {
                remaining--;
                continue;
            }
            cpyBonds[nCpyBonds++] = bond;
        }

        // we know how many hydrogens we removed and we should have removed the
        // same number of bonds otherwise the containers is a strange
        if (nCpyBonds != cpyBonds.length)
            throw new IllegalArgumentException("number of removed bonds was less than the number of removed hydrogens");

        org.setBonds(cpyBonds);

        List<IStereoElement> elements = new ArrayList<IStereoElement>();

        for (IStereoElement se : org.stereoElements()) {
            if (se instanceof ITetrahedralChirality) {
                ITetrahedralChirality tc = (ITetrahedralChirality) se;
                IAtom focus = tc.getChiralAtom();
                IAtom[] neighbors = tc.getLigands();
                boolean updated = false;
                for (int i = 0; i < neighbors.length; i++) {
                    if (hydrogens.contains(neighbors[i])) {
                        neighbors[i] = focus;
                        updated = true;
                    }
                }

                // no changes
                if (!updated) {
                    elements.add(tc);
                } else {
                    elements.add(new TetrahedralChirality(focus, neighbors, tc.getStereo()));
                }
            } else if (se instanceof IDoubleBondStereochemistry) {
                IDoubleBondStereochemistry db = (IDoubleBondStereochemistry) se;
                Conformation conformation = db.getStereo();

                IBond orgStereo = db.getStereoBond();
                IBond orgLeft = db.getBonds()[0];
                IBond orgRight = db.getBonds()[1];

                // we use the following variable names to refer to the
                // double bond atoms and substituents
                // x       y
                //  \     /
                //   u = v

                IAtom u = orgStereo.getBeg();
                IAtom v = orgStereo.getEnd();
                IAtom x = orgLeft.getOther(u);
                IAtom y = orgRight.getOther(v);

                // if xNew == x and yNew == y we don't need to find the
                // connecting bonds
                IAtom xNew = x;
                IAtom yNew = y;

                if (hydrogens.contains(x)) {
                    conformation = conformation.invert();
                    xNew = findOther(org, u, v, x);
                }

                if (hydrogens.contains(y)) {
                    conformation = conformation.invert();
                    yNew = findOther(org, v, u, y);
                }

                // no other atoms connected, invalid double-bond configuration?
                if (x == null || y == null) continue;

                // no changes
                if (x == xNew && y == yNew) {
                    elements.add(db);
                    continue;
                }

                // XXX: may perform slow operations but works for now
                IBond cpyLeft = xNew != x ? org.getBond(u, xNew) : orgLeft;
                IBond cpyRight = yNew != y ? org.getBond(v, yNew) : orgRight;

                elements.add(new DoubleBondStereochemistry(orgStereo, new IBond[]{cpyLeft, cpyRight}, conformation));
            }
        }

        org.setStereoElements(elements);

        // single electron and lone pairs are not really used but we update
        // them just in-case but we just use the inefficient AtomContainer
        // methods

        if (org.getSingleElectronCount() > 0) {
            Set<ISingleElectron> remove = new HashSet<ISingleElectron>();
            for (ISingleElectron se : org.singleElectrons()) {
                if (!hydrogens.contains(se.getAtom())) remove.add(se);
            }
            for (ISingleElectron se : remove) {
                org.removeSingleElectron(se);
            }
        }

        if (org.getLonePairCount() > 0) {
            Set<ILonePair> remove = new HashSet<ILonePair>();
            for (ILonePair lp : org.lonePairs()) {
                if (!hydrogens.contains(lp.getAtom())) remove.add(lp);
            }
            for (ILonePair lp : remove) {
                org.removeLonePair(lp);
            }
        }

        return org;
    }

    /**
     * Create an copy of the {@code org} structure with explicit hydrogens
     * removed. Stereochemistry is updated but up and down bonds in a depiction
     * may need to be recalculated (see. StructureDiagramGenerator).
     *
     * @param org The AtomContainer from which to remove the hydrogens
     * @return The molecule without hydrogens.
     * @cdk.keyword hydrogens, removal, suppress
     * @see #copyAndSuppressedHydrogens
     */
    public static IAtomContainer removeHydrogens(IAtomContainer org) {
        return copyAndSuppressedHydrogens(org);
    }

    /**
     * Is the {@code atom} a suppressible hydrogen and can be represented as
     * implicit. A hydrogen is suppressible if it is not an ion, not the major
     * isotope (i.e. it is a deuterium or tritium atom) and is not molecular
     * hydrogen.
     *
     * @param container the structure
     * @param atom      an atom in the structure
     * @return the atom is a hydrogen and it can be suppressed (implicit)
     */
    private static boolean suppressibleHydrogen(final IAtomContainer container, final IAtom atom) {
        // is the atom a hydrogen
        if (!"H".equals(atom.getSymbol())) return false;
        // is the hydrogen an ion?
        if (atom.getFormalCharge() != null && atom.getFormalCharge() != 0) return false;
        // is the hydrogen deuterium / tritium?
        if (atom.getMassNumber() != null && atom.getMassNumber() != 1) return false;
        // molecule hydrogen with implicit H?
        if (atom.getImplicitHydrogenCount() != null && atom.getImplicitHydrogenCount() != 0) return false;
        // molecule hydrogen
        List<IAtom> neighbors = container.getConnectedAtomsList(atom);
        if (neighbors.size() == 1 && (neighbors.get(0).getSymbol().equals("H") ||
                                      neighbors.get(0) instanceof IPseudoAtom)) return false;
        // what about bridging hydrogens?
        // hydrogens with atom-atom mapping?
        return true;
    }

    /**
     * Increment the implicit hydrogen count of the provided atom. If the atom
     * was a non-pseudo atom and had an unset hydrogen count an exception is
     * thrown.
     *
     * @param atom an atom to increment the hydrogen count of
     */
    private static void incrementImplHydrogenCount(final IAtom atom) {
        Integer hCount = atom.getImplicitHydrogenCount();

        if (hCount == null) {
            if (!(atom instanceof IPseudoAtom))
                throw new IllegalArgumentException("a non-pseudo atom had an unset hydrogen count");
            hCount = 0;
        }

        atom.setImplicitHydrogenCount(hCount + 1);
    }

    /**
     * Is the {@code atom} a suppressible hydrogen and can be represented as
     * implicit. A hydrogen is suppressible if it is not an ion, not the major
     * isotope (i.e. it is a deuterium or tritium atom) and is not molecular
     * hydrogen.
     *
     * @param container the structure
     * @param graph     adjacent list representation
     * @param v         vertex (atom index)
     * @return the atom is a hydrogen and it can be suppressed (implicit)
     */
    private static boolean suppressibleHydrogen(final IAtomContainer container, final int[][] graph, final GraphUtil.EdgeToBondMap bondmap, final int v) {

        IAtom atom = container.getAtom(v);

        // is the atom a hydrogen
        if (!"H".equals(atom.getSymbol())) return false;
        // is the hydrogen an ion?
        if (atom.getFormalCharge() != null && atom.getFormalCharge() != 0) return false;
        // is the hydrogen deuterium / tritium?
        if (atom.getMassNumber() != null && atom.getMassNumber() != 1) return false;
        // hydrogen is either not attached to 0 or 2 neighbors
        if (graph[v].length != 1) return false;
        // non-single bond
        if (bondmap.get(v, graph[v][0]).getOrder() != Order.SINGLE) return false;

        // okay the hydrogen has one neighbor, if that neighbor is a
        // hydrogen (i.e. molecular hydrogen) then we can not suppress it
        if ("H".equals(container.getAtom(graph[v][0]).getSymbol()))
            return false;
        // can not nicely suppress hydrogens on pseudo atoms
        if (container.getAtom(graph[v][0]) instanceof IPseudoAtom)
            return false;
        return true;
    }

    /**
     * Finds an neighbor connected to 'atom' which is not 'exclude1'
     * or 'exclude2'. If no neighbor exists - null is returned.
     *
     * @param container structure
     * @param atom      atom to find a neighbor of
     * @param exclude1  the neighbor should not be this atom
     * @param exclude2  the neighbor should also not be this atom
     * @return a neighbor of 'atom', null if not found
     */
    private static IAtom findOther(IAtomContainer container, IAtom atom, IAtom exclude1, IAtom exclude2) {
        for (IAtom neighbor : container.getConnectedAtomsList(atom)) {
            if (neighbor != exclude1 && neighbor != exclude2) return neighbor;
        }
        return null;
    }

    /**
     * Produces an AtomContainer without explicit Hs but with H count from one with Hs.
     * Hs bonded to more than one heavy atom are preserved.  The new molecule is a deep copy.
     *
     * @return         The mol without Hs.
     * @cdk.keyword    hydrogens, removal
     * @deprecated {@link #suppressHydrogens} will now not removed bridging hydrogens by default
     */
    @Deprecated
    public static IAtomContainer removeHydrogensPreserveMultiplyBonded(IAtomContainer ac) {
        return copyAndSuppressedHydrogens(ac);
    }

    /**
     * Produces an AtomContainer without explicit Hs (except those listed) but with H count from one with Hs.
     * The new molecule is a deep copy.
     *
     * @param  preserve  a list of H atoms to preserve.
     * @return           The mol without Hs.
     * @cdk.keyword      hydrogens, removal
     * @deprecated not used by the internal API {@link #suppressHydrogens} will
     *             now only suppress hydrogens that can be represent as a h count
     */
    @Deprecated
    private static IAtomContainer removeHydrogens(IAtomContainer ac, List<IAtom> preserve) {
        Map<IAtom, IAtom> map = new HashMap<IAtom, IAtom>();
        // maps original atoms to clones.
        List<IAtom> remove = new ArrayList<IAtom>();
        // lists removed Hs.

        // Clone atoms except those to be removed.
        IAtomContainer mol = ac.getBuilder().newInstance(IAtomContainer.class);
        int count = ac.getAtomCount();
        for (int i = 0; i < count; i++) {
            // Clone/remove this atom?
            IAtom atom = ac.getAtom(i);
            if (!suppressibleHydrogen(ac, atom) || preserve.contains(atom)) {
                IAtom a = null;
                try {
                    a = (IAtom) atom.clone();
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
                a.setImplicitHydrogenCount(0);
                mol.addAtom(a);
                map.put(atom, a);
            } else {
                remove.add(atom);
                // maintain list of removed H.
            }
        }

        // Clone bonds except those involving removed atoms.
        count = ac.getBondCount();
        for (int i = 0; i < count; i++) {
            // Check bond.
            final IBond bond = ac.getBond(i);
            IAtom atom0 = bond.getBeg();
            IAtom atom1 = bond.getEnd();
            boolean remove_bond = false;
            for (IAtom atom : bond.atoms()) {
                if (remove.contains(atom)) {
                    remove_bond = true;
                    break;
                }
            }

            // Clone/remove this bond?
            if (!remove_bond) {
                // if (!remove.contains(atoms[0]) && !remove.contains(atoms[1]))

                IBond clone = null;
                try {
                    clone = (IBond) ac.getBond(i).clone();
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
                clone.setAtoms(new IAtom[]{map.get(atom0), map.get(atom1)});
                mol.addBond(clone);
            }
        }

        // Recompute hydrogen counts of neighbours of removed Hydrogens.
        for (IAtom removeAtom : remove) {
            // Process neighbours.
            for (IAtom neighbor : ac.getConnectedAtomsList(removeAtom)) {
                final IAtom neighb = map.get(neighbor);
                neighb.setImplicitHydrogenCount(neighb.getImplicitHydrogenCount() + 1);
            }
        }

        return (mol);
    }

    /**
     * Sets a property on all <code>Atom</code>s in the given container.
     */
    public static void setAtomProperties(IAtomContainer container, Object propKey, Object propVal) {
        if (container != null) {
            for (IAtom atom : container.atoms()) {
                atom.setProperty(propKey, propVal);
            }
        }
    }

    /**
     *  A method to remove ElectronContainerListeners.
     *  ElectronContainerListeners are used to detect changes
     *  in ElectronContainers (like bonds) and to notifiy
     *  registered Listeners in the event of a change.
     *  If an object looses interest in such changes, it should
     *  unregister with this AtomContainer in order to improve
     *  performance of this class.
     */
    public static void unregisterElectronContainerListeners(IAtomContainer container) {
        for (IElectronContainer electronContainer : container.electronContainers())
            electronContainer.removeListener(container);
    }

    /**
     *  A method to remove AtomListeners.
     *  AtomListeners are used to detect changes
     *  in Atom objects within this AtomContainer and to notifiy
     *  registered Listeners in the event of a change.
     *  If an object looses interest in such changes, it should
     *  unregister with this AtomContainer in order to improve
     *  performance of this class.
     */
    public static void unregisterAtomListeners(IAtomContainer container) {
        for (IAtom atom : container.atoms())
            atom.removeListener(container);
    }

    /**
     * Compares this AtomContainer with another given AtomContainer and returns
     * the Intersection between them. <p>
     *
     * <b>Important Note</b> : This is not the maximum common substructure.
     *
     * @param  container1 an AtomContainer object
     * @param  container2 an AtomContainer object
     * @return            An AtomContainer containing the intersection between
     *                    container1 and container2
     */
    public static IAtomContainer getIntersection(IAtomContainer container1, IAtomContainer container2) {
        IAtomContainer intersection = container1.getBuilder().newInstance(IAtomContainer.class);

        for (int i = 0; i < container1.getAtomCount(); i++) {
            if (container2.contains(container1.getAtom(i))) {
                intersection.addAtom(container1.getAtom(i));
            }
        }
        for (int i = 0; i < container1.getElectronContainerCount(); i++) {
            if (container2.contains(container1.getElectronContainer(i))) {
                intersection.addElectronContainer(container1.getElectronContainer(i));
            }
        }
        return intersection;
    }

    /**
     * Constructs an array of Atom objects from an AtomContainer.
     * @param  container The original AtomContainer.
     * @return The array of Atom objects.
     */
    public static IAtom[] getAtomArray(IAtomContainer container) {
        IAtom[] ret = new IAtom[container.getAtomCount()];
        for (int i = 0; i < ret.length; ++i)
            ret[i] = container.getAtom(i);
        return ret;
    }

    /**
     * Constructs an array of Atom objects from a List of Atom objects.
     * @param  list The original List.
     * @return The array of Atom objects.
     */
    public static IAtom[] getAtomArray(List<IAtom> list) {
        IAtom[] ret = new IAtom[list.size()];
        for (int i = 0; i < ret.length; ++i)
            ret[i] = list.get(i);
        return ret;
    }

    /**
     * Constructs an array of Bond objects from an AtomContainer.
     * @param  container The original AtomContainer.
     * @return The array of Bond objects.
     */
    public static IBond[] getBondArray(IAtomContainer container) {
        IBond[] ret = new IBond[container.getBondCount()];
        for (int i = 0; i < ret.length; ++i)
            ret[i] = container.getBond(i);
        return ret;
    }

    /**
     * Constructs an array of Atom objects from a List of Atom objects.
     * @param  list The original List.
     * @return The array of Atom objects.
     */
    public static IBond[] getBondArray(List<IBond> list) {
        IBond[] ret = new IBond[list.size()];
        for (int i = 0; i < ret.length; ++i)
            ret[i] = list.get(i);
        return ret;
    }

    /**
     * Constructs an array of Bond objects from an AtomContainer.
     * @param  container The original AtomContainer.
     * @return The array of Bond objects.
     */
    public static IElectronContainer[] getElectronContainerArray(IAtomContainer container) {
        IElectronContainer[] ret = new IElectronContainer[container.getElectronContainerCount()];
        for (int i = 0; i < ret.length; ++i)
            ret[i] = container.getElectronContainer(i);
        return ret;
    }

    /**
     * Constructs an array of Atom objects from a List of Atom objects.
     * @param  list The original List.
     * @return The array of Atom objects.
     */
    public static IElectronContainer[] getElectronContainerArray(List<IElectronContainer> list) {
        IElectronContainer[] ret = new IElectronContainer[list.size()];
        for (int i = 0; i < ret.length; ++i)
            ret[i] = list.get(i);
        return ret;
    }

    /**
     * Convenience method to perceive atom types for all <code>IAtom</code>s in the
     * <code>IAtomContainer</code>, using the <code>CDKAtomTypeMatcher</code>. If the
     * matcher finds a matching atom type, the <code>IAtom</code> will be configured
     * to have the same properties as the <code>IAtomType</code>. If no matching atom
     * type is found, no configuration is performed.
     * <b>This method overwrites existing values.</b>
     *
     * @param container
     * @throws CDKException
     */
    public static void percieveAtomTypesAndConfigureAtoms(IAtomContainer container) throws CDKException {
        CDKAtomTypeMatcher matcher = CDKAtomTypeMatcher.getInstance(container.getBuilder());
        for (IAtom atom : container.atoms()) {
            IAtomType matched = matcher.findMatchingAtomType(container, atom);
            if (matched != null) AtomTypeManipulator.configure(atom, matched);
        }
    }

    /**
     * Convenience method to perceive atom types for all <code>IAtom</code>s in the
     * <code>IAtomContainer</code>, using the <code>CDKAtomTypeMatcher</code>. If the
     * matcher finds a matching atom type, the <code>IAtom</code> will be configured
     * to have the same properties as the <code>IAtomType</code>. If no matching atom
     * type is found, no configuration is performed.
     * <b>This method overwrites existing values.</b>
     *
     * @param container
     * @throws CDKException
     */
    public static void percieveAtomTypesAndConfigureUnsetProperties(IAtomContainer container) throws CDKException {
        CDKAtomTypeMatcher matcher = CDKAtomTypeMatcher.getInstance(container.getBuilder());
        for (IAtom atom : container.atoms()) {
            IAtomType matched = matcher.findMatchingAtomType(container, atom);
            if (matched != null) AtomTypeManipulator.configureUnsetProperties(atom, matched);
        }
    }

    /**
     * This method will reset all atom configuration to UNSET.
     *
     * This method is the reverse of {@link #percieveAtomTypesAndConfigureAtoms(org.openscience.cdk.interfaces.IAtomContainer)}
     * and after a call to this method all atoms will be "unconfigured".
     *
     * Note that it is not a complete reversal of {@link #percieveAtomTypesAndConfigureAtoms(org.openscience.cdk.interfaces.IAtomContainer)}
     * since the atomic symbol of the atoms remains unchanged. Also, all the flags that were set
     * by the configuration method (such as IS_HYDROGENBOND_ACCEPTOR or ISAROMATIC) will be set to False.
     *
     * @param container The molecule, whose atoms are to be unconfigured
     * @see #percieveAtomTypesAndConfigureAtoms(org.openscience.cdk.interfaces.IAtomContainer)
     */
    public static void clearAtomConfigurations(IAtomContainer container) {
        for (IAtom atom : container.atoms()) {
            atom.setAtomTypeName((String) CDKConstants.UNSET);
            atom.setMaxBondOrder((IBond.Order) CDKConstants.UNSET);
            atom.setBondOrderSum((Double) CDKConstants.UNSET);
            atom.setCovalentRadius((Double) CDKConstants.UNSET);
            atom.setValency((Integer) CDKConstants.UNSET);
            atom.setFormalCharge((Integer) CDKConstants.UNSET);
            atom.setHybridization((IAtomType.Hybridization) CDKConstants.UNSET);
            atom.setFormalNeighbourCount((Integer) CDKConstants.UNSET);
            atom.setFlag(CDKConstants.IS_HYDROGENBOND_ACCEPTOR, false);
            atom.setFlag(CDKConstants.IS_HYDROGENBOND_DONOR, false);
            atom.setProperty(CDKConstants.CHEMICAL_GROUP_CONSTANT, CDKConstants.UNSET);
            atom.setFlag(CDKConstants.ISAROMATIC, false);
            atom.setProperty("org.openscience.cdk.renderer.color", CDKConstants.UNSET);
            atom.setExactMass((Double) CDKConstants.UNSET);
        }
    }

    /**
     * Returns the sum of bond orders, where a single bond counts as one
     * <i>single bond equivalent</i>, a double as two, etc.
     */
    public static int getSingleBondEquivalentSum(IAtomContainer container) {
        int sum = 0;
        for (IBond bond : container.bonds()) {
            IBond.Order order = bond.getOrder();
            if (order != null) {
                sum += order.numeric();
            }
        }
        return sum;
    }

    public static IBond.Order getMaximumBondOrder(IAtomContainer container) {
        return BondManipulator.getMaximumBondOrder(container.bonds().iterator());
    }

    /**
     * Returns a set of nodes excluding all the hydrogens.
     *
     * @return         The heavyAtoms value
     * @cdk.keyword    hydrogens, removal
     */
    public static List<IAtom> getHeavyAtoms(IAtomContainer container) {
        List<IAtom> newAc = new ArrayList<IAtom>();
        for (int f = 0; f < container.getAtomCount(); f++) {
            if (!container.getAtom(f).getSymbol().equals("H")) {
                newAc.add(container.getAtom(f));
            }
        }
        return newAc;
    }

    /**
     * Generates a cloned atomcontainer with all atoms being carbon, all bonds
     * being single non-aromatic
     *
     * @param atomContainer The input atomcontainer
     * @return The new atomcontainer
     * @throws CloneNotSupportedException The atomcontainer cannot be cloned
     * @deprecated not all attributes are removed producing unexpected results, use
     *             {@link #anonymise}
     */
    public static IAtomContainer createAllCarbonAllSingleNonAromaticBondAtomContainer(IAtomContainer atomContainer)
            throws CloneNotSupportedException {
        IAtomContainer query = (IAtomContainer) atomContainer.clone();
        for (int i = 0; i < query.getBondCount(); i++) {
            query.getBond(i).setOrder(IBond.Order.SINGLE);
            query.getBond(i).setFlag(CDKConstants.ISAROMATIC, false);
            query.getBond(i).setFlag(CDKConstants.SINGLE_OR_DOUBLE, false);
            query.getBond(i).getBeg().setSymbol("C");
            query.getBond(i).getBeg().setHybridization(null);
            query.getBond(i).getEnd().setSymbol("C");
            query.getBond(i).getEnd().setHybridization(null);
            query.getBond(i).getBeg().setFlag(CDKConstants.ISAROMATIC, false);
            query.getBond(i).getEnd().setFlag(CDKConstants.ISAROMATIC, false);
        }
        return query;
    }

    /**
     * Anonymise the provided container to single-bonded carbon atoms. No
     * information other then the connectivity from the original container is
     * retrained.
     *
     * @param src an atom container
     * @return anonymised container
     */
    public static IAtomContainer anonymise(IAtomContainer src) {

        IChemObjectBuilder builder = src.getBuilder();

        IAtom[] atoms = new IAtom[src.getAtomCount()];
        IBond[] bonds = new IBond[src.getBondCount()];

        for (int i = 0; i < atoms.length; i++) {
            atoms[i] = builder.newInstance(IAtom.class, "C");
        }
        for (int i = 0; i < bonds.length; i++) {
            IBond bond = src.getBond(i);
            int u = src.indexOf(bond.getBeg());
            int v = src.indexOf(bond.getEnd());
            bonds[i] = builder.newInstance(IBond.class, atoms[u], atoms[v]);
        }

        IAtomContainer dest = builder.newInstance(IAtomContainer.class, 0, 0, 0, 0);
        dest.setAtoms(atoms);
        dest.setBonds(bonds);
        return dest;
    }

    /**
     * Create a skeleton copy of the provided structure. The skeleton copy is
     * similar to an anonymous copy ({@link #anonymise}) except that atom
     * elements are preserved. All bonds are converted to single bonds and a
     * 'clean' atom is created for the input elements. The 'clean' atom has
     * unset charge, mass, and hydrogen count.
     *
     * @param src input structure
     * @return the skeleton copy
     */
    public static IAtomContainer skeleton(IAtomContainer src) {

        IChemObjectBuilder builder = src.getBuilder();

        IAtom[] atoms = new IAtom[src.getAtomCount()];
        IBond[] bonds = new IBond[src.getBondCount()];

        for (int i = 0; i < atoms.length; i++) {
            atoms[i] = builder.newInstance(IAtom.class, src.getAtom(i).getAtomicNumber());
        }
        for (int i = 0; i < bonds.length; i++) {
            IBond bond = src.getBond(i);
            int u = src.indexOf(bond.getBeg());
            int v = src.indexOf(bond.getEnd());
            bonds[i] = builder.newInstance(IBond.class, atoms[u], atoms[v]);
        }

        IAtomContainer dest = builder.newInstance(IAtomContainer.class, 0, 0, 0, 0);
        dest.setAtoms(atoms);
        dest.setBonds(bonds);
        return dest;
    }

    /**
     * Returns the sum of the bond order equivalents for a given IAtom. It
     * considers single bonds as 1.0, double bonds as 2.0, triple bonds as 3.0,
     * and quadruple bonds as 4.0.
     *
     * @param  atom  The atom for which to calculate the bond order sum
     * @return       The number of bond order equivalents for this atom
     */
    public static double getBondOrderSum(IAtomContainer container, IAtom atom) {
        double count = 0;
        for (IBond bond : container.getConnectedBondsList(atom)) {
            IBond.Order order = bond.getOrder();
            if (order != null) {
                count += order.numeric();
            }
        }
        return count;
    }

    /**
     * Assigns {@link CDKConstants#SINGLE_OR_DOUBLE} flags to the bonds of
     * a container. The single or double flag indicates uncertainty of bond
     * order and in this case is assigned to all aromatic bonds (and atoms)
     * which occur in rings. If any such bonds are found the flag is also set
     * on the container.
     *
     * <blockquote><pre>
     *     SmilesParser parser = new SmilesParser(...);
     *     parser.setPreservingAromaticity(true);
     *
     *     IAtomContainer biphenyl = parser.parseSmiles("c1cccc(c1)c1ccccc1");
     *
     *     AtomContainerManipulator.setSingleOrDoubleFlags(biphenyl);
     * </pre></blockquote>
     *
     * @param ac container to which the flags are assigned
     * @return the input for convenience
     */
    public static IAtomContainer setSingleOrDoubleFlags(IAtomContainer ac) {
        // note - we could check for any aromatic bonds to avoid RingSearch but
        // RingSearch is fast enough it probably wouldn't do much to check
        // before hand
        RingSearch rs = new RingSearch(ac);
        boolean singleOrDouble = false;
        for (IBond bond : rs.ringFragments().bonds()) {
            if (bond.getFlag(CDKConstants.ISAROMATIC)) {
                bond.setFlag(SINGLE_OR_DOUBLE, true);
                bond.getBeg().setFlag(SINGLE_OR_DOUBLE, true);
                bond.getEnd().setFlag(SINGLE_OR_DOUBLE, true);
                singleOrDouble = singleOrDouble | true;
            }
        }
        if (singleOrDouble) {
            ac.setFlag(CDKConstants.SINGLE_OR_DOUBLE, true);
        }
        return ac;
    }
}
