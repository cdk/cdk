package org.openscience.cdk.renderer.elements;

import java.awt.Color;

/**
 * A ring is just a circle - in other words, an oval whose width and height are
 * the same.
 * 
 * @cdk.module renderbasic
 * @cdk.githash
 */
public class RingElement extends OvalElement implements IRenderingElement {

    /**
     * Make a ring element centered on (x, y) with radius and color given.
     * 
     * @param x the x-coordinate of the ring center
     * @param y the y-coordinate of the ring center
     * @param radius the radius of the circle
     * @param color the color of the circle
     */
    public RingElement(double x, double y, double radius, Color color) {
        super(x, y, radius, false, color);
    }

    /** {@inheritDoc} */
    public void accept(IRenderingVisitor v) {
        v.visit(this);
    }

}
