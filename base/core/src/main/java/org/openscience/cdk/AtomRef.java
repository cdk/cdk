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

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import java.util.List;
import java.util.Map;

/**
 * A AtomRef offers a view of an atom that belongs
 * to an particular {@link IAtomContainer}. In addition to the normal methods
 * of an atom this class provides the index ({@link #getIndex()}) in
 * the parent {@link IAtomContainer} and the connected
 * bonds ({@link #bonds()}).
 * <br>
 * Modifications to the atom (e.g. {@link #setAtomicNumber(Integer)} are passed
 * through to the underlying {@link IAtom}.
 * <br>
 * AtomRefs are created and accessed by an {@link AtomContainerRef}.
 * <pre>
 * {@code
 * IAtomContainer   ac    = ...;
 * AtomContainerRef acref = new AtomContainerRef(ac);
 *
 * AtomRef aref = acref.getAtom(0);
 * }
 * </pre>
 *
 * @see AtomContainerRef
 */
public final class AtomRef implements IAtom {

    final         IAtom         atom;
    private final int           idx;
    final         List<BondRef> bonds;

    AtomRef(int idx, IAtom atom, List<BondRef> bonds) {
        this.idx = idx;
        this.atom = atom;
        this.bonds = bonds;
    }

    /**
     * The index of the atoms in the 'owning' {@link IAtomContainer}.
     * @return atom index
     */
    public int getIndex() {
        return idx;
    }

    /**
     * The bonds connected to this atom.
     *
     * @return iterable over the bonds
     */
    public Iterable<BondRef> bonds() {
        return bonds;
    }

    /**
     * The number of bonds connected to this atom (degree).
     * @return connected bond count
     */
    public int getBondCount() {
        return bonds.size();
    }

    /** {@inheritDoc} */
    @Override
    public IChemObjectBuilder getBuilder() {
        return atom.getBuilder();
    }

    /** {@inheritDoc} */
    @Override
    public void setCharge(Double charge) {
        atom.setCharge(charge);
    }

    /** {@inheritDoc} */
    @Override
    public void setNaturalAbundance(Double naturalAbundance) {
        atom.setNaturalAbundance(naturalAbundance);
    }

    /** {@inheritDoc} */
    @Override
    public Double getCharge() {
        return atom.getCharge();
    }

    /** {@inheritDoc} */
    @Override
    public void setExactMass(Double exactMass) {
        atom.setExactMass(exactMass);
    }

    /** {@inheritDoc} */
    @Override
    public Integer getAtomicNumber() {
        return atom.getAtomicNumber();
    }

    /** {@inheritDoc} */
    @Override
    public void setImplicitHydrogenCount(Integer hydrogenCount) {
        atom.setImplicitHydrogenCount(hydrogenCount);
    }

    /** {@inheritDoc} */
    @Override
    public Double getNaturalAbundance() {
        return atom.getNaturalAbundance();
    }

    /** {@inheritDoc} */
    @Override
    public void setAtomicNumber(Integer atomicNumber) {
        atom.setAtomicNumber(atomicNumber);
    }

    /** {@inheritDoc} */
    @Override
    public Integer getImplicitHydrogenCount() {
        return atom.getImplicitHydrogenCount();
    }

    /** {@inheritDoc} */
    @Override
    public Double getExactMass() {
        return atom.getExactMass();
    }

    /** {@inheritDoc} */
    @Override
    public void setMaxBondOrder(IBond.Order maxBondOrder) {
        atom.setMaxBondOrder(maxBondOrder);
    }

    /** {@inheritDoc} */
    @Override
    public String getSymbol() {
        return atom.getSymbol();
    }

    /** {@inheritDoc} */
    @Override
    public void setSymbol(String symbol) {
        atom.setSymbol(symbol);
    }

    /** {@inheritDoc} */
    @Override
    public Integer getMassNumber() {
        return atom.getMassNumber();
    }

    /** {@inheritDoc} */
    @Override
    public Point2d getPoint2d() {
        return atom.getPoint2d();
    }

    /** {@inheritDoc} */
    @Override
    public void setPoint2d(Point2d point2d) {
        atom.setPoint2d(point2d);
    }

    /** {@inheritDoc} */
    @Override
    public Point3d getPoint3d() {
        return atom.getPoint3d();
    }

    /** {@inheritDoc} */
    @Override
    public void setPoint3d(Point3d point3d) {
        atom.setPoint3d(point3d);
    }

    /** {@inheritDoc} */
    @Override
    public void setFractionalPoint3d(Point3d point3d) {
        atom.setFractionalPoint3d(point3d);
    }

    /** {@inheritDoc} */
    @Override
    public Point3d getFractionalPoint3d() {
        return atom.getFractionalPoint3d();
    }

    /** {@inheritDoc} */
    @Override
    public void setBondOrderSum(Double bondOrderSum) {
        atom.setBondOrderSum(bondOrderSum);
    }

    /** {@inheritDoc} */
    @Override
    public void setMassNumber(Integer massNumber) {
        atom.setMassNumber(massNumber);
    }

    /** {@inheritDoc} */
    @Override
    public void setAtomTypeName(String identifier) {
        atom.setAtomTypeName(identifier);
    }

    /** {@inheritDoc} */
    @Override
    public String getAtomTypeName() {
        return atom.getAtomTypeName();
    }

    /** {@inheritDoc} */
    @Override
    public IBond.Order getMaxBondOrder() {
        return atom.getMaxBondOrder();
    }

    /** {@inheritDoc} */
    @Override
    public Double getBondOrderSum() {
        return atom.getBondOrderSum();
    }

    /** {@inheritDoc} */
    @Override
    public Integer getStereoParity() {
        return atom.getStereoParity();
    }

    /** {@inheritDoc} */
    @Override
    public void setStereoParity(Integer stereoParity) {
        atom.setStereoParity(stereoParity);
    }

    /** {@inheritDoc} */
    @Override
    public void setFormalCharge(Integer charge) {
        atom.setFormalCharge(charge);
    }

    /** {@inheritDoc} */
    @Override
    public Integer getFormalCharge() {
        return atom.getFormalCharge();
    }

    /** {@inheritDoc} */
    @Override
    public void setProperty(Object description, Object property) {
        atom.setProperty(description, getFractionalPoint3d());
    }

    /** {@inheritDoc} */
    @Override
    public void setFormalNeighbourCount(Integer count) {
        atom.setFormalNeighbourCount(count);
    }

    /** {@inheritDoc} */
    @Override
    public Integer getFormalNeighbourCount() {
        return atom.getFormalNeighbourCount();
    }

    /** {@inheritDoc} */
    @Override
    public void removeProperty(Object description) {
        atom.removeProperty(description);
    }

    /** {@inheritDoc} */
    @Override
    public void setHybridization(Hybridization hybridization) {
        atom.setHybridization(hybridization);
    }

    /** {@inheritDoc} */
    @Override
    public Hybridization getHybridization() {
        return atom.getHybridization();
    }

    /** {@inheritDoc} */
    @Override
    public void setCovalentRadius(Double radius) {
        atom.setCovalentRadius(radius);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isAromatic() {
        return atom.isAromatic();
    }

    /** {@inheritDoc} */
    @Override
    public void setIsAromatic(boolean arom) {
        atom.setIsAromatic(arom);
    }

    /** {@inheritDoc} */
    @Override
    public Double getCovalentRadius() {
        return atom.getCovalentRadius();
    }

    /** {@inheritDoc} */
    @Override
    public void setValency(Integer valency) {
        atom.setValency(valency);
    }

    /** {@inheritDoc} */
    @Override
    public Integer getValency() {
        return atom.getValency();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isInRing() {
        return atom.isInRing();
    }

    /** {@inheritDoc} */
    @Override
    public void setIsInRing(boolean ring) {
        atom.setIsInRing(ring);
    }

    /**
     * <b>Not supported</b>
     * {@inheritDoc}
     * @throws CloneNotSupportedException
     */
    @Override
    public IAtom clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    /** {@inheritDoc} */
    @Override
    public <T> T getProperty(Object description) {
        return atom.getProperty(description);
    }

    /** {@inheritDoc} */
    @Override
    public <T> T getProperty(Object description, Class<T> c) {
        return atom.getProperty(description, c);
    }

    /** {@inheritDoc} */
    @Override
    public Map<Object, Object> getProperties() {
        return atom.getProperties();
    }

    /** {@inheritDoc} */
    @Override
    public String getID() {
        return atom.getID();
    }

    /** {@inheritDoc} */
    @Override
    public void setID(String identifier) {
        atom.setID(identifier);
    }

    /** {@inheritDoc} */
    @Override
    public void setFlag(int mask, boolean value) {
        atom.setFlag(mask, value);
    }

    /** {@inheritDoc} */
    @Override
    public boolean getFlag(int mask) {
        return atom.getFlag(mask);
    }

    /** {@inheritDoc} */
    @Override
    public void setProperties(Map<Object, Object> properties) {
        atom.setProperties(properties);
    }

    /** {@inheritDoc} */
    @Override
    public void addProperties(Map<Object, Object> properties) {
        atom.setProperties(properties);
    }

    /** {@inheritDoc} */
    @Override
    public void setFlags(boolean[] newFlags) {
        atom.setFlags(newFlags);
    }

    /** {@inheritDoc} */
    @Override
    public boolean[] getFlags() {
        return atom.getFlags();
    }

    /** {@inheritDoc} */
    @Override
    public Number getFlagValue() {
        return atom.getFlagValue();
    }

    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException not supported
     */
    @Override
    public void addListener(IChemObjectListener col) {
        throw new UnsupportedOperationException("Notifications not supported");
    }

    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException not supported
     */
    @Override
    public int getListenerCount() {
        throw new UnsupportedOperationException("Notifications not supported");
    }

    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException not supported
     */
    @Override
    public void setNotification(boolean bool) {
        throw new UnsupportedOperationException("Notifications not supported");
    }

    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException not supported
     */
    @Override
    public boolean getNotification() {
        throw new UnsupportedOperationException("Notifications not supported");
    }

    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException not supported
     */
    @Override
    public void notifyChanged() {
        throw new UnsupportedOperationException("Notifications not supported");
    }

    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException not supported
     */
    @Override
    public void removeListener(IChemObjectListener col) {
        throw new UnsupportedOperationException("Notifications not supported");
    }

    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException not supported
     */
    @Override
    public void notifyChanged(IChemObjectChangeEvent evt) {
        throw new UnsupportedOperationException("Notifications not supported");
    }
}
