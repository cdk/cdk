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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.graph.ConnectedComponents;
import org.openscience.cdk.graph.GraphUtil;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.silent.Atom;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

/**
 * @author John May
 */
class ComponentGroupingTest {

    // mock matching [#8].[#8]
    @Test
    void ungrouped() {
        Assertions.assertTrue(create(null, oxidanone()).apply(new int[]{0, 1}));
        Assertions.assertTrue(create(null, oxidanone()).apply(new int[]{1, 0}));
        Assertions.assertTrue(create(null, ethyleneGlycol()).apply(new int[]{0, 3}));
        Assertions.assertTrue(create(null, ethyleneGlycol()).apply(new int[]{3, 0}));
        Assertions.assertTrue(create(null, ethylAlcoholHydrate()).apply(new int[]{0, 3}));
        Assertions.assertTrue(create(null, ethylAlcoholHydrate()).apply(new int[]{3, 0}));
    }

    // mock matching ([#8].[#8])
    @Test
    void grouped() {
        int[] grouping = {1, 1, 1};
        Assertions.assertTrue(create(grouping, oxidanone()).apply(new int[]{0, 1}));
        Assertions.assertTrue(create(grouping, oxidanone()).apply(new int[]{1, 0}));
        Assertions.assertTrue(create(grouping, ethyleneGlycol()).apply(new int[]{0, 3}));
        Assertions.assertTrue(create(grouping, ethyleneGlycol()).apply(new int[]{3, 0}));
        Assertions.assertFalse(create(grouping, ethylAlcoholHydrate()).apply(new int[]{0, 3}));
        Assertions.assertFalse(create(grouping, ethylAlcoholHydrate()).apply(new int[]{3, 0}));
    }

    // mock matching ([#8]).([#8])
    @Test
    void multipleGroups() {
        int[] grouping = {1, 2, 2};
        Assertions.assertFalse(create(grouping, oxidanone()).apply(new int[]{0, 1}));
        Assertions.assertFalse(create(grouping, oxidanone()).apply(new int[]{1, 0}));
        Assertions.assertFalse(create(grouping, ethyleneGlycol()).apply(new int[]{0, 3}));
        Assertions.assertFalse(create(grouping, ethyleneGlycol()).apply(new int[]{3, 0}));
        Assertions.assertTrue(create(grouping, ethylAlcoholHydrate()).apply(new int[]{0, 3}));
        Assertions.assertTrue(create(grouping, ethylAlcoholHydrate()).apply(new int[]{3, 0}));
    }

    /** @cdk.inchi InChI=1/O2/c1-2 */
    static IAtomContainer oxidanone() {
        IAtomContainer m = SilentChemObjectBuilder.getInstance().newAtomContainer();
        m.addAtom(new Atom("O"));
        m.addAtom(new Atom("O"));
        m.addBond(0, 1, IBond.Order.DOUBLE);
        return m;
    }

    /** @cdk.inchi InChI=1/C2H6O2/c3-1-2-4/h3-4H,1-2H2 */
    static IAtomContainer ethyleneGlycol() {
        IAtomContainer m = SilentChemObjectBuilder.getInstance().newAtomContainer();
        m.addAtom(new Atom("O"));
        m.addAtom(new Atom("C"));
        m.addAtom(new Atom("C"));
        m.addAtom(new Atom("O"));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(1, 2, IBond.Order.SINGLE);
        m.addBond(2, 3, IBond.Order.SINGLE);
        return m;
    }

    /** InChI=1/C2H6O.H2O/c1-2-3;/h3H,2H2,1H3;1H2 */
    static IAtomContainer ethylAlcoholHydrate() {
        IAtomContainer m = SilentChemObjectBuilder.getInstance().newAtomContainer();
        m.addAtom(new Atom("O"));
        m.addAtom(new Atom("C"));
        m.addAtom(new Atom("C"));
        m.addAtom(new Atom("O"));
        m.addBond(1, 2, IBond.Order.SINGLE);
        m.addBond(2, 3, IBond.Order.SINGLE);
        return m;
    }

    static ComponentFilter create(int[] grouping, IAtomContainer container) {
        return new ComponentFilter(grouping, new ConnectedComponents(GraphUtil.toAdjList(container)).components());
    }

}
