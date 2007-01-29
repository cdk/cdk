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
package org.openscience.cdk.test.tools.manipulator;

import java.io.InputStream;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.ChemModel;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;

/**
 * @cdk.module test-standard
 */
public class ChemModelManipulatorTest extends CDKTestCase {
    
	private final static LoggingTool logger = new LoggingTool(ChemModelManipulatorTest.class);
	
	public ChemModelManipulatorTest(String name) {
        super(name);
    }
    
	public static Test suite() {
		return new TestSuite(ChemModelManipulatorTest.class);
	}

    public void testGetAllAtomContainers_IChemModel() throws Exception {
        String filename = "data/mdl/a-pinene.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);

        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemModel chemFile = (ChemModel)reader.read((ChemObject)new ChemModel());
        assertNotNull(chemFile);
        List containersList = ChemModelManipulator.getAllAtomContainers(chemFile);
        assertEquals(1, containersList.size());
    }
}


