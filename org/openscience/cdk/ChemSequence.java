/* ChemSequence.java
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




public class ChemSequence extends ChemObject 
{
	protected ChemModel[] chemModels;
	
	protected int chemModelCount;
	protected int growArraySize = 5;



	/**
	 *  Constructs an empty ChemSequence
	 */
	public ChemSequence()   
	{
		chemModelCount = 0;
		chemModels = new ChemModel[growArraySize];
	}


	
	/**
	 *  Adds an chemModel to this container 
	 *
	 * @param  chemModel  The chemModel to be added to this container 
	 */
	public void addChemModel(ChemModel chemModel)
	{
		if (chemModelCount + 1 >= chemModels.length)
		{
			growChemModelArray();
		}
		chemModels[chemModelCount] = chemModel;
		chemModelCount++;
	}

	
	/**
	 *  Returns the array of ChemModels of this container 
	 *
	 * @return    The array of ChemModels of this container 
	 */
	public ChemModel[] getChemModels()
	{
		return chemModels;
	}
	
	
	/**
	 *  
	 * Returns the ChemModel at position <code>number</code> in the
	 * container
	 *
	 * @param  number  The position of the ChemModel to be returned. 
	 * @return         The ChemModel at position <code>number</code> . 
	 */
	public ChemModel getChemModel(int number)
	{
		return chemModels[number];
	}
	
	/**
	 *  Grows the chemModel array by a given size 
	 *
	 * @see    org.openscience.cdk.AtomContainer#growArraySize growArraySize 
	 */
	protected void growChemModelArray()
	{
		growArraySize = chemModels.length;
		ChemModel[] newchemModels = new ChemModel[chemModels.length + growArraySize];
		System.arraycopy(chemModels, 0, newchemModels, 0, chemModels.length);
		chemModels = newchemModels;
	}
	

	/**
	 * Returns the number of ChemModels in this Container
	 *
	 * @return     
	 */
	public int getChemModelCount()
	{
		return this.chemModelCount;
	}

	
}
