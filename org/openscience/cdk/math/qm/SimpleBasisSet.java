/* SimpleBasisSet.java
 * 
 * Autor: Stephan Michels 
 * EMail: stephan@vern.chem.tu-berlin.de
 * Datum: 11.7.2001
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

import org.openscience.cdk.Atom;
import org.openscience.cdk.math.Vector;
 
public class SimpleBasisSet extends GaussiansBasis
{
	private final static double f1 = 0.1;
	private final static double f2 = 0.3;
	private final static double f3 = 0.9;
	private final static double f4 = 2.7;

	public SimpleBasisSet(Atom[] atoms)
	{
		int i, j;
		int size = 0;
		int atomicnumber;
		for(i=0; i<atoms.length; i++)
		{
			atomicnumber = atoms[i].getElement().getAtomicNumber();
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
			atomicnumber = atoms[i].getElement().getAtomicNumber();
			rN = new Vector(atoms[i].getPoint3D());
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
		System.out.println("j="+j+" size="+size);

		setBasis(nx, ny, nz, alpha, r, atoms);
	}
}
