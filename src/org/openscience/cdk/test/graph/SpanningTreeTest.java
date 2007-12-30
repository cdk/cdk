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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.graph.SpanningTree;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.test.NewCDKTestCase;

import java.io.InputStream;

/**
 * @cdk.module test-atomtype
 */
public class SpanningTreeTest extends NewCDKTestCase {
    
	private static SpanningTree azulene = null;
	

    @Before
    public void setUp() throws Exception {
    	if (azulene == null) {
    		// load azulene
    		String filename = "data/mdl/azulene.mol";
    		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
    		MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
    		IChemFile chemFile = (IChemFile) reader.read(new ChemFile());
    		IChemSequence seq = chemFile.getChemSequence(0);
    		IChemModel model = seq.getChemModel(0);
    		IMolecule azuleneMolecule = model.getMoleculeSet().getMolecule(0);
    		Assert.assertEquals(10, azuleneMolecule.getAtomCount());
    		Assert.assertEquals(11, azuleneMolecule.getBondCount());
    		azulene = new SpanningTree(azuleneMolecule);
    	}
    }
    

    @Test
	public void testSpanningTree_IAtomContainer() {
		SpanningTree sTree = new SpanningTree(new AtomContainer());
		Assert.assertNotNull(sTree);
	}

    @Test
    public void testGetCyclicFragmentsContainer() throws Exception {
		IAtomContainer ringSystems = azulene.getCyclicFragmentsContainer();
		Assert.assertEquals(10, ringSystems.getAtomCount());
		Assert.assertEquals(11, ringSystems.getBondCount());
	}

    @Test
    public void testGetBondsCyclicCount() throws Exception {
		Assert.assertEquals(11, azulene.getBondsCyclicCount());
	}

    @Test
    public void testGetBondsAcyclicCount() throws Exception {
		Assert.assertEquals(0, azulene.getBondsAcyclicCount());
	}

    @Test
    public void testClear() {
		Assert.fail("Missing JUnit test");
	}
    @Test
    public void testGetPath_IAtomContainer_IAtom_IAtom() {
		Assert.fail("Missing JUnit test");
	}
    @Test
    public void testResetFlags_IAtomContainer() {
		Assert.fail("Missing JUnit test");
	}
    @Test
    public void testIsDisconnected() {
		Assert.fail("Missing JUnit test");
	}
    @Test
    public void testGetSpanningTree() {
		Assert.fail("Missing JUnit test");
	}
    @Test
    public void testGetBasicRings() {
		Assert.fail("Missing JUnit test");
	}
    @Test
    public void testGetAllRings() {
		Assert.fail("Missing JUnit test");
	}
    @Test
    public void testGetSpanningTreeSize() {
		Assert.fail("Missing JUnit test");
	}
	
}