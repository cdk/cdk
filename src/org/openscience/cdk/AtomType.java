/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 *
 *  Copyright (C) 2001-2003  The Chemistry Development Kit (CDK) project
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
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.openscience.cdk;

/**
 *  The base class for atom types. Atom types are typically used to describe the
 *  behaviour of an atom of a particular element in different environment like 
 *  sp<sup>3</sup>
 *  hybridized carbon C3, etc., in some molecular modelling applications.
 *
 * @author     steinbeck
 * @created    2001-08-08
 *
 * @keyword     atom, type
 */
public class AtomType extends Isotope implements java.io.Serializable, Cloneable
{
	/**
	 *  An id for this atom type, like C3 for sp3 carbon.
	 */
	String id;

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
	 *  Constructor for the AtomType object.
     *
     * @param elementSymbol  Symbol of the atom
	 */
	public AtomType(String elementSymbol)
	{
		super(elementSymbol);
		this.id = null;
	}


	/**
	 *  Constructor for the AtomType object.
	 *
	 * @param  id             An id for this atom type, like C3 for sp3 carbon
	 * @param  elementSymbol  The element symbol identifying the element to which this atom type applies
	 */
	public AtomType(String id, String elementSymbol)
	{
		this(elementSymbol);
		setAtomTypeName(id);
	}


	/**
	 *  Sets the if attribute of the AtomType object.
	 *
	 * @param  id  The new AtomTypeID value
     *
     * @see    #getAtomTypeName
	 */
	public void setAtomTypeName(String id)
	{
		this.id = id;
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
		return id;
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
     * Compare a atom type with this atom type.
     *
     * @param  object Object of type AtomType
     * @return        Return true, if the atomtypes are equal
     */
    public boolean compare(Object object) {
        if (!(object instanceof AtomType)) {
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
    
    public void setVanderwaalsRadius(double radius) {
        this.vanderwaalsRadius = radius;
    }
    
    public double getVanderwaalsRadius() {
        return this.vanderwaalsRadius;
    }
    
    public void setCovalentRadius(double radius) {
        this.covalentRadius = radius;
    }
    
    public double getCovalentRadius() {
        return this.covalentRadius;
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("AtomType(");
        sb.append(getAtomTypeName() + ", ");
        sb.append("MBO:" + getMaxBondOrder() + ", ");
        sb.append("BOS:" + getBondOrderSum() + ", ");
        sb.append(super.toString());
        sb.append(")");
        return sb.toString(); 
    }
}

