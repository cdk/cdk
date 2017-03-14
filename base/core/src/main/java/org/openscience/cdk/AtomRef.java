/*
 * Copyright (c) 2017 John May <jwmay@users.sf.net>
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A container atom offers a snapshot view of an atom that belongs
 * to an atom container. In addition to the normal methods of an
 * atom this class provides the index ({@link #getIndex()}) in
 * the parent {@link IAtomContainer} and the connected
 * bonds ({@link #getBonds()}).
 */
public final class AtomRef implements IAtom {

    final IAtom                 atom;
    private final int           idx;
    private final List<BondRef> bonds;

    private AtomRef(int idx, IAtom atom, List<BondRef> bonds) {
        this.idx   = idx;
        this.atom  = atom;
        this.bonds = bonds;
    }

    public static AtomRef[] getAtomRefs(IAtomContainer mol) {

        final int numAtoms = mol.getAtomCount();
        final int numBonds = mol.getBondCount();
        AtomRef[] atoms    = new AtomRef[numAtoms];

        final Map<IAtom,AtomRef> atomCache = new HashMap<>();

        for (int i = 0; i < numAtoms; i++) {
            atoms[i] = new AtomRef(i,
                                   mol.getAtom(i),
                                   new ArrayList<BondRef>());
            atomCache.put(atoms[i].atom, atoms[i]);
        }
        for (int i = 0; i < numBonds; i++){
            IBond bond = mol.getBond(i);
            for (IAtom batom : bond.atoms()) {
                atomCache.get(batom).bonds.add(new BondRef(bond, i, atomCache));
            }
        }

        return atoms;
    }

    public int getIndex() {
        return idx;
    }

    public <T extends IBond> List<T> getBonds() {
        return (List<T>) bonds;
    }

    @Override
    public IChemObjectBuilder getBuilder() {
        return atom.getBuilder();
    }

    @Override
    public void setCharge(Double charge) {
        atom.setCharge(charge);
    }

    @Override
    public void setNaturalAbundance(Double naturalAbundance) {
        atom.setNaturalAbundance(naturalAbundance);
    }

    @Override
    public Double getCharge() {
        return atom.getCharge();
    }

    @Override
    public void setExactMass(Double exactMass) {
        atom.setExactMass(exactMass);
    }

    @Override
    public Integer getAtomicNumber() {
        return atom.getAtomicNumber();
    }

    @Override
    public void setImplicitHydrogenCount(Integer hydrogenCount) {
        atom.setImplicitHydrogenCount(hydrogenCount);
    }

    @Override
    public Double getNaturalAbundance() {
        return atom.getNaturalAbundance();
    }

    @Override
    public void setAtomicNumber(Integer atomicNumber) {
        atom.setAtomicNumber(atomicNumber);
    }

    @Override
    public Integer getImplicitHydrogenCount() {
        return atom.getImplicitHydrogenCount();
    }

    @Override
    public void addListener(IChemObjectListener col) {
        atom.addListener(col);
    }

    @Override
    public void removeListener(IChemObjectListener col) {
        atom.removeListener(col);
    }

    @Override
    public int getListenerCount() {
        return atom.getListenerCount();
    }

    @Override
    public void setNotification(boolean bool) {
        atom.setNotification(bool);
    }

    @Override
    public boolean getNotification() {
        return atom.getNotification();
    }

    @Override
    public void notifyChanged() {
        atom.notifyChanged();
    }

    @Override
    public void notifyChanged(IChemObjectChangeEvent evt) {
        atom.notifyChanged(evt);
    }

    @Override
    public Double getExactMass() {
        return atom.getExactMass();
    }

    @Override
    public void setMaxBondOrder(IBond.Order maxBondOrder) {
        atom.setMaxBondOrder(maxBondOrder);
    }

    @Override
    public String getSymbol() {
        return atom.getSymbol();
    }

    @Override
    public void setSymbol(String symbol) {
        atom.setSymbol(symbol);
    }

    @Override
    public Integer getMassNumber() {
        return atom.getMassNumber();
    }

    @Override
    public Point2d getPoint2d() {
        return atom.getPoint2d();
    }

    @Override
    public void setPoint2d(Point2d point2d) {
        atom.setPoint2d(point2d);
    }

    @Override
    public Point3d getPoint3d() {
        return atom.getPoint3d();
    }

    @Override
    public void setPoint3d(Point3d point3d) {
        atom.setPoint3d(point3d);
    }

    @Override
    public void setFractionalPoint3d(Point3d point3d) {
        atom.setFractionalPoint3d(point3d);
    }

    @Override
    public Point3d getFractionalPoint3d() {
        return atom.getFractionalPoint3d();
    }

    @Override
    public void setBondOrderSum(Double bondOrderSum) {
        atom.setBondOrderSum(bondOrderSum);
    }

    @Override
    public void setMassNumber(Integer massNumber) {
        atom.setMassNumber(massNumber);
    }

    @Override
    public void setAtomTypeName(String identifier) {
        atom.setAtomTypeName(identifier);
    }

    @Override
    public String getAtomTypeName() {
        return atom.getAtomTypeName();
    }

    @Override
    public IBond.Order getMaxBondOrder() {
        return atom.getMaxBondOrder();
    }

    @Override
    public Double getBondOrderSum() {
        return atom.getBondOrderSum();
    }

    @Override
    public Integer getStereoParity() {
        return atom.getStereoParity();
    }

    @Override
    public void setStereoParity(Integer stereoParity) {
        atom.setStereoParity(stereoParity);
    }

    @Override
    public void setFormalCharge(Integer charge) {
        atom.setFormalCharge(charge);
    }

    @Override
    public Integer getFormalCharge() {
        return atom.getFormalCharge();
    }

    @Override
    public void setProperty(Object description, Object property) {
        atom.setProperty(description, getFractionalPoint3d());
    }

    @Override
    public void setFormalNeighbourCount(Integer count) {
        atom.setFormalNeighbourCount(count);
    }

    @Override
    public Integer getFormalNeighbourCount() {
        return atom.getFormalNeighbourCount();
    }

    @Override
    public void removeProperty(Object description) {
        atom.removeProperty(description);
    }

    @Override
    public void setHybridization(Hybridization hybridization) {
        atom.setHybridization(hybridization);
    }

    @Override
    public Hybridization getHybridization() {
        return atom.getHybridization();
    }

    @Override
    public void setCovalentRadius(Double radius) {
        atom.setCovalentRadius(radius);
    }

    @Override
    public boolean isAromatic() {
        return atom.isAromatic();
    }

    @Override
    public void setIsAromatic(boolean arom) {
        atom.setIsAromatic(arom);
    }

    @Override
    public Double getCovalentRadius() {
        return atom.getCovalentRadius();
    }

    @Override
    public void setValency(Integer valency) {
        atom.setValency(valency);
    }

    @Override
    public Integer getValency() {
        return atom.getValency();
    }

    @Override
    public boolean isInRing() {
        return atom.isInRing();
    }

    @Override
    public void setIsInRing(boolean ring) {
        atom.setIsInRing(ring);
    }

    @Override
    public IAtom clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    @Override
    public <T> T getProperty(Object description) {
        return atom.getProperty(description);
    }

    @Override
    public <T> T getProperty(Object description, Class<T> c) {
        return atom.getProperty(description, c);
    }

    @Override
    public Map<Object, Object> getProperties() {
        return atom.getProperties();
    }

    @Override
    public String getID() {
        return atom.getID();
    }

    @Override
    public void setID(String identifier) {
        atom.setID(identifier);
    }

    @Override
    public void setFlag(int mask, boolean value) {
        atom.setFlag(mask, value);
    }

    @Override
    public boolean getFlag(int mask) {
        return atom.getFlag(mask);
    }

    @Override
    public void setProperties(Map<Object, Object> properties) {
        atom.setProperties(properties);
    }

    @Override
    public void addProperties(Map<Object, Object> properties) {
        atom.setProperties(properties);
    }

    @Override
    public void setFlags(boolean[] newFlags) {
        atom.setFlags(newFlags);
    }

    @Override
    public boolean[] getFlags() {
        return atom.getFlags();
    }

    @Override
    public Number getFlagValue() {
        return atom.getFlagValue();
    }
}
