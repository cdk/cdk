/*
 * $RCSfile$    $Author$    $Date$    $Revision$
 * 
 * Copyright (C) 1997-2002  The Chemistry Development Kit (CDK) project
 * 
 * Contact: steinbeckice.mpg.de, gezeltermaul.chem.nd.edu, egonwsci.kun.nl
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

package org.openscience.cdk.test.smiles;


import org.openscience.cdk.*;
import org.openscience.cdk.smiles.*;
import org.openscience.cdk.io.*;
import org.openscience.cdk.tools.*;
import org.openscience.cdk.renderer.*;
import org.openscience.cdk.layout.*;
import javax.vecmath.*;

import java.util.*;

import java.io.*;
import java.net.URL;
import junit.framework.*;

public class SmilesParserTest extends TestCase
{
	boolean standAlone = false;
	
	public SmilesParserTest(String name)
	{
		super(name);
	}

	public static Test suite()
	{
		return new TestSuite(SmilesParserTest.class);
	}

	public void setStandAlone(boolean standAlone)
	{
		this.standAlone = standAlone;
	}
	
	public void testSmilesParser()
	{
		SmilesParser sp = new SmilesParser();
		MoleculeListViewer mlv = new MoleculeListViewer();
		String[] smiles = 
		{
			"C1c2c(c3c(c(O)cnc3)cc2)CC(=O)C1",
			"O=C(O3)C1=COC(OC4OC(CO)C(O)C(O)C4O)C2C1C3C=C2COC(C)=O",
			"CN1C=NC2=C1C(N(C)C(N2C)=O)=O",
			"CN(C)CCC2=CNC1=CC=CC(OP(O)(O)=O)=C12",
			"O=C(O)C1C(OC(C3=CC=CC=C3)=O)CC2N(C)C1CC2", 
			"C1(C2(C)(C))C(C)=CCC2C1",
			"C1(C=C(C=C(C=C(C=C(C=CC%35=C%36)C%31=C%35C%32=C%33C%36=C%34)C%22=C%31C%23=C%32C%24=C%25C%33=C%26C%34=CC%27=CC%28=CC=C%29)C%14=C%22C%15=C%23C%16=C%24C%17=C%18C%25=C%19C%26=C%27C%20=C%28C%29=C%21)C6=C%14C7=C%15C8=C%16C9=C%17C%12=C%11C%18=C%10C%19=C%20C%21=CC%10=CC%11=CC(C=C%30)=C%12%13)=C(C6=C(C7=C(C8=C(C9=C%13C%30=C5)C5=C4)C4=C3)C3=C2)C2=CC=C1"
		};
		for (int f = 0; f < smiles.length; f++)
		{
			try{
				Molecule mol = sp.parseSmiles(smiles[f]);
				StructureDiagramGenerator sdg = new StructureDiagramGenerator();
				MoleculeViewer2D mv = new MoleculeViewer2D();
				sdg.setMolecule((Molecule)mol.clone());
				sdg.generateCoordinates();
				mv.setAtomContainer(sdg.getMolecule());
				mlv.addStructure(mv, "Structure " + (f + 1)); 
			}catch(Exception exc){exc.printStackTrace();}
		}
		long l1 = System.currentTimeMillis();
			try{
				Molecule mol = sp.parseSmiles(smiles[6]);
				StructureDiagramGenerator sdg = new StructureDiagramGenerator();
				MoleculeViewer2D mv = new MoleculeViewer2D();
				sdg.setMolecule((Molecule)mol.clone());
				sdg.generateCoordinates();
				mv.setAtomContainer(sdg.getMolecule());
				mv.display();
				//mlv.addStructure(mv, "Structure " + (f + 1)); 
			}catch(Exception exc){exc.printStackTrace();}
		long l2 = System.currentTimeMillis();
		System.out.println(l2-l1);
							
	}

	
	public static void main(String[] args)
	{
		SmilesParserTest spt = new SmilesParserTest("SmilesParserTest");
		spt.setStandAlone(true);
		spt.testSmilesParser();
	}	
}

