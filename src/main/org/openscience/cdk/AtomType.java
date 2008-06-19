/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 *
 *  Copyright (C) 2001-2007  Christoph Steinbeck <steinbeck@users.sf.net>
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
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IElement;

import java.io.Serializable;

/**
 * The base class for atom types. Atom types are typically used to describe the
 * behaviour of an atom of a particular element in different environment like 
 * sp<sup>3</sup>
 * hybridized carbon C3, etc., in some molecular modelling applications.
 *
 * @author       steinbeck
 * @cdk.created  2001-08-08
 * @cdk.module   data
 * @cdk.svnrev  $Revision$
 * @cdk.keyword  atom, type
 */
public class AtomType extends Isotope implements IAtomType, Serializable, Cloneable
{

	/**
     * Determines if a de-serialized object is compatible with this class.
     *
     * This value must only be changed if and only if the new version
     * of this class is incompatible with the old version. See Sun docs
     * for <a href=http://java.sun.com/products/jdk/1.1/docs/guide
     * /serialization/spec/version.doc.html>details</a>.
	 */
	private static final long serialVersionUID = -7950397716808229972L;

	/**
	 *  The maximum bond order allowed for this atom type.
	 */
	IBond.Order maxBondOrder = null;
	/**
	 *  The maximum sum of all bond orders allowed for this atom type.
	 */
	Double bondOrderSum = (Double) CDKConstants.UNSET;

    /**
     * The covalent radius of this atom type.
     */
    Double covalentRadius = (Double) CDKConstants.UNSET;
    
    /**
     *  The formal charge of the atom with CDKConstants.UNSET as default. Implements RFC #6.
     *
     */
    protected Integer formalCharge = (Integer) CDKConstants.UNSET;

    /**
     * The hybridization state of this atom with CDKConstants.HYBRIDIZATION_UNSET
     * as default.
     */
    protected IAtomType.Hybridization hybridization = (Hybridization) CDKConstants.UNSET;

    /**
     *  The electron Valency of this atom with CDKConstants.UNSET as default.
     */
    protected Integer electronValency = (Integer) CDKConstants.UNSET;

    /**
     * The formal number of neighbours this atom type can have with CDKConstants_UNSET
     * as default. This includes explicitely and implicitely connected atoms, including
     * implicit hydrogens.
     */
    protected Integer formalNeighbourCount = (Integer) CDKConstants.UNSET;

    /**
     * String representing the identifier for this atom type with null as default.
     */
    private String identifier;
    
    /**
	 * Constructor for the AtomType object. Defaults to a zero formal charge.
     *
     * @param elementSymbol  Symbol of the atom
	 */
	public AtomType(String elementSymbol) {
		super(elementSymbol);
		this.identifier = null;
		this.formalNeighbourCount = 0;
		this.electronValency = 0;
		this.formalCharge = 0;
	}


	/**
	 * Constructor for the AtomType object. Defaults to a zero formal charge.
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
	 * Constructs an isotope by copying the symbol, atomic number,
	 * flags, identifier, exact mass, natural abundance and mass 
	 * number from the given IIsotope. It does not copy the
	 * listeners and properties. If the element is an instanceof
	 * IAtomType, then the maximum bond order, bond order sum,
	 * van der Waals and covalent radii, formal charge, hybridization,
	 * electron valency, formal neighbour count and atom type name
	 * are copied too.
	 * 
	 * @param element IIsotope to copy information from
	 */
	public AtomType(IElement element) {
		super(element);
		if (element instanceof IAtomType) {
			this.maxBondOrder = ((IAtomType)element).getMaxBondOrder();
			this.bondOrderSum = ((IAtomType)element).getBondOrderSum();
			this.covalentRadius = ((IAtomType)element).getCovalentRadius();
			this.formalCharge = ((IAtomType)element).getFormalCharge();
			this.hybridization = ((IAtomType)element).getHybridization();
			this.electronValency = ((IAtomType)element).getValency();
			this.formalNeighbourCount = ((IAtomType)element).getFormalNeighbourCount();
			this.identifier = ((IAtomType)element).getAtomTypeName();
		}
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
	public void setMaxBondOrder(IBond.Order maxBondOrder)
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
	public void setBondOrderSum(Double bondOrderSum)
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
	public IBond.Order getMaxBondOrder()
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
	public Double getBondOrderSum()
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
    public void setFormalCharge(Integer charge) {
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
    public Integer getFormalCharge() {
        return this.formalCharge;
    }
    
    /**
     * Sets the formal neighbour count of this atom.
     *
     * @param  count  The neighbour count
     *
     * @see    #getFormalNeighbourCount
     */
    public void setFormalNeighbourCount(Integer count) {
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
    public Integer getFormalNeighbourCount() {
        return this.formalNeighbourCount;
    }
    
    /**
     *  Sets the hybridization of this atom.
     *
     * @param  hybridization  The hybridization
     *
     * @see    #getHybridization
     */
    public void setHybridization(IAtomType.Hybridization hybridization) {
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
    public IAtomType.Hybridization getHybridization() {
        return this.hybridization;
    }
    
    /**
     * Compares a atom type with this atom type.
     *
     * @param  object Object of type AtomType
     * @return        true if the atom types are equal
     */
    public boolean compare(Object object) {
        if (!(object instanceof IAtomType)) {
            return false;
        }
        if (!super.compare(object)) {
            return false;
        }
        AtomType type = (AtomType) object;
        return (getAtomTypeName() == type.getAtomTypeName()) &&
                (maxBondOrder == type.maxBondOrder) &&
                (bondOrderSum == type.bondOrderSum);
    }

    /**
     * Sets the covalent radius for this AtomType.
     *
     * @param radius The covalent radius for this AtomType
     * @see    #getCovalentRadius
     */
    public void setCovalentRadius(Double radius) {
        this.covalentRadius = radius;
	notifyChanged();
    }
    
    /**
     * Returns the covalent radius for this AtomType.
     *
     * @return The covalent radius for this AtomType
     * @see    #setCovalentRadius
     */
    public Double getCovalentRadius() {
        return this.covalentRadius;
    }
    
	/**
	 *  Sets the the exact electron valency of the AtomType object.
	 *
	 * @param  valency  The new valency value
	 * @see #getValency
	 *
	 */
	public void setValency(Integer valency)
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
	public Integer getValency()
	{
		return this.electronValency;
	}

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
	
    public String toString() {
        StringBuffer resultString = new StringBuffer(64);
        resultString.append("AtomType(").append(hashCode());
        if (getAtomTypeName() != null) {
        	resultString.append(", N:").append(getAtomTypeName());
        }
        if (getMaxBondOrder() != null) {
        	resultString.append(", MBO:").append(getMaxBondOrder());
        }
        if (getBondOrderSum() != null) {
        	resultString.append(", BOS:").append(getBondOrderSum());
        }
        if (getFormalCharge() != null) {
        	resultString.append(", FC:").append(getFormalCharge());
        }
        if (getHybridization() != null) {
        	resultString.append(", H:").append(getHybridization());
        }
        if (getFormalNeighbourCount() != null) {
        	resultString.append(", NC:").append(getFormalNeighbourCount());
        }
        if (getCovalentRadius() != null) {
        	resultString.append(", CR:").append(getCovalentRadius());
        }        
        if (getValency() != null) {
        	resultString.append(", EV:").append(getValency());
        }
        resultString.append(", ").append(super.toString());
        resultString.append(')');
        return resultString.toString(); 
    }
}

