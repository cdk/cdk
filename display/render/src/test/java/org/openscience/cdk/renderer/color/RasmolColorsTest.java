/*
 * Copyright (C) 2009  Mark Rijnbeek <mark_rynbeek@users.sf.net>
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
package org.openscience.cdk.renderer.color;

import java.awt.Color;

import org.junit.Assert;
import org.junit.Test;

import org.openscience.cdk.Atom;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.interfaces.IAtom;

/**
 * @cdk.module test-render
 */
public class RasmolColorsTest extends CDKTestCase {

    @Test
    public void testGetAtomColor() {
        RasmolColors colors = new RasmolColors();

        Assert.assertNotNull(colors);
        IAtom sulfur = new Atom("S");
        Assert.assertEquals(new Color(255, 200, 50), colors.getAtomColor(sulfur));

        IAtom helium = new Atom("He");
        Assert.assertEquals(new Color(255, 192, 203), colors.getAtomColor(helium));
    }

    @Test
    public void testGetDefaultAtomColor() {
        RasmolColors colors = new RasmolColors();

        Assert.assertNotNull(colors);
        IAtom imaginary = new PseudoAtom("Ix");
        Assert.assertEquals(Color.ORANGE, colors.getAtomColor(imaginary, Color.ORANGE));
    }

}
