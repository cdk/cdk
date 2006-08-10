/* $RCSfile: $
 * $Author: egonw $
 * $Date: 2006-07-30 16:38:18 -0400 (Sun, 30 Jul 2006) $
 * $Revision: 6707 $
 *
 * Copyright (C) 2006  Egon Willighagen <egonw@users.sf.net>
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
 *  */
package org.openscience.cdk.test.io;

import java.io.InputStream;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.io.CTXReader;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.LoggingTool;

/**
 * TestCase for the reading CTX files using a test file.
 *
 * @cdk.module test-io
 *
 * @see org.openscience.cdk.io.CrystClustReader
 */
public class CTXReaderTest extends CDKTestCase {

    private LoggingTool logger;

    public CTXReaderTest(String name) {
        super(name);
        logger = new LoggingTool(this);
    }

    public static Test suite() {
        return new TestSuite(CTXReaderTest.class);
    }

    public void testAccepts() {
    	CTXReader reader = new CTXReader();
    	assertTrue(reader.accepts(ChemFile.class));
    }
    
    public void testMethanol() {
        String filename = "data/ctx/methanol_with_descriptors.ctx";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            CTXReader reader = new CTXReader(ins);
            IChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
            
            assertNotNull(chemFile);
            assertEquals(1, chemFile.getChemSequenceCount());
            IChemSequence seq = chemFile.getChemSequence(0);
            assertNotNull(seq);
            assertEquals(1, seq.getChemModelCount());
            IChemModel model = seq.getChemModel(0);
            assertNotNull(model);
            
            IMoleculeSet moleculeSet = model.getSetOfMolecules();
            assertNotNull(moleculeSet);
            assertEquals(1, moleculeSet.getAtomContainerCount());
            
            IAtomContainer container = moleculeSet.getAtomContainer(0);
            assertNotNull(container);
            assertEquals("Incorrect atom count.", 6, container.getAtomCount());
            assertEquals(5, container.getBondCount());
            
            assertEquals("Petra", container.getID());

            assertNotNull(container.getProperty(CDKConstants.TITLE));
            assertEquals("CH4O", container.getProperty(CDKConstants.TITLE));
            
        } catch (Exception exception) {
            System.out.println("Error while reading file: " + exception.getMessage());
            exception.printStackTrace();
            fail(exception.toString());
        }
    }
}
