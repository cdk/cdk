/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 1997-2006  The Chemistry Development Kit (CDK) project
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
 * The base class for atom types. Atom types are typically used to describe the
 * behaviour of an atom of a particular element in different environment like 
 * sp<sup>3</sup> hybridized carbon C3, etc., in some molecular modelling 
 * applications.
 *
 * @cdk.module interfaces
 *
 * @author      egonw
 * @cdk.created 2005-08-24
 *
 * @cdk.keyword atom, type
 */
public interface IAtomType extends IIsotope {

	/**
	 * Sets the if attribute of the AtomType object.
	 *
	 * @param  identifier  The new AtomTypeID value. Null if unset.
     * @see    #getAtomTypeName
	 */
	public void setAtomTypeName(String identifier);

	/**
	 * Sets the MaxBondOrder attribute of the AtomType object.
	 *
	 * @param  maxBondOrder  The new MaxBondOrder value
     * @see    #getMaxBondOrder
	 */
	public void setMaxBondOrder(double maxBondOrder);

	/**
	 * Sets the the exact bond order sum attribute of the AtomType object.
	 *
	 * @param  bondOrderSum  The new bondOrderSum value
     * @see    #getBondOrderSum
	 */
	public void setBondOrderSum(double bondOrderSum);

	/**
	 * Gets the id attribute of the AtomType object.
	 *
	 * @return    The id value
     * @see       #setAtomTypeName
	 */
	public String getAtomTypeName();

	/**
	 * Gets the MaxBondOrder attribute of the AtomType object.
	 *
	 * @return    The MaxBondOrder value
     * @see       #setMaxBondOrder
	 */
	public double getMaxBondOrder();

	/**
	 * Gets the bondOrderSum attribute of the AtomType object.
	 *
	 * @return    The bondOrderSum value
     * @see       #setBondOrderSum
	 */
	public double getBondOrderSum();
	
    /**
     * Sets the formal charge of this atom.
     *
     * @param  charge  The formal charge
     * @see    #getFormalCharge
     */
    public void setFormalCharge(int charge);
    
    /**
     * Returns the formal charge of this atom.
     *
     * @return the formal charge of this atom
     * @see    #setFormalCharge
     */
    public int getFormalCharge();
    
    /**
     * Sets the formal neighbour count of this atom.
     *
     * @param  count  The neighbour count
     * @see    #getFormalNeighbourCount
     */
    public void setFormalNeighbourCount(int count);
    
    /**
     * Returns the formal neighbour count of this atom.
     *
     * @return the formal neighbour count of this atom
     * @see    #setFormalNeighbourCount
     */
    public int getFormalNeighbourCount();
    
    /**
     * Sets the hybridization of this atom.
     *
     * @param  hybridization  The hybridization
     * @see    #getHybridization
     */
    public void setHybridization(int hybridization);
    
    /**
     * Returns the hybridization of this atom.
     *
     * @return the hybridization of this atom
     * @see    #setHybridization
     */
    public int getHybridization();
    
    /**
     * Sets the Vanderwaals radius for this AtomType.
     *
     * @param radius The Vanderwaals radius for this AtomType
     * @see   #getVanderwaalsRadius
     */
    public void setVanderwaalsRadius(double radius);
    
    /**
     * Returns the Vanderwaals radius for this AtomType.
     *
     * @return The Vanderwaals radius for this AtomType
     * @see    #setVanderwaalsRadius
     */
    public double getVanderwaalsRadius();
    
    /**
     * Sets the covalent radius for this AtomType.
     *
     * @param radius The covalent radius for this AtomType
     * @see    #getCovalentRadius
     */
    public void setCovalentRadius(double radius);
    
    /**
     * Returns the covalent radius for this AtomType.
     *
     * @return The covalent radius for this AtomType
     * @see    #setCovalentRadius
     */
    public double getCovalentRadius();
    
	/**
	 * Sets the the exact electron valency of the AtomType object.
	 *
	 * @param  valency  The new valency value
	 */
	public void setValency(int valency);

	/**
	 * Gets the the exact electron valency of the AtomType object.
	 *
	 * @return The valency value
	 */
	public int getValency();

}

