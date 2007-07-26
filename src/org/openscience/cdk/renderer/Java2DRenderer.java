package org.openscience.cdk.renderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import javax.vecmath.Point2d;
import java.awt.font.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.validate.ProblemMarker;


public class Java2DRenderer implements IRenderer2D {

	private Renderer2DModel rendererModel;

	protected LoggingTool logger;

	public Java2DRenderer(Renderer2DModel model) {
		this.rendererModel = model;
		logger = new LoggingTool(this);
	}
	
	public void paintChemModel(IChemModel model, Graphics2D graphics) {
		// TODO Auto-generated method stub

	}

	public void paintMoleculeSet(IMoleculeSet moleculeSet, Graphics2D graphics) {
		// TODO Auto-generated method stub

	}

	public void paintReaction(IReaction reaction, Graphics2D graphics) {
		// TODO Auto-generated method stub

	}

	public void paintReactionSet(IReactionSet reactionSet, Graphics2D graphics) {
		// TODO Auto-generated method stub

	}

	public void paintMolecule(IAtomContainer atomCon, Graphics2D graphics) {
		// TODO Auto-generated method stub
		
	}

	public void paintMolecule(IAtomContainer atomCon, Graphics2D graphics,
			Rectangle2D bounds) {
		List shapes = new ArrayList();
		// create the bond shapes
		Iterator bonds = atomCon.bonds();
		while (bonds.hasNext()) {
			IBond bond = (IBond)bonds.next();
			shapes.add(
				new Line2D.Double(
					bond.getAtom(0).getPoint2d().x,
					bond.getAtom(0).getPoint2d().y,
					bond.getAtom(1).getPoint2d().x,
					bond.getAtom(1).getPoint2d().y
				)
			);
		}
		// calculate the molecule boundaries via the shapes
		Rectangle2D molBounds = createRectangle2D(shapes);
		
		scaleGraphics(graphics, molBounds, bounds);
		translateGraphics(graphics, molBounds, bounds);
		
		// draw the shapes
		graphics.setColor(Color.BLACK);
		graphics.setStroke(new BasicStroke(
			(float) (rendererModel.getBondWidth()/rendererModel.getBondLength()),
			BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
		);
		for (Iterator iter = shapes.iterator(); iter.hasNext();) {
			graphics.draw((Shape)iter.next());
		}
		
		// add rendering of atom symbols?
		paintAtoms(atomCon, graphics);
		
		System.out.println("transform matrix:" + graphics.getTransform());
	}
	/**
	 *  Searches through all the atoms in the given array of atoms, triggers the
	 *  paintColouredAtoms method if the atom has got a certain color and triggers
	 *  the paintAtomSymbol method if the symbol of the atom is not C.
	 */
	public void paintAtoms(IAtomContainer atomCon, Graphics2D graphics)
	{
		for (int i = 0; i < atomCon.getAtomCount(); i++)
		{
			paintAtom(atomCon, atomCon.getAtom(i), graphics);
		}
	}
	public void paintAtom(IAtomContainer container, IAtom atom, Graphics2D graphics)
	{
		//System.out.println("IAtom Symbol:" + atom.getSymbol() + " atom:" + atom);
		Font font;
		font = new Font("Serif", Font.PLAIN, 20);
		
		float fscale = 25;
		float[] transmatrix = { 1f / fscale, 0f, 0f, -1f / fscale};
		AffineTransform trans = new AffineTransform(transmatrix);
		font = font.deriveFont(trans);
		
		graphics.setFont(font);
		String symbol = "";
		if (atom.getSymbol() != null) {
			symbol = atom.getSymbol();
		}
		boolean drawSymbol = true; //paint all Atoms for the time being
		boolean isRadical = (container.getConnectedSingleElectronsCount(atom) > 0);
		if (atom instanceof IPseudoAtom)
		{
			drawSymbol = false;
//			if (atom instanceof FragmentAtom) {
//				paintFragmentAtom((FragmentAtom)atom, atomBackColor, graphics,
//						alignment, isRadical);
//			} else {
			//	paintPseudoAtomLabel((IPseudoAtom) atom, atomBackColor, graphics, alignment, isRadical);
//			}
			return;
		} else if (!atom.getSymbol().equals("C"))
		{
			/*
			 *  only show element for non-carbon atoms,
			 *  unless (see below)...
			 */
			drawSymbol = true;
		} else if (getRenderer2DModel().getKekuleStructure())
		{
			// ... unless carbon must be drawn because in Kekule mode
			drawSymbol = true;
		} else if (atom.getFormalCharge() != 0)
		{
			// ... unless carbon is charged
			drawSymbol = true;
		} else if (container.getConnectedBondsList(atom).size() < 1)
		{
			// ... unless carbon is unbonded
			drawSymbol = true;
		} else if (getRenderer2DModel().getShowEndCarbons() && (container.getConnectedBondsList(atom).size() == 1))
		{
			drawSymbol = true;
		} else if (atom.getProperty(ProblemMarker.ERROR_MARKER) != null)
		{
			// ... unless carbon is unbonded
			drawSymbol = true;
		} else if (atom.getMassNumber() != 0)
		{
			try
			{
				if (atom.getMassNumber() != IsotopeFactory.getInstance(container.getBuilder()).
						getMajorIsotope(atom.getSymbol()).getMassNumber())
				{
					drawSymbol = true;
				}
			} catch (Exception exception) {
                logger.debug("Could not get an instance of IsotopeFactory");
            }

		}

		if (drawSymbol != true)
			return;
		
	//	AffineTransform affine = graphics.getTransform();

	//	Point2D srcPts = new Point2D.Double( ,  );
				
	//	graphics.setTransform(new AffineTransform()); //set the graphics transform to the identity
		
	//	Point2D desPts = srcPts; //new Point2D.Double();
	//	desPts = affine.transform(srcPts, desPts); //get screencoordinates for atom
		

		FontRenderContext frc = graphics.getFontRenderContext();
		TextLayout layout = new TextLayout(symbol, font, frc);
		Rectangle2D bounds = layout.getBounds();
		
		float margin = 0.02f; 
		float screenX = (float)(atom.getPoint2d().x - bounds.getWidth()/2);
		float screenY = (float)(atom.getPoint2d().y - bounds.getHeight()/2);

		bounds.setRect(bounds.getX() + screenX - margin,
		                  bounds.getY() + screenY - margin,
		                  bounds.getWidth() + 2 * margin,
		                  bounds.getHeight() + 2 * margin);
		
		Color atomColor = getRenderer2DModel().getAtomColor(atom, Color.BLACK);
		Color saveColor = graphics.getColor();
		Color bgColor = graphics.getBackground();
		graphics.setColor(bgColor);
		graphics.fill(bounds);
		
		graphics.setColor(atomColor);
		layout.draw(graphics, screenX, screenY);

	//	graphics.setTransform(affine);

		graphics.setColor(saveColor);
//		graphics.drawString(symbol, screenX, screenY );
		
	}

	private void scaleGraphics(Graphics2D graphics, Rectangle2D contextBounds, Rectangle2D rendererBounds) {
		double factor = rendererModel.getZoomFactor() * (1.0 - rendererModel.getMargin() * 2.0);
	    double scaleX = factor * rendererBounds.getWidth() / contextBounds.getWidth();
	    double scaleY = factor * rendererBounds.getHeight() / contextBounds.getHeight();

	    if (scaleX > scaleY) {
//	    	logger.debug("Scaled by Y: " + scaleY);
	    	// FIXME: should be -X: to put the origin in the lower left corner 
	    	graphics.scale(scaleY, -scaleY);
	    } else {
//	    	logger.debug("Scaled by X: " + scaleX);
	    	// FIXME: should be -X: to put the origin in the lower left corner 
	    	graphics.scale(scaleX, -scaleX);
	    }
	  }

	private void translateGraphics(Graphics2D graphics, Rectangle2D contextBounds, Rectangle2D rendererBounds) {
		double scale = graphics.getTransform().getScaleX();
	    double dx = -contextBounds.getX() * scale + 0.5 * (rendererBounds.getWidth() - contextBounds.getWidth() * scale);
	    double dy = -contextBounds.getY() * scale - 0.5 * (rendererBounds.getHeight() + contextBounds.getHeight() * scale);
	    						
	    graphics.translate(dx / scale, dy / scale);
	}

	private Rectangle2D createRectangle2D(List shapes) {
	    Iterator it = shapes.iterator();
	    Rectangle2D result = ((Shape) it.next()).getBounds2D();
	    
	    while (it.hasNext()) { 
	      Rectangle2D.union(result, ((Shape) it.next()).getBounds2D(), result);
	    }
	        
	    // FIXME: should add a small white margin around this
	    return result;      
	}
	/**
	 *  Returns model coordinates from screencoordinates provided by the graphics translation
	 *   
	 * @param graphics
	 * @param ptSrc the point to convert
	 * @return
	 */
	public Point2D GetCoorFromScreen(Graphics2D graphics, Point2D ptSrc) {
		Point2D ptDst = new Point2D.Double();
		AffineTransform affine = graphics.getTransform();
		try {
			affine.inverseTransform(ptSrc, ptDst);
		}
		catch (Exception exception) {
			logger.warn("Unable to reverse affine transformation");
			logger.debug(exception);
		}
		return ptDst;
	}
	public Renderer2DModel getRenderer2DModel() {
		return this.rendererModel;
	}

	public void setRenderer2DModel(Renderer2DModel model) {
		this.rendererModel = model;
	}
}
