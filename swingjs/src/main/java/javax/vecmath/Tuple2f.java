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
 * A generic 2-element tuple that is represented by single-precision
 * floating point x,y coordinates.
 *
 */
public abstract class Tuple2f implements java.io.Serializable, Cloneable {

    static final long serialVersionUID = 9011180388985266884L;

    /**
     * The x coordinate.
     */
    public	float	x;

    /**
     * The y coordinate.
     */
    public	float	y;


    /**
     * Constructs and initializes a Tuple2f from the specified xy coordinates.
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public Tuple2f(float x, float y)
    {
	this.x = x;
	this.y = y;
    }


    /**
     * Constructs and initializes a Tuple2f from the specified array.
     * @param t the array of length 2 containing xy in order
     */
    public Tuple2f(float[] t)
    {
	this.x = t[0];
	this.y = t[1];
    }


    /**
     * Constructs and initializes a Tuple2f from the specified Tuple2f.
     * @param t1 the Tuple2f containing the initialization x y data
     */
    public Tuple2f(Tuple2f t1)
    {
	this.x = t1.x;
	this.y = t1.y;
    }


    /**
     * Constructs and initializes a Tuple2f from the specified Tuple2d.
     * @param t1 the Tuple2d containing the initialization x y data
     */
    public Tuple2f(Tuple2d t1)
    {
	this.x = (float) t1.x;
	this.y = (float) t1.y;
    }


    /**
     * Constructs and initializes a Tuple2f to (0,0).
     */
    public Tuple2f()
    {
	this.x = (float) 0.0;
	this.y = (float) 0.0;
    }


    /**
     * Sets the value of this tuple to the specified xy coordinates.
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public final void set(float x, float y)
    {
	this.x = x;
	this.y = y;
    }


    /**
     * Sets the value of this tuple from the 2 values specified in
     * the array.
     * @param t the array of length 2 containing xy in order
     */
    public final void set(float[] t)
    {
	this.x = t[0];
	this.y = t[1];
    }


    /**
     * Sets the value of this tuple to the value of the Tuple2f argument.
     * @param t1 the tuple to be copied
     */
    public final void set(Tuple2f t1)
    {
	this.x = t1.x;
	this.y = t1.y;
    }


    /**
     * Sets the value of this tuple to the value of the Tuple2d argument.
     * @param t1 the tuple to be copied
     */
    public final void set(Tuple2d t1)
    {
	this.x = (float) t1.x;
	this.y = (float) t1.y;
    }


   /**
    *  Copies the value of the elements of this tuple into the array t.
    *  @param t the array that will contain the values of the vector
    */
   public final void get(float[] t)
    {
        t[0] = this.x;
        t[1] = this.y;
    }


    /**
     * Sets the value of this tuple to the vector sum of tuples t1 and t2.
     * @param t1 the first tuple
     * @param t2 the second tuple
     */
    public final void add(Tuple2f t1, Tuple2f t2)
    {
	this.x = t1.x + t2.x;
	this.y = t1.y + t2.y;
    }


    /**
     * Sets the value of this tuple to the vector sum of itself and tuple t1.
     * @param t1 the other tuple
     */
    public final void add(Tuple2f t1)
    {
        this.x += t1.x;
        this.y += t1.y;
    }


    /**
     * Sets the value of this tuple to the vector difference of
     * tuple t1 and t2 (this = t1 - t2).
     * @param t1 the first tuple
     * @param t2 the second tuple
     */
    public final void sub(Tuple2f t1, Tuple2f t2)
    {
        this.x = t1.x - t2.x;
        this.y = t1.y - t2.y;
    }


    /**
     * Sets the value of this tuple to the vector difference of
     * itself and tuple t1 (this = this - t1).
     * @param t1 the other tuple
     */
    public final void sub(Tuple2f t1)
    {
        this.x -= t1.x;
        this.y -= t1.y;
    }


    /**
     * Sets the value of this tuple to the negation of tuple t1.
     * @param t1 the source tuple
     */
    public final void negate(Tuple2f t1)
    {
	this.x = -t1.x;
	this.y = -t1.y;
    }


    /**
     * Negates the value of this vector in place.
     */
    public final void negate()
    {
	this.x = -this.x;
	this.y = -this.y;
    }


    /**
     * Sets the value of this tuple to the scalar multiplication
     * of tuple t1.
     * @param s the scalar value
     * @param t1 the source tuple
     */
    public final void scale(float s, Tuple2f t1)
    {
	this.x = s*t1.x;
	this.y = s*t1.y;
    }


    /**
     * Sets the value of this tuple to the scalar multiplication
     * of itself.
     * @param s the scalar value
     */
    public final void scale(float s)
    {
	this.x *= s;
	this.y *= s;
    }


    /**
     * Sets the value of this tuple to the scalar multiplication
     * of tuple t1 and then adds tuple t2 (this = s*t1 + t2).
     * @param s the scalar value
     * @param t1 the tuple to be multipled
     * @param t2 the tuple to be added
     */
    public final void scaleAdd(float s, Tuple2f t1, Tuple2f t2)
    {
        this.x = s*t1.x + t2.x;
        this.y = s*t1.y + t2.y;
    }


    /**
     * Sets the value of this tuple to the scalar multiplication
     * of itself and then adds tuple t1 (this = s*this + t1).
     * @param s the scalar value
     * @param t1 the tuple to be added
     */
    public final void scaleAdd(float s, Tuple2f t1)
    {
        this.x = s*this.x + t1.x;
        this.y = s*this.y + t1.y;
    }



    /**
     * Returns a hash code value based on the data values in this
     * object.  Two different Tuple2f objects with identical data values
     * (i.e., Tuple2f.equals returns true) will return the same hash
     * code value.  Two objects with different data members may return the
     * same hash value, although this is not likely.
     * @return the integer hash code value
     */
    @Override
    public int hashCode() {
	long bits = 1L;
	bits = VecMathUtil.hashFloatBits(bits, x);
	bits = VecMathUtil.hashFloatBits(bits, y);
	return VecMathUtil.hashFinish(bits);
    }


   /**
     * Returns true if all of the data members of Tuple2f t1 are
     * equal to the corresponding data members in this Tuple2f.
     * @param t1  the vector with which the comparison is made
     * @return  true or false
     */
    public boolean equals(Tuple2f t1)
    {
        try {
           return(this.x == t1.x && this.y == t1.y);
        }
        catch (NullPointerException e2) {return false;}

    }

   /**
     * Returns true if the Object t1 is of type Tuple2f and all of the
     * data members of t1 are equal to the corresponding data members in
     * this Tuple2f.
     * @param t1  the object with which the comparison is made
     * @return  true or false
     */
    @Override
    public boolean equals(Object t1)
    {
        try {
           Tuple2f t2 = (Tuple2f) t1;
           return(this.x == t2.x && this.y == t2.y);
        }
        catch (NullPointerException e2) {return false;}
        catch (ClassCastException   e1) {return false;}

    }

   /**
     * Returns true if the L-infinite distance between this tuple
     * and tuple t1 is less than or equal to the epsilon parameter,
     * otherwise returns false.  The L-infinite
     * distance is equal to MAX[abs(x1-x2), abs(y1-y2)].
     * @param t1  the tuple to be compared to this tuple
     * @param epsilon  the threshold value
     * @return  true or false
     */
    public boolean epsilonEquals(Tuple2f t1, float epsilon)
    {
       float diff;

       diff = x - t1.x;
       if(Float.isNaN(diff)) return false;
       if((diff<0?-diff:diff) > epsilon) return false;

       diff = y - t1.y;
       if(Float.isNaN(diff)) return false;
       if((diff<0?-diff:diff) > epsilon) return false;

       return true;
    }

   /**
     * Returns a string that contains the values of this Tuple2f.
     * The form is (x,y).
     * @return the String representation
     */
   @Override
   public String toString()
   {
        return("(" + this.x + ", " + this.y + ")");
   }


  /**
    *  Clamps the tuple parameter to the range [low, high] and
    *  places the values into this tuple.
    *  @param min   the lowest value in the tuple after clamping
    *  @param max  the highest value in the tuple after clamping
    *  @param t   the source tuple, which will not be modified
    */
   public final void clamp(float min, float max, Tuple2f t)
   {
        if( t.x > max ) {
          x = max;
        } else if( t.x < min ){
          x = min;
        } else {
          x = t.x;
        }

        if( t.y > max ) {
          y = max;
        } else if( t.y < min ){
          y = min;
        } else {
          y = t.y;
        }

   }


  /**
    *  Clamps the minimum value of the tuple parameter to the min
    *  parameter and places the values into this tuple.
    *  @param min   the lowest value in the tuple after clamping
    *  @param t   the source tuple, which will not be modified
    */
   public final void clampMin(float min, Tuple2f t)
   {
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

   }


  /**
    *  Clamps the maximum value of the tuple parameter to the max
    *  parameter and places the values into this tuple.
    *  @param max   the highest value in the tuple after clamping
    *  @param t   the source tuple, which will not be modified
    */
   public final void clampMax(float max, Tuple2f t)
   {
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

   }


  /**
    *  Sets each component of the tuple parameter to its absolute
    *  value and places the modified values into this tuple.
    *  @param t   the source tuple, which will not be modified
    */
  public final void absolute(Tuple2f t)
  {
       x = Math.abs(t.x);
       y = Math.abs(t.y);
  }



  /**
    *  Clamps this tuple to the range [low, high].
    *  @param min  the lowest value in this tuple after clamping
    *  @param max  the highest value in this tuple after clamping
    */
   public final void clamp(float min, float max)
   {
        if( x > max ) {
          x = max;
        } else if( x < min ){
          x = min;
        }

        if( y > max ) {
          y = max;
        } else if( y < min ){
          y = min;
        }

   }


  /**
    *  Clamps the minimum value of this tuple to the min parameter.
    *  @param min   the lowest value in this tuple after clamping
    */
   public final void clampMin(float min)
   {
      if( x < min ) x=min;
      if( y < min ) y=min;
   }


  /**
    *  Clamps the maximum value of this tuple to the max parameter.
    *  @param max   the highest value in the tuple after clamping
    */
   public final void clampMax(float max)
   {
      if( x > max ) x=max;
      if( y > max ) y=max;
   }


  /**
    *  Sets each component of this tuple to its absolute value.
    */
  public final void absolute()
  {
     x = Math.abs(x);
     y = Math.abs(y);
  }


  /**
    *  Linearly interpolates between tuples t1 and t2 and places the
    *  result into this tuple:  this = (1-alpha)*t1 + alpha*t2.
    *  @param t1  the first tuple
    *  @param t2  the second tuple
    *  @param alpha  the alpha interpolation parameter
    */
  public final void interpolate(Tuple2f t1, Tuple2f t2, float alpha)
  {
           this.x = (1-alpha)*t1.x + alpha*t2.x;
           this.y = (1-alpha)*t1.y + alpha*t2.y;

  }


  /**
    *  Linearly interpolates between this tuple and tuple t1 and
    *  places the result into this tuple:  this = (1-alpha)*this + alpha*t1.
    *  @param t1  the first tuple
    *  @param alpha  the alpha interpolation parameter
    */
  public final void interpolate(Tuple2f t1, float alpha)
  {

     this.x = (1-alpha)*this.x + alpha*t1.x;
     this.y = (1-alpha)*this.y + alpha*t1.y;

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
	 * @return  the <i>x</i> coordinate.
	 *
	 * @since vecmath 1.5
	 */
	public final float getX() {
		return x;
	}


	/**
	 * Set the <i>x</i> coordinate.
	 *
	 * @param x  value to <i>x</i> coordinate.
	 *
	 * @since vecmath 1.5
	 */
	public final void setX(float x) {
		this.x = x;
	}


	/**
	 * Get the <i>y</i> coordinate.
	 *
	 * @return  the <i>y</i> coordinate.
	 *
	 * @since vecmath 1.5
	 */
	public final float getY() {
		return y;
	}


	/**
	 * Set the <i>y</i> coordinate.
	 *
	 * @param y value to <i>y</i> coordinate.
	 *
	 * @since vecmath 1.5
	 */
	public final void setY(float y) {
		this.y = y;
	}
}
