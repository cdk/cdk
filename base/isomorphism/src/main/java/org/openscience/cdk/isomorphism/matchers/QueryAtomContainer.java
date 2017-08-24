/* Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
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
package org.openscience.cdk.isomorphism.matchers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IElectronContainer;
import org.openscience.cdk.interfaces.ILonePair;
import org.openscience.cdk.interfaces.ISingleElectron;
import org.openscience.cdk.interfaces.IStereoElement;

/**
 * @cdk.module  isomorphism
 * @cdk.githash
 */
public class QueryAtomContainer extends QueryChemObject implements IQueryAtomContainer {

    private static final long serialVersionUID = -1876912362585898476L;

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("QueryAtomContainer(");
        s.append(this.hashCode());
        s.append(", #A:").append(getAtomCount());
        s.append(", #EC:").append(getElectronContainerCount());
        for (int i = 0; i < getAtomCount(); i++) {
            s.append(", ").append(getAtom(i).toString());
        }
        for (int i = 0; i < getBondCount(); i++) {
            s.append(", ").append(getBond(i).toString());
        }
        for (int i = 0; i < getLonePairCount(); i++) {
            s.append(", ").append(getLonePair(i).toString());
        }
        for (int i = 0; i < getSingleElectronCount(); i++) {
            s.append(", ").append(getSingleElectron(i).toString());
        }
        s.append(')');
        return s.toString();
    }

    /**
     *  Number of atoms contained by this object.
     */
    protected int                  atomCount;

    /**
     *  Number of bonds contained by this object.
     */
    protected int                  bondCount;

    /**
     *  Number of lone pairs contained by this object.
     */
    protected int                  lonePairCount;

    /**
     *  Number of single electrons contained by this object.
     */
    protected int                  singleElectronCount;

    /**
     *  Amount by which the bond and atom arrays grow when elements are added and
     *  the arrays are not large enough for that.
     */
    protected int                  growArraySize = 10;

    /**
     *  Internal array of atoms.
     */
    protected IAtom[]              atoms;

    /**
     *  Internal array of bonds.
     */
    protected IBond[]              bonds;

    /**
     *  Internal array of lone pairs.
     */
    protected ILonePair[]          lonePairs;

    /**
     *  Internal array of single electrons.
     */
    protected ISingleElectron[]    singleElectrons;

    /**
     * Internal list of atom parities.
     */
    protected List<IStereoElement> stereoElements;

    /**
     *  Constructs an empty AtomContainer.
     */
    public QueryAtomContainer(IChemObjectBuilder builder) {
        this(10, 10, 0, 0, builder);
    }

    /**
     * Constructs an AtomContainer with a copy of the atoms and electronContainers
     * of another AtomContainer (A shallow copy, i.e., with the same objects as in
     * the original AtomContainer).
     *
     * @param  container  An AtomContainer to copy the atoms and electronContainers from
     */
    public QueryAtomContainer(IAtomContainer container, IChemObjectBuilder builder) {
        super(builder);
        this.atomCount = container.getAtomCount();
        this.bondCount = container.getBondCount();
        this.lonePairCount = container.getLonePairCount();
        this.singleElectronCount = container.getSingleElectronCount();
        this.atoms = new IAtom[this.atomCount];
        this.bonds = new IBond[this.bondCount];
        this.lonePairs = new ILonePair[this.lonePairCount];
        this.singleElectrons = new ISingleElectron[this.singleElectronCount];

        stereoElements = new ArrayList<IStereoElement>(atomCount / 2);

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
     *  Constructs an empty AtomContainer that will contain a certain number of
     *  atoms and electronContainers. It will set the starting array lengths to the
     *  defined values, but will not create any Atom or ElectronContainer's.
     *
     *@param  atomCount        Number of atoms to be in this container
     *@param  bondCount        Number of bonds to be in this container
     *@param  lpCount          Number of lone pairs to be in this container
     *@param  seCount          Number of single electrons to be in this container
     *
     */
    public QueryAtomContainer(int atomCount, int bondCount, int lpCount, int seCount, IChemObjectBuilder builder) {
        super(builder);
        this.atomCount = 0;
        this.bondCount = 0;
        this.lonePairCount = 0;
        this.singleElectronCount = 0;
        atoms = new IAtom[atomCount];
        bonds = new IBond[bondCount];
        lonePairs = new ILonePair[lpCount];
        singleElectrons = new ISingleElectron[seCount];
        stereoElements = new ArrayList<IStereoElement>(atomCount / 2);
    }

    /** {@inheritDoc} */
    @Override
    public void addStereoElement(IStereoElement element) {
        stereoElements.add(element);
    }

    /** {@inheritDoc} */
    @Override
    public Iterable<IStereoElement> stereoElements() {
        return new Iterable<IStereoElement>() {

            @Override
            public Iterator<IStereoElement> iterator() {
                return stereoElements.iterator();
            }
        };
    }

    /**
     *  Sets the array of atoms of this AtomContainer.
     *
     *@param  atoms  The array of atoms to be assigned to this AtomContainer
     *@see           #getAtom
     */
    @Override
    public void setAtoms(IAtom[] atoms) {
        this.atoms = atoms;
        for (IAtom atom : atoms) {
            atom.addListener(this);
        }
        this.atomCount = atoms.length;
        notifyChanged();
    }

    /**
     * Sets the array of bonds of this AtomContainer.
     *
     * @param  bonds  The array of bonds to be assigned to
     *                             this AtomContainer
     * @see  #getBond
     */
    @Override
    public void setBonds(IBond[] bonds) {
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
        List<IStereoElement> oldStereo = null;
        List<IStereoElement> newStereo = null;
        for (IStereoElement se : stereoElements()) {
            if (se.contains(oldAtom)) {
                if (oldStereo == null) {
                    oldStereo = new ArrayList<>();
                    newStereo = new ArrayList<>();
                }
                oldStereo.add(se);
                Map<IAtom, IAtom> amap = Collections.singletonMap(oldAtom, atom);
                Map<IBond, IBond> bmap = Collections.emptyMap();
                newStereo.add(se.map(amap, bmap));
            }
        }
        if (oldStereo != null) {
            stereoElements.removeAll(oldStereo);
            stereoElements.addAll(newStereo);
        }

        notifyChanged();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IAtom getAtom(int idx) {
        if (idx < 0 || idx >= atomCount)
            throw new IndexOutOfBoundsException("Atom index out of bounds: 0 <= " + idx + " < " + atomCount);
        return atoms[idx];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IBond getBond(int idx) {
        if (idx < 0 || idx >= bondCount)
            throw new IndexOutOfBoundsException("Bond index out of bounds: 0 <= " + idx + " < " + bondCount);
        return bonds[idx];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ILonePair getLonePair(int idx) {
        if (idx < 0 || idx >= lonePairCount)
            throw new IndexOutOfBoundsException("Lone Pair index out of bounds: 0 <= " + idx + " < " + lonePairCount);
        return lonePairs[idx];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ISingleElectron getSingleElectron(int idx) {
        if (idx < 0 || idx >= singleElectronCount)
            throw new IndexOutOfBoundsException("Single Electrong index out of bounds: 0 <= " + idx + " < " + singleElectronCount);
        return singleElectrons[idx];
    }

    /**
     *  Returns an Iterable for looping over all atoms in this container.
     *
     *@return    An Iterable with the atoms in this container
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
     * The inner AtomIterator class.
     *
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
            removeAtomOnly(--pointer);
        }

    }

    /**
     *  Returns an Iterable for looping over all bonds in this container.
     *
     *@return    An Iterable with the bonds in this container
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
     * The inner BondIterator class.
     *
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
     *  Returns an Iterable for looping over all lone pairs in this container.
     *
     *@return    An Iterable with the lone pairs in this container
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
     * The inner LonePairIterator class.
     *
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
     *  Returns an Iterable for looping over all single electrons in this container.
     *
     *@return    An Iterable with the single electrons in this container
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
     * The inner SingleElectronIterator class.
     *
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
     *  Returns an Iterable for looping over all electron containers in this container.
     *
     *@return    An Iterable with the electron containers in this container
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
     * The inner ElectronContainerIterator class.
     *
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

    /**
     *  Returns the atom at position 0 in the container.
     *
     *@return    The atom at position 0 .
     */
    @Override
    public IAtom getFirstAtom() {
        return atoms[0];
    }

    /**
     *  Returns the atom at the last position in the container.
     *
     *@return    The atom at the last position
     */
    @Override
    public IAtom getLastAtom() {
        return getAtomCount() > 0 ? (IAtom) atoms[getAtomCount() - 1] : null;
    }

    /**
     *  Returns the position of a given atom in the atoms array. It returns -1 if
     *  the atom does not exist.
     *
     *@param  atom  The atom to be sought
     *@return       The Position of the atom in the atoms array in [0,..].
     */
    @Override
    public int getAtomNumber(IAtom atom) {
        return indexOf(atom);
    }

    /**
     *  Returns the position of the bond between two given atoms in the
     *  electronContainers array. It returns -1 if the bond does not exist.
     *
     *@param  atom1  The first atom
     *@param  atom2  The second atom
     *@return        The Position of the bond between a1 and a2 in the
     *               electronContainers array.
     */
    @Override
    public int getBondNumber(IAtom atom1, IAtom atom2) {
        return indexOf(getBond(atom1, atom2));
    }

    /**
     *  Returns the position of a given bond in the electronContainers array. It
     *  returns -1 if the bond does not exist.
     *
     *@param  bond  The bond to be sought
     *@return       The Position of the bond in the electronContainers array in [0,..].
     */
    @Override
    public int getBondNumber(IBond bond) {
        return indexOf(bond);
    }

    /**
     *  Returns the position of a given lone pair in the lone pair array.
     *  It returns -1 if the lone pair does not exist.
     *
     *@param  lonePair  The lone pair to be sought
     *@return       The Position of the lone pair in the array..
     */
    @Override
    public int getLonePairNumber(ILonePair lonePair) {
        return indexOf(lonePair);
    }

    /**
     *  Returns the position of a given single electron in the single electron array.
     *  It returns -1 if the single electron does not exist.
     *
     *@param  singleElectron  The single electron to be sought
     *@return       The Position of the single electron in the array.
     */
    @Override
    public int getSingleElectronNumber(ISingleElectron singleElectron) {
        return indexOf(singleElectron);
    }

    @Override
    public int indexOf(IAtom atom) {
        for (int i = 0; i < atomCount; i++) {
            if (atoms[i].equals(atom)) return i;
        }
        return -1;
    }

    @Override
    public int indexOf(IBond bond) {
        for (int i = 0; i < bondCount; i++) {
            if (bonds[i].equals(bond)) return i;
        }
        return -1;
    }

    @Override
    public int indexOf(ISingleElectron electron) {
        for (int i = 0; i < singleElectronCount; i++) {
            if (singleElectrons[i] == electron) return i;
        }
        return -1;
    }

    @Override
    public int indexOf(ILonePair pair) {
        for (int i = 0; i < lonePairCount; i++) {
            if (lonePairs[i] == pair) return i;
        }
        return -1;
    }

    /**
     *  Returns the ElectronContainer at position <code>number</code> in the
     *  container.
     *
     * @param  number  The position of the ElectronContainer to be returned.
     * @return         The ElectronContainer at position <code>number</code>.
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
     * Returns the bond that connects the two given atoms.
     *
     * @param  atom1  The first atom
     * @param  atom2  The second atom
     * @return        The bond that connects the two atoms
     */
    @Override
    public IBond getBond(IAtom atom1, IAtom atom2) {
        for (int i = 0; i < getBondCount(); i++) {
            if (bonds[i].contains(atom1) && bonds[i].getOther(atom1).equals(atom2)) {
                return bonds[i];
            }
        }
        return null;
    }

    /**
     *  Returns the number of Atoms in this Container.
     *
     *@return    The number of Atoms in this Container
     */
    @Override
    public int getAtomCount() {
        return this.atomCount;
    }

    /**
     *  Returns the number of Bonds in this Container.
     *
     *@return    The number of Bonds in this Container
     */
    @Override
    public int getBondCount() {
        return this.bondCount;
    }

    /**
     *  Returns the number of LonePairs in this Container.
     *
     *@return    The number of LonePairs in this Container
     */
    @Override
    public int getLonePairCount() {
        return this.lonePairCount;
    }

    /**
     *  Returns the number of the single electrons in this container,
     *
     *@return       The number of SingleElectron objects of this AtomContainer
     */
    @Override
    public int getSingleElectronCount() {
        return this.singleElectronCount;
    }

    /**
     * Returns the number of ElectronContainers in this Container.
     *
     * @return    The number of ElectronContainers in this Container
     */
    @Override
    public int getElectronContainerCount() {
        return this.bondCount + this.lonePairCount + this.singleElectronCount;
    }

    /**
     *  Returns an ArrayList of all atoms connected to the given atom.
     *
     *@param  atom  The atom the bond partners are searched of.
     *@return       The ArrayList with the connected atoms
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
     *  Returns an ArrayList of all Bonds connected to the given atom.
     *
     *@param  atom  The atom the connected bonds are searched of
     *@return       The ArrayList with connected atoms
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
     * Returns the array of lone pairs connected to an atom.
     *
     * @param atom The atom for which to get lone pairs
     * @return The array of LonePairs of this AtomContainer
     * @see #getElectronContainer
     * @see #electronContainers()
     * @see #getBond
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
     *  Returns an array of all SingleElectron connected to the given atom.
     *
     *@param  atom  The atom on which the single electron is located
     *@return       The array of SingleElectron of this AtomContainer
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
     *  Returns an ArrayList of all electronContainers connected to the given atom.
     *
     *@param  atom  The atom the connected electronContainers are searched of
     *@return       The ArrayList with the  connected atoms
     */
    @Override
    public List<IElectronContainer> getConnectedElectronContainersList(IAtom atom) {
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
     *  Returns the number of atoms connected to the given atom.
     *
     *@param  atom  The atom the number of bond partners are searched of.
     *@return       The the size of connected atoms
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
     *  Returns the number of Bonds for a given Atom.
     *
     *@param  atom  The atom
     *@return       The number of Bonds for this atom
     */
    @Override
    public int getConnectedBondsCount(IAtom atom) {
        return getConnectedAtomsCount(atom);
    }

    /**
     *  Returns the number of connected atoms (degree) to the given atom.
     *
     *@param  idx  The atomnumber the degree is searched for
     *@return             The number of connected atoms (degree)
     */
    @Override
    public int getConnectedBondsCount(int idx) {
        return getConnectedAtomsCount(atoms[idx]);
    }

    /**
     *  Returns the number of LonePairs for a given Atom.
     *
     *@param  atom  The atom
     *@return       The number of LonePairs for this atom
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
     *  Returns the sum of the SingleElectron for a given Atom.
     *
     *@param  atom  The atom on which the single electron is located
     *@return       The array of SingleElectron of this AtomContainer
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
     * Returns the sum of the bond orders for a given Atom.
     *
     * @param  atom  The atom
     * @return       The number of bond orders for this atom
     *
     * @deprecated   Replaced by <code>AtomContainerManipulator#getBondOrderSum(IAtomContainer, IAtom)</code>
     */
    @Override
    public double getBondOrderSum(IAtom atom) {
        double count = 0;
        for (int i = 0; i < bondCount; i++) {
            if (bonds[i].contains(atom)) {
                if (bonds[i].getOrder() == IBond.Order.SINGLE) {
                    count += 1;
                } else if (bonds[i].getOrder() == IBond.Order.DOUBLE) {
                    count += 2;
                } else if (bonds[i].getOrder() == IBond.Order.TRIPLE) {
                    count += 3;
                } else if (bonds[i].getOrder() == IBond.Order.QUADRUPLE) {
                    count += 4;
                }
            }
        }
        return count;
    }

    /**
     * Returns the maximum bond order that this atom currently has in the context
     * of this AtomContainer.
     *
     * @param  atom  The atom
     * @return       The maximum bond order that this atom currently has
     */
    @Override
    public Order getMaximumBondOrder(IAtom atom) {
        IBond.Order max = IBond.Order.SINGLE;
        for (int i = 0; i < bondCount; i++) {
            if (bonds[i].contains(atom) && bonds[i].getOrder().ordinal() > max.ordinal()) {
                max = bonds[i].getOrder();
            }
        }
        return max;
    }

    /**
     *  Returns the minimum bond order that this atom currently has in the context
     *  of this AtomContainer.
     *
     *@param  atom  The atom
     *@return       The minimum bond order that this atom currently has
     */
    @Override
    public Order getMinimumBondOrder(IAtom atom) {
        IBond.Order min = IBond.Order.QUADRUPLE;
        for (int i = 0; i < bondCount; i++) {
            if (bonds[i].contains(atom) && bonds[i].getOrder().ordinal() < min.ordinal()) {
                min = bonds[i].getOrder();
            }
        }
        return min;
    }

    /**
     *  Adds all atoms and electronContainers of a given atomcontainer to this
     *  container.
     *
     *@param  atomContainer  The atomcontainer to be added
     */
    @Override
    public void add(IAtomContainer atomContainer) {
        if (atomContainer instanceof QueryAtomContainer) {
            for (int f = 0; f < atomContainer.getAtomCount(); f++) {
                if (!contains(atomContainer.getAtom(f))) {
                    addAtom(atomContainer.getAtom(f));
                }
            }
            for (int f = 0; f < atomContainer.getBondCount(); f++) {
                if (!contains(atomContainer.getBond(f))) {
                    addBond(atomContainer.getBond(f));
                }
            }
            for (int f = 0; f < atomContainer.getLonePairCount(); f++) {
                if (!contains(atomContainer.getLonePair(f))) {
                    addLonePair(atomContainer.getLonePair(f));
                }
            }
            for (int f = 0; f < atomContainer.getSingleElectronCount(); f++) {
                if (!contains(atomContainer.getSingleElectron(f))) {
                    addSingleElectron(atomContainer.getSingleElectron(f));
                }
            }
            notifyChanged();
        } else {
            throw new IllegalArgumentException("AtomContainer is not of type QueryAtomContainer");
        }
    }

    /**
     *  Adds an atom to this container.
     *
     *@param  atom  The atom to be added to this container
     */
    @Override
    public void addAtom(IAtom atom) {
        if (contains(atom)) {
            return;
        }

        if (atomCount + 1 >= atoms.length) {
            growAtomArray();
        }
        atom.addListener(this);
        atoms[atomCount] = atom;
        atomCount++;
        notifyChanged();
    }

    /**
     *  Adds a Bond to this AtomContainer.
     *
     *@param  bond  The bond to added to this container
     */
    @Override
    public void addBond(IBond bond) {
        if (bondCount >= bonds.length) growBondArray();
        bonds[bondCount] = bond;
        ++bondCount;
        notifyChanged();
    }

    /**
     *  Adds a lone pair to this AtomContainer.
     *
     *@param  lonePair  The LonePair to added to this container
     */
    @Override
    public void addLonePair(ILonePair lonePair) {
        if (lonePairCount >= lonePairs.length) growLonePairArray();
        lonePairs[lonePairCount] = lonePair;
        ++lonePairCount;
        notifyChanged();
    }

    /**
     *  Adds a single electron to this AtomContainer.
     *
     *@param  singleElectron  The SingleElectron to added to this container
     */
    @Override
    public void addSingleElectron(ISingleElectron singleElectron) {
        if (singleElectronCount >= singleElectrons.length) growSingleElectronArray();
        singleElectrons[singleElectronCount] = singleElectron;
        ++singleElectronCount;
        notifyChanged();
    }

    /**
     *  Adds a ElectronContainer to this AtomContainer.
     *
     *@param  electronContainer  The ElectronContainer to added to this container
     */
    @Override
    public void addElectronContainer(IElectronContainer electronContainer) {
        if (electronContainer instanceof IBond) this.addBond((IBond) electronContainer);
        if (electronContainer instanceof ILonePair) this.addLonePair((ILonePair) electronContainer);
        if (electronContainer instanceof ISingleElectron) this.addSingleElectron((ISingleElectron) electronContainer);
    }

    /**
     *  Removes all atoms and electronContainers of a given atomcontainer from this
     *  container.
     *
     *@param  atomContainer  The atomcontainer to be removed
     */
    @Override
    public void remove(IAtomContainer atomContainer) {
        for (int f = 0; f < atomContainer.getAtomCount(); f++) {
            removeAtomOnly(atomContainer.getAtom(f));
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
     *  Removes the atom at the given position from the AtomContainer. Note that
     *  the electronContainers are unaffected: you also have to take care of
     *  removing all electronContainers to this atom from the container manually.
     *
     *@param  position  The position of the atom to be removed.
     */
    @Override
    public void removeAtomOnly(int position) {
        atoms[position].removeListener(this);
        for (int i = position; i < atomCount - 1; i++) {
            atoms[i] = atoms[i + 1];
        }
        atoms[atomCount - 1] = null;
        atomCount--;
        notifyChanged();
    }

    /**
     *  Removes the given atom from the AtomContainer. Note that the
     *  electronContainers are unaffected: you also have to take care of removing
     *  all electronContainers to this atom from the container.
     *
     *@param  atom  The atom to be removed
     */
    @Override
    public void removeAtomOnly(IAtom atom) {
        int position = getAtomNumber(atom);
        if (position != -1) {
            removeAtomOnly(position);
        }
    }

    /**
     *  Removes the bond at the given position from the AtomContainer.
     *
     *@param  position  The position of the bond to be removed.
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
     * Removes the bond that connects the two given atoms.
     *
     * @param  atom1  The first atom
     * @param  atom2  The second atom
     * @return        The bond that connects the two atoms
     */
    @Override
    public IBond removeBond(IAtom atom1, IAtom atom2) {
        int pos = indexOf(getBond(atom1, atom2));
        IBond bond = null;
        if (pos != -1) {
            bond = bonds[pos];
            removeBond(pos);
        }
        return bond;
    }

    /**
     * Removes the bond from this container.
     *
     * @param  bond   The bond to be removed.
     */
    @Override
    public void removeBond(IBond bond) {
        int pos = getBondNumber(bond);
        if (pos != -1) removeBond(pos);
    }

    /**
     *  Removes the lone pair at the given position from the AtomContainer.
     *
     *@param  position  The position of the LonePair to be removed.
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
     *  Removes the lone pair from the AtomContainer.
     *
     *@param  lonePair  The LonePair to be removed.
     */
    @Override
    public void removeLonePair(ILonePair lonePair) {
        int pos = indexOf(lonePair);
        if (pos != -1) removeLonePair(pos);
    }

    /**
     *  Removes the single electron at the given position from the AtomContainer.
     *
     *@param  position  The position of the SingleElectron to be removed.
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
     *  Removes the single electron from the AtomContainer.
     *
     *@param  singleElectron  The SingleElectron to be removed.
     */
    @Override
    public void removeSingleElectron(ISingleElectron singleElectron) {
        int pos = indexOf(singleElectron);
        if (pos != -1) removeSingleElectron(pos);
    }

    /**
     * Removes the bond at the given position from this container.
     *
     * @param  number  The position of the bond in the electronContainers array
     * @return           Bond that was removed
     */
    @Override
    public IElectronContainer removeElectronContainer(int number) {
        if (number < this.bondCount) return removeBond(number);
        number -= this.bondCount;
        if (number < this.lonePairCount) return removeLonePair(number);
        number -= this.lonePairCount;
        if (number < this.singleElectronCount) return removeSingleElectron(number);
        return null;
    }

    /**
     * Removes this ElectronContainer from this container.
     *
     * @param electronContainer The electronContainer to be removed
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
    @Deprecated
    public void removeAtomAndConnectedElectronContainers(IAtom atom) {
        removeAtom(atom);
    }

    /**
     *  Removes the given atom and all connected electronContainers from the
     *  AtomContainer.
     *
     *@param  atom  The atom to be removed
     */
    @Override
    public void removeAtom(IAtom atom) {
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
            removeAtomOnly(position);
        }
        notifyChanged();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAtom(int pos) {
        removeAtom(getAtom(pos));
    }

    /**
     * Removes all atoms and bond from this container.
     */
    @Override
    public void removeAllElements() {
        removeAllElectronContainers();
        for (int f = 0; f < getAtomCount(); f++) {
            getAtom(f).removeListener(this);
        }
        atoms = new IAtom[growArraySize];
        atomCount = 0;
        notifyChanged();
    }

    /**
     *  Removes electronContainers from this container.
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
     *  Removes all Bonds from this container.
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
     *  Adds a bond to this container.
     *
     *@param  atom1   Id of the first atom of the Bond in [0,..]
     *@param  atom2   Id of the second atom of the Bond in [0,..]
     *@param  order   Bondorder
     *@param  stereo  Stereochemical orientation
     */
    @Override
    public void addBond(int atom1, int atom2, IBond.Order order, IBond.Stereo stereo) {
        IBond bond = getBuilder().newInstance(IBond.class, getAtom(atom1), getAtom(atom2), order, stereo);

        if (contains(bond)) {
            return;
        }

        if (bondCount >= bonds.length) {
            growBondArray();
        }
        addBond(bond);
        /*
         * no notifyChanged() here because addBond(bond) does it already
         */
    }

    /**
     *  Adds a bond to this container.
     *
     *@param  atom1  Id of the first atom of the Bond in [0,..]
     *@param  atom2  Id of the second atom of the Bond in [0,..]
     *@param  order  Bondorder
     */
    @Override
    public void addBond(int atom1, int atom2, IBond.Order order) {
        IBond bond = getBuilder().newInstance(IBond.class, getAtom(atom1), getAtom(atom2), order);

        if (bondCount >= bonds.length) {
            growBondArray();
        }
        addBond(bond);
        /*
         * no notifyChanged() here because addBond(bond) does it already
         */
    }

    /**
     *  Adds a LonePair to this Atom.
     *
     *@param  atomID  The atom number to which the LonePair is added in [0,..]
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
     *  Adds a LonePair to this Atom.
     *
     *@param  atomID  The atom number to which the LonePair is added in [0,..]
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
     *  True, if the AtomContainer contains the given atom object.
     *
     *@param  atom  the atom this AtomContainer is searched for
     *@return       true if the AtomContainer contains the given atom object
     */
    @Override
    public boolean contains(IAtom atom) {
        for (int i = 0; i < getAtomCount(); i++) {
            if (atoms[i].equals(atom)) return true;
        }
        return false;
    }

    /**
     *  True, if the AtomContainer contains the given bond object.
     *
     *@param  bond  the bond this AtomContainer is searched for
     *@return       true if the AtomContainer contains the given bond object
     */
    @Override
    public boolean contains(IBond bond) {
        for (int i = 0; i < getBondCount(); i++) {
            if (bonds[i].equals(bond)) return true;
        }
        return false;
    }

    /**
     *  True, if the AtomContainer contains the given LonePair object.
     *
     *@param  lonePair  the LonePair this AtomContainer is searched for
     *@return           true if the AtomContainer contains the given LonePair object
     */
    @Override
    public boolean contains(ILonePair lonePair) {
        for (int i = 0; i < getLonePairCount(); i++) {
            if (lonePair == lonePairs[i]) return true;
        }
        return false;
    }

    /**
     *  True, if the AtomContainer contains the given SingleElectron object.
     *
     *@param  singleElectron  the LonePair this AtomContainer is searched for
     *@return           true if the AtomContainer contains the given LonePair object
     */
    @Override
    public boolean contains(ISingleElectron singleElectron) {
        for (int i = 0; i < getSingleElectronCount(); i++) {
            if (singleElectron == singleElectrons[i]) return true;
        }
        return false;
    }

    /**
     *  True, if the AtomContainer contains the given ElectronContainer object.
     *
     *@param  electronContainer ElectronContainer that is searched for
     *@return                   true if the AtomContainer contains the given bond object
     */
    @Override
    public boolean contains(IElectronContainer electronContainer) {
        if (electronContainer instanceof IBond) return contains((IBond) electronContainer);
        if (electronContainer instanceof ILonePair) return contains((ILonePair) electronContainer);
        if (electronContainer instanceof ISingleElectron) return contains((ISingleElectron) electronContainer);
        return false;
    }

    /**
     * Clones this AtomContainer object and its content.
     *
     * @return    The cloned object
     * @see       #QueryAtomContainer(org.openscience.cdk.interfaces.IAtomContainer, org.openscience.cdk.interfaces.IChemObjectBuilder)
     */
    @Override
    public IQueryAtomContainer clone() throws CloneNotSupportedException {
        IAtom[] newAtoms;
        IQueryAtomContainer clone = (IQueryAtomContainer) super.clone();
        // start from scratch
        clone.removeAllElements();
        // clone all atoms
        for (int f = 0; f < getAtomCount(); f++) {
            clone.addAtom((IAtom) getAtom(f).clone());
        }
        // clone bonds
        IBond bond;
        IBond newBond;
        for (int i = 0; i < getBondCount(); ++i) {
            bond = getBond(i);
            newBond = (IBond) bond.clone();
            newAtoms = new IAtom[bond.getAtomCount()];
            for (int j = 0; j < bond.getAtomCount(); ++j) {
                newAtoms[j] = clone.getAtom(getAtomNumber(bond.getAtom(j)));
            }
            newBond.setAtoms(newAtoms);
            clone.addBond(newBond);
        }
        ILonePair lp;
        ILonePair newLp;
        for (int i = 0; i < getLonePairCount(); ++i) {
            lp = getLonePair(i);
            newLp = (ILonePair) lp.clone();
            if (lp.getAtom() != null) {
                newLp.setAtom(clone.getAtom(getAtomNumber(lp.getAtom())));
            }
            clone.addLonePair(newLp);
        }
        ISingleElectron se;
        ISingleElectron newSe;
        for (int i = 0; i < getSingleElectronCount(); ++i) {
            se = getSingleElectron(i);
            newSe = (ISingleElectron) se.clone();
            if (se.getAtom() != null) {
                newSe.setAtom(clone.getAtom(getAtomNumber(se.getAtom())));
            }
            clone.addSingleElectron(newSe);
        }
        return clone;
    }

    /**
     *  Grows the atom array by a given size.
     *
     *@see    #growArraySize
     */
    private void growAtomArray() {
        growArraySize = (atoms.length < growArraySize) ? growArraySize : atoms.length;
        IAtom[] newatoms = new IAtom[atoms.length + growArraySize];
        System.arraycopy(atoms, 0, newatoms, 0, atoms.length);
        atoms = newatoms;
    }

    /**
     *  Grows the bond array by a given size.
     *
     *@see    #growArraySize
     */
    private void growBondArray() {
        growArraySize = (bonds.length < growArraySize) ? growArraySize : bonds.length;
        IBond[] newBonds = new IBond[bonds.length + growArraySize];
        System.arraycopy(bonds, 0, newBonds, 0, bonds.length);
        bonds = newBonds;
    }

    /**
     *  Grows the lone pair array by a given size.
     *
     *@see    #growArraySize
     */
    private void growLonePairArray() {
        growArraySize = (lonePairs.length < growArraySize) ? growArraySize : lonePairs.length;
        ILonePair[] newLonePairs = new ILonePair[lonePairs.length + growArraySize];
        System.arraycopy(lonePairs, 0, newLonePairs, 0, lonePairs.length);
        lonePairs = newLonePairs;
    }

    /**
     *  Grows the single electron array by a given size.
     *
     *@see    #growArraySize
     */
    private void growSingleElectronArray() {
        growArraySize = (singleElectrons.length < growArraySize) ? growArraySize : singleElectrons.length;
        ISingleElectron[] newSingleElectrons = new ISingleElectron[singleElectrons.length + growArraySize];
        System.arraycopy(singleElectrons, 0, newSingleElectrons, 0, singleElectrons.length);
        singleElectrons = newSingleElectrons;
    }

    /**
    *  Called by objects to which this object has
    *  registered as a listener.
    *
    *@param  event  A change event pointing to the source of the change
    */
    @Override
    public void stateChanged(IChemObjectChangeEvent event) {
        notifyChanged(event);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isEmpty() {
        return atomCount == 0;
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

    @Override
    public void setStereoElements(List<IStereoElement> elements) {
        this.stereoElements.clear();
        this.stereoElements.addAll(elements);
    }
}
