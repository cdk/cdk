/* Job.java
 * 
 * Autor: Stephan Michels 
 * EMail: stephan@vern.chem.tu-berlin.de
 * Datum: 14.6.2001
 * 
 * Copyright (C) 1997-2002  The Chemistry Development Kit (CDK) project
 * 
 * Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
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

package org.openscience.cdk.math.qm;

import org.openscience.cdk.math.*;
import org.openscience.cdk.io.LogWriter;

/**
 * Calculates the orbitals and orbitalenergies of electron systems
 * with closed shells
 */
public class ClosedShellJob
{
  private Orbitals orbitals;
  private Vector E;

  private LogWriter log = new LogWriter(System.out);

  private int iterations = 0;

  public ClosedShellJob(Orbitals orbitals)
  { 
    this.orbitals = orbitals;
  }

  public LogWriter getLog()
  {
    return log;
  }

  public Vector getEnergies()
  {
    return E.duplicate();
  }

  /**
   * Sorts the orbitals by their energies
   */
  private void sort(Matrix C, Vector E)
  {
    int i,j;
    double value;
    boolean changed;
    do
    {
      changed = false;
      for(i=1; i<E.size; i++)
        if (E.vector[i-1]>E.vector[i])
        {
          value = E.vector[i];
          E.vector[i] = E.vector[i-1];
          E.vector[i-1] = value;

          for(j=0; j<C.rows; j++)
          {
            value = C.matrix[j][i];
            C.matrix[j][i] = C.matrix[j][i-1];
            C.matrix[j][i-1] = value;
          }
          changed = true;
        }
    } while (changed);
  }

  private Matrix calculateS(Basis basis)
  {
    int size = basis.getSize();
    Matrix S = new Matrix(size,size);
    int i,j;
    for(i=0; i<size; i++)
      for(j=0; j<size; j++)
        S.matrix[i][j] = basis.calcS(i,j);

    return S;
  }

  /**
   * Calculates the matrix for the kinetic energy
   *
   * T_i,j = (1/2) * -<d^2/dx^2 chi_i | chi_j>
   */
  private Matrix calculateT(Basis basis)
  {
    int size = basis.getSize();
    Matrix J = new Matrix(size,size);
    int i,j; 
    for(i=0; i<size; i++)
      for(j=0; j<size; j++)
        // (1/2) * -<d^2/dx^2 chi_i | chi_j>
        J.matrix[i][j] = basis.calcJ(j,i)/2; // Vorsicht indizies sind vertauscht

    return J;
  }

  /**
   * Calculates the matrix for the potential matrix
   *
   * V_i,j = <chi_i | 1/r | chi_j>
   */
  private Matrix calculateV(Basis basis)
  {
    int size = basis.getSize();
    Matrix V = new Matrix(size,size);
    int i,j;
    for(i=0; i<size; i++)
      for(j=0; j<size; j++)
        V.matrix[i][j] = basis.calcV(i,j);

    return V;
  }

  /**
   * Calculates thes values for the 2 electron interactions
   */
  private double[][][][] calculateI(Basis basis)
  {
    int i,j,k,l;
    int p = 0;
    int size = basis.getSize();

    double[][][][] result = new double[size][][][];
    for(i=0; i<size; i++)
    {
      result[i] = new double[i+1][][];
      for(j=0; j<=i; j++)
      {
        result[i][j] = new double[size][];
        for(k=0; k<size; k++) 
        {
          result[i][j][k] = new double[k+1];
          for(l=0; l<=k; l++)
          {
            result[i][j][k][l] = basis.calcI(i,j,k,l);
            //log.println("("+(i+1)+" "+(j+1)+"|"+(k+1)+" "+(l+1)+")="+result[i][j][k][l]);
          }
        }
      }
    }
    return result;
  }

  /**
   * Calculates the density matrix
   */
  private Matrix calculateD(Basis basis, Matrix C, int count_electrons)
  {
    int i,j,k;
    int size = basis.getSize();
    int orbitals = C.getColumns();
    int occ = count_electrons/2;
    int locc = count_electrons%2;
    Matrix D = new Matrix(size,size);
    log.println("D:occ="+occ+" locc="+locc);

    if (locc!=0)
      System.out.println("This class work only correct for closed shells");

    for(i=0; i<size; i++)
      for(j=0; j<size; j++)
      {
        D.matrix[i][j] = 0d;
        for(k=0; (k<orbitals) && (k<occ); k++)
          D.matrix[i][j] += 2d*C.matrix[i][k]*C.matrix[j][k];

        if ((locc==1) && (k+1<orbitals))
          D.matrix[i][j] += C.matrix[i][k+1]*C.matrix[j][k+1];
      }
    return D;
  }

  private Matrix calculateJ(Basis basis, double[][][][] I, Matrix D)
  { 
    int i,j,k,l;
    int size = basis.getSize();
    double value;
    Matrix J = new Matrix(size,size);
    for(i=0; i<size; i++)
      for(j=0; j<size; j++)
      {
        J.matrix[i][j] = 0;
        for(k=0; k<size; k++)
          for(l=0; l<size; l++)
          {
            if (i>=j)
            {
              if (k>=l)
                J.matrix[i][j] += D.matrix[k][l]*I[i][j][k][l];
              else
                J.matrix[i][j] += D.matrix[k][l]*I[i][j][l][k];
            }
            else
            {
              if (k>=l)
                J.matrix[i][j] += D.matrix[k][l]*I[j][i][k][l];
              else
                J.matrix[i][j] += D.matrix[k][l]*I[j][i][l][k];
            }
          }
        J.matrix[i][j] *= 2d;
      }
    return J;
  }

  private Matrix calculateK(Basis basis, double[][][][] I, Matrix D)
  { 
    int i,j,k,l;
    int size = basis.getSize();
    double value;
    Matrix K = new Matrix(size,size);
    for(i=0; i<size; i++)
      for(j=0; j<size; j++)
      { 
        K.matrix[i][j] = 0;
        for(k=0; k<size; k++)
          for(l=0; l<size; l++)
          { 
            if (i>=j)
            {
              if (k>=l)
                K.matrix[i][j] += D.matrix[k][l]*I[i][j][k][l];
              else
                K.matrix[i][j] += D.matrix[k][l]*I[i][j][l][k];
            }
            else
            {
              if (k>=l)
                K.matrix[i][j] += D.matrix[k][l]*I[j][i][k][l];
              else
                K.matrix[i][j] += D.matrix[k][l]*I[j][i][l][k];
            }
          } 
      }   
    return K;
  }

  private double contraction(Matrix A, Matrix B)
  {
    int i,j;
    double result = 0;
    for(i=0; i<A.rows; i++)
      for(j=0; j<A.columns; j++)
        result += A.matrix[i][j]*B.matrix[i][j];
    return result;
  }
  
  public Orbitals calculate()
  {
    long time = System.currentTimeMillis();

    Matrix C,S,T,V,HAO,H,D,J,K,F,U;
    double[][][][] I;
    double energy;
    Basis basis = orbitals.getBasis();

    int count_electrons = orbitals.getCountElectrons();

    C = orbitals.getCoefficients().duplicate();

    S = calculateS(basis);

    log.println("S = \n"+S+"\n");

    log.println("C = \n"+C+"\n");

    C = C.orthonormalize(S);
    log.println("C' = \n"+C+"\n");
    log.println("C't * S * C' = \n"+S.similar(C)+"\n");

    T = calculateT(basis);
    log.println("T = \n"+T+"\n");

    V = calculateV(basis);
    log.println("V = \n"+V+"\n");

    HAO = T.add(V);
    log.println("HAO = \n"+HAO+"\n");
 
    H = HAO.similar(C);
    log.println("H = C't * HAO * C' = \n"+H.similar(C)+"\n");

    U = H.diagonalize(50);
    E = H.similar(U).getVectorFromDiagonal();
    C = C.mul(U);
    sort(C,E);
    log.println("C(neu) = \n"+C+"\n");
    log.println("E = \n"+E+"\n");

    for(int j=0; j<E.size; j++)
      log.println("E("+(j+1)+".Orbital)="+(E.vector[j]*27.211)+" eV");
    log.println();

    time = System.currentTimeMillis()-time;
    log.println("Time = "+time+" ms");
    time = System.currentTimeMillis();

    if (iterations>0)
      I = calculateI(basis);
    else
      I = null;

    for(int i=0; i<iterations; i++)
    {
      log.println((i+1)+".Durchlauf\n");

      time = System.currentTimeMillis();

      log.println("C't * S * C' = \n"+S.similar(C)+"\n");

      log.println("count of electrons = "+count_electrons+"\n");

      D = calculateD(basis, C, count_electrons);
      log.println("D = \n"+D+"\n");

      //log.println("2*contraction(D*S) = "+(D.mul(S)).contraction()*2+"\n");
      log.println("2*contraction(D*S) = "+contraction(D,S)*2+"\n");

      //J = calculateJ(basis, D);
      J = calculateJ(basis, I, D);
      log.println("J = \n"+J+"\n");

      //K = calculateK(basis, D);
      K = calculateK(basis, I, D);
      log.println("K = \n"+K+"\n");

      F = HAO.add(J).sub(K);
      log.println("F = H+J-K = \n"+F+"\n");
      
      H = F.similar(C);
      log.println("H = C't * F * C' = \n"+H+"\n");

      U = H.diagonalize(50);
      E = H.similar(U).getVectorFromDiagonal();
      C = C.mul(U);
      sort(C,E);
      log.println("C(neu) = \n"+C+"\n");
      log.println("E = \n"+E+"\n");

      for(int j=0; j<E.size; j++)
        log.println("E("+(j+1)+".Orbital)="+(E.vector[j]*27.211)+" eV");
      log.println();

      energy = contraction(D,HAO.add(F));
      log.println("Gesamtenergie = "+energy+" ("+energy*27.211+" eV)\n");

      time = System.currentTimeMillis()-time;
      log.println("Time = "+time+" ms");

      System.gc();
    }

    return new Orbitals(basis, C);
  }
}
