/*
 * Copyright (C) 2019  The Chemistry Development Kit (CDK) project
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

package org.openscience.cdk.isomorphism;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainerCreator;
import org.openscience.cdk.smarts.Smarts;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

public class HydrogenSuppressionTest {

    private static void test(String smaexp,
                             String smainp) {
        IAtomContainer qry = new QueryAtomContainer(null);
        assertTrue(Smarts.parse(qry, smainp));
        IAtomContainer sup    = QueryAtomContainerCreator.suppressQueryHydrogens(qry);
        String         smaact = Smarts.generate(sup);
        assertThat(smaact, is(smaexp));
    }

    @Test public void oneHydrogen() {
        test("[c!H0]", "c[H]");
        test("[c!H0]", "c[#1]");
    }

    @Test public void twoHydrogens() {
        test("[c!H0!H1]", "c([H])[H]");
        test("[c!H0!H1]", "c([#1])[#1]");
    }

    @Test public void deuteriumIsKept() {
        test("[c!H0][2#1]", "c([2H])[H]");
        test("[c!H0][2#1]", "c([2#1])[#1]");
    }

    @Test public void bridgingIsKept() {
        test("B[#1]B", "B[H]B");
    }
}
