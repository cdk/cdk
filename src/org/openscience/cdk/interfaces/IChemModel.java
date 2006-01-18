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
package org.openscience.cdk.interfaces;

/** 
 * An object containig multiple SetOfMolecules and 
 * the other lower level concepts like rings, sequences, 
 * fragments, etc.
 *
 * @cdk.module interfaces
 */
public interface IChemModel extends IChemObject {

	/**
	 * Returns the SetOfMolecules of this ChemModel.
	 *
	 * @return   The SetOfMolecules of this ChemModel
     * @see      #setSetOfMolecules
	 */
	public SetOfMolecules getSetOfMolecules();

	/**
	 * Sets the SetOfMolecules of this ChemModel.
	 *
	 * @param   setOfMolecules  the content of this model
     * @see      #getSetOfMolecules
	 */
	public void setSetOfMolecules(SetOfMolecules setOfMolecules);	

	/**
	 * Returns the RingSet of this ChemModel.
	 *
	 * @return the ringset of this model
     * @see      #setRingSet
	 */
	public RingSet getRingSet();

	/**
	 * Sets the RingSet of this ChemModel.
	 *
	 * @param   ringSet         the content of this model
     * @see      #getRingSet
	 */
	public void setRingSet(RingSet ringSet);

    /**
     * Gets the Crystal contained in this ChemModel.
     *
     * @return The crystal in this model
     * @see      #setCrystal
     */
    public ICrystal getCrystal();

    /**
     * Sets the Crystal contained in this ChemModel.
     *
     * @param   crystal  the Crystal to store in this model
     * @see      #getCrystal
     */
    public void setCrystal(ICrystal crystal);

    /**
     * Gets the SetOfReactions contained in this ChemModel.
     *
     * @return The SetOfReactions in this model
     * @see      #setSetOfReactions
     */
    public SetOfReactions getSetOfReactions();

    /**
     * Sets the SetOfReactions contained in this ChemModel.
     *
     * @param sor the SetOfReactions to store in this model
     * @see       #getSetOfReactions
     */
    public void setSetOfReactions(SetOfReactions sor);
    
}

