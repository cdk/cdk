/* $RCSfile$
 * $Author$
 * $Date$
 *
 * Copyright (C) 2003-2004  The Jmol Development Team
 * Copyright (C) 2003-2004  The CDK Project
 *
 * Contact: cdk-devel@lists.sf.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 *  02111-1307  USA.
 */
package org.openscience.cdk.graph.rebond;


/**
 * @author  Miguel Howard
 * @cdk.created 2003-05
 */
public class Point implements Bspt.Tuple {
  double x;
  double y;
  double z;

  Point(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public double getDimValue(int dim) {
    if (dim == 0)
      return x;
    if (dim == 1)
      return y;
    return z;
  }

  public String toString() {
    return "<" + x + "," + y + "," + z + ">";
  }
}
