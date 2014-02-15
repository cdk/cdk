/*
 * Copyright (c) 2014 European Bioinformatics Institute (EMBL-EBI)
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

package org.openscience.cdk.graph;

import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author John May
 * @cdk.module test-standard
 */
public class MatchingTest {

    @Ignore("no operation performed")
    public void nop() {
    }

    @Test public void match() {
        Matching matching = Matching.withCapacity(8);
        matching.match(2, 5);
        matching.match(6, 7);
        assertTrue(matching.matched(2));
        assertTrue(matching.matched(5));
        assertTrue(matching.matched(6));
        assertTrue(matching.matched(7));
        assertThat(matching.other(2), is(5));
        assertThat(matching.other(5), is(2));
        assertThat(matching.other(6), is(7));
        assertThat(matching.other(7), is(6));
    }

    @Test public void replace() {
        Matching matching = Matching.withCapacity(8);
        matching.match(2, 5);
        matching.match(6, 7);
        matching.match(5, 6);
        assertFalse(matching.matched(2));
        assertTrue(matching.matched(5));
        assertTrue(matching.matched(6));
        assertFalse(matching.matched(7));
        assertThat(matching.other(5), is(6));
        assertThat(matching.other(6), is(5));
    }

    @Test(expected = IllegalArgumentException.class) public void other() {
        Matching matching = Matching.withCapacity(8);
        matching.match(2, 5);
        matching.match(6, 7);
        matching.match(5, 6);
        matching.other(2); // 2 is unmatched!   
    }

    @Test public void unmatch() {
        Matching matching = Matching.withCapacity(5);
        matching.match(2, 4);
        matching.unmatch(4); // also unmatches 2
        assertFalse(matching.matched(4));
        assertFalse(matching.matched(2));
    }

    @Test public void string() {
        Matching matching = Matching.withCapacity(9);
        matching.match(1, 3);
        matching.match(4, 8);
        assertThat(matching.toString(),
                   is("[1=3, 4=8]"));
    }

}
