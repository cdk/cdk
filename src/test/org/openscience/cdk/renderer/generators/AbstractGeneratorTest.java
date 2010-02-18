package org.openscience.cdk.renderer.generators;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.List;

import javax.vecmath.Point2d;

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
 * Base class for test classes that test IGenerators.  
 * 
 * @author maclean
 *
 */
public abstract class AbstractGeneratorTest {
	
	protected IChemObjectBuilder builder = 
		NoNotificationChemObjectBuilder.getInstance();
	
	protected RendererModel model;
	
	protected ElementUtility elementUtil;
	
	/**
	 * Sets up the model and transform.
	 * Call from the 'Before' method in subclasses.
	 */
	public void setup() {
		model = new RendererModel();
		elementUtil = new ElementUtility();
		elementUtil.setTransform(this.getTransform());
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
	
	public static double distance(double x1, double y1, double x2, double y2) {
		return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
	}
	
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
	 * Makes a single atom, centered on the origin.
	 * 
	 * @return an atom container with a single atom
	 */
	public IAtomContainer makeSingleAtom() {
		IAtomContainer container = builder.newAtomContainer();
		container.addAtom(builder.newAtom("C", new Point2d(0,0)));
		return container;
	}
	
	/**
	 * Makes a single C-C bond aligned horizontally. The endpoints are (0, -1)
	 * and (0, 1) in model space.
	 * 
	 * @return an atom container with a single C-C bond
	 */
	public IAtomContainer makeSingleBond() {
		IAtomContainer container = builder.newAtomContainer();
		container.addAtom(builder.newAtom("C", new Point2d(0,-1)));
		container.addAtom(builder.newAtom("C", new Point2d(0, 1)));
		container.addBond(0, 1, IBond.Order.SINGLE);
		return container;
	}
	
	/**
	 * Make a square (sort-of cyclobutane, without any hydrogens) centered on 
	 * the origin, with a bond length of 2.
	 * 
	 * @return four carbon atoms connected by bonds into a square
	 */
	public IAtomContainer makeSquare() {
		IAtomContainer container = builder.newAtomContainer();
		container.addAtom(builder.newAtom("C", new Point2d(-1,-1)));
		container.addAtom(builder.newAtom("C", new Point2d( 1,-1)));
		container.addAtom(builder.newAtom("C", new Point2d( 1, 1)));
		container.addAtom(builder.newAtom("C", new Point2d(-1, 1)));
		container.addBond(0, 1, IBond.Order.SINGLE);
		container.addBond(0, 3, IBond.Order.SINGLE);
		container.addBond(1, 2, IBond.Order.SINGLE);
		container.addBond(2, 3, IBond.Order.SINGLE);
		return container;
		
	}
}