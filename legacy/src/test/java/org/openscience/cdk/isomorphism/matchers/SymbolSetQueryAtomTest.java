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
package org.openscience.cdk.isomorphism.matchers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;

/**
 * Checks the functionality of the IsomorphismTester
 *
 * @cdk.module test-isomorphism
 */
class SymbolSetQueryAtomTest extends CDKTestCase {

    private static SymbolSetQueryAtom symbolSet = null;

    @BeforeAll
    static void setUp() {
        symbolSet = new SymbolSetQueryAtom(DefaultChemObjectBuilder.getInstance());
        symbolSet.addSymbol("C");
        symbolSet.addSymbol("Fe");
    }

    @Test
    void testMatches() {
        Atom c = new Atom("C");
        Atom n = new Atom("N");
        Assertions.assertTrue(symbolSet.matches(c));
        Assertions.assertFalse(symbolSet.matches(n));
    }

    @Test
    void testRemoveSymbol() {
        symbolSet.removeSymbol("Fe");
        Assertions.assertEquals(1, symbolSet.getSymbolSet().size());
        Assertions.assertFalse(symbolSet.hasSymbol("Fe"));
        Assertions.assertTrue(symbolSet.hasSymbol("C"));
        symbolSet.addSymbol("Fe");
    }

    @Test
    void testHasSymbol() {
        Assertions.assertTrue(symbolSet.hasSymbol("C"));
        Assertions.assertFalse(symbolSet.hasSymbol("N"));
    }

}
