/*
 *  This file is part of the SIRIUS library for analyzing MS and MS/MS data
 *
 *  Copyright (C) 2013-2015 Kai Dührkop
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with SIRIUS.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openscience.cdk.decomp;

/**
 * A POJO storing the weight information about a character in the alphabet
 * @param <T>
 */
class Weight<T> implements Comparable<Weight<T>> {

    /**
     * corresponding character in the alphabet
     */
    private final T owner;

    /**
     * the exact mass of the character
     */
    private final double mass;

    /**
     * the transformation of the mass in the integer space
     */
    private int integerMass;

    private int l;
    private int lcm;

    public Weight(T owner, double mass) {
        this.owner = owner;
        this.mass = mass;
    }

    public T getOwner() {
        return owner;
    }

    public double getMass() {
        return mass;
    }

    public int getIntegerMass() {
        return integerMass;
    }

    public void setIntegerMass(int integerMass) {
        this.integerMass = integerMass;
    }

    public int getL() {
        return l;
    }

    public void setL(int l) {
        this.l = l;
    }

    public int getLcm() {
        return lcm;
    }

    public void setLcm(int lcm) {
        this.lcm = lcm;
    }

    @Override
    public int compareTo(Weight<T> tWeight) {
        return (int)Math.signum(mass - tWeight.mass);
    }
}
