

/* $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2004  The Chemistry Development Kit (CDK) project
 * 
 * Contact: cdk-devel@lists.sourceforge.net
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
package org.openscience.cdk.test.structgen.deterministic;

import java.util.Vector;

import javax.vecmath.Vector2d;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.*;
import org.openscience.cdk.structgen.deterministic.*;
import org.openscience.cdk.applications.swing.*;
import org.openscience.cdk.Bond;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.applications.swing.MoleculeListViewer;
import org.openscience.cdk.applications.swing.MoleculeViewer2D;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.structgen.RandomGenerator;
import org.openscience.cdk.templates.MoleculeFactory;

/**
 * @cdk.module test
 */
public class GENMDeterministicGeneratorTest extends TestCase
{
	public boolean debug = false;
	boolean standAlone = false;

	public MoleculeListViewer moleculeListViewer = null;
    
    public GENMDeterministicGeneratorTest(String name) {
        super(name);
    }
    
    public GENMDeterministicGeneratorTest() {
        this("GENMDeterministicGeneratorTest");
        moleculeListViewer = new MoleculeListViewer();
    }

	public void setStandAlone(boolean standAlone)
	{
		this.standAlone = standAlone;
	}

	public static Test suite()
	{
		return new TestSuite(GENMDeterministicGeneratorTest.class);
	}

	/** A complex alkaloid with two separate ring systems to 
	  * be laid out.
	  */
	public void testIt()
	{
		try
		{	
			GENMDeterministicGenerator gdg = new GENMDeterministicGenerator("C6H6");
			Vector structures=gdg.getStructures();
			everythingOk(structures);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}


	private boolean everythingOk(Vector structures)
	{
		StructureDiagramGenerator sdg = null;
		MoleculeViewer2D mv = null;
		Molecule mol = null;
		for (int f = 0; f<structures.size(); f++)
		{
			sdg = new StructureDiagramGenerator();

			mol = (Molecule)structures.elementAt(f);
			//System.out.println(mol.getAtomCount());
			//System.out.println(mol.getBondCount());
			sdg.setMolecule((Molecule)mol.clone());
			//sdg.setMolecule(mol);
			try
			{
				sdg.generateCoordinates(new Vector2d(0,1));
			}
			catch(Exception exc)
			{
				exc.printStackTrace();
				fail("*** Exit due to an unexpected error during coordinate generation ***");
			}
            if (standAlone) {
                
                mv = new MoleculeViewer2D();
		mv.setAtomContainer(sdg.getMolecule());
                //			Renderer2DModel r2dm = new Renderer2DModel();
                //			r2dm.setDrawNumbers(true);
                //			mv.setRenderer2DModel(r2dm);
                moleculeListViewer.addStructure(mv, "Structure no. " + (f + 1));
            }
		}
		return true;
	}
	
	public static void main(String[] args)
	{
		GENMDeterministicGeneratorTest test = new GENMDeterministicGeneratorTest();
		test.setStandAlone(true);
		test.testIt();
	}
}


