/* Basis.java
 * 
 * Autor: Stephan Michels 
 * EMail: stephan@vern.chem.tu-berlin.de
 * Datum: 2.7.2001
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

/**
 * A base must implement this class
 */ 
public interface Basis
{
  /**
   * Get the count of base vectors
   */
  public int getSize();

  /**
   * Get the dimension of the volume, which descripes the base
   */
  public double getMinX();

  /**
   * Get the dimension of the volume, which descripes the base
   */
  public double getMaxX();

  /**
   * Get the dimension of the volume, which descripes the base
   */
  public double getMinY();

  /**
   * Get the dimension of the volume, which descripes the base
   */
  public double getMaxY();

  /**
   * Get the dimension of the volume, which descripes the base
   */
  public double getMinZ();

  /**
   * Get the dimension of the volume, which descripes the base
   */
  public double getMaxZ();

  /**
   * Calculates the function value an (x,y,z)
   * @param index The number of the base 
   */
  public double getValue(int index, double x, double y, double z);

  /**
   * Calculates the function values
   * @param index The number of the base 
   */
  public Vector getValues(int index, Matrix x);

  /**
   * Calculate the "Überlappungsintegrale"
   * S = <phi_i|phi_j>
   *
   * @param i Index of the first base
   * @param j Index of the second base
   */
  public double calcS(int i, int j);

  /**
   * Calculates the impulse
   * J = -<d/dr chi_i | d/dr chi_j>
   *
   * @param i Index of the first base
   * @param j Index of the second base
   */
  public double calcJ(int i, int j);

  /**
   * Calculates the potential
   * V = <chi_i | 1/r | chi_j>
   *
   * @param i Index of the first base
   * @param j Index of the second base
   */
  public double calcV(int i, int j);

  /**
   * Calculates a two eletron fout center integral
   * I = <chi_i chi_j | 1/r12 | chi_k chi_l >
   *
   * @param i Index of the first base
   * @param j Index of the second base
   * @param k Index of the third base
   * @param l Index of the fourth base
   */
  public double calcI(int i, int j, int k, int l);
}
