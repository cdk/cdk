/*  Copyright (C) 2026 John Mayfield
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
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
package org.openscience.cdk.renderer.generators.standard;

/**
 * Controls the alignment of a double bond in a depiction. A double bond
 * can be rendered either in the center, the left or the right.
 * <p/>
 * Sketch formats (e.g. ChemDraw) store this is an intrinsic property of the
 * bond. An internal algorithm is used to determine which side to place them.
 *
 * @author John Mayfield
 */
public enum DoubleBondAlign {
    /**
     * Double bond is centered.
     */
    Center,
    /**
     * Double bond is on the left (viewing from the "begin" to the "end" atom)
     */
    Left,
    /**
     * Double bond is on the right (viewing from the "begin" to the "end" atom)
     */
    Right
}
