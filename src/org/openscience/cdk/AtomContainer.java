/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2006  The Chemistry Development Kit (CDK) project
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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomParity;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;
import org.openscience.cdk.interfaces.IElectronContainer;
import org.openscience.cdk.interfaces.ILonePair;
import org.openscience.cdk.interfaces.ISingleElectron;

/**
 *  Base class for all chemical objects that maintain a list of Atoms and
 *  ElectronContainers. <p>
 *
 *  Looping over all Bonds in the AtomContainer is typically done like: <pre>
 *  Bond[] bonds = atomContainer.getBonds();
 *  for (int i = 0; i < bonds.length; i++) {
 *      Bond b = bonds[i];
 *  }
 *  </pre>
 *
 * @cdk.module data
 *
 * @author     steinbeck
 * @cdk.created    2000-10-02
 */
public class AtomContainer extends ChemObject implements java.io.Serializable, IAtomContainer , IChemObjectListener {

	/**
     * Determines if a de-serialized object is compatible with this class.
     *
     * This value must only be changed if and only if the new version
     * of this class is imcompatible with the old version. See Sun docs
     * for <a href=http://java.sun.com/products/jdk/1.1/docs/guide
     * /serialization/spec/version.doc.html>details</a>.
	 */
	private static final long serialVersionUID = 5678100348445919254L;

	/**
	 *  Number of atoms contained by this object.
	 */
	protected int atomCount;

	/**
	 *  Number of electronContainers contained by this object.
	 */
	protected int electronContainerCount;

	/**
	 *  Amount by which the bond and arom arrays grow when elements are added and
	 *  the arrays are not large enough for that.
	 */
	protected int growArraySize = 10;

	/**
	 *  Internal array of atoms.
	 */
	protected IAtom[] atoms;

	/**
	 *  Internal array of bond.
	 */
	protected IElectronContainer[] electronContainers;

	/**
	 * Internal list of atom parities.
	 */
	protected Hashtable atomParities;


	/**
	 *  Constructs an empty AtomContainer.
	 */
	public AtomContainer() {
        this(10, 10);
	}


	/**
	 * Constructs an AtomContainer with a copy of the atoms and electronContainers
	 * of another AtomContainer (A shallow copy, i.e., with the same objects as in
	 * the original AtomContainer).
	 *
	 * @param  container  An AtomContainer to copy the atoms and electronContainers from
	 */
	public AtomContainer(IAtomContainer container)
	{
		this.atomCount = container.getAtomCount();
		this.electronContainerCount = container.getElectronContainerCount();
		atoms = new IAtom[this.atomCount];
		electronContainers = new IElectronContainer[this.electronContainerCount];
		atomParities = new Hashtable((int)(atomCount/2));

		for (int f = 0; f < container.getAtomCount(); f++) {
			atoms[f] = container.getAtomAt(f);
			container.getAtomAt(f).addListener(this);
		}
		for (int f = 0; f < container.getElectronContainerCount(); f++) {
			electronContainers[f] = container.getElectronContainerAt(f);
			container.getElectronContainerAt(f).addListener(this);
		}
	}


	/**
	 *  Constructs an empty AtomContainer that will contain a certain number of
	 *  atoms and electronContainers. It will set the starting array lengths to the
	 *  defined values, but will not create any Atom or ElectronContainer's.
	 *
	 *@param  atomCount               Number of atoms to be in this container
	 *@param  electronContainerCount  Number of electronContainers to be in this
	 *      container
	 */
	public AtomContainer(int atomCount, int electronContainerCount)
	{
		this.atomCount = 0;
		this.electronContainerCount = 0;
		atoms = new IAtom[atomCount];
		electronContainers = new IElectronContainer[electronContainerCount];
        atomParities = new Hashtable((int)(atomCount/2));
	}

    /**
     * Adds an AtomParity to this container. If a parity is already given for the
     * affected Atom, it is overwritten.
     *
     * @param parity The new AtomParity for this container
     * @see   #getAtomParity
     */
    public void addAtomParity(IAtomParity parity) {
        atomParities.put(parity.getAtom(), parity);
    }

    /**
     * Returns the atom parity for the given Atom. If no parity is associated
     * with the given Atom, it returns null.
     *
     * @param  atom   Atom for which the parity must be returned
     * @return The AtomParity for the given Atom, or null if that Atom does
     *         not have an associated AtomParity
     * @see    #addAtomParity
     */
    public IAtomParity getAtomParity(IAtom atom) {
        return (AtomParity)atomParities.get(atom);
    }
    
	/**
	 *  Sets the array of atoms of this AtomContainer.
	 *
	 *@param  atoms  The array of atoms to be assigned to this AtomContainer
	 *@see           #getAtoms
	 */
	public void setAtoms(IAtom[] atoms)
	{
		this.atoms = atoms;
		for (int f = 0; f < atoms.length; f++)
		{
			atoms[f].addListener(this);	
		}
		setAtomCount(atoms.length);
		notifyChanged();

	}


	/**
	 *  Sets the array of electronContainers of this AtomContainer.
	 *
	 *@param  electronContainers  The array of electronContainers to be assigned to
	 *      this AtomContainer
	 *@see  #getElectronContainers
	 */
	public void setElectronContainers(IElectronContainer[] electronContainers)
	{
		this.electronContainers = electronContainers;
		for (int f = 0; f < electronContainers.length; f++)
		{
			electronContainers[f].addListener(this);	
		}
		setElectronContainerCount(electronContainers.length);
		notifyChanged();
	}


	/**
	 *  Set the atom at position <code>number</code> in [0,..].
	 *
	 *@param  number  The position of the atom to be set.
	 *@param  atom    The atom to be stored at position <code>number</code>
	 *@see            #getAtomAt
	 */
	public void setAtomAt(int number, IAtom atom)
	{
		atom.addListener(this);
		atoms[number] = atom;
		notifyChanged();
	}


	/**
	 *  Get the atom at position <code>number</code> in [0,..].
	 *
	 *@param  number  The position of the atom to be retrieved.
	 *@return         The atomAt value
	 *@see            #setAtomAt
	 */
	public IAtom getAtomAt(int number)
	{
		return atoms[number];
	}


	/**
	 *  Get the bond at position <code>number</code> in [0,..].
	 *
	 *@param  number  The position of the bond to be retrieved.
	 *@return         The bondAt value
	 *@see            #setElectronContainerAt
	 */
	public IBond getBondAt(int number)
	{
		return getBonds()[number];
	}



	/**
	 * Sets the ElectronContainer at position <code>number</code> in [0,..].
	 *
	 * @param  number            The position of the ElectronContainer to be set.
	 * @param  electronContainer The ElectronContainer to be stored at position <code>number</code>
	 * @see                      #getElectronContainerAt
	 */
	public void setElectronContainerAt(int number, IElectronContainer electronContainer)
	{
		electronContainer.addListener(this);
		electronContainers[number] = electronContainer;
		notifyChanged();
	}


	/**
	 * Sets the number of electronContainers in this container.
	 *
	 * @param  electronContainerCount  The number of electronContainers in this
	 *                                 container
	 * @see                            #getElectronContainerCount
	 */
	public void setElectronContainerCount(int electronContainerCount)
	{
		this.electronContainerCount = electronContainerCount;
		notifyChanged();
	}


	/**
	 *  Sets the number of atoms in this container.
	 *
	 *@param  atomCount  The number of atoms in this container
	 *@see               #getAtomCount
	 */
	public void setAtomCount(int atomCount)
	{
		this.atomCount = atomCount;
		notifyChanged();
	}


	/**
	 *  Returns the array of atoms of this AtomContainer.
	 *
	 *@return    The array of atoms of this AtomContainer
	 *@see       #setAtoms
	 */
	public IAtom[] getAtoms()
	{
		IAtom[] returnAtoms = new IAtom[getAtomCount()];
		System.arraycopy(this.atoms, 0, returnAtoms, 0, returnAtoms.length);
		return returnAtoms;
	}


	/**
	 *  Returns an AtomEnumeration for looping over all atoms in this container.
	 *
	 *@return    An AtomEnumeration with the atoms in this container
	 *@see       #getAtoms
	 */
	public Enumeration atoms()
	{
		return new AtomEnumeration(this);
	}


	/**
	 *  Returns the array of electronContainers of this AtomContainer.
	 *
	 *@return    The array of electronContainers of this AtomContainer
	 *@see       #setElectronContainers
	 */
	public IElectronContainer[] getElectronContainers()
	{
		IElectronContainer[] returnElectronContainers = new IElectronContainer[getElectronContainerCount()];
		System.arraycopy(this.electronContainers, 0, returnElectronContainers, 0, returnElectronContainers.length);
		return returnElectronContainers;
	}


	/**
	 *  Returns the array of Bonds of this AtomContainer.
	 *
	 *@return    The array of Bonds of this AtomContainer
	 *@see       #getElectronContainers
	 */
	public IBond[] getBonds()
	{
		int bondCount = getBondCount();
		IBond[] result = new IBond[bondCount];
		int bondCounter = 0;
		for (int i = 0; i < getElectronContainerCount(); i++)
		{
			IElectronContainer electronContainer = getElectronContainerAt(i);
			if (electronContainer instanceof IBond)
			{
				result[bondCounter] = (Bond) electronContainer;
				bondCounter++;
			}
		}
		return result;
	}


	/**
	 *  Returns the array of Bonds of this AtomContainer.
	 *
	 *@return    The array of Bonds of this AtomContainer
	 *@see       #getElectronContainers
	 *@see       #getBonds
	 */
	public ILonePair[] getLonePairs()
	{
		int count = getLonePairCount();
		ILonePair[] result = new ILonePair[count];
		int counter = 0;
		for (int i = 0; i < getElectronContainerCount(); i++)
		{
			IElectronContainer electronContainer = getElectronContainerAt(i);
			if (electronContainer instanceof ILonePair)
			{
				result[counter] = (ILonePair) electronContainer;
				counter++;
			}
		}
		return result;
	}


	/**
	 *  Returns the array of Bonds of this AtomContainer.
	 *
	 *@param  atom  Description of the Parameter
	 *@return       The array of Bonds of this AtomContainer
	 *@see          #getElectronContainers
	 *@see          #getBonds
	 */
	public ILonePair[] getLonePairs(IAtom atom)
	{
		List lps = new ArrayList();
		for (int i = 0; i < getElectronContainerCount(); i++)
		{
			IElectronContainer electronContainer = getElectronContainerAt(i);
			if ((electronContainer instanceof ILonePair) && 
			    (((ILonePair) electronContainer).contains(atom)))
			{
				lps.add(electronContainer);
			}
		}
		ILonePair[] result = new ILonePair[lps.size()];
		for (int i=0; i<lps.size(); i++) {
			result[i] = (ILonePair)lps.get(i);
		}
		return result;
	}


	/**
	 *  Returns the atom at position 0 in the container.
	 *
	 *@return    The atom at position 0 .
	 */
	public IAtom getFirstAtom()
	{
		return (Atom)atoms[0];
	}


	/**
	 *  Returns the atom at the last position in the container.
	 *
	 *@return    The atom at the last position
	 */
	public IAtom getLastAtom()
	{
		return (Atom)atoms[getAtomCount() - 1];
	}


	/**
	 *  Returns the position of a given atom in the atoms array. It returns -1 if
	 *  the atom atom does not exist.
	 *
	 *@param  atom  The atom to be sought
	 *@return       The Position of the atom in the atoms array in [0,..].
	 */
	public int getAtomNumber(IAtom atom)
	{
		for (int f = 0; f < getAtomCount(); f++)
		{
			if (getAtomAt(f) == atom)
			{
				return f;
			}
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
	public int getBondNumber(IAtom atom1, IAtom atom2)
	{
		return (getBondNumber(getBond(atom1, atom2)));
	}


	/**
	 *  Returns the position of a given bond in the electronContainers array. It
	 *  returns -1 if the bond does not exist.
	 *
	 *@param  bond  The bond to be sought
	 *@return       The Position of the bond in the electronContainers array in [0,..].
	 */
	public int getBondNumber(IBond bond)
	{
		for (int f = 0; f < getElectronContainerCount(); f++)
		{
			if (getElectronContainerAt(f) == bond)
			{
				return f;
			}
		}
		return -1;
	}


	/**
	 *  Returns the ElectronContainer at position <code>number</code> in the
	 *  container.
	 *
	 *@param  number  The position of the ElectronContainer to be returned.
	 *@return         The ElectronContainer at position <code>number</code>.
	 *@see            #setElectronContainerAt
	 */
	public IElectronContainer getElectronContainerAt(int number)
	{
		return (ElectronContainer)electronContainers[number];
	}


	/**
	 * Returns the bond that connectes the two given atoms.
	 *
	 * @param  atom1  The first atom
	 * @param  atom2  The second atom
	 * @return        The bond that connectes the two atoms
	 */
	public IBond getBond(IAtom atom1, IAtom atom2)
	{
		for (int i = 0; i < getElectronContainerCount(); i++)
		{
			if (electronContainers[i] instanceof IBond &&
					((Bond) electronContainers[i]).contains(atom1))
			{
				if (electronContainers[i] instanceof IBond &&
						((Bond) electronContainers[i]).getConnectedAtom(atom1) == atom2)
				{
					return (Bond) electronContainers[i];
				}
			}
		}
		return null;
	}


	/**
	 *  Returns an array of all atoms connected to the given atom.
	 *
	 *@param  atom  The atom the bond partners are searched of.
	 *@return       The array of <code>Atom</code>s with the size of connected
	 *      atoms
	 */
	public IAtom[] getConnectedAtoms(IAtom atom) {
		List atomList = getConnectedAtomsVector(atom);
		IAtom[] atoms = new IAtom[atomList.size()];
		for (int i=0; i<atomList.size(); i++) {
			atoms[i] = (IAtom)atomList.get(i);
		}
		return atoms;
	}


	/**
	 *  Returns a vector of all atoms connected to the given atom.
	 *
	 *@param  atom  The atom the bond partners are searched of.
	 *@return       The vector with the size of connected atoms
	 */
	public List getConnectedAtomsVector(IAtom atom)
	{
		List atomsVec = new ArrayList();
		IElectronContainer electronContainer;
		for (int i = 0; i < electronContainerCount; i++)
		{
			electronContainer = (IElectronContainer)electronContainers[i];
			if (electronContainer instanceof IBond && ((IBond) electronContainer).contains(atom))
			{
				atomsVec.add(((IBond) electronContainer).getConnectedAtom(atom));
			}
		}
		return atomsVec;
	}


	/**
	 *  Returns an array of all Bonds connected to the given atom.
	 *
	 *@param  atom  The atom the connected bonds are searched of
	 *@return       The array with the size of connected atoms
	 */
	public IBond[] getConnectedBonds(IAtom atom)
	{
		List bondList = getConnectedBondsVector(atom);
		IBond[] bonds = new IBond[bondList.size()];
		for (int i=0; i<bondList.size(); i++) {
			bonds[i] = (IBond)bondList.get(i);
		}
		return bonds;
	}
  
	/**
	 *  Returns a Vector of all Bonds connected to the given atom.
	 *
	 *@param  atom  The atom the connected bonds are searched of
	 *@return       The vector with the size of connected atoms
	 */
  public List getConnectedBondsVector(IAtom atom)
	{
		List bondsVec = new ArrayList();
		for (int i = 0; i < electronContainerCount; i++)
		{
			if (electronContainers[i] instanceof IBond &&
					((IBond) electronContainers[i]).contains(atom))
			{
				bondsVec.add(electronContainers[i]);
			}
		}
    return(bondsVec);
  }


	/**
	 *  Returns an array of all electronContainers connected to the given atom.
	 *
	 *@param  atom  The atom the connected electronContainers are searched of
	 *@return       The array with the size of connected atoms
	 */
	public IElectronContainer[] getConnectedElectronContainers(IAtom atom)
	{
		List bondsVec = new ArrayList();
		for (int i = 0; i < electronContainerCount; i++)
		{
			if (electronContainers[i] instanceof IBond &&
					((IBond) electronContainers[i]).contains(atom)) {
				bondsVec.add(electronContainers[i]);
			} else if (electronContainers[i] instanceof ILonePair &&
                    ((ILonePair) electronContainers[i]).contains((Atom)atom)) {
				bondsVec.add(electronContainers[i]);
			} else if (electronContainers[i] instanceof ISingleElectron &&
					((ISingleElectron) electronContainers[i]).contains((Atom)atom)) {
				bondsVec.add(electronContainers[i]);
			}
		}
		IElectronContainer[] cons = new IElectronContainer[bondsVec.size()];
		for (int i=0; i<bondsVec.size(); i++) {
			cons[i] = (IElectronContainer)bondsVec.get(i);
		}
		return cons;
	}


	/**
	 *  Returns the number of connected atoms (degree) to the given atom.
	 *
	 *@param  atomnumber  The atomnumber the degree is searched for
	 *@return             The number of connected atoms (degree)
	 */
	public int getBondCount(int atomnumber)
	{
		return getBondCount(getAtomAt(atomnumber));
	}


	/**
	 *  Returns the number of Atoms in this Container.
	 *
	 *@return    The number of Atoms in this Container
	 *@see       #setAtomCount
	 */
	public int getAtomCount()
	{
		return this.atomCount;
	}


	/**
	 * Returns the number of ElectronContainers in this Container.
	 *
	 * @return    The number of ElectronContainers in this Container
     * @see       #setElectronContainerCount
	 */
	public int getElectronContainerCount()
	{
		return this.electronContainerCount;
	}


	/**
	 *  Returns the number of LonePairs in this Container.
	 *
	 *@return    The number of LonePairs in this Container
	 */
	public int getLonePairCount()
	{
		int count = 0;
		for (int i = 0; i < electronContainerCount; i++)
		{
			if (electronContainers[i] instanceof ILonePair)
			{
				count++;
			}
		}
		return count;
	}


	/**
	 *  Returns the number of Bonds in this Container.
	 *
	 *@return    The number of Bonds in this Container
	 */
	public int getBondCount()
	{
		int bondCount = 0;
		for (int i = 0; i < electronContainerCount; i++)
		{
			if (electronContainers[i] instanceof IBond)
			{
				bondCount++;
			}
		}
		return bondCount;
	}


	/**
	 *  Returns the number of Bonds for a given Atom.
	 *
	 *@param  atom  The atom
	 *@return       The number of Bonds for this atom
	 */
	public int getBondCount(IAtom atom)
	{
		int count = 0;
		for (int i = 0; i < getElectronContainerCount(); i++)
		{
			if (electronContainers[i] instanceof IBond &&
					((Bond) electronContainers[i]).contains(atom))
			{
				count++;
			}
		}
		return count;
	}


	/**
	 *  Returns the number of LonePairs for a given Atom.
	 *
	 *@param  atom  The atom
	 *@return       The number of LonePairs for this atom
	 */
	public int getLonePairCount(IAtom atom)
	{
		int count = 0;
		for (int i = 0; i < getElectronContainerCount(); i++)
		{
			if (electronContainers[i] instanceof ILonePair &&
					((ILonePair) electronContainers[i]).contains(atom))
			{
				count++;
			}
		}
		return count;
	}
	/**
	 *  Returns an array of all SingleElectron connected to the given atom.
	 *
	 *@param  atom  The atom on which the single electron is located
	 *@return       The array of SingleElectron of this AtomContainer
	 */
	public ISingleElectron[] getSingleElectron(IAtom atom)
	{
		List lps = new ArrayList();
		for (int i = 0; i < getElectronContainerCount(); i++)
		{
			if ((electronContainers[i] instanceof ISingleElectron) && 
				(((ISingleElectron) electronContainers[i]).contains((Atom)atom)))
			{
				lps.add(electronContainers[i]);
			}
		}
		ISingleElectron[] result = new ISingleElectron[lps.size()];
		for (int i=0; i<lps.size(); i++) {
			result[i] = (ISingleElectron)lps.get(i);
		}
		return result;
	}
	/**
	 *  Returns the sum of the SingleElectron for a given Atom.
	 *
	 *@param  atom  The atom on which the single electron is located
	 *@return       The array of SingleElectron of this AtomContainer
	 */
	public int getSingleElectronSum(IAtom atom)
	{
		int count = 0;
		for (int i = 0; i <  getElectronContainerCount(); i++)
		{if ((electronContainers[i] instanceof ISingleElectron) && 
			 (((ISingleElectron) electronContainers[i]).contains((Atom)atom)))
			{
				count++;
			}
		}
		return count;
	}
	/**
	 * Returns the sum of the bond orders for a given Atom.
	 *
	 * @param  atom  The atom
	 * @return       The number of bondorders for this atom
	 */
	public double getBondOrderSum(IAtom atom)
	{
		double count = 0;
		for (int i = 0; i < getElectronContainerCount(); i++)
		{
			if (electronContainers[i] instanceof IBond &&
					((Bond) electronContainers[i]).contains(atom))
			{
				count += ((Bond) electronContainers[i]).getOrder();
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
	public double getMaximumBondOrder(IAtom atom) {
		double max = 0.0;
		for (int i = 0; i < getElectronContainerCount(); i++)
		{
			if (electronContainers[i] instanceof IBond &&
					((Bond) electronContainers[i]).contains(atom) &&
					((Bond) electronContainers[i]).getOrder() > max)
			{
				max = ((Bond) electronContainers[i]).getOrder();
			}
		}
		return max;
	}


	/**
	 *  Returns the minimum bond order that this atom currently has in the context
	 *  of this AtomContainer.
	 *
	 *@param  atom  The atom
	 *@return       The minimim bond order that this atom currently has
	 */
	public double getMinimumBondOrder(IAtom atom)
	{
		double min = 6;
		for (int i = 0; i < getElectronContainerCount(); i++)
		{
			if (electronContainers[i] instanceof IBond &&
					((Bond) electronContainers[i]).contains(atom) &&
					((Bond) electronContainers[i]).getOrder() < min)
			{
				min = ((Bond) electronContainers[i]).getOrder();
			}
		}
		return min;
	}



	/**
	 * Compares this AtomContainer with another given AtomContainer and returns
	 * the Intersection between them. <p>
	 * 
	 * <b>Important Note</b> : This is not the maximum common substructure.
	 *
	 * @param  container  an AtomContainer object
	 * @return            An AtomContainer containing the Intersection between this
	 *                    AtomContainer and another given one
	 */

	public IAtomContainer getIntersection(IAtomContainer container)
	{
		IAtomContainer intersection = getBuilder().newAtomContainer();

		for (int i = 0; i < getAtomCount(); i++)
		{
			if (container.contains(getAtomAt(i)))
			{
				intersection.addAtom(getAtomAt(i));
			}
		}
		for (int i = 0; i < getElectronContainerCount(); i++)
		{
			if (container.contains(getElectronContainerAt(i)))
			{
				intersection.addElectronContainer(getElectronContainerAt(i));
			}
		}
		return intersection;
	}

	/**
	 *  Adds the <code>ElectronContainer</code>s found in atomContainer to this
	 *  container.
	 *
	 *@param  atomContainer  AtomContainer with the new ElectronContainers
	 */
	public void addElectronContainers(IAtomContainer atomContainer)
	{
		for (int f = 0; f < atomContainer.getElectronContainerCount(); f++)
		{
			if (!contains(atomContainer.getElectronContainerAt(f)))
			{
				addElectronContainer(atomContainer.getElectronContainerAt(f));
			}
		}
		notifyChanged();
	}


	/**
	 *  Adds all atoms and electronContainers of a given atomcontainer to this
	 *  container.
	 *
	 *@param  atomContainer  The atomcontainer to be added
	 */
	public void add(IAtomContainer atomContainer)
	{
		for (int f = 0; f < atomContainer.getAtomCount(); f++)
		{
			if (!contains(atomContainer.getAtomAt(f)))
			{
				addAtom(atomContainer.getAtomAt(f));
			}
		}
		for (int f = 0; f < atomContainer.getElectronContainerCount(); f++)
		{
			if (!contains(atomContainer.getElectronContainerAt(f)))
			{
				addElectronContainer(atomContainer.getElectronContainerAt(f));
			}
		}
		notifyChanged();
	}


	/**
	 *  Adds an atom to this container.
	 *
	 *@param  atom  The atom to be added to this container
	 */
	public void addAtom(IAtom atom)
	{
		if (contains(atom))
		{
			return;
		}

		if (atomCount + 1 >= atoms.length)
		{
			growAtomArray();
		}
		atom.addListener(this);
		atoms[atomCount] = atom;
		atomCount++;
		notifyChanged();
	}


	/**
	 *  Wrapper method for adding Bonds to this AtomContainer.
	 *
	 *@param  bond  The bond to added to this container
	 */
	public void addBond(IBond bond)
	{
		addElectronContainer(bond);
		notifyChanged();
	}


	/**
	 *  Adds a ElectronContainer to this AtomContainer.
	 *
	 *@param  electronContainer  The ElectronContainer to added to this container
	 */
	public void addElectronContainer(IElectronContainer electronContainer)
	{
		if (electronContainerCount + 1 >= electronContainers.length)
		{
			growElectronContainerArray();
		}
		// are we supposed to check if the atoms forming this bond are
		// already in here and add them if neccessary? No, core classes
		// must not check parameter input.
		electronContainer.addListener(this);
		electronContainers[electronContainerCount] = electronContainer;
		electronContainerCount++;
		notifyChanged();
	}


	/**
	 *  Removes all atoms and electronContainers of a given atomcontainer from this
	 *  container.
	 *
	 *@param  atomContainer  The atomcontainer to be removed
	 */
	public void remove(IAtomContainer atomContainer)
	{
		for (int f = 0; f < atomContainer.getAtomCount(); f++)
		{
			removeAtom(atomContainer.getAtomAt(f));
		}
		for (int f = 0; f < atomContainer.getElectronContainerCount(); f++)
		{
			removeElectronContainer(atomContainer.getElectronContainerAt(f));
		}
		notifyChanged();
	}


	/**
	 * Removes the bond at the given position from this container.
	 *
	 * @param  position  The position of the bond in the electronContainers array
	 * @return           Bond that was removed
	 */
	public IElectronContainer removeElectronContainer(int position)
	{
		IElectronContainer electronContainer = getElectronContainerAt(position);
		electronContainer.removeListener(this);
		for (int i = position; i < electronContainerCount - 1; i++)
		{
			electronContainers[i] = electronContainers[i + 1];
		}
		electronContainers[electronContainerCount - 1] = null;
		electronContainerCount--;
		notifyChanged();
		return electronContainer;
	}


	/**
	 * Removes this ElectronContainer from this container.
	 *
	 * @param  electronContainer    The electronContainer to be removed
	 * @return                      Bond that was removed
	 */
	public IElectronContainer removeElectronContainer(IElectronContainer electronContainer)
	{
		for (int i = getElectronContainerCount() - 1; i >= 0; i--)
		{
			if (electronContainers[i].equals(electronContainer))
			{
				/* we don't notifyChanged here because the
				   method called below does is already  */ 
				return removeElectronContainer(i);
			}
		}
		return null;
	}


	/**
	 * Removes the bond that connects the two given atoms.
	 *
	 * @param  atom1  The first atom
	 * @param  atom2  The second atom
	 * @return        The bond that connectes the two atoms
	 */
	public IBond removeBond(IAtom atom1, IAtom atom2)
	{
		for (int i = 0; i < getElectronContainerCount(); i++)
		{
			if (electronContainers[i] instanceof IBond &&
					((Bond) electronContainers[i]).contains(atom1))
			{
				if (((Bond) electronContainers[i]).getConnectedAtom(atom1) == atom2)
				{
					/* We don't call notify changed here because
					   the method called below does it */
					return (Bond) removeElectronContainer(electronContainers[i]);
				}
			}
		}
		return null;
	}



	/**
	 *  Removes the atom at the given position from the AtomContainer. Note that
	 *  the electronContainers are unaffected: you also have to take care of
	 *  removing all electronContainers to this atom from the container manually.
	 *
	 *@param  position  The position of the atom to be removed.
	 */
	public void removeAtom(int position)
	{
		atoms[position].removeListener(this);
		for (int i = position; i < atomCount - 1; i++)
		{
			atoms[i] = atoms[i + 1];
		}
		atoms[atomCount - 1] = null;
		atomCount--;
		notifyChanged();
	}


	/**
	 *  Removes the given atom and all connected electronContainers from the
	 *  AtomContainer.
	 *
	 *@param  atom  The atom to be removed
	 */
	public void removeAtomAndConnectedElectronContainers(IAtom atom)
	{
		int position = getAtomNumber(atom);
		if (position != -1)
		{
			IElectronContainer[] electronContainers = getConnectedElectronContainers(atom);
           for (int f = 0; f < electronContainers.length; f++)
			{
				removeElectronContainer(electronContainers[f]);
			}
			removeAtom(position);
		}
		notifyChanged();
	}


	/**
	 *  Removes the given atom from the AtomContainer. Note that the
	 *  electronContainers are unaffected: you also have to take care of removeing
	 *  all electronContainers to this atom from the container.
	 *
	 *@param  atom  The atom to be removed
	 */
	public void removeAtom(IAtom atom)
	{
		int position = getAtomNumber(atom);
		if (position != -1)
		{
			removeAtom(position);
		}
		notifyChanged();
	}


	/**
	 * Removes all atoms and bond from this container.
	 */
	public void removeAllElements() {
        for (int f = 0; f < getAtomCount(); f++) {
			getAtomAt(f).removeListener(this);	
		}
		for (int f = 0; f < getElectronContainerCount(); f++) {
			getElectronContainerAt(f).removeListener(this);	
		}
		atoms = new IAtom[growArraySize];
		electronContainers = new IElectronContainer[growArraySize];
		atomCount = 0;
		electronContainerCount = 0;
		notifyChanged();
	}


	/**
	 *  Removes electronContainers from this container.
	 */
	public void removeAllElectronContainers()
	{
		for (int f = 0; f < getElectronContainerCount(); f++) {
			getElectronContainerAt(f).removeListener(this);	
		}
		electronContainers = new IElectronContainer[growArraySize];
		electronContainerCount = 0;
		notifyChanged();
	}

    /**
     *  Removes all Bonds from this container.
     */
    public void removeAllBonds() {
    	IBond[] bonds = getBonds();
        for (int i=0; i<bonds.length; i++) {
            removeElectronContainer(bonds[i]);
        }
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
	public void addBond(int atom1, int atom2, double order, int stereo)
	{
		IBond bond = getBuilder().newBond(getAtomAt(atom1), getAtomAt(atom2), order, stereo);

		if (contains(bond))
		{
			return;
		}

		if (electronContainerCount >= electronContainers.length)
		{
			growElectronContainerArray();
		}
		addBond(bond);
		/* no notifyChanged() here because addBond(bond) does 
		   it already */
	}


	/**
	 *  Adds a bond to this container.
	 *
	 *@param  atom1  Id of the first atom of the Bond in [0,..]
	 *@param  atom2  Id of the second atom of the Bond in [0,..]
	 *@param  order  Bondorder
	 */
	public void addBond(int atom1, int atom2, double order)
	{
		IBond bond = getBuilder().newBond(getAtomAt(atom1), getAtomAt(atom2), order);

		if (electronContainerCount >= electronContainers.length)
		{
			growElectronContainerArray();
		}
		addBond(bond);
		/* no notifyChanged() here because addBond(bond) does 
		   it already */
	}


	/**
	 *  Adds a LonePair to this Atom.
	 *
	 *@param  atomID  The atom number to which the LonePair is added in [0,..]
	 */
	public void addLonePair(int atomID)
	{
		IElectronContainer lonePair = getBuilder().newLonePair((Atom)atoms[atomID]);
		lonePair.addListener(this);
		addElectronContainer(lonePair);
		/* no notifyChanged() here because addElectronContainer() does 
		   it already */
	}


	/**
	 *  True, if the AtomContainer contains the given ElectronContainer object.
	 *
	 *@param  electronContainer ElectronContainer that is searched for
	 *@return                   True, if the AtomContainer contains the given bond object
	 */
	public boolean contains(IElectronContainer electronContainer)
	{
		for (int i = 0; i < getElectronContainerCount(); i++)
		{
			if (electronContainer == electronContainers[i])
			{
				return true;
			}
		}
		return false;
	}


	/**
	 *  True, if the AtomContainer contains the given atom object.
	 *
	 *@param  atom  the atom this AtomContainer is searched for
	 *@return       True, if the AtomContainer contains the given atom object
	 */
	public boolean contains(IAtom atom)
	{
		for (int i = 0; i < getAtomCount(); i++)
		{
			if (atom == atoms[i])
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 *  Returns a one line string representation of this Container. This method is
	 *  conform RFC #9.
	 *
	 *@return    The string representation of this Container
	 */
	public String toString()
	{
		IElectronContainer electronContainer;
		StringBuffer stringContent = new StringBuffer();
		stringContent.append("AtomContainer(");
		stringContent.append(this.hashCode()).append(", ");
		stringContent.append("#A:").append(getAtomCount()).append(", ");
		stringContent.append("#EC:").append(getElectronContainerCount()).append(", ");
		for (int i = 0; i < getAtomCount(); i++)
		{
			stringContent.append(getAtomAt(i).toString()).append(", ");
		}
		for (int i = 0; i < getElectronContainerCount(); i++)
		{
			electronContainer = getElectronContainerAt(i);
			// this check should be removed!
			if (electronContainer != null)
			{
				stringContent.append(electronContainer.toString()).append(", ");
			}
		}
        stringContent.append(", AP:[#").append(atomParities.size()).append(", ");
        Enumeration parities = atomParities.elements();
        while (parities.hasMoreElements()) {
			stringContent.append(((AtomParity)parities.nextElement()).toString());
            if (parities.hasMoreElements()) stringContent.append(", ");
		}
		stringContent.append("])");
		return stringContent.toString();
	}


	/**
	 * Clones this AtomContainer object and its content.
	 *
	 * @return    The cloned object
	 * @see       #shallowCopy
	 */
	public Object clone() throws CloneNotSupportedException {
		IElectronContainer electronContainer = null;
		IElectronContainer newEC = null;
		IAtom[] natoms;
		IAtom[] newAtoms;
		IAtomContainer clone = (IAtomContainer) super.clone();
        // start from scratch
		clone.removeAllElements();
        // clone all atoms
		for (int f = 0; f < getAtomCount(); f++) {
			clone.addAtom((Atom) getAtomAt(f).clone());
		}
        // clone the electronContainer
		for (int f = 0; f < getElectronContainerCount(); f++) {
			electronContainer = this.getElectronContainerAt(f);
			newEC = getBuilder().newElectronContainer();
			if (electronContainer instanceof IBond) {
				IBond bond = (IBond) electronContainer;
				newEC = (IElectronContainer)bond.clone();
				natoms = bond.getAtoms();
				newAtoms = new IAtom[natoms.length];
				for (int g = 0; g < bond.getAtomCount(); g++) {
					newAtoms[g] = clone.getAtomAt(getAtomNumber(natoms[g]));
				}
				((IBond) newEC).setAtoms(newAtoms);
			} else if (electronContainer instanceof ILonePair) {
				IAtom atom = ((ILonePair) electronContainer).getAtom();
				newEC = (ILonePair)electronContainer.clone();
				((ILonePair) newEC).setAtom(clone.getAtomAt(getAtomNumber(atom)));
            } else if (electronContainer instanceof ISingleElectron) {
            	IAtom atom = ((ISingleElectron) electronContainer).getAtom();
                newEC = (ISingleElectron)electronContainer.clone();
                ((ISingleElectron) newEC).setAtom(clone.getAtomAt(getAtomNumber(atom)));
			} else {
				//System.out.println("Expecting EC, got: " + electronContainer.getClass().getName());
				newEC = (IElectronContainer) electronContainer.clone();
			}
			clone.addElectronContainer(newEC);
		}
		return clone;
	}

	/**
	 *  Grows the ElectronContainer array by a given size.
	 *
	 *@see    #growArraySize
	 */
	protected void growElectronContainerArray()
	{
		growArraySize = (electronContainers.length < growArraySize) ? growArraySize : electronContainers.length;
		IElectronContainer[] newelectronContainers = new IElectronContainer[electronContainers.length + growArraySize];
		System.arraycopy(electronContainers, 0, newelectronContainers, 0, electronContainers.length);
		electronContainers = newelectronContainers;
	}


	/**
	 *  Grows the atom array by a given size.
	 *
	 *@see    #growArraySize
	 */
	protected void growAtomArray()
	{
		growArraySize = (atoms.length < growArraySize) ? growArraySize : atoms.length;
		IAtom[] newatoms = new IAtom[atoms.length + growArraySize];
		System.arraycopy(atoms, 0, newatoms, 0, atoms.length);
		atoms = newatoms;
	}
	
	 /**
	 *  Called by objects to which this object has
	 *  registered as a listener.
	 *
	 *@param  event  A change event pointing to the source of the change
	 */
	public void stateChanged(IChemObjectChangeEvent event)
	{
		notifyChanged(event);
	}   

}

