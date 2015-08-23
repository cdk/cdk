/* Copyright (C) 2012-2013  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.config;

import java.util.Map;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;
import org.openscience.cdk.interfaces.IIsotope;

/**
 * A read-only class used by {@link Isotopes} for the natural elements. This class is not to
 * be used than by only {@link Isotopes}.
 *
 * @author      egonw
 * @cdk.module  core
 * @cdk.githash
 */
final class BODRIsotope implements IIsotope {

    private String  element;
    private Integer atomicNumber;
    private Double  naturalAbundance;
    private Double  exactMass;
    private Integer massNumber;

    protected BODRIsotope(String element, Integer atomicNumber, Integer massNumber, Double exactMass,
            Double naturalAbundance) {
        this.element = element;
        this.atomicNumber = atomicNumber;
        this.massNumber = massNumber;
        this.naturalAbundance = naturalAbundance;
        this.exactMass = exactMass;
    }

    // ignored methods

    @Override
    public void addListener(IChemObjectListener col) {}

    @Override
    public int getListenerCount() {
        return 0;
    }

    @Override
    public void removeListener(IChemObjectListener col) {}

    @Override
    public void setNotification(boolean bool) {}

    @Override
    public boolean getNotification() {
        return false;
    }

    @Override
    public void notifyChanged() {}

    @Override
    public void notifyChanged(IChemObjectChangeEvent evt) {}

    // unsupported methods

    @Override
    public Number getFlagValue() {
        return (short) 0;
    }

    @Override
    public void setProperty(Object description, Object property) {}

    @Override
    public void removeProperty(Object description) {}

    @Override
    public <T> T getProperty(Object description) {
        return null;
    }

    @Override
    public <T> T getProperty(Object description, Class<T> c) {
        return null;
    }

    @Override
    public Map<Object, Object> getProperties() {
        return null;
    }

    @Override
    public String getID() {
        return null;
    }

    @Override
    public void setID(String identifier) {}

    @Override
    public void setFlag(int flagType, boolean flagValue) {}

    @Override
    public boolean getFlag(int flagType) {
        return false;
    }

    @Override
    public void setProperties(Map<Object, Object> properties) {}

    @Override
    public void addProperties(Map<Object, Object> properties) {}

    @Override
    public void setFlags(boolean[] flagsNew) {}

    @Override
    public boolean[] getFlags() {
        return new boolean[CDKConstants.MAX_FLAG_INDEX + 1];
    }

    @Override
    public IChemObjectBuilder getBuilder() {
        return null;
    }

    @Override
    public void setAtomicNumber(Integer atomicNumber) {}

    @Override
    public void setSymbol(String symbol) {}

    @Override
    public void setExactMass(Double exactMass) {}

    @Override
    public void setNaturalAbundance(Double naturalAbundance) {}

    @Override
    public void setMassNumber(Integer massNumber) {}

    // implemented methods

    @Override
    public String getSymbol() {
        return element;
    }

    @Override
    public Integer getAtomicNumber() {
        return atomicNumber;
    }

    @Override
    public Double getNaturalAbundance() {
        return naturalAbundance;
    }

    @Override
    public Double getExactMass() {
        return exactMass;
    };

    @Override
    public Integer getMassNumber() {
        return massNumber;
    }

    @Override
    public Object clone() {
        return this;
    }

}
