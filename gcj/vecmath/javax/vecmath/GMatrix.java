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
 * A double precision, general, real, and dynamically resizeable 
 * two dimensional N x M matrix class. Row and column numbering 
 * begins with zero. The representation is row major. 
 * @version specification 1.1, implementation $Revision$, $Date$
 * @author Kenji hiranabe
 */
public class GMatrix implements Serializable {
/*
 * $Log$
 * Revision 1.1  2002/08/22 20:01:14  egonw
 * Lots of new files. Amongst which the source code of vecmath.jar.
 * The latter has been changed to compile with gcj-3.0.4.
 * Actually, CDK does now compile, i.e. at least the classes mentioned
 * in core.classes and extra.classes. *And* a binary executable can get
 * generated that works!
 *
 * Revision 1.13  1999/11/25  10:30:23  hiranabe
 * get(GMatrix) bug
 *
 * Revision 1.13  1999/11/25  10:30:23  hiranabe
 * get(GMatrix) bug
 *
 * Revision 1.12  1999/10/05  07:03:50  hiranabe
 * copyright change
 *
 * Revision 1.11  1999/03/04  09:16:33  hiranabe
 * small bug fix and copyright change
 *
 * Revision 1.10  1998/10/22  01:41:07  hiranabe
 * add(GMatrix, GMatrix) method bug reported by kaneta@elelab.nsc.co.jp
 *
 * Revision 1.9  1998/10/21  00:18:44  hiranabe
 * GMatrix#mulTransposeRight bug
 * thanks > kaneta@elelab.nsc.co.jp
 *
 * Revision 1.8  1998/10/14  00:49:10  hiranabe
 * API1.1 Beta02
 *
 * Revision 1.7  1998/07/27  04:28:13  hiranabe
 * API1.1Alpha01 ->API1.1Alpha03
 *
 * Revision 1.6  1998/04/17  10:30:46  hiranabe
 * null check for equals
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
 * added rank calculation to SVD
 * toString   '\n' -> "line.separator"
 *
 * Revision 1.1  1997/11/26  03:00:44  hiranabe
 * Initial revision
 *
 */

    /*
     * Implementation note:
     * The size of the matrix does NOT automaticly grow.
     * It does only when setSize is called. I believe this is the spec
     * and makes less confusion, less user bugs.
     */

    /**
     * The data of the GMatrix.(1D array. The (i,j) element is stored in elementData[i*nCol + j])
     */
    private double elementData[];

    /**
     * The number of rows in this matrix.
     */
    private int nRow;

    /**
     * The number of columns in this matrix.
     */
    private int nCol;

    /**
     * Constructs an nRow by nCol identity matrix. 
     * Note that even though row and column numbering begins with
     * zero, nRow and nCol will be one larger than the maximum
     * possible matrix index values.
     * @param nRow number of rows in this matrix.
     * @param nCol number of columns in this matrix.
     */
    public GMatrix(int nRow, int nCol) {
	if (nRow < 0)
	    throw new NegativeArraySizeException(nRow + " < 0");
	if (nCol < 0)
	    throw new NegativeArraySizeException(nCol + " < 0");

	this.nRow = nRow;
	this.nCol = nCol;
	elementData = new double[nRow*nCol];
	setIdentity();
    }
  
    /**
     * Constructs an nRow by nCol matrix initialized to the values 
     * in the matrix array.  The array values are copied in one row at
     * a time in row major fashion.  The array should be at least 
     * nRow*nCol in length.
     * Note that even though row and column numbering begins with 
     * zero, nRow and nCol will be one larger than the maximum
     * possible matrix index values.
     * @param nRow number of rows in this matrix.
     * @param nCol number of columns in this matrix.
     * @param matrix a 1D array that specifies a matrix in row major fashion
     */
    public GMatrix(int nRow, int nCol, double matrix[]) {
	if (nRow < 0)
	    throw new NegativeArraySizeException(nRow + " < 0");
	if (nCol < 0)
	    throw new NegativeArraySizeException(nCol + " < 0");

	this.nRow = nRow;
	this.nCol = nCol;
	this.elementData = new double[nRow*nCol];
	set(matrix);
    }

    /**
     * Constructs a new GMatrix and copies the initial values
     * from the parameter matrix.
     * @param matrix the source of the initial values of the new GMatrix
     */
    public GMatrix(GMatrix matrix) {
	this.nRow = matrix.nRow;
	this.nCol = matrix.nCol;
	int newSize = nRow*nCol;
	this.elementData = new double[newSize];
	System.arraycopy(matrix.elementData, 0, elementData, 0, newSize);
    }

    /**
     * Sets the value of this matrix to the result of multiplying itself
     * with matrix m1 (this = this * m1). 
     * @param m1 the other matrix
     */
    public final void mul(GMatrix m1) {
	// alias-safe.
	mul(this, m1);
    }

    /**
     * Sets the value of this matrix to the result of multiplying
     * the two argument matrices together (this = m1 * m2).
     * @param m1 the first matrix
     * @param m2 the second matrix
     */
    public final void mul(GMatrix m1, GMatrix m2) {
	// for alias-safety, decided to new double [nCol*nRow].
	// Is there any good way to avoid this big new ?
	if( nRow != m1.nRow )
	    throw new ArrayIndexOutOfBoundsException(
		"nRow:" + nRow + " != m1.nRow:" + m1.nRow);
	if( nCol != m2.nCol )
	    throw new ArrayIndexOutOfBoundsException(
		"nCol:" + nCol + " != m2.nCol:" + m2.nCol);
	if( m1.nCol != m2.nRow )
	    throw new ArrayIndexOutOfBoundsException(
		"m1.nCol:" + m1.nCol + " != m2.nRow:" + m2.nRow);

	double [] newData = new double [nCol*nRow];
	for(int i = 0; i < nRow; i++) {
	    for(int j = 0; j < nCol; j++) {
		double sum = 0.0;
		for(int k = 0; k < m1.nCol; k++)
		    sum += m1.elementData[i*m1.nCol + k] * m2.elementData[k*m2.nCol + j];
		newData[i*nCol + j] = sum;
	    }
	}
	elementData = newData;
    }
  
    /**
     * Computes the outer product of the two vectors; multiplies the 
     * the first vector by the transpose of the second vector
     * and places the matrix result into this matrix. This matrix must
     * be as big or bigger than getSize(v1)xgetSize(v2).
     * @param v1 the first vector, treated as a row vector 
     * @param v2 the second vector, treated as a column vector
     */
    public final void mul(GVector v1, GVector v2) {
	if (nRow < v1.getSize())
	    throw new IllegalArgumentException(
		"nRow:"+nRow+" < v1.getSize():" +v1.getSize());
	if (nCol < v2.getSize())
	    throw new IllegalArgumentException(
		"nCol:"+nCol+" < v2.getSize():" +v2.getSize());

	for (int i = 0; i < nRow; i++)
	    for (int j = 0; j < nCol; j++)
		elementData[i*nCol + j] = v1.getElement(i)*v2.getElement(j);
    }        
  
    /**
     * Sets the value of this matrix to sum of itself and matrix m1.
     * @param m1 the other matrix
     */
    public final void add(GMatrix m1) {
	if (nRow != m1.nRow || nCol != m1.nCol)
	    throw new IllegalArgumentException("this:(" +nRow+"x"+nCol+") != m1:("+m1.nRow+"x"+m1.nCol+").");

	for(int i = 0; i < nRow*nCol; i++)
	    elementData[i] += m1.elementData[i];
    }
  
    /**
     * Sets the value of this matrix to the matrix sum of matrices m1 and m2.
     * @param m1 the first matrix
     * @param m2 the second matrix
     */
    public final void add(GMatrix m1, GMatrix m2) {
	if (nRow != m1.nRow || nCol != m1.nCol)
	    throw new IllegalArgumentException("this:(" +nRow+"x"+nCol+") != m1:("+m1.nRow+"x"+m1.nCol+").");
	if (nRow != m2.nRow || nCol != m2.nCol)
	    throw new IllegalArgumentException("this:(" +nRow+"x"+nCol+") != m2:("+m2.nRow+"x"+m2.nCol+").");

	for(int i = 0; i < nRow*nCol; i++)
	    elementData[i] = m1.elementData[i] + m2.elementData[i];
    }

    /**
     * Sets the value of this matrix to the matrix difference of itself
     * and matrix m1 (this = this - m1).
     * @param m1 the other matrix
     */
    public final void sub(GMatrix m1) {
	if (nRow != m1.nRow || nCol != m1.nCol)
	    throw new IllegalArgumentException("this:(" +nRow+"x"+nCol+") != m1:("+m1.nRow+"x"+m1.nCol+").");
	for (int i = 0; i < nRow*nCol; i++)
		elementData[i] -= m1.elementData[i];
    }

    /**
     * Sets the value of this matrix to the matrix difference
     * of matrices m1 and m2 (this = m1 - m2).
     * @param m1 the first matrix
     * @param m2 the second matrix
     */
    public final void sub(GMatrix m1, GMatrix m2) {
	if (nRow != m1.nRow || nCol != m1.nCol)
	    throw new IllegalArgumentException("this:(" +nRow+"x"+nCol+") != m1:("+m1.nRow+"x"+m1.nCol+").");
	if (nRow != m2.nRow || nCol != m2.nCol)
	    throw new IllegalArgumentException("this:(" +nRow+"x"+nCol+") != m2:("+m2.nRow+"x"+m2.nCol+").");

	for(int i = 0; i < nRow*nCol; i++)
	    elementData[i] = m1.elementData[i] - m2.elementData[i];
    }

    /**
     * Negates the value of this matrix: this = -this.
     */
    public final void negate() {
	for (int i = 0; i < nRow*nCol; i++)
		elementData[i] = -elementData[i];
    }

    /**
     * Sets the value of this matrix to the negation of the GMatrix parameter.
     * @param m1 The source matrix
     */
    public final void negate(GMatrix m1) {
	set(m1);
	negate();
    }

    /**
     * Sets this GMatrix to the identity matrix.
     */
    public final void setIdentity() {
	setZero();
	int min = nRow < nCol ? nRow : nCol;
	for (int i = 0; i < min; i++)
	    elementData[i*nCol + i] = 1.0;
    }
  
    /**
     * Sets all the values in this matrix to zero.
     */
    public final void setZero() {
	for (int i = 0; i < nRow*nCol; i++)
	    elementData[i] = 0.0;
    }

    /**
     * Subtracts this matrix from the identity matrix and puts the values
     * back into this (this = I - this).
     */
    public final void identityMinus() {
	negate();
	int min = nRow < nCol ? nRow : nCol;
	for (int i = 0; i < min; i++)
	    elementData[i*nCol + i] += 1.0;
    }

    /**
     * Inverts this matrix in place. 
     */
    public final void invert() {
	if (nRow != nCol)
	    throw new ArrayIndexOutOfBoundsException("not a square matrix");
	int n = nRow;

	GMatrix LU = new GMatrix(n, n);
	GVector permutation = new GVector(n);
	GVector column = new GVector(n);
	GVector unit = new GVector(n);
	LUD(LU, permutation);
	for (int j = 0; j < n; j++) {
	    unit.zero();
	    unit.setElement(j, 1.0);
	    column.LUDBackSolve(LU, unit, permutation);
	    setColumn(j, column);
	}
    }

    /**
     * Inverts matrix m1 and places the new values into this matrix.  Matrix
     * m1 is not modified. 
     * @param m1 the matrix to be inverted
     */
    public final void invert(GMatrix m1)  {
	set(m1);
	invert();
    }

    /**
     * Copies a sub-matrix derived from this matrix into the target matrix.
     * The upper left of the sub-matrix is located at (rowSource, colSource);
     * the lower right of the sub-matrix is located at 
     * (lastRowSource,lastColSource).  The sub-matrix is copied into the
     * the target matrix starting at (rowDest, colDest).
     * @param rowSource the top-most row of the sub-matrix
     * @param colSource the left-most column of the sub-matrix
     * @param numRow the number of rows in the sub-matrix
     * @param numCol the number of columns in the sub-matrix
     * @param rowDest the top-most row of the position of the copied sub-matrix
     *                  within the target matrix
     * @param colDest the left-most column of the position of the copied sub-matrix
     *                  within the target matrix
     * @param target the matrix into which the sub-matrix will be copied
     */
    public final void copySubMatrix(int rowSource, int colSource, int numRow, int numCol,
				    int rowDest, int colDest, GMatrix target) {
	if (rowSource < 0 || colSource < 0 || rowDest < 0 || colDest < 0)
	    throw new ArrayIndexOutOfBoundsException(
		"rowSource,colSource,rowDest,colDest < 0.");
	else if (nRow < numRow + rowSource || nCol < numCol + colSource)
	    throw new ArrayIndexOutOfBoundsException("Source GMatrix too small.");
	else if (target.nRow < numRow + rowDest || target.nCol < numCol + colDest)
	    throw new ArrayIndexOutOfBoundsException("Target GMatrix too small.");

	for(int i = 0; i < numRow; i++)
	    for(int j = 0; j < numCol; j++)
		target.elementData[(i+rowDest)*nCol + (j+colDest)] = elementData[(i+rowSource)*nCol + (j+colSource)];
    }
  
    /**
     * Changes the size of this matrix dynamically.  If the size is increased
     * no data values will be lost.  If the size is decreased, only those data
     * values whose matrix positions were eliminated will be lost.
     *
     * @param nRow number of desired rows in this matrix
     * @param nCol number of desired columns in this matrix
     */
    public final void setSize(int nRow, int nCol)  {
	if (nRow < 0 || nCol < 0)
	    throw new NegativeArraySizeException("nRow or nCol < 0");

	int oldnRow = this.nRow;
	int oldnCol = this.nCol;
	int oldSize = this.nRow*this.nCol;
	this.nRow = nRow;
	this.nCol = nCol;
	int newSize = nRow*nCol;
	double [] oldData = elementData;

	if (oldnCol == nCol) {
	    // no need to reload elements.
	    if (nRow <= oldnRow)
		return;	

	    // have to allocate memory.
	    elementData = new double [newSize];

	    // copy the old data.
	    System.arraycopy(oldData, 0, elementData, 0, oldSize);

	    // no need to pad. 0.0 is automaticly padded.
	    // for (int i = oldSize; i < newSize; i++)
	    //     elementData[i] = 0.0;

	} else {
	    elementData = new double [newSize];
	    setZero();
	    for (int i = 0; i < oldnRow; i++)
		System.arraycopy(oldData, i*oldnCol, elementData, i*nCol, oldnCol);
	}
    }

    /**
     * Sets the value of this matrix to the values found in the array parameter.
     * The values are copied in one row at a time, in row major 
     * fashion.  The array should be at least equal in length to
     * the number of matrix rows times the number of matrix columns
     * in this matrix.
     * @param matrix the row major source array
     */
    public final void set(double matrix[]) {
	int size = nRow*nCol;
	System.arraycopy(matrix, 0, elementData, 0, size);
    }
  
    /**
     * Sets the value of this matrix to that of the Matrix3f provided. 
     * @param m1 the source matrix
     */
    public final void set(Matrix3f m1) {
	// This implementation is in 'no automatic size grow' policy.
	// When size mismatch, exception will be thrown from the below.
	elementData[0] = m1.m00;
	elementData[1] = m1.m01;
	elementData[2] = m1.m02;
	elementData[nCol] = m1.m10;
	elementData[nCol + 1] = m1.m11;
	elementData[nCol + 2] = m1.m12;
	elementData[2*nCol] = m1.m20;
	elementData[2*nCol + 1] = m1.m21;
	elementData[2*nCol + 2] = m1.m22;
    }

    /**
     * Sets the value of this matrix to that of the Matrix3d provided. 
     * @param m1 the source matrix
     */
    public final void set(Matrix3d m1) {
	// This implementation is in 'no automatic size grow' policy.
	// When size mismatch, exception will be thrown from the below.
	elementData[0] = m1.m00;
	elementData[1] = m1.m01;
	elementData[2] = m1.m02;
	elementData[nCol] = m1.m10;
	elementData[nCol + 1] = m1.m11;
	elementData[nCol + 2] = m1.m12;
	elementData[2*nCol] = m1.m20;
	elementData[2*nCol + 1] = m1.m21;
	elementData[2*nCol + 2] = m1.m22;
    }

    /**
     * Sets the value of this matrix to that of the Matrix4f provided. 
     * @param m1 the source matrix
     */
    public final void set(Matrix4f m1) {
	// This implementation is in 'no automatic size grow' policy.
	// When size mismatch, exception will be thrown from the below.
	elementData[0] = m1.m00;
	elementData[1] = m1.m01;
	elementData[2] = m1.m02;
	elementData[3] = m1.m03;
	elementData[nCol] = m1.m10;
	elementData[nCol + 1] = m1.m11;
	elementData[nCol + 2] = m1.m12;
	elementData[nCol + 3] = m1.m13;
	elementData[2*nCol] = m1.m20;
	elementData[2*nCol + 1] = m1.m21;
	elementData[2*nCol + 2] = m1.m22;
	elementData[2*nCol + 3] = m1.m23;
	elementData[3*nCol] = m1.m30;
	elementData[3*nCol + 1] = m1.m31;
	elementData[3*nCol + 2] = m1.m32;
	elementData[3*nCol + 3] = m1.m33;
    }

    /**
     * Sets the value of this matrix to that of the Matrix4d provided. 
     * @param m1 the source matrix
     */
    public final void set(Matrix4d m1) {
	// This implementation is in 'no automatic size grow' policy.
	// When size mismatch, exception will be thrown from the below.
	elementData[0] = m1.m00;
	elementData[1] = m1.m01;
	elementData[2] = m1.m02;
	elementData[3] = m1.m03;
	elementData[nCol] = m1.m10;
	elementData[nCol + 1] = m1.m11;
	elementData[nCol + 2] = m1.m12;
	elementData[nCol + 3] = m1.m13;
	elementData[2*nCol] = m1.m20;
	elementData[2*nCol + 1] = m1.m21;
	elementData[2*nCol + 2] = m1.m22;
	elementData[2*nCol + 3] = m1.m23;
	elementData[3*nCol] = m1.m30;
	elementData[3*nCol + 1] = m1.m31;
	elementData[3*nCol + 2] = m1.m32;
	elementData[3*nCol + 3] = m1.m33;
    }


    /**
     * Sets the value of this matrix to the values found in matrix m1.
     * @param m1 the source matrix
     */
    public final void set(GMatrix m1) {
	// This implementation is in 'no automatic size grow' policy.
	// When size mismatch, exception will be thrown from the below.
	if (m1.nRow < nRow || m1.nCol < nCol)
	    throw new ArrayIndexOutOfBoundsException("m1 smaller than this matrix");
	System.arraycopy(m1.elementData, 0, elementData, 0, nRow*nCol);
    }


    /**
     * Returns the number of rows in this matrix.
     * @return number of rows in this matrix
     */
    public final int getNumRow() {
	return nRow;
    }

    /**
     * Returns the number of colmuns in this matrix.
     * @return number of columns in this matrix
     */
    public final int getNumCol() {
	return nCol;
    }
  
    /**
     * Retrieves the value at the specified row and column of this matrix.
     * @param row the row number to be retrieved (zero indexed)
     * @param column the column number to be retrieved (zero indexed)
     * @return the value at the indexed element
     */
    public final double getElement(int row, int column) {
	if (nRow <= row)
	    throw new ArrayIndexOutOfBoundsException("row:"+row+" > matrix's nRow:"+nRow);
	if (row < 0)
	    throw new ArrayIndexOutOfBoundsException("row:"+row+" < 0");
	if (nCol <= column)
	    throw new ArrayIndexOutOfBoundsException("column:"+column+" > matrix's nCol:"+nCol);
	if (column < 0)
	    throw new ArrayIndexOutOfBoundsException("column:"+column+" < 0");

	return elementData[row*nCol + column];
    }

    /**
     * Modifies the value at the specified row and column of this matrix.
     * @param row the row number to be modified (zero indexed)
     * @param column the column number to be modified (zero indexed)
     * @param value the new matrix element value
     */
    public final void setElement(int row, int column, double value) {
	if (nRow <= row)
	    throw new ArrayIndexOutOfBoundsException("row:"+row+" > matrix's nRow:"+nRow);
	if (row < 0)
	    throw new ArrayIndexOutOfBoundsException("row:"+row+" < 0");
	if (nCol <= column)
	    throw new ArrayIndexOutOfBoundsException("column:"+column+" > matrix's nCol:"+nCol);
	if (column < 0)
	    throw new ArrayIndexOutOfBoundsException("column:"+column+" < 0");

	elementData[row*nCol + column] = value;
    }

    /**
     * Places the values of the specified row into the array parameter. 
     * @param row the target row number
     * @param array the array into which the row values will be placed
     */
    public final void getRow(int row, double array[]) {
	if (nRow <= row)
	    throw new ArrayIndexOutOfBoundsException("row:"+row+" > matrix's nRow:"+nRow);
	if (row < 0)
	    throw new ArrayIndexOutOfBoundsException("row:"+row+" < 0");
	if (array.length < nCol)
	    throw new ArrayIndexOutOfBoundsException("array length:"+array.length+" smaller than matrix's nCol:"+nCol);

	System.arraycopy(elementData, row*nCol, array, 0, nCol);
    }

    /**
     * Places the values of the specified row into the vector parameter.
     * @param row the target row number
     * @param vector the vector into which the row values will be placed
     */
    public final void getRow(int row, GVector vector) {
	if (nRow <= row)
	    throw new ArrayIndexOutOfBoundsException("row:"+row+" > matrix's nRow:"+nRow);
	if (row < 0)
	    throw new ArrayIndexOutOfBoundsException("row:"+row+" < 0");
	if (vector.getSize() < nCol)
	    throw new ArrayIndexOutOfBoundsException("vector size:"+vector.getSize()+" smaller than matrix's nCol:"+nCol);

	for (int i = 0; i < nCol; i++) {
	    vector.setElement(i, elementData[row*nCol + i]);

	    // if may use package friendly accessibility, would do;
	    //	System.arraycopy(elementData, row*nCol ,vector.elementData, 0, nCol);
	}
    }

    /**
     * Places the values of the specified column into the array parameter. 
     * @param col the target column number
     * @param array the array into which the column values will be placed
     */
    public final void getColumn(int col, double array[]) {
	if (nCol <= col)
	    throw new ArrayIndexOutOfBoundsException("col:"+col+" > matrix's nCol:"+nCol);
	if (col < 0)
	    throw new ArrayIndexOutOfBoundsException("col:"+col+" < 0");
	if (array.length < nRow)
	    throw new ArrayIndexOutOfBoundsException("array.length:"+array.length+" < matrix's nRow="+nRow);

	for (int i = 0; i < nRow; i++)
	    array[i] = elementData[i*nCol + col];
    }

    /**
     * Places the values of the specified column into the vector parameter.
     * @param col the target column number
     * @param vector the vector into which the column values will be placed
     */
    public final void getColumn(int col, GVector vector) {
	if (nCol <= col)
	    throw new ArrayIndexOutOfBoundsException("col:"+col+" > matrix's nCol:"+nCol);
	if (col < 0)
	    throw new ArrayIndexOutOfBoundsException("col:"+col+" < 0");
	if (vector.getSize() < nRow)
	    throw new ArrayIndexOutOfBoundsException("vector size:"+vector.getSize()+" < matrix's nRow:"+nRow);
	for (int i = 0; i < nRow; i++) {
	    vector.setElement(i, elementData[i*nCol + col]);
	}
    }
 
    /**
     * Places the values in the upper 3X3 of this GMatrix into the matrix m1.
     * @param m1 The matrix that will hold the new values 
     */
    public final void get(Matrix3d m1) {
	m1.m00 = elementData[0];
	m1.m01 = elementData[1];
	m1.m02 = elementData[2];
	m1.m10 = elementData[nCol];
	m1.m11 = elementData[nCol + 1];
	m1.m12 = elementData[nCol + 2];
	m1.m20 = elementData[2*nCol];
	m1.m21 = elementData[2*nCol + 1];
	m1.m22 = elementData[2*nCol + 2];
    }

    /**
     * Places the values in the upper 3X3 of this GMatrix into the matrix m1. 
     * @param m1 The matrix that will hold the new values 
     */
    public final void get(Matrix3f m1) {
	m1.m00 = (float)elementData[0];
	m1.m01 = (float)elementData[1];
	m1.m02 = (float)elementData[2];
	m1.m10 = (float)elementData[nCol];
	m1.m11 = (float)elementData[nCol + 1];
	m1.m12 = (float)elementData[nCol + 2];
	m1.m20 = (float)elementData[2*nCol];
	m1.m21 = (float)elementData[2*nCol + 1];
	m1.m22 = (float)elementData[2*nCol + 2];
    }

    /**
     * Places the values in the upper 4X4 of this GMatrix into the matrix m1.
     *
     * @param m1 The matrix that will hold the new values 
     */
    public final void get(Matrix4d m1) {
	m1.m00 = elementData[0];
	m1.m01 = elementData[1];
	m1.m02 = elementData[2];
	m1.m03 = elementData[3];
	m1.m10 = elementData[nCol];
	m1.m11 = elementData[nCol + 1];
	m1.m12 = elementData[nCol + 2];
	m1.m13 = elementData[nCol + 3];
	m1.m20 = elementData[2*nCol];
	m1.m21 = elementData[2*nCol + 1];
	m1.m22 = elementData[2*nCol + 2];
	m1.m23 = elementData[2*nCol + 3];
	m1.m30 = elementData[3*nCol];
	m1.m31 = elementData[3*nCol + 1];
	m1.m32 = elementData[3*nCol + 2];
	m1.m33 = elementData[3*nCol + 3];
    }

    /**
     * Places the values in the upper 4X4 of this GMatrix into the matrix m1.
     *
     * @param m1 The matrix that will hold the new values 
     */
    public final void get(Matrix4f m1) {
	m1.m00 = (float)elementData[0];
	m1.m01 = (float)elementData[1];
	m1.m02 = (float)elementData[2];
	m1.m03 = (float)elementData[3];
	m1.m10 = (float)elementData[nCol];
	m1.m11 = (float)elementData[nCol + 1];
	m1.m12 = (float)elementData[nCol + 2];
	m1.m13 = (float)elementData[nCol + 3];
	m1.m20 = (float)elementData[2*nCol];
	m1.m21 = (float)elementData[2*nCol + 1];
	m1.m22 = (float)elementData[2*nCol + 2];
	m1.m23 = (float)elementData[2*nCol + 3];
	m1.m30 = (float)elementData[3*nCol];
	m1.m31 = (float)elementData[3*nCol + 1];
	m1.m32 = (float)elementData[3*nCol + 2];
	m1.m33 = (float)elementData[3*nCol + 3];
    }

    /**
     * Places the values in the this matrix into the matrix m1; m1
     * should be at least as large as this GMatrix.
     * @param m1 The matrix that will hold the new values 
     */
    public final void get(GMatrix m1) {
	// spec does not completely mirrors set(GMatrix).
	// need error check.

	if (m1.nRow < nRow || m1.nCol < nCol)
	    throw new IllegalArgumentException(
		"m1 matrix is smaller than this matrix.");

    if (m1.nCol == nCol) {
        System.arraycopy(elementData, 0, m1.elementData, 0, nRow*nCol);
    } else {
        for (int i = 0; i < nRow; i++) {
            System.arraycopy(elementData, i*nCol , m1.elementData, i*m1.nCol, nCol);
        }
    }
    }

    /**
     * Copy the values from the array into the specified row of this
     * matrix.
     * @param row the row of this matrix into which the array values
     *             will be copied.
     * @param array the source array
     */
    public final void setRow(int row,  double array[]) {
	if (nRow <= row)
	    throw new ArrayIndexOutOfBoundsException("row:"+row+" > matrix's nRow:"+nRow);
	if (row < 0)
	    throw new ArrayIndexOutOfBoundsException("row:"+row+" < 0");
	if (array.length < nCol)
	    throw new ArrayIndexOutOfBoundsException("array length:"+array.length+" < matrix's nCol="+nCol);

	System.arraycopy(array, 0, elementData, row*nCol, nCol);
    }

    /**
     * Copy the values from the array into the specified row of this
     * matrix. 
     * @param row the row of this matrix into which the vector values
     *             will be copied.
     * @param vector the source vector
     */
    public final void setRow(int row,  GVector vector) {
	if (nRow <= row)
	    throw new ArrayIndexOutOfBoundsException("row:"+row+" > matrix's nRow:"+nRow);
	if (row < 0)
	    throw new ArrayIndexOutOfBoundsException("row:"+row+" < 0");
	int vecSize = vector.getSize();
	if (vecSize < nCol)
	    throw new ArrayIndexOutOfBoundsException("vector's size:"+vecSize+" < matrix's nCol="+nCol);

	for (int i = 0; i < nCol; i++) {
	    elementData[row*nCol + i] = vector.getElement(i);
	    // if may use package friendly accessibility, would do;
	    // System.arraycopy(vector.elementData, 0, elementData, row*nCol, nCol);
	}
    }

    /**
     * Copy the values from the array into the specified column of this
     * matrix. 
     * @param row the column of this matrix into which the array values
     * will be copied.
     * @param array the source array
     */
    public final void setColumn(int col,  double array[]) {
	if (nCol <= col)
	    throw new ArrayIndexOutOfBoundsException("col:"+col+" > matrix's nCol="+nCol);
	if (col < 0)
	    throw new ArrayIndexOutOfBoundsException("col:"+col+" < 0");

    	if (array.length < nRow)
	    throw new ArrayIndexOutOfBoundsException("array length:"+array.length+" < matrix's nRow:"+nRow);
	for (int i = 0; i < nRow; i++)
	    elementData[i*nCol + col] = array[i];
    }

    /**
     * Copy the values from the array into the specified column of this
     * matrix.
     *
     * @param row the column of this matrix into which the vector values
     * will be copied.
     * @param vector the source vector
     */
    public final void setColumn(int col, GVector vector) {
	if (nCol <= col)
	    throw new ArrayIndexOutOfBoundsException("col:"+col+" > matrix's nCol="+nCol);
	if (col < 0)
	    throw new ArrayIndexOutOfBoundsException("col:"+col+" < 0");

	int vecSize = vector.getSize();
    	if (vecSize < nRow)
	    throw new ArrayIndexOutOfBoundsException("vector size:"+vecSize+" < matrix's nRow="+nRow);
	for (int i = 0; i < nRow; i++)
	    elementData[i*nCol + col] = vector.getElement(i);
    }

    /**
     * Multiplies the transpose of matrix m1 times the transpose of matrix m2, and places the
     * result into this.
     * @param m1 The matrix on the left hand side of the multiplication
     * @param m2 The matrix on the right hand side of the multiplication
     */
    public final void mulTransposeBoth(GMatrix m1, GMatrix m2) {
	mul(m2, m1);
	transpose();
    }

    /**
     * Multiplies matrix m1 times the transpose of matrix m2, and places the
     * result into this. 
     */
    public final void mulTransposeRight(GMatrix m1, GMatrix m2) {
	if (m1.nCol != m2.nCol || nRow != m1.nRow || nCol != m2.nRow)
	    throw new ArrayIndexOutOfBoundsException("matrices mismatch");

	for (int i = 0; i < nRow; i++) {
	    for (int j = 0; j < nCol; j++) {
		double sum = 0.0;
		for (int k = 0; k < m1.nCol; k++)
		    sum += m1.elementData[i*m1.nCol + k]*m2.elementData[j*m2.nCol + k];
		this.elementData[i*nCol + j] = sum;
	    }
	}
    }

    /**
     * Multiplies the transpose of matrix m1 times the matrix m2, and places the
     * result into this.
     * @param m1 The matrix on the left hand side of the multiplication
     * @param m2 The matrix on the right hand side of the multiplication
     */
    public final void mulTransposeLeft(GMatrix m1, GMatrix m2) {
	transpose(m1);
	mul(m2);
    }

    /**
     * Transposes this matrix in place.
     */
    public final void transpose() {
	for(int i = 0; i < nRow; i++)
	    for(int j = i + 1; j < nCol; j++) {  // note j starts from i+1
		double tmp = elementData[i*nCol + j];
		elementData[i*nCol + j] = elementData[j*nCol + i];
		elementData[j*nCol + i] = tmp;
	    }
    }

    /**
     * Places the matrix values of the transpose of matrix m1 into this matrix. <p>
     * @param m1 the matrix to be transposed (but not modified)
     */
    public final void transpose(GMatrix m1) {
	set(m1);
	transpose();
    }

    /**
     * Returns a string that contains the values of this GMatrix.
     * @return the String representation
     */
    public String toString() {
	String nl = System.getProperty("line.separator"); 
	StringBuffer out = new StringBuffer("[");
	out.append(nl);

	for (int i = 0; i < nRow; i++) {
	    out.append("  [");
	    for (int j = 0; j < nCol; j++) {
		if (0 < j)
		    out.append("\t");
		out.append(elementData[i*nCol + j]);
	    }
	    if(i+1 < nRow) {
		out.append("]");
		out.append(nl);
	    } else {
		out.append("] ]");
	    }
	}
	return out.toString();
    }

    /**
     * Returns a hash number based on the data values in this
     * object.  Two different GMatrix objects with identical data values
     * (ie, returns true for equals(GMatrix) ) will return the same hash
     * number.  Two objects with different data members may return the
     * same hash value, although this is not likely.
     * @return the integer hash value
     */
    public int hashCode() {
	int hash = 0;
	for (int i = 0; i < nRow*nCol; i++) {
	    long bits = Double.doubleToLongBits(elementData[i]);
	    hash ^= (int)(bits ^ (bits >> 32));
	}
	return hash;
    }

    /**
     * Returns true if all of the data members of Matrix4d m1 are
     * equal to the corresponding data members in this Matrix4d.
     *
     * @param m1 The matrix with which the comparison is made.
     * @return true or false
     */ 
    public boolean equals(GMatrix m1) {
	if (m1 == null)
	    return false;
	if(m1.nRow != nRow)
	    return false;
	if(m1.nCol != nCol)
	    return false;
	for (int i = 0; i < nRow; i++)
	    for (int j = 0; j < nCol; j++)
		if (elementData[i*nCol + j] != m1.elementData[i*nCol + j])
		    return false;
	return true;
    }

    /**
      * Returns true if the Object o1 is of type GMatrix and all of the data
      * members of t1 are equal to the corresponding data members in this
      * GMatrix.
      * @param o1 the object with which the comparison is made.
      */
    public boolean equals(Object o1) {
	return o1 != null && (o1 instanceof GMatrix) && equals((GMatrix)o1);
    }

    /**
     * Returns true if the L-infinite distance between this matrix and 
     * matrix m1 is less than or equal to the epsilon parameter,
     * otherwise returns false. The L-infinite distance is equal to 
     * MAX[i=0,1,2, . . .n ; j=0,1,2, . . .n ; abs(this.m(i,j) - m1.m(i,j)] . 
     * @deprecated The double version of this method should be used.
     * @param m1 The matrix to be compared to this matrix 
     * @param epsilon the threshold value
     */
    public boolean epsilonEquals(GMatrix m1, float epsilon) {
	if(m1.nRow != nRow)
	    return false;
	if(m1.nCol != nCol)
	    return false;
	for (int i = 0; i < nRow; i++)
	    for (int j = 0; j < nCol; j++)
		if (
		    epsilon < Math.abs(elementData[i*nCol + j]-m1.elementData[i*nCol + j])
		    )
		    return false;

	return true;
    }
    /**
     * Returns true if the L-infinite distance between this matrix and 
     * matrix m1 is less than or equal to the epsilon parameter,
     * otherwise returns false. The L-infinite distance is equal to 
     * MAX[i=0,1,2, . . .n ; j=0,1,2, . . .n ; abs(this.m(i,j) - m1.m(i,j)] . 
     * @param m1 The matrix to be compared to this matrix 
     * @param epsilon the threshold value
     */
    public boolean epsilonEquals(GMatrix m1, double epsilon) {
	if(m1.nRow != nRow)
	    return false;
	if(m1.nCol != nCol)
	    return false;
	for (int i = 0; i < nRow; i++)
	    for (int j = 0; j < nCol; j++)
		if (
		    epsilon < Math.abs(elementData[i*nCol + j]-m1.elementData[i*nCol + j])
		    )
		    return false;

	return true;
    }
    
    /**
     * Returns the trace of this matrix.
     * @return the trace of this matrix.
     */
    public final double trace() {
	int min = nRow < nCol ? nRow : nCol;
	double trace=0.0;
	for (int i = 0; i < min; i++)
	    trace += elementData[i*nCol + i];
	return trace;
    }

    /**
     * Sets this matrix to a uniform scale matrix; all of the values are reset.
     * @param scale The new scale value 
     */
    public final void setScale(double scale) {
	setZero();
	int min = nRow < nCol ? nRow : nCol;
	for (int i = 0; i < min; i++)
	    elementData[i*nCol + i] = scale;
    }

    private void setDiag(int i, double value) {
	elementData[i*nCol + i] = value;
    }
    private double  getDiag(int i) {
	return elementData[i*nCol + i];
    }

    private double dpythag(double a, double b) {
	double absa = Math.abs(a);
	double absb = Math.abs(b);
	if (absa > absb) {
	    if (absa == 0.0)
		return 0.0;
	    double term = absb/absa;
	    if (Math.abs(term) <= Double.MIN_VALUE)
		return absa;
	    return (absa*Math.sqrt(1.0+term*term));
	} else {
	    if (absb == 0.0)
		return 0.0;
	    double term = absa/absb;
	    if (Math.abs(term) <= Double.MIN_VALUE)
		return absb;
	    return (absb*Math.sqrt(1.0+term*term));
	}
    }
    /**
     * Finds the singular value decomposition (SVD) of this matrix such that 
     * this = U*W*transpose(V); and returns the rank of this matrix; the values 
     * of U,W,V are all overwritten. Note that the matrix V is output as V,
     * and not transpose(V). If this matrix is mxn, then U is mxm, W is a 
     * diagonal matrix that is mxn, and V is nxn. Using the notation W = diag(w),
     * then the inverse of this matrix is: inverse(this) = V*diag(1/w)*tranpose(U), 
     * where diag(1/w) is the same matrix as W except that the reciprocal of each 
     * of the diagonal components is used. 
     * @param U The computed U matrix in the equation this = U*W*transpose(V) 
     * @param W The computed W matrix in the equation this = U*W*transpose(V) 
     * @param V The computed V matrix in the equation this = U*W*transpose(V) 
     * @return The rank of this matrix. 
     */
    public final int SVD(GMatrix u, GMatrix w, GMatrix v)  {
	if (u.nRow != nRow || u.nCol != nRow)
	    throw new ArrayIndexOutOfBoundsException(
		"The U Matrix invalid size");
	if (v.nRow != nCol || v.nCol != nCol)
	    throw new ArrayIndexOutOfBoundsException(
		"The V Matrix invalid size");
	if (w.nCol != nCol || w.nRow != nRow)
	    throw new ArrayIndexOutOfBoundsException(
		"The W Matrix invalid size");

	int m = nRow;
	int n = nCol;
	int imax = m > n ? m : n;
	double [] A = u.elementData;
	double [] V = v.elementData;
	int i,its,j,jj,k,l=0,nm=0;
	double anorm,c,f,g,h,s,scale,x,y,z;
	double [] rv1 = new double [n];

    // copy this to [u]
	this.get(u);
	// pad 0.0 to the other elements.
	for (i = m; i < imax; i++)
	    for (j = 0; j < imax; j++)
            A[i*m + j] = 0.0;
    for (j = n; j < imax; j++)
        for (i = 0; i < imax; i++)
            A[i*m + j] = 0.0;
	// pad 0.0 to w
	w.setZero();
	    

	g=scale=anorm=0.0;
	for (i=0;i<n;i++) {
		l=i+1;
		rv1[i]=scale*g;
		g=s=scale=0.0;
		if (i < m) {
			for (k=i;k<m;k++) scale += Math.abs(A[k*m+i]);
			if (scale!=0.0) {
				for (k=i;k<m;k++) {
					A[k*m + i] /= scale;
					s += A[k*m+i]*A[k*m+i];
				}
				f=A[i*m+i];

// #define SIGN(a,b) ((b) >= 0.0 ? fabs(a) : -fabs(a))
//				g = -SIGN(sqrt(s),f);

				g = (f < 0.0 ? Math.sqrt(s) : -Math.sqrt(s));
				h=f*g-s;
				A[i*m+i]=f-g;
				for (j=l;j<n;j++) {
					for (s=0.0,k=i;k<m;k++) s += A[k*m+i]*A[k*m+j];
					f=s/h;
					for (k=i;k<m;k++) A[k*m+j] += f*A[k*m+i];
				}
				for (k=i;k<m;k++) A[k*m+i] *= scale;
			}
		}
		w.setDiag(i, scale*g);
		g=s=scale=0.0;
		if (i < m && i != n-1) {
			for (k=l;k<n;k++) scale += Math.abs(A[i*m+k]);
			if (scale != 0.0) {
				for (k=l;k<n;k++) {
					A[i*m+k] /= scale;
					s += A[i*m+k]*A[i*m+k];
				}
				f=A[i*m+l];

// #define SIGN(a,b) ((b) >= 0.0 ? fabs(a) : -fabs(a))
//				g = -SIGN(sqrt(s),f);

				g = (f < 0.0 ? Math.sqrt(s) : -Math.sqrt(s));
				h=f*g-s;
				A[i*m+l]=f-g;
				for (k=l;k<n;k++) rv1[k]=A[i*m+k]/h;
				for (j=l;j<m;j++) {
					for (s=0.0,k=l;k<n;k++) s += A[j*m+k]*A[i*m+k];
					for (k=l;k<n;k++) A[j*m+k] += s*rv1[k];
				}
				for (k=l;k<n;k++) A[i*m+k] *= scale;
			}
		}
//		anorm=MAX2INT(anorm,(Math.abs(w.getDiag(i))+Math.abs(rv1[i])));
		double a1 = Math.abs(w.getDiag(i))+Math.abs(rv1[i]);
		if (a1 > anorm)
		    anorm = a1;
	}
	for (i=n-1;i>=0;i--) {
		if (i < n-1) {
			if (g != 0.0) {
				for (j=l;j<n;j++)
					V[j*n+i]=(A[i*m+j]/A[i*m+l])/g;
				for (j=l;j<n;j++) {
					for (s=0.0,k=l;k<n;k++) s += A[i*m+k]*V[k*n+j];
					for (k=l;k<n;k++) V[k*n+j] += s*V[k*n+i];
				}
			}
			for (j=l;j<n;j++) V[i*n+j]=V[j*n+i]=0.0;
		}
		V[i*n+i]=1.0;
		g=rv1[i];
		l=i;
	}
//	for (i=IMIN(m,n)-1;i>=0;i--) {
	int imin = m < n ? m : n;
	for (i=imin-1;i>=0;i--) {
		l=i+1;
		g=w.getDiag(i);
		for (j=l;j<n;j++) A[i*m+j]=0.0;
		if (g != 0.0) {
			g=1.0/g;
			for (j=l;j<n;j++) {
				for (s=0.0,k=l;k<m;k++) s += A[k*m+i]*A[k*m+j];
				f=(s/A[i*m+i])*g;
				for (k=i;k<m;k++) A[k*m+j] += f*A[k*m+i];
			}
			for (j=i;j<m;j++) A[j*m+i] *= g;
		} else for (j=i;j<m;j++) A[j*m+i]=0.0;
		++A[i*m+i];
	}
	for (k=n-1;k>=0;k--) {
		for (its=1;its<=30;its++) {
			boolean flag=true;
			for (l=k;l>=0;l--) {
				nm=l-1;
				if ((double)(Math.abs(rv1[l])+anorm) == anorm) {
					flag=false;
					break;
				}
				if ((double)(Math.abs(w.getDiag(nm))+anorm) == anorm) break;
			}
			if (flag) {
				c=0.0;
				s=1.0;
				for (i=l;i<=k;i++) {
					f=s*rv1[i];
					rv1[i]=c*rv1[i];
					if ((double)(Math.abs(f)+anorm) == anorm) break;
					g=w.getDiag(i);
					h=dpythag(f,g);
					w.setDiag(i, h);
					h=1.0/h;
					c=g*h;
					s = -f*h;
					for (j=0;j<m;j++) {
						y=A[j*m+nm];
						z=A[j*m+i];
						A[j*m+nm]=y*c+z*s;
						A[j*m+i]=z*c-y*s;
					}
				}
			}
			z=w.getDiag(k);
			if (l == k) {
				if (z < 0.0) {
					w.setDiag(k, -z);
					for (j=0;j<n;j++) V[j*n+k] = -V[j*n+k];
				}
				break;	/* NORMAL EXIT */
			}
			if (its == 30) {
				return 0; // not solved.
			}
			x=w.getDiag(l);
			nm=k-1;
			y=w.getDiag(nm);
			g=rv1[nm];
			h=rv1[k];
			f=((y-z)*(y+z)+(g-h)*(g+h))/(2.0*h*y);
			g=dpythag(f,1.0);

// #define SIGN(a,b) ((b) >= 0.0 ? fabs(a) : -fabs(a))
//			f=((x-z)*(x+z)+h*((y/(f+SIGN(g,f)))-h))/x;

			f=((x-z)*(x+z)+h*((y/(f+
					      (f >= 0.0 ? Math.abs(g) : -Math.abs(g))
					      ))-h))/x;
			c=s=1.0;
			for (j=l;j<=nm;j++) {
				i=j+1;
				g=rv1[i];
				y=w.getDiag(i);
				h=s*g;
				g=c*g;
				z=dpythag(f,h);
				rv1[j]=z;
				c=f/z;
				s=h/z;
				f=x*c+g*s;
				g = g*c-x*s;
				h=y*s;
				y *= c;
				for (jj=0;jj<n;jj++) {
					x=V[jj*n+j];
					z=V[jj*n+i];
					V[jj*n+j]=x*c+z*s;
					V[jj*n+i]=z*c-x*s;
				}
				z=dpythag(f,h);
				w.setDiag(j, z);
				if (z != 0.0) {
					z=1.0/z;
					c=f*z;
					s=h*z;
				}
				f=c*g+s*y;
				x=c*y-s*g;
				for (jj=0;jj<m;jj++) {
					y=A[jj*m+j];
					z=A[jj*m+i];
					A[jj*m+j]=y*c+z*s;
					A[jj*m+i]=z*c-y*s;
				}
			}
			rv1[l]=0.0;
			rv1[k]=f;
			w.setDiag(k, x);
		}
	}

	// find the number of non-zero w which is the rank of this matrix
	int rank = 0;
	for (i = 0; i < n; i++)
	    if (w.getDiag(i) > 0.0)
		rank++;

	return rank;
    }


    private void swapRows(int i, int j) {
	for (int k = 0; k < nCol; k++) {
	    double tmp = elementData[i*nCol + k];
	    elementData[i*nCol + k] = elementData[j*nCol + k];
	    elementData[j*nCol + k] = tmp;
	}
    }
    /**
     * LU Decomposition; this matrix must be a square matrix; the LU GMatrix 
     * parameter must be the same size as this matrix. The matrix LU will be 
     * overwritten as the combination of a lower diagonal and upper diagonal
     * matrix decompostion of this matrix; the diagonal elements of L (unity)
     * are not stored. The GVector parameter records the row permutation 
     * effected by the partial pivoting, and is used as a parameter to the
     * GVector method LUDBackSolve to solve sets of linear equations. This 
     * method returns +/- 1 depending on whether the number of row interchanges
     * was even or odd, respectively. 
     * @param permutation The row permutation effected by the 
     * partial pivoting 
     * @return +-1 depending on whether the number of row interchanges 
     * was even or odd respectively 
     */
    public final int LUD(GMatrix LU, GVector permutation) {
	// note: this is from William H. Press et.al Numerical Recipes in C.
	// hiranabe modified 1-n indexing to 0-(n-1), and not to use implicit
	// scaling factors(which uses 'new' and I don't belive relative pivot is better)
	// I fixed some bugs in NRC, which are missing permutation handling.
        if (nRow != nCol)
            throw new ArrayIndexOutOfBoundsException(
		"not a square matrix");
	int n = nRow;
        if (n != LU.nRow)
            throw new ArrayIndexOutOfBoundsException(
		"this.nRow:"+n+ " != LU.nRow:" + LU.nRow);
        if (n != LU.nCol)
            throw new ArrayIndexOutOfBoundsException(
		"this.nCol:"+n+ " != LU.nCol:" + LU.nCol);
        if (permutation.getSize() < n)
            throw new ArrayIndexOutOfBoundsException(
                "permutation.size:"+permutation.getSize()+ " < this.nCol:" + n);

	if (this != LU)
	    LU.set(this);

	int even = 1;	// permutation Odd/Even
	double [] a = LU.elementData;

	// initialize index
	for (int i = 0; i < n; i++)
	    permutation.setElement(i, i);

	// start Crout's method
	for (int j = 0; j < n; j++) {
	    double  big, dum, sum;
	    int imax;	// the pivot row number

	    // upper portion (U)
	    for (int i = 0; i < j; i++) {
		sum = a[i*n + j];
		for (int k = 0; k < i; k++) {
		    if (a[i*n + k] != 0.0 && a[k*n + j] != 0.0)
			sum -= a[i*n + k] * a[k*n +j];
		}
		a[i*n + j] = sum;
	    }
	    big = 0.0;
	    imax = j;

	    // lower part (L)
	    for (int i = j; i < n; i++) {
		sum = a[i*n + j];
		for (int k = 0; k < j; k++) {
		    if (a[i*n +  k] != 0.0 && a[k*n + j] != 0.0)
			sum -= a[i*n + k] * a[k*n + j];
		}
		a[i*n + j] = sum;
		dum = Math.abs(sum);
		if (dum >= big) {
		    big = dum;
		    imax = i;	// imax is the pivot
		}
	    }

	    if (j != imax) {	// if pivot is not on the diagonal
		// swap rows
		LU.swapRows(imax, j);
		double tmp = permutation.getElement(imax);
		permutation.setElement(imax, permutation.getElement(j));
		permutation.setElement(j, tmp);
		even = -even;
	    }

	    // zero-div occurs.
	    // if (a[j][j] == 0.0) 

	    if (j != n - 1) {
		dum = 1.0 / a[j*n + j];
		for (int i = j + 1; i < n; i++) 
		    a[i*n + j] *= dum;
	    }

	} // end of for j

	return even;

    }
}
