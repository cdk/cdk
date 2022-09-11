/* Copyright (C) 2009  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. All we ask is that proper credit is given for our work,
 * which includes - but is not limited to - adding the above copyright notice to
 * the beginning of your source code files, and to any copyright notice that you
 * may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.renderer.color;

import java.awt.Color;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.interfaces.IAtom;

/**
 * @cdk.module test-render
 */
class PartialAtomicChargeColorsTest extends CDKTestCase {

    @Test
    void testGetAtomColor() {
        PartialAtomicChargeColors colors = new PartialAtomicChargeColors();
        Assertions.assertNotNull(colors);
        IAtom hydrogen = new Atom("H");
        hydrogen.setAtomicNumber(1);
        Assertions.assertEquals(Color.white, colors.getAtomColor(hydrogen));
        IAtom helium = new Atom("He");
        helium.setAtomicNumber(2);
        Assertions.assertEquals(Color.white, colors.getAtomColor(helium));
    }

    @Test
    void testGetDefaultAtomColor() {
        PartialAtomicChargeColors colors = new PartialAtomicChargeColors();

        Assertions.assertNotNull(colors);
        IAtom imaginary = new PseudoAtom("Ix");
        Assertions.assertEquals(Color.ORANGE, colors.getAtomColor(imaginary, Color.ORANGE));
    }
}
