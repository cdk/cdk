/* Orbitals.java
 * 
 * Autor: Stephan Michels 
 * EMail: stephan@vern.chem.tu-berlin.de
 * Datum: 14.6.2001
 * 
 * Copyright (C) 1997-2001  The Chemistry Development Kit (CDK) project
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
 
public class Orbitals
{
  private int count_basis;
  private int count_orbitals;
  private Matrix C;
  private Basis basis;
  private int count_electrons = 1;

  public Orbitals(Basis basis)
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

  public Orbitals(Basis basis, Matrix C)
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

  public void setCoefficients(Matrix C)
  {
    if (count_basis==C.rows)
    {
      this.C = C;
      count_orbitals = C.columns;
    }
  }

  public Matrix getCoefficients()
  {
    return C;
  }

  public double getValue(int index, double x, double y, double z)
  {
    double sum = 0;
    for(int i=0; i<count_basis; i++)
      if (C.matrix[i][index]!=0d)
        sum += C.matrix[i][index]*basis.getValue(i, x, y, z);
    return sum;
  }

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

  public Basis getBasis()
  {
    return basis;
  }

  public int getCountBasis()
  {
    return count_basis;
  }

  public int getCountOrbitals()
  {
    return count_orbitals;
  }

  public void setCountElectrons(int count)
  {
    if (count>0)
      count_electrons = count;
  }

  public int getCountElectrons()
  {
    return count_electrons;
  }
}
