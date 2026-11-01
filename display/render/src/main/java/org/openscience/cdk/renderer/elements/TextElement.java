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


/**
 * Text element as used in the chemical drawing. This can be a element symbol.
 *
 */
public class TextElement implements IRenderingElement {

    /**
     * Horizontal alignment of text.
     */
    public enum Align {
        Left,
        Center,
        Right
    }

    /** The x coordinate where the text should be displayed. */
    public final double xCoord;

    /** The y coordinate where the text should be displayed. */
    public final double yCoord;

    /** The text to be displayed. */
    public final String text;

    /** The color of the text. */
    public final Color  color;

    /** Background color */
    public final Color  backColor;

    public final Align align;

    public TextElement(double xCoord, double yCoord, String text,
                       Color color, Color backColor, Align align) {
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.text = text;
        this.color = color;
        this.backColor = backColor;
        this.align = align;
    }

    public TextElement(double xCoord, double yCoord, String text,
                       Color color, Color backColor) {
        this(xCoord, yCoord, text, color, backColor, Align.Center);
    }

    public TextElement(double xCoord, double yCoord, String text, Color color) {
        this(xCoord, yCoord, text, color, null, Align.Center);
    }

    public TextElement(double xCoord, double yCoord, String text) {
        this(xCoord, yCoord, text, Color.BLACK, null, Align.Center);
    }

    public TextElement(double xCoord, double yCoord, String text, Align align) {
        this(xCoord, yCoord, text, Color.BLACK, null, align);
    }

    /** {@inheritDoc} */
    @Override
    public void accept(IRenderingVisitor visotor) {
        visotor.visit(this);
    }
}
