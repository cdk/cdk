/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 *
 * Copyright (C) 1997-2005  The Chemistry Development Kit (CDK) project
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

package org.openscience.cdk;

import org.openscience.cdk.interfaces.ChemObjectChangeEvent;
import org.openscience.cdk.interfaces.ChemObjectListener;

/** 
 * A sequence of ChemModels, which can, for example, be used to
 * store the course of a reaction. Each state of the reaction would be
 * stored in one ChemModel.
 *
 * @cdk.module data
 *
 * @cdk.keyword animation
 * @cdk.keyword reaction
 */
public class ChemSequence extends ChemObject implements java.io.Serializable, org.openscience.cdk.interfaces.ChemSequence, ChemObjectListener
{

	/**
     * Determines if a de-serialized object is compatible with this class.
     *
     * This value must only be changed if and only if the new version
     * of this class is imcompatible with the old version. See Sun docs
     * for <a href=http://java.sun.com/products/jdk/1.1/docs/guide
     * /serialization/spec/version.doc.html>details</a>.
	 */
	private static final long serialVersionUID = 2199218627455492000L;

	/**
	 *  Array of ChemModels.
	 */
	protected org.openscience.cdk.interfaces.IChemModel[] chemModels;
	
	/**
	 *  Number of ChemModels contained by this container.
	 */
	protected int chemModelCount;
	
	/**
	 *  Amount by which the chemModels array grows when elements are added and
	 *  the array is not large enough for that. 
	 */
	protected int growArraySize = 4;



	/**
	 *  Constructs an empty ChemSequence.
	 */
	public ChemSequence()   
	{
		chemModelCount = 0;
		chemModels = new ChemModel[growArraySize];
	}


	
	/**
	 *  Adds an chemModel to this container.
	 *
	 * @param  chemModel  The chemModel to be added to this container
     *
     * @see            #getChemModel
	 */
	public void addChemModel(org.openscience.cdk.interfaces.IChemModel chemModel)
	{
		if (chemModelCount + 1 >= chemModels.length)
		{
			growChemModelArray();
		}
		chemModels[chemModelCount] = chemModel;
		chemModelCount++;
		chemModel.addListener(this);
		notifyChanged();
	}


    /**
     * Returns an array of ChemModels of length matching the number of ChemModels 
     * in this container.
     *
     * @return    The array of ChemModels in this container
     *
     * @see       #addChemModel
     */
     public org.openscience.cdk.interfaces.IChemModel[] getChemModels() {
    	 org.openscience.cdk.interfaces.IChemModel[] returnModels = new ChemModel[getChemModelCount()];
         System.arraycopy(this.chemModels, 0, returnModels, 0, returnModels.length);
         return returnModels;
     }


	/**
	 *
	 * Returns the ChemModel at position <code>number</code> in the
	 * container.
	 *
	 * @param  number  The position of the ChemModel to be returned.
	 * @return         The ChemModel at position <code>number</code>.
     *
     * @see            #addChemModel
	 */
	public org.openscience.cdk.interfaces.IChemModel getChemModel(int number)
	{
		return chemModels[number];
	}
	
	/**
	 *  Grows the chemModel array by a given size.
	 *
	 * @see    growArraySize
	 */
	protected void growChemModelArray()
	{
		ChemModel[] newchemModels = new ChemModel[chemModels.length + growArraySize];
		System.arraycopy(chemModels, 0, newchemModels, 0, chemModels.length);
		chemModels = newchemModels;
	}
	

	/**
	 * Returns the number of ChemModels in this Container.
	 *
	 * @return    The number of ChemModels in this Container
	 */
	public int getChemModelCount()
	{
		return this.chemModelCount;
	}

	public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("ChemSequence(#M=");
        org.openscience.cdk.interfaces.IChemModel[] models = getChemModels();
        buffer.append(models.length);
        buffer.append(", ");
        for (int i=0; i<models.length; i++) {
        	org.openscience.cdk.interfaces.IChemModel model = models[i];
            buffer.append(model.toString());
        }
        buffer.append(")");
        return buffer.toString();
    }
	
	public Object clone() {
		ChemSequence clone = (ChemSequence)super.clone();
        // clone the chemModels
        clone.chemModelCount = getChemModelCount();
		clone.chemModels = new ChemModel[clone.chemModelCount];
		for (int f = 0; f < clone.chemModelCount; f++) {
			clone.chemModels[f] = (ChemModel)((ChemModel)chemModels[f]).clone();
		}
		return clone;
	}
	
	/**
	 *  Called by objects to which this object has
	 *  registered as a listener.
	 *
	 *@param  event  A change event pointing to the source of the change
	 */
	public void stateChanged(ChemObjectChangeEvent event)
	{
		notifyChanged(event);
	}
}
