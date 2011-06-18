/* Copyright (C) 2009  Egon Willighagen <egonw@users.lists.sf>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All I ask is that proper credit is given for my work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.renderer;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.List;

import javax.vecmath.Point2d;

import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.generators.IGenerator;
import org.openscience.cdk.renderer.generators.IGeneratorParameter;
import org.openscience.cdk.renderer.visitor.IDrawVisitor;

/**
 * Interface that all 2D renderers implement. The constructor is responsible
 * for registering the {@link IAtomContainerGenerator}s' {@link IGeneratorParameter}s with
 * with the associated {@link RendererModel}.
 *
 * @author egonw
 * @cdk.module render
 * @cdk.githash
 */
public interface IRenderer<T extends IChemObject> {
    
    /**
     * Internal method to generate the intermediate format. 
     * 
     * @param obj the IChemObject to generate a diagram for
     * @return a tree of rendering elements
     */
    public IRenderingElement generateDiagram(T obj);
    
    /**
     * Returns the drawing model, giving access to drawing parameters.
     * 
     * @return the rendering model
     */
	public RendererModel getRenderer2DModel();

	/**
	 * Converts screen coordinates into model (or world) coordinates.
	 *
	 * @param screenXTo the screen's x coordinate
	 * @param screenYTo the screen's y coordinate
	 * @return          the matching model coordinates
	 *
	 * @see #toScreenCoordinates(dhttp://download.eclipse.org/egit/updates/ouble, double)
	 */
	public Point2d toModelCoordinates(double screenXTo, double screenYTo);

    /**
     * Converts model (or world) coordinates into screen coordinates.
     *
     * @param screenXTo the model's x coordinate
     * @param screenYTo the model's y coordinate
     * @return          the matching screen coordinates
     *
     * @see #toModelCoordinates(double, double)
     */
	public Point2d toScreenCoordinates(double screenXTo, double screenYTo);
	
	/**
	 * Set a new zoom factor.
	 *
	 * @param zoomFactor the new zoom factor
	 */
	public void setZoom(double zoomFactor);

    /**
     * Set a new drawing center in screen coordinates.
     *
     * @param screenX the x screen coordinate of the drawing center 
     * @param screenY the y screen coordinate of the drawing center 
     */
	public void shiftDrawCenter(double screenX, double screenY);

	/**
	 * Paint an IChemObject.
	 * 
	 * @param object the chem object to paint
	 * @param drawVisitor the class that visits the generated elements  
	 * @return
	 */
	public Rectangle paint(T object, IDrawVisitor drawVisitor);
	
	/**
	 * Paint the chem object within the specified bounds.
	 * 
	 * @param reactionSet
	 * @param drawVisitor
	 * @param bounds
	 * @param resetCenter
	 */
	public void paint(T object, IDrawVisitor drawVisitor, Rectangle2D bounds, 
	        boolean resetCenter);

	/**
	 * Setup the transformations necessary to draw the {@link IChemObject}
	 * matching this {@link IRenderer} implementation.
	 */
	public void setup(T object, Rectangle screen);

	/**
	 * Set the scale for an {@link IChemObject}. It calculates the average bond
	 * length of the model and calculates the multiplication factor to transform
	 * this to the bond length that is set in the {@link RendererModel}.
	 * 
	 * @param  object the {@link IChemObject} to draw.
	 */
	public void setScale(T object);

	/**
	 * Given a {@link IChemObject}, calculates the bounding rectangle in screen
	 * space.
	 *
	 * @param  object the {@link IChemObject} to draw.
	 * @return        a rectangle in screen space.
	 */
	public Rectangle calculateDiagramBounds(T object);
	
	public List<IGenerator<T>> getGenerators();
}
