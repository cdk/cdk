/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2006-2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.interfaces;

/**
 * A set of AtomContainers.
 *
 * @author     egonw
 * @cdk.module interfaces
 * @cdk.svnrev  $Revision$
 */
public interface IAtomContainerSet extends IChemObject {

	/**
	 * Adds an atomContainer to this container.
	 *
	 * @param  atomContainer  The atomContainer to be added to this container
	 */
	public void addAtomContainer(IAtomContainer atomContainer);

	/**
	 * Removes an AtomContainer from this container.
	 *
	 * @param  atomContainer  The atomContainer to be removed from this container
	 */
	public void removeAtomContainer(IAtomContainer atomContainer);

	/**
	 * Removes all AtomContainer from this container.
	 */
	public void removeAllAtomContainers();	
	
	/**
	 * Removes an AtomContainer from this container.
	 *
	 * @param  pos  The position of the AtomContainer to be removed from this container
	 */
	public void removeAtomContainer(int pos);
	
	/**
	 * Replace the AtomContainer at a specific position (array has to be large enough).
	 * 
	 * @param position   position in array for AtomContainer
	 * @param container  the replacement AtomContainer
	 */
	public void replaceAtomContainer(int position, IAtomContainer container);
	
	/**
	 * Sets the coefficient of a AtomContainer to a given value.
	 *
	 * @param  container   The AtomContainer for which the multiplier is set
	 * @param  multiplier  The new multiplier for the AtomContatiner
	 * @return             true if multiplier has been set
	 * @see                #getMultiplier(IAtomContainer)
	 */
	public boolean setMultiplier(IAtomContainer container, Double multiplier);
	
	/**
	 * Sets the coefficient of a AtomContainer to a given value.
	 *
	 * @param  position    The position of the AtomContainer for which the multiplier is
	 *                    set in [0,..]
	 * @param  multiplier  The new multiplier for the AtomContatiner at
	 *                    <code>position</code>
	 * @see                #getMultiplier(int)
	 */
	public void setMultiplier(int position, Double multiplier);

	/**
	 * Returns an array of double with the stoichiometric coefficients
	 * of the products.
	 *
	 * @return    The multipliers for the AtomContainer's in this set
	 * @see       #setMultipliers
	 */
	public Double[] getMultipliers();

	/**
	 * Sets the multipliers of the AtomContainers.
	 *
	 * @param  newMultipliers  The new multipliers for the AtomContainers in this set
	 * @return                 true if multipliers have been set.
	 * @see                    #getMultipliers
	 */
	public boolean setMultipliers(Double[] newMultipliers);
	
	/**
	 * Adds an atomContainer to this container with the given
	 * multiplier.
	 *
	 * @param  atomContainer  The atomContainer to be added to this container
	 * @param  multiplier     The multiplier of this atomContainer
	 */
	public void addAtomContainer(IAtomContainer atomContainer, double multiplier);

	/**
	 * Adds all atomContainers in the AtomContainerSet to this container.
	 *
	 * @param  atomContainerSet  The AtomContainerSet
	 */
	public void add(IAtomContainerSet atomContainerSet);
	
	/**
	 *  Get an Iterable for this AtomContainerSet.
     * 
     * @return A new Iterable for this AtomContainerSet.
	 */
	public Iterable<IAtomContainer> atomContainers();

	/**
	 * Returns the AtomContainer at position <code>number</code> in the
	 * container.
	 *
	 * @param  number  The position of the AtomContainer to be returned.
	 * @return         The AtomContainer at position <code>number</code> .
	 */
	public IAtomContainer getAtomContainer(int number);

	/**
	 * Returns the multiplier for the AtomContainer at position <code>number</code> in the
	 * container.
	 *
	 * @param  number  The position of the multiplier of the AtomContainer to be returned.
	 * @return         The multiplier for the AtomContainer at position <code>number</code> .
	 * @see            #setMultiplier(int, Double)
	 */
	public Double getMultiplier(int number);

	/**
	 * Returns the multiplier of the given AtomContainer.
	 *
	 * @param  container  The AtomContainer for which the multiplier is given
	 * @return            -1, if the given molecule is not a container in this set
	 * @see               #setMultiplier(IAtomContainer, Double)
	 */
	public Double getMultiplier(IAtomContainer container);

	/**
	 * Returns the number of AtomContainers in this Container.
	 *
	 * @return    The number of AtomContainers in this Container
	 */
	public int getAtomContainerCount();

}

