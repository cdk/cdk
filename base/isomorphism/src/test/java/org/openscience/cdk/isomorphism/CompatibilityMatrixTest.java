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

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author John May
 * @cdk.module test-isomorphism
 */
public class CompatibilityMatrixTest {

    @Test
    public void accessAndModify() throws Exception {
        CompatibilityMatrix m = new CompatibilityMatrix(5, 5);
        assertFalse(m.get(0, 1));
        assertFalse(m.get(0, 4));
        assertFalse(m.get(1, 0));
        assertFalse(m.get(1, 3));
        m.set(0, 1);
        m.set(0, 4);
        m.set(1, 0);
        m.set(1, 3);
        assertTrue(m.get(0, 1));
        assertTrue(m.get(0, 4));
        assertTrue(m.get(1, 0));
        assertTrue(m.get(1, 3));
    }

    @Test
    public void mark() throws Exception {
        CompatibilityMatrix m = new CompatibilityMatrix(5, 5);
        m.set(0, 1);
        m.set(0, 2);
        m.set(0, 4);
        m.set(1, 0);
        m.set(1, 3);
        assertTrue(m.get(0, 1));
        assertTrue(m.get(0, 4));
        m.mark(0, 1, -1);
        m.mark(0, 4, -4);
        m.mark(1, 3, -6);
        assertFalse(m.get(0, 1));
        assertFalse(m.get(0, 4));
        assertFalse(m.get(1, 3));
        m.resetRows(0, -1);
        assertTrue(m.get(0, 1));
        assertFalse(m.get(0, 4));
        assertFalse(m.get(1, 3));
        m.resetRows(0, -4);
        assertTrue(m.get(0, 1));
        assertTrue(m.get(0, 4));
        assertFalse(m.get(1, 3));
        m.resetRows(0, -6);
        assertTrue(m.get(0, 1));
        assertTrue(m.get(0, 4));
        assertTrue(m.get(1, 3));
    }

    @Test
    public void markRow() throws Exception {
        CompatibilityMatrix m = new CompatibilityMatrix(5, 5);
        m.set(0, 1);
        m.set(0, 2);
        m.set(0, 4);
        assertThat(m.fix()[0], is(new int[]{0, 1, 1, 0, 1}));
        m.markRow(0, -1);
        assertThat(m.fix()[0], is(new int[]{0, -1, -1, 0, -1}));
    }

    @Test
    public void fix() throws Exception {
        CompatibilityMatrix m = new CompatibilityMatrix(5, 5);
        m.set(0, 1);
        m.set(0, 2);
        m.set(0, 4);
        m.set(1, 0);
        m.set(1, 3);
        m.set(2, 4);
        assertThat(m.fix(), is(new int[][]{{0, 1, 1, 0, 1}, {1, 0, 0, 1, 0}, {0, 0, 0, 0, 1}, {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},}));
    }
}
