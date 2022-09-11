/* Copyright (C) 2007  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.ringsearch.cyclebasis;

import org._3pq.jgrapht.graph.SimpleGraph;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.test.CDKTestCase;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * @cdk.module test-standard
 */
class SimpleCycleTest extends CDKTestCase {

    SimpleCycleTest() {
        super();
    }

    @Test
    void testSimpleCycle_UndirectedGraph_Collection() {
        SimpleCycle cycle = new SimpleCycle(new SimpleGraph(), new ArrayList());
        Assertions.assertNotNull(cycle);
    }

    @Test
    void testSimpleCycle_UndirectedGraph_Set() {
        SimpleCycle cycle = new SimpleCycle(new SimpleGraph(), new HashSet());
        Assertions.assertNotNull(cycle);
    }

}
