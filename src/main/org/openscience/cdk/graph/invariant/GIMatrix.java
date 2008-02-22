/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk.graph.invariant;

import org.openscience.cdk.graph.invariant.exception.BadMatrixFormatException;
import org.openscience.cdk.graph.invariant.exception.IndexOutOfBoundsException;
import org.openscience.cdk.graph.invariant.exception.MatrixNotInvertibleException;

/**
 * This class is intended to provide the user an efficient way of implementing matrix of double number and
 * using normal operations (linear operations, addition, substraction, multiplication, inversion, concatenation)
 * on them. The internal representation of a matrix is an array of array of double objects. For the moment,
 * double class is the best way I have developped to perform exact operation on numbers; however, for
 * irdoubles, normal operations on float and doubles have to be performed, with the well-known risks of error
 * this implies. This class also provides a way of representing matrix as arrays of String for output use.
 *
 * <P>Please note that although in most books matrix elements' indexes take values between [1..n] I chose not
 * to disturb Java language way of calling indexes; so the indexes used here take values between [0..n-1] instead.
 *
 * @author Jean-Sebastien Senecal
 * @cdk.svnrev  $Revision$
 * @version 1.0
 * @cdk.created 1999-05-20
 */
public class GIMatrix {

    private double[][] array; // the matrix itself as an array of doubles
    private int m, n; // matrix's params (m=no of line, n=no of columns)
    
    /**
     * Class constructor. Uses an array of integers to create a new Matrix object. Note that integers
     * will be converted to double objects so mathematical operations may be properly performed and
     * provide exact solutions. The given array should be properly instantiated as a matrix i.e. it
     * must contain a fixed number of lines and columns, otherwise an exception will be thrown.
     * Array must be at leat 1x1.
     * @param array an array of integer (first index is the line, second is the column)
     * @exception BadMatrixFormatException in case the given array is unproper to construct a matrix
     */
    public GIMatrix(int[][] array)  {
	double[][] temp = new double[array.length][];
	for (int i = 0; i < array.length; i++) {
	    temp[i] = new double[array[i].length]; // line by line ...
	    for (int j = 0; j < array[i].length; j++)
		temp[i][j] = array[i][j]; // converts ints to doubles
	}
//	verifyMatrixFormat(temp);
	this.array = temp;
	m = temp.length;
	n = temp[0].length;
    } // constructor Matrix(int[][])

    /**
     * Class constructor. Uses an array of doubles to create a new Matrix object. The given array should
     * be properly instantiated as a matrix i.e. it must contain a fixed number of lines and columns,
     * otherwise an exception will be thrown. Array must be at leat 1x1.
     *
     * @param array an array of double objects (first index is the line, second is the column)
     * @exception BadMatrixFormatException in case the given array is unproper to construct a matrix
     */
    public GIMatrix(double[][] array) throws BadMatrixFormatException {
	verifyMatrixFormat(array);
	double[][] temp = new double[array.length][];
	for (int i = 0; i < array.length; i++) {
	    temp[i] = new double[array[i].length]; // line by line ...
	    for (int j = 0; j < array[i].length; j++)
		temp[i][j] = array[i][j];
	}
	this.array = temp;
	m = array.length;
	n = array[0].length;
    } // constructor Matrix(double[][])
    
    /**
     * Class constructor. Creates a new Matrix object with fixed dimensions. The matrix is
     * initialised to the "zero" matrix.
     *
     * @param line number of lines
     * @param col number of columns
     */
    public GIMatrix(int line, int col) {
		array = new double[line][col];
		for (int i = 0; i < line; i++)
			for (int j = 0; j < col; j++)
				array[i][j] = 0.0;
		m = line;
		n = col;		
    } // constructor Matrix(int,int)

    /**
     * Class constructor. Copies an already existing Matrix object in a new Matrix object.
     * @param matrix a Matrix object
     */
    public GIMatrix(GIMatrix matrix) {
	double[][] temp = new double[matrix.height()][];
	for (int i = 0; i < matrix.height(); i++) {
	    temp[i] = new double[matrix.width()]; // line by line ...
	    for (int j = 0; j < matrix.width(); j++) {
		try { temp[i][j] = matrix.getValueAt(i,j); }
		catch (IndexOutOfBoundsException e) {} // never happens
	    }
	}
	this.array = temp;
	m = array.length;
	n = array[0].length;
    } // constructor Matrix(Matrix)

    /**
     * Class constructor. Creates a new Matrix object using a table of matrices (an array of Matrix objects).
     * The given array should be properly instantiated i.e. it must contain a fixed number of lines and columns, 
     * otherwise an exception will be thrown.
     * @param table an array of matrices
     * @exception BadMatrixFormatException if the table is not properly instantiated
     */
    public GIMatrix(GIMatrix[][] table) throws BadMatrixFormatException {
	verifyTableFormat(table);
	m = n = 0;
	for (int i = 0; i < table.length; i++) m += table[i][0].height();
	for (int j = 0; j < table[0].length; j++) n += table[0][j].width();
	double[][] temp = new double[m][n];
	int k = 0; // counters for matrices
	for (int i = 0; i < m; i++) {
	    temp[i] = new double[n]; // line by line ...
	    if (i == table[k][0].height()) k++; // last line of matrix reached
	    int h = 0;
	    for (int j = 0; j < n; j++) {
		if (j == table[k][h].width()) h++; // last column of matrix reached
		try {
		    GIMatrix tempMatrix = table[k][h];
		    temp[i][j] = tempMatrix.getValueAt(i - k*tempMatrix.height(),j - h*tempMatrix.width());
		}
		catch (IndexOutOfBoundsException e) {} // never happens
	    }
	}
	this.array = temp;
    } // constructor Matrix(Matrix)
    
    /**
     * Returns the number of lines of the matrix.
     * @return the height of the matrix
     */
    public int height() { return m; } // method heigth()

    /**
     * Returns the number of columns of the matrix.
     * @return the width of the matrix
     */
    public int width() { return n; } // method width()

    /**
     * Returns the internal representation of the matrix, that is an array of double objects.
     * @return an array of double equivalent to the matrix
     */
    public double[][] getArrayValue() { return array; } // method getArrayValue()
    
    /**
     * Resets the value of the matrix to the given array of double numbers
     * @param array an array of double objects (first index is the line, second is the column)
     * @exception BadMatrixFormatException in case the given array is unproper to construct a matrix
     */
    public void setArrayValue(double[][] array) throws BadMatrixFormatException {
	verifyMatrixFormat(array);
	this.array = array;
    } // method setArrayValue(double[][])

    /**
     * Returns the value of the given element.
     * @param i the line number
     * @param j the column number
     * @return the double at the given index in the Matrix
     * @exception IndexOutOfBoundsException if the given index is out of the matrix's range
     */
    public double getValueAt(int i, int j) throws IndexOutOfBoundsException { 
	if ( (i < 0)||(i >= m)||(j < 0)||(j >= n) ) throw new IndexOutOfBoundsException();
	return array[i][j];
    } // method getValueAt(int,int)
    
    /**
     * Sets the value of the element at the given index.
     * @param i the line number
     * @param j the column number
     * @param element the double to place at the given index in the Matrix
     * @exception IndexOutOfBoundsException if the given index is out of the matrix's range
     */
    public void setValueAt(int i, int j, double element) throws IndexOutOfBoundsException {
	if ( (i < 0)||(i >= m)||(j < 0)||(j >= n) ) throw new IndexOutOfBoundsException();
	array[i][j] = element;
    } // method setValueAt(int,int,double)

    /**
     * Returns the line-matrix at the given line index
     * @param i the line number
     * @return the specified line as a Matrix object
     * @exception IndexOutOfBoundsException if the given index is out of the matrix's range
     */ 
    public GIMatrix getLine(int i) throws IndexOutOfBoundsException {
	if ( (i < 0)||(i >= m) ) throw new IndexOutOfBoundsException();
	double[][] line = new double[1][n];
	for (int k = 0; k < n; k++) line[0][k] = array[i][k];
	try { return new GIMatrix(line); } // format is always OK anyway ...
	catch (BadMatrixFormatException e) { return null; }
    } // method getLine(int)

    /**
     * Returns the column-matrix at the given line index
     * @param j the column number
     * @return the specified column as a Matrix object
     * @exception IndexOutOfBoundsException if the given index is out of the matrix's range
     */ 
    public GIMatrix getColumn(int j) throws IndexOutOfBoundsException {
	if ( (j < 0)||(j >= n) ) throw new IndexOutOfBoundsException();
	double[][] column = new double[m][1];
	for (int k = 0; k < m; k++) column[k][0] = array[k][j];
	try { return new GIMatrix(column); } // format is always OK anyway ...
	catch (BadMatrixFormatException e) { return null; }
    } // method getColumn(int)

    /**
     * Sets the line of the matrix at the specified index to a new value.
     * @param i the line number
     * @param line the line to be placed at the specified index
     * @exception IndexOutOfBoundsException if the given index is out of the matrix's range
     * @exception BadMatrixFormatException in case the given Matrix is unproper to replace a line of this Matrix
     */
    public void setLine(int i, GIMatrix line) throws IndexOutOfBoundsException, BadMatrixFormatException {
	if ( (i < 0)||(i >= m) ) throw new IndexOutOfBoundsException();
	if ((line.height() != 1) || (line.width() != n)) throw new BadMatrixFormatException();
	for (int k = 0; k < n; k++) array[i][k] = line.getValueAt(0,k);
    } // method setLine(int,Matrix)

    /**
     * Sets the column of the matrix at the specified index to a new value.
     * @param j the column number
     * @param column the colums to be placed at the specified index
     * @exception IndexOutOfBoundsException if the given index is out of the matrix's range
     * @exception BadMatrixFormatException in case the given Matrix is unproper to replace a column of this Matrix
     */
    public void setColumn(int j, GIMatrix column) throws IndexOutOfBoundsException, BadMatrixFormatException {
	if ( (j < 0)||(j >= n) ) throw new IndexOutOfBoundsException();
	if ((column.height() != m) || (column.width() != 1)) throw new BadMatrixFormatException();
	for (int k = 0; k < m; k++) array[k][j] = column.getValueAt(k,0);
    } // method setColumn(int,Matrix)

    /**
     * Returns the identity matrix.
     * @param n the matrix's dimension (identity matrix is a square matrix)
     * @return the identity matrix of format nxn
     */
    public static GIMatrix identity(int n) {
	double[][] identity = new double[n][n];
	for (int i = 0; i < n; i++) {
	    for (int j = 0; j < i; j++) identity[i][j] = 0.0;
	    identity[i][i] = 1.0;
	    for (int j = i+1; j < n; j++) identity[i][j] = 0.0;
	}
	try { return new GIMatrix(identity); } // format is always OK anyway ...
	catch (BadMatrixFormatException e) { return null; }
    } // method identity(int)
    
    /**
     * Returns a null matrix (with zeros everywhere) of given dimensions.
     * @param m number of lines
     * @param n number of columns
     * @return the zero (null) matrix of format mxn
     */
    public static GIMatrix zero(int m, int n) {
	double[][] zero = new double[m][n];
	for (int i = 0; i < m; i++)
	    for (int j = 0; j < n; j++)
		zero[i][j] = 0.0;
	try { return new GIMatrix(zero); } // format is always OK anyway ...
	catch (BadMatrixFormatException e) { return null; }
    } // method zero(int,int)

    /**
     * Verifies if two given matrix are equal or not. The matrix must be of the same size and dimensions,
     * otherwise an exception will be thrown.
     * @param matrix the Matrix object to be compared to
     * @return true if both matrix are equal element to element
     * @exception BadMatrixFormatException if the given matrix doesn't have the same dimensions as this one
     */
    public boolean equals(GIMatrix matrix) throws BadMatrixFormatException {
	if ( (height() != matrix.height())||(width() != matrix.width()) )
	    throw new BadMatrixFormatException();
	double[][] temp = matrix.getArrayValue();
	for (int i = 0; i < m; i++)
	    for (int j = 0; j < n; j++)
		if (!(array[i][j]==temp[i][j])) return false;
	return true;
    } // method equals(Matrix)

    /**
     * Verifies if the matrix is square, that is if it has an equal number of lines and columns.
     * @return true if this matrix is square
     */
    public boolean isSquare() { return (m == n); } // method isSquare()
   
    /**
     * Verifies if the matrix is symetric, that is if the matrix is equal to it's transpose.
     * @return true if the matrix is symetric
     * @exception BadMatrixFormatException if the matrix is not square
     */
    public boolean isSymmetric() throws BadMatrixFormatException {
	if (m != n) throw new BadMatrixFormatException();
	// the loop looks in the lower half of the matrix to find non-symetric elements
	for (int i = 1; i < m; i++) // starts at index 1 because index (0,0) always symmetric
	    for (int j = 0; j < i; j++)
		if (!(array[i][j]==array[j][i])) return false;
	return true; // the matrix has passed the test
    } //method isSymmetric()

    // NOT OVER, LOOK MORE CAREFULLY FOR DEFINITION
    /**
     * Verifies if the matrix is antisymetric, that is if the matrix is equal to the opposite of
     * it's transpose.
     * @return true if the matrix is antisymetric
     * @exception BadMatrixFormatException if the matrix is not square
     */
    public boolean isAntisymmetric() throws BadMatrixFormatException {
	if (m != n) throw new BadMatrixFormatException();
	// the loop looks in the lower half of the matrix to find non-antisymetric elements
	for (int i = 0; i < m; i++) // not as isSymmetric() loop
	    for (int j = 0; j <= i; j++)
		if (!(array[i][j]==-array[j][i])) return false;
	return true; // the matrix has passed the test
    } // method isAntisymmetric()

    /**
     * Verifies if the matrix is triangular superior or not. A triangular superior matrix has
     * zero (0) values everywhere under it's diagonal.
     * @return true if the matrix is triangular superior
     * @exception BadMatrixFormatException if the matrix is not square
     */
    public boolean isTriangularSuperior() throws BadMatrixFormatException {
	if (m != n) throw new BadMatrixFormatException();
	// the loop looks in the lower half of the matrix to find non-null elements
	for (int i = 1; i < m; i++) // starts at index 1 because index (0,0) is on the diagonal
	    for (int j = 0; j < i; j++)
		if (!(array[i][j] == 0.0)) return false;
	return true; // the matrix has passed the test
    } // method isTriangularSuperior
    
    /**
     * Verifies if the matrix is triangular inferior or not. A triangular inferior matrix has
     * zero (0) values everywhere upper it's diagonal.
     * @return true if the matrix is triangular inferior
     * @exception BadMatrixFormatException if the matrix is not square
     */
    public boolean isTriangularInferior() throws BadMatrixFormatException {
	if (m != n) throw new BadMatrixFormatException();
	// the loop looks in the upper half of the matrix to find non-null elements
	for (int i = 1; i < m; i++) // starts at index 1 because index (0,0) is on the diagonal
	    for (int j = i; j < n; j++)
		if (!(array[i][j] == 0.0)) return false;
	return true; // the matrix has passed the test
    } // method isTriangularInferior()

    /**
     * Verifies whether or not the matrix is diagonal. A diagonal matrix only has elements on its diagonal
     * and zeros (0) at every other index. The matrix must be square.
     * @return true if the matrix is diagonal
     * @exception BadMatrixFormatException if the matrix is not square
     */
    public boolean isDiagonal() throws BadMatrixFormatException {
	if (m != n) throw new BadMatrixFormatException();
	// the loop looks both halves of the matrix to find non-null elements
	for (int i = 1; i < m; i++) // starts at index 1 because index (0,0) must not be checked
	    for (int j = 0; j < i; j++)
		if ( (!(array[i][j] == 0.0))||(!(array[j][i]== 0.0)) )
		    return false; // not null
	return true;
    } // method isDiagonal
    
    /**
     * Verifies if the matrix is invertible or not by asking for its determinant.
     * @return true if the matrix is invertible
     * @exception BadMatrixFormatException if the matrix is not square 
     */
    public boolean isInvertible() throws BadMatrixFormatException {
	if (m != n) throw new BadMatrixFormatException();
	return (!(determinant() == 0)); // det != 0
    } // method isInvertible()

    /**
     * Returns the transpose of this matrix. The transpose of a matrix A = {a(i,j)} is the matrix B = {b(i,j)}
     * such that b(i,j) = a(j,i) for every i,j i.e. it is the symetrical reflexion of the matrix along its
     * diagonal. The matrix must be square to use this method, otherwise an exception will be thrown.
     * @return the matrix's transpose as a Matrix object
     * @exception BadMatrixFormatException if the matrix is not square
     */
    public GIMatrix inverse() throws MatrixNotInvertibleException {
	try {
	    if (!isInvertible()) throw new MatrixNotInvertibleException();
	} catch (BadMatrixFormatException e) {
	    throw new MatrixNotInvertibleException();
	}
	GIMatrix I = identity(n); // Creates an identity matrix of same dimensions
	GIMatrix table;
	try {
	    GIMatrix[][] temp = {{this,I}};
	    table = new GIMatrix(temp);
	} catch (BadMatrixFormatException e) { return null; } // never happens
	table = table.GaussJordan(); // linear reduction method applied
	double[][] inv = new double[m][n];
	for (int i = 0; i < m; i++) // extracts inverse matrix
	    for (int j = n; j < 2*n; j++) {
		try { inv[i][j-n] = table.getValueAt(i,j); }
		catch (IndexOutOfBoundsException e) { return null; } // never happens
	    }
	try { return new GIMatrix(inv); }
	catch (BadMatrixFormatException e) { return null; } // never happens...
    } // method inverse()
    
    /**
     * Gauss-Jordan algorithm. Returns the reduced-echeloned matrix of this matrix. The
     * algorithm has not yet been optimised but since it is quite simple, it should not be
     * a serious problem.
     * @return the reduced matrix
     */
    public GIMatrix GaussJordan() {
	GIMatrix tempMatrix= new GIMatrix(this);
	try {
	    int i = 0;
	    int j = 0;
	    int k = 0;
	    boolean end = false;
	    while ( (i < m) && (!end) ) {
		boolean allZero = true; // true if all elements under line i are null (zero)
		while (j < n) { // determination of the pivot
		    for (k = i; k < m; k++) {
			if (!(tempMatrix.getValueAt(k,j)== 0.0)) { // if an element != 0
			    allZero = false;
			    break;
			} }
		    if (allZero) j++;
		    else break;
		}
		if (j == n) end = true;
		else {
		    if (k != i) tempMatrix = tempMatrix.invertLine(i,k);
		    if (!(tempMatrix.getValueAt(i,j)== 1.0)) // if element != 1
			tempMatrix = // A = L(i)(1/a(i,j))(A)
			    tempMatrix.multiplyLine(i,1/tempMatrix.getValueAt(i,j));
		    for (int q = 0; q < m; q++)
			if (q != i) // A = L(q,i)(-a(q,j))(A)
			tempMatrix = tempMatrix.addLine(q,i,-tempMatrix.getValueAt(q,j));
		}
		i++;
	    }
	    // normally here, r = i-1
	    return tempMatrix;
	} catch (IndexOutOfBoundsException e) { return null; } // never happens... well I hope ;)
	/*
	  From: LEROUX, P. Algebre lineaire: une approche matricielle.
	  Modulo Editeur, 1983. p. 75. (In French)
	*/
    } // method GaussJordan()
    
    /**
     * Returns the transpose of this matrix. The transpose of a matrix A = {a(i,j)} is the matrix B = {b(i,j)}
     * such that b(i,j) = a(j,i) for every i,j i.e. it is the symetrical reflexion of the matrix along its
     * diagonal. The matrix must be square to use this method, otherwise an exception will be thrown.
     * @return the matrix's transpose as a Matrix object
     * @exception BadMatrixFormatException if the matrix is not square
     */
    public GIMatrix transpose() throws BadMatrixFormatException {
	if (m != n) throw new BadMatrixFormatException();
	double[][] transpose = new double[array.length][array[0].length];
	for (int i = 0; i < m; i++)
	    for (int j = 0; j < n; j++)
	    transpose[i][j] = array[j][i];
	return new GIMatrix(transpose);
    } // method transpose()

    /**
     * Returns a matrix containing all of the diagonal elements of this matrix and zero (0) everywhere
     * else. This matrix is called the diagonal of the matrix.
     * @return the diagonal of the matrix
     * @exception BadMatrixFormatException if the matrix is not square
     */
     public GIMatrix diagonal() throws BadMatrixFormatException {
	if (m != n) throw new BadMatrixFormatException();
	double[][] diagonal = new double[array.length][array[0].length];;
	for (int i = 0; i < m; i++)
	    for (int j = 0; j < n; j++) {
		if (i == j) diagonal[i][j] = array[i][j];
		else diagonal[i][j] = 0;
	    }
	return new GIMatrix(diagonal);
    } // method diagonal()

    /**
     * Returns the resulting matrix of an elementary linear operation that consists of multiplying a
     * single line of the matrix by a constant.
     * @param i the line number
     * @param c the double constant that multiplies the line
     * @return the resulting Matrix object of the linear operation
     * @exception IndexOutOfBoundsException if the given index is out of the matrix's range
     */
    public GIMatrix multiplyLine(int i, double c) throws IndexOutOfBoundsException {
	if ( (i < 0)||(i >= m) ) throw new IndexOutOfBoundsException();
	double[][] temp = array;
	for (int k = 0; k < n; k++) temp[i][k] = c*temp[i][k]; // mutliply every member of the line by c
	try { return new GIMatrix(temp); } // format is always OK anyway ...
	catch (BadMatrixFormatException e) { return null; }
    } // method multiplyLine(int,int)
    
    /**
     * Returns the resulting matrix of an elementary linear operation that consists of inverting two lines.
     * @param i the first line number
     * @param j the second line number
     * @return the resulting Matrix object of the linear operation
     * @exception IndexOutOfBoundsException if the given index is out of the matrix's range
     */
    public GIMatrix invertLine(int i, int j) throws IndexOutOfBoundsException {
	if ( (i < 0)||(i >= m)||(j < 0)||(j >= m) ) throw new IndexOutOfBoundsException();
	double[][] temp = array;
        double[] tempLine = temp[j]; // temporary line
	temp[j] = temp[i];
	temp[i] = tempLine;
	try { return new GIMatrix(temp); } // format is always OK anyway ...
	catch (BadMatrixFormatException e) { return null; }
    } // method invertLine(int,int) 

   /**
     * Returns the resulting matrix of an elementary linear operation that consists of adding one line,
     * multiplied by some constant factor, to another line.
     * @param i the first line number
     * @param j the second line number (to be added to the first)
     * @param c the double constant that multiplies the first line
     * @return the resulting Matrix object of the linear operation
     * @exception IndexOutOfBoundsException if the given index is out of the matrix's range
     */
    public GIMatrix addLine(int i, int j, double c) throws IndexOutOfBoundsException{
	if ( (i < 0)||(i >= m)||(j < 0)||(j >= m) ) throw new IndexOutOfBoundsException();
        double[][] temp = array;
	for (int k = 0; k < n; k++)
	    temp[i][k] = temp[i][k]+c*temp[j][k]; // add multiplied element of i to element of j
	try { return new GIMatrix(temp); } // format is always OK anyway ...
	catch (BadMatrixFormatException e) { return null; }
    } // method addLine(int,int,double)

    
	/**
     *  Addition from two matrices.
     */
	public GIMatrix add(GIMatrix b)
	{
		if ((b==null) || (m!=b.m) || (n!=b.n))
			return null;
		
		int i, j;
		GIMatrix result = new GIMatrix(m,n);
		for(i=0; i<m; i++)
			for(j=0; j<n; j++)
				result.array[i][j] = array[i][j]+b.array[i][j];
		return result;
	}
	
	
	
	/**
     * Returns the result of the scalar multiplication of the matrix, that is the multiplication of every
     * of its elements by a given number.
     * @param c the constant by which the matrix is multiplied
     * @return the resulting matrix of the scalar multiplication
     */
    public GIMatrix multiply(double c) {
	double[][] temp = array;
	for (int i = 0; i < m; i++)
	    for (int j = 0; j < n; j++)
		temp[i][j] = c*temp[i][j];
	try { return new GIMatrix(temp); } // format is always OK anyway ...
	catch (BadMatrixFormatException e) { return null; }
    } // method multiply(double)

    /**
     * Returns the result of the matricial multiplication of this matrix by another one. The matrix passed
     * as parameter <i>follows</i> this matrix in the multiplication, so for an example if the dimension of
     * the actual matrix is mxn, the dimension of the second one should be nxp in order for the multiplication
     * to be performed (otherwise an exception will be thrown) and the resulting matrix will have dimension mxp.
     * @param matrix the matrix following this one in the matricial multiplication
     * @return the resulting matrix of the matricial multiplication
     * @exception BadMatrixFormatException if the matrix passed in arguments has wrong dimensions
     */
    public GIMatrix multiply(GIMatrix matrix) throws BadMatrixFormatException {
	if (n != matrix.height()) throw new BadMatrixFormatException(); // unsuitable dimensions
	int p = matrix.width();
	double[][] temp = new double[m][p];
	double[][] multiplied = matrix.getArrayValue();
	for (int i = 0; i < m; i++) // line index of the first matrix
	    for (int k = 0; k < p; k++) { // column index of the second matrix
		temp[i][k] = array[i][0]*multiplied[0][k]; // first multiplication
		for (int j = 1; j < n; j++) // sum of multiplications
		    temp[i][k] = temp[i][k]+array[i][j]*multiplied[j][k];
	    }
	return new GIMatrix(temp);
    } // method multiply(Matrix)
   
    /**
     * Returns the determinant of this matrix. The matrix must be
     * square in order to use this method, otherwise an exception will be thrown.
     * <i>Warning: this algorithm is very unefficient and takes too much time to compute
     * with large matrices.</i>
     * @return the determinant of the matrix
     * @exception BadMatrixFormatException if the matrix is not square
     */
    public double determinant() throws BadMatrixFormatException {
	if (m != n) throw new BadMatrixFormatException();
	return det(array); // use of recursive method
    } // method determinant()

    // Method used for recursive determinant algorithm. Supposes the given array is square.
    private double det(double[][] mat) {
	if (mat.length == 1) return mat[0][0];
	double temp = mat[0][0]*det(M(mat,0,0)); // (-1)^(0+0)*m[0][0]*det(M(i,j)) ... first assignation
	for (int k = 1; k < mat.length; k++)
	    temp = temp+(det(M(mat,0,k))*((k % 2 == 0)?mat[0][k]:-mat[0][k]));
	// Note: ((0+k)%2 == 0)?1:-1 is equivalent to (-1)^(0+k)
	return temp;
    } // method det(double[][])

    // Returns the minor of the array (supposed square) i.e. the array least its i-th line
    // and j-th column
    private double[][] M(double[][] mat, int i, int j) {
	double[][] temp = new double[mat.length-1][mat[0].length-1]; // "void minor"
	for (int k = 0; k < i; k++) {
	    for (int h = 0; h < j; h++)
		temp[k][h] = mat[k][h];
	    for (int h = j+1; h < mat[0].length; h++)
		temp[k][h-1] = mat[k][h];
	}
	for (int k = i+1; k < mat.length; k++) {
	    for (int h = 0; h < j; h++)
		temp[k-1][h] = mat[k][h];
	    for (int h = j+1; h < mat[0].length; h++)
		temp[k-1][h-1] = mat[k][h];
	}
	return temp;
    } // method M(double[][],int,int)

    /**
     * Returns the trace of this matrix, that is the sum of the elements of its diagonal. The matrix must be
     * square in order to use this method, otherwise an exception will be thrown.
     * @return the trace of the matrix
     * @exception BadMatrixFormatException if the matrix is not square
     */
    public double trace() throws BadMatrixFormatException {
	if (m != n) throw new BadMatrixFormatException();
	double trace = array[0][0];
	for (int i = 1; i < m; i++) trace = trace+array[i][i];
	return trace;
    } // method trace()

//    /**
//     * Returns the matrix as a String.
//     * The double numbers are printed using the form "p/q" or "a b/c" depending on the value of the boolean,
//     * and use a minimal number of spaces as specified. It is the user's responsibility to grant a number
//     * of spaces wide enough to get proper alignment.
//     * @see tatien.toolbox.double#toString
//     * @param simple must be true to get simple expression, false otherwise as specified
//     * @param spaces number of spaces
//     */
//    public String print(boolean simple, int spaces) {
//	String print = "";
//	for (int i = 0; i < m; i++) {
//	    print = print + array[i][0].toString(simple,spaces);
//	    for (int j = 1; j < n; j++) {
//	       print = print + array[i][j].toString(simple,spaces);
//	    }
//	    print = print + "\n";
//	}
//	return print;
//    } // method print()
    
    // Verifies if the matrix is of good format when calling a constructor or setArrayValue
    private void verifyMatrixFormat(double[][] testedMatrix) throws BadMatrixFormatException {
	if ( (testedMatrix.length == 0)||(testedMatrix[0].length == 0) ) throw new BadMatrixFormatException();
	int noOfColumns = testedMatrix[0].length;
	for (int i = 1; i < testedMatrix.length; i++)
	    if (testedMatrix[i].length != noOfColumns) throw new BadMatrixFormatException(); 
    } // method verifyMatrixFormat(double[][])
    
    // In the case of the implementation of a table i.e. an array of matrices, verifies if the table is proper.
    private void verifyTableFormat(GIMatrix[][] testedTable) throws BadMatrixFormatException {
	if ( (testedTable.length == 0)||(testedTable[0].length == 0) ) throw new BadMatrixFormatException();
	int noOfColumns = testedTable[0].length;
	int currentHeigth, currentWidth;
	for (int i = 0; i < testedTable.length; i++) { // verifies correspondence of m's (heigth)
	    if (testedTable[i].length != noOfColumns) throw new BadMatrixFormatException();
	    currentHeigth = testedTable[i][0].height();
	    for (int j = 1; j < testedTable[0].length; j++)
		if (testedTable[i][j].height() != currentHeigth) throw new BadMatrixFormatException();
	}
	for (int j = 0; j < testedTable[0].length; j++) { // verifies correspondence of n's (width)
	    currentWidth = testedTable[0][j].width();
	    for (int i = 1; i < testedTable.length; i++)
		if (testedTable[i][j].width() != currentWidth) throw new BadMatrixFormatException();
	}
    } // method verifyTableFormat(Matrix[][])
	    
} // class Matrix







