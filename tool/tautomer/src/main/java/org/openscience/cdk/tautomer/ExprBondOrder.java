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

import org.openscience.cdk.BondRef;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.Expr;
import org.openscience.cdk.isomorphism.matchers.IQueryBond;
import org.openscience.cdk.isomorphism.matchers.QueryBond;

enum ExprBondOrder {
    Single,
    Double,
    Triple,
    Quadruple,
    Any,
    Undef;

    private ExprBondOrder and(ExprBondOrder o) {
        if (this == o)
            return this;
        if (this == Any)
            return o;
        if (o == Any)
            return this;
        return Undef;
    }

    private ExprBondOrder or(ExprBondOrder o) {
        if (this == o)
            return this;
        return Undef;
    }

    static ExprBondOrder get(Expr e) {
        switch (e.type()) {
            case AND:
                return get(e.left()).and(get(e.right()));
            case OR:
                return get(e.left()).or(get(e.right()));
            case ORDER:
            case ALIPHATIC_ORDER:
                switch (e.value()) {
                    case 1: return Single;
                    case 2: return Double;
                    case 3: return Triple;
                    case 4: return Quadruple;
                    default: return Undef;
                }
            case IS_ALIPHATIC:
            case IS_AROMATIC:
            case IS_IN_CHAIN:
            case IS_IN_RING:
                return Any;
            case FALSE:
            case NOT:
            case SINGLE_OR_AROMATIC:
            case SINGLE_OR_DOUBLE:
            case DOUBLE_OR_AROMATIC:
                return Undef;
            case TRUE:
                return Any;
            default:
                throw new IllegalArgumentException("Unsupported bond expression: " + e);
        }
    }

    static ExprBondOrder get(IBond bond) {
        if (bond instanceof IQueryBond)
            return get(((QueryBond)BondRef.deref(bond)).getExpression());
        return Undef;
    }
}
