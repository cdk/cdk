/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 *
 *  Copyright (C) 1997-2003  The Chemistry Development Kit (CDK) project
 *
 *  Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
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
 * @created    8. August 2001
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
	double maxBondOrderSum;

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
	 *  Sets the MaxBondCount attribute of the AtomType object.
	 *
	 * @param  maxBondOrderSum  The new MaxBondOrderSum value
     *
     * @see       #getMaxBondOrderSum
	 */
	public void setMaxBondOrderSum(double maxBondOrderSum)
	{
		this.maxBondOrderSum = maxBondOrderSum;
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
	 *  Gets the MaxBondCount attribute of the AtomType object.
	 *
	 * @return    The MaxBondCount value
     *
     * @see       #setMaxBondOrderSum
	 */
	public double getMaxBondOrderSum()
	{
		return maxBondOrderSum;
	}

    /**
    * Compare a atom type with this atom type.
    *
    * @param  object Object of type AtomType
    * @return        Return true, if the atomtypes are equal
    */
    public boolean compare(Object object)
    {
        if (object instanceof AtomType) {
            AtomType type = (AtomType) object;
            if ((getAtomTypeName()==type.getAtomTypeName()) &&
                (maxBondOrder==type.maxBondOrder) &&
            (maxBondOrderSum==type.maxBondOrderSum))
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
        sb.append("MB:" + getMaxBondOrder() + ", ");
        sb.append("TBS:" + getMaxBondOrderSum());
        sb.append(super.toString());
        sb.append(")");
        return sb.toString(); 
    }
}

