package org.openscience.cdk.renderer.elements;

import java.awt.Point;

import org.openscience.cdk.renderer.IRenderingVisitor;

public class DoubleLineElement extends LineElement {

    public DoubleLineElement(Point p1, Point p2) {
        super( p1, p2 );
    }

    @Override
    public void accept( IRenderingVisitor v ) {
        v.visitDoubleLine( this );
    }
}
