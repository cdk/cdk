/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2004  The Chemistry Development Kit (CDK) project
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
import org.openscience.cdk.geometry.*;
import java.io.*;
import junit.framework.*;

public class VASPReaderTest extends TestCase {

    private org.openscience.cdk.tools.LoggingTool logger;

    public VASPReaderTest(String name) {
        super(name);
        logger = new org.openscience.cdk.tools.LoggingTool(this.getClass().getName());
    }

    public static Test suite() {
        return new TestSuite(VASPReaderTest.class);
    }

    public void testReading() {
        String filename = "data/LiMoS2_optimisation_ISIF3.vasp";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            VASPReader reader = new VASPReader(new InputStreamReader(ins));
            ChemFile chemFile = (ChemFile)reader.read(new ChemFile());
            assertNotNull(chemFile);
            ChemSequence sequence = chemFile.getChemSequence(0);
            assertNotNull(sequence);
            assertEquals(6, sequence.getChemModelCount());
            ChemModel model = sequence.getChemModel(0);
            assertNotNull(model);
            Crystal crystal = model.getCrystal();
            assertNotNull(crystal);
            assertEquals(16, crystal.getAtomCount());
            Atom atom = crystal.getAtomAt(0);
            assertNotNull(atom);
            assertNotNull(atom.getFractionalPoint3d());
        } catch (Exception exception) {
            logger.error(exception.getMessage());
            logger.debug(exception);
            fail(exception.getMessage());
        }
    }
}
