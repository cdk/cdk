/* $Revision$ $Author$ $Date$
*
*  Copyright (C) 2008 Gilleain Torrance <gilleain.torrance@gmail.com>
*
*  Contact: cdk-devel@list.sourceforge.net
*
*  This program is free software; you can redistribute it and/or
*  modify it under the terms of the GNU Lesser General Public License
*  as published by the Free Software Foundation; either version 2.1
*  of the License, or (at your option) any later version.
*
*  This program is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*  GNU Lesser General Public License for more details.
*
*  You should have received a copy of the GNU Lesser General Public License
*  along with this program; if not, write to the Free Software
*  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
*/

package org.openscience.cdk.renderer.visitor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.ArrowElement;
import org.openscience.cdk.renderer.elements.AtomSymbolElement;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.GeneralPath;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.LineElement;
import org.openscience.cdk.renderer.elements.OvalElement;
import org.openscience.cdk.renderer.elements.PathElement;
import org.openscience.cdk.renderer.elements.RectangleElement;
import org.openscience.cdk.renderer.elements.TextElement;
import org.openscience.cdk.renderer.elements.TextGroupElement;
import org.openscience.cdk.renderer.elements.WedgeLineElement;
import org.openscience.cdk.renderer.elements.path.Type;
import org.openscience.cdk.renderer.font.AWTFontManager;
import org.openscience.cdk.renderer.font.IFontManager;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator;
import org.openscience.cdk.renderer.generators.IGeneratorParameter;
import org.openscience.cdk.renderer.generators.BasicBondGenerator.WedgeWidth;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator.Scale;
import org.openscience.cdk.renderer.generators.ReactionSceneGenerator.ArrowHeadWidth;


/**
 * @cdk.module renderawt
 */
public class AWTDrawVisitor extends AbstractAWTDrawVisitor {
	
    /**
     * The font manager cannot be set by the constructor as it needs to
     * be managed by the Renderer.
     */
    private AWTFontManager fontManager;

    /**
     * The renderer model cannot be set by the constructor as it needs to
     * be managed by the Renderer.
     */
	private RendererModel rendererModel;
	
	private final Map<Integer, BasicStroke> strokeMap = 
	    new HashMap<Integer, BasicStroke>();
	
	private final Map<TextAttribute, Object> map = 
        new Hashtable<TextAttribute, Object>();
	
	private final Graphics2D g;

    private Color backgroundColor;
	
	public AWTDrawVisitor(Graphics2D g) {
		this.g = g;
		this.fontManager = null;
		this.rendererModel = null;
		
        map.put(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUB);

        for (IGeneratorParameter<?> param :
            new BasicSceneGenerator().getParameters()) {
            if (param instanceof BasicSceneGenerator.BackgroundColor)
            this.backgroundColor = (Color)param.getDefault();
        }
	}
	
	public void visitElementGroup(ElementGroup elementGroup) {
		elementGroup.visitChildren(this);
	}
	
    public void visit(ElementGroup elementGroup) {
        elementGroup.visitChildren(this);
    }

    public void visit(ArrowElement line) {
    	double scale = this.rendererModel.getParameter(
            Scale.class).getValue();
        Stroke savedStroke = this.g.getStroke();
        
        int w = (int) (line.width * scale);
        if (strokeMap.containsKey(w)) {
            this.g.setStroke(strokeMap.get(w));
        } else {
            BasicStroke stroke = new BasicStroke(w);
            this.g.setStroke(stroke);
            strokeMap.put(w, stroke);
        }
        
        this.g.setColor(line.color);
        int[] a = this.transformPoint(line.x1, line.y1);
        int[] b = this.transformPoint(line.x2, line.y2);
        this.g.drawLine(a[0], a[1], b[0], b[1]);
        double aW = rendererModel.getParameter(
        	ArrowHeadWidth.class
        ).getValue() / scale;
        if(line.direction){
	        int[] c = this.transformPoint(line.x1-aW, line.y1-aW);
	        int[] d = this.transformPoint(line.x1-aW, line.y1+aW);
	        this.g.drawLine(a[0], a[1], c[0], c[1]);
	        this.g.drawLine(a[0], a[1], d[0], d[1]);
        }else{
	        int[] c = this.transformPoint(line.x2+aW, line.y2-aW);
	        int[] d = this.transformPoint(line.x2+aW, line.y2+aW);
	        this.g.drawLine(b[0], b[1], c[0], c[1]);
	        this.g.drawLine(b[0], b[1], d[0], d[1]);
        }        
        this.g.setStroke(savedStroke);
    }
    
    
    public void visit(LineElement line) {
        Stroke savedStroke = this.g.getStroke();
        
        int w = (int) (line.width * this.rendererModel.getParameter(
            	Scale.class).getValue());
        if (strokeMap.containsKey(w)) {
            this.g.setStroke(strokeMap.get(w));
        } else {
            BasicStroke stroke = new BasicStroke(w);
            this.g.setStroke(stroke);
            strokeMap.put(w, stroke);
        }
        
        this.g.setColor(line.color);
        int[] a = this.transformPoint(line.x1, line.y1);
        int[] b = this.transformPoint(line.x2, line.y2);
        this.g.drawLine(a[0], a[1], b[0], b[1]);
        
        this.g.setStroke(savedStroke);
    }

    public void visit(OvalElement oval) {
        this.g.setColor(oval.color);
        int radius = scaleX(oval.radius);
        int diameter = scaleX(oval.radius * 2);
 
        if (oval.fill) {
        	this.g.fillOval(transformX(oval.x) - radius,
                        transformY(oval.y) - radius,
                        diameter,
                        diameter );
        } else { 
        	this.g.drawOval(transformX(oval.x) - radius,
                        transformY(oval.y) - radius,
                        diameter,
                        diameter );
        }
    }
    
    private int scaleX(double x) {
        return (int) (x*transform.getScaleX());
    }
    
    private int transformX(double x) {
        return (int) transform( x, 1 )[0];
    }
 
    private int transformY(double y) {
        return (int) transform( 1, y )[1];
    }
 
    private double[] transform(double x, double y) {
        double [] result = new double[2];
        transform.transform( new double[] {x,y}, 0, result, 0, 1 );
        return result;
    }

    public void visit(TextElement textElement) {
        this.g.setFont(this.fontManager.getFont());
        Point p = this.getTextBasePoint(
                textElement.text, textElement.xCoord, textElement.yCoord, g);
        Rectangle2D textBounds =
                this.getTextBounds(
                        textElement.text, textElement.xCoord, textElement.yCoord, g);
        this.g.setColor(backgroundColor);
        this.g.fill(textBounds);
        this.g.setColor(textElement.color);
        this.g.drawString(textElement.text, p.x, p.y);
    }
    
    public void visit(WedgeLineElement wedge) {
        // make the vector normal to the wedge axis
        Vector2d normal = 
            new Vector2d(wedge.y1 - wedge.y2, wedge.x2 - wedge.x1);
        normal.normalize();
        normal.scale(
                rendererModel.getParameter(WedgeWidth.class).getValue() 
                / rendererModel.getParameter(Scale.class).getValue());  
        
        // make the triangle corners
        Point2d vertexA = new Point2d(wedge.x1, wedge.y1);
        Point2d vertexB = new Point2d(wedge.x2, wedge.y2);
        Point2d vertexC = new Point2d(vertexB);
        vertexB.add(normal);
        vertexC.sub(normal);
        this.g.setColor(wedge.color);
        if (wedge.isDashed) {
            this.drawDashedWedge(vertexA, vertexB, vertexC);
        } else {
            this.drawFilledWedge(vertexA, vertexB, vertexC);
        }
    }
    
    private void drawFilledWedge(
            Point2d vertexA, Point2d vertexB, Point2d vertexC) {
        int[] pB = this.transformPoint(vertexB.x, vertexB.y);
        int[] pC = this.transformPoint(vertexC.x, vertexC.y);
        int[] pA = this.transformPoint(vertexA.x, vertexA.y);
        
        int[] xs = new int[] { pB[0], pC[0], pA[0] };
        int[] ys = new int[] { pB[1], pC[1], pA[1] };
        this.g.fillPolygon(xs, ys, 3);
    }
    
    private void drawDashedWedge(
            Point2d vertexA, Point2d vertexB, Point2d vertexC) {
        // store the current stroke
        Stroke storedStroke = this.g.getStroke();
        this.g.setStroke(new BasicStroke(1));
        
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
            int[] p1T = this.transformPoint(p1.x, p1.y);
            int[] p2T = this.transformPoint(p2.x, p2.y);
            this.g.drawLine(p1T[0], p1T[1], p2T[0], p2T[1]);
            if (distance * (d + gapFactor) >= distance) {
                break;
            } else {
                d += gapFactor;
            }
        }
        this.g.setStroke(storedStroke);
    }
    
    public void visit(AtomSymbolElement atomSymbol) {
        this.g.setFont(this.fontManager.getFont());
        Point p = 
            super.getTextBasePoint(
                    atomSymbol.text, atomSymbol.xCoord, atomSymbol.yCoord, g);
        Rectangle2D textBounds = 
            this.getTextBounds(atomSymbol.text, atomSymbol.xCoord, atomSymbol.yCoord, g);
        this.g.setColor(backgroundColor);
        this.g.fill(textBounds);
        this.g.setColor(atomSymbol.color);
        this.g.drawString(atomSymbol.text, p.x, p.y);
        
        int offset = 10;    // XXX
        String chargeString;
        if (atomSymbol.formalCharge == 0) {
            return;
        } else if (atomSymbol.formalCharge == 1) {
            chargeString = "+";
        } else if (atomSymbol.formalCharge > 1) {
            chargeString = atomSymbol.formalCharge + "+";
        } else if (atomSymbol.formalCharge == -1) {
            chargeString = "-";
        } else if (atomSymbol.formalCharge < -1) {
            int absCharge = Math.abs(atomSymbol.formalCharge);
            chargeString = absCharge + "-";
        } else {
            return;
        }
       
        int x = (int) textBounds.getCenterX();
        int y = (int) textBounds.getCenterY();
        if (atomSymbol.alignment == 1) {           // RIGHT
            this.g.drawString(
                    chargeString, x + offset, (int)textBounds.getMinY());
        } else if (atomSymbol.alignment == -1) {   // LEFT
            this.g.drawString(
                    chargeString, x - offset, (int)textBounds.getMinY());
        } else if (atomSymbol.alignment == 2) {    // TOP
            this.g.drawString(
                    chargeString, x, y - offset);
        } else if (atomSymbol.alignment == -2) {   // BOT
            this.g.drawString(
                    chargeString, x, y + offset);
        }
        
    }
    
    public void visit(RectangleElement rectangle) {
        int[] p1 = this.transformPoint(rectangle.x, rectangle.y);
        int[] p2 = this.transformPoint(
                rectangle.x + rectangle.width, rectangle.y + rectangle.height);
        this.g.setColor(rectangle.color);
        if (rectangle.filled) {
            this.g.fillRect(p1[0], p1[1], p2[0] - p1[0], p2[1] - p1[1]);
        } else {
            this.g.drawRect(p1[0], p1[1], p2[0] - p1[0], p2[1] - p1[1]);
        }
    }
    
    public void visit(PathElement path) {
        this.g.setColor(path.color);
        for (int i = 1; i < path.points.size(); i++) {
            Point2d point1 = path.points.get(i - 1);
            Point2d point2 = path.points.get(i);
            int[] p1 = this.transformPoint(point1.x, point1.y);
            int[] p2 = this.transformPoint(point2.x, point2.y);
            this.g.drawLine(p1[0], p1[1], p2[0], p2[1]);
        }
    }
    
    public void visit(GeneralPath path) {
        this.g.setColor( path.color );
        java.awt.geom.GeneralPath gp = new java.awt.geom.GeneralPath();
        gp.append( getPathIterator( path, transform) , false );
        this.g.draw( gp );
    }

    private static PathIterator getPathIterator(final GeneralPath path,final AffineTransform transform) {
        return new PathIterator() {

            int index;

            private int type(Type type) {
                switch ( type ) {
                    case MoveTo: return SEG_MOVETO;
                    case LineTo: return SEG_LINETO;
                    case QuadTo: return SEG_QUADTO;
                    case CubicTo: return SEG_CUBICTO;
                    case Close: return SEG_CLOSE;
                    default: return SEG_CLOSE;
                }
            }
            public void next() {
               index++;
            }

            public boolean isDone() {
                return index>= path.elements.size();
            }

            public int getWindingRule() {

                return WIND_EVEN_ODD;
            }

            public int currentSegment( double[] coords ) {
                float[] src = new float[6];
                int type = currentSegment( src );
                double[] srcD = coords;
                for(int i=0;i<src.length;i++){
                    srcD[i] = (double) src[i];
                }
                return type;
            }

            public int currentSegment( float[] coords ) {

                float[] src = path.elements.get( index ).points();
                transform.transform( src, 0, coords, 0, src.length/2 );
                return type(path.elements.get( index ).type());
            }
        };
    }

    public void visit(TextGroupElement textGroup) {
        this.g.setFont(this.fontManager.getFont());
        Point p = 
            super.getTextBasePoint(
                    textGroup.text, textGroup.xCoord, textGroup.yCoord, g);
        Rectangle2D textBounds = 
            this.getTextBounds(textGroup.text, textGroup.xCoord, textGroup.yCoord, g);
        this.g.setColor(backgroundColor);
        this.g.fill(textBounds);
        this.g.setColor(textGroup.color);
        this.g.drawString(textGroup.text, p.x, p.y);
        
        int x = (int) textBounds.getCenterX();
        int y = (int) textBounds.getCenterY();
        int x1 = (int) textBounds.getMinX();
        int y1 = (int) textBounds.getMinY();
        int x2 = p.x + (int)textBounds.getWidth();
        int y2 = (int) textBounds.getMaxY();

        int oW = x2 - x1;
        int oH = y2 - y1;
        for (TextGroupElement.Child child : textGroup.children) {
            int cx;
            int cy;
            
            switch (child.position) {
                case NE:
                    cx = x2;
                    cy = y1;
                    break;
                case N:
                    cx = x1;
                    cy = y1;
                    break;
                case NW:
                    cx = x1 - oW;
                    cy = y1;
                    break;
                case W:
                    cx = x1 - oW;
                    cy = p.y;
                    break;
                case SW:
                    cx = x1 - oW;
                    cy = y1 + oH;
                    break;
                case S:
                    cx = x1;
                    cy = y2 + oH;
                    break;
                case SE:
                    cx = x2;
                    cy = y2 + oH;
                    break;
                case E:
                    cx = x2;
                    cy = p.y;
                    break;
                default:
                    cx = x;
                    cy = y;
                    break;
            }
            
            this.g.drawString(child.text, cx, cy);
            if (child.subscript != null) {
                Rectangle2D childBounds = getTextBounds(child.text, cx, cy, g);
                int scx = (int)(cx + (childBounds.getWidth() * 0.75));
                int scy = (int)(cy + (childBounds.getHeight() / 3));
                Font f = this.g.getFont();   // TODO : move to font manager
                Font subscriptFont = f.deriveFont(f.getStyle(), f.getSize() - 2); 
                this.g.setFont(subscriptFont);
                this.g.drawString(child.subscript, scx, scy);
            } 
        }
    }

    public void visit(IRenderingElement element) {
        Color savedColor = this.g.getColor();
        if (element instanceof ElementGroup)
            visit((ElementGroup) element);
        else if (element instanceof WedgeLineElement)
            visit((WedgeLineElement) element);
        else if (element instanceof LineElement)
            visit((LineElement) element);
        else if (element instanceof ArrowElement)
            visit((ArrowElement) element);
        else if (element instanceof OvalElement)
            visit((OvalElement) element);
        else if (element instanceof TextGroupElement)
            visit((TextGroupElement) element);
        else if (element instanceof AtomSymbolElement)
            visit((AtomSymbolElement) element);
        else if (element instanceof TextElement)
            visit((TextElement) element);
        else if (element instanceof RectangleElement)
            visit((RectangleElement) element);
        else if (element instanceof PathElement)
            visit((PathElement) element);
        else if (element instanceof GeneralPath)
            visit((GeneralPath)element);
        else
            System.err.println("Visitor method for "
                    + element.getClass().getName() + " is not implemented");
        this.g.setColor(savedColor);
    }

    /**
     * The font manager must be set by any renderer that uses this class! 
     */
    public void setFontManager(IFontManager fontManager) {
        this.fontManager = (AWTFontManager) fontManager;
    }

    public void setRendererModel(RendererModel rendererModel) {
        this.rendererModel = rendererModel;
        if (rendererModel.getParameter(UseAntiAliasing.class)
            .getValue()) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
//            g.setStroke(new BasicStroke((int)rendererModel.getBondWidth()));
        }
    }
}
