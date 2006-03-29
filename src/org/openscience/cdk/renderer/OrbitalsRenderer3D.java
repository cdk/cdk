/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2001-2006  The Chemistry Development Kit (CDK) project
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
 *  */

package org.openscience.cdk.renderer;

import java.util.Vector;

import javax.media.j3d.Appearance;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Geometry;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Switch;
import javax.media.j3d.TransparencyAttributes;
import javax.media.j3d.TriangleArray;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

import org.openscience.cdk.math.Matrix;
import org.openscience.cdk.math.qm.Basis;
import org.openscience.cdk.math.qm.Orbitals;

/**
 * This class render orbitals. The algorithm used was found on the
 * following site:
 * <a href="http://www.swin.edu.au/astronomy/pbourke/modelling/polygonise/">
 * http://www.swin.edu.au/astronomy/pbourke/modelling/polygonise/</a>
 *
 * @cdk.module java3d
 *
 * @author  Stephan Michels <stephan@vern.chem.tu-berlin.de>
 * @cdk.created 2001-07-20
 *
 * @cdk.keyword orbital
 * @cdk.keyword viewer
 */
public class OrbitalsRenderer3D extends Switch
{
	private double isolevel = 0.1;
	private Orbitals orbitals;
  private int index = 0; //Index of the current orbital
	private int parts;
  private Shape3D[] shapes;
  private boolean[] calculated;
	//private Shape3D shape;
	double minx, maxx, miny, maxy, minz, maxz;

  private double[][][] values,px,py,pz,gx,gy,gz;
  
  public OrbitalsRenderer3D(Orbitals orbitals, int parts)
  {
    super();

		this.orbitals = orbitals;
		this.parts = parts;

		//Shape3D shape;
		Geometry geometry;
		Appearance appearance = createAppearance(true);

		Basis basis = orbitals.getBasis();

		minx = basis.getMinX(); maxx = basis.getMaxX();
		miny = basis.getMinY(); maxy = basis.getMaxY();
		minz = basis.getMinZ(); maxz = basis.getMaxZ();

    //System.out.println("minx="+minx+" maxx="+maxx+" miny="+miny+" maxy="+maxy+" minz="+minz+" maxz="+maxz);

		System.out.println("Calculation from the orbital for the visualisation");
		//System.out.print("["+orbitals.getCountOrbitals()+"] : ");
		shapes = new Shape3D[orbitals.getCountOrbitals()];
    calculated = new boolean[orbitals.getCountOrbitals()];

		values = new double[parts+1][parts+1][parts+1];
    px = new double[parts+1][parts+1][parts+1];
    py = new double[parts+1][parts+1][parts+1];
    pz = new double[parts+1][parts+1][parts+1];
    gx = new double[parts+1][parts+1][parts+1];
    gy = new double[parts+1][parts+1][parts+1];
    gz = new double[parts+1][parts+1][parts+1];

		for(int i=0; i<orbitals.getCountOrbitals(); i++)
		{
			//System.out.print((i+1)+"  ");
			shapes[i] = new Shape3D();
    
			/*geometry = createGeometry(orbitals, i, parts, minx, maxx, miny, maxy, minz, maxz);
    	if (geometry!=null)
      	shapes[i].setGeometry(geometry);*/
    
			shapes[i].setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
      shapes[i].setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
    	shapes[i].setAppearance(appearance);
    
			addChild(shapes[i]);

      calculated[i] = false;

			//System.gc();
		}

		//shape = new Shape3D();
    
    geometry = createGeometry(orbitals, 0, parts, minx, maxx, miny, maxy, minz, maxz);
    if (geometry!=null)
      shapes[0].setGeometry(geometry);
    calculated[0] = true;
    
    /*shapes.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
		shapes.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
    shapes.setAppearance(appearance);*/
    
    //addChild(shape);
    System.gc();
		System.out.println();

		setCapability(ALLOW_SWITCH_WRITE);
		setWhichChild(0);
  }

  /**
   * Sets the current orbital
   */
	public void setCurrentOrbital(int index)
	{
    //System.out.println("setCurrentOrbital("+index+")");
		if ((index>=0) && (index<orbitals.getCountOrbitals()) && (index!=this.index))
		{
      if (!calculated[index])
      {
  			Geometry geometry = createGeometry(orbitals, index, parts, minx, maxx, miny, maxy, minz, maxz);
	      if (geometry!=null)
  	      shapes[index].setGeometry(geometry);
        calculated[index] = true;
        System.gc();
        System.out.println();
      }
      setWhichChild(index);

      this.index = index;
		}
	}

  /**
   * Set if the orbital should be opqque
   */
	public void setOpaque(boolean flag)
	{
		Appearance appearance = createAppearance(flag);
		for(int i=0; i<orbitals.getCountOrbitals(); i++)
			//shapes[i].setAppearance(appearance);
			shapes[index].setAppearance(appearance);
	}

  // from http://www.swin.edu.au/astronomy/pbourke/modelling/polygonise/
  private int[] edgeTable=
  {
		0x0  , 0x109, 0x203, 0x30a, 0x406, 0x50f, 0x605, 0x70c,
		0x80c, 0x905, 0xa0f, 0xb06, 0xc0a, 0xd03, 0xe09, 0xf00,
		0x190, 0x99 , 0x393, 0x29a, 0x596, 0x49f, 0x795, 0x69c,
		0x99c, 0x895, 0xb9f, 0xa96, 0xd9a, 0xc93, 0xf99, 0xe90,
		0x230, 0x339, 0x33 , 0x13a, 0x636, 0x73f, 0x435, 0x53c,
		0xa3c, 0xb35, 0x83f, 0x936, 0xe3a, 0xf33, 0xc39, 0xd30,
		0x3a0, 0x2a9, 0x1a3, 0xaa , 0x7a6, 0x6af, 0x5a5, 0x4ac,
		0xbac, 0xaa5, 0x9af, 0x8a6, 0xfaa, 0xea3, 0xda9, 0xca0,
		0x460, 0x569, 0x663, 0x76a, 0x66 , 0x16f, 0x265, 0x36c,
		0xc6c, 0xd65, 0xe6f, 0xf66, 0x86a, 0x963, 0xa69, 0xb60,
		0x5f0, 0x4f9, 0x7f3, 0x6fa, 0x1f6, 0xff , 0x3f5, 0x2fc,
		0xdfc, 0xcf5, 0xfff, 0xef6, 0x9fa, 0x8f3, 0xbf9, 0xaf0,
		0x650, 0x759, 0x453, 0x55a, 0x256, 0x35f, 0x55 , 0x15c,
		0xe5c, 0xf55, 0xc5f, 0xd56, 0xa5a, 0xb53, 0x859, 0x950,
		0x7c0, 0x6c9, 0x5c3, 0x4ca, 0x3c6, 0x2cf, 0x1c5, 0xcc ,
		0xfcc, 0xec5, 0xdcf, 0xcc6, 0xbca, 0xac3, 0x9c9, 0x8c0,
		0x8c0, 0x9c9, 0xac3, 0xbca, 0xcc6, 0xdcf, 0xec5, 0xfcc,
		0xcc , 0x1c5, 0x2cf, 0x3c6, 0x4ca, 0x5c3, 0x6c9, 0x7c0,
		0x950, 0x859, 0xb53, 0xa5a, 0xd56, 0xc5f, 0xf55, 0xe5c,
		0x15c, 0x55 , 0x35f, 0x256, 0x55a, 0x453, 0x759, 0x650,
		0xaf0, 0xbf9, 0x8f3, 0x9fa, 0xef6, 0xfff, 0xcf5, 0xdfc,
		0x2fc, 0x3f5, 0xff , 0x1f6, 0x6fa, 0x7f3, 0x4f9, 0x5f0,
		0xb60, 0xa69, 0x963, 0x86a, 0xf66, 0xe6f, 0xd65, 0xc6c,
		0x36c, 0x265, 0x16f, 0x66 , 0x76a, 0x663, 0x569, 0x460,
		0xca0, 0xda9, 0xea3, 0xfaa, 0x8a6, 0x9af, 0xaa5, 0xbac,
		0x4ac, 0x5a5, 0x6af, 0x7a6, 0xaa , 0x1a3, 0x2a9, 0x3a0,
		0xd30, 0xc39, 0xf33, 0xe3a, 0x936, 0x83f, 0xb35, 0xa3c,
		0x53c, 0x435, 0x73f, 0x636, 0x13a, 0x33 , 0x339, 0x230,
		0xe90, 0xf99, 0xc93, 0xd9a, 0xa96, 0xb9f, 0x895, 0x99c,
		0x69c, 0x795, 0x49f, 0x596, 0x29a, 0x393, 0x99 , 0x190,
		0xf00, 0xe09, 0xd03, 0xc0a, 0xb06, 0xa0f, 0x905, 0x80c,
		0x70c, 0x605, 0x50f, 0x406, 0x30a, 0x203, 0x109, 0x0   
	};
		
	private int[][] triTable =
	{
		{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{0, 8, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{0, 1, 9, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{1, 8, 3, 9, 8, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{1, 2, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{0, 8, 3, 1, 2, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{9, 2, 10, 0, 2, 9, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{2, 8, 3, 2, 10, 8, 10, 9, 8, -1, -1, -1, -1, -1, -1, -1},
		{3, 11, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{0, 11, 2, 8, 11, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{1, 9, 0, 2, 3, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{1, 11, 2, 1, 9, 11, 9, 8, 11, -1, -1, -1, -1, -1, -1, -1},
		{3, 10, 1, 11, 10, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{0, 10, 1, 0, 8, 10, 8, 11, 10, -1, -1, -1, -1, -1, -1, -1},
		{3, 9, 0, 3, 11, 9, 11, 10, 9, -1, -1, -1, -1, -1, -1, -1},
		{9, 8, 10, 10, 8, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{4, 7, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{4, 3, 0, 7, 3, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{0, 1, 9, 8, 4, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{4, 1, 9, 4, 7, 1, 7, 3, 1, -1, -1, -1, -1, -1, -1, -1},
		{1, 2, 10, 8, 4, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{3, 4, 7, 3, 0, 4, 1, 2, 10, -1, -1, -1, -1, -1, -1, -1},
		{9, 2, 10, 9, 0, 2, 8, 4, 7, -1, -1, -1, -1, -1, -1, -1},
		{2, 10, 9, 2, 9, 7, 2, 7, 3, 7, 9, 4, -1, -1, -1, -1},
		{8, 4, 7, 3, 11, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{11, 4, 7, 11, 2, 4, 2, 0, 4, -1, -1, -1, -1, -1, -1, -1},
		{9, 0, 1, 8, 4, 7, 2, 3, 11, -1, -1, -1, -1, -1, -1, -1},
		{4, 7, 11, 9, 4, 11, 9, 11, 2, 9, 2, 1, -1, -1, -1, -1},
		{3, 10, 1, 3, 11, 10, 7, 8, 4, -1, -1, -1, -1, -1, -1, -1},
		{1, 11, 10, 1, 4, 11, 1, 0, 4, 7, 11, 4, -1, -1, -1, -1},
		{4, 7, 8, 9, 0, 11, 9, 11, 10, 11, 0, 3, -1, -1, -1, -1},
		{4, 7, 11, 4, 11, 9, 9, 11, 10, -1, -1, -1, -1, -1, -1, -1},
		{9, 5, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{9, 5, 4, 0, 8, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{0, 5, 4, 1, 5, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{8, 5, 4, 8, 3, 5, 3, 1, 5, -1, -1, -1, -1, -1, -1, -1},
		{1, 2, 10, 9, 5, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{3, 0, 8, 1, 2, 10, 4, 9, 5, -1, -1, -1, -1, -1, -1, -1},
		{5, 2, 10, 5, 4, 2, 4, 0, 2, -1, -1, -1, -1, -1, -1, -1},
		{2, 10, 5, 3, 2, 5, 3, 5, 4, 3, 4, 8, -1, -1, -1, -1},
		{9, 5, 4, 2, 3, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{0, 11, 2, 0, 8, 11, 4, 9, 5, -1, -1, -1, -1, -1, -1, -1},
		{0, 5, 4, 0, 1, 5, 2, 3, 11, -1, -1, -1, -1, -1, -1, -1},
		{2, 1, 5, 2, 5, 8, 2, 8, 11, 4, 8, 5, -1, -1, -1, -1},
		{10, 3, 11, 10, 1, 3, 9, 5, 4, -1, -1, -1, -1, -1, -1, -1},
		{4, 9, 5, 0, 8, 1, 8, 10, 1, 8, 11, 10, -1, -1, -1, -1},
		{5, 4, 0, 5, 0, 11, 5, 11, 10, 11, 0, 3, -1, -1, -1, -1},
		{5, 4, 8, 5, 8, 10, 10, 8, 11, -1, -1, -1, -1, -1, -1, -1},
		{9, 7, 8, 5, 7, 9, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{9, 3, 0, 9, 5, 3, 5, 7, 3, -1, -1, -1, -1, -1, -1, -1},
		{0, 7, 8, 0, 1, 7, 1, 5, 7, -1, -1, -1, -1, -1, -1, -1},
		{1, 5, 3, 3, 5, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{9, 7, 8, 9, 5, 7, 10, 1, 2, -1, -1, -1, -1, -1, -1, -1},
		{10, 1, 2, 9, 5, 0, 5, 3, 0, 5, 7, 3, -1, -1, -1, -1},
		{8, 0, 2, 8, 2, 5, 8, 5, 7, 10, 5, 2, -1, -1, -1, -1},
		{2, 10, 5, 2, 5, 3, 3, 5, 7, -1, -1, -1, -1, -1, -1, -1},
		{7, 9, 5, 7, 8, 9, 3, 11, 2, -1, -1, -1, -1, -1, -1, -1},
		{9, 5, 7, 9, 7, 2, 9, 2, 0, 2, 7, 11, -1, -1, -1, -1},
		{2, 3, 11, 0, 1, 8, 1, 7, 8, 1, 5, 7, -1, -1, -1, -1},
		{11, 2, 1, 11, 1, 7, 7, 1, 5, -1, -1, -1, -1, -1, -1, -1},
		{9, 5, 8, 8, 5, 7, 10, 1, 3, 10, 3, 11, -1, -1, -1, -1},
		{5, 7, 0, 5, 0, 9, 7, 11, 0, 1, 0, 10, 11, 10, 0, -1},
		{11, 10, 0, 11, 0, 3, 10, 5, 0, 8, 0, 7, 5, 7, 0, -1},
		{11, 10, 5, 7, 11, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{10, 6, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{0, 8, 3, 5, 10, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{9, 0, 1, 5, 10, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{1, 8, 3, 1, 9, 8, 5, 10, 6, -1, -1, -1, -1, -1, -1, -1},
		{1, 6, 5, 2, 6, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{1, 6, 5, 1, 2, 6, 3, 0, 8, -1, -1, -1, -1, -1, -1, -1},
		{9, 6, 5, 9, 0, 6, 0, 2, 6, -1, -1, -1, -1, -1, -1, -1},
		{5, 9, 8, 5, 8, 2, 5, 2, 6, 3, 2, 8, -1, -1, -1, -1},
		{2, 3, 11, 10, 6, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{11, 0, 8, 11, 2, 0, 10, 6, 5, -1, -1, -1, -1, -1, -1, -1},
		{0, 1, 9, 2, 3, 11, 5, 10, 6, -1, -1, -1, -1, -1, -1, -1},
		{5, 10, 6, 1, 9, 2, 9, 11, 2, 9, 8, 11, -1, -1, -1, -1},
		{6, 3, 11, 6, 5, 3, 5, 1, 3, -1, -1, -1, -1, -1, -1, -1},
		{0, 8, 11, 0, 11, 5, 0, 5, 1, 5, 11, 6, -1, -1, -1, -1},
		{3, 11, 6, 0, 3, 6, 0, 6, 5, 0, 5, 9, -1, -1, -1, -1},
		{6, 5, 9, 6, 9, 11, 11, 9, 8, -1, -1, -1, -1, -1, -1, -1},
		{5, 10, 6, 4, 7, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{4, 3, 0, 4, 7, 3, 6, 5, 10, -1, -1, -1, -1, -1, -1, -1},
		{1, 9, 0, 5, 10, 6, 8, 4, 7, -1, -1, -1, -1, -1, -1, -1},
		{10, 6, 5, 1, 9, 7, 1, 7, 3, 7, 9, 4, -1, -1, -1, -1},
		{6, 1, 2, 6, 5, 1, 4, 7, 8, -1, -1, -1, -1, -1, -1, -1},
		{1, 2, 5, 5, 2, 6, 3, 0, 4, 3, 4, 7, -1, -1, -1, -1},
		{8, 4, 7, 9, 0, 5, 0, 6, 5, 0, 2, 6, -1, -1, -1, -1},
		{7, 3, 9, 7, 9, 4, 3, 2, 9, 5, 9, 6, 2, 6, 9, -1},
		{3, 11, 2, 7, 8, 4, 10, 6, 5, -1, -1, -1, -1, -1, -1, -1},
		{5, 10, 6, 4, 7, 2, 4, 2, 0, 2, 7, 11, -1, -1, -1, -1},
		{0, 1, 9, 4, 7, 8, 2, 3, 11, 5, 10, 6, -1, -1, -1, -1},
		{9, 2, 1, 9, 11, 2, 9, 4, 11, 7, 11, 4, 5, 10, 6, -1},
		{8, 4, 7, 3, 11, 5, 3, 5, 1, 5, 11, 6, -1, -1, -1, -1},
		{5, 1, 11, 5, 11, 6, 1, 0, 11, 7, 11, 4, 0, 4, 11, -1},
		{0, 5, 9, 0, 6, 5, 0, 3, 6, 11, 6, 3, 8, 4, 7, -1},
		{6, 5, 9, 6, 9, 11, 4, 7, 9, 7, 11, 9, -1, -1, -1, -1},
		{10, 4, 9, 6, 4, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{4, 10, 6, 4, 9, 10, 0, 8, 3, -1, -1, -1, -1, -1, -1, -1},
		{10, 0, 1, 10, 6, 0, 6, 4, 0, -1, -1, -1, -1, -1, -1, -1},
		{8, 3, 1, 8, 1, 6, 8, 6, 4, 6, 1, 10, -1, -1, -1, -1},
		{1, 4, 9, 1, 2, 4, 2, 6, 4, -1, -1, -1, -1, -1, -1, -1},
		{3, 0, 8, 1, 2, 9, 2, 4, 9, 2, 6, 4, -1, -1, -1, -1},
		{0, 2, 4, 4, 2, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{8, 3, 2, 8, 2, 4, 4, 2, 6, -1, -1, -1, -1, -1, -1, -1},
		{10, 4, 9, 10, 6, 4, 11, 2, 3, -1, -1, -1, -1, -1, -1, -1},
		{0, 8, 2, 2, 8, 11, 4, 9, 10, 4, 10, 6, -1, -1, -1, -1},
		{3, 11, 2, 0, 1, 6, 0, 6, 4, 6, 1, 10, -1, -1, -1, -1},
		{6, 4, 1, 6, 1, 10, 4, 8, 1, 2, 1, 11, 8, 11, 1, -1},
		{9, 6, 4, 9, 3, 6, 9, 1, 3, 11, 6, 3, -1, -1, -1, -1},
		{8, 11, 1, 8, 1, 0, 11, 6, 1, 9, 1, 4, 6, 4, 1, -1},
		{3, 11, 6, 3, 6, 0, 0, 6, 4, -1, -1, -1, -1, -1, -1, -1},
		{6, 4, 8, 11, 6, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{7, 10, 6, 7, 8, 10, 8, 9, 10, -1, -1, -1, -1, -1, -1, -1},
		{0, 7, 3, 0, 10, 7, 0, 9, 10, 6, 7, 10, -1, -1, -1, -1},
		{10, 6, 7, 1, 10, 7, 1, 7, 8, 1, 8, 0, -1, -1, -1, -1},
		{10, 6, 7, 10, 7, 1, 1, 7, 3, -1, -1, -1, -1, -1, -1, -1},
		{1, 2, 6, 1, 6, 8, 1, 8, 9, 8, 6, 7, -1, -1, -1, -1},
		{2, 6, 9, 2, 9, 1, 6, 7, 9, 0, 9, 3, 7, 3, 9, -1},
		{7, 8, 0, 7, 0, 6, 6, 0, 2, -1, -1, -1, -1, -1, -1, -1},
		{7, 3, 2, 6, 7, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{2, 3, 11, 10, 6, 8, 10, 8, 9, 8, 6, 7, -1, -1, -1, -1},
		{2, 0, 7, 2, 7, 11, 0, 9, 7, 6, 7, 10, 9, 10, 7, -1},
		{1, 8, 0, 1, 7, 8, 1, 10, 7, 6, 7, 10, 2, 3, 11, -1},
		{11, 2, 1, 11, 1, 7, 10, 6, 1, 6, 7, 1, -1, -1, -1, -1},
		{8, 9, 6, 8, 6, 7, 9, 1, 6, 11, 6, 3, 1, 3, 6, -1},
		{0, 9, 1, 11, 6, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{7, 8, 0, 7, 0, 6, 3, 11, 0, 11, 6, 0, -1, -1, -1, -1},
		{7, 11, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{7, 6, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{3, 0, 8, 11, 7, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{0, 1, 9, 11, 7, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{8, 1, 9, 8, 3, 1, 11, 7, 6, -1, -1, -1, -1, -1, -1, -1},
		{10, 1, 2, 6, 11, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{1, 2, 10, 3, 0, 8, 6, 11, 7, -1, -1, -1, -1, -1, -1, -1},
		{2, 9, 0, 2, 10, 9, 6, 11, 7, -1, -1, -1, -1, -1, -1, -1},
		{6, 11, 7, 2, 10, 3, 10, 8, 3, 10, 9, 8, -1, -1, -1, -1},
		{7, 2, 3, 6, 2, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{7, 0, 8, 7, 6, 0, 6, 2, 0, -1, -1, -1, -1, -1, -1, -1},
		{2, 7, 6, 2, 3, 7, 0, 1, 9, -1, -1, -1, -1, -1, -1, -1},
		{1, 6, 2, 1, 8, 6, 1, 9, 8, 8, 7, 6, -1, -1, -1, -1},
		{10, 7, 6, 10, 1, 7, 1, 3, 7, -1, -1, -1, -1, -1, -1, -1},
		{10, 7, 6, 1, 7, 10, 1, 8, 7, 1, 0, 8, -1, -1, -1, -1},
		{0, 3, 7, 0, 7, 10, 0, 10, 9, 6, 10, 7, -1, -1, -1, -1},
		{7, 6, 10, 7, 10, 8, 8, 10, 9, -1, -1, -1, -1, -1, -1, -1},
		{6, 8, 4, 11, 8, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{3, 6, 11, 3, 0, 6, 0, 4, 6, -1, -1, -1, -1, -1, -1, -1},
		{8, 6, 11, 8, 4, 6, 9, 0, 1, -1, -1, -1, -1, -1, -1, -1},
		{9, 4, 6, 9, 6, 3, 9, 3, 1, 11, 3, 6, -1, -1, -1, -1},
		{6, 8, 4, 6, 11, 8, 2, 10, 1, -1, -1, -1, -1, -1, -1, -1},
		{1, 2, 10, 3, 0, 11, 0, 6, 11, 0, 4, 6, -1, -1, -1, -1},
		{4, 11, 8, 4, 6, 11, 0, 2, 9, 2, 10, 9, -1, -1, -1, -1},
		{10, 9, 3, 10, 3, 2, 9, 4, 3, 11, 3, 6, 4, 6, 3, -1},
		{8, 2, 3, 8, 4, 2, 4, 6, 2, -1, -1, -1, -1, -1, -1, -1},
		{0, 4, 2, 4, 6, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{1, 9, 0, 2, 3, 4, 2, 4, 6, 4, 3, 8, -1, -1, -1, -1},
		{1, 9, 4, 1, 4, 2, 2, 4, 6, -1, -1, -1, -1, -1, -1, -1},
		{8, 1, 3, 8, 6, 1, 8, 4, 6, 6, 10, 1, -1, -1, -1, -1},
		{10, 1, 0, 10, 0, 6, 6, 0, 4, -1, -1, -1, -1, -1, -1, -1},
		{4, 6, 3, 4, 3, 8, 6, 10, 3, 0, 3, 9, 10, 9, 3, -1},
		{10, 9, 4, 6, 10, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{4, 9, 5, 7, 6, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{0, 8, 3, 4, 9, 5, 11, 7, 6, -1, -1, -1, -1, -1, -1, -1},
		{5, 0, 1, 5, 4, 0, 7, 6, 11, -1, -1, -1, -1, -1, -1, -1},
		{11, 7, 6, 8, 3, 4, 3, 5, 4, 3, 1, 5, -1, -1, -1, -1},
		{9, 5, 4, 10, 1, 2, 7, 6, 11, -1, -1, -1, -1, -1, -1, -1},
		{6, 11, 7, 1, 2, 10, 0, 8, 3, 4, 9, 5, -1, -1, -1, -1},
		{7, 6, 11, 5, 4, 10, 4, 2, 10, 4, 0, 2, -1, -1, -1, -1},
		{3, 4, 8, 3, 5, 4, 3, 2, 5, 10, 5, 2, 11, 7, 6, -1},
		{7, 2, 3, 7, 6, 2, 5, 4, 9, -1, -1, -1, -1, -1, -1, -1},
		{9, 5, 4, 0, 8, 6, 0, 6, 2, 6, 8, 7, -1, -1, -1, -1},
		{3, 6, 2, 3, 7, 6, 1, 5, 0, 5, 4, 0, -1, -1, -1, -1},
		{6, 2, 8, 6, 8, 7, 2, 1, 8, 4, 8, 5, 1, 5, 8, -1},
		{9, 5, 4, 10, 1, 6, 1, 7, 6, 1, 3, 7, -1, -1, -1, -1},
		{1, 6, 10, 1, 7, 6, 1, 0, 7, 8, 7, 0, 9, 5, 4, -1},
		{4, 0, 10, 4, 10, 5, 0, 3, 10, 6, 10, 7, 3, 7, 10, -1},
		{7, 6, 10, 7, 10, 8, 5, 4, 10, 4, 8, 10, -1, -1, -1, -1},
		{6, 9, 5, 6, 11, 9, 11, 8, 9, -1, -1, -1, -1, -1, -1, -1},
		{3, 6, 11, 0, 6, 3, 0, 5, 6, 0, 9, 5, -1, -1, -1, -1},
		{0, 11, 8, 0, 5, 11, 0, 1, 5, 5, 6, 11, -1, -1, -1, -1},
		{6, 11, 3, 6, 3, 5, 5, 3, 1, -1, -1, -1, -1, -1, -1, -1},
		{1, 2, 10, 9, 5, 11, 9, 11, 8, 11, 5, 6, -1, -1, -1, -1},
		{0, 11, 3, 0, 6, 11, 0, 9, 6, 5, 6, 9, 1, 2, 10, -1},
		{11, 8, 5, 11, 5, 6, 8, 0, 5, 10, 5, 2, 0, 2, 5, -1},
		{6, 11, 3, 6, 3, 5, 2, 10, 3, 10, 5, 3, -1, -1, -1, -1},
		{5, 8, 9, 5, 2, 8, 5, 6, 2, 3, 8, 2, -1, -1, -1, -1},
		{9, 5, 6, 9, 6, 0, 0, 6, 2, -1, -1, -1, -1, -1, -1, -1},
		{1, 5, 8, 1, 8, 0, 5, 6, 8, 3, 8, 2, 6, 2, 8, -1},
		{1, 5, 6, 2, 1, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{1, 3, 6, 1, 6, 10, 3, 8, 6, 5, 6, 9, 8, 9, 6, -1},
		{10, 1, 0, 10, 0, 6, 9, 5, 0, 5, 6, 0, -1, -1, -1, -1},
		{0, 3, 8, 5, 6, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{10, 5, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{11, 5, 10, 7, 5, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{11, 5, 10, 11, 7, 5, 8, 3, 0, -1, -1, -1, -1, -1, -1, -1},
		{5, 11, 7, 5, 10, 11, 1, 9, 0, -1, -1, -1, -1, -1, -1, -1},
		{10, 7, 5, 10, 11, 7, 9, 8, 1, 8, 3, 1, -1, -1, -1, -1},
		{11, 1, 2, 11, 7, 1, 7, 5, 1, -1, -1, -1, -1, -1, -1, -1},
		{0, 8, 3, 1, 2, 7, 1, 7, 5, 7, 2, 11, -1, -1, -1, -1},
		{9, 7, 5, 9, 2, 7, 9, 0, 2, 2, 11, 7, -1, -1, -1, -1},
		{7, 5, 2, 7, 2, 11, 5, 9, 2, 3, 2, 8, 9, 8, 2, -1},
		{2, 5, 10, 2, 3, 5, 3, 7, 5, -1, -1, -1, -1, -1, -1, -1},
		{8, 2, 0, 8, 5, 2, 8, 7, 5, 10, 2, 5, -1, -1, -1, -1},
		{9, 0, 1, 5, 10, 3, 5, 3, 7, 3, 10, 2, -1, -1, -1, -1},
		{9, 8, 2, 9, 2, 1, 8, 7, 2, 10, 2, 5, 7, 5, 2, -1},
		{1, 3, 5, 3, 7, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{0, 8, 7, 0, 7, 1, 1, 7, 5, -1, -1, -1, -1, -1, -1, -1},
		{9, 0, 3, 9, 3, 5, 5, 3, 7, -1, -1, -1, -1, -1, -1, -1},
		{9, 8, 7, 5, 9, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{5, 8, 4, 5, 10, 8, 10, 11, 8, -1, -1, -1, -1, -1, -1, -1},
		{5, 0, 4, 5, 11, 0, 5, 10, 11, 11, 3, 0, -1, -1, -1, -1},
		{0, 1, 9, 8, 4, 10, 8, 10, 11, 10, 4, 5, -1, -1, -1, -1},
		{10, 11, 4, 10, 4, 5, 11, 3, 4, 9, 4, 1, 3, 1, 4, -1},
		{2, 5, 1, 2, 8, 5, 2, 11, 8, 4, 5, 8, -1, -1, -1, -1},
		{0, 4, 11, 0, 11, 3, 4, 5, 11, 2, 11, 1, 5, 1, 11, -1},
		{0, 2, 5, 0, 5, 9, 2, 11, 5, 4, 5, 8, 11, 8, 5, -1},
		{9, 4, 5, 2, 11, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{2, 5, 10, 3, 5, 2, 3, 4, 5, 3, 8, 4, -1, -1, -1, -1},
		{5, 10, 2, 5, 2, 4, 4, 2, 0, -1, -1, -1, -1, -1, -1, -1},
		{3, 10, 2, 3, 5, 10, 3, 8, 5, 4, 5, 8, 0, 1, 9, -1},
		{5, 10, 2, 5, 2, 4, 1, 9, 2, 9, 4, 2, -1, -1, -1, -1},
		{8, 4, 5, 8, 5, 3, 3, 5, 1, -1, -1, -1, -1, -1, -1, -1},
		{0, 4, 5, 1, 0, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{8, 4, 5, 8, 5, 3, 9, 0, 5, 0, 3, 5, -1, -1, -1, -1},
		{9, 4, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{4, 11, 7, 4, 9, 11, 9, 10, 11, -1, -1, -1, -1, -1, -1, -1},
		{0, 8, 3, 4, 9, 7, 9, 11, 7, 9, 10, 11, -1, -1, -1, -1},
		{1, 10, 11, 1, 11, 4, 1, 4, 0, 7, 4, 11, -1, -1, -1, -1},
		{3, 1, 4, 3, 4, 8, 1, 10, 4, 7, 4, 11, 10, 11, 4, -1},
		{4, 11, 7, 9, 11, 4, 9, 2, 11, 9, 1, 2, -1, -1, -1, -1},
		{9, 7, 4, 9, 11, 7, 9, 1, 11, 2, 11, 1, 0, 8, 3, -1},
		{11, 7, 4, 11, 4, 2, 2, 4, 0, -1, -1, -1, -1, -1, -1, -1},
		{11, 7, 4, 11, 4, 2, 8, 3, 4, 3, 2, 4, -1, -1, -1, -1},
		{2, 9, 10, 2, 7, 9, 2, 3, 7, 7, 4, 9, -1, -1, -1, -1},
		{9, 10, 7, 9, 7, 4, 10, 2, 7, 8, 7, 0, 2, 0, 7, -1},
		{3, 7, 10, 3, 10, 2, 7, 4, 10, 1, 10, 0, 4, 0, 10, -1},
		{1, 10, 2, 8, 7, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{4, 9, 1, 4, 1, 7, 7, 1, 3, -1, -1, -1, -1, -1, -1, -1},
		{4, 9, 1, 4, 1, 7, 0, 8, 1, 8, 7, 1, -1, -1, -1, -1},
		{4, 0, 3, 7, 4, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{4, 8, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{9, 10, 8, 10, 11, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{3, 0, 9, 3, 9, 11, 11, 9, 10, -1, -1, -1, -1, -1, -1, -1},
		{0, 1, 10, 0, 10, 8, 8, 10, 11, -1, -1, -1, -1, -1, -1, -1},
		{3, 1, 10, 11, 3, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{1, 2, 11, 1, 11, 9, 9, 11, 8, -1, -1, -1, -1, -1, -1, -1},
		{3, 0, 9, 3, 9, 11, 1, 2, 9, 2, 11, 9, -1, -1, -1, -1},
		{0, 2, 11, 8, 0, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{3, 2, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{2, 3, 8, 2, 8, 10, 10, 8, 9, -1, -1, -1, -1, -1, -1, -1},
		{9, 10, 2, 0, 9, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{2, 3, 8, 2, 8, 10, 0, 1, 8, 1, 10, 8, -1, -1, -1, -1},
		{1, 10, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{1, 3, 8, 9, 1, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{0, 9, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{0, 3, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}
	};

	private class GridCell
	{
		public double[] values = new double[8];

		public double[] px = new double[8];
		public double[] py = new double[8];
		public double[] pz = new double[8];

		public double[] gx = new double[8];
    public double[] gy = new double[8];
    public double[] gz = new double[8];

		public Point3d[] vertlist = new Point3d[12];
    public Vector3f[] gradlist = new Vector3f[12];
	}

	/*
			Given a grid cell and an isolevel, calculate the triangular
			facets required to represent the isosurface through the cell.
			Return the number of triangular facets, the array "triangles"
			will be loaded up with the vertices at most 5 triangular facets.
			0 will be returned if the grid cell is either totally above
			of totally below the isolevel.
	*/
	private void polygonise(GridCell grid, double isolevel, Vector triangles)
	{
		int i;
		int cubeindex;
		
		/*
		  Determine the index into the edge table which
		  tells us which vertices are inside of the surface
		*/
		cubeindex = 0;
		if (grid.values[0] < isolevel) cubeindex |= 1;
		if (grid.values[1] < isolevel) cubeindex |= 2;
		if (grid.values[2] < isolevel) cubeindex |= 4;
		if (grid.values[3] < isolevel) cubeindex |= 8;
		if (grid.values[4] < isolevel) cubeindex |= 16;
		if (grid.values[5] < isolevel) cubeindex |= 32;
		if (grid.values[6] < isolevel) cubeindex |= 64;
		if (grid.values[7] < isolevel) cubeindex |= 128;
		
		/* Cube is entirely in/out of the surface */
		if (edgeTable[cubeindex] == 0)
			return;
	
		/* Find the vertices where the surface intersects the cube */
		if ((edgeTable[cubeindex] & 1)>0)
			vertexInterp(grid, isolevel, 0, 1, 0);
	  if ((edgeTable[cubeindex] & 2)>0)
			vertexInterp(grid, isolevel, 1, 2, 1);
	  if ((edgeTable[cubeindex] & 4)>0)
			vertexInterp(grid, isolevel, 2, 3, 2);
	  if ((edgeTable[cubeindex] & 8)>0)
			vertexInterp(grid, isolevel, 3, 0, 3);
	  if ((edgeTable[cubeindex] & 16)>0)
			vertexInterp(grid, isolevel, 4, 5, 4);
	  if ((edgeTable[cubeindex] & 32)>0)
			vertexInterp(grid, isolevel, 5, 6, 5);
	  if ((edgeTable[cubeindex] & 64)>0)
			vertexInterp(grid, isolevel, 6, 7, 6);
	  if ((edgeTable[cubeindex] & 128)>0)
			vertexInterp(grid, isolevel, 7, 4, 7);
	  if ((edgeTable[cubeindex] & 256)>0)
			vertexInterp(grid, isolevel, 0, 4, 8);
	  if ((edgeTable[cubeindex] & 512)>0)
			vertexInterp(grid, isolevel, 1, 5, 9);
	  if ((edgeTable[cubeindex] & 1024)>0)
			vertexInterp(grid, isolevel, 2, 6, 10);
	  if ((edgeTable[cubeindex] & 2048)>0)
			vertexInterp(grid, isolevel, 3, 7, 11);
	
		/* Create the triangle */
	  for (i=0;triTable[cubeindex][i]!=-1;i+=3) 
	  {
	    triangles.addElement(new Triangle3D(grid.vertlist[triTable[cubeindex][i  ]],
																					grid.vertlist[triTable[cubeindex][i+1]],
																					grid.vertlist[triTable[cubeindex][i+2]], 
																					grid.gradlist[triTable[cubeindex][i  ]],
                                          grid.gradlist[triTable[cubeindex][i+1]],
                                          grid.gradlist[triTable[cubeindex][i+2]],
																					isolevel>=0d));
		}
	}
	
	/*
	   Linearly interpolate the position where an isosurface cuts
	   an edge between two vertices, each with their own scalar value
	*/
	private void vertexInterp(GridCell grid, double isolevel, int p1, int p2, int p)
  {
		if ((Math.abs(isolevel-grid.values[p1]) < 1E-10) || (Math.abs(grid.values[p1]-grid.values[p2]) < 1E-10))
		{
			grid.vertlist[p] = new Point3d(grid.px[p1], grid.py[p1], grid.pz[p1]);
			grid.gradlist[p] = new Vector3f((float)grid.gx[p1],(float)grid.gy[p1],(float)grid.gz[p1]);
    	return;
		}
  	if (Math.abs(isolevel-grid.values[p2]) < 1E-10)
		{
			grid.vertlist[p] = new Point3d(grid.px[p1], grid.py[p1], grid.pz[p1]);
      grid.gradlist[p] = new Vector3f((float)grid.gx[p1],(float)grid.gy[p1],(float)grid.gz[p1]);
    	return;
		}
      
		double mu = (isolevel - grid.values[p1]) / (grid.values[p2] - grid.values[p1]);
    grid.vertlist[p] = new Point3d(grid.px[p1] + mu * (grid.px[p2] - grid.px[p1]),
                     				       grid.py[p1] + mu * (grid.py[p2] - grid.py[p1]),
                            			 grid.pz[p1] + mu * (grid.pz[p2] - grid.pz[p1]));

		grid.gradlist[p] = new Vector3f((float)(grid.gx[p1] + mu * (grid.gx[p2] - grid.gx[p1])),
                                    (float)(grid.gy[p1] + mu * (grid.gy[p2] - grid.gy[p1])),
                                   	(float)(grid.gz[p1] + mu * (grid.gz[p2] - grid.gz[p1])));
  }

	private Geometry createGeometry(Orbitals orbitals, int index, int parts, 
							double x1, double x2, double y1, double y2, double z1, double z2)
  {
    double wx, wy, wz, tx, ty, tz;

    double dx = (x2-x1);
    double dy = (y2-y1);
    double dz = (z2-z1);
    
    long start = System.currentTimeMillis();
		long mem = Runtime.getRuntime().freeMemory();

		double value, length;
		double h = 1E-4;
   
		Matrix m = new Matrix(3,(parts+1)*(parts+1)*(parts+1));
		// Calculation from the points
		int j = 0;
		for(int x=0; x<=parts; x++)
      for(int y=0; y<=parts; y++)
        for(int z=0; z<=parts; z++)
        {
					tx = ((double)x/(double)parts);
          ty = ((double)y/(double)parts);
          tz = ((double)z/(double)parts);
	          
          px[x][y][z] = m.matrix[0][j] = tx*dx+x1;
          py[x][y][z] = m.matrix[1][j] = ty*dy+y1;
          pz[x][y][z] = m.matrix[2][j] = tz*dz+z1;

				}

		start = System.currentTimeMillis()-start;
    System.out.println("0.Zeit:"+start+" ms");
    start = System.currentTimeMillis();
		System.out.println("Memory = "+(mem-Runtime.getRuntime().freeMemory()));
		mem = Runtime.getRuntime().freeMemory();

		//mmthc.math.Vector v = orbitals.getValues(index, m);

		start = System.currentTimeMillis()-start;
    System.out.println("1.Zeit:"+start+" ms");
    start = System.currentTimeMillis();
		System.out.println("Memory = "+(mem-Runtime.getRuntime().freeMemory()));
    mem = Runtime.getRuntime().freeMemory();

		j = 0;
		for(int x=0; x<=parts; x++)
      for(int y=0; y<=parts; y++)
        for(int z=0; z<=parts; z++)
        {
					//values[x][y][z] = v.vector[j];
					values[x][y][z] = orbitals.getValue(index, px[x][y][z], py[x][y][z], pz[x][y][z]);
					if (Double.isNaN(values[x][y][z]))
            values[x][y][z] = 0d;
		
					j++;
				}

		start = System.currentTimeMillis()-start;
    System.out.println("2.Zeit:"+start+" ms");
    start = System.currentTimeMillis();
		System.out.println("Memory = "+(mem-Runtime.getRuntime().freeMemory()));
    mem = Runtime.getRuntime().freeMemory();

		dx /= parts; dy /= parts; dz /= parts;

		// Calculation from the gradients for the normals
		for(int x=0; x<=parts; x++)
      for(int y=0; y<=parts; y++)
        for(int z=0; z<=parts; z++)
        {
					if (x<parts)
					{
						if (x>0)
							gx[x][y][z] = (values[x+1][y][z]-values[x-1][y][z])/(2*dx);
						else
							gx[x][y][z] = (values[x+1][y][z]-values[x][y][z])/dx;
					}
					else
						gx[x][y][z] = (values[x][y][z]-values[x-1][y][z])/dx;

					if (y<parts)
					{
						if (y>0)
              gy[x][y][z] = (values[x][y+1][z]-values[x][y-1][z])/(2*dy);
            else
					    gy[x][y][z] = (values[x][y+1][z]-values[x][y][z])/dy;
					}
					else
						gy[x][y][z] = (values[x][y][z]-values[x][y-1][z])/dy;

					if (z<parts)
					{
						if (z>0)
              gz[x][y][z] = (values[x][y][z+1]-values[x][y][z-1])/(2*dz);
            else
					    gz[x][y][z] = (values[x][y][z+1]-values[x][y][z])/dz;
					}
					else
						gz[x][y][z] = (values[x][y][z]-values[x][y][z-1])/dz;

					length = Math.sqrt(gx[x][y][z]*gx[x][y][z]+gy[x][y][z]*gy[x][y][z]+gz[x][y][z]*gz[x][y][z]);
					gx[x][y][z] /= length; gy[x][y][z] /= length; gz[x][y][z] /= length;
        }
   
    start = System.currentTimeMillis()-start;
    System.out.println("3.Zeit:"+start+" ms");
    start = System.currentTimeMillis();
		System.out.println("Memory = "+(mem-Runtime.getRuntime().freeMemory()));
    mem = Runtime.getRuntime().freeMemory();
    
		// Polygonise
    Vector triangles = new Vector();
		GridCell gridCell = new GridCell();
   	for(int x=0; x<parts; x++)
      for(int y=0; y<parts; y++)
        for(int z=0; z<parts; z++)
        {
					gridCell.px[0] = px[x  ][y  ][z  ];
          gridCell.px[1] = px[x+1][y  ][z  ];
          gridCell.px[2] = px[x+1][y  ][z+1];
          gridCell.px[3] = px[x  ][y  ][z+1];
          gridCell.px[4] = px[x  ][y+1][z  ];
          gridCell.px[5] = px[x+1][y+1][z  ];
          gridCell.px[6] = px[x+1][y+1][z+1];
          gridCell.px[7] = px[x  ][y+1][z+1];

					gridCell.py[0] = py[x  ][y  ][z  ];
          gridCell.py[1] = py[x+1][y  ][z  ];
          gridCell.py[2] = py[x+1][y  ][z+1];
          gridCell.py[3] = py[x  ][y  ][z+1];
          gridCell.py[4] = py[x  ][y+1][z  ];
          gridCell.py[5] = py[x+1][y+1][z  ];
          gridCell.py[6] = py[x+1][y+1][z+1];
          gridCell.py[7] = py[x  ][y+1][z+1];

					gridCell.pz[0] = pz[x  ][y  ][z  ];
          gridCell.pz[1] = pz[x+1][y  ][z  ];
          gridCell.pz[2] = pz[x+1][y  ][z+1];
          gridCell.pz[3] = pz[x  ][y  ][z+1];
          gridCell.pz[4] = pz[x  ][y+1][z  ];
          gridCell.pz[5] = pz[x+1][y+1][z  ];
          gridCell.pz[6] = pz[x+1][y+1][z+1];
          gridCell.pz[7] = pz[x  ][y+1][z+1];
	          
          gridCell.values[0] = values[x  ][y  ][z  ];
          gridCell.values[1] = values[x+1][y  ][z  ];
          gridCell.values[2] = values[x+1][y  ][z+1];
          gridCell.values[3] = values[x  ][y  ][z+1];
          gridCell.values[4] = values[x  ][y+1][z  ];
          gridCell.values[5] = values[x+1][y+1][z  ];
          gridCell.values[6] = values[x+1][y+1][z+1];
          gridCell.values[7] = values[x  ][y+1][z+1];

					gridCell.gx[0] = gx[x  ][y  ][z  ];
          gridCell.gx[1] = gx[x+1][y  ][z  ];
          gridCell.gx[2] = gx[x+1][y  ][z+1];
          gridCell.gx[3] = gx[x  ][y  ][z+1];
          gridCell.gx[4] = gx[x  ][y+1][z  ];
          gridCell.gx[5] = gx[x+1][y+1][z  ];
          gridCell.gx[6] = gx[x+1][y+1][z+1];
          gridCell.gx[7] = gx[x  ][y+1][z+1];

          gridCell.gy[0] = gy[x  ][y  ][z  ];
          gridCell.gy[1] = gy[x+1][y  ][z  ];
          gridCell.gy[2] = gy[x+1][y  ][z+1];
          gridCell.gy[3] = gy[x  ][y  ][z+1];
          gridCell.gy[4] = gy[x  ][y+1][z  ];
          gridCell.gy[5] = gy[x+1][y+1][z  ];
          gridCell.gy[6] = gy[x+1][y+1][z+1];
          gridCell.gy[7] = gy[x  ][y+1][z+1];
          
          gridCell.gz[0] = gz[x  ][y  ][z  ];
          gridCell.gz[1] = gz[x+1][y  ][z  ];
          gridCell.gz[2] = gz[x+1][y  ][z+1];
          gridCell.gz[3] = gz[x  ][y  ][z+1];
          gridCell.gz[4] = gz[x  ][y+1][z  ];
          gridCell.gz[5] = gz[x+1][y+1][z  ];
          gridCell.gz[6] = gz[x+1][y+1][z+1];
          gridCell.gz[7] = gz[x  ][y+1][z+1];
	          
          polygonise(gridCell, isolevel, triangles);
					polygonise(gridCell, -isolevel, triangles);
        }
        
    start = System.currentTimeMillis()-start;
    System.out.println("4.Zeit:"+start+" ms");
    start = System.currentTimeMillis();
		System.out.println("Memory = "+(mem-Runtime.getRuntime().freeMemory()));
    mem = Runtime.getRuntime().freeMemory();
    
    System.out.println("Count of triangles:"+triangles.size());
    

		// Construction of the geometry
    TriangleArray trisArray;
	  if (triangles.size()==0) return null;
	  trisArray  = new TriangleArray(triangles.size()*3,TriangleArray.COORDINATES | 
																											TriangleArray.NORMALS | 
																											TriangleArray.COLOR_3);

	  Triangle3D t;
	  for(int i=0; i<triangles.size(); i++)
	  {
	    t = (Triangle3D) triangles.elementAt(i);
	    trisArray.setCoordinates(i*3, t.getPoints());
	    trisArray.setColors(i*3, t.getColors());
			trisArray.setNormals(i*3, t.getNormals());
	  }

		start = System.currentTimeMillis()-start;
    System.out.println("5.Zeit:"+start+" ms");
		System.out.println("Memory = "+(mem-Runtime.getRuntime().freeMemory()));

		System.out.println();
     
    return trisArray;
  }
  
  private Appearance createAppearance(boolean opaque)
  {
    Appearance appearance = new Appearance();
    PolygonAttributes polyAttrib = new PolygonAttributes();
    //polyAttrib.setPolygonMode(PolygonAttributes.POLYGON_LINE);
    //polyAttrib.setPolygonMode(PolygonAttributes.POLYGON_POINT);

		polyAttrib.setCullFace(PolygonAttributes.CULL_FRONT);
    //polyAttrib.setCullFace(PolygonAttributes.CULL_NONE);
    //polyAttrib.setBackFaceNormalFlip(true);

    appearance.setPolygonAttributes(polyAttrib);
		
		javax.vecmath.Color3f objColor = new javax.vecmath.Color3f(1f, 1f, 1f);
    javax.vecmath.Color3f black = new javax.vecmath.Color3f(0, 0, 0);
    javax.media.j3d.Material material = new javax.media.j3d.Material(objColor, black, objColor, 
				new javax.vecmath.Color3f(1f, 1f, 1f), 10f); 
		appearance.setMaterial(material);

		ColoringAttributes colorattributes = new ColoringAttributes();
		colorattributes.setShadeModel(ColoringAttributes.NICEST);
		appearance.setColoringAttributes(colorattributes);

		if (!opaque)
	    appearance.setTransparencyAttributes(new TransparencyAttributes(TransparencyAttributes.BLENDED, 0.5f));

    return appearance;
  }
}
