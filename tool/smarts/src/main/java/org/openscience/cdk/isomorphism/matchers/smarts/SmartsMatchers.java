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

package org.openscience.cdk.isomorphism.matchers.smarts;

import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * Bridging class between the SMARTS matcher and the parser/query tool. The
 * class are currently split across different packages. This classes temporary
 * functionality is to expose package private functionality through a single
 * location.
 *
 * @author John May
 * @cdk.module smarts
 * @cdk.githash
 */
public final class SmartsMatchers {

    /**
     * Do not use - temporary method until the SMARTS packages are cleaned up.
     *
     * Prepares a target molecule for matching with SMARTS.
     *
     * @param container the container to initialise
     * @param ringQuery whether the smarts will check ring size queries
     */
    public static void prepare(IAtomContainer container, boolean ringQuery) {
        if (ringQuery) {
            SMARTSAtomInvariants.configureDaylightWithRingInfo(container);
        } else {
            SMARTSAtomInvariants.configureDaylightWithoutRingInfo(container);
        }
    }

}
