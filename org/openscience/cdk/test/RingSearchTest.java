/* RingSearchTest.java
 * 
 * $RCSfile$    $Author$    $Date$    $Revision$
 * 
 * Copyright (C) 1997-2000  The CompChem project
 * 
 * Contact: steinbeck@ice.mpg.de, geelter@maul.chem.nd.edu, egonw@sci.kun.nl
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
 */

package org.openscience.cdk.test;


import org.openscience.cdk.*;
import org.openscience.cdk.ringsearch.*;
import org.openscience.cdk.io.*;
import org.openscience.cdk.tools.*;
import java.util.*;
import java.io.*;
import java.net.URL;


public class RingSearchTest
{
	MDLReader mr;
	ChemFile chemFile;
	ChemSequence chemSequence;
	ChemModel chemModel;
	SetOfMolecules setOfMolecules;
	Molecule molecule;
	TestFrame[] frames;
	TestFrame frame;
	
	SSSRFinder sssrf;
	RingSet ringSet;
	Ring ring;
	Molecule[] rings;
	

	public RingSearchTest(String inFile)
	{
		long start,end;
		sssrf = new SSSRFinder();
		try
		{
			FileInputStream fis = new FileInputStream(inFile);
			mr = new MDLReader(fis);
			chemFile = mr.readChemFile();
			fis.close();
			
			chemSequence = chemFile.getChemSequence(0);
			chemModel = chemSequence.getChemModel(0);
			setOfMolecules = chemModel.getSetOfMolecules(0);
			molecule = setOfMolecules.getMolecule(0);
			start = System.currentTimeMillis();
			for (int i = 0; i < 5000; i++)
			{
				ringSet = sssrf.findSSSR(molecule);
			}
			end = System.currentTimeMillis();
			System.out.println(ringSet.size() + " Rings in " + inFile);
			System.out.println(SwissArmyKnife.getDuration((long)((end-start)/5000)));
			System.exit(0);
//			System.out.println("number of rings found  "+ringSet.size());
//			for (int i = 0; i < ringSet.size(); i++)
//			{
//				ring = (Ring)ringSet.elementAt(i);
//				System.out.println("ring number "+ (i + 1) +" has "+ ring.getRingSize() + " edges");
//				System.out.println("ring  "+ ring.toString());
//			}
		}
		catch(Exception exc)
		{
			exc.printStackTrace();
		}
//		System.out.println("Molekuel  "+ molecule.toString());
		frame = new TestFrame(molecule);
		frame.show();
	}


	/**
	 * The main method.
	 *
	 * @param   args    The Arguments from the commandline
	 */
	public static void main(String[] args)
	{
		new RingSearchTest(args[0]);
	}
}

