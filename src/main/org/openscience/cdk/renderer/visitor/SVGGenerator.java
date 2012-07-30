/**
 *  Copyright (C) 2001-2007  The Chemistry Development Kit (CDK) Project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.jchempaint.renderer.visitor;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector4d;

import org.openscience.jchempaint.renderer.RendererModel;
import org.openscience.jchempaint.renderer.elements.ArrowElement;
import org.openscience.jchempaint.renderer.elements.AtomSymbolElement;
import org.openscience.jchempaint.renderer.elements.ElementGroup;
import org.openscience.jchempaint.renderer.elements.IRenderingElement;
import org.openscience.jchempaint.renderer.elements.LineElement;
import org.openscience.jchempaint.renderer.elements.OvalElement;
import org.openscience.jchempaint.renderer.elements.PathElement;
import org.openscience.jchempaint.renderer.elements.RectangleElement;
import org.openscience.jchempaint.renderer.elements.TextElement;
import org.openscience.jchempaint.renderer.elements.TextGroupElement;
import org.openscience.jchempaint.renderer.elements.WedgeLineElement;
import org.openscience.jchempaint.renderer.elements.WigglyLineElement;
import org.openscience.jchempaint.renderer.font.FreeSansBoldGM;
import org.openscience.jchempaint.renderer.font.GlyphMetrics;
import org.openscience.jchempaint.renderer.font.IFontManager;

/**
 * We can only guarantee the same quality of SVG output everywhere
 * by drawing paths and not using fonts. This is an indirect
 * consequence of font commercialisation which has successfully
 * prevented SVG fonts from becoming usable on all browsers. See
 * https://github.com/JChemPaint/jchempaint/wiki/The-svg-font-problem-and-its-solution
 *
 * So, we convert an open font to SVG paths and use these.
 * To resolve the problem of placement, we use bbox, advance and
 * (maybe later) kerning values from the same font. 
 * To make sure bonds don't cross text, we use two passes where
 * text is drawn first and the bonds second.
 * 
 * Two-pass implementation (c) 2012 by
 * @author Ralf Stephan <ralf@ark.in-berlin.de>
 * @jcp.issue #2
 * 
 * First code layer (c) 2007 by
 * @author maclean
 * @cdk.module rendersvg
 * @cdk.bug 2403250
 */
public class SVGGenerator implements IDrawVisitor {

    /**
     * The renderer model cannot be set by the constructor as it needs to
     * be managed by the Renderer.
     */
	private RendererModel rendererModel;
	
	public static final String HEADER = "<?xml version=\"1.0\"?>\n" +
			"<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\"\n" +
			"\"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n" +
			"<svg xmlns=\"http://www.w3.org/2000/svg\" " +
			"xmlns:xlink=\"http://www.w3.org/1999/xlink\" " +
			"viewBox=\"0 0 1234567890\">\n" +
			"<g transform=\"translate(12345,67890)\">";

	private final StringBuffer svg = new StringBuffer();
	
	private FreeSansBoldGM the_fm;

	private AffineTransform transform;
	
	private List<IRenderingElement> elList;
	private List<String> tcList;
	private HashMap<String,Point2d> tgMap;
	private HashMap<Integer,Point2d> ptMap;
	private List<Rectangle2D> bbList;
	
	private double trscale, subscale, subshift, tgpadding, vbpadding;
	private Rectangle2D bbox;
	
	//------------------------------------------------------------------

	public SVGGenerator() {
		the_fm = new FreeSansBoldGM();
		the_fm.init();
		elList = new ArrayList<IRenderingElement>();
		tcList = new ArrayList<String>();
		tgMap = new HashMap<String,Point2d>();
		ptMap = new HashMap<Integer,Point2d>();
		bbList = new ArrayList<Rectangle2D>();
		bbox = null;
				
		svg.append(SVGGenerator.HEADER);
		newline();
		svg.append("<defs>");
		tgpadding = 4;
		vbpadding = 40;
		trscale = 0.03;
		subscale = trscale*0.7;
		subshift = 0.5;
	}

	private void newline() {
		svg.append("\n");
	}
	
	public double[] transformPoint(double x, double y) {
        double[] src = new double[] {x, y};
        double[] dest = new double[2];
        this.transform.transform(src, 0, dest, 0, 1);
        return dest;
    }
	
	public double[] invTransformPoint (double x, double y) {
        double[] src = new double[] {x, y};
        double[] dest = new double[2];
        try {
        	this.transform.createInverse().transform(src, 0, dest, 0, 1);
        } catch (NoninvertibleTransformException e) {
        	System.err.println ("Cannot invert transform!\n");
        }
        return dest;
    }

	/**
	 * Fills two lists: tcList contains all characters used in
	 * atoms, tgList has all strings. We also write the character
	 * paths immediately as DEFS into the SVG, for reference.
	 * 
	 * @param e
	 */
	private void writeDEFS (TextGroupElement e) {
		if (e.text.length()>1 && !tgMap.containsKey(e.text))
			tgMap.put(e.text, new Point2d(0,0));
		for (char c : e.text.toCharArray()) { 
			String idstr = "Atom-" + c;
			GlyphMetrics m = the_fm.map.get((int) c);
			if (!ptMap.containsKey((int) c)) 
				ptMap.put((int) c, new Point2d(m.xMax, m.yMax - m.yMin));
			if(!tcList.contains(idstr)) {
				tcList.add(idstr);
				newline();
				svg.append (String.format(
						"  <path id=\"%s\" transform=\"scale(%1.3f,%1.3f)\" d=\"%s\" />",
						idstr, trscale, -trscale, m.outline));
			}
		}
		
		// Set hyd and hPos according to entry
		int hyd=0, hPos=0;
		for (TextGroupElement.Child ch : e.children) {
			if (ch.text.equals ("H")) {
				if (ch.subscript == null) hyd=1;
				else if (ch.subscript.equals("2")) hyd=2;
				else hyd=3;
				if (ch.position==TextGroupElement.Position.E) hPos=1;
				else if (ch.position==TextGroupElement.Position.W) hPos=-1;
			}
		}
		if (hyd>0) {
			if (!tcList.contains("Atom-H")) {
				tcList.add("Atom-H");
				GlyphMetrics m = the_fm.map.get((int) "H".charAt(0));
				svg.append (String.format(
						"  <path id=\"Atom-H\" transform=\"scale(%1.3f,%1.3f)\" d=\"%s\" />",
						trscale, -trscale, m.outline));
			}
			if (hyd>=2) {
				char c = '2';
				if (hyd==3) c='3';
				String idstr = "Atom-" + c;
				GlyphMetrics m = the_fm.map.get((int) c);
				if(!tcList.contains(idstr)) {
					tcList.add(idstr);
					newline();
					svg.append (String.format(
							"  <path id=\"%s\" transform=\"scale(%1.4f,%1.4f)\" d=\"%s\" />",
							idstr, subscale, -subscale, m.outline));
				}				
			}
		}
	}

	/**
	 * In this first pass, visiting elements are copied to
	 * a list, and DEFS/PATH elements are written for all TextGroups.
	 */
	public void visit(IRenderingElement element) {
		elList.add(element);
		
		if (element instanceof ElementGroup)
			((ElementGroup) element).visitChildren(this);
        else if (element instanceof TextGroupElement)
            writeDEFS ((TextGroupElement) element);
	}
	
	public void draw (OvalElement oval) {
		newline();
		double[] p1 = transformPoint(oval.x - oval.radius, oval.y - oval.radius);
		double[] p2 = transformPoint(oval.x + oval.radius, oval.y + oval.radius);
		double x, y, w, h;
		x = Math.min(p1[0], p2[0]);
		y = Math.min(p1[1], p2[1]);
		w = Math.abs(p2[0] - p1[0]);
		h = Math.abs(p2[1] - p1[1]);
		Rectangle2D rect = new Rectangle2D.Double(x, y, w, h);
		if (bbox==null) bbox=rect;
		else bbox = bbox.createUnion(rect);
		double r = w / 2;
		svg.append(String.format(
				"<ellipse cx=\"%4.2f\" cy=\"%4.2f\" rx=\"%4.2f\" ry=\"%4.2f\" " +
				"style=\"stroke:black; stroke-width:1px; fill:none;\" />",
				x + r, y + r, r, r));
	}

	public void draw (AtomSymbolElement atomSymbol) {
		newline();
		double[] p = transformPoint(atomSymbol.x, atomSymbol.y);
		svg.append(String.format(
				"<text x=\"%s\" y=\"%s\" style=\"fill:%s\"" +
				">%s</text>",
				p[0],
				p[1],
				toColorString(atomSymbol.color),
				atomSymbol.text
				));
	}

	// this is a stupid method, but no idea how else to do it...
	private String toColorString(Color color) {
		if (color == Color.RED) {
			return "red";
		} else if (color == Color.BLUE) {
			return "blue";
		} else {
			return "black";
		}
	}

	public void draw (TextElement textElement) {
		newline();
		double[] p = transformPoint(textElement.x, textElement.y);
		svg.append(String.format(
				"<text x=\"%s\" y=\"%s\">%s</text>",
				p[0],
				p[1],
				textElement.text
				));
	}
	
	/**
	 * At the time of this call, all that we need is in place:
	 * the SVG character macros are written and the bboxes computed.
	 * The textgroup text is now placed with its center at the
	 * position of the atom. Implicit hydrogens are added.
	 * 
	 * @param e
	 */
	public void draw (TextGroupElement e) {
		newline();
		double[] pos = transformPoint(e.x, e.y);
		
		// Determine the bbox of the Atom symbol text
		Point2d bb;
		if (e.text.length() == 1) 
			bb = ptMap.get((int)e.text.charAt(0));
		else
			bb = tgMap.get(e.text);
		
		// Set hyd and hPos according to entry
		int hyd=0, hPos=0;
		for (TextGroupElement.Child c : e.children) {
			if (c.text.equals ("H")) {
				if (c.subscript == null) hyd=1;
				else if (c.subscript.equals("2")) hyd=2;
				else hyd=3;
				if (c.position==TextGroupElement.Position.E) hPos=1;
				else if (c.position==TextGroupElement.Position.W) hPos=-1;
			}
		}

		// Set v to the bbox of the whole TextGroup, add it to
		// the list of such bboxes and enlarge the viewport bbox
		double x, y, w, h;
		x = pos[0]-trscale*bb.x/2;
		y = pos[1]-trscale*bb.y/2-tgpadding;
		w = trscale*bb.x+tgpadding;
		h = trscale*bb.y+2*tgpadding;
		Rectangle2D v = new Rectangle2D.Double(x, y, w, h);
		bbList.add (v);
		if (bbox==null) bbox = v;
                else bbox = bbox.createUnion (v);
 
		// Output use command(s)
		x = pos[0] - trscale*bb.x/2;
		y = pos[1] + trscale*bb.y/2;
		svg.append(String.format(
				"<use xlink:href=\"#Atom-%s\" x=\"%4.2f\" y=\"%4.2f\"/>",
				e.text, x, y));
		if (hyd != 0) {
			GlyphMetrics m = the_fm.map.get(50+hyd);
			if (hPos>0) 
				x += trscale*bb.x;
			else {
				x -= trscale* the_fm.map.get((int) "H".charAt(0)).adv;
				if (hyd>=2)
					x -= subscale*m.adv;
			}
			svg.append(String.format(
					"<use xlink:href=\"#Atom-H\" x=\"%4.2f\" y=\"%4.2f\"/>",
					x, y));
			if (hyd>=2) {
				char c = '2';
				if (hyd==3) c='3';
				String idstr = "Atom-" + c;
				x += trscale* the_fm.map.get((int) "H".charAt(0)).adv;
				y += subshift*subscale*(m.yMax-m.yMin);
				svg.append(String.format(
						"<use xlink:href=\"#%s\" x=\"%4.2f\" y=\"%4.2f\"/>",
						idstr, x, y));
			}
		}
	}
	
	/**
	 * In this second pass, everything except bonds (and arrows)
	 * is placed, the textgroups referring to the DEFS ids. For
	 * intermediate caching, we first add strings to the DEFS block.
	 */
	public void drawNoBonds() {
		newline();
		svg.append("</defs>");
		if (!tgMap.isEmpty()) { newline(); svg.append("<defs>"); }
		for (String s : tgMap.keySet()) {
			newline();
			svg.append(String.format("<g id=\"Atom-%s\">", s));
			boolean first = true;
			int advance = 0;
			int xMin = 9999, xMax = 0, yMin = 9999, yMax = 0;
			for (char c : s.toCharArray()) { 
				svg.append(String.format("<use xlink:href=\"#Atom-%c\" ", c));
				if (first) {
					first=false;
				}
				else {
					svg.append(String.format("transform=\"translate(%4.2f,0)\"", advance*trscale));
				}
				GlyphMetrics m = the_fm.map.get((int)c);
				if (m.xMin + advance < xMin) xMin = m.xMin + advance;
				if (m.xMax + advance > xMax) xMax = m.xMax + advance;
				if (m.yMin < yMin) yMin = m.yMin;
				if (m.yMax > yMax) yMax = m.yMax;
				advance += m.adv;
				svg.append("/>");
			}
			svg.append("</g>");
			Point2d p = tgMap.get(s);
			p.x = xMax - xMin;
			p.y = yMax - yMin;
		}
		if (!tgMap.isEmpty()) { newline(); svg.append("</defs>"); }

		for (IRenderingElement element : elList) {
			if (element instanceof OvalElement)
				draw((OvalElement) element);
	        else if (element instanceof TextGroupElement)
	            draw((TextGroupElement) element);
			else if (element instanceof AtomSymbolElement)
				draw((AtomSymbolElement) element);
			else if (element instanceof TextElement)
				draw((TextElement) element);
			else if (element instanceof RectangleElement)
				draw((RectangleElement) element);
			else if (element instanceof PathElement)
				draw((PathElement) element);
		}
	}
	
	/**
	 * In the third pass, bonds (and arrows) are drawn,
	 * taking care to leave a small distance to atoms with
	 * text.
	 */
	public void drawBonds() {
		for (IRenderingElement element : elList) {
			if (element instanceof WedgeLineElement)
				draw((WedgeLineElement) element);
			else if (element instanceof LineElement)
				draw((LineElement) element);
	        else if (element instanceof ArrowElement)
	            draw((ArrowElement) element);
	        else if (element instanceof WigglyLineElement)
	        	draw((WigglyLineElement) element);
		}
	}

	/**
	 * This is where most of the work is done by calling
	 * the 2nd and 3rd passes, and finally computing
	 * width and height of the document which is set at last.
	 * @return the SVG document as String
	 */
	public String getResult() {
		drawNoBonds();
		drawBonds();
		newline();
		svg.append("</g>\n</svg>\n");
		
		int i = svg.indexOf ("0 0 1234567890");
		svg.replace(i, i+14, String.format("0 0 %4.0f %4.0f",
				bbox.getWidth()+2*vbpadding,
				bbox.getHeight()+2*vbpadding));
		i = svg.indexOf ("12345,67890");
		svg.replace(i, i+11, String.format ("%4.0f,%4.0f",
				-bbox.getMinX()+vbpadding,
				-bbox.getMinY()+vbpadding));
		return svg.toString();
	}

	/**
	 * Applies all collected bboxes to the two points and, if one is
	 * inside a bbox, places it at the bbox's edge, same direction.
	 * Intended to be cumulative, i.e., both points may be moved more
	 * than once, or never. Returns true if any segment is left.
	 * @param p1
	 * @param p2
	 */
	private boolean shorten_line(double[] p1, double[] p2)
	{
		for (Rectangle2D v : bbList) {   // shorten line acc. to bboxes
			boolean inside1 = v.contains(p1[0], p1[1]);
			boolean inside2 = v.contains(p2[0], p2[1]);
			if (!inside1 && !inside2) continue;
			if (inside1 && inside2) return false;
                        
			double px, py, qx, qy, cx=0.0, cy=0.0;
			if (inside1) {
				px = p1[0]; py = p1[1];
				qx = p2[0]; qy = p2[1];
			} else {
				px = p2[0]; py = p2[1];
				qx = p1[0]; qy = p1[1];
			}
			if (qx<v.getX() && v.getX()<px) cx = v.getX();
			if (px<v.getMaxX() && v.getMaxX()<qx) cx = v.getMaxX();
			if (qy<v.getY() && v.getY()<py) cy = v.getY();
			if (py<v.getMaxY() && v.getMaxY()<qy) cy = v.getMaxY();
                        
                        double rx, ry;
			if (qy==py) { rx=cx; ry=py; }
			else if (cx == 0.0) { ry = cy; rx = px + (cy-py)*(qx-px)/(qy-py); }
			else if (qx==px) { ry=cy; rx=px; }
			else if (cy == 0.0) { rx = cx; ry = py + (cx-px)*(qy-py)/(qx-px); }
			else {  // cx, cy, qx-px, qy-py all nonzero
				if (Math.abs((cx-px)/(cy-py)) > Math.abs((qx-px)/(qy-py)))
				{ ry = cy; rx = px + (cy-py)*(qx-px)/(qy-py); }
				else
				{ rx = cx; ry = py + (cx-px)*(qy-py)/(qx-px); }
			}
			if (inside1) {
				p1[0] = (int)rx; p1[1] = (int)ry;
			} else {
				p2[0] = (int)rx; p2[1] = (int)ry;
			}
		}
		return true;
	}

	public void draw (WedgeLineElement wedge) {
		double[] p1 = transformPoint(wedge.x1, wedge.y1);
		double[] p2 = transformPoint(wedge.x2, wedge.y2);
		if (bbox==null) bbox = new Rectangle2D.Double(
                            Math.min(p1[0], p2[0]), Math.min(p1[1], p2[1]),
                            Math.abs(p2[0] - p1[0]), Math.abs(p2[1] - p1[1]));
                else bbox = bbox.createUnion(new Rectangle2D.Double(
                        Math.min(p1[0], p2[0]), Math.min(p1[1], p2[1]),
                        Math.abs(p2[0] - p1[0]), Math.abs(p2[1] - p1[1])));
		if (!shorten_line (p1, p2)) return;
		double w1[] = invTransformPoint (p1[0], p1[1]);
		double w2[] = invTransformPoint (p2[0], p2[1]);
        // make the vector normal to the wedge axis
        Vector2d normal = 
            new Vector2d(w1[1] - w2[1], w2[0] - w1[0]);
        normal.normalize();
        normal.scale(rendererModel.getWedgeWidth() / rendererModel.getScale());  
        
        // make the triangle corners
        Point2d vertexA = new Point2d(w1[0], w1[1]);
        Point2d vertexB = new Point2d(w2[0], w2[1]);
        Point2d vertexC = new Point2d(vertexB);
        vertexB.add(normal);
        vertexC.sub(normal);
        if (wedge.wedgeType==0) {
            this.drawDashedWedge(vertexA, vertexB, vertexC);
        } else if (wedge.wedgeType==1) {
            this.drawFilledWedge(vertexA, vertexB, vertexC);
        } else {
        	this.drawCrissCrossWedge(vertexA, vertexB, vertexC);
        }
	}
	
	    public void draw (WigglyLineElement wedge) {
		    	//TODO add code. see http://www.w3.org/TR/SVG/paths.html#PathDataCurveCommands
		    }
	
	    private void drawCrissCrossWedge(Point2d vertexA, Point2d vertexB,
						Point2d vertexC) {
			        
			        // calculate the distances between lines
			        double distance = vertexB.distance(vertexA);
			        double gapFactor = 0.1;
			        double gap = distance * gapFactor;
			        double numberOfDashes = distance / gap;
			        double d = 0;
			        double[] old=null;
			        
			        // draw by interpolating along the edges of the triangle
			        for (int i = 0; i < numberOfDashes; i++) {
			            double d2 = d-gapFactor;
			            Point2d p1 = new Point2d();
			            p1.interpolate(vertexA, vertexB, d);
			            Point2d p2 = new Point2d();
			            p2.interpolate(vertexA, vertexC, d2);
			            double[] p1T = this.transformPoint(p1.x, p1.y);
			            double[] p2T = this.transformPoint(p2.x, p2.y);
			    		svg.append(String.format(
								"<line x1=\"%4.2f\" y1=\"%4.2f\" x2=\"%4.2f\" y2=\"%4.2f\" " +
								"style=\"stroke:black; stroke-width:1px;\" />",
								p1T[0],
								p1T[1],
								p2T[0],
								p2T[1]
								));
			            if(old==null)
			            	old = p2T;
			    		svg.append(String.format(
								"<line x1=\"%4.2f\" y1=\"%4.2f\" x2=\"%4.2f\" y2=\"%4.2f\" " +
								"style=\"stroke:black; stroke-width:1px;\" />",
								old[0],
								old[1],
								p2T[0],
								p2T[1]
								));
			            old = p1T;
			            if (distance * (d + gapFactor) >= distance) {
			                break;
			            } else {
			                d += gapFactor*2;
			            }
			        }
				}

	
    private void drawFilledWedge(
            Point2d vertexA, Point2d vertexB, Point2d vertexC) {
        double[] pB = this.transformPoint(vertexB.x, vertexB.y);
        double[] pC = this.transformPoint(vertexC.x, vertexC.y);
        double[] pA = this.transformPoint(vertexA.x, vertexA.y);
        
		svg.append(String.format(
				"<polygon points=\"%4.2f,%4.2f %4.2f,%4.2f %4.2f,%4.2f\" "+
					"style=\"fill:black;"+
					"stroke:black;stroke-width:1\"/>", 
				pB[0],pB[1],
				pC[0],pC[1],
				pA[0],pA[1]
				));
    }

	public void draw (PathElement path) {

	}
	

	public void draw (LineElement line) {
		newline();
		double[] p1 = transformPoint(line.x1, line.y1);
		double[] p2 = transformPoint(line.x2, line.y2);
		if (!shorten_line (p1, p2)) return;
                if (bbox == null) {
                    bbox = new Rectangle2D.Double(
                            Math.min(p1[0], p2[0]), Math.min(p1[1], p2[1]),
                            Math.abs(p2[0] - p1[0]), Math.abs(p2[1] - p1[1]));
                } else {
                    bbox = bbox.createUnion(new Rectangle2D.Double(
                            Math.min(p1[0], p2[0]), Math.min(p1[1], p2[1]),
                            Math.abs(p2[0] - p1[0]), Math.abs(p2[1] - p1[1])));
                }
    		svg.append(String.format(
				"<line x1=\"%4.2f\" y1=\"%4.2f\" x2=\"%4.2f\" y2=\"%4.2f\" " +
				"style=\"stroke:black; stroke-width:3px;\" />",
				p1[0],
				p1[1],
				p2[0],
				p2[1]
				));
	}
	
    private void drawDashedWedge(
            Point2d vertexA, Point2d vertexB, Point2d vertexC) {
        
        // calculate the distances between lines
        double distance = vertexB.distance(vertexA);
        double gapFactor = 0.1;
        double gap = distance * gapFactor;
        double numberOfDashes = distance / gap;
        double d = 0;
        
        // draw by interpolating along the edges of the triangle
        for (int i = 0; i < numberOfDashes; i++) {
            Point2d p1 = new Point2d();
            p1.interpolate(vertexA, vertexB, d);
            Point2d p2 = new Point2d();
            p2.interpolate(vertexA, vertexC, d);
            double[] p1T = this.transformPoint(p1.x, p1.y);
            double[] p2T = this.transformPoint(p2.x, p2.y);
    		svg.append(String.format(
					"<line x1=\"%4.2f\" y1=\"%4.2f\" x2=\"%4.2f\" y2=\"%4.2f\" " +
					"style=\"stroke:black; stroke-width:1px;\" />",
					p1T[0],
					p1T[1],
					p2T[0],
					p2T[1]
					));
            
            if (distance * (d + gapFactor) >= distance) {
                break;
            } else {
                d += gapFactor;
            }
        }
    }

    public void draw (ArrowElement line) {
      
        int w = (int) (line.width * this.rendererModel.getScale());
        double[] a = this.transformPoint(line.x1, line.y1);
        double[] b = this.transformPoint(line.x2, line.y2);
        newline();
		svg.append(String.format(
				"<line x1=\"%4.2d\" y1=\"%4.2f\" x2=\"%4.2f\" y2=\"%4.2f\" " +
				"style=\"stroke:black; stroke-width:"+w+"px;\" />",
				a[0],
				a[1],
				b[0],
				b[1]
				));
        double aW = rendererModel.getArrowHeadWidth() / rendererModel.getScale();
        if(line.direction){
	        double[] c = this.transformPoint(line.x1-aW, line.y1-aW);
	        double[] d = this.transformPoint(line.x1-aW, line.y1+aW);
	        newline();
			svg.append(String.format(
					"<line x1=\"%s\" y1=\"%s\" x2=\"%s\" y2=\"%s\" " +
					"style=\"stroke:black; stroke-width:"+w+"px;\" />",
					a[0],
					a[1],
					c[0],
					c[1]
					));
			newline();
			svg.append(String.format(
					"<line x1=\"%s\" y1=\"%s\" x2=\"%s\" y2=\"%s\" " +
					"style=\"stroke:black; stroke-width:"+w+"px;\" />",
					a[0],
					a[1],
					d[0],
					d[1]
					));
        }else{
	        double[] c = this.transformPoint(line.x2+aW, line.y2-aW);
	        double[] d = this.transformPoint(line.x2+aW, line.y2+aW);
	        newline();
			svg.append(String.format(
					"<line x1=\"%s\" y1=\"%s\" x2=\"%s\" y2=\"%s\" " +
					"style=\"stroke:black; stroke-width:"+w+"px;\" />",
					a[0],
					a[1],
					c[0],
					c[1]
					));
			newline();
			svg.append(String.format(
					"<line x1=\"%s\" y1=\"%s\" x2=\"%s\" y2=\"%s\" " +
					"style=\"stroke:black; stroke-width:"+w+"px;\" />",
					a[0],
					a[1],
					d[0],
					d[1]
					));
        }        
    }


	public void draw (RectangleElement rectangleElement) {
        double[] pA = this.transformPoint(rectangleElement.x, rectangleElement.y);
        double[] pB = this.transformPoint(rectangleElement.x+rectangleElement.width, rectangleElement.y);
        double[] pC = this.transformPoint(rectangleElement.x, rectangleElement.y+rectangleElement.height);
        double[] pD = this.transformPoint(rectangleElement.x+rectangleElement.width, rectangleElement.y+rectangleElement.height);
        
        newline();
		svg.append(String.format(
				"<polyline points=\"%s,%s %s,%s %s,%s %s,%s %s,%s\""+
					"style=\"fill:none;"+
					"stroke:black;stroke-width:1\"/>", 
				pA[0],pA[1],
				pB[0],pB[1],
				pD[0],pD[1],
				pC[0],pC[1],
				pA[0],pA[1]
				));

	}

    public void setTransform(AffineTransform transform) {
		this.transform = transform;
		this.transform.setToScale(30, -30);
//		System.err.println(transform.toString());
//		System.err.println(String.format("scale=%f zoom=%f\n", transform.getScaleX(), transform.getScaleY()));
	}

    public void setFontManager(IFontManager fontManager) {
    }

    public void setRendererModel(RendererModel rendererModel) {
        this.rendererModel = rendererModel;
    }

}
