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
 
/**
 * This class represents a set of orbitals
 * 
 * @author  Stephan Michels <stephan@vern.chem.tu-berlin.de>
 * @cdk.svnrev  $Revision$
 * @cdk.created 2001-06-14
 * @cdk.module  qm
 */
public class Orbitals
{
  private int count_basis;
  private int count_orbitals;
  private Matrix C;
  private IBasis basis;
  private int count_electrons = 1;

  /**
   * Constructs orbitals with a specified base set
   */
  public Orbitals(IBasis basis)
  { 
    this.basis = basis;
    count_orbitals = count_basis = basis.getSize();
    C = new Matrix(count_basis, count_basis);
    for(int i=0; i<count_basis; i++)
      for(int j=0; j<count_basis; j++)
        if (i==j)
          C.matrix[i][j] = 1d;
        else
          C.matrix[i][j] = 0d;
  }

  /**
   * Constructs orbitals with a specified base set and a coefficient matrix
   */
  public Orbitals(IBasis basis, Matrix C)
  {
    this.basis = basis;
    count_basis = basis.getSize();
    if (count_basis==C.rows)
    {
      this.C = C;
      count_orbitals = C.columns;
    }
    else
    {
      this.C = new Matrix(count_basis, count_basis);
      for(int i=0; i<count_basis; i++)
        for(int j=0; j<count_basis; j++)
          if (i==j)
            this.C.matrix[i][j] = 1d;
          else
            this.C.matrix[i][j] = 0d;
      count_orbitals = count_basis;
    }
  }

  /**
   * Set a coefficient matrix
   */
  public void setCoefficients(Matrix C)
  {
    if (count_basis==C.rows)
    {
      this.C = C;
      count_orbitals = C.columns;
    }
  }

  /**
   * Get the coefficient matrix
   */
  public Matrix getCoefficients()
  {
    return C;
  }

  /**
   * Get the function value of a orbital at the position (x,y,z)
   */
  public double getValue(int index, double x, double y, double z)
  {
    double sum = 0;
    for(int i=0; i<count_basis; i++)
      if (C.matrix[i][index]!=0d)
        sum += C.matrix[i][index]*basis.getValue(i, x, y, z);
    return sum;
  }

  /**
   * Get the function value of a orbital
   */
  public Vector getValues(int index, Matrix m)
  {
    if (m.rows!=3)
      return null;

    Vector result = basis.getValues(0, m).mul(C.matrix[0][index]);
    for(int i=1; i<count_basis; i++)
      if (C.matrix[i][index]!=0d)
        result.add(basis.getValues(i, m).mul(C.matrix[0][index]));
    return result;
  }

  /**
   * Returns the basis set of this orbitals
   */
  public IBasis getBasis()
  {
    return basis;
  }

  /**
   * Set the count of bases
   */
  public int getCountBasis()
  {
    return count_basis;
  }

  /**
   * Returns the count of orbitals
   */
  public int getCountOrbitals()
  {
    return count_orbitals;
  }

  /**
   * Sets the count of electrons, which occupies the orbitals
   */
  public void setCountElectrons(int count)
  {
    if (count>0)
      count_electrons = count;
  }

  /**
   * Gets the count of electrons, which occupies the orbitals
   */
  public int getCountElectrons()
  {
    return count_electrons;
  }
}
