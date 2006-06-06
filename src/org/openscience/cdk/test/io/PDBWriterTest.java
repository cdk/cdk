/* $RCSfile$
 * $Author: egonw $
 * $Date: 2006-04-19 15:22:09 +0200 (Wed, 19 Apr 2006) $
 * $Revision: 6013 $
 * 
 * Copyright (C) 1997-2006  The Chemistry Development Kit (CDK) project
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *  */
package org.openscience.cdk.test.io;

import java.io.StringWriter;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.Crystal;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.io.PDBReader;
import org.openscience.cdk.io.PDBWriter;

/**
 * TestCase for the PDBWriter class.
 *
 * @cdk.module test-io
 *
 * @author      Egon Willighagen
 * @cdk.created 2001-08-09
 */
public class PDBWriterTest extends TestCase {

	public PDBWriterTest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(PDBWriterTest.class);
	}

    public void testRoundTrip() {
    	StringWriter sWriter = new StringWriter();
    	PDBWriter writer = new PDBWriter(sWriter);
    	
    	Crystal crystal = new Crystal();
    	crystal.setA(new Vector3d(0,1,0));
    	crystal.setB(new Vector3d(1,0,0));
    	crystal.setC(new Vector3d(0,0,2));
    	
    	IAtom atom = new Atom("C");
    	atom.setPoint3d(new Point3d(0.1,0.1,0.3));
    	crystal.addAtom(atom);

    	try {
			writer.write(crystal);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Failed to write PDB: " + e.getMessage());
		}
    	
		String output = sWriter.toString();
		System.out.println(output);
		assertNotNull(output);
		assertTrue(output.length() > 0);
		
		PDBReader reader = new PDBReader();
		ChemFile chemFile = null;
		try {
			chemFile = (ChemFile)reader.read(new ChemFile());
			
		} catch (CDKException e) {
			e.printStackTrace();
			fail("Failed to read PDB: " + e.getMessage());
		}
		
		assertNotNull(chemFile);
		assertEquals(1, chemFile.getChemSequenceCount());
		IChemSequence sequence = chemFile.getChemSequence(0);
		assertEquals(1, sequence.getChemModelCount());
		IChemModel chemModel = sequence.getChemModel(0);
		assertNotNull(chemModel);
		
		// can't do further testing as the PDBReader does not read
		// Crystal structures :(
    }

}
