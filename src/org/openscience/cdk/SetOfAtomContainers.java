/* $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 2003  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
 */

package org.openscience.cdk;


/** 
 * A set of AtomContainers.
 */
public class SetOfAtomContainers extends ChemObject implements java.io.Serializable, Cloneable {

	/**
	 *  Array of AtomContainers.
	 */
	protected AtomContainer[] atomContainers;
	
	/**
	 *  Number of AtomContainers contained by this container.
	 */
	protected int atomContainerCount;

	/**
	 *  Amount by which the AtomContainers array grows when elements are added and
	 *  the array is not large enough for that. 
	 */
	protected int growArraySize = 5;


	/**
	 *  Constructs an empty SetOfAtomContainers.
	 */
	public SetOfAtomContainers()   
	{
		atomContainerCount = 0;
		atomContainers = new AtomContainer[growArraySize];
	}


	
	/**
	 *  Adds an atomContainer to this container.
	 *
	 * @param  atomContainer  The atomContainer to be added to this container 
	 */
	public void addAtomContainer(AtomContainer atomContainer)
	{
		if (atomContainerCount + 1 >= atomContainers.length) {
			growAtomContainerArray();
		}
		atomContainers[atomContainerCount] = atomContainer;
		atomContainerCount++;
	}

	/**
	 *  Adds all atomContainers in the SetOfAtomContainers to this container.
	 *
	 * @param  atomContainerSet  The SetOfAtomContainers 
	 */
	public void add(SetOfAtomContainers atomContainerSet) {
        AtomContainer[] mols = atomContainerSet.getAtomContainers();
        for (int i=0; i< mols.length; i++) {
            addAtomContainer(mols[i]);
        }
    }


	/**
	 *  Returns the array of AtomContainers of this container.
	 *
	 * @return    The array of AtomContainers of this container 
	 */
	public AtomContainer[] getAtomContainers() {
        AtomContainer[] result = new AtomContainer[atomContainerCount];
        System.arraycopy(this.atomContainers, 0, result, 0, result.length);
		return result;
	}
	
	
	/**
	 *  
	 * Returns the AtomContainer at position <code>number</code> in the
	 * container.
	 *
	 * @param  number  The position of the AtomContainer to be returned. 
	 * @return         The AtomContainer at position <code>number</code> . 
	 */
	public AtomContainer  getAtomContainer(int number)
	{
		return atomContainers[number];
	}
	
	
	/**
	 *  Grows the atomContainer array by a given size.
	 *
	 * @see    growArraySize
	 */
	protected void growAtomContainerArray()
	{
		growArraySize = atomContainers.length;
		AtomContainer[] newatomContainers = new AtomContainer[atomContainers.length + growArraySize];
		System.arraycopy(atomContainers, 0, newatomContainers, 0, atomContainers.length);
		atomContainers = newatomContainers;
	}
	

	/**
	 * Returns the number of AtomContainers in this Container.
	 *
	 * @return     The number of AtomContainers in this Container
	 */
	public int getAtomContainerCount()
	{
		return this.atomContainerCount;
	}

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("SetOfAtomContainers(");
        buffer.append(this.hashCode() + ", ");
        buffer.append("M=" + getAtomContainerCount() + ", ");
        AtomContainer[] atomContainers = getAtomContainers();
        for (int i=0; i<atomContainers.length; i++) {
            buffer.append(atomContainers[i].toString());
        }
        buffer.append(")");
        return buffer.toString();
    }
	
}
