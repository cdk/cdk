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

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
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
@TestClass("org.openscience.cdk.geometry.cip.ImmutableHydrogenTest")
class ImmutableHydrogen implements IAtom {

    private static final String SYMBOL = "H";

    /** {@inheritDoc}} */
    @TestMethod("testClone")
    public Object clone() {
        return this;
    }
    
    /**
     * This field is not used by this immutable hydrogen.
     *
     * @return null.
     */
    @TestMethod("testReturnsNull")
    public Double getCharge() {
        return null;
    }

    /**
     * This field is not used by this immutable hydrogen.
     *
     * @return null.
     */
    @TestMethod("testReturnsNull")
    public Point3d getFractionalPoint3d() {
        return null;
    }

    /**
     * This field is not used by this immutable hydrogen.
     *
     * @return null.
     */
    @TestMethod("testReturnsNull")
    public Integer getImplicitHydrogenCount() {
        return null;
    }

    /**
     * This field is not used by this immutable hydrogen.
     *
     * @return null.
     */
    @TestMethod("testReturnsNull")
    public Point2d getPoint2d() {
        return null;
    }

    /**
     * This field is not used by this immutable hydrogen.
     *
     * @return null.
     */
    @TestMethod("testReturnsNull")
    public Point3d getPoint3d() {
        return null;
    }

    /**
     * This field is not used by this immutable hydrogen.
     *
     * @return null.
     */
    @TestMethod("testReturnsNull")
    public Integer getStereoParity() {
        return null;
    }

    /**
     * This field is not used by this immutable hydrogen.
     * Any setting will have no effect what so ever.
     *
     * @param charge The value will be disregarded.
     */
    @TestMethod("testSetIsSilent")
    public void setCharge(Double charge) {}

    /**
     * This field is not used by this immutable hydrogen.
     * Any setting will have no effect what so ever.
     * 
     * @param point3d The value will be disregarded.
     */
    @TestMethod("testSetIsSilent")
    public void setFractionalPoint3d(Point3d point3d) {}

    /**
     * This field is not used by this immutable hydrogen.
     * Any setting will have no effect what so ever.
     *
     * @param hydrogenCount The value will be disregarded.
     */
    @TestMethod("testSetIsSilent")
    public void setImplicitHydrogenCount(Integer hydrogenCount) {}

    /**
     * This field is not used by this immutable hydrogen.
     * Any setting will have no effect what so ever.
     * 
     * @param point2d The value will be disregarded.
     */
    @TestMethod("testSetIsSilent")
    public void setPoint2d(Point2d point2d) {}

    /**
     * This field is not used by this immutable hydrogen.
     * Any setting will have no effect what so ever.
     * 
     * @param point3d The value will be disregarded.
     */
    @TestMethod("testSetIsSilent")
    public void setPoint3d(Point3d point3d) {}

    /**
     * This field is not used by this immutable hydrogen.
     * Any setting will have no effect what so ever.
     * 
     * @param stereoParity The value will be disregarded.
     */
    @TestMethod("testSetIsSilent")
    public void setStereoParity(Integer stereoParity) {}

    /**
     * This field is not used by this immutable hydrogen.
     *
     * @return null.
     */
    @TestMethod("testReturnsNull")
    public String getAtomTypeName() {
        return null;
    }

    /**
     * This field is not used by this immutable hydrogen.
     *
     * @return null.
     */
    @TestMethod("testReturnsNull")
    public Double getBondOrderSum() {
        return null;
    }

    /**
     * This field is not used by this immutable hydrogen.
     *
     * @return null.
     */
    @TestMethod("testReturnsNull")
    public Double getCovalentRadius() {
        return null;
    }

    /**
     * This field is not used by this immutable hydrogen.
     *
     * @return null.
     */
    @TestMethod("testReturnsNull")
    public Integer getFormalCharge() {
        return null;
    }

    /**
     * This field is not used by this immutable hydrogen.
     *
     * @return null.
     */
    @TestMethod("testReturnsNull")
    public Integer getFormalNeighbourCount() {
        return null;
    }

    /**
     * This field is not used by this immutable hydrogen.
     *
     * @return null.
     */
    @TestMethod("testReturnsNull")
    public Hybridization getHybridization() {
        return null;
    }

    /**
     * This field is not used by this immutable hydrogen.
     *
     * @return null.
     */
    @TestMethod("testReturnsNull")
    public Order getMaxBondOrder() {
        return null;
    }

    /**
     * This field is not used by this immutable hydrogen.
     *
     * @return null.
     */
    @TestMethod("testReturnsNull")
    public Integer getValency() {
        return null;
    }

    /**
     * This field is not used by this immutable hydrogen.
     * Any setting will have no effect what so ever.
     * 
     * @param identifier The value will be disregarded.
     */
    @TestMethod("testSetIsSilent")
    public void setAtomTypeName(String identifier) {}

    /**
     * This field is not used by this immutable hydrogen.
     * Any setting will have no effect what so ever.
     * 
     * @param bondOrderSum The value will be disregarded.
     */
    @TestMethod("testSetIsSilent")
    public void setBondOrderSum(Double bondOrderSum) {}

    /**
     * This field is not used by this immutable hydrogen.
     * Any setting will have no effect what so ever.
     * 
     * @param radius The value will be disregarded.
     */
    @TestMethod("testSetIsSilent")
    public void setCovalentRadius(Double radius) {}

    /**
     * This field is not used by this immutable hydrogen.
     * Any setting will have no effect what so ever.
     * 
     * @param charge The value will be disregarded.
     */
    @TestMethod("testSetIsSilent")
    public void setFormalCharge(Integer charge) {}

    /**
     * This field is not used by this immutable hydrogen.
     * Any setting will have no effect what so ever.
     * 
     * @param count The value will be disregarded.
     */
    @TestMethod("testSetIsSilent")
    public void setFormalNeighbourCount(Integer count) {}

    /**
     * This field is not used by this immutable hydrogen.
     * Any setting will have no effect what so ever.
     * 
     * @param hybridization The value will be disregarded.
     */
    @TestMethod("testSetIsSilent")
    public void setHybridization(Hybridization hybridization) {}

    /**
     * This field is not used by this immutable hydrogen.
     * Any setting will have no effect what so ever.
     * 
     * @param maxBondOrder The value will be disregarded.
     */
    @TestMethod("testSetIsSilent")
    public void setMaxBondOrder(Order maxBondOrder) {}

    /**
     * This field is not used by this immutable hydrogen.
     * Any setting will have no effect what so ever.
     * 
     * @param valency The value will be disregarded.
     */
    @TestMethod("testSetIsSilent")
    public void setValency(Integer valency) {}

    /**
     * This field is not used by this immutable hydrogen.
     *
     * @return null.
     */
    @TestMethod("testReturnsNull")
    public Double getExactMass() {
        return null;
    }

    /**
     * Returns the immutable mass number of hydrogen.
     *
     * @return 1.
     */
    @TestMethod("testExpectedValues")
    public Integer getMassNumber() {
        return Integer.valueOf(1);
    }

    /**
     * This field is not used by this immutable hydrogen.
     *
     * @return null.
     */
    @TestMethod("testReturnsNull")
    public Double getNaturalAbundance() {
        return null;
    }

    /**
     * This field is not used by this immutable hydrogen.
     * Any setting will have no effect what so ever.
     * 
     * @param exactMass The value will be disregarded.
     */
    @TestMethod("testSetIsSilent")
    public void setExactMass(Double exactMass) {}

    /**
     * This field is not used by this immutable hydrogen.
     * Any setting will have no effect what so ever.
     * 
     * @param massNumber The value will be disregarded.
     */
    @TestMethod("testSetIsSilent")
    public void setMassNumber(Integer massNumber) {}

    /**
     * This field is not used by this immutable hydrogen.
     * Any setting will have no effect what so ever.
     * 
     * @param naturalAbundance The value will be disregarded.
     */
    @TestMethod("testSetIsSilent")
    public void setNaturalAbundance(Double naturalAbundance) {}

    /**
     * Returns the atomic number of hydrogen.
     * 
     * @return 1.
     */
    @TestMethod("testExpectedValues")
    public Integer getAtomicNumber() {
        return Integer.valueOf(1);
    }

    /**
     * Returns the symbol of the hydrogen element.
     * 
     * @return "H".
     */
    @TestMethod("testExpectedValues")
    public String getSymbol() {
        return SYMBOL;
    }

    /** Using this method on this immutable object has no effect.
     * 
     * @param atomicNumber An integer that will just be disregarded.
     */
    @TestMethod("testOverwriteStaticValues")
    public void setAtomicNumber(Integer atomicNumber) {}

    /** Using this method on this immutable object has no effect.
     * 
     * @param symbol A string that will just be disregarded.
     */
    @TestMethod("testOverwriteStaticValues")
    public void setSymbol(String symbol) {}

    /** {@inheritDoc}} */
    @TestMethod("testListenerStuff")
    public void addListener(IChemObjectListener col) {}

    /**
     * This field is not used by this immutable hydrogen.
     *
     * @param  flagType The value will be disregarded.
     * @return null.
     */
    @TestMethod("testReturnsNull")
    public boolean getFlag(int flagType) {
        return false;
    }

    /**
     * This field is not used by this immutable hydrogen.
     *
     * @return null.
     */
    @TestMethod("testReturnsNull")
    public boolean[] getFlags() {
        return null;
    }


    /**
     * This field is not used by this immutable hydrogen.
     *
     * @return null.
     */
    @Override
    @TestMethod("testReturnsNull")
    public Number getFlagValue() {
        return null;
    }

    /** {@inheritDoc}} */
    /**
     * This field is not used by this immutable hydrogen.
     *
     * @return null.
     */
    @TestMethod("testReturnsNull")
    public String getID() {
        return null;
    }

    /** {@inheritDoc}} */
    @TestMethod("testListenerStuff")
    public int getListenerCount() {
        return 0;
    }

    /** {@inheritDoc}} */
    @TestMethod("testListenerStuff")
    public boolean getNotification() {
        return false;
    }

    /**
     * This field is not used by this immutable hydrogen.
     *
     * @return null.
     */
    @TestMethod("testReturnsNull")
    public Map<Object, Object> getProperties() {
        return null;
    }

    /**
     * This field is not used by this immutable hydrogen.
     *
     * @param  description The value will be disregarded.
     * @return null.
     */
    @TestMethod("testReturnsNull")
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
    @TestMethod("testReturnsNull")
    public <T> T getProperty(Object description, Class<T> c) {
        return null;
    }

    /** {@inheritDoc}} */
    @TestMethod("testListenerStuff")
    public void notifyChanged() {}

    /** {@inheritDoc}} */
    @TestMethod("testListenerStuff")
    public void notifyChanged(IChemObjectChangeEvent evt) {}

    /** {@inheritDoc}} */
    @TestMethod("testListenerStuff")
    public void removeListener(IChemObjectListener col) {}

    /** {@inheritDoc}} */
    @TestMethod("testSetIsSilent")
    public void removeProperty(Object description) {}

    /** {@inheritDoc}} */
    @TestMethod("testSetIsSilent")
    public void setFlag(int flagType, boolean flagValue) {}

    /**
     * This field is not used by this immutable hydrogen.
     * Any setting will have no effect what so ever.
     * 
     * @param flagsNew The value will be disregarded.
     */
    @TestMethod("testSetIsSilent")
    public void setFlags(boolean[] flagsNew) {}

    /**
     * This field is not used by this immutable hydrogen.
     * Any setting will have no effect what so ever.
     * 
     * @param identifier The value will be disregarded.
     */
    @TestMethod("testSetIsSilent")
    public void setID(String identifier) {}

    /** {@inheritDoc}} */
    @TestMethod("testListenerStuff")
    public void setNotification(boolean bool) {}

    /**
     * This field is not used by this immutable hydrogen.
     * Any setting will have no effect what so ever.
     * 
     * @param properties The value will be disregarded.
     */
    @TestMethod("testSetIsSilent")
    public void setProperties(Map<Object, Object> properties) {}

    /**
     * This field is not used by this immutable hydrogen.
     * Any setting will have no effect what so ever.
     * 
     * @param description The value will be disregarded.
     * @param property    The value will be disregarded.
     */
    @TestMethod("testSetIsSilent")
    public void setProperty(Object description, Object property) {}

    /**
     * This field is not used by this immutable hydrogen.
     *
     * @return null.
     */
    @TestMethod("testReturnsNull")
    public IChemObjectBuilder getBuilder() {
        return null;
    }

}
