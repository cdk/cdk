/* $RCSfile$
 * $Author: egonw $
 * $Date: 2007-01-04 18:46:10 +0100 (Thu, 04 Jan 2007) $
 * $Revision: 7636 $
 *
 *  Copyright (C) 2005-2007  Miguel Rojas <miguelrojasch@users.sf.net>
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

import java.util.Iterator;

import org.openscience.cdk.MolecularFormula;

/**
 * Represents a set of MolecularFormula.
 * 
 * @cdk.module  interfaces
 * @author      rojasm
 * @cdk.created 2007-10-25
 */
public interface IMolecularFormula extends IChemObject {

	/**
	 *  Adds a molecular formula to this MolecularFormula.
	 *
	 *@param  atomContainer  The molecular formula to be added
	 */
	public void add(MolecularFormula molecularFormula);
	
	/**
	 * add an Element
	 *
	 * @param  element  The element to be added to this MolecularFormula
	 * @see             #getElements
	 */
	public void addElement(IElement element);

	/**
	 *  Adds an element to this MolecularFormula indicating the number of occurence of
	 *  this element must be in the MolecularFormula.
	 *
	 * @param  element  The element to be added to this MolecularFormula
	 * @param  occur    The number of occurence of this element
	 */
	public void addElement(IElement element, int occur);
	

	/**
	 *  Get the element at position <code>number</code> in [0,..].
	 *
	 * @param  number  The position of the element to be retrieved.
	 * @return         The elementAt value
     *
	 */
	public IElement getElement(int number);
	
	/**
	 *  Returns an Iterator for looping over all elements in this molecular formula.
	 *
	 *@return    An Iterator with the element in this molecular formula
	 */
	public Iterator<IElement> elements();
	
	/**
	 *  Returns the element at position 0 in the container.
	 *
	 *@return    The atom at position 0 .
	 */
	public IElement getFirstElement();
	
	/**
	 *  Returns the element at the last position in the molecularFormula.
	 *
	 *@return    The element at the last position
	 */
	public IElement getLastElement();
	
	/**
	 *  Returns the position of a given element in the elements array. It returns -1 if
	 *  the element does not exist.
	 *
	 *@param  element  The element to be sought
	 *@return          The Position of the element in the elements array in [0,..].
	 */
	public int getElementNumber(IElement element);
	
	/**
	 *  Returns the number of different elements in this molecular formula.
	 *
	 *@return    The number of different elements in this Container
	 */
	public int getElementCount();
	
	/**
	 *  Returns the number of total elements in this molecular formula.
	 *
	 *@return    The number of total elements in this Container
	 */
	public int getAtomCount();
	
	
	/**
	 *  Checks a set of Nodes for the occurence of a particular
	 *  element. It returns -1 if the element does not exist.
	 *
	 *@param     The IElement
	 *@return    The occurence of this element in this molecular formula
	 */
	public int getAtomCount(IElement element);
	
	/**
     *  Sets the partial charge of this atom.
     *
     * @param  charge  The partial charge
     *
     * @see    #getCharge
     */
    public void setCharge(Double charge) ;
    /**
     *  Returns the partial charge of this atom.
     *
     * If the charge has not been set the return value is Double.NaN
     *
     * @return the charge of this atom
     *
     * @see    #setCharge
     */
    public Double getCharge();
    
    /**
	 *  Removes all elements of a given molecular formula from this
	 *  molecular formula.
	 *
	 *@param  molecularformula  The molecular formula to be removed
	 */
	public void remove(MolecularFormula molecularFormula);
	
	/**
	 *  Removes the element at the given position from the MolecularFormula. 
	 *
	 * @param  position  The position of the element to be removed.
	 */
	public void removeElement(int position);
	/**
	 *  Removes the given element from the MolecularFormula.
	 *
	 *@param  element  The element to be removed
	 */
	public void removeElement(IElement element);
	
	/**
	 * Removes all elements of this molecular formula.
	 */
	public void removeAllElements();
	
	/**
	 *  True, if the MolecularFormula contains the given element object.
	 *
	 * @param  element  the element this MolecularFormula is searched for
	 * @return          True, if the MolecularFormula contains the given element object
	 */
	public boolean contains(IElement element);
	
    
}

