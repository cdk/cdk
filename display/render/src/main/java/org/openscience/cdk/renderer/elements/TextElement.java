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

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;

/**
 * Text element as used in the chemical drawing. This can be a element symbol.
 *
 * @cdk.module render
 * @cdk.githash
 */
@TestClass("org.openscience.cdk.renderer.elements.TextElementTest")
public class TextElement implements IRenderingElement {

    /** The x coordinate where the text should be displayed. */
    public final double xCoord;
    
    /** The y coordinate where the text should be displayed. */
    public final double yCoord;
    
    /** The text to be displayed. */
    public final String text;
    
    /** The color of the text. */
    public final Color color;

    /**
     * Constructs a new TextElement with the content <code>text</code> to be
     * drawn at position (x,y) in the color <code>color</code>.
     *
     * @param xCoord     x coordinate where the text should be displayed
     * @param yCoord     y coordinate where the text should be displayed
     * @param text  the text to be drawn
     * @param color the color of the text
     */
    @TestMethod("testConstructor")
    public TextElement(double xCoord, double yCoord, String text, Color color) {
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.text = text;
        this.color = color;
    }

    /** {@inheritDoc} */
    @TestMethod("testAccept")
    public void accept(IRenderingVisitor visotor) {
        visotor.visit(this);
    }

}
