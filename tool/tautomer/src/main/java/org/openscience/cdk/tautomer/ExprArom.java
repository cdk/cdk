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

enum ExprArom {
    Aromatic,
    Aliphatic,
    Either,
    Undef;

    private ExprArom and(ExprArom o) {
        if (this == o)
            return this;
        if (this == Either)
            return o;
        if (o == Either)
            return this;
        return Undef;
    }

    private ExprArom or(ExprArom o) {
        if (this == o)
            return this;
        return Undef;
    }

    static ExprArom get(Expr e) {
        switch (e.type()) {
            case AND:
                return get(e.left()).and(get(e.right()));
            case OR:
                return get(e.left()).or(get(e.right()));
            case ORDER:
                return Either;
            case ALIPHATIC_ORDER:
                return Aliphatic;
            case IS_ALIPHATIC:
                return Aliphatic;
            case IS_AROMATIC:
                return Aromatic;
            case IS_IN_CHAIN:
                return Aliphatic;
            case IS_IN_RING:
                return Either;
            case FALSE:
            case NOT:
            case SINGLE_OR_AROMATIC:
            case SINGLE_OR_DOUBLE:
            case DOUBLE_OR_AROMATIC:
                return Undef;
            case TRUE:
                return Either;
            default:
                throw new IllegalArgumentException("Unsupported bond expression: " + e);
        }
    }

    static ExprArom get(IBond bond) {
        if (bond instanceof IQueryBond)
            return get(((QueryBond)BondRef.deref(bond)).getExpression());
        return Undef;
    }
}
