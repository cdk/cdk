/*
 * Copyright (c) 2017 John Mayfield <jwmay@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version. All we ask is that proper credit is given
 * for our work, which includes - but is not limited to - adding the above
 * copyright notice to the beginning of your source code files, and to any
 * copyright notice that you may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 */
package org.openscience.cdk.silent;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.NoSuchAtomException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.interfaces.IBond.Stereo;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IElectronContainer;
import org.openscience.cdk.interfaces.ILonePair;
import org.openscience.cdk.interfaces.IPDBAtom;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.ISingleElectron;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.AtomRef;
import org.openscience.cdk.BondRef;
import org.openscience.cdk.isomorphism.matchers.IQueryBond;
import org.openscience.cdk.sgroup.Sgroup;
import org.openscience.cdk.stereo.DoubleBondStereochemistry;
import org.openscience.cdk.stereo.ExtendedTetrahedral;
import org.openscience.cdk.stereo.TetrahedralChirality;
import org.openscience.cdk.tools.manipulator.SgroupManipulator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * This class should not be used directly.
 *
 * @author John Mayfield
 */
final class AtomContainer2 extends ChemObject implements IAtomContainer {

    private static final int DEFAULT_CAPACITY = 20;

    private BaseAtomRef[]     atoms;
    private BaseBondRef[]     bonds;
    private ILonePair[]       lonepairs;
    private ISingleElectron[] electrons;
    private List<IStereoElement> stereo = new ArrayList<>();

    private int numAtoms;
    private int numBonds;
    private int numLonePairs;
    private int numSingleElectrons;

    /**
     * Create a new container with the specified capacities.
     *
     * @param numAtoms           expected number of atoms
     * @param numBonds           expected number of bonds
     * @param numLonePairs       expected number of lone pairs
     * @param numSingleElectrons expected number of single electrons
     */
    AtomContainer2(int numAtoms,
                          int numBonds,
                          int numLonePairs,
                          int numSingleElectrons) {
        this.atoms = new BaseAtomRef[numAtoms];
        this.bonds = new BaseBondRef[numBonds];
        this.lonepairs = new ILonePair[numLonePairs];
        this.electrons = new ISingleElectron[numSingleElectrons];
    }

    /**
     * Constructs an empty AtomContainer.
     */
    AtomContainer2() {
        this(0, 0, 0, 0);
    }

    /**
     * Constructs a shallow copy of the provided IAtomContainer with the same
     * atoms, bonds, electron containers and stereochemistry of another
     * AtomContainer. Removing atoms/bonds in this copy will not affect
     * the original, however changing the properties will.
     *
     * @param src the source atom container
     */
    AtomContainer2(IAtomContainer src) {
        this(src.getAtomCount(),
             src.getBondCount(),
             src.getLonePairCount(),
             src.getSingleElectronCount());
        for (IAtom atom : src.atoms())
            addAtom(atom);
        for (IBond bond : src.bonds())
            addBond(bond);
        for (ISingleElectron se : src.singleElectrons())
            addSingleElectron(se);
        for (ILonePair lp : src.lonePairs())
            addLonePair(lp);
        for (IStereoElement se : src.stereoElements())
            addStereoElement(se);
    }

    private <T> T[] grow(T[] arr, int required) {
        int grow = arr.length == 0 ? DEFAULT_CAPACITY :
                   arr.length + (arr.length >> 1);
        if (grow < required)
            grow = required;
        return Arrays.copyOf(arr, grow);
    }

    private void ensureAtomCapacity(int required) {
        if (required >= atoms.length)
            atoms = grow(atoms, required);
    }

    private void ensureBondCapacity(int required) {
        if (required >= bonds.length)
            bonds = grow(bonds, required);
    }

    private void ensureLonePairCapacity(int required) {
        if (required >= lonepairs.length)
            lonepairs = grow(lonepairs, required);
    }

    private void ensureElectronCapacity(int required) {
        if (required >= electrons.length)
            electrons = grow(electrons, required);
    }

    private static IAtom unbox(IAtom atom) {
        while (atom instanceof AtomRef)
            atom = ((AtomRef) atom).deref();
        return atom;
    }

    private static IBond unbox(IBond bond) {
        while (bond instanceof BondRef)
            bond = ((BondRef) bond).deref();
        return bond;
    }

    private BaseAtomRef getAtomRefUnsafe(IAtom atom) {
        if (atom.getContainer() == this &&
            atoms[atom.getIndex()] == atom)
            return (BaseAtomRef) atom;
        atom = unbox(atom);
        for (int i = 0; i < numAtoms; i++)
            if (Objects.equals(atoms[i], atom))
                return atoms[i];
        return null;
    }

    private BaseAtomRef getAtomRef(IAtom atom) {
        BaseAtomRef atomref = getAtomRefUnsafe(atom);
        if (atomref == null)
            throw new NoSuchAtomException("Atom is not a member of this AtomContainer");
        return atomref;
    }

    private BaseAtomRef newAtomRef(IAtom atom) {
        // most common implementation we'll encounter..
        if (atom.getClass() == Atom.class)
            return new BaseAtomRef(this, atom);
        atom = unbox(atom);
        // re-check the common case now we've un-boxed
        if (atom.getClass() == Atom.class) {
            return new BaseAtomRef(this, atom);
        } else if (atom instanceof IPseudoAtom) {
            return new PsuedoAtomRef(this, (IPseudoAtom) atom);
        } else if (atom instanceof IQueryAtom) {
            return new QueryAtomRef(this, (IQueryAtom) atom);
        } else if (atom instanceof IPDBAtom) {
            return new PdbAtomRef(this, (IPDBAtom) atom);
        } else {
            return new BaseAtomRef(this, atom);
        }
    }

    private BondRef getBondRefUnsafe(IBond bond) {
        if (bond.getContainer() == this &&
            bonds[bond.getIndex()] == bond)
            return (BondRef) bond;
        bond = unbox(bond);
        for (int i = 0; i < numBonds; i++)
            if (bonds[i].deref() == bond)
                return bonds[i];
        return null;
    }

    private BaseBondRef newBondRef(IBond bond) {
        BaseAtomRef beg = bond.getBegin() == null ? null : getAtomRef(bond.getBegin());
        BaseAtomRef end = bond.getEnd() == null ? null : getAtomRef(bond.getEnd());
        if (bond.getClass() == Bond.class)
            return new BaseBondRef(this, bond, beg, end);
        bond = unbox(bond);
        if (bond instanceof IQueryBond)
            return new QueryBondRef(this, (IQueryBond) bond, beg, end);
        return new BaseBondRef(this, bond, beg, end);
    }

    private void addToEndpoints(BaseBondRef bondref) {
        if (bondref.getAtomCount() == 2) {
            BaseAtomRef beg = getAtomRef(bondref.getBegin());
            BaseAtomRef end = getAtomRef(bondref.getEnd());
            beg.bonds.add(bondref);
            end.bonds.add(bondref);
        } else {
            for (int i = 0; i < bondref.getAtomCount(); i++) {
                getAtomRef(bondref.getAtom(i)).bonds.add(bondref);
            }
        }
    }

    private void delFromEndpoints(BondRef bondref) {
        for (int i = 0; i < bondref.getAtomCount(); i++) {
            BaseAtomRef aref = getAtomRefUnsafe(bondref.getAtom(i));
            // atom may have already been deleted, naughty!
            if (aref != null)
                aref.bonds.remove(bondref);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addStereoElement(IStereoElement element) {
        stereo.add(element);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setStereoElements(List<IStereoElement> elements) {
        this.stereo.clear();
        this.stereo.addAll(elements);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<IStereoElement> stereoElements() {
        return Collections.unmodifiableList(stereo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAtoms(IAtom[] newatoms) {
        ensureAtomCapacity(newatoms.length);

        boolean reindexBonds = false;

        for (int i = 0; i < newatoms.length; i++) {
            // doing a move/reorder...
            if (newatoms[i].getContainer() == this) {
                atoms[i] = (BaseAtomRef) newatoms[i];
                atoms[i].setIndex(i);
            } else
            {
                atoms[i] = newAtomRef(newatoms[i]);
                atoms[i].setIndex(i);
                reindexBonds = true;
            }
        }

        // null-fill rest of the array
        if (newatoms.length < numAtoms) {
            Arrays.fill(this.atoms, newatoms.length, numAtoms, null);
        }
        numAtoms = newatoms.length;

        // ensure adjacency information is in sync, this code is only
        // called if the bonds are non-empty and 'external' atoms have been
        // added
        if (numBonds > 0 && reindexBonds) {
            for (int i = 0; i < numAtoms; i++)
                atoms[i].bonds.clear();
            for (int i = 0; i < numBonds; i++)
                addToEndpoints(bonds[i]);
        }

    }


    private void clearAdjacency() {
        for (int i = 0; i < numAtoms; i++) {
            atoms[i].bonds.clear();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBonds(IBond[] newbonds) {
        // replace existing bonds to clear their adjacency
        if (numBonds > 0) {
            clearAdjacency();
        }
        ensureBondCapacity(newbonds.length);
        for (int i = 0; i < newbonds.length; i++) {
            BaseBondRef bondRef = newBondRef(newbonds[i]);
            bondRef.setIndex(i);
            addToEndpoints(bondRef);
            bonds[i] = bondRef;
        }
        // null-fill
        if (newbonds.length < numBonds) {
            Arrays.fill(this.bonds, newbonds.length, numBonds, null);
        }
        numBonds = newbonds.length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAtom(int idx, IAtom atom) {
        if (atom == null)
            throw new NullPointerException("Null atom provided");
        if (contains(atom))
            throw new IllegalArgumentException("Atom already in container at index: " + indexOf(atom));
        if (idx < 0 || idx >= numAtoms)
            throw new IndexOutOfBoundsException("No current atom at index: " + idx);
        BaseAtomRef rep = newAtomRef(atom);
        BaseAtomRef org = atoms[idx];
        atoms[idx] = rep;
        atoms[idx].setIndex(idx);
        for (IBond bond : new ArrayList<>(org.bonds)) {
            if (bond.getBegin().equals(org))
                bond.setAtom(rep, 0);
            else if (bond.getEnd().equals(org))
                bond.setAtom(rep, 1);
        }

        // update single electrons and lone pairs
        for (ISingleElectron ec : singleElectrons()) {
            if (org.equals(ec.getAtom()))
                ec.setAtom(rep);
        }
        for (ILonePair lp : lonePairs()) {
            if (org.equals(lp.getAtom()))
                lp.setAtom(rep);
        }

        // update stereo
        for (int i = 0; i < this.stereo.size(); i++) {
            IStereoElement se = stereo.get(i);
            if (se.contains(org)) {
                Map<IAtom, IAtom> amap = Collections.<IAtom,IAtom>singletonMap(org, rep);
                Map<IBond, IBond> bmap = Collections.emptyMap();
                this.stereo.set(i, se.map(amap, bmap));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IAtom getAtom(int idx) {
        if (idx >= numAtoms)
            throw new IndexOutOfBoundsException("No atom at index: " + idx);
        return atoms[idx];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IBond getBond(int idx) {
        if (idx >= numBonds)
            throw new IndexOutOfBoundsException("No bond at index: " + idx);
        return bonds[idx];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ILonePair getLonePair(int idx) {
        if (idx >= numLonePairs)
            throw new NoSuchElementException("No lone pair at index: " + idx);
        return lonepairs[idx];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ISingleElectron getSingleElectron(int idx) {
        if (idx >= numSingleElectrons)
            throw new NoSuchElementException("No electron at index: " + idx);
        return electrons[idx];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<IAtom> atoms() {
        return new Iterable<IAtom>() {

            @Override
            public Iterator<IAtom> iterator() {
                return new AtomIterator();
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<IBond> bonds() {
        return new Iterable<IBond>() {

            @Override
            public Iterator<IBond> iterator() {
                return new BondIterator();
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<ILonePair> lonePairs() {
        return new Iterable<ILonePair>() {

            @Override
            public Iterator<ILonePair> iterator() {
                return new LonePairIterator();
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<ISingleElectron> singleElectrons() {
        return new Iterable<ISingleElectron>() {
            @Override
            public Iterator<ISingleElectron> iterator() {
                return new SingleElectronIterator();
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<IElectronContainer> electronContainers() {
        return new Iterable<IElectronContainer>() {
            @Override
            public Iterator<IElectronContainer> iterator() {
                return new ElectronContainerIterator();
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IAtom getFirstAtom() {
        return numAtoms > 0 ? atoms[0] : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IAtom getLastAtom() {
        return numAtoms > 0 ? atoms[numAtoms - 1] : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getAtomNumber(IAtom atom) {
        return indexOf(atom);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getBondNumber(IAtom beg, IAtom end) {
        return indexOf(getBond(beg, end));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getBondNumber(IBond bond) {
        return indexOf(bond);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getLonePairNumber(ILonePair lonePair) {
        return indexOf(lonePair);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSingleElectronNumber(ISingleElectron singleElectron) {
        return indexOf(singleElectron);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int indexOf(IAtom atom) {
        final AtomRef aref = getAtomRefUnsafe(atom);
        return aref == null ? -1 : aref.getIndex();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int indexOf(IBond bond) {
        final BondRef bref = getBondRefUnsafe(bond);
        return bref == null ? -1 : bref.getIndex();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int indexOf(ISingleElectron electron) {
        for (int i = 0; i < numSingleElectrons; i++) {
            if (electrons[i] == electron) return i;
        }
        return -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int indexOf(ILonePair pair) {
        for (int i = 0; i < numLonePairs; i++) {
            if (lonepairs[i] == pair) return i;
        }
        return -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IElectronContainer getElectronContainer(int number) {
        if (number < numBonds) return bonds[number];
        number -= numBonds;
        if (number < numLonePairs) return lonepairs[number];
        number -= numLonePairs;
        if (number < numSingleElectrons) return electrons[number];
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IBond getBond(IAtom beg, IAtom end) {
        final AtomRef begref = getAtomRefUnsafe(beg);
        return begref != null ? begref.getBond(end) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getAtomCount() {
        return numAtoms;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getBondCount() {
        return numBonds;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getLonePairCount() {
        return numLonePairs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSingleElectronCount() {
        return numSingleElectrons;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getElectronContainerCount() {
        return numBonds + numSingleElectrons + numLonePairs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<IAtom> getConnectedAtomsList(IAtom atom) {
        AtomRef aref = getAtomRef(atom);
        List<IAtom> nbrs = new ArrayList<>(aref.getBondCount());
        for (IBond bond : aref.bonds()) {
            nbrs.add(bond.getOther(atom));
        }
        return nbrs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<IBond> getConnectedBondsList(IAtom atom) {
        BaseAtomRef atomref = getAtomRef(atom);
        return new ArrayList<>(atomref.bonds);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ILonePair> getConnectedLonePairsList(IAtom atom) {
        getAtomRef(atom);
        List<ILonePair> lps = new ArrayList<>();
        for (int i = 0; i < numLonePairs; i++) {
            if (lonepairs[i].contains(atom)) lps.add(lonepairs[i]);
        }
        return lps;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ISingleElectron> getConnectedSingleElectronsList(IAtom atom) {
        getAtomRef(atom);
        List<ISingleElectron> ses = new ArrayList<>();
        for (int i = 0; i < numSingleElectrons; i++) {
            if (electrons[i].contains(atom)) ses.add(electrons[i]);
        }
        return ses;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<IElectronContainer> getConnectedElectronContainersList(
        IAtom atom) {
        List<IElectronContainer> ecs  = new ArrayList<>();
        AtomRef                  aref = getAtomRef(atom);
        for (IBond bond : aref.bonds()) {
            ecs.add(bond);
        }
        for (int i = 0; i < numLonePairs; i++) {
            if (lonepairs[i].contains(atom))
                ecs.add(lonepairs[i]);
        }
        for (int i = 0; i < numSingleElectrons; i++) {
            if (electrons[i].contains(atom))
                ecs.add(electrons[i]);
        }
        return ecs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getConnectedAtomsCount(IAtom atom) {
        return getConnectedBondsCount(atom);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getConnectedBondsCount(IAtom atom) {
        return getAtomRef(atom).getBondCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getConnectedBondsCount(int idx) {
        return getAtom(idx).getBondCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getConnectedLonePairsCount(IAtom atom) {
        // check atom is present
        getAtomRef(atom);
        int count = 0;
        for (int i = 0; i < numLonePairs; i++) {
            if (lonepairs[i].contains(atom)) ++count;
        }
        return count;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getConnectedSingleElectronsCount(IAtom atom) {
        getAtomRef(atom);
        int count = 0;
        for (int i = 0; i < numSingleElectrons; i++) {
            if (electrons[i].contains(atom)) ++count;
        }
        return count;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getBondOrderSum(IAtom atom) {
        double count = 0;
        for (IBond bond : getAtomRef(atom).bonds()) {
            Order order = bond.getOrder();
            if (order != null) {
                count += order.numeric();
            }
        }
        return count;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Order getMaximumBondOrder(IAtom atom) {
        IBond.Order max = null;
        for (IBond bond : getAtomRef(atom).bonds()) {
            if (max == null || bond.getOrder().numeric() > max.numeric()) {
                max = bond.getOrder();
            }
        }
        if (max == null) {
            if (atom.getImplicitHydrogenCount() != null &&
                atom.getImplicitHydrogenCount() > 0)
                max = Order.SINGLE;
            else
                max = Order.UNSET;
        }
        return max;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Order getMinimumBondOrder(IAtom atom) {
        IBond.Order min = null;
        for (IBond bond : getAtomRef(atom).bonds()) {
            if (min == null || bond.getOrder().numeric() < min.numeric()) {
                min = bond.getOrder();
            }
        }
        if (min == null) {
            if (atom.getImplicitHydrogenCount() != null &&
                atom.getImplicitHydrogenCount() > 0)
                min = Order.SINGLE;
            else
                min = Order.UNSET;
        }
        return min;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(IAtomContainer that) {

        // mark visited
        for (IAtom atom : that.atoms())
            atom.setFlag(CDKConstants.VISITED, false);
        for (IBond bond : that.bonds())
            bond.setFlag(CDKConstants.VISITED, false);
        for (IAtom atom : this.atoms())
            atom.setFlag(CDKConstants.VISITED, true);
        for (IBond bond : this.bonds())
            bond.setFlag(CDKConstants.VISITED, true);

        // do stereo elements first
        for (IStereoElement se : that.stereoElements()) {
            if (se instanceof TetrahedralChirality &&
                !((TetrahedralChirality) se).getChiralAtom().getFlag(CDKConstants.VISITED)) {
                this.addStereoElement(se);
            } else if (se instanceof DoubleBondStereochemistry &&
                       !((DoubleBondStereochemistry) se).getStereoBond().getFlag(CDKConstants.VISITED)) {
                this.addStereoElement(se);
            } else if (se instanceof ExtendedTetrahedral &&
                       !((ExtendedTetrahedral) se).focus().getFlag(CDKConstants.VISITED)) {
                this.addStereoElement(se);
            }
        }

        // append atoms/bonds not visited
        for (IAtom atom : that.atoms()) {
            if (!atom.getFlag(CDKConstants.VISITED)) {
                atom.setFlag(CDKConstants.VISITED, true);
                addAtom(atom);
            }
        }
        for (IBond bond : that.bonds()) {
            if (!bond.getFlag(CDKConstants.VISITED)) {
                bond.setFlag(CDKConstants.VISITED, true);
                addBond(bond);
            }
        }
        // linear indexOf.. but we expected there to be few electron/lone pairs
        // instances
        for (ISingleElectron se : that.singleElectrons()) {
            if (this.indexOf(se) < 0)
                addSingleElectron(se);
        }
        for (ILonePair lp : that.lonePairs()) {
            if (this.indexOf(lp) < 0)
                addLonePair(lp);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addAtom(IAtom atom) {
        if (contains(atom))
            return;
        ensureAtomCapacity(numAtoms + 1);
        final BaseAtomRef aref = newAtomRef(atom);
        aref.setIndex(numAtoms);
        atoms[numAtoms++] = aref;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addBond(IBond bond) {
        ensureBondCapacity(numBonds + 1);
        final BaseBondRef bref = newBondRef(bond);
        bref.setIndex(numBonds);
        addToEndpoints(bref);
        bonds[numBonds++] = bref;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addLonePair(ILonePair lp) {
        ensureLonePairCapacity(numLonePairs + 1);
        lonepairs[numLonePairs++] = lp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addSingleElectron(ISingleElectron e) {
        ensureElectronCapacity(numSingleElectrons + 1);
        electrons[numSingleElectrons++] = e;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addElectronContainer(IElectronContainer ec) {
        if (ec instanceof IBond)
            this.addBond((IBond) ec);
        if (ec instanceof ILonePair)
            this.addLonePair((ILonePair) ec);
        if (ec instanceof ISingleElectron)
            this.addSingleElectron((ISingleElectron) ec);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(IAtomContainer atomContainer) {
        // FIXME: can be better
        for (int f = 0; f < atomContainer.getBondCount(); f++) {
            removeBond(atomContainer.getBond(f));
        }
        for (int f = 0; f < atomContainer.getLonePairCount(); f++) {
            removeLonePair(atomContainer.getLonePair(f));
        }
        for (int f = 0; f < atomContainer.getSingleElectronCount(); f++) {
            removeSingleElectron(atomContainer.getSingleElectron(f));
        }
        for (int f = 0; f < atomContainer.getAtomCount(); f++) {
            removeAtom(atomContainer.getAtom(f));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAtomOnly(int idx) {
        if (idx >= 0 && idx < numAtoms) {
            numAtoms--;
            for (int i = idx; i < numAtoms; i++) {
                atoms[i] = atoms[i + 1];
                atoms[i].setIndex(i);
            }
            atoms[numAtoms] = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAtomOnly(IAtom atom) {
        removeAtomOnly(indexOf(atom));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IBond removeBond(int idx) {
        BondRef bond = null;
        if (idx >= 0 && idx < numBonds) {
            bond = bonds[idx];
            numBonds--;
            for (int i = idx; i < numBonds; i++) {
                bonds[i] = bonds[i + 1];
                bonds[i].setIndex(i);
            }
            delFromEndpoints(bond);
            bonds[numBonds] = null;
        }
        return bond;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IBond removeBond(IAtom beg, IAtom end) {
        return removeBond(indexOf(getBond(beg, end)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeBond(IBond bond) {
        removeBond(indexOf(bond));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ILonePair removeLonePair(int idx) {
        ILonePair lonepair = null;
        if (idx >= 0 && idx < numLonePairs) {
            lonepair = lonepairs[idx];
            numLonePairs--;
            System.arraycopy(lonepairs, idx + 1, lonepairs, idx, numLonePairs - idx);
            lonepairs[numLonePairs] = null;
        }
        return lonepair;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeLonePair(ILonePair lonePair) {
        removeLonePair(indexOf(lonePair));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ISingleElectron removeSingleElectron(int idx) {
        ISingleElectron electron = null;
        if (idx >= 0 && idx < numSingleElectrons) {
            electron = electrons[idx];
            numSingleElectrons--;
            System.arraycopy(electrons, idx + 1, electrons, idx, numSingleElectrons - idx);
            electrons[numSingleElectrons] = null;
        }
        return electron;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeSingleElectron(ISingleElectron electron) {
        removeSingleElectron(indexOf(electron));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IElectronContainer removeElectronContainer(int number) {
        if (number < numBonds)
            return removeBond(number);
        number -= numBonds;
        if (number < numLonePairs)
            return removeLonePair(number);
        number -= numLonePairs;
        if (number < numSingleElectrons)
            return removeSingleElectron(number);
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeElectronContainer(IElectronContainer ec) {
        if (ec instanceof IBond)
            removeBond((IBond) ec);
        else if (ec instanceof ILonePair)
            removeLonePair((ILonePair) ec);
        else if (ec instanceof ISingleElectron)
            removeSingleElectron((ISingleElectron) ec);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAtomAndConnectedElectronContainers(IAtom atom) {
        removeAtom(atom);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAtom(IAtom atom) {
        AtomRef atomref = getAtomRefUnsafe(atom);
        if (atomref != null) {
            if (atomref.getBondCount() > 0) {
                // update bonds
                int newNumBonds = 0;
                for (int i = 0; i < numBonds; i++) {
                    if (!bonds[i].contains(atom)) {
                        bonds[newNumBonds] = bonds[i];
                        bonds[newNumBonds].setIndex(newNumBonds);
                        newNumBonds++;
                    } else {
                        delFromEndpoints(bonds[i]);
                    }
                }
                numBonds = newNumBonds;
            }

            // update single electrons
            int newNumSingleElectrons = 0;
            for (int i = 0; i < numSingleElectrons; i++) {
                if (!electrons[i].contains(atom)) {
                    electrons[newNumSingleElectrons] = electrons[i];
                    newNumSingleElectrons++;
                }
            }
            numSingleElectrons = newNumSingleElectrons;

            // update lone pairs
            int newNumLonePairs = 0;
            for (int i = 0; i < numLonePairs; i++) {
                if (!lonepairs[i].contains(atom)) {
                    lonepairs[newNumLonePairs] = lonepairs[i];
                    newNumLonePairs++;
                }
            }
            numLonePairs = newNumLonePairs;

            List<IStereoElement> atomElements = new ArrayList<>();
            for (IStereoElement element : stereo) {
                if (element.contains(atom)) atomElements.add(element);
            }
            stereo.removeAll(atomElements);
            
            removeAtomOnly(atomref.getIndex());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAtom(int pos) {
        removeAtom(getAtom(pos));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAllElements() {
        removeAllElectronContainers();
        atoms = new BaseAtomRef[0];
        numAtoms = 0;
        stereo.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAllElectronContainers() {
        removeAllBonds();
        lonepairs = new ILonePair[0];
        electrons = new ISingleElectron[0];
        numLonePairs = 0;
        numSingleElectrons = 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAllBonds() {
        bonds = new BaseBondRef[0];
        numBonds = 0;
        clearAdjacency();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addBond(int beg, int end, Order order, Stereo stereo) {
        IBond bond = getBuilder().newBond();
        bond.setAtoms(new IAtom[]{getAtom(beg), getAtom(end)});
        bond.setOrder(order);
        bond.setStereo(stereo);
        addBond(bond);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addBond(int beg, int end, Order order) {
        addBond(beg, end, order, Stereo.NONE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addLonePair(int idx) {
        ILonePair lp = getBuilder().newInstance(ILonePair.class);
        lp.setAtom(getAtom(idx));
        addLonePair(lp);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addSingleElectron(int idx) {
        ISingleElectron electron = getBuilder().newInstance(ISingleElectron.class);
        electron.setAtom(getAtom(idx));
        addSingleElectron(electron);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(IAtom atom) {
        return indexOf(atom) >= 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(IBond bond) {
        return indexOf(bond) >= 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(ILonePair lonepair) {
        return indexOf(lonepair) >= 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(ISingleElectron electron) {
        return indexOf(electron) >= 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(IElectronContainer electronContainer) {
        if (electronContainer instanceof IBond)
            return contains((IBond) electronContainer);
        else if (electronContainer instanceof ILonePair)
            return contains((ILonePair) electronContainer);
        else if (electronContainer instanceof ISingleElectron)
            return contains((SingleElectron) electronContainer);
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitle() {
        return getProperty(CDKConstants.TITLE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTitle(String title) {
        setProperty(CDKConstants.TITLE, title);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(64);
        sb.append("AtomContainer(");
        sb.append(this.hashCode());
        if (getAtomCount() > 0) {
            sb.append(", #A:").append(getAtomCount());
            for (int i = 0; i < getAtomCount(); i++) {
                sb.append(", ").append(getAtom(i).toString());
            }
        }
        if (getBondCount() > 0) {
            sb.append(", #B:").append(getBondCount());
            for (int i = 0; i < getBondCount(); i++) {
                sb.append(", ").append(getBond(i).toString());
            }
        }
        if (getLonePairCount() > 0) {
            sb.append(", #LP:").append(getLonePairCount());
            for (int i = 0; i < getLonePairCount(); i++) {
                sb.append(", ").append(getLonePair(i).toString());
            }
        }
        if (getSingleElectronCount() > 0) {
            sb.append(", #SE:").append(getSingleElectronCount());
            for (int i = 0; i < getSingleElectronCount(); i++) {
                sb.append(", ").append(getSingleElectron(i).toString());
            }
        }
        if (stereo.size() > 0) {
            sb.append(", ST:[#").append(stereo.size());
            for (IStereoElement elements : stereo) {
                sb.append(", ").append(elements.toString());
            }
            sb.append(']');
        }
        sb.append(')');
        return sb.toString();
    }

    @Override
    public IAtomContainer shallowCopy() {
        return new AtomContainer2(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IAtomContainer clone() throws CloneNotSupportedException {
        // this is pretty wasteful as we need to delete most the data
        // we can't simply create an empty instance as the sub classes (e.g. AminoAcid)
        // would have a ClassCastException when they invoke clone
        AtomContainer2 clone = (AtomContainer2) super.clone();

        // remove existing elements - we need to set the stereo elements list as list.clone() doesn't
        // work as expected and will also remove all elements from the original
        clone.numAtoms = 0;
        clone.numBonds = 0;
        clone.numSingleElectrons = 0;
        clone.numLonePairs = 0;
        clone.atoms = new BaseAtomRef[0];
        clone.bonds = new BaseBondRef[0];
        clone.electrons = new ISingleElectron[0];
        clone.lonepairs = new ILonePair[0];
        clone.stereo = new ArrayList<>();
        clone.removeAllElements();

        // create a mapping of the original atoms/bonds to the cloned atoms/bonds
        // we need this mapping to correctly clone bonds, single/paired electrons
        // and stereo elements
        // - the expected size stop the map be resized - method from Google Guava
        Map<IAtom, IAtom> atomMap = new HashMap<>(numAtoms + (numAtoms >> 1));
        Map<IBond, IBond> bondMap = new HashMap<>(numBonds + (numBonds >> 1));

        // clone atoms
        IAtom[] atoms = new IAtom[this.numAtoms];
        for (int i = 0; i < atoms.length; i++) {
            atoms[i] = this.atoms[i].deref().clone();
        }
        clone.setAtoms(atoms);
        for (int i = 0; i < atoms.length; i++)
            atomMap.put(this.atoms[i], clone.getAtom(i));

        // clone bonds using a the mappings from the original to the clone
        IBond[] bonds = new IBond[this.numBonds];
        for (int i = 0; i < bonds.length; i++) {

            BondRef original = this.bonds[i];
            IBond   bond     = original.deref().clone();
            int     n        = bond.getAtomCount();
            IAtom[] members  = new IAtom[n];

            for (int j = 0; j < n; j++) {
                members[j] = atomMap.get(original.getAtom(j));
            }

            bond.setAtoms(members);
            bondMap.put(this.bonds[i], bond);
            bonds[i] = bond;
        }
        clone.setBonds(bonds);
        for (int i = 0; i < bonds.length; i++)
            bondMap.put(this.bonds[i], clone.getBond(i));

        // clone lone pairs (we can't use an array to buffer as there is no setLonePairs())
        for (int i = 0; i < numLonePairs; i++) {

            ILonePair original = this.lonepairs[i];
            ILonePair pair     = (ILonePair) original.clone();

            if (pair.getAtom() != null)
                pair.setAtom(atomMap.get(original.getAtom()));

            clone.addLonePair(pair);
        }

        // clone single electrons (we can't use an array to buffer as there is no setSingleElectrons())
        for (int i = 0; i < numSingleElectrons; i++) {

            ISingleElectron original = this.electrons[i];
            ISingleElectron electron = (ISingleElectron) original.clone();

            if (electron.getAtom() != null)
                electron.setAtom(atomMap.get(original.getAtom()));

            clone.addSingleElectron(electron);
        }

        // map each stereo element to a new instance in the clone
        for (IStereoElement element : stereo) {
            clone.addStereoElement(element.map(atomMap, bondMap));
        }

        // update sgroups
        Collection<Sgroup> sgroups = getProperty(CDKConstants.CTAB_SGROUPS);
        if (sgroups != null) {
            Map<IChemObject,IChemObject> replace = new HashMap<>();
            replace.putAll(atomMap);
            replace.putAll(bondMap);
            clone.setProperty(CDKConstants.CTAB_SGROUPS,
                              SgroupManipulator.copy(sgroups, replace));
        }

        return clone;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stateChanged(IChemObjectChangeEvent event) {
        // ignored
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return numAtoms == 0;
    }

    private class AtomIterator implements Iterator<IAtom> {

        private int idx = 0;

        @Override
        public boolean hasNext() {
            return idx < numAtoms;
        }

        @Override
        public IAtom next() {
            return atoms[idx++];
        }

        @Override
        public void remove() {
            removeAtomOnly(--idx);
        }
    }

    private class BondIterator implements Iterator<IBond> {

        private int idx = 0;

        @Override
        public boolean hasNext() {
            return idx < numBonds;
        }

        @Override
        public IBond next() {
            return bonds[idx++];
        }

        @Override
        public void remove() {
            removeBond(--idx);
        }

    }

    private class LonePairIterator implements Iterator<ILonePair> {

        private int idx = 0;

        @Override
        public boolean hasNext() {
            return idx < numLonePairs;
        }

        @Override
        public ILonePair next() {
            return lonepairs[idx++];
        }

        @Override
        public void remove() {
            removeLonePair(--idx);
        }

    }

    private class SingleElectronIterator implements Iterator<ISingleElectron> {

        private int idx = 0;

        @Override
        public boolean hasNext() {
            return idx < numSingleElectrons;
        }

        @Override
        public ISingleElectron next() {
            return electrons[idx++];
        }

        @Override
        public void remove() {
            removeSingleElectron(--idx);
        }

    }

    private class ElectronContainerIterator implements Iterator<IElectronContainer> {

        private int idx = 0;

        @Override
        public boolean hasNext() {
            return idx < (numBonds + numLonePairs + numSingleElectrons);
        }

        @Override
        public IElectronContainer next() {
            if (idx < numBonds)
                return bonds[idx++];
            else if (idx < numBonds + numLonePairs)
                return lonepairs[(idx++) - numBonds];
            else if (idx < numBonds + numLonePairs + numSingleElectrons)
                return electrons[(idx++) - (numBonds + numLonePairs)];
            return null;
        }

        @Override
        public void remove() {
            if (idx <= numBonds) {
                removeBond(--idx);
            } else if (idx <= numBonds + numLonePairs) {
                removeLonePair((--idx) - (numBonds));
            } else if (idx <= numBonds + numLonePairs + numSingleElectrons) {
                removeSingleElectron((--idx) - (numBonds - numLonePairs));
            }
        }

    }

    private static class BaseAtomRef extends AtomRef {

        private int idx = -1;
        private final IAtomContainer mol;
        private final List<IBond> bonds = new ArrayList<>(4);

        private BaseAtomRef(IAtomContainer mol, IAtom atom) {
            super(atom);
            this.mol = mol;
        }

        @Override
        public final int getIndex() {
            return idx;
        }

        public final void setIndex(int idx) {
            this.idx = idx;
        }

        @Override
        public final IAtomContainer getContainer() {
            return mol;
        }

        @Override
        public final int getBondCount() {
            return bonds.size();
        }

        @Override
        public final Iterable<IBond> bonds() {
            return bonds;
        }

        @Override
        public IBond getBond(IAtom atom) {
            for (IBond bond : bonds) {
                if (bond.getBegin().equals(atom) ||
                    bond.getEnd().equals(atom))
                    return bond;
            }
            return null;
        }

        @Override
        public int hashCode() {
            return deref().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof AtomRef)
                return deref().equals(((AtomRef) obj).deref());
            return deref().equals(obj);
        }
    }

    private static final class PsuedoAtomRef extends BaseAtomRef implements IPseudoAtom {

        private final IPseudoAtom pseudo;

        private PsuedoAtomRef(IAtomContainer mol, IPseudoAtom atom) {
            super(mol, atom);
            this.pseudo = atom;
        }

        @Override
        public String getLabel() {
            return pseudo.getLabel();
        }

        @Override
        public void setLabel(String label) {
            pseudo.setLabel(label);
        }

        @Override
        public int getAttachPointNum() {
            return pseudo.getAttachPointNum();
        }

        @Override
        public void setAttachPointNum(int ap) {
            pseudo.setAttachPointNum(ap);
        }

        @Override
        public int hashCode() {
            return deref().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof BondRef)
                return deref().equals(((BondRef) obj).deref());
            return deref().equals(obj);
        }

        @Override
        public IPseudoAtom clone() throws CloneNotSupportedException {
            return new PsuedoAtomRef(super.mol, pseudo.clone());
        }
    }

    private static final class QueryAtomRef extends BaseAtomRef implements IQueryAtom {
        private final IQueryAtom qatom;

        private QueryAtomRef(IAtomContainer mol, IQueryAtom atom) {
            super(mol, atom);
            this.qatom = atom;
        }

        @Override
        public boolean matches(IAtom atom) {
            return qatom.matches(atom);
        }
    }

    private static final class PdbAtomRef extends BaseAtomRef implements IPDBAtom {
        private final IPDBAtom pdbAtom;

        private PdbAtomRef(IAtomContainer mol, IPDBAtom atom) {
            super(mol, atom);
            this.pdbAtom = atom;
        }

        @Override
        public String getRecord() {
            return pdbAtom.getRecord();
        }

        @Override
        public void setRecord(String newRecord) {
            pdbAtom.setRecord(newRecord);
        }

        @Override
        public Double getTempFactor() {
            return pdbAtom.getTempFactor();
        }

        @Override
        public void setTempFactor(Double newTempFactor) {
            pdbAtom.setTempFactor(newTempFactor);
        }

        @Override
        public String getResName() {
            return pdbAtom.getResName();
        }

        @Override
        public void setResName(String newResName) {
            pdbAtom.setResName(newResName);
        }

        @Override
        public String getICode() {
            return pdbAtom.getICode();
        }

        @Override
        public void setICode(String newICode) {
            pdbAtom.setICode(newICode);
        }

        @Override
        public String getName() {
            return pdbAtom.getName();
        }

        @Override
        public void setName(String newName) {
            pdbAtom.setName(newName);
        }

        @Override
        public String getChainID() {
            return pdbAtom.getChainID();
        }

        @Override
        public void setChainID(String newChainID) {
            pdbAtom.setChainID(newChainID);
        }

        @Override
        public String getAltLoc() {
            return pdbAtom.getAltLoc();
        }

        @Override
        public void setAltLoc(String newAltLoc) {
            pdbAtom.setAltLoc(newAltLoc);
        }

        @Override
        public String getSegID() {
            return pdbAtom.getSegID();
        }

        @Override
        public void setSegID(String newSegID) {
            pdbAtom.setSegID(newSegID);
        }

        @Override
        public Integer getSerial() {
            return pdbAtom.getSerial();
        }

        @Override
        public void setSerial(Integer newSerial) {
            pdbAtom.setSerial(newSerial);
        }

        @Override
        public String getResSeq() {
            return pdbAtom.getResSeq();
        }

        @Override
        public void setResSeq(String newResSeq) {
            pdbAtom.setResSeq(newResSeq);
        }

        @Override
        public Boolean getOxt() {
            return pdbAtom.getOxt();
        }

        @Override
        public void setOxt(Boolean newOxt) {
            pdbAtom.setOxt(newOxt);
        }

        @Override
        public Boolean getHetAtom() {
            return pdbAtom.getHetAtom();
        }

        @Override
        public void setHetAtom(Boolean newHetAtom) {
            pdbAtom.setHetAtom(newHetAtom);
        }

        @Override
        public Double getOccupancy() {
            return pdbAtom.getOccupancy();
        }

        @Override
        public void setOccupancy(Double newOccupancy) {
            pdbAtom.setOccupancy(newOccupancy);
        }
    }

    private static class BaseBondRef extends BondRef {

        private       int            idx;
        private final AtomContainer2 mol;
        private       BaseAtomRef    beg, end;

        private BaseBondRef(AtomContainer2 mol, IBond bond, BaseAtomRef beg,
                            BaseAtomRef end) {
            super(bond);
            this.mol = mol;
            this.beg = beg;
            this.end = end;
        }

        @Override
        public int getIndex() {
            return idx;
        }

        public void setIndex(int idx) {
            this.idx = idx;
        }

        @Override
        public IAtomContainer getContainer() {
            return mol;
        }

        @Override
        public BaseAtomRef getBegin() {
            return beg;
        }

        @Override
        public BaseAtomRef getEnd() {
            return end;
        }

        @Override
        public IAtom getAtom(int idx) {
            switch (idx) {
                case 0:
                    return getBegin();
                case 1:
                    return getEnd();
                default:
                    return mol.getAtomRef(super.getAtom(idx));
            }
        }

        @Override
        public AtomRef getOther(IAtom atom) {
            if (atom == beg)
                return end;
            else if (atom == end)
                return beg;
            atom = AtomContainer2.unbox(atom);
            if (atom == beg.deref())
                return end;
            else if (atom == end.deref())
                return beg;
            return null;
        }

        @Override
        public void setAtoms(IAtom[] atoms) {
            assert atoms.length == 2;
            super.setAtoms(atoms);
            // check for swap: intended ref check
            if (atoms[0] == end && atoms[1] == beg) {
                BaseAtomRef tmp = beg;
                beg = end;
                end = tmp;
                return;
            }
            if (beg != null)
                beg.bonds.remove(this);
            if (end != null)
                end.bonds.remove(this);
            beg = mol.getAtomRef(atoms[0]);
            end = mol.getAtomRef(atoms[1]);
            beg.bonds.add(this);
            end.bonds.add(this);
        }

        @Override
        public void setAtom(IAtom atom, int idx) {
            super.setAtom(atom, idx);
            if (idx == 0) {
                if (beg != null)
                    beg.bonds.remove(this);
                beg = mol.getAtomRef(atom);
                beg.bonds.add(this);
            } else if (idx == 1) {
                if (end != null)
                    end.bonds.remove(this);
                end = mol.getAtomRef(atom);
                end.bonds.add(this);
            }
        }

        @Override
        public int hashCode() {
            return deref().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof BondRef)
                return deref().equals(((BondRef) obj).deref());
            return deref().equals(obj);
        }
    }

    private static final class QueryBondRef
        extends BaseBondRef
        implements IQueryBond {

        public QueryBondRef(AtomContainer2 mol,
                             IQueryBond bond,
                             BaseAtomRef beg,
                             BaseAtomRef end) {
            super(mol, bond, beg, end);
        }

        @Override
        public boolean matches(IBond bond) {
            return ((IQueryBond) deref()).matches(bond);
        }
    }

}
