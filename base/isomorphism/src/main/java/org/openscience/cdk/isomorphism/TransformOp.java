/*
 * Copyright (C) 2022 NextMove Software
 *               2022 John Mayfield
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */

package org.openscience.cdk.isomorphism;

import org.openscience.cdk.config.Elements;

import java.util.Locale;
import java.util.Objects;

/**
 * A transform operation (TransformOp) describes a change to be made to some
 * mapped set of indexed atoms (0 &le; <b>idx</b> &lt; N). An op-code has a
 * type and then up to 4 additional integer parameters name a, b, c, d.
 * <br/>
 * What these parameters mean depend on the type and summarised below:
 * <table summary="OpCode parameter types">
 *     <thead>
 *       <tr><th>Type</th><th>Param a</th><th>Param b</th><th>Param c</th><th>Param d</th></tr>
 *     </thead>
 *     <tbody>
 *         <tr><th>NewAtom</th><td>idx</td><td>atomic number</td><td>impl H count</td><td>is aromatic?</td></tr>
 *         <tr><th>NewBond</th><td>idx</td><td>idx</td><td>bond order</td><td></td></tr>
 *         <tr><th>DeleteAtom</th><td>idx</td><td></td><td></td><td></td></tr>
 *         <tr><th>DeleteBond</th><td>idx</td><td>idx</td><td></td><td></td></tr>
 *         <tr><th>BondOrder</th><td>idx</td><td>idx</td><td>bond order</td><td></td></tr>
 *         <tr><th>Mass</th><td>idx</td><td>isotope mass</td><td></td><td></td></tr>
 *         <tr><th>Element</th><td>idx</td><td>atomic number</td><td></td><td></td></tr>
 *         <tr><th>Aromatic</th><td>idx</td><td>is aromatic?</td><td></td><td></td></tr>
 *         <tr><th>Charge</th><td>idx</td><td>formal charge</td><td></td><td></td></tr>
 *         <tr><th>ImplH</th><td>idx</td><td>impl H</td><td></td><td></td></tr>
 *         <tr><th>AdjustH</th><td>idx</td><td>+/- impl H change</td><td></td><td></td></tr>
 *         <tr><th>MoveH</th><td>idx</td><td>idx</td><td></td><td></td></tr>
 *         <tr><th>Tetrahedral</th><td>idx1</td><td>idx2</td><td>idx3</td><td>idx4</td></tr>
 *         <tr><th>DbTogether</th><td>idx1</td><td>idx2</td><td>idx3</td><td>idx4</td></tr>
 *         <tr><th>DbOpposite</th><td>idx1</td><td>idx2</td><td>idx3</td><td>idx4</td></tr>
 *     </tbody>
 * </table>
 */
public final class TransformOp implements Comparable<TransformOp> {

    // note the ordering here is important, atoms must be created before bonds,
    // bonds must be deleted before atoms.
    public enum Type {
        /**
         * Create a new atom.
         * {@code params: {idx, elem, hcnt, isarom}}
         */
        NewAtom,
        /**
         * Create a new bond with between the two atom indexes.
         * {@code params: {idx1, idx2, order}}
         */
        NewBond,
        /**
         * Delete a bond.
         * {@code params: {idx1, idx2}}
         */
        DeleteBond,
        /**
         * Delete an atom.
         * {@code params: {idx}}
         */
        DeleteAtom,
        /**
         * Set the bond order.
         * {@code params: {idx1, idx2, order}}
         */
        BondOrder,
        /**
         * Set the mass number (isotope) of an atom.
         * {@code params: {idx, mass}}
         */
        Mass,
        /**
         * Set the atomic number (element) of an atom.
         * {@code params: {idx, elem}}
         */
        Element,
        /**
         * Set or clear the aromatic flag of an atom.
         * {@code params: {idx, isarom}}
         */
        Aromatic,
        /**
         * Set or clear the aromatic flag of an bond.
         * {@code params: {idx1, idx2, isarom}}
         */
        AromaticBond,
        /**
         * Set the formal charge of an atom.
         * {@code params: {idx, charg}}
         */
        Charge,
        /**
         * Set the implicit hydrogen count of an atom.
         * {@code params: {idx, hcnt}}
         */
        ImplH,
        /**
         * Adjust the total hydrogen count (up or down) of an atom.
         * {@code params: {idx, hdelta}}
         */
        AdjustH,
        /**
         * Move a hydrogen from one atom to another.
         * {@code params: {idx1, idx2}}
         */
        MoveH,
        /**
         * Set the tetrahedral left-handed
         * {@code params: {foci, nbr1, nbr2, nbr3}}
         */
        Tetrahedral,
        /**
         * Set the double bond (ndr1-idx1=idx2-nbr2) such that the
         * neighbours are on together (cis) on the same side of the double
         * bond.
         * {@code params: {idx1, idx2, nbr1, nbr2}}
         */
        DbTogether,
        /**
         * Set the double bond (ndr1-idx1=idx2-nbr2) such that the
         * neighbours are on opposite (trans) sides of the double bond.
         * <p>
         * {@code params: {idx1, idx2, nbr1, nbr2}}
         */
        DbOpposite,

        /* Replace* Ops are put in by the optimization pass. */

        /**
         * Replace an existing atom, keep bonding to other mapped atoms intact
         * but removing bonds to unmapped atoms. This is a convenient way to
         * preserve stereochemistry when we delete and then add a atom/bond
         * pair. Note this op-code is automatically put in by an
         * optimisation pass.
         * <p>
         * {@code params: {idx, elem, hcnt, isarom}}
         */
        ReplaceAtom,
        /**
         * Replace an existing implicit or explicit atom, If it's an
         * explicit hydrogen, and bond to unmapped atoms are removed. This
         * is a convenient way to preserve stereochemistry when we delete
         * a hydrogen then bond a new atom. Note this op-code is
         * automatically put in by an optimisation pass.
         * <p>
         * {@code params: {idx1, idx2, elem, hcnt}}
         */
        ReplaceHydrogen
    }

    final Type type;
    final int a;
    final int b;
    final int c;
    final int d;

    public TransformOp(Type type, int a, int b, int c, int d) {
        this.type = type;
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    public TransformOp(Type type, int a, int b, int c) {
        this(type, a, b, c, 0);
    }

    public TransformOp(Type type, int a, int b) {
        this(type, a, b, 0);
    }

    public TransformOp(Type type, int a) {
        this(type, a, 0, 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransformOp opCode = (TransformOp) o;
        return a == opCode.a && b == opCode.b && c == opCode.c && d == opCode.d && type == opCode.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, a, b, c, d);
    }

    int getOtherIdx(int x) {
        switch (type) {
            case NewBond:
            case DeleteBond:
            case BondOrder:
            case MoveH:
                if (x == a) return b;
                else if (x == b) return a;
                return -1;
            default:
                return -1;
        }
    }

    int getMaxAtomIdx() {
        switch (type) {
            case NewAtom:
            case ReplaceAtom:
            case DeleteAtom:
            case Mass:
            case Element:
            case Aromatic:
            case Charge:
            case ImplH:
            case AdjustH:
                return a;
            case NewBond:
            case DeleteBond:
            case BondOrder:
            case AromaticBond:
            case MoveH:
                return Math.max(a, b);
            case DbOpposite:
            case DbTogether:
            case Tetrahedral:
                return Math.max(Math.max(a, b), Math.max(c, d));
            default:
                throw new IllegalStateException(type + " atom index?");
        }
    }

    private int getMinAtomIdx() {
        switch (type) {
            case NewAtom:
            case DeleteAtom:
            case ReplaceAtom:
            case Mass:
            case Element:
            case Aromatic:
            case Charge:
            case ImplH:
            case AdjustH:
                return a;
            case NewBond:
            case DeleteBond:
            case BondOrder:
            case AromaticBond:
            case MoveH:
                return Math.min(a, b);
            case DbOpposite:
            case DbTogether:
            case Tetrahedral:
                return Math.min(Math.min(a, b), Math.min(c, d));
            default:
                throw new IllegalStateException();
        }
    }

    private int getPriority() {
        switch (type) {
            case NewAtom:
            case NewBond:
            case AdjustH:
                return 0;
            case MoveH:
                return 1;
            case DeleteAtom:
            case DeleteBond:
                return 2;
            case Tetrahedral:
            case DbOpposite:
            case DbTogether:
                return 5;
            default:
                return 3;
        }
    }

    @Override
    public int compareTo(TransformOp that) {
        int cmp = Integer.compare(this.getPriority(), that.getPriority());
        if (cmp != 0)
            return cmp;
        int thisMinIdx = this.getMinAtomIdx();
        int thisMaxIdx = this.getMaxAtomIdx();
        int thatMinIdx = that.getMinAtomIdx();
        int thatMaxIdx = that.getMaxAtomIdx();
        if (thisMaxIdx < thatMinIdx)
            return -1;
        if (thatMaxIdx < thisMinIdx)
            return +1;
        return this.type.compareTo(that.type);
    }

    @Override
    public String toString() {
        switch (type) {
            case ReplaceAtom:
            case NewAtom:
                String desc = Elements.ofNumber(b).symbol();
                if (d != 0)
                    desc = desc.toLowerCase(Locale.ROOT);
                desc += "H" + c;
                return type + "{[" + desc + "@" + a + "]}";
            case ReplaceHydrogen:
                desc = Elements.ofNumber(c).symbol();
                desc += "H" + d;
                return type + "{[" + desc + "@" + a + "=>" + b + "]}";
            case DeleteAtom:
                return type + "{" + a + "}";
            case Mass:
            case Element:
            case Aromatic:
            case Charge:
            case ImplH:
            case AdjustH:
                return type + "{" + b + "@" + a + "}";
            case DeleteBond:
                return type + "{" + a + "-" + b + "}";
            case NewBond:
            case BondOrder:
            case AromaticBond:
                switch (c) {
                    case 1:
                        return type + "{" + a + "-" + b + "}";
                    case 2:
                        return type + "{" + a + "=" + b + "}";
                    case 3:
                        return type + "{" + a + "#" + b + "}";
                    default:
                        return type + "{" + a + "," + b + ",order=" + c + "}";
                }

            case MoveH:
                return type + "{" + a + "=>" + b + "}";
            case Tetrahedral:
                return type + "{" + a + ",@(" + b + "," + c + "," + d + "}";
            case DbOpposite:
                return type + "{" + c + "/" + a + "=" + b + "/" + d + "}";
            case DbTogether:
                return type + "{" + c + "/" + a + "=" + b + "\\" + d + "}";
            default:
                throw new IllegalStateException("Unknown op:" + type);
        }
    }
}
