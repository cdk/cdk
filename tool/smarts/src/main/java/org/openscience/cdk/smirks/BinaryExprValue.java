/*
 * Copyright (C) 2022 John Mayfield
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

package org.openscience.cdk.smirks;

import java.util.Objects;

/**
 * Internal.
 *
 * A helper utility to assist in evaluating binary expressions that may have
 * an undefined or invalid result. Consider a SMARTS atom like this: {@code
 * [CH2,NH2] => OR(AND(elem=C,H=2),AND(elem=N,H=2))} we can deduce that the
 * element is "conflicting" but the hydrogen count must be "2".
 *
 * @author John Mayfield
 */
final class BinaryExprValue {

    public static final BinaryExprValue ZERO = new BinaryExprValue(0, Type.Integer);

    enum Type {
        Integer,
        Boolean,
        Conflict,
        Undefined
    }

    final int val;
    final Type type;

    static final BinaryExprValue UNDEF = new BinaryExprValue(0, Type.Undefined);
    static final BinaryExprValue CONFLICTING = new BinaryExprValue(0, Type.Conflict);
    static final BinaryExprValue FALSE = new BinaryExprValue(0, Type.Boolean);
    static final BinaryExprValue TRUE = new BinaryExprValue(1, Type.Boolean);

    BinaryExprValue(int x, Type t) {
        this.val = x;
        this.type = t;
    }

    BinaryExprValue(int x) {
        this.val = x;
        this.type = Type.Integer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BinaryExprValue that = (BinaryExprValue) o;
        return val == that.val && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(val, type);
    }

    BinaryExprValue and(BinaryExprValue that) {
        if (this.type == Type.Undefined)
            return that;
        if (that.type == Type.Undefined)
            return this;
        if (this.type == Type.Conflict || that.type == Type.Conflict)
            return CONFLICTING;
        if (this.val == that.val)
            return this;
        return CONFLICTING;
    }

    BinaryExprValue or(BinaryExprValue that) {
        if (this.type == Type.Conflict || that.type == Type.Conflict)
            return CONFLICTING;
        if (this.type == Type.Undefined && that.type == Type.Undefined)
            return UNDEF;
        if (this.type == that.type && this.val == that.val)
            return this;
        return CONFLICTING;
    }

    BinaryExprValue not() {
        if (this.type == Type.Undefined)
            return UNDEF;
        // !true => false etc
        if (this.type == Type.Boolean)
            return this.val != 0 ? FALSE : TRUE;
        return CONFLICTING;
    }

    public boolean ok() {
        return type == Type.Integer || type == Type.Boolean;
    }

    public boolean invalid() {
        return type == Type.Conflict;
    }

    public boolean undef() {
        return type == Type.Undefined;
    }

    @Override
    public String toString() {
        if (type == Type.Undefined)
            return "{undefined}";
        if (type == Type.Conflict)
            return "{conflict}";
        if (type == Type.Boolean)
            return val != 0 ? "(true)" : "(false)";
        else
            return "(" + val + ")";
    }

}
