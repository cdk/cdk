package org.openscience.cdk.renderer.elements;

import org.openscience.cdk.renderer.IRenderingVisitor;

public interface IRenderingElement {

    public void accept(IRenderingVisitor v);
}
