/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2002-2005  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.renderer;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.tools.LoggingTool;

/**
 * A Renderer class which draws 2D representations of curly arrows as used for
 * representing electron movements in organic chemistry text books.
 * Adapted from <a href="http://www.wam.umd.edu/~petersd/Interp2_code.java">http://www.wam.umd.edu/~petersd/Interp2_code.java</a>.
 * 
 * @author      steinbeck
 * @cdk.module  render
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
	Vector2d v3;

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
		Polygon p = contructArrowHead(arc);
		graphics.fill(p);
		graphics.draw(contructArrowHead(arc));
		graphics.draw(new Ellipse2D.Double(center.x, ss.getHeight() - center.y, 10,10));
	}
	
	public Arc2D.Double contructArc(Arrow arrow)
	{
		Dimension ss = r2dm.getBackgroundDimension();
		IAtom start = arrow.getStart();
		IAtom end = arrow.getEnd();
		Arc2D.Double arc = new Arc2D.Double();
		IAtomContainer ac = start.getBuilder().newAtomContainer();
		ac.addAtom(start);
		ac.addAtom(end);
		center = GeometryTools.get2DCenter(ac,r2dm.getRenderingCoordinates());
		Point2d p1 = new Point2d((Point2d)r2dm.getRenderingCoordinate(start));
		Point2d p2 = new Point2d((Point2d)r2dm.getRenderingCoordinate(end));
		Vector2d v1 = new Vector2d(p1);
		Vector2d v2 = new Vector2d(p2);
		v2.sub(v1);
		

		rotAngle = GeometryTools.getAngle(v2.x, v2.y);
		offsetAngle = rotAngle + (Math.PI/2);
		v3 = new Vector2d(Math.cos(offsetAngle), Math.sin(offsetAngle));
		v3.normalize();
		v3.scale(20);
		center.add(v3);
		System.out.println("rotAngle: " + rotAngle * 360 / (Math.PI * 2));
		System.out.println("offsetAngle: " + offsetAngle * 360 / (Math.PI * 2));
		arc.setArcByCenter(center.x, ss.height - center.y, p1.distance(p2)/2,(rotAngle* 360 / (Math.PI * 2)),180,Arc2D.OPEN );
		return arc;
	
	}

	public Polygon contructArrowHead(Arc2D.Double arc)
	{

		Polygon polygon = new Polygon();
		double wingOffset = (Math.PI/18); 
		Vector2d v2 = new Vector2d(Math.cos(offsetAngle - wingOffset ), Math.sin(offsetAngle - wingOffset));
		Vector2d v3 = new Vector2d(Math.cos(offsetAngle + wingOffset), Math.sin(offsetAngle + wingOffset ));
		Point2d p2 = new Point2d(arc.getStartPoint().getX(), arc.getStartPoint().getY());
		Point2d p3 = new Point2d(arc.getStartPoint().getX(), arc.getStartPoint().getY());
		v2.normalize();
		v2.scale(10);
		v3.normalize();
		v3.scale(10);
		//Dimension ss = r2dm.getBackgroundDimension();
		p2.add(v2);
		p3.add(v3);
		polygon.addPoint((int)arc.getStartPoint().getX(), (int)arc.getStartPoint().getY());
		polygon.addPoint((int)p2.x, (int)p2.y);
		polygon.addPoint((int)p3.x, (int)p3.y);
		return polygon;
	}

	
}
