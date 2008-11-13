package org.openscience.cdk.renderer;

import org.openscience.cdk.renderer.elements.DoubleLineElement;
import org.openscience.cdk.renderer.elements.LineElement;
import org.openscience.cdk.renderer.elements.OvalElement;
import org.openscience.cdk.renderer.elements.RenderingModel;
import org.openscience.cdk.renderer.elements.TripleLineElement;

public interface IRenderingVisitor {
    
    public void visitModel(RenderingModel element);
    
    public void visitOval(OvalElement element);
    public void visitLine(LineElement element);
    public void visitDoubleLine(DoubleLineElement element);

    public void visitTripleLine( TripleLineElement element );
    
}
