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
 * A four-element axis angle represented by double-precision floating point
 * x,y,z,angle components.  An axis angle is a rotation of angle (radians)
 * about the vector (x,y,z).
 *
 */
public class AxisAngle4d implements java.io.Serializable, Cloneable {


    // Compatible with 1.1
    static final long serialVersionUID = 3644296204459140589L;

    /**
     * The x coordinate.
     */
    public	double	x;

    /**
     * The y coordinate.
     */
    public	double	y;

    /**
     * The z coordinate.
     */
    public	double	z;

    /**
     * The angle of rotation in radians.
     */
    public	double	angle;

    // Fixed to issue 538
    final static double EPS = 1.0e-12;

    /**
     * Constructs and initializes an AxisAngle4d from the specified
     * x, y, z, and angle.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @param angle the angle of rotation in radians
     */
    public AxisAngle4d(double x, double y, double z, double angle)
    {
	this.x = x;
	this.y = y;
	this.z = z;
	this.angle = angle;
    }


    /**
     * Constructs and initializes an AxisAngle4d from the components
     * contained in the array.
     * @param a  the array of length 4 containing x,y,z,angle in order
     */
        public AxisAngle4d(double[] a)
     {
         this.x = a[0];
         this.y = a[1];
         this.z = a[2];
         this.angle = a[3];
     }
    /**
     * Constructs and initializes an AxisAngle4d from the specified AxisAngle4d.
     * @param a1 the AxisAngle4d containing the initialization x y z angle data
     */
    public AxisAngle4d(AxisAngle4d a1)
    {
	this.x = a1.x;
	this.y = a1.y;
	this.z = a1.z;
	this.angle = a1.angle;
    }


    /**
     * Constructs and initializes an AxisAngle4d from the specified
     * AxisAngle4f.
     * @param a1 the AxisAngle4f containing the initialization x y z angle data
     */
    public AxisAngle4d(AxisAngle4f a1)
    {
	this.x = a1.x;
	this.y = a1.y;
	this.z = a1.z;
	this.angle = a1.angle;
    }


    /**
     * Constructs and initializes an AxisAngle4d from the specified
     * axis and angle.
     * @param axis the axis
     * @param angle the angle of rotation in radian
     *
     * @since vecmath 1.2
     */
    public AxisAngle4d(Vector3d axis, double angle) {
	this.x = axis.x;
	this.y = axis.y;
	this.z = axis.z;
	this.angle = angle;
    }


    /**
     * Constructs and initializes an AxisAngle4d to (0,0,1,0).
     */
    public AxisAngle4d()
    {
	this.x = 0.0;
	this.y = 0.0;
	this.z = 1.0;
	this.angle = 0.0;
    }


    /**
     * Sets the value of this axis angle to the specified x,y,z,angle.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @param angle  the angle of rotation in radians
     */
    public final void set(double x, double y, double z, double angle)
    {
	this.x = x;
	this.y = y;
	this.z = z;
	this.angle = angle;
    }


    /**
     * Sets the value of this axis angle to the specified x,y,z,angle.
     * @param a  the array of length 4 containing x,y,z,angle in order
     */
    public final void set(double[] a)
    {
	this.x = a[0];
	this.y = a[1];
	this.z = a[2];
	this.angle = a[3];
    }


    /**
     * Sets the value of this axis angle to the value of axis angle a1.
     * @param a1 the axis angle to be copied
     */
    public final void set(AxisAngle4d a1)
    {
	this.x = a1.x;
	this.y = a1.y;
	this.z = a1.z;
	this.angle = a1.angle;
    }


    /**
     * Sets the value of this axis angle to the value of axis angle a1.
     * @param a1 the axis angle to be copied
     */
    public final void set(AxisAngle4f a1)
    {
	this.x = a1.x;
	this.y = a1.y;
	this.z = a1.z;
	this.angle = a1.angle;
    }


    /**
     * Sets the value of this AxisAngle4d to the specified
     * axis and angle.
     * @param axis the axis
     * @param angle the angle of rotation in radians
     *
     * @since vecmath 1.2
     */
    public final void set(Vector3d axis, double angle) {
	this.x = axis.x;
	this.y = axis.y;
	this.z = axis.z;
	this.angle = angle;
    }


    /**
     * Gets the value of this axis angle and places it into the array a of
     * length four in x,y,z,angle order.
     * @param a  the array of length four
     */
    public final void get(double[] a)
    {
        a[0] = this.x;
        a[1] = this.y;
        a[2] = this.z;
        a[3] = this.angle;
    }


    /**
     * Sets the value of this axis-angle to the rotational component of
     * the passed matrix.
     * If the specified matrix has no rotational component, the value
     * of this AxisAngle4d is set to an angle of 0 about an axis of (0,1,0).
     *
     * @param m1 the matrix4f
     */
    public final void set(Matrix4f m1)
    {
        Matrix3d m3d = new Matrix3d();

        m1.get(m3d);

        x = (float)(m3d.m21 - m3d.m12);
        y = (float)(m3d.m02 - m3d.m20);
        z = (float)(m3d.m10 - m3d.m01);
        double mag = x*x + y*y + z*z;

	if (mag > EPS ) {
	    mag = Math.sqrt(mag);
	    double sin = 0.5*mag;
	    double cos = 0.5*(m3d.m00 + m3d.m11 + m3d.m22 - 1.0);

	    angle = (float)Math.atan2(sin, cos);

	    double invMag = 1.0/mag;
	    x = x*invMag;
	    y = y*invMag;
	    z = z*invMag;
	} else {
	    x = 0.0f;
	    y = 1.0f;
	    z = 0.0f;
	    angle = 0.0f;
	}
    }


    /**
     * Sets the value of this axis-angle to the rotational component of
     * the passed matrix.
     * If the specified matrix has no rotational component, the value
     * of this AxisAngle4d is set to an angle of 0 about an axis of (0,1,0).
     *
     * @param m1 the matrix4d
     */
    public final void set(Matrix4d m1)
    {
        Matrix3d m3d = new Matrix3d();

        m1.get(m3d);

        x = (float)(m3d.m21 - m3d.m12);
        y = (float)(m3d.m02 - m3d.m20);
        z = (float)(m3d.m10 - m3d.m01);

        double mag = x*x + y*y + z*z;

	if (mag > EPS ) {
	    mag = Math.sqrt(mag);

	    double sin = 0.5*mag;
	    double cos = 0.5*(m3d.m00 + m3d.m11 + m3d.m22 - 1.0);
	    angle = (float)Math.atan2(sin, cos);

	    double invMag = 1.0/mag;
	    x = x*invMag;
	    y = y*invMag;
	    z = z*invMag;
	} else {
	    x = 0.0f;
	    y = 1.0f;
	    z = 0.0f;
	    angle = 0.0f;
	}
    }


    /**
     * Sets the value of this axis-angle to the rotational component of
     * the passed matrix.
     * If the specified matrix has no rotational component, the value
     * of this AxisAngle4d is set to an angle of 0 about an axis of (0,1,0).
     * @param m1 the matrix3f
     */
    public final void set(Matrix3f m1)
    {
        x = (float)(m1.m21 - m1.m12);
        y = (float)(m1.m02 - m1.m20);
        z = (float)(m1.m10 - m1.m01);
        double mag = x*x + y*y + z*z;

	if (mag > EPS ) {
	    mag = Math.sqrt(mag);

	    double sin = 0.5*mag;
	    double cos = 0.5*(m1.m00 + m1.m11 + m1.m22 - 1.0);
	    angle = (float)Math.atan2(sin, cos);

	    double invMag = 1.0/mag;
	    x = x*invMag;
	    y = y*invMag;
	    z = z*invMag;
	} else {
	    x = 0.0f;
	    y = 1.0f;
	    z = 0.0f;
	    angle = 0.0f;
	}
    }


    /**
     * Sets the value of this axis-angle to the rotational component of
     * the passed matrix.
     * If the specified matrix has no rotational component, the value
     * of this AxisAngle4d is set to an angle of 0 about an axis of (0,1,0).
     * @param m1 the matrix3d
     */
    public final void set(Matrix3d m1)
    {
        x = (float)(m1.m21 - m1.m12);
        y = (float)(m1.m02 - m1.m20);
        z = (float)(m1.m10 - m1.m01);

        double mag = x*x + y*y + z*z;

	if (mag > EPS ) {
	    mag = Math.sqrt(mag);

	    double sin = 0.5*mag;
	    double cos = 0.5*(m1.m00 + m1.m11 + m1.m22 - 1.0);

	    angle = (float)Math.atan2(sin, cos);

	    double invMag = 1.0/mag;
	    x = x*invMag;
	    y = y*invMag;
	    z = z*invMag;
	} else {
	    x = 0.0f;
	    y = 1.0f;
	    z = 0.0f;
	    angle = 0.0f;
	}

    }



    /**
      * Sets the value of this axis-angle to the rotational equivalent
      * of the passed quaternion.
      * If the specified quaternion has no rotational component, the value
      * of this AxisAngle4d is set to an angle of 0 about an axis of (0,1,0).
      * @param q1  the Quat4f
      */
    public final void set(Quat4f q1)
    {
        double mag = q1.x*q1.x + q1.y*q1.y + q1.z*q1.z;

        if( mag > EPS ) {
	    mag = Math.sqrt(mag);
	    double invMag = 1.0/mag;

	    x = q1.x*invMag;
	    y = q1.y*invMag;
	    z = q1.z*invMag;
	    angle = 2.0*Math.atan2(mag, q1.w);
	} else {
           x = 0.0f;
           y = 1.0f;
           z = 0.0f;
	   angle = 0.0f;
        }
  }


    /**
      * Sets the value of this axis-angle to the rotational equivalent
      * of the passed quaternion.
      * If the specified quaternion has no rotational component, the value
      * of this AxisAngle4d is set to an angle of 0 about an axis of (0,1,0).
      * @param q1  the Quat4d
      */
    public final void set(Quat4d q1)
    {
	double mag = q1.x*q1.x + q1.y*q1.y + q1.z*q1.z;

	if ( mag > EPS ) {
	    mag = Math.sqrt(mag);
	    double invMag = 1.0/mag;

	    x = q1.x*invMag;
	    y = q1.y*invMag;
	    z = q1.z*invMag;
	    angle = 2.0*Math.atan2(mag, q1.w);
        } else {
	    x = 0.0f;
	    y = 1.0f;
	    z = 0.0f;
	    angle = 0f;
	}
    }


   /**
     * Returns a string that contains the values of this AxisAngle4d.
     * The form is (x,y,z,angle).
     * @return the String representation
     */
    @Override
    public String toString() {
	return "(" + this.x + ", " + this.y + ", " + this.z + ", " + this.angle + ")";
    }


   /**
     * Returns true if all of the data members of AxisAngle4d a1 are
     * equal to the corresponding data members in this AxisAngle4d.
     * @param a1  the axis-angle with which the comparison is made
     * @return  true or false
     */
    public boolean equals(AxisAngle4d a1)
    {
        try {
           return(this.x == a1.x && this.y == a1.y && this.z == a1.z
            && this.angle == a1.angle);
        }
        catch (NullPointerException e2) {return false;}

    }
    /**
     * Returns true if the Object o1 is of type AxisAngle4d and all of the
     * data members of o1 are equal to the corresponding data members in
     * this AxisAngle4d.
     * @param o1  the object with which the comparison is made
     * @return  true or false
      */
    @Override
    public boolean equals(Object o1)
    {
        try {
           AxisAngle4d a2 = (AxisAngle4d) o1;
           return(this.x == a2.x && this.y == a2.y && this.z == a2.z
            && this.angle == a2.angle);
        }
        catch (NullPointerException e2) {return false;}
        catch (ClassCastException   e1) {return false;}

    }


   /**
     * Returns true if the L-infinite distance between this axis-angle
     * and axis-angle a1 is less than or equal to the epsilon parameter,
     * otherwise returns false.  The L-infinite
     * distance is equal to
     * MAX[abs(x1-x2), abs(y1-y2), abs(z1-z2), abs(angle1-angle2)].
     * @param a1  the axis-angle to be compared to this axis-angle
     * @param epsilon  the threshold value
     */
    public boolean epsilonEquals(AxisAngle4d a1, double epsilon)
    {
       double diff;

       diff = x - a1.x;
       if((diff<0?-diff:diff) > epsilon) return false;

       diff = y - a1.y;
       if((diff<0?-diff:diff) > epsilon) return false;

       diff = z - a1.z;
       if((diff<0?-diff:diff) > epsilon) return false;

       diff = angle - a1.angle;
       if((diff<0?-diff:diff) > epsilon) return false;

       return true;
    }


    /**
     * Returns a hash code value based on the data values in this
     * object.  Two different AxisAngle4d objects with identical data values
     * (i.e., AxisAngle4d.equals returns true) will return the same hash
     * code value.  Two objects with different data members may return the
     * same hash value, although this is not likely.
     * @return the integer hash code value
     */
    @Override
    public int hashCode() {
	long bits = 1L;
	bits = VecMathUtil.hashDoubleBits(bits, x);
	bits = VecMathUtil.hashDoubleBits(bits, y);
	bits = VecMathUtil.hashDoubleBits(bits, z);
	bits = VecMathUtil.hashDoubleBits(bits, angle);
	return VecMathUtil.hashFinish(bits);
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
	 * Get the axis angle, in radians.<br>
	 * An axis angle is a rotation angle about the vector (x,y,z).
     *
	 * @return the angle, in radians.
	 *
	 * @since vecmath 1.5
	 */
	public final double getAngle() {
		return angle;
	}


	/**
	 * Set the axis angle, in radians.<br>
	 * An axis angle is a rotation angle about the vector (x,y,z).
	 *
	 * @param angle The angle to set, in radians.
	 *
	 * @since vecmath 1.5
	 */
	public final void setAngle(double angle) {
		this.angle = angle;
	}


	/**
	 * Get value of <i>x</i> coordinate.
	 *
	 * @return the <i>x</i> coordinate.
	 *
	 * @since vecmath 1.5
	 */
	public double getX() {
		return x;
	}


	/**
	 * Set a new value for <i>x</i> coordinate.
	 *
	 * @param x the <i>x</i> coordinate.
	 *
	 * @since vecmath 1.5
	 */
	public final void setX(double x) {
		this.x = x;
	}


	/**
	 * Get value of <i>y</i> coordinate.
	 *
	 * @return the <i>y</i> coordinate.
	 *
	 * @since vecmath 1.5
	 */
	public  final double getY() {
		return y;
	}


	/**
	 * Set a new value for <i>y</i> coordinate.
	 *
	 * @param y the <i>y</i> coordinate.
	 *
	 * @since vecmath 1.5
	 */
	public final void setY(double y) {
		this.y = y;
	}


	/**
	 * Get  value of <i>z</i> coordinate.
	 *
	 * @return  the <i>z</i> coordinate.
	 *
	 * @since vecmath 1.5
	 */
	public double getZ() {
		return z;
	}


	/**
	 * Set a new value for <i>z</i> coordinate.
	 *
	 * @param z the <i>z</i> coordinate.
	 *
	 * @since vecmath 1.5
	 */
	public final void setZ(double z) {
		this.z = z;
	}

}
