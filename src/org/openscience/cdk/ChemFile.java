/*
 *  $RCSfile$
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

import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;
import org.openscience.cdk.interfaces.IChemSequence;

/**
 *  A Object containing a number of ChemSequences. This is supposed to be the
 *  top level container, which can contain all the concepts stored in a chemical
 *  document
 *
 *@author        steinbeck
 *@cdk.module    data
 */
public class ChemFile extends ChemObject implements java.io.Serializable,
		IChemFile, IChemObjectListener
{

	/**
     * Determines if a de-serialized object is compatible with this class.
     *
     * This value must only be changed if and only if the new version
     * of this class is imcompatible with the old version. See Sun docs
     * for <a href=http://java.sun.com/products/jdk/1.1/docs/guide
     * /serialization/spec/version.doc.html>details</a>.
	 */
	private static final long serialVersionUID = 1926781734333430132L;

	/**
	 *  Array of ChemSquences.
	 */
	protected IChemSequence[] chemSequences;

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
	 *  Constructs an empty ChemFile.
	 */
	public ChemFile()
	{
		chemSequenceCount = 0;
		chemSequences = new ChemSequence[growArraySize];
	}


	/**
	 *  Adds an ChemSequence to this container.
	 *
	 *@param  chemSequence  The chemSequence to be added to this container
	 *@see                  #getChemSequences
	 */
	public void addChemSequence(IChemSequence chemSequence)
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
	public IChemSequence[] getChemSequences()
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
	public IChemSequence getChemSequence(int number)
	{
		return (ChemSequence)chemSequences[number];
	}


	/**
	 *  Grows the ChemSequence array by a given size.
	 *
	 *@see    #growArraySize
	 */
	protected void growChemSequenceArray()
	{
		growArraySize = chemSequences.length;
		IChemSequence[] newchemSequences = new ChemSequence[chemSequences.length + growArraySize];
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
	 * Returns a String representation of this class. It implements
         * RFC #9.
	 *
	 *@return    String representation of the Object
	 */
	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append("ChemFile(#S=");
		IChemSequence[] seqs = getChemSequences();
		buffer.append(seqs.length);
		buffer.append(", ");
		for (int i = 0; i < seqs.length; i++)
		{
			IChemSequence sequence = seqs[i];
			buffer.append(sequence.toString());
		}
		buffer.append(")");
		return buffer.toString();
	}


	/**
	 *  Allows for getting an clone of this object.
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
			clone.chemSequences[f] = (ChemSequence)((ChemSequence)chemSequences[f]).clone();
		}
		return clone;
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

