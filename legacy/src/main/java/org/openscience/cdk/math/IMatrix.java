/* IMatrix.java
 *
 * Copyright (C) 1997-2007  Stephan Michels <stephan@vern.chem.tu-berlin.de>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *  */

package org.openscience.cdk.math;

import java.text.DecimalFormat;

/**
 * This class contains a complex matrix.
 *
 * @cdk.module qm
 */
public class IMatrix {

    // Attention! Variables are unprotected
    /** the real part of the content */
    public double[][] realmatrix;
    /** the imaginary part of the content */
    public double[][] imagmatrix;

    /** the count of rows of the matrix */
    public int        rows;
    /** the count of columns of the matrix */
    public int        columns;

    /**
     * Creates a complex matrix
     */
    public IMatrix(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        realmatrix = new double[rows][columns];
        imagmatrix = new double[rows][columns];
    }

    /**
     * Creates a complex copy of a matrix
     */
    public IMatrix(Matrix m) {
        rows = m.rows;
        columns = m.columns;
        realmatrix = m.matrix.clone();
        imagmatrix = new double[rows][columns];
    }

    /**
     * Returns the count of rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * Returns the count of columns
     */
    public int getColumns() {
        return columns;
    }

    /**
     * Creates a vector with the content of a row from this matrix
     */
    public IVector getVectorFromRow(int index) {
        IVector result = new IVector(columns);
        for (int i = 0; i < columns; i++) {
            result.realvector[i] = realmatrix[index][i];
            result.imagvector[i] = imagmatrix[index][i];
        }
        return result;
    }

    /**
     * Creates a vector with the content of a column from this matrix
     */
    public IVector getVectorFromColumn(int index) {
        IVector result = new IVector(rows);
        for (int i = 0; i < rows; i++) {
            result.realvector[i] = realmatrix[i][index];
            result.imagvector[i] = imagmatrix[i][index];
        }
        return result;
    }

    /**
     * Creates a vector with the content of the diagonal elements from this matrix
     */
    public IVector getVectorFromDiagonal() {
        int size = Math.min(rows, columns);
        IVector result = new IVector(size);
        for (int i = 0; i < rows; i++) {
            result.realvector[i] = realmatrix[i][i];
            result.imagvector[i] = imagmatrix[i][i];
        }
        return result;
    }

    /**
     *  Addition from two matrices
     */
    public IMatrix add(IMatrix b) {
        IMatrix result = new IMatrix(rows, columns);
        add(b, result);
        return result;
    }

    /**
     *  Addition from two matrices
     */
    public void add(IMatrix b, IMatrix result) {
        if ((b == null) || (rows != b.rows) || (columns != b.columns)) return;

        if ((result.rows != rows) || (result.columns != columns)) result.reshape(rows, columns);

        int i, j;
        for (i = 0; i < rows; i++)
            for (j = 0; j < columns; j++) {
                result.realmatrix[i][j] = realmatrix[i][j] + b.realmatrix[i][j];
                result.imagmatrix[i][j] = imagmatrix[i][j] + b.imagmatrix[i][j];
            }
    }

    /**
     *  Subtraktion from two matrices
     */
    public IMatrix sub(IMatrix b) {
        IMatrix result = new IMatrix(rows, columns);
        sub(b, result);
        return result;
    }

    /**
     *  Subtraktion from two matrices
     */
    public void sub(IMatrix b, IMatrix result) {
        if ((b == null) || (rows != b.rows) || (columns != b.columns)) return;

        if ((result.rows != rows) || (result.columns != columns)) result.reshape(rows, columns);

        int i, j;
        for (i = 0; i < rows; i++)
            for (j = 0; j < columns; j++) {
                result.realmatrix[i][j] = realmatrix[i][j] - b.realmatrix[i][j];
                result.imagmatrix[i][j] = imagmatrix[i][j] - b.imagmatrix[i][j];
            }
    }

    /**
     *  Multiplikation from two matrices
     */
    public IMatrix mul(IMatrix b) {
        IMatrix result = new IMatrix(rows, columns);
        mul(b, result);
        return result;
    }

    /**
     *  Multiplikation from two matrices
     */
    public void mul(IMatrix b, IMatrix result) {
        if ((b == null) || (columns != b.rows)) return;

        if ((result.rows != rows) || (result.columns != b.columns)) result.reshape(rows, b.columns);
        int i, j, k;
        double realsum, imagsum;
        for (i = 0; i < rows; i++)
            for (k = 0; k < b.columns; k++) {
                realsum = 0;
                imagsum = 0;
                for (j = 0; j < columns; j++) {
                    realsum += realmatrix[i][j] * b.realmatrix[j][k] - imagmatrix[i][j] * b.imagmatrix[j][k];
                    imagsum += realmatrix[i][j] * b.imagmatrix[j][k] + imagmatrix[i][j] * b.realmatrix[j][k];
                }
                result.realmatrix[i][k] = realsum;
                result.imagmatrix[i][k] = imagsum;
            }
    }

    /**
     *  Multiplikation from a vector and a matrix
     */
    public IVector mul(IVector a) {
        IVector result = new IVector(rows);
        mul(a, result);
        return result;
    }

    /**
     *  Multiplikation from a vector and a matrix
     */
    public void mul(IVector a, IVector result) {
        if ((a == null) || (columns != a.size)) return;

        if (result.size != rows) result.reshape(rows);

        int i, j;
        double realsum, imagsum;
        for (i = 0; i < rows; i++) {
            realsum = 0;
            imagsum = 0;
            for (j = 0; j < columns; j++) {
                realsum += realmatrix[i][j] * a.realvector[j] - imagmatrix[i][j] * a.imagvector[j];
                imagsum += realmatrix[i][j] * a.imagvector[j] + imagmatrix[i][j] * a.realvector[j];
            }
            result.realvector[i] = realsum;
            result.imagvector[i] = imagsum;
        }
    }

    /**
     *  Multiplikation from a scalar and a matrix
     */
    public IMatrix mul(Complex a) {
        IMatrix result = new IMatrix(rows, columns);
        mul(a, result);
        return result;
    }

    /**
     *  Multiplikation from a scalar and a matrix
     */
    public void mul(Complex a, IMatrix result) {
        if ((result.rows != rows) || (result.columns != columns)) result.reshape(rows, columns);

        int i, j;
        for (i = 0; i < rows; i++)
            for (j = 0; j < columns; j++) {
                result.realmatrix[i][j] = realmatrix[i][j] * a.real - imagmatrix[i][j] * a.imag;
                result.imagmatrix[i][j] = realmatrix[i][j] * a.imag + imagmatrix[i][j] * a.real;
            }
    }

    /**
     *  Copy a matrix
     */
    public IMatrix duplicate() {
        IMatrix result = new IMatrix(rows, columns);
        duplicate(result);
        return result;
    }

    /**
     *  Copy a matrix
     */
    public void duplicate(IMatrix result) {
        if ((result.rows != rows) || (result.columns != columns)) result.reshape(rows, columns);

        int i, j;
        for (i = 0; i < rows; i++)
            for (j = 0; j < columns; j++) {
                result.realmatrix[i][j] = realmatrix[i][j];
                result.imagmatrix[i][j] = imagmatrix[i][j];
            }
    }

    /**
     *  Transpose a matrix
     */
    public IMatrix transpose() {
        IMatrix result = new IMatrix(rows, columns);
        transpose(result);
        return result;
    }

    /**
     *  Transpose a matrix
     */
    public void transpose(IMatrix result) {
        if ((result.rows != rows) || (result.columns != columns)) result.reshape(rows, columns);

        int i, j;
        for (i = 0; i < rows; i++)
            for (j = 0; j < columns; j++) {
                result.realmatrix[j][i] = realmatrix[i][j];
                result.imagmatrix[j][i] = imagmatrix[i][j];
            }
    }

    /**
     * Similar transformation
     * Ut * M * U
     */
    public IMatrix similar(IMatrix U) {
        IMatrix result = new IMatrix(rows, columns);
        similar(U, result);
        return result;
    }

    /**
     * Similar transformation
     * Ut * M * U
     */
    public void similar(IMatrix U, IMatrix result) {
        if ((result.rows != U.columns) || (result.columns != U.columns)) result.reshape(U.columns, U.columns);

        double realsum, imagsum, realinnersum, imaginnersum;
        for (int i = 0; i < U.columns; i++)
            for (int j = 0; j < U.columns; j++) {
                realsum = 0d;
                imagsum = 0d;
                for (int k = 0; k < U.columns; k++) {
                    realinnersum = 0d;
                    imaginnersum = 0d;
                    for (int l = 0; l < U.columns; l++) {
                        realinnersum += realmatrix[k][l] * U.realmatrix[l][j] - imagmatrix[k][l] * U.imagmatrix[l][j];
                        imaginnersum += realmatrix[k][l] * U.imagmatrix[l][j] + imagmatrix[k][l] * U.realmatrix[l][j];
                    }
                    realsum += U.realmatrix[k][i] * realinnersum - U.imagmatrix[k][i] * imaginnersum;
                    imagsum += U.realmatrix[k][i] * imaginnersum + U.imagmatrix[k][i] * realinnersum;
                }
                result.realmatrix[i][j] = realsum;
                result.imagmatrix[i][j] = imagsum;
            }
    }

    /**
     * Calculates the contraction from a matrix
     */
    public Complex contraction() {
        int i, j;
        Complex result = new Complex(0d, 0d);
        for (i = 0; i < rows; i++)
            for (j = 0; j < columns; j++) {
                result.real += realmatrix[i][j];
                result.imag += imagmatrix[i][j];
            }
        return result;
    }

    /**
     *  Return a matrix as a string
     */
    @Override
    public String toString() {
        if ((rows <= 0) || (columns <= 0)) return "[]";
        int i, j;
        DecimalFormat format = new DecimalFormat("00.0000");
        format.setPositivePrefix("+");

        StringBuffer str = new StringBuffer();
        for (i = 0; i < (rows - 1); i++) {
            for (j = 0; j < (columns - 1); j++)
                if ((Math.round(realmatrix[i][j] * 10000) != 0) && (Math.round(imagmatrix[i][j] * 10000) != 0))
                    str.append(format.format(realmatrix[i][j]) + "+i*" + format.format(imagmatrix[i][j]) + " ");
                else
                    str.append("--------+i*-------- ");
            if ((Math.round(realmatrix[i][columns - 1] * 10000) != 0)
                    && (Math.round(imagmatrix[i][columns - 1] * 10000) != 0))
                str.append(format.format(realmatrix[i][columns - 1]) + "+i*"
                        + format.format(imagmatrix[i][columns - 1]) + "\n");
            else
                str.append("--------+i*--------\n");
        }
        for (j = 0; j < (columns - 1); j++)
            if ((Math.round(realmatrix[rows - 1][j] * 10000) != 0)
                    && (Math.round(imagmatrix[rows - 1][j] * 10000) != 0))
                str.append(format.format(realmatrix[rows - 1][j]) + "+i*" + format.format(imagmatrix[rows - 1][j])
                        + " ");
            else
                str.append("--------+i*-------- ");
        if ((Math.round(realmatrix[rows - 1][columns - 1] * 10000) != 0)
                && (Math.round(imagmatrix[rows - 1][columns - 1] * 10000) != 0))
            str.append(format.format(realmatrix[rows - 1][columns - 1]) + "+i*"
                    + format.format(imagmatrix[rows - 1][columns - 1]));
        else
            str.append("--------+i*-------- ");
        return str.toString();
    }

    /**
     * Resize the matrix
     */
    public void reshape(int newrows, int newcolumns) {
        if (((newrows == rows) && (newcolumns == columns)) || (newrows <= 0) || (newcolumns <= 0)) return;

        double[][] newrealmatrix = new double[newrows][newcolumns];
        double[][] newimagmatrix = new double[newrows][newcolumns];
        int minrows = Math.min(rows, newrows);
        int mincolumns = Math.min(columns, newcolumns);
        int i, j;
        for (i = 0; i < minrows; i++)
            for (j = 0; j < mincolumns; j++) {
                newrealmatrix[i][j] = realmatrix[i][j];
                newimagmatrix[i][j] = imagmatrix[i][j];
            }

        for (i = minrows; i < newrows; i++)
            for (j = 0; j < mincolumns; j++) {
                newrealmatrix[i][j] = 0d;
                newimagmatrix[i][j] = 0d;
            }

        for (i = 0; i < minrows; i++)
            for (j = mincolumns; j < newcolumns; j++) {
                newrealmatrix[i][j] = 0d;
                newimagmatrix[i][j] = 0d;
            }

        for (i = minrows; i < newrows; i++)
            for (j = mincolumns; j < newcolumns; j++) {
                newrealmatrix[i][j] = 0d;
                newimagmatrix[i][j] = 0d;
            }

        realmatrix = newrealmatrix;
        imagmatrix = newimagmatrix;
        rows = newrows;
        columns = newcolumns;
    }
}
