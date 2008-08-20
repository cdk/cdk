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

import org.openscience.cdk.math.Complex;
import org.openscience.cdk.math.IMatrix;
import org.openscience.cdk.math.Matrix;
import org.openscience.cdk.math.Vector;

/**
 * This class is used to calculate angular momentum states.
 * 
 * @author  Stephan Michels <stephan@vern.chem.tu-berlin.de>
 * @cdk.svnrev  $Revision$
 * @cdk.created 2001-07-22
 * @cdk.module  qm
 */ 
public class AngularMomentum {
  private double J;
  private int size;
  private Matrix basis;

  public AngularMomentum(double J)
  {
    this.J = J;
    size = (int)Math.round(J*2.0+1.0);
    basis = new Matrix(size,size);
    int i,j;
    for(i=0; i<size; i++)
      for(j=0; j<size; j++)
        basis.matrix[i][j] = 0d;
    for(i=0; i<size; i++)
      basis.matrix[i][i] = 1d;
  }

  /**
   * Calculates the Ix operator
   */
  public IMatrix getIx()
  { 
    return (new IMatrix(getIplus().add(getIminus()))).mul(new Complex(0.5,0d));
  }

  /**
   * Calculates the Iy operator
   */
  public IMatrix getIy()
  {
    return (new IMatrix(getIplus().sub(getIminus()))).mul(new Complex(0d,1d)).mul(new Complex(0.5,0d));
  }

  /**
   * Calculates the Iz operator
   */
  public IMatrix getIz()
  {
    IMatrix Iz = new IMatrix(size,size);
    int i,j;
    for(i=0; i<size; i++)
      for(j=0; j<size; j++)
      {
        Iz.realmatrix[i][j] = 0d;
        Iz.imagmatrix[i][j] = 0d;
      }
    for(i=0; i<size; i++)
    {
      Iz.realmatrix[i][i] = J-i;
      Iz.imagmatrix[i][i] = J-i;
    }
    return Iz;
  }

  /**
   * Calculates the I+ operator
   */
  public Matrix getIplus()
  {
    Matrix Iplus = new Matrix(size,size);
    int i,j;
    for(i=0; i<size; i++)
      for(j=0; j<size; j++)
        Iplus.matrix[i][j] = 0d;
    for(i=1; i<size; i++)
      Iplus.matrix[i-1][i] = Math.sqrt(J*J+J-(J-i+1)*(J-i+1)+(J-i+1));
    return Iplus;
  }

  /**
   * Calculates the I- operator
   */
  public Matrix getIminus()
  {
    Matrix Iminus = new Matrix(size,size);
    int i,j;
    for(i=0; i<size; i++)
      for(j=0; j<size; j++)
        Iminus.matrix[i][j] = 0d;
    for(i=1; i<size; i++)
      Iminus.matrix[i][i-1] = Math.sqrt(J*J+J-(J-i)*(J-i)-(J-i));
    return Iminus;
  }

  /**
   * Calculates a spin vector by a direction specified by theta and phi
   */
  public Vector getSpinVector(double theta, double phi)
  {
    Vector spinvector = new Vector(3);
    spinvector.vector[0] = Math.sin(theta)*Math.cos(phi);
    spinvector.vector[1] = Math.sin(theta)*Math.sin(phi);
    spinvector.vector[2] = Math.cos(phi);
    return spinvector;
  }
}

