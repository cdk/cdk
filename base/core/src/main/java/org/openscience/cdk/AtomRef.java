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

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

/**
 * An atom ref, references a CDK {@link IAtom} indirectly. All
 * methods are passed through to the referenced atom. The reference can
 * be used to override the behaviour of the base atom.
 *
 * @author John Mayfield
 */
public class AtomRef extends ChemObjectRef implements IAtom {

    private final IAtom atom;

    /**
     * Create a pointer for the provided atom.
     *
     * @param atom the atom to reference
     */
    public AtomRef(IAtom atom) {
        super(atom);
        this.atom = atom;
    }

    /**
     * Utility method to dereference an atom. If the atom is not
     * an {@link AtomRef} it simply returns the input.
     *
     * @param atom the atom
     * @return non-pointer atom
     */
    public static IAtom deref(IAtom atom) {
        while (atom instanceof AtomRef)
            atom = ((AtomRef) atom).deref();
        return atom;
    }

    /**
     * Dereference the atom pointer once providing access to the base
     * atom.
     *
     * @return the atom pointed to
     */
    public IAtom deref() {
        return atom;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Double getCharge() {
        return atom.getCharge();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCharge(Double charge) {
        atom.setCharge(charge);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getAtomicNumber() {
        return atom.getAtomicNumber();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAtomicNumber(Integer atomicNumber) {
        atom.setAtomicNumber(atomicNumber);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Double getNaturalAbundance() {
        return atom.getNaturalAbundance();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNaturalAbundance(Double naturalAbundance) {
        atom.setNaturalAbundance(naturalAbundance);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getImplicitHydrogenCount() {
        return atom.getImplicitHydrogenCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setImplicitHydrogenCount(Integer hydrogenCount) {
        atom.setImplicitHydrogenCount(hydrogenCount);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Double getExactMass() {
        return atom.getExactMass();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setExactMass(Double exactMass) {
        atom.setExactMass(exactMass);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSymbol() {
        return atom.getSymbol();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSymbol(String symbol) {
        atom.setSymbol(symbol);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getMassNumber() {
        return atom.getMassNumber();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMassNumber(Integer massNumber) {
        atom.setMassNumber(massNumber);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAtomTypeName() {
        return atom.getAtomTypeName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAtomTypeName(String identifier) {
        atom.setAtomTypeName(identifier);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IBond.Order getMaxBondOrder() {
        return atom.getMaxBondOrder();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMaxBondOrder(IBond.Order maxBondOrder) {
        atom.setMaxBondOrder(maxBondOrder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Double getBondOrderSum() {
        return atom.getBondOrderSum();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBondOrderSum(Double bondOrderSum) {
        atom.setBondOrderSum(bondOrderSum);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Point2d getPoint2d() {
        return atom.getPoint2d();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPoint2d(Point2d point2d) {
        atom.setPoint2d(point2d);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Point3d getPoint3d() {
        return atom.getPoint3d();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPoint3d(Point3d point3d) {
        atom.setPoint3d(point3d);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getFormalCharge() {
        return atom.getFormalCharge();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFormalCharge(Integer charge) {
        atom.setFormalCharge(charge);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Point3d getFractionalPoint3d() {
        return atom.getFractionalPoint3d();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFractionalPoint3d(Point3d point3d) {
        atom.setFractionalPoint3d(point3d);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getFormalNeighbourCount() {
        return atom.getFormalNeighbourCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFormalNeighbourCount(Integer count) {
        atom.setFormalNeighbourCount(count);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getStereoParity() {
        return atom.getStereoParity();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setStereoParity(Integer stereoParity) {
        atom.setStereoParity(stereoParity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Hybridization getHybridization() {
        return atom.getHybridization();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHybridization(Hybridization hybridization) {
        atom.setHybridization(hybridization);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Double getCovalentRadius() {
        return atom.getCovalentRadius();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCovalentRadius(Double radius) {
        atom.setCovalentRadius(radius);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IAtomContainer getContainer() {
        return atom.getContainer();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getIndex() {
        return atom.getIndex();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getValency() {
        return atom.getValency();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValency(Integer valency) {
        atom.setValency(valency);
    }

    public Iterable<IBond> bonds() {
        return atom.bonds();
    }

    public int getBondCount() {
        return atom.getBondCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAromatic() {
        return atom.isAromatic();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setIsAromatic(boolean arom) {
        atom.setIsAromatic(arom);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInRing() {
        return atom.isInRing();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setIsInRing(boolean ring) {
        atom.setIsInRing(ring);
    }

    @Override
    public int hashCode() {
        return atom.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return atom.equals(obj);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IAtom clone() throws CloneNotSupportedException {
        return atom.clone();
    }
}
