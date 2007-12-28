/* $Revision: 7691 $ $Author: egonw $ $Date: 2007-01-11 12:47:48 +0100 (Thu, 11 Jan 2007) $
 * 
 * Copyright (C) 2007  Egon Willighagen <egonw@users.sf.net>
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA. 
 */
package org.openscience.cdk.test.graph;

import java.io.InputStream;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.graph.SpanningTree;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.test.CDKTestCase;

/**
 * @cdk.module test-atomtype
 */
public class SpanningTreeTest extends CDKTestCase {
    
	private SpanningTree azulene;
	
    public SpanningTreeTest(String name) {
        super(name);
    }
    
    public void setUp() throws Exception {
    	// load azulene
		String filename = "data/mdl/azulene.mol";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		MDLReader reader = new MDLReader(ins, Mode.STRICT);
		IChemFile chemFile = (IChemFile) reader.read(new ChemFile());
		IChemSequence seq = chemFile.getChemSequence(0);
		IChemModel model = seq.getChemModel(0);
		IMolecule azulene = model.getMoleculeSet().getMolecule(0);
		assertEquals(10, azulene.getAtomCount());
		assertEquals(11, azulene.getBondCount());
		this.azulene = new SpanningTree(azulene);
    }
    
	public static Test suite() {
		return new TestSuite(SpanningTreeTest.class);
	}
	
	public void testSpanningTree_IAtomContainer() {
		SpanningTree sTree = new SpanningTree(new AtomContainer());
		assertNotNull(sTree);
	}
	
	public void testGetCyclicFragmentsContainer() throws Exception {
		IAtomContainer ringSystems = this.azulene.getCyclicFragmentsContainer();
		assertEquals(10, ringSystems.getAtomCount());
		assertEquals(11, ringSystems.getBondCount());
	}

	public void testGetBondsCyclicCount() throws Exception {
		assertEquals(11, this.azulene.getBondsCyclicCount());
	}

	public void testGetBondsAcyclicCount() throws Exception {
		assertEquals(0, this.azulene.getBondsAcyclicCount());
	}

	public void testClear() {
		fail("Missing JUnit test");
	}
	public void testGetPath_IAtomContainer_IAtom_IAtom() {
		fail("Missing JUnit test");
	}
	public void testResetFlags_IAtomContainer() {
		fail("Missing JUnit test");
	}
	public void testIsDisconnected() {
		fail("Missing JUnit test");
	}
	public void testGetSpanningTree() {
		fail("Missing JUnit test");
	}
	public void testGetBasicRings() {
		fail("Missing JUnit test");
	}
	public void testGetAllRings() {
		fail("Missing JUnit test");
	}
	public void testGetSpanningTreeSize() {
		fail("Missing JUnit test");
	}
	
}