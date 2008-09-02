/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 *
 * Copyright (C) 2006-2007  Egon Willighagen <egonw@users.sf.net>
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


/** 
 * A sequence of ChemModels, which can, for example, be used to
 * store the course of a reaction. Each state of the reaction would be
 * stored in one ChemModel.
 *
 * @cdk.module  interfaces
 * @cdk.svnrev  $Revision$
 *
 * @cdk.keyword animation
 * @cdk.keyword reaction
 */
public interface IChemSequence extends IChemObject {

	/**
	 * Adds an chemModel to this container.
	 *
	 * @param  chemModel The chemModel to be added to this container
     * @see              #getChemModel
	 */
	public void addChemModel(IChemModel chemModel);

	/**
	 * Remove a ChemModel from this ChemSequence.
	 *
	 * @param  pos  The position of the ChemModel to be removed.
	 */
	public void removeChemModel(int pos);
	
    /**
     * Returns an Iterable to ChemModels in this container.
     *
     * @return    The Iterable to ChemModels in this container
     * @see       #addChemModel
     */
     public Iterable<IChemModel> chemModels();

	/**
	 * Returns the ChemModel at position <code>number</code> in the
	 * container.
	 *
	 * @param  number  The position of the ChemModel to be returned.
	 * @return         The ChemModel at position <code>number</code>.
     * @see            #addChemModel
	 */
	public IChemModel getChemModel(int number);
	
	/**
	 * Returns the number of ChemModels in this Container.
	 *
	 * @return    The number of ChemModels in this Container
	 */
	public int getChemModelCount();

}
