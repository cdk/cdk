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

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import java.util.Map;

/**
 * Companion class to {@link AtomRef}. A BondRef offers a view of an bond that belongs
 * to an particular {@link IAtomContainer}. The BondRef knows it's index in the container
 * and stores the {@link AtomRef} of each end-point.
 * <br>
 * AtomRefs are created and accessed by an {@link AtomContainerRef}.
 * <pre>
 * {@code
 * IAtomContainer   ac    = ...;
 * AtomContainerRef acref = new AtomContainerRef(ac);
 *
 * BondRef bref = acref.getBond(0);
 * }
 * </pre>
 */
public final class BondRef implements IBond {

    private final IBond   bond;
    private final int     idx;
    private final AtomRef beg, end;

    BondRef(IBond bond, int idx, AtomRef beg, AtomRef end) {
        this.bond = bond;
        this.idx = idx;
        this.beg = beg;
        this.end = end;
    }

    /**
     * The index of the bond in the 'owning' molecule.
     * @return the index
     */
    public int getIndex() {
        return idx;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setElectronCount(Integer electronCount) {
        bond.setElectronCount(electronCount);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IChemObjectBuilder getBuilder() {
        return bond.getBuilder();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getElectronCount() {
        return bond.getElectronCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setProperty(Object description, Object property) {
        bond.setProperty(description, property);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeProperty(Object description) {
        bond.removeProperty(description);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<IAtom> atoms() {
        return FluentIterable.from(bond.atoms()).transform(new Function<IAtom, IAtom>() {
            @Override
            public IAtom apply(IAtom input) {
                return input; //atomCache.get(input);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getAtomCount() {
        return bond.getAtomCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AtomRef getAtom(int position) {
        switch (position) {
            case 0:
                return beg;
            case 1:
                return end;
            default:
                return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AtomRef getConnectedAtom(IAtom atom) {
        if (atom == beg || atom == beg.atom)
            return end;
        else if (atom == end || atom == end.atom)
            return beg;
        throw new IllegalArgumentException("Atom");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T getProperty(Object description) {
        return bond.getProperty(description);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IAtom[] getConnectedAtoms(IAtom atom) {
        // we check for n-way bond on creation
        return new IAtom[]{getConnectedAtom(atom)};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(IAtom atom) {
        return atom == beg || atom == end || atom == beg.atom || atom == end.atom;
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException read-only
     */
    @Override
    public void setAtom(IAtom atom, int position) {
        throw new UnsupportedOperationException("BondRef is read-only");
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException read-only
     */
    @Override
    public void setAtoms(IAtom[] atoms) {
        throw new UnsupportedOperationException("BondRef is read-only");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Order getOrder() {
        return bond.getOrder();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T getProperty(Object description, Class<T> c) {
        return bond.getProperty(description, c);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOrder(Order order) {
        bond.setOrder(order);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stereo getStereo() {
        return bond.getStereo();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Object, Object> getProperties() {
        return bond.getProperties();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getID() {
        return bond.getID();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setStereo(Stereo stereo) {
        bond.setStereo(stereo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Point2d get2DCenter() {
        return bond.get2DCenter();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setID(String identifier) {
        bond.setID(identifier);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Point3d get3DCenter() {
        return bond.get3DCenter();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean compare(Object object) {
        return bond.compare(object);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isConnectedTo(IBond bond) {
        return bond.isConnectedTo(bond);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFlag(int mask, boolean value) {
        bond.setFlag(mask, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAromatic() {
        return bond.isAromatic();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setIsAromatic(boolean arom) {
        bond.setIsAromatic(arom);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getFlag(int mask) {
        return bond.getFlag(mask);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInRing() {
        return bond.isInRing();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setIsInRing(boolean ring) {
        bond.setIsInRing(ring);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setProperties(Map<Object, Object> properties) {
        bond.setProperties(properties);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IBond clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addProperties(Map<Object, Object> properties) {
        bond.addProperties(properties);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFlags(boolean[] newFlags) {
        bond.setFlags(newFlags);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean[] getFlags() {
        return bond.getFlags();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Number getFlagValue() {
        return bond.getFlagValue();
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException not supported
     */
    @Override
    public void addListener(IChemObjectListener col) {
        throw new UnsupportedOperationException("Notifications not supported");
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException not supported
     */
    @Override
    public int getListenerCount() {
        throw new UnsupportedOperationException("Notifications not supported");
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException not supported
     */
    @Override
    public void setNotification(boolean bool) {
        throw new UnsupportedOperationException("Notifications not supported");
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException not supported
     */
    @Override
    public boolean getNotification() {
        throw new UnsupportedOperationException("Notifications not supported");
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException not supported
     */
    @Override
    public void notifyChanged() {
        throw new UnsupportedOperationException("Notifications not supported");
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException not supported
     */
    @Override
    public void removeListener(IChemObjectListener col) {
        throw new UnsupportedOperationException("Notifications not supported");
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException not supported
     */
    @Override
    public void notifyChanged(IChemObjectChangeEvent evt) {
        throw new UnsupportedOperationException("Notifications not supported");
    }
}
