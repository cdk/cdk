/* $RCSfile$
 * $Author: egonw $
 * $Date: 2007-02-09 00:35:55 +0100 (Fri, 09 Feb 2007) $
 * $Revision: 7921 $
 *
 * Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
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
 *
 */
package org.openscience.cdk.test.io;

import java.io.InputStream;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.ChemModel;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.io.MDLRXNV3000Reader;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.LoggingTool;

/**
 * TestCase for the reading MDL RXN files using one test file.
 *
 * @cdk.module test-io
 *
 * @see org.openscience.cdk.io.MDLRXNReader
 */
public class MDLRXNV3000ReaderTest extends CDKTestCase {

    private LoggingTool logger;

    public MDLRXNV3000ReaderTest(String name) {
        super(name);
        logger = new LoggingTool(this);
    }

    public static Test suite() {
        return new TestSuite(MDLRXNV3000ReaderTest.class);
    }

    public void testAccepts() {
    	MDLRXNV3000Reader reader = new MDLRXNV3000Reader();
    	assertTrue(reader.accepts(ChemModel.class));
    	assertTrue(reader.accepts(Reaction.class));
    }

    /**
     * @cdk.bug 1849925
     */
    public void testReadReactions1() throws Exception {
        String filename1 = "data/mdl/reaction_v3.rxn";
        logger.info("Testing: " + filename1);
        InputStream ins1 = this.getClass().getClassLoader().getResourceAsStream(filename1);
        MDLRXNV3000Reader reader1 = new MDLRXNV3000Reader(ins1, Mode.STRICT);
        IReaction reaction1 = new Reaction();
        reaction1 = (IReaction)reader1.read(reaction1);
        reader1.close();

        assertNotNull(reaction1);
        assertEquals(1, reaction1.getReactantCount());
        assertEquals(1, reaction1.getProductCount());
        IAtomContainer reactant = reaction1.getReactants().getAtomContainer(0);
        assertNotNull(reactant);
        assertEquals(32, reactant.getAtomCount());
        assertEquals(29, reactant.getBondCount());
        IAtomContainer product = reaction1.getProducts().getAtomContainer(0);
        assertNotNull(product);
        assertEquals(32, product.getAtomCount());
        assertEquals(29, product.getBondCount());
        
    }

}
