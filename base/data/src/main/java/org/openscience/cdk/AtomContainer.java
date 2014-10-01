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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openscience.cdk.annotations.TestMethod;
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
 *  Base class for all chemical objects that maintain a list of Atoms and
 *  ElectronContainers. <p>
 *
 *  Looping over all Bonds in the AtomContainer is typically done like: <pre>
 * Iterator iter = atomContainer.bonds();
 * while (iter.hasNext()) {
 *   IBond aBond = (IBond) iter.next();
 * }
 *
 *  </pre>
 *
 * @cdk.module data
 * @cdk.githash
 *
 * @author steinbeck
 * @cdk.created 2000-10-02
 */
public class AtomContainer extends ChemObject implements IAtomContainer, IChemObjectListener, Serializable, Cloneable {

    /**
     * Determines if a de-serialized object is compatible with this class.
     *
     * This value must only be changed if and only if the new version
     * of this class is incompatible with the old version. See Sun docs
     * for <a href=http://java.sun.com/products/jdk/1.1/docs/guide
     * /serialization/spec/version.doc.html>details</a>.
     */
    private static final long     serialVersionUID = 5678100348445919254L;

    /**
     *  Number of atoms contained by this object.
     */
    protected int                 atomCount;

    /**
     *  Number of bonds contained by this object.
     */
    protected int                 bondCount;

    /**
     *  Number of lone pairs contained by this object.
     */
    protected int                 lonePairCount;

    /**
     *  Number of single electrons contained by this object.
     */
    protected int                 singleElectronCount;

    /**
     *  Amount by which the bond and atom arrays grow when elements are added and
     *  the arrays are not large enough for that.
     */
    protected int                 growArraySize    = 10;

    /**
     *  Internal array of atoms.
     */
    protected IAtom[]             atoms;

    /**
     *  Internal array of bonds.
     */
    protected IBond[]             bonds;

    /**
     *  Internal array of lone pairs.
     */
    protected ILonePair[]         lonePairs;

    /**
     *  Internal array of single electrons.
     */
    protected ISingleElectron[]   singleElectrons;

    /**
     * Internal list of atom parities.
     */
    protected Set<IStereoElement> stereoElements;

    /**
     *  Constructs an empty AtomContainer.
     */
    public AtomContainer() {
        this(10, 10, 0, 0);
    }

    /**
     * Constructs an AtomContainer with a copy of the atoms and electronContainers
     * of another AtomContainer (A shallow copy, i.e., with the same objects as in
     * the original AtomContainer).
     *
     * @param  container  An AtomContainer to copy the atoms and electronContainers from
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
    public AtomContainer(int atomCount, int bondCount, int lpCount, int seCount) {
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

    /** {@inheritDoc} */
    @Override
    public void addStereoElement(IStereoElement element) {
        stereoElements.add(element);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void setStereoElements(List<IStereoElement> elements) {
        this.stereoElements = new HashSet<IStereoElement>();
        this.stereoElements.addAll(elements);
    }

    /** {@inheritDoc} */
    @Override
    public Iterable<IStereoElement> stereoElements() {
        return Collections.unmodifiableSet(stereoElements);
    }

    /**
     *  Sets the array of atoms of this AtomContainer.
     *
     *@param  atoms  The array of atoms to be assigned to this AtomContainer
     *@see           #getAtom
     */
    @Override
    public void setAtoms(IAtom[] atoms) {
        // unregister this as listener with the old atoms
        for (IAtom atom : this.atoms)
            if (atom != null) atom.removeListener(this);

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
     *  Sets the array of electronContainers of this AtomContainer.
     *
     *@param  electronContainers  The array of electronContainers to be assigned to
     *      this AtomContainer
     *@see  #getElectronContainers
     */
    //	public void setElectronContainers(IElectronContainer[] electronContainers)
    //	{
    //		this.electronContainers = electronContainers;
    //		for (int f = 0; f < electronContainers.length; f++)
    //		{
    //			electronContainers[f].addListener(this);
    //		}
    //		setElectronContainerCount(electronContainers.length);
    //		notifyChanged();
    //	}

    /**
     *  Sets the atom at position <code>number</code> in [0,..].
     *
     *@param  number  The position of the atom to be set.
     *@param  atom    The atom to be stored at position <code>number</code>
     *@see            #getAtom(int)
     */
    @Override
    public void setAtom(int number, IAtom atom) {
        atom.addListener(this);
        atoms[number] = atom;
        notifyChanged();
    }

    /**
     *  Get the atom at position <code>number</code> in [0,..].
     *
     *@param  number  The position of the atom to be retrieved.
     *@return         The atomAt value
     * @see #setAtom(int, org.openscience.cdk.interfaces.IAtom)
     * @see #setAtoms(org.openscience.cdk.interfaces.IAtom[])
     *
     */
    @Override
    public IAtom getAtom(int number) {
        return atoms[number];
    }

    /**
     *  Get the bond at position <code>number</code> in [0,..].
     *
     *@param  number  The position of the bond to be retrieved.
     *@return         The bondAt value
     */
    @Override
    public IBond getBond(int number) {
        return bonds[number];
    }

    /**
     *  Get the lone pair at position <code>number</code> in [0,..].
     *
     *@param  number  The position of the LonePair to be retrieved.
     *@return         The lone pair number
     */
    @Override
    public ILonePair getLonePair(int number) {
        return lonePairs[number];
    }

    /**
     *  Get the single electron at position <code>number</code> in [0,..].
     *
     *@param  number  The position of the SingleElectron to be retrieved.
     *@return         The single electron number
     */
    @Override
    public ISingleElectron getSingleElectron(int number) {
        return singleElectrons[number];
    }

    /**
     * Sets the ElectronContainer at position <code>number</code> in [0,..].
     *
     * @param  number            The position of the ElectronContainer to be set.
     * @param  electronContainer The ElectronContainer to be stored at position <code>number</code>
     * @see                      #getElectronContainer(int)
     */
    //	public void setElectronContainer(int number, IElectronContainer electronContainer)
    //	{
    //		electronContainer.addListener(this);
    //		electronContainers[number] = electronContainer;
    //		notifyChanged();
    //	}

    /**
     * Sets the number of electronContainers in this container.
     *
     * @param  electronContainerCount  The number of electronContainers in this
     *                                 container
     * @see                            #getElectronContainerCount
     */
    //	public void setElectronContainerCount(int electronContainerCount)
    //	{
    //		this.electronContainerCount = electronContainerCount;
    //		notifyChanged();
    //	}

    /**
     *  Sets the number of atoms in this container.
     *
     *@param  atomCount  The number of atoms in this container
     *@see               #getAtomCount
     */
    //	public void setAtomCount(int atomCount)
    //	{
    //		this.atomCount = atomCount;
    //		notifyChanged();
    //	}

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
            removeAtom(--pointer);
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
        for (int f = 0; f < atomCount; f++) {
            if (atoms[f] == atom) return f;
        }
        return -1;
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
        return (getBondNumber(getBond(atom1, atom2)));
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
        for (int f = 0; f < bondCount; f++) {
            if (bonds[f] == bond) return f;
        }
        return -1;
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
        for (int f = 0; f < lonePairCount; f++) {
            if (lonePairs[f] == lonePair) return f;
        }
        return -1;
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
        for (int f = 0; f < singleElectronCount; f++) {
            if (singleElectrons[f] == singleElectron) return f;
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
            if (bonds[i].contains(atom1) && bonds[i].getConnectedAtom(atom1) == atom2) {
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
     *  Returns the number of the single electrons in this container.
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
            if (bonds[i].contains(atom)) atomsList.add(bonds[i].getConnectedAtom(atom));
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
     *@param  atomNumber  The atomnumber the degree is searched for
     *@return             The number of connected atoms (degree)
     */
    @Override
    public int getConnectedBondsCount(int atomNumber) {
        return getConnectedAtomsCount(atoms[atomNumber]);
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
                IBond.Order order = bonds[i].getOrder();
                if (order != null) {
                    count += order.numeric();
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
            if (bonds[i].contains(atom) && bonds[i].getOrder().numeric() > max.numeric()) {
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
            if (bonds[i].contains(atom) && bonds[i].getOrder().numeric() < min.numeric()) {
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

        for (IStereoElement se : atomContainer.stereoElements())
            stereoElements.add(se);

        notifyChanged();
    }

    /**
     *  Adds the <code>ElectronContainer</code>s found in atomContainer to this
     *  container.
     *
     *@param  atomContainer  AtomContainer with the new ElectronContainers
     */
    //	public void addElectronContainers(IAtomContainer atomContainer)
    //	{
    //
    //		notifyChanged();
    //	}

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
     *  Removes the atom at the given position from the AtomContainer. Note that
     *  the electronContainers are unaffected: you also have to take care of
     *  removing all electronContainers to this atom from the container manually.
     *
     *@param  position  The position of the atom to be removed.
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
     *  Removes the given atom from the AtomContainer. Note that the
     *  electronContainers are unaffected: you also have to take care of removing
     *  all electronContainers to this atom from the container.
     *
     *@param  atom  The atom to be removed
     */
    @Override
    public void removeAtom(IAtom atom) {
        int position = getAtomNumber(atom);
        if (position != -1) {
            removeAtom(position);
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
        int pos = getBondNumber(atom1, atom2);
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
        int pos = getLonePairNumber(lonePair);
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
        int pos = getSingleElectronNumber(singleElectron);
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

    /** @inheritDoc */
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
     * @inheritDoc
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
            if (atom == atoms[i]) return true;
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
            if (bond == bonds[i]) return true;
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
        if (electronContainer instanceof ISingleElectron) return contains((SingleElectron) electronContainer);
        return false;
    }

    /**
     *  Returns a one line string representation of this Container. This method is
     *  conform RFC #9.
     *
     *@return    The string representation of this Container
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
     * Clones this AtomContainer object and its content.
     *
     * @return    The cloned object
     * @see       #shallowCopy
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

            IBond original = this.bonds[i];
            IBond bond = (IBond) original.clone();
            int n = bond.getAtomCount();
            IAtom[] members = new IAtom[n];

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
            ILonePair pair = (ILonePair) original.clone();

            if (pair.getAtom() != null) pair.setAtom(atomMap.get(original.getAtom()));

            clone.addLonePair(pair);
        }

        // clone single electrons (we can't use an array to buffer as there is no setSingleElectrons())
        for (int i = 0; i < singleElectronCount; i++) {

            ISingleElectron original = this.singleElectrons[i];
            ISingleElectron electron = (ISingleElectron) original.clone();

            if (electron.getAtom() != null) electron.setAtom(atomMap.get(original.getAtom()));

            clone.addSingleElectron(electron);
        }

        // map each stereo element to a new instance in the clone
        for (IStereoElement element : stereoElements) {
            clone.addStereoElement(element.map(atomMap, bondMap));
        }

        return clone;

    }

    /**
     *  Grows the ElectronContainer array by a given size.
     *
     *@see    #growArraySize
     */
    //	protected void growElectronContainerArray()
    //	{
    //		growArraySize = (electronContainers.length < growArraySize) ? growArraySize : electronContainers.length;
    //		IElectronContainer[] newelectronContainers = new IElectronContainer[electronContainers.length + growArraySize];
    //		System.arraycopy(electronContainers, 0, newelectronContainers, 0, electronContainers.length);
    //		electronContainers = newelectronContainers;
    //	}

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

    /**
     * @inheritDoc
     */
    @TestMethod("testIsEmpty")
    @Override
    public boolean isEmpty() {
        return atomCount == 0;
    }
}
