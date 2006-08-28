/* $Revision: 6707 $ $Author: egonw $ $Date: 2006-07-30 16:38:18 -0400 (Sun, 30 Jul 2006) $
 *
 * Copyright (C) 2004-2006  Egon Willighagen <egonw@users.sf.net>
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

import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.io.Mol2Reader;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.LoggingTool;

/**
 * TestCase for the reading Cerius2 Polymorph Predictor files using a test file.
 *
 * @cdk.module test-io
 *
 * @see org.openscience.cdk.io.PMP2Reader
 */
public class PMPReaderTest extends CDKTestCase {

    private org.openscience.cdk.tools.LoggingTool logger;

    public PMPReaderTest(String name) {
        super(name);
        logger = new LoggingTool(this);
    }

    public static Test suite() {
        return new TestSuite(PMPReaderTest.class);
    }

    public void testAccepts() {
    	Mol2Reader reader = new Mol2Reader();
    	assertTrue(reader.accepts(ChemFile.class));
    	assertTrue(reader.accepts(ChemModel.class));
    }

    public void testAceticAcid() {
        String filename = "data/pmp/aceticacid.pmp";
        logger.info("Testing: ", filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            Mol2Reader reader = new Mol2Reader(ins);
            ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
            
            assertNotNull(chemFile);
        } catch (Exception e) {
            fail(e.toString());
        }
    }

}
