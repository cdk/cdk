/* $Revision: 6006 $ $Author: egonw $ $Date: 2006-04-19 13:55:06 +0200 (Wed, 19 Apr 2006) $
 *
 * Copyright (C) 2006  Egon Willighagen <egonw@users.sf.net>
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
 */
package org.openscience.cdk.test.io;

import java.io.InputStream;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.ChemFile;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.PCCompoundASNReader;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

/**
 * @cdk.module test-io
 */
public class PCCompoundASNReaderTest extends CDKTestCase {

    private LoggingTool logger;

    public PCCompoundASNReaderTest(String name) {
        super(name);
        logger = new LoggingTool(this);
    }

    public static Test suite() {
        return new TestSuite(PCCompoundASNReaderTest.class);
    }

    public void testAccepts() {
    	PCCompoundASNReader reader = new PCCompoundASNReader();
    	assertTrue(reader.accepts(ChemFile.class));
    }

    public void testReading() {
        String filename = "data/pc-asn/cid1.asn";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
        	PCCompoundASNReader reader = new PCCompoundASNReader(ins);
        	IChemFile cFile = (IChemFile)reader.read(new ChemFile());
        	List containers = ChemFileManipulator.getAllAtomContainers(cFile);
        	assertEquals(1, containers.size());
        	assertTrue(containers.get(0) instanceof IMolecule);
        	IMolecule molecule = (IMolecule)containers.get(0);
        	assertNotNull(molecule);
        	assertEquals(31, molecule.getAtomCount());
        } catch (Exception e) {
            fail(e.toString());
        }
    }
}
