/* $Revision$ $Author$ $Date$
 *  
 * Copyright (C) 2006-2007  Christoph Steinbeck <steinbeck@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
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

import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.tools.LoggingTool;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;

/**
 * A Renderer class which draws 2D representations of curly arrows as used for
 * representing electron movements in organic chemistry text books.
 * Adapted from <a href="http://www.wam.umd.edu/~petersd/Interp2_code.java">http://www.wam.umd.edu/~petersd/Interp2_code.java</a>.
 * 
 * @author      steinbeck
 * @cdk.module  render
 * @cdk.svnrev  $Revision$
 * @cdk.created 2006-03-28
 * @cdk.keyword viewer, 2D-viewer
 * @see         org.openscience.cdk.renderer.Renderer2DModel
 */
public class ArrowRenderer2D {

	Renderer2DModel r2dm;

	protected LoggingTool logger;
	double rotAngle; 
	double offsetAngle;
	Point2d center;	
	Vector2d vector3;

	//private Vector points = new Vector(16, 4);

	//private int precision = 10;
	

	/**
	 * Constructs a ArrowRenderer2D with a default settings model.
	 */
	public ArrowRenderer2D() {
		this(new Renderer2DModel());
	}

	/**
	 * Constructs a ArrowRenderer2D.
	 * 
	 * @param r2dm
	 *            The settings model to use for renderingraphics.
	 */
	public ArrowRenderer2D(Renderer2DModel r2dm) {
		this.r2dm = r2dm;
		logger = new LoggingTool(this);

	}

	/**
	 */
	public void paintArrows(Arrow[] arrows, Graphics2D graphics) 
	{
		
		for (int f = 0; f < arrows.length; f++)
		{
			paintArrow(arrows[f], graphics);
		}
	}

	void paintArrow(Arrow arrow, Graphics2D graphics)
	{
		Dimension ss = r2dm.getBackgroundDimension();
		Arc2D.Double arc = contructArc(arrow);
		graphics.draw(arc);
		Polygon polygon = contructArrowHead(arc);
		graphics.fill(polygon);
		graphics.draw(contructArrowHead(arc));
		graphics.draw(new Ellipse2D.Double(center.x, ss.getHeight() - center.y, 10,10));
	}
	
	public Arc2D.Double contructArc(Arrow arrow)
	{
		Dimension ss = r2dm.getBackgroundDimension();
		IAtom start = arrow.getStart();
		IAtom end = arrow.getEnd();
		Arc2D.Double arc = new Arc2D.Double();
		IAtomContainer atomContainer = start.getBuilder().newAtomContainer();
		atomContainer.addAtom(start);
		atomContainer.addAtom(end);
		center = GeometryTools.get2DCenter(atomContainer,r2dm.getRenderingCoordinates());
		Point2d point1 = new Point2d((Point2d)r2dm.getRenderingCoordinate(start));
		Point2d point2 = new Point2d((Point2d)r2dm.getRenderingCoordinate(end));
		Vector2d vector1 = new Vector2d(point1);
		Vector2d vector2 = new Vector2d(point2);
		vector2.sub(vector1);
		

		rotAngle = GeometryTools.getAngle(vector2.x, vector2.y);
		offsetAngle = rotAngle + (Math.PI/2);
		vector3 = new Vector2d(Math.cos(offsetAngle), Math.sin(offsetAngle));
		vector3.normalize();
		vector3.scale(20);
		center.add(vector3);
		logger.debug("rotAngle: ", rotAngle * 360 / (Math.PI * 2));
		logger.debug("offsetAngle: ", offsetAngle * 360 / (Math.PI * 2));
		arc.setArcByCenter(center.x, ss.height - center.y, point1.distance(point2)/2,(rotAngle* 360 / (Math.PI * 2)),180,Arc2D.OPEN );
		return arc;
	
	}

	public Polygon contructArrowHead(Arc2D.Double arc)
	{

		Polygon polygon = new Polygon();
		double wingOffset = (Math.PI/18); 
		Vector2d vector2 = new Vector2d(Math.cos(offsetAngle - wingOffset ), Math.sin(offsetAngle - wingOffset));
		Vector2d vector3 = new Vector2d(Math.cos(offsetAngle + wingOffset), Math.sin(offsetAngle + wingOffset ));
		Point2d point2 = new Point2d(arc.getStartPoint().getX(), arc.getStartPoint().getY());
		Point2d point3 = new Point2d(arc.getStartPoint().getX(), arc.getStartPoint().getY());
		vector2.normalize();
		vector2.scale(10);
		vector3.normalize();
		vector3.scale(10);
		//Dimension ss = r2dm.getBackgroundDimension();
		point2.add(vector2);
		point3.add(vector3);
		polygon.addPoint((int)arc.getStartPoint().getX(), (int)arc.getStartPoint().getY());
		polygon.addPoint((int)point2.x, (int)point2.y);
		polygon.addPoint((int)point3.x, (int)point3.y);
		return polygon;
	}

	
}
