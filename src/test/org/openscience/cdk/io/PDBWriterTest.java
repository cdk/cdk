/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.io;

import java.io.StringWriter;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.Crystal;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.ICrystal;

/**
 * TestCase for the PDBWriter class.
 *
 * @cdk.module test-io
 *
 * @author      Egon Willighagen
 * @cdk.created 2001-08-09
 */
public class PDBWriterTest extends ChemObjectIOTest {

    private static IChemObjectBuilder builder;

    @BeforeClass public static void setup() {
        builder = DefaultChemObjectBuilder.getInstance();
        setChemObjectIO(new MDLRXNWriter());
    }

    @Test public void testRoundTrip() throws Exception {
    	StringWriter sWriter = new StringWriter();
    	PDBWriter writer = new PDBWriter(sWriter);
    	
    	ICrystal crystal = builder.newCrystal();
    	crystal.setA(new Vector3d(0,1,0));
    	crystal.setB(new Vector3d(1,0,0));
    	crystal.setC(new Vector3d(0,0,2));
    	
    	IAtom atom = builder.newAtom("C");
    	atom.setPoint3d(new Point3d(0.1,0.1,0.3));
    	crystal.addAtom(atom);

    	writer.write(crystal);
    	writer.close();
    	
		String output = sWriter.toString();
		System.out.println(output);
		Assert.assertNotNull(output);
		Assert.assertTrue(output.length() > 0);
		
		PDBReader reader = new PDBReader();
		ChemFile chemFile = (ChemFile)reader.read(new ChemFile());
		
		Assert.assertNotNull(chemFile);
		Assert.assertEquals(1, chemFile.getChemSequenceCount());
		IChemSequence sequence = chemFile.getChemSequence(0);
		Assert.assertEquals(1, sequence.getChemModelCount());
		IChemModel chemModel = sequence.getChemModel(0);
		Assert.assertNotNull(chemModel);
		
		// can't do further testing as the PDBReader does not read
		// Crystal structures :(
    }

    @Test public void testRoundTrip_fractionalCoordinates() throws Exception {
    	StringWriter sWriter = new StringWriter();
    	PDBWriter writer = new PDBWriter(sWriter);
    	
    	Crystal crystal = new Crystal();
    	crystal.setA(new Vector3d(0,1,0));
    	crystal.setB(new Vector3d(1,0,0));
    	crystal.setC(new Vector3d(0,0,2));
    	
    	IAtom atom = new Atom("C");
    	atom.setFractionalPoint3d(new Point3d(0.1,0.1,0.3));
    	crystal.addAtom(atom);

    	writer.write(crystal);
    	writer.close();
    	
		String output = sWriter.toString();
		System.out.println(output);
		Assert.assertNotNull(output);
		Assert.assertTrue(output.length() > 0);
		
		PDBReader reader = new PDBReader();
		ChemFile chemFile = (ChemFile)reader.read(new ChemFile());
		
		Assert.assertNotNull(chemFile);
		Assert.assertEquals(1, chemFile.getChemSequenceCount());
		IChemSequence sequence = chemFile.getChemSequence(0);
		Assert.assertEquals(1, sequence.getChemModelCount());
		IChemModel chemModel = sequence.getChemModel(0);
		Assert.assertNotNull(chemModel);
		
		// can't do further testing as the PDBReader does not read
		// Crystal structures :(
    }
}
