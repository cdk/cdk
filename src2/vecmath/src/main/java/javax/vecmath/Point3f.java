/*
 * Copyright 1997-2008 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 *
 */

package javax.vecmath;


/**
 * A 3 element point that is represented by single precision floating point
 * x,y,z coordinates.
 *
 */
public class Point3f extends Tuple3f implements java.io.Serializable {


    // Compatible with 1.1
    static final long serialVersionUID = -8689337816398030143L;

    /**
     * Constructs and initializes a Point3f from the specified xyz coordinates.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     */
    public Point3f(float x, float y, float z)
    {
        super(x,y,z);
    }


    /**
     * Constructs and initializes a Point3f from the array of length 3.
     * @param p the array of length 3 containing xyz in order
     */
    public Point3f(float[] p)
    {
       super(p);
    }


    /**
     * Constructs and initializes a Point3f from the specified Point3f.
     * @param p1 the Point3f containing the initialization x y z data
     */
    public Point3f(Point3f p1)
    {
       super(p1);
    }


    /**
     * Constructs and initializes a Point3f from the specified Point3d.
     * @param p1 the Point3d containing the initialization x y z data
     */
    public Point3f(Point3d p1)
    {
       super(p1);
    }


    /**
     * Constructs and initializes a Point3f from the specified Tuple3f.
     * @param t1 the Tuple3f containing the initialization x y z data
     */
    public Point3f(Tuple3f t1)
    {
       super(t1);
    }


    /**
     * Constructs and initializes a Point3f from the specified Tuple3d.
     * @param t1 the Tuple3d containing the initialization x y z data
     */
    public Point3f(Tuple3d t1)
    {
       super(t1);
    }


    /**
     * Constructs and initializes a Point3f to (0,0,0).
     */
    public Point3f()
    {
        super();
    }


 /**
   * Computes the square of the distance between this point and
   * point p1.
   * @param p1 the other point
   * @return  the square of the distance
   */
  public final float distanceSquared(Point3f p1)
    {
      float dx, dy, dz;

      dx = this.x-p1.x;
      dy = this.y-p1.y;
      dz = this.z-p1.z;
      return dx*dx+dy*dy+dz*dz;
    }


  /**
   * Computes the distance between this point and point p1.
   * @param p1 the other point
   * @return the distance
   */
  public final float distance(Point3f p1)
    {
      float  dx, dy, dz;

      dx = this.x-p1.x;
      dy = this.y-p1.y;
      dz = this.z-p1.z;
      return (float) Math.sqrt(dx*dx+dy*dy+dz*dz);
    }


  /**
    * Computes the L-1 (Manhattan) distance between this point and
    * point p1.  The L-1 distance is equal to:
    *  abs(x1-x2) + abs(y1-y2) + abs(z1-z2).
    * @param p1 the other point
    * @return  the L-1 distance
    */
  public final float distanceL1(Point3f p1)
    {
       return( Math.abs(this.x-p1.x) + Math.abs(this.y-p1.y) + Math.abs(this.z-p1.z));
    }


  /**
    * Computes the L-infinite distance between this point and
    * point p1.  The L-infinite distance is equal to
    * MAX[abs(x1-x2), abs(y1-y2), abs(z1-z2)].
    * @param p1 the other point
    * @return  the L-infinite distance
    */
  public final float distanceLinf(Point3f p1)
    {
       float tmp;
       tmp = Math.max( Math.abs(this.x-p1.x), Math.abs(this.y-p1.y));
       return(Math.max(tmp,Math.abs(this.z-p1.z)));

    }


  /**
    *  Multiplies each of the x,y,z components of the Point4f parameter
    *  by 1/w and places the projected values into this point.
    *  @param  p1  the source Point4f, which is not modified
    */
   public final void project(Point4f p1)
   {
     float oneOw;

     oneOw = 1/p1.w;
     x = p1.x*oneOw;
     y = p1.y*oneOw;
     z = p1.z*oneOw;

   }


}
