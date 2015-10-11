/* Copyright (C) 2010  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
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
package org.openscience.cdk.geometry.cip;

import java.util.Map;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;
import org.openscience.cdk.interfaces.IBond.Order;

/**
 * Helper class to represent a immutable hydrogen. All set methods are void, and only
 * {@link #getSymbol()}, {@link #getAtomicNumber()}, and {@link #getMassNumber()} are
 * implemented.
 *
 * @author egonw
 * @cdk.module cip
 * @cdk.githash
 */
class ImmutableHydrogen implements IAtom {

    private static final String SYMBOL = "H";

    /** {@inheritDoc} */
    @Override
    public IAtom clone() {
        return this;
    }

    /**
     * This field is not used by this immutable hydrogen.
     *
     * @return null.
     */
    @Override
    public Double getCharge() {
        return null;
    }

    /**
     * This field is not used by this immutable hydrogen.
     *
     * @return null.
     */
    @Override
    public Point3d getFractionalPoint3d() {
        return null;
    }

    /**
     * This field is not used by this immutable hydrogen.
     *
     * @return null.
     */
    @Override
    public Integer getImplicitHydrogenCount() {
        return null;
    }

    /**
     * This field is not used by this immutable hydrogen.
     *
     * @return null.
     */
    @Override
    public Point2d getPoint2d() {
        return null;
    }

    /**
     * This field is not used by this immutable hydrogen.
     *
     * @return null.
     */
    @Override
    public Point3d getPoint3d() {
        return null;
    }

    /**
     * This field is not used by this immutable hydrogen.
     *
     * @return null.
     */
    @Override
    public Integer getStereoParity() {
        return null;
    }

    /**
     * This field is not used by this immutable hydrogen.
     * Any setting will have no effect what so ever.
     *
     * @param charge The value will be disregarded.
     */
    @Override
    public void setCharge(Double charge) {}

    /**
     * This field is not used by this immutable hydrogen.
     * Any setting will have no effect what so ever.
     *
     * @param point3d The value will be disregarded.
     */
    @Override
    public void setFractionalPoint3d(Point3d point3d) {}

    /**
     * This field is not used by this immutable hydrogen.
     * Any setting will have no effect what so ever.
     *
     * @param hydrogenCount The value will be disregarded.
     */
    @Override
    public void setImplicitHydrogenCount(Integer hydrogenCount) {}

    /**
     * This field is not used by this immutable hydrogen.
     * Any setting will have no effect what so ever.
     *
     * @param point2d The value will be disregarded.
     */
    @Override
    public void setPoint2d(Point2d point2d) {}

    /**
     * This field is not used by this immutable hydrogen.
     * Any setting will have no effect what so ever.
     *
     * @param point3d The value will be disregarded.
     */
    @Override
    public void setPoint3d(Point3d point3d) {}

    /**
     * This field is not used by this immutable hydrogen.
     * Any setting will have no effect what so ever.
     *
     * @param stereoParity The value will be disregarded.
     */
    @Override
    public void setStereoParity(Integer stereoParity) {}

    /**
     * This field is not used by this immutable hydrogen.
     *
     * @return null.
     */
    @Override
    public String getAtomTypeName() {
        return null;
    }

    /**
     * This field is not used by this immutable hydrogen.
     *
     * @return null.
     */
    @Override
    public Double getBondOrderSum() {
        return null;
    }

    /**
     * This field is not used by this immutable hydrogen.
     *
     * @return null.
     */
    @Override
    public Double getCovalentRadius() {
        return null;
    }

    /**
     * This field is not used by this immutable hydrogen.
     *
     * @return null.
     */
    @Override
    public Integer getFormalCharge() {
        return null;
    }

    /**
     * This field is not used by this immutable hydrogen.
     *
     * @return null.
     */
    @Override
    public Integer getFormalNeighbourCount() {
        return null;
    }

    /**
     * This field is not used by this immutable hydrogen.
     *
     * @return null.
     */
    @Override
    public Hybridization getHybridization() {
        return null;
    }

    /**
     * This field is not used by this immutable hydrogen.
     *
     * @return null.
     */
    @Override
    public Order getMaxBondOrder() {
        return null;
    }

    /**
     * This field is not used by this immutable hydrogen.
     *
     * @return null.
     */
    @Override
    public Integer getValency() {
        return null;
    }

    /**
     * This field is not used by this immutable hydrogen.
     * Any setting will have no effect what so ever.
     *
     * @param identifier The value will be disregarded.
     */
    @Override
    public void setAtomTypeName(String identifier) {}

    /**
     * This field is not used by this immutable hydrogen.
     * Any setting will have no effect what so ever.
     *
     * @param bondOrderSum The value will be disregarded.
     */
    @Override
    public void setBondOrderSum(Double bondOrderSum) {}

    /**
     * This field is not used by this immutable hydrogen.
     * Any setting will have no effect what so ever.
     *
     * @param radius The value will be disregarded.
     */
    @Override
    public void setCovalentRadius(Double radius) {}

    /**
     * This field is not used by this immutable hydrogen.
     * Any setting will have no effect what so ever.
     *
     * @param charge The value will be disregarded.
     */
    @Override
    public void setFormalCharge(Integer charge) {}

    /**
     * This field is not used by this immutable hydrogen.
     * Any setting will have no effect what so ever.
     *
     * @param count The value will be disregarded.
     */
    @Override
    public void setFormalNeighbourCount(Integer count) {}

    /**
     * This field is not used by this immutable hydrogen.
     * Any setting will have no effect what so ever.
     *
     * @param hybridization The value will be disregarded.
     */
    @Override
    public void setHybridization(Hybridization hybridization) {}

    /**
     * This field is not used by this immutable hydrogen.
     * Any setting will have no effect what so ever.
     *
     * @param maxBondOrder The value will be disregarded.
     */
    @Override
    public void setMaxBondOrder(Order maxBondOrder) {}

    /**
     * This field is not used by this immutable hydrogen.
     * Any setting will have no effect what so ever.
     *
     * @param valency The value will be disregarded.
     */
    @Override
    public void setValency(Integer valency) {}

    /**
     * This field is not used by this immutable hydrogen.
     *
     * @return null.
     */
    @Override
    public Double getExactMass() {
        return null;
    }

    /**
     * Returns the immutable mass number of hydrogen.
     *
     * @return 1.
     */
    @Override
    public Integer getMassNumber() {
        return Integer.valueOf(1);
    }

    /**
     * This field is not used by this immutable hydrogen.
     *
     * @return null.
     */
    @Override
    public Double getNaturalAbundance() {
        return null;
    }

    /**
     * This field is not used by this immutable hydrogen.
     * Any setting will have no effect what so ever.
     *
     * @param exactMass The value will be disregarded.
     */
    @Override
    public void setExactMass(Double exactMass) {}

    /**
     * This field is not used by this immutable hydrogen.
     * Any setting will have no effect what so ever.
     *
     * @param massNumber The value will be disregarded.
     */
    @Override
    public void setMassNumber(Integer massNumber) {}

    /**
     * This field is not used by this immutable hydrogen.
     * Any setting will have no effect what so ever.
     *
     * @param naturalAbundance The value will be disregarded.
     */
    @Override
    public void setNaturalAbundance(Double naturalAbundance) {}

    /**
     * Returns the atomic number of hydrogen.
     *
     * @return 1.
     */
    @Override
    public Integer getAtomicNumber() {
        return Integer.valueOf(1);
    }

    /**
     * Returns the symbol of the hydrogen element.
     *
     * @return "H".
     */
    @Override
    public String getSymbol() {
        return SYMBOL;
    }

    /** Using this method on this immutable object has no effect.
     *
     * @param atomicNumber An integer that will just be disregarded.
     */
    @Override
    public void setAtomicNumber(Integer atomicNumber) {}

    /** Using this method on this immutable object has no effect.
     *
     * @param symbol A string that will just be disregarded.
     */
    @Override
    public void setSymbol(String symbol) {}

    /** {@inheritDoc} */
    @Override
    public void addListener(IChemObjectListener col) {}

    /**
     * This field is not used by this immutable hydrogen.
     *
     * @param  flagType The value will be disregarded.
     * @return null.
     */
    @Override
    public boolean getFlag(int flagType) {
        return false;
    }

    /**
     * This field is not used by this immutable hydrogen.
     *
     * @return null.
     */
    @Override
    public boolean[] getFlags() {
        return null;
    }

    /**
     * This field is not used by this immutable hydrogen.
     *
     * @return null.
     */
    @Override
    public Number getFlagValue() {
        return null;
    }

    /** {@inheritDoc} */
    /**
     * This field is not used by this immutable hydrogen.
     *
     * @return null.
     */
    @Override
    public String getID() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public int getListenerCount() {
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public boolean getNotification() {
        return false;
    }

    /**
     * This field is not used by this immutable hydrogen.
     *
     * @return null.
     */
    @Override
    public Map<Object, Object> getProperties() {
        return null;
    }

    /**
     * This field is not used by this immutable hydrogen.
     *
     * @param  description The value will be disregarded.
     * @return null.
     */
    @Override
    public Object getProperty(Object description) {
        return null;
    }

    /**
     * This field is not used by this immutable hydrogen.
     *
     * @param  description The value will be disregarded.
     * @return null.
     */
    @Override
    public <T> T getProperty(Object description, Class<T> c) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void notifyChanged() {}

    /** {@inheritDoc} */
    @Override
    public void notifyChanged(IChemObjectChangeEvent evt) {}

    /** {@inheritDoc} */
    @Override
    public void removeListener(IChemObjectListener col) {}

    /** {@inheritDoc} */
    @Override
    public void removeProperty(Object description) {}

    /** {@inheritDoc} */
    @Override
    public void setFlag(int flagType, boolean flagValue) {}

    /**
     * This field is not used by this immutable hydrogen.
     * Any setting will have no effect what so ever.
     *
     * @param flagsNew The value will be disregarded.
     */
    @Override
    public void setFlags(boolean[] flagsNew) {}

    /**
     * This field is not used by this immutable hydrogen.
     * Any setting will have no effect what so ever.
     *
     * @param identifier The value will be disregarded.
     */
    @Override
    public void setID(String identifier) {}

    /** {@inheritDoc} */
    @Override
    public void setNotification(boolean bool) {}

    /**
     * This field is not used by this immutable hydrogen.
     * Any setting will have no effect what so ever.
     *
     * @param properties The value will be disregarded.
     */
    @Override
    public void setProperties(Map<Object, Object> properties) {}

    /**
     * This field is not used by this immutable hydrogen.
     * Any setting will have no effect what so ever.
     *
     * @param properties The value will be disregarded.
     */
    @Override
    public void addProperties(Map<Object, Object> properties) {}

    /**
     * This field is not used by this immutable hydrogen.
     * Any setting will have no effect what so ever.
     *
     * @param description The value will be disregarded.
     * @param property    The value will be disregarded.
     */
    @Override
    public void setProperty(Object description, Object property) {}

    /**
     * This field is not used by this immutable hydrogen.
     *
     * @return null.
     */
    @Override
    public IChemObjectBuilder getBuilder() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isAromatic() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void setIsAromatic(boolean arom) {
    }

    /** {@inheritDoc} */
    @Override
    public boolean isInRing() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void setIsInRing(boolean ring) {
    }

}
