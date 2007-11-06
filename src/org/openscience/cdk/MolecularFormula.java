/*  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2007-09-03 12:53:05 +0200 (Mon, 03 Sep 2007) $
 *  $Revision: 8848 $
 *
 *  Copyright (C) 2005-2007  Miguel Rojas <miguelrojasch@users.sf.net>
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

import org.openscience.cdk.interfaces.*;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Iterator;

/**
 *  Class defining Molecular Formula object. It consists on occurrence of 
 *  different atoms in one molecule of a chemical compound. You have to
 *  take account that the hydrogens must be implicit.<p>
 *
 *  </pre>
 *
 * @cdk.module data
 *
 * @author Miguel Rojas
 * @cdk.created 2007-10-02
 */
public class MolecularFormula extends ChemObject 
  implements IMolecularFormula, IChemObjectListener, Serializable, Cloneable {

	/**
     * Determines if a de-serialized object is compatible with this class.
     *
     * This value must only be changed if and only if the new version
     * of this class is imcompatible with the old version. See Sun docs
     * for <a href=http://java.sun.com/products/jdk/1.1/docs/guide
     * /serialization/spec/version.doc.html>details</a>.
	 */
	private static final long serialVersionUID = -4339235392003286731L;

	/**
	 *  Number of different elements contained by this object.
	 */
	protected int elementCount;
	
	/**
	 *  Number of total of atoms contained by this object.
	 */
	protected int atomCount;

	/**
	 *  Amount by which the elements arrays grow when elements are added and
	 *  the arrays are not large enough for that.
	 */
	protected int growArraySize = 10;

	/**
	 *  Internal array of elements.
	 */
	protected IElement[] elements;

	/**
	 * Internal list of element repetitions.
	 */
	protected Hashtable<String, Integer> elementRepetitions;


    /**
     *  The partial charge of the atom. The default value is 0.0
     */
    protected Double charge = 0.0;
    
	/**
	 *  Constructs an empty MolecularFormula.
	 */
	public MolecularFormula() {
		this.elementCount = 0;
		elements = new IElement[elementCount];
		elementRepetitions = new Hashtable<String, Integer>();
		
	}


	/**
	 * Constructs an AtomContainer with a copy of the atoms and electronContainers
	 * of another AtomContainer (A shallow copy, i.e., with the same objects as in
	 * the original AtomContainer).
	 *
	 * @param  container  An AtomContainer to copy the atoms and electronContainers from
	 */
	public MolecularFormula(IAtomContainer container){
		
		this.atomCount = container.getAtomCount();
		this.elements = new IElement[this.elementCount];
		
		elementRepetitions = new Hashtable<String, Integer>();

		Iterator<IAtom> iterator = container.atoms();
		while(iterator.hasNext()){
			IAtom atom = iterator.next();
			IElement newElement = atom.getBuilder().newElement(atom.getSymbol());
			if (atom.getCharge() != null)
				charge =+ atom.getCharge();
			if (contains(newElement)){
				int repetitions = elementRepetitions.get(newElement.getSymbol());
				elementRepetitions.put(newElement.getSymbol(), repetitions + 1);
			}else{
				growElementArray(newElement);
			}
		}
		
	}
    
	/**
	 *  Adds a molecular formula to this MolecularFormula.
	 *
	 *@param  molecularFormula  The molecular formula to be added
	 */
	public void add(IMolecularFormula molecularFormula){
		
		for (int f = 0; f < molecularFormula.getElementCount(); f++){
			
			if (!contains(molecularFormula.getElement(f))){
				
				addElement(molecularFormula.getElement(f));
			}
		}
		notifyChanged();
	}


	/**
	 *  Adds an element to this MolecularFormula.
	 *
	 * @param  element  The element to be added to this MolecularFormula
	 * @see             #addElement(IElement, int)
	 */
	public void addElement(IElement element){
		
		IElement newElement = element.getBuilder().newElement(element.getSymbol());

		if (contains(newElement)){
			int repetitions = elementRepetitions.get(newElement.getSymbol());
			elementRepetitions.put(newElement.getSymbol(), repetitions + 1);
		}else{
			growElementArray(newElement);
		}

		atomCount++;
	}
	
	/**
	 *  Adds an element to this MolecularFormula indicating the number of occurence of
	 *  this element must be in the MolecularFormula.
	 *
	 * @param  element  The element to be added to this MolecularFormula
	 * @param  occur    The number of occurence of this element
	 */
	public void addElement(IElement element, int occur){
		
		IElement newElement = element.getBuilder().newElement(element.getSymbol());

		if (contains(newElement)){
			int repetitions = elementRepetitions.get(newElement.getSymbol());
			elementRepetitions.put(newElement.getSymbol(), repetitions + occur);
		}else{
			growElementArray(newElement,occur);
		}

		atomCount = atomCount + occur;
	}
	/**
	 *  Get the element at position <code>number</code> in [0,..].
	 *
	 * @param  number  The position of the element to be retrieved.
	 * @return         The elementAt value
     *
	 */
	public IElement getElement(int number){
		
		return elements[number];
		
	}


	/**
	 *  Returns an Iterator for looping over all elements in this molecular formula.
	 *
	 *@return    An Iterator with the element in this molecular formula
	 */
	public Iterator<IElement> elements(){
		
		return new ElementIterator();
	}

	/**
     * The inner ElementIterator class.
     *
     */
    private class ElementIterator implements Iterator<IElement> {

        private int pointer = 0;
    	
        public boolean hasNext() {
            return pointer < elementCount;
        }

        public IElement next() {
            return elements[pointer++];
        }

        public void remove() {
            removeElement(--pointer);
        }
    	
    }
	
	
	/**
	 *  Returns the element at position 0 in the container.
	 *
	 *@return    The atom at position 0 .
	 */
	public IElement getFirstElement(){
		
		return (IElement)elements[0];
	}


	/**
	 *  Returns the element at the last position in the molecularFormula.
	 *
	 *@return    The element at the last position
	 */
	public IElement getLastElement(){
		
		return getElementCount() > 0 ? elements[getElementCount() - 1] : null;
	}


	/**
	 *  Returns the position of a given element in the elements array. It returns -1 if
	 *  the element does not exist.
	 *
	 *@param  element  The element to be sought
	 *@return          The Position of the element in the elements array in [0,..].
	 */
	public int getElementNumber(IElement element){
		
		for (int f = 0; f < elementCount; f++){
			
			if (elements[f].getSymbol() == element.getSymbol()) return f;
		}
		return -1;
	}



	/**
	 *  Returns the number of different elements in this molecular formula.
	 *
	 *@return    The number of different elements in this Container
	 */
	public int getElementCount(){
		
		return this.elementCount;
	}
	
	
	/**
	 *  Returns the number of total elements in this molecular formula.
	 *
	 *@return    The number of total elements in this Container
	 */
	public int getAtomCount(){
		
		return this.atomCount;
	}

	
	/**
	 *  Checks a set of Nodes for the occurence of a particular
	 *  element. It returns -1 if the element does not exist.
	 *
	 *@param     element The IElement
	 *@return    The occurence of this element in this molecular formula
	 */
	public int getAtomCount(IElement element){
		
		if(!contains(element))
			return -1;
		
		return this.elementRepetitions.get(element.getSymbol()).intValue();
	}

	/**
     *  Sets the partial charge of this atom.
     *
     * @param  charge  The partial charge
     *
     * @see    #getCharge
     */
    public void setCharge(Double charge) {
    	this.charge = charge;
    	notifyChanged();
    }

    /**
     *  Returns the partial charge of this atom.
     *
     * If the charge has not been set the return value is Double.NaN
     *
     * @return the charge of this atom
     *
     * @see    #setCharge
     */
    public Double getCharge() {
    	return this.charge;
    }
	/**
	 *  Removes all elements of a given molecular formula from this
	 *  molecular formula.
	 *
	 *@param  molecularFormula  The molecular formula to be removed
	 */
	public void remove(IMolecularFormula molecularFormula)
	{
		for (int f = 0; f < molecularFormula.getElementCount(); f++){
			
			removeElement(molecularFormula.getElement(f));
		}
	}

	/**
	 *  Removes the element at the given position from the MolecularFormula. 
	 *
	 * @param  position  The position of the element to be removed.
	 */
	public void removeElement(int position){
		elements[position].removeListener(this);
		int repetitions = getAtomCount(elements[position]);
		elementRepetitions.remove(elements[position].getSymbol());
		for (int i = position; i < elementCount - 1; i++){
			elements[i] = elements[i + 1];
		}
		elements[elementCount - 1] = null;
		atomCount = atomCount - repetitions;
		elementCount--;
		
		notifyChanged();
	}
	
	/**
	 *  Removes the given element from the MolecularFormula.
	 *
	 *@param  element  The element to be removed
	 */
	public void removeElement(IElement element){
		
		int position = getElementNumber(element);
		if (position != -1){
			removeElement(position);
		}
	}


	/**
	 * Removes all elements of this molecular formula.
	 */
	public void removeAllElements() {
        for (int f = 0; f < getElementCount(); f++) {
			getElement(f).removeListener(this);	
		}
        elements = new IElement[growArraySize];
        elementCount = 0;
        atomCount = 0;
		notifyChanged();
	}


	/**
	 *  True, if the MolecularFormula contains the given element object.
	 *
	 * @param  element  the element this MolecularFormula is searched for
	 * @return          True, if the MolecularFormula contains the given element object
	 */
	public boolean contains(IElement element){
		
		for (int i = 0; i < getElementCount(); i++){
			
			if (element.getSymbol().equals(elements[i].getSymbol())) return true;
		}
		
		return false;
	}
	
	/**
	 *  Returns a one line string representation of this MolecularFormula. This method is
	 *  conform RFC #9.
	 *
	 *@return    The string representation of this MolecularFormula
	 */
	public String toString(){
		
		StringBuffer stringContent = new StringBuffer(64);
		stringContent.append("MolecularFormula(");
		stringContent.append("#S:");
		for (int i = 0; i < getElementCount(); i++){
			
			stringContent.append(getElement(i).getSymbol()).append("-").append(elementRepetitions.get(getElement(i).getSymbol()));
			stringContent.append(", ");
		}
		stringContent.append("#C:").append(getCharge());
		stringContent.append(")");
		
		return stringContent.toString();
	}


	/**
	 * Clones this MolecularFormula object and its content.
	 *
	 * @return    The cloned object
	 * @see       #shallowCopy
	 */
	public Object clone() throws CloneNotSupportedException {
		MolecularFormula clone = (MolecularFormula) super.clone();
        // start from scratch
		clone.removeAllElements();
        // clone all atoms
		for (int f = 0; f < getElementCount(); f++) {
			clone.addElement((IElement) getElement(f).clone(),elementRepetitions.get(getElement(f).getSymbol()));
		}
		return clone;
	}
	/**
	 *  Grows in some size the element array by a given element.
	 *
	 *@see    #growArraySize
	 */
	protected void growElementArray(IElement newElement, int repetitions){
		elementRepetitions.put(newElement.getSymbol(),new Integer(repetitions) );
		if(elementCount + 1 >= elements.length){
			growElementArray();
		}
		elements[elementCount] = newElement;
		newElement.addListener(this);
		notifyChanged();
		elementCount++;
	}
	
	/**
	 *  Grows the element array by a given element.
	 *
	 *@see    #growArraySize
	 */
	protected void growElementArray(IElement newElement){
		growElementArray(newElement, 1);
	}
	/**
	 *  Grows the element array by a given size.
	 *
	 *@see    #growArraySize
	 */
	protected void growElementArray(){
		
		growArraySize = (elements.length < growArraySize) ? growArraySize : elements.length;
		IElement[] newelements = new IElement[elements.length + growArraySize];
		System.arraycopy(elements, 0, newelements, 0, elements.length);
		elements = newelements;
	}
	
	 /**
	 *  Called by objects to which this object has
	 *  registered as a listener.
	 *
	 *@param  event  A change event pointing to the source of the change
	 */
	public void stateChanged(IChemObjectChangeEvent event){
		
		notifyChanged(event);
		
	}   
	
	

}


