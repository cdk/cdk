/* RandomStructureGeneratorTest.java
 * 
 * $RCSfile$    $Author$    $Date$    $Revision$
 * 
 * Copyright (C) 1997-2001  The Chemistry Development Kit (CDK) project
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

import org.openscience.cdk.controller.*;
import org.openscience.cdk.*;
import org.openscience.cdk.io.*;
import org.openscience.cdk.layout.*;
import org.openscience.cdk.renderer.*;
import org.openscience.cdk.structgen.*;
import java.util.*;
import java.io.*;
import java.net.URL;
import javax.vecmath.Vector2d;
import junit.framework.*;

public class RandomStructureGeneratorTest extends TestCase
{
	public RandomStructureGeneratorTest(String name)
	{
		super(name);
	}
	
	public void setUp()
	{
	}

	public static Test suite() {
		return new TestSuite(RandomStructureGeneratorTest.class);
	}


	/** A complex alkaloid with two separate ring systems to 
	  * be laid out.
	  */
	public void testIt()
	{
		Vector structures = new Vector();	
		Molecule molecule = MoleculeFactory.loadMolecule("data/a-pinene.mol");
		RandomGenerator rg = new RandomGenerator();
		rg.setMolecule(molecule);
		for (int f = 0; f < 1000; f++)
		{
			molecule = rg.proposeStructure();
			if ((double)f/(double)50 == f/50)
			{
				structures.addElement(molecule);
			}
			rg.acceptStructure();			
		}
		assert(everythingOk(structures));
	}


	private boolean everythingOk(Vector structures)
	{
		StructureDiagramGenerator sdg = null;
		MoleculeViewer2D mv = null;
		Molecule mol = null;
		for (int f = 0; f < structures.size(); f++)
		{
			sdg = new StructureDiagramGenerator();
			mv = new MoleculeViewer2D();
			mol = (Molecule)structures.elementAt(f);
			sdg.setMolecule(mol);

			try
			{
				sdg.generateCoordinates(new Vector2d(0,1));
			}
			catch(Exception exc)
			{
				System.out.println("*** Exit due to an unexpected error during coordinate generation ***");
				exc.printStackTrace();
			}
			mv.setAtomContainer(sdg.getMolecule());
			CDKTests.moleculeListViewer.addStructure(mv, "RandomGent Result no. " + (f + 1));
		}
		return true;
	}
	
}

