/* ChemFile.java
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



public class ChemFile extends ChemObject 
{
	protected ChemSequence[] chemSequences;
	protected int chemSequenceCount;
	protected int growArraySize = 5;



	/**
	 *  Constructs an empty SetOfChemSequences
	 */
	public ChemFile()   
	{
		chemSequenceCount = 0;
		chemSequences = new ChemSequence[growArraySize];
	}


	
	/**
	 *  Adds an chemSequence to this container 
	 *
	 * @param  chemSequence  The chemSequence to be added to this container 
	 */
	public void addChemSequence(ChemSequence chemSequence)
	{
		if (chemSequenceCount + 1 >= chemSequences.length)
		{
			growChemSequenceArray();
		}
		chemSequences[chemSequenceCount] = chemSequence;
		chemSequenceCount++;
	}
	
	

	/**
	 *  Returns the array of ChemSequences of this container 
	 *
	 * @return    The array of ChemSequences of this container 
	 */
	public ChemSequence[] getChemSequences()
	{
		return chemSequences;
	}


	/**
	 *  
	 * Returns the ChemSequence at position <code>number</code> in the
	 * container
	 *
	 * @param  number  The position of the ChemSequence to be returned. 
	 * @return         The ChemSequence at position <code>number</code> . 
	 */
	public ChemSequence getChemSequence(int number)
	{
		return chemSequences[number];
	}
	
	
	/**
	 *  Grows the chemSequence array by a given size 
	 *
	 * @see    org.openscience.cdk.AtomContainer#growArraySize growArraySize 
	 */
	protected void growChemSequenceArray()
	{
		growArraySize = chemSequences.length;
		ChemSequence[] newchemSequences = new ChemSequence[chemSequences.length + growArraySize];
		System.arraycopy(chemSequences, 0, newchemSequences, 0, chemSequences.length);
		chemSequences = newchemSequences;
	}
	

	/**
	 * Returns the number of ChemSequences in this Container
	 *
	 * @return     
	 */
	public int getChemSequenceCount()
	{
		return this.chemSequenceCount;
	}

	
}
