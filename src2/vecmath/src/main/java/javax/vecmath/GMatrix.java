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
 * A double precision, general, dynamically-resizable,
 * two-dimensional matrix class.  Row and column numbering begins with
 * zero.  The representation is row major.
 */

public class GMatrix implements java.io.Serializable, Cloneable {

    // Compatible with 1.1
    static final long serialVersionUID = 2777097312029690941L;
    private static final boolean debug = false;

    int nRow;
    int nCol;

    // double dereference is slow
    double[][] values;

    private static final double EPS = 1.0E-10;

    /**
     * Constructs an nRow by NCol identity matrix.
     * Note that because row and column numbering begins with
     * zero, nRow and nCol will be one larger than the maximum
     * possible matrix index values.
     * @param nRow  number of rows in this matrix.
     * @param nCol  number of columns in this matrix.
     */
    public GMatrix(int nRow, int nCol)
    {
        values = new double[nRow][nCol];
	this.nRow = nRow;
	this.nCol = nCol;

	int i, j;
	for (i = 0; i < nRow; i++) {
	    for (j = 0; j < nCol; j++) {
		values[i][j] = 0.0;
	    }
	}

	int l;
	if (nRow < nCol)
	    l = nRow;
        else
	    l = nCol;

	for (i = 0; i < l; i++) {
	    values[i][i] = 1.0;
	}
    }

    /**
     * Constructs an nRow by nCol matrix initialized to the values
     * in the matrix array.  The array values are copied in one row at
     * a time in row major fashion.  The array should be at least
     * nRow*nCol in length.
     * Note that because row and column numbering begins with
     * zero, nRow and nCol will be one larger than the maximum
     * possible matrix index values.
     * @param nRow  number of rows in this matrix.
     * @param nCol  number of columns in this matrix.
     * @param matrix  a 1D array that specifies a matrix in row major fashion
     */
    public GMatrix(int nRow, int nCol, double[] matrix)
    {
        values = new double[nRow][nCol];
	this.nRow = nRow;
	this.nCol = nCol;

	int i, j;
	for (i = 0; i < nRow; i++) {
	    for (j = 0; j < nCol; j++) {
		values[i][j] = matrix[i*nCol+j];
	    }
	}
    }

    /**
     * Constructs a new GMatrix and copies the initial values
     * from the parameter matrix.
     * @param matrix  the source of the initial values of the new GMatrix
     */
    public GMatrix(GMatrix matrix)
    {
        nRow = matrix.nRow;
	nCol = matrix.nCol;
        values = new double[nRow][nCol];

	int i, j;
	for (i = 0; i < nRow; i++) {
	    for (j = 0; j < nCol; j++) {
		values[i][j] = matrix.values[i][j];
	    }
	}
    }

    /**
     * Sets the value of this matrix to the result of multiplying itself
     * with matrix m1 (this = this * m1).
     * @param m1 the other matrix
     */
    public final void mul(GMatrix m1)
    {
	int i, j, k;

	if (nCol != m1.nRow ||  nCol != m1.nCol)
	    throw new MismatchedSizeException
		(VecMathI18N.getString("GMatrix0"));

	double [][] tmp = new double[nRow][nCol];

	for (i = 0; i < nRow; i++) {
	    for (j = 0; j < nCol; j++) {
		tmp[i][j] = 0.0;
		for (k = 0; k < nCol; k++) {
		    tmp[i][j] += values[i][k]*m1.values[k][j];
		}
	    }
	}

	values = tmp;
    }

    /**
     * Sets the value of this matrix to the result of multiplying
     * the two argument matrices together (this = m1 * m2).
     * @param m1 the first matrix
     * @param m2 the second matrix
     */
    public final void mul(GMatrix m1, GMatrix m2)
    {
	int i, j, k;

	if (m1.nCol != m2.nRow || nRow != m1.nRow || nCol != m2.nCol)
	    throw new MismatchedSizeException
		(VecMathI18N.getString("GMatrix1"));

	double[][] tmp = new double[nRow][nCol];

	for (i = 0; i < m1.nRow; i++) {
	    for (j = 0; j < m2.nCol; j++) {
		tmp[i][j] = 0.0;
		for (k = 0; k < m1.nCol; k++) {
		    tmp[i][j] += m1.values[i][k]*m2.values[k][j];
		}
	    }
	}

	values = tmp;
    }

    /**
     * Computes the outer product of the two vectors; multiplies the
     * the first vector by the transpose of the second vector and places
     * the matrix result into this matrix.  This matrix must be
     * be as big or bigger than getSize(v1)xgetSize(v2).
     * @param v1 the first vector, treated as a row vector
     * @param v2 the second vector, treated as a column vector
     */
    public final void mul(GVector v1, GVector v2)
    {
	int i, j;

	if (nRow < v1.getSize())
	    throw new MismatchedSizeException
		(VecMathI18N.getString("GMatrix2"));

	if (nCol < v2.getSize())
	    throw new MismatchedSizeException
		(VecMathI18N.getString("GMatrix3"));

	for (i = 0; i < v1.getSize(); i++ ) {
	    for (j = 0; j < v2.getSize(); j++ ) {
		values[i][j] = v1.values[i]*v2.values[j];
	    }
	}
    }

    /**
     * Sets the value of this matrix to sum of itself and matrix m1.
     * @param m1 the other matrix
     */
    public final void add(GMatrix m1)
    {
	int i, j;

	if (nRow != m1.nRow)
	    throw new MismatchedSizeException
		(VecMathI18N.getString("GMatrix4"));

	if (nCol != m1.nCol)
	    throw new MismatchedSizeException
		(VecMathI18N.getString("GMatrix5"));

	for (i = 0; i < nRow; i++) {
	    for (j = 0; j < nCol; j++) {
		values[i][j] = values[i][j] + m1.values[i][j];
	    }
	}
    }

    /**
     * Sets the value of this matrix to the matrix sum of matrices m1 and m2.
     * @param m1 the first matrix
     * @param m2 the second matrix
     */
    public final void add(GMatrix m1, GMatrix m2)
    {
	int i, j;

	if (m2.nRow != m1.nRow)
	    throw new MismatchedSizeException
		(VecMathI18N.getString("GMatrix6"));

	if (m2.nCol != m1.nCol)
	    throw new MismatchedSizeException
		(VecMathI18N.getString("GMatrix7"));

	if (nCol != m1.nCol  || nRow != m1.nRow)
	    throw new MismatchedSizeException
		(VecMathI18N.getString("GMatrix8"));

	for (i = 0; i < nRow; i++) {
	    for (j = 0; j < nCol; j++) {
		values[i][j] = m1.values[i][j] + m2.values[i][j];
	    }
	}
    }

    /**
     * Sets the value of this matrix to the matrix difference of itself
     * and matrix m1 (this = this - m1).
     * @param m1 the other matrix
     */
    public final void sub(GMatrix m1)
    {
	int i, j;
	if (nRow != m1.nRow)
	    throw new MismatchedSizeException
		(VecMathI18N.getString("GMatrix9"));

	if (nCol != m1.nCol)
	    throw new MismatchedSizeException
		(VecMathI18N.getString("GMatrix28"));

	for (i = 0; i < nRow; i++) {
	    for (j = 0; j < nCol; j++) {
		values[i][j] = values[i][j] - m1.values[i][j];
	    }
	}
    }

    /**
     * Sets the value of this matrix to the matrix difference
     * of matrices m1 and m2 (this = m1 - m2).
     * @param m1 the first matrix
     * @param m2 the second matrix
     */
    public final void sub(GMatrix m1, GMatrix m2)
    {
	int i, j;
	if (m2.nRow != m1.nRow)
	    throw new MismatchedSizeException
		(VecMathI18N.getString("GMatrix10"));

	if (m2.nCol != m1.nCol)
	    throw new MismatchedSizeException
		(VecMathI18N.getString("GMatrix11"));

	if (nRow !=  m1.nRow || nCol != m1.nCol)
	    throw new MismatchedSizeException
		(VecMathI18N.getString("GMatrix12"));

	for (i = 0; i < nRow; i++) {
	    for (j = 0; j < nCol; j++) {
		values[i][j] = m1.values[i][j] - m2.values[i][j];
	    }
	}
    }

    /**
     * Negates the value of this matrix: this = -this.
     */
    public final void negate()
    {
	int i, j;
	for (i = 0; i < nRow; i++) {
	    for (j = 0;j < nCol; j++) {
		values[i][j] = -values[i][j];
	    }
	}
    }

    /**
     *  Sets the value of this matrix equal to the negation of
     *  of the GMatrix parameter.
     *  @param m1  The source matrix
     */
    public final void negate(GMatrix m1)
    {
	int i, j;
	if (nRow != m1.nRow  || nCol != m1.nCol)
	    throw new MismatchedSizeException
		(VecMathI18N.getString("GMatrix13"));

	for (i = 0; i < nRow; i++) {
	    for (j = 0; j < nCol; j++) {
		values[i][j] =  -m1.values[i][j];
	    }
	}
    }

    /**
     * Sets this GMatrix to the identity matrix.
     */
    public final void setIdentity()
    {
        int i, j;
        for (i = 0; i < nRow; i++) {
	    for (j = 0; j < nCol; j++) {
		values[i][j] = 0.0;
	    }
        }

        int l;
        if (nRow < nCol)
	    l = nRow;
        else
	    l = nCol;

        for (i = 0; i < l; i++) {
	    values[i][i] = 1.0;
        }
    }

    /**
     * Sets all the values in this matrix to zero.
     */
    public final void setZero()
    {
	int i, j;
	for (i = 0; i < nRow; i++) {
	    for (j = 0; j < nCol; j++) {
		values[i][j] = 0.0;
	    }
	}
    }

    /**
     * Subtracts this matrix from the identity matrix and puts the values
     * back into this (this = I - this).
     */
    public final void identityMinus()
    {
	int i, j;

	for(i = 0; i < nRow; i++) {
	    for(j = 0; j < nCol; j++) {
		values[i][j] = -values[i][j];
	    }
	}

        int l;
        if( nRow < nCol)
	    l = nRow;
        else
	    l = nCol;

        for(i = 0; i < l; i++) {
	    values[i][i] += 1.0;
        }
    }


    /**
     * Inverts this matrix in place.
     */
    public final void invert()
    {
	invertGeneral(this);
    }

    /**
     * Inverts matrix m1 and places the new values into this matrix.  Matrix
     * m1 is not modified.
     * @param m1   the matrix to be inverted
     */
    public final void invert(GMatrix m1)
    {
	invertGeneral(m1);
    }

    /**
     * Copies a sub-matrix derived from this matrix into the target matrix.
     * The upper left of the sub-matrix is located at (rowSource, colSource);
     * the lower right of the sub-matrix is located at
     * (lastRowSource,lastColSource).  The sub-matrix is copied into the
     * the target matrix starting at (rowDest, colDest).
     * @param rowSource   the top-most row of the sub-matrix
     * @param colSource   the left-most column of the sub-matrix
     * @param numRow   the number of rows in the sub-matrix
     * @param numCol  the number of columns in the sub-matrix
     * @param rowDest  the top-most row of the position of the copied
     *                 sub-matrix within the target matrix
     * @param colDest  the left-most column of the position of the copied
     *                 sub-matrix within the target matrix
     * @param target  the matrix into which the sub-matrix will be copied
     */
    public final void copySubMatrix(int rowSource, int colSource,
				    int numRow, int numCol, int rowDest,
				    int colDest, GMatrix target)
    {
        int i, j;

	if (this != target) {
	    for (i = 0; i < numRow; i++) {
		for (j = 0; j < numCol; j++) {
		    target.values[rowDest+i][colDest+j] =
			values[rowSource+i][colSource+j];
		}
	    }
	} else {
	    double[][] tmp = new double[numRow][numCol];
	    for (i = 0; i < numRow; i++) {
		for (j = 0; j < numCol; j++) {
		    tmp[i][j] = values[rowSource+i][colSource+j];
		}
	    }
	    for (i = 0; i < numRow; i++) {
		for (j = 0; j < numCol; j++) {
		    target.values[rowDest+i][colDest+j] = tmp[i][j];
		}
	    }
	}
    }

    /**
     * Changes the size of this matrix dynamically.  If the size is increased
     * no data values will be lost.  If the size is decreased, only those data
     * values whose matrix positions were eliminated will be lost.
     * @param nRow  number of desired rows in this matrix
     * @param nCol  number of desired columns in this matrix
     */
    public final void setSize(int nRow, int nCol)
    {
	double[][] tmp = new double[nRow][nCol];
	int i, j, maxRow, maxCol;

	if (this.nRow < nRow)
	    maxRow = this.nRow;
	else
	    maxRow = nRow;

	if (this.nCol < nCol)
	    maxCol = this.nCol;
	else
	    maxCol = nCol;

	for (i = 0; i < maxRow; i++) {
	    for (j = 0; j < maxCol; j++) {
		tmp[i][j] = values[i][j];
	    }
	}

	this.nRow = nRow;
	this.nCol = nCol;

	values = tmp;
    }

    /**
     * Sets the value of this matrix to the values found in the array parameter.
     * The values are copied in one row at a time, in row major
     * fashion.  The array should be at least equal in length to
     * the number of matrix rows times the number of matrix columns
     * in this matrix.
     * @param matrix  the row major source array
     */
    public final void set(double[] matrix)
    {
	int i, j;

	for (i = 0; i < nRow; i++) {
	    for (j = 0; j < nCol; j++) {
		values[i][j] = matrix[nCol*i+j];
	    }
	}
    }

    /**
     * Sets the value of this matrix to that of the Matrix3f provided.
     * @param m1 the matrix
     */
    public final void set(Matrix3f m1)
    {
	int i, j;

	if (nCol < 3 || nRow < 3) { // expand matrix if too small
	    nCol = 3;
	    nRow = 3;
	    values = new double[nRow][nCol];
        }

        values[0][0] = m1.m00;
        values[0][1] = m1.m01;
        values[0][2] = m1.m02;

        values[1][0] = m1.m10;
        values[1][1] = m1.m11;
        values[1][2] = m1.m12;

        values[2][0] = m1.m20;
        values[2][1] = m1.m21;
        values[2][2] = m1.m22;

        for (i = 3; i < nRow; i++) {   // pad rest or matrix with zeros
	    for (j = 3; j < nCol; j++) {
		values[i][j] = 0.0;
	    }
        }
    }

    /**
     * Sets the value of this matrix to that of the Matrix3d provided.
     * @param m1 the matrix
     */
    public final void set(Matrix3d m1)
    {
	if (nRow < 3 || nCol < 3) {
	    values = new double[3][3];
	    nRow = 3;
	    nCol = 3;
	}

        values[0][0] = m1.m00;
        values[0][1] = m1.m01;
        values[0][2] = m1.m02;

        values[1][0] = m1.m10;
        values[1][1] = m1.m11;
        values[1][2] = m1.m12;

        values[2][0] = m1.m20;
        values[2][1] = m1.m21;
        values[2][2] = m1.m22;

        for (int i = 3; i < nRow; i++) {   // pad rest or matrix with zeros
	    for(int j = 3; j < nCol; j++) {
		values[i][j] = 0.0;
	    }
        }

    }

    /**
     * Sets the value of this matrix to that of the Matrix4f provided.
     * @param m1 the matrix
     */
    public final void set(Matrix4f m1)
    {
	if (nRow < 4 || nCol < 4) {
	    values = new double[4][4];
	    nRow = 4;
	    nCol = 4;
	}

        values[0][0] = m1.m00;
        values[0][1] = m1.m01;
        values[0][2] = m1.m02;
        values[0][3] = m1.m03;

        values[1][0] = m1.m10;
        values[1][1] = m1.m11;
        values[1][2] = m1.m12;
        values[1][3] = m1.m13;

        values[2][0] = m1.m20;
        values[2][1] = m1.m21;
        values[2][2] = m1.m22;
        values[2][3] = m1.m23;

        values[3][0] = m1.m30;
        values[3][1] = m1.m31;
        values[3][2] = m1.m32;
        values[3][3] = m1.m33;

        for (int i = 4 ; i < nRow; i++) {   // pad rest or matrix with zeros
	    for (int j = 4; j < nCol; j++) {
		values[i][j] = 0.0;
	    }
        }
    }

    /**
     * Sets the value of this matrix to that of the Matrix4d provided.
     * @param m1 the matrix
     */
    public final void set(Matrix4d m1)
    {
	if (nRow < 4 || nCol < 4) {
	    values = new double[4][4];
	    nRow = 4;
	    nCol = 4;
	}

        values[0][0] = m1.m00;
        values[0][1] = m1.m01;
        values[0][2] = m1.m02;
        values[0][3] = m1.m03;

        values[1][0] = m1.m10;
        values[1][1] = m1.m11;
        values[1][2] = m1.m12;
        values[1][3] = m1.m13;

        values[2][0] = m1.m20;
        values[2][1] = m1.m21;
        values[2][2] = m1.m22;
        values[2][3] = m1.m23;

        values[3][0] = m1.m30;
        values[3][1] = m1.m31;
        values[3][2] = m1.m32;
        values[3][3] = m1.m33;

        for (int i = 4; i < nRow; i++) {   // pad rest or matrix with zeros
	    for (int j = 4; j < nCol; j++) {
		values[i][j] = 0.0;
	    }
        }
    }

    /**
     * Sets the value of this matrix to the values found in matrix m1.
     * @param m1  the source matrix
     */
    public final void set(GMatrix m1)
    {
	int i, j;

	if (nRow < m1.nRow || nCol < m1.nCol) {
	    nRow = m1.nRow;
	    nCol = m1.nCol;
	    values = new double[nRow][nCol];
	}

	for (i = 0; i < Math.min(nRow, m1.nRow); i++) {
	    for (j = 0; j < Math.min(nCol, m1.nCol); j++) {
		values[i][j] = m1.values[i][j];
	    }
	}

        for (i = m1.nRow; i < nRow; i++) {   // pad rest or matrix with zeros
	    for (j = m1.nCol; j < nCol; j++) {
		values[i][j] = 0.0;
	    }
        }
    }

    /**
     * Returns the number of rows in this matrix.
     * @return  number of rows in this matrix
     */
    public final int getNumRow()
    {
        return(nRow);
    }

    /**
     * Returns the number of colmuns in this matrix.
     * @return  number of columns in this matrix
     */
    public final int getNumCol()
    {
	return(nCol);
    }

    /**
     * Retrieves the value at the specified row and column of this matrix.
     * @param row the row number to be retrieved (zero indexed)
     * @param column the column number to be retrieved (zero indexed)
     * @return the value at the indexed element
     */
    public final double getElement(int row, int column)
    {
        return(values[row][column]);
    }


    /**
     * Modifies the value at the specified row and column of this matrix.
     * @param row  the row number to be modified (zero indexed)
     * @param column  the column number to be modified (zero indexed)
     * @param value  the new matrix element value
     */
    public final void setElement(int row, int column, double value)
    {
	values[row][column] = value;
    }

    /**
     * Places the values of the specified row into the array parameter.
     * @param row  the target row number
     * @param array  the array into which the row values will be placed
     */
    public final void getRow(int row, double[] array)
    {
	for (int i = 0; i < nCol; i++) {
            array[i] = values[row][i];
	}
    }

    /**
     * Places the values of the specified row into the vector parameter.
     * @param row  the target row number
     * @param vector  the vector into which the row values will be placed
     */
    public final void getRow(int row, GVector vector)
    {
	if (vector.getSize() < nCol)
	    vector.setSize(nCol);

	for (int i = 0; i < nCol; i++) {
            vector.values[i] = values[row][i];
	}
    }

    /**
     * Places the values of the specified column into the array parameter.
     * @param col  the target column number
     * @param array  the array into which the column values will be placed
     */
    public final void getColumn(int col, double[] array)
    {
	for (int i = 0; i < nRow; i++) {
            array[i] = values[i][col];
	}

    }

    /**
     * Places the values of the specified column into the vector parameter.
     * @param col  the target column number
     * @param vector  the vector into which the column values will be placed
     */
    public final void getColumn(int col, GVector vector)
    {
	if (vector.getSize() < nRow)
	    vector.setSize(nRow);

	for (int i = 0; i < nRow; i++) {
            vector.values[i] = values[i][col];
	}
    }

    /**
     * Places the values in the upper 3x3 of this GMatrix into
     * the matrix m1.
     * @param m1  The matrix that will hold the new values
     */
    public final void get(Matrix3d m1)
    {
	if (nRow < 3 || nCol < 3) {
	    m1.setZero();
	    if (nCol > 0) {
		if (nRow > 0){
		    m1.m00 = values[0][0];
		    if (nRow > 1){
			m1.m10 = values[1][0];
			if( nRow > 2 ){
			    m1.m20= values[2][0];
			}
		    }
		}
		if (nCol > 1) {
		    if (nRow > 0) {
			m1.m01 = values[0][1];
			if (nRow > 1){
			    m1.m11 = values[1][1];
			    if (nRow >  2){
				m1.m21 = values[2][1];
			    }
			}
		    }
		    if (nCol > 2) {
			if (nRow > 0) {
			    m1.m02 = values[0][2];
			    if (nRow > 1) {
				m1.m12 = values[1][2];
				if (nRow > 2) {
				    m1.m22 = values[2][2];
				}
			    }
			}
		    }
		}
	    }
	} else {
	    m1.m00 = values[0][0];
	    m1.m01 = values[0][1];
	    m1.m02 = values[0][2];

	    m1.m10 = values[1][0];
	    m1.m11 = values[1][1];
	    m1.m12 = values[1][2];

	    m1.m20 = values[2][0];
	    m1.m21 = values[2][1];
	    m1.m22 = values[2][2];
	}
    }

    /**
     * Places the values in the upper 3x3 of this GMatrix into
     * the matrix m1.
     * @param m1  The matrix that will hold the new values
     */
    public final void get(Matrix3f m1)
    {

	if (nRow < 3 || nCol < 3) {
	    m1.setZero();
	    if (nCol > 0) {
		if (nRow > 0) {
		    m1.m00 = (float)values[0][0];
		    if (nRow > 1) {
			m1.m10 = (float)values[1][0];
			if (nRow > 2) {
			    m1.m20 = (float)values[2][0];
			}
		    }
		}
		if (nCol > 1) {
		    if (nRow > 0) {
			m1.m01 = (float)values[0][1];
			if (nRow >  1){
			    m1.m11 = (float)values[1][1];
			    if (nRow >  2){
				m1.m21 = (float)values[2][1];
			    }
			}
		    }
		    if (nCol > 2) {
			if (nRow > 0) {
			    m1.m02 = (float)values[0][2];
			    if (nRow > 1) {
				m1.m12 = (float)values[1][2];
				if (nRow > 2) {
				    m1.m22 = (float)values[2][2];
				}
			    }
			}
		    }
		}
	    }
        } else {
	    m1.m00 = (float)values[0][0];
	    m1.m01 = (float)values[0][1];
	    m1.m02 = (float)values[0][2];

	    m1.m10 = (float)values[1][0];
	    m1.m11 = (float)values[1][1];
	    m1.m12 = (float)values[1][2];

	    m1.m20 = (float)values[2][0];
	    m1.m21 = (float)values[2][1];
	    m1.m22 = (float)values[2][2];
	}
    }

    /**
     * Places the values in the upper 4x4 of this GMatrix into
     * the matrix m1.
     * @param m1  The matrix that will hold the new values
     */
    public final void get(Matrix4d m1)
    {
	if (nRow < 4 || nCol < 4) {
	    m1.setZero();
	    if (nCol > 0) {
		if (nRow > 0) {
		    m1.m00 = values[0][0];
		    if (nRow > 1) {
			m1.m10 = values[1][0];
			if (nRow > 2) {
			    m1.m20 = values[2][0];
			    if (nRow > 3) {
				m1.m30 = values[3][0];
			    }
			}
		    }
		}
		if (nCol > 1) {
		    if (nRow > 0) {
			m1.m01 = values[0][1];
			if (nRow > 1) {
			    m1.m11 = values[1][1];
			    if (nRow > 2) {
				m1.m21 = values[2][1];
				if (nRow > 3) {
				    m1.m31 = values[3][1];
				}
			    }
			}
		    }
		    if (nCol > 2) {
			if (nRow > 0) {
			    m1.m02 = values[0][2];
			    if (nRow > 1) {
				m1.m12 = values[1][2];
				if (nRow > 2) {
				    m1.m22 = values[2][2];
				    if (nRow > 3) {
					m1.m32 = values[3][2];
				    }
				}
			    }
			}
			if (nCol > 3) {
			    if (nRow > 0) {
				m1.m03 = values[0][3];
				if (nRow > 1) {
				    m1.m13 = values[1][3];
				    if (nRow > 2) {
					m1.m23 = values[2][3];
					if (nRow > 3) {
					    m1.m33 = values[3][3];
					}
				    }
				}
			    }
			}
		    }
		}
	    }
        } else {
	    m1.m00 = values[0][0];
	    m1.m01 = values[0][1];
	    m1.m02 = values[0][2];
	    m1.m03 = values[0][3];

	    m1.m10 = values[1][0];
	    m1.m11 = values[1][1];
	    m1.m12 = values[1][2];
	    m1.m13 = values[1][3];

	    m1.m20 = values[2][0];
	    m1.m21 = values[2][1];
	    m1.m22 = values[2][2];
	    m1.m23 = values[2][3];

	    m1.m30 = values[3][0];
	    m1.m31 = values[3][1];
	    m1.m32 = values[3][2];
	    m1.m33 = values[3][3];
	}

    }

    /**
     * Places the values in the upper 4x4 of this GMatrix into
     * the matrix m1.
     * @param m1  The matrix that will hold the new values
     */
    public final void get(Matrix4f m1)
    {

	if (nRow < 4 || nCol < 4) {
	    m1.setZero();
	    if (nCol > 0) {
		if (nRow > 0) {
		    m1.m00 = (float)values[0][0];
		    if (nRow > 1) {
			m1.m10 = (float)values[1][0];
			if (nRow > 2) {
			    m1.m20 = (float)values[2][0];
			    if (nRow > 3) {
				m1.m30 = (float)values[3][0];
			    }
			}
		    }
		}
		if (nCol > 1) {
		    if (nRow > 0) {
			m1.m01 = (float)values[0][1];
			if (nRow > 1) {
			    m1.m11 = (float)values[1][1];
			    if (nRow > 2) {
				m1.m21 = (float)values[2][1];
				if (nRow > 3) {
				    m1.m31 = (float)values[3][1];
				}
			    }
			}
		    }
		    if (nCol > 2) {
			if (nRow > 0) {
			    m1.m02 = (float)values[0][2];
			    if (nRow > 1) {
				m1.m12 = (float)values[1][2];
				if (nRow > 2) {
				    m1.m22 = (float)values[2][2];
				    if (nRow > 3) {
					m1.m32 = (float)values[3][2];
				    }
				}
			    }
			}
			if (nCol > 3) {
			    if (nRow > 0) {
				m1.m03 = (float)values[0][3];
				if (nRow > 1) {
				    m1.m13 = (float)values[1][3];
				    if (nRow > 2) {
					m1.m23 = (float)values[2][3];
					if (nRow > 3) {
					    m1.m33 = (float)values[3][3];
					}
				    }
				}
			    }
			}
		    }
		}
	    }
        } else {
	    m1.m00 = (float)values[0][0];
	    m1.m01 = (float)values[0][1];
	    m1.m02 = (float)values[0][2];
	    m1.m03 = (float)values[0][3];

	    m1.m10 = (float)values[1][0];
	    m1.m11 = (float)values[1][1];
	    m1.m12 = (float)values[1][2];
	    m1.m13 = (float)values[1][3];

	    m1.m20 = (float)values[2][0];
	    m1.m21 = (float)values[2][1];
	    m1.m22 = (float)values[2][2];
	    m1.m23 = (float)values[2][3];

	    m1.m30 = (float)values[3][0];
	    m1.m31 = (float)values[3][1];
	    m1.m32 = (float)values[3][2];
	    m1.m33 = (float)values[3][3];
	}
    }

    /**
     * Places the values in the this GMatrix into the matrix m1;
     * m1 should be at least as large as this GMatrix.
     * @param m1  The matrix that will hold the new values
     */
    public final void get(GMatrix m1)
    {
	int i, j, nc, nr;

	if (nCol < m1.nCol)
	    nc = nCol;
	else
	    nc = m1.nCol;

	if (nRow < m1.nRow)
	    nr = nRow;
	else
	    nr = m1.nRow;

	for (i = 0; i < nr; i++) {
	    for (j = 0; j < nc; j++) {
		m1.values[i][j] = values[i][j];
	    }
	}
	for (i = nr; i < m1.nRow; i++) {
	    for (j = 0; j < m1.nCol; j++) {
		m1.values[i][j] = 0.0;
	    }
	}
	for (j = nc; j < m1.nCol; j++) {
	    for (i = 0; i < nr; i++) {
		m1.values[i][j] = 0.0;
	    }
	}
    }

    /**
     * Copy the values from the array into the specified row of this
     * matrix.
     * @param row  the row of this matrix into which the array values
     *             will be copied.
     * @param array  the source array
     */
    public final void setRow(int row, double[] array)
    {
	for (int i = 0; i < nCol; i++) {
            values[row][i] = array[i];
	}
    }

    /**
     * Copy the values from the vector into the specified row of this
     * matrix.
     * @param row  the row of this matrix into which the array values
     *             will be copied
     * @param vector  the source vector
     */
    public final void setRow(int row, GVector vector)
    {
	for(int i = 0; i < nCol; i++) {
            values[row][i] = vector.values[i];
	}
    }

    /**
     * Copy the values from the array into the specified column of this
     * matrix.
     * @param col  the column of this matrix into which the array values
     *             will be copied
     * @param array  the source array
     */
    public final void setColumn(int col, double[] array)
    {
	for(int i = 0; i < nRow; i++) {
            values[i][col] = array[i];
	}
    }

    /**
     * Copy the values from the vector into the specified column of this
     * matrix.
     * @param col  the column of this matrix into which the array values
     *             will be copied
     * @param vector  the source vector
     */
    public final void setColumn(int col, GVector vector)
    {
	for(int i = 0; i < nRow; i++) {
            values[i][col] = vector.values[i];
	}

    }

    /**
     *  Multiplies the transpose of matrix m1 times the transpose of matrix
     *  m2, and places the result into this.
     *  @param m1  The matrix on the left hand side of the multiplication
     *  @param m2  The matrix on the right hand side of the multiplication
     */
    public final void mulTransposeBoth(GMatrix m1, GMatrix m2)
    {
	int i, j, k;

	if (m1.nRow != m2.nCol || nRow != m1.nCol || nCol != m2.nRow)
	    throw new MismatchedSizeException
		(VecMathI18N.getString("GMatrix14"));

	if (m1 == this || m2 == this) {
	    double[][] tmp = new double[nRow][nCol];
	    for (i = 0; i < nRow; i++) {
		for (j = 0; j < nCol; j++) {
		    tmp[i][j] = 0.0;
		    for (k = 0; k < m1.nRow; k++) {
			tmp[i][j] += m1.values[k][i]*m2.values[j][k];
		    }
		}
	    }
	    values = tmp;
	} else {
	    for (i = 0; i < nRow; i++) {
		for (j = 0; j < nCol; j++) {
		    values[i][j] = 0.0;
		    for (k = 0; k < m1.nRow; k++) {
			values[i][j] += m1.values[k][i]*m2.values[j][k];
		    }
		}
	    }
	}
    }

    /**
     *  Multiplies matrix m1 times the transpose of matrix m2, and
     *  places the result into this.
     *  @param m1  The matrix on the left hand side of the multiplication
     *  @param m2  The matrix on the right hand side of the multiplication
     */
    public final void mulTransposeRight(GMatrix m1, GMatrix m2)
    {
	int i, j, k;

	if (m1.nCol != m2.nCol || nCol != m2.nRow || nRow != m1.nRow)
	    throw new MismatchedSizeException
		(VecMathI18N.getString("GMatrix15"));

	if (m1 == this || m2 == this) {
	    double[][] tmp = new double[nRow][nCol];
	    for (i = 0; i < nRow; i++) {
		for (j = 0; j < nCol; j++) {
		    tmp[i][j] = 0.0;
		    for (k = 0; k < m1.nCol; k++) {
			tmp[i][j] += m1.values[i][k]*m2.values[j][k];
		    }
		}
	    }
	    values = tmp;
	} else {
	    for (i = 0; i < nRow; i++) {
		for (j = 0;j < nCol; j++) {
		    values[i][j] = 0.0;
		    for (k = 0; k < m1.nCol; k++) {
			values[i][j] += m1.values[i][k]*m2.values[j][k];
		    }
		}
	    }
	}

    }


    /**
     *  Multiplies the transpose of matrix m1 times matrix m2, and
     *  places the result into this.
     *  @param m1  The matrix on the left hand side of the multiplication
     *  @param m2  The matrix on the right hand side of the multiplication
     */
    public final void mulTransposeLeft(GMatrix m1, GMatrix m2)
    {
	int i, j, k;

	if (m1.nRow != m2.nRow || nCol != m2.nCol || nRow != m1.nCol)
	    throw new MismatchedSizeException
		(VecMathI18N.getString("GMatrix16"));

	if (m1 == this || m2 == this) {
	    double[][] tmp = new double[nRow][nCol];
	    for (i = 0; i < nRow; i++) {
		for (j = 0; j < nCol; j++) {
		    tmp[i][j] = 0.0;
		    for (k = 0; k < m1.nRow; k++) {
			tmp[i][j] += m1.values[k][i]*m2.values[k][j];
		    }
		}
	    }
	    values = tmp;
	} else {
	    for (i = 0; i < nRow; i++) {
		for (j = 0; j < nCol; j++) {
		    values[i][j] = 0.0;
		    for (k = 0; k < m1.nRow; k++) {
			values[i][j] += m1.values[k][i]*m2.values[k][j];
		    }
		}
	    }
	}
    }


    /**
     * Transposes this matrix in place.
     */
    public final void transpose()
    {
        int i, j;

        if (nRow != nCol) {
	    double[][] tmp;
	    i=nRow;
	    nRow = nCol;
	    nCol = i;
	    tmp = new double[nRow][nCol];
	    for (i = 0; i < nRow; i++) {
		for (j = 0; j < nCol; j++) {
		    tmp[i][j] = values[j][i];
		}
	    }
	    values = tmp;
        } else {
	    double swap;
	    for (i = 0; i < nRow; i++) {
		for (j = 0; j < i; j++) {
		    swap = values[i][j];
		    values[i][j] = values[j][i];
		    values[j][i] = swap;
		}
	    }
	}
    }

    /**
     * Places the matrix values of the transpose of matrix m1 into this matrix.
     * @param m1  the matrix to be transposed (but not modified)
     */
    public final void transpose(GMatrix m1)
    {
        int i, j;

        if (nRow != m1.nCol || nCol != m1.nRow)
	    throw new MismatchedSizeException
		(VecMathI18N.getString("GMatrix17"));

        if (m1 != this) {
	    for (i = 0; i < nRow; i++) {
		for (j = 0;j < nCol; j++) {
		    values[i][j] = m1.values[j][i];
		}
	    }
	} else {
	    transpose();
        }
    }

    /**
     * Returns a string that contains the values of this GMatrix.
     * @return the String representation
     */
    @Override
    public String toString()
    {
	StringBuffer buffer = new StringBuffer(nRow*nCol*8);

	int i, j;

	for (i = 0; i < nRow; i++) {
	    for (j = 0; j < nCol; j++) {
		buffer.append(values[i][j]).append(" ");
	    }
	    buffer.append("\n");
	}

	return buffer.toString();
    }

//    private static void checkMatrix( GMatrix m)
//    {
//	int i, j;
//
//	for (i = 0; i < m.nRow; i++) {
//	    for (j = 0; j < m.nCol; j++) {
//		if (Math.abs(m.values[i][j]) < 0.0000000001) {
//		    System.out.print(" 0.0     ");
//		} else {
//		    System.out.print(" " + m.values[i][j]);
//		}
//	    }
//	    System.out.print("\n");
//	}
//    }


    /**
     * Returns a hash code value based on the data values in this
     * object.  Two different GMatrix objects with identical data
     * values (i.e., GMatrix.equals returns true) will return the
     * same hash number.  Two GMatrix objects with different data
     * members may return the same hash value, although this is not
     * likely.
     * @return the integer hash code value
     */
    @Override
    public int hashCode() {
	long bits = 1L;

	bits = VecMathUtil.hashLongBits(bits, nRow);
	bits = VecMathUtil.hashLongBits(bits, nCol);

	for (int i = 0; i < nRow; i++) {
		for (int j = 0; j < nCol; j++) {
			bits = VecMathUtil.hashDoubleBits(bits, values[i][j]);
		}
	}

	return VecMathUtil.hashFinish(bits);
    }


    /**
     * Returns true if all of the data members of GMatrix m1 are
     * equal to the corresponding data members in this GMatrix.
     * @param m1  The matrix with which the comparison is made.
     * @return  true or false
     */
    public boolean equals(GMatrix m1)
    {
	try {
	    int i, j;

	    if (nRow != m1.nRow || nCol != m1.nCol)
		return false;

	    for (i = 0;i < nRow; i++) {
		for (j = 0; j < nCol; j++) {
		    if (values[i][j] != m1.values[i][j])
			return false;
		}
	    }
	    return true;
	}
	catch (NullPointerException e2) {
	    return false;
	}
    }

    /**
     * Returns true if the Object o1 is of type GMatrix and all of the
     * data members of o1 are equal to the corresponding data members in
     * this GMatrix.
     * @param o1  The object with which the comparison is made.
     * @return  true or false
     */
    @Override
    public boolean equals(Object o1)
    {
        try {
	    GMatrix m2 = (GMatrix) o1;
	    int i, j;
	    if (nRow != m2.nRow || nCol != m2.nCol)
		return false;

	    for (i = 0; i < nRow; i++) {
                for (j = 0; j < nCol; j++) {
		    if (values[i][j] != m2.values[i][j])
			return false;
                }
	    }
	    return true;
        }
        catch (ClassCastException e1) {
	    return false;
	}
        catch (NullPointerException e2) {
	    return false;
	}
    }

    /**
     * @deprecated Use epsilonEquals(GMatrix, double) instead
     */
    public boolean epsilonEquals(GMatrix m1, float epsilon) {
	return epsilonEquals(m1, (double)epsilon);
    }

    /**
     * Returns true if the L-infinite distance between this matrix
     * and matrix m1 is less than or equal to the epsilon parameter,
     * otherwise returns false.  The L-infinite
     * distance is equal to
     * MAX[i=0,1,2, . . .n ; j=0,1,2, . . .n ; abs(this.m(i,j) - m1.m(i,j)]
     * @param m1  The matrix to be compared to this matrix
     * @param epsilon  the threshold value
     */
    public boolean epsilonEquals(GMatrix m1, double epsilon)
    {
	int i, j;
	double diff;
	if (nRow != m1.nRow || nCol != m1.nCol)
	    return false;

        for (i = 0; i < nRow; i++) {
	    for (j = 0; j < nCol; j++) {
		diff = values[i][j] - m1.values[i][j];
		if ((diff < 0 ? -diff : diff) > epsilon)
		    return false;
	    }
        }
        return true;
    }

    /**
     * Returns the trace of this matrix.
     * @return  the trace of this matrix
     */
    public final double trace()
    {
	int i, l;
	double t;

	if (nRow < nCol)
	    l = nRow;
	else
	    l = nCol;

	t = 0.0;
	for (i = 0; i < l; i++) {
	    t += values[i][i];
	}
	return t;
    }

    /**
     *  Finds the singular value decomposition (SVD) of this matrix
     *  such that this = U*W*transpose(V); and returns the rank of
     *  this matrix; the values of U,W,V are all overwritten.  Note
     *  that the matrix V is output as V, and
     *  not transpose(V).  If this matrix is mxn, then U is mxm, W
     *  is a diagonal matrix that is mxn, and V is nxn.  Using the
     *  notation W = diag(w), then the inverse of this matrix is:
     *  inverse(this) = V*diag(1/w)*tranpose(U), where diag(1/w)
     *  is the same matrix as W except that the reciprocal of each
     *  of the diagonal components is used.
     *  @param U  The computed U matrix in the equation this = U*W*transpose(V)
     *  @param W  The computed W matrix in the equation this = U*W*transpose(V)
     *  @param V  The computed V matrix in the equation this = U*W*transpose(V)
     *  @return  The rank of this matrix.
     */
    public final int SVD(GMatrix U, GMatrix W, GMatrix V)
    {
	// check for consistancy in dimensions
	if (nCol != V.nCol || nCol != V.nRow) {
	    throw new MismatchedSizeException
		(VecMathI18N.getString("GMatrix18"));
	}

	if (nRow != U.nRow || nRow != U.nCol) {
	    throw new MismatchedSizeException
		(VecMathI18N.getString("GMatrix25"));
	}

	if (nRow != W.nRow || nCol != W.nCol) {
	    throw new MismatchedSizeException
		(VecMathI18N.getString("GMatrix26"));
	}

	// Fix ArrayIndexOutOfBounds for 2x2 matrices, which partially
	// addresses bug 4348562 for J3D 1.2.1.
	//
	// Does *not* fix the following problems reported in 4348562,
	// which will wait for J3D 1.3:
	//
	//   1) no output of W
	//   2) wrong transposition of U
	//   3) wrong results for 4x4 matrices
	//   4) slow performance
	if (nRow == 2 && nCol == 2) {
	    if (values[1][0] == 0.0) {
		U.setIdentity();
		V.setIdentity();

		if (values[0][1] == 0.0) {
		    return 2;
		}

		double[] sinl = new double[1];
		double[] sinr = new double[1];
		double[] cosl = new double[1];
		double[] cosr = new double[1];
		double[] single_values = new double[2];

		single_values[0] = values[0][0];
		single_values[1] = values[1][1];

		compute_2X2(values[0][0], values[0][1], values[1][1],
			    single_values, sinl, cosl, sinr, cosr, 0);

		update_u(0, U, cosl, sinl);
		update_v(0, V, cosr, sinr);

		return 2;
	    }
	    // else call computeSVD() and check for 2x2 there
	}

	return computeSVD(this, U, W, V);
    }

    /**
     * LU Decomposition: this matrix must be a square matrix and the
     * LU GMatrix parameter must be the same size as this matrix.
     * The matrix LU will be overwritten as the combination of a
     * lower diagonal and upper diagonal matrix decompostion of this
     * matrix; the diagonal
     * elements of L (unity) are not stored.  The GVector parameter
     * records the row permutation effected by the partial pivoting,
     * and is used as a parameter to the GVector method LUDBackSolve
     * to solve sets of linear equations.
     * This method returns +/- 1 depending on whether the number
     * of row interchanges was even or odd, respectively.
     * @param LU  The matrix into which the lower and upper decompositions
     * will be placed.
     * @param permutation  The row permutation effected by the partial
     * pivoting
     * @return  +-1 depending on whether the number of row interchanges
     * was even or odd respectively
     */
    public final int LUD(GMatrix LU, GVector permutation)
    {
        int size = LU.nRow*LU.nCol;
        double[] temp = new double[size];
	int[] even_row_exchange = new int[1];
	int[] row_perm = new int[LU.nRow];
	int i, j;

        if (nRow != nCol) {
	    throw new MismatchedSizeException
		(VecMathI18N.getString("GMatrix19"));
        }

        if (nRow != LU.nRow) {
	    throw new MismatchedSizeException
		(VecMathI18N.getString("GMatrix27"));
        }

        if (nCol != LU.nCol) {
	    throw new MismatchedSizeException
		(VecMathI18N.getString("GMatrix27"));
        }

        if (LU.nRow != permutation.getSize()) {
	    throw new MismatchedSizeException
		(VecMathI18N.getString("GMatrix20"));
        }

	for (i = 0; i < nRow; i++) {
	    for (j = 0; j < nCol; j++) {
		temp[i*nCol+j] = values[i][j];
	    }
        }

        // Calculate LU decomposition: Is the matrix singular?
        if (!luDecomposition(LU.nRow, temp, row_perm, even_row_exchange)) {
            // Matrix has no inverse
            throw new SingularMatrixException
		(VecMathI18N.getString("GMatrix21"));
        }

	for (i = 0; i < nRow; i++) {
	    for (j = 0; j < nCol; j++) {
		LU.values[i][j] = temp[i*nCol+j];
	    }
        }

	for (i = 0; i < LU.nRow; i++){
	    permutation.values[i] = (double)row_perm[i];
        }

        return even_row_exchange[0];
    }

    /**
     *  Sets this matrix to a uniform scale matrix; all of the
     *  values are reset.
     *  @param scale  The new scale value
     */
    public final void setScale(double scale)
    {
	int i, j, l;

	if (nRow < nCol)
	    l = nRow;
	else
	    l = nCol;

	for (i = 0; i < nRow; i++) {
	    for (j = 0; j < nCol; j++) {
		values[i][j] = 0.0;
	    }
	}

	for (i = 0; i < l; i++) {
	    values[i][i] = scale;
	}
    }

    /**
     * General invert routine.  Inverts m1 and places the result in "this".
     * Note that this routine handles both the "this" version and the
     * non-"this" version.
     *
     * Also note that since this routine is slow anyway, we won't worry
     * about allocating a little bit of garbage.
     */
    final void invertGeneral(GMatrix  m1) {
        int size = m1.nRow*m1.nCol;
	double temp[] = new double[size];
	double result[] = new double[size];
	int row_perm[] = new int[m1.nRow];
	int[] even_row_exchange = new int[1];
	int i, j;

	// Use LU decomposition and backsubstitution code specifically
	// for floating-point nxn matrices.
	if (m1.nRow != m1.nCol) {
	    // Matrix is either under or over determined
	    throw new MismatchedSizeException
		(VecMathI18N.getString("GMatrix22"));
	}

	// Copy source matrix to temp
	for (i = 0; i < nRow; i++) {
	    for (j = 0; j < nCol; j++) {
		temp[i*nCol+j] = m1.values[i][j];
	    }
	}

	// Calculate LU decomposition: Is the matrix singular?
	if (!luDecomposition(m1.nRow, temp, row_perm, even_row_exchange)) {
	    // Matrix has no inverse
	    throw new SingularMatrixException
		(VecMathI18N.getString("GMatrix21"));
	}

	// Perform back substitution on the identity matrix
        for (i = 0; i < size; i++)
	    result[i] = 0.0;

        for (i = 0; i < nCol; i++)
	    result[i+i*nCol] = 1.0;

	luBacksubstitution(m1.nRow, temp, row_perm, result);

	for (i = 0; i < nRow; i++) {
	    for (j = 0; j < nCol; j++) {
		values[i][j] =  result[i*nCol+j];
	    }
        }
    }

    /**
     * Given a nxn array "matrix0", this function replaces it with the
     * LU decomposition of a row-wise permutation of itself.  The input
     * parameters are "matrix0" and "dim".  The array "matrix0" is also
     * an output parameter.  The vector "row_perm[]" is an output
     * parameter that contains the row permutations resulting from partial
     * pivoting.  The output parameter "even_row_xchg" is 1 when the
     * number of row exchanges is even, or -1 otherwise.  Assumes data
     * type is always double.
     *
     * @return true if the matrix is nonsingular, or false otherwise.
     */
    //
    // Reference: Press, Flannery, Teukolsky, Vetterling,
    //	      _Numerical_Recipes_in_C_, Cambridge University Press,
    //	      1988, pp 40-45.
    //
    static boolean luDecomposition(int dim, double[] matrix0,
				   int[] row_perm, int[] even_row_xchg) {

	double row_scale[] = new double[dim];

	// Determine implicit scaling information by looping over rows
	int i, j;
	int ptr, rs, mtx;
	double big, temp;

	ptr = 0;
	rs = 0;
	even_row_xchg[0] = 1;

	// For each row ...
	i = dim;
	while (i-- != 0) {
	    big = 0.0;

	    // For each column, find the largest element in the row
	    j = dim;
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

	// For all columns, execute Crout's method
	mtx = 0;
	for (j = 0; j < dim; j++) {
	    int imax, k;
	    int target, p1, p2;
	    double sum;

	    // Determine elements of upper diagonal matrix U
	    for (i = 0; i < j; i++) {
		target = mtx + (dim*i) + j;
		sum = matrix0[target];
		k = i;
		p1 = mtx + (dim*i);
		p2 = mtx + j;
		while (k-- != 0) {
		    sum -= matrix0[p1] * matrix0[p2];
		    p1++;
		    p2 += dim;
		}
		matrix0[target] = sum;
	    }

	    // Search for largest pivot element and calculate
	    // intermediate elements of lower diagonal matrix L.
	    big = 0.0;
	    imax = -1;
	    for (i = j; i < dim; i++) {
		target = mtx + (dim*i) + j;
		sum = matrix0[target];
		k = j;
		p1 = mtx + (dim*i);
		p2 = mtx + j;
		while (k-- != 0) {
		    sum -= matrix0[p1] * matrix0[p2];
		    p1++;
		    p2 += dim;
		}
		matrix0[target] = sum;

		// Is this the best pivot so far?
		if ((temp = row_scale[i] * Math.abs(sum)) >= big) {
		    big = temp;
		    imax = i;
		}
	    }

	    if (imax < 0) {
		throw new RuntimeException(VecMathI18N.getString("GMatrix24"));
	    }

	    // Is a row exchange necessary?
	    if (j != imax) {
		// Yes: exchange rows
		k = dim;
		p1 = mtx + (dim*imax);
		p2 = mtx + (dim*j);
		while (k-- != 0) {
		    temp = matrix0[p1];
		    matrix0[p1++] = matrix0[p2];
		    matrix0[p2++] = temp;
		}

		// Record change in scale factor
		row_scale[imax] = row_scale[j];
		even_row_xchg[0] = -even_row_xchg[0]; // change exchange parity
	    }

	    // Record row permutation
	    row_perm[j] = imax;

	    // Is the matrix singular
	    if (matrix0[(mtx + (dim*j) + j)] == 0.0) {
		return false;
	    }

	    // Divide elements of lower diagonal matrix L by pivot
	    if (j != (dim-1)) {
		temp = 1.0 / (matrix0[(mtx + (dim*j) + j)]);
		target = mtx + (dim*(j+1)) + j;
		i = (dim-1) - j;
		while (i-- != 0) {
		    matrix0[target] *= temp;
		    target += dim;
		}
	    }

	}

	return true;
    }

    /**
     * Solves a set of linear equations.  The input parameters "matrix1",
     * and "row_perm" come from luDecompostion and do not change
     * here.  The parameter "matrix2" is a set of column vectors assembled
     * into a nxn matrix of floating-point values.  The procedure takes each
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
    static void luBacksubstitution(int dim, double[] matrix1,
				   int[] row_perm,
				   double[] matrix2) {

	int i, ii, ip, j, k;
	int rp;
	int cv, rv, ri;
	double tt;

	// rp = row_perm;
	rp = 0;

	// For each column vector of matrix2 ...
	for (k = 0; k < dim; k++) {
	    // cv = &(matrix2[0][k]);
	    cv = k;
	    ii = -1;

	    // Forward substitution
	    for (i = 0; i < dim; i++) {
		double sum;

		ip = row_perm[rp+i];
		sum = matrix2[cv+dim*ip];
		matrix2[cv+dim*ip] = matrix2[cv+dim*i];
		if (ii >= 0) {
		    // rv = &(matrix1[i][0]);
		    rv = i*dim;
		    for (j = ii; j <= i-1; j++) {
			sum -= matrix1[rv+j] * matrix2[cv+dim*j];
		    }
		}
		else if (sum != 0.0) {
		    ii = i;
		}
		matrix2[cv+dim*i] = sum;
	    }

	    // Backsubstitution
	    for (i = 0; i < dim; i++) {
		ri = (dim-1-i);
		rv = dim*(ri);
		tt = 0.0;
		for(j=1;j<=i;j++) {
		    tt += matrix1[rv+dim-j] * matrix2[cv+dim*(dim-j)];
		}
		matrix2[cv+dim*ri]= (matrix2[cv+dim*ri] - tt) / matrix1[rv+ri];
            }
	}
    }

    static int computeSVD(GMatrix mat, GMatrix U, GMatrix W, GMatrix V) {
	int i, j, k;
	int nr, nc, si;

	int rank;
	double mag,scale, t;
	int eLength, sLength, vecLength;

	GMatrix tmp = new GMatrix(mat.nRow, mat.nCol);
	GMatrix u = new GMatrix(mat.nRow, mat.nCol);
	GMatrix v = new GMatrix(mat.nRow, mat.nCol);
	GMatrix m = new GMatrix(mat);

	// compute the number of singular values
	if (m.nRow >= m.nCol) {
	    sLength = m.nCol;
	    eLength = m.nCol-1;
	}else {
	    sLength = m.nRow;
	    eLength = m.nRow;
	}

	if (m.nRow > m.nCol)
	    vecLength = m.nRow;
	else
	    vecLength = m.nCol;

	double[] vec = new double[vecLength];
	double[] single_values = new double[sLength];
	double[] e = new double[eLength];

	if(debug) {
	    System.out.println("input to compute_svd = \n"+m.toString());
	}

	rank = 0;

	U.setIdentity();
	V.setIdentity();

	nr = m.nRow;
	nc = m.nCol;

	// householder reduction
	for (si = 0; si < sLength; si++) {
	    // for each singular value

	    if (nr > 1) {
		// zero out column
		if (debug)
		    System.out.println
			("*********************** U ***********************\n");

		// compute reflector
		mag = 0.0;
		for (i = 0; i < nr; i++) {
		    mag += m.values[i+si][si] * m.values[i+si][si];
		    if (debug)
			System.out.println
			    ("mag = " + mag + " matrix.dot = " +
			     m.values[i+si][si] * m.values[i+si][si]);
		}

		mag = Math.sqrt(mag);
		if (m.values[si][si] == 0.0) {
		    vec[0] = mag;
		} else {
		    vec[0] = m.values[si][si] + d_sign(mag, m.values[si][si]);
		}

		for (i = 1; i < nr; i++) {
		    vec[i] =  m.values[si+i][si];
		}

		scale = 0.0;
		for (i = 0; i < nr; i++) {
		    if (debug)
			System.out.println("vec["+i+"]="+vec[i]);

		    scale += vec[i]*vec[i];
		}

		scale = 2.0/scale;
		if (debug)
		    System.out.println("scale = "+scale);

		for (j = si; j < m.nRow; j++) {
		    for (k = si; k < m.nRow; k++) {
			u.values[j][k] = -scale * vec[j-si]*vec[k-si];
		    }
		}

		for (i = si; i < m.nRow; i++){
		    u.values[i][i] +=  1.0;
		}

		// compute s
		t = 0.0;
		for (i = si; i < m.nRow; i++){
		    t += u.values[si][i] * m.values[i][si];
		}
		m.values[si][si] = t;

		// apply reflector
		for (j = si; j < m.nRow; j++) {
		    for (k = si+1; k < m.nCol; k++) {
			tmp.values[j][k] = 0.0;
			for (i = si; i < m.nCol; i++) {
			    tmp.values[j][k] += u.values[j][i] * m.values[i][k];
			}
		    }
		}

		for (j = si; j < m.nRow; j++) {
		    for (k = si+1; k < m.nCol; k++) {
			m.values[j][k] = tmp.values[j][k];
		    }
		}

		if (debug) {
		    System.out.println("U =\n" + U.toString());
		    System.out.println("u =\n" + u.toString());
		}

		// update U matrix
		for (j = si; j < m.nRow; j++) {
		    for (k = 0; k < m.nCol; k++) {
			tmp.values[j][k] = 0.0;
			for (i = si; i < m.nCol; i++) {
			    tmp.values[j][k] += u.values[j][i] * U.values[i][k];
			}
		    }
		}

		for (j = si; j < m.nRow; j++) {
		    for (k = 0; k < m.nCol; k++) {
			U.values[j][k] = tmp.values[j][k];
		    }
		}

		if (debug) {
		    System.out.println("single_values["+si+"] =\n" +
				       single_values[si]);
		    System.out.println("m =\n" + m.toString());
		    System.out.println("U =\n" + U.toString());
		}

		nr--;
	    }

	    if( nc > 2 ) {
		// zero out row
		if (debug)
		    System.out.println
			("*********************** V ***********************\n");

		mag = 0.0;
		for (i = 1; i < nc; i++){
		    mag += m.values[si][si+i] * m.values[si][si+i];
		}

		if (debug)
		    System.out.println("mag = " + mag);

		// generate the reflection vector, compute the first entry and
		// copy the rest from the row to be zeroed
		mag = Math.sqrt(mag);
		if (m.values[si][si+1] == 0.0) {
		    vec[0] = mag;
		} else {
		    vec[0] = m.values[si][si+1] +
			d_sign(mag, m.values[si][si+1]);
		}

		for (i = 1; i < nc - 1; i++){
		    vec[i] =  m.values[si][si+i+1];
		}

		// use reflection vector to compute v matrix
		scale = 0.0;
		for (i = 0; i < nc - 1; i++){
		    if( debug )System.out.println("vec["+i+"]="+vec[i]);
		    scale += vec[i]*vec[i];
		}

		scale = 2.0/scale;
		if (debug)
		    System.out.println("scale = "+scale);

		for (j = si + 1; j < nc; j++) {
		    for (k = si+1; k < m.nCol; k++) {
			v.values[j][k] = -scale * vec[j-si-1]*vec[k-si-1];
		    }
		}

		for (i = si + 1; i < m.nCol; i++){
		    v.values[i][i] +=  1.0;
		}

		t=0.0;
		for (i = si; i < m.nCol; i++){
		    t += v.values[i][si+1] * m.values[si][i];
		}
		m.values[si][si+1]=t;

		// apply reflector
		for (j = si + 1; j < m.nRow; j++) {
		    for (k = si + 1; k < m.nCol; k++) {
			tmp.values[j][k] = 0.0;
			for (i = si + 1; i < m.nCol; i++) {
			    tmp.values[j][k] += v.values[i][k] * m.values[j][i];
			}
		    }
		}

		for (j = si + 1; j < m.nRow; j++) {
		    for (k = si + 1; k < m.nCol; k++) {
			m.values[j][k] = tmp.values[j][k];
		    }
		}

		if (debug) {
		    System.out.println("V =\n" + V.toString());
		    System.out.println("v =\n" + v.toString());
		    System.out.println("tmp =\n" + tmp.toString());
		}

		// update V matrix
		for (j = 0; j < m.nRow; j++) {
		    for (k = si + 1; k < m.nCol; k++) {
			tmp.values[j][k] = 0.0;
			for (i = si + 1; i < m.nCol; i++) {
			    tmp.values[j][k] += v.values[i][k] * V.values[j][i];
			}
		    }
		}

		if (debug)
		    System.out.println("tmp =\n" + tmp.toString());

		for (j = 0;j < m.nRow; j++) {
		    for (k = si + 1; k < m.nCol; k++) {
			V.values[j][k] = tmp.values[j][k];
		    }
		}

		if (debug) {
		    System.out.println("m =\n" + m.toString());
		    System.out.println("V =\n" + V.toString());
		}

		nc--;
	    }
	}

	for (i = 0; i < sLength; i++){
	    single_values[i] = m.values[i][i];
	}

	for (i = 0; i < eLength; i++){
	    e[i] = m.values[i][i+1];
	}

	// Fix ArrayIndexOutOfBounds for 2x2 matrices, which partially
	// addresses bug 4348562 for J3D 1.2.1.
	//
	// Does *not* fix the following problems reported in 4348562,
	// which will wait for J3D 1.3:
	//
	//   1) no output of W
	//   2) wrong transposition of U
	//   3) wrong results for 4x4 matrices
	//   4) slow performance
	if (m.nRow == 2 && m.nCol == 2) {
	    double[] cosl = new double[1];
	    double[] cosr = new double[1];
	    double[] sinl = new double[1];
	    double[] sinr = new double[1];

	    compute_2X2(single_values[0], e[0], single_values[1],
			single_values, sinl, cosl, sinr, cosr, 0);

	    update_u(0, U, cosl, sinl);
	    update_v(0, V, cosr, sinr);

	    return 2;
	}

	// compute_qr causes ArrayIndexOutOfBounds for 2x2 matrices
	compute_qr (0, e.length-1, single_values, e, U, V);

	// compute rank = number of non zero singular values
	rank = single_values.length;

	// sort by order of size of single values
	// and check for zero's
	return rank;
    }

    static void compute_qr(int start, int end, double[] s, double[] e,
			   GMatrix u, GMatrix v) {

	int i, k, n, sl;
	boolean converged;
	double shift, r, f, g;
	double[] cosl = new double[1];
	double[] cosr = new double[1];
	double[] sinl = new double[1];
	double[] sinr = new double[1];
	GMatrix m = new GMatrix(u.nCol, v.nRow);

	final int MAX_INTERATIONS = 2;
	final double CONVERGE_TOL = 4.89E-15;

	if (debug) {
	    System.out.println("start =" + start);
	    System.out.println("s =\n");
	    for(i=0;i<s.length;i++) {
		System.out.println(s[i]);
	    }

	    System.out.println("\nes =\n");
	    for (i = 0; i < e.length; i++) {
		System.out.println(e[i]);
	    }

	    for (i = 0; i < s.length; i++) {
		m.values[i][i] = s[i];
	    }

	    for (i = 0; i < e.length; i++) {
		m.values[i][i+1] = e[i];
	    }
	    System.out.println("\nm =\n" + m.toString());
	}

	double c_b48 =  1.0;
	converged = false;

	if (debug)
	    print_svd(s, e, u, v);

	f = 0.0;
	g = 0.0;

	for (k = 0; k < MAX_INTERATIONS && !converged;k++) {
	    for (i = start; i <= end; i++) {

		// if at start of iterfaction compute shift
		if (i == start) {
		    if (e.length == s.length)
			sl = end;
		    else
			sl = end + 1;

		    shift = compute_shift(s[sl-1], e[end], s[sl]);

		    f = (Math.abs(s[i]) - shift) *
			(d_sign(c_b48, s[i]) + shift/s[i]);
		    g = e[i];
		}

		r = compute_rot(f, g, sinr, cosr);
		if (i != start)
		    e[i-1] = r;

		f = cosr[0] * s[i] + sinr[0] * e[i];
		e[i] = cosr[0] * e[i] - sinr[0] * s[i];
		g = sinr[0] * s[i+1];
		s[i+1] = cosr[0] * s[i+1];

		// if (debug) print_se(s,e);
		update_v (i, v, cosr, sinr);
		if (debug)
		    print_m(m,u,v);

		r = compute_rot(f, g, sinl, cosl);
		s[i] = r;
		f = cosl[0] * e[i] + sinl[0] * s[i+1];
		s[i+1] = cosl[0] * s[i+1] - sinl[0] * e[i];

		if( i < end) {
		    // if not last
		    g = sinl[0] * e[i+1];
		    e[i+1] =  cosl[0] * e[i+1];
		}
		//if (debug) print_se(s,e);

		update_u(i, u, cosl, sinl);
		if (debug)
		    print_m(m,u,v);
	    }

	    // if extra off diagonal perform one more right side rotation
	    if (s.length == e.length) {
		r = compute_rot(f, g, sinr, cosr);
		f = cosr[0] * s[i] + sinr[0] * e[i];
		e[i] = cosr[0] * e[i] - sinr[0] * s[i];
		s[i+1] = cosr[0] * s[i+1];

		update_v(i, v, cosr, sinr);
		if (debug)
		    print_m(m,u,v);
	    }

	    if (debug) {
		System.out.println
		    ("\n*********************** iteration #" + k +
		     " ***********************\n");
		print_svd(s, e, u, v);
	    }

	    // check for convergence on off diagonals and reduce
	    while ((end-start > 1) && (Math.abs(e[end]) < CONVERGE_TOL)) {
		end--;
	    }

	    // check if need to split
	    for (n = end - 2; n > start; n--) {
		if (Math.abs(e[n]) < CONVERGE_TOL) {     // split
		    compute_qr(n + 1, end, s, e, u, v);  // do lower matrix
		    end = n - 1;                         // do upper matrix

		    // check for convergence on off diagonals and reduce
		    while ((end - start > 1) &&
			   (Math.abs(e[end]) < CONVERGE_TOL)) {
			end--;
		    }
		}
	    }

	    if (debug)
		System.out.println("start = " + start);

	    if ((end - start <= 1) && (Math.abs(e[start+1]) < CONVERGE_TOL)) {
		converged = true;
	    } else {
		// check if zero on the diagonal
	    }

	}

	if (debug)
	    System.out.println("\n****call compute_2X2 ********************\n");

	if (Math.abs(e[1]) < CONVERGE_TOL) {
	    compute_2X2(s[start], e[start], s[start+1], s,
			sinl, cosl, sinr, cosr, 0);
	    e[start] = 0.0;
	    e[start+1] = 0.0;
	} else {
	}

	i = start;
	update_u(i, u, cosl, sinl);
	update_v(i, v, cosr, sinr);

	if(debug) {
	    System.out.println
		("\n*******after call compute_2X2 **********************\n");
	    print_svd(s, e, u, v);
	}

	return;
    }

//    private static void print_se(double[] s, double[] e) {
//	System.out.println("\ns =" + s[0] + " " + s[1] + " " + s[2]);
//	System.out.println("e =" + e[0] + " " + e[1]);
//    }

    private static void update_v(int index, GMatrix v,
				 double[] cosr, double[] sinr) {
	int j;
	double vtemp;

	for (j = 0; j < v.nRow; j++) {
	    vtemp = v.values[j][index];
	    v.values[j][index] =
		cosr[0]*vtemp + sinr[0]*v.values[j][index+1];
	    v.values[j][index+1] =
	       -sinr[0]*vtemp + cosr[0]*v.values[j][index+1];
	}
    }

//    private static void chase_up(double[] s, double[] e, int k, GMatrix v) {
//	double f, g, r;
//	double[] cosr = new double[1];
//	double[] sinr = new double[1];
//	int i;
//	GMatrix t = new GMatrix(v.nRow, v.nCol);
//	GMatrix m = new GMatrix(v.nRow, v.nCol);
//
//	if (debug) {
//	    m.setIdentity();
//	    for (i = 0; i < s.length; i++) {
//		m.values[i][i] = s[i];
//	    }
//	    for (i = 0; i < e.length; i++) {
//		m.values[i][i+1] = e[i];
//	    }
//	}
//
//	f = e[k];
//	g = s[k];
//
//	for (i = k; i > 0; i--) {
//	    r = compute_rot(f, g, sinr, cosr);
//	    f = -e[i-1] * sinr[0];
//	    g = s[i-1];
//	    s[i] = r;
//	    e[i-1] = e[i-1] * cosr[0];
//	    update_v_split(i, k+1, v, cosr, sinr, t, m);
//	}
//
//	s[i+1] = compute_rot(f, g, sinr, cosr);
//	update_v_split(i, k+1, v, cosr, sinr, t, m);
//    }
//
//    private static void chase_across(double[] s, double[] e, int k, GMatrix u) {
//	double f, g, r;
//	double[] cosl = new double[1];
//	double[] sinl = new double[1];
//	int i;
//	GMatrix t = new GMatrix(u.nRow, u.nCol);
//	GMatrix m = new GMatrix(u.nRow, u.nCol);
//
//	if (debug) {
//	    m.setIdentity();
//	    for (i = 0; i < s.length; i++) {
//		m.values[i][i] = s[i];
//	    }
//	    for (i = 0; i < e.length; i++) {
//		m.values[i][i+1] = e[i];
//	    }
//	}
//
//	g = e[k];
//	f = s[k+1];
//
//	for (i = k; i < u.nCol-2; i++){
//	    r = compute_rot(f, g, sinl, cosl);
//	    g = -e[i+1] * sinl[0];
//	    f = s[i+2];
//	    s[i+1] = r;
//	    e[i+1] = e[i+1] * cosl[0];
//	    update_u_split(k, i + 1, u, cosl, sinl, t, m);
//	}
//
//	s[i+1] = compute_rot(f, g, sinl, cosl);
//	update_u_split(k, i + 1, u, cosl, sinl, t, m);
//    }

//    private static void update_v_split(int topr, int bottomr, GMatrix v,
//				       double[] cosr, double[] sinr,
//				       GMatrix t, GMatrix m) {
//	int j;
//	double vtemp;
//
//	for (j = 0; j < v.nRow; j++) {
//	    vtemp = v.values[j][topr];
//	    v.values[j][topr] = cosr[0]*vtemp - sinr[0]*v.values[j][bottomr];
//	    v.values[j][bottomr] = sinr[0]*vtemp + cosr[0]*v.values[j][bottomr];
//	}
//
//	if (debug) {
//	    t.setIdentity();
//	    for (j = 0; j < v.nRow; j++) {
//		vtemp = t.values[j][topr];
//		t.values[j][topr] =
//		    cosr[0]*vtemp - sinr[0]*t.values[j][bottomr];
//		t.values[j][bottomr] =
//		    sinr[0]*vtemp + cosr[0]*t.values[j][bottomr];
//	    }
//	}
//
//	System.out.println("topr    =" + topr);
//	System.out.println("bottomr =" + bottomr);
//	System.out.println("cosr =" + cosr[0]);
//	System.out.println("sinr =" + sinr[0]);
//	System.out.println("\nm =");
//	checkMatrix(m);
//	System.out.println("\nv =");
//	checkMatrix(t);
//	m.mul(m,t);
//	System.out.println("\nt*m =");
//	checkMatrix(m);
//    }
//
//    private static void update_u_split(int topr, int bottomr, GMatrix u,
//				       double[] cosl, double[] sinl,
//				       GMatrix t, GMatrix m) {
//	int j;
//	double utemp;
//
//	for (j = 0; j < u.nCol; j++) {
//	    utemp = u.values[topr][j];
//	    u.values[topr][j]    = cosl[0]*utemp - sinl[0]*u.values[bottomr][j];
//	    u.values[bottomr][j] = sinl[0]*utemp + cosl[0]*u.values[bottomr][j];
//	}
//
//	if(debug) {
//	    t.setIdentity();
//	    for (j = 0;j < u.nCol; j++) {
//		utemp = t.values[topr][j];
//		t.values[topr][j] =
//		    cosl[0]*utemp - sinl[0]*t.values[bottomr][j];
//		t.values[bottomr][j] =
//		    sinl[0]*utemp + cosl[0]*t.values[bottomr][j];
//	    }
//	}
//	System.out.println("\nm=");
//	checkMatrix(m);
//	System.out.println("\nu=");
//	checkMatrix(t);
//	m.mul(t,m);
//	System.out.println("\nt*m=");
//	checkMatrix(m);
//    }
//
    private static void update_u(int index, GMatrix u,
				 double[] cosl, double[] sinl) {
	int j;
	double utemp;

	for (j = 0; j < u.nCol; j++) {
	    utemp = u.values[index][j];
	    u.values[index][j] =
		cosl[0]*utemp + sinl[0]*u.values[index+1][j];
	    u.values[index+1][j] =
	       -sinl[0]*utemp + cosl[0]*u.values[index+1][j];
	}
    }

    private static void print_m(GMatrix m, GMatrix u, GMatrix v) {
	GMatrix mtmp = new GMatrix(m.nCol, m.nRow);

	mtmp.mul(u, mtmp);
	mtmp.mul(mtmp, v);
	System.out.println("\n m = \n" + GMatrix.toString(mtmp));

    }

    private static String toString(GMatrix m)
    {
	StringBuffer buffer = new StringBuffer(m.nRow * m.nCol * 8);
	int i, j;

	for (i = 0; i < m.nRow; i++) {
	    for(j = 0; j < m.nCol; j++) {
		if (Math.abs(m.values[i][j]) < .000000001) {
		    buffer.append("0.0000 ");
		} else {
		    buffer.append(m.values[i][j]).append(" ");
		}
	    }
	    buffer.append("\n");
	}
	return buffer.toString();
    }

    private static void print_svd(double[] s, double[] e,
				  GMatrix u, GMatrix v) {
	int i;
	GMatrix mtmp = new GMatrix(u.nCol, v.nRow);

	System.out.println(" \ns = ");
	for (i = 0; i < s.length; i++) {
	    System.out.println(" " + s[i]);
	}

	System.out.println(" \ne = ");
	for (i = 0; i < e.length; i++) {
	    System.out.println(" " + e[i]);
	}

	System.out.println(" \nu  = \n" + u.toString());
	System.out.println(" \nv  = \n" + v.toString());

	mtmp.setIdentity();
	for (i = 0; i < s.length; i++) {
	    mtmp.values[i][i] = s[i];
	}
	for (i = 0; i < e.length; i++) {
	    mtmp.values[i][i+1] = e[i];
	}
	System.out.println(" \nm  = \n"+mtmp.toString());

	mtmp.mulTransposeLeft(u, mtmp);
	mtmp.mulTransposeRight(mtmp, v);

	System.out.println(" \n u.transpose*m*v.transpose  = \n" +
			   mtmp.toString());
    }

    static double max(double a, double b) {
	if (a > b)
	    return a;
	else
	    return b;
    }

    static double min(double a, double b) {
	if (a < b)
	    return a;
	else
	    return b;
    }

    static double compute_shift(double f, double g, double h) {
	double d__1, d__2;
	double fhmn, fhmx, c, fa, ga, ha, as, at, au;
	double ssmin;

	fa = Math.abs(f);
	ga = Math.abs(g);
	ha = Math.abs(h);
	fhmn = min(fa,ha);
	fhmx = max(fa,ha);

	if (fhmn == 0.0) {
	    ssmin = 0.0;
	    if (fhmx == 0.0) {
	    } else {
		d__1 = min(fhmx,ga) / max(fhmx,ga);
	    }
	} else {
	    if (ga < fhmx) {
		as = fhmn / fhmx + 1.0;
		at = (fhmx - fhmn) / fhmx;
		d__1 = ga / fhmx;
		au = d__1 * d__1;
		c = 2.0 / (Math.sqrt(as * as + au) + Math.sqrt(at * at + au));
		ssmin = fhmn * c;
	    } else {
		au = fhmx / ga;
		if (au == 0.0) {
		    ssmin = fhmn * fhmx / ga;
		} else {
		    as = fhmn / fhmx + 1.0;
		    at = (fhmx - fhmn) / fhmx;
		    d__1 = as * au;
		    d__2 = at * au;
		    c = 1.0 / (Math.sqrt(d__1 * d__1 + 1.0) +
			       Math.sqrt(d__2 * d__2 + 1.0));
		    ssmin = fhmn * c * au;
		    ssmin += ssmin;
		}
	    }
	}

	return ssmin;
    }

    static int compute_2X2(double f, double g, double h,
			   double[] single_values, double[] snl, double[] csl,
			   double[] snr, double[] csr, int index) {

	double c_b3 = 2.0;
	double c_b4 = 1.0;

	double d__1;
	int pmax;
	double temp;
	boolean swap;
	double a, d, l, m, r, s, t, tsign, fa, ga, ha;
	double ft, gt, ht, mm;
	boolean gasmal;
	double tt, clt, crt, slt, srt;
	double ssmin,ssmax;

	ssmax = single_values[0];
	ssmin = single_values[1];
	clt = 0.0;
	crt = 0.0;
	slt = 0.0;
	srt = 0.0;
	tsign = 0.0;

	ft = f;
	fa = Math.abs(ft);
	ht = h;
	ha = Math.abs(h);

	pmax = 1;
	if (ha > fa)
	    swap = true;
	else
	    swap = false;

	if (swap) {
	    pmax = 3;
	    temp = ft;
	    ft = ht;
	    ht = temp;
	    temp = fa;
	    fa = ha;
	    ha = temp;

	}

	gt = g;
	ga = Math.abs(gt);
	if (ga == 0.0) {
	    single_values[1] = ha;
	    single_values[0] = fa;
	    clt = 1.0;
	    crt = 1.0;
	    slt = 0.0;
	    srt = 0.0;
	} else {
	    gasmal = true;
	    if (ga > fa) {
		pmax = 2;
		if (fa / ga < EPS) {
		    gasmal = false;
		    ssmax = ga;

		    if (ha > 1.0) {
			ssmin = fa / (ga / ha);
		    } else {
			ssmin = fa / ga * ha;
		    }
		    clt = 1.0;
		    slt = ht / gt;
		    srt = 1.0;
		    crt = ft / gt;
		}
	    }
	    if (gasmal) {
		d = fa - ha;
		if (d == fa) {

		    l = 1.0;
		} else {
		    l = d / fa;
		}

		m = gt / ft;
 		t = 2.0 - l;
 		mm = m * m;
		tt = t * t;
		s = Math.sqrt(tt + mm);

		if (l == 0.0) {
		    r = Math.abs(m);
		} else {
		    r = Math.sqrt(l * l + mm);
		}

		a = (s + r) * 0.5;
		if (ga > fa) {
		    pmax = 2;
		    if (fa / ga < EPS) {
			gasmal = false;
			ssmax = ga;
			if (ha > 1.0) {
			    ssmin = fa / (ga / ha);
			} else {
			    ssmin = fa / ga * ha;
			}
			clt = 1.0;
			slt = ht / gt;
			srt = 1.0;
			crt = ft / gt;
		    }
		}
		if (gasmal) {
		    d = fa - ha;
		    if (d == fa) {
			l = 1.0;
		    } else {
			l = d / fa;
		    }

		    m = gt / ft;
		    t = 2.0 - l;

		    mm = m * m;
		    tt = t * t;
		    s = Math.sqrt(tt + mm);

		    if (l == 0.) {
			r = Math.abs(m);
		    } else {
			r = Math.sqrt(l * l + mm);
		    }

		    a = (s + r) * 0.5;
		    ssmin = ha / a;
		    ssmax = fa * a;

		    if (mm == 0.0) {
			if (l == 0.0) {
			    t = d_sign(c_b3, ft) * d_sign(c_b4, gt);
			} else {
			    t = gt / d_sign(d, ft) + m / t;
			}
		    } else {
			t = (m / (s + t) + m / (r + l)) * (a + 1.0);
		    }

		    l = Math.sqrt(t * t + 4.0);
		    crt = 2.0 / l;
		    srt = t / l;
		    clt = (crt + srt * m) / a;
		    slt = ht / ft * srt / a;
		}
	    }
	    if (swap) {
		csl[0] = srt;
		snl[0] = crt;
		csr[0] = slt;
		snr[0] = clt;
	    } else {
		csl[0] = clt;
		snl[0] = slt;
		csr[0] = crt;
		snr[0] = srt;
	    }

	    if (pmax == 1) {
		tsign = d_sign(c_b4, csr[0]) *
		    d_sign(c_b4, csl[0]) * d_sign(c_b4, f);
	    }
	    if (pmax == 2) {
		tsign = d_sign(c_b4, snr[0]) *
		    d_sign(c_b4, csl[0]) * d_sign(c_b4, g);
	    }
	    if (pmax == 3) {
		tsign = d_sign(c_b4, snr[0]) *
		    d_sign(c_b4, snl[0]) * d_sign(c_b4, h);
	    }

	    single_values[index] = d_sign(ssmax, tsign);
	    d__1 = tsign * d_sign(c_b4, f) * d_sign(c_b4, h);
	    single_values[index+1] = d_sign(ssmin, d__1);
	}

	return 0;
    }

    static double compute_rot(double f, double g, double[] sin, double[] cos) {
	double cs, sn;
	int i;
	double scale;
	int count;
	double f1, g1;
	double r;
	final double safmn2 = 2.002083095183101E-146;
	final double safmx2 = 4.994797680505588E+145;

	if (g == 0.0) {
	    cs = 1.0;
	    sn = 0.0;
	    r = f;
	} else if (f == 0.0) {
	    cs = 0.0;
	    sn = 1.0;
	    r = g;
	} else {
	    f1 = f;
	    g1 = g;
	    scale = max(Math.abs(f1),Math.abs(g1));
	    if (scale >= safmx2) {
		count = 0;
		while(scale >= safmx2) {
		    ++count;
		    f1 *= safmn2;
		    g1 *= safmn2;
		    scale = max(Math.abs(f1), Math.abs(g1));
		}
		r = Math.sqrt(f1*f1 + g1*g1);
		cs = f1 / r;
		sn = g1 / r;
		for (i = 1; i <= count; ++i) {
		    r *= safmx2;
		}
	    } else if (scale <= safmn2) {
		count = 0;
		while(scale <= safmn2) {
		    ++count;
		    f1 *= safmx2;
		    g1 *= safmx2;
		    scale = max(Math.abs(f1), Math.abs(g1));
		}
		r = Math.sqrt(f1*f1 + g1*g1);
		cs = f1 / r;
		sn = g1 / r;
		for (i = 1; i <= count; ++i) {
		    r *= safmn2;
		}
	    } else {
		r = Math.sqrt(f1*f1 + g1*g1);
		cs = f1 / r;
		sn = g1 / r;
	    }
	    if (Math.abs(f) > Math.abs(g) && cs < 0.0) {
		cs = -cs;
		sn = -sn;
		r = -r;
	    }
	}
	sin[0] = sn;
	cos[0] = cs;
	return r;
    }

    static double d_sign(double a, double b) {
	double x;
	x = (a >= 0 ? a : - a);
	return (b >= 0 ? x : -x);
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
	GMatrix m1 = null;
	try {
	    m1 = (GMatrix)super.clone();
	} catch (CloneNotSupportedException e) {
	    // this shouldn't happen, since we are Cloneable
	    throw new InternalError();
	}

	// Also need to clone array of values
        m1.values = new double[nRow][nCol];
	for (int i = 0; i < nRow; i++) {
	   for(int j = 0; j < nCol; j++) {
	       m1.values[i][j] = values[i][j];
	   }
	}

	return m1;
    }

}
