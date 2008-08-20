/* Complex.java
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
 * This class handles complex values.
 * 
 * @cdk.module qm
 */ 
public class Complex
{
  /** The real part of this value */
  public double real = 0;
  /** The imaginary part of this value */
  public double imag = 0;
  
  /**
   * Creates a complex number
   *
   * @param real the real part
   * @param imag the imaginary part
   */
  public Complex(double real, double imag)
  {
    this.real = real;
    this.imag = imag;
  }
  
  /**
   * Creates a copy of a complex object
   */
  public Complex(Complex c)
  {
    real = c.real;
    imag = c.imag;
  }
  
  /**
   * Sets the real part of this complex value
   */
  public void setRealPart(double real)
  {
    this.real = real;
  }
  
  /**
   * Gets the real part of this complex value
   */
  public double getRealPart()
  {
    return real;
  }
  
  /**
   * Sets the imaginary part of this value
   */
  public void setImaginaryPart(double imag)
  {
    this.imag = imag;
  }
  
  /**
   * Gets the imaginary part of this value
   */
  public double getImaginaryPart()
  {
    return imag;
  }
  
  /**
   * Add a complex value
   */
  public void add(Complex c)
  {
    real += c.real;
    imag += c.imag;
  }
  
  /**
   * Subtracs a complex value
   */
  public void sub(Complex c)
  {
    real -= c.real;
    imag -= c.imag;
  }
  
  /**
   * Multiply this value with a complex value
   */
  public void mul(Complex c)
  {
    double newreal = real*c.real-imag*c.imag;
    double newimag = real*c.imag+imag*c.real;
    real = newreal;
    imag = newimag;
  }
  
  /**
   * Div this value by a complex value
   */
  public void div(Complex c)
  {
    double modulus = c.real*c.real+c.imag*c.imag;
    double newreal = imag*c.imag+real*c.real;
    double newimag = imag*c.real-real*c.imag;
    real = newreal/modulus;
    imag = newimag/modulus;
  }
  
  /**
   * Create a string of the content of this class
   */
  public String toString()
  {
    return "("+real+"+i*"+imag+")";
  }
}
