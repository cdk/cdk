/* Copyright (C) 2008 Gilleain Torrance <gilleain.torrance@gmail.com>
 *               2011 Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.renderer.visitor;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;

/**
 * Partial implementation of the {@link IDrawVisitor} interface for the AWT
 * widget toolkit, allowing molecules to be rendered with toolkits based on
 * AWT, like the Java reference graphics platform Swing.
 *
 * @cdk.module renderawt
 */
@TestClass("org.openscience.cdk.renderer.visitor.AbstractAWTDrawVisitorTest")
public abstract class AbstractAWTDrawVisitor implements IDrawVisitor {
	
	/**
	 * This is initially null, and must be set in the setTransform method!
	 */
	protected AffineTransform transform = null;

	/**
	 * Transforms a point according to the current affine transformation,
	 * converting a world coordinate into a screen coordinate.
	 *
	 * @param xCoord x-coordinate of the world point to transform
	 * @param yCoord y-coordinate of the world point to transform
	 * @return       the transformed screen coordinate
	 */
	@TestMethod("testTransformPoint")
	public int[] transformPoint(double xCoord, double yCoord) {
        double[] src = new double[] {xCoord, yCoord};
        double[] dest = new double[2];
        this.transform.transform(src, 0, dest, 0, 1);
        return new int[] { (int) dest[0], (int) dest[1] };
    }

	/**
	 * Calculates the boundaries of a text string in screen coordinates.
	 *
	 * @param text     the text string
	 * @param xCoord   the world x-coordinate of where the text should be placed
	 * @param yCoord   the world y-coordinate of where the text should be placed
	 * @param graphics the graphics to which the text is outputted
	 * @return         the screen coordinates
	 */
	@TestMethod("testGetTextBounds")
    protected Rectangle2D getTextBounds(String text, double xCoord, double yCoord,
            Graphics2D graphics) {
        FontMetrics fontMetrics = graphics.getFontMetrics();
        Rectangle2D bounds = fontMetrics.getStringBounds(text, graphics);
        
        double widthPad = 3;
        double heightPad = 1;
        
        double width = bounds.getWidth() + widthPad;
        double height = bounds.getHeight() + heightPad;
        int[] point = this.transformPoint(xCoord, yCoord);
        return new Rectangle2D.Double(point[0] - width / 2, point[1] - height / 2, width, height);
    }

    /**
     * Calculates the base point where text should be rendered, as text in Java
     * is typically placed using the left-lower corner point in screen coordinates.
     * However, because the Java coordinate system is inverted in the y-axis with
     * respect to scientific coordinate systems (Java has 0,0 in the top left
     * corner, while in science we have 0,0 in the lower left corner), some
     * special action is needed, involving the size of the text. 
     *
     * @param text     the text string
     * @param xCoord   the world x-coordinate of where the text should be placed
     * @param yCoord   the world y-coordinate of where the text should be placed
     * @param graphics the graphics to which the text is outputted
     * @return         the screen coordinates
     */
	@TestMethod("testGetTextBasePoint")
    protected Point getTextBasePoint(String text, double xCoord, double yCoord, 
            Graphics2D graphics) {
        FontMetrics fontMetrics = graphics.getFontMetrics();
        Rectangle2D stringBounds = fontMetrics.getStringBounds(text, graphics);
        int[] point = this.transformPoint(xCoord, yCoord);
        int baseX = (int) (point[0] - (stringBounds.getWidth() / 2));
        
        // correct the baseline by the ascent
        int baseY = (int) (point[1] + 
                (fontMetrics.getAscent() - stringBounds.getHeight() / 2));
        return new Point(baseX, baseY);
    }

	/**
	 * Sets a new affine transformation to convert world coordinates into
	 * screen coordinates.
	 *
	 * @param transform the new {@link AffineTransform}.
	 */
    @TestMethod("testSetAffineTransformation")
    public void setTransform(AffineTransform transform) {
        this.transform = transform;
    }
}
