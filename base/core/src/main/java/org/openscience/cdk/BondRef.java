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
 * A bond ref, references a CDK {@link IBond} indirectly. All
 * methods are passed through to the referenced bond. The reference can
 * be used to override the behaviour of the base bond.
 *
 * @author John Mayfield
 */
public class BondRef extends ChemObjectRef implements IBond {

    private final IBond bond;

    /**
     * Create a pointer for the provided bond.
     *
     * @param bond the bond to reference
     */
    public BondRef(IBond bond) {
        super(bond);
        this.bond = bond;
    }

    /**
     * Utility method to dereference an bond pointer. If the bond is not
     * an {@link BondRef} it simply returns the input.
     *
     * @param bond the bond
     * @return non-pointer bond
     */
    public static IBond deref(IBond bond) {
        while (bond instanceof BondRef)
            bond = ((BondRef) bond).deref();
        return bond;
    }

    /**
     * Dereference the bond pointer once providing access to the base
     * bond.
     *
     * @return the bond pointed to
     */
    public IBond deref() {
        return bond;
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
    public void setElectronCount(Integer count) {
        bond.setElectronCount(count);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<IAtom> atoms() {
        return bond.atoms();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAtoms(IAtom[] atoms) {
        bond.setAtoms(atoms);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IAtom getBegin() {
        return bond.getBegin();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IAtom getEnd() {
        return bond.getEnd();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getIndex() {
        return bond.getIndex();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IAtomContainer getContainer() {
        return bond.getContainer();
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
    public IAtom getAtom(int position) {
        return bond.getAtom(position);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IAtom getConnectedAtom(IAtom atom) {
        return bond.getConnectedAtom(atom);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IAtom getOther(IAtom atom) {
        return bond.getOther(atom);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IAtom[] getConnectedAtoms(IAtom atom) {
        return bond.getConnectedAtoms(atom);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(IAtom atom) {
        return bond.contains(atom);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAtom(IAtom atom, int position) {
        bond.setAtom(atom, position);
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
    public void setStereo(Stereo stereo) {
        bond.setStereo(stereo);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Display getDisplay() {
        return bond.getDisplay();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDisplay(Display display) {
        bond.setDisplay(display);
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
        return this.bond.isConnectedTo(bond);
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

    @Override
    public int hashCode() {
        return bond.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return bond.equals(obj);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IBond clone() throws CloneNotSupportedException {
        return bond.clone();
    }

    @Override
    public String toString() {
        return "BondRef{" + bond + "}";
    }
}
