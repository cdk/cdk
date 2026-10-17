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

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;
import org.openscience.cdk.interfaces.IIsotope;

import java.util.Collections;
import java.util.Map;

/**
 * A read-only class used by {@link Isotopes} for the natural elements.
 * This class is not to be used than by only {@link Isotopes}.
 *
 * @author egonw
 */
final class ImmutableIsotope implements IIsotope {

    private final int    atomicNumber;
    private final int    massNumber;
    private final double naturalAbundance;
    private final double exactMass;

    ImmutableIsotope(int atomicNumber,
                     int massNumber,
                     double exactMass,
                     double naturalAbundance) {
        this.atomicNumber = atomicNumber;
        this.massNumber = massNumber;
        this.naturalAbundance = naturalAbundance;
        this.exactMass = exactMass;
    }

    @Override
    public void addListener(IChemObjectListener col) {
        throw new UnsupportedOperationException("Isotope is read-only");
    }

    @Override
    public int getListenerCount() {
        return 0;
    }

    @Override
    public void removeListener(IChemObjectListener col) {
        throw new UnsupportedOperationException("Isotope is read-only");
    }

    @Override
    public void setNotification(boolean bool) {
        throw new UnsupportedOperationException("Isotope is read-only");
    }

    @Override
    public boolean getNotification() {
        return false;
    }

    @Override
    public void notifyChanged() {
        // ignore any notify
    }

    @Override
    public void notifyChanged(IChemObjectChangeEvent evt) {
        throw new UnsupportedOperationException("Isotope is read-only");
    }

    @Override
    public Number getFlagValue() {
        return (short) 0;
    }

    @Override
    public void setProperty(Object description, Object property) {
        throw new UnsupportedOperationException("Isotope is read-only");
    }

    @Override
    public void removeProperty(Object description) {
        throw new UnsupportedOperationException("Isotope is read-only");
    }

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
        return Collections.emptyMap();
    }

    @Override
    public String getID() {
        return null;
    }

    @Override
    public void setID(String identifier) {
        throw new UnsupportedOperationException("Isotope is read-only");
    }

    @Override
    public void setFlag(int flagType, boolean flagValue) {
        throw new UnsupportedOperationException("Isotope is read-only");
    }

    @Override
    public boolean getFlag(int flagType) {
        return false;
    }

    @Override
    public void setProperties(Map<Object, Object> properties) {
        throw new UnsupportedOperationException("Isotope is read-only");
    }

    @Override
    public void addProperties(Map<Object, Object> properties) {
        throw new UnsupportedOperationException("Isotope is read-only");
    }

    @Override
    public void setFlags(boolean[] flagsNew) {
        throw new UnsupportedOperationException("Isotope is read-only");
    }

    @Override
    public boolean[] getFlags() {
        return new boolean[CDKConstants.MAX_FLAG_INDEX + 1];
    }

    @Override
    public void set(int flags) {
        throw new UnsupportedOperationException("Isotope is read-only");
    }

    @Override
    public void clear(int flags) {
        throw new UnsupportedOperationException("Isotope is read-only");
    }

    @Override
    public boolean is(int flags) {
        return false;
    }

    @Override
    public int flags() {
        return 0;
    }

    @Override
    public IChemObjectBuilder getBuilder() {
        return null;
    }

    @Override
    public void setAtomicNumber(Integer atomicNumber) {
        throw new UnsupportedOperationException("Isotope is read-only");
    }

    @Override
    public void setSymbol(String symbol) {
        throw new UnsupportedOperationException("Isotope is read-only");
    }

    @Override
    public void setExactMass(Double exactMass) {
        throw new UnsupportedOperationException("Isotope is read-only");
    }

    @Override
    public void setNaturalAbundance(Double naturalAbundance) {
        throw new UnsupportedOperationException("Isotope is read-only");
    }

    @Override
    public void setMassNumber(Integer massNumber) {
        throw new UnsupportedOperationException("Isotope is read-only");
    }

    // implemented methods

    @Override
    public String getSymbol() {
        return Elements.ofNumber(atomicNumber).symbol();
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
    }

    @Override
    public Integer getMassNumber() {
        return massNumber;
    }

    @Override
    public Object clone() {
        return this;
    }

    @Override
    public String toString() {
        return massNumber + "-" + getSymbol() + " isotope [" + exactMass + ", " + naturalAbundance + "%]";
    }
}
