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
  * A 4-element tuple represented by signed integer x,y,z,w coordinates. 
  *
  * @since Java 3D 1.2
  * @version specification 1.2, implementation $Revision$, $Date$
  * @author Kenji hiranabe
  */
public abstract class Tuple4i implements Serializable {
/*
 * $Log$
 * Revision 1.1  2002/08/22 20:01:16  egonw
 * Lots of new files. Amongst which the source code of vecmath.jar.
 * The latter has been changed to compile with gcj-3.0.4.
 * Actually, CDK does now compile, i.e. at least the classes mentioned
 * in core.classes and extra.classes. *And* a binary executable can get
 * generated that works!
 *
# Revision 1.1  1999/11/25  10:29:35  hiranabe
# Initial revision
#
# Revision 1.1  1999/11/25  10:29:35  hiranabe
# Initial revision
#
 */


    /**
      * The x coordinate.
      */
    public int x;

    /**
      * The y coordinate.
      */
    public int y;

    /**
      * The z coordinate.
      */
    public int z;

    /**
      * The w coordinate.
      */
    public int w;

    /**
      * Constructs and initializes a Tuple4i from the specified x, y, z and w
      * coordinates.
      * @param x the x coordinate.
      * @param y the y coordinate.
      * @param z the z coordinate.
      * @param w the w coordinate.
      */
    public Tuple4i(int x, int y, int z, int w) {
	this.x = x;
	this.y = y;
	this.z = z;
	this.w = w;
    }

    /**
      * 
      * Constructs and initializes a Tuple4i from input array of length 4.
      * @param t the array of length 4 containing x, y, z, and w in order.
      */
    public Tuple4i(int t[]) {
	// ArrayIndexOutOfBounds is thrown if t.length < 4
	this.x = t[0];
	this.y = t[1];
	this.z = t[2];
	this.w = t[3];
    }

    /**
      * Constructs and initializes a Tuple4i from the specified Tuple4i.
      * @param t1 the Tuple4i containing the initialization x y z w data
      */
    public Tuple4i(Tuple4i t1) {
	x = t1.x;
	y = t1.y;
	z = t1.z;
	w = t1.w;
    }

    /**
      * Constructs and initializes a Tuple4i to (0,0,0,0).
      */
    public Tuple4i() {
	x = 0;
	y = 0;
	z = 0;
	w = 0;
    }

    /**
      * Sets the value of this tuple to to the specified x, y, z, and w coordinates.
      * @param x the x coordinate.
      * @param y the y coordinate.
      * @param z the z coordinate.
      * @param w the w coordinate.
      */
    public final void set(int x, int y, int z, int w) {
	this.x = x;
	this.y = y;
	this.z = z;
	this.w = w;
    }

    /**
      * Sets the value of this tuple to the specified coordinates int the 
      * array of length 4.
      * @param t array of length 4 x, y, and z in order.
      */
    public final void set(int t[]) {
	// ArrayIndexOutOfBounds is thrown if t.length < 4
	x = t[0];
	y = t[1];
	z = t[2];
	w = t[3];
    }

    /**
      * Sets the value of this tuple to the value of tuple t1.
      * @param t1 the tuple to be copied.
      */
    public final void set(Tuple4i t1) {
	x = t1.x;
	y = t1.y;
	z = t1.z;
	w = t1.w;
    }

    /**
      * Copies the values of this tuple into the array t.
      * @param t is the array
      */
    public final void get(int t[]) {
	// ArrayIndexOutOfBounds is thrown if t.length < 4
	t[0] = x;
	t[1] = y;
	t[2] = z;
	t[3] = w;
    }

    /**
      * Copies the values of this tuple into the tuple t.
      * @param t is the target tuple
      */
    public final void get(Tuple4i t) {
	t.x = x;
	t.y = y;
	t.z = z;
	t.w = w;
    }

    /**
      * Sets the value of this tuple to the sum of tuples t1 and t2.
      * @param t1 the first tuple
      * @param t2 the second tuple
      */
    public final void add(Tuple4i t1, Tuple4i t2) {
	x = t1.x + t2.x;
	y = t1.y + t2.y;
	z = t1.z + t2.z;
	w = t1.w + t2.w;
    }

    /**
      * Sets the value of this tuple to the sum of itself and t1.
      * @param t is the other tuple
      */
    public final void add(Tuple4i t1) {
	x += t1.x;
	y += t1.y;
	z += t1.z;
	w += t1.w;
    }

    /**
      * Sets the value of this tuple to the difference of tuples t1 and t2.
      * @param t1 the first tuple
      * @param t2 the second tuple
      */
    public final void sub(Tuple4i t1, Tuple4i t2) {
	x = t1.x - t2.x;
	y = t1.y - t2.y;
	z = t1.z - t2.z;
	w = t1.w - t2.w;
    }

    /**
      * Sets the value of this tuple to the difference of itself and t1.
      * @param t is the other tuple
      */
    public final void sub(Tuple4i t1) {
	x -= t1.x;
	y -= t1.y;
	z -= t1.z;
	w -= t1.w;
    }

    /**
      * Sets the value of this tuple to the negation of tuples t1.
      * @param t1 the source tuple
      */
    public final void negate(Tuple4i t1) {
	x = -t1.x;
	y = -t1.y;
	z = -t1.z;
	w = -t1.w;
    }

    /**
      * Sets the value of this tuple to the negation of itself.
      */
    public final void negate() {
	x = -x;
	y = -y;
	z = -z;
	w = -w;
    }

    /**
      * Sets the value of this tuple to the scalar multiplication of tuples t1.
      * @param s the scalar value
      * @param t1 the source tuple
      */
    public final void scale(int s, Tuple4i t1) {
	x = s*t1.x;
	y = s*t1.y;
	z = s*t1.z;
	w = s*t1.w;
    }

    /**
      * Sets the value of this tuple to the scalar multiplication of itself.
      * @param s the scalar value
      */
    public final void scale(int s) {
	x *= s;
	y *= s;
	z *= s;
	w *= s;
    }

    /**
      * Sets the value of this tuple to the scalar multiplication
      * of tuple t1 plus tuple t2 (this = s*t1 + t2).
      * @param s the scalar value
      * @param t1 the tuple to be multipled
      * @param t2 the tuple to be added
      */
    public final void scaleAdd(int s, Tuple4i t1, Tuple4i t2) {
	x = s*t1.x + t2.x;
	y = s*t1.y + t2.y;
	z = s*t1.z + t2.z;
	w = s*t1.w + t2.w;
    }

    /**
      * Sets the value of this tuple to the scalar multiplication of itself
      * and then adds tuple t1 (this = s*this + t1).
      * @param s the scalar value
      * @param t the tuple to be added
      */
    public final void scaleAdd(int s, Tuple4i t1) {
	x = s*x + t1.x;
	y = s*y + t1.y;
	z = s*z + t1.z;
	w = s*w + t1.w;
    }

    /**
      * Returns a hash number based on the data values in this object. 
      * Two different Tuple4i objects with identical data  values
      * (ie, returns true for equals(Tuple4i) ) will return the same hash number.
      * Two vectors with different data members may return the same hash value,
      * although this is not likely.
      */
      public int hashCode() {
	  return  (x ^ y) ^ (z ^ w);
      }

    /**
      * Returns true if all of the data members of Tuple4i t1 are equal to the corresponding
      * data members in this
      * @param t1 the vector with which the comparison is made.
      */
    private boolean equals(Tuple4i t1) {
	return t1 != null && x == t1.x && y == t1.y && z == t1.z;
    }

    /**
      * Returns true if the Object o1 is of type Tuple4i and all of the data
      * members of t1 are equal to the corresponding data members in this
      * Tuple4i.
      * @param o1 the object with which the comparison is made.
      */
    public boolean equals(Object o1) {
	return o1 != null && (o1 instanceof Tuple4i) && equals((Tuple4i)o1);
    }


    /**
      * Returns a string that contains the values of this Tuple4i. The form is (x,y,z,w).
      * @return the String representation
      */
    public String toString() {
	    return "(" + x + ", " + y + ", " + z + ", " + w + ")";
    }


    /**
     * Clamps the tuple parameter to the range [low, high] and places the values into
     * this tuple.
     * @param min the lowest value in the tuple after clamping
     * @param max the highest value in the tuple after clamping
     * @param t the source tuple, which will not be modified
     */
    public final void clamp(int min, int max, Tuple4i t) {
        set(t);
        clamp(min, max);
    }

    /**
     * Clamps the minimum value of the tuple parameter to the min parameter and places
     * the values into this tuple.
     * @param min the lowest value in the tuple after clamping
     * @param t the source tuple, which will not be modified
     */
    public final void clampMin(int min, Tuple4i t) {
        set(t);
        clampMin(min);
    }

    /**
     * Clamps the maximum value of the tuple parameter to the max parameter and places
     * the values into this tuple.
     * @param max the highest value in the tuple after clamping
     * @param t the source tuple, which will not be modified
     */
    public final void clampMax(int max, Tuple4i t) {
        set(t);
        clampMax(max);
    }

    /**
     * Sets each component of the tuple parameter to its absolute value and places the
     * modified values into this tuple.
     * @param t the source tuple, which will not be modified
     */
    public final void absolute(Tuple4i t) {
        set(t);
        absolute();
    }


    /**
     * Clamps this tuple to the range [low, high].
     * @param min the lowest value in this tuple after clamping
     * @param max the highest value in this tuple after clamping
     */
    public final void clamp(int min, int max) {
        clampMin(min);
        clampMax(max);
    }

    /**
     * 
     * Clamps the minimum value of this tuple to the min parameter.
     * @param min the lowest value in this tuple after clamping
     */
    public final void clampMin(int min) {
        if (x < min)
            x = min;
        if (y < min)
            y = min;
        if (z < min)
            z = min;
        if (w < min)
            w = min;
    }

    /**
     * Clamps the maximum value of this tuple to the max parameter.
     * @param max the highest value in the tuple after clamping
     */
    public final void clampMax(int max) {
        if (x > max)
            x = max;
        if (y > max)
            y = max;
        if (z > max)
            z = max;
        if (w > max)
            w = max;
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
        if (w < 0.0)
            w = -w;
    }
}
