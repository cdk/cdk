/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2001-2007  Stephan Michels <stephan@vern.chem.tu-berlin.de>
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
 *
 */
package org.openscience.cdk.math;
 
import java.text.DecimalFormat;

/**
 * This class contains a matrix.
 *
 * @author  Stephan Michels <stephan@vern.chem.tu-berlin.de>
 * @cdk.svnrev  $Revision$
 * @cdk.created 2001-06-07
 * @cdk.module  qm
 */
public class Matrix
{
  // Attention! Variables are unprotected
  /** the content of this matrix **/
  public double[][] matrix;

  /** the number of rows of this matrix */
  public int rows;
  /** the number of columns of this matrix */
  public int columns;

  /**
   * Creates a new Matrix.
   */ 
  public Matrix(int rows, int columns)
  {
    this.rows = rows;
    this.columns = columns;
    matrix = new double[rows][columns];
  }

  /**
   * Creates a Matrix with content of an array.
   */
  public Matrix(double[][] array)
  {
    rows = array.length;
    int i,j;
    columns = array[0].length;
    for(i=1; i<rows; i++)
      columns = Math.min(columns,array[i].length);
    
    matrix = new double[rows][columns];
    for(i=0; i<rows; i++)
      for(j=0; j<columns; j++)
        matrix[i][j] = array[i][j];
  }

  /**
   * Returns the number of rows.
   */ 
  public int getRows()
  {
    return rows;
  }

  /**
   * Returns the number of columns.
   */
  public int getColumns()
  {
    return columns;
  }

  /**
   * Creates a Vector with the content of a row from this Matrix.
   */
  public Vector getVectorFromRow(int index)
  {
    double[] row = new double[columns];
    for(int i=0; i<columns; i++)
      row[i] = matrix[index][i];
    return new Vector(row);
  }

  /**
   * Creates a Vector with the content of a column from this Matrix.
   */
  public Vector getVectorFromColumn(int index)
  {
    double[] column = new double[rows];
    for(int i=0; i<rows; i++)
      column[i] = matrix[i][index];
    return new Vector(column);
  }

  /**
   * Creates a Vector with the content of the diagonal elements from this Matrix.
   */
  public Vector getVectorFromDiagonal()
  {
    int size = Math.min(rows, columns);
    Vector result = new Vector(size);
    for(int i=0; i<rows; i++)
      result.vector[i] = matrix[i][i];
    return result;
  }

  /**
   * Adds two matrices.
   */
  public Matrix add(Matrix b)
  {
    if ((b==null) ||
        (rows!=b.rows) || (columns!=b.columns))
      return null;
      
    int i, j;
    Matrix result = new Matrix(rows, columns);
    for(i=0; i<rows; i++)
      for(j=0; j<columns; j++)
        result.matrix[i][j] = matrix[i][j]+b.matrix[i][j];
    return result;
  }
  
  /**
   * Subtracts from two matrices.
   */
  public Matrix sub(Matrix b)
  {
    if ((b==null) ||
        (rows!=b.rows) || (columns!=b.columns))
      return null;
      
    int i, j;
    Matrix result = new Matrix(rows, columns);
    for(i=0; i<rows; i++) 
      for(j=0; j<columns; j++)
        result.matrix[i][j] = matrix[i][j]-b.matrix[i][j];
    return result;
  }

  /**
   * Multiplies this Matrix with another one.
   */
  public Matrix mul(Matrix b)
  {
    if ((b==null) ||
        (columns!=b.rows))
      return null;

    Matrix result = new Matrix(rows, b.columns);
    int i,j,k;
    double sum;
    for(i=0; i<rows; i++)
      for(k=0; k<b.columns; k++)
      {
        sum = 0;
        for(j=0; j<columns; j++)
          sum += matrix[i][j]*b.matrix[j][k];
        result.matrix[i][k] = sum;
      }

    return result;
  }
  
  /**
   *  Multiplies a Vector with this Matrix.
   */
  public Vector mul(Vector a)
  {
    if ((a==null) ||
        (columns!=a.size))
      return null;

    Vector result = new Vector(rows);
    int i,j;
    double sum;
    for(i=0; i<rows; i++)
    {
      sum = 0;
      for(j=0; j<columns; j++)
        sum += matrix[i][j]*a.vector[j];
      result.vector[i] = sum;
    }
    return result;
  }

  /**
   * Multiplies a scalar with this Matrix.
   */
  public Matrix mul(double a)
  {
    Matrix result = new Matrix(rows, columns);
    int i,j;
    for(i=0; i<rows; i++)
      for(j=0; j<columns; j++)
        result.matrix[i][j] = matrix[i][j]*a;

    return result;
  }

  /**
   * Copies a matrix.
   */
  public Matrix duplicate()
  {
    Matrix result = new Matrix(rows, columns);
    int i,j;
    for(i=0; i<rows; i++)
      for(j=0; j<columns; j++)
        result.matrix[i][j] = matrix[i][j];
    return result;
  }

  /**
   * Transposes a matrix.
   */
  public Matrix transpose()
  { 
    Matrix result = new Matrix(columns, rows);
    int i,j; 
    for(i=0; i<rows; i++) 
      for(j=0; j<columns; j++)
        result.matrix[j][i] = matrix[i][j];
    return result;
  }

  /**
   * Similar transformation
   * Ut * M * U
   */
  public Matrix similar(Matrix U)
  {
    Matrix result = new Matrix(U.columns, U.columns);
    double sum, innersum;
    for(int i=0; i<U.columns; i++)
      for(int j=0; j<U.columns; j++)
      {
        sum = 0d;
        for(int k=0; k<U.columns; k++)
        {
          innersum = 0d;
          for(int l=0; l<U.columns; l++)
            innersum += matrix[k][l]*U.matrix[l][j];
          sum += U.matrix[k][i]*innersum;
        }
        result.matrix[i][j] = sum;
      }
    return result;
  }      

  public double contraction()
  {
    int i,j;
    double result = 0d;
    for(i=0; i<rows; i++)
      for(j=0; j<columns; j++)
        result += matrix[i][j];
    return result;
  }  
  
  /**
   *  Return a matrix as a String.
   */
  public String toString()
  {
    if ((rows<=0) || (columns<=0))
      return "[]";
    int i,j;
    DecimalFormat format = new DecimalFormat("00.0000");
    format.setPositivePrefix("+");

    StringBuffer str = new StringBuffer();
    for(i=0; i<(rows-1); i++)
    {
      for(j=0; j<(columns-1); j++)
        if (Math.round(matrix[i][j]*10000)!=0)
          str.append(format.format(matrix[i][j])+" ");
        else
          str.append("-------- ");
      if (Math.round(matrix[i][columns-1]*10000)!=0)
        str.append(format.format(matrix[i][columns-1])+"\n");
      else
          str.append("--------\n");
    }
    for(j=0; j<(columns-1); j++)
      if (Math.round(matrix[rows-1][j]*10000)!=0)
        str.append(format.format(matrix[rows-1][j])+" ");
      else
          str.append("-------- ");
    if (Math.round(matrix[rows-1][columns-1]*10000)!=0)
      str.append(format.format(matrix[rows-1][columns-1]));
    else
      str.append("-------- ");
    return str.toString();
  }


  /** 
   * Diagonalize this matrix with the Jacobi algorithm.
   *
   * @param nrot Count of max. rotations
   * @return Matrix m, with m^t * this * m = diagonal
   *
   * @cdk.keyword Jacobi algorithm
   * @cdk.keyword diagonalization
   */
  public Matrix diagonalize(int nrot)
  {
    Matrix m = duplicate();
    if (m.rows!=m.columns)
        
    {
      System.err.println("Matrix.diagonal: Sizes mismatched");
      return null;
    }
    int n = m.rows;

    int j,iq,ip,i;
    
    double tresh,theta,tau,t,sm,s,h,g,c;
    double[] b,z;

    Matrix v = new Matrix(columns,columns);
    Vector d = new Vector(columns);
    
    b = new double[n+1];
    z = new double[n+1];
    for (ip=0;ip<n;ip++)
    {
      for (iq=0;iq<n;iq++)
        v.matrix[ip][iq]=0.0;
      v.matrix[ip][ip]=1.0;
    } 
    
    for (ip=0;ip<n;ip++)
    {
      b[ip]=d.vector[ip]=m.matrix[ip][ip];
      z[ip]=0.0;
    }

    nrot=0;
    for (i=1;i<=50;i++)
    {
      sm=0.0;
      for (ip=0;ip<n-1;ip++)
      {
        for (iq=ip+1;iq<n;iq++)
          sm += Math.abs(m.matrix[ip][iq]);
      }   

      // Ready ??
      if (sm == 0.0)
        return v;

      if (i < 4)
        tresh=0.2*sm/(n*n);
      else
        tresh=0.0;

      for (ip=0;ip<n-1;ip++)
      {
        for (iq=ip+1;iq<n;iq++)
        {
          g=100.0*Math.abs(m.matrix[ip][iq]);
          if ((i > 4) && (Math.abs(d.vector[ip])+g == Math.abs(d.vector[ip]))
                      && (Math.abs(d.vector[iq])+g == Math.abs(d.vector[iq])))
            m.matrix[ip][iq]=0.0;
          else if (Math.abs(m.matrix[ip][iq]) > tresh)
          {
            h = d.vector[iq]-d.vector[ip];
            if (Math.abs(h)+g == Math.abs(h))
              t = (m.matrix[ip][iq])/h;
            else 
            {
              theta = 0.5*h/(m.matrix[ip][iq]);
              t = 1.0/(Math.abs(theta)+Math.sqrt(1.0+theta*theta));
              if (theta < 0.0) t = -t;
            } 
            c = 1.0/Math.sqrt(1+t*t);
            s = t*c;
            tau = s/(1.0+c);
            h = t*m.matrix[ip][iq];
            z[ip] -= h;
            z[iq] += h;
            d.vector[ip] -= h;
            d.vector[iq] += h;
            m.matrix[ip][iq]=0.0;

            // Case of rotaions 1<=j<p
            for (j=0;j<ip;j++)
            {
              g=m.matrix[j][ip];
              h=m.matrix[j][iq];
              m.matrix[j][ip]=g-s*(h+g*tau);
              m.matrix[j][iq]=h+s*(g-h*tau);
            } 
            // Case of rotaions p<j<q
            for (j=ip+1;j<iq;j++) 
            {
              g=m.matrix[ip][j];
              h=m.matrix[j][iq];
              m.matrix[ip][j]=g-s*(h+g*tau);
              m.matrix[j][iq]=h+s*(g-h*tau);
            } 
            // Case of rotaions q<j<=n
            for (j=iq+1;j<n;j++) 
            {
              g=m.matrix[ip][j];
              h=m.matrix[iq][j];
              m.matrix[ip][j]=g-s*(h+g*tau);
              m.matrix[iq][j]=h+s*(g-h*tau);
            } 


            for (j=0;j<n;j++)
            {
              g=v.matrix[j][ip];
              h=v.matrix[j][iq];
              v.matrix[j][ip]=g-s*(h+g*tau);
              v.matrix[j][iq]=h+s*(g-h*tau);
            } 
            ++nrot;
          } 
        } 
      } 

      for (ip=0;ip<n;ip++)
      {
        b[ip] += z[ip];
        d.vector[ip]=b[ip];
        z[ip]=0.0;
      } 
    } 
    System.out.println("Too many iterations in routine JACOBI");
    return v;
  }

  /**
   * Solves a linear equation system with Gauss elimination.
   *
   * @cdk.keyword Gauss elimination
   */
  public static Vector elimination(Matrix matrix, Vector vector)
  {
    int i,j,k,ipvt;
    int n = vector.size;
    int[] pivot = new int[n];
    double c, temp;
    //double[] x = new double[n];
    Vector result = new Vector(n);
    Matrix a = matrix.duplicate();
    Vector b = vector.duplicate();
    
    for(j=0; j<(n-1); j++)
    {
      c = Math.abs(a.matrix[j][j]);
      pivot[j] = j;
      ipvt = j;
      for(i=j+1; i<n; i++)
        if (Math.abs(a.matrix[i][j])>c)
        {
          c = Math.abs(a.matrix[i][j]);
          ipvt = i;
        } 
        
      // Exchanges rows when necessary
      if (pivot[j]!=ipvt)
      {
        pivot[j] = ipvt;
        pivot[ipvt] = j;
        for(k=0; k<n; k++)
        {
          temp = a.matrix[j][k];
          a.matrix[j][k] = a.matrix[pivot[j]][k];
          a.matrix[pivot[j]][k] = temp;
        } 
        
        temp = b.vector[j];
        b.vector[j] = b.vector[pivot[j]];
        b.vector[pivot[j]] = temp;
      } 
      
      // Store multipliers
      for(i=j+1; i<n; i++)
        a.matrix[i][j] = a.matrix[i][j] / a.matrix[j][j];
        
      // Give elements below the diagonal a zero value
      for(i=j+1; i<n; i++)
      {
        for(k=j+1; k<n; k++)
          a.matrix[i][k] = a.matrix[i][k] - a.matrix[i][j]*a.matrix[j][k];
        b.vector[i] = b.vector[i] - a.matrix[i][j]*b.vector[j];
        
        a.matrix[i][j] = 0; // Not necessary
      } 
    } 
    
    // Rueckwaertseinsetzen (which is?)
    result.vector[n-1] = b.vector[n-1]/a.matrix[n-1][n-1];
    for(j=n-2; j>=0; j--)
    {
      result.vector[j] = b.vector[j];
      for(k=n-1; k>j; k--)
        result.vector[j] = result.vector[j] - result.vector[k]*a.matrix[j][k];
      result.vector[j] = result.vector[j]/a.matrix[j][j];
    } 
    
    return result;
  }

  /**
   * Orthonormalize the vectors of this matrix by Gram-Schmidt.
   *
   * @cdk.keyword orthonormalization
   * @cdk.keyword Gram-Schmidt algorithm
   */
  public Matrix orthonormalize(Matrix S)
  {
    int p,q,k,i,j;
    double innersum;
    double length;
    //Matrix scr = S.mul(this);
    Matrix result = duplicate();
    for(p=0; p<columns; p++) // Loops over all vectors
    {
      for(i=0; i<rows; i++)
        result.matrix[i][p] = matrix[i][p];

      for(k=0; k<p; k++)  // Substracts the previous vector 
      {
        // First the calculation of the product <phi_p|phi_k>=length
        length = 0;
        for(i=0; i<rows; i++) // Loops over all vectors
        { 
          innersum = 0;
          for(j=0; j<rows; j++)
          {
            innersum += result.matrix[j][p]*S.matrix[i][j];
          }
          length += result.matrix[i][k]*innersum;
        } 

        // Then the substraction of  phi_k*length
        for(q=0; q<rows; q++)
          result.matrix[q][p] -= result.matrix[q][k]*length;
      }

      // Calculates the integral for normalization
      length = 0;
      for(i=0; i<rows; i++)
        for(j=0; j<rows; j++)
          length += result.matrix[i][p]*result.matrix[j][p]*S.matrix[i][j];

      length = Math.sqrt(length);

      // Normalizes the vector
      if (length!=0d)
        for(q=0; q<rows; q++)
          result.matrix[q][p] /= length;
      else
        System.out.println("Warning(orthonormalize):"+(p+1)+". Vector has length null");
    }
    return result;
  }

  /**
   * Normalizes the vectors of this matrix.
   */
  public Matrix normalize(Matrix S)
  {
    int p,q,i,j;
    double length;
    Matrix result = duplicate();
    for(p=0; p<columns; p++) // Loops over all vectors
    {
      // Calculates the normalization factor
      length = 0;
      for(i=0; i<rows; i++)
        for(j=0; j<rows; j++)
          length += result.matrix[i][p]*result.matrix[j][p]*S.matrix[i][j];
          
      length = Math.sqrt(length);
      
      // Normalizes the vector
      if (length!=0d)
        for(q=0; q<rows; q++)
          result.matrix[q][p] /= length;
      else
        System.out.println("Warning(orthonormalize):"+(p+1)+". Vector has length null");
    }
    return result;
  }

}
