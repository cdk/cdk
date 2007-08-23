package org.openscience.cdk.renderer.progz;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.renderer.Renderer2DModel;
import org.openscience.cdk.renderer.IRenderer2D;

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
import java.awt.geom.Ellipse2D.Double;
import java.awt.geom.Ellipse2D;
import java.awt.Polygon;
import java.awt.geom.GeneralPath;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.renderer.progz.GeometryToolsInternalCoordinates;
import org.openscience.cdk.ringsearch.SSSRFinder;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.RingSetManipulator;
import org.openscience.cdk.validate.ProblemMarker;


public class Java2DRenderer implements IJava2DRenderer {

	private Renderer2DModel rendererModel;
	private AffineTransform affine;

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
		//rendererModel.setShowAromaticity(true);
		
		
		
		// calculate the molecule boundaries via the shapes
		Rectangle2D molBounds = createRectangle2D(shapes);
		if (molBounds == null) {
			molBounds = new Rectangle2D.Double();
				
			IAtom atom = atomCon.getAtom(0);
			double x = atom.getPoint2d().x;
			double y = atom.getPoint2d().y;
			
			molBounds.setRect(x - 1, y - 1, 2, 2);
		}
		AffineTransform transformMatrix = createScaleTransform(molBounds,bounds);
		affine = transformMatrix;
		graphics.transform(transformMatrix);
		System.out.println("transform matrix:" + graphics.getTransform());

		// set basic shape form
		graphics.setColor(Color.BLACK);
		graphics.setStroke(new BasicStroke(
			(float) (rendererModel.getBondWidth()/rendererModel.getBondLength()),
			BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
		);

		IRingSet ringSet = getRingSet(atomCon);
		
		// draw bonds
		paintBonds(atomCon, ringSet, graphics);

		// add rendering of atom symbols?
		paintAtoms(atomCon, graphics);
		
	}
	protected IRingSet getRingSet(IAtomContainer atomContainer)
	{
	  IRingSet ringSet = atomContainer.getBuilder().newRingSet();
	  java.util.Iterator molecules = null;
  try
	  {
	    molecules = ConnectivityChecker.partitionIntoMolecules(atomContainer).molecules();
	  }

	  catch (Exception exception)
	  {
	    logger.warn("Could not partition molecule: ", exception.getMessage());
	    logger.debug(exception);
	    return ringSet;
	  }

	  while (molecules.hasNext())
	  {
	    SSSRFinder sssrf = new SSSRFinder((IMolecule)molecules.next());

	    ringSet.add(sssrf.findSSSR());
	  }

	  return ringSet;
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
		System.out.println("IAtom Symbol:" + atom.getSymbol() + " atom:" + atom);
				
		String symbol = "";
		if (atom.getSymbol() != null) {
			symbol = atom.getSymbol();
		}
		//symbol = "L"; //to test if a certain symbol is spaced out right 

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
		
		Color saveColor = graphics.getColor();

		Font font;
		//font = new Font("Serif", Font.PLAIN, 20);
		if (rendererModel.getFont() != null) {
			font = rendererModel.getFont();
			System.out.println("the font is now: " + font);
		}
		else 
			font = new Font("Arial", Font.PLAIN, 20);

		//the graphics objects has a transform which is 'reversed' to go from world coordinates
		//to screencoordinates, so transform the charaters to show them right.
		float fscale = 25; 
		float[] transmatrix = { 1f / fscale, 0f, 0f, -1f / fscale};
		AffineTransform trans = new AffineTransform(transmatrix);
		font = font.deriveFont(trans);
		
		float sizeSmall = (float)(font.getSize2D() * 0.4);
		Font fontSmall = font.deriveFont(sizeSmall); //font for upper/lower text such as Massnumber, Charges, HydrogenCount etc..

		graphics.setFont(font);

		FontRenderContext frc = graphics.getFontRenderContext();
		TextLayout layoutAtom = new TextLayout(symbol, font, frc);
		Rectangle2D bounds = layoutAtom.getBounds();
		
		float margin = 0.03f; //size of clean area next to text
		//btest has to be substracted to get the text on the exact right position
		//FIXME: get this value *somehow* from the graphics object?.
		float marginSmall = (float)(margin * 0.6);
		
		float btest = (float) (rendererModel.getBondWidth()/rendererModel.getBondLength());
		float screenAtomX = (float)(atom.getPoint2d().x - bounds.getWidth()/2 - btest); 
		float screenAtomY = (float)(atom.getPoint2d().y - bounds.getHeight()/2);

		bounds.setRect((float)(bounds.getX() + screenAtomX - margin),
				(float)(bounds.getY() + screenAtomY - margin),
				(float)(bounds.getWidth() + 2 * margin),
				(float)(bounds.getHeight() + 2 * margin));
		
		Color atomColor = getRenderer2DModel().getAtomColor(atom, Color.BLACK);
		
		if (atom.getMassNumber() != 0) {
			graphics.setFont(fontSmall);
			String textMass = Integer.toString(atom.getMassNumber());
			FontRenderContext frcMass = graphics.getFontRenderContext();
			TextLayout layoutMass = new TextLayout(textMass, fontSmall, frcMass);
			Rectangle2D boundsMass = layoutMass.getBounds();

			float screenMassX = (float)(screenAtomX - boundsMass.getWidth() - margin - marginSmall); 
			float screenMassY = (float)(screenAtomY + bounds.getHeight() - margin - marginSmall - boundsMass.getHeight() / 2);

			boundsMass.setRect(boundsMass.getX() + screenMassX - marginSmall,
						boundsMass.getY() + screenMassY - marginSmall,
						boundsMass.getWidth() + 2 * marginSmall,
						boundsMass.getHeight() + 2 * marginSmall);
		
			Color colorMass = Color.BLACK;
			Color bgColor = Color.green;
			graphics.setColor(bgColor);
			graphics.fill(boundsMass);// draw atom background
			graphics.setFont(fontSmall);
			graphics.setColor(colorMass);
			layoutMass.draw(graphics, screenMassX, screenMassY);// draw atom symbol
		}
		if (atom.getFormalCharge() != null && atom.getFormalCharge() != 0) {
			graphics.setFont(fontSmall);
			String baseString = "+";
			String textFormal = "";
			if (atom.getFormalCharge() != 1 && atom.getFormalCharge() != -1)
				textFormal += Integer.toString(atom.getFormalCharge());
			
			if (atom.getFormalCharge() > 0)
				textFormal += "+";
			else 
				textFormal += "-";
			
			FontRenderContext frcFormal = graphics.getFontRenderContext();
			TextLayout layoutFormal = new TextLayout(textFormal, fontSmall, frcFormal);
			TextLayout layoutBase = new TextLayout(baseString, fontSmall, frcFormal);
			Rectangle2D boundsFormalC = layoutFormal.getBounds();
		
			float screenFormalX = (float)(screenAtomX + bounds.getWidth() + marginSmall); 
			float screenFormalY = (float)(screenAtomY + bounds.getHeight() - layoutBase.getAdvance() );//- boundsFormalC.getHeight() / 2
			System.out.println(
					//" TextLayout Descent: " + layoutFormal.getDescent() + 
					//" Ascent: " + layoutFormal.getAscent() +
					" getAdvance: " + layoutFormal.getAdvance() +
					" getBaseline: " + layoutFormal.getBaseline() +
					" boundsFormalC.getHeight()" + boundsFormalC.getHeight() +
					" BasegetAdvance: " + layoutBase.getAdvance() +
					" BasegetBaseline: " + layoutBase.getBaseline() );
			
			boundsFormalC.setRect((float)(boundsFormalC.getX() + screenFormalX - marginSmall),
					(float)(boundsFormalC.getY() + screenFormalY - marginSmall),
					(float)(boundsFormalC.getWidth() + 2 * marginSmall),
					(float)(boundsFormalC.getHeight() + 2 * marginSmall));
		
			Color colorFormalC = Color.BLACK;
			Color bgColor = Color.green;
			graphics.setColor(bgColor);
			graphics.fill(boundsFormalC);// draw Formal Charge background
			graphics.setFont(fontSmall);
			graphics.setColor(colorFormalC);
			layoutFormal.draw(graphics, screenFormalX, screenFormalY);// draw Formal Charge
			
		}
		Color bgColor = graphics.getBackground();
		bgColor = Color.BLUE;
		graphics.setColor(bgColor);
		graphics.fill(bounds);// draw atom background
		graphics.setFont(font);
		graphics.setColor(atomColor);
		layoutAtom.draw(graphics, screenAtomX, screenAtomY);// draw atom symbol
	
		
		
		graphics.setColor(saveColor);
	}
	/**
	 *  Paints a rectangle of the given color at the position of the given atom.
	 *  For example when the atom is highlighted.
	 *
	 *@param  atom      The atom to be drawn
	 *@param  color     The color of the atom to be drawn
	 */
	public void paintColouredAtomBackground(org.openscience.cdk.interfaces.IAtom atom, Color color, Graphics2D graphics)
	{
		double x = atom.getPoint2d().x;
		double y = atom.getPoint2d().y;
		System.out.println("painting paintColouredAtomBackground now at " + x + " / " + y);
		//FIXME: right size for this AtomRadius (currently estimate)
		double atomRadius = 0.8;
		
		graphics.setColor(color);
	
		Rectangle2D shape = new Rectangle2D.Double();
		shape.setFrame(x - (atomRadius / 2), y - (atomRadius / 2), atomRadius, atomRadius);
		if(rendererModel.getIsCompact())
			graphics.draw(shape);
		else
			graphics.fill(shape);
	}
	/**
	 *  A ring is defined aromatic if all atoms are aromatic, -or- all bonds are
	 *  aromatic.
	 *  copied from AbstractRenderer2D
	 */
	public boolean ringIsAromatic(IRing ring)
	{
		boolean isAromatic = true;
		for (int i = 0; i < ring.getAtomCount(); i++)
		{
			if (!ring.getAtom(i).getFlag(CDKConstants.ISAROMATIC))
			{
				isAromatic = false;
			}
		}
		if (!isAromatic)
		{
			isAromatic = true;
			Iterator bonds = ring.bonds();
			while (bonds.hasNext())
				if (!((IBond)bonds.next()).getFlag(CDKConstants.ISAROMATIC))
					return false;
		}
		return isAromatic;
	}
	
	public static double distance2points(Point2d a, Point2d b) {
		return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
	}
	public static double distance2points(double x0, double y0, double x1, double y1) {
		return Math.sqrt(Math.pow(x0 - x1, 2) + Math.pow(y0 - y1, 2));
	}
	/**
	 *  Paints the inner bond of a double bond that is part of a ring.
	 *
	 *@param  bond       The bond to be drawn
	 *@param  ring       The ring the bond is part of
	 *@param  bondColor  Color of the bond
	 */
	public void paintInnerBond(org.openscience.cdk.interfaces.IBond bond, IRing ring, Color bondColor, Graphics2D graphics)
	{
		Point2d center = GeometryToolsInternalCoordinates.get2DCenter(ring);
		System.out.println("  paintInnerBond (=working) now at " + center);
		//next few lines draw a green and pink line just for debugging, to be removed later
		graphics.setColor(Color.green);
		Line2D line = new Line2D.Double(
				bond.getAtom(0).getPoint2d().x,				bond.getAtom(0).getPoint2d().y,
				center.x,				center.y						);
		graphics.draw(line);
		Line2D line2 = new Line2D.Double(
				bond.getAtom(1).getPoint2d().x,				bond.getAtom(1).getPoint2d().y,
				center.x,				center.y			);
		graphics.draw(line2);
		Point2d a = bond.getAtom(0).getPoint2d();
		Point2d b = bond.getAtom(1).getPoint2d();
	
		//TODO: put distanceconstant in the renderermodel
		double distanceconstant = 0.15; //distance between inner and outerbond (in world coordinates)

		double distance = distance2points(a,b);
		double u = ((center.x - a.x)*(b.x - a.x) + (center.y - a.y)*(b.y - a.y)) / (Math.pow(distance, 2));
		double px = a.x + u*(b.x - a.x);
		double py = a.y + u*(b.y - a.y);
		System.out.println("distancea and b: " + distance + " u: " + u + " px: " + px + " py " + py);
		graphics.setColor(Color.pink);
		Point2d z = new Point2d(px, py);
		Line2D linepink = new Line2D.Double(
				z.x,
				z.y,
				center.x,
				center.y
			);
		graphics.draw(linepink);
		graphics.setColor(bondColor);

		double ae = distance2points(a ,z) / distance2points(center, z) * distanceconstant;
		double af = Math.sqrt(Math.pow(ae,2) + Math.pow(distanceconstant,2));
		
		double pfx = a.x + af*(center.x - a.x);
		double pfy = a.y + af*(center.y - a.y);
		
		
		double bh = distance2points(b, z) / distance2points(center, z) * distanceconstant;
		double bi = Math.sqrt(Math.pow(bh, 2) + Math.pow(distanceconstant,2));
		
		double pix = b.x + bi*(center.x - b.x);
		double piy = b.y + bi*(center.y - b.y);

		graphics.setColor(Color.BLACK);
		Line2D linegood = new Line2D.Double(
				pfx,
				pfy,
				pix,
				piy
			);
		graphics.draw(linegood);
		graphics.setColor(bondColor);
		
	}

	/**
	 *  Triggers the suitable method to paint each of the given bonds and selects
	 *  the right color.
	 *
	 *@param  ringSet   The set of rings the molecule contains
	 */
	public void paintBonds(IAtomContainer atomCon, IRingSet ringSet, Graphics2D graphics)
	{
		Color bondColor;
		IRing ring;
		Iterator bonds = atomCon.bonds();
		ArrayList painted_rings = new ArrayList();

		logger.debug("Painting bonds...");
		System.out.println("--doing paintBonds now");
		while (bonds.hasNext())
		{
			IBond currentBond = (IBond)bonds.next();
			
			bondColor = (Color) rendererModel.getColorHash().get(currentBond);
			if (bondColor == null)
			{
				bondColor = rendererModel.getForeColor();
			}
			if (currentBond == rendererModel.getHighlightedBond() && 
					(rendererModel.getSelectedPart()==null || !rendererModel.getSelectedPart().contains(currentBond)))
			{
				bondColor = rendererModel.getHoverOverColor();
				for (int j = 0; j < currentBond.getAtomCount(); j++)
				{
					paintColouredAtomBackground(currentBond.getAtom(j), bondColor, graphics);
					
				}
			}
			ring = RingSetManipulator.getHeaviestRing(ringSet, currentBond);
			if (ring != null)
			{
				System.out.println("found a ring, ringIsAromatic(ring) " + ringIsAromatic(ring) + " getShowAromaticity: "
						+ rendererModel.getShowAromaticity());

				logger.debug("Found ring to draw");
				if (ringIsAromatic(ring) && rendererModel.getShowAromaticity())
				{
					logger.debug("Ring is aromatic");
					if (!painted_rings.contains(ring))
					{
						paintRingRing(ring, bondColor, graphics);
						painted_rings.add(ring);
					}
					paintSingleBond(currentBond, bondColor, graphics);
				} else
				{
					logger.debug("Ring is *not* aromatic");
					paintRingBond(currentBond, ring, bondColor, graphics);
				}
			} else
			{
				System.out.println("no ring found!");

				logger.debug("Drawing a non-ring bond");
				paintBond(currentBond, bondColor, graphics);
			}
		}
	}
	/**
	 *  Triggers the paint method suitable to the bondorder of the given bond that
	 *  is part of a ring with CDK's grey inner bonds.
	 *
	 *@param  bond       The Bond to be drawn.
	 */
	public void paintRingBond(org.openscience.cdk.interfaces.IBond bond, IRing ring, Color bondColor, Graphics2D graphics)
	{
		Point2d center = GeometryToolsInternalCoordinates.get2DCenter(ring);
		System.out.println(" painting paintRingBond now at " + center + " bond: " + bond);


		if (bond.getOrder() == 1.0)
		{
			// Added by rstefani (in fact, code copied from paintBond)
			if (bond.getStereo() != CDKConstants.STEREO_BOND_NONE && bond.getStereo() != CDKConstants.STEREO_BOND_UNDEFINED)
			{
				// Draw stero information if available
				if (bond.getStereo() >= CDKConstants.STEREO_BOND_UP)
				{
					paintWedgeBond(bond, bondColor, graphics);
				} else
				{
					paintDashedWedgeBond(bond, bondColor, graphics);
				}
			} else
			{
				// end code by rstefani
				System.out.println("  singlebond in ring");
				paintSingleBond(bond, bondColor, graphics);
			}
		} else if (bond.getOrder() == 2.0)
		{
			
			paintSingleBond(bond, bondColor, graphics);
			paintInnerBond(bond, ring, bondColor, graphics);
		} else if (bond.getOrder() == 3.0)
		{
			paintTripleBond(bond, bondColor, graphics);
		} else
		{
			logger.warn("Drawing bond as single even though it has order: ", bond.getOrder());
			System.out.println("Drawing bond as single even though it has order: " + bond.getOrder());

			paintSingleBond(bond, bondColor, graphics);
		}
	}
	/**
	 *  Paints the given bond as a wedge bond.
	 *
	 *@param  bond       The singlebond to be drawn
	 *@param  bondColor  Color of the bond
	 */
	public void paintWedgeBond(org.openscience.cdk.interfaces.IBond bond, Color bondColor, Graphics2D graphics)
	{
		System.out.print("painting paintWedgeBond now for: " + bond);
		double wedgeWidth = rendererModel.getBondWidth() /10;
		//perhaps introduce a new setting instead of using getBondWidth here
		System.out.println(" wedgeWidth: " + wedgeWidth);
		
		double x0, x1, y0, y1;

		if (bond.getStereo() == CDKConstants.STEREO_BOND_UP)
		{ //FIXME: check if this is correct, I think the difference between STEREO_BOND_UP and the 
			//other is which is the 'startpoint', please tell me (Nout) if this is correct. 
			x0 = bond.getAtom(0).getPoint2d().x;
			x1 = bond.getAtom(1).getPoint2d().x;
			y0 = bond.getAtom(0).getPoint2d().y;
			y1 = bond.getAtom(1).getPoint2d().y;
		} else
		{
			x1 = bond.getAtom(0).getPoint2d().x;
			x0 = bond.getAtom(1).getPoint2d().x;
			y1 = bond.getAtom(0).getPoint2d().y;
			y0 = bond.getAtom(1).getPoint2d().y;
		}
		double angle;
		if ((x1 - x0) == 0) {
			angle = Math.PI / 2;
		} else {
			angle = Math.atan((y1 - y0) / (x1 - x0));
		}
		float newxup = (float)(x1 - Math.sin(angle) * wedgeWidth);
		float newyup = (float)(y1 + Math.cos(angle) * wedgeWidth);
		
		float newxdown = (float)(x1 + Math.sin(angle) * wedgeWidth);
		float newydown = (float)(y1 - Math.cos(angle) * wedgeWidth);
		
		GeneralPath p = new GeneralPath(); //create a triangle with GenaralPath
		p.moveTo((float)x0, (float)y0);
		p.lineTo(newxup, newyup);
		p.lineTo(newxdown, newydown);
		p.closePath();
		
		graphics.setColor(bondColor);
        graphics.fill(p);
	}
	/**
	 *  Paints the given bond as a dashed wedge bond.
	 *
	 *@param  bond       The single bond to be drawn
	 *@param  bondColor  Color of the bond
	 */
	public void paintDashedWedgeBond(org.openscience.cdk.interfaces.IBond bond, Color bondColor, Graphics2D graphics)
	{
		System.out.println("painting paintDashedWedgeBond now for: " + bond);
		double wedgeWidth = rendererModel.getBondWidth() /10;
		double bondWidth = rendererModel.getBondWidth() / 40;
		double x0, x1, y0, y1;

		if (bond.getStereo() == CDKConstants.STEREO_BOND_DOWN)
		{ //FIXME: check if this is correct, I think the difference between STEREO_BOND_UP and the 
			//other is which is the 'startpoint', please tell me (Nout) if this is correct. 
			x0 = bond.getAtom(0).getPoint2d().x;
			x1 = bond.getAtom(1).getPoint2d().x;
			y0 = bond.getAtom(0).getPoint2d().y;
			y1 = bond.getAtom(1).getPoint2d().y;
		} else
		{
			x1 = bond.getAtom(0).getPoint2d().x;
			x0 = bond.getAtom(1).getPoint2d().x;
			y1 = bond.getAtom(0).getPoint2d().y;
			y0 = bond.getAtom(1).getPoint2d().y;
		}
		double angle;
		if ((x1 - x0) == 0) {
			angle = Math.PI / 2;
		} else {
			angle = Math.atan((y1 - y0) / (x1 - x0));
		}
		float newxup = (float)(x1 - Math.sin(angle) * wedgeWidth);
		float newyup = (float)(y1 + Math.cos(angle) * wedgeWidth);
		
		float newxdown = (float)(x1 + Math.sin(angle) * wedgeWidth);
		float newydown = (float)(y1 - Math.cos(angle) * wedgeWidth);
		
		double bondLength = distance2points(bond.getAtom(0).getPoint2d(), bond.getAtom(1).getPoint2d());
		int numberOfLines = (int) (bondLength / bondWidth / 2);

		System.out.println("lines: " + numberOfLines);
		
		graphics.setColor(bondColor);
		
		double xl, xr, yl, yr;
		Line2D.Double line = new Line2D.Double();
		for (int i = 0; i < numberOfLines - 2; i++) {
			double t = (double)i / numberOfLines;
			xl = x0 + t * (newxup - x0);
			xr = x0 + t * (newxdown - x0);
			yl = y0 + t * (newyup - y0);
			yr = y0 + t * (newydown - y0);
			//System.out.println(i + " : " + t + " from " + xl + " ; " + yl + " to: " + xr + " ; " + yr);
			line.setLine(xl, yl, xr, yr);
			graphics.draw(line);
		}
	}

	/**
	 *  Draws the ring in an aromatic ring.
	 */
	public void paintRingRing(IRing ring, Color bondColor, Graphics2D graphics)
	{
		Point2d center = GeometryToolsInternalCoordinates.get2DCenter(ring);
		System.out.println(" painting a Ringring now at " + center);
		
		double[] minmax = GeometryToolsInternalCoordinates.getMinMax(ring);
		double width = (minmax[2] - minmax[0]) * 0.7;
		double height = (minmax[3] - minmax[1]) * 0.7;
		
		//make a circle
		if (width > height)
			width = height;
		else if (height > width)
			height = width;
		
		double[] coords = { (center.x - (width / 2.0)), (center.y - (height / 2.0)) };
		//offset is the width of the ring
		double offset = (0.05 * Math.max(width, height));
		double offsetX2 = 2 * offset;

		// Fill outer oval.
		graphics.setColor(bondColor);
		Shape shape = new Ellipse2D.Double(coords[0], coords[1], width, height);
		graphics.fill(shape);
		
		// Erase inner oval.
		graphics.setColor(rendererModel.getBackColor());
		shape = new Ellipse2D.Double(coords[0] + offset, coords[1] + offset, width - offsetX2, height - offsetX2);
		graphics.fill(shape);
		
		// Reset drawing colour.
		graphics.setColor(bondColor);
	}
	/**
	 *  Triggers the paint method suitable to the bondorder of the given bond.
	 *
	 *@param  bond       The Bond to be drawn.
	 */
	public void paintBond(IBond bond, Color bondColor, Graphics2D graphics)
	{

		System.out.println("      paintBond, getstereo: " + bond.getStereo() + " getorder: " + bond.getOrder() + " x,y: " + bond.getAtom(0).getPoint2d().x + "," +
				bond.getAtom(0).getPoint2d().y);
		
		if (!GeometryToolsInternalCoordinates.has2DCoordinates(bond)) {
			return;
		}
		
		if (!rendererModel.getShowExplicitHydrogens()) {
			if (bond.getAtom(0).getSymbol().equals("H")) return;
			if (bond.getAtom(1).getSymbol().equals("H")) return;
		}

		if (bond.getStereo() != CDKConstants.STEREO_BOND_NONE && bond.getStereo() != CDKConstants.STEREO_BOND_UNDEFINED)
		{
			// Draw stereo information if available
			if (bond.getStereo() >= CDKConstants.STEREO_BOND_UP)
			{
				paintWedgeBond(bond, bondColor, graphics);
			} else
			{
				paintDashedWedgeBond(bond, bondColor, graphics);
			}
		} else
		{
			// Draw bond order when no stereo info is available
			if (bond.getOrder() == CDKConstants.BONDORDER_SINGLE)
			{
				paintSingleBond(bond, bondColor, graphics);
			} else if (bond.getOrder() == CDKConstants.BONDORDER_DOUBLE)
			{
				paintDoubleBond(bond, bondColor, graphics);
			} else if (bond.getOrder() == CDKConstants.BONDORDER_TRIPLE)
			{
				paintTripleBond(bond, bondColor, graphics);
			} else if (bond.getOrder() == 8.)
			{
				paintAnyBond(bond, bondColor, graphics);
			} else
			{
				System.out.println("       painting single bond because order > 3?");
				// paint all other bonds as single bonds
				paintSingleBond(bond, bondColor, graphics);
			}
		}
	}
	/**
	 *  Paints the given 'Any'  bond.
	 *
	 *@param  bond The given 'Any'  bond to be drawn
	 */
	public void paintAnyBond(org.openscience.cdk.interfaces.IBond bond, Color bondColor, Graphics2D graphics)
	{
		//TODO: rewrite this old code:
		/*if (GeometryToolsInternalCoordinates.has2DCoordinates(bond))
		{
			int[] screencoords=getScreenCoordinates(GeometryTools.getBondCoordinates(bond, r2dm.getRenderingCoordinates()));
			int dashlength=4;
			int spacelength=4;
            if ((screencoords[0] == screencoords[2]) && (screencoords[1] == screencoords[3]))
            {
                    graphics.drawLine(screencoords[0], screencoords[1], screencoords[2], screencoords[3]);
                    return;
            }
            double linelength = Math.sqrt((screencoords[2] - screencoords[0]) * (screencoords[2] - screencoords[0]) + (screencoords[3] - screencoords[1]) * (screencoords[3] - screencoords[1]));
            double xincdashspace = (screencoords[2] - screencoords[0]) / (linelength / (dashlength + spacelength));
            double yincdashspace = (screencoords[3] - screencoords[1]) / (linelength / (dashlength + spacelength));
            double xincdash = (screencoords[2] - screencoords[0]) / (linelength / (dashlength));
            double yincdash = (screencoords[3] - screencoords[1]) / (linelength / (dashlength));
            int counter = 0;
            for (double i = 0; i < linelength - dashlength; i += dashlength + spacelength)
            {
                    graphics.drawLine((int) (screencoords[0] + xincdashspace * counter), (int) (screencoords[1] + yincdashspace * counter), (int) (screencoords[0] + xincdashspace * counter + xincdash), (int) (screencoords[1] + yincdashspace * counter + yincdash));
                    counter++;
            }
            if ((dashlength + spacelength) * counter <= linelength)
            {
                    graphics.drawLine((int) (screencoords[0] + xincdashspace * counter), (int) (screencoords[1] + yincdashspace * counter), screencoords[2], screencoords[3]);
            }
		}*/
	}
	/**
	 *  Paints the given double bond.
	 *
	 *@param  bond       The double bond to be drawn
	 */
	public void paintDoubleBond(IBond bond, Color bondColor, Graphics2D graphics)
	{
		if (GeometryToolsInternalCoordinates.has2DCoordinates(bond))
		{
			double[] tempc = new double[] { bond.getAtom(0).getPoint2d().x, bond.getAtom(0).getPoint2d().y,
					bond.getAtom(1).getPoint2d().x, bond.getAtom(1).getPoint2d().y};
			
			double[] coords = GeometryToolsInternalCoordinates.distanceCalculator(tempc, 0.1);

			Line2D line = new Line2D.Double(
					coords[0], coords[1], coords[6], coords[7]
				);
			paintOneBond(line, bondColor, graphics);

			Line2D line2 = new Line2D.Double(
					coords[2], coords[3], coords[4], coords[5]
				);
			paintOneBond(line2, bondColor, graphics);
			
		}
	}
	/**
	 *  Paints the given triple bond.
	 *
	 *@param  bond       The triple bond to be drawn
	 */
	public void paintTripleBond(org.openscience.cdk.interfaces.IBond bond, Color bondColor, Graphics2D graphics)
	{
		System.out.println("painting paintTripleBond now at " + bond.getAtom(0).getPoint2d());

		paintSingleBond(bond, bondColor, graphics);
		//TODO: rewrite this old code:
	/*	int[] coords = GeometryTools.distanceCalculator(GeometryTools.getBondCoordinates(bond,r2dm.getRenderingCoordinates()), (r2dm.getBondWidth() / 2 + r2dm.getBondDistance()));

		int[] newCoords1 = {coords[0], coords[1], coords[6], coords[7]};
		paintOneBond(newCoords1, bondColor, graphics);

		int[] newCoords2 = {coords[2], coords[3], coords[4], coords[5]};
		paintOneBond(newCoords2, bondColor, graphics);*/
	}

	public void paintSingleBond(IBond bond, Color bondColor, Graphics2D graphics)
	{
		System.out.println("  painting paintSingleBond " + bond.getAtom(0).getPoint2d() + " // " + bond.getAtom(1).getPoint2d());
		if (GeometryToolsInternalCoordinates.has2DCoordinates(bond))
		{
			Line2D line = new Line2D.Double(
					bond.getAtom(0).getPoint2d().x,
					bond.getAtom(0).getPoint2d().y,
					bond.getAtom(1).getPoint2d().x,
					bond.getAtom(1).getPoint2d().y
				);
			paintOneBond(line, bondColor, graphics);
		}
	}
	
	/**
	 *  Really paints the bond. It is triggered by all the other paintbond methods
	 *  to draw a polygon as wide as bond width.
	 *
	 *@param  coords
	 *@param  bondColor  Color of the bond
	 */
	public void paintOneBond(Line2D line, Color bondColor, Graphics2D graphics)
	{
		// draw the shapes
		graphics.setColor(bondColor);
		graphics.setStroke(new BasicStroke(
				(float) (rendererModel.getBondWidth()/rendererModel.getBondLength()), 
				BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
		);
		graphics.setColor(bondColor);
		graphics.draw(line);
	}
	
	private AffineTransform createScaleTransform(Rectangle2D contextBounds, Rectangle2D rendererBounds) {
		AffineTransform affinet = new AffineTransform();
		
		//scale
		double factor = rendererModel.getZoomFactor() * (1.0 - rendererModel.getMargin() * 2.0);
	    double scaleX = factor * rendererBounds.getWidth() / contextBounds.getWidth();
	    double scaleY = factor * rendererBounds.getHeight() / contextBounds.getHeight();

	    if (scaleX > scaleY) {
	    	//System.out.println("Scaled by Y: " + scaleY);
	    	// FIXME: should be -X: to put the origin in the lower left corner 
	    	affinet.scale(scaleY, -scaleY);
	    } else {
	    	//System.out.println("Scaled by X: " + scaleX);
	    	// FIXME: should be -X: to put the origin in the lower left corner 
	    	affinet.scale(scaleX, -scaleX);
	    }
	    //translate
	    double scale = affinet.getScaleX();
		//System.out.println("scale: " + scale);
	    double dx = -contextBounds.getX() * scale + 0.5 * (rendererBounds.getWidth() - contextBounds.getWidth() * scale);
	    double dy = -contextBounds.getY() * scale - 0.5 * (rendererBounds.getHeight() + contextBounds.getHeight() * scale);
	    //System.out.println("dx: " + dx + " dy:" +dy);						
	    affinet.translate(dx / scale, dy / scale);
	    
		return affinet;
	}
	
	
	/*public static Point2D getCoorFromScreen(Graphics2D graphics, Point2D ptSrc) {
		Point2D ptDst = new Point2D.Double();
		AffineTransform affine = graphics.getTransform();
		try {
			affine.inverseTransform(ptSrc, ptDst);
		}
		catch (Exception exception) {
			System.out.println("Unable to reverse affine transformation");
			System.exit(0);
		}
		return ptDst;
	}*/
	/**
	 *  Returns model coordinates from screencoordinates provided by the graphics translation
	 *   
	 * @param ptSrc the point to convert
	 * @return Point2D in real world coordinates
	 */
	public Point2D getCoorFromScreen(Point2D ptSrc) {
		Point2D ptDst = new Point2D.Double();
		try {
			affine.inverseTransform(ptSrc, ptDst);
		}
		catch (Exception exception) {
			System.out.println("Unable to reverse affine transformation");
			System.exit(0);
		}
		return ptDst;
	}
	/**
	 * 
	 * @param container
	 * @param ptSrc in real world coordinates (ie not screencoordinates)
	 */
	public static void showClosestAtomOrBond(IAtomContainer container, Point2D ptSrc) {
		IAtom atom = GeometryToolsInternalCoordinates.getClosestAtom( ptSrc.getX(), ptSrc.getY(), container);
		double Atomdist = Math.sqrt(Math.pow(atom.getPoint2d().x - ptSrc.getX(), 2) + Math.pow(atom.getPoint2d().y - ptSrc.getY(), 2));

		System.out.println("closest Atom distance: " + Atomdist + " Atom:" + atom);
		
		IBond bond = GeometryToolsInternalCoordinates.getClosestBond( ptSrc.getX(), ptSrc.getY(), container);
		Point2d bondCenter = GeometryToolsInternalCoordinates.get2DCenter(bond.atoms());
		
		double Bonddist = Math.sqrt(Math.pow(bondCenter.x - ptSrc.getX(), 2) + Math.pow(bondCenter.y - ptSrc.getY(), 2));
		System.out.println("closest Bond distance: " + Bonddist + " Bond: " + bond);
	}
	
	private Rectangle2D createRectangle2D(List shapes) {
	    Iterator it = shapes.iterator();
	    
	    if (it.hasNext()) {
	    	Rectangle2D result = ((Shape) it.next()).getBounds2D();
	    
	    	while (it.hasNext()) { 
	    		Rectangle2D.union(result, ((Shape) it.next()).getBounds2D(), result);
	    	}
	        
	    	// FIXME: make a decent estimate for the margin
	    	double margin = result.getHeight() / 20; //5% margin
	    	if (margin < 1) {
	    		margin = 1; //1 is ~enough to make symbols + text appear on screen	
	    	}
	    	result.setRect(result.getMinX() - margin, result.getMinY() - margin, result.getWidth() + 2 * margin, result.getHeight() + 2 * margin);
	    
	    	return result;    
	    }
	    else 
	    	return null;
	}
	
	public Renderer2DModel getRenderer2DModel() {
		return this.rendererModel;
	}

	public void setRenderer2DModel(Renderer2DModel model) {
		this.rendererModel = model;
	}
}
