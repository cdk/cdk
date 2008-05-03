/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2007  Egon Willighagen <egonw@users.sf.net>
 * 
 * Contact: cdk-devel@slists.sourceforge.net
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
 */
package org.openscience.cdk.io;

import java.io.InputStream;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.io.MDLRXNV2000Reader;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.tools.LoggingTool;

/**
 * TestCase for the reading MDL RXN files using one test file.
 *
 * @cdk.module test-io
 *
 * @see org.openscience.cdk.io.MDLRXNReader
 */
public class MDLRXNV2000ReaderTest extends CDKTestCase {

    private LoggingTool logger;

    public MDLRXNV2000ReaderTest(String name) {
        super(name);
        logger = new LoggingTool(this);
    }

    public static Test suite() {
        return new TestSuite(MDLRXNV2000ReaderTest.class);
    }

    public void testAccepts() {
    	MDLRXNV2000Reader reader = new MDLRXNV2000Reader();
    	assertTrue(reader.accepts(ChemFile.class));
    	assertTrue(reader.accepts(ChemModel.class));
    	assertTrue(reader.accepts(Reaction.class));
    }

    /**
     * @cdk.bug 1849923
     */
    public void testReadReactions1() throws Exception {
        String filename1 = "data/mdl/0024.stg02.rxn";
        logger.info("Testing: " + filename1);
        InputStream ins1 = this.getClass().getClassLoader().getResourceAsStream(filename1);
        MDLRXNV2000Reader reader1 = new MDLRXNV2000Reader(ins1, Mode.STRICT);
        IReaction reaction1 = new Reaction();
        reaction1 = (IReaction)reader1.read(reaction1);
        reader1.close();

        assertNotNull(reaction1);
        assertEquals(1, reaction1.getReactantCount());
        assertEquals(1, reaction1.getProductCount());
        IAtomContainer reactant = reaction1.getReactants().getAtomContainer(0);
        assertNotNull(reactant);
        assertEquals(46, reactant.getAtomCount());
        assertEquals(44, reactant.getBondCount());
        IAtomContainer product = reaction1.getProducts().getAtomContainer(0);
        assertNotNull(product);
        assertEquals(46, product.getAtomCount());
        assertEquals(43, product.getBondCount());
        
    }

    /**
     * @cdk.bug 1851202
     */
    public void testBug1851202() throws Exception {
        String filename1 = "data/mdl/0002.stg01.rxn";
        logger.info("Testing: " + filename1);
        InputStream ins1 = this.getClass().getClassLoader().getResourceAsStream(filename1);
        MDLRXNV2000Reader reader1 = new MDLRXNV2000Reader(ins1, Mode.STRICT);
        IReaction reaction1 = new Reaction();
        reaction1 = (IReaction)reader1.read(reaction1);
        reader1.close();

        assertNotNull(reaction1);
        assertEquals(1, reaction1.getReactantCount());
        assertEquals(1, reaction1.getProductCount());
        IAtomContainer reactant = reaction1.getReactants().getAtomContainer(0);
        assertNotNull(reactant);
        assertEquals(30, reactant.getAtomCount());
        assertEquals(25, reactant.getBondCount());
        IAtomContainer product = reaction1.getProducts().getAtomContainer(0);
        assertNotNull(product);
        assertEquals(30, product.getAtomCount());
        assertEquals(26, product.getBondCount());
        
    }

}
