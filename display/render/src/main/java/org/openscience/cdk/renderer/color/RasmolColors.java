/* Copyright (C) 2009  Mark Rijnbeek <mark_rynbeek@users.sf.net>
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

import org.openscience.cdk.interfaces.IAtom;

/**
 * Atom coloring following RasMol/Chime Color scheme
 * <a href="http://www.umass.edu/microbio/rasmol/rascolor.htm"
 * >http://www.umass.edu/microbio/rasmol/rascolor.htm</a>.
 *
 * @cdk.module render
 * @cdk.githash
 */
public class RasmolColors implements IAtomColorer, java.io.Serializable {

    private static final long         serialVersionUID = 2588969984094169759L;

    private final static Color        DEFAULT          = new Color(255, 20, 147);

    private static Map<String, Color> colorMap;

    /*
     * Color map with RasMol/Chime Color RGB Values. Excepted H and C (too
     * light).
     */
    static {
        colorMap = new HashMap<String, Color>();

        colorMap.put("C", new Color(144, 144, 144));
        colorMap.put("H", new Color(144, 144, 144));
        colorMap.put("O", new Color(240, 0, 0));
        colorMap.put("N", new Color(143, 143, 255));
        colorMap.put("S", new Color(255, 200, 50));
        colorMap.put("Cl", new Color(0, 255, 0));
        colorMap.put("B", new Color(0, 255, 0));
        colorMap.put("P", new Color(255, 165, 0));
        colorMap.put("Fe", new Color(255, 165, 0));
        colorMap.put("Ba", new Color(255, 165, 0));
        colorMap.put("Na", new Color(0, 0, 255));
        colorMap.put("Mg", new Color(34, 139, 34));
        colorMap.put("Zn", new Color(165, 42, 42));
        colorMap.put("Cu", new Color(165, 42, 42));
        colorMap.put("Ni", new Color(165, 42, 42));
        colorMap.put("Br", new Color(165, 42, 42));
        colorMap.put("Ca", new Color(128, 128, 144));
        colorMap.put("Mn", new Color(128, 128, 144));
        colorMap.put("Al", new Color(128, 128, 144));
        colorMap.put("Ti", new Color(128, 128, 144));
        colorMap.put("Cr", new Color(128, 128, 144));
        colorMap.put("Ag", new Color(128, 128, 144));
        colorMap.put("F", new Color(218, 165, 32));
        colorMap.put("Si", new Color(218, 165, 32));
        colorMap.put("Au", new Color(218, 165, 32));
        colorMap.put("I", new Color(160, 32, 240));
        colorMap.put("Li", new Color(178, 34, 34));
        colorMap.put("He", new Color(255, 192, 203));

    }

    /**
     * Returns the Rasmol color for the given atom's element.
     *
     * @param atom IAtom to get a color for
     * @return     the atom's color according to this coloring scheme.
     */
    @Override
    public Color getAtomColor(IAtom atom) {
        return getAtomColor(atom, DEFAULT);
    }

    /**
     * Returns the Rasmol color for the given atom's element, or
     * defaults to the given color if no color is defined.
     *
     * @param atom         IAtom to get a color for
     * @param defaultColor Color returned if this scheme does not define
     *                     a color for the passed IAtom
     * @return             the atom's color according to this coloring scheme.
     */
    @Override
    public Color getAtomColor(IAtom atom, Color defaultColor) {
        Color color = defaultColor;
        String symbol = atom.getSymbol();
        if (colorMap.containsKey(symbol)) {
            color = colorMap.get(symbol);
        }
        return color;
    }
}
