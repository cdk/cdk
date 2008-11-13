package org.openscience.cdk.renderer.elements;

import java.awt.Point;

import org.openscience.cdk.renderer.IRenderingVisitor;


public class OvalElement implements IRenderingElement {

    private final Point position;
    
    public OvalElement(Point pos) {        
        position = new Point(pos);      
    }
    
    public OvalElement(int x, int y) {
        position = new Point(x,y);
    }

    public int getX() {
        return position.x;
    }
    
    public int getY() {
        return position.y;
    }
    
    public void accept( IRenderingVisitor v ) {
        v.visitOval( this );        
    }
}
