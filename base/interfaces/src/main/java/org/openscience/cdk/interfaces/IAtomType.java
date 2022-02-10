/* Copyright (C) 2006-2007  Egon Willighagen <egonw@users.sf.net>
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
 * The base class for atom types.
 *
 * Atom types are typically used to describe the
 * behaviour of an atom of a particular element in different environment like
 * sp<sup>3</sup> hybridized carbon C3, etc., in some molecular modelling
 * applications.
 *
 * @cdk.module  interfaces
 * @cdk.githash
 *
 * @author      egonw
 * @cdk.created 2005-08-24
 *
 * @cdk.keyword atom, type
 */
public interface IAtomType extends IIsotope {

    /**
     *
     * An enum for the different hybridization states.
     *
     */
    enum Hybridization {
        S, SP1, // linear
        SP2, // trigonal planar (single pi-electron in pz)
        SP3, // tetrahedral
        PLANAR3, // trigonal planar (lone pair in pz)
        SP3D1, // trigonal planar
        SP3D2, // octahedral
        SP3D3, // pentagonal bipyramid
        SP3D4, // square antiprim??
        SP3D5 // tricapped trigonal prism
    }

    /**
     * Sets the if attribute of the AtomType object.
     *
     * @param  identifier  The new AtomTypeID value. Null if unset.
     * @see    #getAtomTypeName
     */
    void setAtomTypeName(String identifier);

    /**
     * Sets the MaxBondOrder attribute of the AtomType object.
     *
     * @param  maxBondOrder  The new MaxBondOrder value
     * @see    #getMaxBondOrder
     */
    void setMaxBondOrder(IBond.Order maxBondOrder);

    /**
     * Sets the the exact bond order sum attribute of the AtomType object.
     *
     * @param  bondOrderSum  The new bondOrderSum value
     * @see    #getBondOrderSum
     */
    void setBondOrderSum(Double bondOrderSum);

    /**
     * Gets the id attribute of the AtomType object.
     *
     * @return    The id value
     * @see       #setAtomTypeName
     */
    String getAtomTypeName();

    /**
     * Gets the MaxBondOrder attribute of the AtomType object.
     *
     * @return    The MaxBondOrder value
     * @see       #setMaxBondOrder
     */
    IBond.Order getMaxBondOrder();

    /**
     * Gets the bondOrderSum attribute of the AtomType object.
     *
     * @return    The bondOrderSum value
     * @see       #setBondOrderSum
     */
    Double getBondOrderSum();

    /**
     * Sets the formal charge of this atom.
     *
     * @param  charge  The formal charge
     * @see    #getFormalCharge
     */
    void setFormalCharge(Integer charge);

    /**
     * Returns the formal charge of this atom.
     *
     * @return the formal charge of this atom
     * @see    #setFormalCharge
     */
    Integer getFormalCharge();

    /**
     * Sets the formal neighbour count of this atom.
     *
     * @param  count  The neighbour count
     * @see    #getFormalNeighbourCount
     */
    void setFormalNeighbourCount(Integer count);

    /**
     * Returns the formal neighbour count of this atom.
     *
     * @return the formal neighbour count of this atom
     * @see    #setFormalNeighbourCount
     */
    Integer getFormalNeighbourCount();

    /**
     * Sets the hybridization of this atom.
     *
     * @param  hybridization  The hybridization
     * @see    #getHybridization
     */
    void setHybridization(Hybridization hybridization);

    /**
     * Returns the hybridization of this atom.
     *
     * @return the hybridization of this atom
     * @see    #setHybridization
     */
    Hybridization getHybridization();

    /**
     * Sets the covalent radius for this AtomType.
     *
     * @param radius The covalent radius for this AtomType
     * @see    #getCovalentRadius
     */
    void setCovalentRadius(Double radius);

    /**
     * Returns the covalent radius for this AtomType.
     *
     * @return The covalent radius for this AtomType
     * @see    #setCovalentRadius
     */
    Double getCovalentRadius();

    /**
     * Sets the the exact electron valency of the AtomType object.
     *
     * @param  valency  The new valency value
     * @see    #getValency()
     */
    void setValency(Integer valency);

    /**
     * Gets the the exact electron valency of the AtomType object.
     *
     * @return The valency value
     * @see    #setValency(Integer)
     */
    Integer getValency();

}
