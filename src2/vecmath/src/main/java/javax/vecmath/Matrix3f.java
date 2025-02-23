/*
 * Copyright 1996-2008 Sun Microsystems, Inc.  All Rights Reserved.
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
 * A single precision floating point 3 by 3 matrix.
 * Primarily to support 3D rotations.
 *
 */
public class Matrix3f implements java.io.Serializable, Cloneable {

  // Compatible with 1.1
  static final long serialVersionUID = 329697160112089834L;

  /**
    * The first matrix element in the first row.
    */
    public	float	m00;

  /**
    * The second matrix element in the first row.
    */
    public	float	m01;

  /**
    * The third matrix element in the first row.
    */
    public	float	m02;

  /**
    * The first matrix element in the second row.
    */
    public	float	m10;

  /**
    * The second matrix element in the second row.
    */
    public	float	m11;

  /**
    * The third matrix element in the second row.
    */
    public	float	m12;

  /**
    * The first matrix element in the third row.
    */
    public	float	m20;

  /**
    * The second matrix element in the third row.
    */
    public	float	m21;

  /**
    * The third matrix element in the third row.
    */
    public	float	m22;
    /*
    double[]    tmp       = new double[9];  // scratch matrix
    double[]    tmp_rot   = new double[9];  // scratch matrix
    double[]    tmp_scale = new double[3];  // scratch matrix
    */
    private static final double EPS = 1.0E-8;



    /**
     * Constructs and initializes a Matrix3f from the specified nine values.
     * @param m00 the [0][0] element
     * @param m01 the [0][1] element
     * @param m02 the [0][2] element
     * @param m10 the [1][0] element
     * @param m11 the [1][1] element
     * @param m12 the [1][2] element
     * @param m20 the [2][0] element
     * @param m21 the [2][1] element
     * @param m22 the [2][2] element
     */
    public Matrix3f(float m00, float m01, float m02,
		    float m10, float m11, float m12,
		    float m20, float m21, float m22)
    {
	this.m00 = m00;
	this.m01 = m01;
	this.m02 = m02;

	this.m10 = m10;
	this.m11 = m11;
	this.m12 = m12;

	this.m20 = m20;
	this.m21 = m21;
	this.m22 = m22;

    }

    /**
     * Constructs and initializes a Matrix3f from the specified
     * nine-element array.   this.m00 =v[0], this.m01=v[1], etc.
     * @param v the array of length 9 containing in order
     */
    public Matrix3f(float[] v)
    {
	this.m00 = v[ 0];
	this.m01 = v[ 1];
	this.m02 = v[ 2];

	this.m10 = v[ 3];
	this.m11 = v[ 4];
	this.m12 = v[ 5];

	this.m20 = v[ 6];
	this.m21 = v[ 7];
	this.m22 = v[ 8];

    }

   /**
     *  Constructs a new matrix with the same values as the
     *  Matrix3d parameter.
     *  @param m1  the source matrix
     */
   public Matrix3f(Matrix3d m1)
   {
        this.m00 = (float)m1.m00;
        this.m01 = (float)m1.m01;
        this.m02 = (float)m1.m02;

        this.m10 = (float)m1.m10;
        this.m11 = (float)m1.m11;
        this.m12 = (float)m1.m12;

        this.m20 = (float)m1.m20;
        this.m21 = (float)m1.m21;
        this.m22 = (float)m1.m22;

   }


   /**
     *  Constructs a new matrix with the same values as the
     *  Matrix3f parameter.
     *  @param m1  the source matrix
     */
   public Matrix3f(Matrix3f m1)
   {
        this.m00 = m1.m00;
        this.m01 = m1.m01;
        this.m02 = m1.m02;

        this.m10 = m1.m10;
        this.m11 = m1.m11;
        this.m12 = m1.m12;

        this.m20 = m1.m20;
        this.m21 = m1.m21;
        this.m22 = m1.m22;

   }


    /**
     * Constructs and initializes a Matrix3f to all zeros.
     */
    public Matrix3f()
    {
	this.m00 = (float) 0.0;
	this.m01 = (float) 0.0;
	this.m02 = (float) 0.0;

	this.m10 = (float) 0.0;
	this.m11 = (float) 0.0;
	this.m12 = (float) 0.0;

	this.m20 = (float) 0.0;
	this.m21 = (float) 0.0;
	this.m22 = (float) 0.0;

    }

   /**
     * Returns a string that contains the values of this Matrix3f.
     * @return the String representation
     */
    @Override
    public String toString() {
      return
	this.m00 + ", " + this.m01 + ", " + this.m02 + "\n" +
	this.m10 + ", " + this.m11 + ", " + this.m12 + "\n" +
	this.m20 + ", " + this.m21 + ", " + this.m22 + "\n";
    }

    /**
     * Sets this Matrix3f to identity.
     */
    public final void setIdentity()
    {
	this.m00 = (float) 1.0;
	this.m01 = (float) 0.0;
	this.m02 = (float) 0.0;

	this.m10 = (float) 0.0;
	this.m11 = (float) 1.0;
	this.m12 = (float) 0.0;

	this.m20 = (float) 0.0;
	this.m21 = (float) 0.0;
	this.m22 = (float) 1.0;
    }

   /**
     * Sets the scale component of the current matrix by factoring
     * out the current scale (by doing an SVD) and multiplying by
     * the new scale.
     * @param scale  the new scale amount
     */
    public final void setScale(float scale)
    {
	double[]    tmp_rot = new double[9];  // scratch matrix
	double[]    tmp_scale = new double[3];  // scratch matrix

	getScaleRotate( tmp_scale, tmp_rot );

        this.m00 = (float)(tmp_rot[0] * scale);
        this.m01 = (float)(tmp_rot[1] * scale);
        this.m02 = (float)(tmp_rot[2] * scale);

        this.m10 = (float)(tmp_rot[3] * scale);
        this.m11 = (float)(tmp_rot[4] * scale);
        this.m12 = (float)(tmp_rot[5] * scale);

        this.m20 = (float)(tmp_rot[6] * scale);
        this.m21 = (float)(tmp_rot[7] * scale);
        this.m22 = (float)(tmp_rot[8] * scale);

    }

    /**
     * Sets the specified element of this matrix3f to the value provided.
     * @param row the row number to be modified (zero indexed)
     * @param column the column number to be modified (zero indexed)
     * @param value the new value
     */
    public final void setElement(int row, int column, float value)
    {
	switch (row)
	  {
	  case 0:
	    switch(column)
	      {
	      case 0:
		this.m00 = value;
		break;
	      case 1:
		this.m01 = value;
		break;
	      case 2:
		this.m02 = value;
		break;
	      default:
		throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f0"));
	      }
	    break;

	  case 1:
	    switch(column)
	      {
	      case 0:
		this.m10 = value;
		break;
	      case 1:
		this.m11 = value;
		break;
	      case 2:
		this.m12 = value;
		break;
	      default:
		throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f0"));
	      }
	    break;

	  case 2:
	    switch(column)
	      {
	      case 0:
		this.m20 = value;
		break;
	      case 1:
		this.m21 = value;
		break;
	      case 2:
		this.m22 = value;
		break;
	      default:

		throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f0"));
	      }
	    break;

	  default:
		throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f0"));
	  }
    }

    /**
     * Copies the matrix values in the specified row into the vector parameter.
     * @param row  the matrix row
     * @param v    the vector into which the matrix row values will be copied
     */
    public final void getRow(int row, Vector3f v) {
         if( row == 0 ) {
           v.x = m00;
           v.y = m01;
           v.z = m02;
        } else if(row == 1) {
           v.x = m10;
           v.y = m11;
           v.z = m12;
        } else if(row == 2) {
           v.x = m20;
           v.y = m21;
           v.z = m22;
        } else {
          throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f1"));
        }

    }

    /**
     * Copies the matrix values in the specified row into the array parameter.
     * @param row  the matrix row
     * @param v    the array into which the matrix row values will be copied
     */
    public final void getRow(int row, float v[]) {
        if( row == 0 ) {
           v[0] = m00;
           v[1] = m01;
           v[2] = m02;
        } else if(row == 1) {
           v[0] = m10;
           v[1] = m11;
           v[2] = m12;
        } else if(row == 2) {
           v[0] = m20;
           v[1] = m21;
           v[2] = m22;
        } else {
          throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f1"));
        }

    }

    /**
     * Copies the matrix values in the specified column into the vector
     * parameter.
     * @param column  the matrix column
     * @param v    the vector into which the matrix row values will be copied
     */
    public final void getColumn(int column, Vector3f v) {
        if( column == 0 ) {
           v.x = m00;
           v.y = m10;
           v.z = m20;
        } else if(column == 1) {
           v.x = m01;
           v.y = m11;
           v.z = m21;
        }else if(column == 2){
           v.x = m02;
           v.y = m12;
           v.z = m22;
        } else {
           throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f3"));
        }

    }

    /**
     * Copies the matrix values in the specified column into the array
     * parameter.
     * @param column  the matrix column
     * @param v    the array into which the matrix row values will be copied
     */
    public final void getColumn(int column, float v[]) {
        if( column == 0 ) {
           v[0] = m00;
           v[1] = m10;
           v[2] = m20;
        } else if(column == 1) {
           v[0] = m01;
           v[1] = m11;
           v[2] = m21;
        }else if(column == 2) {
           v[0] = m02;
           v[1] = m12;
           v[2] = m22;
        }else {
          throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f3"));
        }
    }

    /**
     * Retrieves the value at the specified row and column of this
     * matrix.
     * @param row the row number to be retrieved (zero indexed)
     * @param column the column number to be retrieved (zero indexed)
     * @return the value at the indexed element.
     */
    public final float getElement(int row, int column)
    {
	switch (row)
	  {
	  case 0:
	    switch(column)
	      {
	      case 0:
		return(this.m00);
	      case 1:
		return(this.m01);
	      case 2:
		return(this.m02);
	      default:
                break;
	      }
	    break;
	  case 1:
	    switch(column)
	      {
	      case 0:
		return(this.m10);
	      case 1:
		return(this.m11);
	      case 2:
		return(this.m12);
	      default:
                break;
	      }
	    break;

	  case 2:
	    switch(column)
	      {
	      case 0:
		return(this.m20);
	      case 1:
		return(this.m21);
	      case 2:
		return(this.m22);
	      default:
                break;
	      }
	    break;

	  default:
            break;
	  }
       throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f5"));
    }

    /**
     * Sets the specified row of this matrix3f to the three values provided.
     * @param row the row number to be modified (zero indexed)
     * @param x the first column element
     * @param y the second column element
     * @param z the third column element
     */
    public final void setRow(int row, float x, float y, float z)
    {
	switch (row) {
	case 0:
	    this.m00 = x;
	    this.m01 = y;
	    this.m02 = z;
	    break;

	case 1:
	    this.m10 = x;
	    this.m11 = y;
	    this.m12 = z;
	    break;

	case 2:
	    this.m20 = x;
	    this.m21 = y;
	    this.m22 = z;
	    break;

	default:
	  throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f6"));
	}
    }

    /**
     * Sets the specified row of this matrix3f to the Vector provided.
     * @param row the row number to be modified (zero indexed)
     * @param v the replacement row
     */
    public final void setRow(int row, Vector3f v)
    {
	switch (row) {
	case 0:
	    this.m00 = v.x;
	    this.m01 = v.y;
	    this.m02 = v.z;
	    break;

	case 1:
	    this.m10 = v.x;
	    this.m11 = v.y;
	    this.m12 = v.z;
	    break;

	case 2:
	    this.m20 = v.x;
	    this.m21 = v.y;
	    this.m22 = v.z;
	    break;

	default:
	  throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f6"));
	}
    }

    /**
     * Sets the specified row of this matrix3f to the three values provided.
     * @param row the row number to be modified (zero indexed)
     * @param v the replacement row
     */
    public final void setRow(int row, float v[])
    {
	switch (row) {
	case 0:
	    this.m00 = v[0];
	    this.m01 = v[1];
	    this.m02 = v[2];
	    break;

	case 1:
	    this.m10 = v[0];
	    this.m11 = v[1];
	    this.m12 = v[2];
	    break;

	case 2:
	    this.m20 = v[0];
	    this.m21 = v[1];
	    this.m22 = v[2];
	    break;

	default:
	    throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f6"));
	}
    }

    /**
     * Sets the specified column of this matrix3f to the three values provided.
     * @param column the column number to be modified (zero indexed)
     * @param x the first row element
     * @param y the second row element
     * @param z the third row element
     */
    public final void setColumn(int column, float x, float y, float z)
    {
	switch (column) {
	case 0:
	    this.m00 = x;
	    this.m10 = y;
	    this.m20 = z;
	    break;

	case 1:
	    this.m01 = x;
	    this.m11 = y;
	    this.m21 = z;
	    break;

	case 2:
	    this.m02 = x;
	    this.m12 = y;
	    this.m22 = z;
	    break;

	default:
	    throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f9"));
	}
    }

    /**
     * Sets the specified column of this matrix3f to the vector provided.
     * @param column the column number to be modified (zero indexed)
     * @param v the replacement column
     */
    public final void setColumn(int column, Vector3f v)
    {
	switch (column) {
	case 0:
	    this.m00 = v.x;
	    this.m10 = v.y;
	    this.m20 = v.z;
	    break;

	case 1:
	    this.m01 = v.x;
	    this.m11 = v.y;
	    this.m21 = v.z;
	    break;

	case 2:
	    this.m02 = v.x;
	    this.m12 = v.y;
	    this.m22 = v.z;
	    break;

	default:
	    throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f9"));
	}
    }

    /**
     * Sets the specified column of this matrix3f to the three values provided.
     * @param column the column number to be modified (zero indexed)
     * @param v the replacement column
     */
    public final void setColumn(int column, float v[])
    {
	switch (column) {
	case 0:
	    this.m00 = v[0];
	    this.m10 = v[1];
	    this.m20 = v[2];
	    break;

	case 1:
	    this.m01 = v[0];
	    this.m11 = v[1];
	    this.m21 = v[2];
	    break;

	case 2:
	    this.m02 = v[0];
	    this.m12 = v[1];
	    this.m22 = v[2];
	    break;

	default:
	    throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f9"));
	}
    }

   /**
     * Performs an SVD normalization of this matrix to calculate
     * and return the uniform scale factor. If the matrix has non-uniform
     * scale factors, the largest of the x, y, and z scale factors will
     * be returned. This matrix is not modified.
     * @return  the scale factor of this matrix
     */
    public final float getScale()
    {

	double[]    tmp_rot = new double[9];  // scratch matrix
	double[]    tmp_scale = new double[3];  // scratch matrix
	getScaleRotate(tmp_scale, tmp_rot);

        return( (float)Matrix3d.max3(tmp_scale ));

    }

   /**
     *  Adds a scalar to each component of this matrix.
     *  @param scalar  the scalar adder
     */
    public final void add(float scalar)
    {
        m00 += scalar;
        m01 += scalar;
        m02 += scalar;
        m10 += scalar;
        m11 += scalar;
        m12 += scalar;
        m20 += scalar;
        m21 += scalar;
        m22 += scalar;
    }

   /**
     *  Adds a scalar to each component of the matrix m1 and places
     *  the result into this.  Matrix m1 is not modified.
     *  @param scalar  the scalar adder.
     *  @param m1  the original matrix values
     */
    public final void add(float scalar, Matrix3f m1)
    {
        this.m00 = m1.m00 +  scalar;
        this.m01 = m1.m01 +  scalar;
        this.m02 = m1.m02 +  scalar;
        this.m10 = m1.m10 +  scalar;
        this.m11 = m1.m11 +  scalar;
        this.m12 = m1.m12 +  scalar;
        this.m20 = m1.m20 +  scalar;
        this.m21 = m1.m21 +  scalar;
        this.m22 = m1.m22 +  scalar;
    }

    /**
     * Sets the value of this matrix to the matrix sum of matrices m1 and m2.
     * @param m1 the first matrix
     * @param m2 the second matrix
     */
    public final void add(Matrix3f m1, Matrix3f m2)
    {
	this.m00 = m1.m00 + m2.m00;
	this.m01 = m1.m01 + m2.m01;
	this.m02 = m1.m02 + m2.m02;

	this.m10 = m1.m10 + m2.m10;
	this.m11 = m1.m11 + m2.m11;
	this.m12 = m1.m12 + m2.m12;

	this.m20 = m1.m20 + m2.m20;
	this.m21 = m1.m21 + m2.m21;
	this.m22 = m1.m22 + m2.m22;
    }

    /**
     * Sets the value of this matrix to the matrix sum of itself and
     * matrix m1.
     * @param m1 the other matrix
     */
    public final void add(Matrix3f m1)
    {
        this.m00 += m1.m00;
        this.m01 += m1.m01;
        this.m02 += m1.m02;

        this.m10 += m1.m10;
        this.m11 += m1.m11;
        this.m12 += m1.m12;

        this.m20 += m1.m20;
        this.m21 += m1.m21;
        this.m22 += m1.m22;
    }

    /**
     * Sets the value of this matrix to the matrix difference
     * of matrices m1 and m2.
     * @param m1 the first matrix
     * @param m2 the second matrix
     */
    public final void sub(Matrix3f m1, Matrix3f m2)
    {
	this.m00 = m1.m00 - m2.m00;
	this.m01 = m1.m01 - m2.m01;
	this.m02 = m1.m02 - m2.m02;

	this.m10 = m1.m10 - m2.m10;
	this.m11 = m1.m11 - m2.m11;
	this.m12 = m1.m12 - m2.m12;

	this.m20 = m1.m20 - m2.m20;
	this.m21 = m1.m21 - m2.m21;
	this.m22 = m1.m22 - m2.m22;
    }

    /**
     * Sets the value of this matrix to the matrix difference
     * of itself and matrix m1 (this = this - m1).
     * @param m1 the other matrix
     */
    public final void sub(Matrix3f m1)
    {
        this.m00 -= m1.m00;
        this.m01 -= m1.m01;
        this.m02 -= m1.m02;

        this.m10 -= m1.m10;
        this.m11 -= m1.m11;
        this.m12 -= m1.m12;

        this.m20 -= m1.m20;
        this.m21 -= m1.m21;
        this.m22 -= m1.m22;
    }

    /**
     * Sets the value of this matrix to its transpose.
     */
    public final void transpose()
    {
	float temp;

	temp = this.m10;
	this.m10 = this.m01;
	this.m01 = temp;

	temp = this.m20;
	this.m20 = this.m02;
	this.m02 = temp;

	temp = this.m21;
	this.m21 = this.m12;
	this.m12 = temp;
    }

    /**
     * Sets the value of this matrix to the transpose of the argument matrix.
     * @param m1 the matrix to be transposed
     */
    public final void transpose(Matrix3f m1)
    {
	if (this != m1) {
	    this.m00 = m1.m00;
	    this.m01 = m1.m10;
	    this.m02 = m1.m20;

	    this.m10 = m1.m01;
	    this.m11 = m1.m11;
	    this.m12 = m1.m21;

	    this.m20 = m1.m02;
	    this.m21 = m1.m12;
	    this.m22 = m1.m22;
	} else
	    this.transpose();
    }

    /**
     * Sets the value of this matrix to the matrix conversion of the
     * (single precision) quaternion argument.
     * @param q1 the quaternion to be converted
     */
    public final void set(Quat4f q1)
    {
	this.m00 = 1.0f - 2.0f*q1.y*q1.y - 2.0f*q1.z*q1.z;
	this.m10 = 2.0f*(q1.x*q1.y + q1.w*q1.z);
	this.m20 = 2.0f*(q1.x*q1.z - q1.w*q1.y);

	this.m01 = 2.0f*(q1.x*q1.y - q1.w*q1.z);
	this.m11 = 1.0f - 2.0f*q1.x*q1.x - 2.0f*q1.z*q1.z;
	this.m21 = 2.0f*(q1.y*q1.z + q1.w*q1.x);

	this.m02 = 2.0f*(q1.x*q1.z + q1.w*q1.y);
	this.m12 = 2.0f*(q1.y*q1.z - q1.w*q1.x);
	this.m22 = 1.0f - 2.0f*q1.x*q1.x - 2.0f*q1.y*q1.y;
    }

    /**
     * Sets the value of this matrix to the matrix conversion of the
     * (single precision) axis and angle argument.
     * @param a1 the axis and angle to be converted
     */
    public final void set(AxisAngle4f a1)
    {
      float mag = (float)Math.sqrt( a1.x*a1.x + a1.y*a1.y + a1.z*a1.z);
      if( mag < EPS ) {
	 m00 = 1.0f;
	 m01 = 0.0f;
	 m02 = 0.0f;

	 m10 = 0.0f;
	 m11 = 1.0f;
	 m12 = 0.0f;

	 m20 = 0.0f;
 	 m21 = 0.0f;
	 m22 = 1.0f;
      } else {
	 mag = 1.0f/mag;
         float ax = a1.x*mag;
         float ay = a1.y*mag;
         float az = a1.z*mag;

         float sinTheta = (float)Math.sin((float)a1.angle);
         float cosTheta = (float)Math.cos((float)a1.angle);
         float t = (float)1.0 - cosTheta;

         float xz = ax * az;
         float xy = ax * ay;
         float yz = ay * az;

         m00 = t * ax * ax + cosTheta;
         m01 = t * xy - sinTheta * az;
         m02 = t * xz + sinTheta * ay;

         m10 = t * xy + sinTheta * az;
         m11 = t * ay * ay + cosTheta;
         m12 = t * yz - sinTheta * ax;

         m20 = t * xz - sinTheta * ay;
         m21 = t * yz + sinTheta * ax;
         m22 = t * az * az + cosTheta;
      }

    }

    /**
     * Sets the value of this matrix to the matrix conversion of the
     * (double precision) axis and angle argument.
     * @param a1 the axis and angle to be converted
     */
    public final void set(AxisAngle4d a1)
    {
      double mag = Math.sqrt( a1.x*a1.x + a1.y*a1.y + a1.z*a1.z);
      if( mag < EPS ) {
	 m00 = 1.0f;
	 m01 = 0.0f;
	 m02 = 0.0f;

	 m10 = 0.0f;
	 m11 = 1.0f;
	 m12 = 0.0f;

	 m20 = 0.0f;
 	 m21 = 0.0f;
	 m22 = 1.0f;
      } else {
	 mag = 1.0/mag;
         double ax = a1.x*mag;
         double ay = a1.y*mag;
         double az = a1.z*mag;

         double sinTheta = Math.sin(a1.angle);
         double cosTheta = Math.cos(a1.angle);
         double t = 1.0 - cosTheta;

         double xz = ax * az;
         double xy = ax * ay;
         double yz = ay * az;

         m00 = (float)(t * ax * ax + cosTheta);
         m01 = (float)(t * xy - sinTheta * az);
         m02 = (float)(t * xz + sinTheta * ay);

         m10 = (float)(t * xy + sinTheta * az);
         m11 = (float)(t * ay * ay + cosTheta);
         m12 = (float)(t * yz - sinTheta * ax);

         m20 = (float)(t * xz - sinTheta * ay);
         m21 = (float)(t * yz + sinTheta * ax);
         m22 = (float)(t * az * az + cosTheta);
      }

    }

    /**
     * Sets the value of this matrix to the matrix conversion of the
     * (single precision) quaternion argument.
     * @param q1 the quaternion to be converted
     */
    public final void set(Quat4d q1)
    {
	this.m00 = (float) (1.0 - 2.0*q1.y*q1.y - 2.0*q1.z*q1.z);
	this.m10 = (float) (2.0*(q1.x*q1.y + q1.w*q1.z));
	this.m20 = (float) (2.0*(q1.x*q1.z - q1.w*q1.y));

	this.m01 = (float) (2.0*(q1.x*q1.y - q1.w*q1.z));
	this.m11 = (float) (1.0 - 2.0*q1.x*q1.x - 2.0*q1.z*q1.z);
	this.m21 = (float) (2.0*(q1.y*q1.z + q1.w*q1.x));

	this.m02 = (float) (2.0*(q1.x*q1.z + q1.w*q1.y));
	this.m12 = (float) (2.0*(q1.y*q1.z - q1.w*q1.x));
	this.m22 = (float) (1.0 - 2.0*q1.x*q1.x - 2.0*q1.y*q1.y);
    }

    /**
     *  Sets the values in this Matrix3f equal to the row-major
     *  array parameter (ie, the first three elements of the
     *  array will be copied into the first row of this matrix, etc.).
     *  @param m  the single precision array of length 9
     */
    public final void set(float[] m)
    {
       m00 = m[0];
       m01 = m[1];
       m02 = m[2];

       m10 = m[3];
       m11 = m[4];
       m12 = m[5];

       m20 = m[6];
       m21 = m[7];
       m22 = m[8];


    }

    /**
     * Sets the value of this matrix to the value of the Matrix3f
     * argument.
     * @param m1 the source matrix3f
     */
    public final void set(Matrix3f m1) {

        this.m00 = m1.m00;
        this.m01 = m1.m01;
        this.m02 = m1.m02;

        this.m10 = m1.m10;
        this.m11 = m1.m11;
        this.m12 = m1.m12;

        this.m20 = m1.m20;
        this.m21 = m1.m21;
        this.m22 = m1.m22;

    }


    /**
     * Sets the value of this matrix to the float value of the Matrix3d
     * argument.
     * @param m1 the source matrix3d
     */
    public final void set(Matrix3d m1) {

        this.m00 = (float)m1.m00;
        this.m01 = (float)m1.m01;
        this.m02 = (float)m1.m02;

        this.m10 = (float)m1.m10;
        this.m11 = (float)m1.m11;
        this.m12 = (float)m1.m12;

        this.m20 = (float)m1.m20;
        this.m21 = (float)m1.m21;
        this.m22 = (float)m1.m22;

    }


    /**
     * Sets the value of this matrix to the matrix inverse
     * of the passed matrix m1.
     * @param m1 the matrix to be inverted
     */
    public final void invert(Matrix3f m1)
    {
	 invertGeneral( m1);
    }

    /**
     * Inverts this matrix in place.
     */
    public final void invert()
    {
	invertGeneral( this );
    }

    /**
     * General invert routine.  Inverts m1 and places the result in "this".
     * Note that this routine handles both the "this" version and the
     * non-"this" version.
     *
     * Also note that since this routine is slow anyway, we won't worry
     * about allocating a little bit of garbage.
     */
    private final void invertGeneral(Matrix3f  m1) {
	double temp[] = new double[9];
	double result[] = new double[9];
	int row_perm[] = new int[3];
	int i;

	// Use LU decomposition and backsubstitution code specifically
	// for floating-point 3x3 matrices.

	// Copy source matrix to t1tmp
        temp[0] = (double)m1.m00;
        temp[1] = (double)m1.m01;
        temp[2] = (double)m1.m02;

        temp[3] = (double)m1.m10;
        temp[4] = (double)m1.m11;
        temp[5] = (double)m1.m12;

        temp[6] = (double)m1.m20;
        temp[7] = (double)m1.m21;
        temp[8] = (double)m1.m22;


	// Calculate LU decomposition: Is the matrix singular?
	if (!luDecomposition(temp, row_perm)) {
	    // Matrix has no inverse
	    throw new SingularMatrixException(VecMathI18N.getString("Matrix3f12"));
	}

	// Perform back substitution on the identity matrix
        for(i=0;i<9;i++) result[i] = 0.0;
        result[0] = 1.0; result[4] = 1.0; result[8] = 1.0;
	luBacksubstitution(temp, row_perm, result);

        this.m00 = (float)result[0];
        this.m01 = (float)result[1];
        this.m02 = (float)result[2];

        this.m10 = (float)result[3];
        this.m11 = (float)result[4];
        this.m12 = (float)result[5];

        this.m20 = (float)result[6];
        this.m21 = (float)result[7];
        this.m22 = (float)result[8];

    }

    /**
     * Given a 3x3 array "matrix0", this function replaces it with the
     * LU decomposition of a row-wise permutation of itself.  The input
     * parameters are "matrix0" and "dimen".  The array "matrix0" is also
     * an output parameter.  The vector "row_perm[3]" is an output
     * parameter that contains the row permutations resulting from partial
     * pivoting.  The output parameter "even_row_xchg" is 1 when the
     * number of row exchanges is even, or -1 otherwise.  Assumes data
     * type is always double.
     *
     * This function is similar to luDecomposition, except that it
     * is tuned specifically for 3x3 matrices.
     *
     * @return true if the matrix is nonsingular, or false otherwise.
     */
    //
    // Reference: Press, Flannery, Teukolsky, Vetterling,
    //	      _Numerical_Recipes_in_C_, Cambridge University Press,
    //	      1988, pp 40-45.
    //
    static boolean luDecomposition(double[] matrix0,
				   int[] row_perm) {

	double row_scale[] = new double[3];

	// Determine implicit scaling information by looping over rows
	{
	    int i, j;
	    int ptr, rs;
	    double big, temp;

	    ptr = 0;
	    rs = 0;

	    // For each row ...
	    i = 3;
	    while (i-- != 0) {
		big = 0.0;

		// For each column, find the largest element in the row
		j = 3;
		while (j-- != 0) {
		    temp = matrix0[ptr++];
		    temp = Math.abs(temp);
		    if (temp > big) {
			big = temp;
		    }
		}

		// Is the matrix singular?
		if (big == 0.0) {
		    return false;
		}
		row_scale[rs++] = 1.0 / big;
	    }
	}

	{
	    int j;
	    int mtx;

	    mtx = 0;

	    // For all columns, execute Crout's method
	    for (j = 0; j < 3; j++) {
		int i, imax, k;
		int target, p1, p2;
		double sum, big, temp;

		// Determine elements of upper diagonal matrix U
		for (i = 0; i < j; i++) {
		    target = mtx + (3*i) + j;
		    sum = matrix0[target];
		    k = i;
		    p1 = mtx + (3*i);
		    p2 = mtx + j;
		    while (k-- != 0) {
			sum -= matrix0[p1] * matrix0[p2];
			p1++;
			p2 += 3;
		    }
		    matrix0[target] = sum;
		}

		// Search for largest pivot element and calculate
		// intermediate elements of lower diagonal matrix L.
		big = 0.0;
		imax = -1;
		for (i = j; i < 3; i++) {
		    target = mtx + (3*i) + j;
		    sum = matrix0[target];
		    k = j;
		    p1 = mtx + (3*i);
		    p2 = mtx + j;
		    while (k-- != 0) {
			sum -= matrix0[p1] * matrix0[p2];
			p1++;
			p2 += 3;
		    }
		    matrix0[target] = sum;

		    // Is this the best pivot so far?
		    if ((temp = row_scale[i] * Math.abs(sum)) >= big) {
			big = temp;
			imax = i;
		    }
		}

		if (imax < 0) {
		    throw new RuntimeException(VecMathI18N.getString("Matrix3f13"));
		}

		// Is a row exchange necessary?
		if (j != imax) {
		    // Yes: exchange rows
		    k = 3;
		    p1 = mtx + (3*imax);
		    p2 = mtx + (3*j);
		    while (k-- != 0) {
			temp = matrix0[p1];
			matrix0[p1++] = matrix0[p2];
			matrix0[p2++] = temp;
		    }

		    // Record change in scale factor
		    row_scale[imax] = row_scale[j];
		}

		// Record row permutation
		row_perm[j] = imax;

		// Is the matrix singular
		if (matrix0[(mtx + (3*j) + j)] == 0.0) {
		    return false;
		}

		// Divide elements of lower diagonal matrix L by pivot
		if (j != (3-1)) {
		    temp = 1.0 / (matrix0[(mtx + (3*j) + j)]);
		    target = mtx + (3*(j+1)) + j;
		    i = 2 - j;
		    while (i-- != 0) {
			matrix0[target] *= temp;
			target += 3;
		    }
		}
	    }
	}

	return true;
    }

    /**
     * Solves a set of linear equations.  The input parameters "matrix1",
     * and "row_perm" come from luDecompostionD3x3 and do not change
     * here.  The parameter "matrix2" is a set of column vectors assembled
     * into a 3x3 matrix of floating-point values.  The procedure takes each
     * column of "matrix2" in turn and treats it as the right-hand side of the
     * matrix equation Ax = LUx = b.  The solution vector replaces the
     * original column of the matrix.
     *
     * If "matrix2" is the identity matrix, the procedure replaces its contents
     * with the inverse of the matrix from which "matrix1" was originally
     * derived.
     */
    //
    // Reference: Press, Flannery, Teukolsky, Vetterling,
    //	      _Numerical_Recipes_in_C_, Cambridge University Press,
    //	      1988, pp 44-45.
    //
    static void luBacksubstitution(double[] matrix1,
				   int[] row_perm,
				   double[] matrix2) {

	int i, ii, ip, j, k;
	int rp;
	int cv, rv;

	//	rp = row_perm;
	rp = 0;

	// For each column vector of matrix2 ...
	for (k = 0; k < 3; k++) {
	    //	    cv = &(matrix2[0][k]);
	    cv = k;
	    ii = -1;

	    // Forward substitution
	    for (i = 0; i < 3; i++) {
		double sum;

		ip = row_perm[rp+i];
		sum = matrix2[cv+3*ip];
		matrix2[cv+3*ip] = matrix2[cv+3*i];
		if (ii >= 0) {
		    //		    rv = &(matrix1[i][0]);
		    rv = i*3;
		    for (j = ii; j <= i-1; j++) {
			sum -= matrix1[rv+j] * matrix2[cv+3*j];
		    }
		}
		else if (sum != 0.0) {
		    ii = i;
		}
		matrix2[cv+3*i] = sum;
	    }

	    // Backsubstitution
	    //	    rv = &(matrix1[3][0]);
	    rv = 2*3;
	    matrix2[cv+3*2] /= matrix1[rv+2];

	    rv -= 3;
	    matrix2[cv+3*1] = (matrix2[cv+3*1] -
			    matrix1[rv+2] * matrix2[cv+3*2]) / matrix1[rv+1];

	    rv -= 3;
	    matrix2[cv+4*0] = (matrix2[cv+3*0] -
			    matrix1[rv+1] * matrix2[cv+3*1] -
			    matrix1[rv+2] * matrix2[cv+3*2]) / matrix1[rv+0];

	}
    }
   /**
     * Computes the determinant of this matrix.
     * @return the determinant of this matrix
     */
    public final float determinant()
    {
       float total;
       total =  this.m00*(this.m11*this.m22 - this.m12*this.m21)
              + this.m01*(this.m12*this.m20 - this.m10*this.m22)
              + this.m02*(this.m10*this.m21 - this.m11*this.m20);
       return total;
    }

    /**
     * Sets the value of this matrix to a scale matrix with
     * the passed scale amount.
     * @param scale the scale factor for the matrix
     */
    public final void set(float scale)
    {
	this.m00 = scale;
	this.m01 = (float) 0.0;
	this.m02 = (float) 0.0;

	this.m10 = (float) 0.0;
	this.m11 = scale;
	this.m12 = (float) 0.0;

	this.m20 = (float) 0.0;
	this.m21 = (float) 0.0;
	this.m22 = scale;
    }

    /**
     * Sets the value of this matrix to a counter clockwise rotation
     * about the x axis.
     * @param angle the angle to rotate about the X axis in radians
     */
    public final void rotX(float angle)
    {
	float	sinAngle, cosAngle;

	sinAngle = (float) Math.sin((double) angle);
	cosAngle = (float) Math.cos((double) angle);

	this.m00 = (float) 1.0;
	this.m01 = (float) 0.0;
	this.m02 = (float) 0.0;

	this.m10 = (float) 0.0;
	this.m11 = cosAngle;
	this.m12 = -sinAngle;

	this.m20 = (float) 0.0;
	this.m21 = sinAngle;
	this.m22 = cosAngle;
    }

    /**
     * Sets the value of this matrix to a counter clockwise rotation
     * about the y axis.
     * @param angle the angle to rotate about the Y axis in radians
     */
    public final void rotY(float angle)
    {
	float	sinAngle, cosAngle;

	sinAngle = (float) Math.sin((double) angle);
	cosAngle = (float) Math.cos((double) angle);

	this.m00 = cosAngle;
	this.m01 = (float) 0.0;
	this.m02 = sinAngle;

	this.m10 = (float) 0.0;
	this.m11 = (float) 1.0;
	this.m12 = (float) 0.0;

	this.m20 = -sinAngle;
	this.m21 = (float) 0.0;
	this.m22 = cosAngle;
    }

    /**
     * Sets the value of this matrix to a counter clockwise rotation
     * about the z axis.
     * @param angle the angle to rotate about the Z axis in radians
     */
    public final void rotZ(float angle)
    {
	float	sinAngle, cosAngle;

	sinAngle = (float) Math.sin((double) angle);
	cosAngle = (float) Math.cos((double) angle);

	this.m00 = cosAngle;
	this.m01 = -sinAngle;
	this.m02 = (float) 0.0;

	this.m10 = sinAngle;
	this.m11 = cosAngle;
	this.m12 = (float) 0.0;

	this.m20 = (float) 0.0;
	this.m21 = (float) 0.0;
	this.m22 = (float) 1.0;
    }

   /**
     * Multiplies each element of this matrix by a scalar.
     * @param scalar  the scalar multiplier
     */
    public final void mul(float scalar)
    {
       m00 *= scalar;
       m01 *= scalar;
       m02 *= scalar;

       m10 *= scalar;
       m11 *= scalar;
       m12 *= scalar;

       m20 *= scalar;
       m21 *= scalar;
       m22 *= scalar;
    }

   /**
     * Multiplies each element of matrix m1 by a scalar and places
     * the result into this.  Matrix m1 is not modified.
     * @param scalar  the scalar multiplier
     * @param m1  the original matrix
     */
    public final void mul(float scalar, Matrix3f m1)
    {
        this.m00 = scalar * m1.m00;
        this.m01 = scalar * m1.m01;
        this.m02 = scalar * m1.m02;

        this.m10 = scalar * m1.m10;
        this.m11 = scalar * m1.m11;
        this.m12 = scalar * m1.m12;

        this.m20 = scalar * m1.m20;
        this.m21 = scalar * m1.m21;
        this.m22 = scalar * m1.m22;

    }

   /**
     * Sets the value of this matrix to the result of multiplying itself
     * with matrix m1.
     * @param m1 the other matrix
     */
    public final void mul(Matrix3f m1)
    {
          float       m00, m01, m02,
                      m10, m11, m12,
                      m20, m21, m22;

            m00 = this.m00*m1.m00 + this.m01*m1.m10 + this.m02*m1.m20;
            m01 = this.m00*m1.m01 + this.m01*m1.m11 + this.m02*m1.m21;
            m02 = this.m00*m1.m02 + this.m01*m1.m12 + this.m02*m1.m22;

            m10 = this.m10*m1.m00 + this.m11*m1.m10 + this.m12*m1.m20;
            m11 = this.m10*m1.m01 + this.m11*m1.m11 + this.m12*m1.m21;
            m12 = this.m10*m1.m02 + this.m11*m1.m12 + this.m12*m1.m22;

            m20 = this.m20*m1.m00 + this.m21*m1.m10 + this.m22*m1.m20;
            m21 = this.m20*m1.m01 + this.m21*m1.m11 + this.m22*m1.m21;
            m22 = this.m20*m1.m02 + this.m21*m1.m12 + this.m22*m1.m22;

            this.m00 = m00; this.m01 = m01; this.m02 = m02;
            this.m10 = m10; this.m11 = m11; this.m12 = m12;
            this.m20 = m20; this.m21 = m21; this.m22 = m22;
    }

    /**
     * Sets the value of this matrix to the result of multiplying
     * the two argument matrices together.
     * @param m1 the first matrix
     * @param m2 the second matrix
     */
    public final void mul(Matrix3f m1, Matrix3f m2)
    {
	if (this != m1 && this != m2) {
            this.m00 = m1.m00*m2.m00 + m1.m01*m2.m10 + m1.m02*m2.m20;
            this.m01 = m1.m00*m2.m01 + m1.m01*m2.m11 + m1.m02*m2.m21;
            this.m02 = m1.m00*m2.m02 + m1.m01*m2.m12 + m1.m02*m2.m22;

            this.m10 = m1.m10*m2.m00 + m1.m11*m2.m10 + m1.m12*m2.m20;
            this.m11 = m1.m10*m2.m01 + m1.m11*m2.m11 + m1.m12*m2.m21;
            this.m12 = m1.m10*m2.m02 + m1.m11*m2.m12 + m1.m12*m2.m22;

            this.m20 = m1.m20*m2.m00 + m1.m21*m2.m10 + m1.m22*m2.m20;
            this.m21 = m1.m20*m2.m01 + m1.m21*m2.m11 + m1.m22*m2.m21;
            this.m22 = m1.m20*m2.m02 + m1.m21*m2.m12 + m1.m22*m2.m22;
	} else {
	    float	m00, m01, m02,
			m10, m11, m12,
			m20, m21, m22;

            m00 = m1.m00*m2.m00 + m1.m01*m2.m10 + m1.m02*m2.m20;
            m01 = m1.m00*m2.m01 + m1.m01*m2.m11 + m1.m02*m2.m21;
            m02 = m1.m00*m2.m02 + m1.m01*m2.m12 + m1.m02*m2.m22;

            m10 = m1.m10*m2.m00 + m1.m11*m2.m10 + m1.m12*m2.m20;
            m11 = m1.m10*m2.m01 + m1.m11*m2.m11 + m1.m12*m2.m21;
            m12 = m1.m10*m2.m02 + m1.m11*m2.m12 + m1.m12*m2.m22;

            m20 = m1.m20*m2.m00 + m1.m21*m2.m10 + m1.m22*m2.m20;
            m21 = m1.m20*m2.m01 + m1.m21*m2.m11 + m1.m22*m2.m21;
            m22 = m1.m20*m2.m02 + m1.m21*m2.m12 + m1.m22*m2.m22;

            this.m00 = m00; this.m01 = m01; this.m02 = m02;
            this.m10 = m10; this.m11 = m11; this.m12 = m12;
            this.m20 = m20; this.m21 = m21; this.m22 = m22;
	}
    }

   /**
     *  Multiplies this matrix by matrix m1, does an SVD normalization
     *  of the result, and places the result back into this matrix.
     *  this = SVDnorm(this*m1).
     *  @param  m1 the matrix on the right hand side of the multiplication
     */
    public final void mulNormalize(Matrix3f m1){

	double[]    tmp = new double[9];  // scratch matrix
	double[]    tmp_rot = new double[9];  // scratch matrix
	double[]    tmp_scale = new double[3];  // scratch matrix

	tmp[0] = this.m00*m1.m00 + this.m01*m1.m10 + this.m02*m1.m20;
	tmp[1] = this.m00*m1.m01 + this.m01*m1.m11 + this.m02*m1.m21;
	tmp[2] = this.m00*m1.m02 + this.m01*m1.m12 + this.m02*m1.m22;

	tmp[3] = this.m10*m1.m00 + this.m11*m1.m10 + this.m12*m1.m20;
	tmp[4] = this.m10*m1.m01 + this.m11*m1.m11 + this.m12*m1.m21;
	tmp[5] = this.m10*m1.m02 + this.m11*m1.m12 + this.m12*m1.m22;

	tmp[6] = this.m20*m1.m00 + this.m21*m1.m10 + this.m22*m1.m20;
	tmp[7] = this.m20*m1.m01 + this.m21*m1.m11 + this.m22*m1.m21;
	tmp[8] = this.m20*m1.m02 + this.m21*m1.m12 + this.m22*m1.m22;

	Matrix3d.compute_svd( tmp, tmp_scale, tmp_rot);

	this.m00 = (float)(tmp_rot[0]);
	this.m01 = (float)(tmp_rot[1]);
	this.m02 = (float)(tmp_rot[2]);

	this.m10 = (float)(tmp_rot[3]);
	this.m11 = (float)(tmp_rot[4]);
	this.m12 = (float)(tmp_rot[5]);

	this.m20 = (float)(tmp_rot[6]);
	this.m21 = (float)(tmp_rot[7]);
	this.m22 = (float)(tmp_rot[8]);

    }

   /**
     *  Multiplies matrix m1 by matrix m2, does an SVD normalization
     *  of the result, and places the result into this matrix.
     *  this = SVDnorm(m1*m2).
     *  @param m1  the matrix on the left hand side of the multiplication
     *  @param m2  the matrix on the right hand side of the multiplication
     */
    public final void mulNormalize(Matrix3f m1, Matrix3f m2){

	double[]    tmp = new double[9];  // scratch matrix
	double[]    tmp_rot = new double[9];  // scratch matrix
	double[]    tmp_scale = new double[3];  // scratch matrix


	tmp[0] = m1.m00*m2.m00 + m1.m01*m2.m10 + m1.m02*m2.m20;
	tmp[1] = m1.m00*m2.m01 + m1.m01*m2.m11 + m1.m02*m2.m21;
	tmp[2] = m1.m00*m2.m02 + m1.m01*m2.m12 + m1.m02*m2.m22;

	tmp[3] = m1.m10*m2.m00 + m1.m11*m2.m10 + m1.m12*m2.m20;
	tmp[4] = m1.m10*m2.m01 + m1.m11*m2.m11 + m1.m12*m2.m21;
	tmp[5] = m1.m10*m2.m02 + m1.m11*m2.m12 + m1.m12*m2.m22;

	tmp[6] = m1.m20*m2.m00 + m1.m21*m2.m10 + m1.m22*m2.m20;
	tmp[7] = m1.m20*m2.m01 + m1.m21*m2.m11 + m1.m22*m2.m21;
	tmp[8] = m1.m20*m2.m02 + m1.m21*m2.m12 + m1.m22*m2.m22;

	Matrix3d.compute_svd( tmp, tmp_scale, tmp_rot);

	this.m00 = (float)(tmp_rot[0]);
	this.m01 = (float)(tmp_rot[1]);
	this.m02 = (float)(tmp_rot[2]);

	this.m10 = (float)(tmp_rot[3]);
	this.m11 = (float)(tmp_rot[4]);
	this.m12 = (float)(tmp_rot[5]);

	this.m20 = (float)(tmp_rot[6]);
	this.m21 = (float)(tmp_rot[7]);
	this.m22 = (float)(tmp_rot[8]);
    }

   /**
     *  Multiplies the transpose of matrix m1 times the transpose of matrix
     *  m2, and places the result into this.
     *  @param m1  the matrix on the left hand side of the multiplication
     *  @param m2  the matrix on the right hand side of the multiplication
     */
    public final void mulTransposeBoth(Matrix3f m1, Matrix3f m2)
    {
        if (this != m1 && this != m2) {
            this.m00 = m1.m00*m2.m00 + m1.m10*m2.m01 + m1.m20*m2.m02;
            this.m01 = m1.m00*m2.m10 + m1.m10*m2.m11 + m1.m20*m2.m12;
            this.m02 = m1.m00*m2.m20 + m1.m10*m2.m21 + m1.m20*m2.m22;

            this.m10 = m1.m01*m2.m00 + m1.m11*m2.m01 + m1.m21*m2.m02;
            this.m11 = m1.m01*m2.m10 + m1.m11*m2.m11 + m1.m21*m2.m12;
            this.m12 = m1.m01*m2.m20 + m1.m11*m2.m21 + m1.m21*m2.m22;

            this.m20 = m1.m02*m2.m00 + m1.m12*m2.m01 + m1.m22*m2.m02;
            this.m21 = m1.m02*m2.m10 + m1.m12*m2.m11 + m1.m22*m2.m12;
            this.m22 = m1.m02*m2.m20 + m1.m12*m2.m21 + m1.m22*m2.m22;
        } else {
            float       m00, m01, m02,
                        m10, m11, m12,
		m20, m21, m22;  // vars for temp result matrix

            m00 = m1.m00*m2.m00 + m1.m10*m2.m01 + m1.m20*m2.m02;
            m01 = m1.m00*m2.m10 + m1.m10*m2.m11 + m1.m20*m2.m12;
            m02 = m1.m00*m2.m20 + m1.m10*m2.m21 + m1.m20*m2.m22;

            m10 = m1.m01*m2.m00 + m1.m11*m2.m01 + m1.m21*m2.m02;
            m11 = m1.m01*m2.m10 + m1.m11*m2.m11 + m1.m21*m2.m12;
            m12 = m1.m01*m2.m20 + m1.m11*m2.m21 + m1.m21*m2.m22;

            m20 = m1.m02*m2.m00 + m1.m12*m2.m01 + m1.m22*m2.m02;
            m21 = m1.m02*m2.m10 + m1.m12*m2.m11 + m1.m22*m2.m12;
            m22 = m1.m02*m2.m20 + m1.m12*m2.m21 + m1.m22*m2.m22;

            this.m00 = m00; this.m01 = m01; this.m02 = m02;
            this.m10 = m10; this.m11 = m11; this.m12 = m12;
            this.m20 = m20; this.m21 = m21; this.m22 = m22;
        }

    }


   /**
     *  Multiplies matrix m1 times the transpose of matrix m2, and
     *  places the result into this.
     *  @param m1  the matrix on the left hand side of the multiplication
     *  @param m2  the matrix on the right hand side of the multiplication
     */
    public final void mulTransposeRight(Matrix3f m1, Matrix3f m2)
  {
    if (this != m1 && this != m2) {
      this.m00 = m1.m00*m2.m00 + m1.m01*m2.m01 + m1.m02*m2.m02;
      this.m01 = m1.m00*m2.m10 + m1.m01*m2.m11 + m1.m02*m2.m12;
      this.m02 = m1.m00*m2.m20 + m1.m01*m2.m21 + m1.m02*m2.m22;

      this.m10 = m1.m10*m2.m00 + m1.m11*m2.m01 + m1.m12*m2.m02;
      this.m11 = m1.m10*m2.m10 + m1.m11*m2.m11 + m1.m12*m2.m12;
      this.m12 = m1.m10*m2.m20 + m1.m11*m2.m21 + m1.m12*m2.m22;

      this.m20 = m1.m20*m2.m00 + m1.m21*m2.m01 + m1.m22*m2.m02;
      this.m21 = m1.m20*m2.m10 + m1.m21*m2.m11 + m1.m22*m2.m12;
      this.m22 = m1.m20*m2.m20 + m1.m21*m2.m21 + m1.m22*m2.m22;
    } else {
      float	m00, m01, m02,
	m10, m11, m12,
	  m20, m21, m22;  // vars for temp result matrix

      m00 = m1.m00*m2.m00 + m1.m01*m2.m01 + m1.m02*m2.m02;
      m01 = m1.m00*m2.m10 + m1.m01*m2.m11 + m1.m02*m2.m12;
      m02 = m1.m00*m2.m20 + m1.m01*m2.m21 + m1.m02*m2.m22;

      m10 = m1.m10*m2.m00 + m1.m11*m2.m01 + m1.m12*m2.m02;
      m11 = m1.m10*m2.m10 + m1.m11*m2.m11 + m1.m12*m2.m12;
      m12 = m1.m10*m2.m20 + m1.m11*m2.m21 + m1.m12*m2.m22;

      m20 = m1.m20*m2.m00 + m1.m21*m2.m01 + m1.m22*m2.m02;
      m21 = m1.m20*m2.m10 + m1.m21*m2.m11 + m1.m22*m2.m12;
      m22 = m1.m20*m2.m20 + m1.m21*m2.m21 + m1.m22*m2.m22;

      this.m00 = m00; this.m01 = m01; this.m02 = m02;
      this.m10 = m10; this.m11 = m11; this.m12 = m12;
      this.m20 = m20; this.m21 = m21; this.m22 = m22;
    }
  }

   /**
     *  Multiplies the transpose of matrix m1 times matrix m2, and
     *  places the result into this.
     *  @param m1  the matrix on the left hand side of the multiplication
     *  @param m2  the matrix on the right hand side of the multiplication
     */
    public final void mulTransposeLeft(Matrix3f m1, Matrix3f m2)
    {
      if (this != m1 && this != m2) {
	this.m00 = m1.m00*m2.m00 + m1.m10*m2.m10 + m1.m20*m2.m20;
	this.m01 = m1.m00*m2.m01 + m1.m10*m2.m11 + m1.m20*m2.m21;
	this.m02 = m1.m00*m2.m02 + m1.m10*m2.m12 + m1.m20*m2.m22;

	this.m10 = m1.m01*m2.m00 + m1.m11*m2.m10 + m1.m21*m2.m20;
	this.m11 = m1.m01*m2.m01 + m1.m11*m2.m11 + m1.m21*m2.m21;
	this.m12 = m1.m01*m2.m02 + m1.m11*m2.m12 + m1.m21*m2.m22;

	this.m20 = m1.m02*m2.m00 + m1.m12*m2.m10 + m1.m22*m2.m20;
	this.m21 = m1.m02*m2.m01 + m1.m12*m2.m11 + m1.m22*m2.m21;
	this.m22 = m1.m02*m2.m02 + m1.m12*m2.m12 + m1.m22*m2.m22;
      } else {
	float	m00, m01, m02,
	  m10, m11, m12,
	    m20, m21, m22;  // vars for temp result matrix

	m00 = m1.m00*m2.m00 + m1.m10*m2.m10 + m1.m20*m2.m20;
	m01 = m1.m00*m2.m01 + m1.m10*m2.m11 + m1.m20*m2.m21;
	m02 = m1.m00*m2.m02 + m1.m10*m2.m12 + m1.m20*m2.m22;

	m10 = m1.m01*m2.m00 + m1.m11*m2.m10 + m1.m21*m2.m20;
	m11 = m1.m01*m2.m01 + m1.m11*m2.m11 + m1.m21*m2.m21;
	m12 = m1.m01*m2.m02 + m1.m11*m2.m12 + m1.m21*m2.m22;

	m20 = m1.m02*m2.m00 + m1.m12*m2.m10 + m1.m22*m2.m20;
	m21 = m1.m02*m2.m01 + m1.m12*m2.m11 + m1.m22*m2.m21;
	m22 = m1.m02*m2.m02 + m1.m12*m2.m12 + m1.m22*m2.m22;

	this.m00 = m00; this.m01 = m01; this.m02 = m02;
	this.m10 = m10; this.m11 = m11; this.m12 = m12;
	this.m20 = m20; this.m21 = m21; this.m22 = m22;
      }
    }

   /**
     * Performs singular value decomposition normalization of this matrix.
     */
    public final void normalize(){

	double[]    tmp_rot = new double[9];  // scratch matrix
	double[]    tmp_scale = new double[3];  // scratch matrix
	getScaleRotate( tmp_scale, tmp_rot );

	this.m00 = (float)tmp_rot[0];
	this.m01 = (float)tmp_rot[1];
	this.m02 = (float)tmp_rot[2];

	this.m10 = (float)tmp_rot[3];
	this.m11 = (float)tmp_rot[4];
	this.m12 = (float)tmp_rot[5];

	this.m20 = (float)tmp_rot[6];
	this.m21 = (float)tmp_rot[7];
	this.m22 = (float)tmp_rot[8];

    }

   /**
     * Perform singular value decomposition normalization of matrix m1
     * and place the normalized values into this.
     * @param m1  the matrix values to be normalized
     */
    public final void normalize(Matrix3f m1){
	double[]    tmp = new double[9];  // scratch matrix
	double[]    tmp_rot = new double[9];  // scratch matrix
	double[]    tmp_scale = new double[3];  // scratch matrix

	tmp[0] = m1.m00;
	tmp[1] = m1.m01;
	tmp[2] = m1.m02;

	tmp[3] = m1.m10;
	tmp[4] = m1.m11;
	tmp[5] = m1.m12;

	tmp[6] = m1.m20;
	tmp[7] = m1.m21;
	tmp[8] = m1.m22;

	Matrix3d.compute_svd( tmp, tmp_scale, tmp_rot );

	this.m00 = (float)(tmp_rot[0]);
	this.m01 = (float)(tmp_rot[1]);
	this.m02 = (float)(tmp_rot[2]);

	this.m10 = (float)(tmp_rot[3]);
	this.m11 = (float)(tmp_rot[4]);
	this.m12 = (float)(tmp_rot[5]);

	this.m20 = (float)(tmp_rot[6]);
	this.m21 = (float)(tmp_rot[7]);
	this.m22 = (float)(tmp_rot[8]);

    }

   /**
     * Perform cross product normalization of this matrix.
     */
    public final void normalizeCP()
    {
       float mag = 1.0f/(float)Math.sqrt(m00*m00 + m10*m10 + m20*m20);
       m00 = m00*mag;
       m10 = m10*mag;
       m20 = m20*mag;

       mag = 1.0f/(float)Math.sqrt(m01*m01 + m11*m11 + m21*m21);
       m01 = m01*mag;
       m11 = m11*mag;
       m21 = m21*mag;

       m02 = m10*m21 - m11*m20;
       m12 = m01*m20 - m00*m21;
       m22 = m00*m11 - m01*m10;

    }

   /**
     * Perform cross product normalization of matrix m1 and place the
     * normalized values into this.
     * @param m1  Provides the matrix values to be normalized
     */
    public final void normalizeCP(Matrix3f m1)
    {
       float mag = 1.0f/(float)Math.sqrt(m1.m00*m1.m00 + m1.m10*m1.m10 + m1.m20*m1.m20);
       m00 = m1.m00*mag;
       m10 = m1.m10*mag;
       m20 = m1.m20*mag;

       mag = 1.0f/(float)Math.sqrt(m1.m01*m1.m01 + m1.m11*m1.m11 + m1.m21*m1.m21);
       m01 = m1.m01*mag;
       m11 = m1.m11*mag;
       m21 = m1.m21*mag;

       m02 = m10*m21 - m11*m20;
       m12 = m01*m20 - m00*m21;
       m22 = m00*m11 - m01*m10;

    }

   /**
     * Returns true if all of the data members of Matrix3f m1 are
     * equal to the corresponding data members in this Matrix3f.
     * @param m1  the matrix with which the comparison is made
     * @return  true or false
     */
    public boolean equals(Matrix3f m1)
    {
      try {

        return(this.m00 == m1.m00 && this.m01 == m1.m01 && this.m02 == m1.m02
            && this.m10 == m1.m10 && this.m11 == m1.m11 && this.m12 == m1.m12
            && this.m20 == m1.m20 && this.m21 == m1.m21 && this.m22 == m1.m22);
      }
      catch (NullPointerException e2) { return false; }

    }

   /**
     * Returns true if the Object o1 is of type Matrix3f and all of the
     * data members of o1 are equal to the corresponding data members in
     * this Matrix3f.
     * @param o1  the object with which the comparison is made
     * @return  true or false
     */
    @Override
    public boolean equals(Object o1)
    {
      try {

           Matrix3f m2 = (Matrix3f) o1;
           return(this.m00 == m2.m00 && this.m01 == m2.m01 && this.m02 == m2.m02
             && this.m10 == m2.m10 && this.m11 == m2.m11 && this.m12 == m2.m12
             && this.m20 == m2.m20 && this.m21 == m2.m21 && this.m22 == m2.m22);
        }
        catch (ClassCastException   e1) { return false; }
        catch (NullPointerException e2) { return false; }
    }

   /**
     * Returns true if the L-infinite distance between this matrix
     * and matrix m1 is less than or equal to the epsilon parameter,
     * otherwise returns false.  The L-infinite
     * distance is equal to
     * MAX[i=0,1,2 ; j=0,1,2 ; abs(this.m(i,j) - m1.m(i,j)]
     * @param m1  the matrix to be compared to this matrix
     * @param epsilon  the threshold value
     */
    public boolean epsilonEquals(Matrix3f m1, float epsilon)
    {
        boolean status = true;

        if( Math.abs( this.m00 - m1.m00) > epsilon) status = false;
        if( Math.abs( this.m01 - m1.m01) > epsilon) status = false;
        if( Math.abs( this.m02 - m1.m02) > epsilon) status = false;

        if( Math.abs( this.m10 - m1.m10) > epsilon) status = false;
        if( Math.abs( this.m11 - m1.m11) > epsilon) status = false;
        if( Math.abs( this.m12 - m1.m12) > epsilon) status = false;

        if( Math.abs( this.m20 - m1.m20) > epsilon) status = false;
        if( Math.abs( this.m21 - m1.m21) > epsilon) status = false;
        if( Math.abs( this.m22 - m1.m22) > epsilon) status = false;

        return( status );

    }


    /**
     * Returns a hash code value based on the data values in this
     * object.  Two different Matrix3f objects with identical data values
     * (i.e., Matrix3f.equals returns true) will return the same hash
     * code value.  Two objects with different data members may return the
     * same hash value, although this is not likely.
     * @return the integer hash code value
     */
    @Override
    public int hashCode() {
	long bits = 1L;
	bits = VecMathUtil.hashFloatBits(bits, m00);
	bits = VecMathUtil.hashFloatBits(bits, m01);
	bits = VecMathUtil.hashFloatBits(bits, m02);
	bits = VecMathUtil.hashFloatBits(bits, m10);
	bits = VecMathUtil.hashFloatBits(bits, m11);
	bits = VecMathUtil.hashFloatBits(bits, m12);
	bits = VecMathUtil.hashFloatBits(bits, m20);
	bits = VecMathUtil.hashFloatBits(bits, m21);
	bits = VecMathUtil.hashFloatBits(bits, m22);
	return VecMathUtil.hashFinish(bits);
    }


  /**
    *  Sets this matrix to all zeros.
    */
   public final void setZero()
   {
        m00 = 0.0f;
        m01 = 0.0f;
        m02 = 0.0f;

        m10 = 0.0f;
        m11 = 0.0f;
        m12 = 0.0f;

        m20 = 0.0f;
        m21 = 0.0f;
        m22 = 0.0f;

   }

   /**
     * Negates the value of this matrix: this = -this.
     */
    public final void negate()
    {
        this.m00 = -this.m00;
        this.m01 = -this.m01;
        this.m02 = -this.m02;

        this.m10 = -this.m10;
        this.m11 = -this.m11;
        this.m12 = -this.m12;

        this.m20 = -this.m20;
        this.m21 = -this.m21;
        this.m22 = -this.m22;

    }

   /**
     *  Sets the value of this matrix equal to the negation of
     *  of the Matrix3f parameter.
     *  @param m1  the source matrix
     */
    public final void negate(Matrix3f m1)
    {
        this.m00 = -m1.m00;
        this.m01 = -m1.m01;
        this.m02 = -m1.m02;

        this.m10 = -m1.m10;
        this.m11 = -m1.m11;
        this.m12 = -m1.m12;

        this.m20 = -m1.m20;
        this.m21 = -m1.m21;
        this.m22 = -m1.m22;

    }

   /**
    * Multiply this matrix by the tuple t and place the result
    * back into the tuple (t = this*t).
    * @param t  the tuple to be multiplied by this matrix and then replaced
    */
    public final void transform(Tuple3f t) {
     float x,y,z;
     x = m00* t.x + m01*t.y + m02*t.z;
     y = m10* t.x + m11*t.y + m12*t.z;
     z = m20* t.x + m21*t.y + m22*t.z;
     t.set(x,y,z);
    }

   /**
    * Multiply this matrix by the tuple t and and place the result
    * into the tuple "result" (result = this*t).
    * @param t  the tuple to be multiplied by this matrix
    * @param result  the tuple into which the product is placed
    */
    public final void transform(Tuple3f t, Tuple3f result) {
     float x,y;
     x = m00* t.x + m01*t.y + m02*t.z;
     y = m10* t.x + m11*t.y + m12*t.z;
     result.z = m20* t.x + m21*t.y + m22*t.z;
     result.x = x;
     result.y = y;
    }

    /**
     * perform SVD (if necessary to get rotational component
     */
    void  getScaleRotate( double[] scales, double[] rot ) {

	double[]    tmp = new double[9];  // scratch matrix
        tmp[0] = m00;
        tmp[1] = m01;
        tmp[2] = m02;
        tmp[3] = m10;
        tmp[4] = m11;
        tmp[5] = m12;
        tmp[6] = m20;
        tmp[7] = m21;
        tmp[8] = m22;
        Matrix3d.compute_svd(tmp, scales, rot);

        return;

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
	Matrix3f m1 = null;
	try {
	    m1 = (Matrix3f)super.clone();
	} catch (CloneNotSupportedException e) {
	    // this shouldn't happen, since we are Cloneable
	    throw new InternalError();
	}
	return m1;
    }


    /**
	 * Get the first matrix element in the first row.
	 *
	 * @return Returns the m00.
	 *
	 * @since vecmath 1.5
	 */
	public final  float getM00() {
		return m00;
	}

	/**
	 * Set the first matrix element in the first row.
	 *
	 * @param m00 The m00 to set.
	 *
	 * @since vecmath 1.5
	 */
	public final  void setM00(float m00) {
		this.m00 = m00;
	}

	/**
	 * Get the second matrix element in the first row.
	 *
	 * @return Returns the m01.
	 *
	 *
	 * @since vecmath 1.5
	 */
	public final  float getM01() {
		return m01;
	}

	/**
	 * Set the second matrix element in the first row.
	 *
	 * @param m01 The m01 to set.
	 *
	 * @since vecmath 1.5
	 */
	public  final void setM01(float m01) {
		this.m01 = m01;
	}

	/**
	 * Get the third matrix element in the first row.
	 *
	 * @return Returns the m02.
	 *
	 * @since vecmath 1.5
	 */
	public final float getM02() {
		return m02;
	}

	/**
	 * Set the third matrix element in the first row.
	 *
	 * @param m02 The m02 to set.
	 *
	 * @since vecmath 1.5
	 */
	public final  void setM02(float m02) {
		this.m02 = m02;
	}

	/**
	 * Get first matrix element in the second row.
	 *
	 * @return Returns the m10.
	 *
	 * @since vecmath 1.5
	 */
	public final  float getM10() {
		return m10;
	}

	/**
	 * Set first matrix element in the second row.
	 *
	 * @param m10 The m10 to set.
	 *
	 * @since vecmath 1.5
	 */
	public final  void setM10(float m10) {
		this.m10 = m10;
	}

	/**
	 * Get second matrix element in the second row.
	 *
	 * @return Returns the m11.
	 *
	 * @since vecmath 1.5
	 */
	public final  float getM11() {
		return m11;
	}

	/**
	 * Set the second matrix element in the second row.
	 *
	 * @param m11 The m11 to set.
	 *
	 * @since vecmath 1.5
	 */
	public final  void setM11(float m11) {
		this.m11 = m11;
	}

	/**
	 * Get the third matrix element in the second row.
	 *
	 * @return Returns the m12.
	 *
	 * @since vecmath 1.5
	 */
	public final  float getM12() {
		return m12;
	}

	/**
	 * Set the third matrix element in the second row.
	 * @param m12 The m12 to set.
	 * @since vecmath 1.5
	 */
	public final  void setM12(float m12) {
		this.m12 = m12;
	}

	/**
	 * Get the first matrix element in the third row.
	 *
	 * @return Returns the m20.
	 *
	 * @since vecmath 1.5
	 */
	public final  float getM20() {
		return m20;
	}

	/**
	 * Set the first matrix element in the third row.
	 *
	 * @param m20 The m20 to set.
	 *
	 * @since vecmath 1.5
	 */
	public final void setM20(float m20) {
		this.m20 = m20;
	}

	/**
	 * Get the second matrix element in the third row.
	 *
	 * @return Returns the m21.
	 *
	 * @since vecmath 1.5
	 */
	public final float getM21() {
		return m21;
	}

	/**
	 * Set the second matrix element in the third row.
	 *
	 * @param m21 The m21 to set.
	 *
	 * @since vecmath 1.5
	 */
	public final void setM21(float m21) {
		this.m21 = m21;
	}

	/**
	 * Get the third matrix element in the third row .
	 *
	 * @return Returns the m22.
	 *
	 * @since vecmath 1.5
	 */
	public final float getM22() {
		return m22;
	}

	/**
	 * Set the third matrix element in the third row.
	 *
	 * @param m22 The m22 to set.
	 *
	 * @since vecmath 1.5
	 */
	public final void setM22(float m22) {
		this.m22 = m22;
	}

}
