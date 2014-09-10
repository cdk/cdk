/*  Copyright (C) 2008  Arvid Berg <goglepox@users.sf.net>
 *
 *  Contact: cdk-devel@list.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.renderer.elements;

import java.awt.Color;

import org.openscience.cdk.interfaces.IAtom;

/**
 * Rendering element that shows the {@link IAtom} mass number information.
 *
 * @cdk.module renderextra
 * @cdk.githash
 */
public class AtomMassSymbolElement extends AtomSymbolElement {

    /** The {@link IAtom}s mass number. */
    public final int atomMassNumber;

    /**
     * Constructs a new {@link TextElement} displaying the atom's mass number information.
     *
     * @param xCoord        screen x-coordinate of where the text is displayed
     * @param yCoord        screen y-coordinate of where the text is displayed
     * @param symbol        the element symbol of the atom
     * @param formalCharge  the formal charge of the atom
     * @param hydrogenCount the number of implicit hydrogens of the atom
     * @param alignment     indicator of how the text should be aligned
     * @param atomMass      the mass number of the atom
     * @param color         the color
     */
    public AtomMassSymbolElement(double xCoord, double yCoord, String symbol, Integer formalCharge,
            Integer hydrogenCount, int alignment, Integer atomMass, Color color) {
        super(xCoord, yCoord, symbol, formalCharge, hydrogenCount, alignment, color);
        this.atomMassNumber = atomMass != null ? atomMass : -1;
    }
}
