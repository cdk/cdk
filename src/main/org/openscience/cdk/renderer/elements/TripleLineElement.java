package org.openscience.cdk.renderer.elements;

import java.awt.Point;

import org.openscience.cdk.renderer.IRenderingVisitor;


public class TripleLineElement extends LineElement {

    public TripleLineElement(int x, int y, int x1, int y1) {

        super( x, y, x1, y1 );
    }
    
    
    public TripleLineElement(Point p1, Point p2) {
        super( p1, p2);
    }
    
    @Override
    public void accept( IRenderingVisitor v ) {
    
        v.visitTripleLine(this);
    }

}
