/* $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 *
 * Copyright (C) 1997-2003  The Chemistry Development Kit (CDK) project
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

package org.openscience.cdk;

import java.util.*;

/** 
 * An object containig multiple SetOfMolecules and 
 * the other lower level concepts like rings, sequences, 
 * fragments, etc.
 *
 * @cdkPackage core
 */
public class ChemModel extends ChemObject implements java.io.Serializable, Cloneable
{

	/**
	 *  A SetOfMolecules.
	 */
	protected SetOfMolecules setOfMolecules = null;

	/**
	 *  A SetOfReactions.
	 */
	protected SetOfReactions setOfReactions = null;

	/**
	 *  A RingSet.
	 */
	protected RingSet ringSet = null;
	
    /**
	 *  A Crystal.
	 */
     protected Crystal crystal = null;

	/**
	 *  Constructs an new ChemModel with a non-null, but 
     *  empty setOfMolecules.
	 */
	public ChemModel() {
	}

	/**
	 * Returns the SetOfMolecules of this ChemModel.
	 *
	 * @return   The SetOfMolecules of this ChemModel
     *
     * @see      #setSetOfMolecules
	 */
	public SetOfMolecules getSetOfMolecules()
	{
		return this.setOfMolecules;
	}


	/**
	 * Sets the SetOfMolecules of this ChemModel.
	 *
	 * @param   setOfMolecules  the content of this model
     *
     * @see      #getSetOfMolecules
	 */
	public void setSetOfMolecules(SetOfMolecules setOfMolecules)
	{
		this.setOfMolecules = setOfMolecules;
	}

	

	/**
	 * Returns the RingSet of this ChemModel.
	 *
	 * @return the ringset of this model
     *
     * @see      #setRingSet
	 */
	public RingSet getRingSet() {
		return this.ringSet;
	}


	/**
	 * Sets the RingSet of this ChemModel.
	 *
	 * @param   ringSet         the content of this model
     *
     * @see      #getRingSet
	 */
	public void setRingSet(RingSet ringSet)
	{
		this.ringSet = ringSet;
	}

    /**
     * Gets the Crystal contained in this ChemModel.
     *
     * @return The crystal in this model
     *
     * @see      #setCrystal
     */
    public Crystal getCrystal() {
        return this.crystal;
    }

    /**
     * Sets the Crystal contained in this ChemModel.
     *
     * @param   c     the Crystal to store in this model
     *
     * @see      #getCrystal
     */
    public void setCrystal(Crystal c) {
        this.crystal = c;
    }

    /**
     * Gets the SetOfReactions contained in this ChemModel.
     *
     * @return The SetOfReactions in this model
     *
     * @see      #setSetOfReactions
     */
    public SetOfReactions getSetOfReactions() {
        return this.setOfReactions;
    }

    /**
     * Sets the SetOfReactions contained in this ChemModel.
     *
     * @param sor the SetOfReactions to store in this model
     *
     * @see       #getSetOfReactions
     */
    public void setSetOfReactions(SetOfReactions sor) {
        this.setOfReactions = sor;
    }
    
    /**
     * Returns a String representation of the contents of this
     * ChemObject.
     *
     * @return String representation of content
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("ChemModel(");
        buffer.append(hashCode());
        if (getSetOfMolecules() != null) {
            buffer.append(", ");
            buffer.append(getSetOfMolecules().toString());
        } else {
            buffer.append(", No SetOfMolecules");
        }
        if (getCrystal() != null) {
            buffer.append(getCrystal().toString());
            buffer.append(", ");
        } else {
            buffer.append(", No Crystal");
        }
        if (getSetOfReactions() != null) {
            buffer.append(getSetOfReactions().toString());
            buffer.append(", ");
        } else {
            buffer.append(", No SetOfReactions");
        }
        buffer.append(")");
        return buffer.toString();
    }

}

