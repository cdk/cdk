/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 1997-2002  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *  */
package org.openscience.cdk.test.io;

import org.openscience.cdk.*;
import org.openscience.cdk.io.*;
import java.io.*;
import junit.framework.*;

/**
 * TestCase for the reading MDL mol files using one test file.
 *
 * @see org.openscience.cdk.io.MDLReader
 */
public class SMILESReaderTest extends TestCase {

    private org.openscience.cdk.tools.LoggingTool logger;

    public SMILESReaderTest(String name) {
        super(name);
        logger = new org.openscience.cdk.tools.LoggingTool(this.getClass().getName());
    }

    public static Test suite() {
        return new TestSuite(SMILESReaderTest.class);
    }

    public void testReading() {
        String filename = "data/smiles.txt";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            SMILESReader reader = new SMILESReader(new InputStreamReader(ins));
            SetOfMolecules som = (SetOfMolecules)reader.read(new SetOfMolecules());
            assertEquals(8, som.getMoleculeCount());
        } catch (Exception e) {
            fail(e.toString());
        }
    }
}
