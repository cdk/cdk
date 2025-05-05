/*
 * Copyright (C) 2025 The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.tools.manipulator;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IDoubleBondStereochemistry;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.ILonePair;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.ISingleElectron;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.interfaces.ITetrahedralChirality;
import org.openscience.cdk.sgroup.Sgroup;
import org.openscience.cdk.sgroup.SgroupKey;
import org.openscience.cdk.sgroup.SgroupType;
import org.openscience.cdk.stereo.DoubleBondStereochemistry;
import org.openscience.cdk.stereo.ExtendedCisTrans;
import org.openscience.cdk.stereo.ExtendedTetrahedral;
import org.openscience.cdk.tools.LoggingToolFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * This internal API is used converting between implicit and explicit hydrogens,
 * it should be accessed via {@link AtomContainerManipulator#normalizeHydrogens(IAtomContainer, HydrogenState)}.
 */
final class Hydrogens {

    /**
     * Is the {@code atom} a suppressible hydrogen and can be represented as
     * implicit. A hydrogen is safe to suppress if it is not an ion, not the major
     * isotope (i.e. it is a deuterium or tritium atom) and is not molecular
     * hydrogen.
     *
     * @param atom an atom in the structure
     * @return the atom is a hydrogen and it can be suppressed (implicit)
     */
    private static boolean isSafeToSuppress(final IAtom atom,
                                            Set<IAtom> xatoms) {
        // is the atom a hydrogen
        if (!isHydrogen(atom)) return false;
        // is the hydrogen an ion?
        if (atom.getFormalCharge() != null && atom.getFormalCharge() != 0) return false;
        // is the hydrogen deuterium / tritium?
        if (atom.getMassNumber() != null) return false;
        // mapped atom
        if (atom.getMapIdx() != 0) return false;
        // molecule hydrogen with implicit H?
        if (atom.getImplicitHydrogenCount() != null && atom.getImplicitHydrogenCount() != 0) return false;
        // molecule hydrogen
        List<IAtom> neighbors = new ArrayList<>();
        for (IBond bond : atom.bonds())
            neighbors.add(bond.getOther(atom));
        if (neighbors.size() == 1 && (neighbors.get(0).getAtomicNumber() == IElement.H ||
                                      neighbors.get(0) instanceof IPseudoAtom)) return false;
        if (xatoms.contains(atom))
            return false;
        return neighbors.size() == 1;
    }

    /**
     * Update the hydrogen count of any attached atom(s) by +1.
     *
     * @param hydrogen the hydrogen atom
     */
    private static void adjustNeighborHydrogenCount(final IAtom hydrogen) {
        if (hydrogen == null) return;
        for (IBond bond : hydrogen.bonds()) {
            IAtom nbor = bond.getOther(hydrogen);
            if (nbor == null) return;
            Integer hCount = nbor.getImplicitHydrogenCount();
            if (hCount == null)
                hCount = 0;
            nbor.setImplicitHydrogenCount(hCount + 1);
        }
    }

    private static Set<IAtom> getAllCrossingAtoms(IAtomContainer container) {
        Collection<Sgroup> sgroups = container.getProperty(CDKConstants.CTAB_SGROUPS);
        if (sgroups == null)
            return Collections.emptySet();
        Set<IAtom> xatoms = new HashSet<>();
        for (Sgroup sgroup : sgroups) {
            for (IBond bond : sgroup.getBonds()) {
                xatoms.add(bond.getBegin());
                xatoms.add(bond.getEnd());
            }
        }
        return xatoms;
    }

    // general purpose atom stereo update, tetrahedral, square planar, tbpy and
    // octahedral
    private static IStereoElement<IAtom, IAtom> update(IStereoElement<IAtom, IAtom> atomStereo,
                                                       Set<IAtom> atomsToSproutFrom,
                                                       Set<IAtom> atomsToContract) {
        IAtom focus = atomStereo.getFocus();
        if (atomsToSproutFrom.contains(focus)) {
            // update the carriers
            List<IAtom> explH = new ArrayList<>();
            for (IBond bond : focus.bonds()) {
                IAtom nbor = bond.getOther(focus);
                if (!atomStereo.getCarriers().contains(nbor))
                    explH.add(nbor);
            }
            // expl => impl
            return atomStereo.updateCarriers(focus, explH);
        } else {
            // impl => expl
            return atomStereo.updateCarriers(atomsToContract, focus);
        }
    }

    // allene like stereo (rare)
    private static IStereoElement<IAtom, IAtom> update(ExtendedTetrahedral tc,
                                                       Set<IAtom> atomsToSproutFrom,
                                                       Set<IAtom> atomsToContract) {
        IAtom focus = tc.getFocus();
        List<IAtom> carriers = new ArrayList<>(tc.getCarriers());
        IAtomContainer container = focus.getContainer();
        IAtom[] ends = ExtendedTetrahedral.findTerminalAtoms(container, focus);
        boolean updated = false;
        for (int i = 0; i < carriers.size(); i++) {
            IAtom atom = carriers.get(i);
            if (atomsToContract.contains(atom)) {
                if (container.getBond(atom, ends[0]) != null)
                    carriers.set(i, ends[0]);
                else
                    carriers.set(i, ends[1]);
                updated = true;
            } else if (atomsToSproutFrom.contains(atom) &&
                       atom.equals(ends[0]) ||
                       atom.equals(ends[1])) {
                // find the new explicit H neighbour
                for (IBond bond : atom.bonds()) {
                    IAtom nbor = bond.getOther(atom);
                    if (!carriers.contains(nbor) && nbor.getAtomicNumber() == IElement.H) {
                        carriers.set(i, nbor);
                        break;
                    }
                }
                updated = true;
            }
        }
        // no changes
        if (!updated) {
            return tc;
        } else {
            return new ExtendedTetrahedral(focus,
                                           carriers.toArray(new IAtom[4]),
                                           tc.getConfigOrder());
        }
    }

    /**
     * Finds a neighbor connected to 'atom' which is connected by a
     * single bond and is not 'exclude'.
     *
     * @param atom    atom to find a neighbor of
     * @param exclude the neighbor should not be this atom
     * @return a neighbor of 'atom', null if not found
     */
    private static IAtom findSingleBond(IAtom atom, IAtom exclude) {
        for (IBond bond : atom.bonds()) {
            if (bond.getOrder() != IBond.Order.SINGLE)
                continue;
            IAtom neighbor = bond.getOther(atom);
            if (!neighbor.equals(exclude))
                return neighbor;
        }
        return null;
    }

    private static IDoubleBondStereochemistry update(IDoubleBondStereochemistry db,
                                                     Set<IAtom> contract) {
        IDoubleBondStereochemistry.Conformation conformation = db.getStereo();

        IBond orgStereo = db.getStereoBond();
        IBond orgLeft = db.getBonds()[0];
        IBond orgRight = db.getBonds()[1];

        // we use the following variable names to refer to the
        // double bond atoms and substituents
        // x       y
        //  \     /
        //   u = v

        IAtom u = orgStereo.getBegin();
        IAtom v = orgStereo.getEnd();
        IAtom x = orgLeft.getOther(u);
        IAtom y = orgRight.getOther(v);

        // if xNew == x and yNew == y we don't need to find the
        // connecting bonds
        IAtom xNew = x;
        IAtom yNew = y;

        if (contract.contains(x)) {
            conformation = conformation.invert();
            xNew = findSingleBond(u, x);
        }

        if (contract.contains(y)) {
            conformation = conformation.invert();
            yNew = findSingleBond(v, y);
        }

        // no other atoms connected, invalid double-bond configuration
        // is removed. example [2H]/C=C/[H]
        if (x == null || y == null ||
            xNew == null || yNew == null) return null;

        // no changes
        if (x.equals(xNew) && y.equals(yNew)) {
            return db;
        }

        // XXX: may perform slow operations but works for now
        IBond cpyLeft = !Objects.equals(xNew, x) ? u.getBond(xNew) : orgLeft;
        IBond cpyRight = !Objects.equals(yNew, y) ? v.getBond(yNew) : orgRight;

        return new DoubleBondStereochemistry(orgStereo,
                                             new IBond[]{cpyLeft, cpyRight},
                                             conformation);
    }

    private static ExtendedCisTrans update(ExtendedCisTrans db, Set<IAtom> contract) {
        int config = db.getConfigOrder();

        IBond focus = db.getFocus();
        IBond orgLeft = db.getCarriers().get(0);
        IBond orgRight = db.getCarriers().get(1);

        // we use the following variable names to refer to the
        // extended double bond atoms and substituents
        // x       y
        //  \     /
        //   u===v
        IAtom[] ends = ExtendedCisTrans.findTerminalAtoms(focus.getContainer(), focus);
        if (ends == null) return null;
        IAtom u = ends[0];
        IAtom v = ends[1];
        IAtom x = orgLeft.getOther(u);
        IAtom y = orgRight.getOther(v);

        // if xNew == x and yNew == y we don't need to find the
        // connecting bonds
        IAtom xNew = x;
        IAtom yNew = y;

        if (contract.contains(x)) {
            config ^= 0x3;
            xNew = findSingleBond(u, x);
        }

        if (contract.contains(y)) {
            config ^= 0x3;
            yNew = findSingleBond(v, y);
        }

        // no other atoms connected, invalid double-bond configuration
        // is removed. example [2H]/C=C/[H]
        if (x == null || y == null ||
            xNew == null || yNew == null) return null;

        // no changes
        if (x.equals(xNew) && y.equals(yNew)) {
            return db;
        }

        // XXX: may perform slow operations but works for now
        IBond cpyLeft = !Objects.equals(xNew, x) ? u.getBond(xNew) : orgLeft;
        IBond cpyRight = !Objects.equals(yNew, y) ? v.getBond(yNew) : orgRight;

        return new ExtendedCisTrans(focus,
                                    new IBond[]{cpyLeft, cpyRight},
                                    config);
    }

    // stereo chemistry holds references to an atom/bond and the arrangement of
    // other atoms/bonds around it. When stereo involves a hydrogen which is not
    // explicitly represented we store the central/focus/chiral atom in the
    // carrier list as a place-holder. In inorganic stereo there may be more
    // than one. This code updates
    private static List<IStereoElement> updateStereochemistry(IAtomContainer container,
                                                              Set<IAtom> atomToSproutFrom,
                                                              Set<IAtom> atomsToContract) {
        List<IStereoElement> updated = new ArrayList<>();
        for (IStereoElement se : container.stereoElements()) {
            if (se.getConfigClass() == IStereoElement.Tetrahedral ||
                se.getConfigClass() == IStereoElement.SquarePlanar ||
                se.getConfigClass() == IStereoElement.TrigonalBipyramidal |
                se.getConfigClass() == IStereoElement.Octahedral) {
                @SuppressWarnings("unchecked")
                IStereoElement<IAtom, IAtom> atomStereo = se;
                updated.add(update(atomStereo, atomToSproutFrom, atomsToContract));
            } else if (se.getConfigClass() == IStereoElement.Allenal) {
                updated.add(update((ExtendedTetrahedral) se, atomToSproutFrom, atomsToContract));
            } else if (se.getConfigClass() == IStereoElement.CisTrans) {
                IDoubleBondStereochemistry tmp = update((IDoubleBondStereochemistry) se, atomsToContract);
                if (tmp != null)
                    updated.add(tmp);
            } else if (se.getConfigClass() == IStereoElement.Cumulene) {
                ExtendedCisTrans tmp = update((ExtendedCisTrans) se, atomsToContract);
                if (tmp != null)
                    updated.add(tmp);
            } else if (se.getConfigClass() == IStereoElement.Atropisomeric) {
                // no-op - this stereotype cannot have any hydrogens and be sane
                updated.add(se);
            } else {
                updated.add(se);
                LoggingToolFactory
                        .createLoggingTool(Hydrogens.class)
                        .warn("New/Unsupported stereo type? Hydrogens.updateStereochemistry needs updating to handle this");
            }
        }
        return updated;
    }

    // unlikely to have radical on a H but update all the same
    private static void updateRadicals(IAtomContainer container, Set<IAtom> contract) {
        if (container.getSingleElectronCount() > 0) {
            Set<ISingleElectron> remove = new HashSet<>();
            for (ISingleElectron se : container.singleElectrons()) {
                if (contract.contains(se.getAtom())) remove.add(se);
            }
            for (ISingleElectron se : remove) {
                container.removeSingleElectron(se);
            }
        }
    }

    // unlikely to have lone pairs on a H but update all the same
    private static void updateLonePairs(IAtomContainer container, Set<IAtom> contract) {
        if (container.getLonePairCount() > 0) {
            Set<ILonePair> remove = new HashSet<>();
            for (ILonePair lp : container.lonePairs()) {
                if (contract.contains(lp.getAtom())) remove.add(lp);
            }
            for (ILonePair lp : remove) {
                container.removeLonePair(lp);
            }
        }
    }

    // sgroups hold references to atoms/bonds which needed to be updated if
    // they are removed or new H atoms are added (e.g. in a polymer repeat)
    private static void updateSgroups(IAtomContainer container,
                                      Set<IAtom> atomsContracted,
                                      Set<IBond> bondsContracted,
                                      Set<IAtom> atomsToSproutFrom,
                                      Set<IAtom> sproutedAtoms) {
        List<Sgroup> sgroups = container.getProperty(CDKConstants.CTAB_SGROUPS);
        if (sgroups == null)
            return;
        sgroups = new ArrayList<>(sgroups); // ensure we can modify
        for (Iterator<Sgroup> iterator = sgroups.iterator(); iterator.hasNext(); ) {
            Sgroup sgroup = iterator.next();

            // if any bonds are removed the state is invalidated
            boolean invalid = false;
            for (IBond xbond : sgroup.getBonds()) {
                if (bondsContracted.contains(xbond)) {
                    invalid = true;
                    break;
                }
            }

            if (invalid) {
                iterator.remove();
                continue;
            }

            for (IAtom hydrogen : atomsContracted)
                sgroup.removeAtom(hydrogen);

            if (sgroup.getValue(SgroupKey.CtabParentAtomList) != null) {
                Collection<IAtom> pal = sgroup.getValue(SgroupKey.CtabParentAtomList);
                pal.removeAll(atomsContracted);
            }

            // any polymer/abbreviation should be updated if the hydrogen
            // is sprouted from its atoms
            if (sgroup.getType() != SgroupType.ExtMulticenter &&
                sgroup.getType() != SgroupType.ExtAttachOrdering) {
                for (IAtom atom : new ArrayList<>(sgroup.getAtoms())) {
                    if (atomsToSproutFrom.contains(atom)) {
                        for (IBond bond : atom.bonds()) {
                            IAtom nbor = bond.getOther(atom);
                            if (sproutedAtoms.contains(nbor))
                                sgroup.addAtom(nbor);
                        }
                    }
                }
            }
        }
        container.setProperty(CDKConstants.CTAB_SGROUPS, sgroups);
    }

    static void normalize(IAtomContainer container,
                          Set<IAtom> atomsToSproutFrom,
                          Set<IAtom> atomsContracted) {

        // nothing to do
        if (atomsContracted.isEmpty() && atomsToSproutFrom.isEmpty())
            return;

        final Set<IBond> bondsContracted = new HashSet<>();
        final Set<IAtom> spoutedAtoms = new HashSet<>();
        final List<IAtom> keptAtoms = new ArrayList<>();
        final List<IBond> keptBonds = new ArrayList<>();

        for (IAtom hydrogen : atomsContracted) {
            adjustNeighborHydrogenCount(hydrogen);
        }

        // 1a. create an auxiliary list of atoms that we will keep
        for (IAtom atom : container.atoms()) {
            if (!atomsContracted.contains(atom)) {
                keptAtoms.add(atom);
            }
        }

        // 1a. and a list of bonds to be kept
        for (final IBond bond : container.bonds()) {
            if ((atomsContracted.contains(bond.getBegin()) || atomsContracted.contains(bond.getEnd()))) {
                bondsContracted.add(bond);
            } else {
                keptBonds.add(bond);
            }
        }

        // 2. add on any new explicit hydrogens to the sprout set
        for (IAtom atom : atomsToSproutFrom) {
            Integer hcount = atom.getImplicitHydrogenCount();
            if (hcount == null) continue;
            for (int i = 0; i < hcount; i++) {
                IAtom h = container.newAtom(IElement.H, 0);
                h.setAtomTypeName("H");
                spoutedAtoms.add(h);
                keptAtoms.add(h);
                keptBonds.add(container.newBond(atom, keptAtoms.get(keptAtoms.size() - 1)));
            }
            atom.setImplicitHydrogenCount(0);
        }

        // 3. get update stereo elements, and ensure radicals, lone pairs, and Sgroups are all OK
        List<IStereoElement> updated = updateStereochemistry(container, atomsToSproutFrom, atomsContracted);
        updateRadicals(container, atomsContracted);
        updateLonePairs(container, atomsContracted);
        updateSgroups(container, atomsContracted, bondsContracted,
                      atomsToSproutFrom, spoutedAtoms);

        container.setAtoms(keptAtoms.toArray(new IAtom[0]));
        container.setBonds(keptBonds.toArray(new IBond[0]));
        container.setStereoElements(updated);
    }

    // does the provided atom have any bonded atoms which are contracted?
    private static boolean hasContractedNeighbor(IAtom atom, Set<IAtom> contract) {
        for (IBond bond : atom.bonds()) {
            IAtom nbor = bond.getOther(atom);
            if (contract.contains(nbor))
                return true;
        }
        return false;
    }

    // should this stereo element have explicit hydrogens for depiction?
    private static boolean shouldBeExplicit(IStereoElement<?, ?> se,
                                            IAtomContainer container) {
        switch (se.getConfigClass()) {
            case IStereoElement.Tetrahedral:
                @SuppressWarnings("unchecked")
                IStereoElement<IAtom, IAtom> atomStereo = (IStereoElement<IAtom, IAtom>) se;
                IAtom focus = atomStereo.getFocus();
                List<IAtom> carriers = atomStereo.getCarriers();
                int rbonds = 0;
                for (IAtom nbor : carriers) {
                    IBond bond = container.getBond(focus, nbor);
                    if (bond != null && bond.isInRing())
                        rbonds++;
                }
                int adjacentStereo = 0;
                for (IStereoElement<?, ?> otherStereo : container.stereoElements()) {
                    if (otherStereo instanceof ITetrahedralChirality) {
                        if (carriers.contains((IAtom) otherStereo.getFocus()))
                            adjacentStereo++;
                    }
                }
                return rbonds >= 3 || adjacentStereo >= 3;
            case IStereoElement.Allenal:
            case IStereoElement.CisTrans:
            case IStereoElement.SquarePlanar:
            case IStereoElement.Octahedral:
                // all of these stereochemistry types should have explicit
                // hydrogens in depictions
                return true;
        }
        return false;
    }

    // make the hydrogens in this stereo element explicit
    private static void makeExplicit(IStereoElement<?, ?> se,
                                     Set<IAtom> atomsToSproutFrom,
                                     Set<IAtom> atomsToContract) {
        if (se instanceof ITetrahedralChirality) {
            ITetrahedralChirality tc = (ITetrahedralChirality) se;
            IAtom focus = tc.getFocus();
            if (hasImplH(focus))
                atomsToSproutFrom.add(focus);
            for (IAtom atom : tc.getCarriers())
                atomsToContract.remove(atom);
        }
    }

    private static boolean hasImplH(IAtom atom) {
        return atom.getImplicitHydrogenCount() != null &&
               atom.getImplicitHydrogenCount() > 0;
    }

    private static boolean isHydrogen(IAtom atom) {
        return atom.getAtomicNumber() != null &&
               atom.getAtomicNumber() == IElement.H;
    }

    // this method finds the H atoms to contract and where they should be
    // sprouted based on the state option
    static void find(IAtomContainer container,
                     HydrogenState state,
                     Set<IAtom> atomsToContract,
                     Set<IAtom> atomsToSproutFrom) {
        Set<IAtom> xatoms = getAllCrossingAtoms(container);

        switch (state) {
            case Unsafe:
                for (IAtom atom : container.atoms()) {
                    if (isHydrogen(atom) &&
                        (atom.getBondCount() >= 1 && !hasContractedNeighbor(atom, atomsToContract)))
                        atomsToContract.add(atom);
                }
                break;
            case Minimal:
                for (IAtom atom : container.atoms()) {
                    if (isSafeToSuppress(atom, xatoms))
                        atomsToContract.add(atom);
                }
                break;
            case Depiction:
            case Stereo:
                for (IAtom atom : container.atoms()) {
                    if (isSafeToSuppress(atom, xatoms))
                        atomsToContract.add(atom);
                }
                // adjust the hydrogens base on stereo atoms
                for (IStereoElement<?, ?> se : container.stereoElements()) {
                    if (state == HydrogenState.Stereo || shouldBeExplicit(se, container))
                        makeExplicit(se, atomsToSproutFrom, atomsToContract);
                }
                break;
            case Explicit:
                for (IAtom atom : container.atoms()) {
                    if (hasImplH(atom))
                        atomsToSproutFrom.add(atom);
                }
                break;
            default:
                throw new IllegalArgumentException("Unimplemented option! " + state);
        }
    }

    // main entry point
    static void normalize(IAtomContainer container, HydrogenState type) {
        // JWM: note the order for the 'sprout' set important since some test
        //      (mainly in cdk-reaction) depend on the expected output indices
        //      we therefore use a linked hash set
        Set<IAtom> sprout = new LinkedHashSet<>();
        Set<IAtom> contract = new HashSet<>();
        find(container, type, contract, sprout);
        normalize(container, sprout, contract);
    }
}
