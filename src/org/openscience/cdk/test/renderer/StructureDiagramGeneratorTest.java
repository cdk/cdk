/* StructureDiagramGeneratorTest.java
 * 
 * $RCSfile$    $Author$    $Date$    $Revision$
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
 */

package org.openscience.cdk.test.renderer;

import org.openscience.cdk.controller.*;
import org.openscience.cdk.*;
import org.openscience.cdk.io.*;
import org.openscience.cdk.layout.*;
import org.openscience.cdk.renderer.*;
import org.openscience.cdk.templates.*;
import java.util.*;
import java.io.*;
import java.net.URL;
import javax.vecmath.Vector2d;

public class StructureDiagramGeneratorTest
{
	MoleculeListViewer moleculeListViewer = null;
	
	public StructureDiagramGeneratorTest()
	{
		moleculeListViewer = new MoleculeListViewer();
		showIt(MoleculeFactory.loadMolecule("data/reserpine.mol"), "Reserpine");
		showIt(MoleculeFactory.loadMolecule("data/four-ring-5x10.mol"), "5x10 condensed four membered rings");
		showIt(MoleculeFactory.loadMolecule("data/six-ring-4x4.mol"), "4x4 condensed six membered rings");
		showIt(MoleculeFactory.loadMolecule("data/polycarpol.mol"), "Polycarpol");
		showIt(MoleculeFactory.makeAlphaPinene(), "alpha-Pinene");
		showIt(MoleculeFactory.makeBiphenyl(), "Biphenyl");
		showIt(MoleculeFactory.make4x3CondensedRings(), "4x3CondensedRings");
		showIt(MoleculeFactory.makePhenylEthylBenzene(), "PhenylEthylBenzene");
		showIt(MoleculeFactory.makeSpiroRings(), "Spiro");
		showIt(MoleculeFactory.makeMethylDecaline(), "Methyldecaline");
		showIt(MoleculeFactory.makeBranchedAliphatic(), "Branched aliphatic");
		showIt(MoleculeFactory.makeDiamantane(), "Diamantane - A Problem! - Solve it! :-)");
		showIt(MoleculeFactory.makeEthylCyclohexane(), "Ethylcyclohexane");	
		showIt(MoleculeFactory.makeBicycloRings(), "Bicyclo-[2.2.2]-octane");
	}


	private boolean showIt(Molecule molecule, String name)
	{
		StructureDiagramGenerator sdg = new StructureDiagramGenerator();
		MoleculeViewer2D mv = new MoleculeViewer2D();
		try
		{
			sdg.setMolecule((Molecule)molecule.clone());
			sdg.generateCoordinates(new Vector2d(0,1));
			mv.setAtomContainer(sdg.getMolecule());
			moleculeListViewer.addStructure(mv, name);
		}
		catch(Exception exc)
		{
			System.out.println("*** Exit due to an unexpected error during coordinate generation ***");
			exc.printStackTrace();
			return false;
		}
		return true;
	}

	public static void main(String[] args)
	{
		new StructureDiagramGeneratorTest();
		
	}
}

