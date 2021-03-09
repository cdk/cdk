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

package org.openscience.cdk.stereo;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IStereoElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract class AbstractStereo<F extends IChemObject, C extends IChemObject>
    implements IStereoElement<F, C> {

    private int     value;
    private F       focus;
    private List<C> carriers;
    private IChemObjectBuilder builder;

    protected static int numCarriers(int cfg) {
        return ((cfg >>> 12) & 0xf);
    }

    AbstractStereo(F focus, C[] carriers, int value) {
        if (focus == null)
            throw new NullPointerException("Focus of stereochemistry can not be null!");
        if (carriers == null)
            throw new NullPointerException("Carriers of the configuration can not be null!");
        if (carriers.length != numCarriers(value))
            throw new IllegalArgumentException("Unexpected number of stereo carriers! expected " + ((value >>> 12) & 0xf) + " was " + carriers.length);
        for (C carrier : carriers) {
            if (carrier == null)
                throw new NullPointerException("A carrier was undefined!");
        }
        this.value    = value;
        this.focus    = focus;
        this.carriers = new ArrayList<>();
        Collections.addAll(this.carriers, carriers);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public F getFocus() {
        return focus;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<C> getCarriers() {
        return carriers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getConfigClass() {
        return value & CLS_MASK;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getConfigOrder() {
        return value & CFG_MASK;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getConfig() {
        return value & 0xffff;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setConfigOrder(int cfg) {
        value = getConfigClass() | cfg;
    }

    /**
     * {@inheritDoc}
     */
    public int getGroupInfo() {
        return value & IStereoElement.GRP_MASK;
    }

    /**
     * {@inheritDoc}
     */
    public void setGroupInfo(int grp) {
        value &= ~IStereoElement.GRP_MASK; // clear existing hit bits
        value |= (grp & IStereoElement.GRP_MASK); // set the new value ensure low bits aren't overwritten
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(IAtom atom) {
        if (focus.equals(atom) || (focus instanceof IBond && ((IBond) focus).contains(atom)))
            return true;
        for (C carrier : carriers) {
            if (carrier.equals(atom) ||
                (carrier instanceof IBond && ((IBond) carrier).contains(atom)))
                return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IStereoElement<F,C> map(Map<IAtom, IAtom> atoms,
                                   Map<IBond, IBond> bonds) {
        if (atoms == null)
            throw new IllegalArgumentException("Atom map should be non-null");
        if (bonds == null)
            throw new IllegalArgumentException("Bond map should be non-null");
        Map<IChemObject,IChemObject> map = new HashMap<>(atoms.size() + bonds.size());
        map.putAll(atoms);
        map.putAll(bonds);
        return map(map);
    }

    @Override
    @SuppressWarnings("unchecked")
    public IStereoElement<F, C> map(Map<IChemObject, IChemObject> chemobjs) {
        if (chemobjs == null)
            throw new NullPointerException("chemobj map was not provided!");
        F newfocus = (F) chemobjs.get(focus);
        if (newfocus == null)
            newfocus = focus;
        List<C> newcarriers = carriers;
        for (int i = 0; i < newcarriers.size(); i++) {
            C newcarrier = (C) chemobjs.get(newcarriers.get(i));
            if (newcarrier != null) {
                // make a copy if this is the first change
                if (newcarriers == carriers)
                    newcarriers = new ArrayList<>(carriers);
                newcarriers.set(i, newcarrier);
            }
        }
        // no change, return self
        if (newfocus == focus && newcarriers == carriers)
            return this;
        IStereoElement<F, C> se = create(newfocus, newcarriers, value);
        se.setGroupInfo(getGroupInfo());
        return se;
    }

    protected abstract IStereoElement<F,C> create(F focus, List<C> carriers, int cfg);

    /**
     *{@inheritDoc}
     */
    @Override
    public IChemObjectBuilder getBuilder() {
        if (builder == null)
            throw new UnsupportedOperationException("Non-domain object!");
        return this.builder;
    }

    protected void setBuilder(IChemObjectBuilder builder) {
        this.builder = builder;
    }

    // labels for describing permutation
    protected static final int A = 0, B = 1, C = 2, D = 3, E = 4, F = 5;

    // apply the inverse of a permutation
    protected static <T> T[] invapply(T[] src, int[] perm) {
        T[] res = src.clone();
        for (int i = 0; i < src.length; i++)
            res[i] = src[perm[i]];
        return res;
    }
}
