/*
   Copyright (C) 1997,1998,1999
   Kenji Hiranabe, Eiwa System Management, Inc.

   This program is free software.
   Implemented by Kenji Hiranabe(hiranabe@esm.co.jp),
   conforming to the Java(TM) 3D API specification by Sun Microsystems.

   Permission to use, copy, modify, distribute and sell this software
   and its documentation for any purpose is hereby granted without fee,
   provided that the above copyright notice appear in all copies and
   that both that copyright notice and this permission notice appear
   in supporting documentation. Kenji Hiranabe and Eiwa System Management,Inc.
   makes no representations about the suitability of this software for any
   purpose.  It is provided "AS IS" with NO WARRANTY.
*/
package javax.vecmath;

import java.io.Serializable;

/**
  * A 4 element vector that is represented by single precision floating point
  * x,y,z,w coordinates.
  * @version specification 1.1, implementation $Revision$, $Date$
  * @author Kenji hiranabe
  */
public class Vector4f extends Tuple4f implements Serializable {
/*
 * $Log$
 * Revision 1.1  2002/08/22 20:01:16  egonw
 * Lots of new files. Amongst which the source code of vecmath.jar.
 * The latter has been changed to compile with gcj-3.0.4.
 * Actually, CDK does now compile, i.e. at least the classes mentioned
 * in core.classes and extra.classes. *And* a binary executable can get
 * generated that works!
 *
 * Revision 1.9  1999/10/05  07:03:50  hiranabe
 * copyright change
 *
 * Revision 1.9  1999/10/05  07:03:50  hiranabe
 * copyright change
 *
 * Revision 1.8  1999/10/05  04:57:17  hiranabe
 * Java3D 1.2 support
 * Vector4f(Tuple3f t1) constructor
 * set(Tuple3f t1)
 *
 * Revision 1.7  1999/03/04  09:16:33  hiranabe
 * small bug fix and copyright change
 *
 * Revision 1.6  1998/10/14  00:49:10  hiranabe
 * API1.1 Beta02
 *
 * Revision 1.5  1998/04/10  04:52:14  hiranabe
 * API1.0 -> API1.1 (added constructors, methods)
 *
 * Revision 1.4  1998/04/09  08:18:15  hiranabe
 * minor comment change
 *
 * Revision 1.3  1998/04/09  07:05:18  hiranabe
 * API 1.1
 *
 * Revision 1.2  1998/01/05  06:29:31  hiranabe
 * copyright 98
 *
 * Revision 1.1  1997/11/26  03:00:44  hiranabe
 * Initial revision
 *
 */


    /**
      * Constructs and initializes a Vector4f from the specified xyzw coordinates.
      * @param x the x coordinate
      * @param y the y coordinate
      * @param z the z coordinate
      * @param w the w coordinate
      */
    public Vector4f(float x, float y, float z, float w) {
	super(x, y, z, w);
    }

    /**
      * Constructs and initializes a Vector4f from the specified array of length 4.
      * @param v the array of length 4 containing xyzw in order
      */
    public Vector4f(float v[]) {
	super(v);
    }

    /**
      * Constructs and initializes a Vector4f from the specified Vector4f.
      * @param v1 the Vector4f containing the initialization x y z w data
      */
    public Vector4f(Vector4f v1) {
	super(v1);
    }

    /**
      * Constructs and initializes a Vector4f from the specified Vector4d.
      * @param v1 the Vector4d containing the initialization x y z w data
      */
    public Vector4f(Vector4d v1) {
	super(v1);
    }

    /**
      * Constructs and initializes a Vector4f from the specified Tuple4d.
      * @param t1 the Tuple4d containing the initialization x y z w data
      */
    public Vector4f(Tuple4d t1) {
	super(t1);
    }

    /**
      * Constructs and initializes a Vector4f from the specified Tuple4f.
      * @param t1 the Tuple4f containing the initialization x y z w data
      */
    public Vector4f(Tuple4f t1) {
	super(t1);
    }

    /**
      * Constructs and initializes a Vector4f to (0,0,0,0).
      */
    public Vector4f() {
	// super(); called implicitly
    }

    /**
     * Constructs and initializes a Vector4f from the specified Tuple3f.
     * The x,y,z  components of this point are set to the corresponding
     * components
     * of tuple t1. The w component of this point is set to 0.
     *
     * @param t1 the tuple to be copied
     * @since Java3D 1.2
     */
    public Vector4f(Tuple3f t1) {
        super(t1.x, t1.y, t1.z, 0);
    }

    /**
     * Sets the x,y,z components of this point to the corresponding
     * components of tuple t1. The w component of this point is set to 1.
     *
     * @param t1 the tuple to be copied
     * @since Java3D 1.2
     */
    public final void set(Tuple3f t1) {
        set(t1.x, t1.y, t1.z, 0);
    }

    /**
      * Returns the squared length of this vector.
      * @return the squared length of this vectoras a float
      */
    public final float lengthSquared() {
	return x*x + y*y + z*z + w*w;
    }

    /**
      * Returns the length of this vector.
      * @return the length of this vector as a float
      */
      public final float length() {
	  return (float)Math.sqrt(lengthSquared());
      }

    /**
      * Computes the dot product of the this vector and vector v1.
      * @param  v1 the other vector
      * @return the dot product of this vector and v1
      */
    public final float dot(Vector4f v1) {
	return x*v1.x + y*v1.y + z*v1.z + w*v1.w;
    }

    /**
      * Sets the value of this vector to the normalization of vector v1.
      * @param v1 the un-normalized vector
      */
    public final void normalize(Vector4d v1) {
	set(v1);
	normalize();
    }

    /**
      * Normalizes this vector in place.
      */
    public final void normalize() {
	double d = length();

	// zero-div may occur.
	x /= d;
	y /= d;
	z /= d;
	w /= d;
    }

    /**
      * Returns the (4-space) angle in radians between this vector and
      * the vector parameter; the return value is constrained to the
      * range [0,PI].
      * @param v1  the other vector
      * @return the angle in radians in the range [0,PI]
      */
    public final float angle(Vector4f v1) {
	// zero div may occur.
	double d = dot(v1);
	double v1_length = v1.length();
	double v_length = length();

	// numerically, domain error may occur
	return (float)Math.acos(d/v1_length/v_length);
    }
}
