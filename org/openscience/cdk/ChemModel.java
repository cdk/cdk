/* ChemModel.java
 * 
 * $RCSfile$    $Author$    $Date$    $Revision$
 * 
 * Copyright (C) 1997-2000  The CompChem project
 * 
 * Contact: steinbeck@ice.mpg.de, geelter@maul.chem.nd.edu, egonw@sci.kun.nl
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



public class ChemModel extends ChemObject 
{
	protected SetOfMolecules[] setsOfMolecules;
	
	protected int setOfMoleculesCount;
	protected int growArraySize = 5;



	/**
	 *  Constructs an empty SetOfMolecules
	 */
	public ChemModel()   
	{
		setOfMoleculesCount = 0;
		setsOfMolecules = new SetOfMolecules[growArraySize];
	}


	
	/**
	 *  Adds an molecule to this container 
	 *
	 * @param  molecule  The molecule to be added to this container 
	 */
	public void addSetOfMolecules(SetOfMolecules setOfMolecules)
	{
		if (setOfMoleculesCount + 1 >= setsOfMolecules.length)
		{
			growSetOfMoleculesArray();
		}
		setsOfMolecules[setOfMoleculesCount] = setOfMolecules;
		setOfMoleculesCount++;
	}


	
	/**
	 *  Returns the array of SetOfMolecules of this container 
	 *
	 * @return    The array of SetOfMolecules of this container 
	 */
	public SetOfMolecules[] getSetsOfMolecules()
	{
		return setsOfMolecules;
	}
	
	
	/**
	 *  
	 * Returns the SetOfMolecule at position <code>number</code> in the
	 * container
	 *
	 * @param  number  The position of the SetOfMolecule to be returned. 
	 * @return         The SetOfMolecule at position <code>number</code> . 
	 */
	public SetOfMolecules getSetOfMolecules(int number)
	{
		return setsOfMolecules[number];
	}
	

	
	/**
	 *  Grows the molecule array by a given size 
	 *
	 * @see    org.openscience.cdk.AtomContainer#growArraySize growArraySize 
	 */
	protected void growSetOfMoleculesArray()
	{
		growArraySize = setsOfMolecules.length;
		SetOfMolecules[] newsetsOfMolecules = new SetOfMolecules[setsOfMolecules.length + growArraySize];
		System.arraycopy(setsOfMolecules, 0, newsetsOfMolecules, 0, setsOfMolecules.length);
		setsOfMolecules = newsetsOfMolecules;
	}
	

	/**
	 * Returns the number of Molecules in this Container
	 *
	 * @return     
	 */
	public int getSetOfMoleculeCount()
	{
		return this.setOfMoleculesCount;
	}


}
	
