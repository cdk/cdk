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
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Map;

public class BondRef implements IBond {

    private final IBond              bond;
    private final int                idx;
    private final AtomRef beg, end;

    BondRef(IBond bond, int idx, AtomRef beg, AtomRef end) {
        this.bond = bond;
        this.idx = idx;
        this.beg = beg;
        this.end = end;
    }

    public static BondRef[] getBondRefs(IAtomContainer mol) {

        final int numAtoms = mol.getAtomCount();
        final int numBonds = mol.getBondCount();
        BondRef[] bonds    = new BondRef[numBonds];

        final Map<IAtom, AtomRef> atomCache = new IdentityHashMap<>(mol.getAtomCount());

        for (int i = 0; i < numAtoms; i++) {
            final IAtom atom = mol.getAtom(i);
            final AtomRef atomrf = new AtomRef(i,
                                               atom,
                                               new ArrayList<BondRef>());
            atomCache.put(atomrf.atom, atomrf);
        }
        for (int i = 0; i < numBonds; i++) {
            final IBond   bond    = mol.getBond(i);
            AtomRef       beg     = atomCache.get(bond.getAtom(0));
            AtomRef       end     = atomCache.get(bond.getAtom(1));
            final BondRef bondref = new BondRef(bond, i, beg, end);
            beg.bonds.add(bondref);
            end.bonds.add(bondref);
            bonds[i] = bondref;
        }

        return bonds;
    }

    public int getIndex() {
        return idx;
    }

    @Override
    public void setElectronCount(Integer electronCount) {
        bond.setElectronCount(electronCount);
    }

    @Override
    public IChemObjectBuilder getBuilder() {
        return bond.getBuilder();
    }

    @Override
    public Integer getElectronCount() {
        return bond.getElectronCount();
    }

    @Override
    public void addListener(IChemObjectListener col) {
        bond.addListener(col);
    }

    @Override
    public int getListenerCount() {
        return bond.getListenerCount();
    }

    @Override
    public void removeListener(IChemObjectListener col) {
        bond.removeListener(col);
    }

    @Override
    public void setNotification(boolean bool) {
        bond.setNotification(bool);
    }

    @Override
    public boolean getNotification() {
        return bond.getNotification();
    }

    @Override
    public void notifyChanged() {
        bond.notifyChanged();
    }

    @Override
    public void notifyChanged(IChemObjectChangeEvent evt) {
        bond.notifyChanged(evt);
    }

    @Override
    public void setProperty(Object description, Object property) {
        bond.setProperty(description, property);
    }

    @Override
    public void removeProperty(Object description) {
        bond.removeProperty(description);
    }

    @Override
    public Iterable<IAtom> atoms() {
        return FluentIterable.from(bond.atoms()).transform(new Function<IAtom, IAtom>() {
            @Override
            public IAtom apply(IAtom input) {
                return input; //atomCache.get(input);
            }
        });
    }

    @Override
    public void setAtoms(IAtom[] atoms) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getAtomCount() {
        return bond.getAtomCount();
    }

    @Override
    public AtomRef getAtom(int position) {
        switch (position) {
            case 0: return beg;
            case 1: return end;
            default: return null;
        }
    }

    @Override
    public AtomRef getConnectedAtom(IAtom atom) {
        if (atom == beg || atom == beg.atom)
            return end;
        else if (atom == end || atom == end.atom)
            return beg;
        throw new IllegalArgumentException("Atom");
    }

    @Override
    public <T> T getProperty(Object description) {
        return bond.getProperty(description);
    }

    @Override
    public IAtom[] getConnectedAtoms(IAtom atom) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(IAtom atom) {
        return atom == beg || atom == end || atom == beg.atom || atom == end.atom;
    }

    @Override
    public void setAtom(IAtom atom, int position) {
        throw new IllegalArgumentException();
    }

    @Override
    public Order getOrder() {
        return bond.getOrder();
    }

    @Override
    public <T> T getProperty(Object description, Class<T> c) {
        return bond.getProperty(description, c);
    }

    @Override
    public void setOrder(Order order) {
        bond.setOrder(order);
    }

    @Override
    public Stereo getStereo() {
        return bond.getStereo();
    }

    @Override
    public Map<Object, Object> getProperties() {
        return bond.getProperties();
    }

    @Override
    public String getID() {
        return bond.getID();
    }

    @Override
    public void setStereo(Stereo stereo) {
        bond.setStereo(stereo);
    }

    @Override
    public Point2d get2DCenter() {
        return bond.get2DCenter();
    }

    @Override
    public void setID(String identifier) {
        bond.setID(identifier);
    }

    @Override
    public Point3d get3DCenter() {
        return bond.get3DCenter();
    }

    @Override
    public boolean compare(Object object) {
        return bond.compare(object);
    }

    @Override
    public boolean isConnectedTo(IBond bond) {
        return bond.isConnectedTo(bond);
    }

    @Override
    public void setFlag(int mask, boolean value) {
        bond.setFlag(mask, value);
    }

    @Override
    public boolean isAromatic() {
        return bond.isAromatic();
    }

    @Override
    public void setIsAromatic(boolean arom) {
        bond.setIsAromatic(arom);
    }

    @Override
    public boolean getFlag(int mask) {
        return bond.getFlag(mask);
    }

    @Override
    public boolean isInRing() {
        return bond.isInRing();
    }

    @Override
    public void setIsInRing(boolean ring) {
        bond.setIsInRing(ring);
    }

    @Override
    public void setProperties(Map<Object, Object> properties) {
        bond.setProperties(properties);
    }

    @Override
    public IBond clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    @Override
    public void addProperties(Map<Object, Object> properties) {
        bond.addProperties(properties);
    }

    @Override
    public void setFlags(boolean[] newFlags) {
        bond.setFlags(newFlags);
    }

    @Override
    public boolean[] getFlags() {
        return bond.getFlags();
    }

    @Override
    public Number getFlagValue() {
        return bond.getFlagValue();
    }
}
