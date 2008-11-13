package org.openscience.cdk.renderer.elements;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.renderer.IRenderingVisitor;

public class RenderingModel implements IRenderingElement,
                                       Iterable<IRenderingElement> {

    Collection<IRenderingElement> elements = new ArrayList<IRenderingElement>();
    double scalex = 10;
    double scaley = 10;
    int xt,yt;
    
    public Iterator<IRenderingElement> iterator() {
        return elements.iterator();
    }
    
    public void add(IRenderingElement element) {
        elements.add(element);
    }

    public void setScale( double[] scale ) {
        if( scale !=null && scale.length !=2) return;
        scalex = Math.min( scale[0],scale[1]);
        scaley = scalex;
    }
    
    public Point transform(double x,double y) {
        return new Point( (int)(x*scalex)-xt,
                          -(int)(y*scaley)-yt);
    }
    
    public double[] getDimensions(IAtomContainer ac, Dimension size) {
        double  xmin=Double.MAX_VALUE,
                xmax=Double.MIN_VALUE,
                ymin=Double.MAX_VALUE,
                ymax=Double.MIN_VALUE;
        
        for(IAtom atom:ac.atoms()) {
            double x = atom.getPoint2d().x;
            double y = atom.getPoint2d().y;
            xmin = Math.min( xmin, x);
            xmax = Math.max( xmax, x);
            ymin = Math.min( ymin, y);
            ymax = Math.max( ymax, y);
        }
        double[] scale = new double[]{ (size.getWidth()/(xmax-xmin)),
                             (size.getHeight()/(ymax-ymin))};
        setScale( scale );
        xt = (int)(xmin * scalex);
        yt = (int)(-size.getHeight()-(ymin*scaley));
        return scale;
    }

    public void accept( IRenderingVisitor v ) {
        
        v.visitModel( this );
    }
}
