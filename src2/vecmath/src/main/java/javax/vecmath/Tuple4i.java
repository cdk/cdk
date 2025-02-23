/*
 * Copyright 1999-2008 Sun Microsystems, Inc.  All Rights Reserved.
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
 * A 4-element tuple represented by signed integer x,y,z,w
 * coordinates.
 *
 * @since vecmath 1.2
 */
public abstract class Tuple4i implements java.io.Serializable, Cloneable {

    static final long serialVersionUID = 8064614250942616720L;

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
     * Constructs and initializes a Tuple4i from the specified
     * x, y, z, and w coordinates.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @param w the w coordinate
     */
    public Tuple4i(int x, int y, int z, int w) {
	this.x = x;
	this.y = y;
	this.z = z;
	this.w = w;
    }


    /**
     * Constructs and initializes a Tuple4i from the array of length 4.
     * @param t the array of length 4 containing x, y, z, and w in order.
     */
    public Tuple4i(int[] t) {
	this.x = t[0];
	this.y = t[1];
	this.z = t[2];
	this.w = t[3];
    }


    /**
     * Constructs and initializes a Tuple4i from the specified Tuple4i.
     * @param t1 the Tuple4i containing the initialization x, y, z,
     * and w data.
     */
    public Tuple4i(Tuple4i t1) {
	this.x = t1.x;
	this.y = t1.y;
	this.z = t1.z;
	this.w = t1.w;
    }


    /**
     * Constructs and initializes a Tuple4i to (0,0,0,0).
     */
    public Tuple4i() {
	this.x = 0;
	this.y = 0;
	this.z = 0;
	this.w = 0;
    }


    /**
     * Sets the value of this tuple to the specified x, y, z, and w
     * coordinates.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @param w the w coordinate
     */
    public final void set(int x, int y, int z, int w) {
	this.x = x;
	this.y = y;
	this.z = z;
	this.w = w;
    }


    /**
     * Sets the value of this tuple to the specified coordinates in the
     * array of length 4.
     * @param t the array of length 4 containing x, y, z, and w in order.
     */
    public final void set(int[] t) {
	this.x = t[0];
	this.y = t[1];
	this.z = t[2];
	this.w = t[3];
    }


    /**
     * Sets the value of this tuple to the value of tuple t1.
     * @param t1 the tuple to be copied
     */
    public final void set(Tuple4i t1) {
	this.x = t1.x;
	this.y = t1.y;
	this.z = t1.z;
	this.w = t1.w;
    }


    /**
     * Copies the values of this tuple into the array t.
     * @param t the array
     */
    public final void get(int[] t) {
	t[0] = this.x;
	t[1] = this.y;
	t[2] = this.z;
	t[3] = this.w;
    }


    /**
     * Copies the values of this tuple into the tuple t.
     * @param t the target tuple
     */
    public final void get(Tuple4i t) {
	t.x = this.x;
	t.y = this.y;
	t.z = this.z;
	t.w = this.w;
    }


    /**
     * Sets the value of this tuple to the sum of tuples t1 and t2.
     * @param t1 the first tuple
     * @param t2 the second tuple
     */
    public final void add(Tuple4i t1, Tuple4i t2) {
	this.x = t1.x + t2.x;
	this.y = t1.y + t2.y;
	this.z = t1.z + t2.z;
	this.w = t1.w + t2.w;
    }


    /**
     * Sets the value of this tuple to the sum of itself and t1.
     * @param t1 the other tuple
     */
    public final void add(Tuple4i t1) {
	this.x += t1.x;
	this.y += t1.y;
	this.z += t1.z;
	this.w += t1.w;
    }


    /**
     * Sets the value of this tuple to the difference
     * of tuples t1 and t2 (this = t1 - t2).
     * @param t1 the first tuple
     * @param t2 the second tuple
     */
    public final void sub(Tuple4i t1, Tuple4i t2) {
	this.x = t1.x - t2.x;
	this.y = t1.y - t2.y;
	this.z = t1.z - t2.z;
	this.w = t1.w - t2.w;
    }


    /**
     * Sets the value of this tuple to the difference
     * of itself and t1 (this = this - t1).
     * @param t1 the other tuple
     */
    public final void sub(Tuple4i t1) {
	this.x -= t1.x;
	this.y -= t1.y;
	this.z -= t1.z;
	this.w -= t1.w;
    }


    /**
     * Sets the value of this tuple to the negation of tuple t1.
     * @param t1 the source tuple
     */
    public final void negate(Tuple4i t1) {
	this.x = -t1.x;
	this.y = -t1.y;
	this.z = -t1.z;
	this.w = -t1.w;
    }


    /**
     * Negates the value of this tuple in place.
     */
    public final void negate() {
	this.x = -this.x;
	this.y = -this.y;
	this.z = -this.z;
	this.w = -this.w;
    }


    /**
     * Sets the value of this tuple to the scalar multiplication
     * of tuple t1.
     * @param s the scalar value
     * @param t1 the source tuple
     */
    public final void scale(int s, Tuple4i t1) {
	this.x = s*t1.x;
	this.y = s*t1.y;
	this.z = s*t1.z;
	this.w = s*t1.w;
    }


    /**
     * Sets the value of this tuple to the scalar multiplication
     * of the scale factor with this.
     * @param s the scalar value
     */
    public final void scale(int s) {
	this.x *= s;
	this.y *= s;
	this.z *= s;
	this.w *= s;
    }


    /**
     * Sets the value of this tuple to the scalar multiplication
     * of tuple t1 plus tuple t2 (this = s*t1 + t2).
     * @param s the scalar value
     * @param t1 the tuple to be multipled
     * @param t2 the tuple to be added
     */
    public final void scaleAdd(int s, Tuple4i t1, Tuple4i t2) {
	this.x = s*t1.x + t2.x;
	this.y = s*t1.y + t2.y;
	this.z = s*t1.z + t2.z;
	this.w = s*t1.w + t2.w;
    }


    /**
     * Sets the value of this tuple to the scalar multiplication
     * of itself and then adds tuple t1 (this = s*this + t1).
     * @param s the scalar value
     * @param t1 the tuple to be added
     */
    public final void scaleAdd(int s, Tuple4i t1) {
        this.x = s*this.x + t1.x;
        this.y = s*this.y + t1.y;
        this.z = s*this.z + t1.z;
        this.w = s*this.w + t1.w;
    }


    /**
     * Returns a string that contains the values of this Tuple4i.
     * The form is (x,y,z,w).
     * @return the String representation
     */
    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ", " + this.w + ")";
    }


    /**
     * Returns true if the Object t1 is of type Tuple4i and all of the
     * data members of t1 are equal to the corresponding data members in
     * this Tuple4i.
     * @param t1  the object with which the comparison is made
     * @return  true or false
     */
    @Override
    public boolean equals(Object t1) {
        try {
	    Tuple4i t2 = (Tuple4i) t1;
	    return(this.x == t2.x && this.y == t2.y &&
		   this.z == t2.z && this.w == t2.w);
        }
        catch (NullPointerException e2) {
	    return false;
	}
        catch (ClassCastException e1) {
	    return false;
	}
    }


    /**
     * Returns a hash code value based on the data values in this
     * object.  Two different Tuple4i objects with identical data values
     * (i.e., Tuple4i.equals returns true) will return the same hash
     * code value.  Two objects with different data members may return the
     * same hash value, although this is not likely.
     * @return the integer hash code value
     */
    @Override
    public int hashCode() {
	long bits = 1L;
	bits = 31L * bits + (long)x;
	bits = 31L * bits + (long)y;
	bits = 31L * bits + (long)z;
	bits = 31L * bits + (long)w;
	return (int) (bits ^ (bits >> 32));
    }


    /**
     *  Clamps the tuple parameter to the range [low, high] and
     *  places the values into this tuple.
     *  @param min   the lowest value in the tuple after clamping
     *  @param max  the highest value in the tuple after clamping
     *  @param t   the source tuple, which will not be modified
     */
    public final void clamp(int min, int max, Tuple4i t) {
        if( t.x > max ) {
	    x = max;
        } else if( t.x < min ) {
	    x = min;
        } else {
	    x = t.x;
        }

        if( t.y > max ) {
	    y = max;
        } else if( t.y < min ) {
	    y = min;
        } else {
	    y = t.y;
        }

        if( t.z > max ) {
	    z = max;
        } else if( t.z < min ) {
	    z = min;
        } else {
	    z = t.z;
        }

        if( t.w > max ) {
	    w = max;
        } else if( t.w < min ) {
	    w = min;
        } else {
	    w = t.w;
        }
    }


    /**
     *  Clamps the minimum value of the tuple parameter to the min
     *  parameter and places the values into this tuple.
     *  @param min   the lowest value in the tuple after clamping
     *  @param t   the source tuple, which will not be modified
     */
    public final void clampMin(int min, Tuple4i t) {
        if( t.x < min ) {
	    x = min;
        } else {
	    x = t.x;
        }

        if( t.y < min ) {
	    y = min;
        } else {
	    y = t.y;
        }

        if( t.z < min ) {
	    z = min;
        } else {
	    z = t.z;
        }

        if( t.w < min ) {
	    w = min;
        } else {
	    w = t.w;
        }


    }


    /**
     *  Clamps the maximum value of the tuple parameter to the max
     *  parameter and places the values into this tuple.
     *  @param max   the highest value in the tuple after clamping
     *  @param t   the source tuple, which will not be modified
     */
    public final void clampMax(int max, Tuple4i t) {
        if( t.x > max ) {
	    x = max;
        } else {
	    x = t.x;
        }

        if( t.y > max ) {
	    y = max;
        } else {
	    y = t.y;
        }

        if( t.z > max ) {
	    z = max;
        } else {
	    z = t.z;
        }

        if( t.w > max ) {
	    w = max;
        } else {
	    w = t.z;
        }
    }


    /**
     *  Sets each component of the tuple parameter to its absolute
     *  value and places the modified values into this tuple.
     *  @param t   the source tuple, which will not be modified
     */
    public final void absolute(Tuple4i t) {
	x = Math.abs(t.x);
	y = Math.abs(t.y);
	z = Math.abs(t.z);
	w = Math.abs(t.w);
    }


    /**
     *  Clamps this tuple to the range [low, high].
     *  @param min  the lowest value in this tuple after clamping
     *  @param max  the highest value in this tuple after clamping
     */
    public final void clamp(int min, int max) {
	if( x > max ) {
	    x = max;
        } else if( x < min ) {
	    x = min;
        }

        if( y > max ) {
	    y = max;
        } else if( y < min ) {
	    y = min;
        }

        if( z > max ) {
	    z = max;
        } else if( z < min ) {
	    z = min;
        }

        if( w > max ) {
	    w = max;
        } else if( w < min ) {
	    w = min;
        }
    }


    /**
     *  Clamps the minimum value of this tuple to the min parameter.
     *  @param min   the lowest value in this tuple after clamping
     */
    public final void clampMin(int min) {
	if (x < min)
	    x=min;

	if (y < min)
	    y = min;

	if (z < min)
	    z = min;

	if (w < min)
	    w = min;
    }


    /**
     *  Clamps the maximum value of this tuple to the max parameter.
     *  @param max   the highest value in the tuple after clamping
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
     *  Sets each component of this tuple to its absolute value.
     */
    public final void absolute() {
	x = Math.abs(x);
	y = Math.abs(y);
	z = Math.abs(z);
	w = Math.abs(w);
    }

    /**
     * Creates a new object of the same class as this object.
     *
     * @return a clone of this instance.
     * @exception OutOfMemoryError if there is not enough memory.
     * @see java.lang.Cloneable
     * @since vecmath 1.3
     */
    @Override
    public Object clone() {
	// Since there are no arrays we can just use Object.clone()
	try {
	    return super.clone();
	} catch (CloneNotSupportedException e) {
	    // this shouldn't happen, since we are Cloneable
	    throw new InternalError();
	}
    }



    /**
	 * Get the <i>x</i> coordinate.
	 *
	 * @return the <i>x</i> coordinate.
	 *
	 * @since vecmath 1.5
	 */
	public final int getX() {
		return x;
	}


	/**
	 * Set the <i>x</i> coordinate.
	 *
	 * @param x  value to <i>x</i> coordinate.
	 *
	 * @since vecmath 1.5
	 */
	public final void setX(int x) {
		this.x = x;
	}


	/**
	 * Get the <i>y</i> coordinate.
	 *
	 * @return  the <i>y</i> coordinate.
	 *
	 * @since vecmath 1.5
	 */
	public final int getY() {
		return y;
	}


	/**
	 * Set the <i>y</i> coordinate.
	 *
	 * @param y value to <i>y</i> coordinate.
	 *
	 * @since vecmath 1.5
	 */
	public final void setY(int y) {
		this.y = y;
	}

	/**
	 * Get the <i>z</i> coordinate.
	 *
	 * @return the <i>z</i> coordinate.
	 *
	 * @since vecmath 1.5
	 */
	public final int getZ() {
		return z;
	}


	/**
	 * Set the <i>z</i> coordinate.
	 *
	 * @param z value to <i>z</i> coordinate.
	 *
	 * @since vecmath 1.5
	 */
	public final void setZ(int z) {
		this.z = z;
	}


	/**
	 * Get the <i>w</i> coordinate.
	 * @return the  <i>w</i> coordinate.
	 * @since vecmath 1.5
	 */
	public final int getW() {
		return w;
	}


	/**
	 * Set the <i>w</i> coordinate.
	 *
	 * @param w value to <i>w</i> coordinate.
	 *
	 * @since vecmath 1.5
	 */
	public final void setW(int w) {
		this.w = w;
	}

}
