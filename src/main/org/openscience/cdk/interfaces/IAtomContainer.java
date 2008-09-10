/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2006-2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.interfaces;

import org.openscience.cdk.interfaces.IBond.Order;

import java.util.List;

/**
 *  Base class for all chemical objects that maintain a list of Atoms and
 *  ElectronContainers. <p>
 *
 *  Looping over all <code>IBond</code>s in the <code>IAtomContainer</code>
 *  is typically done like:
 *  <pre>
 *  for (IBond bond : atomContainer.bonds()) {
 *    // do something
 *  }
 *  </pre>
 * If you do need an explicit Iterator then use
 * <pre>
 * Iterator<IBond> bondIter = atomContainer.bonds().iterator();
 * </pre>
 *
 * @cdk.module interfaces
 * @cdk.svnrev  $Revision$
 *
 * @author     steinbeck
 * @cdk.created    2000-10-02
 */
public interface IAtomContainer extends IChemObject, IChemObjectListener {

    /**
     * Adds an AtomParity to this container. If a parity is already given for the
     * affected Atom, it is overwritten.
     *
     * @param parity The new AtomParity for this container
     * @see   #getAtomParity
     */
    public void addAtomParity(IAtomParity parity);

    /**
     * Returns the atom parity for the given Atom. If no parity is associated
     * with the given Atom, it returns null.
     *
     * @param  atom   Atom for which the parity must be returned
     * @return The AtomParity for the given Atom, or null if that Atom does
     *         not have an associated AtomParity
     * @see    #addAtomParity
     */
    public IAtomParity getAtomParity(IAtom atom);
    
	/**
	 * Sets the array of atoms of this AtomContainer.
	 *
	 * @param  atoms  The array of atoms to be assigned to this AtomContainer
	 * @see           #atoms
	 */
	public void setAtoms(IAtom[] atoms);
	
	/**
	 * Sets the array of bonds of this AtomContainer.
	 *
	 * @param  bonds  The array of bonds to be assigned to
	 *                             this AtomContainer
	 * @see  #bonds
	 */
	public void setBonds(IBond[] bonds);
	
	/**
	 * Set the atom at position <code>number</code> in [0,..].
	 *
	 * @param  number  The position of the atom to be set.
	 * @param  atom    The atom to be stored at position <code>number</code>
	 * @see            #getAtom
	 */
	public void setAtom(int number, IAtom atom);

	/**
	 * Get the atom at position <code>number</code> in [0,..].
	 *
	 * @param  number  The position of the atom to be retrieved.
	 * @return         The atom number
	 * @see            #setAtom
	 */
	public IAtom getAtom(int number);

	/**
	 *  Get the bond at position <code>number</code> in [0,..].
	 *
	 *@param  number  The position of the bond to be retrieved.
	 *@return         The bond number
	 */
	public IBond getBond(int number);

	/**
	 *  Get the lone pair at position <code>number</code> in [0,..].
	 *
	 *@param  number  The position of the LonePair to be retrieved.
	 *@return         The lone pair number
	 */
	public ILonePair getLonePair(int number);
	
	/**
	 *  Get the single electron at position <code>number</code> in [0,..].
	 *
	 *@param  number  The position of the SingleElectron to be retrieved.
	 *@return         The single electron number
	 */
	public ISingleElectron getSingleElectron(int number);

	/**
	 *  Returns an Iterable for looping over all atoms in this container.
	 *
	 *@return    An Iterable with the atoms in this container
	 */
	public Iterable<IAtom> atoms();

	/**
	 *  Returns an Iterable for looping over all bonds in this container.
	 *
	 *@return    An Iterable with the bonds in this container
	 */
	public Iterable<IBond> bonds();
	
	/**
	 *  Returns an Iterable for looping over all lone pairs in this container.
	 *
	 *@return    An Iterable with the lone pairs in this container
	 */
	public Iterable<ILonePair> lonePairs();
	
	/**
	 *  Returns an Iterable for looping over all single electrons in this container.
	 *
	 *@return    An Iterable with the single electrons in this container
	 */
	public Iterable<ISingleElectron> singleElectrons();
	
	/**
	 *  Returns an Iterable for looping over all electron containers in this container.
	 *
	 *@return    An Iterable with the electron containers in this container
	 */
	public Iterable<IElectronContainer> electronContainers();
	
	/**
	 *  Returns the atom at position 0 in the container.
	 *
	 *@return    The atom at position 0 .
	 */
	public IAtom getFirstAtom();


	/**
	 *  Returns the atom at the last position in the container.
	 *
	 *@return    The atom at the last position
	 */
	public IAtom getLastAtom();


	/**
	 *  Returns the position of a given atom in the atoms array. It returns -1 if
	 *  the atom atom does not exist.
	 *
	 *@param  atom  The atom to be sought
	 *@return       The Position of the atom in the atoms array in [0,..].
	 */
	public int getAtomNumber(IAtom atom);


	/**
	 *  Returns the position of the bond between two given atoms in the
	 *  electronContainers array. It returns -1 if the bond does not exist.
	 *
	 *@param  atom1  The first atom
	 *@param  atom2  The second atom
	 *@return        The Position of the bond between a1 and a2 in the
	 *               electronContainers array.
	 */
	public int getBondNumber(IAtom atom1, IAtom atom2);


	/**
	 *  Returns the position of a given bond in the electronContainers array. It
	 *  returns -1 if the bond does not exist.
	 *
	 *@param  bond  The bond to be sought
	 *@return       The Position of the bond in the electronContainers array in [0,..].
	 */
	public int getBondNumber(IBond bond);

	/**
	 * Returns the position of a given lone pair in the lone pair array. 
	 * It returns -1 if the lone pair does not exist.
	 *
	 * @param  lonePair The lone pair to be sought
	 * @return          The Position of the lone pair in the array..
	 */
	public int getLonePairNumber(ILonePair lonePair);
	
	/**
	 * Returns the position of a given single electron in the single electron array. 
	 * It returns -1 if the single electron does not exist.
	 *
	 * @param  singleElectron The single electron to be sought
	 * @return                The Position of the single electron in the array.
	 */
	public int getSingleElectronNumber(ISingleElectron singleElectron);
	
	/**
	 *  Returns the ElectronContainer at position <code>number</code> in the
	 *  container.
	 *
	 *@param  number  The position of the ElectronContainer to be returned.
	 *@return         The ElectronContainer at position <code>number</code>.
	 *@see            #addElectronContainer(IElectronContainer) 
	 */
	public IElectronContainer getElectronContainer(int number);


	/**
	 * Returns the bond that connectes the two given atoms.
	 *
	 * @param  atom1  The first atom
	 * @param  atom2  The second atom
	 * @return        The bond that connectes the two atoms
	 */
	public IBond getBond(IAtom atom1, IAtom atom2);



	/**
	 *  Returns the number of Atoms in this Container.
	 *
	 *@return    The number of Atoms in this Container
	 */
	public int getAtomCount();

	/**
	 *  Returns the number of Bonds in this Container.
	 *
	 *@return    The number of Bonds in this Container
	 */
	public int getBondCount();

	/**
	 *  Returns the number of LonePairs in this Container.
	 *
	 *@return    The number of LonePairs in this Container
	 */
	public int getLonePairCount();

	/**
	 * Returns the number of the single electrons in this container.
	 *
	 * @return       The number of SingleElectron objects of this AtomContainer
	 */
	public int getSingleElectronCount();
	
	/**
	 * Returns the number of ElectronContainers in this Container.
	 *
	 * @return    The number of ElectronContainers in this Container
	 */
	public int getElectronContainerCount();
	
	/**
	 *  Returns an ArrayList of all atoms connected to the given atom.
	 *
	 *@param  atom  The atom the bond partners are searched of.
	 *@return       The ArrayList with the connected atoms
	 */
	public List<IAtom> getConnectedAtomsList(IAtom atom);

	/**
	 *  Returns an ArrayList of all Bonds connected to the given atom.
	 *
	 *@param  atom  The atom the connected bonds are searched of
	 *@return       The ArrayList with connected atoms
	 */
	public List<IBond> getConnectedBondsList(IAtom atom);

	/**
	 *  Returns the array of lone pairs connected to an atom.
	 *
	 *@param  atom  The atom for which to get lone pairs
	 *@return       The array of LonePairs of this AtomContainer
	 */
	public List<ILonePair> getConnectedLonePairsList(IAtom atom);

	/**
	 *  Returns an array of all SingleElectron connected to the given atom.
	 *
	 *@param  atom  The atom on which the single electron is located
	 *@return       The array of SingleElectron of this AtomContainer
	 */
	public List<ISingleElectron> getConnectedSingleElectronsList(IAtom atom);
	
	/**
	 *  Returns an ArrayList of all electronContainers connected to the given atom.
	 *
	 *@param  atom  The atom the connected electronContainers are searched of
	 *@return       The ArrayList with the  connected atoms
	 */
	public List<IElectronContainer> getConnectedElectronContainersList(IAtom atom);

	
	/**
	 *  Returns the number of atoms connected to the given atom.
	 *
	 *@param  atom  The atom the number of bond partners are searched of.
	 *@return       The the size of connected atoms
	 */
	public int getConnectedAtomsCount(IAtom atom);
	
	/**
	 *  Returns the number of Bonds for a given Atom.
	 *
	 *@param  atom  The atom
	 *@return       The number of Bonds for this atom
	 */
	public int getConnectedBondsCount(IAtom atom);

	/**
	 *  Returns the number of connected atoms (degree) to the given atom.
	 *
	 *@param  atomnumber  The atomnumber the degree is searched for
	 *@return             The number of connected atoms (degree)
	 */
	public int getConnectedBondsCount(int atomnumber);

	/**
	 *  Returns the number of LonePairs for a given Atom.
	 *
	 *@param  atom  The atom
	 *@return       The number of LonePairs for this atom
	 */
	public int getConnectedLonePairsCount(IAtom atom);
	
	/**
	 *  Returns the sum of the SingleElectron for a given Atom.
	 *
	 *@param  atom  The atom on which the single electron is located
	 *@return       The array of SingleElectron of this AtomContainer
	 */
	public int getConnectedSingleElectronsCount(IAtom atom);
	
	/**
	 * Returns the sum of the bond orders for a given Atom.
	 *
	 * @param  atom  The atom
	 * @return       The number of bondorders for this atom
	 */
	public double getBondOrderSum(IAtom atom);

    /**
	 * Returns the maximum bond order that this atom currently has in the context
	 * of this AtomContainer.
	 *
	 * @param  atom  The atom
	 * @return       The maximum bond order that this atom currently has
	 */
	public Order getMaximumBondOrder(IAtom atom);


	/**
	 *  Returns the minimum bond order that this atom currently has in the context
	 *  of this AtomContainer.
	 *
	 *@param  atom  The atom
	 *@return       The minimim bond order that this atom currently has
	 */
	public Order getMinimumBondOrder(IAtom atom);

	/**
	 *  Adds all atoms and electronContainers of a given atomcontainer to this
	 *  container.
	 *
	 *@param  atomContainer  The atomcontainer to be added
	 */
	public void add(IAtomContainer atomContainer);


	/**
	 *  Adds an atom to this container.
	 *
	 *@param  atom  The atom to be added to this container
	 */
	public void addAtom(IAtom atom);


	/**
	 *  Adds a Bond to this AtomContainer.
	 *
	 *@param  bond  The bond to added to this container
	 */
	public void addBond(IBond bond);

	/**
	 *  Adds a lone pair to this AtomContainer.
	 *
	 *@param  lonePair  The LonePair to added to this container
	 */
	public void addLonePair(ILonePair lonePair);
	
	/**
	 *  Adds a single electron to this AtomContainer.
	 *
	 *@param  singleElectron  The SingleElectron to added to this container
	 */
	public void addSingleElectron(ISingleElectron singleElectron);

	/**
	 *  Adds a ElectronContainer to this AtomContainer.
	 *
	 *@param  electronContainer  The ElectronContainer to added to this container
	 */
	public void addElectronContainer(IElectronContainer electronContainer);


	/**
	 *  Removes all atoms and electronContainers of a given atomcontainer from this
	 *  container.
	 *
	 *@param  atomContainer  The atomcontainer to be removed
	 */
	public void remove(IAtomContainer atomContainer);

	/**
	 *  Removes the atom at the given position from the AtomContainer. Note that
	 *  the electronContainers are unaffected: you also have to take care of
	 *  removing all electronContainers to this atom from the container manually.
	 *
	 *@param  position  The position of the atom to be removed.
	 */
	public void removeAtom(int position);

	/**
	 *  Removes the given atom from the AtomContainer. Note that the
	 *  electronContainers are unaffected: you also have to take care of removeing
	 *  all electronContainers to this atom from the container.
	 *
	 *@param  atom  The atom to be removed
	 */
	public void removeAtom(IAtom atom);
	
	/**
	 *  Removes the bond at the given position from the AtomContainer.
	 *
	 *@param  position  The position of the bond to be removed.
	 */
	public IBond removeBond(int position);
	
	/**
	 * Removes the bond that connects the two given atoms.
	 *
	 * @param  atom1  The first atom
	 * @param  atom2  The second atom
	 * @return        The bond that connectes the two atoms
	 */
	public IBond removeBond(IAtom atom1, IAtom atom2);

	/**
	 * Removes the bond from this container.
	 *
	 * @param  bond   The bond to be removed.
	 */
	public void removeBond(IBond bond);
	
	/**
	 * Removes the lone pair at the given position from the AtomContainer.
	 *
	 * @param  position  The position of the LonePair to be removed.
	 * @return           The removed ILonePair.
	 */
	public ILonePair removeLonePair(int position);
	
	/**
	 * Removes the lone pair from the AtomContainer.
	 *
	 * @param  lonePair  The LonePair to be removed.
	 */
	public void removeLonePair(ILonePair lonePair);
	
	/**
	 * Removes the single electron at the given position from the AtomContainer.
	 *
	 * @param  position  The position of the SingleElectron to be removed.
	 * @return           The removed ISingleElectron
	 */
	public ISingleElectron removeSingleElectron(int position);
	
	/**
	 * Removes the single electron from the AtomContainer.
	 *
	 * @param  singleElectron  The SingleElectron to be removed.
	 */
	public void removeSingleElectron(ISingleElectron singleElectron);
	
	/**
	 * Removes the bond at the given position from this container.
	 *
	 * @param  position  The position of the bond in the electronContainers array
	 * @return           the IElectronContainer that was removed
	 */
	public IElectronContainer removeElectronContainer(int position);


	/**
	 * Removes this ElectronContainer from this container.
	 *
	 * @param  electronContainer    The electronContainer to be removed
	 */
	public void removeElectronContainer(IElectronContainer electronContainer);

	
	/**
	 *  Removes the given atom and all connected electronContainers from the
	 *  AtomContainer.
	 *
	 *@param  atom  The atom to be removed
	 */
	public void removeAtomAndConnectedElectronContainers(IAtom atom);

	/**
	 * Removes all atoms and bond from this container.
	 */
	public void removeAllElements();


	/**
	 *  Removes electronContainers from this container.
	 */
	public void removeAllElectronContainers();

    /**
     *  Removes all Bonds from this container.
     */
    public void removeAllBonds();

	/**
	 *  Adds a bond to this container.
	 *
	 *@param  atom1   Id of the first atom of the Bond in [0,..]
	 *@param  atom2   Id of the second atom of the Bond in [0,..]
	 *@param  order   Bondorder
	 *@param  stereo  Stereochemical orientation
	 */
	public void addBond(int atom1, int atom2, IBond.Order order, int stereo);


	/**
	 *  Adds a bond to this container.
	 *
	 *@param  atom1  Id of the first atom of the Bond in [0,..]
	 *@param  atom2  Id of the second atom of the Bond in [0,..]
	 *@param  order  Bondorder
	 */
	public void addBond(int atom1, int atom2, IBond.Order order);


	/**
	 *  Adds a LonePair to this Atom.
	 *
	 *@param  atomID  The atom number to which the LonePair is added in [0,..]
	 */
	public void addLonePair(int atomID);

	/**
	 *  Adds a SingleElectron to this Atom.
	 *
	 *@param  atomID  The atom number to which the SingleElectron is added in [0,..]
	 */
	public void addSingleElectron(int atomID);
	
	/**
	 *  True, if the AtomContainer contains the given atom object.
	 *
	 *@param  atom  the atom this AtomContainer is searched for
	 *@return       True, if the AtomContainer contains the given atom object
	 */
	public boolean contains(IAtom atom);

	/**
	 *  True, if the AtomContainer contains the given bond object.
	 *
	 *@param  bond  the bond this AtomContainer is searched for
	 *@return       True, if the AtomContainer contains the given bond object
	 */
	public boolean contains(IBond bond);
	
	/**
	 *  True, if the AtomContainer contains the given LonePair object.
	 *
	 *@param  lonePair  the LonePair this AtomContainer is searched for
	 *@return           True, if the AtomContainer contains the given LonePair object
	 */
	public boolean contains(ILonePair lonePair);
	
	/**
	 *  True, if the AtomContainer contains the given SingleElectron object.
	 *
	 *@param  singleElectron  the SingleElectron this AtomContainer is searched for
	 *@return                 True, if the AtomContainer contains the given SingleElectron object
	 */
	public boolean contains(ISingleElectron singleElectron);
	
	/**
	 *  True, if the AtomContainer contains the given ElectronContainer object.
	 *
	 *@param  electronContainer ElectronContainer that is searched for
	 *@return                   True, if the AtomContainer contains the given bond object
	 */
	public boolean contains(IElectronContainer electronContainer);

}


