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
 * A 4-element vector represented by single-precision floating point x,y,z,w
 * coordinates.
 *
 */
public class Vector4f extends Tuple4f implements java.io.Serializable {

  // Compatible with 1.1
  static final long serialVersionUID = 8749319902347760659L;

  /**
   * Constructs and initializes a Vector4f from the specified xyzw coordinates.
   * @param x the x coordinate
   * @param y the y coordinate
   * @param z the z coordinate
   * @param w the w coordinate
   */
  public Vector4f(float x, float y, float z, float w)
  {
       super(x,y,z,w);
  }


  /**
   * Constructs and initializes a Vector4f from the array of length 4.
   * @param v the array of length 4 containing xyzw in order
   */
  public Vector4f(float[] v)
  {
     super(v);
  }


  /**
   * Constructs and initializes a Vector4f from the specified Vector4f.
   * @param v1 the Vector4f containing the initialization x y z w data
   */
  public Vector4f(Vector4f v1)
  {
      super(v1);
  }


  /**
   * Constructs and initializes a Vector4f from the specified Vector4d.
   * @param v1 the Vector4d containing the initialization x y z w data
   */
  public Vector4f(Vector4d v1)
  {
      super(v1);
  }


    /**
     * Constructs and initializes a Vector4f from the specified Tuple4f.
     * @param t1 the Tuple4f containing the initialization x y z w data
     */
    public Vector4f(Tuple4f t1)
    {
       super(t1);
    }


    /**
     * Constructs and initializes a Vector4f from the specified Tuple4d.
     * @param t1 the Tuple4d containing the initialization x y z w data
     */
    public Vector4f(Tuple4d t1)
    {
       super(t1);
    }


    /**
     * Constructs and initializes a Vector4f from the specified Tuple3f.
     * The x,y,z components of this vector are set to the corresponding
     * components of tuple t1.  The w component of this vector
     * is set to 0.
     * @param t1 the tuple to be copied
     *
     * @since vecmath 1.2
     */
    public Vector4f(Tuple3f t1) {
	super(t1.x, t1.y, t1.z, 0.0f);
    }


  /**
   * Constructs and initializes a Vector4f to (0,0,0,0).
   */
  public Vector4f()
  {
      super();
  }


    /**
     * Sets the x,y,z components of this vector to the corresponding
     * components of tuple t1.  The w component of this vector
     * is set to 0.
     * @param t1 the tuple to be copied
     *
     * @since vecmath 1.2
     */
    public final void set(Tuple3f t1) {
	this.x = t1.x;
	this.y = t1.y;
	this.z = t1.z;
	this.w = 0.0f;
    }


 /**
   * Returns the length of this vector.
   * @return the length of this vector as a float
   */
  public final float length()
  {
    return
      (float) Math.sqrt(this.x*this.x + this.y*this.y +
                        this.z*this.z + this.w*this.w);
  }

  /**
   * Returns the squared length of this vector
   * @return the squared length of this vector as a float
   */
  public final float lengthSquared()
  {
    return (this.x*this.x + this.y*this.y +
            this.z*this.z + this.w*this.w);
  }

  /**
   * returns the dot product of this vector and v1
   * @param v1 the other vector
   * @return the dot product of this vector and v1
   */
  public final float dot(Vector4f v1)
    {
      return (this.x*v1.x + this.y*v1.y + this.z*v1.z + this.w*v1.w);
    }


 /**
   * Sets the value of this vector to the normalization of vector v1.
   * @param v1 the un-normalized vector
   */
  public final void normalize(Vector4f v1)
  {
    float norm;

    norm = (float) (1.0/Math.sqrt(v1.x*v1.x + v1.y*v1.y +
                                  v1.z*v1.z + v1.w*v1.w));
    this.x = v1.x*norm;
    this.y = v1.y*norm;
    this.z = v1.z*norm;
    this.w = v1.w*norm;
  }


  /**
   * Normalizes this vector in place.
   */
  public final void normalize()
  {
    float norm;

    norm = (float) (1.0/Math.sqrt(this.x*this.x + this.y*this.y +
                                  this.z*this.z + this.w*this.w));
    this.x *= norm;
    this.y *= norm;
    this.z *= norm;
    this.w *= norm;
  }


  /**
    *   Returns the (4-space) angle in radians between this vector and
    *   the vector parameter; the return value is constrained to the
    *   range [0,PI].
    *   @param v1    the other vector
    *   @return   the angle in radians in the range [0,PI]
    */
   public final float angle(Vector4f v1)
   {
      double vDot = this.dot(v1) / ( this.length()*v1.length() );
      if( vDot < -1.0) vDot = -1.0;
      if( vDot >  1.0) vDot =  1.0;
      return((float) (Math.acos( vDot )));
   }

}
