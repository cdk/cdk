/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 1997-2005  The Chemistry Development Kit (CDK) project
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
import java.util.HashMap;

import org.openscience.cdk.interfaces.IAtom;

/**
 * Colors atoms using CPK color scheme {@cdk.cite BER2001}.
 *
 * @cdk.module  render
 * @cdk.keyword atom coloring, CPK
 */
public class CPKAtomColors implements AtomColorer, java.io.Serializable
{
    ////////////
    // CONSTANTS
    ////////////

    // CPK colours.
    private static final Color LIGHT_GREY = new Color(0xC8C8C8);
    private static final Color SKY_BLUE = new Color(0x8F8FFF);
    private static final Color RED = new Color(0xF00000);
    private static final Color YELLOW = new Color(0xFFC832);
    private static final Color WHITE = new Color(0xFFFFFF);
    private static final Color PINK = new Color(0xFFC0CB);
    private static final Color GOLDEN_ROD = new Color(0xDAA520);
    private static final Color BLUE = new Color(0x0000FF);
    private static final Color ORANGE = new Color(0xFFA500);
    private static final Color DARK_GREY = new Color(0x808090);
    private static final Color BROWN = new Color(0xA52A2A);
    private static final Color PURPLE = new Color(0xA020F0);
    private static final Color DEEP_PINK = new Color(0xFF1493);
    private static final Color GREEN = new Color(0x00FF00);
    private static final Color FIRE_BRICK = new Color(0xB22222);
    private static final Color FOREST_GREEN = new Color(0x228B22);

    // The atom color look-up table.
    private static final HashMap ATOM_COLORS = new HashMap();

    // Build table.
    static
    {
        // Colors keyed on (uppercase) atomic symbol.
        ATOM_COLORS.put("H", WHITE);
        ATOM_COLORS.put("HE", PINK);
        ATOM_COLORS.put("LI", FIRE_BRICK);
        ATOM_COLORS.put("B", GREEN);
        ATOM_COLORS.put("C", LIGHT_GREY);
        ATOM_COLORS.put("N", SKY_BLUE);
        ATOM_COLORS.put("O", RED);
        ATOM_COLORS.put("F", GOLDEN_ROD);
        ATOM_COLORS.put("NA", BLUE);
        ATOM_COLORS.put("MG", FOREST_GREEN);
        ATOM_COLORS.put("AL", DARK_GREY);
        ATOM_COLORS.put("SI", GOLDEN_ROD);
        ATOM_COLORS.put("P", ORANGE);
        ATOM_COLORS.put("S", YELLOW);
        ATOM_COLORS.put("CL", GREEN);
        ATOM_COLORS.put("CA", DARK_GREY);
        ATOM_COLORS.put("TI", DARK_GREY);
        ATOM_COLORS.put("CR", DARK_GREY);
        ATOM_COLORS.put("MN", DARK_GREY);
        ATOM_COLORS.put("FE", ORANGE);
        ATOM_COLORS.put("NI", BROWN);
        ATOM_COLORS.put("CU", BROWN);
        ATOM_COLORS.put("ZN", BROWN);
        ATOM_COLORS.put("BR", BROWN);
        ATOM_COLORS.put("AG", DARK_GREY);
        ATOM_COLORS.put("I", PURPLE);
        ATOM_COLORS.put("BA", ORANGE);
        ATOM_COLORS.put("AU", GOLDEN_ROD);

        // Colors keyed on atomic number.
        ATOM_COLORS.put(new Integer(1), ATOM_COLORS.get("H"));
        ATOM_COLORS.put(new Integer(2), ATOM_COLORS.get("HE"));
        ATOM_COLORS.put(new Integer(3), ATOM_COLORS.get("LI"));
        ATOM_COLORS.put(new Integer(5), ATOM_COLORS.get("B"));
        ATOM_COLORS.put(new Integer(6), ATOM_COLORS.get("C"));
        ATOM_COLORS.put(new Integer(7), ATOM_COLORS.get("N"));
        ATOM_COLORS.put(new Integer(8), ATOM_COLORS.get("O"));
        ATOM_COLORS.put(new Integer(9), ATOM_COLORS.get("F"));
        ATOM_COLORS.put(new Integer(11), ATOM_COLORS.get("NA"));
        ATOM_COLORS.put(new Integer(12), ATOM_COLORS.get("MG"));
        ATOM_COLORS.put(new Integer(13), ATOM_COLORS.get("AL"));
        ATOM_COLORS.put(new Integer(14), ATOM_COLORS.get("SI"));
        ATOM_COLORS.put(new Integer(15), ATOM_COLORS.get("P"));
        ATOM_COLORS.put(new Integer(16), ATOM_COLORS.get("S"));
        ATOM_COLORS.put(new Integer(17), ATOM_COLORS.get("CL"));
        ATOM_COLORS.put(new Integer(20), ATOM_COLORS.get("CA"));
        ATOM_COLORS.put(new Integer(22), ATOM_COLORS.get("TI"));
        ATOM_COLORS.put(new Integer(24), ATOM_COLORS.get("CR"));
        ATOM_COLORS.put(new Integer(25), ATOM_COLORS.get("MN"));
        ATOM_COLORS.put(new Integer(26), ATOM_COLORS.get("FE"));
        ATOM_COLORS.put(new Integer(28), ATOM_COLORS.get("NI"));
        ATOM_COLORS.put(new Integer(29), ATOM_COLORS.get("CU"));
        ATOM_COLORS.put(new Integer(30), ATOM_COLORS.get("ZN"));
        ATOM_COLORS.put(new Integer(35), ATOM_COLORS.get("BR"));
        ATOM_COLORS.put(new Integer(47), ATOM_COLORS.get("AG"));
        ATOM_COLORS.put(new Integer(53), ATOM_COLORS.get("I"));
        ATOM_COLORS.put(new Integer(56), ATOM_COLORS.get("BA"));
        ATOM_COLORS.put(new Integer(79), ATOM_COLORS.get("AU"));
    };

    //////////
    // METHODS
    //////////

    /**
     * Returns the font color for a given atom.
     *
     * @param a the atom.
     * @return A color for the atom.
     */
    public Color getAtomColor(IAtom a)
    {
        return getAtomColor(a, DEEP_PINK);
    }

    /**
     * Returns the font color for a given atom.
     *
     * @param a            the atom.
     * @param defaultColor a default color.
     * @return A color for the atom.  The default colour is used if none is
     *         found for the atom.
     */
    public Color getAtomColor(IAtom a, Color defaultColor)
    {
        Color c = defaultColor;
        Integer number = new Integer(a.getAtomicNumber());
        String symbol = a.getSymbol().toUpperCase();
        if (ATOM_COLORS.containsKey(number))
        {
            c = (Color) ATOM_COLORS.get(number);    // lookup by atomic number.
        }
        else if (ATOM_COLORS.containsKey(symbol))
        {
            c = (Color) ATOM_COLORS.get(symbol);    // lookup by atomic symbol.
        }

        return new Color(c.getRed(), c.getGreen(), c.getBlue());    // return a copy.
    }
}
