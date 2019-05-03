/*
 * Copyright (c) 2013 European Bioinformatics Institute (EMBL-EBI)
 *                    John May <jwmay@users.sf.net>
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */

package org.openscience.cdk.isomorphism;

import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.IQueryBond;

import static org.openscience.cdk.CDKConstants.ISAROMATIC;

/**
 * Defines compatibility checking of bonds for (subgraph)-isomorphism mapping.
 *
 * @author John May
 * @cdk.module isomorphism
 */
public abstract class BondMatcher {

    /**
     * Determines if {@code bond1} is compatible with {@code bond2}.
     *
     * @param bond1 a bond from the query structure
     * @param bond2 a bond from the target structure
     * @return the bonds are compatible
     */
    public abstract boolean matches(IBond bond1, IBond bond2);

    /**
     * All bonds are compatible.
     *
     * @return a bond matcher
     */
    public static BondMatcher forAny() {
        return new AnyMatcher();
    }

    /**
     * Bonds are compatible if they are both aromatic or their orders are equal
     * and they are non-aromatic. Under this matcher a single/double bond will
     * not match a single/double bond which is aromatic.
     *
     * @return a bond matcher
     */
    public static BondMatcher forStrictOrder() {
        return new StrictOrderMatcher();
    }

    /**
     * Bonds are compatible if they are both aromatic or their orders are equal.
     * This matcher allows a single/double bond to match a single/double
     * aromatic bond.
     *
     * @return a bond matcher
     */
    public static BondMatcher forOrder() {
        return new OrderMatcher();
    }

    /**
     * Bonds are compatible if the first {@code bond1} (an {@link IQueryBond})
     * matches the second, {@code bond2}.
     *
     * @return a bond matcher
     */
    public static BondMatcher forQuery() {
        return new QueryMatcher();
    }

    /**
     * Bonds are compatible if they are both aromatic or their orders are
     * equal.
     */
    private static final class OrderMatcher extends BondMatcher {

        /**{@inheritDoc} */
        @Override
        public boolean matches(IBond bond1, IBond bond2) {
            return bond1.getFlag(ISAROMATIC) && bond2.getFlag(ISAROMATIC) || bond1.getOrder() == bond2.getOrder();
        }
    }

    /**
     * Bonds are compatible if they are both aromatic or their orders are equal
     * and they are non-aromatic. In this matcher a single or double bond will
     * not match a single or double bond which is part of an aromatic system.
     */
    private static final class StrictOrderMatcher extends BondMatcher {

        /**{@inheritDoc} */
        @Override
        public boolean matches(IBond bond1, IBond bond2) {
            return bond1.getFlag(ISAROMATIC) == bond2.getFlag(ISAROMATIC)
                    && (bond1.getOrder() == bond2.getOrder() || bond1.getFlag(ISAROMATIC) && bond2.getFlag(ISAROMATIC));
        }
    }

    /** All bonds are considered compatible. */
    private static final class AnyMatcher extends BondMatcher {

        /**{@inheritDoc} */
        @Override
        public boolean matches(IBond bond1, IBond bond2) {
            return true;
        }
    }

    /**
     * Bonds are compatible if the first {@code bond1} (an {@link IQueryBond})
     * matches the second, {@code bond2}.
     */
    private static final class QueryMatcher extends BondMatcher {

        /**{@inheritDoc} */
        @Override
        public boolean matches(IBond bond1, IBond bond2) {
            return ((IQueryBond) bond1).matches(bond2);
        }
    }
}
