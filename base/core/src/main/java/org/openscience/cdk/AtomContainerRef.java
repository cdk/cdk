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

package org.openscience.cdk;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;
import org.openscience.cdk.interfaces.IElectronContainer;
import org.openscience.cdk.interfaces.ILonePair;
import org.openscience.cdk.interfaces.ISingleElectron;
import org.openscience.cdk.interfaces.IStereoElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * An AtomContainerRef wraps an existing {@link IAtomContainer} and
 * manages {@link AtomRef}s and {@link BondRef}s and provides faster
 * access methods.
 * <br>
 * Currently the AtomContainerRef is read-only and any attempt to modify
 * the container will throw an {@link UnsupportedOperationException}.
 *
 * <pre>{@code
 * IAtomContainer   ac    = ...;
 *
 * IAtom atom = ac.getAtom(0);
 * ac.getConnectedBonds(atom); // linear, O(m)
 * ac.getAtomNumber(atom); // linear, O(n)
 *
 * AtomContainerRef acref = new AtomContainerRef(mol);
 *
 * AtomRef atomref = ac.getAtom(0);
 * acref.getConnectedBonds(atomref); // constant, O(~1)
 * acref.getAtomNumber(atomref); // constant, O(~1)
 *
 * atomref.bonds(); // constant, O(1) - better
 * atomref.getIndex(); // constant, O(1) - better
 * }</pre>
 *
 */
public final class AtomContainerRef implements IAtomContainer {

    private       IAtomContainer      base;
    private final Map<IAtom, AtomRef> amap;
    private final AtomRef             arefs[];
    private final BondRef             brefs[];

    public AtomContainerRef(IAtomContainer base) {
        this.base = base;
        final int numAtoms = base.getAtomCount();
        final int numBonds = base.getBondCount();
        this.arefs = new AtomRef[numAtoms];
        this.brefs = new BondRef[numBonds];

        this.amap = new IdentityHashMap<>(base.getAtomCount());

        for (int i = 0; i < numAtoms; i++) {
            final IAtom atom = base.getAtom(i);
            final AtomRef atomref = new AtomRef(i,
                                                atom,
                                                new ArrayList<BondRef>());
            amap.put(atomref.atom, atomref);
            arefs[i] = atomref;
        }
        for (int i = 0; i < numBonds; i++) {
            final IBond   bond    = base.getBond(i);
            AtomRef       beg     = amap.get(bond.getAtom(0));
            AtomRef       end     = amap.get(bond.getAtom(1));
            final BondRef bondref = new BondRef(bond, i, beg, end);
            brefs[i] = bondref;
            beg.bonds.add(bondref);
            end.bonds.add(bondref);
        }
    }

    private AtomRef getAtomRef(IAtom atom) {
        AtomRef aref;
        if (atom.getClass() == AtomRef.class) {
            aref = (AtomRef) atom;
        } else {
            aref = amap.get(atom);
            if (aref == null) throw new IllegalArgumentException("Atom does not belong to AtomContainer");
        }
        return aref;
    }

    /** {@inheritDoc} */
    @Override
    public int getAtomCount() {
        return arefs.length;
    }

    /** {@inheritDoc} */
    @Override
    public int getBondCount() {
        return brefs.length;
    }

    /** {@inheritDoc} */
    @Override
    public AtomRef getAtom(int i) {
        return arefs[i];
    }

    /** {@inheritDoc} */
    @Override
    public BondRef getBond(int i) {
        return brefs[i];
    }

    /** {@inheritDoc} */
    @Override
    public int getAtomNumber(IAtom atom) {
        if (atom.getClass() == AtomRef.class)
            return ((AtomRef) atom).getIndex();
        AtomRef aref = amap.get(atom);
        if (aref == null) return -1;
        return aref.getIndex();
    }

    /** {@inheritDoc} */
    @Override
    public List<IAtom> getConnectedAtomsList(IAtom atom) {
        List<IBond> bonds = getConnectedBondsList(atom);
        List<IAtom> atoms = new ArrayList<>(bonds.size());
        for (IBond bond : bonds)
            atoms.add(bond.getConnectedAtom(atom));
        return atoms;
    }

    /** {@inheritDoc} */
    @Override
    public List<IBond> getConnectedBondsList(IAtom atom) {
        List<? extends IBond> brefs = getAtomRef(atom).bonds;
        return (List<IBond>) Collections.unmodifiableList(brefs);
    }

    /** {@inheritDoc} */
    @Override
    public int getConnectedAtomsCount(IAtom atom) {
        return getConnectedBondsCount(atom);
    }

    /** {@inheritDoc} */
    @Override
    public int getConnectedBondsCount(int i) {
        return arefs[i].getBondCount();
    }

    /** {@inheritDoc} */
    @Override
    public int getConnectedBondsCount(IAtom atom) {
        return getAtomRef(atom).getBondCount();
    }

    /** {@inheritDoc} */
    @Override
    public int getBondNumber(IAtom beg, IAtom end) {
        AtomRef aref = getAtomRef(beg);
        for (BondRef bond : aref.bonds())
            if (bond.contains(end))
                return bond.getIndex();
        return -1;
    }

    /** {@inheritDoc} */
    @Override
    public int getBondNumber(IBond bond) {
        if (bond.getClass() == BondRef.class)
            return ((BondRef) bond).getIndex();
        return getBondNumber(bond.getAtom(0), bond.getAtom(1));
    }

    /** {@inheritDoc} */
    @Override
    public IBond getBond(IAtom beg, IAtom end) {
        AtomRef aref = getAtomRef(beg);
        for (BondRef bond : aref.bonds())
            if (bond.contains(end))
                return bond;
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public IChemObjectBuilder getBuilder() {
        return base.getBuilder();
    }

    /** {@inheritDoc} */
    @Override
    public Iterable<IAtom> atoms() {
        return new Iterable<IAtom>() {
            @Override
            public Iterator<IAtom> iterator() {
                return new Iterator<IAtom>() {
                    int pos = 0;
                    @Override
                    public boolean hasNext() {
                        return pos < arefs.length;
                    }

                    @Override
                    public AtomRef next() {
                        return arefs[pos++];
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException("AtomContainerRef is read-only!");
                    }
                };
            }
        };
    }

    /** {@inheritDoc} */
    @Override
    public Iterable<IBond> bonds() {
        return new Iterable<IBond>() {
            @Override
            public Iterator<IBond> iterator() {
                return new Iterator<IBond>() {
                    int pos = 0;
                    @Override
                    public boolean hasNext() {
                        return pos < brefs.length;
                    }

                    @Override
                    public BondRef next() {
                        return brefs[pos++];
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException("AtomContainerRef is read-only!");
                    }
                };
            }
        };
    }

    /** {@inheritDoc} */
    @Override
    public Iterable<IStereoElement> stereoElements() {
        return base.stereoElements();
    }

    /** {@inheritDoc} */
    @Override
    public void setProperty(Object description, Object property) {
        base.setProperty(description, property);
    }

    /** {@inheritDoc} */
    @Override
    public ILonePair getLonePair(int number) {
        return base.getLonePair(number);
    }

    /** {@inheritDoc} */
    @Override
    public void removeProperty(Object description) {
        base.removeProperty(description);
    }

    /** {@inheritDoc} */
    @Override
    public ISingleElectron getSingleElectron(int number) {
        return base.getSingleElectron(number);
    }

    /** {@inheritDoc} */
    @Override
    public Iterable<ILonePair> lonePairs() {
        return base.lonePairs();
    }

    /** {@inheritDoc} */
    @Override
    public Iterable<ISingleElectron> singleElectrons() {
        return base.singleElectrons();
    }

    /** {@inheritDoc} */
    @Override
    public Iterable<IElectronContainer> electronContainers() {
        return base.electronContainers();
    }

    /** {@inheritDoc} */
    @Override
    public AtomRef getFirstAtom() {
        if (arefs.length == 0) throw new NoSuchElementException("AtomContainer has no atoms!");
        return arefs[0];
    }

    /** {@inheritDoc} */
    @Override
    public IAtom getLastAtom() {
        if (arefs.length == 0) throw new NoSuchElementException("AtomContainer has no atoms!");
        return arefs[arefs.length-1];
    }

    /** {@inheritDoc} */
    @Override
    public <T> T getProperty(Object description) {
        return base.getProperty(description);
    }

    /** {@inheritDoc} */
    @Override
    public Map<Object, Object> getProperties() {
        return base.getProperties();
    }

    /** {@inheritDoc} */
    @Override
    public <T> T getProperty(Object description, Class<T> c) {
        return base.getProperty(description, c);
    }

    /** {@inheritDoc} */
    @Override
    public int getLonePairNumber(ILonePair lonePair) {
        return base.getLonePairNumber(lonePair);
    }

    /** {@inheritDoc} */
    @Override
    public String getID() {
        return base.getID();
    }

    /** {@inheritDoc} */
    @Override
    public int getSingleElectronNumber(ISingleElectron singleElectron) {
        return base.getSingleElectronNumber(singleElectron);
    }

    /** {@inheritDoc} */
    @Override
    public void setID(String identifier) {
        base.setID(identifier);
    }

    /** {@inheritDoc} */
    @Override
    public IElectronContainer getElectronContainer(int number) {
        return base.getElectronContainer(number);
    }

    /** {@inheritDoc} */
    @Override
    public int getLonePairCount() {
        return base.getLonePairCount();
    }

    /** {@inheritDoc} */
    @Override
    public int getSingleElectronCount() {
        return base.getSingleElectronCount();
    }

    /** {@inheritDoc} */
    @Override
    public void setFlag(int mask, boolean value) {
        base.setFlag(mask, value);
    }

    /** {@inheritDoc} */
    @Override
    public boolean getFlag(int mask) {
        return base.getFlag(mask);
    }

    /** {@inheritDoc} */
    @Override
    public void setFlags(boolean[] newFlags) {
        base.setFlags(newFlags);
    }

    /** {@inheritDoc} */
    @Override
    public boolean[] getFlags() {
        return base.getFlags();
    }

    /** {@inheritDoc} */
    @Override
    public int getElectronContainerCount() {
        return base.getElectronContainerCount();
    }

    /** {@inheritDoc} */
    @Override
    public void setProperties(Map<Object, Object> properties) {
        base.setProperties(properties);
    }

    /** {@inheritDoc} */
    @Override
    public void addProperties(Map<Object, Object> properties) {
        base.addProperties(properties);
    }

    /** {@inheritDoc} */
    @Override
    public List<ILonePair> getConnectedLonePairsList(IAtom atom) {
        return base.getConnectedLonePairsList(atom);
    }

    /** {@inheritDoc} */
    @Override
    public List<ISingleElectron> getConnectedSingleElectronsList(IAtom atom) {
        return base.getConnectedSingleElectronsList(atom);
    }

    /** {@inheritDoc} */
    @Override
    public List<IElectronContainer> getConnectedElectronContainersList(IAtom atom) {
        return base.getConnectedElectronContainersList(atom);
    }

    /** {@inheritDoc} */
    @Override
    public Number getFlagValue() {
        return base.getFlagValue();
    }

    /** {@inheritDoc} */
    @Override
    public int getConnectedLonePairsCount(IAtom atom) {
        return base.getConnectedLonePairsCount(atom);
    }

    /** {@inheritDoc} */
    @Override
    public int getConnectedSingleElectronsCount(IAtom atom) {
        return base.getConnectedSingleElectronsCount(atom);
    }

    /** {@inheritDoc} */
    @Override
    public double getBondOrderSum(IAtom atom) {
        return base.getBondOrderSum(atom);
    }

    /** {@inheritDoc} */
    @Override
    public IBond.Order getMaximumBondOrder(IAtom atom) {
        return base.getMaximumBondOrder(atom);
    }

    /** {@inheritDoc} */
    @Override
    public IBond.Order getMinimumBondOrder(IAtom atom) {
        return base.getMinimumBondOrder(atom);
    }

    /** {@inheritDoc} */
    @Override
    public boolean contains(IAtom atom) {
        return getAtomNumber(atom) >= 0;
    }

    /** {@inheritDoc} */
    @Override
    public boolean contains(IBond bond) {
        return getBondNumber(bond) >= 0;
    }

    /** {@inheritDoc} */
    @Override
    public boolean contains(ILonePair lonePair) {
        return base.contains(lonePair);
    }

    /** {@inheritDoc} */
    @Override
    public boolean contains(ISingleElectron singleElectron) {
        return base.contains(singleElectron);
    }

    /** {@inheritDoc} */
    @Override
    public boolean contains(IElectronContainer electronContainer) {
        return base.contains(electronContainer);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isEmpty() {
        return base.isEmpty();
    }

    /* Connection Table, Set/Append/Delete Methods */

    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException not supported
     */
    @Override
    public void add(IAtomContainer atomContainer) {
        throw new UnsupportedOperationException("AtomContainerRef is read-only!");
    }

    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException not supported
     */
    @Override
    public void setBonds(IBond[] bonds) {
        throw new UnsupportedOperationException("AtomContainerRef is read-only!");
    }

    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException not supported
     */
    @Override
    public void setAtoms(IAtom[] atoms) {
        throw new UnsupportedOperationException("AtomContainerRef is read-only!");
    }

    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException not supported
     */
    @Override
    public void setAtom(int number, IAtom atom) {
        throw new UnsupportedOperationException("AtomContainerRef is read-only!");
    }

    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException not supported
     */
    @Override
    public void addAtom(IAtom atom) {
        throw new UnsupportedOperationException("AtomContainerRef is read-only!");
    }

    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException not supported
     */
    @Override
    public void addBond(IBond bond) {
        throw new UnsupportedOperationException("AtomContainerRef is read-only!");
    }

    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException not supported
     */
    @Override
    public void addLonePair(ILonePair lonePair) {
        throw new UnsupportedOperationException("AtomContainerRef is read-only!");
    }

    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException not supported
     */
    @Override
    public void addSingleElectron(ISingleElectron singleElectron) {
        throw new UnsupportedOperationException("AtomContainerRef is read-only!");
    }

    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException not supported
     */
    @Override
    public void addElectronContainer(IElectronContainer electronContainer) {
        throw new UnsupportedOperationException("AtomContainerRef is read-only!");
    }

    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException not supported
     */
    @Override
    public void addBond(int atom1, int atom2, IBond.Order order, IBond.Stereo stereo) {
        throw new UnsupportedOperationException("AtomContainerRef is read-only!");
    }

    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException not supported
     */
    @Override
    public void addBond(int atom1, int atom2, IBond.Order order) {
        throw new UnsupportedOperationException("AtomContainerRef is read-only!");
    }

    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException not supported
     */
    @Override
    public void addLonePair(int atomID) {
        throw new UnsupportedOperationException("AtomContainerRef is read-only!");
    }

    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException not supported
     */
    @Override
    public void addSingleElectron(int atomID) {
        throw new UnsupportedOperationException("AtomContainerRef is read-only!");
    }

    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException not supported
     */
    @Override
    public void addStereoElement(IStereoElement element) {
        throw new UnsupportedOperationException("AtomContainerRef is read-only!");
    }

    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException not supported
     */
    @Override
    public void setStereoElements(List<IStereoElement> elements) {
        throw new UnsupportedOperationException("AtomContainerRef is read-only!");
    }

    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException not supported
     */
    @Override
    public void remove(IAtomContainer atomContainer) {
        throw new UnsupportedOperationException("AtomContainerRef is read-only!");
    }

    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException not supported
     */
    @Override
    public void removeAtom(int position) {
        throw new UnsupportedOperationException("AtomContainerRef is read-only!");
    }

    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException not supported
     */
    @Override
    public void removeAtom(IAtom atom) {
        throw new UnsupportedOperationException("AtomContainerRef is read-only!");
    }

    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException not supported
     */
    @Override
    public IBond removeBond(int position) {
        throw new UnsupportedOperationException("AtomContainerRef is read-only!");
    }

    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException not supported
     */
    @Override
    public IBond removeBond(IAtom atom1, IAtom atom2) {
        throw new UnsupportedOperationException("AtomContainerRef is read-only!");
    }

    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException not supported
     */
    @Override
    public void removeBond(IBond bond) {
        throw new UnsupportedOperationException("AtomContainerRef is read-only!");
    }

    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException not supported
     */
    @Override
    public ILonePair removeLonePair(int position) {
        throw new UnsupportedOperationException("AtomContainerRef is read-only!");
    }

    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException not supported
     */
    @Override
    public void removeLonePair(ILonePair lonePair) {
        throw new UnsupportedOperationException("AtomContainerRef is read-only!");
    }

    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException not supported
     */
    @Override
    public ISingleElectron removeSingleElectron(int position) {
        throw new UnsupportedOperationException("AtomContainerRef is read-only!");
    }

    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException not supported
     */
    @Override
    public void removeSingleElectron(ISingleElectron singleElectron) {
        throw new UnsupportedOperationException("AtomContainerRef is read-only!");
    }

    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException not supported
     */
    @Override
    public IElectronContainer removeElectronContainer(int position) {
        throw new UnsupportedOperationException("AtomContainerRef is read-only!");
    }

    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException not supported
     */
    @Override
    public void removeElectronContainer(IElectronContainer electronContainer) {
        throw new UnsupportedOperationException("AtomContainerRef is read-only!");
    }

    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException not supported
     */
    @Override
    public void removeAtomAndConnectedElectronContainers(IAtom atom) {
        throw new UnsupportedOperationException("AtomContainerRef is read-only!");
    }

    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException not supported
     */
    @Override
    public void removeAllElements() {
        throw new UnsupportedOperationException("AtomContainerRef is read-only!");
    }

    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException not supported
     */
    @Override
    public void removeAllElectronContainers() {
        throw new UnsupportedOperationException("AtomContainerRef is read-only!");
    }

    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException not supported
     */
    @Override
    public void removeAllBonds() {
        throw new UnsupportedOperationException("AtomContainerRef is read-only!");
    }

    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException not supported
     */
    @Override
    public IAtomContainer clone() throws CloneNotSupportedException {
        throw new UnsupportedOperationException("AtomContainerRef is read-only!");
    }

    /* Notifications */

    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException not supported
     */
    @Override
    public void addListener(IChemObjectListener col) {
        throw new UnsupportedOperationException("Notifications not supported");
    }

    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException not supported
     */
    @Override
    public void stateChanged(IChemObjectChangeEvent event) {
        throw new UnsupportedOperationException("Notifications not supported");
    }

    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException not supported
     */
    @Override
    public int getListenerCount() {
        throw new UnsupportedOperationException("Notifications not supported");
    }

    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException not supported
     */
    @Override
    public void setNotification(boolean bool) {
        throw new UnsupportedOperationException("Notifications not supported");
    }

    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException not supported
     */
    @Override
    public boolean getNotification() {
        throw new UnsupportedOperationException("Notifications not supported");
    }

    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException not supported
     */
    @Override
    public void notifyChanged() {
        throw new UnsupportedOperationException("Notifications not supported");
    }

    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException not supported
     */
    @Override
    public void removeListener(IChemObjectListener col) {
        throw new UnsupportedOperationException("Notifications not supported");
    }

    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException not supported
     */
    @Override
    public void notifyChanged(IChemObjectChangeEvent evt) {
        throw new UnsupportedOperationException("Notifications not supported");
    }
}
