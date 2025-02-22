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
 * A four byte tuple.  Note that Java defines a byte as a signed integer
 * in the range [-128, 127]. However, colors are more typically
 * represented by values in the range [0, 255]. Java 3D recognizes this
 * and, in those cases where Tuple4b is used to represent color, treats
 * the bytes as if the range were [0, 255]---in other words, as if the
 * bytes were unsigned.
 * Values greater than 127 can be assigned to a byte variable using a
 * type cast.  For example:
 * <ul><li>byteVariable = (byte) intValue; // intValue can be &gt; 127</li></ul>
 * If intValue is greater than 127, then byteVariable will be negative.  The
 * correct value will be extracted when it is used (by masking off the upper
 * bits).
 */
public abstract class Tuple4b implements java.io.Serializable, Cloneable {

    static final long serialVersionUID = -8226727741811898211L;

    /**
     * The first value.
     */
    public	byte	x;

    /**
     * The second value.
     */
    public	byte	y;

    /**
     * The third value.
     */
    public	byte	z;

    /**
     * The fourth value.
     */
    public	byte	w;


    /**
     * Constructs and initializes a Tuple4b from the specified four values.
     * @param b1 the first value
     * @param b2 the second value
     * @param b3 the third value
     * @param b4 the fourth value
     */
    public Tuple4b(byte b1, byte b2, byte b3, byte b4)
    {
	this.x = b1;
	this.y = b2;
	this.z = b3;
	this.w = b4;
    }


    /**
     * Constructs and initializes a Tuple4b from the array of length 4.
     * @param t the array of length 4 containing b1 b2 b3 b4 in order
     */
    public Tuple4b(byte[] t)
    {
	this.x = t[0];
	this.y = t[1];
	this.z = t[2];
	this.w = t[3];
    }


    /**
     * Constructs and initializes a Tuple4b from the specified Tuple4b.
     * @param t1 the Tuple4b containing the initialization x y z w data
     */
    public Tuple4b(Tuple4b t1)
    {
	this.x = t1.x;
	this.y = t1.y;
	this.z = t1.z;
	this.w = t1.w;
    }


    /**
     * Constructs and initializes a Tuple4b to (0,0,0,0).
     */
    public Tuple4b()
    {
	this.x = (byte) 0;
	this.y = (byte) 0;
	this.z = (byte) 0;
	this.w = (byte) 0;
    }


   /**
     * Returns a string that contains the values of this Tuple4b.
     * @return the String representation
     */
    @Override
    public String toString()
    {
        return("("  + ((int)this.x & 0xff) +
	       ", " + ((int)this.y & 0xff) +
	       ", " + ((int)this.z & 0xff) +
	       ", " + ((int)this.w & 0xff) + ")");
    }


   /**
     * Places the value of the x,y,z,w components of this Tuple4b
     * into the array of length 4.
     * @param b   array of length 4 into which the values are placed
     */
    public final void get(byte[] b)
    {
        b[0] = this.x;
        b[1] = this.y;
        b[2] = this.z;
        b[3] = this.w;
    }


   /**
     * Places the value of the x,y,z,w components of this
     * Tuple4b into the tuple t1.
     * @param t1   tuple into which the values are placed
     */
    public final void get(Tuple4b t1)
    {
       t1.x = this.x;
       t1.y = this.y;
       t1.z = this.z;
       t1.w = this.w;
   }


   /**
     * Sets the value of the data members of this tuple to the value
     * of the argument tuple t1.
     * @param t1  the source tuple
     */
    public final void set(Tuple4b t1)
    {
        this.x = t1.x;
        this.y = t1.y;
        this.z = t1.z;
        this.w = t1.w;
    }


   /**
     * Sets the value of the data members of this tuple to the value
     * of the array b of length 4.
     * @param b   the source array of length 4
     */
    public final void set(byte[] b)
    {
        this.x = b[0];
        this.y = b[1];
        this.z = b[2];
        this.w = b[3];
    }


   /**
     * Returns true if all of the data members of tuple t1 are equal to
     * the corresponding data members in this tuple.
     * @param t1  the tuple with which the comparison is made
     */
    public boolean equals(Tuple4b t1)
    {
        try {
           return(this.x == t1.x && this.y == t1.y &&
                  this.z == t1.z && this.w == t1.w);
        }
        catch (NullPointerException e2) {return false;}

    }

   /**
     * Returns true if the Object t1 is of type Tuple4b and all of the
     * data members of t1 are equal to the corresponding data members in
     * this Tuple4b.
     * @param t1  the object with which the comparison is made
     */
    @Override
    public boolean equals(Object t1)
    {
        try {
           Tuple4b t2 = (Tuple4b) t1;
           return(this.x == t2.x && this.y == t2.y &&
                  this.z == t2.z && this.w == t2.w);
        }
        catch (NullPointerException e2) {return false;}
        catch (ClassCastException   e1) {return false;}

    }


    /**
     * Returns a hash code value based on the data values in this
     * object.  Two different Tuple4b objects with identical data values
     * (i.e., Tuple4b.equals returns true) will return the same hash
     * code value.  Two objects with different data members may return the
     * same hash value, although this is not likely.
     * @return the integer hash code value
     */
    @Override
    public int hashCode() {
	return ((((int)x & 0xff) <<  0) |
		(((int)y & 0xff) <<  8) |
		(((int)z & 0xff) << 16) |
		(((int)w & 0xff) << 24));
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
	 * Get <i>x</i>, the first value.
	 *
	 * @return Returns <i>x</i>, the first value.
	 *
	 * @since vecmath 1.5
	 */
	public final byte getX() {
		return x;
	}


	/**
	 * Set <i>x</i>,  the first value.
	 *
	 * @param x the first value.
	 *
	 * @since vecmath 1.5
	 */
	public final void setX(byte x) {
		this.x = x;
	}


	/**
	 * Get <i>y</i>, the second value.
	 *
	 * @return Returns <i>y</i>, the second value.
	 *
	 * @since vecmath 1.5
	 */
	public final byte getY() {
		return y;
	}


	/**
	 * Set <i>y</i>, the second value.
	 *
	 * @param y the second value.
	 *
	 * @since vecmath 1.5
	 */
	public final void setY(byte y) {
		this.y = y;
	}

	/**
	 * Get <i>z</i>, the third value.
	 *
	 *  @return Returns <i>z</i>, the third value.
	 *
	 * @since vecmath 1.5
	 */
	public final byte getZ() {
		return z;
	}


	/**
	 * Set <i>z</i>,  the third value.
	 *
	 * @param z  the third value.
	 *
	 * @since vecmath 1.5
	 */
	public final void setZ(byte z) {
		this.z = z;
	}


	/**
	 * Get <i>w</i>, the fourth value.
	 *
	 * @return Returns <i>w</i> - the fourth value.
	 *
	 * @since vecmath 1.5
	 */
	public final byte getW() {
		return w;
	}


	/**
	 * Set <i>w</i>, the fourth value.
	 *
	 * @param w the fourth value.
	 *
	 * @since vecmath 1.5
	 */
	public final void setW(byte w) {
		this.w = w;
	}
}
