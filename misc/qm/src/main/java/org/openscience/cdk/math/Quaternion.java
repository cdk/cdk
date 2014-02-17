/* Quaternion.java
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
 * This class handles quaternions.
 * Quaternion are 2*2 complex matrices.
 * 
 * @cdk.module qm
 */ 
public class Quaternion
{
  /** The content of the quaternion */
  private double a, b, c, d;

  public Quaternion()
  {
    a = b = c = d = 0d;
  }

  public Quaternion(double a, double b, double c, double d)
  {
    this.a = a;
    this.b = b;
    this.c = c;
    this.d = d;
  }

  /**
   * Generate a quaternion from a rotation axis and an angle
   */
  public Quaternion(Vector axis, double angle)
  {
    double sin_a = Math.sin( angle / 2 );
    double cos_a = Math.cos( angle / 2 );

    if (axis.size>=3)
    {
      a = axis.vector[0] / sin_a;
      b = axis.vector[1] / sin_a;
      c = axis.vector[2] / sin_a;
      d = cos_a;
    }
    else
    {
      a = b = c = 0d;
      d = cos_a;
    }
  }

  /**
   * Generate a quaternion from spherical coordinates and a rotation angle
   */
  public Quaternion(double latitude, double longitude, double angle)
  {
    double sin_a    = Math.sin( angle / 2 );
    double cos_a    = Math.cos( angle / 2 );

    double sin_lat  = Math.sin( latitude );
    double cos_lat  = Math.cos( latitude );

    double sin_long = Math.sin( longitude );
    double cos_long = Math.cos( longitude );

    a = sin_a * cos_lat * sin_long;
    b = sin_a * sin_lat;
    c = sin_a * sin_lat * cos_long;
    d = cos_a;
  }

  public Quaternion add(Quaternion q)
  {
    return new Quaternion(a+q.a, b+q.b, c+q.c, d+q.d);
  }

  public Quaternion sub(Quaternion q)
  {
    return new Quaternion(a-q.a, b-q.b, c-q.c, d-q.d);
  }

  public Quaternion negate()
  {
    return new Quaternion(-a, -b, -c, -d);
  }

  public Quaternion mul(Quaternion q)
  {
    return new Quaternion(a*q.a - b*q.b - c*q.c - d*q.d, 
                          a*q.b + b*q.a + c*q.d - d*q.c,
                          a*q.c + c*q.a + d*q.b - b*q.d,
                          a*q.d + d*q.a + a*q.c - c*q.b);
  }

  public Quaternion mul(double v)
  {
    return new Quaternion(a*v, b*v, c*v, d*v);
  }

  public Quaternion div(Quaternion q)
  {
    Quaternion temp1, temp2;
    temp1 = new Quaternion(q.a, -q.b, -q.c, -q.d);
    temp2 = mul(temp1);
    temp1 = q.mul(temp1);
    return new Quaternion(temp2.a/temp1.a,
                          temp2.b/temp1.a,
                          temp2.c/temp1.a,
                          temp2.d/temp1.a);
  }

  public Quaternion normalize()
  {
    double length = Math.sqrt(a*a + b*b + c*c + d*d);
    return new Quaternion(a/length, b/length, c/length, d/length);
  }

  public Quaternion sqrt()
  {
    double temp = 2*a;
    return new Quaternion(a*a - b*b - c*c - d*d, 
                          temp*b, 
                          temp*c,
                          temp*d);
  }

  public double mag_sq()
  {
    return a*a + b*b;
  }

  public double mag()
  {
    return Math.sqrt(a*a + b*b + c*c + d*d);
  }

  public Matrix toRotationMatrix()
  {
    Matrix result = new Matrix(4,4);

    double xx      = a * a;
    double xy      = a * b;
    double xz      = a * c;
    double xw      = a * d;

    double yy      = b * b;
    double yz      = b * c;
    double yw      = b * d;

    double zz      = c * c;
    double zw      = c * d;

    result.matrix[0][0]  = 1 - 2 * ( yy + zz );
    result.matrix[0][1]  =     2 * ( xy - zw );
    result.matrix[0][2]  =     2 * ( xz + yw );

    result.matrix[1][0]  =     2 * ( xy + zw );
    result.matrix[1][1]  = 1 - 2 * ( xx + zz );
    result.matrix[1][2]  =     2 * ( yz - xw );

    result.matrix[2][0]  =     2 * ( xz - yw );
    result.matrix[2][1]  =     2 * ( yz + xw );
    result.matrix[2][2] = 1 - 2 * ( xx + yy );

    result.matrix[3][0] = result.matrix[3][1] = result.matrix[3][2] = 
    result.matrix[0][3] = result.matrix[1][3] = result.matrix[2][3] = 0d;
    result.matrix[3][3] = 1d;

    return result;
  }

  public static Quaternion fromRotationMatrix(Matrix m)
  {
    if ((m.rows<3) || (m.columns<3))
      return null;

    double trace = m.matrix[0][0] + m.matrix[1][1] + m.matrix[2][2] + 1d;
 
    double S,a,b,c,d;
    if (trace>0)
    {
      S = 0.5 / Math.sqrt(trace);

      a = ( m.matrix[2][1] - m.matrix[1][2] ) * S;
      b = ( m.matrix[0][2] - m.matrix[2][0] ) * S;
      c = ( m.matrix[1][0] - m.matrix[0][1] ) * S;
      d = 0.25 / S;
      
      return new Quaternion(a,b,c,d);
    }
    else if ((m.matrix[0][0]>m.matrix[1][1]) && (m.matrix[0][0]>m.matrix[2][2]))
    {
      S  = Math.sqrt( 1.0 + m.matrix[0][0] - m.matrix[1][1] - m.matrix[2][2] ) * 2;

      a = 0.5 / S;
      b = ( m.matrix[0][1] + m.matrix[1][0] ) / S;
      c = ( m.matrix[0][2] + m.matrix[2][0] ) / S;
      d = ( m.matrix[1][2] + m.matrix[2][1] ) / S;

      return new Quaternion(a,b,c,d);
    }
    else if ((m.matrix[1][1]>m.matrix[0][0]) && (m.matrix[1][1]>m.matrix[2][2]))
    {
      S  = Math.sqrt( 1.0 + m.matrix[1][1] - m.matrix[0][0] - m.matrix[2][2] ) * 2;

      a = ( m.matrix[0][1] + m.matrix[1][0] ) / S;
      b = 0.5 / S;
      c = ( m.matrix[1][2] + m.matrix[2][1] ) / S;
      d = ( m.matrix[0][2] + m.matrix[2][0] ) / S;

      return new Quaternion(a,b,c,d);
    }
    else
    {
      S  = Math.sqrt( 1.0 + m.matrix[2][2] - m.matrix[0][0] - m.matrix[1][1] ) * 2;

      a = ( m.matrix[0][2] + m.matrix[2][0] ) / S;
      b = ( m.matrix[1][2] + m.matrix[2][1] ) / S;
      c = 0.5 / S;
      d = ( m.matrix[0][1] + m.matrix[1][0] ) / S;

      return new Quaternion(a,b,c,d);
    }
  }

  public String toString()
  {
    return "("+a+","+b+","+c+","+d+")";
  }
}
