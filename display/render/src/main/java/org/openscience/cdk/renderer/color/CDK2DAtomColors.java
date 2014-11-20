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

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.config.Elements;
import org.openscience.cdk.interfaces.IAtom;

import static org.openscience.cdk.config.Elements.Carbon;
import static org.openscience.cdk.config.Elements.Hydrogen;
import static org.openscience.cdk.config.Elements.Nitrogen;
import static org.openscience.cdk.config.Elements.Oxygen;
import static org.openscience.cdk.config.Elements.Phosphorus;
import static org.openscience.cdk.config.Elements.Sulfur;

/**
 * Gives a short table of atom colors for 2D display.
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
 */
@TestClass("org.openscience.cdk.renderer.color.CDK2DAtomColorsTest")
public class CDK2DAtomColors implements IAtomColorer, java.io.Serializable {

    private static final long         serialVersionUID = 6712994043820219426L;

    private final static Color        DEFAULT          = Color.black;

    // Mapping of atomic numbers colors to their color
    private final Map<Integer, Color> colorMap         = new HashMap<Integer, Color>(100);

    public CDK2DAtomColors() {
        set(Hydrogen, Color.black);
        set(Carbon, Color.black);
        set(Nitrogen, Color.blue);
        set(Oxygen, Color.red);
        set(Phosphorus, Color.green.darker());
        set(Sulfur, Color.yellow.darker());
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
    @TestMethod("testGetAtomColor")
    @Override
    public Color getAtomColor(IAtom atom) {
        return getAtomColor(atom, DEFAULT);
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
    @TestMethod("testGetDefaultAtomColor")
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
