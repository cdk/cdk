package org.openscience.cdk.renderer.modules;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.renderer.elements.RenderingModel;

public interface IRenderingModule {

    public void process( IAtomContainer ac, RenderingModel renderingModel );

}