/* Copyright (C) 2024  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
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
package org.openscience.cdk.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.interfaces.IAtom;

/**
 * @cdk.module test-core
 */
class ElementsTest {

    @Test
    public void testIsMetal_int() {
    	Assertions.assertFalse(Elements.isMetal((int)Elements.CARBON.getAtomicNumber()));
    	Assertions.assertTrue(Elements.isMetal((int)Elements.IRON.getAtomicNumber()));
    }

    @Test
    public void testIsMetal_IAtom() {
    	Assertions.assertFalse(Elements.isMetal(new Atom(Elements.CARBON)));
    	Assertions.assertTrue(Elements.isMetal(new Atom(Elements.IRON)));
    }

    @Test
    public void testIsMetal_int_negativeValues() {
    	// negative atomic numbers are by implementation 'metal'
    	Assertions.assertTrue(Elements.isMetal(-12));
    }

    @Test
    public void testElectronegativity() {
    	Assertions.assertTrue(Math.abs(2.55 - Elements.Carbon.electronegativity().doubleValue()) < 0.01);
    }

    @Test
    public void testIsMetalloid_int() {
        Assertions.assertTrue(Elements.isMetalloid((int)Elements.BORON.getAtomicNumber()));
        Assertions.assertTrue(Elements.isMetalloid((int)Elements.SILICON.getAtomicNumber()));
        Assertions.assertTrue(Elements.isMetalloid((int)Elements.GERMANIUM.getAtomicNumber()));
        Assertions.assertTrue(Elements.isMetalloid((int)Elements.ARSENIC.getAtomicNumber()));
        Assertions.assertTrue(Elements.isMetalloid((int)Elements.ANTIMONY.getAtomicNumber()));
        Assertions.assertTrue(Elements.isMetalloid((int)Elements.TELLURIUM.getAtomicNumber()));
        Assertions.assertTrue(Elements.isMetalloid((int)Elements.ASTATINE.getAtomicNumber()));
        Assertions.assertFalse(Elements.isMetalloid((int)Elements.Unknown.toIElement().getAtomicNumber()));
        Assertions.assertFalse(Elements.isMetalloid(IAtom.Wildcard));
        Assertions.assertFalse(Elements.isMetalloid((int)Elements.CARBON.getAtomicNumber()));
        Assertions.assertFalse(Elements.isMetalloid((int)Elements.HYDROGEN.getAtomicNumber()));
        Assertions.assertFalse(Elements.isMetalloid((int)Elements.NEON.getAtomicNumber()));
        Assertions.assertFalse(Elements.isMetalloid((int)Elements.PHOSPHORUS.getAtomicNumber()));
        Assertions.assertFalse(Elements.isMetalloid((int)Elements.SODIUM.getAtomicNumber()));
        Assertions.assertFalse(Elements.isMetalloid((int)Elements.GOLD.getAtomicNumber()));
        Assertions.assertFalse(Elements.isMetalloid(-12));
    }

    @Test
    public void testIsMetalloid_IAtom() {
        Assertions.assertTrue(Elements.isMetalloid(new Atom(Elements.BORON)));
        Assertions.assertTrue(Elements.isMetalloid(new Atom(Elements.SILICON)));
        Assertions.assertFalse(Elements.isMetalloid(new Atom(Elements.Unknown.toIElement())));
        Assertions.assertFalse(Elements.isMetalloid(new Atom(IAtom.Wildcard)));
        Assertions.assertFalse(Elements.isMetalloid(new Atom(Elements.SODIUM)));
        Assertions.assertFalse(Elements.isMetalloid(new Atom(Elements.GOLD)));
    }
}
