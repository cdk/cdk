package org.openscience.cdk.renderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;

public class Java2DRenderer implements IRenderer2D {

	private Renderer2DModel rendererModel;

	public Java2DRenderer(Renderer2DModel model) {
		this.rendererModel = model;
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
		IBond[] bonds = atomCon.getBonds();
		for (int i=0; i<bonds.length; i++) {
			shapes.add(
				new Line2D.Double(
					bonds[i].getAtom(0).getPoint2d().x,
					bonds[i].getAtom(0).getPoint2d().y,
					bonds[i].getAtom(1).getPoint2d().x,
					bonds[i].getAtom(1).getPoint2d().y
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
	}

	private void scaleGraphics(Graphics2D graphics, Rectangle2D contextBounds, Rectangle2D rendererBounds) {
		double factor = rendererModel.getZoomFactor() * (1.0 - rendererModel.getMargin() * 2.0);
	    double scaleX = factor * rendererBounds.getWidth() / contextBounds.getWidth();
	    double scaleY = factor * rendererBounds.getHeight() / contextBounds.getHeight();

	    if (scaleX > scaleY) {
	    	System.out.println("Scaled by Y: " + scaleY);
	    	// FIXME: should be -X: to put the origin in the lower left corner 
	    	graphics.scale(scaleY, -scaleY);
	    } else {
	    	System.out.println("Scaled by X: " + scaleX);
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

	public Renderer2DModel getRenderer2DModel() {
		return this.rendererModel;
	}

	public void setRenderer2DModel(Renderer2DModel model) {
		this.rendererModel = model;
	}
}
