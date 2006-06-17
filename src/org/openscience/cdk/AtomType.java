/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 *
 *  Copyright (C) 2001-2006  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk;

import org.openscience.cdk.interfaces.IAtomType;

/**
 *  The base class for atom types. Atom types are typically used to describe the
 *  behaviour of an atom of a particular element in different environment like 
 *  sp<sup>3</sup>
 *  hybridized carbon C3, etc., in some molecular modelling applications.
 *
 * @cdk.module data
 *
 * @author     steinbeck
 * @cdk.created    2001-08-08
 *
 * @cdk.keyword     atom, type
 */
public class AtomType extends Isotope implements java.io.Serializable, IAtomType
{

	/**
     * Determines if a de-serialized object is compatible with this class.
     *
     * This value must only be changed if and only if the new version
     * of this class is imcompatible with the old version. See Sun docs
     * for <a href=http://java.sun.com/products/jdk/1.1/docs/guide
     * /serialization/spec/version.doc.html>details</a>.
	 */
	private static final long serialVersionUID = -7950397716808229972L;

	/**
	 *  The maximum bond order allowed for this atom type.
	 */
	double maxBondOrder;
	/**
	 *  The maximum sum of all bondorders allowed for this atom type.
	 */
	double bondOrderSum;

    /**
     * The Vanderwaals radius of this atom type.
     */
    double vanderwaalsRadius;
    /**
     * The covalent radius of this atom type.
     */
    double covalentRadius;
    
    /**
     *  The formal charge of the atom. Implements RFC #6.
     */
    protected int formalCharge;

    /**
     *  The hybridization state of this atom.
     */
    protected int hybridization;

    /**
     *  The Electron Valency of this atom.
     */
    protected int electronValency;

    /**
     * The formal number of neighbours this atom type can have.
     * This includes explicitely and implicitely connected atoms. The latter
     * includes implicit hydrogens.
     */
    protected int formalNeighbourCount;

    /**
     * String representing the identifier for this atom type.
     */
    private String identifier;
    
    /**
	 *  Constructor for the AtomType object.
     *
     * @param elementSymbol  Symbol of the atom
	 */
	public AtomType(String elementSymbol)
	{
		super(elementSymbol);
	}


	/**
	 *  Constructor for the AtomType object.
	 *
	 * @param  identifier     An id for this atom type, like C3 for sp3 carbon
	 * @param  elementSymbol  The element symbol identifying the element to which this atom type applies
	 */
	public AtomType(String identifier, String elementSymbol)
	{
		this(elementSymbol);
		this.identifier = identifier;
	}


	/**
	 *  Sets the if attribute of the AtomType object.
	 *
	 * @param  identifier  The new AtomTypeID value. Null if unset.
     *
     * @see    #getAtomTypeName
	 */
	public void setAtomTypeName(String identifier)
	{
		this.identifier = identifier;
		notifyChanged();
	}


	/**
	 *  Sets the MaxBondOrder attribute of the AtomType object.
	 *
	 * @param  maxBondOrder  The new MaxBondOrder value
     *
     * @see       #getMaxBondOrder
	 */
	public void setMaxBondOrder(double maxBondOrder)
	{
		this.maxBondOrder = maxBondOrder;
		notifyChanged();
	}


	/**
	 *  Sets the the exact bond order sum attribute of the AtomType object.
	 *
	 * @param  bondOrderSum  The new bondOrderSum value
     *
     * @see       #getBondOrderSum
	 */
	public void setBondOrderSum(double bondOrderSum)
	{
		this.bondOrderSum = bondOrderSum;
		notifyChanged();
	}


	/**
	 *  Gets the id attribute of the AtomType object.
	 *
	 * @return    The id value
     *
     * @see       #setAtomTypeName
	 */
	public String getAtomTypeName()
	{
		return this.identifier;
	}


	/**
	 *  Gets the MaxBondOrder attribute of the AtomType object.
	 *
	 * @return    The MaxBondOrder value
     *
     * @see       #setMaxBondOrder
	 */
	public double getMaxBondOrder()
	{
		return maxBondOrder;
	}


	/**
	 *  Gets the bondOrderSum attribute of the AtomType object.
	 *
	 * @return    The bondOrderSum value
     *
     * @see       #setBondOrderSum
	 */
	public double getBondOrderSum()
	{
		return bondOrderSum;
	}

    /**
     *  Sets the formal charge of this atom.
     *
     * @param  charge  The formal charge
     *
     * @see    #getFormalCharge
     */
    public void setFormalCharge(int charge) {
        this.formalCharge = charge;
	notifyChanged();
    }
    
    /**
     *  Returns the formal charge of this atom.
     *
     * @return the formal charge of this atom
     *
     * @see    #setFormalCharge
     */
    public int getFormalCharge() {
        return this.formalCharge;
    }
    
    /**
     * Sets the formal neighbour count of this atom.
     *
     * @param  count  The neighbour count
     *
     * @see    #getFormalNeighbourCount
     */
    public void setFormalNeighbourCount(int count) {
        this.formalNeighbourCount = count;
	notifyChanged();
    }
    
    /**
     * Returns the formal neighbour count of this atom.
     *
     * @return the formal neighbour count of this atom
     *
     * @see    #setFormalNeighbourCount
     */
    public int getFormalNeighbourCount() {
        return this.formalNeighbourCount;
    }
    
    /**
     *  Sets the hybridization of this atom.
     *
     * @param  hybridization  The hybridization
     *
     * @see    #getHybridization
     */
    public void setHybridization(int hybridization) {
        this.hybridization = hybridization;
        notifyChanged();
    }
    
    /**
     *  Returns the hybridization of this atom.
     *
     * @return the hybridization of this atom
     *
     * @see    #setHybridization
     */
    public int getHybridization() {
        return this.hybridization;
    }
    
    /**
     * Compare a atom type with this atom type.
     *
     * @param  object Object of type AtomType
     * @return        Return true, if the atomtypes are equal
     */
    public boolean compare(Object object) {
        if (!(object instanceof IAtomType)) {
            return false;
        }
        if (!super.compare(object)) {
            return false;
        }
        AtomType type = (AtomType) object;
        if ((getAtomTypeName() == type.getAtomTypeName()) &&
            (maxBondOrder == type.maxBondOrder) &&
            (bondOrderSum == type.bondOrderSum)) {
            return true;
        }
        return false;
    }
    
    /**
     * Sets the Vanderwaals radius for this AtomType.
     *
     * @param radius The Vanderwaals radius for this AtomType
     * @see   #getVanderwaalsRadius
     */
    public void setVanderwaalsRadius(double radius) {
        this.vanderwaalsRadius = radius;
	notifyChanged();
    }
    
    /**
     * Returns the Vanderwaals radius for this AtomType.
     *
     * @return The Vanderwaals radius for this AtomType
     * @see    #setVanderwaalsRadius
     */
    public double getVanderwaalsRadius() {
        return this.vanderwaalsRadius;
    }
    
    /**
     * Sets the covalent radius for this AtomType.
     *
     * @param radius The covalent radius for this AtomType
     * @see    #getCovalentRadius
     */
    public void setCovalentRadius(double radius) {
        this.covalentRadius = radius;
	notifyChanged();
    }
    
    /**
     * Returns the covalent radius for this AtomType.
     *
     * @return The covalent radius for this AtomType
     * @see    #setCovalentRadius
     */
    public double getCovalentRadius() {
        return this.covalentRadius;
    }
    
	/**
	 *  Sets the the exact electron valency of the AtomType object.
	 *
	 * @param  valency  The new valency value
	 * @see #getValency
	 *
	 */
	public void setValency(int valency)
	{
		this.electronValency = valency;
		notifyChanged();
	}

	/**
	 *  Gets the the exact electron valency of the AtomType object.
	 *
	 * @return The valency value
	 * @see #setValency
	 *
	 */
	public int getValency()
	{
		return this.electronValency;
	}
	
	/**
	 *  Clones this AtomType object.
	 *
	 * @return    The cloned object
	 */
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
    public String toString() {
        StringBuffer resultString = new StringBuffer();
        resultString.append("AtomType(");
        resultString.append(getAtomTypeName()).append(", ");
        resultString.append("MBO:").append(getMaxBondOrder()).append(", ");
        resultString.append("BOS:").append(getBondOrderSum()).append(", ");
        resultString.append("FC:").append(getFormalCharge()).append(", ");
        resultString.append("H:").append(getHybridization()).append(", ");
        resultString.append("NC:").append(getFormalNeighbourCount()).append(", ");
        resultString.append("CR:").append(getCovalentRadius()).append(", ");
        resultString.append("VDWR:").append(getVanderwaalsRadius()).append(", ");
        resultString.append("EV:").append(getValency()).append(", ");
        resultString.append(super.toString());
        resultString.append(")");
        return resultString.toString(); 
    }
}

