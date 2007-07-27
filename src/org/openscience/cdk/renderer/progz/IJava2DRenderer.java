package org.openscience.cdk.renderer.progz;

import java.awt.geom.Point2D;

import org.openscience.cdk.renderer.ISimpleRenderer2D;

public interface IJava2DRenderer extends ISimpleRenderer2D {
	/**
	 *  Returns model coordinates from screencoordinates provided by the graphics translation
	 *   
	 * @param ptSrc the point to convert
	 * @return Point2D in real world coordinates
	 */
	public abstract Point2D getCoorFromScreen(Point2D ptSrc);
}
