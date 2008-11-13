package org.openscience.cdk.renderer.elements;

import java.awt.Point;

import org.openscience.cdk.renderer.IRenderingVisitor;

public class LineElement implements IRenderingElement {
    int x,y,x1,y1;
    
    private LineElement() {
    }
    
    public LineElement(Point p1, Point p2) {
        this(p1.x,p1.y,p2.x,p2.y);
    }
    
    public LineElement(int x, int y, int x1, int y1){
        this();
        this.x = x;
        this.y = y;
        this.x1 = x1;
        this.y1 = y1;
    }
    public int getX(){return x;}
    public int getX1(){return x1;}
    public int getY(){return y;}
    public int getY1(){return y1;}
    
    public void accept( IRenderingVisitor v ) {
        v.visitLine( this );
    }
}
