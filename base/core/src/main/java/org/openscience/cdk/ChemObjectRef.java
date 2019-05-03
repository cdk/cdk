/*
 * Copyright (c) 2017 John Mayfield <jwmay@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version. All we ask is that proper credit is given
 * for our work, which includes - but is not limited to - adding the above
 * copyright notice to the beginning of your source code files, and to any
 * copyright notice that you may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package org.openscience.cdk;

import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;

import java.util.Map;

class ChemObjectRef implements IChemObject {

    private final IChemObject chemobj;

    ChemObjectRef(IChemObject chemobj) {
        if (chemobj == null)
            throw new NullPointerException("Proxy object can not be null!");
        this.chemobj = chemobj;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IChemObjectBuilder getBuilder() {
        return chemobj.getBuilder();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addListener(IChemObjectListener col) {
        chemobj.addListener(col);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getListenerCount() {
        return chemobj.getListenerCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeListener(IChemObjectListener col) {
        chemobj.removeListener(col);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNotification(boolean bool) {
        chemobj.setNotification(bool);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getNotification() {
        return chemobj.getNotification();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyChanged() {
        chemobj.notifyChanged();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyChanged(IChemObjectChangeEvent evt) {
        chemobj.notifyChanged(evt);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setProperty(Object description, Object property) {
        chemobj.setProperty(description, property);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeProperty(Object description) {
        chemobj.removeProperty(description);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T getProperty(Object description) {
        return chemobj.getProperty(description);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T getProperty(Object description, Class<T> c) {
        return chemobj.getProperty(description, c);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Object, Object> getProperties() {
        return chemobj.getProperties();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getID() {
        return chemobj.getID();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setID(String identifier) {
        chemobj.setID(identifier);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFlag(int mask, boolean value) {
        chemobj.setFlag(mask, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getFlag(int mask) {
        return chemobj.getFlag(mask);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setProperties(Map<Object, Object> properties) {
        chemobj.setProperties(properties);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addProperties(Map<Object, Object> properties) {
        chemobj.addProperties(properties);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFlags(boolean[] newFlags) {
        chemobj.setFlags(newFlags);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean[] getFlags() {
        return chemobj.getFlags();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Number getFlagValue() {
        return chemobj.getFlagValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return new ChemObjectRef((IChemObject) chemobj.clone());
    }
}
