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
 * A single precision floating point 4 by 4 matrix.
 * @version specification 1.1, implementation $Revision$, $Date$
 * @author Kenji hiranabe
 */
public class Matrix4f implements Serializable {
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
 * Revision 1.10  1998/07/27  04:33:08  hiranabe
 * transpose(M m1) bug. It acted as the same as 'set'.
 *
 * Revision 1.9  1998/07/27  04:28:13  hiranabe
 * API1.1Alpha01 ->API1.1Alpha03
 *
 * Revision 1.8  1998/04/17  10:30:46  hiranabe
 * null check for equals
 *
 * Revision 1.7  1998/04/10  04:52:14  hiranabe
 * API1.0 -> API1.1 (added constructors, methods)
 *
 * Revision 1.6  1998/04/09  08:18:15  hiranabe
 * minor comment change
 *
 * Revision 1.5  1998/04/09  07:05:18  hiranabe
 * API 1.1
 *
 * Revision 1.4  1998/04/08  06:01:08  hiranabe
 * bug fix of set(m,t,s). thanks > t.m.child@surveying.salford.ac.uk
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
      * The fourth element of the first row.
      */
    public float m03;

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
      * The fourth element of the second row.
      */
    public float m13;

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
      * The fourth element of the third row.
      */
    public float m23;

    /**
      * The first element of the fourth row.
      */
    public float m30;

    /**
      * The second element of the fourth row.
      */
    public float m31;

    /**
      * The third element of the fourth row.
      */
    public float m32;

    /**
      * The fourth element of the fourth row.
      */
    public float m33;

    /**
      * 
      * Constructs and initializes a Matrix4f from the specified 16 values.
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
    public Matrix4f(float m00, float m01, float m02, float m03, 
                    float m10, float m11, float m12, float m13,
                    float m20, float m21, float m22, float m23,
                    float m30, float m31, float m32, float m33)  {
	set(
	    m00, m01, m02, m03,
	    m10, m11, m12, m13,
	    m20, m21, m22, m23,
	    m30, m31, m32, m33
	    );
    }

    /**
      * Constructs and initializes a Matrix4f from the specified 16
      * element array.  this.m00 =v[0], this.m01=v[1], etc.
      * @param  v the array of length 16 containing in order
      */
    public Matrix4f(float v[]) {
	set(v);
    }

    /**
      * Constructs and initializes a Matrix4f from the quaternion,
      * translation, and scale values; the scale is applied only to the
      * rotational components of the matrix (upper 3x3) and not to the
      * translational components.
      * @param q1  The quaternion value representing the rotational component
      * @param t1  The translational component of the matrix
      * @param s  The scale value applied to the rotational components
      */
    public Matrix4f(Quat4f q1, Vector3f t1, float s) {
	set(q1, t1, s);
    }

    /**
      * Constructs a new matrix with the same values as the Matrix4d parameter.
      * @param m1 The source matrix.
      */
    public Matrix4f(Matrix4d m1) {
	set(m1);
    }

    /**
      * Constructs a new matrix with the same values as the Matrix4f parameter.
      * @param m1 The source matrix.
      */
    public Matrix4f(Matrix4f m1) {
	set(m1);
    }

    /**
      * Constructs and initializes a Matrix4f from the rotation matrix,
      * translation, and scale values; the scale is applied only to the
      * rotational components of the matrix (upper 3x3) and not to the
      * translational components.
      * @param m1  The rotation matrix representing the rotational components
      * @param t1  The translational components of the matrix
      * @param s  The scale value applied to the rotational components
      */
    public Matrix4f(Matrix3f m1, Vector3f t1, float s) {
	set(m1);
	mulRotationScale(s);
	setTranslation(t1);
	m33 = 1.0f;
    }

    /**
      * Constructs and initializes a Matrix4f to all zeros.
      */
    public Matrix4f() {
	setZero();
    }

    /**
     * Returns a string that contains the values of this Matrix4f.
     * @return the String representation
     */
    public String toString() {
	String nl = System.getProperty("line.separator"); 
        return  "[" + nl + "  ["+m00+"\t"+m01+"\t"+m02+"\t"+m03+"]" + nl +
                   "  ["+m10+"\t"+m11+"\t"+m12+"\t"+m13+"]" + nl +
                   "  ["+m20+"\t"+m21+"\t"+m22+"\t"+m23+"]" + nl +
                   "  ["+m30+"\t"+m31+"\t"+m32+"\t"+m33+"] ]";
    }

    /**
     * Sets this Matrix4f to identity.
     */
    public final void setIdentity() {
        m00 = 1.0f; m01 = 0.0f; m02 = 0.0f; m03 = 0.0f;
        m10 = 0.0f; m11 = 1.0f; m12 = 0.0f; m13 = 0.0f;
        m20 = 0.0f; m21 = 0.0f; m22 = 1.0f; m23 = 0.0f;
        m30 = 0.0f; m31 = 0.0f; m32 = 0.0f; m33 = 1.0f;
    }

    /**
     * Sets the specified element of this matrix4f to the value provided.
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
	    else if (column == 3)
		m03 = value;
	    else
		throw new ArrayIndexOutOfBoundsException("column must be 0 to 3 and is " + column);
	else if (row == 1)
	    if (column == 0)
		m10 = value;
	    else if (column == 1)
		m11 = value;
	    else if (column == 2)
		m12 = value;
	    else if (column == 3)
		m13 = value;
	    else
		throw new ArrayIndexOutOfBoundsException("column must be 0 to 3 and is " + column);
	else if (row == 2)
	    if (column == 0)
		m20 = value;
	    else if (column == 1)
		m21 = value;
	    else if (column == 2)
		m22 = value;
	    else if (column == 3)
		m23 = value;
	    else
		throw new ArrayIndexOutOfBoundsException("column must be 0 to 3 and is " + column);
	else if (row == 3)
	    if (column == 0)
		m30 = value;
	    else if (column == 1)
		m31 = value;
	    else if (column == 2)
		m32 = value;
	    else if (column == 3)
		m33 = value;
	    else
		throw new ArrayIndexOutOfBoundsException("column must be 0 to 3 and is " + column);
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
	    else if (column == 3)
		return m03;
	    else
		throw new ArrayIndexOutOfBoundsException("column must be 0 to 3 and is " + column);
	else if (row == 1)
	    if (column == 0)
		return m10;
	    else if (column == 1)
		return m11;
	    else if (column == 2)
		return m12;
	    else if (column == 3)
		return m13;
	    else
		throw new ArrayIndexOutOfBoundsException("column must be 0 to 3 and is " + column);
	else if (row == 2)
	    if (column == 0)
		return m20;
	    else if (column == 1)
		return m21;
	    else if (column == 2)
		return m22;
	    else if (column == 3)
		return m23;
	    else
		throw new ArrayIndexOutOfBoundsException("column must be 0 to 3 and is " + column);
	else if (row == 3)
	    if (column == 0)
		return m30;
	    else if (column == 1)
		return m31;
	    else if (column == 2)
		return m32;
	    else if (column == 3)
		return m33;
	    else
		throw new ArrayIndexOutOfBoundsException("column must be 0 to 3 and is " + column);
	else
		throw new ArrayIndexOutOfBoundsException("row must be 0 to 3 and is " + row);
    }

    /**
      * Sets the scale component of the current matrix by factoring out the
      * current scale (by doing an SVD) from the rotational component and
      * multiplying by the new scale.
      * @param scale the new scale amount
      */
    public final void setScale(float scale) {
	SVD(null, this);
	mulRotationScale(scale);
    }


    /**
      * Performs an SVD normalization of this matrix in order to acquire the
      * normalized rotational component; the values are placed into the Matrix3d parameter.
      * @param m1 matrix into which the rotational component is placed
      */
    public final void get(Matrix3d m1) {
	SVD(m1);
    }

    /**
      * Performs an SVD normalization of this matrix in order to acquire the
      * normalized rotational component; the values are placed into the Matrix3f parameter.
      * @param m1 matrix into which the rotational component is placed
      */
    public final void get(Matrix3f m1) {
	SVD(m1, null);
    }

    /**
      * Performs an SVD normalization of this matrix to calculate the rotation
      * as a 3x3 matrix, the translation, and the scale. None of the matrix values are modified.
      * @param m1 The normalized matrix representing the rotation
      * @param t1 The translation component
      * @return The scale component of this transform
      */
    public final float get(Matrix3f m1, Vector3f t1) {
	get(t1);
	return SVD(m1, null);
    }

    /**
      * Performs an SVD normalization of this matrix in order to acquire the
      * normalized rotational component; the values are placed into
      * the Quat4f parameter.
      * @param q1 quaternion into which the rotation component is placed
      */
    public final void get(Quat4f q1) {
	q1.set(this);
	q1.normalize();
    }

    /**
      * Retrieves the translational components of this matrix.
      * @param trans the vector that will receive the translational component
      */
    public final void get(Vector3f trans) {
	trans.x = m03;
	trans.y = m13;
	trans.z = m23;
    }

    /**
      * Gets the upper 3x3 values of this matrix and places them into the matrix m1.
      * @param m1 The matrix that will hold the values
      */
    public final void getRotationScale(Matrix3f m1) {
	m1.m00 = m00; m1.m01 = m01; m1.m02 = m02;
	m1.m10 = m10; m1.m11 = m11; m1.m12 = m12;
	m1.m20 = m20; m1.m21 = m21; m1.m22 = m22;
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
      * Replaces the upper 3x3 matrix values of this matrix with the values in the matrix m1.
      * @param m1 The matrix that will be the new upper 3x3
      */
    public final void setRotationScale(Matrix3f m1) {
	m00 = m1.m00; m01 = m1.m01; m02 = m1.m02;
	m10 = m1.m10; m11 = m1.m11; m12 = m1.m12;
	m20 = m1.m20; m21 = m1.m21; m22 = m1.m22;
    }

    /**
     * Sets the specified row of this matrix4f to the four values provided.
     * @param row  the row number to be modified (zero indexed)
     * @param x the first column element
     * @param y the second column element
     * @param z the third column element
     * @param w the fourth column element
     */
    public final void setRow(int row, float x, float y, float z, float w) {
	if (row == 0) {
	    m00 = x;
	    m01 = y;
	    m02 = z;
	    m03 = w;
	} else if (row == 1) {
	    m10 = x;
	    m11 = y;
	    m12 = z;
	    m13 = w;
	} else if (row == 2) {
	    m20 = x;
	    m21 = y;
	    m22 = z;
	    m23 = w;
	} else if (row == 3) {
	    m30 = x;
	    m31 = y;
	    m32 = z;
	    m33 = w;
	} else {
	    throw new ArrayIndexOutOfBoundsException("row must be 0 to 3 and is " + row);
	}
    }

    /**
     * Sets the specified row of this matrix4f to the Vector provided.
     * @param row the row number to be modified (zero indexed)
     * @param v the replacement row
     */
    public final void setRow(int row, Vector4f v) {
	if (row == 0) {
	    m00 = v.x;
	    m01 = v.y;
	    m02 = v.z;
	    m03 = v.w;
	} else if (row == 1) {
	    m10 = v.x;
	    m11 = v.y;
	    m12 = v.z;
	    m13 = v.w;
	} else if (row == 2) {
	    m20 = v.x;
	    m21 = v.y;
	    m22 = v.z;
	    m23 = v.w;
	} else if (row == 3) {
	    m30 = v.x;
	    m31 = v.y;
	    m32 = v.z;
	    m33 = v.w;
	} else {
	    throw new ArrayIndexOutOfBoundsException("row must be 0 to 3 and is " + row);
	}
    }

    /**
      * Sets the specified row of this matrix4f to the four values provided.
      * @param row the row number to be modified (zero indexed)
      * @param v the replacement row
      */
    public final void setRow(int row, float v[]) {
	if (row == 0) {
	    m00 = v[0];
	    m01 = v[1];
	    m02 = v[2];
	    m03 = v[3];
	} else if (row == 1) {
	    m10 = v[0];
	    m11 = v[1];
	    m12 = v[2];
	    m13 = v[3];
	} else if (row == 2) {
	    m20 = v[0];
	    m21 = v[1];
	    m22 = v[2];
	    m23 = v[3];
	} else if (row == 3) {
	    m30 = v[0];
	    m31 = v[1];
	    m32 = v[2];
	    m33 = v[3];
	} else {
	    throw new ArrayIndexOutOfBoundsException("row must be 0 to 3 and is " + row);
	}
    }

    /**
     * Copies the matrix values in the specified row into the
     * vector parameter.
     * @param row the matrix row
     * @param v The vector into which the matrix row values will be copied
     */
    public final void getRow(int row, Vector4f v) {
	if (row == 0) {
	    v.x = m00;
	    v.y = m01;
	    v.z = m02;
	    v.w = m03;
	} else if (row == 1) {
	    v.x = m10;
	    v.y = m11;
	    v.z = m12;
	    v.w = m13;
	} else if (row == 2) {
	    v.x = m20;
	    v.y = m21;
	    v.z = m22;
	    v.w = m23;
	} else if (row == 3) {
	    v.x = m30;
	    v.y = m31;
	    v.z = m32;
	    v.w = m33;
	} else {
	    throw new ArrayIndexOutOfBoundsException("row must be 0 to 3 and is " + row);
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
	    v[3] = m03;
	} else if (row == 1) {
	    v[0] = m10;
	    v[1] = m11;
	    v[2] = m12;
	    v[3] = m13;
	} else if (row == 2) {
	    v[0] = m20;
	    v[1] = m21;
	    v[2] = m22;
	    v[3] = m23;
	} else if (row == 3) {
	    v[0] = m30;
	    v[1] = m31;
	    v[2] = m32;
	    v[3] = m33;
	} else {
	    throw new ArrayIndexOutOfBoundsException("row must be 0 to 3 and is " + row);
	}
    }

    /**
      * Sets the specified column of this matrix4f to the four values provided.
      * @param  column the column number to be modified (zero indexed)
      * @param x the first row element
      * @param y the second row element
      * @param z the third row element
      * @param w the fourth row element
      */
    public final void setColumn(int column, float x, float y, float z, float w) {
	if (column == 0) {
	    m00 = x;
	    m10 = y;
	    m20 = z;
	    m30 = w;
	}  else if (column == 1) {
	    m01 = x;
	    m11 = y;
	    m21 = z;
	    m31 = w;
	} else if (column == 2) {
	    m02 = x;
	    m12 = y;
	    m22 = z;
	    m32 = w;
	} else if (column == 3) {
	    m03 = x;
	    m13 = y;
	    m23 = z;
	    m33 = w;
	} else {
	    throw new ArrayIndexOutOfBoundsException("column must be 0 to 3 and is " + column);
	}
    }

    /**
      * Sets the specified column of this matrix4f to the vector provided.
      * @param column the column number to be modified (zero indexed)
      * @param v the replacement column
      */
    public final void setColumn(int column, Vector4f v) {
	if (column == 0) {
	    m00 = v.x;
	    m10 = v.y;
	    m20 = v.z;
	    m30 = v.w;
	} else if (column == 1) {
	    m01 = v.x;
	    m11 = v.y;
	    m21 = v.z;
	    m31 = v.w;
	} else if (column == 2) {
	    m02 = v.x;
	    m12 = v.y;
	    m22 = v.z;
	    m32 = v.w;
	} else if (column == 3) {
	    m03 = v.x;
	    m13 = v.y;
	    m23 = v.z;
	    m33 = v.w;
	} else {
	    throw new ArrayIndexOutOfBoundsException("column must be 0 to 3 and is " + column);
	}
    }

    /**
      * Sets the specified column of this matrix4f to the four values provided. 
      * @param column  the column number to be modified (zero indexed) 
      * @param v       the replacement column 
      */
    public final void setColumn(int column,  float v[]) {
	if (column == 0) {
	    m00 = v[0];
	    m10 = v[1];
	    m20 = v[2];
	    m30 = v[3];
	} else if (column == 1) {
	    m01 = v[0];
	    m11 = v[1];
	    m21 = v[2];
	    m31 = v[3];
	} else if (column == 2) {
	    m02 = v[0];
	    m12 = v[1];
	    m22 = v[2];
	    m32 = v[3];
	} else if (column == 3) {
	    m03 = v[0];
	    m13 = v[1];
	    m23 = v[2];
	    m33 = v[3];
	} else {
	    throw new ArrayIndexOutOfBoundsException("column must be 0 to 3 and is " + column);
	}
    }

    /**
     * Copies the matrix values in the specified column into the
     * vector parameter.
     * @param column the matrix column
     * @param v The vector into which the matrix column values will be copied
     */
    public final void getColumn(int column, Vector4f v) {
	if (column == 0) {
	    v.x = m00;
	    v.y = m10;
	    v.z = m20;
	    v.w = m30;
	} else if (column == 1) {
	    v.x = m01;
	    v.y = m11;
	    v.z = m21;
	    v.w = m31;
	} else if (column == 2) {
	    v.x = m02;
	    v.y = m12;
	    v.z = m22;
	    v.w = m32;
	} else if (column == 3) {
	    v.x = m03;
	    v.y = m13;
	    v.z = m23;
	    v.w = m33;
	} else {
	    throw new ArrayIndexOutOfBoundsException("column must be 0 to 3 and is " + column);
	}
    }

    /**
      * Copies the matrix values in the specified column into the
      * array parameter.
      * @param column the matrix column
      * @param v The array into which the matrix column values will be copied
      */
    public final void getColumn(int column, float v[]) {
	if (column == 0) {
	    v[0] = m00;
	    v[1] = m10;
	    v[2] = m20;
	    v[3] = m30;
	} else if (column == 1) {
	    v[0] = m01;
	    v[1] = m11;
	    v[2] = m21;
	    v[3] = m31;
	} else if (column == 2) {
	    v[0] = m02;
	    v[1] = m12;
	    v[2] = m22;
	    v[3] = m32;
	} else if (column == 3) {
	    v[0] = m03;
	    v[1] = m13;
	    v[2] = m23;
	    v[3] = m33;
	} else {
	    throw new ArrayIndexOutOfBoundsException("column must be 0 to 3 and is " + column);
	}
    }

    /**
      * Adds a scalar to each component of this matrix.
      * @param scalar The scalar adder.
      */
    public final void add(float scalar) {
	m00 += scalar; m01 += scalar; m02 += scalar; m03 += scalar;
	m10 += scalar; m11 += scalar; m12 += scalar; m13 += scalar;
	m20 += scalar; m21 += scalar; m22 += scalar; m23 += scalar;
	m30 += scalar; m31 += scalar; m32 += scalar; m33 += scalar;
    }

    /**
      * Adds a scalar to each component of the matrix m1 and places
      * the result into this. Matrix m1 is not modified.
      * @param scalar The scalar adder.
      * @parm m1 The original matrix values.
      */
    public final void add(float scalar, Matrix4f m1) {
	set(m1);
	add(scalar);
    }


    /**
     * Sets the value of this matrix to the matrix sum of matrices m1 and m2. 
     * @param m1 the first matrix 
     * @param m2 the second matrix 
     */
    public final void add(Matrix4f m1, Matrix4f m2) {
	set(m1);
	add(m2);
    }

    /**
     * Sets the value of this matrix to sum of itself and matrix m1. 
     * @param m1 the other matrix 
     */
    public final void add(Matrix4f m1) {
	m00 += m1.m00; m01 += m1.m01; m02 += m1.m02; m03 += m1.m03;
	m10 += m1.m10; m11 += m1.m11; m12 += m1.m12; m13 += m1.m13;
	m20 += m1.m20; m21 += m1.m21; m22 += m1.m22; m23 += m1.m23;
	m30 += m1.m30; m31 += m1.m31; m32 += m1.m32; m33 += m1.m33;
    }

    /**
      * Sets the value of this matrix to the matrix difference
      * of matrices m1 and m2. 
      * @param m1 the first matrix 
      * @param m2 the second matrix 
      */
    public final void sub(Matrix4f m1, Matrix4f m2) {
	// note this is alias safe.
	set(
	    m1.m00 - m2.m00,
	    m1.m01 - m2.m01,
	    m1.m02 - m2.m02,
	    m1.m03 - m2.m03,
	    m1.m10 - m2.m10,
	    m1.m11 - m2.m11,
	    m1.m12 - m2.m12,
	    m1.m13 - m2.m13,
	    m1.m20 - m2.m20,
	    m1.m21 - m2.m21,
	    m1.m22 - m2.m22,
	    m1.m23 - m2.m23,
	    m1.m30 - m2.m30,
	    m1.m31 - m2.m31,
	    m1.m32 - m2.m32,
	    m1.m33 - m2.m33
	    );
    }

    /**
     * Sets the value of this matrix to the matrix difference of itself
     * and matrix m1 (this = this - m1). 
     * @param m1 the other matrix 
     */
    public final void sub(Matrix4f m1) {
	m00 -= m1.m00; m01 -= m1.m01; m02 -= m1.m02; m03 -= m1.m03;
	m10 -= m1.m10; m11 -= m1.m11; m12 -= m1.m12; m13 -= m1.m13;
	m20 -= m1.m20; m21 -= m1.m21; m22 -= m1.m22; m23 -= m1.m23;
	m30 -= m1.m30; m31 -= m1.m31; m32 -= m1.m32; m33 -= m1.m33;
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

	tmp = m03;
	m03 = m30;
	m30 = tmp;

	tmp = m12;
	m12 = m21;
	m21 = tmp;

	tmp = m13;
	m13 = m31;
	m31 = tmp;

	tmp = m23;
	m23 = m32;
	m32 = tmp;
    }

    /**
     * Sets the value of this matrix to the transpose of the argument matrix
     * @param m1 the matrix to be transposed 
     */
    public final void transpose(Matrix4f m1) {
	// alias-safe
	set(m1);
	transpose();
    }

    /**
     * Sets the value of this matrix to the matrix conversion of the
     * single precision quaternion argument. 
     * @param q1 the quaternion to be converted 
     */
    public final void set(Quat4f q1)  {
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
      * (double precision) quaternion argument. 
      * @param q1 the quaternion to be converted 
      */
    public final void set(Quat4d q1) {
	setFromQuat(q1.x, q1.y, q1.z, q1.w);
    }

    /**
      * Sets the value of this matrix to the matrix conversion of the
      * single precision axis and angle argument. 
      * @param a1 the axis and angle to be converted 
      */
    public final void set(AxisAngle4d a1) {
	setFromAxisAngle(a1.x, a1.y, a1.z, a1.angle);
    }

  /**
    * Sets the value of this matrix from the rotation expressed by the
    * quaternion q1, the translation t1, and the scale s.
    * @param q1  the rotation expressed as a quaternion
    * @param t1  the translation
    * @param s  the scale value
    */
    public final void set(Quat4d q1, Vector3d t1, double s) {
	set(q1);
	mulRotationScale((float)s);
	m03 = (float)t1.x;
	m13 = (float)t1.y;
	m23 = (float)t1.z;
    }

  /**
    * Sets the value of this matrix from the rotation expressed by the
    * quaternion q1, the translation t1, and the scale s.
    * @param q1  the rotation expressed as a quaternion
    * @param t1  the translation
    * @param s  the scale value
    */
    public final void set(Quat4f q1, Vector3f t1, float s) {
	set(q1);
	mulRotationScale(s);
	m03 = t1.x;
	m13 = t1.y;
	m23 = t1.z;
    }

    /**
      * Sets the value of this matrix to a copy of the
      * passed matrix m1.
      * @param m1 the matrix to be copied
      */
    public final void set(Matrix4d m1) {
	m00 = (float)m1.m00; m01 = (float)m1.m01; m02 = (float)m1.m02; m03 = (float)m1.m03;
	m10 = (float)m1.m10; m11 = (float)m1.m11; m12 = (float)m1.m12; m13 = (float)m1.m13;
	m20 = (float)m1.m20; m21 = (float)m1.m21; m22 = (float)m1.m22; m23 = (float)m1.m23;
	m30 = (float)m1.m30; m31 = (float)m1.m31; m32 = (float)m1.m32; m33 = (float)m1.m33;
    }

    /**
      * Sets the value of this matrix to a copy of the
      * passed matrix m1.
      * @param m1 the matrix to be copied
      */
    public final void set(Matrix4f m1) {
	m00 = m1.m00; m01 = m1.m01; m02 = m1.m02; m03 = m1.m03;
	m10 = m1.m10; m11 = m1.m11; m12 = m1.m12; m13 = m1.m13;
	m20 = m1.m20; m21 = m1.m21; m22 = m1.m22; m23 = m1.m23;
	m30 = m1.m30; m31 = m1.m31; m32 = m1.m32; m33 = m1.m33;
    }



    /**
     * Sets the value of this matrix to the matrix inverse
     * of the passed matrix m1. 
     * @param m1 the matrix to be inverted 
     */
    public final void invert(Matrix4f m1)  {
	set(m1);
	invert();
    }

    /**
     * Sets the value of this matrix to its inverse.
     */
    public final void invert() {
	float s = determinant();
	if (s == 0.0)
	    return;
	s = 1/s;
	// alias-safe way.
	// less *,+,- calculation than expanded expression.
	set(
	    m11*(m22*m33 - m23*m32) + m12*(m23*m31 - m21*m33) + m13*(m21*m32 - m22*m31),
	    m21*(m02*m33 - m03*m32) + m22*(m03*m31 - m01*m33) + m23*(m01*m32 - m02*m31),
	    m31*(m02*m13 - m03*m12) + m32*(m03*m11 - m01*m13) + m33*(m01*m12 - m02*m11),
	    m01*(m13*m22 - m12*m23) + m02*(m11*m23 - m13*m21) + m03*(m12*m21 - m11*m22),

	    m12*(m20*m33 - m23*m30) + m13*(m22*m30 - m20*m32) + m10*(m23*m32 - m22*m33),
	    m22*(m00*m33 - m03*m30) + m23*(m02*m30 - m00*m32) + m20*(m03*m32 - m02*m33),
	    m32*(m00*m13 - m03*m10) + m33*(m02*m10 - m00*m12) + m30*(m03*m12 - m02*m13),
	    m02*(m13*m20 - m10*m23) + m03*(m10*m22 - m12*m20) + m00*(m12*m23 - m13*m22),

	    m13*(m20*m31 - m21*m30) + m10*(m21*m33 - m23*m31) + m11*(m23*m30 - m20*m33),
	    m23*(m00*m31 - m01*m30) + m20*(m01*m33 - m03*m31) + m21*(m03*m30 - m00*m33),
	    m33*(m00*m11 - m01*m10) + m30*(m01*m13 - m03*m11) + m31*(m03*m10 - m00*m13),
	    m03*(m11*m20 - m10*m21) + m00*(m13*m21 - m11*m23) + m01*(m10*m23 - m13*m20),

	    m10*(m22*m31 - m21*m32) + m11*(m20*m32 - m22*m30) + m12*(m21*m30 - m20*m31),
	    m20*(m02*m31 - m01*m32) + m21*(m00*m32 - m02*m30) + m22*(m01*m30 - m00*m31),
	    m30*(m02*m11 - m01*m12) + m31*(m00*m12 - m02*m10) + m32*(m01*m10 - m00*m11),
	    m00*(m11*m22 - m12*m21) + m01*(m12*m20 - m10*m22) + m02*(m10*m21 - m11*m20)
	    );

	mul(s);
    }

    /**
     * Computes the determinant of this matrix. 
     * @return the determinant of the matrix 
     */
    public final float determinant()  {
	// less *,+,- calculation than expanded expression.
	return
	    (m00*m11 - m01*m10)*(m22*m33 - m23*m32)
	   -(m00*m12 - m02*m10)*(m21*m33 - m23*m31)
	   +(m00*m13 - m03*m10)*(m21*m32 - m22*m31)
	   +(m01*m12 - m02*m11)*(m20*m33 - m23*m30)
	   -(m01*m13 - m03*m11)*(m20*m32 - m22*m30)
	   +(m02*m13 - m03*m12)*(m20*m31 - m21*m30);

    }


    /**
      * Sets the rotational component (upper 3x3) of this matrix to the matrix
      * values in the single precision Matrix3f argument; the other elements of
      * this matrix are initialized as if this were an identity matrix
      * (ie, affine matrix with no translational component).
      * @param m1 the 3x3 matrix
      */
    public final void set(Matrix3f m1)  {
	m00 = m1.m00; m01 = m1.m01; m02 = m1.m02; m03 = 0.0f;
	m10 = m1.m10; m11 = m1.m11; m12 = m1.m12; m13 = 0.0f;
	m20 = m1.m20; m21 = m1.m21; m22 = m1.m22; m23 = 0.0f;
	m30 =   0.0f; m31 =   0.0f; m32 =   0.0f; m33 = 1.0f;
    }

    /**
      * Sets the rotational component (upper 3x3) of this matrix to the matrix
      * values in the double precision Matrix3d argument; the other elements of
      * this matrix are initialized as if this were an identity matrix
      * (ie, affine matrix with no translational component).
      * @param m1 the 3x3 matrix
      */
    public final void set(Matrix3d m1)  {
	m00 = (float)m1.m00; m01 = (float)m1.m01; m02 = (float)m1.m02; m03 = 0.0f;
	m10 = (float)m1.m10; m11 = (float)m1.m11; m12 = (float)m1.m12; m13 = 0.0f;
	m20 = (float)m1.m20; m21 = (float)m1.m21; m22 = (float)m1.m22; m23 = 0.0f;
	m30 =    0.0f;       m31 =    0.0f;       m32 =    0.0f;       m33 = 1.0f;
    }

    /**
     * Sets the value of this matrix to a scale matrix with the
     * passed scale amount. 
     * @param scale the scale factor for the matrix 
     */
    public final void set(float scale)  {
	m00 = scale; m01 = 0.0f;  m02 = 0.0f;  m03 = 0.0f;
	m10 = 0.0f;  m11 = scale; m12 = 0.0f;  m13 = 0.0f;
	m20 = 0.0f;  m21 = 0.0f;  m22 = scale; m23 = 0.0f;
	m30 = 0.0f;  m31 = 0.0f;  m32 = 0.0f;  m33 = 1.0f;
    }


    /**
      * Sets the values in this Matrix4f equal to the row-major array parameter
      * (ie, the first four elements of the array will be copied into the first
      * row of this matrix, etc.).
      */
    public final void set(float m[]) {
	m00 = m[ 0]; m01 = m[ 1]; m02 = m[ 2]; m03 = m[ 3];
	m10 = m[ 4]; m11 = m[ 5]; m12 = m[ 6]; m13 = m[ 7];
	m20 = m[ 8]; m21 = m[ 9]; m22 = m[10]; m23 = m[11];
	m30 = m[12]; m31 = m[13]; m32 = m[14]; m33 = m[15];
    }

    /**
     * Sets the value of this matrix to a translate matrix by the
     * passed translation value.
     * @param v1 the translation amount
     */
    public final void set(Vector3f v1) {
	setIdentity();
	setTranslation(v1);
    }

    /**
     * Sets the value of this matrix to a scale and translation matrix;
     * scale is not applied to the translation and all of the matrix
     * values are modified.
     * @param scale the scale factor for the matrix
     * @param v1 the translation amount
     */
    public final void set(float scale, Vector3f v1) {
	set(scale);
	setTranslation(v1);
    }

    /**
     * Sets the value of this matrix to a scale and translation matrix;
     * the translation is scaled by the scale factor and all of the
     * matrix values are modified.
     * @param v1 the translation amount
     * @param scale the scale factor for the matrix
     */
    public final void set(Vector3f v1, float scale) {
	m00 = scale; m01 = 0.0f;  m02 = 0.0f;  m03 = scale*v1.x;
	m10 = 0.0f;  m11 = scale; m12 = 0.0f;  m13 = scale*v1.y;
	m20 = 0.0f;  m21 = 0.0f;  m22 = scale; m23 = scale*v1.z;
	m30 = 0.0f;  m31 = 0.0f;  m32 = 0.0f;  m33 = 1.0f;
    }


    /**
      * Sets the value of this matrix from the rotation expressed by the
      * rotation matrix m1, the translation t1, and the scale s. The translation
      * is not modified by the scale.
      * @param m1 The rotation component
      * @param t1 The translation component
      * @param scale The scale component
      */
    public final void set(Matrix3f m1, Vector3f t1, float scale) {
	setRotationScale(m1);
	mulRotationScale(scale);
	setTranslation(t1);
	m33 = 1.0f;
    }

    /**
      * Sets the value of this matrix from the rotation expressed by the
      * rotation matrix m1, the translation t1, and the scale s. The translation
      * is not modified by the scale.
      * @param m1 The rotation component
      * @param t1 The translation component
      * @param scale The scale component
      */
    public final void set(Matrix3d m1, Vector3d t1, double scale) {
	setRotationScale(m1);
	mulRotationScale((float)scale);
	setTranslation(t1);
	m33 = 1.0f;
    }

    /**
      * Modifies the translational components of this matrix to the values of
      * the Vector3f argument; the other values of this matrix are not modified.
      * @param trans the translational component
      */
    public void setTranslation(Vector3f trans) {
	m03 = trans.x;
        m13 = trans.y;  
	m23 = trans.z;
    }

    /**
     * Sets the value of this matrix to a rotation matrix about the x axis
     * by the passed angle. 
     * @param angle the angle to rotate about the X axis in radians 
     */
    public final void rotX(float angle)  {
	float c = (float)Math.cos(angle);
	float s = (float)Math.sin(angle);
	m00 = 1.0f; m01 = 0.0f; m02 = 0.0f; m03 = 0.0f;
	m10 = 0.0f; m11 = c;    m12 = -s;   m13 = 0.0f;
	m20 = 0.0f; m21 = s;    m22 = c;    m23 = 0.0f;
	m30 = 0.0f; m31 = 0.0f; m32 = 0.0f; m33 = 1.0f; 
    }

    /**
     * Sets the value of this matrix to a rotation matrix about the y axis
     * by the passed angle. 
     * @param angle the angle to rotate about the Y axis in radians 
     */
    public final void rotY(float angle)  {
	float c = (float)Math.cos(angle);
	float s = (float)Math.sin(angle);
	m00 = c;    m01 = 0.0f; m02 = s;    m03 = 0.0f;
	m10 = 0.0f; m11 = 1.0f; m12 = 0.0f; m13 = 0.0f;
	m20 = -s;   m21 = 0.0f; m22 = c;    m23 = 0.0f;
	m30 = 0.0f; m31 = 0.0f; m32 = 0.0f; m33 = 1.0f; 
    }

    /**
     * Sets the value of this matrix to a rotation matrix about the z axis
     * by the passed angle. 
     * @param angle the angle to rotate about the Z axis in radians 
     */
    public final void rotZ(float angle)  {
	float c = (float)Math.cos(angle);
	float s = (float)Math.sin(angle);
	m00 = c;    m01 = -s;   m02 = 0.0f; m03 = 0.0f;
	m10 = s;    m11 = c;    m12 = 0.0f; m13 = 0.0f;
	m20 = 0.0f; m21 = 0.0f; m22 = 1.0f; m23 = 0.0f;
	m30 = 0.0f; m31 = 0.0f; m32 = 0.0f; m33 = 1.0f; 
    }

    /**
      * Multiplies each element of this matrix by a scalar.
      * @param scalar The scalar multiplier.
      */
     public final void mul(float scalar) {
	m00 *= scalar; m01 *= scalar;  m02 *= scalar; m03 *= scalar;
	m10 *= scalar; m11 *= scalar;  m12 *= scalar; m13 *= scalar;
	m20 *= scalar; m21 *= scalar;  m22 *= scalar; m23 *= scalar;
	m30 *= scalar; m31 *= scalar;  m32 *= scalar; m33 *= scalar;
     }

    /**
      * Multiplies each element of matrix m1 by a scalar and places the result
      * into this. Matrix m1 is not modified.
      * @param scalar The scalar multiplier.
      * @param m1 The original matrix.
      */
     public final void mul(float scalar, Matrix4f m1) {
	 set(m1);
	 mul(scalar);
     }

    /**
     * Sets the value of this matrix to the result of multiplying itself
     * with matrix m1. 
     * @param m1 the other matrix 
     */
    public final void mul(Matrix4f m1) {
	mul(this, m1);
    }

    /**
     * Sets the value of this matrix to the result of multiplying
     * the two argument matrices together. 
     * @param m1 the first matrix 
     * @param m2 the second matrix 
     */
    public final void mul(Matrix4f m1, Matrix4f m2) {
	// alias-safe way.
	set(
	    m1.m00*m2.m00 + m1.m01*m2.m10 + m1.m02*m2.m20 + m1.m03*m2.m30,
	    m1.m00*m2.m01 + m1.m01*m2.m11 + m1.m02*m2.m21 + m1.m03*m2.m31,
	    m1.m00*m2.m02 + m1.m01*m2.m12 + m1.m02*m2.m22 + m1.m03*m2.m32,
	    m1.m00*m2.m03 + m1.m01*m2.m13 + m1.m02*m2.m23 + m1.m03*m2.m33,

	    m1.m10*m2.m00 + m1.m11*m2.m10 + m1.m12*m2.m20 + m1.m13*m2.m30,
	    m1.m10*m2.m01 + m1.m11*m2.m11 + m1.m12*m2.m21 + m1.m13*m2.m31,
	    m1.m10*m2.m02 + m1.m11*m2.m12 + m1.m12*m2.m22 + m1.m13*m2.m32,
	    m1.m10*m2.m03 + m1.m11*m2.m13 + m1.m12*m2.m23 + m1.m13*m2.m33,

	    m1.m20*m2.m00 + m1.m21*m2.m10 + m1.m22*m2.m20 + m1.m23*m2.m30,
	    m1.m20*m2.m01 + m1.m21*m2.m11 + m1.m22*m2.m21 + m1.m23*m2.m31,
	    m1.m20*m2.m02 + m1.m21*m2.m12 + m1.m22*m2.m22 + m1.m23*m2.m32,
	    m1.m20*m2.m03 + m1.m21*m2.m13 + m1.m22*m2.m23 + m1.m23*m2.m33,

	    m1.m30*m2.m00 + m1.m31*m2.m10 + m1.m32*m2.m20 + m1.m33*m2.m30,
	    m1.m30*m2.m01 + m1.m31*m2.m11 + m1.m32*m2.m21 + m1.m33*m2.m31,
	    m1.m30*m2.m02 + m1.m31*m2.m12 + m1.m32*m2.m22 + m1.m33*m2.m32,
	    m1.m30*m2.m03 + m1.m31*m2.m13 + m1.m32*m2.m23 + m1.m33*m2.m33
	    );
  }

    /**
      * Multiplies the transpose of matrix m1 times the transpose of matrix m2,
      * and places the result into this.
      * @param m1 The matrix on the left hand side of the multiplication
      * @param m2 The matrix on the right hand side of the multiplication
      */
    public final void mulTransposeBoth(Matrix4f m1, Matrix4f m2) {
	mul(m2, m1);
	transpose();
    }

    /**
      * Multiplies matrix m1 times the transpose of matrix m2, and places the
      * result into this.
      * @param m1 The matrix on the left hand side of the multiplication
      * @param m2 The matrix on the right hand side of the multiplication
      */
    public final void mulTransposeRight(Matrix4f m1, Matrix4f m2) {
	// alias-safe way.
	set(
	    m1.m00*m2.m00 + m1.m01*m2.m01 + m1.m02*m2.m02 + m1.m03*m2.m03,
	    m1.m00*m2.m10 + m1.m01*m2.m11 + m1.m02*m2.m12 + m1.m03*m2.m13,
	    m1.m00*m2.m20 + m1.m01*m2.m21 + m1.m02*m2.m22 + m1.m03*m2.m23,
	    m1.m00*m2.m30 + m1.m01*m2.m31 + m1.m02*m2.m32 + m1.m03*m2.m33,

	    m1.m10*m2.m00 + m1.m11*m2.m01 + m1.m12*m2.m02 + m1.m13*m2.m03,
	    m1.m10*m2.m10 + m1.m11*m2.m11 + m1.m12*m2.m12 + m1.m13*m2.m13,
	    m1.m10*m2.m20 + m1.m11*m2.m21 + m1.m12*m2.m22 + m1.m13*m2.m23,
	    m1.m10*m2.m30 + m1.m11*m2.m31 + m1.m12*m2.m32 + m1.m13*m2.m33,

	    m1.m20*m2.m00 + m1.m21*m2.m01 + m1.m22*m2.m02 + m1.m23*m2.m03,
	    m1.m20*m2.m10 + m1.m21*m2.m11 + m1.m22*m2.m12 + m1.m23*m2.m13,
	    m1.m20*m2.m20 + m1.m21*m2.m21 + m1.m22*m2.m22 + m1.m23*m2.m23,
	    m1.m20*m2.m30 + m1.m21*m2.m31 + m1.m22*m2.m32 + m1.m23*m2.m33,
	    
	    m1.m30*m2.m00 + m1.m31*m2.m01 + m1.m32*m2.m02 + m1.m33*m2.m03,
	    m1.m30*m2.m10 + m1.m31*m2.m11 + m1.m32*m2.m12 + m1.m33*m2.m13,
	    m1.m30*m2.m20 + m1.m31*m2.m21 + m1.m32*m2.m22 + m1.m33*m2.m23,
	    m1.m30*m2.m30 + m1.m31*m2.m31 + m1.m32*m2.m32 + m1.m33*m2.m33
	    );
    }

    
    /**
      * Multiplies the transpose of matrix m1 times matrix m2, and places the
      * result into this.
      * @param m1 The matrix on the left hand side of the multiplication
      * @param m2 The matrix on the right hand side of the multiplication
      */
    public final void mulTransposeLeft(Matrix4f m1, Matrix4f m2) {
	// alias-safe way.
	set(
	    m1.m00*m2.m00 + m1.m10*m2.m10 + m1.m20*m2.m20 + m1.m30*m2.m30,
	    m1.m00*m2.m01 + m1.m10*m2.m11 + m1.m20*m2.m21 + m1.m30*m2.m31,
	    m1.m00*m2.m02 + m1.m10*m2.m12 + m1.m20*m2.m22 + m1.m30*m2.m32,
	    m1.m00*m2.m03 + m1.m10*m2.m13 + m1.m20*m2.m23 + m1.m30*m2.m33,

	    m1.m01*m2.m00 + m1.m11*m2.m10 + m1.m21*m2.m20 + m1.m31*m2.m30,
	    m1.m01*m2.m01 + m1.m11*m2.m11 + m1.m21*m2.m21 + m1.m31*m2.m31,
	    m1.m01*m2.m02 + m1.m11*m2.m12 + m1.m21*m2.m22 + m1.m31*m2.m32,
	    m1.m01*m2.m03 + m1.m11*m2.m13 + m1.m21*m2.m23 + m1.m31*m2.m33,

	    m1.m02*m2.m00 + m1.m12*m2.m10 + m1.m22*m2.m20 + m1.m32*m2.m30,
	    m1.m02*m2.m01 + m1.m12*m2.m11 + m1.m22*m2.m21 + m1.m32*m2.m31,
	    m1.m02*m2.m02 + m1.m12*m2.m12 + m1.m22*m2.m22 + m1.m32*m2.m32,
	    m1.m02*m2.m03 + m1.m12*m2.m13 + m1.m22*m2.m23 + m1.m32*m2.m33,

	    m1.m03*m2.m00 + m1.m13*m2.m10 + m1.m23*m2.m20 + m1.m33*m2.m30,
	    m1.m03*m2.m01 + m1.m13*m2.m11 + m1.m23*m2.m21 + m1.m33*m2.m31,
	    m1.m03*m2.m02 + m1.m13*m2.m12 + m1.m23*m2.m22 + m1.m33*m2.m32,
	    m1.m03*m2.m03 + m1.m13*m2.m13 + m1.m23*m2.m23 + m1.m33*m2.m33
	    );
    }


    /**
     * Returns true if all of the data members of Matrix4f m1 are
     * equal to the corresponding data members in this Matrix4f. 
     * @param m1 The matrix with which the comparison is made. 
     * @return true or false 
     */
    public boolean equals(Matrix4f m1)  {
	return  m1 != null
	        && m00 == m1.m00
		&& m01 == m1.m01
		&& m02 == m1.m02 
		&& m03 == m1.m03
		&& m10 == m1.m10
		&& m11 == m1.m11
		&& m12 == m1.m12
		&& m13 == m1.m13
		&& m20 == m1.m20
		&& m21 == m1.m21
		&& m22 == m1.m22
		&& m23 == m1.m23
		&& m30 == m1.m30
		&& m31 == m1.m31
		&& m32 == m1.m32
		&& m33 == m1.m33;
    }

    /**
      * Returns true if the Object o1 is of type Matrix4f and all of the data
      * members of t1 are equal to the corresponding data members in this
      * Matrix4f.
      * @param o1 the object with which the comparison is made.
      */
    public boolean equals(Object o1) {
	return o1 != null && (o1 instanceof Matrix4f) && equals((Matrix4f)o1);
    }

    /**
      * Returns true if the L-infinite distance between this matrix and matrix
      * m1 is less than or equal to the epsilon parameter, otherwise returns
      * false. The L-infinite distance is equal to MAX[i=0,1,2,3 ; j=0,1,2,3 ;
      * abs(this.m(i,j) - m1.m(i,j)]
      * @param m1 The matrix to be compared to this matrix
      * @param epsilon the threshold value
      */
      public boolean epsilonEquals(Matrix4f m1, float epsilon) {
	  // why epsilon is float ??
	  return  Math.abs(m00 - m1.m00) <= epsilon
		&& Math.abs(m01 - m1.m01) <= epsilon
		&& Math.abs(m02 - m1.m02 ) <= epsilon
		&& Math.abs(m03 - m1.m03) <= epsilon

		&& Math.abs(m10 - m1.m10) <= epsilon
		&& Math.abs(m11 - m1.m11) <= epsilon
		&& Math.abs(m12 - m1.m12) <= epsilon
		&& Math.abs(m13 - m1.m13) <= epsilon

		&& Math.abs(m20 - m1.m20) <= epsilon
		&& Math.abs(m21 - m1.m21) <= epsilon
		&& Math.abs(m22 - m1.m22) <= epsilon
		&& Math.abs(m23 - m1.m23) <= epsilon

		&& Math.abs(m30 - m1.m30) <= epsilon
		&& Math.abs(m31 - m1.m31) <= epsilon
		&& Math.abs(m32 - m1.m32) <= epsilon
		&& Math.abs(m33 - m1.m33) <= epsilon;
      }

    /**
     * Returns a hash number based on the data values in this
     * object.  Two different Matrix4f objects with identical data values
     * (ie, returns true for equals(Matrix4f) ) will return the same hash
     * number.  Two objects with different data members may return the
     * same hash value, although this is not likely.
     * @return the integer hash value 
     */
    public int hashCode() {
	return Float.floatToIntBits(m00) ^
	       Float.floatToIntBits(m01) ^
	       Float.floatToIntBits(m02) ^
	       Float.floatToIntBits(m03) ^
               Float.floatToIntBits(m10) ^
	       Float.floatToIntBits(m11) ^
	       Float.floatToIntBits(m12) ^
	       Float.floatToIntBits(m13) ^
	       Float.floatToIntBits(m20) ^
	       Float.floatToIntBits(m21) ^
	       Float.floatToIntBits(m22) ^
	       Float.floatToIntBits(m23) ^
	       Float.floatToIntBits(m30) ^
	       Float.floatToIntBits(m31) ^
	       Float.floatToIntBits(m32) ^
	       Float.floatToIntBits(m33);
    }

    /**
     * Transform the vector vec using this Matrix4f and place the
     * result into vecOut.
     * @param vec the single precision vector to be transformed
     * @param vecOut the vector into which the transformed values are placed
     */
    public final void transform(Tuple4f vec, Tuple4f vecOut) {
	// alias-safe
	vecOut.set(
	    m00*vec.x + m01*vec.y + m02*vec.z + m03*vec.w,
	    m10*vec.x + m11*vec.y + m12*vec.z + m13*vec.w,
	    m20*vec.x + m21*vec.y + m22*vec.z + m23*vec.w,
	    m30*vec.x + m31*vec.y + m32*vec.z + m33*vec.w
	    );
    }

    /**
     * Transform the vector vec using this Matrix4f and place the
     * result back into vec.
     * @param vec the single precision vector to be transformed
     */
    public final void transform(Tuple4f vec)  {
	transform(vec, vec);
    }

    /**
      * Transforms the point parameter with this Matrix4f and places the result
      * into pointOut. The fourth element of the point input paramter is assumed
      * to be one.
      * @param point the input point to be transformed.
      * @param pointOut the transformed point
      */
    public final void transform(Point3f point, Point3f pointOut) {
	pointOut.set(
	    m00*point.x + m01*point.y + m02*point.z + m03,
	    m10*point.x + m11*point.y + m12*point.z + m13,
	    m20*point.x + m21*point.y + m22*point.z + m23
	    );
    }


    /**
     * Transforms the point parameter with this Matrix4f and
     * places the result back into point.  The fourth element of the
     * point input paramter is assumed to be one.
     * @param point the input point to be transformed.
     */
    public final void transform(Point3f point) {
	transform(point, point);
    }

    /**
     * Transforms the normal parameter by this Matrix4f and places the value
     * into normalOut.  The fourth element of the normal is assumed to be zero.
     * @param normal the input normal to be transformed.
     * @param normalOut the transformed normal
     */
    public final void transform(Vector3f normal, Vector3f normalOut) {
	normalOut.set(
	    m00 * normal.x + m01 * normal.y + m02 * normal.z,
	    m10 * normal.x + m11 * normal.y + m12 * normal.z,
	    m20 * normal.x + m21 * normal.y + m22 * normal.z
	    );
    }

    /**
     * Transforms the normal parameter by this transform and places the value
     * back into normal.  The fourth element of the normal is assumed to be zero.
     * @param normal the input normal to be transformed.
     */
    public final void transform(Vector3f normal) {
	transform(normal, normal);
    }

    /**
      * Sets the rotational component (upper 3x3) of this matrix to the matrix
      * values in the single precision Matrix3f argument; the other elements of
      * this matrix are unchanged; a singular value decomposition is performed
      * on this object's upper 3x3 matrix to factor out the scale, then this
      * object's upper 3x3 matrix components are replaced by the passed rotation
      * components, and then the scale is reapplied to the rotational
      * components.
      * @param m1 single precision 3x3 matrix
      */
    public final void setRotation(Matrix3d m1) {
	float scale = SVD(null);
	setRotationScale(m1);
	mulRotationScale(scale);
    }

    /**
      * Sets the rotational component (upper 3x3) of this matrix to the matrix
      * values in the single precision Matrix3f argument; the other elements of
      * this matrix are unchanged; a singular value decomposition is performed
      * on this object's upper 3x3 matrix to factor out the scale, then this
      * object's upper 3x3 matrix components are replaced by the passed rotation
      * components, and then the scale is reapplied to the rotational
      * components.
      * @param m1 single precision 3x3 matrix
      */
    public final void setRotation(Matrix3f m1) {
	float scale = SVD(null);
	setRotationScale(m1);
	mulRotationScale(scale);
    }

    /**
      * Sets the rotational component (upper 3x3) of this matrix to the matrix
      * equivalent values of the quaternion argument; the other elements of this
      * matrix are unchanged; a singular value decomposition is performed on
      * this object's upper 3x3 matrix to factor out the scale, then this
      * object's upper 3x3 matrix components are replaced by the matrix
      * equivalent of the quaternion, and then the scale is reapplied to the
      * rotational components.
      * @param q1 the quaternion that specifies the rotation
      */
    public final void setRotation(Quat4f q1) {
	float scale = SVD(null, null);

	// save other values
	float tx = m03; 
	float ty = m13; 
	float tz = m23; 
	float w0 = m30;                  
	float w1 = m31;
	float w2 = m32;
	float w3 = m33;

	set(q1);
	mulRotationScale(scale);

	// set back
	m03 = tx;
	m13 = ty;
	m23 = tz;
	m30 = w0;
	m31 = w1;
	m32 = w2;
	m33 = w3;
    }

    /**
      * Sets the rotational component (upper 3x3) of this matrix to the matrix
      * equivalent values of the quaternion argument; the other elements of this
      * matrix are unchanged; a singular value decomposition is performed on
      * this object's upper 3x3 matrix to factor out the scale, then this
      * object's upper 3x3 matrix components are replaced by the matrix
      * equivalent of the quaternion, and then the scale is reapplied to the
      * rotational components.
      * @param q1 the quaternion that specifies the rotation
      */
    public final void setRotation(Quat4d q1) {
	float scale = SVD(null, null);
	// save other values
	float tx = m03; 
	float ty = m13; 
	float tz = m23; 
	float w0 = m30;                  
	float w1 = m31;
	float w2 = m32;
	float w3 = m33;

	set(q1);
	mulRotationScale(scale);

	// set back
	m03 = tx;
	m13 = ty;
	m23 = tz;
	m30 = w0;
	m31 = w1;
	m32 = w2;
	m33 = w3;
    }

    /**
      * Sets the rotational component (upper 3x3) of this matrix to the matrix
      * equivalent values of the axis-angle argument; the other elements of this
      * matrix are unchanged; a singular value decomposition is performed on
      * this object's upper 3x3 matrix to factor out the scale, then this
      * object's upper 3x3 matrix components are replaced by the matrix
      * equivalent of the axis-angle, and then the scale is reapplied to the
      * rotational components.
      * @param a1 the axis-angle to be converted (x, y, z, angle)
      */
    public final void setRotation(AxisAngle4f a1) {
	float scale = SVD(null, null);
	// save other values
	float tx = m03; 
	float ty = m13; 
	float tz = m23; 
	float w0 = m30;                  
	float w1 = m31;
	float w2 = m32;
	float w3 = m33;

	set(a1);
	mulRotationScale(scale);

	// set back
	m03 = tx;
	m13 = ty;
	m23 = tz;
	m30 = w0;
	m31 = w1;
	m32 = w2;
	m33 = w3;
    }

    /**
      * Sets this matrix to all zeros.
      */
    public final void setZero() {
        m00 = 0.0f; m01 = 0.0f; m02 = 0.0f; m03 = 0.0f;
        m10 = 0.0f; m11 = 0.0f; m12 = 0.0f; m13 = 0.0f;
        m20 = 0.0f; m21 = 0.0f; m22 = 0.0f; m23 = 0.0f;
        m30 = 0.0f; m31 = 0.0f; m32 = 0.0f; m33 = 0.0f;
    }

    /**
      * Negates the value of this matrix: this = -this.
      */
    public final void negate() {
        m00 = -m00; m01 = -m01; m02 = -m02; m03 = -m03;
        m10 = -m10; m11 = -m11; m12 = -m12; m13 = -m13;
        m20 = -m20; m21 = -m21; m22 = -m22; m23 = -m23;
        m30 = -m30; m31 = -m31; m32 = -m32; m33 = -m33;
    }

    /**
      * Sets the value of this matrix equal to the negation of of the Matrix4f
      * parameter.
      * @param m1 The source matrix
      */
    public final void negate(Matrix4f m1) {
	set(m1);
	negate();
    }

    /**
      * Sets 16 values	
      */
    private void set(float m00, float m01, float m02, float m03, 
		  float m10, float m11, float m12, float m13,
		  float m20, float m21, float m22, float m23,
		  float m30, float m31, float m32, float m33) {
	this.m00 = m00; this.m01 = m01; this.m02 = m02; this.m03 = m03;
	this.m10 = m10; this.m11 = m11; this.m12 = m12; this.m13 = m13;
	this.m20 = m20; this.m21 = m21; this.m22 = m22; this.m23 = m23;
	this.m30 = m30; this.m31 = m31; this.m32 = m32; this.m33 = m33;
    }

    /**
      * Performs SVD on this matrix and gets scale and rotation.
      * Rotation is placed into rot.
      * @param rot3 the rotation factor(Matrix3d).
      * @param rot4 the rotation factor(Matrix4f) only upper 3x3 elements are changed.
      * @return scale factor
      */
    private float SVD(Matrix3f rot3, Matrix4f rot4) {
	// this is a simple svd.
	// Not complete but fast and reasonable.
	// See comment in Matrix3d.

	float s = (float)Math.sqrt(
	    (
	     m00*m00 + m10*m10 + m20*m20 + 
	     m01*m01 + m11*m11 + m21*m21 +
	     m02*m02 + m12*m12 + m22*m22
	    )/3.0
	    );

	// zero-div may occur.
	float t = (s == 0.0f ? 0.0f : 1.0f/s);

	if (rot3 != null) {
	    this.getRotationScale(rot3);
	    rot3.mul(t);
	}

	if (rot4 != null) {
	    if (rot4 != this)
		rot4.setRotationScale(this);  // private method
	    rot4.mulRotationScale(t);         // private method
	}

	return s;
    }

    /**
      * Performs SVD on this matrix and gets the scale and the pure rotation.
      * The pure rotation is placed into rot.
      * @param rot the rotation factor.
      * @return scale factor
      */
    private float SVD(Matrix3d rot) {
	// this is a simple svd.
	// Not complete but fast and reasonable.
	// See comment in Matrix3d.

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
	    this.getRotationScale(rot);
	    rot.mul(t);
	}

	return s;
    }

    /**
      * Multiplies 3x3 upper elements of this matrix by a scalar.
      * The other elements are unchanged.
      */
    private void mulRotationScale(float scale) {
	m00 *= scale; m01 *= scale; m02 *= scale;
	m10 *= scale; m11 *= scale; m12 *= scale;
	m20 *= scale; m21 *= scale; m22 *= scale;
    }

    /**
      * Sets only 3x3 upper elements of this matrix to that of m1.
      * The other elements are unchanged.
      */
    private void setRotationScale(Matrix4f m1) {
	m00 = m1.m00; m01 = m1.m01; m02 = m1.m02;
	m10 = m1.m10; m11 = m1.m11; m12 = m1.m12;
	m20 = m1.m20; m21 = m1.m21; m22 = m1.m22;
    }

    /**
      * Replaces the upper 3x3 matrix values of this matrix with the values in the matrix m1.
      * @param m1 The matrix that will be the new upper 3x3
      */
    private void setRotationScale(Matrix3d m1) {
	m00 = (float)m1.m00; m01 = (float)m1.m01; m02 = (float)m1.m02;
	m10 = (float)m1.m10; m11 = (float)m1.m11; m12 = (float)m1.m12;
	m20 = (float)m1.m20; m21 = (float)m1.m21; m22 = (float)m1.m22;
    }

    /**
      * Modifies the translational components of this matrix to the values of
      * the Vector3d argument; the other values of this matrix are not modified.
      * @param trans the translational component
      */
    private void setTranslation(Vector3d trans) {
	m03 = (float)trans.x;
        m13 = (float)trans.y;  
	m23 = (float)trans.z;
    }


    /**
      * Gets the upper 3x3 values of this matrix and places them into the matrix m1.
      * @param m1 The matrix that will hold the values
      */
    private final void getRotationScale(Matrix3d m1) {
	m1.m00 = m00; m1.m01 = m01; m1.m02 = m02;
	m1.m10 = m10; m1.m11 = m11; m1.m12 = m12;
	m1.m20 = m20; m1.m21 = m21; m1.m22 = m22;
    }

    private void setFromQuat(double x, double y, double z, double w) {
	double n = x*x + y*y + z*z + w*w;
	double s = (n > 0.0) ? (2.0/n) : 0.0;

	double xs = x*s,  ys = y*s,  zs = z*s;
	double wx = w*xs, wy = w*ys, wz = w*zs;
	double xx = x*xs, xy = x*ys, xz = x*zs;
	double yy = y*ys, yz = y*zs, zz = z*zs;

	setIdentity();
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
