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
 * A 4 element point represented by single precision floating point x,y,z,w
 * coordinates.
 *
 */
public class Point4f extends Tuple4f implements java.io.Serializable {


    // Compatible with 1.1
    static final long serialVersionUID = 4643134103185764459L;

  /**
   * Constructs and initializes a Point4f from the specified xyzw coordinates.
   * @param x the x coordinate
   * @param y the y coordinate
   * @param z the z coordinate
   * @param w the w coordinate
   */
  public Point4f(float x, float y, float z, float w)
  {
       super(x,y,z,w);
  }


  /**
   * Constructs and initializes a Point4f from the array of length 4.
   * @param p the array of length 4 containing xyzw in order
   */
  public Point4f(float[] p)
  {
     super(p);
  }


  /**
   * Constructs and initializes a Point4f from the specified Point4f.
   * @param p1 the Point4f containing the initialization x y z w data
   */
  public Point4f(Point4f p1)
  {
      super(p1);
  }


  /**
   * Constructs and initializes a Point4f from the specified Point4d.
   * @param p1 the Point4d containing the initialization x y z w data
   */
  public Point4f(Point4d p1)
  {
      super(p1);
  }


    /**
     * Constructs and initializes a Point4f from the specified Tuple4f.
     * @param t1 the Tuple4f containing the initialization x y z w data
     */
    public Point4f(Tuple4f t1)
    {
       super(t1);
    }


    /**
     * Constructs and initializes a Point4f from the specified Tuple4d.
     * @param t1 the Tuple4d containing the initialization x y z w data
     */
    public Point4f(Tuple4d t1)
    {
       super(t1);
    }


    /**
     * Constructs and initializes a Point4f from the specified Tuple3f.
     * The x,y,z components of this point are set to the corresponding
     * components of tuple t1.  The w component of this point
     * is set to 1.
     * @param t1 the tuple to be copied
     *
     * @since vecmath 1.2
     */
    public Point4f(Tuple3f t1) {
	super(t1.x, t1.y, t1.z, 1.0f);
    }


  /**
   * Constructs and initializes a Point4f to (0,0,0,0).
   */
  public Point4f()
  {
      super();
  }


    /**
     * Sets the x,y,z components of this point to the corresponding
     * components of tuple t1.  The w component of this point
     * is set to 1.
     * @param t1 the tuple to be copied
     *
     * @since vecmath 1.2
     */
    public final void set(Tuple3f t1) {
	this.x = t1.x;
	this.y = t1.y;
	this.z = t1.z;
	this.w = 1.0f;
    }


 /**
   * Computes the square of the distance between this point and point p1.
   * @param p1 the other point
   * @return the square of distance between these two points as a float
   */
  public final float distanceSquared(Point4f p1)
    {
      float dx, dy, dz, dw;

      dx = this.x-p1.x;
      dy = this.y-p1.y;
      dz = this.z-p1.z;
      dw = this.w-p1.w;
      return (dx*dx+dy*dy+dz*dz+dw*dw);
    }


  /**
   * Computes the distance between this point and point p1.
   * @param p1 the other point
   * @return the distance between the two points
   */
  public final float distance(Point4f p1)
    {
      float dx, dy, dz, dw;

      dx = this.x-p1.x;
      dy = this.y-p1.y;
      dz = this.z-p1.z;
      dw = this.w-p1.w;
      return (float) Math.sqrt(dx*dx+dy*dy+dz*dz+dw*dw);
    }


  /**
    * Computes the L-1 (Manhattan) distance between this point and
    * point p1.  The L-1 distance is equal to:
    *  abs(x1-x2) + abs(y1-y2) + abs(z1-z2) + abs(w1-w2).
    * @param p1 the other point
    * @return  the L-1 distance
    */
  public final float distanceL1(Point4f p1)
    {
       return( Math.abs(this.x-p1.x) + Math.abs(this.y-p1.y) + Math.abs(this.z-p1.z) + Math.abs(this.w-p1.w));
    }


  /**
    * Computes the L-infinite distance between this point and
    * point p1.  The L-infinite distance is equal to
    * MAX[abs(x1-x2), abs(y1-y2), abs(z1-z2), abs(w1-w2)].
    * @param p1 the other point
    * @return  the L-infinite distance
    */
  public final float distanceLinf(Point4f p1)
    {
       float t1, t2;
       t1 = Math.max( Math.abs(this.x-p1.x), Math.abs(this.y-p1.y));
       t2 = Math.max( Math.abs(this.z-p1.z), Math.abs(this.w-p1.w));

       return(Math.max(t1,t2));

    }

  /**
    *  Multiplies each of the x,y,z components of the Point4f parameter
    *  by 1/w, places the projected values into this point, and places
    *  a 1 as the w parameter of this point.
    *  @param  p1  the source Point4f, which is not modified
    */
   public final void project(Point4f p1)
   {
     float oneOw;

     oneOw = 1/p1.w;
     x = p1.x*oneOw;
     y = p1.y*oneOw;
     z = p1.z*oneOw;
     w = 1.0f;

   }

}
