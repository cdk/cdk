package org.openscience.cdk.renderer.modules;

import java.awt.Point;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.renderer.elements.OvalElement;
import org.openscience.cdk.renderer.elements.RenderingModel;

public class AtomModule extends AbstractModule {
    
    public void process( IAtomContainer ac, RenderingModel renderingModel) {
        assert( ac != null);
       for(IAtom atom:ac.atoms()) {
            Point p = renderingModel.transform( atom.getPoint2d().x,
                                                atom.getPoint2d().y);
            
           renderingModel.add( new OvalElement(p) );
       }

    }

}
