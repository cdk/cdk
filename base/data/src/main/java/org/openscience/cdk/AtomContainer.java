/* Copyright (C) 1997-2007  Christoph Steinbeck
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
package org.openscience.cdk;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;
import org.openscience.cdk.interfaces.IElectronContainer;
import org.openscience.cdk.interfaces.ILonePair;
import org.openscience.cdk.interfaces.ISingleElectron;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.interfaces.IBond.Order;

/**
 * Base class for all chemical objects that maintain a list of Atoms and
 * ElectronContainers. <p>
 * <p>
 * Looping over all Bonds in the AtomContainer is typically done like: <pre>
 * Iterator iter = atomContainer.bonds();
 * while (iter.hasNext()) {
 *   IBond aBond = (IBond) iter.next();
 * }
 * <p>
 *  </pre>
 *
 * @author steinbeck
 * @cdk.module data
 * @cdk.githash
 * @cdk.created 2000-10-02
 */
public class AtomContainer extends ChemObject implements IAtomContainer, IChemObjectListener, Serializable, Cloneable {

    private static final int DEFAULT_CAPACITY = 20;

    /**
     * Determines if a de-serialized object is compatible with this class.
     * <p>
     * This value must only be changed if and only if the new version
     * of this class is incompatible with the old version. See Sun docs
     * for <a href=http://java.sun.com/products/jdk/1.1/docs/guide
     * /serialization/spec/version.doc.html>details</a>.
     */
    private static final long serialVersionUID = 5678100348445919254L;

    /**
     * Number of atoms contained by this object.
     */
    protected int atomCount;

    /**
     * Number of bonds contained by this object.
     */
    protected int bondCount;

    /**
     * Number of lone pairs contained by this object.
     */
    protected int lonePairCount;

    /**
     * Number of single electrons contained by this object.
     */
    protected int singleElectronCount;

    /**
     * Amount by which the bond and atom arrays grow when elements are added and
     * the arrays are not large enough for that.
     */
    protected int growArraySize = 10;

    /**
     * Internal array of atoms.
     */
    protected IAtom[] atoms;

    /**
     * Internal array of bonds.
     */
    protected IBond[] bonds;

    /**
     * Internal array of lone pairs.
     */
    protected ILonePair[] lonePairs;

    /**
     * Internal array of single electrons.
     */
    protected ISingleElectron[] singleElectrons;

    /**
     * Internal list of atom parities.
     */
    protected Set<IStereoElement> stereoElements;

    /**
     * Constructs an empty AtomContainer.
     */
    public AtomContainer() {
        this(0, 0, 0, 0);
    }

    /**
     * Constructs an AtomContainer with a copy of the atoms and electronContainers
     * of another AtomContainer (A shallow copy, i.e., with the same objects as in
     * the original AtomContainer).
     *
     * @param container An AtomContainer to copy the atoms and electronContainers from
     */
    public AtomContainer(IAtomContainer container) {
        this.atomCount = container.getAtomCount();
        this.bondCount = container.getBondCount();
        this.lonePairCount = container.getLonePairCount();
        this.singleElectronCount = container.getSingleElectronCount();
        this.atoms = new IAtom[this.atomCount];
        this.bonds = new IBond[this.bondCount];
        this.lonePairs = new ILonePair[this.lonePairCount];
        this.singleElectrons = new ISingleElectron[this.singleElectronCount];

        stereoElements = new HashSet<IStereoElement>(atomCount / 2);

        for (IStereoElement element : container.stereoElements()) {
            addStereoElement(element);
        }

        for (int f = 0; f < container.getAtomCount(); f++) {
            atoms[f] = container.getAtom(f);
            container.getAtom(f).addListener(this);
        }
        for (int f = 0; f < this.bondCount; f++) {
            bonds[f] = container.getBond(f);
            container.getBond(f).addListener(this);
        }
        for (int f = 0; f < this.lonePairCount; f++) {
            lonePairs[f] = container.getLonePair(f);
            container.getLonePair(f).addListener(this);
        }
        for (int f = 0; f < this.singleElectronCount; f++) {
            singleElectrons[f] = container.getSingleElectron(f);
            container.getSingleElectron(f).addListener(this);
        }
    }

    /**
     * Constructs an empty AtomContainer that will contain a certain number of
     * atoms and electronContainers. It will set the starting array lengths to the
     * defined values, but will not create any Atom or ElectronContainer's.
     *
     * @param atomCount Number of atoms to be in this container
     * @param bondCount Number of bonds to be in this container
     * @param lpCount   Number of lone pairs to be in this container
     * @param seCount   Number of single electrons to be in this container
     */
    public AtomContainer(int atomCount, int bondCount, int lpCount,
                         int seCount) {
        this.atomCount = 0;
        this.bondCount = 0;
        this.lonePairCount = 0;
        this.singleElectronCount = 0;
        atoms = new IAtom[atomCount];
        bonds = new IBond[bondCount];
        lonePairs = new ILonePair[lpCount];
        singleElectrons = new ISingleElectron[seCount];
        stereoElements = new HashSet<IStereoElement>(atomCount / 2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addStereoElement(IStereoElement element) {
        stereoElements.add(element);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setStereoElements(List<IStereoElement> elements) {
        this.stereoElements = new HashSet<IStereoElement>();
        this.stereoElements.addAll(elements);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<IStereoElement> stereoElements() {
        return Collections.unmodifiableSet(stereoElements);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAtoms(IAtom[] atoms) {
        // unregister this as listener with the old atoms
        for (int i = 0; i < atomCount; i++) {
            this.atoms[i].removeListener(this);
        }
        this.atoms = atoms;
        for (IAtom atom : atoms) {
            atom.addListener(this);
        }
        this.atomCount = atoms.length;
        notifyChanged();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBonds(IBond[] bonds) {
        for (int i = 0; i < bondCount; i++) {
            this.bonds[i].removeListener(this);
        }
        this.bonds = bonds;
        for (IBond bond : bonds) {
            bond.addListener(this);
        }
        this.bondCount = bonds.length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAtom(int idx, IAtom atom) {
        if (idx >= atomCount)
            throw new IndexOutOfBoundsException("No atom at index: " + idx);
        int aidx = indexOf(atom);
        if (aidx >= 0)
            throw new IllegalArgumentException("Atom already in container at index: " + idx);
        final IAtom oldAtom = atoms[idx];
        atoms[idx] = atom;
        atom.addListener(this);
        oldAtom.removeListener(this);

        // replace in electron containers
        for (IBond bond : bonds()) {
            for (int i = 0; i < bond.getAtomCount(); i++) {
                if (oldAtom.equals(bond.getAtom(i))) {
                    bond.setAtom(atom, i);
                }
            }
        }
        for (ISingleElectron ec : singleElectrons()) {
            if (oldAtom.equals(ec.getAtom()))
                ec.setAtom(atom);
        }
        for (ILonePair lp : lonePairs()) {
            if (oldAtom.equals(lp.getAtom()))
                lp.setAtom(atom);
        }

        // update stereo
        IStereoElement oldStereo = null;
        IStereoElement newStereo = null;
        for (IStereoElement se : stereoElements()) {
            if (se.contains(oldAtom)) {
                oldStereo = se;
                Map<IAtom, IAtom> amap = Collections.singletonMap(oldAtom, atom);
                Map<IBond, IBond> bmap = Collections.emptyMap();
                newStereo = se.map(amap, bmap);
            }
        }
        if (oldStereo != null) {
            stereoElements.remove(oldStereo);
            stereoElements.add(newStereo);
        }

        notifyChanged();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IAtom getAtom(int number) {
        return atoms[number];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IBond getBond(int number) {
        return bonds[number];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ILonePair getLonePair(int number) {
        return lonePairs[number];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ISingleElectron getSingleElectron(int number) {
        return singleElectrons[number];
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
        return atoms[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IAtom getLastAtom() {
        return getAtomCount() > 0 ? (IAtom) atoms[getAtomCount() - 1] : null;
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
    public int getBondNumber(IAtom atom1, IAtom atom2) {
        return indexOf(getBond(atom1, atom2));
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
        for (int i = 0; i < atomCount; i++) {
            if (atoms[i] == atom) return i;
        }
        return -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int indexOf(IBond bond) {
        for (int i = 0; i < bondCount; i++) {
            if (bonds[i] == bond) return i;
        }
        return -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int indexOf(ISingleElectron electron) {
        for (int i = 0; i < singleElectronCount; i++) {
            if (singleElectrons[i] == electron) return i;
        }
        return -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int indexOf(ILonePair pair) {
        for (int i = 0; i < lonePairCount; i++) {
            if (lonePairs[i] == pair) return i;
        }
        return -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IElectronContainer getElectronContainer(int number) {
        if (number < this.bondCount) return bonds[number];
        number -= this.bondCount;
        if (number < this.lonePairCount) return lonePairs[number];
        number -= this.lonePairCount;
        if (number < this.singleElectronCount) return singleElectrons[number];
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IBond getBond(IAtom atom1, IAtom atom2) {
        for (int i = 0; i < getBondCount(); i++) {
            if (bonds[i].contains(atom1) && bonds[i].getOther(atom1) == atom2) {
                return bonds[i];
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getAtomCount() {
        return this.atomCount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getBondCount() {
        return this.bondCount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getLonePairCount() {
        return this.lonePairCount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSingleElectronCount() {
        return this.singleElectronCount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getElectronContainerCount() {
        return this.bondCount + this.lonePairCount + this.singleElectronCount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<IAtom> getConnectedAtomsList(IAtom atom) {
        List<IAtom> atomsList = new ArrayList<IAtom>();
        for (int i = 0; i < bondCount; i++) {
            if (bonds[i].contains(atom)) atomsList.add(bonds[i].getOther(atom));
        }
        return atomsList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<IBond> getConnectedBondsList(IAtom atom) {
        List<IBond> bondsList = new ArrayList<IBond>();
        for (int i = 0; i < bondCount; i++) {
            if (bonds[i].contains(atom)) bondsList.add(bonds[i]);
        }
        return bondsList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ILonePair> getConnectedLonePairsList(IAtom atom) {
        List<ILonePair> lps = new ArrayList<ILonePair>();
        for (int i = 0; i < lonePairCount; i++) {
            if (lonePairs[i].contains(atom)) lps.add(lonePairs[i]);
        }
        return lps;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ISingleElectron> getConnectedSingleElectronsList(IAtom atom) {
        List<ISingleElectron> lps = new ArrayList<ISingleElectron>();
        for (int i = 0; i < singleElectronCount; i++) {
            if (singleElectrons[i].contains(atom)) lps.add(singleElectrons[i]);
        }
        return lps;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<IElectronContainer> getConnectedElectronContainersList(
        IAtom atom) {
        List<IElectronContainer> lps = new ArrayList<IElectronContainer>();
        for (int i = 0; i < bondCount; i++) {
            if (bonds[i].contains(atom)) lps.add(bonds[i]);
        }
        for (int i = 0; i < lonePairCount; i++) {
            if (lonePairs[i].contains(atom)) lps.add(lonePairs[i]);
        }
        for (int i = 0; i < singleElectronCount; i++) {
            if (singleElectrons[i].contains(atom)) lps.add(singleElectrons[i]);
        }
        return lps;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getConnectedAtomsCount(IAtom atom) {
        int count = 0;
        for (int i = 0; i < bondCount; i++) {
            if (bonds[i].contains(atom)) ++count;
        }
        return count;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getConnectedBondsCount(IAtom atom) {
        return getConnectedAtomsCount(atom);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getConnectedBondsCount(int atomNumber) {
        return getConnectedAtomsCount(atoms[atomNumber]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getConnectedLonePairsCount(IAtom atom) {
        int count = 0;
        for (int i = 0; i < lonePairCount; i++) {
            if (lonePairs[i].contains(atom)) ++count;
        }
        return count;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getConnectedSingleElectronsCount(IAtom atom) {
        int count = 0;
        for (int i = 0; i < singleElectronCount; i++) {
            if (singleElectrons[i].contains(atom)) ++count;
        }
        return count;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getBondOrderSum(IAtom atom) {
        double count = 0;
        for (int i = 0; i < bondCount; i++) {
            if (bonds[i].contains(atom)) {
                IBond.Order order = bonds[i].getOrder();
                if (order != null) {
                    count += order.numeric();
                }
            }
        }
        return count;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Order getMaximumBondOrder(IAtom atom) {
        IBond.Order max = IBond.Order.SINGLE;
        for (int i = 0; i < bondCount; i++) {
            if (bonds[i].contains(atom) && bonds[i].getOrder().numeric() > max.numeric()) {
                max = bonds[i].getOrder();
            }
        }
        return max;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Order getMinimumBondOrder(IAtom atom) {
        IBond.Order min = IBond.Order.QUADRUPLE;
        for (int i = 0; i < bondCount; i++) {
            if (bonds[i].contains(atom) && bonds[i].getOrder().numeric() < min.numeric()) {
                min = bonds[i].getOrder();
            }
        }
        return min;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(IAtomContainer that) {
        atoms = Arrays.copyOf(atoms, atomCount + that.getAtomCount());
        bonds = Arrays.copyOf(bonds, bondCount + that.getBondCount());

        for (IAtom atom : that.atoms())
            atom.setFlag(CDKConstants.VISITED, false);
        for (IBond bond : that.bonds())
            bond.setFlag(CDKConstants.VISITED, false);
        for (IAtom atom : this.atoms())
            atom.setFlag(CDKConstants.VISITED, true);
        for (IBond bond : this.bonds())
            bond.setFlag(CDKConstants.VISITED, true);

        for (IAtom atom : that.atoms()) {
            if (!atom.getFlag(CDKConstants.VISITED)) {
                atom.setFlag(CDKConstants.VISITED, true);
                atoms[atomCount++] = atom;
            }
        }
        for (IBond bond : that.bonds()) {
            if (!bond.getFlag(CDKConstants.VISITED)) {
                bond.setFlag(CDKConstants.VISITED, true);
                bonds[bondCount++] = bond;
            }
        }
        for (ILonePair lp : that.lonePairs()) {
            if (!contains(lp)) {
                addLonePair(lp);
            }
        }
        for (ISingleElectron se : that.singleElectrons()) {
            if (!contains(se)) {
                addSingleElectron(se);
            }
        }
        for (IStereoElement se : that.stereoElements())
            stereoElements.add(se);

        notifyChanged();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addAtom(IAtom atom) {
        if (contains(atom)) {
            return;
        }
        ensureAtomCapacity(atomCount + 1);
        atom.addListener(this);
        atoms[atomCount++] = atom;
        notifyChanged();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addBond(IBond bond) {
        ensureBondCapacity(bondCount + 1);
        bonds[bondCount++] = bond;
        notifyChanged();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addLonePair(ILonePair lonePair) {
        ensureLonePairCapacity(lonePairCount + 1);
        lonePairs[lonePairCount++] = lonePair;
        notifyChanged();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addSingleElectron(ISingleElectron singleElectron) {
        ensureElectronCapacity(singleElectronCount + 1);
        singleElectrons[singleElectronCount++] = singleElectron;
        notifyChanged();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addElectronContainer(IElectronContainer electronContainer) {
        if (electronContainer instanceof IBond)
            this.addBond((IBond) electronContainer);
        if (electronContainer instanceof ILonePair)
            this.addLonePair((ILonePair) electronContainer);
        if (electronContainer instanceof ISingleElectron)
            this.addSingleElectron((ISingleElectron) electronContainer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(IAtomContainer atomContainer) {
        for (int f = 0; f < atomContainer.getAtomCount(); f++) {
            removeAtom(atomContainer.getAtom(f));
        }
        for (int f = 0; f < atomContainer.getBondCount(); f++) {
            removeBond(atomContainer.getBond(f));
        }
        for (int f = 0; f < atomContainer.getLonePairCount(); f++) {
            removeLonePair(atomContainer.getLonePair(f));
        }
        for (int f = 0; f < atomContainer.getSingleElectronCount(); f++) {
            removeSingleElectron(atomContainer.getSingleElectron(f));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAtom(int position) {
        atoms[position].removeListener(this);
        for (int i = position; i < atomCount - 1; i++) {
            atoms[i] = atoms[i + 1];
        }
        atoms[atomCount - 1] = null;
        atomCount--;
        notifyChanged();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAtom(IAtom atom) {
        int position = getAtomNumber(atom);
        if (position != -1) {
            removeAtom(position);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IBond removeBond(int position) {
        IBond bond = bonds[position];
        bond.removeListener(this);
        for (int i = position; i < bondCount - 1; i++) {
            bonds[i] = bonds[i + 1];
        }
        bonds[bondCount - 1] = null;
        bondCount--;
        notifyChanged();
        return bond;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IBond removeBond(IAtom atom1, IAtom atom2) {
        int   pos  = indexOf(getBond(atom1, atom2));
        IBond bond = null;
        if (pos != -1) {
            bond = bonds[pos];
            removeBond(pos);
        }
        return bond;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeBond(IBond bond) {
        int pos = getBondNumber(bond);
        if (pos != -1) removeBond(pos);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ILonePair removeLonePair(int position) {
        ILonePair lp = lonePairs[position];
        lp.removeListener(this);
        for (int i = position; i < lonePairCount - 1; i++) {
            lonePairs[i] = lonePairs[i + 1];
        }
        lonePairs[lonePairCount - 1] = null;
        lonePairCount--;
        notifyChanged();
        return lp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeLonePair(ILonePair lonePair) {
        int pos = indexOf(lonePair);
        if (pos != -1) removeLonePair(pos);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ISingleElectron removeSingleElectron(int position) {
        ISingleElectron se = singleElectrons[position];
        se.removeListener(this);
        for (int i = position; i < singleElectronCount - 1; i++) {
            singleElectrons[i] = singleElectrons[i + 1];
        }
        singleElectrons[singleElectronCount - 1] = null;
        singleElectronCount--;
        notifyChanged();
        return se;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeSingleElectron(ISingleElectron singleElectron) {
        int pos = indexOf(singleElectron);
        if (pos != -1) removeSingleElectron(pos);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IElectronContainer removeElectronContainer(int number) {
        if (number < this.bondCount) return removeBond(number);
        number -= this.bondCount;
        if (number < this.lonePairCount) return removeLonePair(number);
        number -= this.lonePairCount;
        if (number < this.singleElectronCount)
            return removeSingleElectron(number);
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeElectronContainer(IElectronContainer electronContainer) {
        if (electronContainer instanceof IBond)
            removeBond((IBond) electronContainer);
        else if (electronContainer instanceof ILonePair)
            removeLonePair((ILonePair) electronContainer);
        else if (electronContainer instanceof ISingleElectron)
            removeSingleElectron((ISingleElectron) electronContainer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAtomAndConnectedElectronContainers(IAtom atom) {
        int position = getAtomNumber(atom);
        if (position != -1) {
            for (int i = 0; i < bondCount; i++) {
                if (bonds[i].contains(atom)) {
                    removeBond(i);
                    --i;
                }
            }
            for (int i = 0; i < lonePairCount; i++) {
                if (lonePairs[i].contains(atom)) {
                    removeLonePair(i);
                    --i;
                }
            }
            for (int i = 0; i < singleElectronCount; i++) {
                if (singleElectrons[i].contains(atom)) {
                    removeSingleElectron(i);
                    --i;
                }
            }
            List<IStereoElement> atomElements = new ArrayList<IStereoElement>(3);
            for (IStereoElement element : stereoElements) {
                if (element.contains(atom)) atomElements.add(element);
            }
            stereoElements.removeAll(atomElements);
            removeAtom(position);
        }
        notifyChanged();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAllElements() {
        removeAllElectronContainers();
        for (int f = 0; f < getAtomCount(); f++) {
            getAtom(f).removeListener(this);
        }
        atoms = new IAtom[growArraySize];
        atomCount = 0;
        stereoElements.clear();
        notifyChanged();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAllElectronContainers() {
        removeAllBonds();
        for (int f = 0; f < getLonePairCount(); f++) {
            getLonePair(f).removeListener(this);
        }
        for (int f = 0; f < getSingleElectronCount(); f++) {
            getSingleElectron(f).removeListener(this);
        }
        lonePairs = new ILonePair[growArraySize];
        singleElectrons = new ISingleElectron[growArraySize];
        lonePairCount = 0;
        singleElectronCount = 0;
        notifyChanged();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAllBonds() {
        for (int f = 0; f < getBondCount(); f++) {
            getBond(f).removeListener(this);
        }
        bonds = new IBond[growArraySize];
        bondCount = 0;
        notifyChanged();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addBond(int atom1, int atom2, IBond.Order order,
                        IBond.Stereo stereo) {
        IBond bond = getBuilder().newInstance(IBond.class, getAtom(atom1), getAtom(atom2), order, stereo);
        addBond(bond);
        /*
         * no notifyChanged() here because addBond(bond) does it already
         */
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addBond(int atom1, int atom2, IBond.Order order) {
        IBond bond = getBuilder().newInstance(IBond.class, getAtom(atom1), getAtom(atom2), order);
        addBond(bond);
        /*
         * no notifyChanged() here because addBond(bond) does it already
         */
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addLonePair(int atomID) {
        ILonePair lonePair = getBuilder().newInstance(ILonePair.class, atoms[atomID]);
        lonePair.addListener(this);
        addLonePair(lonePair);
        /*
         * no notifyChanged() here because addElectronContainer() does it
         * already
         */
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addSingleElectron(int atomID) {
        ISingleElectron singleElectron = getBuilder().newInstance(ISingleElectron.class, atoms[atomID]);
        singleElectron.addListener(this);
        addSingleElectron(singleElectron);
        /*
         * no notifyChanged() here because addSingleElectron() does it already
         */
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(IAtom atom) {
        for (int i = 0; i < getAtomCount(); i++) {
            if (atom == atoms[i]) return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(IBond bond) {
        for (int i = 0; i < getBondCount(); i++) {
            if (bond == bonds[i]) return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(ILonePair lonePair) {
        for (int i = 0; i < getLonePairCount(); i++) {
            if (lonePair == lonePairs[i]) return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(ISingleElectron singleElectron) {
        for (int i = 0; i < getSingleElectronCount(); i++) {
            if (singleElectron == singleElectrons[i]) return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(IElectronContainer electronContainer) {
        if (electronContainer instanceof IBond)
            return contains((IBond) electronContainer);
        if (electronContainer instanceof ILonePair)
            return contains((ILonePair) electronContainer);
        if (electronContainer instanceof ISingleElectron)
            return contains((SingleElectron) electronContainer);
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuffer stringContent = new StringBuffer(64);
        stringContent.append("AtomContainer(");
        stringContent.append(this.hashCode());
        if (getAtomCount() > 0) {
            stringContent.append(", #A:").append(getAtomCount());
            for (int i = 0; i < getAtomCount(); i++) {
                stringContent.append(", ").append(getAtom(i).toString());
            }
        }
        if (getBondCount() > 0) {
            stringContent.append(", #B:").append(getBondCount());
            for (int i = 0; i < getBondCount(); i++) {
                stringContent.append(", ").append(getBond(i).toString());
            }
        }
        if (getLonePairCount() > 0) {
            stringContent.append(", #LP:").append(getLonePairCount());
            for (int i = 0; i < getLonePairCount(); i++) {
                stringContent.append(", ").append(getLonePair(i).toString());
            }
        }
        if (getSingleElectronCount() > 0) {
            stringContent.append(", #SE:").append(getSingleElectronCount());
            for (int i = 0; i < getSingleElectronCount(); i++) {
                stringContent.append(", ").append(getSingleElectron(i).toString());
            }
        }
        if (stereoElements.size() > 0) {
            stringContent.append(", ST:[#").append(stereoElements.size());
            for (IStereoElement elements : stereoElements) {
                stringContent.append(", ").append(elements.toString());
            }
            stringContent.append(']');
        }
        stringContent.append(')');
        return stringContent.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IAtomContainer clone() throws CloneNotSupportedException {

        // this is pretty wasteful as we need to delete most the data
        // we can't simply create an empty instance as the sub classes (e.g. AminoAcid)
        // would have a ClassCastException when they invoke clone
        IAtomContainer clone = (IAtomContainer) super.clone();

        // remove existing elements - we need to set the stereo elements list as list.clone() doesn't
        // work as expected and will also remove all elements from the original
        clone.setStereoElements(new ArrayList<IStereoElement>(stereoElements.size()));
        clone.removeAllElements();

        // create a mapping of the original atoms/bonds to the cloned atoms/bonds
        // we need this mapping to correctly clone bonds, single/paired electrons
        // and stereo elements
        // - the expected size stop the map be resized - method from Google Guava
        Map<IAtom, IAtom> atomMap = new HashMap<IAtom, IAtom>(atomCount >= 3 ? atomCount + atomCount / 3
                                                                             : atomCount + 1);
        Map<IBond, IBond> bondMap = new HashMap<IBond, IBond>(bondCount >= 3 ? bondCount + bondCount / 3
                                                                             : bondCount + 1);

        // clone atoms
        IAtom[] atoms = new IAtom[this.atomCount];
        for (int i = 0; i < atoms.length; i++) {

            atoms[i] = (IAtom) this.atoms[i].clone();
            atomMap.put(this.atoms[i], atoms[i]);
        }
        clone.setAtoms(atoms);

        // clone bonds using a the mappings from the original to the clone
        IBond[] bonds = new IBond[this.bondCount];
        for (int i = 0; i < bonds.length; i++) {

            IBond   original = this.bonds[i];
            IBond   bond     = (IBond) original.clone();
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

        // clone lone pairs (we can't use an array to buffer as there is no setLonePairs())
        for (int i = 0; i < lonePairCount; i++) {

            ILonePair original = this.lonePairs[i];
            ILonePair pair     = (ILonePair) original.clone();

            if (pair.getAtom() != null)
                pair.setAtom(atomMap.get(original.getAtom()));

            clone.addLonePair(pair);
        }

        // clone single electrons (we can't use an array to buffer as there is no setSingleElectrons())
        for (int i = 0; i < singleElectronCount; i++) {

            ISingleElectron original = this.singleElectrons[i];
            ISingleElectron electron = (ISingleElectron) original.clone();

            if (electron.getAtom() != null)
                electron.setAtom(atomMap.get(original.getAtom()));

            clone.addSingleElectron(electron);
        }

        // map each stereo element to a new instance in the clone
        for (IStereoElement element : stereoElements) {
            clone.addStereoElement(element.map(atomMap, bondMap));
        }

        return clone;

    }

    /**
     * Generic grow function, expand an array by a varried amount to have
     * enough (required) space.
     *
     * @param array    the array to expand
     * @param required the minimum required space
     * @param <T>      array type
     * @return the expanded array
     */
    private static <T> T[] grow(T[] array, int required) {
        int oldCapacity = array.length;
        // x1.5: 20, 30, 45, 67, 100, 150, 225, 337, 505, etc
        int newCapacity = oldCapacity == 0 ? DEFAULT_CAPACITY
                                           : oldCapacity + (oldCapacity >> 1);
        if (newCapacity < required)
            newCapacity = required;
        return Arrays.copyOf(array, newCapacity);
    }


    /**
     * Ensure there is enough space to accommodate the specified number of
     * atoms.
     *
     * @param required total number of atoms (inc. already used)
     */
    private void ensureAtomCapacity(int required) {
        if (required > atoms.length)
            atoms = grow(atoms, required);
    }

    /**
     * Ensure there is enough space to accommodate the specified number of
     * bonds.
     *
     * @param required total number of bonds (inc. already used)
     */
    private void ensureBondCapacity(int required) {
        if (required > bonds.length)
            bonds = grow(bonds, required);
    }

    /**
     * Ensure there is enough space to accommodate the specified number of
     * electrons.
     *
     * @param required total number of electrons (inc. already used)
     */
    private void ensureElectronCapacity(int required) {
        if (required > singleElectrons.length)
            singleElectrons = grow(singleElectrons, required);
    }

    /**
     * Ensure there is enough space to accommodate the specified number of
     * lone pairs.
     *
     * @param required total number of lone pairs (inc. already used)
     */
    private void ensureLonePairCapacity(int required) {
        if (required > lonePairs.length)
            lonePairs = grow(lonePairs, required);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stateChanged(IChemObjectChangeEvent event) {
        notifyChanged(event);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return atomCount == 0;
    }

    /**
     * The inner AtomIterator class.
     */
    private class AtomIterator implements Iterator<IAtom> {

        private int pointer = 0;

        @Override
        public boolean hasNext() {
            return pointer < atomCount;
        }

        @Override
        public IAtom next() {
            return atoms[pointer++];
        }

        @Override
        public void remove() {
            removeAtom(--pointer);
        }

    }

    /**
     * The inner BondIterator class.
     */
    private class BondIterator implements Iterator<IBond> {

        private int pointer = 0;

        @Override
        public boolean hasNext() {
            return pointer < bondCount;
        }

        @Override
        public IBond next() {
            return bonds[pointer++];
        }

        @Override
        public void remove() {
            removeBond(--pointer);
        }

    }

    /**
     * The inner LonePairIterator class.
     */
    private class LonePairIterator implements Iterator<ILonePair> {

        private int pointer = 0;

        @Override
        public boolean hasNext() {
            return pointer < lonePairCount;
        }

        @Override
        public ILonePair next() {
            return lonePairs[pointer++];
        }

        @Override
        public void remove() {
            removeLonePair(--pointer);
        }

    }

    /**
     * The inner SingleElectronIterator class.
     */
    private class SingleElectronIterator implements Iterator<ISingleElectron> {

        private int pointer = 0;

        @Override
        public boolean hasNext() {
            return pointer < singleElectronCount;
        }

        @Override
        public ISingleElectron next() {
            return singleElectrons[pointer++];
        }

        @Override
        public void remove() {
            removeSingleElectron(--pointer);
        }

    }

    /**
     * The inner ElectronContainerIterator class.
     */
    private class ElectronContainerIterator implements Iterator<IElectronContainer> {

        private int pointer = 0;

        @Override
        public boolean hasNext() {
            return pointer < (bondCount + lonePairCount + singleElectronCount);
        }

        @Override
        public IElectronContainer next() {
            if (pointer < bondCount)
                return bonds[pointer++];
            else if (pointer < bondCount + lonePairCount)
                return lonePairs[(pointer++) - bondCount];
            else if (pointer < bondCount + lonePairCount + singleElectronCount)
                return singleElectrons[(pointer++) - bondCount - lonePairCount];
            return null;
        }

        @Override
        public void remove() {
            if (pointer <= bondCount)
                removeBond(--pointer);
            else if (pointer <= bondCount + lonePairCount)
                removeLonePair((--pointer) - bondCount);
            else if (pointer <= bondCount + lonePairCount + singleElectronCount)
                removeSingleElectron((--pointer) - bondCount - lonePairCount);
        }

    }
}
