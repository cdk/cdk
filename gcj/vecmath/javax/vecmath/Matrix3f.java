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
 * A single precision floating point 3 by 3 matrix.
 * @version specification 1.1, implementation $Revision$, $Date$
 * @author Kenji hiranabe
 */
public class Matrix3f implements Serializable {
/*
 * $Log$
 * Revision 1.1  2002/08/22 20:01:14  egonw
 * Lots of new files. Amongst which the source code of vecmath.jar.
 * The latter has been changed to compile with gcj-3.0.4.
 * Actually, CDK does now compile, i.e. at least the classes mentioned
 * in core.classes and extra.classes. *And* a binary executable can get
 * generated that works!
 *
 * Revision 1.13  1999/10/05  07:03:50  hiranabe
 * copyright change
 *
 * Revision 1.13  1999/10/05  07:03:50  hiranabe
 * copyright change
 *
 * Revision 1.12  1999/03/04  09:16:33  hiranabe
 * small bug fix and copyright change
 *
 * Revision 1.11  1998/10/14  00:49:10  hiranabe
 * API1.1 Beta02
 *
 * Revision 1.10  1998/08/24  00:16:52  hiranabe
 * ** Matrix3f#set(float []) m22 should be m[ 8] but was m[ 9]
 * thanks > nsawa@po.cnet-ta.ne.jp
 *
 * ** added Matrix3{f,d}f#set(Matrix3{f,d})
 * thanks > nsawa@po.cnet-ta.ne.jp
 *
 * Revision 1.9  1998/07/27  04:33:08  hiranabe
 * transpose(M m1) bug. It acted as the same as 'set'.
 *
 * Revision 1.8  1998/07/27  04:28:13  hiranabe
 * API1.1Alpha01 ->API1.1Alpha03
 *
 * Revision 1.7  1998/04/17  10:30:46  hiranabe
 * null check for equals
 *
 * Revision 1.6  1998/04/10  04:52:14  hiranabe
 * API1.0 -> API1.1 (added constructors, methods)
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
 * Revision 1.2  1997/12/10  06:08:05  hiranabe
 * toString   '\n' -> "line.separator"
 *
 * Revision 1.1  1997/11/26  03:00:44  hiranabe
 * Initial revision
 *
 */

    /**
      * The first element of the first row.
      */
    public float m00;

    /**
      * The second element of the first row.
      */
    public float m01;

    /**
      * third element of the first row.
      */
    public float m02;

    /**
      * The first element of the second row.
      */
    public float m10;

    /**
      * The second element of the second row.
      */
    public float m11;

    /**
      * The third element of the second row.
      */
    public float m12;

    /**
      * The first element of the third row.
      */
    public float m20;

    /**
      * The second element of the third row.
      */
    public float m21;

    /**
      * The third element of the third row.
      */
    public float m22;

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
                    float m20, float m21, float m22) {
	set(
	    m00, m01, m02,
	    m10, m11, m12,
	    m20, m21, m22
	    );
    }

    /**
      * Constructs and initializes a Matrix3f from the specified 9
      * element array.  this.m00 =v[0], this.m01=v[1], etc.
      * @param  v the array of length 9 containing in order
      */
    public Matrix3f(float v[]) {
	set(v);
    }

    /**
      * Constructs a new matrix with the same values as the Matrix3d parameter.
      * @param m1 The source matrix.
      */
    public Matrix3f(Matrix3d m1) {
	m00 = (float)m1.m00; m01 = (float)m1.m01; m02 = (float)m1.m02;
	m10 = (float)m1.m10; m11 = (float)m1.m11; m12 = (float)m1.m12;
	m20 = (float)m1.m20; m21 = (float)m1.m21; m22 = (float)m1.m22;
    }

    /**
      * Constructs a new matrix with the same values as the Matrix3f parameter.
      * @param m1 The source matrix.
      */
    public Matrix3f(Matrix3f m1) {
	m00 = m1.m00; m01 = m1.m01; m02 = m1.m02;
	m10 = m1.m10; m11 = m1.m11; m12 = m1.m12;
	m20 = m1.m20; m21 = m1.m21; m22 = m1.m22;
    }

    /**
      * Constructs and initializes a Matrix3f to all zeros.
      */
    public Matrix3f() {
	setZero();
    }

  /**
    * Returns a string that contains the values of this Matrix3f.
    * @return the String representation
    */
    public String toString() {
	String nl = System.getProperty("line.separator"); 
        return  "["+nl+"  ["+m00+"\t"+m01+"\t"+m02+"]" + nl +
                   "  ["+m10+"\t"+m11+"\t"+m12+"]" + nl +
                   "  ["+m20+"\t"+m21+"\t"+m22+"] ]";
    }

    /**
     * Sets this Matrix3f to identity.
     */
    public final void setIdentity() {
        m00 = 1.0f; m01 = 0.0f; m02 = 0.0f;
        m10 = 0.0f; m11 = 1.0f; m12 = 0.0f;
        m20 = 0.0f; m21 = 0.0f; m22 = 1.0f;
    }

    /**
      * Sets the scale component of the current matrix by factoring out the
      * current scale (by doing an SVD) from the rotational component and
      * multiplying by the new scale.
      * @param scale the new scale amount
      */
    public final void setScale(float scale) {
	SVD(this);
	mul(scale);
    }

    /**
     * Sets the specified element of this matrix3d to the value provided.
     * @param row  the row number to be modified (zero indexed)
     * @param column  the column number to be modified (zero indexed)
     * @param value the new value
     */
    public final void setElement(int row, int column, float value) {
	if (row == 0)
	    if (column == 0)
		m00 = value;
	    else if (column == 1)
		m01 = value;
	    else if (column == 2)
		m02 = value;
	    else
		throw new ArrayIndexOutOfBoundsException("column must be 0 to 2 and is " + column);
	else if (row == 1)
	    if (column == 0)
		m10 = value;
	    else if (column == 1)
		m11 = value;
	    else if (column == 2)
		m12 = value;
	    else
		throw new ArrayIndexOutOfBoundsException("column must be 0 to 2 and is " + column);
	else if (row == 2)
	    if (column == 0)
		m20 = value;
	    else if (column == 1)
		m21 = value;
	    else if (column == 2)
		m22 = value;
	    else
		throw new ArrayIndexOutOfBoundsException("column must be 0 to 2 and is " + column);
	else
		throw new ArrayIndexOutOfBoundsException("row must be 0 to 2 and is " + row);
    }

    /**
     * Retrieves the value at the specified row and column of this matrix.
     * @param row  the row number to be retrieved (zero indexed)
     * @param column  the column number to be retrieved (zero indexed)
     * @return the value at the indexed element
     */
    public final float getElement(int row, int column) {
	if (row == 0)
	    if (column == 0)
		return m00;
	    else if (column == 1)
		return m01;
	    else if (column == 2)
		return m02;
	    else
		throw new ArrayIndexOutOfBoundsException("column must be 0 to 2 and is " + column);
	else if (row == 1)
	    if (column == 0)
		return m10;
	    else if (column == 1)
		return m11;
	    else if (column == 2)
		return m12;
	    else
		throw new ArrayIndexOutOfBoundsException("column must be 0 to 2 and is " + column);

	else if (row == 2)
	    if (column == 0)
		return m20;
	    else if (column == 1)
		return m21;
	    else if (column == 2)
		return m22;
	    else
		throw new ArrayIndexOutOfBoundsException("column must be 0 to 2 and is " + column);
	else
		throw new ArrayIndexOutOfBoundsException("row must be 0 to 2 and is " + row);
    }


    /**
     * Sets the specified row of this matrix3d to the three values provided.
     * @param row  the row number to be modified (zero indexed)
     * @param x the first column element
     * @param y the second column element
     * @param z the third column element
     */
    public final void setRow(int row, float x, float y, float z) {
	if (row == 0) {
	    m00 = x;
	    m01 = y;
	    m02 = z;
	} else if (row == 1) {
	    m10 = x;
	    m11 = y;
	    m12 = z;
	} else if (row == 2) {
	    m20 = x;
	    m21 = y;
	    m22 = z;
	} else {
	    throw new ArrayIndexOutOfBoundsException("row must be 0 to 2 and is " + row);
	}
    }

    /**
     * Sets the specified row of this matrix3d to the Vector provided.
     * @param row the row number to be modified (zero indexed)
     * @param v the replacement row
     */
    public final void setRow(int row, Vector3f v) {
	if (row == 0) {
	    m00 = v.x;
	    m01 = v.y;
	    m02 = v.z;
	} else if (row == 1) {
	    m10 = v.x;
	    m11 = v.y;
	    m12 = v.z;
	} else if (row == 2) {
	    m20 = v.x;
	    m21 = v.y;
	    m22 = v.z;
	} else {
	    throw new ArrayIndexOutOfBoundsException("row must be 0 to 2 and is " + row);
	}
    }

    /**
      * Copies the matrix values in the specified row into the
      * array parameter.
      * @param row the matrix row
      * @param v The array into which the matrix row values will be copied
      */
    public final void getRow(int row, float v[]) {
	if (row == 0) {
	    v[0] = m00;
	    v[1] = m01;
	    v[2] = m02;
	} else if (row == 1) {
	    v[0] = m10;
	    v[1] = m11;
	    v[2] = m12;
	} else if (row == 2) {
	    v[0] = m20;
	    v[1] = m21;
	    v[2] = m22;
	} else {
	    throw new ArrayIndexOutOfBoundsException("row must be 0 to 2 and is " + row);
	}
    }

    /**
     * Copies the matrix values in the specified row into the
     * vector parameter.
     * @param row the matrix row
     * @param v The vector into which the matrix row values will be copied
     */
    public final void getRow(int row, Vector3f v) {
	if (row == 0) {
	    v.x = m00;
	    v.y = m01;
	    v.z = m02;
	} else if (row == 1) {
	    v.x = m10;
	    v.y = m11;
	    v.z = m12;
	} else if (row == 2) {
	    v.x = m20;
	    v.y = m21;
	    v.z = m22;
	} else {
	    throw new ArrayIndexOutOfBoundsException("row must be 0 to 2 and is " + row);
	}
    }

    /**
      * Sets the specified row of this matrix3d to the four values provided.
      * @param row the row number to be modified (zero indexed)
      * @param v the replacement row
      */
    public final void setRow(int row, float v[]) {
	if (row == 0) {
	    m00 = v[0];
	    m01 = v[1];
	    m02 = v[2];
	} else if (row == 1) {
	    m10 = v[0];
	    m11 = v[1];
	    m12 = v[2];
	} else if (row == 2) {
	    m20 = v[0];
	    m21 = v[1];
	    m22 = v[2];
	} else {
	    throw new ArrayIndexOutOfBoundsException("row must be 0 to 2 and is " + row);
	}
    }

    /**
      * Sets the specified column of this matrix3d to the three values provided.
      * @param  column the column number to be modified (zero indexed)
      * @param x the first row element
      * @param y the second row element
      * @param z the third row element
      */
    public final void setColumn(int column, float x, float y, float z) {
	if (column == 0) {
	    m00 = x;
	    m10 = y;
	    m20 = z;
	}  else if (column == 1) {
	    m01 = x;
	    m11 = y;
	    m21 = z;
	} else if (column == 2) {
	    m02 = x;
	    m12 = y;
	    m22 = z;
	} else {
	    throw new ArrayIndexOutOfBoundsException("column must be 0 to 2 and is " + column);
	}
    }

    /**
      * Sets the specified column of this matrix3d to the vector provided.
      * @param column the column number to be modified (zero indexed)
      * @param v the replacement column
      */
    public final void setColumn(int column, Vector3f v) {
	if (column == 0) {
	    m00 = v.x;
	    m10 = v.y;
	    m20 = v.z;
	} else if (column == 1) {
	    m01 = v.x;
	    m11 = v.y;
	    m21 = v.z;
	} else if (column == 2) {
	    m02 = v.x;
	    m12 = v.y;
	    m22 = v.z;
	} else {
	    throw new ArrayIndexOutOfBoundsException("column must be 0 to 2 and is " + column);
	}
    }

    /**
      * Sets the specified column of this matrix3d to the four values provided. 
      * @param column  the column number to be modified (zero indexed) 
      * @param v       the replacement column 
      */
    public final void setColumn(int column,  float v[]) {
	if (column == 0) {
	    m00 = v[0];
	    m10 = v[1];
	    m20 = v[2];
	} else if (column == 1) {
	    m01 = v[0];
	    m11 = v[1];
	    m21 = v[2];
	} else if (column == 2) {
	    m02 = v[0];
	    m12 = v[1];
	    m22 = v[2];
	} else {
	    throw new ArrayIndexOutOfBoundsException("column must be 0 to 2 and is " + column);
	}
    }

    /**
      * Copies the matrix values in the specified column into the vector 
      * parameter.
      * @param column the matrix column
      * @param v The vector into which the matrix row values will be copied
      */
    public final void getColumn(int column, Vector3f v) {
	if (column == 0) {
	    v.x = m00;
	    v.y = m10;
	    v.z = m20;
	} else if (column == 1) {
	    v.x = m01;
	    v.y = m11;
	    v.z = m21;
	} else if (column == 2) {
	    v.x = m02;
	    v.y = m12;
	    v.z = m22;
	} else {
	    throw new ArrayIndexOutOfBoundsException("column must be 0 to 2 and is " + column);
	}
    }

    /**
      * Copies the matrix values in the specified column into the array
      * parameter.
      * @param column the matrix column
      * @param v The array into which the matrix row values will be copied
      */
    public final void getColumn(int column,  float v[]) {
	if (column == 0) {
	    v[0] = m00;
	    v[1] = m10;
	    v[2] = m20;
	} else if (column == 1) {
	    v[0] = m01;
	    v[1] = m11;
	    v[2] = m21;
	} else if (column == 2) {
	    v[0] = m02;
	    v[1] = m12;
	    v[2] = m22;
	} else {
	    throw new ArrayIndexOutOfBoundsException("column must be 0 to 2 and is " + column);
	}
    }


    /**
      * Performs an SVD normalization of this matrix to calculate and return the
      * uniform scale factor. This matrix is not modified.
      * @return the scale factor of this matrix
      */
    public final float getScale() {
	return SVD(null);
    }


    /**
      * Adds a scalar to each component of this matrix.
      * @param scalar The scalar adder.
      */
    public final void add(float scalar) {
	m00 += scalar; m01 += scalar; m02 += scalar;
	m10 += scalar; m11 += scalar; m12 += scalar;
	m20 += scalar; m21 += scalar; m22 += scalar;
    }

    /**
      * Adds a scalar to each component of the matrix m1 and places
      * the result into this. Matrix m1 is not modified.
      * @param scalar The scalar adder.
      * @parm m1 The original matrix values.
      */
    public final void add(float scalar, Matrix3f m1) {
	set(m1);
	add(scalar);
    }


    /**
     * Sets the value of this matrix to the matrix sum of matrices m1 and m2. 
     * @param m1 the first matrix 
     * @param m2 the second matrix 
     */
    public final void add(Matrix3f m1, Matrix3f m2) {
	// note this is alias safe.
	set(
	    m1.m00 + m2.m00,
	    m1.m01 + m2.m01,
	    m1.m02 + m2.m02,
	    m1.m10 + m2.m10,
	    m1.m11 + m2.m11,
	    m1.m12 + m2.m12,
	    m1.m20 + m2.m20,
	    m1.m21 + m2.m21,
	    m1.m22 + m2.m22
	    );
    }

    /**
     * Sets the value of this matrix to sum of itself and matrix m1. 
     * @param m1 the other matrix 
     */
    public final void add(Matrix3f m1) {
	m00 += m1.m00; m01 += m1.m01; m02 += m1.m02;
	m10 += m1.m10; m11 += m1.m11; m12 += m1.m12;
	m20 += m1.m20; m21 += m1.m21; m22 += m1.m22;
    }

    /**
      * Sets the value of this matrix to the matrix difference
      * of matrices m1 and m2. 
      * @param m1 the first matrix 
      * @param m2 the second matrix 
      */
    public final void sub(Matrix3f m1, Matrix3f m2) {
	set(
	    m1.m00 - m2.m00,
	    m1.m01 - m2.m01,
	    m1.m02 - m2.m02,
	    m1.m10 - m2.m10,
	    m1.m11 - m2.m11,
	    m1.m12 - m2.m12,
	    m1.m20 - m2.m20,
	    m1.m21 - m2.m21,
	    m1.m22 - m2.m22
	    );
    }

    /**
     * Sets the value of this matrix to the matrix difference of itself
     * and matrix m1 (this = this - m1). 
     * @param m1 the other matrix 
     */
    public final void sub(Matrix3f m1) {
	m00 -= m1.m00; m01 -= m1.m01; m02 -= m1.m02;
	m10 -= m1.m10; m11 -= m1.m11; m12 -= m1.m12;
	m20 -= m1.m20; m21 -= m1.m21; m22 -= m1.m22;
    }

    /**
      * Sets the value of this matrix to its transpose. 
      */
    public final void transpose() {
	float tmp = m01;
	m01 = m10;
	m10 = tmp;

	tmp = m02;
	m02 = m20;
	m20 = tmp;

	tmp = m12;
	m12 = m21;
	m21 = tmp;

    }

    /**
     * Sets the value of this matrix to the transpose of the argument matrix
     * @param m1 the matrix to be transposed 
     */
    public final void transpose(Matrix3f m1) {
	// alias-safe
	set(m1);
	transpose();
    }

    /**
      * Sets the value of this matrix to the matrix conversion of the
      * (single precision) quaternion argument. 
      * @param q1 the quaternion to be converted 
      */
    public final void set(Quat4f q1) {
	setFromQuat(q1.x, q1.y, q1.z, q1.w);
    }

    /**
      * Sets the value of this matrix to the matrix conversion of the
      * single precision axis and angle argument. 
      * @param a1 the axis and angle to be converted 
      */
    public final void set(AxisAngle4f a1) {
	setFromAxisAngle(a1.x, a1.y, a1.z, a1.angle);
    }

    /**
      * Sets the value of this matrix to the matrix conversion of the
      * double precision axis and angle argument. 
      * @param a1 the axis and angle to be converted 
      */
    public final void set(AxisAngle4d a1) {
	setFromAxisAngle(a1.x, a1.y, a1.z, a1.angle);
    }

    /**
      * Sets the value of this matrix to the matrix conversion of the
      * (double precision) quaternion argument. 
      * @param q1 the quaternion to be converted 
      */
    public final void set(Quat4d q1) {
	setFromQuat(q1.x, q1.y, q1.z, q1.w);
    }

    /**
      * Sets the value of this matrix to the double value of the Matrix3f
      * argument.
      * @param m1 the matrix3f
    */
    public final void set(Matrix3f m1)  {
	m00 = m1.m00; m01 = m1.m01; m02 = m1.m02;
	m10 = m1.m10; m11 = m1.m11; m12 = m1.m12;
	m20 = m1.m20; m21 = m1.m21; m22 = m1.m22;
    }

    /**
      * Sets the value of this matrix to the float value of the Matrix3d
      * argument.
      * @param m1 the matrix3f
    */
    public final void set(Matrix3d m1)  {
	m00 = (float)m1.m00; m01 = (float)m1.m01; m02 = (float)m1.m02;
	m10 = (float)m1.m10; m11 = (float)m1.m11; m12 = (float)m1.m12;
	m20 = (float)m1.m20; m21 = (float)m1.m21; m22 = (float)m1.m22;
    }


    /**
      * Sets the values in this Matrix3f equal to the row-major array parameter
      * (ie, the first four elements of the array will be copied into the first
      * row of this matrix, etc.).
      */
    public final void set(float m[]) {
	m00 = m[ 0]; m01 = m[ 1]; m02 = m[ 2];
	m10 = m[ 3]; m11 = m[ 4]; m12 = m[ 5];
	m20 = m[ 6]; m21 = m[ 7]; m22 = m[ 8];
    }

    /**
     * Sets the value of this matrix to the matrix inverse
     * of the passed matrix m1. 
     * @param m1 the matrix to be inverted 
     */
    public final void invert(Matrix3f m1)  {
	set(m1);
	invert();
    }

    /**
     * Sets the value of this matrix to its inverse.
     */
    public final void invert() {
	double s = determinant();
	if (s == 0.0)
	    return;
	s = 1/s;
	// alias-safe way.
	set(
	    m11*m22 - m12*m21, m02*m21 - m01*m22, m01*m12 - m02*m11,
	    m12*m20 - m10*m22, m00*m22 - m02*m20, m02*m10 - m00*m12,
	    m10*m21 - m11*m20, m01*m20 - m00*m21, m00*m11 - m01*m10
	    );
	mul((float)s);
    }

    /**
     * Computes the determinant of this matrix. 
     * @return the determinant of the matrix 
     */
    public final float determinant()  {
	// less *,+,- calculation than expanded expression.
	return m00*(m11*m22 - m21*m12)
	     - m01*(m10*m22 - m20*m12)
	     + m02*(m10*m21 - m20*m11);
    }

    /**
     * Sets the value of this matrix to a scale matrix with the
     * passed scale amount. 
     * @param scale the scale factor for the matrix 
     */
    public final void set(float scale)  {
	m00 = scale; m01 = 0.0f;   m02 = 0.0f;
	m10 = 0.0f;   m11 = scale; m12 = 0.0f;
	m20 = 0.0f;   m21 = 0.0f;   m22 = scale;
    }


    /**
     * Sets the value of this matrix to a rotation matrix about the x axis
     * by the passed angle. 
     * @param angle the angle to rotate about the X axis in radians 
     */
    public final void rotX(float angle)  {
	double c = Math.cos(angle);
	double s = Math.sin(angle);
	m00 = 1.0f; m01 = 0.0f; m02 = 0.0f;
	m10 = 0.0f; m11 = (float)c;   m12 = (float)-s;
	m20 = 0.0f; m21 = (float)s;   m22 = (float)c;
    }

    /**
     * Sets the value of this matrix to a rotation matrix about the y axis
     * by the passed angle. 
     * @param angle the angle to rotate about the Y axis in radians 
     */
    public final void rotY(float angle)  {
	double c = Math.cos(angle);
	double s = Math.sin(angle);
	m00 = (float)c;   m01 = 0.0f; m02 = (float)s;
	m10 = 0.0f;       m11 = 1.0f; m12 = 0.0f;
	m20 = (float)-s;  m21 = 0.0f; m22 = (float)c;
    }

    /**
     * Sets the value of this matrix to a rotation matrix about the z axis
     * by the passed angle. 
     * @param angle the angle to rotate about the Z axis in radians 
     */
    public final void rotZ(float angle)  {
	double c = Math.cos(angle);
	double s = Math.sin(angle);
	m00 = (float)c;   m01 = (float)-s;  m02 = 0.0f;
	m10 = (float)s;   m11 = (float)c;   m12 = 0.0f;
	m20 = 0.0f;       m21 = 0.0f;       m22 = 1.0f;
    }

    /**
      * Multiplies each element of this matrix by a scalar.
      * @param scalar The scalar multiplier.
      */
     public final void mul(float scalar) {
	m00 *= scalar; m01 *= scalar;  m02 *= scalar;
	m10 *= scalar; m11 *= scalar;  m12 *= scalar;
	m20 *= scalar; m21 *= scalar;  m22 *= scalar;
     }

    /**
      * Multiplies each element of matrix m1 by a scalar and places the result
      * into this. Matrix m1 is not modified.
      * @param scalar The scalar multiplier.
      * @param m1 The original matrix.
      */
     public final void mul(float scalar, Matrix3f m1) {
	 set(m1);
	 mul(scalar);
     }

    /**
     * Sets the value of this matrix to the result of multiplying itself
     * with matrix m1. 
     * @param m1 the other matrix 
     */
    public final void mul(Matrix3f m1) {
	mul(this, m1);
    }

    /**
     * Sets the value of this matrix to the result of multiplying
     * the two argument matrices together. 
     * @param m1 the first matrix 
     * @param m2 the second matrix 
     */
    public final void mul(Matrix3f m1, Matrix3f m2) {
	// alias-safe way.
	set(
	    m1.m00*m2.m00 + m1.m01*m2.m10 + m1.m02*m2.m20,
	    m1.m00*m2.m01 + m1.m01*m2.m11 + m1.m02*m2.m21,
	    m1.m00*m2.m02 + m1.m01*m2.m12 + m1.m02*m2.m22,

	    m1.m10*m2.m00 + m1.m11*m2.m10 + m1.m12*m2.m20,
	    m1.m10*m2.m01 + m1.m11*m2.m11 + m1.m12*m2.m21,
	    m1.m10*m2.m02 + m1.m11*m2.m12 + m1.m12*m2.m22,

	    m1.m20*m2.m00 + m1.m21*m2.m10 + m1.m22*m2.m20,
	    m1.m20*m2.m01 + m1.m21*m2.m11 + m1.m22*m2.m21,
	    m1.m20*m2.m02 + m1.m21*m2.m12 + m1.m22*m2.m22
	    );
    }

    /**
      * Multiplies this matrix by matrix m1, does an SVD normalization of the
      * result, and places the result back into this matrix this =
      * SVDnorm(this*m1).
      * @param m1 the matrix on the right hand side of the multiplication
      */
    public final void mulNormalize(Matrix3f m1) {
	mul(m1);
	SVD(this);
    }


    /**
      * Multiplies matrix m1 by matrix m2, does an SVD normalization of the
      * result, and places the result into this matrix this = SVDnorm(m1*m2).
      * @param m1  the matrix on the left hand side of the multiplication
      * @param m2  the matrix on the right hand side of the multiplication
      */
    public final void mulNormalize(Matrix3f m1, Matrix3f m2) {
	mul(m1, m2);
	SVD(this);
    }

    /**
      * Multiplies the transpose of matrix m1 times the transpose of matrix m2,
      * and places the result into this.
      * @param m1 The matrix on the left hand side of the multiplication
      * @param m2 The matrix on the right hand side of the multiplication
      */
    public final void mulTransposeBoth(Matrix3f m1, Matrix3f m2) {
	mul(m2, m1);
	transpose();
    }

    /**
      * Multiplies matrix m1 times the transpose of matrix m2, and places the
      * result into this.
      * @param m1 The matrix on the left hand side of the multiplication
      * @param m2 The matrix on the right hand side of the multiplication
      */
    public final void mulTransposeRight(Matrix3f m1, Matrix3f m2) {
	// alias-safe way.
	set(
	    m1.m00*m2.m00 + m1.m01*m2.m01 + m1.m02*m2.m02,
	    m1.m00*m2.m10 + m1.m01*m2.m11 + m1.m02*m2.m12,
	    m1.m00*m2.m20 + m1.m01*m2.m21 + m1.m02*m2.m22,

	    m1.m10*m2.m00 + m1.m11*m2.m01 + m1.m12*m2.m02,
	    m1.m10*m2.m10 + m1.m11*m2.m11 + m1.m12*m2.m12,
	    m1.m10*m2.m20 + m1.m11*m2.m21 + m1.m12*m2.m22,

	    m1.m20*m2.m00 + m1.m21*m2.m01 + m1.m22*m2.m02,
	    m1.m20*m2.m10 + m1.m21*m2.m11 + m1.m22*m2.m12,
	    m1.m20*m2.m20 + m1.m21*m2.m21 + m1.m22*m2.m22
	    );
    }

    
    /**
      * Multiplies the transpose of matrix m1 times matrix m2, and places the
      * result into this.
      * @param m1 The matrix on the left hand side of the multiplication
      * @param m2 The matrix on the right hand side of the multiplication
      */
    public final void mulTransposeLeft(Matrix3f m1, Matrix3f m2) {
	// alias-safe way.
	set(
	    m1.m00*m2.m00 + m1.m10*m2.m10 + m1.m20*m2.m20,
	    m1.m00*m2.m01 + m1.m10*m2.m11 + m1.m20*m2.m21,
	    m1.m00*m2.m02 + m1.m10*m2.m12 + m1.m20*m2.m22,

	    m1.m01*m2.m00 + m1.m11*m2.m10 + m1.m21*m2.m20,
	    m1.m01*m2.m01 + m1.m11*m2.m11 + m1.m21*m2.m21,
	    m1.m01*m2.m02 + m1.m11*m2.m12 + m1.m21*m2.m22,

	    m1.m02*m2.m00 + m1.m12*m2.m10 + m1.m22*m2.m20,
	    m1.m02*m2.m01 + m1.m12*m2.m11 + m1.m22*m2.m21,
	    m1.m02*m2.m02 + m1.m12*m2.m12 + m1.m22*m2.m22
	    );
    }

    /**
      * Performs singular value decomposition normalization of this matrix.
      */
    public final void normalize() {
	SVD(this);
    }

    /**
      * Perform singular value decomposition normalization of matrix m1 and
      * place the normalized values into this.
      * @param m1 Provides the matrix values to be normalized
      */
    public final void normalize(Matrix3f m1) {
	set(m1);
	SVD(this);
    }

    /**
      * Perform cross product normalization of this matrix.
      */
    public final void normalizeCP() {
	// domain error may occur
	double s = Math.pow(determinant(), -1.0/3.0);
	mul((float)s);
    }
      
    /**
      * Perform cross product normalization of matrix m1 and place the
      * normalized values into this.
      * @param m1 Provides the matrix values to be normalized
      */
    public final void normalizeCP(Matrix3f m1) {
	set(m1);
	normalizeCP();
    }



    /**
     * Returns true if all of the data members of Matrix3f m1 are
     * equal to the corresponding data members in this Matrix3f. 
     * @param m1 The matrix with which the comparison is made. 
     * @return true or false 
     */
    public boolean equals(Matrix3f m1)  {
	return  m1 != null
	        && m00 == m1.m00
		&& m01 == m1.m01
		&& m02 == m1.m02 
		&& m10 == m1.m10
		&& m11 == m1.m11
		&& m12 == m1.m12
		&& m20 == m1.m20
		&& m21 == m1.m21
		&& m22 == m1.m22;
    }

    /**
      * Returns true if the Object o1 is of type Matrix3f and all of the data
      * members of t1 are equal to the corresponding data members in this
      * Matrix3f.
      * @param o1 the object with which the comparison is made.
      */
    public boolean equals(Object o1) {
	return o1 != null && (o1 instanceof Matrix3f) && equals((Matrix3f)o1);
    }

    /**
      * Returns true if the L-infinite distance between this matrix and matrix
      * m1 is less than or equal to the epsilon parameter, otherwise returns
      * false. The L-infinite distance is equal to MAX[i=0,1,2,3 ; j=0,1,2,3 ;
      * abs(this.m(i,j) - m1.m(i,j)]
      * @param m1 The matrix to be compared to this matrix
      * @param epsilon the threshold value
      */
      public boolean epsilonEquals(Matrix3f m1, double epsilon) {
	  return  Math.abs(m00 - m1.m00) <= epsilon
		&& Math.abs(m01 - m1.m01) <= epsilon
		&& Math.abs(m02 - m1.m02 ) <= epsilon

		&& Math.abs(m10 - m1.m10) <= epsilon
		&& Math.abs(m11 - m1.m11) <= epsilon
		&& Math.abs(m12 - m1.m12) <= epsilon

		&& Math.abs(m20 - m1.m20) <= epsilon
		&& Math.abs(m21 - m1.m21) <= epsilon
		&& Math.abs(m22 - m1.m22) <= epsilon;
      }

    /**
     * Returns a hash number based on the data values in this
     * object.  Two different Matrix3f objects with identical data values
     * (ie, returns true for equals(Matrix3f) ) will return the same hash
     * number.  Two objects with different data members may return the
     * same hash value, although this is not likely.
     * @return the integer hash value 
     */
    public int hashCode() {
	return Float.floatToIntBits(m00) ^
	       Float.floatToIntBits(m01) ^
	       Float.floatToIntBits(m02) ^
               Float.floatToIntBits(m10) ^
	       Float.floatToIntBits(m11) ^
	       Float.floatToIntBits(m12) ^
	       Float.floatToIntBits(m20) ^
	       Float.floatToIntBits(m21) ^
	       Float.floatToIntBits(m22);
    }

    /**
     * Sets this matrix to all zeros. 
     */
    public final void setZero() {
	m00 = 0.0f; m01 = 0.0f; m02 = 0.0f;
	m10 = 0.0f; m11 = 0.0f; m12 = 0.0f;
	m20 = 0.0f; m21 = 0.0f; m22 = 0.0f;
    }

    /**
      * Negates the value of this matrix: this = -this.
      */
    public final void negate() {
        m00 = -m00; m01 = -m01; m02 = -m02;
        m10 = -m10; m11 = -m11; m12 = -m12;
        m20 = -m20; m21 = -m21; m22 = -m22;
    }

    /**
      * Sets the value of this matrix equal to the negation of of the Matrix3f
      * parameter.
      * @param m1 The source matrix
      */
    public final void negate(Matrix3f m1) {
	set(m1);
	negate();
    }

    /**
     * Transform the vector vec using this Matrix3f and place the
     * result back into vec.
     * @param vec the single precision vector to be transformed
     */
    public final void transform(Tuple3f t)  {
	// alias-safe
	transform(t, t);
    }

    /**
     * Transform the vector vec using this Matrix3f and place the
     * result into vecOut.
     * @paramt the single precision vector to be transformed
     * @param result the vector into which the transformed values are placed
     */
    public final void transform(Tuple3f t, Tuple3f result) {
	// alias-safe
	result.set(
	    m00*t.x + m01*t.y + m02*t.z,
	    m10*t.x + m11*t.y + m12*t.z,
	    m20*t.x + m21*t.y + m22*t.z
	    );
    }

    /**
      * Sets 9 values	
      */
    private void set(float m00, float m01, float m02,
		  float m10, float m11, float m12,
		  float m20, float m21, float m22) {
	this.m00 = m00; this.m01 = m01; this.m02 = m02;
	this.m10 = m10; this.m11 = m11; this.m12 = m12;
	this.m20 = m20; this.m21 = m21; this.m22 = m22;
    }

    /**
      * Performs SVD on this matrix and gets scale and rotation.
      * Rotation is placed into rot.
      * @param rot the rotation factor.
      * @return scale factor
      */
    private float SVD(Matrix3f rot) {
	// this is a simple svd.
	// Not complete but fast and reasonable.

	/*
	 * SVD scale factors(squared) are the 3 roots of
	 * 
	 *     | xI - M*MT | = 0.
	 * 
	 * This will be expanded as follows
	 * 
	 * x^3 - A x^2 + B x - C = 0
	 * 
	 * where A, B, C can be denoted by 3 roots x0, x1, x2.
	 *
	 * A = (x0+x1+x2), B = (x0x1+x1x2+x2x0), C = x0x1x2.
	 *
	 * An avarage of x0,x1,x2 is needed here. C^(1/3) is a cross product
	 * normalization factor.
	 * So here, I use A/3. Note that x should be sqrt'ed for the
	 * actual factor.
	 */

	float s = (float)Math.sqrt(
	    (
	     m00*m00 + m10*m10 + m20*m20 + 
	     m01*m01 + m11*m11 + m21*m21 +
	     m02*m02 + m12*m12 + m22*m22
	    )/3.0
	    );

	// zero-div may occur.
	float t = (s == 0.0f ? 0.0f : 1.0f/s);

	if (rot != null) {
	    if (rot != this)
		rot.set(this);
	    rot.mul(t);
	}

	return s;
    }

    private void setFromQuat(double x, double y, double z, double w) {
	double n = x*x + y*y + z*z + w*w;
	double s = (n > 0.0) ? (2.0/n) : 0.0;

	double xs = x*s,  ys = y*s,  zs = z*s;
	double wx = w*xs, wy = w*ys, wz = w*zs;
	double xx = x*xs, xy = x*ys, xz = x*zs;
	double yy = y*ys, yz = y*zs, zz = z*zs;

	m00 = (float)(1.0 - (yy + zz));	m01 = (float)(xy - wz);         m02 = (float)(xz + wy);
	m10 = (float)(xy + wz);         m11 = (float)(1.0 - (xx + zz)); m12 = (float)(yz - wx);
	m20 = (float)(xz - wy);         m21 = (float)(yz + wx);         m22 = (float)(1.0 - (xx + yy));
    }

    private void setFromAxisAngle(double x, double y, double z, double angle) {
	// Taken from Rick's which is taken from Wertz. pg. 412
	// Bug Fixed and changed into right-handed by hiranabe
	double n = Math.sqrt(x*x + y*y + z*z);
	// zero-div may occur
	n = 1/n;
	x *= n;
	y *= n;
	z *= n;
	double c = Math.cos(angle);
	double s = Math.sin(angle);
	double omc = 1.0 - c;
	m00 = (float)(c + x*x*omc);
	m11 = (float)(c + y*y*omc);
	m22 = (float)(c + z*z*omc);

	double tmp1 = x*y*omc;
	double tmp2 = z*s;
	m01 = (float)(tmp1 - tmp2);
	m10 = (float)(tmp1 + tmp2);

	tmp1 = x*z*omc;
	tmp2 = y*s;
	m02 = (float)(tmp1 + tmp2);
	m20 = (float)(tmp1 - tmp2);

	tmp1 = y*z*omc;
	tmp2 = x*s;
	m12 = (float)(tmp1 - tmp2);
	m21 = (float)(tmp1 + tmp2);
    }

}
