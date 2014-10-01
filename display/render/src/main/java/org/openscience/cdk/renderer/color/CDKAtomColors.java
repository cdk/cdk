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
 */
package org.openscience.cdk.renderer.color;

import java.awt.Color;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.interfaces.IAtom;

/**
 * Gives a short table of atom colors for 3D display.
 *
 * @cdk.module render
 * @cdk.githash
 */
@TestClass("org.openscience.cdk.renderer.color.CDKAtomColorsTest")
public class CDKAtomColors implements IAtomColorer {

    private final static Color HYDROGEN   = Color.white;
    private final static Color CARBON     = Color.black;
    private final static Color NITROGEN   = Color.blue;
    private final static Color OXYGEN     = Color.red;
    private final static Color PHOSPHORUS = Color.green;
    private final static Color SULPHUR    = Color.yellow;
    private final static Color CHLORINE   = Color.magenta;

    private final static Color DEFAULT    = Color.darkGray;

    /**
     * Returns the CDK scheme color for the given atom's element.
     *
     * @param atom         IAtom to get a color for
     * @return             the atom's color according to this coloring scheme.
     */
    @TestMethod("testGetAtomColor")
    @Override
    public Color getAtomColor(IAtom atom) {
        return getAtomColor(atom, DEFAULT);
    }

    /**
     * Returns the CDK scheme color for the given atom's element, or
     * defaults to the given color if no color is defined.
     *
     * @param atom         IAtom to get a color for
     * @param defaultColor Color returned if this scheme does not define
     *                     a color for the passed IAtom
     * @return             the atom's color according to this coloring scheme.
     */
    @TestMethod("testGetDefaultAtomColor")
    @Override
    public Color getAtomColor(IAtom atom, Color defaultColor) {
        Color color = defaultColor;
        if (atom.getAtomicNumber() == null) return defaultColor;
        int atomnumber = atom.getAtomicNumber();
        switch (atomnumber) {
            case 1:
                color = HYDROGEN;
                break;
            case 6:
                color = CARBON;
                break;
            case 7:
                color = NITROGEN;
                break;
            case 8:
                color = OXYGEN;
                break;
            case 15:
                color = PHOSPHORUS;
                break;
            case 16:
                color = SULPHUR;
                break;
            case 17:
                color = CHLORINE;
                break;
        }
        return color;
    }
}
