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
  * A 2 element vector that is represented by single precision
  * floating point x,y coordinates.
  * @version specification 1.1, implementation $Revision$, $Date$
  * @author Kenji hiranabe
  */
public class Vector2d extends Tuple2d implements Serializable {
/*
 * $Log$
 * Revision 1.1  2002/08/22 20:01:16  egonw
 * Lots of new files. Amongst which the source code of vecmath.jar.
 * The latter has been changed to compile with gcj-3.0.4.
 * Actually, CDK does now compile, i.e. at least the classes mentioned
 * in core.classes and extra.classes. *And* a binary executable can get
 * generated that works!
 *
 * Revision 1.4  1999/10/05  07:03:50  hiranabe
 * copyright change
 *
 * Revision 1.4  1999/10/05  07:03:50  hiranabe
 * copyright change
 *
 * Revision 1.3  1999/03/04  09:16:33  hiranabe
 * small bug fix and copyright change
 *
 * Revision 1.2  1998/10/14  00:49:10  hiranabe
 * API1.1 Beta02
 *
# Revision 1.1  1998/07/27  04:28:13  hiranabe
# API1.1Alpha01 ->API1.1Alpha03
#
 *
 */

    /**
      * Constructs and initializes a Vector2d from the specified xy coordinates.
      * @param x the x coordinate
      * @param y the y coordinate
      */
    public Vector2d(double x, double y) {
	super(x, y);
    }

    /**
      * Constructs and initializes a Vector2d from the specified array.
      * @param v the array of length 2 containing xy in order
      */
    public Vector2d(double v[]) {
	super(v);
    }

    /**
      * Constructs and initializes a Vector2d from the specified Vector2d.
      * @param v1 the Vector2d containing the initialization x y data
      */
    public Vector2d(Vector2d v1) {
	super(v1);
    }

    /**
      * Constructs and initializes a Vector2d from the specified Vector2f.
      * @param v1 the Vector2f containing the initialization x y data
      */
    public Vector2d(Vector2f v1) {
	super(v1);
    }

    /**
      * Constructs and initializes a Vector2d from the specified Tuple2d.
      * @param t1 the Tuple2d containing the initialization x y data
      */
    public Vector2d(Tuple2d t1) {
	super(t1);
    }

    /**
      * Constructs and initializes a Vector2d from the specified Tuple2f.
      * @param t1 the Tuple2f containing the initialization x y data
      */
    public Vector2d(Tuple2f t1) {
	super(t1);
    }

    /**
      * Constructs and initializes a Vector2d to (0,0).
      */
    public Vector2d() {
	super();
    }

    /**
      * Computes the dot product of the this vector and vector v1.
      * @param  v1 the other vector
      */
    public final double dot(Vector2d v1) {
	return x*v1.x + y*v1.y;
    }

    /**
      * Returns the length of this vector.
      * @return the length of this vector
      */
    public final double length() {
	return Math.sqrt(x*x + y*y);
    }

    /**
      * Returns the squared length of this vector.
      * @return the squared length of this vector
      */
    public final double lengthSquared() {
	return x*x + y*y;
    }

    /**
      * Normalizes this vector in place.
      */
    public final void normalize() {
	double d = length();

	// zero-div may occur.
	x /= d;
	y /= d;
    }

    /**
      * Sets the value of this vector to the normalization of vector v1.
      * @param v1 the un-normalized vector
      */
    public final void normalize(Vector2d v1) {
	set(v1);
	normalize();
    }

    /**
      * Returns the angle in radians between this vector and
      * the vector parameter; the return value is constrained to the
      * range [0,PI].
      * @param v1  the other vector
      * @return the angle in radians in the range [0,PI]
      */
    public final double angle(Vector2d v1) {
	// stabler than acos
	return Math.abs(Math.atan2(x*v1.y - y*v1.x , dot(v1)));
    }
}
