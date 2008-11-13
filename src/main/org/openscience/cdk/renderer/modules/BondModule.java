package org.openscience.cdk.renderer.modules;

import java.awt.Point;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.renderer.elements.DoubleLineElement;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.LineElement;
import org.openscience.cdk.renderer.elements.RenderingModel;
import org.openscience.cdk.renderer.elements.TripleLineElement;

public class BondModule extends AbstractModule implements IRenderingModule {

    public void process( IAtomContainer ac, RenderingModel renderingModel ) {
        assert( ac != null);
        for(IBond bond:ac.bonds()) {
                Point[] points = new Point[bond.getAtomCount()];
             for(int i=0 ;i<bond.getAtomCount();i++) {
                 IAtom atom = bond.getAtom( i );
                 points[i] = renderingModel.transform( atom.getPoint2d().x,
                                                       atom.getPoint2d().y);
                 
             }
             IRenderingElement element;
             Point p1 = points[0];
             Point p2 = points[1];
             if(bond.getOrder() == CDKConstants.BONDORDER_DOUBLE)
                 element = new DoubleLineElement(p1,p2);
             else if( bond.getOrder() == CDKConstants.BONDORDER_TRIPLE) 
                 element = new TripleLineElement(p1,p2);
             else element = new LineElement(p1,p2);
            renderingModel.add( element );
        }
    }
}
