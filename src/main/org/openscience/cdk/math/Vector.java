/* $Revision$ $Author$ $Date$
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

import javax.vecmath.Tuple3d;
 
/**
 * This class handles vectors.
 * 
 * @cdk.module  qm
 */
public class Vector
{
  /** Null vector in 3 dimensional space */
  public final static Vector NULLVECTOR = new Vector(new double[] {0d,0d,0d});
  /** Unary vector in 3 dimensional space */
  public final static Vector EX = new Vector(new double[] {1d,0d,0d});
  /** Unary vector in 3 dimensional space */
  public final static Vector EY = new Vector(new double[] {0d,1d,0d});
  /** Unary vector in 3 dimensional space */
  public final static Vector EZ = new Vector(new double[] {0d,0d,1d});

  // Attention! Variables are unprotected
  /** The content of this vector */
  public double[] vector;
  /** The size of this vector */
  public int size;

  /**
   * Constructs a Vector with "size" elements
   */
  public Vector(int size)
  {
    vector = new double[size];
    this.size = size;
  }

  /**
   * Constructs a vector with the content of a array
   */
  public Vector(double[] array)
  {
    vector = array;
    size = array.length;
  }

  /**
   * Constructs a Vector with a Tuple3d, Vector3d or Point3d
   */
  public Vector(Tuple3d t)
  { 
    vector = new double[3];
    size = 3;
    vector[0] = t.x; vector[1] = t.y; vector[2] = t.z;
  }

  /**
   * Returns the size of this vector
   */
  public int getSize()
  {
    return size;
  }

  /**
   *  Addition from two vectors
   */
  public Vector add(Vector b)
  {
    if ((b==null) ||
         (size!=b.size))
      return null;
      
    int i;
    Vector result = new Vector(size);
    for(i=0; i<size; i++)
      result.vector[i] = vector[i]+b.vector[i];
    return result;
  }

  /**
   *  Subtraktion from two vectors
   */
  public Vector sub(Vector b)
  {
    if ((b==null) ||
        (size!=b.size))
      return null;
      
    int i;
    Vector result = new Vector(size);
    for(i=0; i<size; i++)
      result.vector[i] = vector[i]-b.vector[i];
    return result;
  }
  
  /**
   *  Multiplikation from a vectors with an double
   */
  public Vector mul(double b)
  {
    Vector result = new Vector(size);
    int i;
    for(i=0; i<size; i++)
      result.vector[i] = vector[i]*b;
    return result;
  }

  /**
   *  Multiplikation from two vectors
   */
  public double dot(Vector b)
  {
    if ((b==null) ||
        (size!=b.size))
      return Double.NaN;

    double result = 0;
    int i;
    for(i=0; i<size; i++)
      result += vector[i]*b.vector[i];
    return result;
  }

  /**
   * Cross product, only well definited in R^3
   */
  public Vector cross(Vector b)
  {
    if ((b==null) ||
        (size!=3) || (b.size!=3))
      return null;

    Vector result = new Vector(3);
    result.vector[0] = vector[1]*b.vector[2]-vector[2]*b.vector[1];
    result.vector[1] = vector[2]*b.vector[0]-vector[0]*b.vector[2];
    result.vector[2] = vector[0]*b.vector[1]-vector[1]*b.vector[0];
    return result;
  }

  /**
   * Return the length from this vector
   */
  public double length()
  {
    double value = 0;
    for(int i=0; i<size; i++)
      value += vector[i]*vector[i];
    return Math.sqrt(value);
  }

  /**
   * Normalize this vector
   */
  public Vector normalize()
  {
    Vector result = new Vector(size);
    double length = length();
    for(int i=0; i<size; i++)
      result.vector[i] = vector[i]/length;
    return result;
  }

  /**
   * Negates this vector
   */
  public Vector negate()
  {
    Vector result = new Vector(size);
    for(int i=0; i<size; i++)
      result.vector[i] = -vector[i];
    return result;
  }

  /**
   *  Copy a vector
   */
  public Vector duplicate()
  { 
    Vector result = new Vector(size);
    int i; 
    for(i=0; i<size; i++)
      result.vector[i] = vector[i];
    return result;
  }
  
  /**
   *  Return a vector as a string
   */
  public String toString()
  {
    int i;
    StringBuffer str = new StringBuffer();
    str.append("[ ");
    for(i=0; i<(size-1); i++)
      str.append(vector[i]+" ");
    str.append(vector[size-1]+" ]");
    return str.toString();
  }
}
