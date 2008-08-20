/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2001-2007  Stephan Michels <stephan@vern.chem.tu-berlin.de>
 * 
 * Contact: cdk-devel@lists.sf.net
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
package org.openscience.cdk.math.qm;

import org.openscience.cdk.math.Matrix;
import org.openscience.cdk.math.Vector;
import org.openscience.cdk.tools.LoggingTool;
 
/** 
 * Calculates the orbitals and orbital energies of electron systems
 * without electron electron interactions
 * 
 * @author  Stephan Michels <stephan@vern.chem.tu-berlin.de>
 * @cdk.svnrev  $Revision$
 * @cdk.created 2001-09-03
 * @cdk.module  qm
 */
public class OneElectronJob
{
  private Orbitals orbitals;
  private Vector E;

  private LoggingTool log = new LoggingTool(OneElectronJob.class);

  /**
   * Constructs a one electron job
   */
  public OneElectronJob(Orbitals orbitals)
  { 
    this.orbitals = orbitals;
  }

  /**
   * Returns the energies of the orbitals
   */
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

  private Matrix calculateS(IBasis basis)
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
  private Matrix calculateT(IBasis basis)
  {
    int size = basis.getSize();
    Matrix J = new Matrix(size,size);
    int i,j; 
    for(i=0; i<size; i++)
      for(j=0; j<size; j++)
        // (1/2) * -<d^2/dx^2 chi_i | chi_j>
        J.matrix[i][j] = basis.calcJ(j,i)/2; // Attention indicies are exchanged

    return J;
  }

  /**
   * Calculates the matrix for the potential matrix
   *
   * V_i,j = <chi_i | 1/r | chi_j>
   */
  private Matrix calculateV(IBasis basis)
  {
    int size = basis.getSize();
    Matrix V = new Matrix(size,size);
    int i,j;
    for(i=0; i<size; i++)
      for(j=0; j<size; j++)
        V.matrix[i][j] = basis.calcV(i,j);

    return V;
  }

  public Orbitals calculate()
  {
    long time = System.currentTimeMillis();

    Matrix C,S,T,V,HAO,H,U;
    IBasis basis = orbitals.getBasis();

    //int count_electrons = orbitals.getCountElectrons();

    C = orbitals.getCoefficients().duplicate();

    S = calculateS(basis);

    log.debug("S = \n"+S+"\n");

    log.debug("C = \n"+C+"\n");

    C = C.orthonormalize(S);
    log.debug("C' = \n"+C+"\n");
    log.debug("C't * S * C' = \n"+S.similar(C)+"\n");

    T = calculateT(basis);
    log.debug("T = \n"+T+"\n");

    V = calculateV(basis);
    log.debug("V = \n"+V+"\n");

    HAO = T.add(V);
    log.debug("HAO = \n"+HAO+"\n");
 
    H = HAO.similar(C);
    log.debug("H = C't * HAO * C' = \n"+H.similar(C)+"\n");

    U = H.diagonalize(50);
    E = H.similar(U).getVectorFromDiagonal();
    C = C.mul(U);
    sort(C,E);
    log.debug("C(neu) = \n"+C+"\n");
    log.debug("E = \n"+E+"\n");

    for(int j=0; j<E.size; j++)
      log.debug("E("+(j+1)+".Orbital)="+(E.vector[j]*27.211)+" eV");

    time = System.currentTimeMillis()-time;
    log.debug("Time = "+time+" ms");
    time = System.currentTimeMillis();

    return new Orbitals(basis, C);
  }
}
