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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
 */
package org.openscience.cdk.interfaces;

/** 
 * A sequence of ChemModels, which can, for example, be used to
 * store the course of a reaction. Each state of the reaction would be
 * stored in one ChemModel.
 *
 * @cdk.module  interfaces
 *
 * @cdk.keyword animation
 * @cdk.keyword reaction
 */
public interface ChemSequence extends ChemObject {

	/**
	 * Adds an chemModel to this container.
	 *
	 * @param  chemModel The chemModel to be added to this container
     * @see              #getChemModel
	 */
	public void addChemModel(ChemModel chemModel);

    /**
     * Returns an array of ChemModels of length matching the number of ChemModels 
     * in this container.
     *
     * @return    The array of ChemModels in this container
     * @see       #addChemModel
     */
     public ChemModel[] getChemModels();

	/**
	 * Returns the ChemModel at position <code>number</code> in the
	 * container.
	 *
	 * @param  number  The position of the ChemModel to be returned.
	 * @return         The ChemModel at position <code>number</code>.
     * @see            #addChemModel
	 */
	public ChemModel getChemModel(int number);
	
	/**
	 * Returns the number of ChemModels in this Container.
	 *
	 * @return    The number of ChemModels in this Container
	 */
	public int getChemModelCount();

}
