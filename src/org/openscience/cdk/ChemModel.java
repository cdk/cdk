/*
 * $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 *
 * Copyright (C) 1997-2002  The Chemistry Development Kit (CDK) project
 *
 * Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
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

import java.util.*;

/** An object containig multiple SetOfMolecules and 
  * the other lower level concepts like rings, sequences, 
  * fragments, etc.
  */
public class ChemModel extends ChemObject
{

	/**
	 *  A SetOfMolecules
	 */
	protected SetOfMolecules setOfMolecules = null;

	/**
	 *  A RingSet
	 */
	protected RingSet ringSet = null;
	
    /**
	 *  A Crystal
	 */
     protected Crystal crystal = null;

	/**
	 *  Constructs an empty SetOfMolecules
	 */
	public ChemModel()   
	{
		setOfMolecules = new SetOfMolecules();
	}



	/**
	 * Puts all the Molecules of this container together in one AtomCcntainer.
	 *
	 * @return  The AtomContainer with all the Molecules of this container   
	 */
	public AtomContainer getAllInOneContainer()
	{
		AtomContainer ac = new AtomContainer();
		for (int i = 0; i < setOfMolecules.getMoleculeCount(); i++)
		{
			ac.add(setOfMolecules.getMolecule(i));
		}
		return ac;
	}
	
	/**
	 * Returns the SetOfMolecules of this ChemModel
	 *
	 * @return   The SetOfMolecules of this ChemModel 
	 */
	public SetOfMolecules getSetOfMolecules()
	{
		return this.setOfMolecules;
	}


	/**
	 * Sets the SetOfMolecules of this ChemModel
	 *
	 * @param   setOfMolecules  
	 */
	public void setSetOfMolecules(SetOfMolecules setOfMolecules)
	{
		this.setOfMolecules = setOfMolecules;
	}

	

	/**
	 * Returns the RingSet of this ChemModel
	 *
	 * @return the ringset of this model
	 */
	public RingSet getRingSet()
	{
		return this.ringSet;
	}


	/**
	 * Sets the RingSet of this ChemModel
	 *
	 * @param   ringSet
	 */
	public void setRingSet(RingSet ringSet)
	{
		this.ringSet = ringSet;
	}

    /**
     * Gets the Crystal contained in this ChemModel
     *
     * @return The crystal in this model
     */
    public Crystal getCrystal() {
        return this.crystal;
    }

    /**
     * Sets the Crystal contained in this ChemModel
     *
     * @param   c     the Crystal to store in this model
     */
    public void setCrystal(Crystal c) {
        this.crystal = c;
    }
}

