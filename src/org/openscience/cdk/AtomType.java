/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 *
 *  Copyright (C) 2001-2005  The Chemistry Development Kit (CDK) project
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
 * @cdk.module data
 *
 * @author     steinbeck
 * @cdk.created    2001-08-08
 *
 * @cdk.keyword     atom, type
 */
public class AtomType extends Isotope implements java.io.Serializable, org.openscience.cdk.interfaces.AtomType
{

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
     *  Property if atom is Lewis or Bronsted acceptor
     */
    protected boolean acceptor=false;
    
    /**
     *  Property if atom is Lewis or Bronsted donor
     */
    protected boolean donor=false;   
    
    /**
     *  Stores the CDKRingConstant for this atom type.
     */
    protected int chemicalGroupConstant=0;   
    
    /**
     *  Stores the size of the ring to which this atom type belongs.
     */
    protected int ringSize=0;
    
    /**
     *  Stores the information if this atom type is aromatic.
     */
    protected boolean isAromatic=false;
    
    /**
     *  Stores the spherical matching code,eg.
     *  A regular expression for the hose code description of this atom type.
     *  This is a work around for the missing SMARTS handling.
     */
    protected String sphericalMatcher=null;   
    
    
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
		setAtomTypeName(identifier);
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
		setID(identifier);
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
		return getID();
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
	 *
	 */
	public int getValency()
	{
		return this.electronValency;
	}

	/**
	 * Sets the property of acceptor (Lewis or Bronsted type) type boolean
	 * @param  isAcceptor  boolean
	 *
	 */
	public void setAcceptor(boolean isAcceptor)
	{
		this.acceptor = isAcceptor;
		notifyChanged();
	}

	/**
	 *  Gets the the acceptor type of the AtomType object.
	 *  
	 *  returns boolean
	 *
	 */
	public boolean getAcceptor()
	{
		return this.acceptor;
	}

	/**
	 * Sets the property of donor (Lewis or Bronsted type) type boolean
	 * @param  isDonor  boolean
	 *
	 */
	public void setDonor(boolean isDonor)
	{
		this.donor = isDonor;
		notifyChanged();
	}

	/**
	 *  Gets the the donor type of the AtomType object.
	 *  
	 *  returns boolean
	 *
	 */
	public boolean getDonor()
	{
		return this.donor;
	}
	
	/**
	 * Sets the property to which chemical group (e.g. ring system) the atom type belongs.
	 * Values can be found in CDKChemicalGroupConstants
	 * 
	 * @param  chemicalGroupCDKConstant  int
	 *
	 */
	public void setChemicalGroupConstant(int chemicalGroupCDKConstant)
	{
		this.chemicalGroupConstant = chemicalGroupCDKConstant;
		notifyChanged();
	}

	/**
	 *  Gets the property to which ring system the atom type belongs.
	 *  
	 *  returns ringConstant int
	 *
	 */
	public int getChemicalGroupConstant()
	{
		return this.chemicalGroupConstant;
	}
	
	/**
	 * Sets ring size for which the atom type is defined.
	 * 
	 * @param  sizeOfRingSystem  int
	 *
	 */
	public void setRingSize(int sizeOfRingSystem)
	{
		this.ringSize = sizeOfRingSystem;
		notifyChanged();
	}

	/**
	 *  Gets the ring size for which the atom type is defined.
	 *  
	 *  returns ringSize int
	 *
	 */
	public int getRingSize()
	{
		return this.ringSize;
	}
	
	/**
	 * Sets if atom type is aromatic.
	 * 
	 * @param  ISAROMATIC  boolean
	 *
	 */
	public void setIsAromatic(boolean ISAROMATIC)
	{
		this.isAromatic = ISAROMATIC;
		notifyChanged();
	}

	/**
	 *  Gets if atom type is defined as aromatic.
	 *  
	 *  returns isAromatic boolean
	 *
	 */
	public boolean getIsAromatic()
	{
		return this.isAromatic;
	}
	
	/**
	 * Sets regular expression/or e.g. hoseCode for the corresponding atom to the atom type,
	 * (In an atom it stores the e.g. hose code, 
	 * in atomType for AtomtypeFactory it stores the regular expression)
	 * 
	 * @param  description  String
	 *
	 */
	public void setSphericalMatcher(String description)
	{
		this.sphericalMatcher = description;
		notifyChanged();
	}

	/**
	 *  Gets the regular expression for the hose code of the corresponding atom to the atom type.
	 *  
	 *  returns regularExpression String
	 *
	 */
	public String getSphericalMatcher()
	{
		return this.sphericalMatcher;
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
        resultString.append("A:").append(getAcceptor()).append(", ");
        resultString.append("D:").append(getDonor()).append(", ");
        resultString.append("ChemicalGroupCode:").append(getChemicalGroupConstant()).append(", ");
        resultString.append("RingSize:").append(getRingSize()).append(", ");
        resultString.append("Aromatic:").append(getIsAromatic()).append(", ");
        resultString.append("SphericalDescriptor:").append(getSphericalMatcher()).append(", ");
        resultString.append(super.toString());
        resultString.append(")");
        return resultString.toString(); 
    }
}

