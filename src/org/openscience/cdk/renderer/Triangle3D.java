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
 *
 */
package org.openscience.cdk.renderer;
 
import java.awt.Color;

import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

/**
 * @cdk.module experimental
 *
 * @author  Stephan Michels <stephan@vern.chem.tu-berlin.de>
 * @cdk.created 2001-06-09
 */
public class Triangle3D 
{
  private Point3d[] pts;
	private Point3d[] i_pts;
  private Vector3f[] tri;

	private Vector3f[] normals;
	private Vector3f[] i_normals;

  private Color3f[] colors;
  private static Color3f white = new Color3f(1f,1f,1f);

	private boolean sign = true;

	private Color3f[] redcolors = new Color3f[] 
			{new Color3f(Color.red),new Color3f(Color.red),new Color3f(Color.red)};

	private Color3f[] greencolors = new Color3f[]
      {new Color3f(Color.green),new Color3f(Color.green),new Color3f(Color.green)};
  
  public Triangle3D(Point3d a, Point3d b, Point3d c, boolean sign)
  {
    pts = new Point3d[3];
    pts[0] = a;
    pts[1] = b;
    pts[2] = c;

		i_pts = new Point3d[3];
    i_pts[0] = a;
    i_pts[1] = c;
    i_pts[2] = b;
    
    tri = new Vector3f[3];
    tri[0] = new Vector3f((float)a.x, (float)a.y, (float)a.z); 
    tri[1] = new Vector3f((float)b.x, (float)b.y, (float)b.z); 
    tri[2] = new Vector3f((float)c.x, (float)c.y, (float)c.z);
    
    colors = new Color3f[3];
    colors[0] = white;
    colors[1] = white;
    colors[2] = white;

		normals = new Vector3f[3];
    normals[0] = normals[1] = normals[2] = getNormal();

		i_normals = normals;

		this.sign = sign;
  }

	public Triangle3D(Point3d a, Point3d b, Point3d c, Vector3f n1, Vector3f n2, Vector3f n3, boolean sign)
  {
    pts = new Point3d[3];
    pts[0] = a;
    pts[1] = b;
    pts[2] = c;
    
    i_pts = new Point3d[3];
    i_pts[0] = a;
    i_pts[1] = c;
    i_pts[2] = b;
    
    tri = new Vector3f[3];
    tri[0] = new Vector3f((float)a.x, (float)a.y, (float)a.z);
    tri[1] = new Vector3f((float)b.x, (float)b.y, (float)b.z);
    tri[2] = new Vector3f((float)c.x, (float)c.y, (float)c.z);
    
    colors = new Color3f[3];
    colors[0] = white;
    colors[1] = white;
    colors[2] = white;

		normals = new Vector3f[3];
		normals[0] = new Vector3f(n1);
		normals[1] = new Vector3f(n2);
		normals[2] = new Vector3f(n3);
		normals[0].negate();
    normals[1].negate();
    normals[2].negate();

		i_normals = new Vector3f[3];
    i_normals[0] = new Vector3f(n1);
    i_normals[1] = new Vector3f(n3);
    i_normals[2] = new Vector3f(n2);
		/*i_normals[0].negate();
		i_normals[1].negate();
		i_normals[2].negate();*/

    this.sign = sign;
  }
  
  public double distance(Point3d x)
  {
    return Math.min(pts[0].distance(x),Math.min(pts[1].distance(x),pts[2].distance(x)));
  }
  
  public Point3d[] getPoints()
  {
		return sign?pts:i_pts;
		//return sign?i_pts:pts;
  }
  
  public Color3f[] getColors()
  {
		return sign?redcolors:greencolors;
  }
  
	private Vector3f getNormal()
  {
		Vector3f v1 = new Vector3f(tri[1]);
		v1.sub(tri[0]);
		Vector3f v2 = new Vector3f(tri[2]);
    v2.sub(tri[0]);
    Vector3f n = new Vector3f();
		n.cross(v1, v2);
		n.normalize();
		if (sign)
			n.negate();
    return n;
  }

	public Vector3f[] getNormals()
	{
		return sign?normals:i_normals;
		//return sign?i_normals:normals;
	}
}
