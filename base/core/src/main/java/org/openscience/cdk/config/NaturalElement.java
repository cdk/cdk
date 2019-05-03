/* Copyright (C) 2012  Egon Willighagen <egonw@users.sf.net>
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
import org.openscience.cdk.interfaces.IElement;

/**
 * A read-only class used by {@link Elements} for the natural elements. This class is not to
 * be used than by only {@link Elements}.
 *
 * @author      egonw
 * @cdk.module  core
 * @cdk.githash
 */
final class NaturalElement implements IElement {

    private final String  element;
    private final Integer atomicNumber;

    protected NaturalElement(String element, Integer atomicNumber) {
        this.element = element;
        this.atomicNumber = atomicNumber;
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
    public Object clone() {
        return this;
    }
}
