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
import java.util.HashMap;
import java.util.Map;

import org.openscience.cdk.config.Elements;
import org.openscience.cdk.interfaces.IAtom;

import static org.openscience.cdk.config.Elements.Aluminium;
import static org.openscience.cdk.config.Elements.Argon;
import static org.openscience.cdk.config.Elements.Barium;
import static org.openscience.cdk.config.Elements.Beryllium;
import static org.openscience.cdk.config.Elements.Boron;
import static org.openscience.cdk.config.Elements.Bromine;
import static org.openscience.cdk.config.Elements.Cadmium;
import static org.openscience.cdk.config.Elements.Caesium;
import static org.openscience.cdk.config.Elements.Calcium;
import static org.openscience.cdk.config.Elements.Carbon;
import static org.openscience.cdk.config.Elements.Chlorine;
import static org.openscience.cdk.config.Elements.Fluorine;
import static org.openscience.cdk.config.Elements.Francium;
import static org.openscience.cdk.config.Elements.Helium;
import static org.openscience.cdk.config.Elements.Hydrogen;
import static org.openscience.cdk.config.Elements.Iodine;
import static org.openscience.cdk.config.Elements.Iron;
import static org.openscience.cdk.config.Elements.Krypton;
import static org.openscience.cdk.config.Elements.Lithium;
import static org.openscience.cdk.config.Elements.Magnesium;
import static org.openscience.cdk.config.Elements.Neon;
import static org.openscience.cdk.config.Elements.Nitrogen;
import static org.openscience.cdk.config.Elements.Oxygen;
import static org.openscience.cdk.config.Elements.Phosphorus;
import static org.openscience.cdk.config.Elements.Potassium;
import static org.openscience.cdk.config.Elements.Radium;
import static org.openscience.cdk.config.Elements.Rubidium;
import static org.openscience.cdk.config.Elements.Silver;
import static org.openscience.cdk.config.Elements.Sodium;
import static org.openscience.cdk.config.Elements.Strontium;
import static org.openscience.cdk.config.Elements.Sulfur;
import static org.openscience.cdk.config.Elements.Titanium;
import static org.openscience.cdk.config.Elements.Unknown;
import static org.openscience.cdk.config.Elements.Xenon;

/**
 * Gives a short table of atom colors for 2D display. The coloring is loosely
 * based on CPK.
 *
 * The internal color map can be modified by invoking the set method. For convenience the set method
 * returns the colorer instance for chaining.
 * 
 * <pre>{@code
 * IAtomColorer colorer = new CDK2DAtomColors().set("H", Color.LIGHT_GRAY)
 *                                             .set("O", Color.RED.lighter());
 * }</pre>
 *
 * @cdk.module render
 * @cdk.githash
 * @see <a href="http://en.wikipedia.org/wiki/CPK_coloring">CPK coloring</a>
 */
public class CDK2DAtomColors implements IAtomColorer, java.io.Serializable {

    private static final long         serialVersionUID = 6712994043820219426L;

    private final static Color        OFF_BLACK        = new Color(0x333333);
    private final static Color        SKY_BLUE         = new Color(0x455FFF);
    private final static Color        PASTEL_RED       = new Color(0xFF4B54);
    private final static Color        LUMINOUS_GREEN   = new Color(0x44FF54);
    private final static Color        MAROON           = new Color(0x983534);
    private final static Color        VIOLET           = new Color(0x983E91);
    private final static Color        CYAN             = new Color(0x67C2FF);
    private final static Color        ORANGE           = new Color(0xFFB24A);
    private final static Color        MUSTARD          = new Color(0xFFDD0B);
    private final static Color        PEACH            = new Color(0xFFB8A0);
    private final static Color        FOREST_GREEN     = new Color(0x2A9E3B);
    private final static Color        SILVER           = new Color(0xC0C0C0);
    private final static Color        DARK_ORANGE      = new Color(0xFF9A12);
    private final static Color        PINK             = new Color(0xFF59AE);

    // Mapping of atomic numbers colors to their color
    private final Map<Integer, Color> colorMap         = new HashMap<Integer, Color>(100);

    public CDK2DAtomColors() {
        // Superatoms/aliases 
        set(Unknown, OFF_BLACK);
        // organic
        set(Hydrogen, OFF_BLACK);
        set(Carbon, OFF_BLACK);
        set(Nitrogen, SKY_BLUE);
        set(Oxygen, PASTEL_RED);
        set(Phosphorus, ORANGE);
        set(Sulfur, MUSTARD);
        set(Boron, PEACH);
        // halogens
        set(Fluorine, LUMINOUS_GREEN);
        set(Chlorine, LUMINOUS_GREEN);
        set(Bromine, MAROON);
        set(Iodine, VIOLET);
        // nobel gases
        set(Helium, CYAN);
        set(Neon, CYAN);
        set(Argon, CYAN);
        set(Xenon, CYAN);
        set(Krypton, CYAN);
        // alkali metals
        set(Lithium, VIOLET);
        set(Sodium, VIOLET);
        set(Potassium, VIOLET);
        set(Rubidium, VIOLET);
        set(Caesium, VIOLET);
        set(Francium, VIOLET);
        // alkali earth metals
        set(Beryllium, FOREST_GREEN);
        set(Magnesium, FOREST_GREEN);
        set(Calcium, FOREST_GREEN);
        set(Strontium, FOREST_GREEN);
        set(Barium, FOREST_GREEN);
        set(Radium, FOREST_GREEN);
        // others
        set(Titanium, SILVER);
        set(Aluminium, SILVER);
        set(Silver, SILVER);
        set(Iron, DARK_ORANGE);
    }

    /**
     * Set the color of the specified element.
     *
     * @param symbol symbol of element
     * @param color  the color
     * @return self reference for method chaining
     */
    CDK2DAtomColors set(String symbol, Color color) {
        return set(Elements.ofString(symbol), color);
    }

    /**
     * Set the color of the specified element.
     *
     * @param element instance of element
     * @param color   the color
     * @return self reference for method chaining
     */
    CDK2DAtomColors set(Elements element, Color color) {
        return set(element.number(), color);
    }

    /**
     * Set the color of the specified element.
     *
     * @param atomicNumber atomic number of element
     * @param color        the color
     * @return self reference for method chaining
     */
    CDK2DAtomColors set(int atomicNumber, Color color) {
        colorMap.put(atomicNumber, color);
        return this;
    }

    /**
     * Returns the CDK 2D color for the given atom's element.
     *
     * @param atom IAtom to get a color for
     * @return the atom's color according to this coloring scheme.
     */
    @Override
    public Color getAtomColor(IAtom atom) {
        return getAtomColor(atom, PINK);
    }

    /**
     * Returns the CDK 2D color for the given atom's element, or defaults to the given color if no
     * color is defined.
     *
     * @param atom         IAtom to get a color for
     * @param defaultColor Color returned if this scheme does not define a color for the passed
     *                     IAtom
     * @return the atom's color according to this coloring scheme.
     */
    @Override
    public Color getAtomColor(IAtom atom, Color defaultColor) {
        Color color = null;
        if (atom.getAtomicNumber() != null) color = colorMap.get(atom.getAtomicNumber());
        if (color != null) return color;
        if (atom.getAtomicNumber() != null) color = colorMap.get(Elements.ofString(atom.getSymbol()).number());
        if (color != null) return color;
        return defaultColor;
    }
}
