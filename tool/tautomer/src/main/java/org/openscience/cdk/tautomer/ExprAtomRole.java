/*
 * Copyright (C) 2024 John Mayfield
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

package org.openscience.cdk.tautomer;

import org.openscience.cdk.AtomRef;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.isomorphism.matchers.Expr;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.QueryAtom;

public enum ExprAtomRole {
    Acceptor,
    Donor,
    Any;

    private static AtomType[] atypes = AtomType.values();

    static ExprAtomRole get(Expr e) {
        ExprAtomRole role = null;
        for (AtomType atype : atypes) {
            if (!atype.matches(e)) {
                continue;
            }
            if (role == null || role == atype.r)
                role = atype.r;
            else
                return Any;
        }
        if (role == null)
            return Any;
        return role;
    }

    static ExprAtomRole get(IAtom a) {
        if (a instanceof IQueryAtom)
            return get(((QueryAtom)AtomRef.deref(a)).getExpression());
        return Any;
    }

    public enum AtomType {

        OH_DONOR(Donor, IAtom.O, 1, 1, 0, 2),
        O_ACCEPT(Acceptor, IAtom.O, 0, 1, 0, 2),
        NH_DONOR(Donor, IAtom.N, 1, 2, 0, 3),
        NH2_DONOR(Donor, IAtom.N, 2, 1, 0, 3),
        N_ACCEPT(Acceptor, IAtom.N, 0, 2, 0, 3);

        private final ExprAtomRole r;
        private final int e;
        private final int h;
        private final int d;
        private final int q;
        private final int v;

        AtomType(ExprAtomRole r, int e, int h, int d, int q, int v) {
            this.r = r;
            this.e = e;
            this.h = h;
            this.d = d;
            this.q = q;
            this.v = v;
        }

        boolean matches(Expr e) {
            switch (e.type()) {
                case AND:
                    return matches(e.left()) && matches(e.right());
                case OR:
                    return matches(e.left()) || matches(e.right());
                case NOT:
                    return !matches(e.left());
                case ALIPHATIC_ELEMENT:
                case ELEMENT:
                case AROMATIC_ELEMENT:
                    return e.value() == this.e;
                case IMPL_H_COUNT:
                case TOTAL_H_COUNT:
                    return e.value() == this.h;
                case DEGREE:
                    return e.value() == this.d;
                case TOTAL_DEGREE:
                    return e.value() == this.d+this.h;
                case FORMAL_CHARGE:
                    return e.value() == this.q;
                case VALENCE:
                    return e.value() == this.v;
                case ISOTOPE:
                    return true;
                case UNSATURATED:
                    return this.d+this.h < this.v;
                default:
                    return true;
            }
        }
    }
}
