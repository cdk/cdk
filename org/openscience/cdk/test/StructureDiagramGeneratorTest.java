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
	
	public StructureDiagramGeneratorTest(String name)
	{
		super(name);
	}
	
	public void setUp()
	{

	}

	public static Test suite() {
		return new TestSuite(StructureDiagramGeneratorTest.class);
	}


	/** A complex alkaloid with two separate ring systems to 
	  * be laid out.
	  */
	public void testReserpine()
	{
		assert(showIt(MoleculeFactory.loadMolecule("data/reserpine.mol"), "Reserpine"));
	}
	
//	public void testAlphaPinene()
//	{
//		assert(showIt(MoleculeFactory.makeAlphaPinene(), "alpha-Pinene"));
//	}
//
//
//	public void testPolycarpole()
//	{
//		assert(showIt(MoleculeFactory.loadMolecule("data/polycarpol.mol"), "Polycarpol"));
//	}
//
//
//	public void testSpiro()
//	{
//		assert(showIt(MoleculeFactory.makeSpiroRings(), "Spiro"));
//	}
//
//
//	public void testRingSubstituents()
//	{
//		assert(showIt(MoleculeFactory.makeMethylDecaline(), "Methyldecaline"));
//	}
//
//
//	public void testBranchedAliphatic()
//	{
//		assert(showIt(MoleculeFactory.makeBranchedAliphatic(), "Branched aliphatic"));
//	}
//	
//	public void testAdamantane()
//	{
//		assert(showIt(MoleculeFactory.makeAdamantane(), "Adamantane"));
//	}
//
//
//	/** This was interesting because the 
//	  * method "placeRingSubstituents" just places
//	  * the next aliphatic atom close to a ring
//	  * and there was initially a problem with
//	  * just one atom left to place in the aliphatic chain
//	  */
//	public void testEthylCyclohexane()
//	{
//		assert(showIt(MoleculeFactory.makeEthylCyclohexane(), "Ethylcyclohexane"));
//	}
//
	private boolean showIt(Molecule molecule, String name)
	{
		StructureDiagramGenerator sdg = new StructureDiagramGenerator();
		MoleculeViewer2D mv = new MoleculeViewer2D();
		Renderer2DModel r2dm = new Renderer2DModel();
		r2dm.setDrawNumbers(true);
		mv.setRenderer2DModel(r2dm);
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
		CDKTests.moleculeListViewer.addStructure(mv, name);
		return true;
	}
}

