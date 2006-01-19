/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2003-2005  The Chemistry Development Kit (CDK) project
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

import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;

/**
 * A set of AtomContainers.
 *
 * @author        hel
 * @cdk.module    data
 */
public class SetOfAtomContainers extends ChemObject implements java.io.Serializable, org.openscience.cdk.interfaces.ISetOfAtomContainers, IChemObjectListener {

	/**
     * Determines if a de-serialized object is compatible with this class.
     *
     * This value must only be changed if and only if the new version
     * of this class is imcompatible with the old version. See Sun docs
     * for <a href=http://java.sun.com/products/jdk/1.1/docs/guide
     * /serialization/spec/version.doc.html>details</a>.
	 */
	private static final long serialVersionUID = -521290297592768395L;

	/**  Array of AtomContainers. */
	protected org.openscience.cdk.interfaces.IAtomContainer[] atomContainers;

	/**  Number of AtomContainers contained by this container. */
	protected int atomContainerCount;

	/**
	 * Defines the number of instances of a certain molecule
	 * in the set. It is 1 by default.
	 */
	protected double[] multipliers;

	/**
	 *  Amount by which the AtomContainers array grows when elements are added and
	 *  the array is not large enough for that.
	 */
	protected int growArraySize = 5;


	/**  Constructs an empty SetOfAtomContainers. */
	public SetOfAtomContainers() {
		atomContainerCount = 0;
		atomContainers = new AtomContainer[growArraySize];
		multipliers = new double[growArraySize];
	}

	/**
	 * Adds an atomContainer to this container.
	 *
	 * @param  atomContainer  The atomContainer to be added to this container
	 */
	public void addAtomContainer(org.openscience.cdk.interfaces.IAtomContainer atomContainer) {
		atomContainer.addListener(this);
		addAtomContainer(atomContainer, 1.0);
		/*
		 *  notifyChanged is called below
		 */
	}

	/**
	 * Removes an AtomContainer from this container.
	 *
	 * @param  atomContainer  The atomContainer to be removed from this container
	 */
	public void removeAtomContainer(org.openscience.cdk.interfaces.IAtomContainer atomContainer) {
		for (int i = 0; i < atomContainerCount; i++) {
			if (atomContainers[i] == atomContainer)
				removeAtomContainer(i);
		}
	}

	/**
	 * Removes all AtomContainer from this container.
	 */
	public void removeAllAtomContainers() {
		for (int pos = atomContainerCount - 1; pos >= 0; pos--)
		{
			atomContainers[pos].removeListener(this);
			multipliers[pos] = 0;
			atomContainers[pos] = null;
		}
		atomContainerCount = 0;
		notifyChanged();
	}
	
	
	/**
	 * Removes an AtomContainer from this container.
	 *
	 * @param  pos  The position of the AtomContainer to be removed from this container
	 */
	public void removeAtomContainer(int pos) {
		atomContainers[pos].removeListener(this);
		for (int i = pos; i < atomContainerCount - 1; i++) {
			atomContainers[i] = atomContainers[i + 1];
			multipliers[i] = multipliers[i + 1];
		}
		atomContainers[atomContainerCount - 1] = null;
		atomContainerCount--;
		notifyChanged();
	}

	/**
	 * Sets the coefficient of a AtomContainer to a given value.
	 *
	 * @param  container   The AtomContainer for which the multiplier is set
	 * @param  multiplier  The new multiplier for the AtomContatiner
	 * @return             true if multiplier has been set
	 * @see                #getMultiplier(AtomContainer)
	 */
	public boolean setMultiplier(org.openscience.cdk.interfaces.IAtomContainer container, double multiplier) {
		for (int i = 0; i < atomContainers.length; i++) {
			if (atomContainers[i] == container) {
				multipliers[i] = multiplier;
				notifyChanged();
				return true;
			}
		}
		return false;
	}

	/**
	 * Sets the coefficient of a AtomContainer to a given value.
	 *
	 * @param  position    The position of the AtomContainer for which the multiplier is
	 *                    set in [0,..]
	 * @param  multiplier  The new multiplier for the AtomContatiner at
	 *                    <code>position</code>
	 * @see                #getMultiplier(int)
	 */
	public void setMultiplier(int position, double multiplier) {
		multipliers[position] = multiplier;
		notifyChanged();
	}

	/**
	 * Returns an array of double with the stoichiometric coefficients
	 * of the products.
	 *
	 * @return    The multipliers for the AtomContainer's in this set
	 * @see       #setMultipliers
	 */
	public double[] getMultipliers() {
		double[] returnArray = new double[this.atomContainerCount];
		System.arraycopy(this.multipliers, 0, returnArray, 0, this.atomContainerCount);
		return returnArray;
	}

	/**
	 * Sets the multipliers of the AtomContainers.
	 *
	 * @param  newMultipliers  The new multipliers for the AtomContainers in this set
	 * @return                 true if multipliers have been set.
	 * @see                    #getMultipliers
	 */
	public boolean setMultipliers(double[] newMultipliers) {
		if (newMultipliers.length == atomContainerCount) {
			System.arraycopy(newMultipliers, 0, multipliers, 0, atomContainerCount);
			notifyChanged();
			return true;
		}

		return false;
	}

	/**
	 * Adds an atomContainer to this container with the given
	 * multiplier.
	 *
	 * @param  atomContainer  The atomContainer to be added to this container
	 * @param  multiplier     The multiplier of this atomContainer
	 */
	public void addAtomContainer(org.openscience.cdk.interfaces.IAtomContainer atomContainer, double multiplier) {
		if (atomContainerCount + 1 >= atomContainers.length) {
			growAtomContainerArray();
		}
		atomContainer.addListener(this);
		atomContainers[atomContainerCount] = atomContainer;
		multipliers[atomContainerCount] = multiplier;
		atomContainerCount++;
		notifyChanged();
	}

	/**
	 *  Adds all atomContainers in the SetOfAtomContainers to this container.
	 *
	 * @param  atomContainerSet  The SetOfAtomContainers
	 */
	public void add(org.openscience.cdk.interfaces.ISetOfAtomContainers atomContainerSet) {
		org.openscience.cdk.interfaces.IAtomContainer[] mols = atomContainerSet.getAtomContainers();
		for (int i = 0; i < mols.length; i++) {
			addAtomContainer(mols[i]);
		}
		/*
		 *  notifyChanged() is called by addAtomContainer()
		 */
	}

	/**
	 *  Returns the array of AtomContainers of this container.
	 *
	 * @return    The array of AtomContainers of this container
	 */
	public org.openscience.cdk.interfaces.IAtomContainer[] getAtomContainers() {
		org.openscience.cdk.interfaces.IAtomContainer[] result = new AtomContainer[atomContainerCount];
		System.arraycopy(this.atomContainers, 0, result, 0, result.length);
		return result;
	}


	/**
	 * Returns the AtomContainer at position <code>number</code> in the
	 * container.
	 *
	 * @param  number  The position of the AtomContainer to be returned.
	 * @return         The AtomContainer at position <code>number</code> .
	 */
	public org.openscience.cdk.interfaces.IAtomContainer getAtomContainer(int number) {
		return (AtomContainer)atomContainers[number];
	}

	/**
	 * Returns the multiplier for the AtomContainer at position <code>number</code> in the
	 * container.
	 *
	 * @param  number  The position of the multiplier of the AtomContainer to be returned.
	 * @return         The multiplier for the AtomContainer at position <code>number</code> .
	 * @see            #setMultiplier(int, double)
	 */
	public double getMultiplier(int number) {
		return multipliers[number];
	}

	/**
	 * Returns the multiplier of the given AtomContainer.
	 *
	 * @param  container  The AtomContainer for which the multiplier is given
	 * @return            -1, if the given molecule is not a container in this set
	 * @see               #setMultiplier(AtomContainer, double)
	 */
	public double getMultiplier(org.openscience.cdk.interfaces.IAtomContainer container) {
		for (int i = 0; i < atomContainerCount; i++) {
			if (atomContainers[i].equals(container)) {
				return multipliers[i];
			}
		}
		return -1.0;
	}

	/**
	 *  Grows the atomContainer array by a given size.
	 *
	 * @see    growArraySize
	 */
	protected void growAtomContainerArray() {
		growArraySize = atomContainers.length;
		AtomContainer[] newatomContainers = new AtomContainer[atomContainers.length + growArraySize];
		System.arraycopy(atomContainers, 0, newatomContainers, 0, atomContainers.length);
		atomContainers = newatomContainers;
		double[] newMultipliers = new double[multipliers.length + growArraySize];
		System.arraycopy(multipliers, 0, newMultipliers, 0, multipliers.length);
		multipliers = newMultipliers;
	}


	/**
	 * Returns the number of AtomContainers in this Container.
	 *
	 * @return    The number of AtomContainers in this Container
	 */
	public int getAtomContainerCount() {
		return this.atomContainerCount;
	}

	/**
	 * Returns the String representation of this SetOfAtomContainers.
	 *
	 * @return    The String representation of this SetOfAtomContainers
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("SetOfAtomContainers(");
		buffer.append(this.hashCode()).append(", ");
		buffer.append("M=").append(getAtomContainerCount()).append(", ");
		org.openscience.cdk.interfaces.IAtomContainer[] atomContainers = getAtomContainers();
		for (int i = 0; i < atomContainers.length; i++) {
			buffer.append(atomContainers[i].toString());
			if (i < atomContainers.length - 1) {
				buffer.append(", ");
			}
		}
		buffer.append(")");
		return buffer.toString();
	}


	/**
	 *  Clones this SetOfAtomContainers and its content
	 *
	 * @return    the cloned Object
	 */
	public Object clone() {
		SetOfAtomContainers clone = new SetOfAtomContainers();
		org.openscience.cdk.interfaces.IAtomContainer[] result = getAtomContainers();
		for (int i = 0; i < result.length; i++) {
				clone.addAtomContainer((AtomContainer)((AtomContainer)result[i]).clone(), 1.0);
		}
		return (Object) clone;
	}

	/**
	 *  Called by objects to which this object has
	 *  registered as a listener.
	 *
	 * @param  event  A change event pointing to the source of the change
	 */
	public void stateChanged(IChemObjectChangeEvent event) {
		notifyChanged(event);
	}

}

