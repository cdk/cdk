/* Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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
 *
 */
package org.openscience.cdk.debug;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.test.interfaces.AbstractReactionTest;
import org.openscience.cdk.interfaces.IReaction;

/**
 * Checks the functionality of the {@link DebugReaction}.
 *
 */
class DebugReactionTest extends AbstractReactionTest {

    @BeforeAll
    static void setUp() {
        setTestObjectBuilder(DebugReaction::new);
    }

    @Test
    void testDebugReaction() {
        IReaction polymer = new DebugReaction();
        Assertions.assertTrue(polymer instanceof DebugReaction);
    }
}
