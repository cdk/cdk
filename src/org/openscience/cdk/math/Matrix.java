/* Matrix.java
 * 
 * Autor: Stephan Michels 
 * EMail: stephan@vern.chem.tu-berlin.de
 * Datum: 7.6.2001
 * 
 * Copyright (C) 1997-2002  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *  */

package org.openscience.cdk.math;
 
import java.io.StreamTokenizer;
import java.io.InputStream;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 * This class contains a matrix
 */
public class Matrix
{
  // Attention! Variables are unprotected
  /** the content of this matrix **/
  public double[][] matrix;

  /** the count of rows of this matrix */
  public int rows;
  /** the count of columns of this matrix */
  public int columns;

  /**
   * Creates a matrix
   */ 
  public Matrix(int rows, int columns)
  {
    this.rows = rows;
    this.columns = columns;
    matrix = new double[rows][columns];
  }

  /**
   * Creates a matrix with content of an array
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
   * Returns the count of rows
   */ 
  public int getRows()
  {
    return rows;
  }

  /**
   * Returns the count of columns
   */
  public int getColumns()
  {
    return columns;
  }

  /**
   * Creates a vector with the content of a row from this matrix
   */
  public Vector getVectorFromRow(int index)
  {
    double[] row = new double[columns];
    for(int i=0; i<columns; i++)
      row[i] = matrix[index][i];
    return new Vector(row);
  }

  /**
   * Creates a vector with the content of a column from this matrix
   */
  public Vector getVectorFromColumn(int index)
  {
    double[] column = new double[rows];
    for(int i=0; i<rows; i++)
      column[i] = matrix[i][index];
    return new Vector(column);
  }

  /**
   * Creates a vector with the content of the diagonal elements from this matrix
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
   *  Addition from two matrices
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
   *  Subtraktion from two matrices
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
   *  Multiplikation from two matrices
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
   *  Multiplikation from a vector and a matrix
   */
  public Vector mul(Vector a)
  {
    if ((a==null) ||
        (columns!=a.size))
      return null;

    Vector result = new Vector(rows);
    int i,j,k;
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
   *  Multiplikation from a scalar and a matrix
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
   *  Copy a matrix
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
   *  Transpose a matrix
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
   *  Return a matrix as a string
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

      // Fertig ??
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
   * Solves a linear equationsystem with Gauss elimination
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
        
      // Vertausche Zeilen, wenn notwendig
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
      
      // Multiplikatoren speichern
      for(i=j+1; i<n; i++)
        a.matrix[i][j] = a.matrix[i][j] / a.matrix[j][j];
        
      // Bildung von Nullen unter Diagonalen
      for(i=j+1; i<n; i++)
      {
        for(k=j+1; k<n; k++)
          a.matrix[i][k] = a.matrix[i][k] - a.matrix[i][j]*a.matrix[j][k];
        b.vector[i] = b.vector[i] - a.matrix[i][j]*b.vector[j];
        
        a.matrix[i][j] = 0; // Nicht notwendig
      } 
    } 
    
    // Rückwärtseinsetzen
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
   * Orthonormalize the vectors of this matrix by Gram-Schmidt
   */
  public Matrix orthonormalize(Matrix S)
  {
    int p,q,k,i,j;
    double sum;
    double innersum;
    double c;
    double length;
    //Matrix scr = S.mul(this);
    Matrix result = duplicate();
    for(p=0; p<columns; p++) // Geht alle Vektoren in dieser Matrix durch
    {
      for(i=0; i<rows; i++)
        result.matrix[i][p] = matrix[i][p];

      for(k=0; k<p; k++)  // Substrahiert die vorherriegen Vektoren 
      {
        // Zuerst die Berechnung des Produktes <phi_p|phi_k>=length
        length = 0;
        for(i=0; i<rows; i++) // Geht alle Komponenten der Vektoren durch
        { 
          innersum = 0;
          for(j=0; j<rows; j++)
          {
            innersum += result.matrix[j][p]*S.matrix[i][j];
          }
          length += result.matrix[i][k]*innersum;
        } 

        // Dann die Subtraktion von phi_k*length
        for(q=0; q<rows; q++)
          result.matrix[q][p] -= result.matrix[q][k]*length;
      }

      //Berechnet das Intergal für die normierung  
      length = 0;
      for(i=0; i<rows; i++)
        for(j=0; j<rows; j++)
          length += result.matrix[i][p]*result.matrix[j][p]*S.matrix[i][j];

      length = Math.sqrt(length);

      //Normiert den Vektor
      if (length!=0d)
        for(q=0; q<rows; q++)
          result.matrix[q][p] /= length;
      else
        System.out.println("Warning(orthonormalize):"+(p+1)+".Vektor hat die Länge null");
    }
    return result;
  }

  /**
   * Normalizes the vectors of this matrix
   */
  public Matrix normalize(Matrix S)
  {
    int p,q,i,j;
    double length;
    Matrix result = duplicate();
    for(p=0; p<columns; p++) // Geht alle Vektoren in dieser Matrix durch
    {
      //Berechnet das Intergal für die normierung 
      length = 0;
      for(i=0; i<rows; i++)
        for(j=0; j<rows; j++)
          length += result.matrix[i][p]*result.matrix[j][p]*S.matrix[i][j];
          
      length = Math.sqrt(length);
      
      //Normiert den Vektor
      if (length!=0d)
        for(q=0; q<rows; q++)
          result.matrix[q][p] /= length;
      else
        System.out.println("Warning(orthonormalize):"+(p+1)+".Vektor hat die Länge null");
    }
    return result;
  }

  /*public static Matrix read(InputStream in) throws IOException
  { 
    StreamTokenizer stok = new StreamTokenizer(in);
    stok.resetSyntax();
    stok.commentChar('!');
    stok.whitespaceChars(0, 32);
    stok.wordChars(34, 126);
    stok.parseNumbers();
    stok.eolIsSignificant(true);
    
    int rows = (int) readValue(stok);
    int columns = (int) readValue(stok);
    Matrix result = new Matrix(rows, columns);
    for(int i=0; i<rows; i++)
      for(int j=0; j<columns; j++)
        result.matrix[i][j] = readValue(stok);
    return result;
  }

  private static double readValue(StreamTokenizer stok) throws IOException
  {
    int type;
    do
    {
      type = stok.nextToken();
    } while ((type!=stok.TT_NUMBER) &&  (type!=stok.TT_EOF));

    if (type==stok.TT_NUMBER)
      return stok.nval;

    stok.pushBack();
    return Double.NaN;
  }*/
}
