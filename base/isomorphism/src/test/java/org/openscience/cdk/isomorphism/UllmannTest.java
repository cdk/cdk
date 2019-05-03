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

import com.google.common.collect.FluentIterable;
import org.junit.Test;
import org.openscience.cdk.templates.TestMoleculeFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Simple tests for exact and non exact matching.
 *
 * @author John May
 * @cdk.module test-isomorphism
 */
public class UllmannTest {

    @Test
    public void benzeneSubsearch() throws Exception {
        int[] match = Ullmann.findSubstructure(TestMoleculeFactory.makeBenzene()).match(
                TestMoleculeFactory.makeNaphthalene());
        assertThat(match, is(new int[]{2, 7, 6, 5, 4, 3}));
        int count = FluentIterable.from(
                Ullmann.findSubstructure(TestMoleculeFactory.makeBenzene()).matchAll(
                        TestMoleculeFactory.makeNaphthalene())).size();
        assertThat(count, is(6)); // note: aromatic one would be 24
    }

    @Test
    public void napthaleneSubsearch() throws Exception {
        int[] match = Ullmann.findSubstructure(TestMoleculeFactory.makeNaphthalene()).match(
                TestMoleculeFactory.makeBenzene());
        assertThat(match, is(new int[0]));
        int count = FluentIterable.from(
                Ullmann.findSubstructure(TestMoleculeFactory.makeNaphthalene()).matchAll(
                        TestMoleculeFactory.makeBenzene())).size();
        assertThat(count, is(0));
    }
}
