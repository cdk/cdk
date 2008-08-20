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

import org.openscience.cdk.math.Vector;

/**
 * This class will generate a simple base set for a atom container.
 * 
 * @author  Stephan Michels <stephan@vern.chem.tu-berlin.de>
 * @cdk.svnrev  $Revision$
 * @cdk.created 2001-07-11
 * @cdk.module  qm
 */ 
public class SimpleBasisSet extends GaussiansBasis
{
  private final static double f1 = 0.1;
  private final static double f2 = 0.3;
  private final static double f3 = 0.9;
  private final static double f4 = 2.7;

  /**
   * Create a base set
   */
  public SimpleBasisSet(org.openscience.cdk.interfaces.IAtom[] atoms)
  {
    int i, j;
    int size = 0;
    int atomicnumber;
    for(i=0; i<atoms.length; i++)
    {
      atomicnumber = atoms[i].getAtomicNumber();
      if (atomicnumber<=2)
        size += 4;
      else if (atomicnumber<=18)
        size += 4+12;
      else 
        size += 4+12+12;
    }

    int[] nx = new int[size];
    int[] ny = new int[size];
    int[] nz = new int[size];
    double[] alpha = new double[size];
    Vector[] r = new Vector[size];

    Vector rN;

    j = 0;
    for(i=0; i<atoms.length; i++)
    {
      atomicnumber = atoms[i].getAtomicNumber();
      rN = new Vector(atoms[i].getPoint3d());
      if (atomicnumber<=2)
      {
        nx[j] = 0; ny[j] = 0; nz[j] = 0; alpha[j] = f1; r[j] = rN; j++;
        nx[j] = 0; ny[j] = 0; nz[j] = 0; alpha[j] = f2; r[j] = rN; j++;
        nx[j] = 0; ny[j] = 0; nz[j] = 0; alpha[j] = f3; r[j] = rN; j++;
        nx[j] = 0; ny[j] = 0; nz[j] = 0; alpha[j] = f4; r[j] = rN; j++;
      }
      else if (atomicnumber<=18)
      {
        nx[j] = 0; ny[j] = 0; nz[j] = 0; alpha[j] = f1; r[j] = rN; j++;
        nx[j] = 0; ny[j] = 0; nz[j] = 0; alpha[j] = f2; r[j] = rN; j++;
        nx[j] = 0; ny[j] = 0; nz[j] = 0; alpha[j] = f3; r[j] = rN; j++;
        nx[j] = 0; ny[j] = 0; nz[j] = 0; alpha[j] = f4; r[j] = rN; j++;

        nx[j] = 1; ny[j] = 0; nz[j] = 0; alpha[j] = f1; r[j] = rN; j++;
        nx[j] = 1; ny[j] = 0; nz[j] = 0; alpha[j] = f2; r[j] = rN; j++;
        nx[j] = 1; ny[j] = 0; nz[j] = 0; alpha[j] = f3; r[j] = rN; j++;
        nx[j] = 1; ny[j] = 0; nz[j] = 0; alpha[j] = f4; r[j] = rN; j++;

        nx[j] = 0; ny[j] = 1; nz[j] = 0; alpha[j] = f1; r[j] = rN; j++;
        nx[j] = 0; ny[j] = 1; nz[j] = 0; alpha[j] = f2; r[j] = rN; j++;
        nx[j] = 0; ny[j] = 1; nz[j] = 0; alpha[j] = f3; r[j] = rN; j++;
        nx[j] = 0; ny[j] = 1; nz[j] = 0; alpha[j] = f4; r[j] = rN; j++;

        nx[j] = 0; ny[j] = 0; nz[j] = 1; alpha[j] = f1; r[j] = rN; j++;
        nx[j] = 0; ny[j] = 0; nz[j] = 1; alpha[j] = f2; r[j] = rN; j++;
        nx[j] = 0; ny[j] = 0; nz[j] = 1; alpha[j] = f3; r[j] = rN; j++;
        nx[j] = 0; ny[j] = 0; nz[j] = 1; alpha[j] = f4; r[j] = rN; j++;
      }
      else 
      {
        nx[j] = 0; ny[j] = 0; nz[j] = 0; alpha[j] = f1; r[j] = rN; j++;
        nx[j] = 0; ny[j] = 0; nz[j] = 0; alpha[j] = f2; r[j] = rN; j++;
        nx[j] = 0; ny[j] = 0; nz[j] = 0; alpha[j] = f3; r[j] = rN; j++;
        nx[j] = 0; ny[j] = 0; nz[j] = 0; alpha[j] = f4; r[j] = rN; j++;
        
        nx[j] = 1; ny[j] = 0; nz[j] = 0; alpha[j] = f1; r[j] = rN; j++;
        nx[j] = 1; ny[j] = 0; nz[j] = 0; alpha[j] = f2; r[j] = rN; j++;
        nx[j] = 1; ny[j] = 0; nz[j] = 0; alpha[j] = f3; r[j] = rN; j++;
        nx[j] = 1; ny[j] = 0; nz[j] = 0; alpha[j] = f4; r[j] = rN; j++;
        
        nx[j] = 0; ny[j] = 1; nz[j] = 0; alpha[j] = f1; r[j] = rN; j++;
        nx[j] = 0; ny[j] = 1; nz[j] = 0; alpha[j] = f2; r[j] = rN; j++;
        nx[j] = 0; ny[j] = 1; nz[j] = 0; alpha[j] = f3; r[j] = rN; j++;
        nx[j] = 0; ny[j] = 1; nz[j] = 0; alpha[j] = f4; r[j] = rN; j++;
        
        nx[j] = 0; ny[j] = 0; nz[j] = 1; alpha[j] = f1; r[j] = rN; j++;
        nx[j] = 0; ny[j] = 0; nz[j] = 1; alpha[j] = f2; r[j] = rN; j++;
        nx[j] = 0; ny[j] = 0; nz[j] = 1; alpha[j] = f3; r[j] = rN; j++;
        nx[j] = 0; ny[j] = 0; nz[j] = 1; alpha[j] = f4; r[j] = rN; j++;

        nx[j] = 1; ny[j] = 1; nz[j] = 0; alpha[j] = f1; r[j] = rN; j++;
        nx[j] = 1; ny[j] = 1; nz[j] = 0; alpha[j] = f2; r[j] = rN; j++;
        nx[j] = 1; ny[j] = 1; nz[j] = 0; alpha[j] = f3; r[j] = rN; j++;
        nx[j] = 1; ny[j] = 1; nz[j] = 0; alpha[j] = f4; r[j] = rN; j++;
        
        nx[j] = 0; ny[j] = 1; nz[j] = 1; alpha[j] = f1; r[j] = rN; j++;
        nx[j] = 0; ny[j] = 1; nz[j] = 1; alpha[j] = f2; r[j] = rN; j++;
        nx[j] = 0; ny[j] = 1; nz[j] = 1; alpha[j] = f3; r[j] = rN; j++;
        nx[j] = 0; ny[j] = 1; nz[j] = 1; alpha[j] = f4; r[j] = rN; j++;
        
        nx[j] = 1; ny[j] = 0; nz[j] = 1; alpha[j] = f1; r[j] = rN; j++;
        nx[j] = 1; ny[j] = 0; nz[j] = 1; alpha[j] = f2; r[j] = rN; j++;
        nx[j] = 1; ny[j] = 0; nz[j] = 1; alpha[j] = f3; r[j] = rN; j++;
        nx[j] = 1; ny[j] = 0; nz[j] = 1; alpha[j] = f4; r[j] = rN; j++;
      }
    }
//    logger.debug("j="+j+" size="+size);

    setBasis(nx, ny, nz, alpha, r, atoms);
  }
}
