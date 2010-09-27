/* Copyright (C) 2010  Gilleain Torrance <gilleain.torrance@gmail.com>
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. All we ask is that proper credit is given for our work,
 * which includes - but is not limited to - adding the above copyright notice to
 * the beginning of your source code files, and to any copyright notice that you
 * may distribute with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.renderer.generators;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.List;

import javax.vecmath.Point2d;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.renderer.ElementUtility;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.LineElement;
import org.openscience.cdk.renderer.elements.OvalElement;

/**
 * Base class for test classes that test {@link IAtomContainerGenerator}s.  
 * 
 * @author     maclean
 * @cdk.module test-renderbasic
 */
public abstract class AbstractGeneratorTest {
	
	protected IChemObjectBuilder builder = 
		NoNotificationChemObjectBuilder.getInstance();
	
	protected RendererModel model;
	
	protected ElementUtility elementUtil;
	
	protected BasicSceneGenerator sceneGenerator;
	
	/**
	 * Sets up the model and transform.
	 * Call from the 'Before' method in subclasses.
	 */
	public void setup() {
		model = new RendererModel();
		elementUtil = new ElementUtility();
		elementUtil.setTransform(this.getTransform());
		sceneGenerator = new BasicSceneGenerator();
		model.registerParameters(sceneGenerator);
	}
    
    public <T> boolean containsParameterType(
            List<IGeneratorParameter<?>> list, Class<T> type) {
        for (IGeneratorParameter<?> item : list) {
            if (item.getClass().getName().equals(type.getName())) return true; 
        }
        return false;
    }
	
	public List<IRenderingElement> getAllSimpleElements(
	        IGenerator generator, IAtomContainer container) {
	    IRenderingElement root = generator.generate(container, model);
	    return elementUtil.getAllSimpleElements(root);
	}
	
	/**
	 * Implement this in derived classes, either returning null if no custom 
	 * canvas is desired, or with a Rectangle with the appropriate size.
	 * 
	 * @return a Rectangle representing a custom drawing canvas
	 */
	public abstract Rectangle getCustomCanvas();
	
	/**
	 * Gets the default canvas for drawing on.
	 * 
	 * @return a Rectangle representing the default drawing canvas 
	 */
	public Rectangle getDefaultCanvas() {
		return new Rectangle(0, 0, 100, 100);
	}
	
	/**
	 * Gets the transform to be used when converting rendering elements into
	 * graphical objects. 
	 * 
	 * @return the affine transform based on the current canvas
	 */
	public AffineTransform getTransform() {
		Rectangle canvas = getCustomCanvas();
		if (canvas == null) {
			canvas = this.getDefaultCanvas();
		}
		return makeTransform(canvas);
	}

	/**
	 * Uses a rectangle canvas to work out how to translate from model space
	 * to screen space.
	 *  
	 * @param canvas the rectangular canvas to draw on
	 * @return the transform needed to translate to screen space
	 */
	public AffineTransform makeTransform(Rectangle canvas) {
		AffineTransform transform = new AffineTransform();
		int cX = canvas.x + canvas.width / 2;
		int cY = canvas.y + canvas.height / 2;
		transform.translate(cX, cY);
		// TODO : include scale and zoom!
		return transform;
	}
	
	/**
	 * A utility method to determine the length of a line element (in 
	 * model space)
	 * 
	 * @param line the line element to determine the length of
	 * @return a length
	 */
	public static double length(LineElement line) {
		return 
			AbstractGeneratorTest.distance(line.x1, line.y1, line.x2, line.y2);
	}
	
	/**
	 * The distance between two coordinates.
	 * 
	 * @param x1 the first x coordinate 
	 * @param y1 the first y coordinate
	 * @param x2 the second x coordinate
	 * @param y2 the second y coordinate
	 * @return the distance
	 */
	public static double distance(double x1, double y1, double x2, double y2) {
		return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
	}
	
	/**
	 * Find the center of a list of elements.
	 * 
	 * @param elements
	 * @return
	 */
	public static Point2d center(List<IRenderingElement> elements) {
		double centerX = 0.0;
		double centerY = 0.0;
		double counter = 0;
		for (IRenderingElement element : elements) {
			if (element instanceof OvalElement) {
				OvalElement o = (OvalElement) element;
				centerX += o.x;
				centerY += o.y;
				counter++;
			} else if (element instanceof LineElement) {
				LineElement l = (LineElement) element;
				centerX += l.x1;
				centerX += l.x2;
				centerY += l.y1;
				centerY += l.y2;
				counter += 2;
			}
		}
		if (counter > 0) {
			return new Point2d(centerX / counter, centerY / counter);
		} else {
			return new Point2d(0, 0);
		}
	}
	
	/**
	 * Makes a single carbon atom, centered on the origin.
	 * 
	 * @return an atom container with a single atom
	 */
	public IAtomContainer makeSingleAtom() {
		IAtomContainer container = builder.newInstance(IAtomContainer.class);
		container.addAtom(builder.newInstance(IAtom.class, "C", new Point2d(0,0)));
		return container;
	}
	
	/**
     * Makes a single atom of a particular element, centered on the origin.
     * 
     * @return an atom container with a single atom
     */
    public IAtomContainer makeSingleAtom(String elementSymbol) {
        IAtomContainer container = builder.newInstance(IAtomContainer.class);
        container.addAtom(
             builder.newInstance(IAtom.class, elementSymbol, new Point2d(0,0)));
        return container;
    }
    
    public IAtomContainer makeMethane() {
        IAtomContainer methane = builder.newInstance(IAtomContainer.class);
        methane.addAtom(builder.newInstance(IAtom.class, "C", new Point2d( 0, 0)));
        methane.addAtom(builder.newInstance(IAtom.class, "H", new Point2d( 1, 1)));
        methane.addAtom(builder.newInstance(IAtom.class, "H", new Point2d( 1,-1)));
        methane.addAtom(builder.newInstance(IAtom.class, "H", new Point2d(-1, 1)));
        methane.addAtom(builder.newInstance(IAtom.class, "H", new Point2d(-1,-1)));
        return methane;
    }
	
	/**
	 * Makes a single C-C bond aligned horizontally. The endpoints are (0, -1)
	 * and (0, 1) in model space.
	 * 
	 * @return an atom container with a single C-C bond
	 */
	public IAtomContainer makeSingleBond() {
		IAtomContainer container = builder.newInstance(IAtomContainer.class);
		container.addAtom(builder.newInstance(IAtom.class,"C", new Point2d(0,-1)));
		container.addAtom(builder.newInstance(IAtom.class,"C", new Point2d(0, 1)));
		container.addBond(0, 1, IBond.Order.SINGLE);
		return container;
	}
	
	public IAtomContainer makeCCC() {
	    IAtomContainer container = builder.newInstance(IAtomContainer.class);
        container.addAtom(builder.newInstance(IAtom.class,"C", new Point2d(-1,-1)));
        container.addAtom(builder.newInstance(IAtom.class,"C", new Point2d( 0, 0)));
        container.addAtom(builder.newInstance(IAtom.class,"C", new Point2d( 1,-1)));
        container.addBond(0, 1, IBond.Order.SINGLE);
        container.addBond(1, 2, IBond.Order.SINGLE);
        return container;
	}
	
	/**
	 * Make a square (sort-of cyclobutane, without any hydrogens) centered on 
	 * the origin, with a bond length of 2.
	 * 
	 * @return four carbon atoms connected by bonds into a square
	 */
	public IAtomContainer makeSquare() {
		IAtomContainer container = builder.newInstance(IAtomContainer.class);
		container.addAtom(builder.newInstance(IAtom.class, "C", new Point2d(-1,-1)));
		container.addAtom(builder.newInstance(IAtom.class, "C", new Point2d( 1,-1)));
		container.addAtom(builder.newInstance(IAtom.class, "C", new Point2d( 1, 1)));
		container.addAtom(builder.newInstance(IAtom.class, "C", new Point2d(-1, 1)));
		container.addBond(0, 1, IBond.Order.SINGLE);
		container.addBond(0, 3, IBond.Order.SINGLE);
		container.addBond(1, 2, IBond.Order.SINGLE);
		container.addBond(2, 3, IBond.Order.SINGLE);
		return container;
	}
	
	/**
	 * Make a square with four different elements, to test things like atom 
	 * color.
	 * 
	 * @return an unlikely S-N-O-P square
	 */
	public IAtomContainer makeSNOPSquare() {
        IAtomContainer container = builder.newInstance(IAtomContainer.class);
        container.addAtom(builder.newInstance(IAtom.class, "S", new Point2d(-1,-1)));
        container.addAtom(builder.newInstance(IAtom.class, "N", new Point2d( 1,-1)));
        container.addAtom(builder.newInstance(IAtom.class, "O", new Point2d( 1, 1)));
        container.addAtom(builder.newInstance(IAtom.class, "P", new Point2d(-1, 1)));
        container.addBond(0, 1, IBond.Order.SINGLE);
        container.addBond(0, 3, IBond.Order.SINGLE);
        container.addBond(1, 2, IBond.Order.SINGLE);
        container.addBond(2, 3, IBond.Order.SINGLE);
        return container;
	    
	}
}