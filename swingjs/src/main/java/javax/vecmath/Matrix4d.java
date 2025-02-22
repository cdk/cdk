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
 * A double precision floating point 4 by 4 matrix.
 * Primarily to support 3D rotations.
 *
 */
public class Matrix4d implements java.io.Serializable, Cloneable {

    // Compatible with 1.1
    static final long serialVersionUID = 8223903484171633710L;

    /**
     *  The first element of the first row.
     */
    public	double	m00;

    /**
     *  The second element of the first row.
     */
    public	double	m01;

    /**
     *  The third element of the first row.
     */
    public	double	m02;

    /**
     *  The fourth element of the first row.
     */
    public	double	m03;

    /**
     *  The first element of the second row.
     */
    public	double	m10;

    /**
     *  The second element of the second row.
     */
    public	double	m11;

    /**
     *  The third element of the second row.
     */
    public	double	m12;

    /**
     *  The fourth element of the second row.
     */
    public	double	m13;

    /**
     *  The first element of the third row.
     */
    public	double	m20;

    /**
     *  The second element of the third row.
     */
    public	double	m21;

    /**
     *  The third element of the third row.
     */
    public	double	m22;

    /**
     *  The fourth element of the third row.
     */
    public	double	m23;

    /**
     *  The first element of the fourth row.
     */
    public	double	m30;

    /**
     *  The second element of the fourth row.
     */
    public	double	m31;

    /**
     *  The third element of the fourth row.
     */
    public	double	m32;

    /**
     *  The fourth element of the fourth row.
     */
    public	double	m33;
    /*
    double[] tmp = new double[16];
    double[] tmp_rot = new double[9];  // scratch matrix
    double[] tmp_scale = new double[3];  // scratch matrix
    */
    private static final double EPS = 1.0E-10;


    /**
     * Constructs and initializes a Matrix4d from the specified 16 values.
     * @param m00 the [0][0] element
     * @param m01 the [0][1] element
     * @param m02 the [0][2] element
     * @param m03 the [0][3] element
     * @param m10 the [1][0] element
     * @param m11 the [1][1] element
     * @param m12 the [1][2] element
     * @param m13 the [1][3] element
     * @param m20 the [2][0] element
     * @param m21 the [2][1] element
     * @param m22 the [2][2] element
     * @param m23 the [2][3] element
     * @param m30 the [3][0] element
     * @param m31 the [3][1] element
     * @param m32 the [3][2] element
     * @param m33 the [3][3] element
     */
    public Matrix4d(double m00, double m01, double m02, double m03,
		    double m10, double m11, double m12, double m13,
		    double m20, double m21, double m22, double m23,
		    double m30, double m31, double m32, double m33)
    {
	this.m00 = m00;
	this.m01 = m01;
	this.m02 = m02;
	this.m03 = m03;

	this.m10 = m10;
	this.m11 = m11;
	this.m12 = m12;
	this.m13 = m13;

	this.m20 = m20;
	this.m21 = m21;
	this.m22 = m22;
	this.m23 = m23;

	this.m30 = m30;
	this.m31 = m31;
	this.m32 = m32;
	this.m33 = m33;

    }

    /**
     * Constructs and initializes a Matrix4d from the specified 16
     * element array.  this.m00 =v[0], this.m01=v[1], etc.
     * @param v the array of length 16 containing in order
     */
    public Matrix4d(double[] v)
    {
	this.m00 = v[ 0];
	this.m01 = v[ 1];
	this.m02 = v[ 2];
	this.m03 = v[ 3];

	this.m10 = v[ 4];
	this.m11 = v[ 5];
	this.m12 = v[ 6];
	this.m13 = v[ 7];

	this.m20 = v[ 8];
	this.m21 = v[ 9];
	this.m22 = v[10];
	this.m23 = v[11];

	this.m30 = v[12];
	this.m31 = v[13];
	this.m32 = v[14];
	this.m33 = v[15];

    }

   /**
     * Constructs and initializes a Matrix4d from the quaternion,
     * translation, and scale values; the scale is applied only to the
     * rotational components of the matrix (upper 3x3) and not to the
     * translational components.
     * @param q1  the quaternion value representing the rotational component
     * @param t1  the translational component of the matrix
     * @param s   the scale value applied to the rotational components
     */
    public Matrix4d(Quat4d q1, Vector3d t1, double s)
    {
        m00 = s*(1.0 - 2.0*q1.y*q1.y - 2.0*q1.z*q1.z);
        m10 = s*(2.0*(q1.x*q1.y + q1.w*q1.z));
        m20 = s*(2.0*(q1.x*q1.z - q1.w*q1.y));

        m01 = s*(2.0*(q1.x*q1.y - q1.w*q1.z));
        m11 = s*(1.0 - 2.0*q1.x*q1.x - 2.0*q1.z*q1.z);
        m21 = s*(2.0*(q1.y*q1.z + q1.w*q1.x));

        m02 = s*(2.0*(q1.x*q1.z + q1.w*q1.y));
        m12 = s*(2.0*(q1.y*q1.z - q1.w*q1.x));
        m22 = s*(1.0 - 2.0*q1.x*q1.x - 2.0*q1.y*q1.y);

        m03 = t1.x;
        m13 = t1.y;
        m23 = t1.z;

        m30 = 0.0;
        m31 = 0.0;
        m32 = 0.0;
        m33 = 1.0;

    }

   /**
     * Constructs and initializes a Matrix4d from the quaternion,
     * translation, and scale values; the scale is applied only to the
     * rotational components of the matrix (upper 3x3) and not to the
     * translational components.
     * @param q1  the quaternion value representing the rotational component
     * @param t1  the translational component of the matrix
     * @param s   the scale value applied to the rotational components
     */
    public Matrix4d(Quat4f q1, Vector3d t1, double s)
    {
        m00 = s*(1.0 - 2.0*q1.y*q1.y - 2.0*q1.z*q1.z);
        m10 = s*(2.0*(q1.x*q1.y + q1.w*q1.z));
        m20 = s*(2.0*(q1.x*q1.z - q1.w*q1.y));

        m01 = s*(2.0*(q1.x*q1.y - q1.w*q1.z));
        m11 = s*(1.0 - 2.0*q1.x*q1.x - 2.0*q1.z*q1.z);
        m21 = s*(2.0*(q1.y*q1.z + q1.w*q1.x));

        m02 = s*(2.0*(q1.x*q1.z + q1.w*q1.y));
        m12 = s*(2.0*(q1.y*q1.z - q1.w*q1.x));
        m22 = s*(1.0 - 2.0*q1.x*q1.x - 2.0*q1.y*q1.y);

        m03 = t1.x;
        m13 = t1.y;
        m23 = t1.z;

        m30 = 0.0;
        m31 = 0.0;
        m32 = 0.0;
        m33 = 1.0;

    }

   /**
     *  Constructs a new matrix with the same values as the
     *  Matrix4d parameter.
     *  @param m1  the source matrix
     */
   public Matrix4d(Matrix4d m1)
   {
        this.m00 = m1.m00;
        this.m01 = m1.m01;
        this.m02 = m1.m02;
        this.m03 = m1.m03;

        this.m10 = m1.m10;
        this.m11 = m1.m11;
        this.m12 = m1.m12;
        this.m13 = m1.m13;

        this.m20 = m1.m20;
        this.m21 = m1.m21;
        this.m22 = m1.m22;
        this.m23 = m1.m23;

        this.m30 = m1.m30;
        this.m31 = m1.m31;
        this.m32 = m1.m32;
        this.m33 = m1.m33;

   }

   /**
     *  Constructs a new matrix with the same values as the
     *  Matrix4f parameter.
     *  @param m1  the source matrix
     */
   public Matrix4d(Matrix4f m1)
   {
        this.m00 = m1.m00;
        this.m01 = m1.m01;
        this.m02 = m1.m02;
        this.m03 = m1.m03;

        this.m10 = m1.m10;
        this.m11 = m1.m11;
        this.m12 = m1.m12;
        this.m13 = m1.m13;

        this.m20 = m1.m20;
        this.m21 = m1.m21;
        this.m22 = m1.m22;
        this.m23 = m1.m23;

        this.m30 = m1.m30;
        this.m31 = m1.m31;
        this.m32 = m1.m32;
        this.m33 = m1.m33;

   }

   /**
     * Constructs and initializes a Matrix4d from the rotation matrix,
     * translation, and scale values; the scale is applied only to the
     * rotational components of the matrix (upper 3x3) and not to the
     * translational components of the matrix.
     * @param m1  the rotation matrix representing the rotational components
     * @param t1  the translational components of the matrix
     * @param s   the scale value applied to the rotational components
     */
    public Matrix4d(Matrix3f m1, Vector3d t1, double s)
    {
        this.m00 = m1.m00*s;
        this.m01 = m1.m01*s;
        this.m02 = m1.m02*s;
        this.m03 = t1.x;

        this.m10 = m1.m10*s;
        this.m11 = m1.m11*s;
        this.m12 = m1.m12*s;
        this.m13 = t1.y;

        this.m20 = m1.m20*s;
        this.m21 = m1.m21*s;
        this.m22 = m1.m22*s;
        this.m23 = t1.z;

        this.m30 = 0.0;
        this.m31 = 0.0;
        this.m32 = 0.0;
        this.m33 = 1.0;

    }

   /**
     * Constructs and initializes a Matrix4f from the rotation matrix,
     * translation, and scale values; the scale is applied only to the
     * rotational components of the matrix (upper 3x3) and not to the
     * translational components of the matrix.
     * @param m1  the rotation matrix representing the rotational components
     * @param t1  the translational components of the matrix
     * @param s   the scale value applied to the rotational components
     */
    public Matrix4d(Matrix3d m1, Vector3d t1, double s)
    {
        this.m00 = m1.m00*s;
        this.m01 = m1.m01*s;
        this.m02 = m1.m02*s;
        this.m03 = t1.x;

        this.m10 = m1.m10*s;
        this.m11 = m1.m11*s;
        this.m12 = m1.m12*s;
        this.m13 = t1.y;

        this.m20 = m1.m20*s;
        this.m21 = m1.m21*s;
        this.m22 = m1.m22*s;
        this.m23 = t1.z;

        this.m30 = 0.0;
        this.m31 = 0.0;
        this.m32 = 0.0;
        this.m33 = 1.0;

    }

    /**
     * Constructs and initializes a Matrix4d to all zeros.
     */
    public Matrix4d()
    {
	this.m00 = 0.0;
	this.m01 = 0.0;
	this.m02 = 0.0;
	this.m03 = 0.0;

	this.m10 = 0.0;
	this.m11 = 0.0;
	this.m12 = 0.0;
	this.m13 = 0.0;

	this.m20 = 0.0;
	this.m21 = 0.0;
	this.m22 = 0.0;
	this.m23 = 0.0;

	this.m30 = 0.0;
	this.m31 = 0.0;
	this.m32 = 0.0;
	this.m33 = 0.0;

    }

   /**
     * Returns a string that contains the values of this Matrix4d.
     * @return the String representation
     */
    @Override
    public String toString() {
      return
	this.m00 + ", " + this.m01 + ", " + this.m02 + ", " + this.m03 + "\n" +
	this.m10 + ", " + this.m11 + ", " + this.m12 + ", " + this.m13 + "\n" +
	this.m20 + ", " + this.m21 + ", " + this.m22 + ", " + this.m23 + "\n" +
	this.m30 + ", " + this.m31 + ", " + this.m32 + ", " + this.m33 + "\n";
    }

    /**
     * Sets this Matrix4d to identity.
     */
    public final void setIdentity()
    {
	this.m00 = 1.0;
	this.m01 = 0.0;
	this.m02 = 0.0;
	this.m03 = 0.0;

	this.m10 = 0.0;
	this.m11 = 1.0;
	this.m12 = 0.0;
	this.m13 = 0.0;

	this.m20 = 0.0;
	this.m21 = 0.0;
	this.m22 = 1.0;
	this.m23 = 0.0;

	this.m30 = 0.0;
	this.m31 = 0.0;
	this.m32 = 0.0;
	this.m33 = 1.0;
    }

    /**
     * Sets the specified element of this matrix4f to the value provided.
     * @param row the row number to be modified (zero indexed)
     * @param column the column number to be modified (zero indexed)
     * @param value the new value
     */
    public final void setElement(int row, int column, double value)
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
	      case 3:
		this.m03 = value;
		break;
	      default:
		throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d0"));
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
	      case 3:
		this.m13 = value;
		break;
	      default:
		throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d0"));
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
	      case 3:
		this.m23 = value;
		break;
	      default:
		throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d0"));
	      }
	    break;

	  case 3:
	    switch(column)
	      {
	      case 0:
		this.m30 = value;
		break;
	      case 1:
		this.m31 = value;
		break;
	      case 2:
		this.m32 = value;
		break;
	      case 3:
		this.m33 = value;
		break;
	      default:
		throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d0"));
	      }
	    break;

	  default:
		throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d0"));
	  }
    }

    /**
     * Retrieves the value at the specified row and column of this matrix.
     * @param row the row number to be retrieved (zero indexed)
     * @param column the column number to be retrieved (zero indexed)
     * @return the value at the indexed element
     */
    public final double getElement(int row, int column)
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
	      case 3:
		return(this.m03);
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
	      case 3:
		return(this.m13);
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
	      case 3:
		return(this.m23);
	      default:
		break;
	      }
	    break;

	  case 3:
	    switch(column)
	      {
	      case 0:
		return(this.m30);
	      case 1:
		return(this.m31);
	      case 2:
		return(this.m32);
	      case 3:
		return(this.m33);
	      default:
		break;
	      }
	    break;

	  default:
	    break;
	  }
	throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d1"));
    }

    /**
     * Copies the matrix values in the specified row into the vector parameter.
     * @param row  the matrix row
     * @param v    the vector into which the matrix row values will be copied
     */
    public final void getRow(int row, Vector4d v) {
        if( row == 0 ) {
           v.x = m00;
           v.y = m01;
           v.z = m02;
           v.w = m03;
        } else if(row == 1) {
           v.x = m10;
           v.y = m11;
           v.z = m12;
           v.w = m13;
        } else if(row == 2) {
           v.x = m20;
           v.y = m21;
           v.z = m22;
           v.w = m23;
        } else if(row == 3) {
           v.x = m30;
           v.y = m31;
           v.z = m32;
           v.w = m33;
        } else {
          throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d2"));
        }
    }


    /**
     * Copies the matrix values in the specified row into the array parameter.
     * @param row  the matrix row
     * @param v    the array into which the matrix row values will be copied
     */
    public final void getRow(int row, double v[]) {
        if( row == 0 ) {
		v[0] = m00;
		v[1] = m01;
		v[2] = m02;
		v[3] = m03;
        } else if(row == 1) {
		v[0] = m10;
		v[1] = m11;
		v[2] = m12;
		v[3] = m13;
        } else if(row == 2) {
		v[0] = m20;
		v[1] = m21;
		v[2] = m22;
		v[3] = m23;
        } else if(row == 3) {
		v[0] = m30;
		v[1] = m31;
		v[2] = m32;
		v[3] = m33;
        } else {

          throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d2"));
        }
    }



    /**
     * Copies the matrix values in the specified column into the vector
     * parameter.
     * @param column  the matrix column
     * @param v    the vector into which the matrix column values will be copied
     */
    public final void getColumn(int column, Vector4d v) {
        if( column == 0 ) {
           v.x = m00;
           v.y = m10;
           v.z = m20;
           v.w = m30;
        } else if(column == 1) {
           v.x = m01;
           v.y = m11;
           v.z = m21;
           v.w = m31;
        } else if(column == 2) {
           v.x = m02;
           v.y = m12;
           v.z = m22;
           v.w = m32;
        } else if(column == 3) {
           v.x = m03;
           v.y = m13;
           v.z = m23;
           v.w = m33;
        } else {
          throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d3"));

        }

    }



    /**
     * Copies the matrix values in the specified column into the array
     * parameter.
     * @param column the matrix column
     * @param v    the array into which the matrix column values will be copied
     */
    public final void getColumn(int column, double v[]) {
        if( column == 0 ) {
           v[0] = m00;
           v[1] = m10;
           v[2] = m20;
           v[3] = m30;
        } else if(column == 1) {
           v[0] = m01;
           v[1] = m11;
           v[2] = m21;
           v[3] = m31;
        } else if(column == 2) {
           v[0] = m02;
           v[1] = m12;
           v[2] = m22;
           v[3] = m32;
        } else if(column == 3) {
           v[0] = m03;
           v[1] = m13;
           v[2] = m23;
           v[3] = m33;
        } else {
          throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d3"));

        }

    }


    /**
     * Performs an SVD normalization of this matrix in order to acquire
     * the normalized rotational component; the values are placed into
     * the Matrix3d parameter.
     * @param m1 the matrix into which the rotational component is placed
     */
    public final void get(Matrix3d m1)
    {

	double[]    tmp_rot = new double[9];  // scratch matrix
	double[]    tmp_scale = new double[3];  // scratch matrix
	getScaleRotate( tmp_scale, tmp_rot );

        m1.m00 = tmp_rot[0];
        m1.m01 = tmp_rot[1];
        m1.m02 = tmp_rot[2];

        m1.m10 = tmp_rot[3];
        m1.m11 = tmp_rot[4];
        m1.m12 = tmp_rot[5];

        m1.m20 = tmp_rot[6];
        m1.m21 = tmp_rot[7];
        m1.m22 = tmp_rot[8];

    }


    /**
     * Performs an SVD normalization of this matrix in order to acquire
     * the normalized rotational component; the values are placed into
     * the Matrix3f parameter.
     * @param m1 the matrix into which the rotational component is placed
     */
    public final void get(Matrix3f m1)
    {
	double[]    tmp_rot = new double[9];  // scratch matrix
	double[]    tmp_scale = new double[3];  // scratch matrix

	getScaleRotate( tmp_scale, tmp_rot );

        m1.m00 = (float)tmp_rot[0];
        m1.m01 = (float)tmp_rot[1];
        m1.m02 = (float)tmp_rot[2];

        m1.m10 = (float)tmp_rot[3];
        m1.m11 = (float)tmp_rot[4];
        m1.m12 = (float)tmp_rot[5];

        m1.m20 = (float)tmp_rot[6];
        m1.m21 = (float)tmp_rot[7];
        m1.m22 = (float)tmp_rot[8];
    }

   /**
     * Performs an SVD normalization of this matrix to calculate
     * the rotation as a 3x3 matrix, the translation, and the scale.
     * None of the matrix values are modified.
     * @param m1  the normalized matrix representing the rotation
     * @param t1  the translation component
     * @return  the scale component of this transform
     */
    public final double get(Matrix3d m1, Vector3d t1)
    {

	double[]    tmp_rot = new double[9];  // scratch matrix
	double[]    tmp_scale = new double[3];  // scratch matrix
	getScaleRotate( tmp_scale, tmp_rot );

        m1.m00 = tmp_rot[0];
        m1.m01 = tmp_rot[1];
        m1.m02 = tmp_rot[2];

        m1.m10 = tmp_rot[3];
        m1.m11 = tmp_rot[4];
        m1.m12 = tmp_rot[5];

        m1.m20 = tmp_rot[6];
        m1.m21 = tmp_rot[7];
        m1.m22 = tmp_rot[8];

        t1.x = m03;
        t1.y = m13;
        t1.z = m23;

        return( Matrix3d.max3( tmp_scale ));

    }

   /**
     * Performs an SVD normalization of this matrix to calculate
     * the rotation as a 3x3 matrix, the translation, and the scale.
     * None of the matrix values are modified.
     * @param m1  the normalized matrix representing the rotation
     * @param t1  the translation component
     * @return  the scale component of this transform
     */
    public final double get(Matrix3f m1, Vector3d t1){

	double[]    tmp_rot = new double[9];  // scratch matrix
	double[]    tmp_scale = new double[3];  // scratch matrix
        getScaleRotate( tmp_scale, tmp_rot );

        m1.m00 = (float)tmp_rot[0];
        m1.m01 = (float)tmp_rot[1];
        m1.m02 = (float)tmp_rot[2];

        m1.m10 = (float)tmp_rot[3];
        m1.m11 = (float)tmp_rot[4];
        m1.m12 = (float)tmp_rot[5];

        m1.m20 = (float)tmp_rot[6];
        m1.m21 = (float)tmp_rot[7];
        m1.m22 = (float)tmp_rot[8];

        t1.x = m03;
        t1.y = m13;
        t1.z = m23;

        return( Matrix3d.max3( tmp_scale ));

    }

    /**
     * Performs an SVD normalization of this matrix in order to acquire
     * the normalized rotational component; the values are placed into
     * the Quat4f parameter.
     * @param q1  quaternion into which the rotation component is placed
     */
    public final void get(Quat4f q1)
    {

	double[]    tmp_rot = new double[9];  // scratch matrix
	double[]    tmp_scale = new double[3];  // scratch matrix
        getScaleRotate( tmp_scale, tmp_rot );

        double ww;

        ww = 0.25*(1.0 + tmp_rot[0] + tmp_rot[4] + tmp_rot[8]);
        if(!((ww<0?-ww:ww) < 1.0e-30)) {
          q1.w = (float)Math.sqrt(ww);
          ww = 0.25/q1.w;
          q1.x = (float)((tmp_rot[7] - tmp_rot[5])*ww);
          q1.y = (float)((tmp_rot[2] - tmp_rot[6])*ww);
          q1.z = (float)((tmp_rot[3] - tmp_rot[1])*ww);
          return;
        }

        q1.w = 0.0f;
        ww = -0.5*(tmp_rot[4] + tmp_rot[8]);
        if(!((ww<0?-ww:ww) < 1.0e-30)) {
          q1.x =  (float)Math.sqrt(ww);
          ww = 0.5/q1.x;
          q1.y = (float)(tmp_rot[3]*ww);
          q1.z = (float)(tmp_rot[6]*ww);
          return;
        }

        q1.x = 0.0f;
        ww = 0.5*(1.0 - tmp_rot[8]);
        if(!((ww<0?-ww:ww) < 1.0e-30)) {
          q1.y =  (float)(Math.sqrt(ww));
          q1.z = (float)(tmp_rot[7]/(2.0*q1.y));
          return;
        }

        q1.y = 0.0f;
        q1.z = 1.0f;

    }

    /**
     * Performs an SVD normalization of q1 matrix in order to acquire
     * the normalized rotational component; the values are placed into
     * the Quat4d parameter.
     * @param q1  the quaternion into which the rotation component is placed
     */
    public final void get(Quat4d q1)
    {
	double[]    tmp_rot = new double[9];  // scratch matrix
	double[]    tmp_scale = new double[3];  // scratch matrix

	getScaleRotate( tmp_scale, tmp_rot );

        double ww;

        ww = 0.25*(1.0 + tmp_rot[0] + tmp_rot[4] + tmp_rot[8]);
        if(!((ww<0?-ww:ww) < 1.0e-30)) {
          q1.w = Math.sqrt(ww);
          ww = 0.25/q1.w;
          q1.x = (tmp_rot[7] - tmp_rot[5])*ww;
          q1.y = (tmp_rot[2] - tmp_rot[6])*ww;
          q1.z = (tmp_rot[3] - tmp_rot[1])*ww;
          return;
        }

        q1.w = 0.0f;
        ww = -0.5*(tmp_rot[4] + tmp_rot[8]);
        if(!((ww<0?-ww:ww) < 1.0e-30)) {
          q1.x =  Math.sqrt(ww);
          ww = 0.5/q1.x;
          q1.y = tmp_rot[3]*ww;
          q1.z = tmp_rot[6]*ww;
          return;
        }

        q1.x = 0.0;
        ww = 0.5*(1.0 - tmp_rot[8]);
        if(!((ww<0?-ww:ww) < 1.0e-30)) {
          q1.y =  Math.sqrt(ww);
          q1.z = tmp_rot[7]/(2.0*q1.y);
          return;
        }

        q1.y = 0.0;
        q1.z = 1.0;
    }

   /**
     * Retrieves the translational components of this matrix.
     * @param trans  the vector that will receive the translational component
     */
    public final void get(Vector3d trans)
    {
        trans.x = m03;
        trans.y = m13;
        trans.z = m23;
    }

  /**
    * Gets the upper 3x3 values of this matrix and places them into
    * the matrix m1.
    * @param m1  the matrix that will hold the values
    */
    public final void getRotationScale(Matrix3f m1)
    {
      m1.m00 = (float)m00; m1.m01 = (float)m01; m1.m02 = (float)m02;
      m1.m10 = (float)m10; m1.m11 = (float)m11; m1.m12 = (float)m12;
      m1.m20 = (float)m20; m1.m21 = (float)m21; m1.m22 = (float)m22;
    }

  /**
    * Gets the upper 3x3 values of this matrix and places them into
    * the matrix m1.
    * @param m1  the matrix that will hold the values
    */
    public final void getRotationScale(Matrix3d m1)
    {
      m1.m00 = m00; m1.m01 = m01; m1.m02 = m02;
      m1.m10 = m10; m1.m11 = m11; m1.m12 = m12;
      m1.m20 = m20; m1.m21 = m21; m1.m22 = m22;
    }

   /**
     * Performs an SVD normalization of this matrix to calculate
     * and return the uniform scale factor. If the matrix has non-uniform
     * scale factors, the largest of the x, y, and z scale factors will
     * be returned. This matrix is not modified.
     * @return  the scale factor of this matrix
     */
    public final double getScale()
    {

	double[]    tmp_rot = new double[9];  // scratch matrix
	double[]    tmp_scale = new double[3];  // scratch matrix
	getScaleRotate( tmp_scale, tmp_rot );

        return( Matrix3d.max3( tmp_scale ));

    }

  /**
   * Replaces the upper 3x3 matrix values of this matrix with the
   * values in the matrix m1.
   * @param m1  the matrix that will be the new upper 3x3
   */
    public final void setRotationScale(Matrix3d m1)
    {
      m00 = m1.m00; m01 = m1.m01; m02 = m1.m02;
      m10 = m1.m10; m11 = m1.m11; m12 = m1.m12;
      m20 = m1.m20; m21 = m1.m21; m22 = m1.m22;
    }

  /**
   * Replaces the upper 3x3 matrix values of this matrix with the
   * values in the matrix m1.
   * @param m1  the matrix that will be the new upper 3x3
   */
    public final void setRotationScale(Matrix3f m1)
    {
      m00 = m1.m00; m01 = m1.m01; m02 = m1.m02;
      m10 = m1.m10; m11 = m1.m11; m12 = m1.m12;
      m20 = m1.m20; m21 = m1.m21; m22 = m1.m22;
    }

   /**
     * Sets the scale component of the current matrix by factoring
     * out the current scale (by doing an SVD) from the rotational
     * component and multiplying by the new scale.
     * @param scale  the new scale amount
     */
    public final void setScale(double scale)
    {
	double[]    tmp_rot = new double[9];  // scratch matrix
	double[]    tmp_scale = new double[3];  // scratch matrix

	getScaleRotate( tmp_scale, tmp_rot );

        m00 = tmp_rot[0]*scale;
        m01 = tmp_rot[1]*scale;
        m02 = tmp_rot[2]*scale;

        m10 = tmp_rot[3]*scale;
        m11 = tmp_rot[4]*scale;
        m12 = tmp_rot[5]*scale;

        m20 = tmp_rot[6]*scale;
        m21 = tmp_rot[7]*scale;
        m22 = tmp_rot[8]*scale;

    }

    /**
     * Sets the specified row of this matrix4d to the four values provided.
     * @param row the row number to be modified (zero indexed)
     * @param x the first column element
     * @param y the second column element
     * @param z the third column element
     * @param w the fourth column element
     */
    public final void setRow(int row, double x, double y, double z, double w)
    {
	switch (row) {
	case 0:
	    this.m00 = x;
	    this.m01 = y;
	    this.m02 = z;
	    this.m03 = w;
	    break;

	case 1:
	    this.m10 = x;
	    this.m11 = y;
	    this.m12 = z;
	    this.m13 = w;
	    break;

	case 2:
	    this.m20 = x;
	    this.m21 = y;
	    this.m22 = z;
	    this.m23 = w;
	    break;

	case 3:
	    this.m30 = x;
	    this.m31 = y;
	    this.m32 = z;
	    this.m33 = w;
	    break;

	default:
            throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d4"));

	}
    }

    /**
     * Sets the specified row of this matrix4d to the Vector provided.
     * @param row the row number to be modified (zero indexed)
     * @param v the replacement row
     */
    public final void setRow(int row, Vector4d v)
    {
	switch (row) {
	case 0:
	    this.m00 = v.x;
	    this.m01 = v.y;
	    this.m02 = v.z;
	    this.m03 = v.w;
	    break;

	case 1:
	    this.m10 = v.x;
	    this.m11 = v.y;
	    this.m12 = v.z;
	    this.m13 = v.w;
	    break;

	case 2:
	    this.m20 = v.x;
	    this.m21 = v.y;
	    this.m22 = v.z;
	    this.m23 = v.w;
	    break;

	case 3:
	    this.m30 = v.x;
	    this.m31 = v.y;
	    this.m32 = v.z;
	    this.m33 = v.w;
	    break;

	default:
            throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d4"));
	}
    }

    /**
     * Sets the specified row of this matrix4d to the four values provided.
     * @param row the row number to be modified (zero indexed)
     * @param v the replacement row
     */
    public final void setRow(int row, double v[])
    {
	switch (row) {
	case 0:
	    this.m00 = v[0];
	    this.m01 = v[1];
	    this.m02 = v[2];
	    this.m03 = v[3];
	    break;

	case 1:
	    this.m10 = v[0];
	    this.m11 = v[1];
	    this.m12 = v[2];
	    this.m13 = v[3];
	    break;

	case 2:
	    this.m20 = v[0];
	    this.m21 = v[1];
	    this.m22 = v[2];
	    this.m23 = v[3];
	    break;

	case 3:
	    this.m30 = v[0];
	    this.m31 = v[1];
	    this.m32 = v[2];
	    this.m33 = v[3];
	    break;

	default:
            throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d4"));
	}
    }

    /**
     * Sets the specified column of this matrix4d to the four values provided.
     * @param column the column number to be modified (zero indexed)
     * @param x the first row element
     * @param y the second row element
     * @param z the third row element
     * @param w the fourth row element
     */
    public final void setColumn(int column, double x, double y, double z, double w)
    {
	switch (column) {
	case 0:
	    this.m00 = x;
	    this.m10 = y;
	    this.m20 = z;
	    this.m30 = w;
	    break;

	case 1:
	    this.m01 = x;
	    this.m11 = y;
	    this.m21 = z;
	    this.m31 = w;
	    break;

	case 2:
	    this.m02 = x;
	    this.m12 = y;
	    this.m22 = z;
	    this.m32 = w;
	    break;

	case 3:
	    this.m03 = x;
	    this.m13 = y;
	    this.m23 = z;
	    this.m33 = w;
	    break;

	default:
            throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d7"));
	}
    }

    /**
     * Sets the specified column of this matrix4d to the vector provided.
     * @param column the column number to be modified (zero indexed)
     * @param v the replacement column
     */
    public final void setColumn(int column, Vector4d v)
    {
	switch (column) {
	case 0:
	    this.m00 = v.x;
	    this.m10 = v.y;
	    this.m20 = v.z;
	    this.m30 = v.w;
	    break;

	case 1:
	    this.m01 = v.x;
	    this.m11 = v.y;
	    this.m21 = v.z;
	    this.m31 = v.w;
	    break;

	case 2:
	    this.m02 = v.x;
	    this.m12 = v.y;
	    this.m22 = v.z;
	    this.m32 = v.w;
	    break;

	case 3:
	    this.m03 = v.x;
	    this.m13 = v.y;
	    this.m23 = v.z;
	    this.m33 = v.w;
	    break;

	default:
            throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d7"));
	}
    }

    /**
     * Sets the specified column of this matrix4d to the four values provided.
     * @param column the column number to be modified (zero indexed)
     * @param v the replacement column
     */
    public final void setColumn(int column, double v[])
    {
	switch (column) {
	case 0:
	    this.m00 = v[0];
	    this.m10 = v[1];
	    this.m20 = v[2];
	    this.m30 = v[3];
	    break;

	case 1:
	    this.m01 = v[0];
	    this.m11 = v[1];
	    this.m21 = v[2];
	    this.m31 = v[3];
	    break;

	case 2:
	    this.m02 = v[0];
	    this.m12 = v[1];
	    this.m22 = v[2];
	    this.m32 = v[3];
	    break;

	case 3:
	    this.m03 = v[0];
	    this.m13 = v[1];
	    this.m23 = v[2];
	    this.m33 = v[3];
	    break;

	default:
            throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d7"));
	}
    }

   /**
     *  Adds a scalar to each component of this matrix.
     *  @param scalar  the scalar adder
     */
    public final void add(double scalar)
    {
        m00 += scalar;
        m01 += scalar;
        m02 += scalar;
        m03 += scalar;
        m10 += scalar;
        m11 += scalar;
        m12 += scalar;
        m13 += scalar;
        m20 += scalar;
        m21 += scalar;
        m22 += scalar;
        m23 += scalar;
        m30 += scalar;
        m31 += scalar;
        m32 += scalar;
        m33 += scalar;
    }

   /**
     *  Adds a scalar to each component of the matrix m1 and places
     *  the result into this.  Matrix m1 is not modified.
     *  @param scalar  the scalar adder
     *  @param m1  the original matrix values
     */
    public final void add(double scalar, Matrix4d m1)
    {
        this.m00 = m1.m00 +  scalar;
        this.m01 = m1.m01 +  scalar;
        this.m02 = m1.m02 +  scalar;
        this.m03 = m1.m03 +  scalar;
        this.m10 = m1.m10 +  scalar;
        this.m11 = m1.m11 +  scalar;
        this.m12 = m1.m12 +  scalar;
        this.m13 = m1.m13 +  scalar;
        this.m20 = m1.m20 +  scalar;
        this.m21 = m1.m21 +  scalar;
        this.m22 = m1.m22 +  scalar;
        this.m23 = m1.m23 +  scalar;
        this.m30 = m1.m30 +  scalar;
        this.m31 = m1.m31 +  scalar;
        this.m32 = m1.m32 +  scalar;
        this.m33 = m1.m33 +  scalar;
    }

    /**
     * Sets the value of this matrix to the matrix sum of matrices m1 and m2.
     * @param m1 the first matrix
     * @param m2 the second matrix
     */
    public final void add(Matrix4d m1, Matrix4d m2)
    {
	this.m00 = m1.m00 + m2.m00;
	this.m01 = m1.m01 + m2.m01;
	this.m02 = m1.m02 + m2.m02;
	this.m03 = m1.m03 + m2.m03;

	this.m10 = m1.m10 + m2.m10;
	this.m11 = m1.m11 + m2.m11;
	this.m12 = m1.m12 + m2.m12;
	this.m13 = m1.m13 + m2.m13;

	this.m20 = m1.m20 + m2.m20;
	this.m21 = m1.m21 + m2.m21;
	this.m22 = m1.m22 + m2.m22;
	this.m23 = m1.m23 + m2.m23;

	this.m30 = m1.m30 + m2.m30;
	this.m31 = m1.m31 + m2.m31;
	this.m32 = m1.m32 + m2.m32;
	this.m33 = m1.m33 + m2.m33;
    }

    /**
     * Sets the value of this matrix to sum of itself and matrix m1.
     * @param m1 the other matrix
     */
    public final void add(Matrix4d m1)
    {
        this.m00 += m1.m00;
        this.m01 += m1.m01;
        this.m02 += m1.m02;
        this.m03 += m1.m03;

        this.m10 += m1.m10;
        this.m11 += m1.m11;
        this.m12 += m1.m12;
        this.m13 += m1.m13;

        this.m20 += m1.m20;
        this.m21 += m1.m21;
        this.m22 += m1.m22;
        this.m23 += m1.m23;

        this.m30 += m1.m30;
        this.m31 += m1.m31;
        this.m32 += m1.m32;
        this.m33 += m1.m33;
    }

    /**
     * Sets the value of this matrix to the matrix difference
     * of matrices m1 and m2.
     * @param m1 the first matrix
     * @param m2 the second matrix
     */
    public final void sub(Matrix4d m1, Matrix4d m2)
    {
	this.m00 = m1.m00 - m2.m00;
	this.m01 = m1.m01 - m2.m01;
	this.m02 = m1.m02 - m2.m02;
	this.m03 = m1.m03 - m2.m03;

	this.m10 = m1.m10 - m2.m10;
	this.m11 = m1.m11 - m2.m11;
	this.m12 = m1.m12 - m2.m12;
	this.m13 = m1.m13 - m2.m13;

	this.m20 = m1.m20 - m2.m20;
	this.m21 = m1.m21 - m2.m21;
	this.m22 = m1.m22 - m2.m22;
	this.m23 = m1.m23 - m2.m23;

	this.m30 = m1.m30 - m2.m30;
	this.m31 = m1.m31 - m2.m31;
	this.m32 = m1.m32 - m2.m32;
	this.m33 = m1.m33 - m2.m33;
    }


    /**
     * Sets the value of this matrix to the matrix difference of itself
     * and matrix m1 (this = this - m1).
     * @param m1 the other matrix
     */
    public final void sub(Matrix4d m1)
    {
        this.m00 -= m1.m00;
        this.m01 -= m1.m01;
        this.m02 -= m1.m02;
        this.m03 -= m1.m03;

        this.m10 -= m1.m10;
        this.m11 -= m1.m11;
        this.m12 -= m1.m12;
        this.m13 -= m1.m13;

        this.m20 -= m1.m20;
        this.m21 -= m1.m21;
        this.m22 -= m1.m22;
        this.m23 -= m1.m23;

        this.m30 -= m1.m30;
        this.m31 -= m1.m31;
        this.m32 -= m1.m32;
        this.m33 -= m1.m33;
    }

    /**
     * Sets the value of this matrix to its transpose.
     */
    public final void transpose()
    {
	double temp;

	temp = this.m10;
	this.m10 = this.m01;
	this.m01 = temp;

	temp = this.m20;
	this.m20 = this.m02;
	this.m02 = temp;

	temp = this.m30;
	this.m30 = this.m03;
	this.m03 = temp;

	temp = this.m21;
	this.m21 = this.m12;
	this.m12 = temp;

	temp = this.m31;
	this.m31 = this.m13;
	this.m13 = temp;

	temp = this.m32;
	this.m32 = this.m23;
	this.m23 = temp;
    }

    /**
     * Sets the value of this matrix to the transpose of the argument matrix
     * @param m1 the matrix to be transposed
     */
    public final void transpose(Matrix4d m1)
    {
	if (this != m1) {
	    this.m00 = m1.m00;
	    this.m01 = m1.m10;
	    this.m02 = m1.m20;
	    this.m03 = m1.m30;

	    this.m10 = m1.m01;
	    this.m11 = m1.m11;
	    this.m12 = m1.m21;
	    this.m13 = m1.m31;

	    this.m20 = m1.m02;
	    this.m21 = m1.m12;
	    this.m22 = m1.m22;
	    this.m23 = m1.m32;

	    this.m30 = m1.m03;
	    this.m31 = m1.m13;
	    this.m32 = m1.m23;
	    this.m33 = m1.m33;
	} else
	    this.transpose();
    }

    /**
     *  Sets the values in this Matrix4d equal to the row-major
     *  array parameter (ie, the first four elements of the
     *  array will be copied into the first row of this matrix, etc.).
     *  @param m  the double precision array of length 16
     */
    public final void set(double[] m)
    {
          m00 = m[0];
          m01 = m[1];
          m02 = m[2];
          m03 = m[3];
          m10 = m[4];
          m11 = m[5];
          m12 = m[6];
          m13 = m[7];
          m20 = m[8];
          m21 = m[9];
          m22 = m[10];
          m23 = m[11];
          m30 = m[12];
          m31 = m[13];
          m32 = m[14];
          m33 = m[15];
    }

    /**
     * Sets the rotational component (upper 3x3) of this matrix to the
     * matrix values in the single precision Matrix3f argument; the other
     * elements of this matrix are initialized as if this were an identity
     * matrix (i.e., affine matrix with no translational component).
     * @param m1   the double precision 3x3 matrix
     */
    public final void set(Matrix3f m1)
    {
       m00 = m1.m00; m01 = m1.m01; m02 = m1.m02; m03 = 0.0;
       m10 = m1.m10; m11 = m1.m11; m12 = m1.m12; m13 = 0.0;
       m20 = m1.m20; m21 = m1.m21; m22 = m1.m22; m23 = 0.0;
       m30 = 0.0;    m31 = 0.0   ; m32 = 0.0   ; m33 = 1.0;
    }

    /**
     * Sets the rotational component (upper 3x3) of this matrix to the
     * matrix values in the double precision Matrix3d argument; the other
     * elements of this matrix are initialized as if this were an identity
     * matrix (i.e., affine matrix with no translational component).
     * @param m1   the double precision 3x3 matrix
     */
    public final void set(Matrix3d m1)
    {
       m00 = m1.m00; m01 = m1.m01; m02 = m1.m02; m03 = 0.0;
       m10 = m1.m10; m11 = m1.m11; m12 = m1.m12; m13 = 0.0;
       m20 = m1.m20; m21 = m1.m21; m22 = m1.m22; m23 = 0.0;
       m30 = 0.0;    m31 = 0.0   ; m32 = 0.0   ; m33 = 1.0;
    }

    /**
     * Sets the value of this matrix to the matrix conversion of the
     * (double precision) quaternion argument.
     * @param q1 the quaternion to be converted
     */
    public final void set(Quat4d q1)
    {
	this.m00 = (1.0 - 2.0*q1.y*q1.y - 2.0*q1.z*q1.z);
	this.m10 = (2.0*(q1.x*q1.y + q1.w*q1.z));
	this.m20 = (2.0*(q1.x*q1.z - q1.w*q1.y));

	this.m01 = (2.0*(q1.x*q1.y - q1.w*q1.z));
	this.m11 = (1.0 - 2.0*q1.x*q1.x - 2.0*q1.z*q1.z);
	this.m21 = (2.0*(q1.y*q1.z + q1.w*q1.x));

	this.m02 = (2.0*(q1.x*q1.z + q1.w*q1.y));
	this.m12 = (2.0*(q1.y*q1.z - q1.w*q1.x));
	this.m22 = (1.0 - 2.0*q1.x*q1.x - 2.0*q1.y*q1.y);

	this.m03 = 0.0;
	this.m13 = 0.0;
	this.m23 = 0.0;

	this.m30 = 0.0;
	this.m31 = 0.0;
	this.m32 = 0.0;
	this.m33 = 1.0;
    }

    /**
     * Sets the value of this matrix to the matrix conversion of the
     * double precision axis and angle argument.
     * @param a1 the axis and angle to be converted
     */
    public final void set(AxisAngle4d a1)
    {
      double mag = Math.sqrt( a1.x*a1.x + a1.y*a1.y + a1.z*a1.z);

      if( mag < EPS ) {
	 m00 = 1.0;
	 m01 = 0.0;
	 m02 = 0.0;

	 m10 = 0.0;
	 m11 = 1.0;
	 m12 = 0.0;

	 m20 = 0.0;
	 m21 = 0.0;
	 m22 = 1.0;
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

      m03 = 0.0;
      m13 = 0.0;
      m23 = 0.0;

      m30 = 0.0;
      m31 = 0.0;
      m32 = 0.0;
      m33 = 1.0;
    }

    /**
     * Sets the value of this matrix to the matrix conversion of the
     * single precision quaternion argument.
     * @param q1 the quaternion to be converted
     */
    public final void set(Quat4f q1)
    {
	this.m00 = (1.0 - 2.0*q1.y*q1.y - 2.0*q1.z*q1.z);
	this.m10 = (2.0*(q1.x*q1.y + q1.w*q1.z));
	this.m20 = (2.0*(q1.x*q1.z - q1.w*q1.y));

	this.m01 = (2.0*(q1.x*q1.y - q1.w*q1.z));
	this.m11 = (1.0 - 2.0*q1.x*q1.x - 2.0*q1.z*q1.z);
	this.m21 = (2.0*(q1.y*q1.z + q1.w*q1.x));

	this.m02 = (2.0*(q1.x*q1.z + q1.w*q1.y));
	this.m12 = (2.0*(q1.y*q1.z - q1.w*q1.x));
	this.m22 = (1.0 - 2.0*q1.x*q1.x - 2.0*q1.y*q1.y);

	this.m03 = 0.0;
	this.m13 = 0.0;
	this.m23 = 0.0;

	this.m30 = 0.0;
	this.m31 = 0.0;
	this.m32 = 0.0;
	this.m33 = 1.0;
    }

    /**
     * Sets the value of this matrix to the matrix conversion of the
     * single precision axis and angle argument.
     * @param a1 the axis and angle to be converted
     */
    public final void set(AxisAngle4f a1)
    {
      double mag = Math.sqrt( a1.x*a1.x + a1.y*a1.y + a1.z*a1.z);

      if( mag < EPS ) {
 	 m00 = 1.0;
	 m01 = 0.0;
	 m02 = 0.0;

	 m10 = 0.0;
	 m11 = 1.0;
	 m12 = 0.0;

	 m20 = 0.0;
	 m21 = 0.0;
	 m22 = 1.0;
      } else {
	 mag = 1.0/mag;
         double ax = a1.x*mag;
         double ay = a1.y*mag;
         double az = a1.z*mag;

         double sinTheta = Math.sin((double)a1.angle);
         double cosTheta = Math.cos((double)a1.angle);
         double t = 1.0 - cosTheta;

         double xz = ax * az;
         double xy = ax * ay;
         double yz = ay * az;

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
      m03 = 0.0;
      m13 = 0.0;
      m23 = 0.0;

      m30 = 0.0;
      m31 = 0.0;
      m32 = 0.0;
      m33 = 1.0;
    }

    /**
     * Sets the value of this matrix from the rotation expressed
     * by the quaternion q1, the translation t1, and the scale s.
     * @param q1 the rotation expressed as a quaternion
     * @param t1 the translation
     * @param s the scale value
     */
    public final void set(Quat4d q1, Vector3d t1, double s)
    {
        this.m00 = s*(1.0 - 2.0*q1.y*q1.y - 2.0*q1.z*q1.z);
        this.m10 = s*(2.0*(q1.x*q1.y + q1.w*q1.z));
        this.m20 = s*(2.0*(q1.x*q1.z - q1.w*q1.y));

        this.m01 = s*(2.0*(q1.x*q1.y - q1.w*q1.z));
        this.m11 = s*(1.0 - 2.0*q1.x*q1.x - 2.0*q1.z*q1.z);
        this.m21 = s*(2.0*(q1.y*q1.z + q1.w*q1.x));

        this.m02 = s*(2.0*(q1.x*q1.z + q1.w*q1.y));
        this.m12 = s*(2.0*(q1.y*q1.z - q1.w*q1.x));
        this.m22 = s*(1.0 - 2.0*q1.x*q1.x - 2.0*q1.y*q1.y);

        this.m03 = t1.x;
        this.m13 = t1.y;
        this.m23 = t1.z;

        this.m30 = 0.0;
        this.m31 = 0.0;
        this.m32 = 0.0;
        this.m33 = 1.0;
    }

    /**
     * Sets the value of this matrix from the rotation expressed
     * by the quaternion q1, the translation t1, and the scale s.
     * @param q1 the rotation expressed as a quaternion
     * @param t1 the translation
     * @param s the scale value
     */
    public final void set(Quat4f q1, Vector3d t1, double s)
    {
	this.m00 = s*(1.0 - 2.0*q1.y*q1.y - 2.0*q1.z*q1.z);
	this.m10 = s*(2.0*(q1.x*q1.y + q1.w*q1.z));
	this.m20 = s*(2.0*(q1.x*q1.z - q1.w*q1.y));

	this.m01 = s*(2.0*(q1.x*q1.y - q1.w*q1.z));
	this.m11 = s*(1.0 - 2.0*q1.x*q1.x - 2.0*q1.z*q1.z);
	this.m21 = s*(2.0*(q1.y*q1.z + q1.w*q1.x));

	this.m02 = s*(2.0*(q1.x*q1.z + q1.w*q1.y));
	this.m12 = s*(2.0*(q1.y*q1.z - q1.w*q1.x));
	this.m22 = s*(1.0 - 2.0*q1.x*q1.x - 2.0*q1.y*q1.y);

	this.m03 = t1.x;
	this.m13 = t1.y;
	this.m23 = t1.z;

	this.m30 = 0.0;
	this.m31 = 0.0;
	this.m32 = 0.0;
	this.m33 = 1.0;
    }

    /**
     * Sets the value of this matrix from the rotation expressed
     * by the quaternion q1, the translation t1, and the scale s.
     * @param q1 the rotation expressed as a quaternion
     * @param t1 the translation
     * @param s the scale value
     */
    public final void set(Quat4f q1, Vector3f t1, float s)
   {
	this.m00 = s*(1.0 - 2.0*q1.y*q1.y - 2.0*q1.z*q1.z);
	this.m10 = s*(2.0*(q1.x*q1.y + q1.w*q1.z));
	this.m20 = s*(2.0*(q1.x*q1.z - q1.w*q1.y));

	this.m01 = s*(2.0*(q1.x*q1.y - q1.w*q1.z));
	this.m11 = s*(1.0 - 2.0*q1.x*q1.x - 2.0*q1.z*q1.z);
	this.m21 = s*(2.0*(q1.y*q1.z + q1.w*q1.x));

	this.m02 = s*(2.0*(q1.x*q1.z + q1.w*q1.y));
        this.m12 = s*(2.0*(q1.y*q1.z - q1.w*q1.x));
	this.m22 = s*(1.0 - 2.0*q1.x*q1.x - 2.0*q1.y*q1.y);

	this.m03 = t1.x;
	this.m13 = t1.y;
	this.m23 = t1.z;

	this.m30 = 0.0;
	this.m31 = 0.0;
	this.m32 = 0.0;
	this.m33 = 1.0;
   }

    /**
     * Sets the value of this matrix to a copy of the
     * passed matrix m1.
     * @param m1 the matrix4f
     */
    public final void set(Matrix4f m1)
    {
	this.m00 = m1.m00;
	this.m01 = m1.m01;
	this.m02 = m1.m02;
	this.m03 = m1.m03;

	this.m10 = m1.m10;
	this.m11 = m1.m11;
	this.m12 = m1.m12;
	this.m13 = m1.m13;

	this.m20 = m1.m20;
	this.m21 = m1.m21;
	this.m22 = m1.m22;
	this.m23 = m1.m23;

	this.m30 = m1.m30;
	this.m31 = m1.m31;
	this.m32 = m1.m32;
	this.m33 = m1.m33;
    }

    /**
     * Sets the value of this matrix to a copy of the
     * passed matrix m1.
     * @param m1 the matrix to be copied
     */
    public final void set(Matrix4d m1)
    {
	this.m00 = m1.m00;
	this.m01 = m1.m01;
	this.m02 = m1.m02;
	this.m03 = m1.m03;

	this.m10 = m1.m10;
	this.m11 = m1.m11;
	this.m12 = m1.m12;
	this.m13 = m1.m13;

	this.m20 = m1.m20;
	this.m21 = m1.m21;
	this.m22 = m1.m22;
	this.m23 = m1.m23;

	this.m30 = m1.m30;
	this.m31 = m1.m31;
	this.m32 = m1.m32;
	this.m33 = m1.m33;
    }

  /**
   * Sets the value of this matrix to the matrix inverse
   * of the passed (user declared) matrix m1.
   * @param m1 the matrix to be inverted
   */
  public final void invert(Matrix4d m1)
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
    final void invertGeneral(Matrix4d  m1) {
	double result[] = new double[16];
	int row_perm[] = new int[4];
	int i;

	// Use LU decomposition and backsubstitution code specifically
	// for floating-point 4x4 matrices.
	double[]    tmp = new double[16];  // scratch matrix
	// Copy source matrix to t1tmp
        tmp[0] = m1.m00;
        tmp[1] = m1.m01;
        tmp[2] = m1.m02;
        tmp[3] = m1.m03;

        tmp[4] = m1.m10;
        tmp[5] = m1.m11;
        tmp[6] = m1.m12;
        tmp[7] = m1.m13;

        tmp[8] = m1.m20;
        tmp[9] = m1.m21;
        tmp[10] = m1.m22;
        tmp[11] = m1.m23;

        tmp[12] = m1.m30;
        tmp[13] = m1.m31;
        tmp[14] = m1.m32;
        tmp[15] = m1.m33;

	// Calculate LU decomposition: Is the matrix singular?
	if (!luDecomposition(tmp, row_perm)) {
	    // Matrix has no inverse
	    throw new SingularMatrixException(VecMathI18N.getString("Matrix4d10"));
	}

	// Perform back substitution on the identity matrix
        for(i=0;i<16;i++) result[i] = 0.0;
        result[0] = 1.0; result[5] = 1.0; result[10] = 1.0; result[15] = 1.0;
	luBacksubstitution(tmp, row_perm, result);

        this.m00 = result[0];
        this.m01 = result[1];
        this.m02 = result[2];
        this.m03 = result[3];

        this.m10 = result[4];
        this.m11 = result[5];
        this.m12 = result[6];
        this.m13 = result[7];

        this.m20 = result[8];
        this.m21 = result[9];
        this.m22 = result[10];
        this.m23 = result[11];

        this.m30 = result[12];
        this.m31 = result[13];
        this.m32 = result[14];
        this.m33 = result[15];

    }

    /**
     * Given a 4x4 array "matrix0", this function replaces it with the
     * LU decomposition of a row-wise permutation of itself.  The input
     * parameters are "matrix0" and "dimen".  The array "matrix0" is also
     * an output parameter.  The vector "row_perm[4]" is an output
     * parameter that contains the row permutations resulting from partial
     * pivoting.  The output parameter "even_row_xchg" is 1 when the
     * number of row exchanges is even, or -1 otherwise.  Assumes data
     * type is always double.
     *
     * This function is similar to luDecomposition, except that it
     * is tuned specifically for 4x4 matrices.
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

	double row_scale[] = new double[4];

	// Determine implicit scaling information by looping over rows
	{
	    int i, j;
	    int ptr, rs;
	    double big, temp;

	    ptr = 0;
	    rs = 0;

	    // For each row ...
	    i = 4;
	    while (i-- != 0) {
		big = 0.0;

		// For each column, find the largest element in the row
		j = 4;
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
	    for (j = 0; j < 4; j++) {
		int i, imax, k;
		int target, p1, p2;
		double sum, big, temp;

		// Determine elements of upper diagonal matrix U
		for (i = 0; i < j; i++) {
		    target = mtx + (4*i) + j;
		    sum = matrix0[target];
		    k = i;
		    p1 = mtx + (4*i);
		    p2 = mtx + j;
		    while (k-- != 0) {
			sum -= matrix0[p1] * matrix0[p2];
			p1++;
			p2 += 4;
		    }
		    matrix0[target] = sum;
		}

		// Search for largest pivot element and calculate
		// intermediate elements of lower diagonal matrix L.
		big = 0.0;
		imax = -1;
		for (i = j; i < 4; i++) {
		    target = mtx + (4*i) + j;
		    sum = matrix0[target];
		    k = j;
		    p1 = mtx + (4*i);
		    p2 = mtx + j;
		    while (k-- != 0) {
			sum -= matrix0[p1] * matrix0[p2];
			p1++;
			p2 += 4;
		    }
		    matrix0[target] = sum;

		    // Is this the best pivot so far?
		    if ((temp = row_scale[i] * Math.abs(sum)) >= big) {
			big = temp;
			imax = i;
		    }
		}

		if (imax < 0) {
		    throw new RuntimeException(VecMathI18N.getString("Matrix4d11"));
		}

		// Is a row exchange necessary?
		if (j != imax) {
		    // Yes: exchange rows
		    k = 4;
		    p1 = mtx + (4*imax);
		    p2 = mtx + (4*j);
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
		if (matrix0[(mtx + (4*j) + j)] == 0.0) {
		    return false;
		}

		// Divide elements of lower diagonal matrix L by pivot
		if (j != (4-1)) {
		    temp = 1.0 / (matrix0[(mtx + (4*j) + j)]);
		    target = mtx + (4*(j+1)) + j;
		    i = 3 - j;
		    while (i-- != 0) {
			matrix0[target] *= temp;
			target += 4;
		    }
		}
	    }
	}

	return true;
    }

    /**
     * Solves a set of linear equations.  The input parameters "matrix1",
     * and "row_perm" come from luDecompostionD4x4 and do not change
     * here.  The parameter "matrix2" is a set of column vectors assembled
     * into a 4x4 matrix of floating-point values.  The procedure takes each
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
	for (k = 0; k < 4; k++) {
	    //	    cv = &(matrix2[0][k]);
	    cv = k;
	    ii = -1;

	    // Forward substitution
	    for (i = 0; i < 4; i++) {
		double sum;

		ip = row_perm[rp+i];
		sum = matrix2[cv+4*ip];
		matrix2[cv+4*ip] = matrix2[cv+4*i];
		if (ii >= 0) {
		    //		    rv = &(matrix1[i][0]);
		    rv = i*4;
		    for (j = ii; j <= i-1; j++) {
			sum -= matrix1[rv+j] * matrix2[cv+4*j];
		    }
		}
		else if (sum != 0.0) {
		    ii = i;
		}
		matrix2[cv+4*i] = sum;
	    }

	    // Backsubstitution
	    //	    rv = &(matrix1[3][0]);
	    rv = 3*4;
	    matrix2[cv+4*3] /= matrix1[rv+3];

	    rv -= 4;
	    matrix2[cv+4*2] = (matrix2[cv+4*2] -
			    matrix1[rv+3] * matrix2[cv+4*3]) / matrix1[rv+2];

	    rv -= 4;
	    matrix2[cv+4*1] = (matrix2[cv+4*1] -
			    matrix1[rv+2] * matrix2[cv+4*2] -
			    matrix1[rv+3] * matrix2[cv+4*3]) / matrix1[rv+1];

	    rv -= 4;
	    matrix2[cv+4*0] = (matrix2[cv+4*0] -
			    matrix1[rv+1] * matrix2[cv+4*1] -
			    matrix1[rv+2] * matrix2[cv+4*2] -
			    matrix1[rv+3] * matrix2[cv+4*3]) / matrix1[rv+0];
	}
    }

    /**
     * Computes the determinant of this matrix.
     * @return the determinant of the matrix
     */
    public final double determinant()
    {
       double det;

       // cofactor exapainsion along first row

	det = m00*(m11*m22*m33+ m12*m23*m31 + m13*m21*m32
		 - m13*m22*m31 -m11*m23*m32 - m12*m21*m33);
	det -= m01*(m10*m22*m33+ m12*m23*m30 + m13*m20*m32
 		  - m13*m22*m30 -m10*m23*m32 - m12*m20*m33);
	det += m02*(m10*m21*m33+ m11*m23*m30 + m13*m20*m31
 		  - m13*m21*m30 -m10*m23*m31 - m11*m20*m33);
	det -= m03*(m10*m21*m32+ m11*m22*m30 + m12*m20*m31
		  - m12*m21*m30 -m10*m22*m31 - m11*m20*m32);

        return( det );
    }

    /**
     * Sets the value of this matrix to a scale matrix with the
     * passed scale amount.
     * @param scale the scale factor for the matrix
     */
    public final void set(double scale)
    {
	this.m00 = scale;
	this.m01 = 0.0;
	this.m02 = 0.0;
	this.m03 = 0.0;

	this.m10 = 0.0;
	this.m11 = scale;
	this.m12 = 0.0;
	this.m13 = 0.0;

	this.m20 = 0.0;
	this.m21 = 0.0;
	this.m22 = scale;
	this.m23 = 0.0;

	this.m30 = 0.0;
	this.m31 = 0.0;
	this.m32 = 0.0;
	this.m33 = 1.0;
    }

    /**
     * Sets the value of this matrix to a translate matrix by the
     * passed translation value.
     * @param v1 the translation amount
     */
    public final void set(Vector3d v1)
    {
	this.m00 = 1.0;
	this.m01 = 0.0;
	this.m02 = 0.0;
	this.m03 = v1.x;

	this.m10 = 0.0;
	this.m11 = 1.0;
	this.m12 = 0.0;
	this.m13 = v1.y;

	this.m20 = 0.0;
	this.m21 = 0.0;
	this.m22 = 1.0;
	this.m23 = v1.z;

	this.m30 = 0.0;
	this.m31 = 0.0;
	this.m32 = 0.0;
	this.m33 = 1.0;
    }

    /**
     * Sets the value of this transform to a scale and translation matrix;
     * the scale is not applied to the translation and all of the matrix
     * values are modified.
     * @param scale the scale factor for the matrix
     * @param v1 the translation amount
     */
    public final void set(double scale, Vector3d v1)
    {
	this.m00 = scale;
	this.m01 = 0.0;
	this.m02 = 0.0;
	this.m03 = v1.x;

	this.m10 = 0.0;
	this.m11 = scale;
	this.m12 = 0.0;
	this.m13 = v1.y;

	this.m20 = 0.0;
	this.m21 = 0.0;
	this.m22 = scale;
	this.m23 = v1.z;

	this.m30 = 0.0;
	this.m31 = 0.0;
	this.m32 = 0.0;
	this.m33 = 1.0;
    }

   /**
     * Sets the value of this transform to a scale and translation matrix;
     * the translation is scaled by the scale factor and all of the matrix
     * values are modified.
     * @param v1 the translation amount
     * @param scale the scale factor for the matrix
     */
    public final void set(Vector3d v1, double scale)
    {
	this.m00 = scale;
	this.m01 = 0.0;
	this.m02 = 0.0;
	this.m03 = scale*v1.x;

	this.m10 = 0.0;
	this.m11 = scale;
	this.m12 = 0.0;
	this.m13 = scale*v1.y;

	this.m20 = 0.0;
	this.m21 = 0.0;
	this.m22 = scale;
	this.m23 = scale*v1.z;

	this.m30 = 0.0;
	this.m31 = 0.0;
	this.m32 = 0.0;
	this.m33 = 1.0;
    }

   /**
     * Sets the value of this matrix from the rotation expressed by
     * the rotation matrix m1, the translation t1, and the scale factor.
     * The translation is not modified by the scale.
     * @param m1  the rotation component
     * @param t1  the translation component
     * @param scale  the scale component
     */
    public final void set(Matrix3f m1, Vector3f t1, float scale)
    {
        this.m00 = m1.m00*scale;
        this.m01 = m1.m01*scale;
        this.m02 = m1.m02*scale;
        this.m03 = t1.x;

        this.m10 = m1.m10*scale;
        this.m11 = m1.m11*scale;
        this.m12 = m1.m12*scale;
        this.m13 = t1.y;

        this.m20 = m1.m20*scale;
        this.m21 = m1.m21*scale;
        this.m22 = m1.m22*scale;
        this.m23 = t1.z;

        this.m30 = 0.0;
        this.m31 = 0.0;
        this.m32 = 0.0;
        this.m33 = 1.0;
    }


   /**
     * Sets the value of this matrix from the rotation expressed by
     * the rotation matrix m1, the translation t1, and the scale factor.
     * The translation is not modified by the scale.
     * @param m1  the rotation component
     * @param t1  the translation component
     * @param scale  the scale component
     */
    public final void set(Matrix3d m1, Vector3d t1, double scale)
    {
        this.m00 = m1.m00*scale;
        this.m01 = m1.m01*scale;
        this.m02 = m1.m02*scale;
        this.m03 = t1.x;

        this.m10 = m1.m10*scale;
        this.m11 = m1.m11*scale;
        this.m12 = m1.m12*scale;
        this.m13 = t1.y;

        this.m20 = m1.m20*scale;
        this.m21 = m1.m21*scale;
        this.m22 = m1.m22*scale;
        this.m23 = t1.z;

        this.m30 = 0.0;
        this.m31 = 0.0;
        this.m32 = 0.0;
        this.m33 = 1.0;
    }

   /**
     * Modifies the translational components of this matrix to the values
     * of the Vector3d argument; the other values of this matrix are not
     * modified.
     * @param trans  the translational component
     */
    public final void setTranslation(Vector3d trans)
    {
       m03 = trans.x;
       m13 = trans.y;
       m23 = trans.z;
    }

    /**
     * Sets the value of this matrix to a counter-clockwise rotation
     * about the x axis.
     * @param angle the angle to rotate about the X axis in radians
     */
    public final void rotX(double angle)
    {
	double	sinAngle, cosAngle;

	sinAngle = Math.sin(angle);
	cosAngle = Math.cos(angle);

	this.m00 = 1.0;
	this.m01 = 0.0;
	this.m02 = 0.0;
	this.m03 = 0.0;

	this.m10 = 0.0;
	this.m11 = cosAngle;
	this.m12 = -sinAngle;
	this.m13 = 0.0;

	this.m20 = 0.0;
	this.m21 = sinAngle;
	this.m22 = cosAngle;
	this.m23 = 0.0;

	this.m30 = 0.0;
	this.m31 = 0.0;
	this.m32 = 0.0;
	this.m33 = 1.0;
    }

    /**
     * Sets the value of this matrix to a counter-clockwise rotation
     * about the y axis.
     * @param angle the angle to rotate about the Y axis in radians
     */
    public final void rotY(double angle)
    {
	double	sinAngle, cosAngle;

	sinAngle = Math.sin(angle);
	cosAngle = Math.cos(angle);

	this.m00 = cosAngle;
	this.m01 = 0.0;
	this.m02 = sinAngle;
	this.m03 = 0.0;

	this.m10 = 0.0;
	this.m11 = 1.0;
	this.m12 = 0.0;
	this.m13 = 0.0;

	this.m20 = -sinAngle;
	this.m21 = 0.0;
	this.m22 = cosAngle;
	this.m23 = 0.0;

	this.m30 = 0.0;
	this.m31 = 0.0;
	this.m32 = 0.0;
	this.m33 = 1.0;
    }

    /**
     * Sets the value of this matrix to a counter-clockwise rotation
     * about the z axis.
     * @param angle the angle to rotate about the Z axis in radians
     */
    public final void rotZ(double angle)
    {
	double	sinAngle, cosAngle;

	sinAngle = Math.sin(angle);
	cosAngle = Math.cos(angle);

	this.m00 = cosAngle;
	this.m01 = -sinAngle;
	this.m02 = 0.0;
	this.m03 = 0.0;

	this.m10 = sinAngle;
	this.m11 = cosAngle;
	this.m12 = 0.0;
	this.m13 = 0.0;

	this.m20 = 0.0;
	this.m21 = 0.0;
	this.m22 = 1.0;
	this.m23 = 0.0;

	this.m30 = 0.0;
	this.m31 = 0.0;
	this.m32 = 0.0;
	this.m33 = 1.0;
    }

   /**
     * Multiplies each element of this matrix by a scalar.
     * @param scalar  the scalar multiplier.
     */
    public final void mul(double scalar)
    {
      m00 *= scalar;
      m01 *= scalar;
      m02 *= scalar;
      m03 *= scalar;
      m10 *= scalar;
      m11 *= scalar;
      m12 *= scalar;
      m13 *= scalar;
      m20 *= scalar;
      m21 *= scalar;
      m22 *= scalar;
      m23 *= scalar;
      m30 *= scalar;
      m31 *= scalar;
      m32 *= scalar;
      m33 *= scalar;
    }

   /**
     * Multiplies each element of matrix m1 by a scalar and places
     * the result into this.  Matrix m1 is not modified.
     * @param scalar  the scalar multiplier
     * @param m1  the original matrix
     */
    public final void mul(double scalar, Matrix4d m1)
    {
      this.m00 = m1.m00 * scalar;
      this.m01 = m1.m01 * scalar;
      this.m02 = m1.m02 * scalar;
      this.m03 = m1.m03 * scalar;
      this.m10 = m1.m10 * scalar;
      this.m11 = m1.m11 * scalar;
      this.m12 = m1.m12 * scalar;
      this.m13 = m1.m13 * scalar;
      this.m20 = m1.m20 * scalar;
      this.m21 = m1.m21 * scalar;
      this.m22 = m1.m22 * scalar;
      this.m23 = m1.m23 * scalar;
      this.m30 = m1.m30 * scalar;
      this.m31 = m1.m31 * scalar;
      this.m32 = m1.m32 * scalar;
      this.m33 = m1.m33 * scalar;
    }

    /**
     * Sets the value of this matrix to the result of multiplying itself
     * with matrix m1.
     * @param m1 the other matrix
     */
    public final void mul(Matrix4d m1)
    {
            double      m00, m01, m02, m03,
                        m10, m11, m12, m13,
                        m20, m21, m22, m23,
		        m30, m31, m32, m33;  // vars for temp result matrix

        m00 = this.m00*m1.m00 + this.m01*m1.m10 +
              this.m02*m1.m20 + this.m03*m1.m30;
        m01 = this.m00*m1.m01 + this.m01*m1.m11 +
              this.m02*m1.m21 + this.m03*m1.m31;
        m02 = this.m00*m1.m02 + this.m01*m1.m12 +
              this.m02*m1.m22 + this.m03*m1.m32;
        m03 = this.m00*m1.m03 + this.m01*m1.m13 +
              this.m02*m1.m23 + this.m03*m1.m33;

        m10 = this.m10*m1.m00 + this.m11*m1.m10 +
              this.m12*m1.m20 + this.m13*m1.m30;
        m11 = this.m10*m1.m01 + this.m11*m1.m11 +
              this.m12*m1.m21 + this.m13*m1.m31;
        m12 = this.m10*m1.m02 + this.m11*m1.m12 +
              this.m12*m1.m22 + this.m13*m1.m32;
        m13 = this.m10*m1.m03 + this.m11*m1.m13 +
              this.m12*m1.m23 + this.m13*m1.m33;

        m20 = this.m20*m1.m00 + this.m21*m1.m10 +
              this.m22*m1.m20 + this.m23*m1.m30;
        m21 = this.m20*m1.m01 + this.m21*m1.m11 +
              this.m22*m1.m21 + this.m23*m1.m31;
        m22 = this.m20*m1.m02 + this.m21*m1.m12 +
              this.m22*m1.m22 + this.m23*m1.m32;
        m23 = this.m20*m1.m03 + this.m21*m1.m13 +
              this.m22*m1.m23 + this.m23*m1.m33;

        m30 = this.m30*m1.m00 + this.m31*m1.m10 +
              this.m32*m1.m20 + this.m33*m1.m30;
        m31 = this.m30*m1.m01 + this.m31*m1.m11 +
              this.m32*m1.m21 + this.m33*m1.m31;
        m32 = this.m30*m1.m02 + this.m31*m1.m12 +
              this.m32*m1.m22 + this.m33*m1.m32;
        m33 = this.m30*m1.m03 + this.m31*m1.m13 +
              this.m32*m1.m23 + this.m33*m1.m33;

        this.m00 = m00; this.m01 = m01; this.m02 = m02; this.m03 = m03;
        this.m10 = m10; this.m11 = m11; this.m12 = m12; this.m13 = m13;
        this.m20 = m20; this.m21 = m21; this.m22 = m22; this.m23 = m23;
        this.m30 = m30; this.m31 = m31; this.m32 = m32; this.m33 = m33;
    }

    /**
     * Sets the value of this matrix to the result of multiplying
     * the two argument matrices together.
     * @param m1 the first matrix
     * @param m2 the second matrix
     */
    public final void mul(Matrix4d m1, Matrix4d m2)
    {
	if (this != m1 && this != m2) {
	    // code for mat mul
	    this.m00 = m1.m00*m2.m00 + m1.m01*m2.m10 +
                       m1.m02*m2.m20 + m1.m03*m2.m30;
	    this.m01 = m1.m00*m2.m01 + m1.m01*m2.m11 +
                       m1.m02*m2.m21 + m1.m03*m2.m31;
	    this.m02 = m1.m00*m2.m02 + m1.m01*m2.m12 +
                       m1.m02*m2.m22 + m1.m03*m2.m32;
	    this.m03 = m1.m00*m2.m03 + m1.m01*m2.m13 +
                       m1.m02*m2.m23 + m1.m03*m2.m33;

	    this.m10 = m1.m10*m2.m00 + m1.m11*m2.m10 +
                       m1.m12*m2.m20 + m1.m13*m2.m30;
	    this.m11 = m1.m10*m2.m01 + m1.m11*m2.m11 +
                       m1.m12*m2.m21 + m1.m13*m2.m31;
	    this.m12 = m1.m10*m2.m02 + m1.m11*m2.m12 +
                       m1.m12*m2.m22 + m1.m13*m2.m32;
	    this.m13 = m1.m10*m2.m03 + m1.m11*m2.m13 +
                       m1.m12*m2.m23 + m1.m13*m2.m33;

	    this.m20 = m1.m20*m2.m00 + m1.m21*m2.m10 +
                       m1.m22*m2.m20 + m1.m23*m2.m30;
	    this.m21 = m1.m20*m2.m01 + m1.m21*m2.m11 +
                       m1.m22*m2.m21 + m1.m23*m2.m31;
	    this.m22 = m1.m20*m2.m02 + m1.m21*m2.m12 +
                       m1.m22*m2.m22 + m1.m23*m2.m32;
	    this.m23 = m1.m20*m2.m03 + m1.m21*m2.m13 +
                       m1.m22*m2.m23 + m1.m23*m2.m33;

	    this.m30 = m1.m30*m2.m00 + m1.m31*m2.m10 +
                       m1.m32*m2.m20 + m1.m33*m2.m30;
	    this.m31 = m1.m30*m2.m01 + m1.m31*m2.m11 +
                       m1.m32*m2.m21 + m1.m33*m2.m31;
	    this.m32 = m1.m30*m2.m02 + m1.m31*m2.m12 +
                       m1.m32*m2.m22 + m1.m33*m2.m32;
	    this.m33 = m1.m30*m2.m03 + m1.m31*m2.m13 +
                       m1.m32*m2.m23 + m1.m33*m2.m33;
	} else {
	    double	m00, m01, m02, m03,
			m10, m11, m12, m13,
			m20, m21, m22, m23,
		        m30, m31, m32, m33;  // vars for temp result matrix

	    // code for mat mul
	    m00 = m1.m00*m2.m00 + m1.m01*m2.m10 + m1.m02*m2.m20 + m1.m03*m2.m30;
	    m01 = m1.m00*m2.m01 + m1.m01*m2.m11 + m1.m02*m2.m21 + m1.m03*m2.m31;
	    m02 = m1.m00*m2.m02 + m1.m01*m2.m12 + m1.m02*m2.m22 + m1.m03*m2.m32;
	    m03 = m1.m00*m2.m03 + m1.m01*m2.m13 + m1.m02*m2.m23 + m1.m03*m2.m33;

	    m10 = m1.m10*m2.m00 + m1.m11*m2.m10 + m1.m12*m2.m20 + m1.m13*m2.m30;
	    m11 = m1.m10*m2.m01 + m1.m11*m2.m11 + m1.m12*m2.m21 + m1.m13*m2.m31;
	    m12 = m1.m10*m2.m02 + m1.m11*m2.m12 + m1.m12*m2.m22 + m1.m13*m2.m32;
	    m13 = m1.m10*m2.m03 + m1.m11*m2.m13 + m1.m12*m2.m23 + m1.m13*m2.m33;

	    m20 = m1.m20*m2.m00 + m1.m21*m2.m10 + m1.m22*m2.m20 + m1.m23*m2.m30;
	    m21 = m1.m20*m2.m01 + m1.m21*m2.m11 + m1.m22*m2.m21 + m1.m23*m2.m31;
	    m22 = m1.m20*m2.m02 + m1.m21*m2.m12 + m1.m22*m2.m22 + m1.m23*m2.m32;
	    m23 = m1.m20*m2.m03 + m1.m21*m2.m13 + m1.m22*m2.m23 + m1.m23*m2.m33;

	    m30 = m1.m30*m2.m00 + m1.m31*m2.m10 + m1.m32*m2.m20 + m1.m33*m2.m30;
	    m31 = m1.m30*m2.m01 + m1.m31*m2.m11 + m1.m32*m2.m21 + m1.m33*m2.m31;
	    m32 = m1.m30*m2.m02 + m1.m31*m2.m12 + m1.m32*m2.m22 + m1.m33*m2.m32;
	    m33 = m1.m30*m2.m03 + m1.m31*m2.m13 + m1.m32*m2.m23 + m1.m33*m2.m33;

	    this.m00 = m00; this.m01 = m01; this.m02 = m02; this.m03 = m03;
	    this.m10 = m10; this.m11 = m11; this.m12 = m12; this.m13 = m13;
	    this.m20 = m20; this.m21 = m21; this.m22 = m22; this.m23 = m23;
	    this.m30 = m30; this.m31 = m31; this.m32 = m32; this.m33 = m33;

	}
    }

   /**
     *  Multiplies the transpose of matrix m1 times the transpose of matrix
     *  m2, and places the result into this.
     *  @param m1  the matrix on the left hand side of the multiplication
     *  @param m2  the matrix on the right hand side of the multiplication
     */
    public final void mulTransposeBoth(Matrix4d m1, Matrix4d m2)
    {
       if (this != m1 && this != m2) {
            this.m00 = m1.m00*m2.m00 + m1.m10*m2.m01 + m1.m20*m2.m02 + m1.m30*m2.m03;
            this.m01 = m1.m00*m2.m10 + m1.m10*m2.m11 + m1.m20*m2.m12 + m1.m30*m2.m13;
            this.m02 = m1.m00*m2.m20 + m1.m10*m2.m21 + m1.m20*m2.m22 + m1.m30*m2.m23;
            this.m03 = m1.m00*m2.m30 + m1.m10*m2.m31 + m1.m20*m2.m32 + m1.m30*m2.m33;

            this.m10 = m1.m01*m2.m00 + m1.m11*m2.m01 + m1.m21*m2.m02 + m1.m31*m2.m03;
            this.m11 = m1.m01*m2.m10 + m1.m11*m2.m11 + m1.m21*m2.m12 + m1.m31*m2.m13;
            this.m12 = m1.m01*m2.m20 + m1.m11*m2.m21 + m1.m21*m2.m22 + m1.m31*m2.m23;
            this.m13 = m1.m01*m2.m30 + m1.m11*m2.m31 + m1.m21*m2.m32 + m1.m31*m2.m33;

            this.m20 = m1.m02*m2.m00 + m1.m12*m2.m01 + m1.m22*m2.m02 + m1.m32*m2.m03;
            this.m21 = m1.m02*m2.m10 + m1.m12*m2.m11 + m1.m22*m2.m12 + m1.m32*m2.m13;
            this.m22 = m1.m02*m2.m20 + m1.m12*m2.m21 + m1.m22*m2.m22 + m1.m32*m2.m23;
            this.m23 = m1.m02*m2.m30 + m1.m12*m2.m31 + m1.m22*m2.m32 + m1.m32*m2.m33;

            this.m30 = m1.m03*m2.m00 + m1.m13*m2.m01 + m1.m23*m2.m02 + m1.m33*m2.m03;
            this.m31 = m1.m03*m2.m10 + m1.m13*m2.m11 + m1.m23*m2.m12 + m1.m33*m2.m13;
            this.m32 = m1.m03*m2.m20 + m1.m13*m2.m21 + m1.m23*m2.m22 + m1.m33*m2.m23;
            this.m33 = m1.m03*m2.m30 + m1.m13*m2.m31 + m1.m23*m2.m32 + m1.m33*m2.m33;
        } else {
            double      m00, m01, m02, m03,
                        m10, m11, m12, m13,
		        m20, m21, m22, m23,  // vars for temp result matrix
                        m30, m31, m32, m33;

            m00 = m1.m00*m2.m00 + m1.m10*m2.m01 + m1.m20*m2.m02 + m1.m30*m2.m03;
            m01 = m1.m00*m2.m10 + m1.m10*m2.m11 + m1.m20*m2.m12 + m1.m30*m2.m13;
            m02 = m1.m00*m2.m20 + m1.m10*m2.m21 + m1.m20*m2.m22 + m1.m30*m2.m23;
            m03 = m1.m00*m2.m30 + m1.m10*m2.m31 + m1.m20*m2.m32 + m1.m30*m2.m33;

            m10 = m1.m01*m2.m00 + m1.m11*m2.m01 + m1.m21*m2.m02 + m1.m31*m2.m03;
            m11 = m1.m01*m2.m10 + m1.m11*m2.m11 + m1.m21*m2.m12 + m1.m31*m2.m13;
            m12 = m1.m01*m2.m20 + m1.m11*m2.m21 + m1.m21*m2.m22 + m1.m31*m2.m23;
            m13 = m1.m01*m2.m30 + m1.m11*m2.m31 + m1.m21*m2.m32 + m1.m31*m2.m33;

            m20 = m1.m02*m2.m00 + m1.m12*m2.m01 + m1.m22*m2.m02 + m1.m32*m2.m03;
            m21 = m1.m02*m2.m10 + m1.m12*m2.m11 + m1.m22*m2.m12 + m1.m32*m2.m13;
            m22 = m1.m02*m2.m20 + m1.m12*m2.m21 + m1.m22*m2.m22 + m1.m32*m2.m23;
            m23 = m1.m02*m2.m30 + m1.m12*m2.m31 + m1.m22*m2.m32 + m1.m32*m2.m33;

            m30 = m1.m03*m2.m00 + m1.m13*m2.m01 + m1.m23*m2.m02 + m1.m33*m2.m03;
            m31 = m1.m03*m2.m10 + m1.m13*m2.m11 + m1.m23*m2.m12 + m1.m33*m2.m13;
            m32 = m1.m03*m2.m20 + m1.m13*m2.m21 + m1.m23*m2.m22 + m1.m33*m2.m23;
            m33 = m1.m03*m2.m30 + m1.m13*m2.m31 + m1.m23*m2.m32 + m1.m33*m2.m33;

            this.m00 = m00; this.m01 = m01; this.m02 = m02; this.m03 = m03;
            this.m10 = m10; this.m11 = m11; this.m12 = m12; this.m13 = m13;
            this.m20 = m20; this.m21 = m21; this.m22 = m22; this.m23 = m23;
            this.m30 = m30; this.m31 = m31; this.m32 = m32; this.m33 = m33;
        }

    }



   /**
     *  Multiplies matrix m1 times the transpose of matrix m2, and
     *  places the result into this.
     *  @param m1  the matrix on the left hand side of the multiplication
     *  @param m2  the matrix on the right hand side of the multiplication
     */
    public final void mulTransposeRight(Matrix4d m1, Matrix4d m2)
    {
    if (this != m1 && this != m2) {
      this.m00 = m1.m00*m2.m00 + m1.m01*m2.m01 + m1.m02*m2.m02 + m1.m03*m2.m03;
      this.m01 = m1.m00*m2.m10 + m1.m01*m2.m11 + m1.m02*m2.m12 + m1.m03*m2.m13;
      this.m02 = m1.m00*m2.m20 + m1.m01*m2.m21 + m1.m02*m2.m22 + m1.m03*m2.m23;
      this.m03 = m1.m00*m2.m30 + m1.m01*m2.m31 + m1.m02*m2.m32 + m1.m03*m2.m33;

      this.m10 = m1.m10*m2.m00 + m1.m11*m2.m01 + m1.m12*m2.m02 + m1.m13*m2.m03;
      this.m11 = m1.m10*m2.m10 + m1.m11*m2.m11 + m1.m12*m2.m12 + m1.m13*m2.m13;
      this.m12 = m1.m10*m2.m20 + m1.m11*m2.m21 + m1.m12*m2.m22 + m1.m13*m2.m23;
      this.m13 = m1.m10*m2.m30 + m1.m11*m2.m31 + m1.m12*m2.m32 + m1.m13*m2.m33;

      this.m20 = m1.m20*m2.m00 + m1.m21*m2.m01 + m1.m22*m2.m02 + m1.m23*m2.m03;
      this.m21 = m1.m20*m2.m10 + m1.m21*m2.m11 + m1.m22*m2.m12 + m1.m23*m2.m13;
      this.m22 = m1.m20*m2.m20 + m1.m21*m2.m21 + m1.m22*m2.m22 + m1.m23*m2.m23;
      this.m23 = m1.m20*m2.m30 + m1.m21*m2.m31 + m1.m22*m2.m32 + m1.m23*m2.m33;

      this.m30 = m1.m30*m2.m00 + m1.m31*m2.m01 + m1.m32*m2.m02 + m1.m33*m2.m03;
      this.m31 = m1.m30*m2.m10 + m1.m31*m2.m11 + m1.m32*m2.m12 + m1.m33*m2.m13;
      this.m32 = m1.m30*m2.m20 + m1.m31*m2.m21 + m1.m32*m2.m22 + m1.m33*m2.m23;
      this.m33 = m1.m30*m2.m30 + m1.m31*m2.m31 + m1.m32*m2.m32 + m1.m33*m2.m33;
    } else {
            double      m00, m01, m02, m03,
                        m10, m11, m12, m13,
		        m20, m21, m22, m23,  // vars for temp result matrix
                        m30, m31, m32, m33;

      m00 = m1.m00*m2.m00 + m1.m01*m2.m01 + m1.m02*m2.m02 + m1.m03*m2.m03;
      m01 = m1.m00*m2.m10 + m1.m01*m2.m11 + m1.m02*m2.m12 + m1.m03*m2.m13;
      m02 = m1.m00*m2.m20 + m1.m01*m2.m21 + m1.m02*m2.m22 + m1.m03*m2.m23;
      m03 = m1.m00*m2.m30 + m1.m01*m2.m31 + m1.m02*m2.m32 + m1.m03*m2.m33;

      m10 = m1.m10*m2.m00 + m1.m11*m2.m01 + m1.m12*m2.m02 + m1.m13*m2.m03;
      m11 = m1.m10*m2.m10 + m1.m11*m2.m11 + m1.m12*m2.m12 + m1.m13*m2.m13;
      m12 = m1.m10*m2.m20 + m1.m11*m2.m21 + m1.m12*m2.m22 + m1.m13*m2.m23;
      m13 = m1.m10*m2.m30 + m1.m11*m2.m31 + m1.m12*m2.m32 + m1.m13*m2.m33;

      m20 = m1.m20*m2.m00 + m1.m21*m2.m01 + m1.m22*m2.m02 + m1.m23*m2.m03;
      m21 = m1.m20*m2.m10 + m1.m21*m2.m11 + m1.m22*m2.m12 + m1.m23*m2.m13;
      m22 = m1.m20*m2.m20 + m1.m21*m2.m21 + m1.m22*m2.m22 + m1.m23*m2.m23;
      m23 = m1.m20*m2.m30 + m1.m21*m2.m31 + m1.m22*m2.m32 + m1.m23*m2.m33;

      m30 = m1.m30*m2.m00 + m1.m31*m2.m01 + m1.m32*m2.m02 + m1.m33*m2.m03;
      m31 = m1.m30*m2.m10 + m1.m31*m2.m11 + m1.m32*m2.m12 + m1.m33*m2.m13;
      m32 = m1.m30*m2.m20 + m1.m31*m2.m21 + m1.m32*m2.m22 + m1.m33*m2.m23;
      m33 = m1.m30*m2.m30 + m1.m31*m2.m31 + m1.m32*m2.m32 + m1.m33*m2.m33;

      this.m00 = m00; this.m01 = m01; this.m02 = m02; this.m03 = m03;
      this.m10 = m10; this.m11 = m11; this.m12 = m12; this.m13 = m13;
      this.m20 = m20; this.m21 = m21; this.m22 = m22; this.m23 = m23;
      this.m30 = m30; this.m31 = m31; this.m32 = m32; this.m33 = m33;
    }
}


   /**
     *  Multiplies the transpose of matrix m1 times matrix m2, and
     *  places the result into this.
     *  @param m1  the matrix on the left hand side of the multiplication
     *  @param m2  the matrix on the right hand side of the multiplication
     */
    public final void mulTransposeLeft(Matrix4d m1, Matrix4d m2)
    {
    if (this != m1 && this != m2) {
      this.m00 = m1.m00*m2.m00 + m1.m10*m2.m10 + m1.m20*m2.m20 + m1.m30*m2.m30;
      this.m01 = m1.m00*m2.m01 + m1.m10*m2.m11 + m1.m20*m2.m21 + m1.m30*m2.m31;
      this.m02 = m1.m00*m2.m02 + m1.m10*m2.m12 + m1.m20*m2.m22 + m1.m30*m2.m32;
      this.m03 = m1.m00*m2.m03 + m1.m10*m2.m13 + m1.m20*m2.m23 + m1.m30*m2.m33;

      this.m10 = m1.m01*m2.m00 + m1.m11*m2.m10 + m1.m21*m2.m20 + m1.m31*m2.m30;
      this.m11 = m1.m01*m2.m01 + m1.m11*m2.m11 + m1.m21*m2.m21 + m1.m31*m2.m31;
      this.m12 = m1.m01*m2.m02 + m1.m11*m2.m12 + m1.m21*m2.m22 + m1.m31*m2.m32;
      this.m13 = m1.m01*m2.m03 + m1.m11*m2.m13 + m1.m21*m2.m23 + m1.m31*m2.m33;

      this.m20 = m1.m02*m2.m00 + m1.m12*m2.m10 + m1.m22*m2.m20 + m1.m32*m2.m30;
      this.m21 = m1.m02*m2.m01 + m1.m12*m2.m11 + m1.m22*m2.m21 + m1.m32*m2.m31;
      this.m22 = m1.m02*m2.m02 + m1.m12*m2.m12 + m1.m22*m2.m22 + m1.m32*m2.m32;
      this.m23 = m1.m02*m2.m03 + m1.m12*m2.m13 + m1.m22*m2.m23 + m1.m32*m2.m33;

      this.m30 = m1.m03*m2.m00 + m1.m13*m2.m10 + m1.m23*m2.m20 + m1.m33*m2.m30;
      this.m31 = m1.m03*m2.m01 + m1.m13*m2.m11 + m1.m23*m2.m21 + m1.m33*m2.m31;
      this.m32 = m1.m03*m2.m02 + m1.m13*m2.m12 + m1.m23*m2.m22 + m1.m33*m2.m32;
      this.m33 = m1.m03*m2.m03 + m1.m13*m2.m13 + m1.m23*m2.m23 + m1.m33*m2.m33;
    } else {
            double      m00, m01, m02, m03,
                        m10, m11, m12, m13,
		        m20, m21, m22, m23,  // vars for temp result matrix
                        m30, m31, m32, m33;



      m00 = m1.m00*m2.m00 + m1.m10*m2.m10 + m1.m20*m2.m20 + m1.m30*m2.m30;
      m01 = m1.m00*m2.m01 + m1.m10*m2.m11 + m1.m20*m2.m21 + m1.m30*m2.m31;
      m02 = m1.m00*m2.m02 + m1.m10*m2.m12 + m1.m20*m2.m22 + m1.m30*m2.m32;
      m03 = m1.m00*m2.m03 + m1.m10*m2.m13 + m1.m20*m2.m23 + m1.m30*m2.m33;

      m10 = m1.m01*m2.m00 + m1.m11*m2.m10 + m1.m21*m2.m20 + m1.m31*m2.m30;
      m11 = m1.m01*m2.m01 + m1.m11*m2.m11 + m1.m21*m2.m21 + m1.m31*m2.m31;
      m12 = m1.m01*m2.m02 + m1.m11*m2.m12 + m1.m21*m2.m22 + m1.m31*m2.m32;
      m13 = m1.m01*m2.m03 + m1.m11*m2.m13 + m1.m21*m2.m23 + m1.m31*m2.m33;

      m20 = m1.m02*m2.m00 + m1.m12*m2.m10 + m1.m22*m2.m20 + m1.m32*m2.m30;
      m21 = m1.m02*m2.m01 + m1.m12*m2.m11 + m1.m22*m2.m21 + m1.m32*m2.m31;
      m22 = m1.m02*m2.m02 + m1.m12*m2.m12 + m1.m22*m2.m22 + m1.m32*m2.m32;
      m23 = m1.m02*m2.m03 + m1.m12*m2.m13 + m1.m22*m2.m23 + m1.m32*m2.m33;

      m30 = m1.m03*m2.m00 + m1.m13*m2.m10 + m1.m23*m2.m20 + m1.m33*m2.m30;
      m31 = m1.m03*m2.m01 + m1.m13*m2.m11 + m1.m23*m2.m21 + m1.m33*m2.m31;
      m32 = m1.m03*m2.m02 + m1.m13*m2.m12 + m1.m23*m2.m22 + m1.m33*m2.m32;
      m33 = m1.m03*m2.m03 + m1.m13*m2.m13 + m1.m23*m2.m23 + m1.m33*m2.m33;

      this.m00 = m00; this.m01 = m01; this.m02 = m02; this.m03 = m03;
      this.m10 = m10; this.m11 = m11; this.m12 = m12; this.m13 = m13;
      this.m20 = m20; this.m21 = m21; this.m22 = m22; this.m23 = m23;
      this.m30 = m30; this.m31 = m31; this.m32 = m32; this.m33 = m33;
    }

    }


   /**
     * Returns true if all of the data members of Matrix4d m1 are
     * equal to the corresponding data members in this Matrix4d.
     * @param m1  the matrix with which the comparison is made
     * @return  true or false
     */
    public boolean equals(Matrix4d m1)
    {
      try {
        return(this.m00 == m1.m00 && this.m01 == m1.m01 && this.m02 == m1.m02
            && this.m03 == m1.m03 && this.m10 == m1.m10 && this.m11 == m1.m11
            && this.m12 == m1.m12 && this.m13 == m1.m13 && this.m20 == m1.m20
            && this.m21 == m1.m21 && this.m22 == m1.m22 && this.m23 == m1.m23
            && this.m30 == m1.m30 && this.m31 == m1.m31 && this.m32 == m1.m32
            && this.m33 == m1.m33);
      }
      catch (NullPointerException e2) { return false; }

    }

   /**
     * Returns true if the Object t1 is of type Matrix4d and all of the
     * data members of t1 are equal to the corresponding data members in
     * this Matrix4d.
     * @param t1  the matrix with which the comparison is made
     * @return  true or false
     */
    @Override
    public boolean equals(Object t1)
    {
        try {
           Matrix4d m2 = (Matrix4d) t1;
           return(this.m00 == m2.m00 && this.m01 == m2.m01 && this.m02 == m2.m02
             && this.m03 == m2.m03 && this.m10 == m2.m10 && this.m11 == m2.m11
             && this.m12 == m2.m12 && this.m13 == m2.m13 && this.m20 == m2.m20
             && this.m21 == m2.m21 && this.m22 == m2.m22 && this.m23 == m2.m23
             && this.m30 == m2.m30 && this.m31 == m2.m31 && this.m32 == m2.m32
             && this.m33 == m2.m33);
        }
        catch (ClassCastException   e1) { return false; }
        catch (NullPointerException e2) { return false; }
    }

    /**
     * @deprecated Use epsilonEquals(Matrix4d,double) instead
     */
    public boolean epsilonEquals(Matrix4d m1, float epsilon) {
	return epsilonEquals(m1, (double)epsilon);
    }

    /**
     * Returns true if the L-infinite distance between this matrix
     * and matrix m1 is less than or equal to the epsilon parameter,
     * otherwise returns false.  The L-infinite
     * distance is equal to
     * MAX[i=0,1,2,3 ; j=0,1,2,3 ; abs(this.m(i,j) - m1.m(i,j)]
     * @param m1  the matrix to be compared to this matrix
     * @param epsilon  the threshold value
     */
    public boolean epsilonEquals(Matrix4d m1, double epsilon) {
       double diff;

       diff = m00 - m1.m00;
       if((diff<0?-diff:diff) > epsilon) return false;

       diff = m01 - m1.m01;
       if((diff<0?-diff:diff) > epsilon) return false;

       diff = m02 - m1.m02;
       if((diff<0?-diff:diff) > epsilon) return false;

       diff = m03 - m1.m03;
       if((diff<0?-diff:diff) > epsilon) return false;

       diff = m10 - m1.m10;
       if((diff<0?-diff:diff) > epsilon) return false;

       diff = m11 - m1.m11;
       if((diff<0?-diff:diff) > epsilon) return false;

       diff = m12 - m1.m12;
       if((diff<0?-diff:diff) > epsilon) return false;

       diff = m13 - m1.m13;
       if((diff<0?-diff:diff) > epsilon) return false;

       diff = m20 - m1.m20;
       if((diff<0?-diff:diff) > epsilon) return false;

       diff = m21 - m1.m21;
       if((diff<0?-diff:diff) > epsilon) return false;

       diff = m22 - m1.m22;
       if((diff<0?-diff:diff) > epsilon) return false;

       diff = m23 - m1.m23;
       if((diff<0?-diff:diff) > epsilon) return false;

       diff = m30 - m1.m30;
       if((diff<0?-diff:diff) > epsilon) return false;

       diff = m31 - m1.m31;
       if((diff<0?-diff:diff) > epsilon) return false;

       diff = m32 - m1.m32;
       if((diff<0?-diff:diff) > epsilon) return false;

       diff = m33 - m1.m33;
       if((diff<0?-diff:diff) > epsilon) return false;

       return true;
    }

    /**
     * Returns a hash code value based on the data values in this
     * object.  Two different Matrix4d objects with identical data values
     * (i.e., Matrix4d.equals returns true) will return the same hash
     * code value.  Two objects with different data members may return the
     * same hash value, although this is not likely.
     * @return the integer hash code value
     */
    @Override
    public int hashCode() {
	long bits = 1L;
	bits = VecMathUtil.hashDoubleBits(bits, m00);
	bits = VecMathUtil.hashDoubleBits(bits, m01);
	bits = VecMathUtil.hashDoubleBits(bits, m02);
	bits = VecMathUtil.hashDoubleBits(bits, m03);
	bits = VecMathUtil.hashDoubleBits(bits, m10);
	bits = VecMathUtil.hashDoubleBits(bits, m11);
	bits = VecMathUtil.hashDoubleBits(bits, m12);
	bits = VecMathUtil.hashDoubleBits(bits, m13);
	bits = VecMathUtil.hashDoubleBits(bits, m20);
	bits = VecMathUtil.hashDoubleBits(bits, m21);
	bits = VecMathUtil.hashDoubleBits(bits, m22);
	bits = VecMathUtil.hashDoubleBits(bits, m23);
	bits = VecMathUtil.hashDoubleBits(bits, m30);
	bits = VecMathUtil.hashDoubleBits(bits, m31);
	bits = VecMathUtil.hashDoubleBits(bits, m32);
	bits = VecMathUtil.hashDoubleBits(bits, m33);
	return VecMathUtil.hashFinish(bits);
    }


  /**
   * Transform the vector vec using this Matrix4d and place the
   * result into vecOut.
   * @param vec  the double precision vector to be transformed
   * @param vecOut  the vector into which the transformed values are placed
   */
    public final void transform(Tuple4d vec, Tuple4d vecOut)
    {
           double x,y,z;
           x = (m00*vec.x + m01*vec.y
                  + m02*vec.z + m03*vec.w);
           y = (m10*vec.x + m11*vec.y
                   + m12*vec.z + m13*vec.w);
           z = (m20*vec.x + m21*vec.y
                     + m22*vec.z + m23*vec.w);
           vecOut.w = (m30*vec.x + m31*vec.y
                      + m32*vec.z + m33*vec.w);
           vecOut.x = x;
           vecOut.y = y;
           vecOut.z = z;
    }

  /**
   * Transform the vector vec using this Matrix4d and place the
   * result back into vec.
   * @param vec  the double precision vector to be transformed
   */
    public final void transform(Tuple4d vec)
    {
         double x,y,z;

           x = (m00*vec.x + m01*vec.y
                     + m02*vec.z + m03*vec.w);
           y = (m10*vec.x + m11*vec.y
                     + m12*vec.z + m13*vec.w);
           z = (m20*vec.x + m21*vec.y
                     + m22*vec.z + m23*vec.w);
           vec.w = (m30*vec.x + m31*vec.y
                      + m32*vec.z + m33*vec.w);
           vec.x = x;
           vec.y = y;
           vec.z = z;
    }

  /**
   * Transform the vector vec using this Matrix4d and place the
   * result into vecOut.
   * @param vec  the single precision vector to be transformed
   * @param vecOut  the vector into which the transformed values are placed
   */
    public final void transform(Tuple4f vec, Tuple4f vecOut)
    {
        float x,y,z;
           x = (float) (m00*vec.x + m01*vec.y
                     + m02*vec.z + m03*vec.w);
           y = (float) (m10*vec.x + m11*vec.y
                     + m12*vec.z + m13*vec.w);
           z = (float) (m20*vec.x + m21*vec.y
                     + m22*vec.z + m23*vec.w);
           vecOut.w = (float) (m30*vec.x + m31*vec.y
                      + m32*vec.z + m33*vec.w);
           vecOut.x = x;
           vecOut.y = y;
           vecOut.z = z;
    }

  /**
   * Transform the vector vec using this Transform and place the
   * result back into vec.
   * @param vec  the single precision vector to be transformed
   */
    public final void transform(Tuple4f vec)
    {
         float x,y,z;

           x = (float) (m00*vec.x + m01*vec.y
                     + m02*vec.z + m03*vec.w);
           y = (float) (m10*vec.x + m11*vec.y
                     + m12*vec.z + m13*vec.w);
           z = (float) (m20*vec.x + m21*vec.y
                     + m22*vec.z + m23*vec.w);
           vec.w = (float) (m30*vec.x + m31*vec.y
                      + m32*vec.z + m33*vec.w);
           vec.x = x;
           vec.y = y;
           vec.z = z;
    }


  /**
   * Transforms the point parameter with this Matrix4d and
   * places the result into pointOut.  The fourth element of the
   * point input parameter is assumed to be one.
   * @param point  the input point to be transformed.
   * @param pointOut  the transformed point
   */
    public final void transform(Point3d point, Point3d pointOut)
    {
        double x,y;
        x = m00*point.x + m01*point.y + m02*point.z + m03;
        y = m10*point.x + m11*point.y + m12*point.z + m13;
        pointOut.z = m20*point.x + m21*point.y + m22*point.z + m23;
        pointOut.x = x;
        pointOut.y = y;

    }


  /**
   * Transforms the point parameter with this Matrix4d and
   * places the result back into point.  The fourth element of the
   * point input parameter is assumed to be one.
   * @param point  the input point to be transformed.
   */
    public final void transform(Point3d point)
    {
        double x, y;
        x = m00*point.x + m01*point.y + m02*point.z + m03;
        y = m10*point.x + m11*point.y + m12*point.z + m13;
        point.z =  m20*point.x + m21*point.y + m22*point.z + m23;
        point.x = x;
        point.y = y;
    }


  /**
   * Transforms the point parameter with this Matrix4d and
   * places the result into pointOut.  The fourth element of the
   * point input parameter is assumed to be one.
   * @param point  the input point to be transformed.
   * @param pointOut  the transformed point
   */
    public final void transform(Point3f point, Point3f pointOut)
    {
        float x,y;

        x = (float) (m00*point.x + m01*point.y + m02*point.z + m03);
        y = (float) (m10*point.x + m11*point.y + m12*point.z + m13);
        pointOut.z = (float) (m20*point.x + m21*point.y + m22*point.z + m23);
        pointOut.x = x;
        pointOut.y = y;
    }


  /**
   * Transforms the point parameter with this Matrix4d and
   * places the result back into point.  The fourth element of the
   * point input parameter is assumed to be one.
   * @param point  the input point to be transformed.
   */
    public final void transform(Point3f point)
    {
        float  x, y;
        x = (float) (m00*point.x + m01*point.y + m02*point.z + m03);
        y = (float) (m10*point.x + m11*point.y + m12*point.z + m13);
        point.z =  (float) (m20*point.x + m21*point.y + m22*point.z + m23);
        point.x = x;
        point.y = y;
    }


  /**
   * Transforms the normal parameter by this Matrix4d and places the value
   * into normalOut.  The fourth element of the normal is assumed to be zero.
   * @param normal   the input normal to be transformed.
   * @param normalOut  the transformed normal
   */
    public final void transform(Vector3d normal, Vector3d normalOut)
    {
        double x,y;
        x =  m00*normal.x + m01*normal.y + m02*normal.z;
        y =  m10*normal.x + m11*normal.y + m12*normal.z;
        normalOut.z =  m20*normal.x + m21*normal.y + m22*normal.z;
        normalOut.x = x;
        normalOut.y = y;
    }


  /**
   * Transforms the normal parameter by this transform and places the value
   * back into normal.  The fourth element of the normal is assumed to be zero.
   * @param normal   the input normal to be transformed.
   */
    public final void transform(Vector3d normal)
    {
        double x, y;

        x =  m00*normal.x + m01*normal.y + m02*normal.z;
        y =  m10*normal.x + m11*normal.y + m12*normal.z;
        normal.z =  m20*normal.x + m21*normal.y + m22*normal.z;
        normal.x = x;
        normal.y = y;
    }


  /**
   * Transforms the normal parameter by this Matrix4d and places the value
   * into normalOut.  The fourth element of the normal is assumed to be zero.
   * @param normal   the input normal to be transformed.
   * @param normalOut  the transformed normal
   */
    public final void transform(Vector3f normal, Vector3f normalOut)
    {
        float x,y;
        x =  (float) (m00*normal.x + m01*normal.y + m02*normal.z);
        y =  (float) (m10*normal.x + m11*normal.y + m12*normal.z);
        normalOut.z =  (float) (m20*normal.x + m21*normal.y + m22*normal.z);
        normalOut.x = x;
        normalOut.y = y;
    }


  /**
   * Transforms the normal parameter by this transform and places the value
   * back into normal.  The fourth element of the normal is assumed to be zero.
   * @param normal   the input normal to be transformed.
   */
    public final void transform(Vector3f normal)
    {
        float x, y;

        x =  (float) (m00*normal.x + m01*normal.y + m02*normal.z);
        y =  (float) (m10*normal.x + m11*normal.y + m12*normal.z);
        normal.z =  (float) (m20*normal.x + m21*normal.y + m22*normal.z);
        normal.x = x;
        normal.y = y;
    }

   /**
     * Sets the rotational component (upper 3x3) of this matrix to the
     * matrix values in the double precision Matrix3d argument; the other
     * elements of this matrix are unchanged; a singular value
     * decomposition is performed on this object's upper 3x3 matrix to
     * factor out the scale, then this object's upper 3x3 matrix components
     * are replaced by the passed rotation components,
     * and then the scale is reapplied to the rotational components.
     * @param m1   double precision 3x3 matrix
     */
    public final void setRotation( Matrix3d m1){
	double[]    tmp_rot = new double[9];  // scratch matrix
	double[]    tmp_scale = new double[3];  // scratch matrix

	getScaleRotate( tmp_scale, tmp_rot );

	m00 = m1.m00*tmp_scale[0];
	m01 = m1.m01*tmp_scale[1];
	m02 = m1.m02*tmp_scale[2];

	m10 = m1.m10*tmp_scale[0];
	m11 = m1.m11*tmp_scale[1];
	m12 = m1.m12*tmp_scale[2];

	m20 = m1.m20*tmp_scale[0];
	m21 = m1.m21*tmp_scale[1];
	m22 = m1.m22*tmp_scale[2];

    }


   /**
     * Sets the rotational component (upper 3x3) of this matrix to the
     * matrix values in the single precision Matrix3f argument; the other
     * elements of this matrix are unchanged; a singular value
     * decomposition is performed on this object's upper 3x3 matrix to
     * factor out the scale, then this object's upper 3x3 matrix components
     * are replaced by the passed rotation components,
     * and then the scale is reapplied to the rotational components.
     * @param m1   single precision 3x3 matrix
     */
   public final void setRotation( Matrix3f m1)
   {

       double[]    tmp_rot = new double[9];  // scratch matrix
       double[]    tmp_scale = new double[3];  // scratch matrix
       getScaleRotate( tmp_scale, tmp_rot );

        m00 = m1.m00*tmp_scale[0];
        m01 = m1.m01*tmp_scale[1];
        m02 = m1.m02*tmp_scale[2];

        m10 = m1.m10*tmp_scale[0];
        m11 = m1.m11*tmp_scale[1];
        m12 = m1.m12*tmp_scale[2];

        m20 = m1.m20*tmp_scale[0];
        m21 = m1.m21*tmp_scale[1];
        m22 = m1.m22*tmp_scale[2];
   }

   /**
     * Sets the rotational component (upper 3x3) of this matrix to the
     * matrix equivalent values of the quaternion argument; the other
     * elements of this matrix are unchanged; a singular value
     * decomposition is performed on this object's upper 3x3 matrix to
     * factor out the scale, then this object's upper 3x3 matrix components
     * are replaced by the matrix equivalent of the quaternion,
     * and then the scale is reapplied to the rotational components.
     * @param q1    the quaternion that specifies the rotation
     */
   public final void setRotation(Quat4f q1){
       double[]    tmp_rot = new double[9];  // scratch matrix
       double[]    tmp_scale = new double[3];  // scratch matrix
       getScaleRotate( tmp_scale, tmp_rot );

        m00 = (1.0 - 2.0f*q1.y*q1.y - 2.0f*q1.z*q1.z)*tmp_scale[0];
        m10 = (2.0*(q1.x*q1.y + q1.w*q1.z))*tmp_scale[0];
        m20 = (2.0*(q1.x*q1.z - q1.w*q1.y))*tmp_scale[0];

        m01 = (2.0*(q1.x*q1.y - q1.w*q1.z))*tmp_scale[1];
        m11 = (1.0 - 2.0f*q1.x*q1.x - 2.0f*q1.z*q1.z)*tmp_scale[1];
        m21 = (2.0*(q1.y*q1.z + q1.w*q1.x))*tmp_scale[1];

        m02 = (2.0*(q1.x*q1.z + q1.w*q1.y))*tmp_scale[2];
        m12 = (2.0*(q1.y*q1.z - q1.w*q1.x))*tmp_scale[2];
        m22 = (1.0 - 2.0f*q1.x*q1.x - 2.0f*q1.y*q1.y)*tmp_scale[2];

      }


   /**
     * Sets the rotational component (upper 3x3) of this matrix to the
     * matrix equivalent values of the quaternion argument; the other
     * elements of this matrix are unchanged; a singular value
     * decomposition is performed on this object's upper 3x3 matrix to
     * factor out the scale, then this object's upper 3x3 matrix components
     * are replaced by the matrix equivalent of the quaternion,
     * and then the scale is reapplied to the rotational components.
     * @param q1    the quaternion that specifies the rotation
     */
   public final void setRotation(Quat4d q1){

       double[]    tmp_rot = new double[9];  // scratch matrix
       double[]    tmp_scale = new double[3];  // scratch matrix
       getScaleRotate( tmp_scale, tmp_rot );

        m00 = (1.0 - 2.0f*q1.y*q1.y - 2.0f*q1.z*q1.z)*tmp_scale[0];
        m10 = (2.0*(q1.x*q1.y + q1.w*q1.z))*tmp_scale[0];
        m20 = (2.0*(q1.x*q1.z - q1.w*q1.y))*tmp_scale[0];

        m01 = (2.0*(q1.x*q1.y - q1.w*q1.z))*tmp_scale[1];
        m11 = (1.0 - 2.0f*q1.x*q1.x - 2.0f*q1.z*q1.z)*tmp_scale[1];
        m21 = (2.0*(q1.y*q1.z + q1.w*q1.x))*tmp_scale[1];

        m02 = (2.0*(q1.x*q1.z + q1.w*q1.y))*tmp_scale[2];
        m12 = (2.0*(q1.y*q1.z - q1.w*q1.x))*tmp_scale[2];
        m22 = (1.0 - 2.0f*q1.x*q1.x - 2.0f*q1.y*q1.y)*tmp_scale[2];

      }

   /**
     * Sets the rotational component (upper 3x3) of this matrix to the
     * matrix equivalent values of the axis-angle argument; the other
     * elements of this matrix are unchanged; a singular value
     * decomposition is performed on this object's upper 3x3 matrix to
     * factor out the scale, then this object's upper 3x3 matrix components
     * are replaced by the matrix equivalent of the axis-angle,
     * and then the scale is reapplied to the rotational components.
     * @param a1 the axis-angle to be converted (x, y, z, angle)
     */
    public final void setRotation(AxisAngle4d a1)
    {
	double[]    tmp_rot = new double[9];  // scratch matrix
	double[]    tmp_scale = new double[3];  // scratch matrix

	getScaleRotate( tmp_scale, tmp_rot );

        double mag = 1.0/Math.sqrt( a1.x*a1.x + a1.y*a1.y + a1.z*a1.z);
        double ax = a1.x*mag;
        double ay = a1.y*mag;
        double az = a1.z*mag;

        double sinTheta = Math.sin(a1.angle);
        double cosTheta = Math.cos(a1.angle);
        double t = 1.0 - cosTheta;

        double xz = a1.x * a1.z;
        double xy = a1.x * a1.y;
        double yz = a1.y * a1.z;

        m00 = (t * ax * ax + cosTheta)*tmp_scale[0];
        m01 = (t * xy - sinTheta * az)*tmp_scale[1];
        m02 = (t * xz + sinTheta * ay)*tmp_scale[2];

        m10 = (t * xy + sinTheta * az)*tmp_scale[0];
        m11 = (t * ay * ay + cosTheta)*tmp_scale[1];
        m12 = (t * yz - sinTheta * ax)*tmp_scale[2];

        m20 = (t * xz - sinTheta * ay)*tmp_scale[0];
        m21 = (t * yz + sinTheta * ax)*tmp_scale[1];
        m22 = (t * az * az + cosTheta)*tmp_scale[2];

    }

  /**
    *  Sets this matrix to all zeros.
    */
   public final void setZero()
   {
        m00 = 0.0;
        m01 = 0.0;
        m02 = 0.0;
        m03 = 0.0;
        m10 = 0.0;
        m11 = 0.0;
        m12 = 0.0;
        m13 = 0.0;
        m20 = 0.0;
        m21 = 0.0;
        m22 = 0.0;
        m23 = 0.0;
        m30 = 0.0;
        m31 = 0.0;
        m32 = 0.0;
        m33 = 0.0;
   }

   /**
     * Negates the value of this matrix: this = -this.
     */
    public final void negate()
    {
        m00 = -m00;
        m01 = -m01;
        m02 = -m02;
        m03 = -m03;
        m10 = -m10;
        m11 = -m11;
        m12 = -m12;
        m13 = -m13;
        m20 = -m20;
        m21 = -m21;
        m22 = -m22;
        m23 = -m23;
        m30 = -m30;
        m31 = -m31;
        m32 = -m32;
        m33 = -m33;
    }

   /**
     *  Sets the value of this matrix equal to the negation of
     *  of the Matrix4d parameter.
     *  @param m1  the source matrix
     */
    public final void negate(Matrix4d m1)
    {
        this.m00 = -m1.m00;
        this.m01 = -m1.m01;
        this.m02 = -m1.m02;
        this.m03 = -m1.m03;
        this.m10 = -m1.m10;
        this.m11 = -m1.m11;
        this.m12 = -m1.m12;
        this.m13 = -m1.m13;
        this.m20 = -m1.m20;
        this.m21 = -m1.m21;
        this.m22 = -m1.m22;
        this.m23 = -m1.m23;
        this.m30 = -m1.m30;
        this.m31 = -m1.m31;
        this.m32 = -m1.m32;
        this.m33 = -m1.m33;
    }
    private final void getScaleRotate(double scales[], double rots[]) {
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

	Matrix3d.compute_svd( tmp, scales, rots);

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
	Matrix4d m1 = null;
	try {
	    m1 = (Matrix4d)super.clone();
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
	public final  double getM00() {
		return m00;
	}

	/**
	 * Set the first matrix element in the first row.
	 *
	 * @param m00 The m00 to set.
	 *
	 *
	 * @since vecmath 1.5
	 */
	public final  void setM00(double m00) {
		this.m00 = m00;
	}

	/**
	 * Get the second matrix element in the first row.
	 *
	 * @return Returns the m01.
	 *
	 * @since vecmath 1.5
	 */
	public final  double getM01() {
		return m01;
	}

	/**
	 * Set the second matrix element in the first row.
	 *
	 * @param m01 The m01 to set.
	 *
	 * @since vecmath 1.5
	 */
	public  final void setM01(double m01) {
		this.m01 = m01;
	}

	/**
	 * Get the third matrix element in the first row.
	 *
	 * @return Returns the m02.
	 *
	 * @since vecmath 1.5
	 */
	public final double getM02() {
		return m02;
	}

	/**
	 * Set the third matrix element in the first row.
	 *
	 * @param m02 The m02 to set.
	 *
	 * @since vecmath 1.5
	 */
	public final  void setM02(double m02) {
		this.m02 = m02;
	}

	/**
	 * Get first matrix element in the second row.
	 *
	 * @return Returns the m10.
	 *
	 * @since vecmath 1.5
	 */
	public final  double getM10() {
		return m10;
	}

	/**
	 * Set first matrix element in the second row.
	 *
	 * @param m10 The m10 to set.
	 *
	 * @since vecmath 1.5
	 */
	public final  void setM10(double m10) {
		this.m10 = m10;
	}

	/**
	 * Get second matrix element in the second row.
	 *
	 * @return Returns the m11.
	 *
	 * @since vecmath 1.5
	 */
	public final  double getM11() {
		return m11;
	}

	/**
	 * Set the second matrix element in the second row.
	 *
	 * @param m11 The m11 to set.
	 *
	 * @since vecmath 1.5
	 */
	public final  void setM11(double m11) {
		this.m11 = m11;
	}

	/**
	 * Get the third matrix element in the second row.
	 *
	 * @return Returns the m12.
	 *
	 * @since vecmath 1.5
	 */
	public final  double getM12() {
		return m12;
	}

	/**
	 * Set the third matrix element in the second row.
	 *
	 * @param m12 The m12 to set.
	 *
	 *
	 * @since vecmath 1.5
	 */
	public final  void setM12(double m12) {
		this.m12 = m12;
	}

	/**
	 * Get the first matrix element in the third row.
	 *
	 * @return Returns the m20.
	 *
	 * @since vecmath 1.5
	 */
	public final  double getM20() {
		return m20;
	}

	/**
	 * Set the first matrix element in the third row.
	 *
	 * @param m20 The m20 to set.
	 *
	 * @since vecmath 1.5
	 */
	public final void setM20(double m20) {
		this.m20 = m20;
	}

	/**
	 * Get the second matrix element in the third row.
	 *
	 * @return Returns the m21.
	 *
	 * @since vecmath 1.5
	 */
	public final double getM21() {
		return m21;
	}

	/**
	 * Set the second matrix element in the third row.
	 *
	 * @param m21 The m21 to set.
	 *
	 * @since vecmath 1.5
	 */
	public final void setM21(double m21) {
		this.m21 = m21;
	}

	/**
	 * Get the third matrix element in the third row.
	 *
	 * @return Returns the m22.
	 *
	 * @since vecmath 1.5
	 */
	public final double getM22() {
		return m22;
	}

	/**
	 * Set the third matrix element in the third row.
	 *
	 * @param m22 The m22 to set.
	 *
	 * @since vecmath 1.5
	 */
	public final void setM22(double m22) {
		this.m22 = m22;
	}

	/**
	 * Get the fourth element of the first row.
	 *
	 * @return Returns the m03.
	 *
	 * @since vecmath 1.5
	 */
	public final double getM03() {
		return m03;
	}

	/**
	 * Set the fourth element of the first row.
	 *
	 * @param m03 The m03 to set.
	 *
	 * @since vecmath 1.5
	 */
	public final void setM03(double m03) {
		this.m03 = m03;
	}

	/**
	 * Get the fourth element of the second row.
	 *
	 * @return Returns the m13.
	 *
	 * @since vecmath 1.5
	 */
	public final double getM13() {
		return m13;
	}

	/**
	 * Set the fourth element of the second row.
	 *
	 * @param m13 The m13 to set.
	 *
	 * @since vecmath 1.5
	 */
	public final void setM13(double m13) {
		this.m13 = m13;
	}

	/**
	 * Get the fourth element of the third row.
	 *
	 * @return Returns the m23.
	 *
	 * @since vecmath 1.5
	 */
	public final double getM23() {
		return m23;
	}

	/**
	 * Set the fourth element of the third row.
	 *
	 * @param m23 The m23 to set.
	 *
	 * @since vecmath 1.5
	 */
	public final void setM23(double m23) {
		this.m23 = m23;
	}

	/**
	 * Get the first element of the fourth row.
	 *
	 * @return Returns the m30.
	 *
	 * @since vecmath 1.5
	 */
	public final double getM30() {
		return m30;
	}

	/**
	 * Set the first element of the fourth row.
	 *
	 * @param m30 The m30 to set.
	 *
	 * @since vecmath 1.5
	 */
	public final void setM30(double m30) {
		this.m30 = m30;
	}

	/**
	 * Get the second element of the fourth row.
	 *
	 * @return Returns the m31.
	 *
	 * @since vecmath 1.5
	 */
	public final double getM31() {
		return m31;
	}

	/**
	 * Set the second element of the fourth row.
	 *
	 * @param m31 The m31 to set.
	 *
	 * @since vecmath 1.5
	 */
	public final void setM31(double m31) {
		this.m31 = m31;
	}

	/**
	 * Get the third element of the fourth row.
	 *
	 * @return Returns the m32.
	 *
	 *
	 * @since vecmath 1.5
	 */
	public final double getM32() {
		return m32;
	}

	/**
	 * Set the third element of the fourth row.
	 *
	 * @param m32 The m32 to set.
	 *
	 * @since vecmath 1.5
	 */
	public final void setM32(double m32) {
		this.m32 = m32;
	}

	/**
	 * Get the fourth element of the fourth row.
	 *
	 * @return Returns the m33.
	 *
	 * @since vecmath 1.5
	 */
	public final double getM33() {
		return m33;
	}

	/**
	 * Set the fourth element of the fourth row.
	 *
	 * @param m33 The m33 to set.
	 *
	 * @since vecmath 1.5
	 */
	public final void setM33(double m33) {
		this.m33 = m33;
	}
}
