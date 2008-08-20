/* IVector.java
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

/**
 * @cdk.module qm
 */
public class IVector
{
  // Attention! The variables are unprotected
  /** The real part of this vector */
  public double[] realvector;
  /** The imaginary part of this vector */
  public double[] imagvector;

  /** Size of this vector */
  public int size;

  /**
   * Constructs a vector with "size"-elements
   */
  public IVector(int size)
  {
    realvector = new double[size];
    imagvector = new double[size];
    this.size = size;
  }

  /**
   * Return the size from this vector
   */
  public int getSize()
  {
    return size;
  }

  /**
   *  Addition from two vectors
   */
  public IVector add(IVector b)
  {
    IVector result = new IVector(size);
    add(b, result);
    return result;
  }

  /**
   *  Addition from two vectors
   */
  public void add(IVector b, IVector result)
  {
    if ((b==null) ||
         (size!=b.size))
      return;
      
    if (result.size!=size)
      result.reshape(size);

    int i;
    for(i=0; i<size; i++)
    {
      result.realvector[i] = realvector[i]+b.realvector[i];
      result.imagvector[i] = imagvector[i]+b.imagvector[i];
    }
  }

  /**
   *  Subtraction from two vectors
   */
  public IVector sub(IVector b)
  {
    IVector result = new IVector(size);
    sub(b, result); 
    return result;
  } 
  
  /**
   *  Subtraction from two vectors
   */
  public void sub(IVector b, IVector result)
  {
    if ((b==null) ||
        (size!=b.size))
      return;
      
    if (result.size!=size)
      result.reshape(size);

    int i;
    for(i=0; i<size; i++)
    {
      result.realvector[i] = realvector[i]-b.realvector[i];
      result.imagvector[i] = imagvector[i]-b.imagvector[i];
    }
  }
  
  /**
   *  Multiplication from a vectors with an double
   */
  public IVector mul(double b)
  {
    IVector result = new IVector(size);
    mul(b, result); 
    return result;
  } 
  
  /**
   *  Multiplication from a vectors with an double
   */
  public void mul(double b, IVector result)
  {
    if (result.size!=size)
      result.reshape(size);

    int i;
    for(i=0; i<size; i++)
    {
      result.realvector[i] = realvector[i]*b;
      result.imagvector[i] = imagvector[i]*b;
    }
  }

  /**
   *  Multiplication from two vectors
   */
  public Complex dot(IVector b)
  {
    if ((b==null) ||
        (size!=b.size))
      return new Complex(Double.NaN,Double.NaN);

    Complex result = new Complex(0d,0d);
    int i;
    for(i=0; i<size; i++)
    {
      result.real += realvector[i]*b.realvector[i]-imagvector[i]*b.imagvector[i];
      result.imag += realvector[i]*b.imagvector[i]+imagvector[i]*b.realvector[i];
    }
    return result;
  }

  /**
   *  Copy a vector
   */
  public IVector duplicate()
  {
    IVector result = new IVector(size);
    duplicate(result); 
    return result;
  } 
  
  /**
   *  Copy a vector
   */
  public void duplicate(IVector result)
  { 
    if (result.size!=size)
      result.reshape(size);

    int i; 
    for(i=0; i<size; i++)
    {
      result.realvector[i] = realvector[i];
      result.imagvector[i] = imagvector[i];
    }
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
      str.append(realvector[i]+"+i*"+imagvector[i]+" ");
    str.append(realvector[size-1]+"+i*"+imagvector[size-1]+" ]");
    return str.toString();
  }

  /**
   * Resize this vector
   */
  public void reshape(int newsize)
  {
    if ((newsize==size) || (newsize<=0))
      return;

    double[] newrealvector = new double[newsize];
    double[] newimagvector = new double[newsize];
    int min = Math.min(size,newsize);
    int i;
    for(i=0; i<min; i++)
    {
      newrealvector[i] = realvector[i];
      newimagvector[i] = imagvector[i];
    }
    for(i=min; i<newsize; i++)  
    {
      newrealvector[i] = 0d;
      newimagvector[i] = 0d;
    }

    realvector = newrealvector;
    imagvector = newimagvector;
    size = newsize;
  }
}
