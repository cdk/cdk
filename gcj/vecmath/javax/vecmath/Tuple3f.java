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
  * A generic 3 element tuple that is represented by
  * single precision floating point x,y and z coordinates.
  * @version specification 1.1, implementation $Revision$, $Date$
  * @author Kenji hiranabe
  */
public abstract class Tuple3f implements Serializable {
/*
 * $Log$
 * Revision 1.1  2002/08/22 20:01:16  egonw
 * Lots of new files. Amongst which the source code of vecmath.jar.
 * The latter has been changed to compile with gcj-3.0.4.
 * Actually, CDK does now compile, i.e. at least the classes mentioned
 * in core.classes and extra.classes. *And* a binary executable can get
 * generated that works!
 *
 * Revision 1.10  1999/10/05  07:03:50  hiranabe
 * copyright change
 *
 * Revision 1.10  1999/10/05  07:03:50  hiranabe
 * copyright change
 *
 * Revision 1.9  1999/03/04  09:16:33  hiranabe
 * small bug fix and copyright change
 *
 * Revision 1.8  1998/10/14  00:49:10  hiranabe
 * API1.1 Beta02
 *
 * Revision 1.7  1998/07/27  04:28:13  hiranabe
 * API1.1Alpha01 ->API1.1Alpha03
 *
 * Revision 1.6  1998/04/17  10:30:46  hiranabe
 * null check for equals
 *
 * Revision 1.5  1998/04/09  08:18:15  hiranabe
 * minor comment change
 *
 * Revision 1.4  1998/04/09  07:05:18  hiranabe
 * API 1.1
 *
 * Revision 1.3  1998/01/05  06:29:31  hiranabe
 * copyright 98
 *
 * Revision 1.2  1997/12/28  23:41:10  hiranabe
 * scale typo bug suggested by leonvs@iaehv.nl
 *
 * Revision 1.1  1997/11/26  03:00:44  hiranabe
 * Initial revision
 *
 */


    /**
      * The x coordinate.
      */
    public float x;

    /**
      * The y coordinate.
      */
    public float y;

    /**
      * The z coordinate.
      */
    public float z;

    /**
      * Constructs and initializes a Tuple3f from the specified xyz coordinates.
      * @param x the x coordinate
      * @param y the y coordinate
      * @param z the z coordinate
      */
    public Tuple3f(float x, float y, float z) {
	this.x = x;
	this.y = y;
	this.z = z;
    }

    /**
      * Constructs and initializes a Tuple3f from the specified array.
      * @param t the array of length 3 containing xyz in order
      */
    public Tuple3f(float t[]) {
	// ArrayIndexOutOfBounds is thrown if t.length < 3
	this.x = t[0];
	this.y = t[1];
	this.z = t[2];
    }

    /**
      * Constructs and initializes a Tuple3f from the specified Tuple3f.
      * @param t1 the Tuple3f containing the initialization x y z data
      */
    public Tuple3f(Tuple3f t1) {
	x = t1.x;
	y = t1.y;
	z = t1.z;
    }

    /**
      * Constructs and initializes a Tuple3f from the specified Tuple3d.
      * @param t1 the Tuple3d containing the initialization x y z data
      */
    public Tuple3f(Tuple3d t1) {
	x = (float)t1.x;
	y = (float)t1.y;
	z = (float)t1.z;
    }

    /**
      * Constructs and initializes a Tuple3f to (0,0,0).
      */
    public Tuple3f() {
	x = 0.0f;
	y = 0.0f;
	z = 0.0f;
    }

    /**
      * Sets the value of this tuple to the specified xyz coordinates.
      * @param x the x coordinate
      * @param y the y coordinate
      * @param z the z coordinate
      */
    public final void set(float x, float y, float z) {
	this.x = x;
	this.y = y;
	this.z = z;
    }

    /**
      * Sets the value of this tuple from the 3 values specified in the array.
      * @param t the array of length 3 containing xyz in order
      */
    public final void set(float t[]) {
	// ArrayIndexOutOfBounds is thrown if t.length < 3
	x = t[0];
	y = t[1];
	z = t[2];
    }

    /**
      * Sets the value of this tuple to the value of the Tuple3f argument.
      * @param t1 the tuple to be copied
      */
    public final void set(Tuple3f t1) {
	x = t1.x;
	y = t1.y;
	z = t1.z;
    }

    /**
      * Sets the value of this tuple to the value of the Tuple3d argument.
      * @param t1 the tuple to be copied
      */
    public final void set(Tuple3d t1) {
	x = (float)t1.x;
	y = (float)t1.y;
	z = (float)t1.z;
    }

    /**
      * Copies the value of the elements of this tuple into the array t[]. 
      * @param t the array that will contain the values of the vector
      */
    public final void get(float t[]) {
	// ArrayIndexOutOfBounds is thrown if t.length < 3
	t[0] = x;
	t[1] = y;
	t[2] = z;
    }

    /**
      * Gets the value of this tuple and copies the values into the Tuple3f.
      * @param t Tuple3f object into which that values of this object are copied
      */
    public final void get(Tuple3f t) {
	t.x = x;
	t.y = y;
	t.z = z;
    }

    /**
      * Sets the value of this tuple to the vector sum of tuples t1 and t2.
      * @param t1 the first tuple
      * @param t2 the second tuple
      */
    public final void add(Tuple3f t1, Tuple3f t2) {
	x = t1.x + t2.x;
	y = t1.y + t2.y;
	z = t1.z + t2.z;
    }

    /**
      * Sets the value of this tuple to the vector sum of itself and tuple t1.
      * @param t1  the other tuple
      */
    public final void add(Tuple3f t1) {
	x += t1.x;
	y += t1.y;
	z += t1.z;
    }


    /**
      * Sets the value of this tuple to the vector difference of tuple t1 and t2 (this = t1 - t2).
      * @param t1 the first tuple
      * @param t2 the second tuple
      */
    public final void sub(Tuple3f t1, Tuple3f t2) {
	x = t1.x - t2.x;
	y = t1.y - t2.y;
	z = t1.z - t2.z;
    }

    /**
      * Sets the value of this tuple to the vector difference of itself and tuple t1 (this = this - t1).
      * @param t1 the other tuple
      */
    public final void sub(Tuple3f t1) {
	x -= t1.x;
	y -= t1.y;
	z -= t1.z;
    }

    /**
      * Sets the value of this tuple to the negation of tuple t1. 
      * @param t1 the source vector
      */
    public final void negate(Tuple3f t1) {
	x = -t1.x;
	y = -t1.y;
	z = -t1.z;
    }

    /**
      * Negates the value of this vector in place.
      */
    public final void negate() {
	x = -x;
	y = -y;
	z = -z;
    }
      

    /**
      * Sets the value of this tuple to the scalar multiplication of tuple t1.
      * @param s the scalar value
      * @param t1 the source tuple
      */
    public final void scale(float s, Tuple3f t1) {
	x = s*t1.x;
	y = s*t1.y;
	z = s*t1.z;
    }

    /**
      * Sets the value of this tuple to the scalar multiplication of itself.
      * @param s the scalar value
      */
    public final void scale(float s) {
	x *= s;
	y *= s;
	z *= s;
    }

    /**
      * Sets the value of this tuple to the scalar multiplication of tuple t1 and then
      * adds tuple t2 (this = s*t1 + t2).
      * @param s the scalar value
      * @param t1 the tuple to be multipled
      * @param t2 the tuple to be added
      */
    public final void scaleAdd(float s, Tuple3f t1, Tuple3f t2) {
	x = s*t1.x + t2.x;
	y = s*t1.y + t2.y;
	z = s*t1.z + t2.z;
    }

    /**
      * Sets the value of this tuple to the scalar multiplication of itself and then
      * adds tuple t1 (this = s*this + t1).
      * @param s the scalar value
      * @param t1 the tuple to be added
      */
    public final void scaleAdd(float s, Tuple3f t1) {
	x = s*x + t1.x;
	y = s*y + t1.y;
	z = s*z + t1.z;
    }

    /**
      * Returns a hash number based on the data values in this object. 
      * Two different Tuple3f objects with identical data  values
      * (ie, returns true for equals(Tuple3f) ) will return the same hash number.
      * Two vectors with different data members may return the same hash value,
      * although this is not likely.
      */
      public int hashCode() {
	  int xbits = Float.floatToIntBits(x);
	  int ybits = Float.floatToIntBits(y);
	  int zbits = Float.floatToIntBits(z);
	  return xbits ^ ybits ^ zbits;
      }

    /**
      * Returns true if all of the data members of Tuple3f t1 are equal to the corresponding
      * data members in this
      * @param t1 the vector with which the comparison is made.
      */
    public boolean equals(Tuple3f t1) {
	return t1 != null && x == t1.x && y == t1.y && z == t1.z;
    }

    /**
      * Returns true if the L-infinite distance between this tuple and tuple t1 is
      * less than or equal to the epsilon parameter, otherwise returns false. The L-infinite
      * distance is equal to MAX[abs(x1-x2), abs(y1-y2)].
      * @param t1 the tuple to be compared to this tuple
      * @param epsilon the threshold value
      */
    public boolean epsilonEquals(Tuple3f t1, float epsilon) {
	return (Math.abs(t1.x - this.x) <= epsilon) &&
	    (Math.abs(t1.y - this.y) <= epsilon) &&
	    (Math.abs(t1.z - this.z) <= epsilon);
    }

    /**
      * Returns a string that contains the values of this Tuple3f. The form is (x,y,z).
      * @return the String representation
      */
    public String toString() {
	    return "(" + x + ", " + y + ", " + z +")";
    }

    /**
      * Clamps the tuple parameter to the range [low, high] and places the values
      * into this tuple.
      * @param min the lowest value in the tuple after clamping
      * @param max the highest value in the tuple after clamping
      * @param t the source tuple, which will not be modified
      */
    public final void clamp(float min, float max, Tuple3f t) {
	set(t);
	clamp(min, max);
    }
      
    /**
      * Clamps the minimum value of the tuple parameter to the min parameter
      * and places the values into this tuple.
      * @param min the lowest value in the tuple after clamping
      * @parm t the source tuple, which will not be modified
      */
    public final void clampMin(float min, Tuple3f t) {
	set(t);
	clampMin(min);
    }

    /**
      * Clamps the maximum value of the tuple parameter to the max parameter and
      * places the values into this tuple.
      * @param max the highest value in the tuple after clamping
      * @param t the source tuple, which will not be modified
      */
    public final void clampMax(float max, Tuple3f t) {
	set(t);
	clampMax(max);
    }
      

    /**
      * Sets each component of the tuple parameter to its absolute value and
      * places the modified values into this tuple.
      * @param t the source tuple, which will not be modified
      */
    public final void absolute(Tuple3f t) {
	set(t);
	absolute();
    }

    /**
      * Clamps this tuple to the range [low, high].
      * @param min the lowest value in this tuple after clamping
      * @param max the highest value in this tuple after clamping
      */
    public final void clamp(float min, float max) {
	clampMin(min);
	clampMax(max);
    }

    /**
      * Clamps the minimum value of this tuple to the min parameter.
      * @param min the lowest value in this tuple after clamping
      */
    public final void clampMin(float min) {
	if (x < min)
	    x = min;
	if (y < min)
	    y = min;
	if (z < min)
	    z = min;
    }

    /**
      * Clamps the maximum value of this tuple to the max parameter.
      * @param max the highest value in the tuple after clamping
      */
    public final void clampMax(float max) {
	if (x > max)
	    x = max;
	if (y > max)
	    y = max;
	if (z > max)
	    z = max;
    }

    /**
      * Sets each component of this tuple to its absolute value.
      */
    public final void absolute() {
	if (x < 0.0)
	    x = -x;
	if (y < 0.0)
	    y = -y;
	if (z < 0.0)
	    z = -z;
    }

    /**
      * Linearly interpolates between tuples t1 and t2 and places the
      * result into this tuple: this = (1-alpha)*t1 + alpha*t2.
      * @param t1 the first tuple
      * @param t2 the second tuple
      * @param alpha the alpha interpolation parameter
      */
    public final void interpolate(Tuple3f t1, Tuple3f t2, float alpha) {
	set(t1);
	interpolate(t2, alpha);
    }


    /**
      * Linearly interpolates between this tuple and tuple t1 and places the
      * result into this tuple: this = (1-alpha)*this + alpha*t1.
      * @param t1 the first tuple
      * @param alpha the alpha interpolation parameter
      *
      */
    public final void interpolate(Tuple3f t1, float alpha) {
	float beta = 1 - alpha;
	x = beta*x + alpha*t1.x;
	y = beta*y + alpha*t1.y;
	z = beta*z + alpha*t1.z;
    }
}
