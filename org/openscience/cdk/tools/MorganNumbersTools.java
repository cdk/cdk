/* 
 *
 * $RCSfile$    $Author$    $Date$    $Revision$
 * 
 * Copyright (C) 1997-2001  The Chemistry Development Kit (CDK) project
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

package org.openscience.cdk.tools;

import org.openscience.cdk.*;
import org.openscience.cdk.exception.*;
import java.util.*;

/**
 * Tool for calculating Morgan Numbers:
 * Morgan, H. L. 
 * The Generation of a Unique Machine Description for Chemical Structures - 
 * A Technique Developed at Chemical Abstracts Service. 
 * J. Chem. Doc. 1965, 5, 107-113. 
 */
 
public class MorganNumbersTools implements CDKConstants
{
	
	public static int[] getMorganNumbers(AtomContainer atomContainer) throws NoSuchAtomException
	{
		int [] morganMatrix, tempMorganMatrix;
		int N = atomContainer.getAtomCount();
		morganMatrix = new int[N];
		tempMorganMatrix = new int[N];
		Atom[] atoms = null;
		for (int f = 0; f < N; f++)
		{
			morganMatrix[f] = atomContainer.getDegree(f);
			tempMorganMatrix[f] = atomContainer.getDegree(f);
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
