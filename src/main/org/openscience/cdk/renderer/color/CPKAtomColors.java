/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 1997-2007  Chris Pudney
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

import org.openscience.cdk.interfaces.IAtom;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Colors atoms using CPK color scheme {@cdk.cite BER2001}.
 *
 * @cdk.module  render
 * @cdk.svnrev  $Revision$
 * @cdk.keyword atom coloring, CPK
 */
public class CPKAtomColors implements IAtomColorer, java.io.Serializable
{
    private static final long serialVersionUID = -3205785984391537452L;
    
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
    private static final Map<Integer, Color> ATOM_COLORS_MASSNUM = new HashMap<Integer, Color>();
    private static final Map<String, Color> ATOM_COLORS_SYMBOL = new HashMap<String, Color>();

    // Build table.
    static
    {
        // Colors keyed on (uppercase) atomic symbol.
        ATOM_COLORS_SYMBOL.put("H", WHITE);
        ATOM_COLORS_SYMBOL.put("HE", PINK);
        ATOM_COLORS_SYMBOL.put("LI", FIRE_BRICK);
        ATOM_COLORS_SYMBOL.put("B", GREEN);
        ATOM_COLORS_SYMBOL.put("C", LIGHT_GREY);
        ATOM_COLORS_SYMBOL.put("N", SKY_BLUE);
        ATOM_COLORS_SYMBOL.put("O", RED);
        ATOM_COLORS_SYMBOL.put("F", GOLDEN_ROD);
        ATOM_COLORS_SYMBOL.put("NA", BLUE);
        ATOM_COLORS_SYMBOL.put("MG", FOREST_GREEN);
        ATOM_COLORS_SYMBOL.put("AL", DARK_GREY);
        ATOM_COLORS_SYMBOL.put("SI", GOLDEN_ROD);
        ATOM_COLORS_SYMBOL.put("P", ORANGE);
        ATOM_COLORS_SYMBOL.put("S", YELLOW);
        ATOM_COLORS_SYMBOL.put("CL", GREEN);
        ATOM_COLORS_SYMBOL.put("CA", DARK_GREY);
        ATOM_COLORS_SYMBOL.put("TI", DARK_GREY);
        ATOM_COLORS_SYMBOL.put("CR", DARK_GREY);
        ATOM_COLORS_SYMBOL.put("MN", DARK_GREY);
        ATOM_COLORS_SYMBOL.put("FE", ORANGE);
        ATOM_COLORS_SYMBOL.put("NI", BROWN);
        ATOM_COLORS_SYMBOL.put("CU", BROWN);
        ATOM_COLORS_SYMBOL.put("ZN", BROWN);
        ATOM_COLORS_SYMBOL.put("BR", BROWN);
        ATOM_COLORS_SYMBOL.put("AG", DARK_GREY);
        ATOM_COLORS_SYMBOL.put("I", PURPLE);
        ATOM_COLORS_SYMBOL.put("BA", ORANGE);
        ATOM_COLORS_SYMBOL.put("AU", GOLDEN_ROD);

        // Colors keyed on atomic number.
        ATOM_COLORS_MASSNUM.put(Integer.valueOf(1), ATOM_COLORS_SYMBOL.get("H"));
        ATOM_COLORS_MASSNUM.put(Integer.valueOf(2), ATOM_COLORS_SYMBOL.get("HE"));
        ATOM_COLORS_MASSNUM.put(Integer.valueOf(3), ATOM_COLORS_SYMBOL.get("LI"));
        ATOM_COLORS_MASSNUM.put(Integer.valueOf(5), ATOM_COLORS_SYMBOL.get("B"));
        ATOM_COLORS_MASSNUM.put(Integer.valueOf(6), ATOM_COLORS_SYMBOL.get("C"));
        ATOM_COLORS_MASSNUM.put(Integer.valueOf(7), ATOM_COLORS_SYMBOL.get("N"));
        ATOM_COLORS_MASSNUM.put(Integer.valueOf(8), ATOM_COLORS_SYMBOL.get("O"));
        ATOM_COLORS_MASSNUM.put(Integer.valueOf(9), ATOM_COLORS_SYMBOL.get("F"));
        ATOM_COLORS_MASSNUM.put(Integer.valueOf(11), ATOM_COLORS_SYMBOL.get("NA"));
        ATOM_COLORS_MASSNUM.put(Integer.valueOf(12), ATOM_COLORS_SYMBOL.get("MG"));
        ATOM_COLORS_MASSNUM.put(Integer.valueOf(13), ATOM_COLORS_SYMBOL.get("AL"));
        ATOM_COLORS_MASSNUM.put(Integer.valueOf(14), ATOM_COLORS_SYMBOL.get("SI"));
        ATOM_COLORS_MASSNUM.put(Integer.valueOf(15), ATOM_COLORS_SYMBOL.get("P"));
        ATOM_COLORS_MASSNUM.put(Integer.valueOf(16), ATOM_COLORS_SYMBOL.get("S"));
        ATOM_COLORS_MASSNUM.put(Integer.valueOf(17), ATOM_COLORS_SYMBOL.get("CL"));
        ATOM_COLORS_MASSNUM.put(Integer.valueOf(20), ATOM_COLORS_SYMBOL.get("CA"));
        ATOM_COLORS_MASSNUM.put(Integer.valueOf(22), ATOM_COLORS_SYMBOL.get("TI"));
        ATOM_COLORS_MASSNUM.put(Integer.valueOf(24), ATOM_COLORS_SYMBOL.get("CR"));
        ATOM_COLORS_MASSNUM.put(Integer.valueOf(25), ATOM_COLORS_SYMBOL.get("MN"));
        ATOM_COLORS_MASSNUM.put(Integer.valueOf(26), ATOM_COLORS_SYMBOL.get("FE"));
        ATOM_COLORS_MASSNUM.put(Integer.valueOf(28), ATOM_COLORS_SYMBOL.get("NI"));
        ATOM_COLORS_MASSNUM.put(Integer.valueOf(29), ATOM_COLORS_SYMBOL.get("CU"));
        ATOM_COLORS_MASSNUM.put(Integer.valueOf(30), ATOM_COLORS_SYMBOL.get("ZN"));
        ATOM_COLORS_MASSNUM.put(Integer.valueOf(35), ATOM_COLORS_SYMBOL.get("BR"));
        ATOM_COLORS_MASSNUM.put(Integer.valueOf(47), ATOM_COLORS_SYMBOL.get("AG"));
        ATOM_COLORS_MASSNUM.put(Integer.valueOf(53), ATOM_COLORS_SYMBOL.get("I"));
        ATOM_COLORS_MASSNUM.put(Integer.valueOf(56), ATOM_COLORS_SYMBOL.get("BA"));
        ATOM_COLORS_MASSNUM.put(Integer.valueOf(79), ATOM_COLORS_SYMBOL.get("AU"));
    };

    //////////
    // METHODS
    //////////

    /**
     * Returns the font color for atom given atom.
     *
     * @param atom the atom.
     * @return A color for the atom.
     */
    public Color getAtomColor(IAtom atom)
    {
        return getAtomColor(atom, DEEP_PINK);
    }

    /**
     * Returns the font color for atom given atom.
     *
     * @param atom            the atom.
     * @param defaultColor atom default color.
     * @return A color for the atom.  The default colour is used if none is
     *         found for the atom.
     */
    public Color getAtomColor(IAtom atom, Color defaultColor)
    {
        Color color = defaultColor;
        String symbol = atom.getSymbol().toUpperCase();
        if (ATOM_COLORS_MASSNUM.containsKey(atom.getAtomicNumber()))
        {
            color = ATOM_COLORS_MASSNUM.get(atom.getAtomicNumber());    // lookup by atomic number.
        }
        else if (ATOM_COLORS_SYMBOL.containsKey(symbol))
        {
            color = ATOM_COLORS_SYMBOL.get(symbol);    // lookup by atomic symbol.
        }

        return new Color(color.getRed(), color.getGreen(), color.getBlue());    // return atom copy.
    }
}
