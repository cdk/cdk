/* StructureDiagramGeneratorTest.java
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

import org.openscience.cdk.controller.*;
import org.openscience.cdk.*;
import org.openscience.cdk.io.*;
import org.openscience.cdk.layout.*;
import org.openscience.cdk.renderer.*;
import java.util.*;
import java.io.*;
import java.net.URL;
import javax.vecmath.Vector2d;
import junit.framework.*;

public class StructureDiagramGeneratorTest extends TestCase
{
	Vector molecules = new Vector();;
	StructureDiagramGenerator sdg = null;
	MoleculeViewer2D mv = null;
	Renderer2DModel r2dm = null;
	
	public StructureDiagramGeneratorTest(String name)
	{
		super(name);
	}
	
	public void setUp()
	{
		sdg = new StructureDiagramGenerator();
		mv = new MoleculeViewer2D();
		r2dm = new Renderer2DModel();
		r2dm.setDrawNumbers(true);
		mv.setRenderer2DModel(r2dm);
	}

	public static Test suite() {
		return new TestSuite(StructureDiagramGeneratorTest.class);
	}

//	public void testAlphaPinene()
//	{
//		assert(showIt(MoleculeFactory.makeAlphaPinene()));
//	}
//
	public void testMolecule()
	{
		assert(showIt(MoleculeFactory.loadMolecule("data/reserpine.mol")));
	}
	
//	public void testCondensed()
//	{
//		assert(showIt(MoleculeFactory.make4x3CondensedRings()));
//	}
//
//	public void testSpiro()
//	{
//		assert(showIt(MoleculeFactory.makeSpiroRings()));
//	}


//	public void testRingSubstituents()
//	{
//		assert(showIt(MoleculeFactory.makeMethylDecaline()));
//	}


//	public void testBranchedAliphatic()
//	{
//		assert(showIt(MoleculeFactory.makeBranchedAliphatic()));
//	}


	boolean showIt(Molecule molecule)
	{
		sdg.setMolecule(molecule);
		try
		{
			sdg.generateCoordinates(new Vector2d(0,1));
		}
		catch(Exception exc)
		{
			System.out.println("*** Exit due to an unexpected error during coordinate generation ***");
			exc.printStackTrace();
			return false;
		}
		mv.setAtomContainer(sdg.getMolecule());
		mv.display();
		return true;
	}

}

