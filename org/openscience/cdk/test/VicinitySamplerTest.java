/* 
 * $RCSfile$    $Author$    $Date$    $Revision$
 * 
 * Copyright (C) 1997-2001  The JChemPaint project
 * 
 * Contact: steinbeck@ice.mpg.de
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All I ask is that proper credit is given for my work, which includes
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
 * 
 */

package org.openscience.cdk.test;

import org.openscience.cdk.controller.*;
import org.openscience.cdk.*;
import org.openscience.cdk.io.*;
import org.openscience.cdk.layout.*;
import org.openscience.cdk.renderer.*;
import org.openscience.cdk.structgen.*;
import org.openscience.cdk.*;
import org.openscience.cdk.tools.*;
import java.io.*;
import java.util.*;
import junit.framework.*;


public class VicinitySamplerTest extends TestCase
{
	
	public VicinitySamplerTest(String name)
	{
		super(name);
	}

	
	public static Test suite() {
		return new TestSuite(VicinitySamplerTest.class);
	}

	public  void testVicinitySampler()
	{
		Molecule mol = MoleculeFactory.makeAlphaPinene();
		VicinitySampler vs = new VicinitySampler();
		vs.setMolecule(mol);
		Vector structures = vs.sample((AtomContainer) mol);
		System.out.println("There are " + structures.size() + " structures in Faulon-Distance 1 for alpha-Pinene.");
		
		mol = MoleculeFactory.makeEthylPropylPhenantren();
		vs = new VicinitySampler();
		vs.setMolecule(mol);
		structures = vs.sample((AtomContainer) mol);
		System.out.println("There are " + structures.size() + " structures in Faulon-Distance 1 for EthylPropylPhenantren"); 
		display(structures);
	}
	
	private void display(Vector structures)
	{
		MoleculeListViewer moleculeListViewer = new MoleculeListViewer(); 
		StructureDiagramGenerator sdg = null;
		MoleculeViewer2D mv = null;
		Molecule mol = null;
		for (int f = 0; f < structures.size(); f++)
		{
			sdg = new StructureDiagramGenerator();
			mv = new MoleculeViewer2D();
			mol = (Molecule)structures.elementAt(f);
			sdg.setMolecule((Molecule)mol.clone());

			try
			{
				sdg.generateCoordinates();
				mv.setAtomContainer(sdg.getMolecule());
				moleculeListViewer.addStructure(mv, "no. " + (f + 1));

			}
			catch(Exception exc)
			{
				System.out.println("*** Exit due to an unexpected error during coordinate generation ***");
			}
		}
	}

	
	public static void main(String[] args)
	{
		VicinitySamplerTest vst = new VicinitySamplerTest("VicinitySamplerTest");
		vst.testVicinitySampler();
	}
}
