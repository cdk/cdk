/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2004  The Chemistry Development Kit (CDK) project
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
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk;

import org.openscience.cdk.event.ChemObjectChangeEvent;

/**
 *  A Object containing a number of ChemSequences. This is supposed to be the
 *  top level container, which can contain all the concepts stored in a chemical
 *  document
 *
 *@author        steinbeck
 *@created       23. September 2004
 *@cdk.module    core
 */
public class ChemFile extends ChemObject implements java.io.Serializable
		, Cloneable, ChemObjectListener
{

	/**
	 *  Array of ChemSquences.
	 */
	protected ChemSequence[] chemSequences;

	/**
	 *  Number of ChemSequences contained by this container.
	 */
	protected int chemSequenceCount;

	/**
	 *  Amount by which the chemsequence array grows when elements are added and
	 *  the array is not large enough for that.
	 */
	protected int growArraySize = 4;


	/**
	 *  Constructs an empty SetOfChemSequences.
	 */
	public ChemFile()
	{
		chemSequenceCount = 0;
		chemSequences = new ChemSequence[growArraySize];
	}


	/**
	 *  Adds an chemSequence to this container.
	 *
	 *@param  chemSequence  The chemSequence to be added to this container
	 *@see                  #getChemSequences
	 */
	public void addChemSequence(ChemSequence chemSequence)
	{
		chemSequence.addListener(this);
		if (chemSequenceCount + 1 >= chemSequences.length)
		{
			growChemSequenceArray();
		}
		chemSequences[chemSequenceCount] = chemSequence;
		chemSequenceCount++;
		notifyChanged();
	}



	/**
	 *  Returns the array of ChemSequences of this container.
	 *
	 *@return    The array of ChemSequences of this container
	 *@see       #addChemSequence
	 */
	public ChemSequence[] getChemSequences()
	{
		ChemSequence[] returnChemSequences = new ChemSequence[getChemSequenceCount()];
		System.arraycopy(this.chemSequences, 0, returnChemSequences,
				0, returnChemSequences.length);
		return returnChemSequences;
	}


	/**
	 *  Returns the ChemSequence at position <code>number</code> in the container.
	 *
	 *@param  number  The position of the ChemSequence to be returned.
	 *@return         The ChemSequence at position <code>number</code>.
	 *@see            #addChemSequence
	 */
	public ChemSequence getChemSequence(int number)
	{
		return chemSequences[number];
	}


	/**
	 *  Grows the chemSequence array by a given size.
	 *
	 *@see    #growArraySize
	 */
	protected void growChemSequenceArray()
	{
		growArraySize = chemSequences.length;
		ChemSequence[] newchemSequences = new ChemSequence[chemSequences.length + growArraySize];
		System.arraycopy(chemSequences, 0, newchemSequences, 0, chemSequences.length);
		chemSequences = newchemSequences;
	}


	/**
	 *  Returns the number of ChemSequences in this Container.
	 *
	 *@return    The number of ChemSequences in this Container
	 */
	public int getChemSequenceCount()
	{
		return this.chemSequenceCount;
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append("ChemFile(#S=");
		ChemSequence[] seqs = getChemSequences();
		buffer.append(seqs.length);
		buffer.append(", ");
		for (int i = 0; i < seqs.length; i++)
		{
			ChemSequence sequence = seqs[i];
			buffer.append(sequence.toString());
		}
		buffer.append(")");
		return buffer.toString();
	}


	/**
	 *  Allows for getting an clone of this object
	 *
	 *@return    a clone of this object
	 */
	public Object clone()
	{
		ChemFile clone = (ChemFile) super.clone();
		// clone the chemModels
		clone.chemSequenceCount = getChemSequenceCount();
		clone.chemSequences = new ChemSequence[clone.chemSequenceCount];
		for (int f = 0; f < clone.chemSequenceCount; f++)
		{
			clone.chemSequences[f] = (ChemSequence) chemSequences[f].clone();
		}
		return clone;
	}


	/**
	 *  Called by objects to which this object has
	 *  registered as a listener
	 *
	 *@param  event  A change event pointing to the source of the change
	 */
	public void stateChanged(ChemObjectChangeEvent event)
	{
		notifyChanged(event);
	}
}

