/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2002  The Chemistry Development Kit (CDK) project
 * 
 * Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
 * 
 */

package org.openscience.cdk.graphinvariant;

import org.openscience.cdk.*;
import org.openscience.cdk.exception.*;
import java.util.*;

/**
 * Tool for calculating Morgan Numbers.
 *
 * References:
 *   <a href="http://cdk.sf.net/biblio.html#MOR65">MOR65</a>
 *
 * @keyword Morgan number
 */

public class MorganNumbersTools 
{
	
	public static int[] getMorganNumbers(AtomContainer atomContainer) {
		int [] morganMatrix, tempMorganMatrix;
		int N = atomContainer.getAtomCount();
		morganMatrix = new int[N];
		tempMorganMatrix = new int[N];
		Atom[] atoms = null;
		for (int f = 0; f < N; f++)
		{
			morganMatrix[f] = atomContainer.getBondCount(f);
			tempMorganMatrix[f] = atomContainer.getBondCount(f);
		}
		for (int e = 0; e < N; e++)
		{
			for (int f = 0; f < N; f++)
			{
				morganMatrix[f] = 0;
				atoms = atomContainer.getConnectedAtoms(atomContainer.getAtomAt(f));				
				for (int g = 0; g < atoms.length; g ++)
				{
					morganMatrix[f] += tempMorganMatrix[atomContainer.getAtomNumber(atoms[g])];	
				}
			}
			System.arraycopy(morganMatrix, 0, tempMorganMatrix, 0, N);
		}
		return tempMorganMatrix;	
	}
}
