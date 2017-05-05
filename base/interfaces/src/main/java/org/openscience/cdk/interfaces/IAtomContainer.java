/* Copyright (C) 2006-2007,2010  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General License for more details.
 *
 * You should have received a copy of the GNU Lesser General License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.interfaces;

import org.openscience.cdk.interfaces.IBond.Order;

import java.util.List;

/**
 * Base class for all chemical objects that maintain a list of Atoms and
 * ElectronContainers. <p>
 * <p>
 * Looping over all <code>IBond</code>s in the <code>IAtomContainer</code>
 * is typically done like:
 * <pre>
 *  for (IBond bond : atomContainer.bonds()) {
 *    // do something
 *  }
 *  </pre>
 * If you do need an explicit Iterator then use
 * <pre>{@code
 * Iterator<IBond> bondIter = atomContainer.bonds().iterator();
 * }</pre>
 *
 * @author steinbeck
 * @cdk.module interfaces
 * @cdk.githash
 * @cdk.created 2000-10-02
 */
public interface IAtomContainer extends IChemObject, IChemObjectListener {

    /**
     * Adds a stereo element to this container.
     *
     * @param element The new {@link IStereoElement} for this container
     * @see #stereoElements()
     */
    void addStereoElement(IStereoElement element);

    /**
     * Set the stereo elements - this will replace the existing instance
     * with a new instance.
     *
     * @param elements the new stereo elements
     */
    void setStereoElements(List<IStereoElement> elements);

    /**
     * Returns the stereo elements defined for this atom container.
     *
     * @return An {@link Iterable} of {@link IStereoElement}s.
     * @see #addStereoElement(IStereoElement)
     */
    Iterable<IStereoElement> stereoElements();

    /**
     * Sets the array of atoms of this AtomContainer.
     *
     * @param atoms The array of atoms to be assigned to this AtomContainer
     * @see #atoms
     */
    void setAtoms(IAtom[] atoms);

    /**
     * Sets the array of bonds of this AtomContainer.
     *
     * @param bonds The array of bonds to be assigned to
     *              this AtomContainer
     * @see #bonds
     */
    void setBonds(IBond[] bonds);

    /**
     * Set the atom at <code>idx</code>, the index must have an existing atom
     * and therefore be in the range 0 &le; idx &lt; mol.getAtomCount().
     *
     * @param idx  The index of the atom to be set.
     * @param atom The atom to be stored at position <code>idx</code>
     * @throws IndexOutOfBoundsException index is out of bounds
     * @throws IllegalArgumentException the atom counld not be set
     * @see #getAtom
     */
    void setAtom(int idx, IAtom atom);

    /**
     * Get the atom at the specified <b>idx</b>, the index should be in the
     * range 0 &le; <i>idx</i> &lt; {@link #getAtomCount()}.
     *
     * @param idx atom index
     * @return the atom stored at the index
     * @see #setAtom
     * @throws IndexOutOfBoundsException the index is out of range
     */
    IAtom getAtom(int idx);

    /**
     * Get the bond at the specified <b>idx</b>, the index should be in the
     * range 0 &le; <i>idx</i> &lt; {@link #getBondCount()}.
     *
     * @param idx bond index
     * @return the bond stored at the index
     * @throws IndexOutOfBoundsException the index is out of range
     */
    IBond getBond(int idx);

    /**
     * Get the lone pair at the specified <b>idx</b>, the index should be in the
     * range 0 &le; <i>idx</i> &lt; {@link #getLonePairCount()}.
     *
     * @param idx lone pair index
     * @return the lone pair stored at the index
     * @throws IndexOutOfBoundsException the index is out of range
     */
    ILonePair getLonePair(int idx);

    /**
     * Get the single electron at the specified <b>idx</b>, the index should
     * be in the range 0 &le; <i>idx</i> &lt; {@link #getSingleElectronCount()}.
     *
     * @param idx single electron index
     * @return the single electron stored at the index
     * @throws IndexOutOfBoundsException the index is out of range
     */
    ISingleElectron getSingleElectron(int idx);

    /**
     * Returns an Iterable for looping over all atoms in this container.
     *
     * @return An Iterable with the atoms in this container
     */
    Iterable<IAtom> atoms();

    /**
     * Returns an Iterable for looping over all bonds in this container.
     *
     * @return An Iterable with the bonds in this container
     */
    Iterable<IBond> bonds();

    /**
     * Returns an Iterable for looping over all lone pairs in this container.
     *
     * @return An Iterable with the lone pairs in this container
     */
    Iterable<ILonePair> lonePairs();

    /**
     * Returns an Iterable for looping over all single electrons in this container.
     *
     * @return An Iterable with the single electrons in this container
     */
    Iterable<ISingleElectron> singleElectrons();

    /**
     * Returns an Iterable for looping over all electron containers in this container.
     *
     * @return An Iterable with the electron containers in this container
     */
    Iterable<IElectronContainer> electronContainers();

    /**
     * Returns the atom at position 0 in the container.
     *
     * @return The atom at position 0 .
     * @deprecated use {@link #getAtom(int)}
     */
    @Deprecated
    IAtom getFirstAtom();

    /**
     * Returns the atom at the last position in the container.
     *
     * @return The atom at the last position
     * @deprecated use {@link #getAtom(int)}
     */
    @Deprecated
    IAtom getLastAtom();

    /**
     * Returns the position of a given atom in the atoms array. It returns -1 if
     * the atom atom does not exist.
     *
     * @param atom The atom to be sought
     * @return The Position of the atom in the atoms array in [0,..].
     * @deprecated use {@link #indexOf(IAtom)}
     */
    @Deprecated
    int getAtomNumber(IAtom atom);

    /**
     * Returns the position of the bond between two given atoms in the
     * electronContainers array. It returns -1 if the bond does not exist.
     *
     * @param atom1 The first atom
     * @param atom2 The second atom
     * @return The Position of the bond between a1 and a2 in the
     * electronContainers array.
     * @deprecated use {@link #indexOf(IBond)}
     */
    @Deprecated
    int getBondNumber(IAtom atom1, IAtom atom2);

    /**
     * Returns the position of a given bond in the electronContainers array. It
     * returns -1 if the bond does not exist.
     *
     * @param bond The bond to be sought
     * @return The Position of the bond in the electronContainers array in [0,..].
     * @deprecated use {@link #indexOf(IBond)}
     */
    @Deprecated
    int getBondNumber(IBond bond);

    /**
     * Returns the position of a given lone pair in the lone pair array.
     * It returns -1 if the lone pair does not exist.
     *
     * @param lonePair The lone pair to be sought
     * @return The Position of the lone pair in the array..
     * @deprecated use {@link #indexOf(ILonePair)}
     */
    @Deprecated
    int getLonePairNumber(ILonePair lonePair);

    /**
     * Returns the position of a given single electron in the single electron array.
     * It returns -1 if the single electron does not exist.
     *
     * @param singleElectron The single electron to be sought
     * @return The Position of the single electron in the array.
     * @deprecated use {@link #indexOf(ISingleElectron)}
     */
    int getSingleElectronNumber(ISingleElectron singleElectron);

    /**
     * Access the storage index of an atom.
     *
     * @param atom atom instance to find
     * @return the index, -1 if not found
     */
    int indexOf(IAtom atom);

    /**
     * Access the storage index of a bond.
     *
     * @param bond bond instance to find
     * @return the index, -1 if not found
     */
    int indexOf(IBond bond);

    /**
     * Access the storage index of a single electron (radical).
     *
     * @param electron the electron
     * @return the index, -1 if not found
     */
    int indexOf(ISingleElectron electron);

    /**
     * Access the storage index of a long pair.
     *
     * @param pair the long pair
     * @return the index, -1 if not found
     */
    int indexOf(ILonePair pair);

    /**
     * Returns the ElectronContainer at position <code>number</code> in the
     * container.
     *
     * @param number The position of the ElectronContainer to be returned.
     * @return The ElectronContainer at position <code>number</code>.
     * @see #addElectronContainer(IElectronContainer)
     */
    IElectronContainer getElectronContainer(int number);

    /**
     * Returns the bond that connectes the two given atoms.
     *
     * @param atom1 The first atom
     * @param atom2 The second atom
     * @return The bond that connectes the two atoms
     */
    IBond getBond(IAtom atom1, IAtom atom2);

    /**
     * Returns the number of Atoms in this Container.
     *
     * @return The number of Atoms in this Container
     */
    int getAtomCount();

    /**
     * Returns the number of Bonds in this Container.
     *
     * @return The number of Bonds in this Container
     */
    int getBondCount();

    /**
     * Returns the number of LonePairs in this Container.
     *
     * @return The number of LonePairs in this Container
     */
    int getLonePairCount();

    /**
     * Returns the number of the single electrons in this container.
     *
     * @return The number of SingleElectron objects of this AtomContainer
     */
    int getSingleElectronCount();

    /**
     * Returns the number of ElectronContainers in this Container.
     *
     * @return The number of ElectronContainers in this Container
     */
    int getElectronContainerCount();

    /**
     * Returns an ArrayList of all atoms connected to the given atom. If
     * bonds are added to the container after the list is retrieved the
     * connected atoms will not update to reflect this.
     *
     * @param atom The atom the bond partners are searched of.
     * @return The ArrayList with the connected atoms
     */
    List<IAtom> getConnectedAtomsList(IAtom atom);

    /**
     * Returns an ArrayList of all Bonds connected to the given atom. If
     * bonds are added to the container after the list is retrieved the
     * connected atoms will not update to reflect this.
     *
     * @param atom The atom the connected bonds are searched of
     * @return The ArrayList with connected atoms
     */
    List<IBond> getConnectedBondsList(IAtom atom);

    /**
     * Returns the array of lone pairs connected to an atom. If
     * lone pairs are added to the container after the list is
     * retrieved the connected atoms will not update to reflect this.
     *
     * @param atom The atom for which to get lone pairs
     * @return The array of LonePairs of this AtomContainer
     */
    List<ILonePair> getConnectedLonePairsList(IAtom atom);

    /**
     * Returns an array of all SingleElectron connected to the given atom.
     * If single electrons are added to the container after the list is
     * retrieved the connected atoms will not update to reflect this.
     *
     * @param atom The atom on which the single electron is located
     * @return The array of SingleElectron of this AtomContainer
     */
    List<ISingleElectron> getConnectedSingleElectronsList(IAtom atom);

    /**
     * Returns an ArrayList of all electronContainers connected to the given atom.
     *
     * @param atom The atom the connected electronContainers are searched of
     * @return The ArrayList with the  connected atoms
     */
    List<IElectronContainer> getConnectedElectronContainersList(IAtom atom);

    /**
     * Returns the number of atoms connected to the given atom.
     *
     * @param atom The atom the number of bond partners are searched of.
     * @return The the size of connected atoms
     */
    int getConnectedAtomsCount(IAtom atom);

    /**
     * Returns the number of Bonds for a given Atom.
     *
     * @param atom The atom
     * @return The number of Bonds for this atom
     */
    int getConnectedBondsCount(IAtom atom);

    /**
     * Returns the number of connected atoms (degree) to the given atom.
     *
     * @param atomnumber The atomnumber the degree is searched for
     * @return The number of connected atoms (degree)
     */
    int getConnectedBondsCount(int atomnumber);

    /**
     * Returns the number of LonePairs for a given Atom.
     *
     * @param atom The atom
     * @return The number of LonePairs for this atom
     */
    int getConnectedLonePairsCount(IAtom atom);

    /**
     * Returns the sum of the SingleElectron for a given Atom.
     *
     * @param atom The atom on which the single electron is located
     * @return The array of SingleElectron of this AtomContainer
     */
    int getConnectedSingleElectronsCount(IAtom atom);

    /**
     * Returns the sum of the bond orders for a given Atom.
     *
     * @param atom The atom
     * @return The number of bondorders for this atom
     */
    double getBondOrderSum(IAtom atom);

    /**
     * Returns the maximum bond order that this atom currently has in the context
     * of this AtomContainer.
     *
     * @param atom The atom
     * @return The maximum bond order that this atom currently has
     */
    Order getMaximumBondOrder(IAtom atom);

    /**
     * Returns the minimum bond order that this atom currently has
     * in the context of this AtomContainer. If the atom has no bonds
     * but does have implicit hydrogens the minimum bond order is
     * {@link IBond.Order#SINGLE}, otherwise the bond is unset
     * {@link IBond.Order#UNSET}.
     *
     * @param atom The atom
     * @return The minimum bond order that this atom currently has
     * @throws java.util.NoSuchElementException atom does not belong to this container
     */
    Order getMinimumBondOrder(IAtom atom);

    /**
     * Adds all atoms and electronContainers of a given atomcontainer to this
     * container.
     *
     * @param atomContainer The atomcontainer to be added
     */
    void add(IAtomContainer atomContainer);

    /**
     * Adds an atom to this container.
     *
     * @param atom The atom to be added to this container
     */
    void addAtom(IAtom atom);

    /**
     * Adds a Bond to this AtomContainer.
     *
     * @param bond The bond to added to this container
     */
    void addBond(IBond bond);

    /**
     * Adds a lone pair to this AtomContainer.
     *
     * @param lonePair The LonePair to added to this container
     */
    void addLonePair(ILonePair lonePair);

    /**
     * Adds a single electron to this AtomContainer.
     *
     * @param singleElectron The SingleElectron to added to this container
     */
    void addSingleElectron(ISingleElectron singleElectron);

    /**
     * Adds a ElectronContainer to this AtomContainer.
     *
     * @param electronContainer The ElectronContainer to added to this container
     */
    void addElectronContainer(IElectronContainer electronContainer);

    /**
     * Removes all atoms and electronContainers of a given atomcontainer from this
     * container.
     *
     * @param atomContainer The atomcontainer to be removed
     */
    void remove(IAtomContainer atomContainer);

    /**
     * Unsafely remove atom at index.
     * <br>
     * Removes the atom at the given position from the AtomContainer. Note that
     * the electronContainers are unaffected: you also have to take care of
     * removing all electronContainers to this atom from the container manually.
     *
     * @param position The position of the atom to be removed.
     */
    void removeAtomOnly(int position);

    /**
     * Unsafely remove atom.
     * <br>
     * Removes the given atom from the AtomContainer. Note that the
     * electronContainers are unaffected: you also have to take care of removeing
     * all electronContainers to this atom from the container.
     *
     * @param atom The atom to be removed
     */
    void removeAtomOnly(IAtom atom);

    /**
     * Removes the bond at the given position from the AtomContainer.
     *
     * @param position The position of the bond to be removed.
     * @return the bond at the given position
     */
    IBond removeBond(int position);

    /**
     * Removes the bond that connects the two given atoms.
     *
     * @param atom1 The first atom
     * @param atom2 The second atom
     * @return The bond that connectes the two atoms
     */
    IBond removeBond(IAtom atom1, IAtom atom2);

    /**
     * Removes the bond from this container.
     *
     * @param bond The bond to be removed.
     */
    void removeBond(IBond bond);

    /**
     * Removes the lone pair at the given position from the AtomContainer.
     *
     * @param position The position of the LonePair to be removed.
     * @return The removed ILonePair.
     */
    ILonePair removeLonePair(int position);

    /**
     * Removes the lone pair from the AtomContainer.
     *
     * @param lonePair The LonePair to be removed.
     */
    void removeLonePair(ILonePair lonePair);

    /**
     * Removes the single electron at the given position from the AtomContainer.
     *
     * @param position The position of the SingleElectron to be removed.
     * @return The removed ISingleElectron
     */
    ISingleElectron removeSingleElectron(int position);

    /**
     * Removes the single electron from the AtomContainer.
     *
     * @param singleElectron The SingleElectron to be removed.
     */
    void removeSingleElectron(ISingleElectron singleElectron);

    /**
     * Removes the bond at the given position from this container.
     *
     * @param position The position of the bond in the electronContainers array
     * @return the IElectronContainer that was removed
     */
    IElectronContainer removeElectronContainer(int position);

    /**
     * Removes this ElectronContainer from this container.
     *
     * @param electronContainer The electronContainer to be removed
     */
    void removeElectronContainer(IElectronContainer electronContainer);

    /**
     * Safely remove an atom from the container.
     * <br>
     * Removes a single atom from the container updating all internal
     * state to be consistent. All bonds connected to the atom will be
     * deleted as well as all stereo elements. If multiple atoms/bonds are
     * being deleted they should be gathered into a single transaction
     * and removed with {@link #remove(IAtomContainer)}.
     * <br>
     * If you are removing hydrogens one of the
     * utility methods (e.g. AtomContainerManipulator.removeHydrogens(IAtomContainer))
     * is preferable.
     *
     * @param atom the atom to be removed
     */
    void removeAtom(IAtom atom);

    /**
     * Safely remove an atom from the container.
     * @see #removeAtom(IAtom)
     * @deprecated Method has be renamed {@link #removeAtom(IAtom)}.
     */
    @Deprecated
    void removeAtomAndConnectedElectronContainers(IAtom atom);

    /**
     * Removes all atoms, bonds and stereo elements from this container.
     */
    void removeAllElements();

    /**
     * Removes electronContainers from this container.
     */
    void removeAllElectronContainers();

    /**
     * Removes all Bonds from this container.
     */
    void removeAllBonds();

    /**
     * Adds a bond to this container.
     *
     * @param atom1  Id of the first atom of the Bond in [0,..]
     * @param atom2  Id of the second atom of the Bond in [0,..]
     * @param order  Bondorder
     * @param stereo Stereochemical orientation
     */
    void addBond(int atom1, int atom2, IBond.Order order, IBond.Stereo stereo);

    /**
     * Adds a bond to this container.
     *
     * @param atom1 Id of the first atom of the Bond in [0,..]
     * @param atom2 Id of the second atom of the Bond in [0,..]
     * @param order Bondorder
     */
    void addBond(int atom1, int atom2, IBond.Order order);

    /**
     * Adds a LonePair to this Atom.
     *
     * @param atomID The atom number to which the LonePair is added in [0,..]
     */
    void addLonePair(int atomID);

    /**
     * Adds a SingleElectron to this Atom.
     *
     * @param atomID The atom number to which the SingleElectron is added in [0,..]
     */
    void addSingleElectron(int atomID);

    /**
     * True, if the AtomContainer contains the given atom object.
     *
     * @param atom the atom this AtomContainer is searched for
     * @return True, if the AtomContainer contains the given atom object
     */
    boolean contains(IAtom atom);

    /**
     * True, if the AtomContainer contains the given bond object.
     *
     * @param bond the bond this AtomContainer is searched for
     * @return True, if the AtomContainer contains the given bond object
     */
    boolean contains(IBond bond);

    /**
     * True, if the AtomContainer contains the given LonePair object.
     *
     * @param lonePair the LonePair this AtomContainer is searched for
     * @return True, if the AtomContainer contains the given LonePair object
     */
    boolean contains(ILonePair lonePair);

    /**
     * True, if the AtomContainer contains the given SingleElectron object.
     *
     * @param singleElectron the SingleElectron this AtomContainer is searched for
     * @return True, if the AtomContainer contains the given SingleElectron object
     */
    boolean contains(ISingleElectron singleElectron);

    /**
     * True, if the AtomContainer contains the given ElectronContainer object.
     *
     * @param electronContainer ElectronContainer that is searched for
     * @return True, if the AtomContainer contains the given bond object
     */
    boolean contains(IElectronContainer electronContainer);

    /**
     * Indicates whether this container is empty. The container is considered empty if
     * there are no atoms. Bonds are not checked as a graph with no vertexes can not
     * have edges.
     *
     * @return whether the container is empty
     */
    boolean isEmpty();

    /**
     * {@inheritDoc}
     */
    @Override
    IAtomContainer clone() throws CloneNotSupportedException;
}
