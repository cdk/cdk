/* Copyright (C) 1997-2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.renderer.color;

import java.awt.Color;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.interfaces.IAtom;

/**
 * Class defining the color which with atoms are colored.
 *
 * <p>This scheme used the atomic partial charge to determine
 * the Atom's color:
 * uncharged atoms are colored white, positively charged
 * atoms are blue, and negatively charge atoms are red.
 *
 * @cdk.module  render
 * @cdk.githash
 * @cdk.keyword atom coloring, partial charges
 */
@TestClass("org.openscience.cdk.renderer.color.PartialAtomicChargeColorsTest")
public class PartialAtomicChargeColors implements IAtomColorer {

    /**
     * Returns the a color reflecting the given atom's partial charge.
     *
     * @param atom         IAtom to get a color for
     * @return             the color for the given atom.
     */
    @TestMethod("testGetAtomColor")
    @Override
    public Color getAtomColor(IAtom atom) {
        return getAtomColor(atom, Color.white);
    }

    /**
     * Returns the a color reflecting the given atom's partial charge, or
     * defaults to the given color if no color is defined.
     *
     * @param atom         IAtom to get a color for
     * @param defaultColor Color returned if this scheme does not define
     *                     a color for the passed IAtom
     * @return             the color for the given atom.
     */
    @TestMethod("testGetDefaultAtomColor")
    @Override
    public Color getAtomColor(IAtom atom, Color defaultColor) {
        Color color = defaultColor;
        if (atom.getCharge() == null) return defaultColor;
        double charge = atom.getCharge();
        if (charge > 0.0) {
            if (charge < 1.0) {
                int index = 255 - (int) (charge * 255.0);
                color = new Color(index, index, 255);
            } else {
                color = Color.blue;
            }
        } else if (charge < 0.0) {
            if (charge > -1.0) {
                int index = 255 + (int) (charge * 255.0);
                color = new Color(255, index, index);
            } else {
                color = Color.red;
            }
        }
        return color;
    }
}
